package com.wf.ew.common.utils;


import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.baidubce.services.sms.model.SendMessageV2Request;
import com.baidubce.services.sms.model.SendMessageV2Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 导出工具类(暂时不用)
 */
public class SendMessageUtil{

	private static Log log = LogFactory.getLog(SendMessageUtil.class);

	private static final String  endPoint = "http://sms.bj.baidubce.com"; // SMS服务域名
	private static final String accessKeyId = "ab2e11948a184a86a2ebfe89575300da"; //发送账号安全认证的Access Key ID
	private static final String secretAccessKy = "06d9b3b7de8548149f6a7069a85a0cd0";  //发送账号安全认证的Secret Access Key
	private static final String TIME = "2"; //验证码有效时间,单位:分钟	
	private static final String templateCode = "smsTpl:fd2c4ced-7b38-42e1-beff-909d753bc427"; // 本次发送使用的模板Code
	// 定义请求参数
	private static final String invokeId = "xP3sDwTE-qG1l-lhCj"; // 发送使用签名的调用ID
	
	/**
	 * 发送验证码
	 * @param phone 电话号码
	 */
	public static boolean sendCode(String phone, HttpServletRequest httprequest)  {
	
		// ak、sk等config
		SmsClientConfiguration config = new SmsClientConfiguration();
		config.setCredentials(new DefaultBceCredentials(accessKeyId, secretAccessKy));
		config.setEndpoint(endPoint);

		// 实例化发送客户端
		SmsClient smsClient = new SmsClient(config);
		String msgCode=createRandomVcode();
		Map<String, String> vars =new HashMap<String, String>(); // 若模板内容为：您的验证码是${code},在${time}分钟内输入有效
		vars.put("code", msgCode);
		vars.put("time", TIME);

		//实例化请求对象
		SendMessageV2Request request = new SendMessageV2Request();
		request.withInvokeId(invokeId)
				.withPhoneNumber(phone)
				.withTemplateCode(templateCode)
				.withContentVar(vars);

		// 发送请求
		SendMessageV2Response response = smsClient.sendMessage(request);

		// 解析请求响应 response.isSuccess()为true 表示成功
		if (response != null && response.isSuccess()) {
			HttpSession session=httprequest.getSession();
			session.setAttribute("phone_"+phone, msgCode);
			session.setMaxInactiveInterval(2*60);
			log.info("短信发送成功");
			return true;
		} else {
			log.info("短信发送失败");
		}
		return false;
	}	
	
	/**
	 * 生成4位数字验证码
	 */
	public static String createRandomVcode() {
		String vcode = "";
		for (int i = 0; i< 4; i++) {
			vcode = vcode + (int) (Math.random() * 9);
		}
		return vcode;
	}	
	
}
