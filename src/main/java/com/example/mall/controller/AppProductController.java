package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.common.CommonResult;
import com.example.mall.dto.BesUserPro;
import com.example.mall.dto.PageBesPromotion;
import com.example.mall.dto.PmsAttributeVO;
import com.example.mall.dto.PmsProductRelaPormotionVO;
import com.example.mall.model.*;
import com.example.mall.service.*;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.utils.R;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Api("app获取商品详情")
@RequestMapping("/appProduct")
public class AppProductController extends BaseController {

    @Autowired
    private PmsProductService pmsProductService;

    @Autowired
    private PmsSkuStockService pmsSkuStockService;

    @Autowired
    private SmsGroupBuyProductService smsGroupBuyProductService;

    @Autowired
    private SmsGroupbuyPromotionProreduceService smsGroupbuyPromotionProreduceService;

    @Autowired
    BesUserService besUserService;

    @Autowired
    SmsGroupBuyAssembleService smsGroupBuyAssembleService;

    @Autowired
    private BesReduceService besReduceService;
    //根据商品ID获取商品详情
    @GetMapping("/getProduct")
    @ResponseBody
    public CommonResult getProduct(@RequestParam Long id){
        PmsProduct product = pmsProductService.getproductbyid(id);
        if(product == null || product.getDeleteStatus() == 1){
            return  CommonResult.failed("未找到此商品");
        }
        if(product.getPublishStatus() == 0){ //下线
            return CommonResult.failed("商品已被下架");
        }
        //验证商家的状态信息
        BesUser bes =besUserService.selectOne(new EntityWrapper<BesUser>().eq("bes_id",product.getBesId()));
        if(bes== null){
            return CommonResult.failed("商家不存在");
        }
        else if(bes.getStatus() == null || !bes.getStatus().equals("1")){
            return CommonResult.failed("商家不存在");
        }
        PmsProductRelaPormotionVO vo = new PmsProductRelaPormotionVO();
        if(product != null){
            BeanUtils.copyProperties(product,vo);
            //处理库存信息
            Integer stock = vo.getStock();
            Integer lock_stock = vo.getLockStock();
            if(stock != null){
                if(lock_stock != null){
                    int prostock = stock-lock_stock;
                    vo.setStock(prostock<0?0:prostock);
                }
            }else{
                vo.setStock(0);
            }
            //获取最低商品价格
            EntityWrapper en2 = new EntityWrapper();
            en2.eq("product_id",id);
            en2.orderBy(true,"price");
            List<PmsSkuStock> skulist = pmsSkuStockService.selectList(en2);
            if(skulist!=null && skulist.size() > 0){
                PmsSkuStock pss = skulist.get(0);
                Integer stocks = pss.getStock();
                Integer lock_stocks = pss.getLockStock();
                if(stocks != null){
                    if(lock_stocks != null){
                        int sku_stock = stocks-lock_stocks;
                        pss.setStock(sku_stock<0?0:sku_stock);
                    }
                }else{
                    pss.setStock(0);
                }
                vo.setSku(pss);
                vo.setPrice(pss.getPrice());
            }
            EntityWrapper en =new EntityWrapper();
            en.eq("product_id",product.getId());
            en.ge("end_time",new Date()); //还在进行的拼团活动
            en.eq("checkres",0);
            en.eq("statusadmin",0);
            en.eq("status",0);
            SmsGroupBuyProduct gproduct= smsGroupBuyProductService.selectOne(en);
            List<String> listdesc = new ArrayList<>();
            if(!ObjectUtils.isEmpty(gproduct)){
                vo.setIsPromotion(0); // 0 表示团购商品
                vo.setPromotionPrice(gproduct.getPromotionPrice()); //满减活动价格
                vo.setGroupbuyproId(gproduct.getGroupbuyPromotionId());
                vo.setEndTime(gproduct.getEndTime()); //结束时间
                vo.setLimitCount(gproduct.getPromotionLimit() == null?0:gproduct.getPromotionLimit()); //限购件数
                //继续查询是否存在满减信息
                EntityWrapper en1 =new EntityWrapper();
                en1.eq("product_id",product.getId());
                List<SmsGroupbuyPromotionProreduce> reduces=  smsGroupbuyPromotionProreduceService.selectList(en1);
                for(SmsGroupbuyPromotionProreduce s:reduces){
                    listdesc.add("满"+s.getLimitAmount()+"元,减"+s.getReduceAmount()+"元");
                }
                EntityWrapper ent = new EntityWrapper();
                ent.eq("product_id",id);
                ent.ge("end_time",new Date()); //结束时间大于当前时间
                ent.eq("is_full",1);  //查询未满团的团购信息
                List<SmsGroupBuyAssemble> assembleList = smsGroupBuyAssembleService.selectList(ent);
                int totalPersons = 0;
                if(assembleList != null && assembleList.size() > 0){
                    for(SmsGroupBuyAssemble sms:assembleList){
                        sms.setCreateTime(sms.getEndTime());
                        totalPersons += sms.getNowPerson();
                        Pattern p = Pattern.compile("^\\d{11}$");
                        if(StringUtils.isNotBlank(sms.getManPhone())){
                            sms.setGroupMan(purchasePhoneStr(sms.getManPhone()));
                        }else{
                            sms.setGroupMan(purchasePhoneStr(sms.getGroupMan()));
                        }
                    }
                }
                vo.setGroupPerTotal(totalPersons);
                vo.setAssembleList(assembleList);
            }else{
                vo.setIsPromotion(1); //非团购商品
            }
            //查询 店铺满减信息
            List<BesReduce> rlist = besReduceService.selectList(new EntityWrapper<BesReduce>()
                    .eq("bes_id",product.getBesId())
                    .eq("status",0)
                    .orderBy("reduce_amount"));
            BigDecimal bd = new BigDecimal("0");
            if(rlist !=null && rlist.size() > 0 ){
                for(BesReduce r:rlist) {
                    if(bd == null && vo.getPrice().compareTo(r.getLimitAmount()) <= 0){
                        bd = rlist.get(rlist.size()-1).getReduceAmount();
                    }
                    listdesc.add("店铺:满" + r.getLimitAmount() + "减" + r.getReduceAmount() + "元");
                }
            }
            //商品最低价
            BigDecimal bdprc = vo.getPrice();
            if(bdprc != null){
                bdprc = bdprc.subtract(bd);
            }
            //@TODO 系统参数
            BigDecimal SysRate = new BigDecimal(0.05);
            BigDecimal rate = product.getGiftPoint();
            Integer jifen  =(bdprc.multiply(SysRate).multiply(rate).multiply(new BigDecimal(100))).intValue();
            vo.setCredit(jifen);

            vo.setPromotiondescs(listdesc);
            //商家信息
            BesUser besUser = besUserService.selectById(product.getBesId());
            BesUserPro buser = new BesUserPro();
            buser.setBesId(besUser.getBesId());
            buser.setShopico(besUser.getShopico());
            buser.setShopname(besUser.getShopname());
            buser.setShopphone(besUser.getPhone());
            buser.setIsautarky(besUser.getIsautarky());
            vo.setBesUser(buser);
        }
        return CommonResult.success(vo);
    }

