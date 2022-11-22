package com.itheima.aScenary.util;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.listener.CMDSettingListener;
import com.jogamp.newt.event.KeyEvent;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.22
 */
// 相机抽象类
public abstract class Camera implements CMDSettingListener {

    @Override
    public void init(CMDSetting setting) {
    }

    @Override
    public void update(CMDSetting setting) {
    }

    // 定义初始位置
    public abstract void setPosition(Vec3 position);

    // 获取观察矩阵
    public abstract Mat4 getViewMartrix();

    // 对键盘的响应
    public abstract void processKeyboard(KeyEvent event, long deltaTime);

}
