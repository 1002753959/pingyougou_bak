package cn.itcast.core.service;

import cn.itcast.core.entity.GoodsTime;

import java.util.List;

public interface OrderStatServie {
    List<GoodsTime> search( int time, String name);
}
