package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.enums.EventTimelineType;

/**
 * @author Sarang
 */

public class EventTimelinePojo implements Serializable {


	private static final long serialVersionUID = -8248748955061396548L;
	private String id;
	private Buyer buyer;
	private String activityDate;
	private EventTimelineType activity;
	private String description;

	public EventTimelinePojo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Buyer getBuyer() {
		return buyer;
	}

	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}

	public EventTimelineType getActivity() {
		return activity;
	}

	public void setActivity(EventTimelineType activity) {
		this.activity = activity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}