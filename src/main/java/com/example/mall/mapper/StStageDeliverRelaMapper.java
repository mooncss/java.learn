package com.example.mall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.dto.StStageDeliverRelaVo;
import com.example.mall.model.StStageDeliverRela;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
public interface StStageDeliverRelaMapper extends BaseMapper<StStageDeliverRela> {

     List<StStageDeliverRelaVo> selectRelaByStageId(@Param("map") Map<String,Object> map);

     Integer selectRelaByStageIdCount(@Param("map") Map<String,Object> map);
}
