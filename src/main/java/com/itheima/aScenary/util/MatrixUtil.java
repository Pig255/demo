package com.itheima.aScenary.util;

import glm.vec._3.Vec3;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.22
 */
// 矩阵计算相关工具
// TODO: 当前依然是glm与自定义共用，可以考虑全部换成自定义，保持矩阵定义的一致
public class MatrixUtil {

    // 向量叉乘
    public static Vec3 cross(Vec3 x, Vec3 y) {
        return new Vec3(
                x.y * y.z - y.y * x.z,
                x.z * y.x - y.z * x.x,
                x.x * y.y - y.x * x.y
        );
    }

    // 向量点乘
    public static float dot(Vec3 x, Vec3 y) {
        return x.x * y.x
                + x.y * y.y
                + x.z * y.z;
    }

    // 向量标准化
    public static Vec3 normalize(Vec3 v) {
        float q = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        return new Vec3(
                v.x / q,
                v.y / q,
                v.z / q
        );
    }

    // 向量相加
    public static Vec3 addVec3(Vec3 a, Vec3 b) {
        return new Vec3(
                a.x + b.x,
                a.y + b.y,
                a.z + b.z
        );
    }

    // 向量相减
    public static Vec3 subVec3(Vec3 a, Vec3 b) {
        return new Vec3(
                a.x - b.x,
                a.y - b.y,
                a.z - b.z
        );
    }

    // 向量数乘
    public static Vec3 mulNum(Vec3 vec, float num) {
        return new Vec3(
                vec.x * num,
                vec.y * num,
                vec.z * num
        );
    }

    // 矩阵与向量相乘
    public static Vec3 mulMatrix3(Vec3 vec3, double[][] matrix) {
        return new Vec3(
                matrix[0][0] * vec3.x + matrix[0][1] * vec3.y + matrix[0][2] * vec3.z,
                matrix[1][0] * vec3.x + matrix[1][1] * vec3.y + matrix[1][2] * vec3.z,
                matrix[2][0] * vec3.x + matrix[2][1] * vec3.y + matrix[2][2] * vec3.z
        );
    }

    // 方阵相乘
    public static float[][] mulMatrix(float[][] m1, float[][] m2) {
        int size = m1.length;
        float[][] m = new float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    m[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return m;
    }

}
