package com.wf.ew.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.modules.model.PaperLogical;

import java.util.List;
import java.util.Map;

public interface PaperLogicalService extends IService<PaperLogical> {
    /**
     * 根据试卷ID查找逻辑跳转最大顺序号
     * @param paperId
     * @return
     */
    Integer findMaxSortByPaperId(String paperId);

    /**
     * 获取问题Id的顺序
     * @param questionId paperId
     * @return
     */
    Integer getSortByQueId(String questionId,String paperId);

    /**
     * 获取两个顺序之间的问题ids
     * @param smallSort largeSort
     * @param paperId
     * @return
     */
    List<String> getQueIdsByTwoSort(Integer smallSort,Integer largeSort,String paperId);

    /**
     * 获取问题id的选项逻辑跳转的选项
     * @param questionId
     * @param paperId
     * @return
     */
    List<String>  getSkipOps(String  questionId,String paperId,String isManySkip);
    /**
     * 根据试卷ID查找非选项跳转的最后一个节点
     * @param paperId
     * @return
     */
    PaperLogical getLastBysort(String paperId);

    /**
     * 查找非选项跳转且跳转问题为skipQuestionId，sort升序排列的上一个问题的Id
     * @param skipQuestionId paperId
     * @return
     */
    String getQuestionIdBysort(String skipQuestionId,String paperId);


    /**
     * 查找满足条件的ids
     * @param questionId
     * @param paperId
     * @return
     */
    List<String> geIdListByQueId(String questionId,String paperId);


    /**
     *查找满足条件的ids
     * @param skipQuestionId
     * @param paperId
     * @return
     */
    List<String> geIdListBySkipQueId(String skipQuestionId,String paperId);
    /**
     *  问题为A 且 非选项跳转的列表 问题为A 的改为B
     * @param skipQuestionId questionId paperId
     * @return
     */
    int updateLastSkipByQue(String skipQuestionId,String questionId,String paperId);

    /**
     *修改ids的skipQuestionId
     * @param skipQuestionId
     * @param ids
     * @return
     */
    int updateLastSkip(String skipQuestionId, List<String> ids);

    /**
     *  跳转问题为A 且 非选项跳转的列表 跳转问题问题为A 的改为B-
     * @param  questionId skipQuestionId paperId
     * @return
     */
    int updateSkipQues(String questionId, List<String> ids);

    /**
     * 根据试卷ID获取逻辑跳转列表
     * @param paperId
     * @return
     */
    public List<PaperLogical> getList(String paperId);


    /**
     * 根据试卷ID获取多题逻辑跳转列表
     * @param paperId
     * @return
     */
    public Map<String,List<PaperLogical>> getManyList(String paperId);



}
