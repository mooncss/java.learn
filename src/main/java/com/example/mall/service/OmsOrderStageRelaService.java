package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.dao.OmsOrderStageRelaDao;
import com.example.mall.dto.OmsOrderStageRelaNode;
import com.example.mall.model.OmsOrderStageRela;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OmsOrderStageRelaService extends IService<OmsOrderStageRela> {

    //查询出站
    List<OmsOrderStageRelaNode> getArList(@Param("map") Map<String,Object> map);
    //入站
    List<OmsOrderStageRelaNode> getLeavList(@Param("map") Map<String,Object> map);

    //入站数量
    Integer getArCount(@Param("map") Map<String,Object> map);
    //出站数量
    Integer getLeaveCount(@Param("map") Map<String,Object> map);

}
