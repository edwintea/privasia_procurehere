package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Ravi
 */
@Component
public class RfiEventEditor extends PropertyEditorSupport {

	@Autowired
	RfiEventDao rfiEventDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			RfiEvent rfiEvent = rfiEventDao.findById(value);
			this.setValue(rfiEvent);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((RfiEvent) getValue()).toString();
	}
}
