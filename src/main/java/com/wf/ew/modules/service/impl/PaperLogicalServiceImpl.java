package com.wf.ew.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wf.ew.modules.dao.PaperLogicalMapper;
import com.wf.ew.modules.model.PaperLogical;
import com.wf.ew.modules.service.PaperLogicalService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaperLogicalServiceImpl extends ServiceImpl<PaperLogicalMapper, PaperLogical> implements PaperLogicalService {

    /**
     * 根据试卷ID查找逻辑跳转最大顺序号
     * @param paperId
     * @return
     */
    @Override
    public Integer findMaxSortByPaperId(String paperId) {
        return baseMapper.findMaxSortByPaperId(paperId);
    }

    /**
     * 获取问题Id的顺序
     * @param questionId paperId
     * @return
     */
    @Override
    public Integer getSortByQueId(String questionId,String paperId){
        return baseMapper.getSortByQueId(questionId,paperId);
    }

    /**
     * 获取两个顺序之间的问题ids
     * @param smallSort largeSort
     * @param paperId
     * @return
     */
    @Override
    public List<String> getQueIdsByTwoSort(Integer smallSort,Integer largeSort,String paperId){
        return baseMapper.getQueIdsByTwoSort(smallSort,largeSort,paperId);
    }

    /**
     * 获取问题id的选项逻辑跳转的选项
     * @param questionId
     * @param paperId
     * @return
     */
    @Override
    public List<String> getSkipOps(String questionId, String paperId,String isManySkip) {
        return baseMapper.getSkipOps(questionId,paperId,isManySkip);
    }

    /**
     * 根据试卷ID查找非选项跳转的最后一个节点
     * @param paperId
     * @return
     */
    @Override
    public PaperLogical getLastBysort(String paperId){
        return baseMapper.getLastBysort(paperId);
    }

    /**
     * 查找非选项跳转且跳转问题为skipQuestionId，sort升序排列的上一个问题的Id
     * @param skipQuestionId paperId
     * @return
     */
    @Override
    public String getQuestionIdBysort(String skipQuestionId,String paperId){
        return baseMapper.getQuestionIdBysort(skipQuestionId,paperId);
    }



    @Override
    public List<String> geIdListByQueId(String questionId,String paperId){
        return baseMapper.geIdListByQueId(questionId,paperId);
    }

    @Override
    public List<String> geIdListBySkipQueId(String skipQuestionId,String paperId){
        return baseMapper.geIdListBySkipQueId(skipQuestionId,paperId);
    }

    @Override
    public int updateLastSkipByQue(String skipQuestionId, String questionId, String paperId) {
        return baseMapper.updateLastSkipByQue(skipQuestionId,questionId,paperId);
    }

    @Override
    public int updateLastSkip(String skipQuestionId, List<String> ids){
        return baseMapper.updateLastSkip(skipQuestionId,ids);
    }

    /**
     *  跳转问题为A 且 非选项跳转的列表 跳转问题问题为A 的改为B-
     * @param  questionId skipQuestionId paperId
     * @return
     */
    @Override
    public int updateSkipQues(String questionId, List<String> ids){
        return baseMapper.updateSkipQues(questionId,ids);
    }

    /**
     * 根据试卷ID获取逻辑跳转列表
     * @param paperId
     * @return
     */
    @Override
    public List<PaperLogical> getList(String paperId){
        List<PaperLogical> list =  baseMapper.selectList(new QueryWrapper<PaperLogical>().eq("paper_id",paperId).eq("skip","是")
                .orderBy(true,true,"sort").ne("is_many_skip","是"));
        return list;
    }

    /**
     * 根据试卷ID获取多题逻辑跳转列表
     * @param paperId
     * @return
     */
    @Override
    public  Map<String,List<PaperLogical>> getManyList(String paperId) {
        List<PaperLogical> list =  baseMapper.selectList(new QueryWrapper<PaperLogical>().eq("paper_id",paperId).eq("skip","是")
                .orderBy(true,true,"sort").eq("is_many_skip","是"));
        Map<String,List<PaperLogical>> map = list.stream().collect(Collectors.toMap(PaperLogical::getSkipQuestionId, pl ->
                Lists.newArrayList(pl),(List<PaperLogical> newValueList,List<PaperLogical> oldValueList)->
                {oldValueList.addAll(newValueList);
                    return oldValueList;
                })
        );
        return map;
    }
}
