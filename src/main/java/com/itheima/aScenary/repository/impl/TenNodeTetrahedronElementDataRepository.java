package com.itheima.aScenary.repository.impl;

import com.itheima.aScenary.entity.impl.TenNodeTetrahedronElementData;
import com.itheima.aScenary.repository.ElementDataRepository;
import com.itheima.domain.Elementor;
import jogamp.graph.font.typecast.ot.table.ID;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.05.23
 */
@Component
public class TenNodeTetrahedronElementDataRepository extends ElementDataRepository {

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
                int[] nodes = new int[10];
                for (int i = 0; i < 10; i++) {
                    nodes[i] = sc.nextInt();
                }
                elementDataList.add(new TenNodeTetrahedronElementData(nodes));
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
                elementor.setType("TEN_NODE_TETRAHEDRON");
                elementorList.add(elementor);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
