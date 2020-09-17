package com.wf.ew.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FtpUtil {

    Logger logger = LoggerFactory.getLogger(getClass());
    /** 本地字符编码 */
    private  String LOCAL_CHARSET = "GBK";
    // FTP协议里面，规定文件名编码为iso-8859-1
    private  String SERVER_CHARSET = "ISO-8859-1";

    //ftp服务器地址
    @Value("${ftp.server}")
    private String hostname;

    //ftp服务器端口
    @Value("${ftp.port}")
    private int port;

    //ftp登录账号
    @Value("${ftp.userName}")
    private String username;

    //ftp登录密码
    @Value("${ftp.userPassword}")
    private String password;

    //ftp保存目录(以“/”结束)
    @Value("${ftp.bastPath}")
    private String basePath;

    //ftp下载目录
    @Value("${ftp.downPath}")
    private String downPath;

    @Value("${ftp.imageBaseUrl}")
    private String nginxUrl;



    /**
     * 初始化ftp服务器
     */
    public FTPClient getFtpClient() {
        FTPClient ftpClient =null;
        try {
            ftpClient =new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setDataTimeout(1000 * 120);//设置连接超时时间
            logger.info("connecting...ftp服务器:" + hostname + ":" + port);
            try {
                ftpClient.connect(hostname,port); // 连接ftp服务器
            } catch (IOException e) {
                e.printStackTrace();
            }
           // ftpClient.login(username, password); // 登录ftp服务器
            ftpClient.login("anonymous", null);//登录（匿名用户登录）
            int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
                    "OPTS UTF8", "ON"))) {      // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                LOCAL_CHARSET = "UTF-8";
            }
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.error("connect failed...ftp服务器:" + hostname + ":" + port);
            }
            logger.info("connect successfu...ftp服务器:" + hostname + ":" + port);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ftpClient;
    }



    /**
     * 递归遍历出目录下面所有文件
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     *
     */
    public JSONArray getFileList(String pathName){
        JSONArray json = new JSONArray();
        if(pathName.startsWith("/")&&pathName.endsWith("/")){
            String directory = basePath+pathName;
            FTPClient ftpClient=null;
            //更换目录到当前目录
            try {
                ftpClient=getFtpClient();
                boolean st=ftpClient.changeWorkingDirectory(directory);
                if(st){
                    FTPFile[] files = ftpClient.listFiles();
                    for(FTPFile file:files){
                        JSONObject jo = new JSONObject();
                        String name=file.getName();
                        jo.put("name",name);
                        jo.put("updateTime",file.getTimestamp().getTimeInMillis());
                        if(file.isFile()){
                            String fileTyle=name.substring(name.lastIndexOf(".")+1,name.length());
                            jo.put("type",fileTyle);
                            jo.put("url",nginxUrl+directory+name);
                            if(isPhoto(fileTyle)){
                                jo.put("smUrl", nginxUrl+directory+name);
                                jo.put("hasSm", true);
                                 /* try {
                                  String fname=  new String(name.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    ftpClient.retrieveFile(fname,os);
                                    byte[] bytes = os.toByteArray();
                                    String base64Image = new String(Base64.encodeBase64(bytes));
                                    jo.put("smUrl", "data:image/jpg;base64,"+base64Image);
                                    jo.put("hasSm", true);
                                    os.flush();
                                    os.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/

                            }
                            jo.put("isDir",false);
                        }else if(file.isDirectory()){
                            jo.put("type","dir");
                            jo.put("isDir",true);
                        }
                        json.add(jo);
                    }
                }
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

        }
        return json;
    }

    /**
     * 上传文件
     *
     * @param targetDir    ftp服务保存地址
     * @param fileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFileToFtp(String targetDir, String fileName, InputStream inputStream) {
        boolean isSuccess = false;
       // String servicePath = String.format("%s%s%s", basePath, "/", targetDir);
        String servicePath =basePath+targetDir;
                FTPClient ftpClient = getFtpClient();
        try {
            if (ftpClient.isConnected()) {
                logger.info("开始上传文件到FTP,文件名称:" + fileName);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//设置上传文件类型为二进制，否则将无法打开文件
                if(!ftpClient.changeWorkingDirectory(servicePath)){
                   createDirecroty(servicePath,ftpClient); //创建多层目录
                 }
                ftpClient.changeWorkingDirectory(servicePath);
                //设置为被动模式(如上传文件夹成功，不能上传文件，注释这行，否则报错refused:connect  )
                ftpClient.enterLocalPassiveMode();//设置被动模式，文件传输端口设置
                ftpClient.storeFile(fileName, inputStream);
                inputStream.close();
                ftpClient.logout();
                isSuccess = true;
                logger.info(fileName + "文件上传到FTP成功");
            } else {
                logger.error("FTP连接建立失败");
            }
        } catch (Exception e) {
            logger.error(fileName + "文件上传到FTP出现异常");
            logger.error(e.getMessage(), e);
        } finally {
            closeFtpClient(ftpClient);
            closeStream(inputStream);
        }
        return isSuccess;
    }

    /**
     * 关闭
     * @param closeable
     */
    public void closeStream(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 改变目录路径
     * @param ftpClient
     * @param directory
     * @return
     */
    public boolean changeWorkingDirectory(FTPClient ftpClient, String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(new String(directory.getBytes(LOCAL_CHARSET), SERVER_CHARSET));
            if (flag) {
                logger.info("进入文件夹" + directory + " 成功！");

            } else {
                logger.info("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return flag;
    }

    /**
     * 递归创建远程服务器目录
     *
     * @param remote 远程服务器文件绝对路径
     * @param ftpClient FTPClient对象
     * @return remote 目录创建是否成功
     * @throws IOException
     */
    public boolean createDirecroty(String remote, FTPClient ftpClient) throws IOException {
        boolean success = true;
        if (!remote.endsWith("/")) {
            remote = remote + "/";
        }
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(ftpClient,directory))
        {
            // 如果远程目录不存在，则递归创建远程服务器目录
            int start = 0;
            int end = 0;
            if (directory.startsWith("/"))
            {
                start = 1;
            }
            else
            {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true)
            {
                String subDirectory = remote.substring(start, end);
                if (!changeWorkingDirectory(ftpClient,subDirectory))
                {
                    if (makeDirectory(ftpClient,subDirectory)){
                        changeWorkingDirectory(ftpClient,subDirectory);
                    }
                    else{
                        logger.error("创建目录[" + subDirectory + "]失败");
                        return false;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/", start);

                // 检查所有目录是否创建完毕
                if (end <= start)
                {
                    break;
                }
            }
        }
        return success;
    }

    /***
     * 判断ftp服务器文件是否存在
     * @param ftpClient
     * @param path
     * @return
     * @throws IOException
     */
    public boolean existFile(FTPClient ftpClient, String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /***
     * 创建目录
     * @param ftpClient
     * @param dir
     * @return
     */
    public boolean makeDirectory(FTPClient ftpClient, String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                logger.info("创建文件夹" + dir + " 成功！");

            } else {
                logger.info("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return flag;
    }

    /**
     * 下载文件 *
     *
     * @param pathName FTP服务器文件目录 *
     * @param pathName 下载文件的条件*
     * @return
     */
    public Map<String,Object> downloadFile(String pathName, String targetFileName) {
        Map<String,Object> map=new HashMap<String,Object>();
        FTPClient ftpClient = getFtpClient();
        boolean flag = false;
        OutputStream os = null;
        try {
            System.out.println("开始下载文件");
            //切换FTP目录
            ftpClient.changeWorkingDirectory(basePath+pathName);
            ftpClient.enterLocalPassiveMode();
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                String ftpFileName = file.getName();
                if (targetFileName.equals(ftpFileName)) {
                    String path=downPath + File.separator+ ftpFileName;
                    map.put("path",path);
                    File localFile = new File(path);
                    // 判断文件夹是否存在，如果不存在则创建
                    if (!localFile.getParentFile().exists()) {
                        localFile.getParentFile().mkdirs();
                    }
                    os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.flush();
                    os.close();
                }
            }
            ftpClient.logout();
            flag = true;
            logger.info("下载文件成功");
        } catch (Exception e) {
            logger.error("下载文件失败");
            logger.error(e.getMessage(), e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        map.put("isSuccess",flag);
        return map;
    }


    /**
     * 删除文件 *
     *
     * @param pathname FTP服务器保存目录 *
     * @param filename 要删除的文件名称 *
     * @return
     */
    public boolean deleteFile(String pathname, String filename) {
        boolean flag = false;
        FTPClient ftpClient = getFtpClient();
        try {
            logger.info("开始删除文件");
            if (ftpClient.isConnected()) {
                //切换FTP目录
                ftpClient.changeWorkingDirectory(basePath+pathname);
                ftpClient.enterLocalPassiveMode();
                ftpClient.dele(filename);
                ftpClient.logout();
                flag = true;
                logger.info("删除文件成功");
            } else {
                logger.info("删除文件失败");

            }
        } catch (Exception e) {
            logger.error("删除文件失败");
            logger.error(e.getMessage(), e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return flag;
    }

    public void closeFtpClient(FTPClient ftpClient) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 输入流判断是否是图片
     *   // 通过ImageReader来解码这个file并返回一个BufferedImage对象
     *             // 如果找不到合适的ImageReader则会返回null，我们可以认为这不是图片文件
     *             // 或者在解析过程中报错，也返回false
     * @return
     */
    public boolean isImage(InputStream inputStream){
        try {
            Image image = ImageIO.read(inputStream);
            return image != null;
        } catch(IOException ex) {
            return false;
        }
    }

    /***
     * 后缀名判断是否是图片
     * @param fileType
     * @return
     */
    public boolean isPhoto(String fileType){
        String [] imgeArray= {"bmp","dib","gif","jfif","jpe", "jpeg","jpg", "png","tif","tiff", "ico"};
        if(Lists.newArrayList(imgeArray).contains(fileType.toLowerCase())){
            return true;
        }
        return false;
    }



}
