package com.wf.ew.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.system.dao.RoleMapper;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.model.UserRole;
import com.wf.ew.system.service.RoleService;
import com.wf.ew.system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public List<Role> getRolesByUserId(String userId) {
        return baseMapper.getRolesByUserId(userId);
    }
}
