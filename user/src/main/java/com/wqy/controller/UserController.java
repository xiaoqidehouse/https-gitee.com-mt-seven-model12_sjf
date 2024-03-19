package com.wqy.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.barcodes.qrcode.QRCodeWriter;
import com.itextpdf.barcodes.qrcode.WriterException;
import com.wqy.SmsUtils;
import com.wqy.domain.User;
import com.wqy.service.UserService;
import com.wqy.utils.Result;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.zxing.BarcodeFormat.*;

@RequestMapping("user")
@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 登录方法
     */
    @PostMapping("login")
    public Result login(User user){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",user.getUserName());
        User one = userService.getOne(queryWrapper);
        if (one==null){
            return Result.fail("该用户不存在,请注册");
        }
        if (!one.getPassword().equals(user.getPassword())){
            return Result.fail("密码错误");
        }

        String token = JWT.create()
                .setPayload("userName", one.getUserName())
                .setSigner(JWTSignerUtil.none())
                .sign();
        return Result.success(token);
    }

    /**
     * 查询列表
     * @return
     */
    @RequestMapping("list")
    public Result getlist(){
        List<User> list = userService.list();
        return Result.success(list);
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping("add")
    public Result addUser(User user){
        userService.save(user);
        return Result.success("注册用户成功,请登录");
    }

    /**
     * 修改
     * @param user
     * @return
     */
    @RequestMapping("updataById")
    public Result updataById(@RequestBody User user){
        userService.saveOrUpdate(user);
        return Result.success("修改完成");
    }


    /**
     * 删除
     */
    @RequestMapping("delById")
    public Result delById(String userId){
        userService.removeById(userId);
        return Result.success("删除成功完成");
    }

    /**
     * 根据性别统计柱状图
     */
    @RequestMapping("sexBarChart")
    public Map<String,Object> Barchart(){
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Object> x = new ArrayList<>(); //性别
        ArrayList<Object> y = new ArrayList<>(); //数量

        List<User> list =  userService.getsexBarChart();
        for (User user : list) {
            if (user.getX().equals("0")){
                x.add("女");
            }else {
                x.add("男");
            }
            y.add(user.getY());
        }
        map.put("x",x);
        map.put("y",y);
        return map;
    }


    /**
     * 根据出生年份统计柱状图
     */
    @RequestMapping("YearBarChart")
    public Map<String,Object> YearBarChart(){
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Object> x = new ArrayList<>(); //性别
        ArrayList<Object> y = new ArrayList<>(); //数量

        List<User> list =  userService.getYearBarChart();
        for (User user : list) {
            x.add(user.getX());
            y.add(user.getY());
        }
        map.put("x",x);
        map.put("y",y);
        return map;
    }

    //当天生日得用户短信提醒
    @XxlJob("Happy birthday")
    private void sendmail(){
        List<User> list = userService.list();
        for (User user : list) {
           if (user.getCreateTime().equals(new Date())){
               SmsUtils.sendSms(user.getPhonenumber(),"生日快乐","11");
           }
        }
    }

    @Autowired
    JavaMailSender javaMailSender;
    //发送邮件，提醒支付
    @XxlJob("HappyBirthday")
    @RequestMapping("HappyBirthday")
    private void tixing() throws InterruptedException {
        List<User> list = userService.list();

        for (User user : list) {
            //判断今天是否是她的生日
           User user1  =  userService.getMonthDay(user.getUserId());
            if (user1.getResult().equals("相同")){
                TimeUnit.SECONDS.sleep(5);
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setTo("874649276@qq.com");
                simpleMailMessage.setFrom("874649276@qq.com");
                simpleMailMessage.setSubject("生日祝福");
                simpleMailMessage.setText("您好"+user.getNickName()+"祝您生日快乐");
                simpleMailMessage.setSentDate(new Date());
                javaMailSender.send(simpleMailMessage);
            }
        }
        System.err.println("-------------xxl-job提示消息发送成功------------");
    }

    //根据性别分类，然后根据stream流进行统计分类.
    @RequestMapping("steam")
    public Result sexSteam(){
        List<User> users = userService.list();

        //统计用户分类
        Map<String, Long> collect = users.stream()
                .collect(Collectors.groupingBy(User::getSex, Collectors.counting()));

        HashMap<String, Object> map = new HashMap<>();
        map.put("女",collect.get("0"));
        map.put("男",collect.get("1"));

        return Result.success(map);
    }

    //根据出生年龄分类，然后根据stream流进行统计分类.
    @RequestMapping("yearSteam")
    public Result yearStream(){
        //统计用户分类
        List<User> users = userService.list();
        Map<Date, Long> collect = users.stream()
                .collect(Collectors.groupingBy(User::getCreateTime, Collectors.counting()));

        HashMap<String, Object> map = new HashMap<>();
        map.put("2022",collect.get("2022"));
        map.put("2023",collect.get("2023"));
        map.put("2024",collect.get("2024"));
        return Result.success(map);
    }




    //生成二维码
    @RequestMapping("ewCode")
    public void ewCode(){
        QrCodeUtil.generate(//
                "http://hutool.cn/", //二维码内容
                QrConfig.create().setImg("https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg"), //附带logo
                FileUtil.file("e:/qrcodeWithLogo.jpg")//写出到的文件
        );
    }






}
