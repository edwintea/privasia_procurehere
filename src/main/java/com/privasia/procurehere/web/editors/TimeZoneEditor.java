package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class TimeZoneEditor extends PropertyEditorSupport {

	@Autowired
	TimeZoneDao timeZoneDao;

	@Override
	public void setAsText(String value) {
		//LOG.info("-----------value--------------"+value);
		if (StringUtils.checkString(value).length() > 0) {
			TimeZone timeZone = timeZoneDao.findById(value);
			this.setValue(timeZone);
		}
	}

	@Override
	public String getAsText() {
		//LOG.info("Getting as text for TimeZone : " + getValue());
		return getValue() == null ? "" : ((TimeZone) getValue()).toString();
	}

}
