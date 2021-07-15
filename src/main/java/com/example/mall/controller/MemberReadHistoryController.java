package com.example.mall.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 会员商品浏览记录管理Controller
 * Created by macro on 2018/8/3.
 */
@Controller
@Api(tags = "MemberReadHistoryController", description = "会员商品浏览记录管理")
@RequestMapping("/member/readHistory")
public class MemberReadHistoryController {
//    @Autowired
//    private MemberReadHistoryService memberReadHistoryService;
//
//    @ApiOperation("创建浏览记录")
//    @RequestMapping(value = "/create", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult create(@RequestBody MemberReadHistory memberReadHistory) {
//        int count = memberReadHistoryService.create(memberReadHistory);
//        if (count > 0) {
//            return CommonResult.success(count);
//        } else {
//            return CommonResult.failed();
//        }
//    }
//
//    @ApiOperation("删除浏览记录")
//    @RequestMapping(value = "/delete", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult delete(@RequestParam("ids") List<String> ids) {
//        int count = memberReadHistoryService.delete(ids);
//        if (count > 0) {
//            return CommonResult.success(count);
//        } else {
//            return CommonResult.failed();
//        }
//    }
//
//    @ApiOperation("展示浏览记录")
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public CommonResult<List<MemberReadHistory>> list(Long memberId) {
//        List<MemberReadHistory> memberReadHistoryList = memberReadHistoryService.list(memberId);
//        return CommonResult.success(memberReadHistoryList);
//    }
}
