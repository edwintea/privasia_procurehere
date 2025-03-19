package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author sudesha
 */
public class RequestAuditPojo implements Serializable {

	private static final long serialVersionUID = -5091515804482329141L;

	private String actionDate;
	private String description;
	private String type;
	private String actionBy;

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActionBy() {
		return actionBy;
	}

	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}

}
