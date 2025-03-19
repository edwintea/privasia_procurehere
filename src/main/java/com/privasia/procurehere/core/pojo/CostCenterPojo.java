package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.StringUtils;

public class CostCenterPojo implements Serializable {

	private static final long serialVersionUID = -6560512936324388511L;

	private String id;

	private String costCenter;

	private String description;

	private Status status;

	public CostCenterPojo() {

	}

	public CostCenterPojo(String id, String costCenter) {
		this.id = id;
		this.costCenter = StringUtils.checkString(costCenter);
	}

	public CostCenterPojo(String id, String costCenter, String description) {
		this.id = id;
		this.costCenter = StringUtils.checkString(costCenter);
		this.description = StringUtils.checkString(description);
	}

	public CostCenterPojo(String id, String costCenter, String description, Status status) {
		this.id = id;
		this.costCenter = StringUtils.checkString(costCenter);
		this.description = StringUtils.checkString(description);
		this.status = status;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CostCenterPojo other = (CostCenterPojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
