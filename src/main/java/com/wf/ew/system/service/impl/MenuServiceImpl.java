package com.wf.ew.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.system.dao.MenuMapper;
import com.wf.ew.system.model.Menu;
import com.wf.ew.system.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    /**
     * 查询子菜单列表
     * @param pid
     * @return
     */
    @Override
    public List<Menu> findByParentId(String pid){
        return baseMapper.findByParentId(pid);
    }
}
