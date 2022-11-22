package com.itheima.aScenary.repository;

import com.itheima.aScenary.container.CMDContainer;
import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.entity.ElementData;
import com.itheima.aScenary.entity.Face;
import com.itheima.aScenary.entity.Node;
import com.itheima.aScenary.listener.CMDSettingListener;
import com.itheima.aScenary.util.NormalUtil;

import java.util.*;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.12
 */
public class FaceRepository implements CMDSettingListener {

    private CMDSetting setting;

    // 由容器生成bean
    private ElementDataRepository elementDataRepository = CMDContainer.getListner(ElementDataRepository.class);
    private NodeRepository nodeRepository = CMDContainer.getListner(NodeRepository.class);

    private List<Element> elementList;
    private Set<Face> boundaryFaceSet;

    @Override
    public void init(CMDSetting setting) {
        this.setting = setting;
        loadElementList();
        loadBoundaryFaceSet();
    }

    @Override
    public void update(CMDSetting setting) {
        if (fileHasChanged(setting)) {
            init(setting);
        } else {
            this.setting = setting;
        }
    }

    public Collection<Face> getDrawing() {
        switch (setting.getDrawingMode()) {
            case PANORAMA:
                return coloringFace(boundaryFaceSet);
            case PROFILE:
                return coloringFace(getProfile());
        }
        return new ArrayList<>();
    }

    // 数据文件是否有改动
    private boolean fileHasChanged(CMDSetting setting) {
        if (!setting.getElementType().equals(this.setting.getElementType())) {
            return true;
        }
        if (!this.setting.getElementFile().equals(setting.getElementFile())) {
            return true;
        }
        return NormalUtil.needUpdateNodeFile(setting, this.setting);
    }

    // 加载单元数据
    private void loadElementList() {
        List<ElementData> elementDataList = elementDataRepository.getElementDataList();
        Map<Integer, Node> nodeMap = nodeRepository.getNodeMap();

        elementList = new ArrayList<>();
        for (ElementData elementData : elementDataList) {
            elementList.add(elementData.convertToElement(nodeMap));
        }
    }

    // 加载边界面数据
    private void loadBoundaryFaceSet() {
        boundaryFaceSet = new HashSet<>();
        for (Element e : elementList) {
            for (Face f : e.getBoundaryFaces()) {
                if (boundaryFaceSet.contains(f)) {
                    boundaryFaceSet.remove(f);
                } else {
                    boundaryFaceSet.add(f);
                }
            }
        }
        System.out.println("边界面处理完毕，处理后面元数: " + boundaryFaceSet.size());
    }

    // 获取剖面图
    private Collection<Face> getProfile() {
        List<Face> result = new ArrayList<>();
        List<Float> proFileFace = setting.getProfileFace();
        // 1. 获取剖面
        for (Element e : elementList) {
            result.addAll(e.getProfileFaces(proFileFace));
        }
        // 2. 获取部分全景图
        for (Face outFace : boundaryFaceSet) {
            result.addAll(outFace.cutByProfile(proFileFace));
        }
        System.out.println("剖面处理完毕，处理后面元数: " + result.size());
        return result;
    }

    // 面元上色
    private Collection<Face> coloringFace(Collection<Face> facesWithoutColor) {
        float maxData = nodeRepository.getMaxData(),
                minData = nodeRepository.getMinData();
        List<Face> facesWithColor = new ArrayList<>();
        for (Face faceWithoutColor : facesWithoutColor) {
            facesWithColor.addAll(faceWithoutColor.coloring(maxData, minData));
        }
        System.out.println("面元上色完毕，最终面元数: " + facesWithColor.size());
        return facesWithColor;
    }


}
