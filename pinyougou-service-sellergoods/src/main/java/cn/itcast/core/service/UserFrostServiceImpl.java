package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 段志鹏 on 2018/12/28--12:38 O(∩_∩)O
 *
 */
@SuppressWarnings("all")
@Service
@Transactional
public class UserFrostServiceImpl implements UserFrostService {
    private User user;
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private GoodsDao goodsDao;
    @Override
    public PageResult search(int page, int rows, User user) {
        //分页小助手开始
        PageHelper.startPage(page, rows);
        //判断条件是否为空
        UserQuery userQuery = new UserQuery();
        if (user != null) {
            if (user.getUsername() != null && !"".equals(user.getUsername().trim())) {
                UserQuery.Criteria criteria = userQuery.createCriteria();
                criteria.andUsernameLike("%" + user.getUsername() + "%");
            }
            Page<User> pageUser = (Page<User>) userDao.selectByExample(userQuery);
            return new PageResult(pageUser.getTotal(), pageUser.getResult());
        } else {
            Page<User> pageUser = (Page<User>) userDao.selectByExample(null);
            return new PageResult(pageUser.getTotal(), pageUser.getResult());
        }
    }
    @Override
    public User findOne(Long id) {

        return  userDao.selectByPrimaryKey(id);
    }

    @Override
    public void frostStatus(Long id) {
        User user = userDao.selectByPrimaryKey(id);
        user.setStatus("0");
        userDao.updateByPrimaryKeySelective(user);


    }

    @Override
    public void updateStatus(Long id) {
        User user = userDao.selectByPrimaryKey(id);
        user.setStatus("1");
        userDao.updateByPrimaryKeySelective(user);
    }

    /**
     * 获取excel的标题字段
     * @return
     * 例如ArrayList<String>list
     * list.add("登录名")
     * list.add("手机号")
     * list.add("性别")
     * ...
     */
    @Override
    public ArrayList<String> findFieldNameWithExcel() {
        return null;
    }

    /**
     * 获取excel的内容信息
     * @return
     * ArrayList<ArrayList<String>>list
     * list.add("zhangsan")
     * list.add("5465456")
     * list.add("男")
     * ...
     */
    @Override
    public ArrayList<ArrayList<String>> findFieldDataWithExcel() {
        return null;
    }

    /**
     * 准备开始用户数据导出
     * @param username
     * 导出的用户数据：String[] title = {"用户id", "用户名称", "联系电话", "状态"};
     */
    @Override
    public void readyExcelExport(String username) {
        List<User> users = userDao.selectByExample(null);
        String path = "D:\\" + username + "管理的用户表.xlsx";
        String[] title = {"用户id", "用户名称", "联系电话", "状态"};//设置EXCEL的第一行的标题头（改）
        // 创建excel工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建一个工作表sheet
        HSSFSheet sheet = workbook.createSheet();
        // 创建第一行
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        // 插入第一行数据 这一行数据相当于表头
        for (int i = 0; i < title.length; i++) {
            //创建一行的一格
            cell = row.createCell(i);
            //这样每个cell都有了字段
            cell.setCellValue(title[i]);
        }

        int i=1;
        for (User user1 : users) {
            HSSFRow row1 = sheet.createRow(i);
            HSSFCell cell1 = row1.createCell(0);
            cell1.setCellValue(String.valueOf(user1.getId()));
            HSSFCell cell2 = row1.createCell(1);
            cell2.setCellValue(String.valueOf(user1.getUsername()));
            HSSFCell cell3= row1.createCell(2);
            cell3.setCellValue(String.valueOf(user1.getPhone()));
            HSSFCell cell4 = row1.createCell(3);
            cell4.setCellValue(String.valueOf(user1.getStatus()));
            i++;
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(path);
            try {
                workbook.write(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * 准备开始商品数据导出
     * @param username
     * 导出的商品字段： String[] title = {"商家id", "商品名称", "商品描述", "是否下架"};
     */
    @Override
    public void readyExcelExportProduct(String username) {
        List<Goods> goods = goodsDao.selectByExample(null);

        String path = "D:\\" + username + "管理的商品表.xlsx";
        String[] title = {"商家id", "商品名称", "商品描述", "是否下架"};//设置EXCEL的第一行的标题头（改）
        // 创建excel工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建一个工作表sheet
        HSSFSheet sheet = workbook.createSheet();
        // 创建第一行
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        // 插入第一行数据 这一行数据相当于表头
        for (int i = 0; i < title.length; i++) {
            //创建一行的一格
            cell = row.createCell(i);
            //这样每个cell都有了字段
            cell.setCellValue(title[i]);
        }

        int i=1;
        for (Goods good : goods) {
            HSSFRow row1 = sheet.createRow(i);
            HSSFCell cell1 = row1.createCell(0);
            cell1.setCellValue(good.getSellerId());
            HSSFCell cell2 = row1.createCell(1);
            cell2.setCellValue(good.getGoodsName());
            HSSFCell cell3 = row1.createCell(2);
            cell3.setCellValue(good.getCaption());
            HSSFCell cell4 = row1.createCell(3);
            cell4.setCellValue(good.getIsDelete());
            i++;
        }


        FileOutputStream os = null;
        try {
            os = new FileOutputStream(path);
            try {
                workbook.write(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 准备开始订单数据导出
     * @param username
     * 导出的订单数据：String[] title = {"order_id", "payment", "create_time","receiverAreaName", "receiver"}
     */
    @Override
    public void readyExcelExportOrder(String username) {
        List<Order> orders = orderDao.selectByExample(null);

        String path = "D:\\" + username + "管理的订单表.xlsx";
        String[] title = {"order_id", "payment", "create_time","receiverAreaName", "receiver"};//设置EXCEL的第一行的标题头（改）
        // 创建excel工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建一个工作表sheet
        HSSFSheet sheet = workbook.createSheet();
        // 创建第一行
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        // 插入第一行数据 这一行数据相当于表头
        for (int i = 0; i < title.length; i++) {
            //创建一行的一格
            cell = row.createCell(i);
            //这样每个cell都有了字段
            cell.setCellValue(title[i]);
        }

        int i=1;
        for (Order order : orders) {
            HSSFRow row1 = sheet.createRow(i);
            HSSFCell cell1 = row1.createCell(0);
            cell1.setCellValue(String.valueOf(order.getOrderId()));
            HSSFCell cell2 = row1.createCell(1);
            cell2.setCellValue(String.valueOf(order.getPayment()));
            HSSFCell cell3= row1.createCell(2);
            cell3.setCellValue(String.valueOf(order.getCreateTime()));
            HSSFCell cell4 = row1.createCell(3);
            cell4.setCellValue(String.valueOf(order.getReceiverAreaName()));
            HSSFCell cell5 = row1.createCell(4);
            cell5.setCellValue(String.valueOf(order.getReceiver()));
            i++;
        }


        FileOutputStream os = null;
        try {
            os = new FileOutputStream(path);
            try {
                workbook.write(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
