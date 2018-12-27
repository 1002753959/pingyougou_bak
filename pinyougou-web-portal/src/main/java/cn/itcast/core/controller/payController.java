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
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 20:11 2018/12/24
 */
@RestController
@RequestMapping("/pay")
public class payController {
    @Reference
    private PayService payService;

    /**
     * 生成二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map<String,String> createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return payService.createNative(name);
    }

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus (String out_trade_no){
        //判断订单状态
//        SUCCESS—支付成功
        //
        //REFUND—转入退款
        //
        //NOTPAY—未支付
        //
        //CLOSED—已关闭
        //
        //REVOKED—已撤销（付款码支付）
        //
        //USERPAYING--用户支付中（付款码支付）
        //
        //PAYERROR--支付失败(其他原因，如银行返回失败)
        try {
            int x = 0;
        while (true) {
            Map<String, String> map = payService.queryPayStatus(out_trade_no);
            System.out.println("支付状态:"+map.get("trade_state"));
            if ("SUCCESS".equals(map.get("trade_state"))) {
            return new Result(true, "支付成功");
        } else {
                Thread.sleep(3000);
                x += 1;
                if (x >= 100) {
                    return new Result(false, "支付超时");
                }
            }
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Result(false, "支付失败");
        }
    }
}
