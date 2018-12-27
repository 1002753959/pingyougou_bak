package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;
import java.util.Map;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long id);

    void add(ItemCat itemCat);

    ItemCat findOne(Long id);

    void update(ItemCat itemCat);

    List<ItemCat> findAll();

}
