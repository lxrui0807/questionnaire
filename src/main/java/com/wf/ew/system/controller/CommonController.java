package com.wf.ew.system.controller;

import com.wf.ew.common.JsonResult;
import com.wf.ew.system.service.UserService;
import com.yuanjing.framework.common.utils.StringUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "根据手机号修改密码", notes = "忘记密码后，根据手机号修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newPsw", value = "新密码", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping("/updatePsw")
    public JsonResult updatePsw(String phone, String newPsw,String code, HttpServletRequest request) {
        String result=valiCode(phone,code,request);
        String res[]=result.split("#");
           String status= res[0];
           if("ok".equals(status) ){
               if (userService.updatePswByPhone(phone, DigestUtils.md5DigestAsHex(newPsw.getBytes()))>0) {
                   return JsonResult.ok("修改成功");
               }
               return JsonResult.error("修改失败");
           }else{
               return JsonResult.error(res[1]);
           }
    }
    /**
     * 手机号 验证码提交验证
     * @param phone
     * @param code
     * @param request
     * @return
     */
    private String valiCode(String phone,String code,HttpServletRequest request){
        if (null != phone) {
            if (null != phone && phone.length() > 0) {
                if (null != code && code.length() > 0) {
                    String sessioncode = (String) request.getSession().getAttribute("phone_" + phone);
                    if (StringUtils.isBlank(sessioncode)) {
                        return "error#"+"该手机号未发送过验证码！";
                    } else {
                        //验证手机验证码是否匹配
                        if (code.equals(sessioncode)) {
                            String ip = StringUtils.getRemoteAddr(request);
                            HttpSession session = request.getSession();
                            session.setAttribute(ip, ip);
                            session.setMaxInactiveInterval(10 * 60);
                            return "ok#";
                        } else {
                            return "error#"+"验证码错误！";
                        }
                    }
                } else {
                    return "error#"+"验证码不能为空!";
                }

            }
        }
        return "error#"+"信息不能为空!";
    }
}
