package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class IndustryCategoryEditor extends PropertyEditorSupport {

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			IndustryCategory industryCategory = industryCategoryDao.findById(value);
			this.setValue(industryCategory);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((IndustryCategory) getValue()).toString();
	}

}
