package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;

/*
@author By 林中小鸟
@package  com.example.demo.controller
@create 2021 - 07 - 2021\7\2 0002
@QQ: 357182695
@Em: themooncss@gmail.com
*/
@RestController
public class Test {
    @GetMapping("/one")
    public String test() {
        HashMap<String,String> map = new HashMap<>();
        map.put("项目","学习");
        map.put("作者","MoonCss");
        map.put("昵称","我是大魔王");
        map.put("日期","2021-10-21");

        map.remove("昵称");

        Collection values = map.values();
        for (Object val:values){
            System.out.println("val.toString() = " + val.toString());
        };

        System.out.println("--------------------------------");
        System.out.println("map = " + map);
        return "1232131232";
    }
}
