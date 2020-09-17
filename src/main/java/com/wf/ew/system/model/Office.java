package com.wf.ew.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wf.ew.base.model.TreeEntity;
import com.yuanjing.framework.common.utils.excel.annotation.ExcelField;
import org.hibernate.validator.constraints.Length;
import java.util.List;

/**
 * 机构Entity
 */
@TableName(value = "sys_office")
public class Office extends TreeEntity {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id")
	private String id;  // id
	private String areaId;		// 归属区域
	private String code; 	// 机构编码
	private String type; 	// 机构类型（1：公司；2：部门；3：小组）
	private String grade; 	// 机构等级（1：一级；2：二级；3：三级；4：四级）
	private String address; // 联系地址
	private String zipCode; // 邮政编码
	private String master; 	// 负责人
	private String phone; 	// 电话
	private String fax; 	// 传真
	private String email; 	// 邮箱
	private String useable;//是否可用
	private String primaryPerson;//主负责人
	private String deputyPerson;//副负责人

	@TableField(exist = false)
	private List<String> childDeptList;//快速添加子部门

	public Office(){
		this.type = "2";
	}

	public Office(String id){
		this();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getChildDeptList() {
		return childDeptList;
	}

	public void setChildDeptList(List<String> childDeptList) {
		this.childDeptList = childDeptList;
	}

	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public String getPrimaryPerson() {
		return primaryPerson;
	}

	public void setPrimaryPerson(String primaryPerson) {
		this.primaryPerson = primaryPerson;
	}

	public String getDeputyPerson() {
		return deputyPerson;
	}

	public void setDeputyPerson(String deputyPerson) {
		this.deputyPerson = deputyPerson;
	}

	@ExcelField(title="归属区域", align=2, sort=70)
	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	@ExcelField(title="部门类型", align=2, sort=50)
	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@ExcelField(title="部门级别", align=2, sort=60)
	@Length(min=1, max=1)
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	@Length(min=0, max=255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min=0, max=100)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(min=0, max=100)
	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	@Length(min=0, max=200)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@ExcelField(title="所属公司", align=2, sort=80)
	@Length(min=0, max=200)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Length(min=0, max=200)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@ExcelField(title="部门编码", align=2, sort=30)
	@Length(min=0, max=100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return name;
	}

	//	父类中需要导出的字段，需要在子类中进行重写
	@ExcelField(title="上级部门名称", align=2, sort=10)
	public String getParentId() {
		return parentId;
	}

	@ExcelField(title="排序", align=2, sort=40)
	public Integer getSort() {
		return sort;
	}

	@ExcelField(title="部门名称", align=2, sort=20)
	public String getName() {
		return name;
	}
}