package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.log.MyLog;
import com.wf.ew.common.utils.MapUtil;
import com.wf.ew.system.model.Area;
import com.wf.ew.system.service.AreaService;
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

@Api(value = "区域管理", tags = "area")
@RestController
@RequestMapping("${api.version}/area")
public class AreaController {
    @Autowired
    private AreaService areaService;


    @MyLog(value = "查询所有区域记录")  //这里添加了AOP的自定义注解
    @RequiresPermissions("get:/v1/area")
    @ApiOperation(value = "查询所有区域分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<Area> list(String keyword) {

        List<Area> list =  areaService.list(new QueryWrapper<Area>().orderBy(true,true,"sort"));
        // 筛选结果
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            Iterator<Area> iterator = list.iterator();
            while (iterator.hasNext()) {
                Area next = iterator.next();
                boolean b = next.getRemarks().contains(keyword);
                if (!b) {
                    iterator.remove();
                }
            }
        }
        return new PageResult<>(list);
    }

    /**
     * 根据父菜单pid获取子菜单列表(树形)
     * @param pid
     * @return
     */
    @RequiresPermissions("post:/v1/area/subTree")
    @PostMapping("subTree")
    @ApiOperation(value = "树形查询子菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父菜单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    public JsonResult subTree(String pid){
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Area> list=areaService.findByParentId(pid);
        for(Area e:list){
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
    @RequiresPermissions("post:/v1/area/subSel")
    @PostMapping("subSel")
    @ApiOperation(value = "级联选择查询子菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父菜单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    public JsonResult subSel(String pid){
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Area> list=areaService.findByParentId(pid);
        for(Area e:list){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", e.getId());
            map.put("label", e.getName());
            map.put("haveChildren", e.getHaveChild());//sort实际上放的是子节点数目
            mapList.add(map);
        }
        return JsonResult.ok().put("data", mapList);
    }

    @RequiresPermissions("post:/v1/area")
    @ApiOperation(value = "添加区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "area", value = "区域信息", required = true, dataType = "Area", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult add(Area area) {
        if(null!=area&&null!=area.getParentIds()&&area.getParentIds().length()>0){
            String pids=area.getParentIds();
            String[] ids=pids.split(",");
            area.setParentId(ids[ids.length-1]);
        }
        if (areaService.save(area)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @RequiresPermissions("put:/v1/area")
    @ApiOperation(value = "修改区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "area", value = "区域信息", required = true, dataType = "Area", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping()
    public JsonResult update(Area area) {
        if(null!=area&&null!=area.getParentIds()&&area.getParentIds().length()>0){
            String pids=area.getParentIds();
            String[] ids=pids.split(",");
            area.setParentId(ids[ids.length-1]);
        }
        if (areaService.updateById(area)) {
            return JsonResult.ok("修改成功！");
        }
        return JsonResult.error("修改失败！");
    }

    @RequiresPermissions("delete:/v1/area/{id}")
    @ApiOperation(value = "删除区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "区域id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String id) {
        if (areaService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    /**
     * 根据id获取部门信息
     * @param id
     * @return
     */
    @RequiresPermissions("post:/v1/area/{id}")
    @MyLog(value = "根据id获取区域信息")  //这里添加了AOP的自定义注解
    @ApiOperation(value = "根据id获取区域信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "区域id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/{id}")
    public JsonResult getById(@PathVariable("id") String id) {
        Area area= areaService.getById(id);
        return JsonResult.ok().put("area", area);
    }

}
