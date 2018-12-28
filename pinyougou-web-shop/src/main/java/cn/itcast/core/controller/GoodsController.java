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
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);
        return goodsService.shopSearch(page, rows, goods);
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

    /**
     * 更新商品的状态为审核中状态
     * @param ids
     * @return
     */
    @RequestMapping("/upAudit")
    public Result upAudit (Long[] ids){
        try {
            for (Long id : ids) {
                Goods goods = goodsService.findOneCart(id);
                if (goods.getAuditStatus().equals("1")) {
                    return new Result(false, "商品号为:"+id+"的商品状态为审核通过");
                }
                if (goods.getAuditStatus().equals("2")) {
                    return new Result(false, "商品号为:"+id+"的商品状态为审核未通过");
                }
                if (goods.getAuditStatus().equals("3")) {
                    return new Result(false, "商品号为:"+id+"的商品状态为审核中");
                }
                if (goods.getAuditStatus().equals("4")) {
                    return new Result(false, "商品号为:"+id+"的商品状态为关闭");
                }

            }


            goodsService.upAudit(ids);
            return new Result(true,"提交审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交审核失败");
        }
    }

    /**
     * 上架
     * @param ids
     * @return
     */
    @RequestMapping("/up")
    public Result up (Long[] ids){
        for (Long id : ids) {
            Goods goodsOne = goodsService.findGoodsOne(id);
            if (!goodsOne.getAuditStatus().equals("1")) {
                return new Result(false, "该商品还未审核通过");
            }
        }

        try {
            goodsService.up(ids);
            return new Result(true, "上架成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上架失败");
        }
    }


    /**
     * 下架
     * @param ids
     * @return
     */
    @RequestMapping("/down")
    public Result down (Long[] ids){
        for (Long id : ids) {
            Goods goodsOne = goodsService.findGoodsOne(id);
            if (!goodsOne.getAuditStatus().equals("4")) {
                return new Result(false, "该商品非已上架状态");
            }
        }

        try {
            goodsService.down(ids);
            return new Result(true, "下架成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "下架失败");
        }
    }
}
