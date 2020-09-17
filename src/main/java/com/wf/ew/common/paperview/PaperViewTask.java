package com.wf.ew.common.paperview;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wf.ew.common.utils.IPUtils;
import com.wf.ew.common.utils.RedisUtil;
import com.wf.ew.modules.model.PaperAnswers;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.model.PaperRecycle;
import com.wf.ew.modules.service.PaperAnswersService;
import com.wf.ew.modules.service.PaperInfoService;
import com.wf.ew.modules.service.PaperRecycleService;
import com.wf.ew.system.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@EnableScheduling
@Lazy(false)
public class PaperViewTask {
    private static boolean RUNNING = false;
    private Logger logger = LoggerFactory.getLogger("PaperViewTask");

    @Autowired
    private PaperAnswersService paperAnswersService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PaperInfoService paperInfoService;
    @Autowired
    private PaperRecycleService paperRecycleService;

    //每天凌晨一点(暂时不用此类)
    //@Scheduled(cron = "0 0 1 * * ? ")
   //@Scheduled(cron = "0 */2 * * * ?")
    public void start() {
        if (RUNNING) {
            logger.error("已有线程正在运行本程序，跳过本次执行");
            return;
        }
        RUNNING = true;
        logger.info("浏览量入库开始");
        List<PaperInfo> list = paperInfoService.list(new QueryWrapper<PaperInfo>().eq("del_flag","0"));
        for (PaperInfo paper:list) {
            // 获取每一篇文章在redis中的浏览量，存入到数据库中
            String paperId=paper.getId();
            /*String key  = "paperId_"+paperId;
            Long view =  redisUtil.sizeHyper(key);*/
            String mactchkey="paperId_"+paperId+"_ip_*";
            Set<String> keys =redisUtil.keys(mactchkey);
            Long view = Long.valueOf(keys.size());
            if(view>0){
                boolean issuc=false;
                int recycled=paperAnswersService.count(new QueryWrapper<PaperAnswers>().eq("paper_id",paperId).eq("del_flag","0"));
              /*  PaperRecycle pr=new PaperRecycle();
                pr.setId(IdWorker.getIdStr());
                pr.setViews(view);
                pr.setRecycled((long) recycled);
                pr.setPaperId(paperId);
                pr.setRecycleTime(new Date());*/
                //int state=paperRecycleService.insertPr(IdWorker.getIdStr(),paperId,new Date(),(long) recycled,view);
                //issuc=state>0;
                if (issuc) {
                    redisUtil.delete(keys);
                    //redisUtil.delete(key);
                }
            }
        }
        logger.info("浏览量入库结束");
        RUNNING = false;

    }

}
