package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.utils.JSONUtil;
import com.wf.ew.common.utils.ReflectUtil;
import com.wf.ew.system.model.Authorities;
import com.wf.ew.system.model.RoleAuthorities;
import com.wf.ew.system.service.AuthoritiesService;
import com.wf.ew.system.service.RoleAuthoritiesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "权限管理", tags = "authorities")
@RestController
@RequestMapping("${api.version}/authorities")
public class AuthoritiesController extends BaseController {
    @Autowired
    private AuthoritiesService authoritiesService;
    @Autowired
    private RoleAuthoritiesService roleAuthoritiesService;

    @RequiresPermissions("post:/v1/authorities/sync")
    @ApiOperation(value = "同步权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json", value = "权限列表json", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping("/sync")
    public JsonResult add(String json) {
        List<Authorities> list = JSONUtil.parseArray(json, Authorities.class);
        authoritiesService.remove(null);
        authoritiesService.saveBatch(list);
        roleAuthoritiesService.deleteTrash();
        return JsonResult.ok("同步成功");
    }

    @RequiresPermissions("get:/v1/authorities")
    @ApiOperation(value = "查询所有权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<Map<String, Object>> list(Integer page, Integer limit,String roleId, String keyword) {
        if (page == null) {
            page = 0;
        }
        if (limit == null) {
            limit = 10;
        }
        Page<Authorities> authoritiesPage = new Page<>(page, limit);
        List<Map<String, Object>> maps = new ArrayList<>();
        QueryWrapper<Authorities> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("authority_name", keyword).or().like("parent_name",keyword).or().like("authority",keyword);
        }
       authoritiesService.page(authoritiesPage,wrapper.orderBy(true,true,"sort"));
        List<Authorities> authorities =authoritiesPage.getRecords();
//        // 筛选结果
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            keyword = keyword.trim();
//            Iterator<Authorities> iterator = authorities.iterator();
//            while (iterator.hasNext()) {
//                Authorities next = iterator.next();
//                boolean b = (next.getAuthorityName() != null && next.getAuthorityName().contains(keyword)) || (next.getParentName() != null && next.getParentName().contains(keyword)) || (next.getAuthority() != null && next.getAuthority().contains(keyword));
//                if (!b) {
//                    iterator.remove();
//                }
//            }
//        }
        // 回显选中状态
        List<String> roleAuths = authoritiesService.listByRoleId(roleId);
        for (Authorities one : authorities) {
            Map<String, Object> map = ReflectUtil.objectToMap(one);
            map.put("checked", 0);
            for (String roleAuth : roleAuths) {
                if (one.getAuthority().equals(roleAuth)) {
                    map.put("checked", 1);
                    break;
                }
            }
            maps.add(map);
        }
        return new PageResult<>(maps,authoritiesPage.getTotal());
    }

    @RequiresPermissions("post:/v1/authorities/role")
    @ApiOperation(value = "给角色添加权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "authId", value = "权限id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping("/role")
    public JsonResult addRoleAuth(String roleId, String authId) {
        RoleAuthorities roleAuth = new RoleAuthorities();
        roleAuth.setRoleId(roleId);
        roleAuth.setAuthority(authId);
        if (roleAuthoritiesService.save(roleAuth)) {
            return JsonResult.ok();
        }
        return JsonResult.error();
    }

    @RequiresPermissions("delete:/v1/authorities/role")
    @ApiOperation(value = "移除角色权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "authId", value = "权限id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @DeleteMapping("/role")
    public JsonResult deleteRoleAuth(String  roleId, String authId) {
        if (roleAuthoritiesService.remove(new QueryWrapper<RoleAuthorities>().eq("role_id", roleId).eq("authority", authId))) {
            return JsonResult.ok();
        }
        return JsonResult.error();
    }
}
