package com.wf.ew.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wf.ew.cms.model.NewsBase;

import com.wf.ew.cms.service.NewsBaseService;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.utils.UserUtil;
import com.wf.ew.system.model.User;
import com.wf.ew.system.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wf.jwtp.annotation.RequiresPermissions;
import org.wf.jwtp.provider.Token;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Api(value = "新闻管理", tags = "news")
@RestController
@RequestMapping("${api.version}/news")
public class NewsBaseController {
    @Autowired
    private NewsBaseService newsBaseService;
    @Autowired
    private UserService userService;

    /**
     * @param page
     * @param limit
     * @param newsBase
     * @param request
     * @return
     */
    @RequiresPermissions("get:/v1/news")
    @ApiOperation(value = "查询所有新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "第几页", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页多少条", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "news", value = "新闻信息", dataType = "News", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping()
    public PageResult<NewsBase> list(Integer page, Integer limit, NewsBase newsBase, HttpServletRequest request) {
        Page<NewsBase> newsPage = new Page<>(page, limit);
        QueryWrapper<NewsBase> wrapper = new QueryWrapper<>();
        String title = newsBase.getTitle();
        String status = newsBase.getStatus();
        String type = newsBase.getType();
        //  按标题进行查询
        if (title != null && !title.trim().isEmpty()) {
            wrapper.like("title", title);
        }
        if (status != null && !status.trim().isEmpty()) {
            wrapper.like("status", status);
        }
        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq("type", type);
        }
        //不是管理员的只能看自己的消息
        String userId = UserUtil.getLoginUserId(request);
        boolean isPermission=false;
        boolean isAdmin=UserUtil.get(userId).isAdmin(userId);
        if(isAdmin||UserUtil.isPermission("role.news.approve",request)){
            isPermission=true;
        }
        if (!isAdmin&&!isPermission) {
            wrapper.eq("create_by", userId);
        }

        //  指定排序规则
        wrapper.orderBy(true, false, "create_time");
        newsBaseService.page(newsPage, wrapper);
        List<NewsBase> newsBaseList = newsPage.getRecords();
        for(NewsBase newsBase1:newsBaseList){
            newsBase1.setPermission(isPermission);
            String approveBy = newsBase1.getApproveBy();
            if(approveBy!=null&&!approveBy.equals("")){
                User user = userService.getUserById(approveBy);
                String username = user.getUsername();
                newsBase1.setApproveBy(username);
            }
        }
        return new PageResult<>(newsBaseList, newsPage.getTotal());
    }

    /**
     * 添加一条新闻
     *
     * @param newsBase
     * @return
     */
    @RequiresPermissions("post:/v1/news")
    @ApiOperation(value = "添加字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "news", value = "新闻信息", required = true, dataType = "News", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping()
    public JsonResult add(NewsBase newsBase) {
        newsBase.setStatus("待审核");
        if (newsBaseService.save(newsBase)) {
            return JsonResult.ok("添加成功！");
        }
        return JsonResult.error("添加失败！");
    }

    /**
     * 修改新闻
     *
     * @param newsBase
     * @return
     */
    @RequiresPermissions("put:/v1/news")
    @ApiOperation(value = "修改新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "news", value = "新闻信息", required = true, dataType = "News", paramType = "form"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "form")
    })
    @PutMapping()
    public JsonResult update(NewsBase newsBase) {
        newsBase.setApproveBy("");
        newsBase.setStatus("待审核");
        if (newsBaseService.updateById(newsBase)) {
            return JsonResult.ok("修改成功！");
        }
        return JsonResult.error("修改失败！");
    }

