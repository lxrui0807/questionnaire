package com.wf.ew.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.exception.BusinessException;
import com.wf.ew.common.log.MyLog;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.model.User;
import com.wf.ew.system.model.UserRole;
import com.wf.ew.system.service.OthersysService;
import com.wf.ew.system.service.RoleService;
import com.wf.ew.system.service.UserRoleService;
import com.wf.ew.system.service.UserService;
import com.yuanjing.framework.common.beanvalidator.BeanValidators;
import com.yuanjing.framework.common.utils.DateUtils;
import com.yuanjing.framework.common.utils.excel.ExportExcel;
import com.yuanjing.framework.common.utils.excel.ImportExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.wf.jwtp.annotation.RequiresPermissions;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(value = "用户管理", tags = "user")
@RestController
@RequestMapping("${api.version}/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    public RedisTemplate<String,Object> redisTemplate;
    @Autowired
    public OthersysService othersysService;

    /**
     * 验证Bean实例对象
     */
    @Autowired
    protected Validator validator;

    @MyLog(value = "查询所有用户记录")  //这里添加了AOP的自定义注解
    @RequiresPermissions("get:/v1/user")
    @ApiOperation(value = "查询所有用户", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "第几页", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "limit", value = "每页多少条", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "searchKey", value = "筛选条件字段",required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "searchValue", value = "筛选条件关键字",required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @GetMapping()
    public PageResult<User> list(Integer page, Integer limit, String searchKey, String searchValue) {
        if (page == null) {
            page = 0;
        }
        if (limit == null) {
            limit = 10;
        }
        Page<User> userPage = new Page<>(page, limit);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (searchKey != null && !searchKey.trim().isEmpty() && searchValue != null && !searchValue.trim().isEmpty()) {
            wrapper.like(searchKey, searchValue);
        }
        wrapper.orderBy(true,true,"create_time" );
        userService.page(userPage, wrapper);
        List<User> userList = userPage.getRecords();
        // 关联查询role
        List<String> userIds = new ArrayList<>();
        for (User one : userList) {
            userIds.add(one.getUserId());
        }
        if(userIds.size()>0){
            QueryWrapper<UserRole> wrapper1=new QueryWrapper<>();
            List<UserRole> userRoles = userRoleService.list(wrapper1.in("user_id", userIds));
            List<Role> roles = roleService.list(null);
            for (User one : userList) {
                List<Role> tempUrs = new ArrayList<>();
                for (UserRole ur : userRoles) {
                    if (one.getUserId().equals(ur.getUserId())) {
                        for (Role r : roles) {
                            if (ur.getRoleId().equals(r.getRoleId())) {
                                tempUrs.add(r);
                            }
                        }
                    }
                }
                one.setRoles(tempUrs);
            }
        }
        return new PageResult<>(userList, userPage.getTotal());
    }

    @RequiresPermissions("post:/v1/user")
    @ApiOperation(value = "添加用户", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User", paramType = "form"),
            @ApiImplicitParam(name = "roleId", value = "用户角色id，多个用','分割", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult add(User user, String roleIds) {
        String remarks="";
        String[] split = roleIds.split(",");
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        user.setState(null);
        user.setEmailVerified(null);
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
            if (!userRoleService.saveBatch(userRoles)) {
                throw new BusinessException("添加失败");
            }

            //添加四个系统的用户
            if(null!=remarks&&remarks.length()>0){
                remarks=remarks.substring(1);
            }
            //添加四个系统的用户
            User nu=user;
            nu.setPassword("123456");
            nu.setOldUsername(remarks);//角色名
            othersysService.addAllUser(nu);
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @RequiresPermissions("put:/v1/user")
    @ApiOperation(value = "修改用户", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User", paramType = "form"),
            @ApiImplicitParam(name = "roleId", value = "用户角色id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping()
    public JsonResult update(User user, String roleIds) {
        User oldu=userService.getUserById(user.getUserId());
        String[] split = roleIds.split(",");
        user.setPassword(null);
        user.setState(null);
        user.setEmailVerified(null);
        if (userService.updateById(user)) {
            List<UserRole> userRoles = new ArrayList<>();
            for (String roleId : split) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(user.getUserId());
                userRoles.add(userRole);
            }
            userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", user.getUserId()));
            if (!userRoleService.saveBatch(userRoles)) {
                throw new BusinessException("修改失败");
            }

            //修改四个系统的用户信息
            user.setOldUsername(oldu.getUsername());
            othersysService.updateAllUser(user);
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @RequiresPermissions("put:/v1/user/state")
    @ApiOperation(value = "修改用户状态", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "state", value = "状态：0正常，1冻结", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping("/state")
    public JsonResult updateState(String userId, Integer state) {
        if (state == null || (state != 0 && state != 1)) {
            return JsonResult.error("state值需要在[0,1]中");
        }
        User user = new User();
        user.setUserId(userId);
        user.setState(state);
        if (userService.updateById(user)) {
            return JsonResult.ok();
        }
        return JsonResult.error();
    }

    //@RequiresPermissions("put:/v1/user/psw")
    @ApiOperation(value = "修改自己密码", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPsw", value = "原密码", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "newPsw", value = "新密码", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping("/psw")
    public JsonResult updatePsw(String oldPsw, String newPsw, HttpServletRequest request) {
        if (!DigestUtils.md5DigestAsHex(oldPsw.getBytes()).equals(userService.getById(getLoginUserId(request)).getPassword())) {
            return JsonResult.error("原密码不正确");
        }
        User user = new User();
        user.setUserId(getLoginUserId(request));
        user.setPassword(DigestUtils.md5DigestAsHex(newPsw.getBytes()));
        if (userService.updateById(user)) {
            //修改四个系统给他用户密码
            User u=userService.getUserById(user.getUserId());
            othersysService.updateAllPwd(u,oldPsw,newPsw);

            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @RequiresPermissions("put:/v1/user/psw/{id}")
    @ApiOperation(value = "重置密码", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping("/psw/{id}")
    public JsonResult resetPsw(@PathVariable("id") String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        if (userService.updateById(user)) {
            return JsonResult.ok("重置密码成功");
        }
        return JsonResult.error("重置密码失败");
    }

    @RequiresPermissions("delete:/v1/user/{id}")
    @ApiOperation(value = "删除用户", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String userId) {
        User oldu=userService.getUserById(userId);
        if (userService.removeById(userId)) {
            //删除四个系统的他用户
            othersysService.deleteAllUser(oldu);
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    //@RequiresPermissions("put:/v1/user/update")
    @ApiOperation(value = "修改个人信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User", paramType = "form")
    })
    @PutMapping("/update")
    public JsonResult updateUser(User user, HttpServletRequest request) {
        User oldu=userService.getUserById(user.getUserId());
        if (userService.updateById(user)) {
            //放入缓存
            redisTemplate.opsForValue().set("user_"+user.getUserId(),user);
            user.setOldUsername(oldu.getUsername());
            //修改四个系统的他用户
            othersysService.updateAllUser(user);
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @RequiresPermissions("get:/v1/user/export")
    @ApiOperation(value = "导出用户excel", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "用户信息", required = false, dataType = "User", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @GetMapping("/export")
    public void exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户信息"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<User> list=  userService.list(new QueryWrapper<User>().orderBy(true,false,"create_time"));
            for (User u:list ) {
                u.setRoles(roleService.getRolesByUserId(u.getUserId()));
            }
            new ExportExcel("用户信息", User.class).setDataList(list).write(request,response, fileName).dispose();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "导出失败！失败信息："+e.getMessage());
        }
    }

    @RequiresPermissions("get:/v1/user/import/template")
    @ApiOperation(value = "下载用户excel模版", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @GetMapping(value = "import/template")
    public void importFileTemplate(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户数据导入模板.xlsx";
            new ExportExcel("用户数据", User.class).write(request, response, fileName).dispose();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "导入模板下载失败！失败信息:"+e.getMessage());
        }
    }


    /**
     * 导入用户数据
     * @param file
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("post:/v1/user/importdata")
    @ApiOperation(value = "导入用户excel", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping("importdata")
    public JsonResult importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<User> list = ei.getDataList(User.class);
            for (User user : list){
                try{
                    User old=userService.getByUsername(user.getUsername());
                    if(null!=old){
                        failureMsg.append("<br/>登录名 "+user.getUsername()+" 已存在; ");
                        failureNum++;
                    }else{
                        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
                        BeanValidators.validateWithException(validator, user);

                        List<Role> roleList = user.getRoles();
                        if(null!=roleList&&roleList.size()>0){
                            List<String> rolsIds=new ArrayList<String>();
                            for (int i = 0; i <roleList.size() ; i++) {
                                Role role=roleList.get(i);
                                if(null!=role){
                                    List<Role> oldroleList = roleService.list(new QueryWrapper<Role>().eq("role_name",role.getRoleName()));
                                    if(null!=oldroleList&&oldroleList.size()>0&&oldroleList.get(0)!=null){
                                        rolsIds.add(oldroleList.get(0).getRoleId());
                                    }
                                }
                            }
                            if(rolsIds.size()==roleList.size()){
                                userService.save(user);
                                for (int i = 0; i < rolsIds.size(); i++) {
                                    UserRole ur=new UserRole();
                                    ur.setUserId(user.getUserId());
                                    ur.setRoleId(rolsIds.get(i));
                                    ur.setCreateTime(new Date());
                                    userRoleService.save(ur);
                                }
                                successNum++;
                            }else{
                                failureMsg.append("<br/>登录名 "+user.getUsername()+" 的用户含有数据库中不存在角色");
                                failureNum++;
                            }
                        }else{
                            failureMsg.append("<br/>登录名 "+user.getUsername()+" 的用户角色为空");
                            failureNum++;
                        }

                    }
                }catch(ConstraintViolationException ex){
                    failureMsg.append("<br/>登录名 "+user.getUsername()+" 导入失败：");
                    List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
                    for (String message : messageList){
                        failureMsg.append(message+"; ");
                        failureNum++;
                    }
                }catch (Exception ex) {
                    failureMsg.append("<br/>登录名 "+user.getUsername()+" 导入失败："+ex.getMessage());
                }
            }
            if (failureNum>0){
                failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下：");
            }
            return JsonResult.ok("已成功导入 "+successNum+" 条用户"+failureMsg);
        } catch (Exception e) {
            return JsonResult.error("导入用户失败！失败信息："+e.getMessage());
        }
    }
}
