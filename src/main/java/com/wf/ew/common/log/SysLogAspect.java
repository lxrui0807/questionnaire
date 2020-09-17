package com.wf.ew.common.log;

import com.alibaba.fastjson.JSONObject;
import com.wf.ew.common.utils.HttpContextUtils;
import com.wf.ew.common.utils.IPUtils;
import com.wf.ew.common.utils.SystemUtils;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.system.model.LoginRecord;
import com.wf.ew.system.model.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 系统日志：切面处理类
 */
@Aspect
@Component
public class SysLogAspect {

    @Autowired
    private com.wf.ew.system.service.LoginRecordService loginRecordService;
    @Autowired
    private com.wf.ew.system.service.UserService userService;

    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation( MyLog)")
    public void logPoinCut() {
    }

    //切面 配置通知
    @AfterReturning("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        System.out.println("切面。。。。。");
        //保存日志
        LoginRecord sysLog = new LoginRecord();

        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();

        //获取操作
        MyLog myLog = method.getAnnotation(MyLog.class);
        if (myLog != null) {
            String value = myLog.value();
            sysLog.setTitle(value);//保存获取的操作
        }
        //请求的参数
        Object[] args = joinPoint.getArgs();
        Object[] arguments  = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile) {
                //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                continue;
            }
            arguments[i] = args[i];
        }
        String paramter = "";
        if (arguments != null) {
            try {
                paramter = JSONObject.toJSONString(arguments);
            } catch (Exception e) {
                paramter = arguments.toString();
            }
        }
        sysLog.setParams(paramter);

        sysLog.setCreateDate(new Date());
        //获取登录账号

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String uid =UserUtil.getLoginUserId(request);
       User cuser=userService.getUserById(uid);
        sysLog.setLoginName(cuser.getUsername());
        //设置用户名
        sysLog.setName(cuser.getNickName());
        //设置用户ip地址
        sysLog.setRemoteAddr(IPUtils.getIpAddr(request));
        //设置用户浏览器类型
        String userAgent=request.getHeader("user-agent");
        //判断字符串，Edge、Chrome、Safari、Firefox、IE浏览器或其它
        if (userAgent.contains("Edge")) {
            userAgent="Edge";
        }
        else if (userAgent.contains("Chrome")) {
            userAgent="Chrome";
        }
        else if (userAgent.contains("Safari")) {
            userAgent="Safari";
        }
        else if (userAgent.contains("Firefox")) {
            userAgent="Firefox";
        }
        else {
            userAgent="IE浏览器或其它";
        }
        sysLog.setBrowser(userAgent);
        //设置用户系统版本信息
        sysLog.setDeviceType(SystemUtils.getRequestSystemInfo(request));
        //设置当前用户信息
        sysLog.setUserId(cuser.getUserId().toString());
        //设置id
        /*sysLog.setId(IdGen.uuid());*/
        //设置请求的url值
        sysLog.setRequestUri(request.getRequestURI());
        //获取请求的类型
        sysLog.setMethod(request.getMethod());
        //调用service保存SysLog实体类到数据库
        loginRecordService.save(sysLog);
    }

}
