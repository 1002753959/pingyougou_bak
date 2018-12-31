package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Areas;

import java.util.List;

public interface AreasService {
    List<Areas> findAreasByCities(String trim);
}
