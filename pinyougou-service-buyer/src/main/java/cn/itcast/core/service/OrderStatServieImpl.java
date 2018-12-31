package cn.itcast.core.service;
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

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.entity.GoodsTime;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 17:38 2018/12/29
 */
@Service
@Transactional
public class OrderStatServieImpl implements OrderStatServie {
    @Autowired
    private OrderDao orderDao;

    /**
     * 订单分时间统计的方法
     * @param time
     * @param name
     * @return
     */
    @Override
    public List<GoodsTime> search( int time, String name) {
        List<GoodsTime> goodsTimes = new ArrayList<>();
        if (time == 0) {
             goodsTimes = orderDao.selectByTimeWithPage0( name);
        }
        if (time == 1) {
             goodsTimes = orderDao.selectByTimeWithPage1( name);
        }
        if (time == 2) {
             goodsTimes = orderDao.selectByTimeWithPage2( name);
        }
        if (time == 3) {
             goodsTimes = orderDao.selectByTimeWithPage3( name);
        }
        if (time == 4) {
             goodsTimes = orderDao.selectByTimeWithPage4( name);
        }
        if (time == 5) {
           goodsTimes = orderDao.selectByTimeWithPage5( name);
        }
        if (time == 6) {
            goodsTimes = orderDao.selectByTimeWithPage6( name);
        }


        //把对象中的count字段从次数改变成总销售额
        if (goodsTimes != null && goodsTimes.size() > 0) {
            for (GoodsTime goodsTime : goodsTimes) {
                if (goodsTime.getCount() != null && goodsTime.getPrice() != null) {
                    goodsTime.setTotalPrice(goodsTime.getPrice().multiply(new BigDecimal(goodsTime.getCount())));
                }
            }
        }
        return goodsTimes;
    }
}
