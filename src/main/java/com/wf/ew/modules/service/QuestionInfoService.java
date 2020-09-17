package com.wf.ew.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.modules.model.QuestionInfo;
import java.util.List;

public interface QuestionInfoService extends IService<QuestionInfo> {


    /**
     * 根据问卷的ID，查询该问卷所有的问题
     * @param paperId
     * @return
     */
    List<QuestionInfo> findListByPaperId(String paperId);

    /**
     * 根据问卷的ID，查询该问卷排序以后所有的问题
     * @param paperId
     * @return
     */
    public List<QuestionInfo> findLargeListByPaperId(String paperId,String sort);


    /**
     * 根据问卷的ID，查询该问卷排序以前所有的问题
     * @param paperId
     * @return
     */
    public List<QuestionInfo> findSmallListByPaperId(String paperId,String sort);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean move(String id);

    /**
     * 撤销
     * @param id
     * @return
     */
    boolean recovery(String  id);

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
