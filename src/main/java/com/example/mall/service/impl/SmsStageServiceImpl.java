package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.SmsStageDao;
import com.example.mall.model.SmsStage;
import com.example.mall.model.StageDeliverQueryVo;
import com.example.mall.service.SmsStageService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SmsStageServiceImpl extends ServiceImpl<SmsStageDao, SmsStage> implements SmsStageService {
	
    @Autowired
    SmsStageDao smsStageDao;

	@Override
	public List<StageDeliverQueryVo> getDeliverListByStageId(String stageId) {
		return smsStageDao.getDeliverListByStageId(stageId);
	}
	
}
