package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.model.SmsStage;
import com.example.mall.model.StageDeliverQueryVo;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsStageService extends IService<SmsStage> {
	
	List<StageDeliverQueryVo> getDeliverListByStageId(String stageId);
}
