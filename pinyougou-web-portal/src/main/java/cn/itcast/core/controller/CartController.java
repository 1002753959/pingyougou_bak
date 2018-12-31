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

import cn.itcast.core.entity.Cart;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 11:14 2018/12/20
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;


    /**
     * 添加购物车  需要解决跨域
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = {"http://localhost:9005"}, allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            //创建一个购物车集合
            List<Cart> oldCartList = null;

            //1.获取Cookie数组
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
//2.判断是否有购物车这个Cookie
                    if ("cartList".equals(cookie.getName())) {
                        //遍历到了,有购物车的Cookie
                        oldCartList = JSON.parseArray(cookie.getValue(), Cart.class);
                        //遍历到了,跳出去
                        break;
                    }
                }
            }
            //如果上方的遍历完成之后cartList还是null,就没有给cartList附上值,说明没有购物车Cookie
            if (oldCartList == null) {
//3.没有:创建购物车
                oldCartList = new ArrayList<>();
            }

            //根据参数传过来的购物项Id和数量构成一个只有一个商品的购物车,后面是由indexOf()方法判断新添的商品是否存在于之前的购物车集合中
            // 自定义一个新的Cart对象
            Cart newCart = new Cart();
            //首先根据ItemId查item表,查出SellerId和SellerName
            Item item = cartService.queryItembyId(itemId);
            newCart.setSellId(item.getSellerId());
            //商家名称就不用往里加了
            ArrayList<OrderItem> newOrderItems = new ArrayList<>();
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setId(itemId);
            newOrderItem.setNum(num);
            newOrderItems.add(newOrderItem);
            newCart.setOrderItemList(newOrderItems);


//4.添加购物项    每个商家代表一个购物车
            //-判断这个购物项对应的购物车存在于之前的购物车中
            //重写了Cart的SellerId 的hashCode和equls方法
            int index = oldCartList.indexOf(newCart);
            if (index != -1) {
                //--存在:
                //---判断这款购物项在这个商家的购物项集合中是否有相同的商品
                Cart oldCart = oldCartList.get(index);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                //重写了OrderItem的Id 的hashCode和equls方法
                int ItemIndex = oldOrderItemList.indexOf(newOrderItem);
                if (ItemIndex != -1) {
                    //----有:增加数量
                    OrderItem oldOrderItem = oldOrderItemList.get(ItemIndex);
                    oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
                } else {
                    //----没有:购物项集合中添加新的购物项
                    oldOrderItemList.add(newOrderItem);
                }

            } else {
                //--不存在:创建新的购物车,加进购物车集合
                oldCartList.add(newCart);
            }

            //判断是否登录
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(name)) {
                //不是匿名用户,说明登录了
//5.将购物车合并到Redis中
                //需要指定当前用户名,
                cartService.persistenceCart(oldCartList, name);
//6.清空Cookie
                Cookie cookie = new Cookie("cartList", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);

            } else {
                //是匿名用户,说明没有登录
                // 未登录：购物车信息存到Cookie中
//5.把当前的购物车信息存放到Cookie中
                Cookie cookie = new Cookie("cartList", JSON.toJSONString(oldCartList));
                //cookie.setDomain(); 这个可以跨二级域
                cookie.setMaxAge(60 * 60 * 24 * 3);
                cookie.setPath("/");
//6.返回cookie
                response.addCookie(cookie);
            }
            return new Result(true, "添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加购物车失败");
        }
    }


    /**
     * 购物车页面的初始化方法,查询购物车
     * @param request
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
//2.判断Cookie中是否有购物车
        List<Cart> cartList = new ArrayList<>();
        for (Cookie cookie : cookies) {
            if ("cartList".equals(cookie.getName())) {
                cartList = JSON.parseArray(cookie.getValue(), Cart.class);
                break;
            }
        }

        //判断登没登陆
        //判断是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser" != name) {
            //不是匿名用户,说明登录了
            if (cartList != null && cartList.size() > 0) {
//            3.有:将Cookie中的购物车合并到Redis中
                cartService.persistenceCart(cartList, name);
//            4.清空Cookie
                Cookie cookie = new Cookie("cartList", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
//            5.从Redis中取出购物车信息
                cartList = cartService.queryCartListByName(name);

            } else {
                cartList = cartService.queryCartListByName(name);
            }
        }

            //装满购物车

            //1.获取
            if (cartList != null && cartList.size() > 0) {
//3.有(由于我们当时装时只装了SellerId,ItemId,Num)所以在回显的时候我们需要根据这三个数据将购物车装满
                for (Cart cart : cartList) {
                    List<OrderItem> orderItemList = cart.getOrderItemList();
                    for (OrderItem orderItem : orderItemList) {
                        Item item = cartService.queryItembyId(orderItem.getId());
                        orderItem.setTitle(item.getTitle());
                        orderItem.setPrice(item.getPrice());
                        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum()));
                        orderItem.setPicPath(item.getImage());
                        cart.setName(item.getSeller());
                    }
                }

            }


//4.回显
        return cartList;
    }

    //商品收藏
    @RequestMapping("/addGoodsCollection")
    public Result addGoodsCollection(Long itemId){
        return cartService.addGoodsCollection(itemId);
    }

    //查询所有收藏的商品
    @RequestMapping("/findAllGoodsCollection")
    public List findAllGoodsCollection(){
        return cartService.findAllGoodsCollection();
    }

}


































