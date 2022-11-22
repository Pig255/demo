package com.itheima.aScenary.util;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.entity.Node;
import com.itheima.aScenary.enums.ResultTypeEnum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
public class NormalUtil {

    // 角度制转弧度制
    public static double angleToRadian(double angle) {
        return angle / 180.0 * Math.PI;
    }

    // 弧度转角度
    public static double radianToAngle(double radian) {
        return radian / Math.PI * 180.0;
    }

    // 判断浮点数相等
    public static boolean floatEquals(float a, float b) {
        return Math.abs(a - b) < 1e-3;
    }

    // 获取项目根路径
    public static String getRootPath() {
        return NormalUtil.class.getResource("/").getFile();
    }

    // 二进制转字符串
    public static String byteArrToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    // 从文件中读取字符串
    public static String readStringFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new FileReader(filePath))) {
            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
                sb.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    // setting中获取当前使用的node文件
    private static List<String> getNodeFileFromSetting(CMDSetting setting) {
        List<String> result = new ArrayList<>();
        for (Map<ResultTypeEnum, String> nodeFileMap : setting.getNodeFileMapList()) {
            result.add(nodeFileMap.get(setting.getActiveResult()));
        }
        return result;
    }

    // 是否需要重新加载文件
    public static boolean needUpdateNodeFile(CMDSetting setting1, CMDSetting setting2) {
        boolean flag = setting1.getCoefficients().size() != setting2.getCoefficients().size();
        flag = flag || getNodeFileFromSetting(setting1).size() != getNodeFileFromSetting(setting2).size();
        if (flag) return true;

        int size = setting1.getCoefficients().size();
        for (int i = 0; i < size; i++) {
            boolean f1 = !NormalUtil.floatEquals(setting1.getCoefficients().get(i), setting2.getCoefficients().get(i));
            boolean f2 = !getNodeFileFromSetting(setting1).get(i).equals(getNodeFileFromSetting(setting2).get(i));
            if (f1 || f2) {
                return true;
            }
        }
        return false;
    }

    // 两节点线性插值
    public static Node interpolation(Node n1, Node n2, float lineData) {
        Node n = new Node();
        n.setData(lineData);
        for (int i = 0; i < 3; i++) {
            n.getCoord()[i] = n1.getCoord()[i]
                    + (lineData - n1.getData()) / (n2.getData() - n1.getData())
                    * (n2.getCoord()[i] - n1.getCoord()[i]);
        }
        return n;
    }

    // 三节点线性插值

    // 面与线段的交点
    public static Node getIntersection(Node n1, Node n2, List<Float> profile) {
        Node n = new Node();
        float p1 = n1.getCoord()[0] * profile.get(0) + n1.getCoord()[1] * profile.get(1)
                + n1.getCoord()[2] * profile.get(2) + profile.get(3);
        float p2 = n2.getCoord()[0] * profile.get(0) + n2.getCoord()[1] * profile.get(1)
                + n2.getCoord()[2] * profile.get(2) + profile.get(3);
        if (p1 * p2 < 0) {
            for (int i = 0; i < 3; i++) {
                n.getCoord()[i] = n1.getCoord()[i] - p1 * (n2.getCoord()[i] - n1.getCoord()[i]) / (p2 - p1);
            }
            n.setData(n1.getData() - p1 * (n2.getData() - n1.getData()) / (p2 - p1));
        }
        return n;
    }

    // 顶点按照剖面左右排序
    public static void sortNodeByFlag(float[] flag, Node[] nodes) {
        int size = flag.length;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (flag[i] > flag[j]) {
                    float tempNum = flag[i];
                    flag[i] = flag[j];
                    flag[j] = tempNum;
                    Node tempNode = nodes[i];
                    nodes[i] = nodes[j];
                    nodes[j] = tempNode;
                }
            }
        }
    }

    // 根据节点数据获取颜色索引
    public static int getIndexByData(float data, float maxData, float minData) {
        if (data <= minData) return 0;
        if (data >= maxData) return 8;
        return (int) ((data - minData) / (maxData - minData) * 9);
    }
}
