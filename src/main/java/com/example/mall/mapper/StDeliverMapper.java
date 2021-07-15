package com.example.mall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.dto.StDeliverVO;
import com.example.mall.model.StDeliver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StDeliverMapper extends BaseMapper<StDeliver> {

    List<StDeliverVO> selectallDelivers(@Param("map") Map<String,Object> map);
}
