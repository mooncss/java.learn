package com.example.mall.dto;

import com.example.mall.model.OmsOrderStageRela;
import io.swagger.annotations.Api;
import lombok.Data;


@Api("出入库记录查询")
@Data
public class OmsOrderStageRelaNode extends OmsOrderStageRela {
    private String receivePhone;
    private String receiveName;
    private String receiveAddr;
//驿站名
    private String stageName;
}
