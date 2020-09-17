package com.wf.ew.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wf.ew.system.model.Office;

import java.util.List;

public interface OfficeService extends IService<Office> {
    /**
     * 查询子部門列表
     * @param pid
     * @return
     */
    List<Office> findByParentId(String pid);

    /**
     * 顶级部门名称不允许重复
     * @param name
     * @return
     */
    Integer checkTopOffice(String name);

    /**
     * 顶级部门下，部门名称不允许重复
     * @param parentId
     * @param name
     * @return
     */
    Integer checkSubOffice(String parentId,String name);

    /**
     * 根据父级编号获取上级部门名称
     * @param parentId
     * @return
     */
    String getFatherNameByParentId(String parentId);

    /**
     * 根据公司名,获取公司名对应的id
     * @param companyName
     * @return
     */
    String getCompanyIdByCompanyName(String companyName);

    /**
     * 定位(开发一部)
     * @param companyId
     * @param fatherName
     * @return
     */
    Office getLocation(String companyId,String fatherName);
}
