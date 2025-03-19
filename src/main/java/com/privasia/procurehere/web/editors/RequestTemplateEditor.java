package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.PrTemplateDao;
import com.privasia.procurehere.core.dao.SourcingTemplateDao;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class RequestTemplateEditor extends PropertyEditorSupport {

	@Autowired
	SourcingTemplateDao requestTemplateDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			SourcingFormTemplate requestTemplate = requestTemplateDao.findById(value);
			this.setValue(requestTemplate);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((SourcingFormTemplate) getValue()).toString();
	}

}
