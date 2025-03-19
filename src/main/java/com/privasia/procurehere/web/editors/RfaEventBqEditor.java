package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Kapil
 */
@Component
public class RfaEventBqEditor extends PropertyEditorSupport {

	@Autowired
	RfaBqDao rfaDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			RfaEventBq rfaEvent = rfaDao.findById(value);
			this.setValue(rfaEvent);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((RfaEventBq) getValue()).toString();
	}
}
