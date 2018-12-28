package cn.itcast.core.controller;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserFrostService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.opensaml.ws.wspolicy.All;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.SupportedAnnotationTypes;
import java.util.List;

/**
 * Created by 段志鹏 on 2018/12/28--12:26 O(∩_∩)O
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("user")
public class UserFrostController {
    @Reference
    private UserFrostService userFrostService;

    @RequestMapping("search")
    public PageResult search(int page,int rows,@RequestBody User user){
        return userFrostService.search(page,rows,user);
    }
    @RequestMapping("findOne")
    public User findOne(Long id){
        return userFrostService.findOne(id);
    }
    @RequestMapping("frostStatus")
    public Result frostStatus(Long id ){
        try {
            userFrostService.frostStatus(id);
            return new Result(true,"已冻结");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"冻结失败");
        }
    }
    @RequestMapping("updateStatus")
    public Result updateStatus(Long id ,String status){
        try {
            userFrostService.updateStatus(id);
            return new Result(true,"已经解除冻结");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"解除失败");
        }
    }
}
