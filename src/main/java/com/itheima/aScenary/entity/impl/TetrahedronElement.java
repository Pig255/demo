package com.itheima.aScenary.entity.impl;

import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.entity.Face;
import com.itheima.aScenary.entity.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.itheima.aScenary.util.NormalUtil.getIntersection;
import static com.itheima.aScenary.util.NormalUtil.sortNodeByFlag;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
// 四节点四面体
public class TetrahedronElement extends Element {

    public TetrahedronElement() {
    }

    @Override
    public List<Face> getBoundaryFaces() {
        Face[] faces = new Face[]{
                new Face(nodes[0], nodes[1], nodes[2]),
                new Face(nodes[0], nodes[1], nodes[3]),
                new Face(nodes[0], nodes[2], nodes[3]),
                new Face(nodes[1], nodes[2], nodes[3])
        };
        return Arrays.asList(faces);
    }

    @Override
    public List<Face> getProfileFaces(List<Float> proFileFace) {
        float[] flag = new float[4];
        Node[] newOrder = new Node[4];

        List<Face> result = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            newOrder[i] = getNodes()[i];
            flag[i] = newOrder[i].getCoord()[0] * proFileFace.get(0) + newOrder[i].getCoord()[1] * proFileFace.get(1)
                    + newOrder[i].getCoord()[2] * proFileFace.get(2) + proFileFace.get(3);
        }
        sortNodeByFlag(flag, newOrder);
        if (flag[3] < 0 || flag[0] > 0) { // 4负或4正
            // 剖面与单元不相交
        } else if (flag[1] * flag[2] < 0) { // 2负2正
            Node[] newNode = new Node[4];
            newNode[0] = getIntersection(newOrder[0], newOrder[2], proFileFace);
            newNode[1] = getIntersection(newOrder[0], newOrder[3], proFileFace);
            newNode[2] = getIntersection(newOrder[1], newOrder[2], proFileFace);
            newNode[3] = getIntersection(newOrder[1], newOrder[3], proFileFace);
            Face f1 = new Face(newNode[0], newNode[1], newNode[2]);
            Face f2 = new Face(newNode[1], newNode[2], newNode[3]);
            result.add(f1);
            result.add(f2);
        } else if (flag[0] < 0 && flag[1] > 0) { // 1负3正
            Node[] newNode = new Node[3];
            newNode[0] = getIntersection(newOrder[0], newOrder[1], proFileFace);
            newNode[1] = getIntersection(newOrder[0], newOrder[2], proFileFace);
            newNode[2] = getIntersection(newOrder[0], newOrder[3], proFileFace);
            Face f = new Face(newNode[0], newNode[1], newNode[2]);
            result.add(f);
        } else if (flag[2] < 0 && flag[3] > 0) { // 3负1正
            Node[] newNode = new Node[3];
            newNode[0] = getIntersection(newOrder[0], newOrder[3], proFileFace);
            newNode[1] = getIntersection(newOrder[1], newOrder[3], proFileFace);
            newNode[2] = getIntersection(newOrder[2], newOrder[3], proFileFace);
            Face f = new Face(newNode[0], newNode[1], newNode[2]);
            result.add(f);
        }

        return result;
    }
}
