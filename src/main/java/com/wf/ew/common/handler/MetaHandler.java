package com.wf.ew.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.wf.ew.common.utils.HttpContextUtils;
import com.wf.ew.common.utils.UserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 处理新增和更新的基础数据填充
 * 配合BaseEntity和MyBatisPlusConfig使用
 */
@Component
public class MetaHandler implements MetaObjectHandler {

    /**
     * 新增数据执行
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //  获取当前登录的userId
        String loginUserId = UserUtil.getLoginUserId(request);

        this.setFieldValByName("createBy",loginUserId,metaObject);
        this.setFieldValByName("createTime", new Date(),metaObject);


    }

    /**
     * 更新数据执行
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //  获取当前登录的userId
        String loginUserId = UserUtil.getLoginUserId(request);

        this.setFieldValByName("updateBy",loginUserId , metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
