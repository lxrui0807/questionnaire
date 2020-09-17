package com.wf.ew.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.modules.dao.PaperRecycleMapper;
import com.wf.ew.modules.dao.PaperViewsMapper;
import com.wf.ew.modules.model.PaperRecycle;
import com.wf.ew.modules.model.PaperViews;
import com.wf.ew.modules.service.PaperRecycleService;
import com.wf.ew.modules.service.PaperViewsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PaperViewsServiceImpl extends ServiceImpl<PaperViewsMapper, PaperViews> implements PaperViewsService {

    /**
     * 查询某试卷的最大浏览量
     * @param paperId
     * @return
     */
    @Override
    public Long findViewsByPaper(String paperId){
        return baseMapper.findViewsByPaper(paperId);
    }

    /**
     * 新增浏览量
     * @param paperId
     * @param views
     * @return
     */
    @Override
    public int  insertView(String paperId,Long views){
        return baseMapper.insertView(paperId,views);
    }

    /**
     * 修改浏览量
     * @param paperId
     * @param views
     * @return
     */
    @Override
    public int updateView(String paperId, Long views) {
        return baseMapper.updateView(paperId,views);
    }
}
