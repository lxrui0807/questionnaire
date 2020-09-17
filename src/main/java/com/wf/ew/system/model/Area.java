package com.wf.ew.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wf.ew.base.model.TreeEntity;
import org.hibernate.validator.constraints.Length;
import java.util.ArrayList;
import java.util.List;

/**
 * 区域Entity
 */
@TableName(value = "sys_area")
public class Area extends TreeEntity {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id")
	private String id;  // d
	private String code; 	// 区域编码
	private String type; 	// 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县）

	@TableField(exist = false)
	private List<Area> children=new ArrayList<Area>();

	public List<Area> getChildren() {
		return children;
	}

	public void setChildren(List<Area> children) {
		this.children = children;
	}

	public Area(){
		this.sort = 30;
	}

	public Area(String id){
		this();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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
}