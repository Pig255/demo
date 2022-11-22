package com.itheima.aScenary.entity;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
// 网格节点类
public class Node implements Comparable<Node> {
    private int index; // 节点编号
    private float[] coord; // 节点坐标
    private float data; // 节点数据

    public Node() {
        index = -1;
        coord = new float[]{.0f, .0f, .0f};
        data = Float.MAX_VALUE;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float[] getCoord() {
        return coord;
    }

    public void setCoord(float[] coord) {
        this.coord = coord;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    @Override
    public int compareTo(Node node) {
        return Integer.compare(this.index, node.index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return index == node.index;
    }

    @Override
    public int hashCode() {
        return index;
    }
}
