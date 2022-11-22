package com.itheima.aScenary.enums;


/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
// 有限元结果类型
// TODO: 仅考虑了15个张量分量，应力强度等需要二次计算的结果尚待添加
public enum ResultTypeEnum {

    // 变形
    DEFORMATION_X,
    DEFORMATION_Y,
    DEFORMATION_Z,

    // 应力
    STRESS_X,
    STRESS_Y,
    STRESS_Z,
    STRESS_XY,
    STRESS_XZ,
    STRESS_YZ,

    // 应变
    STRAIN_X,
    STRAIN_Y,
    STRAIN_Z,
    STRAIN_XY,
    STRAIN_XZ,
    STRAIN_YZ;

}
