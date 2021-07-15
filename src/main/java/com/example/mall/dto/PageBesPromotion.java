package com.example.mall.dto;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageBesPromotion<T> extends Page {
    private List<String> listpro;
}