    public String purchasePhoneStr(String ph){
        if(StringUtils.isBlank(ph)){
            return "";
        }
        Pattern p = Pattern.compile("^\\d{11}$");
        Matcher m =  p.matcher(ph);
        String phone = null;
        if(m.matches()){ //匹配成功
            phone = ph.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");}
        else{
            phone = ph; }
        return phone;
    }

    //根据分类获取商品  入参 分类ID
    @GetMapping("/getProductByCate")
    @ResponseBody
    public R getProductByCate(@RequestParam Long id,@RequestParam int page,@RequestParam int limit){
        Map<String,Object> map = new HashMap<>();
        map.put("page",(page-1)*limit);
        map.put("limit",limit);
        map.put("id",id);
        List<PmsProduct> ls = pmsProductService.getlistbycate(map);
        for(PmsProduct p:ls){
            BigDecimal PRICE = p.getPrice();
            p.setPriceStr(PRICE.toString());
//            BigDecimal OriginalPrice = p.getOriginalPrice().stripTrailingZeros();
//            p.setOriginalPrice(OriginalPrice);
        }
        Page<PmsProduct> pageres = new Page<>();
        pageres.setRecords(ls);
        int count = pmsProductService.getCountPros(map);
        pageres.setTotal(count);
        pageres.setSize(limit);
        return new R(pageres);
    }

    //根据商家ID查询店铺商品
    /**
     * @param id 商家id
     * **/
    @GetMapping("/getProductByBes")
    @ResponseBody
    public R getProductByBes(@RequestParam Long id,@RequestParam int page,@RequestParam int limit){

        Map<String,Object> map = new HashMap<>();
        map.put("page",(page-1)*limit);
        map.put("limit",limit);
        map.put("id",id);
        List<PmsProduct> ls = pmsProductService.getProductByBes(map);
        PageBesPromotion<PmsProduct> pageres = new PageBesPromotion<>();
        pageres.setRecords(ls);
        pageres.setSize(limit);
        EntityWrapper en = new EntityWrapper();
        en.eq("bes_id",id);
        en.eq("delete_status",0);
        en.eq("publish_status",1);
        int count = pmsProductService.selectCount(en);
        pageres.setTotal(count);//待补充
        //店铺活动信息如何添加？
        List<BesReduce> reduceList = besReduceService.selectList(new EntityWrapper<BesReduce>()
                .eq("bes_id",id)
                .eq("status",0)
                .orderBy("reduce_amount"));
        List<String> listdesc = new ArrayList<>();
        for(BesReduce b:reduceList){
             listdesc.add("店铺:满"+b.getLimitAmount()+"减"+b.getReduceAmount()+"元");
        }
        pageres.setListpro(listdesc);
        return new R(pageres);
    }

    //根据商品查询属性和参数
    @GetMapping("/getAttributeAndParam")
    @ResponseBody
    public R getAttributeType(@RequestParam Long id){  //商品ID
        //查询属性的颜色
        EntityWrapper en1 = new EntityWrapper();
        en1.eq("product_id",id);
        //查询SKU信息
        List<PmsSkuStock> skulist = pmsSkuStockService.selectList(en1);
        EntityWrapper en =new EntityWrapper();
        en.eq("product_id",id);
        en.ge("end_time",new Date()); //还在进行的拼团活动
        en.eq("checkres",0);
        en.eq("statusadmin",0);
        en.eq("status",0);
        SmsGroupBuyProduct gproduct= smsGroupBuyProductService.selectOne(en);
        boolean bool = gproduct==null?false:true;
        for(PmsSkuStock sku:skulist){
            Integer stock = sku.getStock();
            Integer lock_stock = sku.getLockStock();
            if(stock != null){
                if(lock_stock != null){
                    int skustock = stock-lock_stock;
                    sku.setStock(skustock<0?0:skustock);
                }
            }else{
                sku.setStock(0);
            }
            if(bool){  //拼团活动
                sku.setPrice(sku.getGroupbuyPrice() == null?sku.getPrice():sku.getGroupbuyPrice());
            }
        }
        //查询商品属性类别和参数
        List<PmsAttributeVO> attrlist = pmsSkuStockService.getAttrValueList(id);
        List<PmsAttributeVO> attrlist0 = new ArrayList<>();
        List<PmsAttributeVO> attrlist1 =new ArrayList<>();
        for(PmsAttributeVO v:attrlist){
            if(StringUtils.isBlank(v.getValue())){
                continue;
            }
            if(v.getType() == 0){
                attrlist0.add(v);
            }else if(v.getType() == 1){
                attrlist1.add(v);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("skulist",skulist);
        map.put("attrlist0",attrlist0);
        map.put("attrlist1",attrlist1);
        return new R(map);
    }

    /***
     * @param  type 0商品搜索  1店铺
     * @param  keyword 关键词
     * **/
    @GetMapping("/searchForAll")
    @ResponseBody
    public CommonResult getAttributeType(String type, String keyword){  //关键词
        Map<String,Object> map = new HashMap<>();
        //先匹配团购信息
        if(StringUtils.isBlank(keyword)){
            return CommonResult.failed("请输入关键词");
        }
        EntityWrapper en1 = new EntityWrapper();
        en1.eq("search_key",keyword);
        en1.ge("end_time",new Date());
        SmsGroupBuyAssemble asse= smsGroupBuyAssembleService.selectOne(en1);
        if(!ObjectUtils.isEmpty(asse) ){
            PmsProduct product = pmsProductService.selectById(asse.getProductId());
            if (!ObjectUtils.isEmpty(product)) {
                map.put("product", product);
                map.put("assemble", asse);
                map.put("type", 2); //拼团
                return CommonResult.success(map);
            }
        }
        if(type.equals("0")){
            //根据关键词查询
            EntityWrapper en = new EntityWrapper();
            en.like("name",keyword)
            .or().like("sub_title",keyword)
            .or().like("description",keyword)
            .or().like("product_category_name",keyword).or()
                .like("detail_mobile_html",keyword);
            List<PmsProduct> searchlist = pmsProductService.selectList(en);
            map.put("list",searchlist);
            map.put("type",0);
            return CommonResult.success(map);
        }
        else if(type.equals("1")){
            EntityWrapper en = new EntityWrapper();
            en.like("shopname",keyword);
            List<BesUser> searchlist = besUserService.selectList(en);
            map.put("list",searchlist);
            map.put("type",1);
            return CommonResult.success(map);
        }
        return CommonResult.success("加载出错");
    }

}
