package cn.itcast.core.service;
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼                  BUG辟易
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
//         .............................................

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 20:01 2018/12/3
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;

    /**
     * 添加
     * @param seller
     */
    @Override
    public void add(Seller seller) {
        //首次入驻审核状态设置为0
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    public Seller queryUserByUserName(String username)   {
        return sellerDao.selectByPrimaryKey(username);
    }

    /**
     * f分页
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @Override
    public PageResult search(int page, int rows, Seller seller) {
        //开始分页
        PageHelper.startPage(page, rows);
        //判断是否有条件
        if (seller != null) {
            SellerQuery sellerQuery = new SellerQuery();
            SellerQuery.Criteria criteria = sellerQuery.createCriteria();
            criteria.andStatusEqualTo(seller.getStatus());
            if (seller.getName() != null && !"".equals(seller.getName().trim())) {
                criteria.andNameLike("%" + seller.getName() + "%");
            }
            if (seller.getNickName() != null && !"".equals(seller.getNickName())) {
                criteria.andNickNameLike("%" + seller.getNickName() + "%");
            }
            Page<Seller> pageBean = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
            return new PageResult(pageBean.getTotal(), pageBean.getResult());
        } else {
            Page<Seller> pageBean = (Page<Seller>) sellerDao.selectByExample(null);
            return new PageResult(pageBean.getTotal(), pageBean.getResult());
        }

    }

    /**
     * 查一个
     * @param id
     * @return
     */
    @Override
    public Seller findOne(String id) {
        return  sellerDao.selectByPrimaryKey(id);

    }

    /**
     * 更改状态
     * @param sellerId
     * @param status
     */
    @Override
    public void updateStatus(String sellerId, String status) {
        sellerDao.updateStatus(sellerId, status);
    }


}
