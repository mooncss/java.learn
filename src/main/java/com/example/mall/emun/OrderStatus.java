package com.example.mall.emun;
public enum OrderStatus
{
    //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单"
        USERINFOERROR("请登录",100),
        ORDER_DZF("待支付", 0),
        ORDER_DFH("已付款", 1),  // 已支付
        ORDER_YFH("已发货", 2),
        ORDER_DDYZ("驿站入库", 5),  //到达驿站
        ORDER_YZCK("驿站出库", 6), //驿站出库
        ORDER_YWC("已完成", 3),  //用户收到货  确认收货
        ORDER_YGB("已取消", 4),
        //团购订单
        ORDER_PT_DCT("待成团", 7),
        ORDER_PT_CG("拼团成功", 8),
        ORDER_PT_SB("拼团失败", 9);

        private String name;

        private int index;

        OrderStatus(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
}
