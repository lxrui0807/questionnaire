package com.wf.ew.modules.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wf.ew.common.utils.RedisUtil;
import com.wf.ew.modules.model.PaperAnswers;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.model.PaperViews;
import com.wf.ew.modules.service.PaperAnswersService;

import com.wf.ew.common.utils.IPUtils;
import com.wf.ew.modules.service.PaperInfoService;
import com.wf.ew.modules.service.PaperRecycleService;
import com.wf.ew.modules.service.PaperViewsService;
import com.wf.ew.system.model.Area;
import com.wf.ew.system.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@EnableScheduling
@Lazy(false)
public class IPConvertorTask {
	private static boolean RUNNING = false;
    private Logger logger = LoggerFactory.getLogger("IPConvertorTask");

    @Autowired
    private PaperAnswersService paperAnswersService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private PaperInfoService paperInfoService;
    @Autowired
    private PaperRecycleService paperRecycleService;
    @Autowired
    private PaperViewsService paperViewsService;
    @Autowired
    private RedisUtil redisUtil;
	
	// 服务器启动后1分钟开始第一次上传，以后每隔30分钟转一次
    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 1800 * 1000)
   // @Scheduled(cron = "0 */1 * * * ?")
	public void start() {
		if (RUNNING) {
			logger.error("已有线程正在运行本程序，跳过本次执行");
			return;
		}
		RUNNING = true;
		List<String> ips=paperAnswersService.getDistinctUncovertedIps();
		if(ips.isEmpty()){
			RUNNING = false;
			return;
		}
		logger.info("开始将IP转换为地区，待转换数："+ips.size());
		Map<String,String> map= IPUtils.getLocationByIp(ips);
		List<PaperAnswers> paList=new ArrayList<PaperAnswers>();
		if(!map.isEmpty()){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String ip = entry.getKey();
                String location = entry.getValue();
                PaperAnswers pa=new PaperAnswers();
                pa.setIpUrl(ip);
                location=location.replace(",","");
                Map<String,String> areaMap= new HashMap<String,String>();
                if(areaMap==null){
                areaMap=areaService.getAreaMap();
                }
                String areaId=areaMap.get(location);
                if(areaId==null){
                    areaId=areaService.getIdByRemarks(location);
                    pa.setAreaId(areaId);
                    areaMap.put(location,areaId);
                }else{
                    pa.setAreaId(areaId);
                }
                paList.add(pa);
            }
            paperAnswersService.batchUpdateLocations(paList);
		}
		logger.info("将IP转换为地区结束，成功转换数："+map.size());
        logger.info("浏览量入库开始");
        List<PaperInfo> list = paperInfoService.list(new QueryWrapper<PaperInfo>().eq("del_flag","0"));
        if(null!=list&&list.size()>0){
            for (PaperInfo paper:list) {
                // 获取每一篇文章在redis中的总浏览量，存入到数据库中
                String paperId=paper.getId();
                String key="paperId_"+paperId;
                if(redisUtil.hasKey(key)){
                    String value=redisUtil.get(key);
                    Long view =0L;
                    if(null!=value&&value.length()>0){
                        view = Long.parseLong(value);
                    }
                    if(view>0){ //有浏览量才去更新回收表中的数据
                        Long oldv=paperViewsService.findViewsByPaper(paperId);
                        int state=0;
                        if(oldv!=null){
                            state=paperViewsService.updateView(paperId,view+oldv);
                        }else{
                            state=paperViewsService.insertView(paperId,view);
                        }
                        if (state>0) {
                            redisUtil.delete(key);
                        }
                    }
                }


            }
        }
        logger.info("浏览量入库结束");
		RUNNING = false;
		
	}

}
