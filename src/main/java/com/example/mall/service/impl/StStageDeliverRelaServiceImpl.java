package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dto.StStageDeliverRelaVo;
import com.example.mall.mapper.StStageDeliverRelaMapper;
import com.example.mall.model.StStageDeliverRela;
import com.example.mall.service.StStageDeliverRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class StStageDeliverRelaServiceImpl extends ServiceImpl<StStageDeliverRelaMapper, StStageDeliverRela>
        implements StStageDeliverRelaService {

    @Autowired
    StStageDeliverRelaMapper stStageDeliverRelaMapper;

    @Override
    public List<StStageDeliverRelaVo> selectRelaByStageId(Map<String, Object> map) {
        return stStageDeliverRelaMapper.selectRelaByStageId(map);
    }

    @Override
    public Integer selectRelaByStageIdCount(Map<String, Object> map){
        return stStageDeliverRelaMapper.selectRelaByStageIdCount(map);
    }
}
