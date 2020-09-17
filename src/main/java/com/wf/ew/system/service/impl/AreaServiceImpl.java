package com.wf.ew.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.system.dao.AreaMapper;
import com.wf.ew.system.model.Area;
import com.wf.ew.system.service.AreaService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {
    /**
     * 查询子菜单列表
     * @param pid
     * @return
     */
    @Override
    public List<Area> findByParentId(String pid){
        return baseMapper.findByParentId(pid);
    }

    /**
     * 根据id查询REMARKS
     * @param id
     * @return
     */
    @Override
   public String getRemarksById(String id){
        return baseMapper.getRemarksById(id);
    }

    /**
     * 根据remarks查询id
     * @param remarks
     * @return
     */
    public String getIdByRemarks(String remarks){
        return baseMapper.getIdByRemarks(remarks);
    }
    /**
     * 查询到所有remarks为key,id为value的areaMap
     * @return
     */
    public Map<String,String> getAreaMap(){
        List<Area> list=baseMapper.selectList(null);
        Map<String,String> areaMap= new HashMap<String,String>();
        for (Area area:list) {
           areaMap.put(area.getRemarks(),area.getId());
        }
        return areaMap;
    }
}
