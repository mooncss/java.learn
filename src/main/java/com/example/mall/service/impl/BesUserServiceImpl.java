package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.BesUserDao;
import com.example.mall.model.BesUser;
import com.example.mall.service.BesUserService;
import com.example.mall.vo.CartItemBesGroup;
import net.bytebuddy.asm.Advice;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BesUserServiceImpl extends ServiceImpl<BesUserDao, BesUser> implements BesUserService {

    @Autowired
    BesUserDao besUserDao;
    @Override
    @Transactional
    public int batchUpdate(Map<String, Object> map) {
        return besUserDao.batchUpdate(map);
    }

    @Override
    public int batchUpdatestatus(Map<String, Object> map) {
        return besUserDao.batchUpdatestatus(map);
    }

    @Override
    public List<CartItemBesGroup> selectBesFromcart(@Param("map") Map<String, Object> map) {
        return besUserDao.selectBesFromcart(map);
    }

    @Override
    public List<CartItemBesGroup> selectBesFromcart0(@Param("map") Map<String, Object> map) {
        return besUserDao.selectBesFromcart0(map);
    }

    @Override
    public Long selectmaxId() {
        return besUserDao.selectmaxId();
    }


}
