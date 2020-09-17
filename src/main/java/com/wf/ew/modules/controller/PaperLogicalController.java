package com.wf.ew.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.log.MyLog;
import com.wf.ew.common.utils.JSONUtil;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.modules.model.PaperLogical;
import com.wf.ew.modules.model.QuestionInfo;
import com.wf.ew.modules.service.PaperLogicalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.Date;
import java.util.List;

@Api(value = "试卷问题逻辑跳转管理", tags = "paperLogical")
@RestController
@RequestMapping("${api.version}/paperLogical")
public class PaperLogicalController {
    @Autowired
    private PaperLogicalService paperLogicalService;


    /**
     * 查询某个试卷的逻辑跳转列表
     * @param paperId
     * @return
     */
    //@RequiresPermissions("get:/v1/paperLogical/getList")
    @ApiOperation(value = "查询某个试卷的逻辑跳转列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperId", value = "试卷ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "Authorization", value = "令牌", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("getList")
    public JsonResult getList(String paperId) {
        List<PaperLogical> list =  paperLogicalService.getList(paperId);
        return JsonResult.ok().put("data", list);
    }

    /**
     *
     * @param paperId
     * @return
     */
    @ApiOperation(value = "根据问卷的ID查询该问卷最大排序号")
    @GetMapping("/getMaxsortByPaper")
    public JsonResult getListBysort(String paperId){
        Integer sort=paperLogicalService.findMaxSortByPaperId(paperId);
        return JsonResult.ok().put("data",sort==null?1:(sort+1));
    }


    /**
     * 试卷逻辑跳转保存
     * @param json
     * @param paperId
     * @return
     */
    //@RequiresPermissions("post:/v1/paperLogical/save")
    @PostMapping("save")
    @ApiOperation(value = "试卷逻辑跳转保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperId", value = "试卷ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "questionId", value = "问题ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "json", value = "逻辑跳转列表json", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "Authorization", value = "令牌", required = true, dataType = "String", paramType = "header")
    })
    public JsonResult save(String json, String paperId, String questionId, HttpServletRequest request){
        String loginUserId = UserUtil.getLoginUserId(request);
        if(null!=json){
            List<PaperLogical> list = JSONUtil.parseArray(json, PaperLogical.class);
            if(null!=list&&list.size()>0){
                paperLogicalService.remove(new QueryWrapper<PaperLogical>().eq("paper_id",paperId)
                        .eq("question_id",questionId).eq("skip","是").ne("is_many_skip","是"));
                for (PaperLogical pl:list) {
                    pl.setCreateTime(new Date());
                    pl.setCreateBy(loginUserId);
                    pl.setIsManySkip("否");
                }
                paperLogicalService.saveBatch(list);
                return JsonResult.ok("保存成功");
            }
        }
        return JsonResult.error("保存失败");
    }


    /**
     * 试卷多题逻辑跳转保存
     * @param json
     * @param paperId
     * @return
     */
    //@RequiresPermissions("post:/v1/paperLogical/manysave")
    @PostMapping("manysave")
    @ApiOperation(value = "试卷多题逻辑跳转保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperId", value = "试卷ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "questionId", value = "问题ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "json", value = "逻辑跳转列表json", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "Authorization", value = "令牌", required = true, dataType = "String", paramType = "header")
    })
    public JsonResult manysave(String json, String paperId, String questionId, HttpServletRequest request){
        String loginUserId = UserUtil.getLoginUserId(request);
        if(null!=json&&json.length()>0){
            List<PaperLogical> list = JSONUtil.parseArray(json, PaperLogical.class);
            if(null!=list&&list.size()>0){
                paperLogicalService.remove(new QueryWrapper<PaperLogical>().eq("paper_id",paperId).eq("skip_question_id",questionId).eq("skip","是").eq("is_many_skip","是"));
                for (PaperLogical pl:list) {
                    pl.setCreateTime(new Date());
                    pl.setCreateBy(loginUserId);
                    pl.setIsManySkip("是");
                }
                paperLogicalService.saveBatch(list);
                return JsonResult.ok("保存成功");
            }
        }else{
            paperLogicalService.remove(new QueryWrapper<PaperLogical>().eq("paper_id",paperId).eq("skip_question_id",questionId).eq("skip","是").eq("is_many_skip","是"));
            return JsonResult.ok("保存成功");
        }
        return JsonResult.error("保存失败");
    }



}
