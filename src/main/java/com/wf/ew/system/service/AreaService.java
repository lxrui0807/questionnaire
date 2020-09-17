package com.wf.ew.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.system.model.Area;
import java.util.List;
import java.util.Map;

public interface AreaService extends IService<Area> {
    /**
     * 查询子菜单列表
     * @param pid
     * @return
     */
    List<Area> findByParentId(String pid);

    /**
     * 根据id查询REMARKS
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
