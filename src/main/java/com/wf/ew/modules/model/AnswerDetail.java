package com.wf.ew.modules.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;


/**
 * 答案明细entity
 */
@TableName(value = "bd_answer_detail")
public class AnswerDetail implements Serializable {
    private String id;
    private String questionId;      //问题id
    private String questionAnswer;  //问题答案(多个字段以逗号拼接)
    private String  paperAnswerId;  //试卷答案id
    private Date updateTime;// 修改时间
    private String updateBy;// 修改人
    private String delFlag;// 删除标记
    private String remarks;// 备注
    @TableField(exist = false)
    private Integer answerCount;//答案的数量
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public String getPaperAnswerId() {
        return paperAnswerId;
    }

    public void setPaperAnswerId(String paperAnswerId) {
        this.paperAnswerId = paperAnswerId;
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

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }
}
