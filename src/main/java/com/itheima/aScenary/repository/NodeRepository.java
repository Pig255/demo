package com.itheima.aScenary.repository;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.entity.Node;
import com.itheima.aScenary.enums.ResultTypeEnum;
import com.itheima.aScenary.enums.USSDEnum;
import com.itheima.aScenary.listener.CMDSettingListener;
import com.itheima.aScenary.util.NormalUtil;
import com.itheima.dao.NoderDao;
import com.itheima.domain.Noder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.12
 */
@Component
public class NodeRepository implements CMDSettingListener {
    @Autowired
    private NoderDao noderDao;

    public static NodeRepository nodeRepository;

    @PostConstruct
    public void innit() {
        nodeRepository = this;
        nodeRepository.noderDao = this.noderDao;
    }

    public static void importAction(List<Noder> noderList, ResultTypeEnum type) {
        nodeRepository.noderDao.deleteAll();
        String tpe = String.valueOf(type);
        List<Noder> noderList1 = noderList.subList(0, noderList.size() / 2);
        List<Noder> noderList2 = noderList.subList(noderList.size() / 2, noderList.size());
        nodeRepository.noderDao.insertList(noderList1, tpe);
        nodeRepository.noderDao.insertList(noderList2, tpe);
    }


    private Map<Integer, Node> nodeMap;
    private HashMap<Integer, Noder> nodeMapForImport;
    private float maxData;
    private float minData;

    private CMDSetting setting;

    @Override
    public void init(CMDSetting setting) {

        this.setting = setting;
        this.maxData = 0;
        this.minData = 0;
        this.nodeMap = new HashMap<>();
        this.nodeMapForImport = new HashMap<>();

        ResultTypeEnum activeResult = setting.getActiveResult();
        if (setting.getIsImport() == 1) {
            importNoder(setting);
        }
        addNodeMapFromTable(activeResult);
        //???????????????NodeMap
//        for (int i = 0; i < size; i++) {
//            String oneFile = nodeFileMapList.get(i).get(activeResult);
//            if (!NormalUtil.floatEquals(coefficients.get(i), 0.0f)) {
//                addNodeMapFromFile(oneFile, coefficients.get(i));
//            }
//        }
        System.out.println("?????????: " + maxData + ", ?????????: " + minData);
    }

    private void importNoder(CMDSetting setting) {
        List<Float> coefficients = setting.getCoefficients();
        List<Map<ResultTypeEnum, String>> nodeFileMapList = setting.getNodeFileMapList();
        ResultTypeEnum activeResult = setting.getActiveResult();
        int size = coefficients.size();
        for (int i = 0; i < size; i++) {
            String oneFile = nodeFileMapList.get(i).get(activeResult);
            if (!NormalUtil.floatEquals(coefficients.get(i), 0.0f)) {
                importNoderFromFile(oneFile, coefficients.get(i), activeResult);
            }
        }
        List<Noder> noderList = getValueList(nodeMapForImport);
        importAction(noderList, activeResult);
    }

    private void importNoderFromFile(String nodeFile, float coefficient, ResultTypeEnum type) {
        try (BufferedInputStream nodeIn = new BufferedInputStream(
                new FileInputStream(nodeFile))) {
            Scanner sc = new Scanner(nodeIn);
            while (sc.hasNext()) {
                if (!sc.hasNextDouble()) {
                    sc.next();
                    continue;
                }
                int index = sc.nextInt();
                double locx = sc.nextDouble();
                double locy = sc.nextDouble();
                double locz = sc.nextDouble();
                double data = sc.nextFloat() * coefficient;
                Noder curNoder = this.nodeMapForImport.get(index);
                if (curNoder == null) {
                    curNoder = new Noder();
                    curNoder.setID(index);
                    curNoder.setLOC_X(locx);
                    curNoder.setLOC_Y(locy);
                    curNoder.setLOC_Z(locz);
                    curNoder.setData(data * coefficient);
                    this.nodeMapForImport.put(index, curNoder);
                } else {
                    curNoder.setData(curNoder.getData() + coefficient * data);
                }
            }
        } catch (IOException e) {
            System.out.println("??????????????????: " + nodeFile + "??????????????????????????????");
        }
        System.out.println("??????????????????: " + nodeFile + "????????????");
    }

