package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.common.CommonResult;
import com.example.mall.emun.OrderStatus;
import com.example.mall.maputil.MapUtil;
import com.example.mall.model.BesMotion;
import com.example.mall.model.UmsMemberReceiveAddress;
import com.example.mall.service.BesMotionService;
import com.example.mall.service.UmsMemberReceiveAddressService;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.utils.PhoneFormatUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 会员收货地址管理Controller
 * Created by macro on 2018/8/28.
 */
@Controller
@Api(tags = "UmsMemberReceiveAddressController", description = "会员收货地址管理")
@RequestMapping("/member/address")
public class UmsMemberReceiveAddressController extends BaseController {
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;

    @Autowired
    BesMotionService besMotionService;

    @ApiOperation("添加收货地址")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@RequestBody UmsMemberReceiveAddress address) {
        String mobile =  super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.failed(OrderStatus.USERINFOERROR.getName());
        }
        if(!checkAddress(address)){
            return CommonResult.failed("收货信息不完整");
        }
        int total  = memberReceiveAddressService.selectCount(new EntityWrapper<UmsMemberReceiveAddress>().eq("member_id",mobile));
        if(total >= 10){
            return CommonResult.failed("抱歉，您最多有10个收货地址");
        }
        if(StringUtils.isBlank(address.getLat()) || StringUtils.isBlank(address.getLon())){
            return CommonResult.failed("定位失败,请重试");
        }
        List<BesMotion>  list =besMotionService.selectList(new EntityWrapper<>());
        if(!CollectionUtils.isEmpty(list)){
            int size=  list.size();
            double [] lon =new double[size];
            double [] lat=new double[size];
            double pointlon = Double.parseDouble(address.getLon());
            double pointLat =  Double.parseDouble(address.getLat());
            for(int i=0;i<size;i++){
                lon[i] = Double.parseDouble(list.get(i).getLon());
                lat[i] = Double.parseDouble(list.get(i).getLat());
            }
            boolean b  = MapUtil.isInPolygon(pointlon,pointLat,lon,lat);
            if(!b){
                return CommonResult.failed("抱歉，所选区域不在运营范围内");
            }
        }

        int count = memberReceiveAddressService.add(address);
        if (count > 0) {
            UmsMemberReceiveAddress addr =
                    memberReceiveAddressService.selectOne(
                            new EntityWrapper<UmsMemberReceiveAddress>()
                                    .eq("member_id",mobile)
                                    .eq("default_status",0));
            return CommonResult.success(addr.getId());
        }
        return CommonResult.failed();
    }

    //验证收货地址信息
    public boolean checkAddress(UmsMemberReceiveAddress address) {
        if (address == null  || StringUtils.isBlank(address.getProvince()) ||
                StringUtils.isBlank(address.getCity())
                || StringUtils.isBlank(address.getRegion())
                || StringUtils.isBlank(address.getPhoneNumber())
                || StringUtils.isBlank(address.getName())) {
            return false;
        }
        return true;
    }


    @ApiOperation("删除收货地址")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable String id) {
        String mobile =  super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.failed(OrderStatus.USERINFOERROR.getName());
        }
        int count = memberReceiveAddressService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改收货地址 设置为默认地址也可以用此接口")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public CommonResult update(@PathVariable String id, @RequestBody UmsMemberReceiveAddress address) {
        String mobile =  super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.failed(OrderStatus.USERINFOERROR.getName());
        }
        if(!checkAddress(address)){
            return CommonResult.failed("收货信息不完整");
        }
        if(StringUtils.isBlank(address.getLat()) || StringUtils.isBlank(address.getLon())){
            return CommonResult.failed("定位失败,请重试");
        }
        List<BesMotion>  list =besMotionService.selectList(new EntityWrapper<>());
        if(!CollectionUtils.isEmpty(list)){
            int size=  list.size();
            double [] lon =new double[size];
            double [] lat= new double[size];
            double pointlon = Double.parseDouble(address.getLon());
            double pointLat =  Double.parseDouble(address.getLat());
            for(int i=0;i<size;i++){
                lon[i] = Double.parseDouble(list.get(i).getLon());
                lat[i] = Double.parseDouble(list.get(i).getLat());
            }
            boolean b  = MapUtil.isInPolygon(pointlon,pointLat,lon,lat);
            if(!b){
                return CommonResult.failed("抱歉，所选区域不在运营范围内");
            }
        }
        int r = memberReceiveAddressService.update(id, address);
        if(r> 0){
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("更新失败");
    }

   //查询会员有几个收货地址
   @ApiOperation("查询用户收货地址的条数")
   @RequestMapping(value = "/countall", method = RequestMethod.GET)
   @ResponseBody
   public CommonResult countall() {
     String mobile =  super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.failed(OrderStatus.USERINFOERROR.getName());
        }
       EntityWrapper en = new EntityWrapper();
       en.eq("member_id",getUserMobile());
        int count = memberReceiveAddressService.selectCount(en);
    return CommonResult.success(count);
   }

    @ApiOperation("显示所有收货地址")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsMemberReceiveAddress>> list() {
        String mobile =  super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.failed(OrderStatus.USERINFOERROR.getName());
        }
        List<UmsMemberReceiveAddress> addressList = memberReceiveAddressService.list();
        return CommonResult.success(addressList);
    }

    @ApiOperation("根据ID查询收货地址")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsMemberReceiveAddress> getItem(@PathVariable String id) {
        String mobile =  super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.failed(OrderStatus.USERINFOERROR.getName());
        }
        UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(id);
        return CommonResult.success(address);
    }
}
