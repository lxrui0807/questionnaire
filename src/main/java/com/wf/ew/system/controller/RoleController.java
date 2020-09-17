package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.Iterator;
import java.util.List;

@Api(value = "角色管理", tags = "role")
@RestController
@RequestMapping("${api.version}/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @RequiresPermissions("get:/v1/role")
    @ApiOperation(value = "查询所有角色分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<Role> list(Integer page, Integer limit, String keyword) {
        if (page == null) {
            page = 0;
        }
        if (limit == null) {
            limit = 10;
        }
        Page<Role> rolePage = new Page<>(page, limit);

        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("role_name", keyword).or().like("comments",keyword);
        }
        roleService.page(rolePage,wrapper.orderBy(true,true,"create_time"));
        List<Role> list =rolePage.getRecords();
        // 筛选结果
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            keyword = keyword.trim();
//            Iterator<Role> iterator = list.iterator();
//            while (iterator.hasNext()) {
//                Role next = iterator.next();
//                boolean b = next.getRoleName().contains(keyword) || next.getComments().contains(keyword);
//                if (!b) {
//                    iterator.remove();
//                }
//            }
//        }
        return new PageResult<>(list,rolePage.getTotal());
    }

    @RequiresPermissions("post:/v1/role")
    @ApiOperation(value = "添加角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "Role", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult add(Role role) {
        if (roleService.save(role)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @RequiresPermissions("put:/v1/role")
    @ApiOperation(value = "修改角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "Role", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping()
    public JsonResult update(Role role) {
        if (roleService.updateById(role)) {
            return JsonResult.ok("修改成功！");
        }
        return JsonResult.error("修改失败！");
    }

    @RequiresPermissions("delete:/v1/role/{id}")
    @ApiOperation(value = "删除角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String roleId) {
        if (roleService.removeById(roleId)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    @RequiresPermissions("get:/v1/role/list")
    @ApiOperation(value = "查询所有角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult getAlllist(String keyword) {
        List<Role> list =roleService.list(new QueryWrapper<Role>().orderBy(true,true,"create_time"));
        // 筛选结果
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            Iterator<Role> iterator = list.iterator();
            while (iterator.hasNext()) {
                Role next = iterator.next();
                boolean b = next.getRoleName().contains(keyword) || next.getComments().contains(keyword);
                if (!b) {
                    iterator.remove();
                }
            }
        }
        return JsonResult.ok().put("rolelist", list);
    }
}
