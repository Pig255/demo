package com.itheima;

import com.itheima.aScenary.container.CMDContainer;
import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.listener.GLListener;
import com.itheima.solve.SolveEntrance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolveMain {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println("--------启动成功--------");
        CMDSetting setting = CMDSetting.getDefaultSetting();
        SolveEntrance solveEntrance=new SolveEntrance();
        solveEntrance.solve(setting);
        CMDContainer.init(setting);
        GLListener glListener = new GLListener();
        glListener.setUp();
    }


}
