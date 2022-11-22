package com.itheima.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Elementor {
    private int ID;
    private String type;
    private int node1;
    private int node2;
    private int node3;
    private int node4;
    private int node5;
    private int node6;
    private int node7;
    private int node8;
    private int node9;
    private int node10;
    private int node11;
    private int node12;
    private int node13;
    private int node14;
    private int node15;
    private int node16;
    private int node17;
    private int node18;
    private int node19;
    private int node20;
    public int[] getNodesArray(){
        return new int[]{node1,node2,node3,node4,node5,node6,node7,
                        node8,node9,node10,node11,node12,node13,node14,
                        node15,node16,node17,node18,node19,node20};
    }
}
