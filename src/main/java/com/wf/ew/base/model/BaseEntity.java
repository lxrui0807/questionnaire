package com.wf.ew.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础Bean
 */
public class BaseEntity implements Serializable {

    @TableField(value = "create_by", fill = FieldFill.INSERT) // 新增执行
    private String createBy;//创建者

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;//创建日期

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE) // 新增和更新执行
    private String updateBy;//更新者

    @TableField(value = "update_Time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;//更新日期

    @TableField(value = "remarks")
    private String remarks;//备注

    //  配置逻辑删除字段
    @TableLogic
    @TableField(value = "del_Flag")
    private String delFlag = "0";//删除标记（0：正常；1：删除）

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}

