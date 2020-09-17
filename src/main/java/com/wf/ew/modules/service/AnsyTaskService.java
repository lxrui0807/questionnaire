package com.wf.ew.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.common.JsonResult;
import com.wf.ew.modules.model.PaperAnswers;
import com.wf.ew.modules.model.PaperViews;

import java.util.concurrent.CompletableFuture;


/**
 * 问卷浏览信息表，Service接口
 */

public interface AnsyTaskService{
    /**
     * 回答问卷
     * @param paperAnswers
     * @param startTimeStr
     * @param json
     * @return
     */
    public CompletableFuture<Boolean> saveAnswer(PaperAnswers paperAnswers, String startTimeStr, String json);

    /**
     * 投放页面 获取问卷信息
     * @param id
     * @return
     */
    public CompletableFuture<JsonResult> fillPaperView(String id);
}
