package com.privasia.procurehere.core.entity;

/**
 * @author pooja
 */
import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateSerializer;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_PROD_CONT_REMINDER")
public class ProductContractReminder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1528711790505084694L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "REMINDER_DATE")
	private Date reminderDate;

	@Column(name = "REM_INTERVAL", length = 3)
	@Digits(integer = 3, fraction = 0, message = "{event.days}")
	private Integer interval;

	@Column(name = "REMINDER_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean reminderSent = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "CONTRACT_ID", foreignKey = @ForeignKey(name = "FK_PROD_CONTRACT_REM"))
	private ProductContract productContract;

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
	 * @return the reminderDate
	 */
	public Date getReminderDate() {
		return reminderDate;
	}

	/**
	 * @param reminderDate the reminderDate to set
	 */
	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}

	/**
	 * @return the interval
	 */
	public Integer getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	/**
	 * @return the reminderSent
	 */
	public Boolean getReminderSent() {
		return reminderSent;
	}

	/**
	 * @param reminderSent the reminderSent to set
	 */
	public void setReminderSent(Boolean reminderSent) {
		this.reminderSent = reminderSent;
	}

	/**
	 * @return the productContract
	 */
	public ProductContract getProductContract() {
		return productContract;
	}

	/**
	 * @param productContract the productContract to set
	 */
	public void setProductContract(ProductContract productContract) {
		this.productContract = productContract;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((interval == null) ? 0 : interval.hashCode());
		result = prime * result + ((productContract == null) ? 0 : productContract.hashCode());
		result = prime * result + ((reminderDate == null) ? 0 : reminderDate.hashCode());
		result = prime * result + ((reminderSent == null) ? 0 : reminderSent.hashCode());
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
		ProductContractReminder other = (ProductContractReminder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (interval == null) {
			if (other.interval != null)
				return false;
		} else if (!interval.equals(other.interval))
			return false;
		if (productContract == null) {
			if (other.productContract != null)
				return false;
		} else if (!productContract.equals(other.productContract))
			return false;
		if (reminderDate == null) {
			if (other.reminderDate != null)
				return false;
		} else if (!reminderDate.equals(other.reminderDate))
			return false;
		if (reminderSent == null) {
			if (other.reminderSent != null)
				return false;
		} else if (!reminderSent.equals(other.reminderSent))
			return false;
		return true;
	}

}
