package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.example.mall.dto.PmsProductParam;
import com.example.mall.dto.PmsProductQueryParam;
import com.example.mall.dto.PmsProductResult;
import com.example.mall.model.PmsProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 商品管理Service
 * Created by macro on 2018/4/26.
 */
public interface PmsProductService extends IService<PmsProduct>{
    /**
     * 创建商品
     */

    int create(PmsProductParam productParam);

    int getProductByBesCount( Map<String,Object> map);
    /**
     * 根据商品编号获取更新信息
     */
    PmsProductResult getUpdateInfo(Long id);

    /**
     * 更新商品
     */
    int update(Long id, PmsProductParam productParam);

    /**
     * 分页查询商品
     */
    PageInfo<PmsProduct> list(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量修改审核状态
     * @param ids 产品id
     * @param verifyStatus 审核状态
     * @param detail 审核详情
     */
    int updateVerifyStatus(List<Long> ids, Integer verifyStatus, String detail);

    /**
     * 批量修改商品上架状态
     */
    int updatePublishStatus(List<Long> ids, Integer publishStatus);

    /**
     * 批量修改商品推荐状态
     */
    int updateRecommendStatus(List<Long> ids, Integer recommendStatus);

    /**
     * 批量修改新品状态
     */
    int updateNewStatus(List<Long> ids, Integer newStatus);

    /**
     * 批量删除商品
     */
    int updateDeleteStatus(List<Long> ids, Integer deleteStatus);

    /**
     * 根据商品名称或者货号模糊查询
     */
    List<PmsProduct> list(String keyword);

    /*
    * 根据ID查询商品信息
    * */
    PmsProduct getproductbyid(Long id);

    //根据分类查询商品
    List<PmsProduct> getlistbycate(Map<String,Object> map);

    int getCountPros(Map<String,Object> map);

    List<PmsProduct> getProductByBes( Map<String,Object> map);

    int updateJFrate(Map<String,Object> map);

}
