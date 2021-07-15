package com.example.mall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.example.mall.common.CommonResult;
import com.example.mall.model.StOrder;
import com.example.mall.model.StOrderQueryVo;
import java.util.List;
import java.util.Map;

import com.example.mall.model.StOrderTotalVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StOrderMapper extends BaseMapper<StOrder> {
	
	List<StOrderQueryVo> selectPageByCondition(Pagination page,@Param("commitMobile") String commitMobile, @Param("dPhone") String dPhone);

	List<StOrderQueryVo> selectLogisticRecord(@Param("map") Map<String,Object> map);

	int selectLogisticRecordCount(@Param("map") Map<String,Object> map);

	List<StOrderTotalVo> selectLast15day(@Param("map")  Map<String, Object> map);

	StOrderTotalVo selectlast30total(@Param("map") Map<String,Object> map);

	StOrderTotalVo selectalltotal(@Param("map") Map<String,Object> map);
}
