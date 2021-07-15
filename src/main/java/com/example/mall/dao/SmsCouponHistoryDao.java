package com.example.mall.dao;

import com.example.mall.domain.SmsCouponHistoryDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员优惠券领取历史自定义Dao
 * Created by macro on 2018/8/29.
 */
@Repository
public interface SmsCouponHistoryDao {
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);
}
