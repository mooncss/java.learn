package com.example.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.mapper.UmsMemberReceiveAddressMapper;
import com.example.mall.model.UmsMember;
import com.example.mall.model.UmsMemberReceiveAddress;
import com.example.mall.model.UmsMemberReceiveAddressExample;
//import com.example.mall.portal.service.UmsMemberReceiveAddressService;
//import com.example.mall.portal.service.UmsMemberService;
import com.example.mall.service.UmsMemberReceiveAddressService;
import com.example.mall.service.UmsMemberService;
import com.zhihui.uj.management.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户地址管理Service实现类
 * Created by macro on 2018/8/28.
 */
@Service
public class UmsMemberReceiveAddressServiceImpl extends ServiceImpl<UmsMemberReceiveAddressMapper,UmsMemberReceiveAddress>
        implements UmsMemberReceiveAddressService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private UmsMemberReceiveAddressMapper addressMapper;

    @Autowired
    HttpServletRequest request;

    @Override
    public int add(UmsMemberReceiveAddress address) {
        String mobile = request.getHeader("mobile");
        if(address.getDefaultStatus() == 0){
            addressMapper.updateAllserDefault1(mobile);
        }
        address.setId(IdUtils.createID());
        address.setMemberId(mobile);
        return addressMapper.insertA(address);
    }

    @Override
    public int delete(String id) {
//        UmsMember currentMember = memberService.getCurrentMember();
        String mobile  =request.getHeader("mobile");
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(mobile).andIdEqualTo(id);
        return addressMapper.deleteByExample(example);
    }

    @Override
    @Transactional
    public int update(String id, UmsMemberReceiveAddress address) {

        String mobile = request.getHeader("mobile");
        if(address.getDefaultStatus() == 0){
            addressMapper.updateAllserDefault1(mobile);
        }else{
            address.setDefaultStatus(1);
        }
        address.setId(id);
//        UmsMember currentMember = memberService.getCurrentMember();
//        addressMapper.updateAllserDefault1(mobile);
//        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
//        example.createCriteria().andMemberIdEqualTo(mobile).andIdEqualTo(id);
        int b = addressMapper.updateById(address);
        return b;
    }

    @Override
    public List<UmsMemberReceiveAddress> list() {
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(request.getHeader("mobile"));
        return addressMapper.selectByExample(example);
    }

    @Override
    public UmsMemberReceiveAddress getItem(String id) {
        UmsMember currentMember = memberService.getCurrentMember();
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(request.getHeader("mobile")).andIdEqualTo(id);
        List<UmsMemberReceiveAddress> addressList = addressMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(addressList)){
            return addressList.get(0);
        }
        return null;
    }
}
