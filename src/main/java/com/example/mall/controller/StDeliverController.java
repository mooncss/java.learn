package com.example.mall.controller;

import java.util.Map;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.model.StDeliver;
import com.example.mall.service.StDeliverService;
import com.zhihui.uj.management.utils.Query;
import com.zhihui.uj.management.utils.R;
import com.zhihui.uj.management.BaseController.BaseController;

/**
 * <p>
 *-快递员查询控制器
 * </p>
 *
 * @author bj_wang
 * @since 2020-01-02
 */
@RestController
@RequiresAuthentication
@RequestMapping("/stDeliverController")
@Api(description = "快递员查询管理")
public class StDeliverController extends BaseController {
	@Autowired
	private StDeliverService stDeliverService;

	/**
	 * 分页查询信息
	 *
	 * @param params 分页对象
	 * @return 分页对象
	 */
	@GetMapping("/page")
	@ApiOperation(value = "分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params", value = "分页参数：page页,limit页数据量", paramType = "query", dataType = "Map"), })
	public R<Page<StDeliver>> page(@RequestParam Map<String, Object> params,String name , String phone) {
		EntityWrapper<StDeliver> entityWrapper = new EntityWrapper<StDeliver>();
		if (StringUtils.isNotBlank(name)) {
			entityWrapper.like("d_name", name);
		}
		if (StringUtils.isNotBlank(phone)) {
			entityWrapper.eq("d_phone", phone);
		}
		entityWrapper.orderBy("create_time", false);
		Page<StDeliver> pageView = stDeliverService.selectPage(new Query<>(params), entityWrapper);
		return new R<>(pageView);  
	}
}
