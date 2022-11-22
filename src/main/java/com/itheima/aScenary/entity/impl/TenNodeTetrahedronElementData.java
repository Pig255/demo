package com.itheima.aScenary.entity.impl;

import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.entity.ElementData;
import com.itheima.aScenary.entity.Node;

import java.util.Map;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.05.23
 */
public class TenNodeTetrahedronElementData extends ElementData {

    public TenNodeTetrahedronElementData(int[] nodes) {
        super(nodes);
    }

    @Override
    public Element convertToElement(Map<Integer, Node> nodeMap) {
        Element e = new TenNodeTetrahedronElement();
        Node[] nodes = new Node[10];
        for (int i = 0; i < 10; i++) {
            nodes[i] = nodeMap.get(this.nodes[i]);
        }
        e.setNodes(nodes);
        return e;
    }
}
