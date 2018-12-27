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

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

/**~
 * 生成静态化页面的类
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 20:48 2018/12/14
 */
@Service
public class StaticPageServiceImpl implements StaticPageService, ServletContextAware{
    //注入Spring实例化的freeMarker对象
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;

    //实现了ServletContextAware对象就可以获取到ServletContext对象就可以获取到真实路径
    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 使用ServletContext获取的真实路径
     * @param path
     * @return
     */
    public String RealPath(String path){
        return servletContext.getRealPath(path) ;
    }

    /**
     * 使用Id生成静态页面的方法
     * @param id
     */
    public void StaticPage (Long id){
        //创建freeMarker对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        //获取生成静态页面需要的真实路径
        String realPath = RealPath("/" + id + ".html");

//准备数据
        HashMap<String, Object> root = new HashMap<>();
        //商品
        Goods goods = goodsDao.selectByPrimaryKey(id);
        root.put("goods", goods);
        //商品详情
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        root.put("goodsDesc", goodsDesc);
        //库存单元
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        root.put("itemList", itemList);
        //一级分类,二级分类,三级分类
        root.put("itemCat1", goods.getCategory1Id());
        root.put("itemCat2", goods.getCategory2Id());
        root.put("itemCat3", goods.getCategory3Id());

        OutputStreamWriter outputStreamWriter = null;
        try {
//读取模板 读
            Template template = configuration.getTemplate("item.ftl");

            //创建生成静态页面所需要的输出流
            //注意,为了解决乱码问题,
            // 需要configuration对象读取模板的输入流和生成的静态页面的输出流使用同样的编码集
             outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "UTF-8");

            //处理
            template.process(root,outputStreamWriter );

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关流
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






}
