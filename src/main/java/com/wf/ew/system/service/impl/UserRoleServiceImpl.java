package com.wf.ew.system.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.wf.ew.system.dao.UserRoleMapper;
import com.wf.ew.system.model.UserRole;
import com.wf.ew.system.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018-12-19 下午 4:10.
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public String[] getRoleIds(String userId) {

        List<UserRole> userRoles = baseMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", userId));
        String[] roleIds = new String[userRoles.size()];
        for (int i = 0; i < userRoles.size(); i++) {
            roleIds[i] = userRoles.get(i).getRoleId();
        }
        return roleIds;
    }
}
