package com.wf.ew.modules.service.impl;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.utils.JSONUtil;
import com.wf.ew.common.utils.ThreadLocalDateUtil;
import com.wf.ew.modules.dao.PaperAnswersMapper;
import com.wf.ew.modules.model.*;
import com.wf.ew.modules.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AnsyTaskServiceImpl implements AnsyTaskService {

    @Autowired
    private AnswerDetailService answerDetailService;
    @Autowired
    private PaperInfoService paperInfoService;
    @Autowired
    private PaperAnswersService paperAnswersService;
    @Autowired
    private PaperLogicalService paperLogicalService;
    private Logger logger = LoggerFactory.getLogger("AnsyTaskServiceImpl");

    @Async("asyncServiceExecutor")
    @Override
    public CompletableFuture<Boolean> saveAnswer(PaperAnswers paperAnswers, String startTimeStr, String json){
        /*try {
            Thread.sleep(2000);

            logger.debug("异步方法执行了......");
            System.out.println("异步方法执行了......");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(true);*/
        boolean isResult= paperAnswersService.saveAnswer(paperAnswers,startTimeStr,json);
        return CompletableFuture.completedFuture(isResult);
    }


    /**
     * 投放页面 获取问卷信息
     * @param id
     * @return
     */
    @Async("asyncServiceExecutor")
    @Override
    public CompletableFuture<JsonResult> fillPaperView(String id){
        Map<String, Object> map =paperInfoService.viewPaper(id);
        if(null==map){
            map =new HashMap<String, Object>();
        }
        List<PaperLogical> logicalList=paperLogicalService.getList(id);
        Map<String,List<PaperLogical>> manylogicalMap=paperLogicalService.getManyList(id);
        map.put("logicalList",logicalList);
        map.put("manylogicalMap",manylogicalMap);
        return   CompletableFuture.completedFuture(JsonResult.ok().put("data",map));
    }
}
