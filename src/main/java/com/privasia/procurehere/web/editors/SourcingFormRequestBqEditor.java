package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.SourcingFormRequestBqDao;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Pooja
 */
@Component
public class SourcingFormRequestBqEditor extends PropertyEditorSupport {

	@Autowired
	SourcingFormRequestBqDao sourcingFormRequestBqDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			SourcingFormRequestBq sourcingFormRequestBq = sourcingFormRequestBqDao.findById(value);
			this.setValue(sourcingFormRequestBq);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((SourcingFormRequestBq) getValue()).toString();
	}
}
