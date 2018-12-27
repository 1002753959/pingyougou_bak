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

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 21:26 2018/12/12
 */
@Service
@Transactional
public class ItemSearchServiceImpl  implements ItemSearchService{

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据搜索页中传过来的大Map来查出结果集大Map
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},
        //                  'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};'

        //进来先把搜索词进行去空格
        searchMap.put("keywords", searchMap.get("keywords").replace(" ", ""));

        //通过item_keyWords关键词高亮分页查询
        Map<String, Object> map = searchHighlightPage(searchMap);
        //1.从索引库中按照分组查的分类
        List<String> categoryList = searchCategoryBykeyWords(searchMap);
        map.put("categoryList", categoryList);

        //根据分类名称去Redis中查出typeId
        Object typeId = redisTemplate.boundHashOps("itemCats").get(categoryList.get(0));
        //2.品牌
        List<Map> brandList = queryBrandList(typeId);
        map.put("brandList", brandList);
        //3.规格
        List<Map> specList = querySpecList(typeId);
        map.put("specList", specList);

        return map;

    }

    /**
     *  根据第一个分类名称去Redis中查询品牌结果集
     * @param typeId
     * @return
     */

    public List<Map> queryBrandList(Object typeId){
        //通过typeId查出品牌结果集
        List<Map> templateList = (List<Map>) redisTemplate.boundHashOps("brands").get(typeId);

        return templateList;
    }

    /**
     * 根据分类的第一个名字去Redis中查出规格结果集
     * @param typeId
     * @return
     */
    public List<Map> querySpecList (Object typeId){
        //通过typeId查出规格结果集
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specs").get(typeId);
        return specList;
    }


    /**
     * 根据item_keywords在索引库中分组查出分类名集合
     * @param searchMap
     * @return
     */

    public List<String> searchCategoryBykeyWords(Map<String, String> searchMap){
        ArrayList<String> list = new ArrayList<>();
        //搜索表达式
        SimpleQuery simpleQuery = new SimpleQuery("item_keywords:"+searchMap.get("keywords"));
        //根据category进行分组 ==> group by category
        simpleQuery.setGroupOptions(new GroupOptions().addGroupByField("item_category"));
        GroupPage<Item> items = solrTemplate.queryForGroupPage(simpleQuery, Item.class);
        //查完之后设置我查什么 ==> select item_category
        GroupResult<Item> categoryList = items.getGroupResult("item_category");
        //从框架的结果中找出结果集
        Page<GroupEntry<Item>> groupEntries = categoryList.getGroupEntries();
        List<GroupEntry<Item>> content = groupEntries.getContent();
        for (GroupEntry<Item> itemGroupEntry : content) {
            list.add(itemGroupEntry.getGroupValue());
        }
        return list;
    }


    /**
     * 通过item_keyWords关键词高亮分页查询
     * @param searchMap
     * @return
     */
    public Map<String, Object> searchHighlightPage(Map<String, String> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        //4.分页结果集
        //5.总个数
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);


        // 过滤条件  商品分类  品牌  规格  价格区间
        //商品分类
        if (searchMap.get("category") != null && !"".equals(searchMap.get("category").trim())) {
            //有商品分类的过虑信息
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_category").contains(searchMap.get("category").trim()));
            query.addFilterQuery(filterQuery);
        }

        //品牌
        if (searchMap.get("brand") != null && !"".equals(searchMap.get("brand").trim())) {
            //有品牌的过滤条件
            query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_brand").contains(searchMap.get("brand").trim())));
        }

        //规格
        /*
          "item_spec_网络": "联通3G",
          "item_spec_机身内存": "16G",
         */
        String spec = searchMap.get("spec");
        if (spec != null && !"".equals(spec.trim())) {
            Map map = JSONObject.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    String psec = entry.getValue();
                    if (psec != null && !"".equals(spec.trim())) {
                        //字符串item_spec_ + 规格map中的key值,就能和Solr中的动态域的域名相对应
                        Criteria specCriteria = new Criteria("item_spec_" + key).contains(psec);
                        SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(specCriteria);
                        query.addFilterQuery(simpleFilterQuery);
                    }
                }
        }

        //价格区间
        String price = searchMap.get("price");
        if (price != null && !"".equals(price.trim())) {
            //判断有没有* 这个无限大的标识
            String[] split = price.split("-");
            if (!price.contains("*")) {
                //不是无限的区间
                query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").between(split[0], split[1], true, true)));
            } else {
                //是个无限的区间
                query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(split[0])));
            }
        }


        //$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},
        //                  'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
        // 排序   最新排序 价格升序  价格降序
        if (searchMap.get("sortField") != null && !"".equals(searchMap.get("sortField").trim())
                && searchMap.get("sort") != null && !"".equals(searchMap.get("sort"))) {
            //判断是升序还是降序
            if ("DESC".equals(searchMap.get("sort"))) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortField"));
                query.addSort(sort);
            } else {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortField"));
                query.addSort(sort);
            }
        }

        //设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        //哪个域高亮
        highlightOptions.addField("item_title");
        //前缀
        highlightOptions.setSimplePrefix("<span style='color:red'>");
        //后缀
        highlightOptions.setSimplePostfix("</span>");
        query.setHighlightOptions(highlightOptions);


        //分页
        //偏移量  当前页-1 *  每页显示的个数
        query.setOffset( (Integer.parseInt(searchMap.get("pageNo")) - 1) * Integer.parseInt(searchMap.get("pageSize")));
        query.setRows(Integer.parseInt(searchMap.get("pageSize")));

        //执行查询
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        //把高亮的title设置给原来的title
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //原来的
            Item entity = itemHighlightEntry.getEntity();
            //高亮的
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0) {
                //有高亮把高亮设置给原来的
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }

        //获取总个数
        long totalElements = items.getTotalElements();
        resultMap.put("total",totalElements);
        //获取结果
        List<Item> content = items.getContent();
        resultMap.put("rows", content);
        //设置totalPage = 总个数 / 每页显示数
        resultMap.put("totalPages", (totalElements / Integer.parseInt(searchMap.get("pageSize"))));

        return resultMap;
    }

}
