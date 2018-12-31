package cn.itcast.core.dao.order;

import cn.itcast.core.entity.GoodsTime;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderDao {
    int countByExample(OrderQuery example);

    int deleteByExample(OrderQuery example);

    int deleteByPrimaryKey(Long orderId);

    int insert(Order record);

    int insertSelective(Order record);

    List<Order> selectByExample(OrderQuery example);

    Order selectByPrimaryKey(Long orderId);

    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderQuery example);

    int updateByExample(@Param("record") Order record, @Param("example") OrderQuery example);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<GoodsTime> selectByTimeWithPage0(@Param("name") String name);
    List<GoodsTime> selectByTimeWithPage1(@Param("name") String name);
    List<GoodsTime> selectByTimeWithPage2(@Param("name") String name);
    List<GoodsTime> selectByTimeWithPage3(@Param("name") String name);
    List<GoodsTime> selectByTimeWithPage4(@Param("name") String name);
    List<GoodsTime> selectByTimeWithPage5(@Param("name") String name);
    List<GoodsTime> selectByTimeWithPage6(@Param("name") String name);

    List<Integer> findLine(@Param("prefix") int prefix, @Param("postfix") int postfix,@Param("name") String name);
}