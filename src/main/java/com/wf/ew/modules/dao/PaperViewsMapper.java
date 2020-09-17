package com.wf.ew.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.modules.model.PaperViews;


/**
 * 问卷浏览信息表，DAO接口
 */
public interface PaperViewsMapper extends BaseMapper<PaperViews> {
    /**
     * 查询某份问卷的浏览量-
     * @return
     */
    public Long findViewsByPaper(String paperId);

    /**
     * 新增浏览量
     * @param paperId
     * @param views
     * @return
     */
    public int  insertView(String paperId,Long views);

    /**
     * 修改浏览量
     * @param paperId
     * @param views
     * @return
     */
    public int updateView(String paperId,Long views);
}
