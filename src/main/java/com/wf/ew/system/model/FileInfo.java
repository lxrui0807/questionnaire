package com.wf.ew.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName(value = "sys_file_info")
public class FileInfo implements Serializable {

    private static final long serialVersionUID = -4514973310092239991L;
    @TableId(value = "id",type = IdType.INPUT)
    private String id;//id
    private String name;//文件名称
    private String filePath;//文件名称'
    public String getFilePath() {
        return filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
