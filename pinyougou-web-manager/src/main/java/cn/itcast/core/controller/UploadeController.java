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

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 22:41 2018/12/5
 */
@RestController
@RequestMapping("/upload")
public class UploadeController {

    //从内存中读取properties文件中的值
    @Value("${FILE_SERVER_URL}")
    private String url;

    @RequestMapping("/uploadFile")
    //MultipartFile用来接收带有文件的表单
    public Result uploadFile (MultipartFile file) throws Exception {
        try {
            //创建引入的dfsClient工具类
            FastDFSClient dfsClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //获取文件的后缀名
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            //上传文件
            String path = dfsClient.uploadFile(file.getBytes(), ext);
            return new Result(true, url + path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }


    @RequestMapping("/uploadExcel")
    //MultipartFile用来接收带有文件的表单
    public String uploadExcel(MultipartFile file) throws Exception {

        InputStream is = file.getInputStream();
//        InputStream is = new FileInputStream(new File(""));
        Workbook hssfWorkbook = null;
//        if (fileName.endsWith("xlsx")){
//            hssfWorkbook = new XSSFWorkbook(is);//Excel 2007
//        }else if (fileName.endsWith("xls")){
        hssfWorkbook = new HSSFWorkbook(is);//Excel 2003
//
//        }
        // HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
        Brand brandNew = null;
        List<Brand> list = new ArrayList<>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet <hssfWorkbook.getNumberOfSheets(); numSheet++) {
            //HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                //HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                Row hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    brandNew = new Brand();
                    //HSSFCell name = hssfRow.getCell(0);
                    //HSSFCell pwd = hssfRow.getCell(1);
                    Cell id = hssfRow.getCell(0);
                    Cell name = hssfRow.getCell(1);
                    Cell first_char = hssfRow.getCell(2);
                    //这里是自己的逻辑
                    String s = id.toString();
                    Double aDouble = Double.valueOf(s);
                    Integer i = aDouble.intValue();

                    Long idL = Long.valueOf( i.toString());
                    brandNew.setId(idL);


                    brandNew.setName(name.toString());
                    brandNew.setFirstChar(first_char.toString());
                    list.add(brandNew);
                }
            }
        }
        System.out.println(list);

        return "前台传输成功";
    }
}


