package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.log.MyLog;
import com.wf.ew.common.utils.MapUtil;
import com.wf.ew.system.model.Office;
import com.wf.ew.system.service.AreaService;
import com.wf.ew.system.service.OfficeService;
import com.yuanjing.framework.common.utils.DateUtils;
import com.yuanjing.framework.common.utils.IdGen;
import com.yuanjing.framework.common.utils.excel.ExportExcel;
import com.yuanjing.framework.common.utils.excel.ImportExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.wf.jwtp.annotation.RequiresPermissions;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import java.util.*;

@Api(value = "部门管理", tags = "office")
@RestController
@RequestMapping("${api.version}/office")
public class OfficeController {
    @Autowired
    private OfficeService officeService;

    @Autowired
    private AreaService areaService;

    /**
     * 验证Bean实例对象
     */
    @Autowired
    protected Validator validator;

    @MyLog(value = "查询所有部门记录")  //这里添加了AOP的自定义注解
    @RequiresPermissions("get:/v1/office")
    @ApiOperation(value = "查询所有部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<Office> list(String keyword) {

        List<Office> list =  officeService.list(new QueryWrapper<Office>().orderBy(true,true,"sort"));
        // 筛选结果
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            Iterator<Office> iterator = list.iterator();
            while (iterator.hasNext()) {
                Office next = iterator.next();
                boolean b = next.getName().contains(keyword);
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
    @RequiresPermissions("post:/v1/office/subTree")
    @ApiOperation(value = "树形查询子部门列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父部门id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("subTree")
    public JsonResult subTree(String pid){
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list=officeService.findByParentId(pid);
        for(Office e:list){
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
     * 根据父部门pid获取子部门列表(级联选择)
     * @param pid
     * @return
     */
    @RequiresPermissions("post:/v1/office/subSel")
    @PostMapping("subSel")
    @ApiOperation(value = "级联选择查询子部门列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父部门id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    public JsonResult subSel(String pid){
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list=officeService.findByParentId(pid);
        for(Office e:list){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", e.getId());
            map.put("label", e.getName());
            map.put("haveChildren", e.getHaveChild());//sort实际上放的是子节点数目
            mapList.add(map);
        }
        return JsonResult.ok().put("data", mapList);
    }



    @RequiresPermissions("post:/v1/office")
    @ApiOperation(value = "添加部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "office", value = "部门信息", required = true, dataType = "Office", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult add(Office office) {
        if(null!=office&&null!=office.getParentIds()&&office.getParentIds().length()>0){
            String pids=office.getParentIds();
            String[] ids=pids.split(",");
            office.setParentId(ids[ids.length-1]);
        }
        if (officeService.save(office)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @RequiresPermissions("put:/v1/office")
    @ApiOperation(value = "修改部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "office", value = "部门信息", required = true, dataType = "Office", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping()
    public JsonResult update(Office office) {
        if(null!=office&&null!=office.getParentIds()&&office.getParentIds().length()>0){
            String pids=office.getParentIds();
            String[] ids=pids.split(",");
            office.setParentId(ids[ids.length-1]);
        }
        if (officeService.updateById(office)) {
            return JsonResult.ok("修改成功！");
        }
        return JsonResult.error("修改失败！");
    }

    @RequiresPermissions("delete:/v1/office/{id}")
    @ApiOperation(value = "删除部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "部门id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String id) {
        if (officeService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }


    /**
     * 根据id获取部门信息
     * @param id
     * @return
     */
    @RequiresPermissions("post:/v1/office/{id}")
    @MyLog(value = "根据id获取部门信息")  //这里添加了AOP的自定义注解
    @ApiOperation(value = "根据id获取部门信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "部门id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/{id}")
    public JsonResult getById(@PathVariable("id") String id) {
        Office office= officeService.getById(id);
        return JsonResult.ok().put("office", office);
    }

    /**
     * 导出excel
     * @param office
     * @param request
     * @param response
     * @param redirectAttributes
     */
    @RequiresPermissions("get:/v1/office/export")
    @ApiOperation(value = "导出部门excel", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "office", value = "部门信息", required = false, dataType = "Office", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @GetMapping("/export")
    public void exportFile(Office office, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            //  文件名
            String fileName = "部门信息"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            //  查询到的数据
            List<Office> list = officeService.list(new QueryWrapper<Office>().orderBy(true,true ,"PARENT_ID","SORT"));
            for (Office o:list){
                //  归属区域（数字串转文字）
                String areaId = o.getAreaId();
                String remarks = areaService.getRemarksById(areaId);
                o.setAreaId(remarks);
                //  上级部门名称（数字串转文字）
                String parentId = o.getParentId();
                String fatherName = officeService.getFatherNameByParentId(parentId);
                o.setParentId(fatherName);
            }
            //  导出
            new ExportExcel("部门信息", Office.class).setDataList(list).write(request,response, fileName).dispose();
        } catch (Exception e) {
            //  出错提示
            redirectAttributes.addFlashAttribute("message", "导出失败！失败信息："+e.getMessage());
        }
    }

    /**
     * 导入模板
     * @param request
     * @param response
     * @param redirectAttributes
     */
    @RequiresPermissions("get:/v1/office/import/template")
    @ApiOperation(value = "下载部门excel模版", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @GetMapping(value = "import/template")
    public void importFileTemplate(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "部门数据导入模板.xlsx";
            new ExportExcel("部门数据", Office.class).write(request, response, fileName).dispose();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "导入模板下载失败！失败信息:"+e.getMessage());
        }
    }

    /**
     * 导入部门数据
     * @param file
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("post:/v1/office/importdata")
    @ApiOperation(value = "导入部门excel", notes = "")
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
            List<Office> list = ei.getDataList(Office.class);

            List topOfficeList = new ArrayList();
            List subOfficeList = new ArrayList();
            Map<String,String> map= new HashMap<>();
            //  PARENT_IDS
            StringBuffer stringBuffer = new  StringBuffer();

            for (Office office: list){
                //  部门名称
                String name = office.getName();

                //  上级部门名称  (文字)
                String fatherName = office.getParentId();

                //  归属区域    (文字转数字串)
                String remarks = office.getAreaId();
                String areaId = areaService.getIdByRemarks(remarks);
                office.setAreaId(areaId);

                if(fatherName == null || fatherName.equals("")){
                    //  顶级部门名称不允许重复
                    Integer integer = officeService.checkTopOffice(name);
                    if(integer == 0){
                       String id =IdGen.uuid();
                       //   设置顶级部门的id
                       office.setId(id);
                       topOfficeList.add(office);
                       //   放入缓存    例如：zz公司，zz公司的id
                        map.put(name,id);
                        successNum++;
                    }else {
                        failureNum++;
                    }
                }else {
                    //  通过上级部门名称，获取上级部门id
                    String id = map.get(office.getParentId());
                    if(id!=null && !"".equals(id)){
                        //  上级部门id
                        office.setParentId(id);
                        //  本部门id
                        String subId=IdGen.uuid();
                        office.setId(subId);

                        //  所有父级编号
                        if(!stringBuffer.toString().contains(id)){
                            //  不包含进行拼接
                            stringBuffer.append(id);
                            stringBuffer.append(",");
                            office.setParentIds(stringBuffer.substring(0, stringBuffer.length()-1));
                        }else {
                            //  包含，直接用
                            office.setParentIds(stringBuffer.substring(0, stringBuffer.length()-1));
                        }


                        subOfficeList.add(office);

                        //  放入缓存    例如：开发部，开发部的id
                        map.put(name,subId);
                        successNum++;
                    }else{
                        //  获取顶级部门名称
                        String companyName = office.getFax();
                        if(companyName!=null && !companyName.equals("")){

                            //  获取顶级部门名称的Id
                            String companyId = officeService.getCompanyIdByCompanyName(companyName);

                            if(companyId!=null && !companyId.equals("")){

                                // 定位(开发一部) parentId:即开发一部的Id
                                Office of =officeService.getLocation(companyId, fatherName);
                                String parentId = of.getId();
                                if(parentId!=null && !"".equals(parentId)){
                                    office.setParentId(parentId);
                                    //  所有父级编号
                                    String parentIds=of.getParentIds()+","+parentId;
                                    office.setParentIds(parentIds);

                                    //  顶级部门下，部门名称不允许重复
                                    Integer integer = officeService.checkSubOffice(parentId, name);
                                    if(integer == 0){
                                        subOfficeList.add(office);
                                        map.put(name,id);
                                        successNum++;
                                    }else {
                                        failureNum++;
                                    }
                                }
                            }else {
                                //  数据库，该公司不存在
                                failureNum++;
                            }
                        }else {
                            //  顶级部门名称不存在
                            failureNum++;
                        }
                    }
                }
            }
            officeService.saveBatch(topOfficeList);
            officeService.saveBatch(subOfficeList);
            if (failureNum>0){
                failureMsg.insert(0, "，失败 "+failureNum+" 条部门，导入信息如下：");
            }
            return JsonResult.ok("已成功导入 "+successNum+" 条部门"+failureMsg);
        } catch (Exception e) {
            return JsonResult.error("导入部门失败！失败信息："+e.getMessage());
        }
    }
}
