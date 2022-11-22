package com.itheima.aScenary.entity.impl;

import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.entity.ElementData;
import com.itheima.aScenary.entity.Node;

import java.util.Map;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.22
 */
// 四面体
public class TetrahedronElementData extends ElementData {

    public TetrahedronElementData(int[] nodes) {
        super(nodes);
    }

    @Override
    public Element convertToElement(Map<Integer, Node> nodeMap) {
        Element e = new TetrahedronElement();
        Node[] nodes = new Node[4];
        for (int i = 0; i < 4; i++) {
            nodes[i] = nodeMap.get(this.nodes[i]);
        }
        e.setNodes(nodes);
        return e;
    }
}
