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

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.ad.Content;
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
        itemCatDao.insert(itemCat);
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


    //查询商品分类id为1的商品
    @Override
    public List<ItemCat> findByCategoryList1(Long categoryId) {
        List<ItemCat> itemCatList = (List<ItemCat>) redisTemplate.boundHashOps("ItemCat").get(categoryId);
        if (itemCatList != null && itemCatList.size() > 0){
            return itemCatList;
        }else {
            ItemCatQuery itemCatQuery = new ItemCatQuery();
            itemCatQuery.createCriteria().andParentIdEqualTo(categoryId);
            return itemCatDao.selectByExample(itemCatQuery);
        }
    }


}
