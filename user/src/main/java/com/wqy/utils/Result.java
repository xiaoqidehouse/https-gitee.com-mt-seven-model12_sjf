package com.bw.utils;

import java.io.Serializable;
//统一响应结果集
public class Result  implements Serializable {

    private Integer code;
    private String msg;
    private Boolean flag;
    private Object data;


    public static Result success(){
        return new Result(200,"操作成功",true,null);
    }
     public static Result success(Integer code, String msg, Boolean flag,Object data){
        return new Result(code,msg,flag,data);
    }

    public static Result success(Integer code, String msg, Boolean flag){

        return new Result(code,msg,flag,null);
    }
    public static Result success(Object data){

        return new Result(200,"操作成功",true,data);
    }

    public static Result fail(){
        return new Result(500,"操作失败",false,null);
    }
    public static Result fail(String msg){

        return new Result(500,msg,false,null);
    }

    public static Result fail(Integer code, String msg, Boolean flag){
        return new Result(code,msg,flag,null);
    }
    public static Result fail(Object data){
        return new Result(500,"操作失败",false,data);
    }
    public static Result fail(Integer code, String msg, Boolean flag,Object data){
        return new Result(code,msg,flag,data);
    }


    public Result() {
    }

    public Result(Integer code, String msg, Boolean flag, Object data) {
        this.code = code;
        this.msg = msg;
        this.flag = flag;
        this.data = data;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
