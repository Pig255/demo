package com.itheima.aScenary.util.impl;

import com.itheima.aScenary.util.Camera;
import com.itheima.aScenary.util.MatrixUtil;
import com.jogamp.newt.event.KeyEvent;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

import static com.itheima.aScenary.util.NormalUtil.angleToRadian;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.21
 */
// 四元数相机
public class QuaternionCamera extends Camera {
    private Vec3 position; // 相机位置
    private float[][] rotate; // 累积旋转矩阵
    private float movementSpeed; // 平移速度
    private float mouseSensitivity; // 旋转角速度
    private Vec3 axisI; // 当前时刻旋转轴
    private float thetaI; // 当前时刻转角（角度）

    public QuaternionCamera() {
        position = new Vec3();
        rotate = new float[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };
        movementSpeed = 3.0f;
        mouseSensitivity = 1f;
        axisI = new Vec3(0, 0, 1);
        thetaI = 0.0f;
    }

    @Override
    public void setPosition(Vec3 position) {
        this.position = position;
    }

    @Override
    public Mat4 getViewMartrix() {
        float[] view = {
                rotate[0][0], rotate[1][0], rotate[2][0], 0,
                rotate[0][1], rotate[1][1], rotate[2][1], 0,
                rotate[0][2], rotate[1][2], rotate[2][2], 0,
                position.x, position.y, position.z, 1
        };

        return new Mat4(view);
    }

    @Override
    public void processKeyboard(KeyEvent event, long deltaTime) {
        float velocity = movementSpeed * deltaTime;
        float angVelocity = mouseSensitivity * deltaTime;
        switch (event.getKeyCode()) {
            // 平移
            case KeyEvent.VK_Q:
                position.set(position.x, position.y, position.z + velocity);
                break;
            case KeyEvent.VK_E:
                position.set(position.x, position.y, position.z - velocity);
                break;
            case KeyEvent.VK_W:
                position.set(position.x, position.y + velocity, position.z);
                break;
            case KeyEvent.VK_S:
                position.set(position.x, position.y - velocity, position.z);
                break;
            case KeyEvent.VK_A:
                position.set(position.x + velocity, position.y, position.z);
                break;
            case KeyEvent.VK_D:
                position.set(position.x - velocity, position.y, position.z);
                break;
            // 旋转
            case KeyEvent.VK_R:
                axisI.set(0, 0, 1);
                thetaI = angVelocity;
                break;
            case KeyEvent.VK_T:
                axisI.set(0, 0, 1);
                thetaI = -angVelocity;
                break;
            case KeyEvent.VK_F:
                axisI.set(0, 1, 0);
                thetaI = angVelocity;
                break;
            case KeyEvent.VK_G:
                axisI.set(0, 1, 0);
                thetaI = -angVelocity;
                break;
            case KeyEvent.VK_V:
                axisI.set(1, 0, 0);
                thetaI = angVelocity;
                break;
            case KeyEvent.VK_B:
                axisI.set(1, 0, 0);
                thetaI = -angVelocity;
                break;
            default:
                return;
        }

        updateRotate();
    }

    // 根据当前转轴和转角更新旋转矩阵
    private void updateRotate() {
        float a = (float) Math.cos(angleToRadian(thetaI / 2));
        float b = axisI.x * (float) Math.sin(angleToRadian(thetaI / 2));
        float c = axisI.y * (float) Math.sin(angleToRadian(thetaI / 2));
        float d = axisI.z * (float) Math.sin(angleToRadian(thetaI / 2));
        float[][] matrix = {
                {1 - 2 * c * c - 2 * d * d, 2 * b * c - 2 * a * d, 2 * a * c + 2 * b * d},
                {2 * b * c + 2 * a * d, 1 - 2 * b * b - 2 * d * d, 2 * c * d - 2 * a * b},
                {2 * b * d - 2 * a * c, 2 * a * b + 2 * c * d, 1 - 2 * b * b - 2 * c * c}
        };
        rotate = MatrixUtil.mulMatrix(matrix, rotate);
    }

}
