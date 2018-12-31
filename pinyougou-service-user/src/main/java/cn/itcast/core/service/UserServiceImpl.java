package cn.itcast.core.service;//                            _ooOoo_
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

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.log.PayLogQuery;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mysql.jdbc.TimeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 21:44 2018/12/18
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination queueSmsDestination;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PayLogDao payLogDao;

    /**
     * 发送消息给MQ让微服务调用腾讯云发送验证码
     * @param phone
     */
    @Override
    public void sendCode(String phone) {
        //生成六位验证码
        String code = RandomStringUtils.randomNumeric(6);
        //把生成的验证码放到Redis中,等用户输入验证码之后拿出来进行验证
        redisTemplate.boundValueOps(phone).set(code);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.DAYS);
        //发送消息
        jmsTemplate.send(queueSmsDestination,new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile", phone);
                mapMessage.setString("template_code", "249419");
                mapMessage.setString("sign_name", "优码云");
                mapMessage.setString("param", code);

                return mapMessage;
            }
        });
    }

    /**
     * 添加用户
     * @param smscode
     * @param user
     */
    @Override
    public void add(String smscode, User user) {
        //把phone和验证码拿出来去Redis中查
        String phone = user.getPhone();
        
        String code = (String) redisTemplate.boundValueOps(phone).get();
        if (code != null && smscode.equals(code)) {
            //验证码没问题,添加到数据库
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        }else {
            //验证码不正确跑出异常
            throw new RuntimeException("验证码不正确");
        }
    }

    @Override
    //根据名字(当前登录的)查询数据
    public User getUserInfomation(String name) throws Exception {
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();
        criteria.andUsernameEqualTo(name);
        List<User> users = userDao.selectByExample(userQuery);
        if (0<users.size()){
            users.get(0).setPassword("");
            return users.get(0);
        }
        return null;
    }

    @Override
    public void saveUserInfomation(User user) throws Exception {


        UserQuery userQuery = new UserQuery();
        userQuery.createCriteria().andUsernameEqualTo(user.getUsername());
        userDao.updateByExampleSelective(user,userQuery );

    }

    @Override
    public PageResult queryPage(int pageNum, int pageSize) {
        //分页小助手第一步
        PageHelper.startPage(pageNum, pageSize);
        //查询全部,生成List集合对象
        List<User> userList = userDao.selectByExample(null);

        //查询其他需要的数据，并将这些数据，封装到userList对象中
        for (User user : userList) {
            //拿user表的userId
            String username = user.getUsername();
            //设置查询条件
            PayLogQuery payLogQuery = new PayLogQuery();
            PayLogQuery.Criteria criteria1 = payLogQuery.createCriteria();
            criteria1.andUserIdEqualTo(username);
            //查询payLog表，得到结果集
            List<PayLog> payLogs_userId = payLogDao.selectByExample(payLogQuery);
            //循环遍历，查询userId，算出总次数(下单次数)，放到user表中
            int idTimes=0;
            for (PayLog payLog_userId : payLogs_userId) {
                String userId = payLog_userId.getUserId();
                if(userId!=null){
                    idTimes++;
                }
            }
            user.setIdTimes(idTimes);

            //添加查询条件之交易状态，再查询一次
            criteria1.andTradeStateEqualTo("1");
            List<PayLog> payLogs_tradeState = payLogDao.selectByExample(payLogQuery);
            int tradeTimes=0;
            for (PayLog payLog_tradeState : payLogs_tradeState) {
                String tradeState = payLog_tradeState.getUserId();
                if(tradeState!=null){
                    tradeTimes++;
                }
            }
            user.setTradeTimes(tradeTimes);

        }

        //将userList对象强制转换为Page对象
        Page<User> page=(Page<User>)userList;

        //利用page对象，生成pageResult对象
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return  pageResult;
    }

//    分页助手——备份
//    @Override
//    public PageResult queryPage(int pageNum, int pageSize) {
//        //分页小助手第一步
//        PageHelper.startPage(pageNum, pageSize);
//        //查询全部,直接生成Page对象
//        Page<User> page = (Page<User>) userDao.selectByExample(null);
//        for (User user : page) {
//
//        }
//        //生成pageResult对象
//        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
//        return  pageResult;
//    }
}
