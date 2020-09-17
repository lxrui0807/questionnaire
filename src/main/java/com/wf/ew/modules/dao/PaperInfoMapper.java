package com.wf.ew.modules.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.modules.model.PaperInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问卷信息表，DAO接口
 */
public interface PaperInfoMapper extends BaseMapper<PaperInfo> {
    /**
     * 查询某用户的所有(非废纸篓)问卷
     * @param paperPage
     * @param wrapper
     * @return
     */
    List<PaperInfo> findListByUserId(Page<PaperInfo> paperPage,@Param(Constants.WRAPPER) QueryWrapper<PaperInfo> wrapper);

    /**
     * 查询某用户的所有(废纸篓)问卷
     * @param userId
     * @return
     */
    List<PaperInfo> findListByUserIdAtRecycleBin(Page<PaperInfo> paperPage,String userId);

    /**
     * 开始回收/暂停回收
     * @param paperInfo
     * @return
     */
    int updateRecycleStatus(PaperInfo paperInfo);

    /**
     * 移动到废纸篓
     * @param id
     * @return
     */
    int move(String id);

    /**
     * 恢复
     * @param id
     * @return
     */
    int recovery(String id);

    /**
     * 彻底删除
     * @param id
     * @return
     */
    int delete(String id);
    /**
     * 查询模板列表
     *  @param paperPage
     * @param title
     * @return
     */
    List<PaperInfo> findTemplateClassify(Page<PaperInfo> paperPage,String title);
}
