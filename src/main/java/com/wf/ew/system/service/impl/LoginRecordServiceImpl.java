package com.wf.ew.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.system.dao.LoginRecordMapper;
import com.wf.ew.system.model.LoginRecord;
import com.wf.ew.system.service.LoginRecordService;
import org.springframework.stereotype.Service;

@Service
public class LoginRecordServiceImpl extends ServiceImpl<LoginRecordMapper, LoginRecord> implements LoginRecordService {

}
