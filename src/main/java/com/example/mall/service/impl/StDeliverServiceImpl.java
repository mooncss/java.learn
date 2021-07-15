package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dto.StDeliverVO;
import com.example.mall.mapper.StDeliverMapper;
import com.example.mall.model.StDeliver;
import com.example.mall.service.StDeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StDeliverServiceImpl extends ServiceImpl<StDeliverMapper, StDeliver> implements StDeliverService {

    @Autowired
    StDeliverMapper stDeliverMapper;


    @Override
    public List<StDeliverVO> selectallDelivers(Map<String,Object> map) {
        return stDeliverMapper.selectallDelivers(map);
    }
}
