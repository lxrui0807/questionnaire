package com.wf.ew.common.config;

import com.wf.ew.system.model.User;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class OptionsInterceptor implements HandlerInterceptor {




    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行options请求
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header, Authorization");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("requst path " + request.getServletPath());
            Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
            if (assertion != null) {
                System.out.println("cas user ---------> " + assertion.getPrincipal().getName());
            }
            User value = (User) session.getAttribute(MyWebMvcConfigurer.SESSION_LOGIN);
            System.out.println("security session = null ---------> " + (value == null));
            if (value != null) {
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
        //return true;
    }
}
