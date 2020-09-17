package com.wf.ew.base.model;

import com.baomidou.mybatisplus.annotation.TableField;
import org.hibernate.validator.constraints.Length;

public class TreeEntity extends BaseEntity {

    protected String parentId;// 父级编号
    protected String parentIds; // 所有父级编号
    protected Integer sort;		// 排序
    protected String name; 	// 机构名称

    @TableField(exist = false)
    private boolean haveChild;		//  树状图要求字段（是否含有子级元素）

    @TableField(exist = false)
    private Integer state;		//  树状图要求字段


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Length(min=1, max=2000)
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public boolean getHaveChild() {
        return haveChild;
    }

    public void setHaveChild(boolean haveChild) {
        this.haveChild = haveChild;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Length(min=1, max=100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
