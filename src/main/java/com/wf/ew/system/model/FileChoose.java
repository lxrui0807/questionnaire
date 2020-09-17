package com.wf.ew.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;


public class FileChoose implements Serializable {

    private String smUrl;//路径
    private Boolean hasSm;//是否图片'
    private String name;//文件名称'
    private String updateTime;//更新时间'
    private String type;//类型'
    private String url;//路径'
    private Boolean isDir;//是否路径'

    public String getSmUrl() {
        return smUrl;
    }

    public void setSmUrl(String smUrl) {
        this.smUrl = smUrl;
    }

    public Boolean getHasSm() {
        return hasSm;
    }

    public void setHasSm(Boolean hasSm) {
        this.hasSm = hasSm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getDir() {
        return isDir;
    }

    public void setDir(Boolean dir) {
        isDir = dir;
    }
}
