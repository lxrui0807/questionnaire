package com.wf.ew.common.paperview;

import com.wf.ew.common.utils.HttpContextUtils;
import com.wf.ew.common.utils.IPUtils;
import com.wf.ew.common.utils.RedisUtil;
import com.wf.ew.modules.service.PaperViewsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
public class PapeViewAspect {

    private Logger logger = LoggerFactory.getLogger("PageViewAspect");

    @Autowired
    private RedisUtil redisUtil;



    /**
     * 切入点
     */
    @Pointcut("@annotation(com.wf.ew.common.paperview.PaperView)")
    public void PageViewAspect() {

    }

    /**
     * 切入处理
     * @param joinPoint
     * @return
     */
    @Around("PageViewAspect()")
    public  Object around(ProceedingJoinPoint joinPoint) {
        Object[] object = joinPoint.getArgs();
        Object paperId = object[0];
        //String ipAddr=null;

        logger.info("paperId:{}", paperId);
        Object obj = null;
        try {
           /* if(object.length>1){
                ipAddr = object[1].toString();
            }else{
                HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
                ipAddr = IPUtils.getIpAddr(request);
            }
            logger.info("ipAddr:{}", ipAddr);
            String key="paperId_"+paperId+"_ip_"+ipAddr;//paperId_1_ip_192.168.0.12
            // 浏览量存入redis中
            boolean isHas=redisUtil.hasKey(key);
            if(isHas){
                logger.info("该ip:{},访问的浏览量已经新增过了", ipAddr);
            }else{
                redisUtil.set(key,"1");
            }*/
            String key="paperId_"+paperId;
            boolean isHas=redisUtil.hasKey(key);
            long count=1;
            if(isHas){
                String oldv=redisUtil.get(key);
                count= count+Long.parseLong(oldv);
            }
            redisUtil.set(key,count+"");
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }
}
