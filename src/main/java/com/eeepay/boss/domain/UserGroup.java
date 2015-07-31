package com.eeepay.boss.domain;

import java.io.Serializable;

/**
 * 用户分组
 * @author hdb
 */
public class UserGroup extends ToString implements Serializable {

	/**
	 */
	private static final long serialVersionUID = 27297584605491728L;

	private Long id;
	private String group_name;
	private String group_desc;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getGroup_desc() {
		return group_desc;
	}
	public void setGroup_desc(String group_desc) {
		this.group_desc = group_desc;
	}
}
