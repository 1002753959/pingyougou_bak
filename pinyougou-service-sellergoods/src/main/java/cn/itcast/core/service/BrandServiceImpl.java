package cn.itcast.core.service;

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

    public List<Brand> query() {
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
        return pageResult;
    }

    /**
     * 添加商品
     *
     * @param brand
     * @throws Exception
     */
    @Override
    public void add(Brand brand) throws Exception {
        brand.setStatus("3");
        brandDao.insertSelective(brand);
    }


    /**
     * 根据Id查询Brand
     *
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
     *
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
            if (null != brand) {
                if (brand.getStatus() != null) {
                    criteria.andStatusEqualTo(brand.getStatus());
                }
                //如果商品名称不为空并且不是一堆空字符串
                if (brand.getName() != null && !"".equals(brand.getName().trim())) {
                    criteria.andNameLike("%" + brand.getName() + "%");
                }
            }
            //调用dao层条件查询
            Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);

            return new PageResult(page.getTotal(), page.getResult());
        }
        //如果没有查询条件,调用没有查询条件的方法
        return queryPage(pageNum, pageSize);

    }

    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();

    }

    // 修改审核状态
    public void updateStatus(Long[] ids, String status) throws Exception {
        Brand brand = new Brand();
        brand.setStatus(status);
        if (null != ids && ids.length > 0) {
            for (Long id : ids) { // 商品表的ID
                if ("3".equals(queryBrandById(id).getStatus())) {
                    brand.setId(id);
                    // 更新审核状态
                    brandDao.updateByPrimaryKeySelective(brand);
                }
            }
        }
    }

    @Override
    public void updateStatus1(Long[] ids, String status) throws Exception {
        Brand brand = new Brand();
        brand.setStatus(status);
        if (null != ids && ids.length > 0) {
            for (Long id : ids) { // 商品表的ID
                if ("0".equals(queryBrandById(id).getStatus())) {
                    brand.setId(id);
                    // 更新审核状态
                    brandDao.updateByPrimaryKeySelective(brand);
                }
            }
        }
    }
}
