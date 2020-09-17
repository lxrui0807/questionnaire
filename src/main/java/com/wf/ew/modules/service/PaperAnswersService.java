package com.wf.ew.modules.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.modules.model.PaperAnswers;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.model.QuestionInfo;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 试卷答案信息表，Service接口
 */

public interface PaperAnswersService extends IService<PaperAnswers> {
    /**
     * 查询常用设备统计图表数据
     * @param paperId
     * @return
     */
    List<PaperAnswers> findEquipmentChartData(String paperId);

    /**
     * 查询操作系统统计图表数据
     * @param paperId
     * @return
     */
    List<PaperAnswers> findSystemChartData(String paperId);

    /**
     * 查询来源统计图表数据
     * @param paperId
     * @return
     */
    List<PaperAnswers> findSourceChartData(String paperId);
    /**
     * 查询回收数据统计显示列表接口
     * @param paperAnswers
     * @param page
     * @return
     */
    Page<PaperAnswers> findRecycleTongJiData(Page<PaperAnswers> page,PaperAnswers paperAnswers);
    /**
     * 查询数据库里没有转换的ip
     * @return
     */
    List<String> getDistinctUncovertedIps();
//    /**
//     * 根据ip修改area_id值
//     * @param pa
//     */
    public int updateByIp (String ipUrl,String areaId);
    /**
     * 批量修改IP对应的area_id
     * @return
     */
    public boolean batchUpdateLocations(List<PaperAnswers> list);
    /**
     * 回收概况页面回答的地域数量接口
     * @param paperId
     * @param answersPage
     * @return
     */
    List<PaperAnswers> findAnswersByAreaCount(String paperId, Page<PaperAnswers> answersPage);


    /**
     * 根据paperAnsweId获取问题及答案
     * @param id
     * @return
     */
    List<QuestionInfo> findAnswerById(String id);

    /**
     * 回答问卷
     * @param paperAnswers
     * @param startTimeStr
     * @param json
     * @return
     */
    public Boolean saveAnswer(PaperAnswers paperAnswers, String startTimeStr, String json);

    /**
     * 问卷答案设置为无效
     * @param id
     * @return
     */
    public int updateInvalidStatus(String id,String invalidCause);
    /**
     * 查询答题时间超过1小时（60分钟）的答案
     * @return
     */
    public  List<String> findOverTimeOfAnswer();
    /**
     * 问卷答案批量设置为无效
     * @param ids
     * @return
     */
    public int updateInvalidStatusBatch(List<String> ids,String invalidCause);
    /**
     * 查询同一个ip地址，同一个问卷在一小时内的答案超过100份的答案
     * @return
     */
    public  List<String> findSameIpOverCount();
}
