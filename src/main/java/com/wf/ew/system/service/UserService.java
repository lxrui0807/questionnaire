package com.wf.ew.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.system.model.User;

public interface UserService extends IService<User> {

    User getByUsername(String username);

    /**
     *@author 刘喜瑞
     * 根据userId查询user其他信息
     * @param userId
     * @returnUser
     */
    User getUserById(String userId);

    /**
     * @param phone
     * @return
     * 注册页手机号码唯一性校验
     */
    Integer checkPhone(String phone);
    /**
     *@author 刘喜瑞
     * 根据userId查询user其他信息
     * @param phone
     * @param password
     * @return boolean
     */
    Integer updatePswByPhone(String phone,String password);

    /**
     * 其他系统修改用户信息
     * @param user
     * @return
     */
    Integer updatUserBody(User user);
}
