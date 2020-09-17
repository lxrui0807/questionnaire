package com.wf.ew.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wf.ew.system.model.Area;
import java.util.List;
import java.util.Map;

public interface AreaMapper extends BaseMapper<Area> {
    /**
     * 查询子菜单列表
     * @param pid
     * @return
     */
    List<Area> findByParentId(String pid);

    /**
     * 根据id查询REMARKS
     *
     * @param id
     * @return
     */
     String getRemarksById(String id);

    /**
     * 根据remarks查询id
     * @param remarks
     * @return
     */
     String getIdByRemarks(String remarks);

    /**
     * 查询到所有remarks为key,id为value的areaMap
     * @return
     */
    Map<String,String> getAreaMap();
}
