package cn.itcast.core.service;

import cn.itcast.core.dao.address.AreasDao;
import cn.itcast.core.pojo.address.Areas;
import cn.itcast.core.pojo.address.AreasQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author YiEn
 * @create 2018-12-28-21:01
 */
@Service
@Transactional
public class AreasServiceImpl implements AreasService {
    @Autowired
    private AreasDao areasDao;

    @Override
    public List<Areas> findAreasByCities(String cityies) {

        AreasQuery areasQuery = new AreasQuery();
        areasQuery.createCriteria().andCityidEqualTo(cityies);
        List<Areas> areas = areasDao.selectByExample(areasQuery);
        return areas;
    }
}
