package com.wf.ew.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wf.ew.base.model.TreeEntity;

@TableName(value = "sys_menu")
public class Menu extends TreeEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private String id;  // id

    private String menuUrl;  // 菜单url

    private String menuIcon;  // 菜单图标

    private String authority;  // 对应权限

    @TableField(exist = false)
    private String parentName;  // 父级菜单

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Menu() {
    }

   //   带参构造方法

    public Menu(String id, String menuUrl, String menuIcon, String authority, String parentName) {
        this.id = id;
        this.menuUrl = menuUrl;
        this.menuIcon = menuIcon;
        this.authority = authority;
        this.parentName = parentName;
    }
}