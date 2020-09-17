package com.wf.ew.modules.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 试卷回收entity
 */
@TableName("bd_paper_recycle")
public class PaperRecycle implements Serializable {

    private String id;
    private String paperId;     //试卷id
    private Long recycled;    //回收量
    private Date recycleTime;   //回收时间
    private String paperAnswerId;     //试卷答案id
    private String delFlag;// 删除标记
    private Long views;          //浏览量（暂时不用）
    @TableField(exist = false)
    private Double avgTime;//每份试卷的平均完成时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public Long getRecycled() {
        return recycled;
    }

    public void setRecycled(Long recycled) {
        this.recycled = recycled;
    }

    public Date getRecycleTime() {
        return recycleTime;
    }

    public void setRecycleTime(Date recycleTime) {
        this.recycleTime = recycleTime;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Double getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(Double avgTime) {
        this.avgTime = avgTime;
    }

    public String getPaperAnswerId() {
        return paperAnswerId;
    }

    public void setPaperAnswerId(String paperAnswerId) {
        this.paperAnswerId = paperAnswerId;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
