package cn.itcast.core.controller;
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼                  BUG辟易
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
//         .............................................

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.PhoneFormatCheckUtils;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 21:42 2018/12/18
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;


    @RequestMapping("/sendCode")
    public Result sendCode (String phone){
        try {
                if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                userService.sendCode(phone);
                return new Result(true, "发送成功");
            } else {
                return new Result(false, "手机号格式不正确");
            }
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "发送失败");
            }
    }


    /**
     * 新添加用户
     * @param smscode
     * @param user
     */
    @RequestMapping("/add")
    public Result add( String smscode,@RequestBody User user) {
        try {
            userService.add(smscode, user);
            return new Result(true, "注册成功");
        }catch (RuntimeException run){
            //接住验证码那边抛出的异常
            return new Result(false, run.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }

    }
}


