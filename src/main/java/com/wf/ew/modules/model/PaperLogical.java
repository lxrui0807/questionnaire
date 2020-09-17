package com.wf.ew.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 试卷问题逻辑跳转信息表
 */
@TableName("bd_paper_logical")
public class PaperLogical implements Serializable {

    private String id;//ID
    private String paperId;//试卷ID
    private String questionId;//问题Id
    private String skip;//是否选项跳转
    private String skipValue;//跳转选项值
    private String skipQuestionId;//跳转到问题ID
    private Integer sort;//顺序
    private String createBy;//创建人
    private Date createTime;//修改时间
    private Date updateTime;//修改时间
    private String updateBy;//修改人
    private String remarks;//备注
    private String isManySkip;//是否多题选项跳转

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

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }

    public String getSkipValue() {
        return skipValue;
    }

    public void setSkipValue(String skipValue) {
        this.skipValue = skipValue;
    }

    public String getSkipQuestionId() {
        return skipQuestionId;
    }

    public void setSkipQuestionId(String skipQuestionId) {
        this.skipQuestionId = skipQuestionId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIsManySkip() {
        return isManySkip;
    }

    public void setIsManySkip(String isManySkip) {
        this.isManySkip = isManySkip;
    }
}
