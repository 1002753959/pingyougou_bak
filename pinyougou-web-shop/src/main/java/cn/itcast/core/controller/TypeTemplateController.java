package cn.itcast.core.controller;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.SpecificationService;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 10:09 2018/12/2
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @Reference
    SpecificationService specificationService;

    @RequestMapping("/search")
    public PageResult search(int page,int rows, @RequestBody TypeTemplate typeTemplate){
        /*String name= SecurityContextHolder.getContext()
                .getAuthentication().getName();*/
        return typeTemplateService.search(page, rows, typeTemplate);

    }

    /**
     * 添加
     * @param typeTemplate
     * @return
     */
    @RequestMapping("/add")
    public Result add (@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }


    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete (Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }


    /**
     * findOne
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TypeTemplate findOne (Long id){
        return typeTemplateService.findOne(id);
    }


    /**
     * 修改
     * @param typeTemplate
     * @return
     */
    @RequestMapping("/update")
    public Result update (@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }

    /**
     * 根据模板id查出规格ids在查出对应的所有规格option集合封装成List<Map>的格式
     * List集合中的其中一个map结构大致是:
     * <"id":"35">
     * <"text","网络">
     * <"options",List<"移动4G","移动3G","电信3G",....>>
     * @param id
     * @return
     */
    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
      return   typeTemplateService.findBySpecList(id);
    }

    // 修改审核状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            typeTemplateService.updateStatus(ids, status);
            return new Result(true, "成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false, "失败");
        }
    }
}
