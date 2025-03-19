package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.PrTemplateDao;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class PrTemplateEditor extends PropertyEditorSupport {

	@Autowired
	PrTemplateDao prTemplateDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			PrTemplate prTemplate = prTemplateDao.findById(value);
			this.setValue(prTemplate);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((PrTemplate) getValue()).toString();
	}

}
