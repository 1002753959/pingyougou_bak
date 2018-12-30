package cn.itcast.core.controller;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 20:47 2018/12/28
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @RequestMapping("/findAll")
    public List<Order> findAll() {
        return null;
    }

    /**
     * 根据Id查询订单
     */
    @RequestMapping("/queryBrandByOrderId")
    public Order queryBrandByOrderId(Long orderId) throws Exception {
        return orderService.queryBrandByOrderId(orderId);
    }

    @RequestMapping("/search")
    public PageResult search(int page,int rows,@RequestBody(required = false) Order order) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.search(page, rows,order,name);
    }

    // 修改发货状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            orderService.updateStatus(ids, status);
            return new Result(true, "成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false, "失败");
        }
    }

}
