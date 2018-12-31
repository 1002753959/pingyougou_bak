package cn.itcast.core.service;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<Brand> query();

    PageResult queryPage(int pageNum, int pageSize);

    void add(Brand brand) throws Exception;

    Brand queryBrandById(long id)throws Exception;

    void update(Brand brand);

    void deleteByIds(Long[] ids);

    PageResult queryPageWithSearch(int pageNum, int pageSize, Brand brand);

    List<Map> selectOptionList();

    void updateStatus(Long[] ids, String status) throws Exception;

    void updateStatus1(Long[] ids, String status)throws Exception;
}
