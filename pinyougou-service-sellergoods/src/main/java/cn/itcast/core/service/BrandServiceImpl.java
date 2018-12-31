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


import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 0:48 2018/11/28
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    public List<Brand> query(){
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult queryPage(int pageNum, int pageSize) {
        //分页小助手第一步
        PageHelper.startPage(pageNum, pageSize);
        //查询全部,直接生成Page对象
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
        //生成pageResult对象
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return  pageResult;
    }

    /**
     * 添加商品
     * @param brand
     * @throws Exception
     */
    @Override
    public void add(Brand brand) throws Exception {
        brandDao.insertSelective(brand);
    }


    /**
     * 根据Id查询Brand
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public Brand queryBrandById(long id) throws Exception {
        Brand brand = brandDao.selectByPrimaryKey(id);
        return brand;
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        //传统方式
//        for (long id : ids) {
//            brandDao.deleteByPrimaryKey(id);
//        }
        //逆向工程的方式 创建Brand的条件对象
        // BrandQuery.createCriteria()相当于有了where
        //criteria.andIdIn相当于在where后面加条件 id in(x,x,x,);
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));

        brandDao.deleteByExample(brandQuery);
    }

    /**
     * 分页+条件查询
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @Override
    public PageResult queryPageWithSearch(int pageNum, int pageSize, Brand brand) {
        //分页小助手
        PageHelper.startPage(pageNum, pageSize);
        //如果有查询条件
        if (brand != null) {
            //创建条件对象
            BrandQuery brandQuery = new BrandQuery();
            //创建条件
            BrandQuery.Criteria criteria = brandQuery.createCriteria();
            //如果商品名称不为空并且不是一堆空字符串
            if (brand.getName() != null && !"".equals(brand.getName().trim())) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            //如果商品的首字母不是空并且不是一堆空串
            if (brand.getFirstChar() != null && "".equals(brand.getFirstChar().trim())) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }

            //调用dao层条件查询
            Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);

            return new PageResult(page.getTotal(),page.getResult());
        }
        //如果没有查询条件,调用没有查询条件的方法
        return queryPage(pageNum, pageSize);

    }

    @Override
    public List<Map> selectOptionList() {
        return  brandDao.selectOptionList();

    }
}
