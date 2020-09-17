package com.wf.ew.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.system.model.Authorities;

import java.util.List;

public interface AuthoritiesService extends IService<Authorities> {

    List<String> listByUserId(String userId);

    List<String> listByRoleIds(List<String> roleId);

    List<String> listByRoleId(String roleId);

}
