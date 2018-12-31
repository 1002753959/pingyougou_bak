package cn.itcast.core.service;

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.entity.PageResult;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class OrderSearchServiceImpl implements OrderSearchService{
    @Autowired
    private OrderDao orderDao;

    /**
     * 订单查询
     */
    @Override
    public PageResult search(Integer page, Integer rows, Order order) {
        //开始分页查询
        PageHelper.startPage(page, rows);
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();

        //判断有没有条件
        if(order != null ) {

            if (order.getStatus() != null && !"".equals(order.getStatus().trim())) {
                criteria.andStatusEqualTo(order.getStatus());

            }
            if (order.getOrderId() != null && !"".equals(order.getOrderId())) {
                criteria.andOrderIdEqualTo(order.getOrderId());
            }

        }


        Page<Order> pageBean = (Page<Order>) orderDao.selectByExample(orderQuery);
        return new PageResult(pageBean.getTotal(), pageBean.getResult());
    }
}
