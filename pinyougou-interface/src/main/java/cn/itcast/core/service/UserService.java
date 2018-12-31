package cn.itcast.core.service;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.user.User;

public interface UserService {
    void sendCode(String phone);

    void add(String smscode, User user);

    User getUserInfomation(String name) throws Exception;

    void saveUserInfomation(User user) throws  Exception;



    PageResult queryPage(int pageNum, int pageSize);
}