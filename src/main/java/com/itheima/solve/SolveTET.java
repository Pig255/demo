package com.itheima.solve;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.repository.ElementDataRepository;
import com.itheima.aScenary.repository.NodeRepository;
import com.itheima.aScenary.repository.impl.HexahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TenNodeTetrahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TetrahedronElementDataRepository;
import com.itheima.domain.Elementor;
import com.itheima.domain.Noder;

import java.util.ArrayList;
import java.util.List;

public class SolveTET extends SolveELE {
    public void solveM(CMDSetting setting){
        this.setting=setting;
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
        for (int i=0;i<Elements.length;i++){
            Elements[i][0]=elementorList.get(i).getNode1();
            Elements[i][1]=elementorList.get(i).getNode2();
            Elements[i][2]=elementorList.get(i).getNode3();
            Elements[i][3]=elementorList.get(i).getNode4();
            Elements[i][4]=elementorList.get(i).getNode5();
            Elements[i][5]=elementorList.get(i).getNode6();
            Elements[i][6]=elementorList.get(i).getNode7();
            Elements[i][7]=elementorList.get(i).getNode8();
        }
        //获取外力作用节点、方向及外力大小
        //获取约束节点

        //设置E、u的大小（弹性模量与泊松比）
        double E = 210000;
        double u = 0.3;
        //代入求解函数求出节点位移

        //将结果写入数据库
    }
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

    private List<Noder> readNoderForSolve() {
        NodeRepository nodeRepository = new NodeRepository();
        List<Noder> list = nodeRepository.getNoderListForSolve();
        return list;
    }

    private void inputNodeFromFile() {
        NodeRepository nodeRepository = new NodeRepository();
        nodeRepository.initForSolve(this.setting);
    }

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
