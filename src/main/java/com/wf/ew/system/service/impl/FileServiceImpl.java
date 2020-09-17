package com.wf.ew.system.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.common.utils.DateUtil;
import com.wf.ew.common.utils.FtpUtil;
import com.wf.ew.system.dao.FileInfoMapper;
import com.wf.ew.system.model.FileInfo;
import com.wf.ew.system.service.FileService;
import com.yuanjing.framework.common.utils.IdGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileService {
    @Autowired
    private FtpUtil ftpUtil;

    @Override
    public String updatePhoto(MultipartFile file,String userId) {
        //给图片起一个新的名称，防止在图片名称重复
        String newname=new String();
        String targetDir="/"+userId+"/"+ DateUtil.getCurrentYear() +"/"+DateUtil.getCurrentMonth();
        if(file!=null){
            //生成新的图片名称
            //newname =+System.currentTimeMillis()+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName=file.getOriginalFilename();
            String hzm=fileName.substring(fileName.lastIndexOf("."));
            String old_name=fileName.substring(0,fileName.lastIndexOf("."));
            newname =old_name+"_"+System.currentTimeMillis()+hzm;
            try {
                //图片上传，调用ftp工具类 image 上传的文件夹名称，newname 图片名称，inputStream
                boolean hopson = ftpUtil.uploadFileToFtp(targetDir, newname, file.getInputStream());
                //保存到文件表
                if(hopson) {
                    // 把图片信息存入到数据库中
                    //{
                    //    "name": "其他",
                    //        "updateTime": 1545804042000,
                    //        "type": "dir",
                    //        "isDir": true
                    //}
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setId(IdGen.uuid());
                    fileInfo.setName(newname);
                    fileInfo.setFilePath(targetDir+"/"+ newname);
                    this.save(fileInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }
        return targetDir+"/"+ newname;
    }

    @Override
    public List<FileInfo> getFileDir(FileInfo file){
        return baseMapper.getFileDir(file);
    }


    @Override
    public JSONArray getFileList(String path){
        return ftpUtil.getFileList(path);
    }

    @Override
    public Map<String,Object> downfile(String pathName, String filename){
        return ftpUtil.downloadFile(pathName,filename);
    }

    @Override
    public boolean deletefile(String pathname, String filename){
        return ftpUtil.deleteFile(pathname,filename);
    }

}
