package com.privasia.procurehere.core.pojo;

import java.util.List;

public class ContractApprovalUserPojo {
	
	private Integer level;
	
	private String name;
	
	private String status;
	
	private String imgPath;
	
	private String active;
	
	private String type;
	
	List<ContractApprovalUserPojo> approvalList;

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public List<ContractApprovalUserPojo> getApprovalList() {
		return approvalList;
	}

	public void setApprovalList(List<ContractApprovalUserPojo> approvalList) {
		this.approvalList = approvalList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
