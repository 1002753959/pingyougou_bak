package cn.itcast.core.service;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author YiEn
 * @create 2018-12-30-11:12
 */
@Service
@Transactional
public class ReceiverAddressServiceImpl implements ReceiverAddressService {
    @Autowired
    private AddressDao addressDao;

    //保存
    @Override
    public void saveReceiverAddress(Address address) throws Exception {
        if(address.getId()==null){
            addressDao.insertSelective(address);
        }else {
            AddressQuery addressQuery = new AddressQuery();
            addressQuery.createCriteria().andUserIdEqualTo(address.getUserId()).andIdEqualTo(Long.valueOf(address.getId()));
            addressDao.updateByExampleSelective(address,addressQuery );
        }
    }
   //删除
    @Override
    public void deleteReceiverAddressById(Integer id, String name) throws Exception {
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(name).andIdEqualTo(Long.valueOf(id.toString()));

        addressDao.deleteByExample(addressQuery);
    }

    //查询
    @Override
    public List<Address> findReceiverAddressById(Integer id, String name) throws Exception {
        if (id ==0){
            AddressQuery addressQuery = new AddressQuery();
            addressQuery.createCriteria().andUserIdEqualTo(name);
            List<Address> addressList = addressDao.selectByExample(addressQuery);
            return addressList;
        }
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(name).andIdEqualTo(Long.valueOf(id.toString()));

        List<Address> addressList = addressDao.selectByExample(addressQuery);

        return addressList;
    }

    @Override
    public void setDefaultReceiverAddressById(Integer id, String name) throws Exception {
        Address address = new Address();
        address.setIsDefault("0");
        AddressQuery addressQuery = new AddressQuery();
        addressQuery  .createCriteria().andUserIdEqualTo(name);
        addressDao.updateByExampleSelective(address,addressQuery);

        address.setIsDefault("1");
        AddressQuery addressQuery1 = new AddressQuery();
        addressQuery1.createCriteria().andUserIdEqualTo(name).andIdEqualTo(Long.valueOf(id.toString()));
        addressDao.updateByExampleSelective(address,addressQuery1);
    }
}