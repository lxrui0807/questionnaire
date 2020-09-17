package com.wf.ew.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.common.utils.HttpContextUtils;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.modules.dao.PaperLogicalMapper;
import com.wf.ew.modules.dao.PaperQuestionMapper;
import com.wf.ew.modules.dao.QuestionInfoMapper;
import com.wf.ew.modules.model.PaperLogical;
import com.wf.ew.modules.model.PaperQuestion;
import com.wf.ew.modules.model.QuestionInfo;
import com.wf.ew.modules.service.QuestionInfoService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class QuestionInfoServiceImpl extends ServiceImpl<QuestionInfoMapper, QuestionInfo> implements QuestionInfoService{

    /**
     * 根据问卷的ID，查询该问卷所有的问题
     * @param paperId
     * @return
     */
    @Override
    public List<QuestionInfo> findListByPaperId(String paperId) {
        if(paperId != null && !"".equals(paperId)){
            List<QuestionInfo> listByPaperId = baseMapper.findListByPaperId(paperId,null,null);
            return listByPaperId;
        }else {
            return null;
        }
    }

    /**
     * 根据问卷的ID，查询该问卷排序以后所有的问题
     * @param paperId
     * @return
     */
    @Override
    public List<QuestionInfo> findLargeListByPaperId(String paperId,String sort) {
        if(paperId != null && !"".equals(paperId)){
            List<QuestionInfo> listByPaperId = baseMapper.findListByPaperId(paperId,sort,null);
            return listByPaperId;
        }else {
            return null;
        }
    }

    /**
     * 根据问卷的ID，查询该问卷排序以前所有的问题
     * @param paperId
     * @return
     */
    @Override
    public List<QuestionInfo> findSmallListByPaperId(String paperId,String sort) {
        if(paperId != null && !"".equals(paperId)){
            List<QuestionInfo> listByPaperId = baseMapper.findListByPaperId(paperId,sort,"small");
            return listByPaperId;
        }else {
            return null;
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public boolean move(String id) {
        if(id != null && !"".equals(id)){
            int i = baseMapper.move(id);
            if(i == 1){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 撤销
     * @param id
     * @return
     */
    @Override
    public boolean recovery(String id) {
        if(id != null && !"".equals(id)){
            int i = baseMapper.recovery(id);
            if(i == 1){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 查询题库列表
     * @return
     */
    @Override
    public List<QuestionInfo> findQuestionBankList(String questionBankType) {
        List<QuestionInfo> listByPaperId = baseMapper.findQuestionBankList(questionBankType);
        return listByPaperId;
    }

    /**
     * 查询某用户收藏题目列表
     * @param userId
     * @return
     */
    @Override
    public List<QuestionInfo> findQuestionCollect(String userId) {
        List<QuestionInfo> listByPaperId = baseMapper.findQuestionCollect(userId);
        return listByPaperId;
    }

    /**
     * 根据试卷id查找需要删除的问题id list
     * @param paperId
     * @return
     */
    @Override
    public  List<String>  findDelQuestionIds(String paperId){
        return baseMapper.findDelQuestionIds(paperId);
    }


    /**
     * 查询问题题库分类
     * @return
     */
    @Override
    public List<String> findQuestionBanks(){
        return  baseMapper.findQuestionBanks();
    }
}
