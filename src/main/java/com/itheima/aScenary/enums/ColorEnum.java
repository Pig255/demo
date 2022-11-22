package com.itheima.aScenary.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
// RGB颜色
public enum ColorEnum {

    RED(new double[]{1.0, 0.0, 0.0}, "红色"),
    ORANGE(new double[]{1.0, 0.698, 0.0}, "橙色"),
    YELLOW(new double[]{1.0, 1.0, 0.0}, "黄色"),
    GRASS_GREEN(new double[]{0.698, 1.0, 0.0}, "草绿色"),
    GREEN(new double[]{0.0, 1.0, 0.0}, "绿色"),
    TURQUOISE(new double[]{0.0, 1.0, 0.698}, "绿松石色"),
    SKY_BLUE(new double[]{0.0, 1.0, 1.0}, "天蓝色"),
    WATHET(new double[]{0.0, 0.698, 1.0}, "浅蓝色"),
    BLUE(new double[]{0.0, 0.0, 1.0}, "蓝色"),
    UNKNOWN(new double[]{0.0, 0.0, 0.0}, "未知颜色");

    private double[] colorCode; // 归一化后的RGB颜色值
    private String describe; // 描述

    private ColorEnum(double[] colorCode, String describe) {
        this.colorCode = colorCode;
        this.describe = describe;
    }

    public double[] getColorCode() {
        return colorCode;
    }

    private static Map<Integer, ColorEnum> colorMap;

    static {
        colorMap = new HashMap<>();
        colorMap.put(0, BLUE);
        colorMap.put(1, WATHET);
        colorMap.put(2, SKY_BLUE);
        colorMap.put(3, TURQUOISE);
        colorMap.put(4, GREEN);
        colorMap.put(5, GRASS_GREEN);
        colorMap.put(6, YELLOW);
        colorMap.put(7, ORANGE);
        colorMap.put(8, RED);
        colorMap.put(-1, UNKNOWN);
    }

    public static ColorEnum getColorByIndex(int i) {
        ColorEnum colorEnum = colorMap.get(i);
        return colorEnum == null ? UNKNOWN : colorEnum;
    }
}
