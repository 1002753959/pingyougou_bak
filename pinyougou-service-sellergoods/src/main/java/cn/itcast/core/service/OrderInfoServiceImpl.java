package cn.itcast.core.service;

import cn.itcast.core.dao.order.OrderDao;
import com.alibaba.dubbo.config.annotation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public List<Integer> findLine(String name, Integer k) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = k; i < k+6; i++) {
            List<Integer> totalFee = orderDao.findLine(i+1, i, name);
            int count = 0;
            for (Integer integer : totalFee) {
                count += integer;
            }
            list.add(count);
        }
        Collections.reverse(list);

        return list;
    }


//    @Override
//    public List<Integer> findLine(String name,Integer k) {
//        /**
//         * 根据星期划分查出商家的销售额用于前台的折线图
//         * @return
//         */
//        public List<Integer> findLine(String name,Integer k) {
//            ArrayList<Integer> list = new ArrayList<>();
//            for (int i = k; i < k+6; i++) {
//                List<Integer> totalFee = orderDao.findLine(i+1, i, name);
//                int count = 0;
//                for (Integer integer : totalFee) {
//                    count += integer;
//                }
//                list.add(count);
//            }
//            Collections.reverse(list);
//
//            return list;
//        }
}
