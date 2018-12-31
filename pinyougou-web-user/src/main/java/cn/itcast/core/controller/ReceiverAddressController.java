package cn.itcast.core.controller;

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.ReceiverAddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YiEn
 * @create 2018-12-30-11:01
 */
@RestController
@RequestMapping("/receiverAddress")
public class ReceiverAddressController {
    @Reference
    private ReceiverAddressService receiverAddressService;

    //    保存对象
    @RequestMapping("/saveReceiverAddress")
    public Result saveReceiverAddress(@RequestBody Address address) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//还有一个默认名字的判断这里没加
        if (name == null) {
            return null;
        }
        address.setUserId(name);

        try {
            receiverAddressService.saveReceiverAddress(address);
            return new Result(true, "成功");
        } catch (Exception e) {
            return new Result(false, "失败");
        }

    }

    //    查询所有或者单个信息
    @RequestMapping("/findReceiverAddressById")
    public List<Address> findReceiverAddressById(Integer id) {
        if (id == null) {
            return null;
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//还有一个默认名字的判断这里没加
        if (name == null) {
            return null;
        }
        List<Address> addressList = null;

        try {
            addressList = receiverAddressService.findReceiverAddressById(id, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressList;
    }

    //    删除信息
    @RequestMapping("/deleteReceiverAddressById")
    public Result deleteReceiverAddressById(Integer id) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//还有一个默认名字的判断这里没加
        if (name == null) {
            return null;
        }
        try {
            receiverAddressService.deleteReceiverAddressById(id, name);
            return new Result(true, "成功");
        } catch (Exception e) {
            return new Result(false, "失败");
        }

    }
//    设置默认地址
    @RequestMapping("/setDefaultReceiverAddressById")
    public Result setDefaultReceiverAddressById(Integer id) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//还有一个默认名字的判断这里没加
        if (name == null) {
            return null;
        }
        try {
            receiverAddressService.setDefaultReceiverAddressById(id, name);
            return new Result(true, "成功");
        } catch (Exception e) {
            return new Result(false, "失败");
        }

    }


}