    private void addNodeMapFromTable(ResultTypeEnum type) {
        List<Noder> list = nodeRepository.noderDao.getAllNoder();
        System.out.println("----------------????????????????????????--------------" + list.size());
        float curMaxData = Float.MIN_VALUE;
        float curMinData = Float.MAX_VALUE;
        for (Noder noder : list) {
            Node curNode = new Node();
            curNode.setIndex(noder.getID());
            float[] coord = new float[3];
            coord[0] = (float) noder.getLOC_X();
            coord[1] = (float) noder.getLOC_Y();
            coord[2] = (float) noder.getLOC_Z();
            curNode.setCoord(coord);
            curNode.setData((float) noder.getU_Y());//????????????????????????
            this.nodeMap.put(noder.getID(), curNode);
            curMaxData = Math.max(curNode.getData(), curMaxData);
            curMinData = Math.min(curNode.getData(), curMinData);
        }
        this.maxData = curMaxData;
        this.minData = curMinData;
    }

    @Override
    public void update(CMDSetting setting) {
        if (NormalUtil.needUpdateNodeFile(setting, this.setting)) {
            init(setting);
        }
    }

    // ????????????????????????????????????
    private void addNodeMapFromFile(String nodeFile, float coefficient) {

        float curMaxData = Float.MIN_VALUE;
        float curMinData = Float.MAX_VALUE;

        try (BufferedInputStream nodeIn = new BufferedInputStream(
                new FileInputStream(nodeFile))) {
            Scanner sc = new Scanner(nodeIn);
            while (sc.hasNext()) {
                if (!sc.hasNextDouble()) {
                    sc.next();
                    continue;
                }
                int index = sc.nextInt();
                float[] coord = new float[3];
                for (int i = 0; i < 3; i++) {
                    coord[i] = sc.nextFloat();
                }
                float data = sc.nextFloat();
                curMaxData = Math.max(curMaxData, data);
                curMinData = Math.min(curMinData, data);

                Node curNode = this.nodeMap.get(index);
                if (curNode == null) {
                    curNode = new Node();
                    curNode.setIndex(index);
                    curNode.setCoord(coord);
                    curNode.setData(data * coefficient);
                    this.nodeMap.put(index, curNode);
                } else {
                    curNode.setData(curNode.getData() + coefficient * data);
                }
            }

            this.maxData = this.maxData + curMaxData * coefficient;
            this.minData = this.minData + curMinData * coefficient;

        } catch (IOException e) {
            System.out.println("??????????????????: " + nodeFile + "??????????????????????????????");
        }
        System.out.println("??????????????????: " + nodeFile + "????????????");
    }

    public Map<Integer, Node> getNodeMap() {
        return nodeMap;
    }

    public float getMaxData() {
        return maxData;
    }

    public float getMinData() {
        return minData;
    }

    public List<Noder> getValueList(HashMap<Integer, Noder> map) {
        List<Noder> noderList = new ArrayList<>();
        Collection<Noder> values = map.values();
        for (Noder noder : values) {
            noderList.add(noder);
        }
        return noderList;
    }

    public void initForSolve(CMDSetting setting) {
        String nodeFile = setting.getNodeFileForSolve();
        List<Noder> noderList = new ArrayList<>();
        try (BufferedInputStream nodeIn = new BufferedInputStream(
                new FileInputStream(nodeFile))) {
            Scanner sc = new Scanner(nodeIn);
            double wu;
            while (sc.hasNext()) {
                if (!sc.hasNextDouble()) {
                    sc.next();
                    continue;
                }
                int index = sc.nextInt();
                double locx = sc.nextDouble();
                double locy = sc.nextDouble();
                double locz = sc.nextDouble();
                wu = sc.nextDouble();
                Noder curNoder = new Noder();
                curNoder.setID(index);
                curNoder.setLOC_X(locx);
                curNoder.setLOC_Y(locy);
                curNoder.setLOC_Z(locz);
                noderList.add(curNoder);
            }
            ResultTypeEnum activeResult = setting.getActiveResult();
            importAction(noderList, activeResult);
        } catch (IOException e) {
            System.out.println("??????????????????: " + nodeFile + "??????????????????????????????");
        }
        System.out.println("??????????????????: " + nodeFile + "????????????");
    }

    public List<Noder> getNoderListForSolve() {
        List<Noder> noderList=nodeRepository.noderDao.getAllNoder();
        return noderList;
    }

    public void updateResult(USSDEnum ussdEnum, Noder noder){
        switch (ussdEnum){
            case U:
                nodeRepository.noderDao.updateU(noder.getID(),noder.getU_X(),noder.getU_Y(),noder.getU_Z());
                break;
            case STRAIN:
                break;
            case STRESS:
                break;
        }
    }
}
