package cn.itcast.core.service;

import cn.itcast.core.dao.address.CitiesDao;
import cn.itcast.core.pojo.address.Cities;
import cn.itcast.core.pojo.address.CitiesQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author YiEn
 * @create 2018-12-28-21:02
 */
@Service
@Transactional
public class CitiesServiceImp implements CitiesService {
    @Autowired
    private CitiesDao citiesDao;

    @Override
    public List<Cities> findCitiesByProvinceid(String provinceid) {

        CitiesQuery citiesQuery = new CitiesQuery();
        citiesQuery.createCriteria().andProvinceidEqualTo(provinceid);
        List<Cities> citiesList = citiesDao.selectByExample(citiesQuery);
        return citiesList;
    }
}
