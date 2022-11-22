package com.itheima.aScenary.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.entity.impl.HexahedronElement;
import com.itheima.aScenary.entity.impl.TetrahedronElement;
import com.itheima.aScenary.enums.CameraTypeEnum;
import com.itheima.aScenary.enums.DrawingModeEnum;
import com.itheima.aScenary.enums.ElementTypeEnum;
import com.itheima.aScenary.enums.ResultTypeEnum;
import com.itheima.aScenary.util.NormalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.12
 */
// 发布设置内容
public class CMDSetting {

    // 单元类型
    private ElementTypeEnum elementType;

    // 相机类型
    private CameraTypeEnum cameraType;

    // 当前要计算的数据类型
    private ResultTypeEnum activeResult;

    // 绘图模式，全景还是剖面
    private DrawingModeEnum drawingMode;

    private Integer isImport;
    // 剖面图，ax + by + cz + d = 0
    private List<Float> profileFace;

    // 多case线性叠加的系数
    private List<Float> coefficients;

    // 顶点着色器文件
    private String vShaderFile;

    // 片段着色器文件
    private String fShaderFile;

    // 单元数据文件
    private String elementFile;
    //求解时的单元数据、节点数据
    private String elementFileForSolve;

    private String nodeFileForSolve;
    // 节点数据文件
    private List<Map<ResultTypeEnum, String>> nodeFileMapList;

    // 由json文件读入设置
    // TODO：注意这里json文件路径是写死的，后续可以改成由前端传入json
    private CMDSetting() {
        String jsonFile = NormalUtil.getRootPath() + "setting.json";
        String jsonStr = NormalUtil.readStringFromFile(jsonFile);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonRootNode = null;
        try {
            jsonRootNode = objectMapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        boolean useRootPath = jsonRootNode.get("useRootPath").asBoolean();
        int caseNum = jsonRootNode.get("caseNum").asInt();

        this.elementType = ElementTypeEnum.valueOf(
                jsonRootNode.get("elementType").asText());
        this.cameraType = CameraTypeEnum.valueOf(
                jsonRootNode.get("cameraType").asText());
        this.activeResult = ResultTypeEnum.valueOf(
                jsonRootNode.get("activeResult").asText());
        this.drawingMode = DrawingModeEnum.valueOf(
                jsonRootNode.get("drawingMode").asText());

        this.isImport = jsonRootNode.get("isImport").asInt();

        this.profileFace = new ArrayList<>();
        JsonNode profileFaceNode = jsonRootNode.get("profileFace");
        for (int i = 0; i < 4; i++) {
            float oneNum = (float) profileFaceNode.get(i).asDouble();
            profileFace.add(oneNum);
        }

        this.coefficients = new ArrayList<>();
        JsonNode coefficientsNode = jsonRootNode.get("coefficients");
        for (int i = 0; i < caseNum; i++) {
            float oneCoefficient = (float) coefficientsNode.get(i).asDouble();
            coefficients.add(oneCoefficient);
        }

        this.vShaderFile = buildFilePath(useRootPath, jsonRootNode.get("vShaderFile").asText());
        this.fShaderFile = buildFilePath(useRootPath, jsonRootNode.get("fShaderFile").asText());
        this.elementFile = buildFilePath(useRootPath, jsonRootNode.get("elementFile").asText());
        this.elementFileForSolve=buildFilePath(useRootPath, jsonRootNode.get("elementFileForSolve").asText());
        this.nodeFileForSolve=buildFilePath(useRootPath, jsonRootNode.get("nodeFileForSolve").asText());
        this.nodeFileMapList = new ArrayList<>();
        JsonNode nodeFileMapListNode = jsonRootNode.get("nodeFileMapList");
        for (int i = 0; i < caseNum; i++) {
            Map<ResultTypeEnum, String> oneNodeMap = new HashMap<>();
            JsonNode oneNodeFileMapNode = nodeFileMapListNode.get(i);
            for (ResultTypeEnum resultType : ResultTypeEnum.values()) {
                String oneNodeFilePre = oneNodeFileMapNode.get(resultType.name()).asText("");
                String oneNodeFile = oneNodeFilePre.isEmpty() ? oneNodeFilePre :
                        buildFilePath(useRootPath, oneNodeFilePre);
                oneNodeMap.put(resultType, oneNodeFile);
            }
            nodeFileMapList.add(oneNodeMap);
        }
    }

    private String buildFilePath(boolean useRootPath, String oriPath) {
        return useRootPath ? NormalUtil.getRootPath() + oriPath : oriPath;
    }

    // 更改数据
    public void setElementType(ElementTypeEnum elementType) {
        this.elementType = elementType;
    }

    public void setActiveResult(ResultTypeEnum activeResult) {
        this.activeResult = activeResult;
    }

    public void setDrawingMode(DrawingModeEnum drawingMode) {
        this.drawingMode = drawingMode;
    }

    public void setProfileFace(List<Float> profileFace) {
        this.profileFace = profileFace;
    }

    public void setCoefficients(List<Float> coefficients) {
        this.coefficients = coefficients;
    }

    public void setvShaderFile(String vShaderFile) {
        this.vShaderFile = vShaderFile;
    }

    public void setfShaderFile(String fShaderFile) {
        this.fShaderFile = fShaderFile;
    }
    public int getIsImport() {
        return isImport;
    }

    public void setElementFile(String elementFile) {
        this.elementFile = elementFile;
    }

    public void setNodeFileMapList(List<Map<ResultTypeEnum, String>> nodeFileMapList) {
        this.nodeFileMapList = nodeFileMapList;
    }

    public void setCameraType(CameraTypeEnum cameraType) {
        this.cameraType = cameraType;
    }

    // 获取数据
    public ElementTypeEnum getElementType() {
        return elementType;
    }

    public ResultTypeEnum getActiveResult() {
        return activeResult;
    }

    public List<Float> getCoefficients() {
        return coefficients;
    }

    public String getvShaderFile() {
        return vShaderFile;
    }

    public String getfShaderFile() {
        return fShaderFile;
    }

    public String getElementFile() {
        return elementFile;
    }
    public String getElementFileForSolve() {
        return elementFileForSolve;
    }
    public String getNodeFileForSolve() {
        return nodeFileForSolve;
    }

    public List<Map<ResultTypeEnum, String>> getNodeFileMapList() {
        return nodeFileMapList;
    }

    public DrawingModeEnum getDrawingMode() {
        return drawingMode;
    }

    public List<Float> getProfileFace() {
        return profileFace;
    }

    public CameraTypeEnum getCameraType() {
        return cameraType;
    }

    // 静态内容，ElementType与Element接口子类的对应关系
    private static Map<ElementTypeEnum, Class<? extends Element>> elementTypeMap;

    static {
        elementTypeMap = new HashMap<>();
        elementTypeMap.put(ElementTypeEnum.TETRAHEDRON, TetrahedronElement.class);
        elementTypeMap.put(ElementTypeEnum.HEXAHEDRON, HexahedronElement.class);
    }

    public static Class<? extends Element> getElementType(ElementTypeEnum elementType) {
        return elementTypeMap.get(elementType);
    }

    // 单例
    private static CMDSetting setting = new CMDSetting();

    public static CMDSetting getDefaultSetting() {
        return setting;
    }

}

