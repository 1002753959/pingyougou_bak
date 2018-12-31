package cn.itcast.core.controller;


import cn.itcast.core.service.OrderInfoService;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 13:40 2018/12/30
 */
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController {

    @Reference
    private OrderInfoService orderInfoService;


    @RequestMapping("/findLine")
    public List<Integer> findLine(Integer time) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderInfoService.findLine(name,time);

    }
}
