package cn.itcast.core.service;

import java.util.List;

public interface OrderInfoService {

    List<Integer> findLine(String name, Integer num);
}
