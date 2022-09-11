package com.gitee.freakchicken.dbapi.basic.service;

import java.util.Map;

public interface IPService {
    
    void on(String mode, String ip);

    void _on(String mode, String ip);

    void off();

    void _off();

    Map<String, String> detail();

    boolean checkIP(String originIp);

}
