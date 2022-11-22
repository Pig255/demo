package com.itheima.aScenary.entity;

import java.util.Map;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @data 2022.04.10
 */
// 网格单元类，内含节点编号
public abstract class ElementData {
    protected int[] nodes;

    public ElementData(int[] nodes) {
        this.nodes = nodes;
    }

    public int[] getNodes() {
        return nodes;
    }

    public abstract Element convertToElement(Map<Integer, Node> nodeMap);

}
