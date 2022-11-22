package com.itheima.service.impl;

import com.itheima.dao.ElementorDao;
import com.itheima.domain.Elementor;
import com.itheima.service.ElementorService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Data
public class ElementorServiceImpl implements ElementorService {

    @Autowired
    private ElementorDao elementorDao;
    @Override
    public List<Elementor> findAll() {
        return null;
    }

    @Override
    public int finda() {
        return elementorDao.finda();
    }
}
