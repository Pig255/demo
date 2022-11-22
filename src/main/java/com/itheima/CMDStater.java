package com.itheima;

import com.itheima.aScenary.container.CMDContainer;
import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.listener.GLListener;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.13
 */
public class CMDStater {
    public static void main(String[] args) {
        CMDSetting setting = CMDSetting.getDefaultSetting();
        CMDContainer.init(setting);
        GLListener glListener = new GLListener();
        glListener.setUp();

    }
}
