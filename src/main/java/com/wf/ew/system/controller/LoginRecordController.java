package com.wf.ew.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.common.PageResult;
import com.wf.ew.system.model.LoginRecord;
import com.wf.ew.system.service.LoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wf.jwtp.annotation.RequiresPermissions;

import java.util.List;

@Api(value = "登录日志管理", tags = "loginRecord")
@RestController
@RequestMapping("${api.version}/loginRecord")
public class LoginRecordController {
    @Autowired
    private LoginRecordService loginRecordService;
   @RequiresPermissions("get:/v1/loginRecord")
    @ApiOperation(value = "查询所有日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginName", value = "登录账号", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "用户名", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "开始日期", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", required = true, dataType = "Integer", paramType = "query"), @ApiImplicitParam(name = "name", value = "用户名", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页多少条", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<LoginRecord> list(Integer page, Integer limit, String loginName, String name, String createDate,String endDate) {
        if (page == null) {
            page = 0;
        }
        if (limit == null) {
            limit = 10;
        }
        Page<LoginRecord> logPage = new Page<>(page, limit);
        QueryWrapper<LoginRecord> wrapper = new QueryWrapper<>();
        if (loginName != null && !loginName.trim().isEmpty()) {
            wrapper.like("login_name",loginName);
        }
        if ( name != null && !name.trim().isEmpty()) {
            wrapper.like("name",name);
        }
        //过滤大于等于开始日期小于等于结束日期之间的日志记录
        if ( createDate != null &&!createDate.trim().isEmpty()&& endDate != null&&!endDate.trim().isEmpty()) {
            wrapper.between("create_date",createDate,endDate);
        }
        wrapper.orderBy(true,false,"create_Date");
        loginRecordService.page(logPage, wrapper);
        List<LoginRecord> logList = logPage.getRecords();
        return new PageResult<>(logList, logPage.getTotal());
    }
}
