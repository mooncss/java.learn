package com.example.mall.service;


import com.baomidou.mybatisplus.service.IService;
import com.example.mall.dto.StStageDeliverRelaVo;
import com.example.mall.model.StStageDeliverRela;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StStageDeliverRelaService extends IService<StStageDeliverRela> {

    List<StStageDeliverRelaVo> selectRelaByStageId(@Param("map") Map<String,Object> map);

    Integer selectRelaByStageIdCount(@Param("map") Map<String, Object> map);
}
