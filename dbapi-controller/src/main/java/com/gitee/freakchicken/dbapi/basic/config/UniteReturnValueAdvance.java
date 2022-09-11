package com.gitee.freakchicken.dbapi.basic.config;

import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.annotations.CustomReturnValue;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.AnnotatedElement;
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
        boolean isReturnResponseDTOType = ResponseDTO.class.isAssignableFrom(returnType);
        AnnotatedElement annotatedElement = methodParameter.getAnnotatedElement();
        CustomReturnValue annotation = AnnotationUtils.findAnnotation(annotatedElement, CustomReturnValue.class);
        return !isReturnResponseDTOType && Objects.isNull(annotation);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(!(body instanceof ResponseDTO)){
            ResponseDTO responseDto = new ResponseDTO();
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
