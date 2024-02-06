package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 用户提交订单
     * @param orderSubmit
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO orderSubmit);
}
