package cn.itcast.core.service;

import cn.itcast.core.dao.address.ProvincesDao;
import cn.itcast.core.pojo.address.Provinces;
import cn.itcast.core.pojo.address.ProvincesQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author YiEn
 * @create 2018-12-28-20:12
 */
@Service
@Transactional
public class ProvincesServiceImpl implements  ProvincesService {
    @Autowired
    private ProvincesDao provincesDao;
    @Override
    public List<Provinces> findByParentId() throws Exception {
        List<Provinces> list = provincesDao.selectByExample(null);
        return list;
    }

    @Override
    public List<Provinces> findByProvinceid(String provinceid) throws Exception {

        ProvincesQuery provincesQuery = new ProvincesQuery();
        provincesQuery.createCriteria().andProvinceidEqualTo(provinceid);
        List<Provinces> list = provincesDao.selectByExample(provincesQuery);
        return list;
    }
}
