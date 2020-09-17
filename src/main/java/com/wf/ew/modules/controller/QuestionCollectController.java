package com.wf.ew.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.log.MyLog;
import com.wf.ew.common.utils.JSONUtil;
import com.wf.ew.modules.model.PaperLogical;
import com.wf.ew.modules.model.QuestionCollect;
import com.wf.ew.modules.service.PaperLogicalService;
import com.wf.ew.modules.service.QuestionCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.Date;
import java.util.List;

@Api(value = "问题收藏管理", tags = "questionCollect")
@RestController
@RequestMapping("${api.version}/questionCollect")
public class QuestionCollectController {
    @Autowired
    private QuestionCollectService questionCollectService;

    /**
     * 查询某个用户ID的问题收藏列表
     * @param userId
     * @return
     */
    @MyLog(value = "查询某个用户ID的问题收藏列表")
    @RequiresPermissions("get:/v1/questionCollect/getList")
    @ApiOperation(value = "查询某个用户ID的问题收藏列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "Authorization", value = "令牌", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("getList")
    public JsonResult getList(String userId) {
        List<QuestionCollect> list =  questionCollectService.list(new QueryWrapper<QuestionCollect>().eq("user_id",userId)
                                            .orderBy(true,false,"collection_time"));

        return JsonResult.ok().put("data", list);
    }


    /**
     * 收藏试卷问题
     * @param questionCollect
     * @return
     */
    //@RequiresPermissions("post:/v1/questionCollect/save")
    @PostMapping("save")
    @ApiOperation(value = "收藏试卷问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionCollect", value = "试卷收藏信息", required = true, dataType = "QuestionCollect", paramType = "form"),
            @ApiImplicitParam(name = "Authorization", value = "令牌", required = true, dataType = "String", paramType = "header")
    })
    public JsonResult save(QuestionCollect questionCollect){
        if(null!=questionCollect){
            questionCollect.setCollectionTime(new Date());
            boolean isSuccess=questionCollectService.save(questionCollect);
            if(isSuccess){
                return JsonResult.ok("保存成功");
            }
        }
        return JsonResult.error("保存失败");
    }


    /**
     * 取消收藏试卷问题
     * @param id
     * @return
     */
    //@RequiresPermissions("post:/v1/questionCollect/cancel")
    @PostMapping("cancel")
    @ApiOperation(value = "取消收藏试卷问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "收藏ID", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "Authorization", value = "令牌", required = true, dataType = "String", paramType = "header")
    })
    public JsonResult cancel(String id){
        if(null!=id){
            boolean isSuccess=questionCollectService.removeById(id);
            if(isSuccess){
                return JsonResult.ok();
            }
        }
        return JsonResult.error();
    }


}
