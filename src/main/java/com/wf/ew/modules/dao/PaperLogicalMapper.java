package com.wf.ew.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.modules.model.PaperLogical;

import java.util.List;

public interface PaperLogicalMapper extends BaseMapper<PaperLogical> {

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
     * 查找非选项跳转且跳转问题为paperId，sort升序排列的上一个问题的Id
     * @param skipQuestionId
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

}
