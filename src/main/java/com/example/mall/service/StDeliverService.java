package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.dto.StDeliverVO;
import com.example.mall.model.StDeliver;

import java.util.List;
import java.util.Map;


public interface StDeliverService extends IService<StDeliver> {

    List<StDeliverVO> selectallDelivers(Map<String,Object> map);

}
