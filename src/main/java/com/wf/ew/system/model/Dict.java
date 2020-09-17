package com.wf.ew.system.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wf.ew.base.model.BaseEntity;

/**
 * 字典
 */
@TableName(value = "sys_dict")
public class Dict extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private String id;//id
    private String value;//数据值
    private String label;//标签名
    private String type;//类型
    private String description;//描述
    private Integer sort;//排序
    private String parentId;//父Id
    private String  sys;//所属系统或模块

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }
}
