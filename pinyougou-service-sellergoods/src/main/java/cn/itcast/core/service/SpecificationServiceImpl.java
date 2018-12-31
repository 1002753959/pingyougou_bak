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

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.specification.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 9:51 2018/12/1
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService{

    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 查询全部
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult queryList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @param specification
     * @return
     */
    @Override
    public PageResult search(int pageNum, int pageSize, Specification specification) {

        PageHelper.startPage(pageNum, pageSize);
        //判断specification对象中的条件是否为空
        if (specification != null) {
            SpecificationQuery specificationQuery = new SpecificationQuery();
            SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();
            if (specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
            Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
            return new PageResult(page.getTotal(), page.getResult());
        }
            return queryList(pageNum, pageSize);
    }

    /**
     * 添加规格和规格选项
     * @param specificationVo
     */
    @Override
    public void add(SpecificationVo specificationVo) {
        //添加规格
        specificationDao.insertSelective(specificationVo.getSpecification());
        //添加规格的选项
        for (SpecificationOption specificationOption : specificationVo.getSpecificationOptionList()) {
            specificationOption.setSpecId(specificationVo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }

    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    @Override
    public SpecificationVo findOne(Long id) {

        Specification specification = specificationDao.selectByPrimaryKey(id);
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(specification.getId());
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(specificationOptionQuery);
        return new SpecificationVo(specification,specificationOptionList);

    }

    @Override
    public void update(SpecificationVo specificationVo) {
        //更新规格
        specificationDao.updateByPrimaryKeySelective(specificationVo.getSpecification());
        //更新这个规格的规格选项
        //删除原有的选项
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(specificationVo.getSpecification().getId());
        specificationOptionDao.deleteByExample(specificationOptionQuery);
        //遍历添加新的选项
        for (SpecificationOption specificationOption : specificationVo.getSpecificationOptionList()) {
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        SpecificationQuery specificationQuery = new SpecificationQuery();
        specificationQuery.createCriteria().andIdIn(Arrays.asList(ids));
        specificationDao.deleteByExample(specificationQuery);
    }

    /**
     * 查询所有返回List<Map>
     * @return
     */
    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }


    /**
     * 根据spec_id查出Spec_options
     * @param specId
     * @return
     */
    @Override
    public List<SpecificationOption> findOptionsBySpecId(String specId) {
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(Long.parseLong(specId));
        return specificationOptionDao.selectByExample(specificationOptionQuery);
    }


}
