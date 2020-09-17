package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.system.model.Dict;
import com.wf.ew.system.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.List;

@Api(value = "字典管理",tags = "dict")
@RestController
@RequestMapping("${api.version}/dict")
public class DictController {

    @Autowired
    private DictService dictService;

   @RequiresPermissions("get:/v1/dict")
    @ApiOperation(value = "查询所有字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "描述", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sys", value = "字典类型标识", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页多少条", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<Dict> list(Integer page, Integer limit,String type,String description,String sys) {

        if (page == null) {
            page = 0;
        }
        if (limit == null) {
            limit = 10;
        }
        Page<Dict> dictPage = new Page<>(page, limit);
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();

        //  按类型进行查询
        if (type != null && !type.trim().isEmpty()) {
            wrapper.like("TYPE",type);
        }
        //  按描述进行查询
        if ( description != null && !description.trim().isEmpty()) {
            wrapper.like("DESCRIPTION",description);
        }
        //  按描述进行查询
        if ( sys != null && !sys.trim().isEmpty()) {
            wrapper.eq("SYS",sys);
        }
        //  指定排序规则
        wrapper.orderBy(true,true,"description","sort");
        dictService.page(dictPage,wrapper);

        List<Dict> dictList = dictPage.getRecords();
        return new PageResult<>(dictList,dictPage.getTotal());
    }

    /**
     *根据类型获取字典列表
     * @param type
     * @return
     */
    @ApiOperation(value = "根据类型获取字典列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("list/{type}")
    public JsonResult dictlistByType(@PathVariable("type") String type) {
        List<Dict> dictList =dictService.list(new QueryWrapper<Dict>().eq("type", type));
        return JsonResult.ok().put("dictList", dictList);
    }

    @RequiresPermissions("post:/v1/dict")
    @ApiOperation(value = "添加字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dict", value = "字典信息", required = true, dataType = "Dict", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult add(Dict dict) {
        if(dictService.save(dict)){
            return JsonResult.ok("添加成功！");
        }
        return JsonResult.error("添加失败！");
    }


    @RequiresPermissions("put:/v1/dict")
    @ApiOperation(value = "修改字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dict", value = "字典信息", required = true, dataType = "Dict", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping()
    public JsonResult update(Dict dict) {
        if(dictService.updateById(dict)){
            return JsonResult.ok("修改成功！");
        }
        return JsonResult.error("修改失败！");
    }


    @RequiresPermissions("delete:/v1/dict/{id}")
    @ApiOperation(value = "删除字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String id) {
        if(dictService.removeById(id)){
            return JsonResult.ok("删除成功");
        }
        return JsonResult.ok("删除失败");
    }
}
