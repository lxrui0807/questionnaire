package com.wf.ew.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.system.model.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    public List<Role> getRolesByUserId(String userId);
}
