package com.example.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.common.CommonResult;
import com.example.mall.emun.OrderStatus;
import com.example.mall.model.BesUser;
import com.example.mall.service.BesUserService;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

@Api("商家app入驻申请")
@Controller
@RequestMapping("/appbes")
public class AppBesUserController extends BaseController {

    @Autowired
    BesUserService besUserService;

    @PostMapping("/uploadid")
    @ApiOperation(value = "上传身份证", notes = "上传身份证")
    @ApiImplicitParam(name = "file", value = "身份证", paramType = "query", dataType = "MultipartFile")
    @ResponseBody
    public R uploadid(MultipartFile file) throws IOException{

        if(file.getSize() > 1024*1024*2 ){ //图片不超过2M
            new R<>("图片过大", R.FAIL);
        }
        String photo = OssUtil.putObject(file);
        return new R<>(photo, "上传成功", R.SUCCESS);
    }


    //根据手机号获取商家
    @GetMapping("/getbesbyphone")
    @ResponseBody
    public CommonResult getbesbyphone(String phone){
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        EntityWrapper en = new EntityWrapper();
        en.eq("phone",mobile);
        BesUser bu = besUserService.selectOne(en);
        if(bu == null){
            return  CommonResult.failed("未找到入驻信息");
        }
        return CommonResult.success(bu);
    }


    @Transactional
    @RequestMapping(value = "/savebes",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult savebes(@RequestBody BesUser user){
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(user != null){
            //根据用户手机号查询 名称验证 重复
            user.setAccount(user.getPhone());
            EntityWrapper en = new EntityWrapper();
            en.eq("phone",user.getPhone());
//            en.eq("account",user.getPhone());
            BesUser bu =besUserService.selectOne(en);
            if(bu!= null && bu.getBesId() != null){
//                besUserService.deleteById(bu);
                return CommonResult.failed("手机号已经申请");
            }
            EntityWrapper en1 = new EntityWrapper();
            en1.eq("shopname",user.getShopname());
            BesUser bu1 =besUserService.selectOne(en1);
            if(bu!= null && bu.getBesId()!=null){
                return CommonResult.failed("店铺名已存在");
            }
            if(user.getPassword()!=null && user.getPassword().length() < 6 ){
                return CommonResult.failed("密码最少为6位");
            }
            //验证验证码
            if(StringUtils.isBlank(user.getCode())){
                return CommonResult.failed("验证码不能为空");
            }

            //到阿里云去验证  类型为 S300
            String url = "http://www.ujiasmart.com:8080/app/authTypePhone?phone="+user.getPhone()+"&type="+"S300"+"&code="+user.getCode();
            try{
                String res = HttpUtils.get(url);
                JSONObject json = JSONObject.parseObject(res);
                //{"msg":"success","code":0,"data":true}
                if(json.getString("code")!= null && json.getString("code").equals("0")){//验证通过
                }else{
                    return CommonResult.failed("验证码超时");
                }
            }
            catch(Exception e){
            }

//            String s = redisTemplate.opsForValue().get("S300"+user.getPhone());
//            if(!s.equals(user.getCode())){
//                return CommonResult.failed("验证码错误");
//            }
            user.setChecked("1"); //设置审核状态为 待审核
            String pass= new Md5Hash(user.getPassword(),user.getPhone(), 10).toString();
            user.setPassword(pass);
            user.setApplytime(new Date());
            user.setAccount(user.getPhone());
            user.setIsautarky("1"); //非自营商家
            //设置新商家的ID
            Long maxid = besUserService.selectmaxId();
            user.setBesId(maxid+new Random(1).nextInt(8));

            boolean a =besUserService.insert(user);
            return a?CommonResult.success("保存成功"):CommonResult.failed("保存失败");
        }else{
            return CommonResult.failed("保存失败");
        }
    }

//    public static void main(String[] args) {
//        System.out.println(new Md5Hash("llh9160","13603959160", 10).toString());
//    }

    //更新商家入驻信息
    @RequestMapping(value = "/updatebes",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatebes(@RequestBody BesUser user){
        if(user != null){
            //根据用户手机号查询 名称验证 重复
//            if(!checkIdNo(user.getIdno())){
//                return CommonResult.failed("身份证号格式错误");
//            }
            EntityWrapper en = new EntityWrapper();
            en.eq("phone",user.getPhone());
            BesUser bu =  null;
            if(user.getBesId()!=null){
                bu =besUserService.selectById(user.getBesId());
            }else{
                return CommonResult.failed("未找到商家");
            }
            //验证验证码
            if(StringUtils.isBlank(user.getCode())){
                return CommonResult.failed("验证码不能为空");
            }
            if(bu == null){
                return CommonResult.failed("未找到入驻申请信息");
            }
            if(bu.getChecked().equals("2") && bu.getStatus().equals(("2"))){
                user.setChecked("1");//待审核
                user.setStatus("0");
            }
            if(user.getPassword()!=null && user.getPassword().length() < 6 ){
                return CommonResult.failed("密码最少为6位");
            }
            //到阿里云去验证  类型为 S300
            String url = "http://www.ujiasmart.com:8080/app/authTypePhone?phone="+user.getPhone()+"&type="+"S300"+"&code="+user.getCode();
            try{
                String res = HttpUtils.get(url);
                JSONObject json = JSONObject.parseObject(res);
                //{"msg":"success","code":0,"data":true}
                if(json.getString("code")!= null && json.getString("code").equals("0")){//验证通过
                }else{
                    return CommonResult.failed("验证码超时");
                }
            }
            catch(Exception e){
            }
            String pass= new Md5Hash(user.getPassword(),user.getPhone(), 10).toString();
            user.setPhone(user.getPhone());
            user.setPassword(pass);
            boolean a =besUserService.updateById(user);
            return a?CommonResult.success("保存成功"):CommonResult.failed("保存失败");
        }else{
            return CommonResult.failed("保存失败");
        }
    }
//    public boolean checkIdNo(String id){
//        String regex = "\\d{15}(\\d{2}[0-9xX])?";
//        if(id.matches(regex)){
//            return true;
//        }else{
//            return false;
//        }
//    }
}
