package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfxTemplateDao;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.utils.StringUtils;


@Component
public class RfxTemplateEditor extends PropertyEditorSupport {

	@Autowired
	RfxTemplateDao rfxTemplateDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			RfxTemplate rfxTemplate = rfxTemplateDao.findById(value);
			this.setValue(rfxTemplate);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((RfxTemplate) getValue()).toString();
	}

}
