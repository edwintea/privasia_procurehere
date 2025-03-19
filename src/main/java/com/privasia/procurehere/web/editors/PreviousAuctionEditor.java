package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class PreviousAuctionEditor extends PropertyEditorSupport {

	@Autowired
	RfaEventDao rfaEventDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			RfaEvent rfaEvent = rfaEventDao.findById(value);
			this.setValue(rfaEvent);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((RfaEvent) getValue()).getId();
	}
}
