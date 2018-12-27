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

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.pojo.log.PayLog;
import com.alibaba.dubbo.config.annotation.Service;

import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import utils.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Chenqi
 * @Description:
 * @Date: Create in 20:13 2018/12/24
 */
@Service
public class PayServiceImpl implements PayService {
    //配置文件里
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    @Autowired
    private PayLogDao payLogDao;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 调用腾讯,穿参数让腾讯返回生成二维码的地址
     *
     * @param name
     * @return
     */
    @Override
    public Map<String, String> createNative(String name) {
        //获取当前用户合并后的支付日志
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(name);
        //1:调统一下单Api
        //发出Https请求? Https Apache HttpClient
        Map<String, String> param = new HashMap<>();
        // 字段名 	变量名 	必填 	类型 	示例值 	描述
        //公众账号ID 	appid 	是 	String(32) 	wxd678efh567hg6787 	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid", appid);
        //商户号 	mch_id 	是 	String(32) 	1230000109 	微信支付分配的商户号
        param.put("mch_id", partner);
        //随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，长度要求在32位以内。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());

//        商品描述 	body 	是 	String(128) 	腾讯充值中心-QQ会员充值
        param.put("body", "品优购要你钱,你转不");
//        商品简单描述，该字段请按照规范传递，具体请见参数规定
//        商户订单号 	out_trade_no 	是 	String(32) 	20150806125346 	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        param.put("out_trade_no", payLog.getOutTradeNo());
//        标价金额 	total_fee 	是 	Int 	88 	订单总金额，单位为分，详见支付金额
        //param.put("total_fee", String.valueOf(payLog.getTotalFee()));
        param.put("total_fee", "1");
//        终端IP 	spbill_create_ip 	是 	String(16) 	123.12.12.123 	APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
        param.put("spbill_create_ip", "127.0.0.1");
//        订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
//
//        建议：最短失效时间间隔大于1分钟
//        订单优惠标记 	goods_tag 	否 	String(32) 	WXG 	订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠
//        通知地址 	notify_url 	是 	String(256) 	http://www.weixin.qq.com/wxpay/pay.php 	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        param.put("notify_url", "www.itcast.cn");
//        交易类型 	trade_type 	是 	String(16) 	JSAPI
        param.put("trade_type", "NATIVE");


        try {
//         签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	通过签名算法计算得出的签名值，详见签名生成算法
//        签名类型 	sign_type 	否 	String(32) 	MD5 	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);

            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);

            //设置https
            httpClient.setHttps(true);
            //入参  Map 实际字符串
            httpClient.setXmlParam(xml);
            //发出post请求
            httpClient.post();
            //响应
            String result = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            //支付ID
            map.put("out_trade_no", payLog.getOutTradeNo());
            map.put("total_fee", String.valueOf(payLog.getTotalFee() * 100));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        //发出Https请求? Https Apache HttpClient
        Map<String, String> param = new HashMap<>();
        // 字段名 	变量名 	必填 	类型 	示例值 	描述
        //公众账号ID 	appid 	是 	String(32) 	wxd678efh567hg6787 	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid", appid);
        //商户号 	mch_id 	是 	String(32) 	1230000109 	微信支付分配的商户号
        param.put("mch_id", partner);
        //随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，长度要求在32位以内。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        //支付订单号
        param.put("out_trade_no", out_trade_no);
        try {
//        签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	通过签名算法计算得出的签名值，详见签名生成算法
//        签名类型 	sign_type 	否 	String(32) 	MD5 	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);

            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            HttpClient httpClient = new HttpClient(url);

            //设置https
            httpClient.setHttps(true);
            //入参  Map 实际字符串
            httpClient.setXmlParam(xml);
            //post
            httpClient.post();
            //响应
            String result = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;


    }
}
