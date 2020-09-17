package com.wf.ew.modules.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import javax.xml.crypto.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 试卷答案entity
 */
@TableName(value = "bd_paper_answer")
public class PaperAnswers implements Serializable {
    private String id;//主键
    private  String paperId;//试卷id
    private Date startTime;//开始答题时间
    private Date endTime;//结束答题时间
    private String equipment;//常用设备
    private String operatingSystem;//操作系统
    private String source;//操作来源
    private String ipUrl;//IP地址
    private String areaId;//区域ID
    private Date finishTime;//多余字段，业务逻辑暂时没有使用
    private String recycle;//回收标识（0开始回收，1暂停回收）
    private String exchange;//兑换标识
    private Date createTime;//创建时间
    private Date updateTime;// 修改时间
    private String updateBy;// 修改人
    private String delFlag;// 删除标记
    private String remarks;// 备注
    private String isInvalid;// 是否无效数据（0有效（默认），1无效）
    private String invalidCause;//无效的原因
    @TableField(exist = false)
    private String value;//用作饼图结束信息
    @TableField(exist = false)
    private String name;//用作饼图结束信息
    @TableField(exist = false)
    private Integer answerCount;//回答数量
    @TableField(exist = false)
    private List<AnswerDetail> detailList=new ArrayList<AnswerDetail>();//改试卷回答的明细列表
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


    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIpUrl() {
        return ipUrl;
    }

    public void setIpUrl(String ipUrl) {
        this.ipUrl = ipUrl;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getRecycle() {
        return recycle;
    }

    public void setRecycle(String recycle) {
        this.recycle = recycle;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    public List<AnswerDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<AnswerDetail> detailList) {
        this.detailList = detailList;
    }

    public String getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(String isInvalid) {
        this.isInvalid = isInvalid;
    }

    public String getInvalidCause() {
        return invalidCause;
    }

    public void setInvalidCause(String invalidCause) {
        this.invalidCause = invalidCause;
    }
}
