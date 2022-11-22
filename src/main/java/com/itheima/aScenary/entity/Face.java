package com.itheima.aScenary.entity;

import com.itheima.aScenary.enums.ColorEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.itheima.aScenary.util.NormalUtil.*;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @data 2022.04.10
 */
// 面元类
public class Face {
    private Node[] nodes;
    private ColorEnum color;

    public Face(Node[] nodes) {
        Arrays.sort(nodes);
        this.nodes = nodes;
        this.color = ColorEnum.UNKNOWN;
    }

    public Face(Node n1, Node n2, Node n3) {
        nodes = new Node[]{n1, n2, n3};
        Arrays.sort(nodes);
        this.color = ColorEnum.UNKNOWN;
    }

    public Face(Node n1, Node n2, Node n3, int colorIndex) {
        nodes = new Node[]{n1, n2, n3};
        Arrays.sort(nodes);
        this.color = ColorEnum.getColorByIndex(colorIndex);
    }

    public Node[] getNodes() {
        return nodes;
    }

    public ColorEnum getColor() {
        return color;
    }

    public void setColor(ColorEnum color) {
        this.color = color;
    }

    public void setColorByIndex(int colorIndex) {
        this.color = ColorEnum.getColorByIndex(colorIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Face face = (Face) o;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getIndex() != face.nodes[i].getIndex()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (Node node : nodes) {
            int curH = node.getIndex();
            h = (h << 1) ^ curH;
        }
        return h;
    }

    // 剖面图附带面
    public List<Face> cutByProfile(List<Float> profile) {
        List<Face> result = new ArrayList<>();
        float[] flag = new float[3];   // 节点与剖面的距离
        Node[] newOrder = new Node[3]; // 按照剖面距离排序的节点，而不用改变原节点排序
        for (int i = 0; i < 3; i++) {
            newOrder[i] = this.getNodes()[i];
            flag[i] = newOrder[i].getCoord()[0] * profile.get(0) + newOrder[i].getCoord()[1] * profile.get(1)
                    + newOrder[i].getCoord()[2] * profile.get(2) + profile.get(3);
        }
        sortNodeByFlag(flag, newOrder);
        if (flag[0] > 0) { // 3正
            result.add(this);
        } else if (flag[2] < 0) { // 3负
            // ignore
        } else if (flag[0] < 0 && flag[1] > 0) { // 1负2正
            Node newNode1 = getIntersection(newOrder[0], newOrder[1], profile);
            Node newNode2 = getIntersection(newOrder[0], newOrder[2], profile);
            Face f1 = new Face(newNode1, newNode2, newOrder[1]);
            Face f2 = new Face(newOrder[1], newOrder[2], newNode2);
            result.add(f1);
            result.add(f2);
        } else if (flag[1] < 0 && flag[2] > 0) { // 2负1正
            Node newNode1 = getIntersection(newOrder[0], newOrder[2], profile);
            Node newNode2 = getIntersection(newOrder[1], newOrder[2], profile);
            Face f = new Face(newNode1, newNode2, newOrder[2]);
            result.add(f);
        }
        return result;
    }

    // 给定最大最小值，返回上色后的面元
    public List<Face> coloring(float maxData, float minData) {
        List<Face> facesWithColor = new ArrayList<>();
        int[] nodeColor = new int[3];
        // 获取节点颜色
        for (int i = 0; i < 3; i++) {
            nodeColor[i] = getIndexByData(getNodes()[i].getData(), maxData, minData);
        }
        if (nodeColor[0] == nodeColor[1] && nodeColor[0] == nodeColor[2]) {
            // 没有等值线
            this.setColorByIndex(nodeColor[0]);
            facesWithColor.add(this);
        } else {
            // 有等值线，找出node0
            Node[] nodeNew = new Node[5];
            int[] order = new int[3];
            if (nodeColor[0] != nodeColor[1] && nodeColor[0] != nodeColor[2]) {
                order[0] = 0;
                order[1] = 1;
                order[2] = 2;
            } else if (nodeColor[0] != nodeColor[1] && nodeColor[1] != nodeColor[2]) {
                order[0] = 1;
                order[1] = 0;
                order[2] = 2;
            } else if (nodeColor[0] != nodeColor[2] && nodeColor[1] != nodeColor[2]) {
                order[0] = 2;
                order[1] = 0;
                order[2] = 1;
            }
            nodeNew[0] = this.getNodes()[order[0]];
            for (int k = 1; k < 3; k++) {
                nodeNew[k] = this.getNodes()[order[k]];
                float lineData = Math.max(nodeColor[order[0]], nodeColor[order[k]]) / 9.0f * (maxData - minData) + minData; // 等值线的值
                Node newNode = interpolation(nodeNew[0], nodeNew[k], lineData); // 插值得到新节点
                nodeNew[k + 2] = newNode;
            }

            Face[] faceNew = {
                    new Face(nodeNew[0], nodeNew[3], nodeNew[4], nodeColor[order[0]]),
                    new Face(nodeNew[1], nodeNew[2], nodeNew[3], nodeColor[order[1]]),
                    new Face(nodeNew[2], nodeNew[3], nodeNew[4], nodeColor[order[2]])
            };
            facesWithColor.addAll(Arrays.asList(faceNew));
        }
        return facesWithColor;
    }

}
