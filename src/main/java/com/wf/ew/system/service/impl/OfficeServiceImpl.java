package com.wf.ew.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.ew.system.dao.OfficeMapper;
import com.wf.ew.system.model.Office;
import com.wf.ew.system.service.OfficeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeServiceImpl extends ServiceImpl<OfficeMapper, Office> implements OfficeService {
    /**
     * 查询子部門列表
     * @param pid
     * @return
     */
    @Override
    public List<Office> findByParentId(String pid){
        return baseMapper.findByParentId(pid);
    }

    /**
     * 顶级部门名称不允许重复
     * @param name
     * @return
     */
    public Integer checkTopOffice(String name){
        return baseMapper.checkTopOffice(name);
    }

    /**
     * 顶级部门下，部门名称不允许重复
     * @param parentId
     * @param name
     * @return
     */
    public Integer checkSubOffice(String parentId,String name){
        return baseMapper.checkSubOffice(parentId,name);
    }

    /**
     * 根据父级编号获取上级部门名称
     * @param parentId
     * @return
     */
   public String getFatherNameByParentId(String parentId){
       return baseMapper.getFatherNameByParentId(parentId);
    }

    /**
     * 根据公司名,获取公司名对应的id
     * @param companyName
     * @return
     */
    public String getCompanyIdByCompanyName(String companyName){
        return baseMapper.getCompanyIdByCompanyName(companyName);
    }

    /**
     * 定位(开发一部)
     * @param companyId
     * @param fatherName
     * @return
     */
    public Office getLocation(String companyId,String fatherName){
        return baseMapper.getLocation(companyId,fatherName);
    }
}
