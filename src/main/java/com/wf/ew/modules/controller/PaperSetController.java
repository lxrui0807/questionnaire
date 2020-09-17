package com.wf.ew.modules.controller;

import com.wf.ew.common.JsonResult;
import com.wf.ew.modules.model.PaperSet;
import com.wf.ew.modules.service.PaperSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wf.jwtp.annotation.RequiresPermissions;

@Api(value = "试卷设置管理",tags = "paperSet")
@RestController
@RequestMapping("${api.version}/paperSet")
public class PaperSetController {

    @Autowired
    private PaperSetService paperSetService;

    /**
     * 添加设置
     * @param paperSet
     * @return
     */
    @RequiresPermissions("post:/v1/paperSet")
    @ApiOperation(value = "添加设置")
    @PostMapping()
    public JsonResult add(PaperSet paperSet){
        if(paperSetService.save(paperSet)){
            return JsonResult.ok("添加设置成功");
        }else {
            return JsonResult.error("添加设置失败");
        }
    }

    /**
     * 修改设置
     * @param paperSet
     * @return
     */
    @RequiresPermissions("put:/v1/paperSet")
    @ApiOperation(value = "修改设置")
    @PutMapping()
    public JsonResult update(PaperSet paperSet){
        if(paperSetService.updateById(paperSet)){
            return JsonResult.ok("修改设置成功");
        }else {
            return JsonResult.error("修改设置失败");
        }
    }

}
