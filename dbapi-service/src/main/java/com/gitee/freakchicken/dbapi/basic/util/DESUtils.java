package com.gitee.freakchicken.dbapi.basic.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DESUtils {

    /**
     * Description 根据键值进行加密
     *
     * @param data 待加密数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        return Base64.encodeBase64String(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 根据键值进行解密
     *
     * @param data 待解密数据
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data) throws Exception {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        byte[] bytes = Base64.decodeBase64(data);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}