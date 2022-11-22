package com.itheima.aScenary.entity.impl;

import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.entity.Face;
import com.itheima.aScenary.entity.Node;

import java.util.Arrays;
import java.util.List;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.05.23
 */
public class TenNodeTetrahedronElement extends Element {

    public TenNodeTetrahedronElement() {
    }

    @Override
    public List<Face> getBoundaryFaces() {
        Face[] faces = new Face[]{
                new Face(nodes[0], nodes[6], nodes[7]),
                new Face(nodes[6], nodes[2], nodes[9]),
                new Face(nodes[6], nodes[9], nodes[7]),
                new Face(nodes[7], nodes[9], nodes[3]),

                new Face(nodes[0], nodes[6], nodes[4]),
                new Face(nodes[6], nodes[2], nodes[5]),
                new Face(nodes[6], nodes[5], nodes[4]),
                new Face(nodes[4], nodes[5], nodes[1]),

                new Face(nodes[0], nodes[7], nodes[4]),
                new Face(nodes[7], nodes[3], nodes[8]),
                new Face(nodes[7], nodes[8], nodes[4]),
                new Face(nodes[4], nodes[8], nodes[1]),

                new Face(nodes[2], nodes[9], nodes[5]),
                new Face(nodes[9], nodes[3], nodes[8]),
                new Face(nodes[9], nodes[8], nodes[5]),
                new Face(nodes[5], nodes[8], nodes[1])
        };
        return Arrays.asList(faces);
    }

    // 与四节点四面体一致
    @Override
    public List<Face> getProfileFaces(List<Float> proFileFace) {
        TetrahedronElement tetra = new TetrahedronElement();
        Node[] tetraNodes = new Node[]{nodes[0], nodes[1], nodes[2], nodes[3]};
        return tetra.getProfileFaces(proFileFace);
    }
}
