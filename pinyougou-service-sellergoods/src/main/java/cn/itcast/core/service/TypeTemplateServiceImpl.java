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

import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 10:13 2018/12/2
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationServiceImpl specificationServiceImpl;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 添加模板
     * @param typeTemplate
     */
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 删除
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        typeTemplateQuery.createCriteria().andIdIn(Arrays.asList(ids));

        typeTemplateDao.deleteByExample(typeTemplateQuery);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 修改
     * @param typeTemplate
     */
    @Override
    public void update(TypeTemplate typeTemplate) {
        //根据id删除
        typeTemplateDao.deleteByPrimaryKey(typeTemplate.getId());
        //新添进去
        typeTemplateDao.insert(typeTemplate);
    }

    /**
     * 条件分页
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(int page, int rows, TypeTemplate typeTemplate) {
        //查询所有的模板对象,保存到Redis缓存中一份
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for (TypeTemplate template : typeTemplates) {
            //品牌结果集  K:模板Id  V:品牌结果集
            String brandIds = template.getBrandIds();
            List<Map> brandList = JSONObject.parseArray(brandIds, Map.class);
            redisTemplate.boundHashOps("brands").put(template.getId(), brandList);

            //规格结果集  K:模板Id  V:规格结果集
            List<Map> specList = findBySpecList(template.getId());
            redisTemplate.boundHashOps("specs").put(template.getId(), specList);
        }



        //分页小助手开始
        PageHelper.startPage(page, rows);
        //判断条件是否为空
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && !"".equals(typeTemplate.getName().trim())) {
                TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            Page<TypeTemplate> pageBean = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
            return new PageResult(pageBean.getTotal(), pageBean.getResult());
        } else {
            Page<TypeTemplate> pageBean = (Page<TypeTemplate>) typeTemplateDao.selectByExample(null);
            return new PageResult(pageBean.getTotal(), pageBean.getResult());
        }

    }

    /**
     * 根据模板Id封装带有规格选项的List<Map>集合
     * List中有Map,Map中的Value中有List
     * List<Map>大致是:List<map<"id":"27","text":"网络","options":List<option>>>
     * @param id
     * @return
     */
    @Override
    public List<Map> findBySpecList(Long id) {
        //根据模板Id查出这个模板对象
        TypeTemplate typeTemplate = findOne(id);
        //取出模板中的规格字段  大致是:[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //把上面的String格式转换成List<Map> 集合中每个Map大致是:map<"id":"27","text":"网络">
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        for (Map map : maps) {
            Integer specIdsId = (Integer) map.get("id");
            //根据map中的规格Id查出关于这个规格的所有规格选项
            List<SpecificationOption> optionsBySpecId = specificationServiceImpl.findOptionsBySpecId(specIdsId+"");
            //放到Map中   这时Map中的数据大致是:map<"id":"27","text":"网络","options":List<option>>
            map.put("options", optionsBySpecId);
        }
        return maps;
    }


}
