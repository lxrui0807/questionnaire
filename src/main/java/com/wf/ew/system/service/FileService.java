package com.wf.ew.system.service;


import com.alibaba.fastjson.JSONArray;
import com.wf.ew.system.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {
    //图片上传
    String updatePhoto(MultipartFile file,String userId);

    List<FileInfo> getFileDir(FileInfo file);

    public JSONArray getFileList(String path);

    public Map<String,Object> downfile(String pathName, String filename);

    public boolean deletefile(String pathname, String filename);

}
