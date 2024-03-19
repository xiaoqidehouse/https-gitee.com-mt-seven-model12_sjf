package com.wqy;



import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhenzi.sms.ZhenziSmsClient;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @Author 孙俊福
 * @Date 2023/12/25  21:40
 * @Version 1.0
 */
public class SmsUtils {


    /**
     * 授权id
     */
    public static final String APP_ID = "113037";
    /**
     * 授权秘钥
     */
    public static final String APP_SECRET = "5c4d0b8a-60f6-4790-a6fc-2313120258d9";
    /**
     * 榛子云api地址
     */
    public static final String API_URL = "https://sms_developer.zhenzikj.com";

    /**
     * 通用模板
     */
    public static final String TEMPLATE_ID = "11459";

    /**
     * 发送短信
     * @param phone 短信接收号码
     * @param msg 短信内容
     * @param minute 有效时间（分钟）
     * @return 发送是否成功
     */

    public static boolean sendSms(String phone, String msg,String minute) {
        try {
            ZhenziSmsClient client = new ZhenziSmsClient(API_URL, APP_ID, APP_SECRET);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("number", phone);//电话号码
            params.put("templateId", TEMPLATE_ID);//使用的短信模板id
            String[] templateParams = new String[2];
            templateParams[0] = msg;
            templateParams[1] = minute;
            params.put("templateParams", templateParams);//发送的内容
            String result = client.send(params);//发送短信
            JSONObject jsonObject = JSONUtil.parseObj(result);
            Object code = jsonObject.get("code");
            if ("0".equals(code + "")) {
                System.out.println("发送成功！");
                return true;
            } else {
                System.out.println("发送失败");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
