package com.wf.ew.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.common.JsonResult;
import com.wf.ew.modules.dao.PaperInfoMapper;
import com.wf.ew.modules.dao.PaperLogicalMapper;
import com.wf.ew.modules.dao.PaperQuestionMapper;
import com.wf.ew.modules.dao.QuestionInfoMapper;
import com.wf.ew.modules.model.PaperInfo;
import com.wf.ew.modules.model.PaperLogical;
import com.wf.ew.modules.model.PaperQuestion;
import com.wf.ew.modules.model.QuestionInfo;
import com.wf.ew.modules.service.PaperInfoService;
import com.wf.ew.modules.service.PaperLogicalService;
import com.wf.ew.modules.service.QuestionInfoService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 问卷信息表，Service接口的实现类
 */
@Service
public class PaperInfoServiceImpl extends ServiceImpl<PaperInfoMapper, PaperInfo> implements PaperInfoService {

    @Autowired
    private QuestionInfoService questionInfoService;




    /**
     * 查看问卷和问题信息
     * @param id
     * @return
     */
    @Override
    public Map<String,Object> viewPaper(String id){
        Map<String, Object> map = new HashMap<>();
        if (id != null) {
            PaperInfo p =  baseMapper.selectById(id);
            map.put("paper",p);
            if (p != null) {
                List<QuestionInfo> list = questionInfoService.findListByPaperId(id);
                if(null!=list&&list.size()>0){
                    for (QuestionInfo q:list) {
                        String ops=q.getOptiones();
                        if(q.getType().equals("cascade")){
                            JSONArray jsonArr = JSONArray.fromObject(ops);//转换成JSONArray 格式
                            q.setJsonArray(jsonArr);
                        }else{
                            if(null!=ops&&ops.length()>0){
                                List<String> options= Arrays.asList(ops.split(","));
                                q.setOptionArray(options);
                            }
                        }

                    }
                }
                map.put("list",list);
                return  map;
            }
        }
        return null;
    }
    /**
     * 查询某用户的所有(非废纸篓)问卷
     * @param paperPage
     * @param wrapper
     * @return
     */
    @Override
    public List<PaperInfo>  findListByUserId(Page<PaperInfo> paperPage, QueryWrapper<PaperInfo> wrapper) {
            List<PaperInfo>  listByUserId = baseMapper.findListByUserId(paperPage,wrapper);
            return listByUserId;
    }

    /**
     * 查询某用户的所有(废纸篓)问卷
     * @param userId
     * @return
     */
    @Override
    public List<PaperInfo> findListByUserIdAtRecycleBin(Page<PaperInfo> paperPage,String userId) {
            List<PaperInfo> listByUserIdAtRecycleBin = baseMapper.findListByUserIdAtRecycleBin(paperPage,userId);
            return listByUserIdAtRecycleBin;
    }

    /**
     * 开始回收/暂停回收
     * @param paperInfo
     * @return
     */
    @Override
    public boolean updateRecycleStatus(PaperInfo paperInfo) {
        if(paperInfo != null && !"".equals(paperInfo.getId())){
            int i = baseMapper.updateRecycleStatus(paperInfo);
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
     * 移动到废纸篓
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
     * 恢复
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
     * 彻底删除
     * @param id
     * @return
     */
    @Override
    public boolean delete(String id) {
        if(id != null && !"".equals(id)){
            int i = baseMapper.delete(id);
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
     * 查询模板列表
     * @param paperPage
     * @param title
     * @return
     */
    @Override
    public List<PaperInfo> findTemplateClassify(Page<PaperInfo> paperPage, String title) {
            List<PaperInfo> list = baseMapper.findTemplateClassify(paperPage,title);
            return list;
    }


}
