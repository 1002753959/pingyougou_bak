package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Provinces;

import java.util.List;

public interface ProvincesService {
    List<Provinces> findByParentId()  throws  Exception;

    List<Provinces> findByProvinceid(String provinceid) throws  Exception;
}
