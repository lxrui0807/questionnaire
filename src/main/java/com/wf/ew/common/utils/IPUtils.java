package com.wf.ew.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author:刘喜瑞
 * @description:ip地址工具类
 * @param:request
 */
public class IPUtils {
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-real-ip");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
            if (ip != null) {
                ip = ip.split(",")[0].trim();
            }
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
    public static Map<String, String> getLocationByIp(List<String> ips) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        Map<String, String> map = new HashMap<String, String>();
        try {
            for (String ip : ips) {
                if ("127.0.0.1".equals(ip) || ip.startsWith("192.168.0.")) {
                    map.put(ip, "陕西省西安市");
                    continue;
                }
                //https://www.ipip.net/support/api.html接口文档地址2020-03-10
                HttpGet get = new HttpGet("http://freeapi.ipip.net/"+ip);
                get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
                get.addHeader("X-Requested-With", "XMLHttpRequest");
                response=httpclient.execute(get);
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300 || status == 304) {
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity);
                    String[] jsonStr = StringEscapeUtils.unescapeJava(result).split(",");
                        //String contry = jsonStr[0].substring(2,4);
                        String province =jsonStr[1].replace("\"","");
                        String city = jsonStr[2].replace("\"","");
                        if("北京".equals(province)||"天津".equals(province)||"重庆".equals(province)||"上海".equals(province)){
                            province=province+"市";
                        }else{
                        province=province+"省"+city+"市";
                    }
                        map.put(ip, province );
                }
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        } finally {
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}