package com.wf.ew.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.utils.CommonUtils;
import com.wf.ew.common.utils.JSONUtil;
import com.wf.ew.common.utils.RedisUtil;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.modules.model.*;
import com.wf.ew.modules.service.*;
import com.yuanjing.framework.common.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Api(value = "试卷答案", tags = "paperAnswers")
@RestController
@RequestMapping("${api.version}/paperAnswers")
public class PaperAnswersController extends BaseController {

    @Autowired
    private PaperAnswersService paperAnswersService;
    @Autowired
    private QuestionInfoService questionInfoService;
    @Autowired
    private AnswerDetailService answerDetailService;
    @Autowired
    private PaperRecycleService paperRecycleService;
    @Autowired
    private PaperInfoService paperInfoService;
    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private AnsyTaskService ansyTaskService;


    /**
     * 查询常用设备统计图表数据
     */
    // @RequiresPermissions("get:/v1/paperAnswers/findEquipmentChartData")
    @ApiOperation(value = "查询常用设备统计图表数据")
    @GetMapping("/findEquipmentChartData")
    public JsonResult findEquipmentChartData(String paperId){
        List<PaperAnswers> data= paperAnswersService.findEquipmentChartData(paperId);
        return JsonResult.ok().put("data",data);
    }

    /**
     * 查询来源统计图表数据
     */
    // @RequiresPermissions("get:/v1/paperAnswers/findSourceChartData")
    @ApiOperation(value = "查询来源统计图表数据")
    @GetMapping("/findSourceChartData")
    public JsonResult findSourceChartData(String paperId){
        List<PaperAnswers> data= paperAnswersService.findSourceChartData(paperId);
        return JsonResult.ok().put("data",data);
    }
    /**
     * 查询操作系统统计图表数据
     */
    // @RequiresPermissions("get:/v1/paperAnswers/findSystemChartData")
    @ApiOperation(value = "查询操作系统统计图表数据")
    @GetMapping("/findSystemChartData")
    public JsonResult findSystemChartData(String paperId){
        List<PaperAnswers> data= paperAnswersService.findSystemChartData(paperId);
        return JsonResult.ok().put("data",data);
    }

    /**
     * 统计图表页面问题答案统计数量接口，返回答案
     */
    // @RequiresPermissions("get:/v1/paperAnswers/findAnswerPercentList")
    @ApiOperation(value = "统计图表页面问题答案统计数量接口")
    @GetMapping("/findAnswerPercentList")
    public JsonResult findAnswerPercentList(String paperId, String startTimeStr, String endTimeStr,String areaId,String source,Integer value,String op,String ipUrl){
        //根据试卷id查询试卷的所有问题及答案选项
        List<QuestionInfo> questionInfoList=questionInfoService.findListByPaperId(paperId);
        List<QuestionInfo> questionTypeList=new ArrayList<QuestionInfo>();
        Map<String, Object> map = new HashMap<>();
        for (QuestionInfo q:questionInfoList) {
            String type=q.getType();
            String qId=q.getId();
            //根据问题id 查询答案选项的数量
            Date startDate=commonUtils.getDateByDateString(startTimeStr);
            Date endDate=commonUtils.getDateByDateString(endTimeStr);
            List<AnswerDetail> answerCount= answerDetailService.getAnswerCount(qId,startDate,endDate,areaId,source,value,op,ipUrl);
            List<AnswerDetail> answerDetails=new ArrayList<AnswerDetail>();

            if("radio".equals(type)||"checkbox".equals(type)||"select".equals(type)||"star".equals(type)||"imgsel".equals(type)){
                String [] ops= q.getOptiones().split(",");
                for (String s:ops) {
                    AnswerDetail d = new AnswerDetail();
                    boolean isfal=false;
                    boolean isLen=false;
                    if(null!=answerCount&&answerCount.size()>0){
                        int len=0;
                        int count=0;
                        for (AnswerDetail ad:answerCount) {
                            String ss = ad.getQuestionAnswer();
                            if(null!=ss&&ss.length()>0){
                                if("checkbox".equals(type)){
                                    List<String> aslist=Arrays.asList(ss.split(","));
                                    if(aslist.contains(s)){
                                        count=count+ad.getAnswerCount();
                                        continue;
                                    }else{
                                        len=len+1;
                                    }
                                }else{
                                    if (ss.equals(s)) {
                                        d.setQuestionAnswer(s);
                                        d.setAnswerCount(ad.getAnswerCount());
                                        d.setQuestionId(qId);
                                        answerDetails.add(d);
                                        continue;
                                    }else{
                                        len=len+1;
                                    }
                                }
                            }else{
                                len=len+1;
                            }

                        }
                        if(len==answerCount.size()){isLen=true;}
                        if("checkbox".equals(type)){
                            d.setQuestionAnswer(s);
                            d.setAnswerCount(count);
                            d.setQuestionId(qId);
                            answerDetails.add(d);
                        }
                    }else{
                        isfal=true;
                    }
                    if(!"checkbox".equals(type)){
                        if(isfal||isLen){
                            d.setQuestionAnswer(s);
                            d.setAnswerCount(0);
                            d.setQuestionId(qId);
                            answerDetails.add(d);
                        }
                    }

                }
            }else if("checkbox".equals(type)){

            }
            q.setAnswerDetails(answerDetails);
        }
        //返回问题和答案选项及选项数量
        return JsonResult.ok().put("data",questionInfoList);
    }


