package com.wf.ew.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.system.model.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    /**
     * 查询子菜单列表
     * @param pid
     * @return
     */
    List<Menu> findByParentId(String pid);
}
