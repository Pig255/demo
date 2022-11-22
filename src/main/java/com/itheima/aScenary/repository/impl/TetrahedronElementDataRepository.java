package com.itheima.aScenary.repository.impl;

import com.itheima.aScenary.entity.impl.TetrahedronElementData;
import com.itheima.aScenary.repository.ElementDataRepository;
import com.itheima.dao.ElementorDao;
import com.itheima.domain.Elementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
public class TetrahedronElementDataRepository extends ElementDataRepository {

//    @Autowired
//    public ElementorDao elementorDao;
//
//    public static TetrahedronElementDataRepository tetrahedronElementDataRepository;
//
//    @PostConstruct
//    public void inniit() {
//        elementDataRepository = this;
//        elementDataRepository.elementorDao = this.elementorDao;
//    }
    // 读取四面体网格
    @Override
    protected void readElementDataList() {
        try (BufferedInputStream elementIn = new BufferedInputStream(
                new FileInputStream(elementFile))) {
            Scanner sc = new Scanner(elementIn);
            while (sc.hasNext()) {
                if (!sc.hasNextInt()) {
                    sc.next();
                    continue;
                }
                for (int i = 0; i < 6; i++) {
                    sc.next();
                }
                int[] nodes = new int[4];
                for (int i = 0; i < 4; i++) {
                    nodes[i] = sc.nextInt();
                }
                for (int i = 0; i < 6; i++) {
                    if (sc.hasNext()) {
                        sc.next();
                    }
                }
                elementDataList.add(new TetrahedronElementData(nodes));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                elementor.setType("TETRAHEDRON");
                elementorList.add(elementor);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
