package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * 
 * @author RT-Kapil
 *
 */
@Component
public class RfpEventEditor extends PropertyEditorSupport {
	
	@Autowired
	RfpEventDao rfpEventDao; 

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			RfpEvent rftEvent = rfpEventDao.findById(value);
			this.setValue(rftEvent);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((RfpEvent) getValue()).toString();
	}
}
