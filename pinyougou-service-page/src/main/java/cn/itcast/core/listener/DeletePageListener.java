package cn.itcast.core.listener;
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

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.servlet.ServletContext;
import java.io.File;

/**
 * 删除静态页面
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 16:23 2018/12/28
 */
public class DeletePageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage ATM = (ActiveMQTextMessage) message;
        try {
            String id = ATM.getText();
            System.out.println("删除页面的监听器收到的id为"+id);
            //删除id对应的静态页面

            WebApplicationContext currentWebApplicationContext =
                    ContextLoader.getCurrentWebApplicationContext();
            ServletContext servletContext =
                    currentWebApplicationContext.getServletContext();

            String realPath = servletContext.getRealPath(id + ".html");


            System.out.println(realPath);
            File file = new File(realPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            } else {
                System.out.println("文件不存在或非文件形式");
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
