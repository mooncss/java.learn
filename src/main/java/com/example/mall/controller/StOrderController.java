package com.example.mall.controller;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.model.OmsThirdOrderQueryVo;
import com.example.mall.model.StOrderQueryVo;
import com.example.mall.service.OmsThirdOrderService;
import com.example.mall.service.StOrderService;
import com.zhihui.uj.management.utils.R;
import com.zhihui.uj.management.BaseController.BaseController;

/**
 * <p>
 * -入库单查询前端控制器
 * </p>
 *
 * @author bj_wang
 * @since 2020-01-02
 */
@RestController
@RequiresAuthentication
@RequestMapping("/stOrderController")
public class StOrderController extends BaseController {
    private static final String PAGE = "page";
    private static final String LIMIT = "limit";
	
    @Autowired 
    private StOrderService stOrderService;

    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @RequestMapping("/page")
    public R<Page<StOrderQueryVo>> page(@RequestParam Map<String, Object> params,String commitMobile, String dPhone) {
		int current =  Integer.parseInt(params.getOrDefault(PAGE, 1).toString());
		int size = Integer.parseInt(params.getOrDefault(LIMIT, 20).toString());//默认每页20条
		
		Page<StOrderQueryVo> pageView = stOrderService.selectPageByCondition(new Page<>(current, size), commitMobile, dPhone);
		return new R<>(pageView);
    }

}
