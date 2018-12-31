package cn.itcast.core.service;


import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.entity.Cart;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 11:18 2018/12/20
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Item queryItembyId(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }

    /**
     * 将合并后的购物车持久化到Redis中
     *
     * @param newCartList 那边的老购物车结果集到这边就成了新购物车结果集
     * @param name
     */
    @Override
    public void persistenceCart(List<Cart> newCartList, String name) {
        //1.取出Redis中这个用户之前的购物车
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
        //2.Redis之前的购物车和传过来的购物车大合并
        List<Cart> mergeCartList = merge(newCartList, oldCartList);
        //3.把合并后的购物车添加到Redis缓存中
        redisTemplate.boundHashOps("CART").put(name, mergeCartList);
    }

    /**
     * 根据用户名查他的购物车信息
     * @param name
     * @return
     */
    @Override
    public List<Cart> queryCartListByName(String name) {
         return (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
    }

    //商品的收藏
    @Override
    public Result addGoodsCollection(Long itemId) {
        Item item = itemDao.selectByPrimaryKey(itemId);
        try{
            redisTemplate.boundHashOps("goodsCollection").put(itemId,item);
            return new Result(true,"收藏成功");
        }catch(Exception e){
            return new Result(false,"收藏失败");
        }

    }

    //查询所有的收藏商品
    @Override
    public List findAllGoodsCollection() {
        Set goodsCollection = redisTemplate.boundHashOps("goodsCollection").keys();
        return (List) goodsCollection;
    }

    /**
     * 私有的合并方法
     * @param newCartList
     * @param oldCartList
     */
    public List<Cart> merge (List<Cart> newCartList,List<Cart> oldCartList){
        //判断原来Redis中是否有购物车集合
        if (oldCartList != null) {
            //原来Redis中有数据
            for (Cart newCart : newCartList) {
                //-判断这个购物项对应的购物车存在于之前的购物车中
                //重写了Cart的SellerId 的hashCode和equls方法
                int index = oldCartList.indexOf(newCart);
                if (index != -1) {
                    //--存在:
                    //---判断这款购物项在这个商家的购物项集合中是否有相同的商品
                    Cart oldCart = oldCartList.get(index);
                    List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                    List<OrderItem> newOrderItemList = newCart.getOrderItemList();
                    for (OrderItem newOrderItem : newOrderItemList) {
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
                    }
                } else {
                    //--不存在:创建新的购物车,加进购物车集合
                    oldCartList.add(newCart);
                }
            }

        } else {
            //原来Redis中没有数据,返回新购物车集合
            return newCartList;
        }
        return oldCartList;
    }
}
