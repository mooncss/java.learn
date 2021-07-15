package com.example.mall.model;


import lombok.Data;

import java.io.Serializable;

@Data
public class PhoneAndNameModel implements Serializable {
    private String phone;
    private String name;
}
