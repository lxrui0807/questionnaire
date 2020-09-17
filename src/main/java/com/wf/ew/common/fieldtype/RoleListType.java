package com.wf.ew.common.fieldtype;

import com.google.common.collect.Lists;
import com.wf.ew.common.utils.SpringContextUtils;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.service.RoleService;
import com.yuanjing.framework.common.utils.Collections3;
import com.yuanjing.framework.common.utils.StringUtils;

import java.util.List;

/**
 * 字段类型转换
 */
public class RoleListType {


    private static RoleService roleService = SpringContextUtils.getBean(RoleService.class);

    /**
     * 获取对象值（导入）
     */
    public static Object getValue(String val) {
        List<Role> roleList = Lists.newArrayList();
        List<Role> allRoleList = roleService.list();
        for (String s : StringUtils.split(val, ",")){
            for (Role e : allRoleList){
                if (StringUtils.trimToEmpty(s).equals(e.getRoleName())){
                    roleList.add(e);
                }
            }
        }
        return roleList.size()>0?roleList:null;
    }
    /**
     * 设置对象值（导出）
     */
    public static String setValue(Object val) {
        if (val != null){
            @SuppressWarnings("unchecked")
            List<Role> roleList = (List<Role>)val;
            return Collections3.extractToString(roleList, "roleName", ", ");
        }
        return "";
    }

}
