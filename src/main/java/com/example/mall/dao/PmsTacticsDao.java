package com.example.mall.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.model.PmsTactics;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PmsTacticsDao extends BaseMapper<PmsTactics> {
}
