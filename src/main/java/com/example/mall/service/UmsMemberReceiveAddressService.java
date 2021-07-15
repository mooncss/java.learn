package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.model.UmsMemberReceiveAddress;

import java.util.List;

/**
 * 用户地址管理Service
 * Created by macro on 2018/8/28.
 */
public interface UmsMemberReceiveAddressService extends IService<UmsMemberReceiveAddress> {
    /**
     * 添加收货地址
     */
    int add(UmsMemberReceiveAddress address);

    /**
     * 删除收货地址
     * @param id 地址表的id
     */
    int delete(String id);

    /**
     * 修改收货地址
     * @param id 地址表的id
     * @param address 修改的收货地址信息
     */
    int update(String id, UmsMemberReceiveAddress address);

    /**
     * 返回当前用户的收货地址
     */
    List<UmsMemberReceiveAddress> list();

    /**
     * 获取地址详情
     * @param id 地址id
     */
    UmsMemberReceiveAddress getItem(String id);
}
