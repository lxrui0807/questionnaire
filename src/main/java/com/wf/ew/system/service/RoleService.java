package com.wf.ew.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.system.model.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    public List<Role> getRolesByUserId(String userId);
}
