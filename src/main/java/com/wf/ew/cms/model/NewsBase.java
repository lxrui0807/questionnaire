package com.wf.ew.cms.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wf.ew.base.model.BaseEntity;

import java.util.Date;
@TableName(value = "cms_news")
public class NewsBase extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id")
    private String id; // id
    protected String type; // 分类类型（类似新闻存放在同一表中时用这个区分）
    private String title; // 标题
    private String content; // 内容
    private String status; // 状态(草稿、待审、退回、发布、下线)
    private String approveBy; // 审核人
    private Date approveDate; // 审核日期
//    private Date beginTime; // 发布开始时间
//    private Date endTime; // 发布结束时间
    private String copyFrom; // 内容来源网站
    private String copyFromUrl; // 内容来源网址
    private String photo;// 新闻图片
    @TableField(exist = false)
    private boolean isPermission;//是否有审核权限
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date approveDate) {
        this.approveDate = approveDate;
    }

    public String getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
    }

    public String getCopyFromUrl() {
        return copyFromUrl;
    }

    public void setCopyFromUrl(String copyFromUrl) {
        this.copyFromUrl = copyFromUrl;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isPermission() {
        return isPermission;
    }

    public void setPermission(boolean permission) {
        isPermission = permission;
    }
}
