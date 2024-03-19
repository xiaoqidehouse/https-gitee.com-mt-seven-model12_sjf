package com.wqy.controller;

import com.wqy.domain.Student;
import com.wqy.domain.StudentSubject;
import com.wqy.domain.Subject;
import com.wqy.service.StudentService;
import com.wqy.service.StudentSubjectService;
import com.wqy.service.SubjectService;
import com.wqy.utils.Result;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("student")
@CrossOrigin
public class StudentController {

    @Autowired
    StudentService studentService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    StudentSubjectService ssService;
    @Autowired
    RedisTemplate redisTemplate;
    private byte aByte;

    /**
     * 列表
     * @return
     */
    @RequestMapping("list")
    public Result getlist(){
        List<Student> list =  studentService.getlist();
        return Result.success(list);
    }

    /**
     * 删除
     */
    @RequestMapping("delete")
    public Result deleteStudent(String id){
        studentService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 修改
     */
    @RequestMapping("update")
    public Result update(@RequestBody Student student){
        //修改主表
        studentService.updateById(student);
        //修改所选课程
        List<Integer> zkmId = student.getZkmId();
        //先删除所有课程
        ssService.deleteByStudentId(student.getId());
        //再添加所选的课程
        for (Integer i : zkmId) {
            ssService.addById(i,student.getId());
        }

        return Result.success("修改成功");
    }
    //修改回显
    @RequestMapping("echoStudent")
    public Result echoStudent(String id){
        //查看该同学的课程信息
        List<Student> list =  studentService.echoStudent(id);
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (Student student : list) {
            arrayList.add(student.getSubId());
        }
        //看看该同学其他信息
        Student byId = studentService.getById(id);
        byId.setZkmId(arrayList);
        return Result.success(byId);
    }

    /**
     * 课程列表
     * @return
     */
    @RequestMapping("subjectList")
    public Result subjectList(){
        List<Subject> list = subjectService.list();
        return Result.success(list);
    }

    /**
     * 添加
     */
    @RequestMapping("add")
    public Result addStudent(@RequestBody Student student){

        //生成学生编号
        //定义日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //格式化日期
        String format = dateFormat.format(new Date());
        //redis生成自增序列
        Long a = redisTemplate.opsForValue().increment("a");
        //给学生编号赋值
        student.setStudentNum("BW-"+format+"-"+a);

        //先添加进学生的信息
        boolean save = studentService.save(student);
        //获取刚刚添加的学生的id
        if (save==true){
            //获取前端传来的课程id集合
            List<Integer> zkmId = student.getZkmId();
            //获取刚添加的学生id
           Student s =  studentService.getByStudentNum(student.getStudentNum());
            System.err.println(s.getId());
            //添加课程
            for (Integer i : zkmId) {
                ssService.addById(i,s.getId());
            }
        }
        return Result.success("添加成功");
    }

    //报表
    @RequestMapping("baobiao")
    public Map<String,Object> baobiao(){
        HashMap<String, Object> map = new HashMap<>();

        ArrayList<Object> x = new ArrayList<>(); //性别
        ArrayList<Object> y = new ArrayList<>(); //数量

        List<StudentSubject> list =  ssService.getBaoBiao();

        for (StudentSubject subject : list) {
            if (subject.getX().equals("1")){
                x.add("马原");
            } else if (subject.getX().equals("2")) {
                x.add("毛概");
            } else if (subject.getX().equals("3")) {
                x.add("瑜伽");
            } else if (subject.getX().equals("5")) {
                x.add("环创");
            }
           y.add(subject.getY());
        }

        map.put("x",x);
        map.put("y",y);

        return map;
    }
}
