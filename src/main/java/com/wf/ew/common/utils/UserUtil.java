package com.wf.ew.common.utils;

import com.wf.ew.system.model.User;
import com.wf.ew.system.service.RoleService;
import com.wf.ew.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wf.jwtp.provider.Token;
import org.wf.jwtp.util.SubjectUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class UserUtil {
    //第二部注入
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    //第三步 建一个静态的本类
    private static  UserUtil userUtil;
    //第四步 初始化
    @PostConstruct
    public void init() {
        userUtil = this;
    }
    /**
     * 获取当前登录的userId
     */
    public static String getLoginUserId(HttpServletRequest request) {
        Token token = getLoginToken(request);
        return token == null ? null : token.getUserId();
    }

    /**
     * 获取当前登录人的token
     * @param request
     * @return Token
     */
    public static Token getLoginToken(HttpServletRequest request) {
        return SubjectUtil.getToken(request);
    }
    /**
     * 根据ID获取用户
     * @param id
     * @return 取不到返回null
     */
    public static User get(String id){
      //  User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
//        if (user ==  null){
            User user = userUtil.userService.getById(id);
            if (user == null){
                return null;
            }
            user.setRoles(userUtil.roleService.getRolesByUserId(id));
//            CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
//            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
//        }
        return user;
    }

    /**
     * 判斷当前登录人是否拥有某个角色
     * @param role
     * @return
     */
    public static boolean isPermission(String role,HttpServletRequest request){
            Token token= getLoginToken(request);
            String roles[]=token.getRoles();
            return Arrays.asList(roles).contains(role);
    }
}
