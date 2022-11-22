package com.itheima.aScenary.repository;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.entity.ElementData;
import com.itheima.aScenary.entity.Node;
import com.itheima.aScenary.entity.impl.TetrahedronElementData;
import com.itheima.aScenary.listener.CMDSettingListener;
import com.itheima.dao.ElementorDao;
import com.itheima.domain.Elementor;
import com.itheima.domain.Noder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.12
 */
// 加载单元数据
@Component
@Data
public abstract class ElementDataRepository implements CMDSettingListener {
    @Autowired
    private ElementorDao elementorDao;

    public static ElementDataRepository elementDataRepository;

    @PostConstruct
    public void innit() {
        elementDataRepository = this;
        elementDataRepository.elementorDao = this.elementorDao;
    }


    protected String elementFile;
    protected List<ElementData> elementDataList;
    protected List<Elementor> elementorList;

    @Override
    public void init(CMDSetting setting) {
        elementFile = setting.getElementFile();
        elementDataList = new ArrayList<>();
        elementorList = new ArrayList<>();
        if (setting.getIsImport() == 1) {
            importElementor();
        }
        //readElementDataList();
        readElementDataListFromTable();
    }

    private void readElementDataListFromTable() {
        List<Elementor> list = elementDataRepository.elementorDao.getAllElementor();
        System.out.println("----------------单元数据读库成功--------------" + list.size());
        for (Elementor elementor : list) {
            int[] nodes = new int[4];
            nodes[0]=elementor.getNode1();
            nodes[1]=elementor.getNode2();
            nodes[2]=elementor.getNode3();
            nodes[3]=elementor.getNode4();
            elementDataList.add(new TetrahedronElementData(nodes));
        }
    }

    @Override
    public void update(CMDSetting setting) {
        String newElementFile = setting.getElementFile();
        if (!elementFile.equals(newElementFile)) {
            this.init(setting);
        }
    }
    public void importElementor(){
        importElementorFromFile();
        List<Elementor> tempElementorList = elementorList;
        importAction(tempElementorList);
    }

    public static void importAction(List<Elementor> list) {
        int p = list.get(0).getNode11();
        elementDataRepository.elementorDao.deleteAll();
        List<Elementor> list1 = list.subList(0, list.size() / 2);
        List<Elementor> list2 = list.subList(list.size() / 2, list.size());
        if (p == 0) {
            elementDataRepository.elementorDao.insertListOfFour(list1);
            System.out.println("----------四面体单元入库成功-----------");
            elementDataRepository.elementorDao.insertListOfFour(list2);
            System.out.println("----------四面体单元入库成功-----------");
        } else {
            elementDataRepository.elementorDao.insertListOfSix(list1);
            elementDataRepository.elementorDao.insertListOfSix(list2);
            System.out.println("----------六面体单元入库成功-----------");
        }

    }

    public void initForSolve(CMDSetting setting) {
        elementFile = setting.getElementFileForSolve();
        elementDataList = new ArrayList<>();
        elementorList = new ArrayList<>();
        importElementor();
        readElementDataListFromTable();
    }

    // 读取网格
    protected abstract void readElementDataList();

    protected abstract void importElementorFromFile();

    public List<ElementData> getElementDataList() {
        return elementDataList;
    }

    public List<Elementor> getElementListForSolve() {
        List<Elementor> list = elementDataRepository.elementorDao.getAllElementor();
        return list;
    }
}
