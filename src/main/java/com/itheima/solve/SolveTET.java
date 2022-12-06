package com.itheima.solve;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.enums.USSDEnum;
import com.itheima.aScenary.repository.ElementDataRepository;
import com.itheima.aScenary.repository.NodeRepository;
import com.itheima.aScenary.repository.impl.HexahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TenNodeTetrahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TetrahedronElementDataRepository;
import com.itheima.domain.Elementor;
import com.itheima.domain.Noder;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolveTET extends SolveELE {
    public void solveM(CMDSetting setting) {
        this.setting = setting;
        this.noderList = new ArrayList<>();
        this.elementorList = new ArrayList<>();
        //将本地文件里的网格数据存入数据库
        inputElementFromFile();
        inputNodeFromFile();
        //从数据库读取节点、单元信息
        this.noderList = readNoderForSolve();
        this.elementorList = readElementorForSolve();
        double[][] Nodes = new double[noderList.size()][3];
        double[][] Elements = new double[elementorList.size()][4];
        for (int i = 0; i < Nodes.length; i++) {
            Nodes[i][0] = noderList.get(i).getLOC_X();
            Nodes[i][1] = noderList.get(i).getLOC_Y();
            Nodes[i][2] = noderList.get(i).getLOC_Z();
        }
        for (int i = 0; i < Elements.length; i++) {
            Elements[i][0] = elementorList.get(i).getNode1();
            Elements[i][1] = elementorList.get(i).getNode2();
            Elements[i][2] = elementorList.get(i).getNode3();
            Elements[i][3] = elementorList.get(i).getNode4();
        }
        //获取外力作用节点、方向及外力大小
        double[][] Forces = getForceBoundary();
        //获取约束节点
        double[][] Constraints = getConstraintsBoundary();
        //设置E、u的大小（弹性模量与泊松比）
        double E = 210000;
        double u = 0.3;
        SimpleMatrix U = staticSolver(E, u, Forces, Constraints, Nodes, Elements);
        //根据所得的U求出各节点对应的应力、应变值

        //将结果写入数据库
        Noder nodeTemp;
        for (int i = 0; i < noderList.size(); i++) {
            nodeTemp = noderList.get(i);
            nodeTemp.setU_X(U.get(3 * i, 0));
            nodeTemp.setU_Y(U.get(3 * i + 1, 0));
            nodeTemp.setU_Z(U.get(3 * i + 2, 0));
            noderList.set(i, nodeTemp);
            USSDEnum ussdEnum = USSDEnum.U;
            UpdateForResult(ussdEnum, nodeTemp);
        }
        System.out.println("----------节点位移已经完成求解并入库-----------");
    }

    private SimpleMatrix staticSolver(double E, double u, double[][] Forces, double[][] Constraints, double[][] Nodes, double[][] Elements) {
        int Dof = 3;
        int NodeCount = noderList.size();
        int ElementCount = elementorList.size();
        int Dofs = Dof * NodeCount;
        SimpleMatrix K = new SimpleMatrix(Dofs, Dofs);
        SimpleMatrix Force = new SimpleMatrix(Dofs, 1);
        SimpleMatrix D = LinearIsotropicD(E, u);
        for (int i = 0; i < ElementCount; i++) {
            Elementor elementor = elementorList.get(i);
            int[] nodesArray = Arrays.copyOfRange(elementor.getNodesArray(), 0, 4);
            double[][] xyz_ori = new double[4][3];
            for (int p = 0; p < nodesArray.length; p++) {
                Noder noder = noderList.get(nodesArray[p] - 1);
                xyz_ori[p][0] = noder.getLOC_X();
                xyz_ori[p][1] = noder.getLOC_Y();
                xyz_ori[p][2] = noder.getLOC_Z();
            }
            SimpleMatrix xyz = new SimpleMatrix(xyz_ori);

            SimpleMatrix ElementStiffnessMatrix = Ke(D, xyz);

            //计算单元节点自由度编号
            int[] ElementNodeDOF = new int[12];
            //将Ke往K里组装
            for (int j = 0; j < 4; j++) {
                int II = j * Dof;
                int temp = (elementorList.get(i).getNodesArray()[j] - 1) * Dof;
                ElementNodeDOF[II] = temp;
                ElementNodeDOF[II + 1] = temp + 1;
                ElementNodeDOF[II + 2] = temp + 2;
            }
            for (int p = 0; p < 12; p++) {
                for (int q = 0; q < 12; q++) {
                    K.set(ElementNodeDOF[p], ElementNodeDOF[q],
                    (K.get(ElementNodeDOF[p], ElementNodeDOF[q])+ElementStiffnessMatrix.get(p, q)));
                }
            }
        }
        //施加外力
        if (Forces.length > 0) {
            SimpleMatrix Force_1 = new SimpleMatrix(Forces);
            //这里减1是理所当然，第一个单元的第一个节点（1-1）*3+1
            SimpleMatrix temp1 = Force_1.cols(0, 1).minus(1);
            SimpleMatrix temp2 = Force_1.cols(1, 2);

            //这里减1是因为java矩阵的索引是从0开始的
            SimpleMatrix ForceDOF = temp1.scale(Dof).plus(temp2).minus(1);
            for (int i = 0; i < ForceDOF.getNumElements(); i++) {
                int temp = (int) ForceDOF.get(i, 0);
                double ftemp = Forces[i][2];
                Force.set(temp, 0, ftemp);
            }
        }
        //乘大数法施加位移约束
        int BigNumber = 100000000;
        int ConstraintsNumber = Constraints.length;
        if (ConstraintsNumber > 0) {
            SimpleMatrix Constraints_1 = new SimpleMatrix(Constraints);
            //这里减1是理所当然，第一个单元的第一个节点（1-1）*3+1
            SimpleMatrix temp1 = Constraints_1.cols(0, 1).minus(1);
            SimpleMatrix temp2 = Constraints_1.cols(1, 2);

            //这里减1是因为java矩阵的索引是从0开始的
            SimpleMatrix FixedDof = temp1.scale(Dof).plus(temp2).minus(1);
            for (int i = 0; i < ConstraintsNumber; i++) {
                int temp = (int) FixedDof.get(i, 0);
                double new_K = K.get(temp, temp) * BigNumber;
                K.set(temp, temp, new_K);
                Force.set(temp, 0, new_K * Constraints[i][2]);
            }
        }
        //求解线程方程组，得出位移
        SimpleMatrix U = K.invert().mult(Force);
        return U;
    }

    //返回节点受外力
    private double[][] getForceBoundary() {
        double[][] force = new double[11][3];
        int tip = 0;
        for (int i = 0; i < noderList.size(); i++) {
            if (noderList.get(i).getLOC_Z() == 100 && noderList.get(i).getLOC_Y() == 0) {
                force[tip][0] = noderList.get(i).getID();
                force[tip][1] = 2;
                force[tip][2] = -100;
                tip++;
            }
        }
        return force;
    }

    //返回约束节点
    private double[][] getConstraintsBoundary() {
        double[][] constraint = new double[228][3];
        int tip = 0;
        for (int i = 0; i < noderList.size(); i++) {
            if (noderList.get(i).getLOC_Z() == 0) {
                for (int j = 1; j < 4; j++) {
                    constraint[tip][0] = noderList.get(i).getID();
                    constraint[tip][1] = j;
                    constraint[tip][2] = 0;
                    tip++;
                }
            }
        }
        return constraint;
    }

    private SimpleMatrix Ke(SimpleMatrix D, SimpleMatrix xyz) {
        SimpleMatrix B = new SimpleMatrix(6, 12);
        SimpleMatrix[] ans = ShapeFunction(xyz);
        double Coefficient =  1.0/6.0 * ans[1].get(0, 0);
        SimpleMatrix NDerivative = ans[0];
        for (int i = 0; i < 4; i++) {
            int chu = 3 * i;
            B.set(0, chu, NDerivative.get(0, i));
            B.set(1, chu + 1, NDerivative.get(1, i));
            B.set(2, chu + 2, NDerivative.get(2, i));

            B.set(3, chu, NDerivative.get(1, i));
            B.set(3, chu + 1, NDerivative.get(0, i));

            B.set(4, chu + 1, NDerivative.get(2, i));
            B.set(4, chu + 2, NDerivative.get(1, i));

            B.set(5, chu, NDerivative.get(2, i));
            B.set(5, chu + 2, NDerivative.get(0, i));
        }
        SimpleMatrix Ke = B.transpose().mult(D).mult(B).scale(Coefficient);
        return Ke;
    }

    private SimpleMatrix[] ShapeFunction(SimpleMatrix xyz) {
        SimpleMatrix ans[] = new SimpleMatrix[2];
        double[][] ParentNodes = new double[][]{
                {-1, 1, 0, 0},
                {-1, 0, 1, 0},
                {-1, 0, 0, 1}};
        SimpleMatrix P = new SimpleMatrix(ParentNodes);
        SimpleMatrix Jacobi = P.mult(xyz);
        SimpleMatrix JacobiINV = Jacobi.invert();//这里可以优化一下，改成数值表达式，效率提高15%
        double JacobiDET = Jacobi.determinant();
        SimpleMatrix NDerivative = JacobiINV.mult(P);
        SimpleMatrix temp = new SimpleMatrix(1, 1);
        temp.set(0, 0, JacobiDET);
        ans[0] = NDerivative;
        ans[1] = temp;
        return ans;
    }
}
