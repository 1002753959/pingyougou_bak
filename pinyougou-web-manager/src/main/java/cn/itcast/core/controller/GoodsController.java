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

import cn.itcast.core.entity.GoodsVo;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 20:05 2018/12/6
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 添加商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {
            //获取到当前的商家名,加上
            goodsVo.getGoods().setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
            goodsService.add(goodsVo);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }

    /**
     * 分页查询
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(int page, int rows, @RequestBody(required = false) Goods goods) {
        //运营商后台查询所有的商品,不用查某个商家的商品了
        return goodsService.search(page, rows, goods);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }


    /**
     * 修改回显findOne
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public GoodsVo findOne (Long id){
        return goodsService.findOne(id);
    }

    /**
     * 更新商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody(required = false) GoodsVo  goodsVo){
        try {
            goodsService.update(goodsVo);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids, status);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }

    }
}
