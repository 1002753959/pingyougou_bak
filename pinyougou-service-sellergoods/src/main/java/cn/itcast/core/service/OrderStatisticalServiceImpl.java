package cn.itcast.core.service;

import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class OrderStatisticalServiceImpl implements OrderStatisticalService {

    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public PageResult orderstatistical(Integer page, Integer rows, OrderItem orderItem) {
        /**
         * 统计查询
         */
        //开始分页查询
        PageHelper.startPage(page, rows);
        OrderItemQuery orderItemQuery = new OrderItemQuery();
        OrderItemQuery.Criteria criteria = orderItemQuery.createCriteria();

        //判断有没有条件
        if (orderItem != null) {

            if (orderItem.getSellerId() != null && !"".equals(orderItem.getSellerId().trim())) {
                criteria.andSellerIdLike("%" + orderItem.getSellerId() + "%");
            }
            if (orderItem.getOrderId() != null && !"".equals(orderItem.getOrderId())) {
                criteria.andOrderIdEqualTo(orderItem.getOrderId());
            }

        }

        Page<OrderItem> pageBean = (Page<OrderItem>) orderItemDao.selectByExample(orderItemQuery);
        return new PageResult(pageBean.getTotal(), pageBean.getResult());
    }
}
