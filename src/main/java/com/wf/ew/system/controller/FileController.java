package com.wf.ew.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.utils.DateUtil;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.system.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Api(value = "文件管理", tags = "file")
@RestController
@RequestMapping("${api.version}/file")
public class FileController extends BaseController {
    @Autowired
    private FileService fileService;


    @Value("${ftp.imageBaseUrl}")
    private String nginxUrl;

    //ftp保存目录(以“/”结束)
    @Value("${ftp.bastPath}")
    private String basePath;


//    @ApiOperation(value = "文件上传")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
//    })
//    @GetMapping("getFileDir")
//    public JsonResult getFileDir(FileInfo file, HttpServletRequest request) {
//        String userId=UserUtil.getLoginUserId(request);
//        String filePath=file.getFilePath();
//        if("/".equals(filePath)){
//            filePath="/"+userId;
//        }else{
//            filePath="/"+userId+filePath;
//        }
//        file.setFilePath(filePath);
//        //查询下级
//        List<FileInfo>list=fileService.getFileDir(file);
//        for(FileInfo fileinfo:list){
//            FileChoose choose=new FileChoose();
//            if ("/".equals(fileinfo.getFilePath().indexOf(fileinfo.getFilePath().length()))){
//
//            }
//        }
//        return JsonResult.ok().put("list",list);
//    }



    @ApiOperation(value = "文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("uploadFile")
    public JsonResult uploadFile(MultipartFile file, HttpServletRequest request) {
        String userId=UserUtil.getLoginUserId(request);
        String s = fileService.updatePhoto(file,userId);
        Map map=new HashMap();
        map.put("src",s);
        map.put("fullpath",nginxUrl+basePath+s);
        return JsonResult.ok(0,"上传成功").put("data",map);
    }


    @ApiOperation(value = "文件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "路径", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("list")
    public JsonResult list(String path,HttpServletRequest request) {
        String userId=UserUtil.getLoginUserId(request);
        String basepath="/"+userId+"/";
        if(null!=path&&path.length()>1){
            path=basepath+path+"/";
        }else{
            path=basepath;
        }
        JSONArray data=fileService.getFileList(path);
        return JsonResult.ok().put("data", data);
    }


    @ApiOperation(value = "文件下载")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "文件名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("downfile")
    public JsonResult downfile(String fileName,HttpServletRequest request) {
        String userId=UserUtil.getLoginUserId(request);
        String path="/"+userId+"/"+ DateUtil.getCurrentYear() +"/"+DateUtil.getCurrentMonth()+"/";
        Map<String,Object> map=fileService.downfile(path,fileName);
        boolean isSuc= (boolean) map.get("isSuccess");
        if(isSuc){
            return JsonResult.ok().put("path",map.get("path"));
        }
        return JsonResult.error().put("path",map.get("path"));
    }


    @ApiOperation(value = "文件删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "文件名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("deletefile")
    public JsonResult deletefile(String fileName,HttpServletRequest request) {
        String userId=UserUtil.getLoginUserId(request);
        String path="/"+userId+"/"+ DateUtil.getCurrentYear() +"/"+DateUtil.getCurrentMonth()+"/";
        boolean isSuc=fileService.deletefile(path,fileName);
        if(isSuc){
            return JsonResult.ok();
        }
        return JsonResult.error();
    }

}
