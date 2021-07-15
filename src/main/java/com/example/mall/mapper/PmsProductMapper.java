package com.example.mall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pagehelper.PageInfo;
import com.example.mall.model.OmsCartItem;
import com.example.mall.model.PmsProduct;
import com.example.mall.model.PmsProductExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Repository
public interface PmsProductMapper extends  BaseMapper<PmsProduct> {
    long countByExample(PmsProductExample example);

    int deleteByExample(PmsProductExample example);

    int deleteByPrimaryKey(Long id);

    int inserta(PmsProduct record);

    int selectByExampleCount(PmsProductExample example);

    int insertSelective(PmsProduct record);

    List<PmsProduct> selectByExampleWithBLOBs(PmsProductExample example);

    List<PmsProduct> selectByExample(PmsProductExample example);

     Integer selectByExampleCountRecomond();
    //查询爆款
    List<PmsProduct> selectByExampleRecomand(@Param("map") Map<String,Object> map);

    PmsProduct selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsProduct record, @Param("example") PmsProductExample example);

    int updateByExampleWithBLOBs(@Param("record") PmsProduct record, @Param("example") PmsProductExample example);

    int updateByExample(@Param("record") PmsProduct record, @Param("example") PmsProductExample example);

    int updateByPrimaryKeySelective(PmsProduct record);

    int updateByPrimaryKeyWithBLOBs(PmsProduct record);

    int updateByPrimaryKey(PmsProduct record);

    PmsProduct getproductbyid(Long id);

    List<PmsProduct> getlistbycate( Map<String,Object> map);

    int getCountPros(Map<String,Object> map);

    List<PmsProduct> getProductByBes(Map<String,Object> map);

    int getProductByBesCount(Map<String,Object> map);

    int calcStockPronotenough(@Param("map") Map<String,Object> map);

    int calcStockSkunotenough(@Param("map") Map<String,Object> map);

    int updatebatchsetSkuStock(@Param("list")List<OmsCartItem> list);

    int updatebatchsetProStock(@Param("list")List<OmsCartItem> list);

    int updateJFrate(@Param("map") Map<String,Object> map);
}