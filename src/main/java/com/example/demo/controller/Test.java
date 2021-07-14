package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return "1232131232";
    }
}
