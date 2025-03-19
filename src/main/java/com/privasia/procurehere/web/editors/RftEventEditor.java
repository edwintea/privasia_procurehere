package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * 
 * @author RT-Kapil
 *
 */
@Component
public class RftEventEditor extends PropertyEditorSupport {
	
	@Autowired
	RftEventDao rftEventDao; 

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			RftEvent rftEvent = rftEventDao.findById(value);
			this.setValue(rftEvent);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((RftEvent) getValue()).toString();
	}
}
