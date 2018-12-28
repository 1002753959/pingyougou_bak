package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 段志鹏 on 2018/12/28--12:38 O(∩_∩)O
 *
 */
@Service
@Transactional
public class UserFrostServiceImpl implements UserFrostService {
    private User user;
    @Autowired
    private UserDao userDao;
    @Override
    public PageResult search(int page, int rows, User user) {
        //分页小助手开始
        PageHelper.startPage(page, rows);
        //判断条件是否为空
        UserQuery userQuery = new UserQuery();
        if (user != null) {
            if (user.getUsername() != null && !"".equals(user.getUsername().trim())) {
                UserQuery.Criteria criteria = userQuery.createCriteria();
                criteria.andUsernameLike("%" + user.getUsername() + "%");
            }
            Page<User> pageUser = (Page<User>) userDao.selectByExample(userQuery);
            return new PageResult(pageUser.getTotal(), pageUser.getResult());
        } else {
            Page<User> pageUser = (Page<User>) userDao.selectByExample(null);
            return new PageResult(pageUser.getTotal(), pageUser.getResult());
        }
    }
    @Override
    public User findOne(Long id) {

        return  userDao.selectByPrimaryKey(id);
    }

    @Override
    public void frostStatus(Long id) {
        User user = userDao.selectByPrimaryKey(id);
        user.setStatus("0");
        userDao.updateByPrimaryKeySelective(user);


    }

    @Override
    public void updateStatus(Long id) {
        User user = userDao.selectByPrimaryKey(id);
        user.setStatus("1");
        userDao.updateByPrimaryKeySelective(user);
    }

}
