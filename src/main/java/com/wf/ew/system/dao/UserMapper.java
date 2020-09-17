package com.wf.ew.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.system.model.User;

public interface UserMapper extends BaseMapper<User> {

    User getByUsername(String username);
    User getUserById(String userId);

    /**
     * @param phone
     * @return
     * 注册页手机号码唯一性校验
     */
    Integer checkPhone(String phone);
    /**
     * @param phone
     * @param password
     * @return
     * 根据手机号修改密码（手机号也是唯一的标识）
     */
    Integer updatePswByPhone(String phone,String password);

    /**
     * 其他系统修改用户信息
     * @param user
     * @return
     */
    Integer updatUserBody(User user);
}
