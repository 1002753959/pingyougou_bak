package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Cities;

import java.util.List;

public interface CitiesService {

    List<Cities> findCitiesByProvinceid(String provinceid);
}
