package com.wf.ew.modules.controller;

import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.utils.RedisUtil;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.model.PaperRecycle;
import com.wf.ew.modules.model.QuestionInfo;
import com.wf.ew.modules.service.PaperInfoService;
import com.wf.ew.modules.service.PaperRecycleService;
import com.wf.ew.modules.service.QuestionInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "试卷回收", tags = "paperRecycle")
@RestController
@RequestMapping("${api.version}/paperRecycle")
public class PaperRecycleController extends BaseController {

    @Autowired
    private PaperRecycleService paperRecycleService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 查询某份问卷的总回收量、浏览量,每份问卷的平均完成时间
     */
   // @RequiresPermissions("get:/v1/paperRecycle/findRecycledSum")
    @ApiOperation(value = "查询某份问卷的总回收量、浏览量,每份问卷的平均完成时间")
    @GetMapping("/findRecycledSum")
    public JsonResult findRecycledSum(String paperId){
        PaperRecycle data = paperRecycleService.findRecycledSum(paperId);
        if(null!=data){
            String key="paperId_"+paperId;
            String value=redisUtil.get(key);
            if(null!=value&&value.length()>0){
                Long view = Long.parseLong(value);
                data.setViews(data.getViews()+view);
            }
            Double time=paperRecycleService.findAvgTime(paperId);
            data.setAvgTime(time==null?0:time);
        }
        return JsonResult.ok().put("data",data);
    }

    /**
     * 查询每一天的回收量统计图表数据
     */
    // @RequiresPermissions("get:/v1/paperRecycle/findRecycledListByDay")
    @ApiOperation(value = "查询每一天的回收量统计图表数据")
    @GetMapping("/findRecycledListByDay")
    public JsonResult findRecycledListByDay(String paperId){
        List<PaperRecycle> data= paperRecycleService.findRecycledListByDay(paperId);
        return JsonResult.ok().put("data",data);
    }
}
