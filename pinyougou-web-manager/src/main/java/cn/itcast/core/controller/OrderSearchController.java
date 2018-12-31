package cn.itcast.core.controller;



import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.OrderSearchService;

import cn.itcast.core.service.OrderStatisticalService;
import com.alibaba.dubbo.config.annotation.Reference;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 订单查询
 */
@RestController
@RequestMapping("/order")
public class OrderSearchController {
    @Reference
    private OrderSearchService orderSearchService;

    @Reference
    private OrderStatisticalService orderstatisticalService;


    @RequestMapping("/ordersearch")
    public PageResult searchorder(Integer page, Integer rows, @RequestBody Order order){
        return orderSearchService.search(page,rows,order);
    }


    /**
     * 订单统计
     */

    @RequestMapping("orderstatistical")
    public PageResult orderstatistical(Integer page, Integer rows, @RequestBody OrderItem orderItem){
        return orderstatisticalService.orderstatistical(page,rows,orderItem);
    }




}
