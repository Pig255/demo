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
public class HexahedronElementData extends ElementData {

    public HexahedronElementData(int[] nodes) {
        super(nodes);
    }

    @Override
    public Element convertToElement(Map<Integer, Node> nodeMap) {
        Element e = new HexahedronElement();
        Node[] nodes = new Node[6];
        for (int i = 0; i < 6; i++) {
            nodes[i] = nodeMap.get(this.nodes[i]);
        }
        e.setNodes(nodes);
        return e;
    }
}
