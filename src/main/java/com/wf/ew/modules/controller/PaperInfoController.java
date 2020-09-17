package com.wf.ew.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.paperview.PaperView;
import com.wf.ew.common.utils.HttpContextUtils;
import com.wf.ew.common.utils.IPUtils;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.modules.model.*;
import com.wf.ew.modules.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Api(value = "试卷管理", tags = "paperInfo")
@RestController
@RequestMapping("${api.version}/paperInfo")
public class PaperInfoController extends BaseController {

    @Autowired
    private PaperInfoService paperInfoService;
    @Autowired
    private AnsyTaskService ansyTaskService;
    @Autowired
    private PaperRecycleService paperRecycleService;

    private Logger logger = LoggerFactory.getLogger("PaperInfoController");

    @ApiOperation(value = "保存试卷信息")
    @PostMapping("savePaperInfo")
    public JsonResult savePaperInfo(PaperInfo paperInfo,HttpServletRequest request){
        boolean isResult=false;
        String id=null;
        //  获取当前登录的userId
        String loginUserId = UserUtil.getLoginUserId(request);
        if(null!=paperInfo) {
             id = paperInfo.getId();
            PaperInfo edPaperInfo=paperInfo;
            if(null!=id&&id.length()>0){ //编辑
                edPaperInfo=paperInfoService.getById(id);
                edPaperInfo.setTitle(paperInfo.getTitle());
                edPaperInfo.setSubtitle(paperInfo.getSubtitle());
            }else{//新增
                edPaperInfo.setStatus("0");
                edPaperInfo.setUserId(loginUserId);
            }
            isResult=paperInfoService.saveOrUpdate(edPaperInfo);
            id = edPaperInfo.getId();
        }
        if(isResult){
            return JsonResult.ok("保存问卷成功").put("data",id);
        }else {
            return JsonResult.error("保存问卷失败");
        }
    }

    /**
     * 查询某用户的所有(非废纸篓)问卷
     */
   // @RequiresPermissions("get:/v1/paperInfo/nRecycleList")
    @ApiOperation(value = "查询某用户的所有(非废纸篓)问卷")
    @GetMapping("/nRecycleList")
    public PageResult<PaperInfo> findListByUserId(HttpServletRequest request,Integer page, Integer limit,String title,String status){
      /*  logger.debug("主方法执行了......");
        System.out.println("主方法执行了......");
*/
        if(page==null){
             page=0;
        }
        if(limit==null){
            limit=10;
        }
        Page<PaperInfo> paperPage = new Page<>(page, limit);
        QueryWrapper<PaperInfo> wrapper = new QueryWrapper<>();
         if(title!=null && !title.trim().isEmpty()){
             wrapper.like("title",title);
         }
        if(status!=null && !status.trim().isEmpty()){
            wrapper.eq("status",status);
        }
        wrapper.eq("del_flag","0");
        wrapper.ne("template_classify","是");
        boolean hasAdmin=UserUtil.isPermission("1",request);
        if(!hasAdmin){
            String userId=getLoginUserId(request);
            wrapper.eq("user_id",userId);
        }
        wrapper.orderByDesc("status").orderByDesc("create_time");
        List<PaperInfo>  data= paperInfoService.findListByUserId(paperPage,wrapper);
        if(null!=data&&data.size()>0){
            for (PaperInfo p: data) {
                PaperRecycle pr=paperRecycleService.findRecycledSum(p.getId());
                if(null!=pr){
                    p.setRemarks(pr.getRecycled()==null?"0":pr.getRecycled().toString());
                }
            }
        }
        /*logger.debug("主方法执行结束......");
        System.out.println("主方法执执行结束.......");*/
        return new PageResult<PaperInfo>(data,paperPage.getTotal());
    }

    /**
     * 查询某用户的所有(废纸篓)问卷
     */
   // @RequiresPermissions("get:/v1/paperInfo/recycle")
    @ApiOperation(value = "查询某用户的所有(废纸篓)问卷")
    @GetMapping("/recycle/list")
    public PageResult<PaperInfo> findListByUserIdAtRecycleBin(HttpServletRequest request,Integer page, Integer limit){
        if(page==null){
            page=0;
        }
        if(limit==null){
            limit=10;
        }
        Page<PaperInfo> paperPage = new Page<>(page, limit);
        String userId=null;
        boolean hasAdmin=UserUtil.isPermission("1",request);
        if(!hasAdmin){
            userId=getLoginUserId(request);
        }
        List<PaperInfo> data = paperInfoService.findListByUserIdAtRecycleBin(paperPage,userId);
        if(null!=data&&data.size()>0){
            for (PaperInfo p: data) {
                PaperRecycle pr=paperRecycleService.findRecycledSum(p.getId());
                if(null!=pr){
                    p.setRemarks(pr.getRecycled()==null?"0":pr.getRecycled().toString());
                }
            }
        }
        return new PageResult<PaperInfo>(data,paperPage.getTotal());
    }


