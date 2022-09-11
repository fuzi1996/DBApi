package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.mapper.AlarmMapper;
import com.gitee.freakchicken.dbapi.basic.service.IAlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: dbApi
 * @author: kensan
 * @create: 2022-09-11 21:04
 */
@Slf4j
@Service
public class AlarmServiceImpl implements IAlarmService {
		@Autowired
		private AlarmMapper alarmMapper;
}
