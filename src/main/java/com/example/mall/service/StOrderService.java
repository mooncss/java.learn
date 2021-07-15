package com.example.mall.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.example.mall.common.CommonResult;
import com.example.mall.model.StOrder;
import com.example.mall.model.StOrderQueryVo;
import com.example.mall.model.StOrderTotalVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StOrderService extends IService<StOrder> {
	
	Page<StOrderQueryVo> selectPageByCondition(Page<StOrderQueryVo> page,String commitMobile, String dPhone);

	List<StOrderQueryVo> selectLogisticRecord(Map<String,Object> map);

	int selectLogisticRecordCount(@Param("map") Map<String,Object> map);

	List<StOrderTotalVo> selectLast15day(@Param("map") Map<String,Object> map);

	StOrderTotalVo selectlast30total( Map<String,Object> map);

	StOrderTotalVo selectalltotal( Map<String,Object> map);
}
