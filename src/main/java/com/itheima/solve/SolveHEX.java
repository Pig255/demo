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
import org.apache.ibatis.annotations.Update;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolveHEX extends SolveELE {


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
        double[][] Elements = new double[elementorList.size()][8];
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
            Elements[i][4] = elementorList.get(i).getNode5();
            Elements[i][5] = elementorList.get(i).getNode6();
            Elements[i][6] = elementorList.get(i).getNode7();
            Elements[i][7] = elementorList.get(i).getNode8();
        }
        //获取外力作用节点、方向及外力大小
        double[][] Forces = getForceBoundary();
        //获取约束节点
        double[][] Constraints = getConstraintsBoundary();
        //设置E、u的大小（弹性模量与泊松比）
        double E = 210000;
        double u = 0.3;
        //代入求解函数求出节点位移
        SimpleMatrix U = staticSolver(E, u, Forces, Constraints, Nodes, Elements);
        //根据所得的U求出各节点对应的应力、应变值
        //将结果写入数据库
        Noder nodeTemp;
        for(int i=0;i<noderList.size();i++){
            nodeTemp=noderList.get(i);
            nodeTemp.setU_X(U.get(3*i,0));
            nodeTemp.setU_Y(U.get(3*i+1,0));
            nodeTemp.setU_Z(U.get(3*i+2,0));
            noderList.set(i,nodeTemp);
            USSDEnum ussdEnum=USSDEnum.U;
            UpdateForResult(ussdEnum,nodeTemp);
        }
        System.out.println("----------节点位移已经完成求解并入库-----------");
    }

    private void UpdateForResult(USSDEnum ussdEnum, Noder nodeTemp) {
        NodeRepository nodeRepository = new NodeRepository();
        nodeRepository.updateResult(ussdEnum,nodeTemp);
    }

    //在这里面写求解主体
    private SimpleMatrix staticSolver(double E, double u, double[][] Forces, double[][] Constraints, double[][] Nodes, double[][] Elements) {
        int Dof = 3;
        int NodeCount = noderList.size();
        int ElementCount = elementorList.size();
        int Dofs = Dof * NodeCount;
        SimpleMatrix U = new SimpleMatrix(Dofs, 1);
        SimpleMatrix K = new SimpleMatrix(Dofs, Dofs);
        SimpleMatrix Force = new SimpleMatrix(Dofs, 1);
        SimpleMatrix D = LinearIsotropicD(E, u);
        for (int i = 0; i < ElementCount; i++) {
            Elementor elementor = elementorList.get(i);
            int[] nodesArray = Arrays.copyOfRange(elementor.getNodesArray(), 0, 8);
            double[][] xyz_ori = new double[8][3];
            for (int p = 0; p < nodesArray.length; p++) {
                Noder noder = noderList.get(nodesArray[p] - 1);
                xyz_ori[p][0] = noder.getLOC_X();
                xyz_ori[p][1] = noder.getLOC_Y();
                xyz_ori[p][2] = noder.getLOC_Z();
            }
            SimpleMatrix xyz = new SimpleMatrix(xyz_ori);
            SimpleMatrix ElementStiffnessMatrix = Ke(D, xyz);
            //计算单元节点自由度编号
            //SimpleMatrix ElementNodeDOF=new SimpleMatrix(1,24);
            int[] ElementNodeDOF = new int[24];
            //将Ke往K里组装
            for (int j = 0; j < 8; j++) {
                int II = j * Dof;
                int temp = (elementorList.get(i).getNodesArray()[j] - 1) * Dof;
                ElementNodeDOF[II] = temp;
                ElementNodeDOF[II + 1] = temp + 1;
                ElementNodeDOF[II + 2] = temp + 2;
            }
            for (int p = 0; p < 24; p++) {
                for (int q = 0; q < 24; q++) {
                    K.set(ElementNodeDOF[p], ElementNodeDOF[q], ElementStiffnessMatrix.get(p, q));
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
                double orig_K = K.get(temp, temp) * BigNumber;
                K.set(temp, temp, orig_K);
                Force.set(temp, 0, orig_K * Constraints[i][2]);
            }
        }

        //求解线程方程组，得出位移（调用jar包）
        U = K.invert().mult(Force);
        return U;
    }

    //在这里求解单元刚度矩阵
    private SimpleMatrix Ke(SimpleMatrix D, SimpleMatrix xyz) {
        double[] GaussCoordinate = new double[]{-0.57735026918963, 0.57735026918963};
        double[] GaussWeight = new double[]{1.0, 1.0};
        SimpleMatrix Ke = new SimpleMatrix(24, 24);
        for (int X = 0; X < 2; X++) {
            for (int Y = 0; Y < 2; Y++) {
                for (int Z = 0; Z < 2; Z++) {
                    double GP1 = GaussCoordinate[X];
                    double GP2 = GaussCoordinate[Y];
                    double GP3 = GaussCoordinate[Z];
                    SimpleMatrix[] ans = ShapeFunction(GP1, GP2, GP3, xyz);
                    double JacobiDET = ans[1].get(0, 0);
                    SimpleMatrix NDerivative = ans[0];
                    double Coefficient = GaussWeight[X] * GaussWeight[Y] * GaussWeight[Z] * JacobiDET;
                    SimpleMatrix B = new SimpleMatrix(6, 24);
                    for (int i = 0; i < 8; i++) {
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
                    Ke = Ke.plus(B.transpose().mult(D).mult(B).scale(Coefficient));
                }
            }
        }
        return Ke;
    }

    //这里返回的是B*里的元素，和jacobi矩阵的行列式值
    private SimpleMatrix[] ShapeFunction(double GP1, double GP2, double GP3, SimpleMatrix xyz) {
        SimpleMatrix ans[] = new SimpleMatrix[2];
        int[][] ParentNodes = new int[][]{
                {-1, 1, 1, -1, -1, 1, 1, -1},
                {-1, -1, 1, 1, -1, -1, 1, 1},
                {-1, -1, -1, -1, 1, 1, 1, 1}};
        SimpleMatrix N = new SimpleMatrix(8, 1);
        SimpleMatrix ParentNDerivative = new SimpleMatrix(3, 8);
        for (int i = 0; i < 8; i++) {
            int XPoint = ParentNodes[0][i];
            int YPoint = ParentNodes[1][i];
            int ZPoint = ParentNodes[2][i];
            double[] ShapePart = new double[]{(1 + GP1 * XPoint), (1 + GP2 * YPoint), (1 + GP3 * ZPoint)};
            N.set(i, 0, (0.125 * ShapePart[0] * ShapePart[1] * ShapePart[2]));
            ParentNDerivative.set(0, i, 0.125 * XPoint * ShapePart[1] * ShapePart[2]);
            ParentNDerivative.set(1, i, 0.125 * YPoint * ShapePart[0] * ShapePart[2]);
            ParentNDerivative.set(2, i, 0.125 * ZPoint * ShapePart[0] * ShapePart[1]);
        }
        SimpleMatrix Jacobi = ParentNDerivative.mult(xyz);
        SimpleMatrix JacobiINV = Jacobi.invert();
        double JacobiDET = Jacobi.determinant();
        SimpleMatrix NDerivative = JacobiINV.mult(ParentNDerivative);
        SimpleMatrix temp = new SimpleMatrix(1, 1);
        temp.set(0, 0, JacobiDET);
        ans[0] = NDerivative;
        ans[1] = temp;
        return ans;
    }

    //线弹性材料应力-应变矩阵
    private SimpleMatrix LinearIsotropicD(double E, double u) {
        double[][] D = new double[][]{{1 - u, u, u, 0, 0, 0}, {u, 1 - u, u, 0, 0, 0},
                {u, u, 1 - u, 0, 0, 0}, {0, 0, 0, (1 - 2 * u) / 2, 0, 0}, {0, 0, 0, 0, (1 - 2 * u) / 2, 0},
                {0, 0, 0, 0, 0, (1 - 2 * u) / 2}};
        double xishu = E / ((1 + u) * (1 - 2 * u));
        for (int i = 0; i < D.length; i++) {
            for (int j = 0; j < D[0].length; j++) {
                D[i][j] = D[i][j] * xishu;
            }
        }
        return new SimpleMatrix(D);
    }

    //返回节点受外力
    private double[][] getForceBoundary() {
        double[][] force = new double[5][3];
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
        double[][] constraint = new double[90][3];
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

    //从数据库读取单元数据
    private List<Elementor> readElementorForSolve() {
        ElementDataRepository elementDataRepository = null;
        switch (setting.getElementType()) {
            case TETRAHEDRON:
                elementDataRepository = new TetrahedronElementDataRepository();
                break;
            case HEXAHEDRON:
                elementDataRepository = new HexahedronElementDataRepository();
                break;
            case TEN_NODE_TETRAHEDRON:
                elementDataRepository = new TenNodeTetrahedronElementDataRepository();
                break;
        }
        List<Elementor> list = elementDataRepository.getElementListForSolve();
        return list;
    }

    //从数据库读取节点数据
    private List<Noder> readNoderForSolve() {
        NodeRepository nodeRepository = new NodeRepository();
        List<Noder> list = nodeRepository.getNoderListForSolve();
        return list;
    }

    //将节点文件传入数据库
    private void inputNodeFromFile() {
        NodeRepository nodeRepository = new NodeRepository();
        nodeRepository.initForSolve(this.setting);
    }

    //将单元文件传入数据库
    public void inputElementFromFile() {
        ElementDataRepository elementDataRepository = null;
        switch (setting.getElementType()) {
            case TETRAHEDRON:
                elementDataRepository = new TetrahedronElementDataRepository();
                break;
            case HEXAHEDRON:
                elementDataRepository = new HexahedronElementDataRepository();
                break;
            case TEN_NODE_TETRAHEDRON:
                elementDataRepository = new TenNodeTetrahedronElementDataRepository();
                break;
        }
        elementDataRepository.initForSolve(this.setting);
    }
}
