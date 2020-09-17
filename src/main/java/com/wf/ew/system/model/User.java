package com.wf.ew.system.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import com.wf.ew.common.fieldtype.RoleListType;
import com.yuanjing.framework.common.utils.excel.annotation.ExcelField;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@TableName("sys_user")
public class User implements Serializable {
    private static final long serialVersionUID = 242146703513492331L;

    @TableId(value = "user_id")
    private String userId;  // 用户id

    private String officeId;  // 部门id

    private String username;  // 账号

    private String password;  // 密码

    private String nickName;  // 昵称

    private String avatar;  // 头像

    private String sex;  // 性别

    private String phone;  // 手机号

    private String email;  // 邮箱

    private Integer emailVerified;  // 邮箱是否验证，0未验证，1已验证

    private String trueName;  // 真实姓名

    private String idCard;  // 身份证号

    private Date birthday;  // 出生日期

    private Integer departmentId; // 部门id

    private Integer state;  // 状态，0正常，1冻结

    private Date createTime;  // 注册时间

    private Date updateTime;  // 修改时间

    @TableField(exist = false)
    private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

    @TableField(exist = false)
    private List<Authorities> authorities;  // 权限

    @TableField(exist = false)
    private List<Role> roles;  // 角色

    @TableField(exist = false)
    private String oldUsername;  // 账号

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    //@ExcelField(title="ID", type=1, align=2, sort=1)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @ExcelField(title="账号", align=2, sort=20)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ExcelField(title="昵称", align=2, sort=30)
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @ExcelField(title="性别", align=2, sort=40)
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @ExcelField(title="手机号", align=2, sort=50)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ExcelField(title="邮箱", align=2, sort=60)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }

    @ExcelField(title="真实姓名", align=2, sort=70)
    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    @ExcelField(title="身份证号", align=2, sort=80)
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @JSONField(format = "yyyy-MM-dd")
    @ExcelField(title="出生日期", align=2, sort=90)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ExcelField(title="注册时间", align=2, sort=100)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ExcelField(title="修改时间", align=2, sort=110)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<Authorities> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authorities> authorities) {
        this.authorities = authorities;
    }

    @ExcelField(title="拥有角色", align=1, sort=130, fieldType= RoleListType.class)
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public User(String userId) {
        this.userId = userId;
    }

    public User() {
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @JSONField(serialize=false)
    public List<String> getRoleIdList() {
        List<String> roleIdList = Lists.newArrayList();
        for (Role role : roleList) {
            roleIdList.add(role.getRoleId());
        }
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        roleList = Lists.newArrayList();
        for (String roleId : roleIdList) {
            Role role = new Role();
            role.setRoleId(roleId);
            roleList.add(role);
        }
    }

    /**
     * 根據id判斷是否為管理员
     * @param id
     * @return
     */
    public static boolean isAdmin(String id){
        //id是否为1
        if(id != null && "1".equals(id)){
            return true;
        }
        //角色列表是否包含1（管理员角色id=1）
        return new User(id).getRoleIdList().contains("1");
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }
}