    /**
     * 查询回收数据统计显示列表接口
     */
    // @RequiresPermissions("get:/v1/paperAnswers/findRecycleTongJiData")
    @ApiOperation(value = "查询回收数据统计显示列表接口")
    @PostMapping("/findRecycleTongJiData")
    public PageResult<PaperAnswers> findRecycleTongJiData(Integer page, Integer limit,PaperAnswers paperAnswers, String startTimeStr, String endTimeStr){
        if(page==null){
            page=0;
        }
        if(limit==null){
            limit=10;
        }
        Page<PaperAnswers> dataPage = new Page<>(page, limit);

        if(null!=startTimeStr&&startTimeStr.length()>0){
            paperAnswers.setStartTime(commonUtils.getDateByDateString(startTimeStr));
        }
        if(null!=endTimeStr&&endTimeStr.length()>0) {
            paperAnswers.setEndTime(commonUtils.getDateByDateString(endTimeStr));
        }
        //根据试卷ID,查询试卷答案的详细信息
        Page<PaperAnswers> resultPage= paperAnswersService.findRecycleTongJiData(dataPage,paperAnswers);
        List<PaperAnswers> list =resultPage.getRecords();
        return new PageResult<>(list,resultPage.getTotal());
    }

    //@RequiresPermissions("get:/v1/paperAnswers/export")
    @ApiOperation(value = "导出回收数据excel", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperId", value = "试卷id", required = false, dataType = "String", paramType = "form"),
    })
    @GetMapping("/export")
    public void exportFile(String paperId, HttpServletRequest request, HttpServletResponse response) {
        //创建Excel工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建Excel工作表对象
        HSSFSheet sheet = workbook.createSheet();
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 2500);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 6000);

        // 设置表头字体样式
        HSSFFont columnHeadFont = workbook.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        // 列头的样式
        HSSFCellStyle columnHeadStyle = workbook.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
        // 左右居中
        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 上下居中
        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(true);
        // 左边框的颜色
        columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        // 边框的大小
        columnHeadStyle.setBorderLeft((short) 1);
        // 右边框的颜色
        columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);
        // 边框的大小
        columnHeadStyle.setBorderRight((short) 1);
        // 设置单元格的边框为粗体
        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 设置单元格的边框颜色
        columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        // 设置单元格的背景颜色（单元格的样式会覆盖列或行的样式）
        columnHeadStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        // 设置普通单元格字体样式
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);

        //创建Excel工作表第一行
        HSSFRow row0 = sheet.createRow(0);
        // 设置行高
        row0.setHeight((short) 750);
        List<QuestionInfo> questionInfoList= questionInfoService.findListByPaperId(paperId);
        int i=0;
        //循环写入问题数据
        for (QuestionInfo q:questionInfoList) {
            HSSFCell cell = row0.createCell(i);
            //设置单元格内容
            cell.setCellValue(new HSSFRichTextString(q.getTitle()));
            //设置单元格字体样式
            cell.setCellStyle(columnHeadStyle);
            List<PaperAnswers> paperAnswersList = paperAnswersService.list(new QueryWrapper<PaperAnswers>().eq("paper_id", paperId));
            if (null != paperAnswersList && paperAnswersList.size() > 0) {
                for (int m = 0; m < paperAnswersList.size(); m++) {
                    AnswerDetail answerDetail = answerDetailService.getOne(new QueryWrapper<AnswerDetail>().eq("question_id", q.getId())
                            .eq("paper_answer_id", paperAnswersList.get(m).getId()).eq("del_flag", "0"));
                    if (null != answerDetail) {
                        HSSFRow row = sheet.getRow(m + 1);
                        if (null == row) {
                            row = sheet.createRow(m + 1);
                        }
                        HSSFCell rcell = row.createCell(i);
                        rcell.setCellValue(new HSSFRichTextString(answerDetail.getQuestionAnswer()));
                        rcell.setCellStyle(columnHeadStyle);
                    }
                }

            }
            i++;
        }

        // 获取输出流
        OutputStream os = null;
        String filename=  DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
        try {
            filename = new String("试卷回收数据".getBytes("utf-8"),"ISO-8859-1")+ "_"+filename;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition",
                "attachment; filename=" + filename);
        // 定义输出类型
        response.setContentType("application/msexcel");
        // 重置输出流
        //response.reset();
        // 设定输出文件头
        try {
            os =response.getOutputStream();
            workbook.write(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;

    }

    /**
     * 回收概况页面回答的地域数量接口
     */
    // @RequiresPermissions("get:/v1/paperAnswers/findAnswersByAreaCount")
    @ApiOperation(value = "回收概况页面回答的地域数量接口")
    @GetMapping("/findAnswersByAreaCount")
    public PageResult<PaperAnswers> findAnswersByAreaCount(String paperId,Integer page, Integer limit){
        if(page==null){
            page=0;
        }
        if(limit==null){
            limit=10;
        }
        //查询地域及回答数量
        Page<PaperAnswers> answersPage = new Page<>(page, limit);
        List<PaperAnswers> data= paperAnswersService.findAnswersByAreaCount(paperId,answersPage);
        PaperRecycle pr=paperRecycleService.findRecycledSum(paperId);
        if(null!=pr){
            for (PaperAnswers pa:data) {
                Integer count= pa.getAnswerCount();
                String value=String.format("%.2f", ((count / pr.getRecycled().doubleValue()) * 100)) + "%";
                pa.setValue(value);
            }
        }
        return new PageResult<PaperAnswers>(data,answersPage.getTotal());
    }


    /**
     * 回答问卷
     * @param paperAnswers
     * @return
     */
    @ApiOperation(value = "回答问卷")
    @PostMapping("savePaperAnswer")
    public JsonResult savePaperAnswer(PaperAnswers paperAnswers,String startTimeStr,String json) throws ExecutionException, InterruptedException {
        boolean isResult=false;
       CompletableFuture<Boolean> cf=ansyTaskService.saveAnswer(paperAnswers,startTimeStr,json);
        isResult=cf.get();
        if(isResult){
            return JsonResult.ok("提交问卷成功");
        }else {
            return JsonResult.error("提交问卷失败");
        }
    }


    /**
     * 查看问卷某个回答列表
     * @param id
     * @return
     */
    @ApiOperation(value = "查看问卷某个回答列表")
    @GetMapping("/getAnswerDetails")
    public JsonResult getAnswerDetails(String id) {
        Map<String, Object> map = new HashMap<>();
        if(null!=id){
            PaperAnswers paperAnswers=paperAnswersService.getById(id);
            List<QuestionInfo> quelist= paperAnswersService.findAnswerById(id);
            map.put("paperAnswers",paperAnswers);
            map.put("quelist",quelist);
            if(null!=quelist&&quelist.size()>0){
                for (QuestionInfo q:quelist) {
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
            if(null!=paperAnswers){
                PaperInfo paperInfo = paperInfoService.getById(paperAnswers.getPaperId());
                map.put("paperInfo",paperInfo);
            }
        }
        return  JsonResult.ok().put("data",map);
    }


    /**
     * 修改问卷答案
     * @param json
     * @return
     */
    @ApiOperation(value = "修改问卷答案")
    @PostMapping("updatePaperAnswer")
    @Transactional
    public JsonResult savePaperAnswekr(String paperAnswersId,String json,HttpServletRequest request){
        boolean isResult=false;
            try{
                if(null!=paperAnswersId&&null!=json&&json.length()>0){
                    List<AnswerDetail> detailList = JSONUtil.parseArray(json, AnswerDetail.class);
                    if(null!=detailList&&detailList.size()>0){
                        boolean isSuccess=answerDetailService.remove(new QueryWrapper<AnswerDetail>().eq("paper_answer_id",paperAnswersId));
                        if(isSuccess){
                            String loginUserId = UserUtil.getLoginUserId(request);
                            for (AnswerDetail d:detailList) {
                                d.setPaperAnswerId(paperAnswersId);
                                d.setDelFlag("0");
                                d.setUpdateBy(loginUserId);
                                d.setUpdateTime(new Date());
                            }
                        }
                    }
                    answerDetailService.saveBatch(detailList);
                }
                isResult=true;
            }catch (Exception e) {
                //强制手动事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        if(isResult){
            return JsonResult.ok("修改成功");
        }else {
            return JsonResult.error("修改失败");
        }
    }

    /**
     * 修改问卷答案
     * @param isInvalid (0/1)
     * @return
     */
    @ApiOperation(value = "修改问卷答案为无效数据")
    @PostMapping("updateAnswerInvalid")
    @Transactional
    public JsonResult updateAnswerInvalid(String id,String isInvalid){
        boolean isResult=false;
        try{
            if(null!=id&&id.length()>0){
                PaperAnswers paperAnswers=paperAnswersService.getById(id);
                if(null!=paperAnswers){
                    paperAnswers.setIsInvalid(isInvalid==null?"0":isInvalid);
                }
                boolean isSave=paperAnswersService.updateById(paperAnswers);
                if(isSave){
                    paperRecycleService.updateDelFlag(isInvalid==null?"0":isInvalid,id);
                    isResult=true;
                }
            }

        }catch (Exception e) {
            //强制手动事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        if(isResult){
            return JsonResult.ok("修改成功");
        }else {
            return JsonResult.error("修改失败");
        }
    }

}
