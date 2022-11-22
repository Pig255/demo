package com.itheima.aScenary.entity;

import java.util.List;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
// 网格单元接口
public abstract class Element {

    protected Node[] nodes;

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public abstract List<Face> getBoundaryFaces();

    public abstract List<Face> getProfileFaces(List<Float> proFileFace);

}
