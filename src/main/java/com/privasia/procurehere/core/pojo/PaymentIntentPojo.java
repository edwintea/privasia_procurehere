package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class PaymentIntentPojo implements Serializable {

	private static final long serialVersionUID = -7636502029356126411L;

	private String clientSecret;

	private String id;

	private Long amount;

	private Long createdAt;

	private String success;
	
	private String status;
	
	private String publishKey;

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPublishKey() {
		return publishKey;
	}

	public void setPublishKey(String publishKey) {
		this.publishKey = publishKey;
	}
	
	

}
