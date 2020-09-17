package com.wf.ew.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.system.model.Menu;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 查询子菜单列表
     * @param pid
     * @return
     */
    List<Menu> findByParentId(String pid);
}
