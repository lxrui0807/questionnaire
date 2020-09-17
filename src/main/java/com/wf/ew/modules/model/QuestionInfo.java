package com.wf.ew.modules.model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.sf.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 问题信息表
 */
@TableName("bd_question_info")
public class QuestionInfo implements Serializable {

    private String id;// ID
    private String title;// 问题标题
    private String type;// 问题类型（单选题、多选题、简答题、级联题、图片选择题等）
    private String optiones;// 问题的选项（1.选择题：[option1,option2,option3...]2.简答题：空字符串
                    // 3.级联题：[{value:一级1,label:一级1,children:[{{value:一级1-1,label:一级1-1}]
    private Date createTime;// 问题创建时间
    private String subtitle;// 副标题
    private String createBy;// 创建人
    private Date updateTime;// 修改时间
    private String updateBy;// 修改人
    private String delFlag;// 删除标记
    private String remarks;// 备注
    private String questionBank;//是否是题库
    private String questionBankType; //题库类型
    private String starType; //量表类型（字典类型 star_type）
    private String cascadeTitle; //级联题 选项标题
    private String cascadeText; //级联题 默认选项内容

    @TableField(exist = false)
    private JSONArray jsonArray=new JSONArray(); //级练题选项数组
    @TableField(exist = false)
    private List<AnswerDetail> answerDetails=new ArrayList<AnswerDetail>();//答案数量list，用作接收参数
    @TableField(exist = false)
    private List<String> optionArray=new ArrayList<String>();//问题选项list，用作接收参数
    @TableField(exist = false)
    private PaperInfo paperInfo;//问题试卷，用作接收参数
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptiones() {
        return optiones;
    }

    public void setOptiones(String optiones) {
        this.optiones = optiones;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
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

    public String getQuestionBank() {
        return questionBank;
    }

    public void setQuestionBank(String questionBank) {
        this.questionBank = questionBank;
    }

    public String getQuestionBankType() {
        return questionBankType;
    }

    public void setQuestionBankType(String questionBankType) {
        this.questionBankType = questionBankType;
    }

    public List<AnswerDetail> getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(List<AnswerDetail> answerDetails) {
        this.answerDetails = answerDetails;
    }

    public List<String> getOptionArray() {
        return optionArray;
    }

    public void setOptionArray(List<String> optionArray) {
        this.optionArray = optionArray;
    }

    public PaperInfo getPaperInfo() {
        return paperInfo;
    }

    public void setPaperInfo(PaperInfo paperInfo) {
        this.paperInfo = paperInfo;
    }

    public String getStarType() {
        return starType;
    }

    public void setStarType(String starType) {
        this.starType = starType;
    }

    public String getCascadeTitle() {
        return cascadeTitle;
    }

    public void setCascadeTitle(String cascadeTitle) {
        this.cascadeTitle = cascadeTitle;
    }

    public String getCascadeText() {
        return cascadeText;
    }

    public void setCascadeText(String cascadeText) {
        this.cascadeText = cascadeText;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
