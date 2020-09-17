package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.log.MyLog;
import com.wf.ew.common.utils.MapUtil;
import com.wf.ew.system.model.Area;
import com.wf.ew.system.model.Menu;
import com.wf.ew.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Api(value = "菜单管理", tags = "menu")
@RestController
@RequestMapping("${api.version}/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;


    @MyLog(value = "查询所有菜单记录")  //这里添加了AOP的自定义注解
    @RequiresPermissions("get:/v1/menu")
    @ApiOperation(value = "查询所有菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<Menu> list(String keyword) {

        List<Menu> list =  menuService.list(new QueryWrapper<Menu>().orderBy(true,true,"sort"));
        for (Menu one : list) {
            for (Menu t : list) {
                if (one.getParentId() .equals(t.getId() ) ) {
                    one.setParentName(t.getName());
                }
            }
        }
        // 筛选结果
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            Iterator<Menu> iterator = list.iterator();
            while (iterator.hasNext()) {
                Menu next = iterator.next();
                boolean b = next.getName().contains(keyword) || (next.getParentName() != null && next.getParentName().contains(keyword));
                if (!b) {
                    iterator.remove();
                }
            }
        }
        return new PageResult<>(list);
    }


    /**
     * 根据父部门pid获取子部门列表(树形)
     * @param pid
     * @return
     */
    //@RequiresPermissions("post:/v1/menu/subTree")
    @ApiOperation(value = "树形查询子菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父级菜单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("subTree")
    public JsonResult subTree(String pid,String keyword){

        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Menu> list=menuService.findByParentId(pid);
        // 筛选结果
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            Iterator<Menu> iterator = list.iterator();
            while (iterator.hasNext()) {
                Menu next = iterator.next();
                boolean b = next.getName().contains(keyword) || next.getMenuUrl().contains(keyword)|| next.getMenuIcon().contains(keyword)|| next.getAuthority().contains(keyword);
                if (!b) {
                    iterator.remove();
                }
            }
        }
        for(Menu e:list){
            Map<String, Object> map= null;
            try {
                map = MapUtil.objectToMap(e);
                mapList.add(map);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
        return JsonResult.ok().put("data", mapList);
    }


    /**
     * 根据父菜单pid获取子菜单列表(级联选择)
     * @param pid
     * @return
     */
    @RequiresPermissions("post:/v1/menu/subSel")
    @PostMapping("subSel")
    @ApiOperation(value = "级联选择查询子菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父菜单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    public JsonResult subSel(String pid){
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Menu> list = menuService.findByParentId(pid);
        for(Menu e:list){
            Map<String, Object> map = Maps.newHashMap();
            //  菜单id
            map.put("value", e.getId());
            //  菜单名称
            map.put("label", e.getName());
            //  是否存在子菜单
            map.put("haveChildren", e.getHaveChild());
            mapList.add(map);
        }
        return JsonResult.ok().put("data", mapList);
    }


    @RequiresPermissions("post:/v1/menu")
    @ApiOperation(value = "添加菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menu", value = "菜单信息", required = true, dataType = "Menu", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult add(Menu menu) {
        System.out.println(menu);
        if(null!=menu && null!=menu.getParentIds() && menu.getParentIds().length()>0){
            String pids = menu.getParentIds();
            String[] ids=pids.split(",");
            menu.setParentId(ids[ids.length-1]);
        }
        if (menuService.save(menu)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }


    @RequiresPermissions("put:/v1/menu")
    @ApiOperation(value = "修改菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menu", value = "菜单信息", required = true, dataType = "Menu", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping()
    public JsonResult update(Menu menu) {
        if(null!=menu && null!=menu.getParentIds() && menu.getParentIds().length()>0){
            String pids = menu.getParentIds();
            String[] ids=pids.split(",");
            menu.setParentId(ids[ids.length-1]);
        }
        if (menuService.updateById(menu)) {
            return JsonResult.ok("修改成功！");
        }
        return JsonResult.error("修改失败！");
    }


    @RequiresPermissions("delete:/v1/menu/{id}")
    @ApiOperation(value = "删除菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String id) {
        if (menuService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }
}
