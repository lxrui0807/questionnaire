package com.wf.ew.modules.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.modules.dao.AnswerDetailMapper;
import com.wf.ew.modules.model.AnswerDetail;
import com.wf.ew.modules.service.AnswerDetailService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnswerDetailServiceImpl extends ServiceImpl<AnswerDetailMapper, AnswerDetail> implements AnswerDetailService {
    /**
     * 根据问题id查询统计答案的数量
     * @param questionId
     * @return
     */
    public  List<AnswerDetail> getAnswerCount(String questionId, Date startDate, Date endDate, String areaId, String source, Integer value,String op,String ipUrl){
        return baseMapper.getAnswerCount(questionId,startDate,endDate,areaId,source,value,op,ipUrl);
    }
    /**
     * 查询这份试卷每个人的每个问题的答案列表
     * @param paperId
     * @return
     */
    public List<AnswerDetail>getAnswersByUpdateBy(String paperId){
        return baseMapper.getAnswersByUpdateBy(paperId);
    }
    /**
     * 查询当前时间以前的或者createTime至当前时间之间的文本题答案
     * @param createTime
     * @return
     */
    public List<AnswerDetail> findTextAreaOfAnswer(String createTime){
        return baseMapper.findTextAreaOfAnswer(createTime);
    }
}
