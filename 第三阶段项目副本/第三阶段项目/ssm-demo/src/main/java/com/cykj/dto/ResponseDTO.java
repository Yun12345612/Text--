package com.cykj.dto;

import com.cykj.pojo.CyAdmin;
import com.cykj.pojo.User;

/**
 * @author xairi1huadian
 * @description TODO
 * @date 2024/1/5 10:30
 */
public class ResponseDTO {
    private int code;
    private String msg;
    private Object data;

    public ResponseDTO(int code,String msg,Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public static ResponseDTO success(String msg, User dbUser) {
        return new  ResponseDTO(200, msg, dbUser);
    }
    public static ResponseDTO success(Object data, String msg) {
        return new ResponseDTO(200, msg, data);
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static ResponseDTO success() {
        return new ResponseDTO(200,"success",null);
    }
    public static ResponseDTO success(int code,String msg,Object data) {
        return new ResponseDTO(code,msg,data);
    }
    public static ResponseDTO success(Object data) {
        return new ResponseDTO(200,"success",data);
    }
    public static ResponseDTO error() {
        return new ResponseDTO(0,"error",null);
    }
    public static ResponseDTO error(int code,String msg) {
        return new ResponseDTO(code,msg,null);
    }
//    public static ResponseDTO error(String msg) {
//        return new ResponseDTO(0,msg,null);
//    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