    /**
     * 查看问卷及该问卷的所有问题
     */
    //@RequiresPermissions("get:/v1/paperInfo/getView")
    @ApiOperation(value = "查看问卷信息")
    @GetMapping("/getView/{id}")
    public JsonResult getPaperInfo(@PathVariable("id") String id){
        PaperInfo paperInfo = paperInfoService.getById(id);
        return JsonResult.ok().put("data",paperInfo);
    }

    /**
     * 查看问卷及该问卷的所有问题
     */
    //@RequiresPermissions("get:/v1/paperInfo")
    /*@ApiOperation(value = "查看问卷及该问卷的所有问题")
    @GetMapping("/{id}")
    public JsonResult view(@PathVariable("id") String id){

        Map<String, Object> map = new HashMap<>();

        PaperInfo paperInfo = paperInfoService.getById(id);
        map.put("paperInfo",paperInfo);

        if(paperInfo != null && !"".equals(paperInfo.getId())){
            List<QuestionInfo> listByPaperId = questionInfoService.findListByPaperId(paperInfo.getId());
            map.put("listByPaperId",listByPaperId);
        }

        return JsonResult.ok().put("data",map);
    }*/


    /**
     * 开始回收/暂停回收
     * @param paperInfo
     * @return
     */
   // @RequiresPermissions("get:/v1/paperInfo")
    @ApiOperation(value = "开始回收/暂停回收")
    @PostMapping("/updateRecycleStatus")
    public JsonResult updateRecycleStatus(PaperInfo paperInfo){
        if(paperInfoService.updateRecycleStatus(paperInfo)){
            return JsonResult.ok("开始回收/暂停回收成功");
        }else {
            return JsonResult.error("开始回收/暂停回收失败");
        }
    }

    /**
     * 创建空白/模板问卷
     */
    @RequiresPermissions("post:/v1/paperInfo")
    @ApiOperation(value = "创建空白/模板问卷")
    @PostMapping()
    public JsonResult add(PaperInfo paperInfo){
        if(paperInfoService.save(paperInfo)){
            return JsonResult.ok("创建空白/模板问卷成功");
        }else {
            return JsonResult.error("创建空白/模板问卷失败");
        }
    }

    /**
     * 移动到废纸篓
     */
   // @RequiresPermissions("put:/v1/paperInfo/move")
    @ApiOperation(value = "移动到废纸篓")
    @PostMapping("/move/{id}")
    public JsonResult move(@PathVariable("id") String id){
        if(paperInfoService.move(id)){
            return JsonResult.ok("移动到废纸篓成功");
        }else {
            return JsonResult.error("移动到废纸篓失败");
        }
    }

    /**
     * 恢复
     */
   // @RequiresPermissions("put:/v1/paperInfo/recovery")
    @ApiOperation(value = "恢复")
    @PostMapping("/recovery/{id}")
    public JsonResult recovery(@PathVariable("id") String id){
        if(paperInfoService.recovery(id)){
            return JsonResult.ok("恢复成功");
        }else {
            return JsonResult.error("恢复失败");
        }
    }

    /**
     * 彻底删除
     */
    //@RequiresPermissions("delete:/v1/paperInfo/{id}")
    @ApiOperation(value = "彻底删除")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String id){
        if(paperInfoService.delete(id)){
            return JsonResult.ok("彻底删除成功");
        }else {
            return JsonResult.error("彻底删除失败");
        }
    }

    /**
     *查询每个试卷对应的问题数量，作为首页的推荐列表(模板列表查询接口)
     * @return
     */
    // @RequiresPermissions("get:/v1/paperInfo/findTemplateClassify")
    @ApiOperation(value = "查询每个试卷对应的问题数量，作为首页的推荐列表")
    @GetMapping("/findTemplateClassify")
    public PageResult<PaperInfo> findTemplateClassify(Integer page, Integer limit,String title){
        if(page==null){
            page=0;
        }
        if(limit==null){
            limit=10;
        }
        Page<PaperInfo> paperPage = new Page<>(page, limit);
        List<PaperInfo> data= paperInfoService.findTemplateClassify(paperPage,title);
        return new PageResult<PaperInfo>(data,paperPage.getTotal());
    }

    /**
     * 查看问卷页面的问题数据接口
     * @param id
     * @return
     */
    @ApiOperation(value = "查看问卷页面的问题数据接口")
    @GetMapping("/viewPaper")
    public JsonResult viewPaper(String id) {
        Map<String, Object> map = paperInfoService.viewPaper(id);
        if(null!=map){
            return  JsonResult.ok().put("data",map);
        }else{
            return  JsonResult.error("您要查看的试卷不存在");
        }
    }


    @PaperView
    @ApiOperation(value = "记录用户浏览量")
    @GetMapping("/countView")
    public JsonResult countView(String paperId,String ipUrl){
        /*javax.servlet.http.HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddr(request);
        int count=paperAnswersService.count(new QueryWrapper<PaperAnswers>().eq("paper_id",paperId).eq("ip_url",ipUrl));*/
        CompletableFuture<JsonResult> future=ansyTaskService.fillPaperView(paperId);
        try {
            JsonResult jr=future.get();
            return  jr;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return  JsonResult.error("连接超时");
    }
}
