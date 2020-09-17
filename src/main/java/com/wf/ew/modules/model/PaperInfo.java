package com.wf.ew.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wf.ew.base.model.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 试卷信息表
 */
@TableName(value = "bd_paper_info")
public class PaperInfo extends BaseEntity {

    private String id;// ID
    private String userId;// 用户ID
    private String title;// 试卷标题
    private String status;// 回收状态（0未回收；1 回收中）
    private String subtitle;// 试卷副标题
    private String deliveryUrl;// 投放地址
    private String qrcodeUrl;// 二维码图片地址
    private String templateClassify;// 模板分类（为空不是模板）
    private String paperType;// 试卷类型

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDeliveryUrl() {
        return deliveryUrl;
    }

    public void setDeliveryUrl(String deliveryUrl) {
        this.deliveryUrl = deliveryUrl;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getTemplateClassify() {
        return templateClassify;
    }

    public void setTemplateClassify(String templateClassify) {
        this.templateClassify = templateClassify;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }
}
