package com.itheima.dao;

import com.itheima.domain.Elementor;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ElementorDao {
    int insertListOfSix(List<Elementor> elementorList);

    int insertListOfFour(List<Elementor> elementorList);

    int deleteAll();

    @Select("select node1 from element_table where ID=1")
    int finda();

    List<Elementor> getAllElementor();
}
