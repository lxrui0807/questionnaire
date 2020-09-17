package com.wf.ew.modules;

import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.system.service.UserService;
import com.yuanjing.framework.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Api(value = "手机验证码", tags = "phoneEncrypt")
@RequestMapping("/phone")
@Controller
public class PhoneEncryptController extends BaseController {

    @Autowired
    UserService userService;
    /**
     * 发送短信
     * @param phone
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "sendMsg")
    @ApiOperation(value = "给指定手机号发送短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "form"),
    })
    @PostMapping("/sendMsg")
    public JsonResult sendMsg(String phone, HttpServletRequest request) {
        if (null != phone && !StringUtils.isBlank(phone)) {
            //手机号是否存在
            Integer count = userService.checkPhone(phone);
            if(count>0){
//             boolean isreult= SendMessageUtil.sendCode(phone, request); if(isreult){ return JsonResult.ok("验证码发送成功！"); }
                HttpSession session=request.getSession();
                session.setAttribute("phone_"+phone, "1111");
                session.setMaxInactiveInterval(2*60);
                return JsonResult.ok("验证码发送成功！");
            }else{
                return JsonResult.error("该手机号未注册账号不能找回！");
            }
        }
        return JsonResult.error("验证码发送失败！");
    }

}