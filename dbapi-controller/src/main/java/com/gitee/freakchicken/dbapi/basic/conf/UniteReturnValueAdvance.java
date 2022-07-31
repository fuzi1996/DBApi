package com.gitee.freakchicken.dbapi.basic.conf;

import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.annotations.CustomReturnValue;
import com.gitee.freakchicken.dbapi.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @program: dbApi
 * @author: kensan
 * @create: 2022-07-13 20:25
 */
@Slf4j
@RestControllerAdvice
public class UniteReturnValueAdvance implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> returnType = Objects.requireNonNull(methodParameter.getMethod()).getReturnType();
        boolean isReturnResponseDTOType = ResponseDto.class.isAssignableFrom(returnType);
        AnnotatedElement annotatedElement = methodParameter.getAnnotatedElement();
        CustomReturnValue annotation = AnnotationUtils.findAnnotation(annotatedElement, CustomReturnValue.class);
        return !isReturnResponseDTOType && Objects.isNull(annotation);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(!(body instanceof ResponseDto)){
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMsg("");
            responseDto.setData(body);
            responseDto.setSuccess(true);
            if(body instanceof CharSequence){
                return JSONObject.toJSONString(responseDto);
            }
            return responseDto;
        }
        return body;
    }
}
