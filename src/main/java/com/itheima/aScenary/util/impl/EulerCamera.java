package com.itheima.aScenary.util.impl;

import com.itheima.aScenary.util.Camera;
import com.jogamp.newt.event.KeyEvent;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

import static com.itheima.aScenary.util.MatrixUtil.*;
import static com.itheima.aScenary.util.NormalUtil.angleToRadian;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
public class EulerCamera extends Camera {

    private Vec3 position; // 相机位置
    private Vec3 front; // 相机朝向，前轴
    private Vec3 up; // 上轴
    private Vec3 right; // 右轴
    private Vec3 wordUp; // 上向量
    private float yaw; // 欧拉角
    private float pitch; // 欧拉角
    private float movementSpeed; // 相机移动速度
    private float mouseSensitivity; // 鼠标灵敏度

    public EulerCamera() {
        position = new Vec3(0.0f, 0.0f, 0.0f);
        wordUp = new Vec3(0.0f, 1.0f, 0.0f);
        yaw = -90.f;
        pitch = 0.0f;
        movementSpeed = 3.f;
        mouseSensitivity = 0.1f;
        updateCameraVectors();
    }

    @Override
    public void setPosition(Vec3 position) {
        this.position = position;
    }

    @Override
    public Mat4 getViewMartrix() {
        // 绕模型本身旋转的观察矩阵，注意这里看起来转置过了，但是glm里的矩阵就要这么设置
        float[] viewMartrix = {
                right.x, up.x, front.x, 0,
                right.y, up.y, front.y, 0,
                right.z, up.z, front.z, 0,
                position.x, position.y, position.z, 1
        };
        return new Mat4(viewMartrix);

        // return Glm.lookAt(position, NormalUtil.addVec3(position, front), up, new Mat4());
    }

    @Override
    public void processKeyboard(KeyEvent event, long deltaTime) {
        float velocity = movementSpeed * deltaTime;
        float angleVelocity = mouseSensitivity * deltaTime;
        switch (event.getKeyCode()) {
            // 平移
            case KeyEvent.VK_Q:
                position = addVec3(position, mulNum(front, velocity));
                break;
            case KeyEvent.VK_E:
                position = subVec3(position, mulNum(front, velocity));
                break;
            case KeyEvent.VK_A:
                position = subVec3(position, mulNum(right, velocity));
                break;
            case KeyEvent.VK_D:
                position = addVec3(position, mulNum(right, velocity));
                break;
            case KeyEvent.VK_W:
                position = addVec3(position, mulNum(up, velocity));
                break;
            case KeyEvent.VK_S:
                position = subVec3(position, mulNum(up, velocity));
                break;
            // 旋转
            case KeyEvent.VK_F:
                yaw += angleVelocity;
                break;
            case KeyEvent.VK_H:
                yaw -= angleVelocity;
                break;
            case KeyEvent.VK_T:
                pitch += angleVelocity;
                break;
            case KeyEvent.VK_G:
                pitch -= angleVelocity;
                break;
        }
        updateCameraVectors();
    }


    // 由欧拉角计算相机角度
    private void updateCameraVectors() {
        front = new Vec3();
        front.x = (float) Math.cos(angleToRadian(yaw)) * (float) Math.cos(angleToRadian(pitch));
        front.y = (float) Math.sin(angleToRadian(pitch));
        front.z = (float) Math.sin(angleToRadian(yaw)) * (float) Math.cos(angleToRadian(pitch));
        front = normalize(front);

        right = cross(front, wordUp);
        right = normalize(right);

        up = cross(right, front);
        up = normalize(up);
    }


}