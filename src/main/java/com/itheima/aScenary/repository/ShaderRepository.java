package com.itheima.aScenary.repository;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.listener.CMDSettingListener;
import com.itheima.aScenary.util.NormalUtil;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.12
 */
public class ShaderRepository implements CMDSettingListener {

    private String vertexShader;
    private String fragmentShader;

    private String vShaderFile;
    private String fShaderFile;

    @Override
    public void init(CMDSetting setting) {
        vShaderFile = setting.getvShaderFile();
        fShaderFile = setting.getfShaderFile();
        this.vertexShader = NormalUtil.readStringFromFile(vShaderFile);
        this.fragmentShader = NormalUtil.readStringFromFile(fShaderFile);
    }

    @Override
    public void update(CMDSetting setting) {
        if (!setting.getvShaderFile().equals(vertexShader)) {
            vShaderFile = setting.getvShaderFile();
            vertexShader = NormalUtil.readStringFromFile(vShaderFile);
        }
        if (!setting.getfShaderFile().equals(fragmentShader)) {
            fShaderFile = setting.getfShaderFile();
            fragmentShader = NormalUtil.readStringFromFile(fShaderFile);
        }
    }

    public String getVertexShaderCode() {
        return vertexShader;
    }

    public String getFragmentShaderCode() {
        return fragmentShader;
    }


}