    /**
     * 根据id审核新闻
     *
     * @param newsBase
     * @param request
     * @return
     */
    @RequiresPermissions("put:/v1/news/audit")
    @ApiOperation(value = "审核新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "news", value = "新闻信息", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @PutMapping("audit")
    public JsonResult audit(NewsBase newsBase, HttpServletRequest request) {
        String userId = UserUtil.getLoginUserId(request);
//        User user = userService.getUserById(userId);
//        String username = user.getUsername();
        newsBase.setApproveBy(userId);
        newsBase.setApproveDate(new Date());
        String status = newsBase.getStatus();
        if (status.equals("0")) {
            newsBase.setStatus("审核通过");
            boolean b = newsBaseService.updateById(newsBase);
            if (b) {
                return JsonResult.ok("审核成功");
            } else {
                return JsonResult.error("审核失败！");
            }
        } else {
            newsBase.setStatus("审核未通过");
            boolean b = newsBaseService.updateById(newsBase);
            if (b) {
                return JsonResult.ok("审核成功");
            } else {
                return JsonResult.error("审核失败！");
            }
        }
    }

    /**
     * 根据id获取一条新闻
     *
     * @param id
     * @return
     */
    @RequiresPermissions("get:/v1/news/{id}")
    @ApiOperation(value = "获取新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "新闻id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable("id") String id) {
        NewsBase newsBase=newsBaseService.getById(id);
        return JsonResult.ok().put("newsBase", newsBase);
    }

    /**
     * 根据id删除一条新闻
     *
     * @param id
     * @return
     */
    @RequiresPermissions("delete:/v1/news/{id}")
    @ApiOperation(value = "删除新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "新闻id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") String id) {
        if (newsBaseService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.ok("删除失败");
    }

    /**
     * @param request
     * @return
     */
   // @RequiresPermissions("get:/v1/hasPermission")
    @ApiOperation(value = "查看当前用户有没有某个权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permission", value = "权限名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/hasPermission")
    public boolean hasPermission(HttpServletRequest request,String permission) {
        Token token=UserUtil.getLoginToken(request);
        String permissions[]=token.getPermissions();
        return Arrays.asList(permissions).contains(permission);
    }

    @RequiresPermissions("get:/v1/news/newPhoto")
    @ApiOperation(value = "查询图片新闻")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "第几页", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页多少条", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "news", value = "新闻信息", dataType = "News", paramType = "query"),
            @ApiImplicitParam(name = "access_token", value = "令牌", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/newPhoto")
    public PageResult<NewsBase> photoList(Integer page, Integer limit, NewsBase newsBase, HttpServletRequest request) {
        Page<NewsBase> newsPage = new Page<>(page, limit);
        QueryWrapper<NewsBase> wrapper = new QueryWrapper<>();
        String title = newsBase.getTitle();
        String status = newsBase.getStatus();
        String type = newsBase.getType();
        //  按标题进行查询
        if (title != null && !title.trim().isEmpty()) {
            wrapper.like("title", title);
        }
        if (status != null && !status.trim().isEmpty()) {
            wrapper.like("status", status);
        }
        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq("type", type);
        }
        //不是管理员的只能看自己的消息
        String userId = UserUtil.getLoginUserId(request);
        boolean isPermission=false;
        boolean isAdmin=UserUtil.get(userId).isAdmin(userId);
        if(isAdmin||UserUtil.isPermission("role.news.approve",request)){
            isPermission=true;
        }
        if (!isAdmin&&!isPermission) {
            wrapper.eq("create_by", userId);
        }

        //  指定排序规则
        wrapper.orderBy(true, false, "create_time");
        newsBaseService.page(newsPage, wrapper);
        List<NewsBase> newsBaseList = newsPage.getRecords();
        for(NewsBase newsBase1:newsBaseList){
            newsBase1.setPermission(isPermission);
            String approveBy = newsBase1.getApproveBy();
            if(approveBy!=null&&!approveBy.equals("")){
                User user = userService.getUserById(approveBy);
                String username = user.getUsername();
                newsBase1.setApproveBy(username);
            }
        }
        return new PageResult<>(newsBaseList, newsPage.getTotal());
    }
}
