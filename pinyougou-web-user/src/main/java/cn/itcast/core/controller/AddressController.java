package cn.itcast.core.controller;

import cn.itcast.core.pojo.address.Areas;
import cn.itcast.core.pojo.address.Cities;
import cn.itcast.core.pojo.address.Provinces;
import cn.itcast.core.service.AreasService;
import cn.itcast.core.service.CitiesService;
import cn.itcast.core.service.ProvincesService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YiEn
 * @create 2018-12-28-11:47
 */
@RestController
@RequestMapping("address")
public class AddressController {
    @Reference
    private ProvincesService provincesService;
    @Reference
    private CitiesService citiesService;
    @Reference
    private AreasService areasService;
    //{"tb_provinces":"140000","tb_cities":"140100","tb_areas":"140107"}
//    第一次获取所有省信息
    @RequestMapping("findByParentId")
    public List<Provinces> findByParentId() {
        List<Provinces> list= null;
        try {
            list = provincesService.findByParentId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    //    根据省id拿到省信息
    @RequestMapping("findProvinceidByProvinceid")
    public List<Provinces> findByParentId(String provinceid) {
        List<Provinces> list= null;
        try {
            if (provinceid==null||"".equals(provinceid.trim())){return null;};

            list = provincesService.findByProvinceid(provinceid.trim());
        } catch (Exception e) {

        }
        return list;
    }
    //    根据省id拿到市信息
    @RequestMapping("findCitiesByProvinceid")
    public List<Cities> findCitiesByProvinceid(String provinceid) {
        List<Cities> list= null;
        try {
            if (provinceid==null||"".equals(provinceid.trim())){return null;};
            list = citiesService.findCitiesByProvinceid(provinceid);
        } catch (Exception e) {

        }
        return list;
    }
    //    根据市id拿到县信息
    @RequestMapping("findAreasByCities")
    public List<Areas> findAreasByCities(String cityid) {
        List<Areas> list= null;
        try {
            if (cityid==null||"".equals(cityid.trim())){return null;};
            list = areasService.findAreasByCities(cityid.trim());
        } catch (Exception e) {

        }
        return list;
    }

}
