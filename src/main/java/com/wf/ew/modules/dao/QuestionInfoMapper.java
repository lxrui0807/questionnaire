package com.wf.ew.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.model.QuestionInfo;
import java.util.List;

/**
 * 问题信息表，DAO接口
 */
public interface QuestionInfoMapper extends BaseMapper<QuestionInfo> {

    /**
     * 根据问卷的ID，查询该问卷所有的问题
     * sort 排序 大于的列表
     * @param paperId
     * @return
     */
    List<QuestionInfo> findListByPaperId(String paperId,String sort,String relation);

    /**
     * 删除
     * @param id
     * @return
     */
    int move(String id);

    /**
     * 撤销
     * @param id
     * @return
     */
    int recovery(String  id);

    /**
     * 查询题库列表
     * @return
     */
    List<QuestionInfo> findQuestionBankList(String questionBankType);

    /**
     * 查询某用户收藏题目列表
     * @param userId
     * @return
     */
    List<QuestionInfo> findQuestionCollect(String userId);

    /**
     * 根据试卷id查找需要删除的问题id list
     * @param paperId
     * @return
     */
    List<String>  findDelQuestionIds(String paperId);

    /**
     * 查询问题题库分类
     * @return
     */
    List<String> findQuestionBanks();
}
