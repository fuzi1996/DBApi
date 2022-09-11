package com.gitee.freakchicken.dbapi.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @program: api
 * @description:
 * @author: jiangqiang
 * @create: 2020-08-11 11:22
 **/
public class ResponseDTO<T> {

    public static final ResponseDTO<Object> SUCCESS = new ResponseDTO<>(true,"",null);

    private boolean success;
    private String msg;

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private T data;

    public ResponseDTO(){

    }

    public ResponseDTO(boolean success,String msg,T data){
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static ResponseDTO<Object> successWithMsg(String msg) {
        return new ResponseDTO<>(true,msg,null);
    }

    public static <T> ResponseDTO<T> successWithData(T data) {
        return new ResponseDTO<>(true,"",data);
    }

    public static ResponseDTO<Object> fail(String msg) {
        return new ResponseDTO<>(false,msg,null);
    }
}
