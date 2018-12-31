package cn.itcast.core.pojo.specification;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 20:33 2018/12/1
 */
public class SpecificationVo implements Serializable{
    private Specification specification;
    private List<SpecificationOption> specificationOptionList;

    public SpecificationVo() {
    }

    public SpecificationVo(Specification specification, List<SpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
