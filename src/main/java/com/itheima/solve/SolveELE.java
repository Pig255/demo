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

import java.util.List;

public class SolveELE {
    protected List<Noder> noderList;
    protected List<Elementor> elementorList;
    protected CMDSetting setting;

    public void solveM(CMDSetting setting) {
    }

    //从数据库读取单元数据
    protected List<Elementor> readElementorForSolve() {
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
    protected List<Noder> readNoderForSolve() {
        NodeRepository nodeRepository = new NodeRepository();
        List<Noder> list = nodeRepository.getNoderListForSolve();
        return list;
    }

    //线弹性材料应力-应变矩阵
    protected SimpleMatrix LinearIsotropicD(double E, double u) {
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

    //将节点文件传入数据库
    public void inputNodeFromFile() {
        NodeRepository nodeRepository = new NodeRepository();
        nodeRepository.initForSolve(this.setting);
    }

    //将单元文件传入数据库
    public void inputElementFromFile() {
        ElementDataRepository elementDataRepository=null;
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

    protected void UpdateForResult(USSDEnum ussdEnum, Noder nodeTemp) {
        NodeRepository nodeRepository = new NodeRepository();
        nodeRepository.updateResult(ussdEnum, nodeTemp);
    }
}
