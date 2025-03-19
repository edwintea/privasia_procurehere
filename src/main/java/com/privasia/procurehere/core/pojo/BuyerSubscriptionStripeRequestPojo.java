package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.PlanRange;

public class BuyerSubscriptionStripeRequestPojo {

	private Buyer buyer;

	private BuyerPlan plan;

	private PlanRange range;

	private Integer userQuantity;

	public Buyer getBuyer() {
		return buyer;
	}

	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	public BuyerPlan getPlan() {
		return plan;
	}

	public void setPlan(BuyerPlan plan) {
		this.plan = plan;
	}

	public PlanRange getRange() {
		return range;
	}

	public void setRange(PlanRange range) {
		this.range = range;
	}

	public Integer getUserQuantity() {
		return userQuantity;
	}

	public void setUserQuantity(Integer userQuantity) {
		this.userQuantity = userQuantity;
	}

}
