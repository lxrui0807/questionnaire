package com.wf.ew.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.modules.model.PaperRecycle;

import java.util.Date;
import java.util.List;

/**
 * 问卷回收信息表，DAO接口
 */
public interface PaperRecycleMapper extends BaseMapper<PaperRecycle> {
    /**
     * 查询某份问卷的总回收量、浏览量-
     * @return
     */
 PaperRecycle findRecycledSum(String paperId);

    /**
     * 查询每份问卷的平均完成时间
     * @param paperId
     * @return
     */
    Double findAvgTime(String paperId);

    /**
     * 查询每一天的回收量统计图表数据
     * @param paperId
     * @return
     */
   List<PaperRecycle> findRecycledListByDay(String paperId);

    /**
     * 根据答卷id修改是否删除、恢复
     * @param delFlag
     * @param paperAnswerId
     * @return
     */
    int updateDelFlag(String delFlag,String paperAnswerId);
}
