package com.wf.ew.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 问题试卷信息表
 */
@TableName("bd_paper_question")
public class PaperQuestion implements Serializable {

    private String id;//ID
    private String paperId;//试卷ID
    private String questionId;//问题ID
    private String required;//是否必填

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

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }
}
