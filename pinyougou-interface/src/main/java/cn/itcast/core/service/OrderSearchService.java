package cn.itcast.core.service;


import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.order.Order;

public interface OrderSearchService {


    PageResult search(Integer page, Integer rows, Order order);
}
