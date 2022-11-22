package com.itheima.service.impl;

import com.itheima.dao.NoderDao;
import com.itheima.domain.Noder;
import com.itheima.service.NoderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class NoderServiceImpl implements NoderService {
    @Autowired
    private NoderDao noderDao;
    @Override
    public List<Noder> findAll() {
        return noderDao.findAll();
    }

    @Override
    public double finda() {
        return noderDao.finda();
    }
}
