package com.tuling.order.service;

import com.tuling.datasource.entity.Order;
import com.tuling.order.vo.OrderVo;
import io.seata.core.exception.TransactionException;

public interface OrderService {

    /**
     * 保存订单
     */
    Order saveOrder(OrderVo orderVo) throws TransactionException;
}