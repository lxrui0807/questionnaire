package com.wf.ew.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.system.dao.AuthoritiesMapper;
import com.wf.ew.system.model.Authorities;
import com.wf.ew.system.service.AuthoritiesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthoritiesServiceImpl extends ServiceImpl<AuthoritiesMapper, Authorities> implements AuthoritiesService {

    @Override
    public List<String> listByUserId(String userId) {
        return baseMapper.listByUserId(userId);
    }

    @Override
    public List<String> listByRoleIds(List<String> roleIds) {
        if (roleIds == null || roleIds.size() == 0) {
            return new ArrayList<>();
        }
        return baseMapper.listByRoleId(roleIds);
    }

    @Override
    public List<String> listByRoleId(String roleId) {
        List<String> roleIds = new ArrayList<>();
        if (roleId != null) {
            roleIds.add(roleId);
        }
        return listByRoleIds(roleIds);
    }

}
