package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.UmsMentionManDao;
import com.example.mall.model.UmsMentionMan;
import com.example.mall.service.UmsMentionManService;
import org.springframework.stereotype.Service;


@Service
public class UmsMentionManServiceImpl extends ServiceImpl<UmsMentionManDao,UmsMentionMan>
        implements UmsMentionManService {
}
