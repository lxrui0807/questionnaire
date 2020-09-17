package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.utils.StringUtil;
import com.wf.ew.system.model.Authorities;
import com.wf.ew.system.model.Menu;
import com.wf.ew.system.model.User;
import com.wf.ew.system.service.AuthoritiesService;
import com.wf.ew.system.service.MenuService;
import com.wf.ew.system.service.UserRoleService;
import com.wf.ew.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.provider.Token;
import org.wf.jwtp.provider.TokenStore;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Api(value = "个人信息", tags = "main")
@RequestMapping("${api.version}/")
@Controller
public class MainController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private AuthoritiesService authoritiesService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    public RedisTemplate<String,Object> redisTemplate;
    //所有菜单的缓存
    public static final String CACHE_MENU_LIST = "menuList";
    /*@ApiIgnore
    @RequestMapping({"/", "/index"})
    public String index() {
        return "redirect:index.html";
    }*/

    @ResponseBody
    @ApiOperation(value = "获取个人信息")
    @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    @GetMapping("user/info")
    public JsonResult userInfo(HttpServletRequest request) {
        String uid=getLoginUserId(request);
        //先从缓存中查询
        Object object=redisTemplate.opsForValue().get("user_"+uid);
        if(object!=null){
            //缓存中存在即返回缓存里的数据
            return JsonResult.ok().put("user", object);
        }
        User user = userService.getById(uid);
        List<Authorities> auths = new ArrayList<>();
        for (String auth : getLoginToken(request).getPermissions()) {
            Authorities t = new Authorities();
            t.setAuthority(auth);
            auths.add(t);
        }
        user.setAuthorities(auths);
        //放入缓存
        redisTemplate.opsForValue().set("user_"+uid,user);
        return JsonResult.ok().put("user", user);
    }

    @ResponseBody
    @ApiOperation(value = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "账号", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping("user/login")
    public JsonResult login(String username, String password) {
        User olduser = userService.getByUsername(username);
        if (olduser == null) {
            return JsonResult.error("账号不存在");
        } else if (!olduser.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
            return JsonResult.error("密码错误");
        } else if (olduser.getState() != 0) {
            return JsonResult.error("账号被锁定");
        }
        String[] roles = arrayToString(userRoleService.getRoleIds(olduser.getUserId()));
        String[] permissions = listToArray(authoritiesService.listByUserId(olduser.getUserId()));
        Token token = tokenStore.createNewToken(String.valueOf(olduser.getUserId()), permissions, roles,30*60);
        return JsonResult.ok("登录成功").put("token", token).put("user",olduser);
    }

    @ResponseBody
    @ApiOperation(value = "获取所有菜单")
    @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    @GetMapping("user/menu")
    public JsonResult userMenu(HttpServletRequest request) {
        //先从缓存中查询
        String uid=getLoginUserId(request);
        Object object=redisTemplate.opsForValue().get(CACHE_MENU_LIST+uid);
        if(object!=null){
            //缓存中存在即返回缓存里的数据
            return JsonResult.ok().put("data", object);
        }
        // 获取当前用户的权限
        Token token = getLoginToken(request);
        String[] auths = token.getPermissions();
        // 查询所有的菜单
        List<Menu> menus = menuService.list(new QueryWrapper<Menu>().orderBy(true,true,"sort"));
        // 移除没有权限的菜单
        Iterator<Menu> iterator = menus.iterator();
        while (iterator.hasNext()) {
            Menu next = iterator.next();
            boolean haveAuth = false;
            for (String auth : auths) {
                if (StringUtil.isBlank(next.getAuthority()) || next.getAuthority().equals(auth)) {
                    haveAuth = true;
                }
            }
            if (!haveAuth) {
                iterator.remove();
            }
        }
        // 去除空的目录
        iterator = menus.iterator();
        while (iterator.hasNext()) {
            Menu next = iterator.next();
            if (StringUtil.isBlank(next.getMenuUrl())) {
                boolean haveSub = false;
                for (Menu t : menus) {
                    if (t.getParentId().equals(next.getId())) {
                        haveSub = true;
                        break;
                    }
                }
                if (!haveSub) {
                    iterator.remove();
                }
            }
        }
        List<Map<String, Object>> menuTrees=getMenuTree(menus, "");
        //查询的数据放进缓存里
        redisTemplate.opsForValue().set(CACHE_MENU_LIST+uid,menuTrees,60, TimeUnit.SECONDS);
        return JsonResult.ok().put("data", menuTrees);
    }

    // 递归转化树形菜单
    private List<Map<String, Object>> getMenuTree(List<Menu> menus, String parentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < menus.size(); i++) {
            Menu temp = menus.get(i);
            if (parentId.equals(temp.getParentId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", temp.getName());
                map.put("icon", temp.getMenuIcon());
                map.put("url", StringUtil.isBlank(temp.getMenuUrl()) ? "javascript:;" : temp.getMenuUrl());
                map.put("subMenus", getMenuTree(menus, menus.get(i).getId()));
                list.add(map);
            }
        }
        return list;
    }

    private String[] listToArray(List<String> list) {
        String[] strs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strs[i] = list.get(i);
        }
        return strs;
    }

    private String[] arrayToString(Object[] objs) {
        String[] strs = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            strs[i] = String.valueOf(objs[i]);
        }
        return strs;
    }

}
