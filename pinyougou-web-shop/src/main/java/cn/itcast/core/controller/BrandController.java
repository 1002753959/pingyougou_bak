package cn.itcast.core.controller;
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

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 0:56 2018/11/28
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有的商标数据
     * @return
     */
    @RequestMapping("/query")
    @ResponseBody
    public List<Brand> query(){
        return brandService.query();
    }



    /**
     * 分页显示
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("queryPage")
    public PageResult queryPage(int pageNum , int pageSize){
        PageResult pageResult = brandService.queryPage(pageNum, pageSize);
        return pageResult;
    }


    /**
     * 添加商品
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public Result add (@RequestBody Brand brand){
        try {
            brandService.add(brand);
           return new Result(true, "保存成功");
        } catch (Exception e) {
          return new Result(false, "保存失败");
        }
    }

    /**
     * 根据Id查询单个商品
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/queryBrandById")
    public Brand queryBrandById(long id) throws Exception {
        Brand brand = brandService.queryBrandById(id);
        return brand;
    }


    /**
     * 跟新操作
     * @param brand
     * @return
     */
   @RequestMapping("/update")
    public Result update (@RequestBody Brand brand){
       try {
           brandService.update(brand);
           return new Result(true, "修改成功");
       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false, "修改失败");
       }
   }


    /**
     * 批量删除
     * @param ids
     * @return
     */
   @RequestMapping("deleteByIds")
    public Result deleteByIds(Long[] ids){
       try {
           brandService.deleteByIds(ids);
           return new Result(true, "删除成功");
       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false, "删除失败");
       }

    }

    /**
     * 带套件分页
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @RequestMapping("queryPageWithSearch")
    public PageResult queryPageWithSearch (int pageNum,int pageSize,@RequestBody(required = false) Brand brand){
        return brandService.queryPageWithSearch(pageNum, pageSize, brand);
    }


    /**
     * 查询所有返回List<Map>集合
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return   brandService.selectOptionList();
    }

    // 修改审核状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            brandService.updateStatus(ids, status);
            return new Result(true, "成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false, "失败");
        }
    }

}
