package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.exception.BusinessException;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.model.User;
import com.wf.ew.system.model.UserRole;
import com.wf.ew.system.service.RoleService;
import com.wf.ew.system.service.UserRoleService;
import com.wf.ew.system.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/common")
public class OthersysController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;


    /**
     *  添加用户（别的系统调用）
     * @param user
     * @return
     */
    @PostMapping("/addUserBody")
    public JsonResult add(User user) {
        user.setUserId(null);
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setState(null);
        user.setEmailVerified(null);
        if (userService.save(user)) {
            List<UserRole> userRoles = new ArrayList<>();
            if(null!=user.getOldUsername()){
                String[] roleNames = user.getOldUsername().split(",");
                if(roleNames!=null&&roleNames.length>0){
                    List<Role> list=roleService.list();
                    for (int i = 0; i <roleNames.length ; i++) {
                        for (Role r:list) {
                            if(r.getRoleName().equals(roleNames[i])){
                                UserRole userRole = new UserRole();
                                userRole.setRoleId(r.getRoleId());
                                userRole.setUserId(user.getUserId());
                                userRoles.add(userRole);
                                break;
                            }
                        }
                    }
                    if(userRoles.size()<=0){
                        Role r=roleService.getById("role.other");
                        UserRole userRole = new UserRole();
                        userRole.setRoleId(r.getRoleId());
                        userRole.setUserId(user.getUserId());
                        userRoles.add(userRole);
                    }
                    if (!userRoleService.saveBatch(userRoles)) {
                        throw new BusinessException("添加失败");
                    }
                    return JsonResult.ok("添加成功");
                }


            }
        }
        return JsonResult.error("添加失败");
    }


    /**
     * 修改用户信息（别的系统调用）
     * @param user
     * @return
     */
    @PostMapping("/saveUserBody")
    public JsonResult update(User user) {
        User oldu=userService.getByUsername(user.getOldUsername());
        if (StringUtils.isNotBlank(user.getPassword())) {
            oldu.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        oldu.setUsername(user.getUsername());
        oldu.setNickName(user.getNickName());
        Integer result=userService.updatUserBody(oldu);
        if (null!=result&&result>0) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    /**
     * 修改密码（别的系统调用）
     * @param oldPsw
     * @param newPsw
     * @param username
     * @return
     */
    @PostMapping("/savePwdBody")
    public JsonResult updatePsw(String username, String oldPsw, String newPsw) {
        User olduser=userService.getByUsername(username);
        if(null!=olduser){
           /* if (!DigestUtils.md5DigestAsHex(oldPsw.getBytes()).equals(olduser.getPassword())) {
                return JsonResult.error("原密码不正确");
            }*/
            User user = new User();
            user.setUserId(olduser.getUserId());
            user.setPassword(DigestUtils.md5DigestAsHex(newPsw.getBytes()));
            if (userService.updateById(user)) {
                return JsonResult.ok("修改成功");
            }
        }
        return JsonResult.error("修改失败");
    }


    /**
     * 根据用户名删除用户（别的系统调用）
     * @param username
     * @return
     */
    @PostMapping("/deleteBody")
    public JsonResult delete(String username) {
        if (userService.remove(new QueryWrapper<User>().eq("username",username))) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }
}


