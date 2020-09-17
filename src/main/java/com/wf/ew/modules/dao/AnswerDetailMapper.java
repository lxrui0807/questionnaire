package com.wf.ew.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.modules.model.AnswerDetail;
import com.wf.ew.modules.model.PaperAnswers;

import java.util.Date;
import java.util.List;

/**
 * 答案详细表，DAO接口
 */
public interface AnswerDetailMapper extends BaseMapper<AnswerDetail> {
    /**
     * 根据问题id查询统计答案的数量
     * @param questionId
     * @return
     */
    List<AnswerDetail> getAnswerCount(String questionId, Date startDate, Date endDate, String areaId, String source, Integer value,String op,String ipUrl);

    /**
     * 查询这份试卷每个人的每个问题的答案列表
     * @param paperId
     * @return
     */
    List<AnswerDetail>getAnswersByUpdateBy(String paperId);
    /**
     * 查询当前时间以前的或者createTime至当前时间之间的文本题答案
     * @param createTime
     * @return
     */
    public List<AnswerDetail> findTextAreaOfAnswer(String createTime);
}
