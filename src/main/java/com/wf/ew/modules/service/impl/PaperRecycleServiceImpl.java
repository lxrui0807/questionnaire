package com.wf.ew.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.modules.dao.PaperRecycleMapper;
import com.wf.ew.modules.model.PaperRecycle;
import com.wf.ew.modules.service.PaperRecycleService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PaperRecycleServiceImpl extends ServiceImpl<PaperRecycleMapper, PaperRecycle> implements PaperRecycleService {
    /**
     * 查询某份问卷的总回收量、浏览量-
     * @return
     */
    @Override
   public  PaperRecycle findRecycledSum(String paperId){
        return  baseMapper.findRecycledSum(paperId);
    }
    /**
     * 查询每份问卷的平均完成时间
     * @param paperId
     * @return
     */
    public Double findAvgTime(String paperId){
        return baseMapper.findAvgTime(paperId);
    }
    /**
     * 查询每一天的回收量统计图表数据
     * @param paperId
     * @return
     */
    public List<PaperRecycle> findRecycledListByDay(String paperId){
        return baseMapper.findRecycledListByDay(paperId);
    }

    @Override
    public int updateDelFlag(String delFlag, String paperAnswerId) {
        return baseMapper.updateDelFlag(delFlag,paperAnswerId);
    }

}
