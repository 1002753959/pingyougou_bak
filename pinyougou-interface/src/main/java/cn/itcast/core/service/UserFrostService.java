package cn.itcast.core.service;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.user.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List; /**
 * Created by 段志鹏 on 2018/12/28--12:35 O(∩_∩)O
 */
public interface UserFrostService {


    PageResult search(int page, int rows, User user);

    User findOne(Long id);

    void frostStatus(Long id);

    void updateStatus(Long id);

    ArrayList<String> findFieldNameWithExcel();

    ArrayList<ArrayList<String>> findFieldDataWithExcel();

    void readyExcelExport(String username);

    void readyExcelExportProduct(String username);

    void readyExcelExportOrder(String username);

}
