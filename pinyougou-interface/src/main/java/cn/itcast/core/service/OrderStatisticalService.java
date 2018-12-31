package cn.itcast.core.service;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.order.OrderItem;

public interface OrderStatisticalService {
    PageResult orderstatistical(Integer page, Integer rows, OrderItem orderItem);

}
