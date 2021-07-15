package com.example.mall.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.dto.OmsOrderStageRelaNode;
import com.example.mall.model.OmsOrderStageRela;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OmsOrderStageRelaDao extends BaseMapper<OmsOrderStageRela> {
    List<OmsOrderStageRelaNode> getArList(@Param("map") Map<String,Object> map);

    List<OmsOrderStageRelaNode> getLeavList(@Param("map") Map<String,Object> map);

    Integer getArCount(@Param("map") Map<String,Object> map);

    Integer getLeaveCount(@Param("map") Map<String, Object> map);

}
