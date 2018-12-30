package cn.itcast.core.service;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 9:59 2018/12/5
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分类列表查询
     * @param id
     * @return
     */
    @Override
    public List<ItemCat> findByParentId(Long id) {

        //因为这是前台页面的初始化就会执行的方法,所以在这个方法中查出所有的分类列表保存到Redis中
        List<ItemCat> itemCats = findAll();
        for (ItemCat itemCat : itemCats) {

            //分类名称,分类对象中的模板Id
            if (itemCat.getName() != null && itemCat.getTypeId() != null) {
                redisTemplate.boundHashOps("itemCats").put(itemCat.getName(), itemCat.getTypeId());
            }
        }

        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(id);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    /**
     * 添加
     * @param itemCat
     */
    @Override
    public void add(ItemCat itemCat) {
        itemCat.setStatus("3");
        itemCatDao.insertSelective(itemCat);
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    /**
     * 修改
     * @param itemCat
     */
    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.deleteByPrimaryKey(itemCat.getId());
        itemCatDao.insert(itemCat);
    }

    /**
     * 搬运Item_cat数据表
     * @return
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    /**
     * 修改审核状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        ItemCat itemCat = new ItemCat();
        itemCat.setStatus(status);
        if(null != ids && ids.length > 0) {
            for (Long id : ids) { // 商品表的ID
                if ("3".equals(findOne(id).getStatus())) {
                    itemCat.setId(id);
                    itemCatDao.updateByPrimaryKeySelective(itemCat);
                }
            }
        }
    }

    @Override
    public void updateStatus1(Long[] ids, String status) {
        ItemCat itemCat = new ItemCat();
        itemCat.setStatus(status);
        if(null != ids && ids.length > 0) {
            for (Long id : ids) { // 商品表的ID
                if ("0".equals(findOne(id).getStatus())) {
                    itemCat.setId(id);
                    itemCatDao.updateByPrimaryKeySelective(itemCat);
                }
            }
        }
    }
}
