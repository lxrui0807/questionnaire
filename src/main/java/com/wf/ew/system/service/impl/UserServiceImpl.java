package com.wf.ew.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.system.dao.UserMapper;
import com.wf.ew.system.model.User;
import com.wf.ew.system.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByUsername(String username) {
        return baseMapper.getByUsername(username);
    }
    public  User getUserById(String userId){
        return  baseMapper.getUserById(userId);
    }

    /**
     * @param phone
     * @return
     * 注册页手机号码唯一性校验
     */
    public Integer checkPhone(String phone){
        return baseMapper.checkPhone(phone);
    }

    /**
     * @decription 根据手机号修改密码（手机号也是唯一的标识）
     * @param phone
     * @param password
     * @return boolean
     */

    public Integer updatePswByPhone(String phone, String password) {
        return baseMapper.updatePswByPhone(phone,password);
    }


    /**
     * 其他系统修改用户信息
     * @param user
     * @return
     */
    public Integer updatUserBody(User user){
        return baseMapper.updatUserBody(user);
    }
}
