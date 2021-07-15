package com.example.mall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.model.SmsStage;
import com.example.mall.model.StageDeliverQueryVo;

public interface SmsStageDao extends BaseMapper<SmsStage> {
	
	List<StageDeliverQueryVo> getDeliverListByStageId(@Param("stageId") String stageId);
}
