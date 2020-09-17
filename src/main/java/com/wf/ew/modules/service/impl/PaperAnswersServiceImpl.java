package com.wf.ew.modules.service.impl;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.common.utils.CommonUtils;
import com.wf.ew.common.utils.JSONUtil;
import com.wf.ew.common.utils.ThreadLocalDateUtil;
import com.wf.ew.modules.dao.PaperAnswersMapper;
import com.wf.ew.modules.dao.PaperRecycleMapper;
import com.wf.ew.modules.model.*;
import com.wf.ew.modules.service.AnswerDetailService;
import com.wf.ew.modules.service.PaperAnswersService;
import com.wf.ew.modules.service.PaperInfoService;
import com.wf.ew.modules.service.PaperRecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class PaperAnswersServiceImpl extends ServiceImpl<PaperAnswersMapper, PaperAnswers> implements PaperAnswersService {

    @Autowired
    private AnswerDetailService answerDetailService;
    @Autowired
    private PaperInfoService paperInfoService;
    @Autowired
    private PaperRecycleService paperRecycleService;
    @Autowired
    private ThreadLocalDateUtil threadLocalDateUtil;
    /**
     * 查询常用设备统计图表数据
     * @param paperId
     * @return
     */
    public List<PaperAnswers> findEquipmentChartData(String paperId){
        return baseMapper.findEquipmentChartData(paperId);
    }

    /**
     * 查询操作系统统计图表数据
     * @param paperId
     * @return
     */
    public List<PaperAnswers> findSystemChartData(String paperId){
        return baseMapper.findSystemChartData(paperId);
    }

    /**
     * 查询来源统计图表数据
     * @param paperId
     * @return
     */
    public List<PaperAnswers> findSourceChartData(String paperId){
        return baseMapper.findSourceChartData(paperId);
    }
    /**
     * 查询回收数据统计显示列表接口
     * @param page
     * @param paperAnswers
     * @return
     */
    public Page<PaperAnswers> findRecycleTongJiData(Page<PaperAnswers> page, PaperAnswers paperAnswers){
        return page.setRecords(baseMapper.findRecycleTongJiData(page, paperAnswers));
    }
    /**
     * 查询数据库里没有转换的ip
     * @return
     */
    public List<String> getDistinctUncovertedIps(){
        return  baseMapper.getDistinctUncovertedIps();
    }

    @Override
    public int updateByIp(String ipUrl, String areaId) {
        return baseMapper.updateByIp(ipUrl,areaId);
    }
    /**
     * 批量修改IP对应的area_id
     * @return
     */
    public boolean batchUpdateLocations(List<PaperAnswers> list) {
        if(list.size()==0){
            return false;
        }
        int i=0;
       for (PaperAnswers p:list) {
             baseMapper.updateByIp(p.getIpUrl(),p.getAreaId());
             i++;
       }
       return i>0;
    }
    /**
     * 回收概况页面回答的地域数量接口
     * @param paperId
     * @param answersPage
     * @return
     */
   public  List<PaperAnswers> findAnswersByAreaCount(String paperId, Page<PaperAnswers> answersPage){
        return baseMapper.findAnswersByAreaCount(paperId,answersPage);
   }



    /**
     * 根据paperAnsweId获取问题及答案
     * @param id
     * @return
     */
    @Override
    public List<QuestionInfo> findAnswerById(String id){
        return baseMapper.findAnswerById(id);
    }



    @Override
    @Transactional(rollbackFor=Exception.class)
    public Boolean saveAnswer(PaperAnswers paperAnswers, String startTimeStr, String json){
        boolean isResult=false;
        if(null!=paperAnswers&&null!=paperAnswers.getPaperId()){
                /* int count=paperAnswersService.count(new QueryWrapper<PaperAnswers>().eq("paper_id",paperAnswers.getPaperId()).eq("ip_url",paperAnswers.getIpUrl()));
                if(count>0){
                    return JsonResult.error("该ip用户已经提交过该试卷了！");
                }*/
                PaperInfo paperInfo=paperInfoService.getById(paperAnswers.getPaperId());

                //试卷正在回收中且未在废纸篓里才可以提交(IP定时任务统一转areaId)
                if(null!=paperInfo&&paperInfo.getStatus().equals("1")&&paperInfo.getDelFlag().equals("0")){
                    paperAnswers.setEndTime(new Date());
                    paperAnswers.setDelFlag("0");
                    paperAnswers.setCreateTime(new Date());
                    paperAnswers.setStartTime(threadLocalDateUtil.parse(startTimeStr));
                    paperAnswers.setIsInvalid("0");//默认有效数据
                    int isSave=baseMapper.insert(paperAnswers);
                    if(isSave>0){
                        String paId=paperAnswers.getId();
                        if(null!=json&&json.length()>0){
                            List<AnswerDetail> detailList = JSONUtil.parseArray(json, AnswerDetail.class);
                            if(null!=detailList&&detailList.size()>0){
                                for (AnswerDetail d:detailList) {
                                    d.setPaperAnswerId(paId);
                                    d.setDelFlag("0");
                                }
                            }
                            answerDetailService.saveBatch(detailList);
                        }

                        //新增回收量(每提交一次问卷，回收表新增一条数据，浏览量定时任务更新)
                        PaperRecycle pr=new PaperRecycle();
                        pr.setId(IdWorker.getIdStr());
                        pr.setRecycled(1L);
                        pr.setPaperId(paperAnswers.getPaperId());
                        pr.setRecycleTime(new Date());
                        pr.setPaperAnswerId(paperAnswers.getId());
                        pr.setDelFlag("0");
                        paperRecycleService.save(pr);
                        isResult=true;
                    }
                }
        }
        return isResult;
    }

    /**
     * 问卷答案设置为无效
     * @param id
     * @return
     */
    public int updateInvalidStatus(String id,String invalidCause){
        return  baseMapper.updateInvalidStatus(id,invalidCause);
    }
    /**
     * 问卷答案批量设置为无效
     * @param ids
     * @return
     */
    public int updateInvalidStatusBatch(List<String> ids,String invalidCause){
        int i=0;
        for (String id: ids) {
            baseMapper.updateInvalidStatus(id,invalidCause);
            i++;
        }
        return  i;
    }
    /**
     * 查询答题时间超过1小时（60分钟）的答案
     * @return
     */
    public  List<String> findOverTimeOfAnswer(){
     return baseMapper.findOverTimeOfAnswer();
    }
    /**
     * 查询同一个ip地址，同一个问卷在一小时内的答案超过100份的答案
     * @return
     */
    public  List<String> findSameIpOverCount(){
        return baseMapper.findSameIpOverCount();
    }

}
