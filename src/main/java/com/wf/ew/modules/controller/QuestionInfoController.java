package com.wf.ew.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.modules.model.*;
import com.wf.ew.modules.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.*;

@Api(value = "问题管理", tags = "questionInfo")
@RestController
@RequestMapping("${api.version}/questionInfo")
public class QuestionInfoController extends BaseController {

    @Autowired
    private QuestionInfoService questionInfoService;
    @Autowired
    private PaperInfoService paperInfoService;
    @Autowired
    private PaperQuestionService qaperQuestionService;
    @Autowired
    private QuestionCollectService questionCollectService;
    @Autowired
    private PaperLogicalService paperLogicalService;

    /**
     * 根据问卷的ID查询该问卷所有的问题
     * @param paperId  sort大于该顺序的列表
     * @return
     */
    @RequiresPermissions("get:/v1/questionInfo")
    @ApiOperation(value = "根据问卷的ID查询该问卷所有的问题")
    @GetMapping("/{paperId}")
    public JsonResult findListByPaperId(@PathVariable("paperId") String paperId){
        List<QuestionInfo> data = questionInfoService.findListByPaperId(paperId);
        return JsonResult.ok().put("data",data);
    }


    /**
     * 根据问卷的ID查询该问卷所有的问题
     * @param paperId  sort大于该顺序的列表
     * @return
     */
    //@RequiresPermissions("get:/v1/questionInfo/getListBysort")
    @ApiOperation(value = "根据问卷的ID查询该问卷后面的问题")
    @GetMapping("/getListBysort")
    public JsonResult getListBysort(String paperId,String questionId){
        Map<String, Object> map = Maps.newHashMap();
        String sort=null;
        List<PaperLogical> list= paperLogicalService.list(new QueryWrapper<PaperLogical>().eq("question_id",questionId).eq("paper_id",paperId).eq("skip","否"));
        if(null!=list&&list.size()>0){
            sort=list.get(0).getSort().toString();
        }
        List<QuestionInfo> quelist = questionInfoService.findLargeListByPaperId(paperId,sort);
        List<PaperLogical> logicallist= paperLogicalService.list(new QueryWrapper<PaperLogical>().eq("question_id",questionId)
                .eq("paper_id",paperId).eq("skip","是").ne("is_many_skip","是"));
        Integer maxsort=paperLogicalService.findMaxSortByPaperId(paperId);
        map.put("quelist",quelist);
        map.put("logicallist",logicallist);
        map.put("maxsort",maxsort==null?1:(maxsort+1));

        return JsonResult.ok().put("data",map);
    }





    /**
     * 多题跳转根据问题ID获取该问题的选择问题列表、多题逻辑跳转列表
     * @param paperId  (删除有单项逻辑跳转的问题选项)
     * @return
     */
    //@RequiresPermissions("get:/v1/questionInfo/getManyListBysort")
    @ApiOperation(value = "多题跳转根据问题ID获取该问题的选择问题列表和多题逻辑跳转列表")
    @GetMapping("/getManyListBysort")
    public JsonResult getManyListBysort(String paperId,String questionId){
        Map<String, Object> map = Maps.newHashMap();
        Integer sort= paperLogicalService.getSortByQueId(questionId,paperId);
        List<QuestionInfo> quelist = questionInfoService.findSmallListByPaperId(paperId,sort==null?null:sort.toString());
        Iterator<QuestionInfo> itQuelist=quelist.iterator();

        while(itQuelist.hasNext()){
                QuestionInfo que=itQuelist.next();
                List<String> skipops=paperLogicalService.getSkipOps(que.getId(),paperId,"否");
                if(null!=skipops&&skipops.size()>0){
                    List<String> noskipops=new ArrayList<>();
                    String optiones=que.getOptiones();
                    if(null!=optiones&&optiones.length()>0){
                        String[] ops=optiones.split(",");
                        if(skipops.size()==ops.length){
                            itQuelist.remove();
                        }else{
                            for (int i = 0; i < ops.length; i++) {
                                if(!skipops.contains(ops[i])){
                                    noskipops.add(ops[i]);
                                }
                            }
                        }
                    }
                    if(null!=noskipops&&noskipops.size()>0){
                        String opstr=StringUtils.join(noskipops.toArray(), ",");
                        que.setOptiones(opstr);
                    }
                }
        }
        Integer maxsort=paperLogicalService.findMaxSortByPaperId(paperId);
        List<PaperLogical> logicallist= paperLogicalService.list(new QueryWrapper<PaperLogical>().eq("skip_question_id",questionId).eq("paper_id",paperId).eq("skip","是").eq("is_many_skip","是"));
        map.put("quelist",quelist);
        map.put("logicallist",logicallist);
        map.put("maxsort",maxsort==null?1:(maxsort+1));
        return JsonResult.ok().put("data",map);
    }



