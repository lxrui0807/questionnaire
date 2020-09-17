package com.wf.ew.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wf.ew.system.model.User;
import com.wf.ew.system.service.OthersysService;
import com.yuanjing.framework.common.utils.HttpUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OthersysServiceImpl implements OthersysService {

    @Value("${mhBaseUrl}")
    private  String mhBaseUrl;//门户系统

    @Value("${cjBaseUrl}")
    private  String cjBaseUrl;//采集系统

    @Value("${xfzBaseUrl}")
    private  String xfzBaseUrl;//消费者系统

    private static final Logger logger = LoggerFactory.getLogger(OthersysServiceImpl.class);

    /**
     * 添加四个系统的用户
     *@author: 赵媛
     *@description: TODO
     *@param: @param user
     *@return: void
     *@throws:
     */
    public void addAllUser(User user){
        //修改cas用户信息
	     /* String casurl=casBaseUrl+"/addUserBody";
	      JSONObject casResult=addUser("cas",casurl, user);
	      System.out.println(casResult);*/

        //修改门户系统用户信息
        String mhurl=mhBaseUrl+"/f/addUserBody";
        JSONObject cjResult=addUser("caiji",mhurl, user);
        System.out.println(cjResult);


        //修改采集者系统用户信息
        String cjurl=cjBaseUrl+"/f/addUserBody";
        JSONObject xfzResult=addUser("caiji",cjurl, user);
        System.out.println(xfzResult);


        //修改消费者系统用户信息
        String zfzurl=xfzBaseUrl+"/common/addUserBody";
        JSONObject wjResult=addUser("questionire",zfzurl, user);
        System.out.println(wjResult);


    }


    /**
     * 修改四个系统的用户
     *@author: 赵媛
     *@description: TODO
     *@param: @param user
     *@return: void
     *@throws:
     */
    public void updateAllUser(User user){
        //修改cas用户信息
       /* String casurl=casBaseUrl+"/saveUserBody";
        JSONObject casResult=updateUser("cas",casurl, user);
        System.out.println(casResult);*/

        //修改门户系统用户信息
        String mhurl=mhBaseUrl+"/f/saveUserBody";
        JSONObject mhResult=updateUser("caiji",mhurl, user);
        System.out.println(mhResult);

        //修改采集系统用户信息
        String caijiurl=cjBaseUrl+"/f/saveUserBody";
        JSONObject cjResult=updateUser("caiji",caijiurl, user);
        System.out.println(cjResult);

        //修改消费者系统用户信息
        String xfzurl=xfzBaseUrl+"/common/saveUserBody";
        JSONObject xfzResult=updateUser("questionire",xfzurl, user);
        System.out.println(xfzResult);
    }



    /**
     * 修改四个系统的密码
     *@author: 赵媛
     *@description: TODO
     *@param: @param user
     *@param: @param oldPassword
     *@param: @param newPassword
     *@return: void
     *@throws:
     */
    public void updateAllPwd(User user, String oldPassword, String newPassword){

        //修改cas用户信息
       /* String casurl=casBaseUrl+"/saveUserBody";
        JSONObject casResult=updateUserPwd("cas",casurl,user.getUsername(),oldPassword,newPassword);
        System.out.println(casResult);*/

        //修改门户系统用户信息
        String mhurl=mhBaseUrl+"/f/modifyPwdBody";
        JSONObject mhResult=updateUserPwd("caiji",mhurl, user.getUsername(),oldPassword,newPassword);
        System.out.println(mhResult);

        //修改采集系统用户信息
        String caijiurl=cjBaseUrl+"/f/modifyPwdBody";
        JSONObject cjResult=updateUserPwd("caiji",caijiurl, user.getUsername(),oldPassword,newPassword);
        System.out.println(cjResult);

        //修改消费者系统用户信息
        String xfzurl=xfzBaseUrl+"/common/savePwdBody";
        JSONObject xfzResult=updateUserPwd("questionire",xfzurl, user.getUsername(),oldPassword,newPassword);
        System.out.println(xfzResult);
    }


    /***
     * 删除四个系统的用户信息
     *@author: 赵媛
     *@description: TODO
     *@param: @param user
     *@return: void
     *@throws:
     */
    public void deleteAllUser(User user){
        //修改cas用户信息
       /* String casurl=casBaseUrl+"/saveUserBody";
        JSONObject casResult=updateUserPwd("cas",casurl,user.getUsername(),oldPassword,newPassword);
        System.out.println(casResult);*/

        //修改门户系统用户信息
        String mhurl=mhBaseUrl+"/f/deleteBody";
        JSONObject mhResult=deleteUser("caiji",mhurl, user.getUsername());
        System.out.println(mhResult);

        //修改采集系统用户信息
        String caijiurl=cjBaseUrl+"/f/deleteBody";
        JSONObject cjResult=deleteUser("caiji",caijiurl, user.getUsername());
        System.out.println(cjResult);

        //修改消费者系统saveUserBody用户信息
        String xfzurl=xfzBaseUrl+"/common/deleteBody";
        JSONObject xfzResult=deleteUser("questionire",xfzurl,  user.getUsername());
        System.out.println(xfzResult);
    }



    /**
     * 添加其他系统用户信息
     *@author: 赵媛
     *@description: TODO
     *@param: @param url
     *@param: @param username
     *@param: @param password
     *@param: @return
     *@return: JSONObject
     *@throws:
     */
    public JSONObject addUser(String type, String url, User user) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            if(type.equals("questionire")){
                params.put("username", user.getUsername());
                params.put("oldUsername", user.getOldUsername());
                params.put("password", user.getPassword());
                params.put("nickName", user.getNickName());
            }else{
                params.put("loginName", user.getUsername());
                params.put("password", user.getPassword());
                params.put("name", user.getNickName());
                params.put("remarks", user.getOldUsername());
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpUtils httpInstance = HttpUtils.newInstance().httpclient(httpclient);
            String responseText = httpInstance.post(url, params);
            if (!responseText.startsWith("{") || responseText.indexOf("\"error\"") > -1) {
                logger.warn("取数出错");
                return null;
            }
            JSONObject result=JSON.parseObject(responseText);
            return result;
        } catch (Exception e) {
            logger.error(url+"修改失败！", e);
        }
        return null;
    }

    /**
     * 修改其他系统用户信息
     *@author: 赵媛
     *@description: TODO
     *@param: @param url
     *@param: @param username
     *@param: @param password
     *@param: @return
     *@return: JSONObject
     *@throws:
     */
    public JSONObject updateUser(String type, String url, User user) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            if(type.equals("questionire")){
                params.put("username", user.getUsername());
                params.put("oldUsername", user.getOldUsername());
                params.put("password", user.getPassword());
                params.put("nickName", user.getNickName());
            }else{
                params.put("loginName", user.getUsername());
                params.put("oldLoginName", user.getOldUsername());
                params.put("newPassword", user.getPassword());
                params.put("name", user.getNickName());
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpUtils httpInstance = HttpUtils.newInstance().httpclient(httpclient);
            String responseText = httpInstance.post(url, params);
            if (!responseText.startsWith("{") || responseText.indexOf("\"error\"") > -1) {
                logger.warn("取数出错");
                return null;
            }
            JSONObject result=JSON.parseObject(responseText);
            return result;
        } catch (Exception e) {
            logger.error(url+"修改失败！", e);
        }
        return null;
    }

    /**
     * 修改其他系统密码信息
     *@author: 赵媛
     *@description: TODO
     *@param: @param url
     *@param: @param username
     *@param: @param password
     *@param: @return
     *@return: JSONObject
     *@throws:
     */
    public JSONObject updateUserPwd(String type,String url,String username,String oldpwd,String pwd) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            if(type.equals("questionire")){
                params.put("username",username);
                params.put("oldPsw", oldpwd);
                params.put("newPsw", pwd);
            }else{
                params.put("username", username);
                params.put("oldPassword",oldpwd);
                params.put("newPassword", pwd);
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpUtils httpInstance = HttpUtils.newInstance().httpclient(httpclient);
            String responseText = httpInstance.post(url, params);
            if (!responseText.startsWith("{") || responseText.indexOf("\"error\"") > -1) {
                System.out.println("取数出错");
                return null;
            }
            JSONObject result= JSON.parseObject(responseText);
            return result;
        } catch (Exception e) {
            System.out.println(url+"修改失败:"+ e);
        }
        return null;
    }


    /**
     * 删除其他系统用户信息
     *@author: 赵媛
     *@description: TODO
     *@param: @param url
     *@param: @param username
     *@param: @param password
     *@param: @return
     *@return: JSONObject
     *@throws:
     */
    public JSONObject deleteUser(String type,String url,String username) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            if(type.equals("questionire")){
                params.put("username", username);
            }else{
                params.put("loginName", username);
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpUtils httpInstance = HttpUtils.newInstance().httpclient(httpclient);
            String responseText = httpInstance.post(url, params);
            if (!responseText.startsWith("{") || responseText.indexOf("\"error\"") > -1) {
                logger.warn("取数出错");
                return null;
            }
            JSONObject result=JSON.parseObject(responseText);
            return result;
        } catch (Exception e) {
            logger.error(url+"删除失败！", e);
        }
        return null;
    }

}
