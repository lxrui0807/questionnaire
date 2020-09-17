package com.wf.ew.modules.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 试卷浏览量entity
 */
@TableName("bd_paper_views")
public class PaperViews implements Serializable {

    private String id;
    private String paperId;     //试卷id
    private Long views;          //浏览量(获取实时：数据库+REDIS)

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

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }
}
