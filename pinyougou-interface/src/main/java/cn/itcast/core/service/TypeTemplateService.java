package cn.itcast.core.service;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {


    void add(TypeTemplate typeTemplate);

    void delete(Long[] ids);

    TypeTemplate findOne(Long id);

    void update(TypeTemplate typeTemplate);

    PageResult search(int page, int rows, TypeTemplate typeTemplate);


    List<Map> findBySpecList(Long id);

    void updateStatus(Long[] ids, String status);

    void updateStatus1(Long[] ids, String status);
}
