package com.itheima.solve;

import com.itheima.aScenary.container.CMDSetting;
import com.itheima.aScenary.entity.Element;
import com.itheima.aScenary.repository.ElementDataRepository;
import com.itheima.aScenary.repository.NodeRepository;
import com.itheima.aScenary.repository.impl.HexahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TenNodeTetrahedronElementDataRepository;
import com.itheima.aScenary.repository.impl.TetrahedronElementDataRepository;
import com.itheima.domain.Elementor;
import com.itheima.domain.Noder;

import java.util.ArrayList;
import java.util.List;

public class SolveEntrance {
    public void solve(CMDSetting setting) {
        SolveELE solveELE = null;
        switch (setting.getElementType()) {
            case HEXAHEDRON:
                solveELE = new SolveHEX();
                break;
            case TETRAHEDRON:
                solveELE = new SolveTET();
                break;
        }
        solveELE.solveM(setting);
    }
}
