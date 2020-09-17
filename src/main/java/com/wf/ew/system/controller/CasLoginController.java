package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.config.MyWebMvcConfigurer;
import com.wf.ew.common.utils.StringUtil;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.system.model.Authorities;
import com.wf.ew.system.model.Menu;
import com.wf.ew.system.model.User;
import com.wf.ew.system.service.AuthoritiesService;
import com.wf.ew.system.service.MenuService;
import com.wf.ew.system.service.UserRoleService;
import com.wf.ew.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.provider.Token;
import org.wf.jwtp.provider.TokenStore;
import org.wf.jwtp.util.SubjectUtil;
import org.wf.jwtp.util.TokenUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@Controller
public class CasLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthoritiesService authoritiesService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private TokenStore tokenStore;

    @Value("${casClientLogoutUrl}")
    private   String casClientLogoutUrl;

    @Value("${casClientLoginUrl}")
    private   String casClientLoginUrl;


    @RequestMapping(value = "/casLogin", method = RequestMethod.GET)
    public String  caslogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "redirect:"+casClientLoginUrl;
    }

    @ResponseBody
    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    public JsonResult getToken(HttpServletRequest request){
        HttpSession session = request.getSession();
        Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        if (assertion != null) {
            //获取登录用户名
            String username = assertion.getPrincipal().getName();
            System.out.println("user ---------> " + username);
            User temp = userService.getByUsername(username);
            System.out.println("TEMP user ---------> " + (temp.getUsername()));
            if (temp != null) {
//                session.setAttribute(WebSecurityConfig.SESSION_LOGIN, temp);
                session.setAttribute(MyWebMvcConfigurer.SESSION_LOGIN, temp);
                String jsessionid = session.getId();
                String uid = temp.getUserId();
                String[] roles = arrayToString(userRoleService.getRoleIds(uid));
                String[] permissions = listToArray(authoritiesService.listByUserId(uid));
                System.out.println("jsessionid ------> " + jsessionid);
                Token token = tokenStore.createNewToken(String.valueOf(uid), permissions, roles, 30 * 60);
                return JsonResult.ok("登录成功").put("token", token).put("user", temp);
            }
        }
      return null;
    }


    @RequestMapping(value ="/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.invalidate();
        //session.removeAttribute(MyWebMvcConfigurer.SESSION_LOGIN);
        // 使用cas退出成功后,跳转到登录界面
        return "redirect:"+casClientLogoutUrl;
    }




    private String[] listToArray(List<String> list) {
        String[] strs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strs[i] = list.get(i);
        }
        return strs;
    }

    private String[] arrayToString(Object[] objs) {
        String[] strs = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            strs[i] = String.valueOf(objs[i]);
        }
        return strs;
    }
}
