package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.OmsOrderStageRelaDao;
import com.example.mall.dto.OmsOrderStageRelaNode;
import com.example.mall.model.OmsOrderStageRela;
import com.example.mall.service.OmsOrderStageRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OmsOrderStageRelaServiceImpl  extends ServiceImpl<OmsOrderStageRelaDao, OmsOrderStageRela>
        implements OmsOrderStageRelaService
{

    @Autowired
    OmsOrderStageRelaDao omsOrderStageRelaDao;
    @Override
    public List<OmsOrderStageRelaNode> getArList(Map<String, Object> map) {
        return omsOrderStageRelaDao.getArList(map);
    }

    @Override
    public List<OmsOrderStageRelaNode> getLeavList(Map<String, Object> map) {
        return omsOrderStageRelaDao.getLeavList(map);
    }

    @Override
    public Integer getArCount(Map<String, Object> map) {
        return omsOrderStageRelaDao.getArCount(map);
    }

    @Override
    public Integer getLeaveCount(Map<String, Object> map) {
        return omsOrderStageRelaDao.getLeaveCount(map);
    }
}
