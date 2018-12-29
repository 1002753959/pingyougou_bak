package cn.itcast.core.service;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.order.Order;

import java.util.List;

public interface OrderService {
    void add(Order order, String name);

    List<Order> findAll(String name);

    PageResult search(int page, int rows, Order order, String name);
}
