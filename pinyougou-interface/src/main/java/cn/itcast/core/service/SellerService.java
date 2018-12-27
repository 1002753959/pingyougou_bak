package cn.itcast.core.service;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import org.springframework.security.core.userdetails.UserDetails;

public interface SellerService {
    void add(Seller seller);

    Seller queryUserByUserName(String username);


    PageResult search(int page, int rows, Seller seller);

    Seller findOne(String id);

    void updateStatus(String sellerId, String status);
}
