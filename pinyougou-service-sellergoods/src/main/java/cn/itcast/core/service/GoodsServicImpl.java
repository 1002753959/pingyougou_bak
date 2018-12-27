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
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.entity.GoodsVo;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.*;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 9:40 2018/11/28
 */
@Service
@Transactional
public class GoodsServicImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SellerDao sellerDao;


    //注入Spring封装的jmsTemplate发送消息
    @Autowired
    private JmsTemplate jmsTemplate;
    //注入发布/订阅模式的消息发布目标地的对象的接口,实现类在配置文件实例化的那个
    @Autowired
    private Destination topicPageAndSolrDestination;
    //注入点对点模式的消息发布目标地的对象
    @Autowired
    private Destination queueSolrDeleteDestination;

    public List<Goods> qeuryList(){
        return goodsDao.selectByExample(null);
    }



    /**
     * 添加商品
     * @param goodsVo
     */
    @Override
    public void add(GoodsVo goodsVo) {
    //添加goods
        //新加的商品设置成未审核
        goodsVo.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsVo.getGoods());

    //添加goodsdesc
        //获取到添加到goods表中的新生成的主键保存到goodsDesc中,使主键保持一致
        goodsVo.getGoodsDesc().setGoodsId(goodsVo.getGoods().getId());
        goodsDescDao.insertSelective(goodsVo.getGoodsDesc());

//    //添加item
////
////        //设置title  title=goods.goodsName + 规格
////        String title = goodsVo.getGoods().getGoodsName();
////        //遍历出所有的库存量对象
////        List<Item> itemList = goodsVo.getItemList();
////        for (Item item : itemList) {
////            //规格 {"机身内存":"16G","网络":"联通3G"}
////            String itemSpec = item.getSpec();
////            //由字符串转换成Map集合
////            Map<String,String> map = JSONObject.parseObject(itemSpec, Map.class);
////            Set<Map.Entry<String, String>> entries = map.entrySet();
////            //遍历加上规格名称
////            for (Map.Entry<String, String> entry : entries) {
////                String value = entry.getValue();
////                title += " " + value;
////            }
////            //设置上title名称
////            item.setTitle(title)
////            ;
////            //从GoodsDesc中获取图片的中的第一张图片放到Item中
////            String itemImages = goodsVo.getGoodsDesc().getItemImages();
////            List<Map> imgMap = JSON.parseArray(itemImages,Map.class);
////            //判空
////            if (imgMap != null && imgMap.size() > 0) {
////                item.setImage((String) imgMap.get(0).get("url"));
////            }
////            //设置三级分类
////            item.setCategoryid(goodsVo.getGoods().getCategory3Id());
////            //设置三级分类名称
////            ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsVo.getGoods().getCategory3Id());
////            item.setCategory(itemCat.getName());
////            //设置两个时间
////            item.setCreateTime(new Date());
////            item.setUpdateTime(new Date());
////            //设置goodsId
////            item.setGoodsId(goodsVo.getGoods().getId());
////            //设置seller名称
////            String sellerId = goodsVo.getGoods().getSellerId();
////            Seller seller = sellerDao.selectByPrimaryKey(sellerId);
////            item.setSeller(seller.getName());
////            //设置品牌名称
////            Long brandId = goodsVo.getGoods().getBrandId();
////            Brand brand = brandDao.selectByPrimaryKey(brandId);
////            item.setBrand(brand.getName());
////
////            //保存item
////            itemDao.insert(item);
////        }
        insertItem(goodsVo);

    }

    /**
     * 分页查询
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult search(int page, int rows, Goods goods) {
        //开始分页查询
        PageHelper.startPage(page, rows);
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();

        //并且选择没有被删除的商品
        criteria.andIsDeleteIsNull();
        //判断有没有条件
        if (goods != null) {
            if (goods.getAuditStatus() != null) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName().trim())) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
        }
        Page<Goods> pageBean = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(pageBean.getTotal(), pageBean.getResult());
    }



    /**
     * 修改回显findOne
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
//        Goods goods = new Goods();
//        GoodsDesc goodsDesc = new GoodsDesc();
//        List<Item> itemList = new ArrayList<Item>();
        //查goods
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);
        //查goodsDesc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);
        //查item集合
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        goodsVo.setItemList(itemList);
        return goodsVo;
    }

    /**
     * 修改
     * @param goodsVo
     */
    @Override
    public void update(GoodsVo goodsVo) {
        //修改goods
        goodsDao.updateByPrimaryKeySelective(goodsVo.getGoods());
        //修改goodsDesc
        goodsDescDao.updateByPrimaryKeySelective(goodsVo.getGoodsDesc());
        //修改Item,因为item中的id是自增长的,传过来的数据没有id,不能使用update 的接口
            //1.删除原本的数据
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goodsVo.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        //2.保存新的数据
        insertItem(goodsVo);

    }


    /**
     * 私有的保存item的方法
     * @param goodsVo
     */
    private void insertItem(GoodsVo goodsVo) {

        //遍历出所有的库存量对象
        List<Item> itemList = goodsVo.getItemList();
        for (Item item : itemList) {
            //设置title  title=goods.goodsName + 规格
            String title = goodsVo.getGoods().getGoodsName();

            //规格 {"机身内存":"16G","网络":"联通3G"}
            String itemSpec = item.getSpec();
            //由字符串转换成Map集合
            Map<String, String> map = JSONObject.parseObject(itemSpec, Map.class);
            Set<Map.Entry<String, String>> entries = map.entrySet();
            //遍历加上规格名称
            for (Map.Entry<String, String> entry : entries) {
                String value = entry.getValue();
                title += " " + value;
            }
            //设置上title名称
            item.setTitle(title)
            ;
            //从GoodsDesc中获取图片的中的第一张图片放到Item中
            String itemImages = goodsVo.getGoodsDesc().getItemImages();
            List<Map> imgMap = JSON.parseArray(itemImages, Map.class);
            //判空
            if (imgMap != null && imgMap.size() > 0) {
                item.setImage((String) imgMap.get(0).get("url"));
            }
            //设置三级分类
            item.setCategoryid(goodsVo.getGoods().getCategory3Id());
            //设置三级分类名称
            ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsVo.getGoods().getCategory3Id());
            item.setCategory(itemCat.getName());
            //设置两个时间
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            //设置goodsId
            item.setGoodsId(goodsVo.getGoods().getId());
            //设置seller名称
            String sellerId = goodsVo.getGoods().getSellerId();
            Seller seller = sellerDao.selectByPrimaryKey(sellerId);
            item.setSeller(seller.getName());
            //设置品牌名称
            Long brandId = goodsVo.getGoods().getBrandId();
            Brand brand = brandDao.selectByPrimaryKey(brandId);
            item.setBrand(brand.getName());

            //保存item
            itemDao.insert(item);
        }
    }


    /**
     * 设置审核通过的状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        Goods goods = new Goods();
        goods.setAuditStatus(status);
        for (Long id : ids) {
            //1.更改数据库表中的状态
            goods.setId(id);
            goodsDao.updateByPrimaryKeySelective(goods);
            if ("1".equals(status)) {
                //发送消息给MQ.之后的工作其他模块来做
                jmsTemplate.send(topicPageAndSolrDestination,new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(id));
                    }
                });



            }
        }
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");
        for (Long id : ids) {
            //1.删除是吧数据库中的delete字段改成1
            goods.setId(id);
            goodsDao.updateByPrimaryKeySelective(goods);
            //发送点对点消息,让pinyougou-service-search把这个商品的solr删除了
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });

            //TODO 静态页面先不用删除
        }

    }
}
