package com.itheima.aScenary.repository.impl;

import com.itheima.aScenary.repository.ElementDataRepository;
import com.itheima.domain.Elementor;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.22
 */
@Component
public class HexahedronElementDataRepository extends ElementDataRepository {

    // 读取六面体网格 TODO: 根据六面体网格文件格式编写
    @Override
    protected void readElementDataList() {

    }
    @Override
    protected void importElementorFromFile() {
        try (BufferedInputStream elementIn = new BufferedInputStream(
                new FileInputStream(elementFile))) {
            Scanner sc = new Scanner(elementIn);
            while (sc.hasNext()) {
                    if (!sc.hasNextInt()) {
                    sc.next();
                    continue;
                }
                Elementor elementor = new Elementor();
                elementor.setID(sc.nextInt());
                for (int i = 0; i < 5; i++) {
                    sc.next();
                }
                elementor.setNode1(sc.nextInt());
                elementor.setNode2(sc.nextInt());
                elementor.setNode3(sc.nextInt());
                elementor.setNode4(sc.nextInt());
                elementor.setNode5(sc.nextInt());
                elementor.setNode6(sc.nextInt());
                elementor.setNode7(sc.nextInt());
                elementor.setNode8(sc.nextInt());
                elementor.setNode9(sc.nextInt());
                elementor.setNode10(sc.nextInt());
                elementor.setNode11(sc.nextInt());
                elementor.setNode12(sc.nextInt());
                elementor.setNode13(sc.nextInt());
                elementor.setNode14(sc.nextInt());
                elementor.setNode15(sc.nextInt());
                elementor.setNode16(sc.nextInt());
                elementor.setNode17(sc.nextInt());
                elementor.setNode18(sc.nextInt());
                elementor.setNode19(sc.nextInt());
                elementor.setNode20(sc.nextInt());
                elementor.setType("HEXAHEDRON");
                elementorList.add(elementor);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