    /**
     * 根据问题ID,查看问题逻辑跳转选项详情
     * @param questionId ,paperId
     * @return
     */
    //@RequiresPermissions("get:/v1/questionInfo/getLogicalOps")
    @ApiOperation(value = "根据问题ID,查看问题详情")
    @GetMapping("getLogicalOps")
    public JsonResult getLogicalOps(String questionId,String paperId){
        QuestionInfo questionInfo = questionInfoService.getById(questionId);
        List<String> noskipops = null;
        String[] ops =null;
        String optiones = questionInfo.getOptiones();
        if (null != optiones && optiones.length() > 0) {
            ops = optiones.split(",");
            noskipops = new ArrayList<>(Arrays.asList(ops));
        }
        List<String> skipops=paperLogicalService.getSkipOps(questionId,paperId,"是");
        if(null!=skipops&&skipops.size()>0) {
                if (skipops.size() != ops.length) {
                    for (int i = 0; i < skipops.size(); i++) {
                        String tt=skipops.get(i);
                        noskipops.removeIf(s -> s.equals(tt));
                    }
               }
        }
        return JsonResult.ok().put("data",questionInfo).put("noskipops",noskipops);
    }

    /**
     * 保存问卷
     * @param questionInfo
     * @return
     */
    @ApiOperation(value = "保存问卷中的问题")
    @PostMapping("savePaperQuestion")
    @Transactional
    public JsonResult savePaperQuestion(QuestionInfo questionInfo,String paperId,String paperTitle,String paperSubTitle,String templateClassify,HttpServletRequest request){
        boolean isEdit=false;
        boolean isResult=false;
        //  获取当前登录的userId
        String loginUserId = UserUtil.getLoginUserId(request);
        if(null!=questionInfo){
            try{
                PaperInfo edPaperInfo=null;
                if(null!=paperId&&paperId.length()>0){ //编辑
                    isEdit=true;
                    edPaperInfo=paperInfoService.getById(paperId);
                }else{//新增
                    edPaperInfo=new PaperInfo();
                    edPaperInfo.setTitle(paperTitle);
                    edPaperInfo.setSubtitle(paperSubTitle);
                    edPaperInfo.setStatus("0");
                    edPaperInfo.setUserId(loginUserId);
                    edPaperInfo.setTemplateClassify(templateClassify);
                    paperInfoService.save(edPaperInfo);
                }
                    paperId=edPaperInfo.getId();

                if(questionInfo.getId()!= null&&questionInfo.getId().length()>0){  //修改问题内容，删除多余选项的逻辑跳转
                    if (null != questionInfo && questionInfo.getId() != null) {
                        QuestionInfo old = questionInfoService.getById(questionInfo.getId());
                        String required = questionInfo.getRemarks();
                        old.setTitle(questionInfo.getTitle());
                        old.setSubtitle(questionInfo.getSubtitle());
                        old.setOptiones(questionInfo.getOptiones());
                        old.setStarType(questionInfo.getStarType());
                        old.setCascadeTitle(questionInfo.getCascadeTitle());
                        old.setCascadeText(questionInfo.getCascadeText());
                        old.setRemarks(null);
                        old.setUpdateBy(loginUserId);
                        old.setUpdateTime(new Date());
                        boolean isFlag = questionInfoService.updateById(old);
                        if (isFlag) {
                            PaperQuestion pq = qaperQuestionService.getOne(new QueryWrapper<PaperQuestion>().eq("paper_id", paperId).eq("question_id", questionInfo.getId()));
                            pq.setRequired(required);
                            qaperQuestionService.updateById(pq);
                            if(null!=questionInfo.getOptiones()&&questionInfo.getOptiones().length()>0){
                                List<String> ops= Arrays.asList(questionInfo.getOptiones().split(","));
                                List<PaperLogical> logicllist=paperLogicalService.list(new QueryWrapper<PaperLogical>().eq("question_id",questionInfo.getId()).eq("paper_id",paperId).eq("skip","是"));//删除逻辑跳转
                                if(null!=logicllist&&logicllist.size()>0){
                                    for (PaperLogical pl:logicllist) {
                                        if(null!=pl&&null!=pl.getSkipValue()&&!ops.contains(pl.getSkipValue())){
                                            paperLogicalService.removeById(pl.getId());
                                        }else{
                                            continue;
                                        }
                                    }
                                }
                                isResult=true;
                            }
                        }
                    }
                }else{
                    String required = questionInfo.getRemarks();
                    questionInfo.setQuestionBank("否");
                    questionInfo.setRemarks(null);
                    questionInfo.setDelFlag("0");
                    questionInfo.setCreateBy(loginUserId);
                    questionInfo.setCreateTime(new Date());
                    boolean isFlag=questionInfoService.save(questionInfo);//题库里的问题重新复制一份，默认非题库
                    String queId=questionInfo.getId();
                    if(isFlag){
                        PaperQuestion pq=new PaperQuestion();
                        pq.setPaperId(paperId);
                        pq.setQuestionId(queId);
                        pq.setRequired(required);
                        qaperQuestionService.save(pq);
                    }
                    if (isEdit){ //编辑逻辑保存 (保存新问题默认逻辑，修改上个节点的跳转问题id)
                        Integer sort=paperLogicalService.findMaxSortByPaperId(paperId);
                        PaperLogical lastPl= paperLogicalService.getLastBysort(paperId);
                        if(null!=lastPl){
                            lastPl.setSkipQuestionId(queId);
                            lastPl.setUpdateBy(loginUserId);
                            lastPl.setUpdateTime(new Date());
                            paperLogicalService.updateById(lastPl);
                        }
                        PaperLogical pl=new PaperLogical();
                        pl.setPaperId(paperId);
                        pl.setQuestionId(queId);
                        pl.setSkip("否");
                        pl.setSort(sort==null?1:(sort+1));
                        pl.setCreateBy(loginUserId);
                        pl.setCreateTime(new Date());
                        pl.setIsManySkip("否");
                        paperLogicalService.save(pl);
                    }else{
                        PaperLogical pl=new PaperLogical();
                        pl.setPaperId(paperId);
                        pl.setQuestionId(queId);
                        pl.setSkip("否");
                        pl.setSort(1);
                        pl.setCreateBy(loginUserId);
                        pl.setCreateTime(new Date());
                        pl.setIsManySkip("否");
                        paperLogicalService.save(pl);
                    }
                }

                    isResult=true;
            }catch (Exception e) {
                //强制手动事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        if(isResult){
            return JsonResult.ok("保存问卷问题成功").put("data",paperId);
        }else {
            return JsonResult.error("保存问卷问题失败");
        }
    }


    /**
     * 使用模版选中的保存问卷 --重新复制出一份问卷及问题列表
     * @param paperId
     * @return
     */
    @ApiOperation(value = "保存模版问卷中的问题")
    @PostMapping("saveModelPaperQuestion")
    @Transactional
    public JsonResult saveModelPaperQuestion(String  paperId,HttpServletRequest request){
        String newPapaerId=null;
        boolean isResult=false;
        //  获取当前登录的userId
        String loginUserId = UserUtil.getLoginUserId(request);
            try{
                if(null!=paperId&&paperId.length()>0){
                    PaperInfo edPaperInfo=paperInfoService.getById(paperId);
                    if(null!=edPaperInfo){
                        PaperInfo paperInfo=new PaperInfo();
                        paperInfo.setTitle(edPaperInfo.getTitle());
                        paperInfo.setSubtitle(edPaperInfo.getSubtitle());
                        paperInfo.setStatus("0");
                        paperInfo.setUserId(loginUserId);
                        paperInfo.setTemplateClassify("否");
                        boolean isSave=paperInfoService.save(paperInfo);
                        if(isSave){
                            newPapaerId=paperInfo.getId();
                            List<QuestionInfo> questionInfoList = questionInfoService.findListByPaperId(paperId);
                            if(null!=questionInfoList&&questionInfoList.size()>0){
                                List<String> queIds=new ArrayList<String>();
                                for (QuestionInfo q:questionInfoList) {
                                    if(null!=q){
                                        QuestionInfo newque=new QuestionInfo();
                                        newque.setTitle(q.getTitle());
                                        newque.setSubtitle(q.getSubtitle());
                                        newque.setOptiones(q.getOptiones());
                                        newque.setType(q.getType());
                                        newque.setStarType(q.getStarType());
                                        newque.setCascadeTitle(q.getCascadeTitle());
                                        newque.setCascadeText(q.getCascadeText());
                                        newque.setCreateTime(new Date());
                                        newque.setCreateBy(loginUserId);
                                        newque.setQuestionBank("否");
                                        newque.setDelFlag("0");
                                        boolean isFlag=questionInfoService.save(newque);//模版里的问题重新复制一份
                                        if(isFlag){
                                            queIds.add(newque.getId());
                                            PaperQuestion pq = qaperQuestionService.getOne(new QueryWrapper<PaperQuestion>()
                                                    .eq("paper_id", paperId).eq("question_id", q.getId()));
                                            PaperQuestion newpq=new PaperQuestion();
                                            newpq.setPaperId(newPapaerId);
                                            newpq.setQuestionId(newque.getId());
                                            newpq.setRequired(pq.getRequired());
                                            qaperQuestionService.save(newpq);
                                        }
                                    }
                                }
                                if(null!=queIds&&queIds.size()>0){
                                    for (int i = 0; i <queIds.size() ; i++) {
                                        PaperLogical pl=new PaperLogical();
                                        pl.setPaperId(newPapaerId);
                                        pl.setQuestionId(queIds.get(i));
                                        pl.setSkip("否");
                                        pl.setSort(i+1);
                                        if(i+1<queIds.size()){
                                            pl.setSkipQuestionId(queIds.get(i+1));
                                        }
                                        pl.setCreateBy(loginUserId);
                                        pl.setCreateTime(new Date());
                                        pl.setIsManySkip("否");
                                        paperLogicalService.save(pl);
                                    }
                                }
                            }
                            isResult=true;
                        }
                    }
                }
            }catch (Exception e) {
                //强制手动事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        if(isResult){
            return JsonResult.ok("保存问卷问题成功").put("data",newPapaerId);
        }else {
            return JsonResult.error("保存问卷问题失败");
        }
    }

    /**
     * 删除 问卷中的问题(删除关联表、逻辑跳转、收藏表)
     * @param paperId,queId
     * @return
     */
    //@RequiresPermissions("put:/v1/questionInfo/delPaperQuestion")
    @ApiOperation(value = "删除问卷中的问题")
    @PutMapping("/delPaperQuestion")
    @Transactional
    public JsonResult delPaperQuestion(String paperId,String queId){
        boolean isResult=false;
        if(null!=paperId&&null!=queId){
            try{
                if(null!=paperId&&paperId.length()>0&&null!=queId&&queId.length()>0){
                    qaperQuestionService.remove(new QueryWrapper<PaperQuestion>().eq("paper_id",paperId).eq("question_id",queId)); //删除问题试卷关联表
                    /*删除逻辑跳转
                    1.查找非选项跳转且跳转问题为A，sort升序排列的上一个问题的Id——B
                    2.跳转问题为A的逻辑列表 所有删除
                    3.问题为A 且 是选项跳转的列表 所有删除
                    4.问题为A 且 非选项跳转的列表 问题为A 的改为B*/
                    String lastQueId=paperLogicalService.getQuestionIdBysort(queId,paperId);
                    paperLogicalService.remove(new QueryWrapper<PaperLogical>().eq("skip_question_id",queId).eq("paper_id",paperId));//删除逻辑跳转
                    paperLogicalService.remove(new QueryWrapper<PaperLogical>().eq("question_id",queId).eq("paper_id",paperId).eq("skip","是"));//删除逻辑跳转
                    paperLogicalService.updateLastSkipByQue(lastQueId,queId,paperId);
                    questionInfoService.removeById(queId); //删除问题
                    isResult=true;
                }

            }catch (Exception e) {
                //强制手动事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        if(isResult){
            return JsonResult.ok("删除成功");
        }else {
            return JsonResult.error("删除失败");
        }
    }


    /**
     *  问卷中的问题上移、下移等
     * @param paperId
     * @param queId 移动问题id
     * @param queTwoId 被移动问题id
     * @param op up/down(上移/下移)
     * @return
     */
    //@RequiresPermissions("post:/v1/questionInfo/changePaperQuestion")
    @ApiOperation(value = "互换问卷中的问题")
    @PostMapping("/changePaperQuestion")
    @Transactional
    public JsonResult changePaperQuestion(String paperId,String queId, String queTwoId,String op){
        boolean isResult=false;
        if(null!=paperId&&paperId.length()>0){
            try{
                String oldId=queId;
                String newId=queTwoId;
                if(op.equals("up")){ //上移
                    oldId=queTwoId;
                    newId=queId;
                }

                //问题为A 且 非选项跳转的列表 问题为A 的改为B
                // 跳转问题为A 且 非选项跳转的列表 跳转问题问题为A 的改为B
                List<String> newidlist=paperLogicalService.geIdListByQueId(newId,paperId);
                List<String> oldidlist=paperLogicalService.geIdListByQueId(oldId,paperId);

                List<String> newskipids=paperLogicalService.geIdListBySkipQueId(newId,paperId);
                List<String> oldskipids=paperLogicalService.geIdListBySkipQueId(oldId,paperId);

                paperLogicalService.updateLastSkip(newId,oldidlist);
                paperLogicalService.updateLastSkip(oldId,newidlist);

                if(null!=oldskipids&&oldskipids.size()>0){
                    paperLogicalService.updateSkipQues(newId,oldskipids);
                }
                if(null!=newskipids&&newskipids.size()>0) {
                    paperLogicalService.updateSkipQues(oldId, newskipids);
                }
                paperLogicalService.remove(new QueryWrapper<PaperLogical>().eq("skip_question_id",newId).eq("paper_id",paperId).eq("skip","是"));//删除逻辑跳转
                isResult=true;

            }catch (Exception e) {
                //强制手动事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        if(isResult){
            return JsonResult.ok("操作成功");
        }else {
            return JsonResult.error("操作失败");
        }
    }


   // @RequiresPermissions("get:/v1/questionInfo")
    @ApiOperation(value = "查询题库列表信息")
    @GetMapping("/findQuestionBankList")
    public JsonResult findQuestionBankList(){
        Map<String, List<QuestionInfo>> map=new HashMap<String, List<QuestionInfo>>();
        List<String> typeList = questionInfoService.findQuestionBanks();
        if(null!=typeList&&typeList.size()>0){
            for (String type:typeList) {
                List<QuestionInfo> data = questionInfoService.findQuestionBankList(type);
                map.put(type,data);
            }
        }
        return JsonResult.ok().put("data",map);
    }
    /**
     * 查询某用户收藏题目列表
     */
    // @RequiresPermissions("get:/v1/questionInfo/findQuestionCollect")
    @ApiOperation(value = "查询某用户收藏题目列表")
    @GetMapping("/findQuestionCollect")
    public JsonResult findQuestionCollect(HttpServletRequest request){
        String userId=getLoginUserId(request);
        List<QuestionInfo> data= questionInfoService.findQuestionCollect(userId);
        return JsonResult.ok().put("data",data);
    }
}
