package com.example.mall.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class MentionDto implements Serializable {

    private Long besId;
    private String address;
    private String besName;

}
