package com.wf.ew.modules.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.javafx.binding.StringFormatter;
import com.wf.ew.common.utils.IPUtils;
import com.wf.ew.common.utils.RedisUtil;
import com.wf.ew.modules.model.AnswerDetail;
import com.wf.ew.modules.model.PaperAnswers;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.service.*;
import com.wf.ew.system.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@EnableScheduling
@Lazy(false)
public class UpdateInvaildStatusTask {
	private static boolean RUNNING = false;
    private Logger logger = LoggerFactory.getLogger("IPConvertorTask");

    @Autowired
    private PaperAnswersService paperAnswersService;
    @Autowired
    private AnswerDetailService answerDetailService;
    @Autowired
    private RedisUtil redisUtil;
	
	// 每小时执行一次
//    @Scheduled(cron = "0 0 * * * ?")
	public void start() {
		if (RUNNING) {
			logger.error("已有线程正在运行本程序，跳过本次执行");
			return;
		}
		RUNNING = true;
		//查询超时的答案信息
		List<String> ids=paperAnswersService.findOverTimeOfAnswer();
		int count=0;
        String invalidCause="";
        logger.info("开始修改答案的状态，待修改数："+ids.size());
		if(!ids.isEmpty()){
            invalidCause="答案超时";
            count=paperAnswersService.updateInvalidStatusBatch(ids, invalidCause);
        }
        logger.info("由于答案超时，修改为无效的答案数量为"+count);
		//查询同一IP提交答案数量过多
        List<String> idsByIp=paperAnswersService.findSameIpOverCount();
        if(!idsByIp.isEmpty()){
            invalidCause="同一ip地址提交答案数量超过100份";
            count=paperAnswersService.updateInvalidStatusBatch(idsByIp, invalidCause);
        }
        logger.info("由于同一ip提交答案过多，修改为无效的答案数量为"+count);
        String createTime="";
        if(redisUtil.hasKey("answer_key")){
            createTime= redisUtil.get("answer_key");
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date=sdf.format(new Date());
            redisUtil.set("answer_key",date);
        }
        List<AnswerDetail> answerDetails=answerDetailService.findTextAreaOfAnswer(createTime);
        if(!answerDetails.isEmpty()){
            Map<String,String> map = BadWordUtil2.wordMap;
            List<String> idsByMinGan=new ArrayList<String>();
            for (AnswerDetail ad:answerDetails) {
                String questionAnswer=ad.getQuestionAnswer();
                Boolean i = BadWordUtil2.isContaintBadWord(questionAnswer, 2);
                if(i){
                    idsByMinGan.add(ad.getPaperAnswerId());
                }
            }
            if(!idsByMinGan.isEmpty()){
                invalidCause="问卷答案包含敏感词汇";
                count=paperAnswersService.updateInvalidStatusBatch(idsByMinGan, invalidCause);
            }
            logger.info("由于问卷答案包含敏感词汇，修改为无效的答案数量为"+count);
        }
		RUNNING = false;
		
	}

}
