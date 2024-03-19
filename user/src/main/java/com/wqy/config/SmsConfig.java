package com.ff.config;

import com.zhenzi.sms.ZhenziSmsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {

    @Value("${sms.apiUrl}")
    private String apiUrl;

    @Value("${sms.appId}")
    private String appId;

    @Value("${sms.appSecret}")
    private String appSecret;

    //通过配置和Bean注解，把ZhenziSmsClient对象放到spring的IOC容器中来统一管理，这样才能自动装配获取
    @Bean
    public ZhenziSmsClient zhenziSmsClient(){
        return new ZhenziSmsClient(apiUrl, appId, appSecret);
    }
}
