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

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.entity.Cart;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 9:04 2018/12/24
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private PayLogDao payLogDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成订单的方法
     * 把前台传过来的订单保存到订单表和订单详情表,并删除购物车信息
     * @param order
     * @param name
     */
    @Override
    public void add(Order order, String name) {
        //根据当前的用户名查出当前的用户对象
        UserQuery userQuery = new UserQuery();
        userQuery.createCriteria().andUsernameEqualTo(name);
        List<User> users = userDao.selectByExample(userQuery);

        //从缓存中拿出当前用户的购物车,//封装OrderItem对象
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);

        //整个支付的总金额
        BigDecimal TP = new BigDecimal(0);
        //记录订单生产的Id,中间用逗号隔开给支付日志的order_list复制
        ArrayList<String> orderList = new ArrayList<>();

        for (Cart cart : cartList) {
            //封装Order对象
            long orderId = idWorker.nextId();
            orderList.add(String.valueOf(orderId));
            order.setSellerId(cart.getSellId());
            order.setOrderId(orderId);
            order.setStatus("1");
            order.setCreateTime(new Date());
            order.setUserId(String.valueOf(users.get(0).getId()));
            BigDecimal payment = new BigDecimal(0);

            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getId());
                orderItem.setId(idWorker.nextId());
                orderItem.setGoodsId(item.getGoodsId());
                orderItem.setItemId(item.getId());
                orderItem.setOrderId(orderId);
                orderItem.setTitle(item.getTitle());
                orderItem.setPrice(item.getPrice());
                BigDecimal totalFee = new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum());
                orderItem.setTotalFee(totalFee);
                orderItem.setPicPath(item.getImage());
                cart.setName(item.getSeller());

                payment = payment.add(totalFee);
                //存放到数据库中
                orderItemDao.insert(orderItem);
            }
            //计算总支付金额
            TP = TP.add(payment);

            //最后走完了封装Oder对象中的Payment
            order.setPayment(payment);
            //Order对象存放到数据库中
            orderDao.insert(order);
        }
        //删除当前用户的购物车信息
        redisTemplate.boundHashOps("CART").delete(name);


        //之后进行订单日志记录的合并,现在是一个购物车一个订单,保存到订单日志中需要一次支付的若干个订单为合并为
        //一个单独的订单日志,目的是为了一次性付款
        PayLog payLog = new PayLog();
        payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
        payLog.setCreateTime(new Date());
        payLog.setTotalFee(TP.longValue());
        payLog.setUserId(String.valueOf(users.get(0).getId()));
        payLog.setTradeState("0");
        payLog.setOrderList(orderList.toString().replace("[","" ).replace("]","" ));
        payLog.setPayType("1");
        //保存
        payLogDao.insert(payLog);
        //同时保存到Redis中一份,在调用腾讯生成验证码的时候从Redis中查快

        redisTemplate.boundHashOps("payLog").put(name, payLog);


    }

    /**
     * 查询当前商家的订单
     * @return
     * @param name
     */
    @Override
    public List<Order> findAll(String name) {
        //PageHelper.startPage(, );
        return orderDao.selectByExample(null);
    }

    /**
     * 商家后台新添的订单的分页
     * @param page
     * @param rows
     * @param order
     * @param name
     * @return
     */
    @Override
    public PageResult search(int page, int rows, Order order, String name) {
        //分頁小助手
        PageHelper.startPage(page, rows);
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        //选择当前商家的订单'
        criteria.andSellerIdEqualTo(name);
        //判断有没有条件
        if (order != null) {
            if (order.getStatus() != null && !"".equals(order.getStatus().trim())) {
                criteria.andStatusEqualTo(order.getStatus());
            }
            if (order.getOrderId() != null  ) {
                criteria.andOrderIdEqualTo(order.getOrderId());
            }
        }

        //查询
        Page<Order> pageBean = (Page<Order>) orderDao.selectByExample(orderQuery);
        return new PageResult(pageBean.getTotal(), pageBean.getResult());
    }
}
