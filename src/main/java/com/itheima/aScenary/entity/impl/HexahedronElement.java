package com.itheima.aScenary.entity.impl;

import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.entity.Face;

import java.util.List;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
// 六面体
// TODO: 待完善，弄清楚六面体网格节点的顺序
public class HexahedronElement extends Element {

    public HexahedronElement() {
    }

    @Override
    public List<Face> getBoundaryFaces() {
        return null;
    }

    @Override
    public List<Face> getProfileFaces(List<Float> proFileFace) {
        return null;
    }
}
