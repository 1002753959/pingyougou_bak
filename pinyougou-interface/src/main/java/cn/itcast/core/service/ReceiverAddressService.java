package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;

import java.util.List;

public interface ReceiverAddressService {

   void saveReceiverAddress(Address address) throws  Exception;



    void deleteReceiverAddressById(Integer id, String name)throws  Exception;

    List<Address> findReceiverAddressById(Integer id, String name) throws  Exception;

    void setDefaultReceiverAddressById(Integer id, String name)throws  Exception;
}
