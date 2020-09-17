package com.wf.ew.system.controller;

import com.wf.ew.common.JsonResult;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.model.User;
import com.wf.ew.system.model.UserRole;
import com.wf.ew.system.service.OthersysService;
import com.wf.ew.system.service.RoleService;
import com.wf.ew.system.service.UserRoleService;
import com.wf.ew.system.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RoleService roleService;
    @Autowired
    public OthersysService othersysService;

    @ApiOperation(value = "用户名唯一性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "账号", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/checkUsername")
    public JsonResult checkUsername(String username){
        User user = userService.getByUsername(username);
        if(user==null){
            return JsonResult.ok("新用户");
        }else{
            return JsonResult.error("已注册用户");
        }
    }

    @ApiOperation(value = "手机号码唯一性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/checkPhone")
    public JsonResult checkPhone(String phone){
        Integer count = userService.checkPhone(phone);
        if(count==0){
            return JsonResult.ok("该手机号码可用");
        }else {
            return JsonResult.error("该手机号码已存在");
        }
    }

    @ApiOperation(value = "注册用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User", paramType = "form"),
            @ApiImplicitParam(name = "roleIds", value = "角色Id", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult reg(User user,String roleIds){
        String olpe=null;
        //  对密码进行加密
        if(user!=null && user.getPassword()!=null && !user.getPassword().equals("")){
            String password = user.getPassword();
            olpe=password;
            //  对密码进行加密
            String digest = DigestUtils.md5DigestAsHex(password.getBytes());
            user.setPassword(digest);
        }
        String remarks="";
        String[] split = roleIds.split(",");
        //  sys_user新增
        if (userService.save(user)) {

            List<UserRole> userRoles = new ArrayList<>();
            for (String roleId : split) {
                Role r=roleService.getById(roleId);
                if(null!=r){
                    remarks=remarks+","+r.getRoleName();
                }
                UserRole userRole = new UserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(user.getUserId());
                userRoles.add(userRole);
            }

            if (userRoleService.saveBatch(userRoles)) {
                //注册四个系统用户
                if(null!=remarks&&remarks.length()>0){
                    remarks=remarks.substring(1);
                }

                User nu= user;
                nu.setPassword(olpe);
                nu.setOldUsername(remarks);//角色名
                othersysService.addAllUser(nu);
                return JsonResult.ok("sys_user_role 新增成功");
            }else{
                return JsonResult.error("sys_user_role 新增失败");
            }
        } else {
            return JsonResult.error("sys_user新增失败");
        }
    }
}
