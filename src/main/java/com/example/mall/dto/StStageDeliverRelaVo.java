package com.example.mall.dto;

import com.example.mall.model.StStageDeliverRela;
import lombok.Data;
import java.io.Serializable;

@Data
public class StStageDeliverRelaVo extends StStageDeliverRela {

    private String deliverName;
    private String shipperName;
    private String shipperCode; //快递公司代码
    private String deliverPhone;




}
