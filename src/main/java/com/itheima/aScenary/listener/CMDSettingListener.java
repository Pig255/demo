package com.itheima.aScenary.listener;

import com.itheima.aScenary.container.CMDSetting;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.12
 */
// setting订阅者
public interface CMDSettingListener {
    public void init(CMDSetting setting);

    public void update(CMDSetting setting);
}
