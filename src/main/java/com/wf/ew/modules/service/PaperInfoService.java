package com.wf.ew.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.common.JsonResult;
import com.wf.ew.modules.model.PaperAnswers;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.model.QuestionInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 问卷信息表，Service接口
 */

public interface PaperInfoService extends IService<PaperInfo> {
    /**
     * 保存问卷
     * @param paperInfo
     * @param questionInfoList
     * @return
     */
   // boolean savePaperInfo(PaperInfo paperInfo,List<QuestionInfo> questionInfoList);
    /**
     * 查询某用户的所有(非废纸篓)问卷
     * @param paperPage
     * @param wrapper
     * @return
     */
    List<PaperInfo>  findListByUserId(Page<PaperInfo> paperPage, QueryWrapper<PaperInfo> wrapper);

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
    boolean updateRecycleStatus(PaperInfo paperInfo);

    /**
     * 移动到废纸篓
     * @param id
     * @return
     */
    boolean move(String id);

    /**
     * 恢复
     * @param id
     * @return
     */
    boolean recovery(String id);

    /**
     * 彻底删除
     * @param id
     * @return
     */
    boolean delete(String id);

    /**
     * 查询模板列表
     * @param paperPage
     * @param title
     * @return
     */
    List<PaperInfo> findTemplateClassify(Page<PaperInfo> paperPage,String title);
    /**
     * 查看问卷和问题信息
     * @param id
     * @return
     */
    public Map<String,Object> viewPaper(String id);

}
