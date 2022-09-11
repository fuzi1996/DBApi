package com.gitee.freakchicken.dbapi.basic.service;


import com.gitee.freakchicken.dbapi.basic.domain.AppToken;


public interface IAppTokenService {

    AppToken generateToken(String appId, String secret);

    /**
     * 检查token是否有效，有效就返回token对应的appId
     *
     * @param token
     * @return
     */
    String verifyToken(String token);
}