package cn.itcast.core.service;

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


}
