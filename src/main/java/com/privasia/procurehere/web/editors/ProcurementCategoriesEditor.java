package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.ProcurementCategoriesDao;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class ProcurementCategoriesEditor extends PropertyEditorSupport {
	
	@Autowired
	ProcurementCategoriesDao procurementCategoriesDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			ProcurementCategories procurementCategories = procurementCategoriesDao.findById(value);
			this.setValue(procurementCategories);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((ProcurementCategories) getValue()).getProcurementCategories();
	}
}
