package com.itheima.aScenary.container;


import com.itheima.aScenary.listener.CMDSettingListener;
import com.itheima.aScenary.repository.ElementDataRepository;
import com.itheima.aScenary.repository.FaceRepository;
import com.itheima.aScenary.repository.NodeRepository;
import com.itheima.aScenary.repository.ShaderRepository;
import com.itheima.aScenary.repository.impl.HexahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TenNodeTetrahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TetrahedronElementDataRepository;
import com.itheima.aScenary.util.Camera;
import com.itheima.aScenary.util.impl.EulerCamera;
import com.itheima.aScenary.util.impl.QuaternionCamera;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.12
 */
// 容器，发布-订阅模式的管理者
// TODO:
//  1. 新定义的类，凡要引用setting.json者，皆需要在init()和update()方法注册;
//  2. 可以用spring的bean管理工具替代，或者写成注解的形式.
public class CMDContainer {

    // BeanMap
    private static Map<Class<? extends CMDSettingListener>, CMDSettingListener> listenerMap;

    // 上一次的setting
    private static CMDSetting lastSetting;


    // 初始化设置，两个阶段：1. 确定抽象类的子类；2. 子类中注入设置
    public static void init(CMDSetting setting) {
        listenerMap = new LinkedHashMap<>();
        lastSetting = setting;

        // 单元数据加载类
        ElementDataRepository elementDataRepository = null;
        switch (setting.getElementType()) {
            case TETRAHEDRON:
                elementDataRepository = new TetrahedronElementDataRepository();
                break;
            case HEXAHEDRON:
                elementDataRepository = new HexahedronElementDataRepository();
                break;
            case TEN_NODE_TETRAHEDRON:
                elementDataRepository = new TenNodeTetrahedronElementDataRepository();
                break;
        }
        listenerMap.put(ElementDataRepository.class, elementDataRepository);

        // 节点数据加载类
        listenerMap.put(NodeRepository.class, new NodeRepository());

        // 着色器加载类
        listenerMap.put(ShaderRepository.class, new ShaderRepository());

        // 相机类
        Camera camera = null;
        switch (setting.getCameraType()) {
            case EULER:
                camera = new EulerCamera();
                break;
            case QUATERNION:
                camera = new QuaternionCamera();
                break;
        }
        listenerMap.put(Camera.class, camera);

        // 面元加载类
        listenerMap.put(FaceRepository.class, new FaceRepository());

        // 阶段2，执行init()方法
        for (CMDSettingListener listener : listenerMap.values()) {
            listener.init(setting);
        }
    }

    // 更新设置
    public static void update(CMDSetting setting) {

        if (!setting.getElementType().equals(lastSetting.getElementType())) {
            ElementDataRepository elementDataRepository = null;
            switch (setting.getElementType()) {
                case TETRAHEDRON:
                    elementDataRepository = new TetrahedronElementDataRepository();
                    break;
                case HEXAHEDRON:
                    elementDataRepository = new HexahedronElementDataRepository();
            }
            listenerMap.put(ElementDataRepository.class, elementDataRepository);
        }

        if (!setting.getCameraType().equals(lastSetting.getCameraType())) {
            Camera camera = null;
            switch (setting.getCameraType()) {
                case EULER:
                    camera = new EulerCamera();
                    break;
                case QUATERNION:
                    camera = new QuaternionCamera();
                    break;
            }
            listenerMap.put(Camera.class, camera);
        }

        // 阶段2，指定update()方法
        for (CMDSettingListener listener : listenerMap.values()) {
            listener.update(setting);
        }
    }

    // 获取bean
    public static <T> T getListner(Class<T> listenerClass) {
        return (T) listenerMap.get(listenerClass);
    }
}
