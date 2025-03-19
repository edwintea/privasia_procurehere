package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class SpTemplateEditor extends PropertyEditorSupport {

	@Autowired
	SupplierPerformanceTemplateDao supplierPerformanceTemplateDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			SupplierPerformanceTemplate spTemplate = supplierPerformanceTemplateDao.findById(value);
			this.setValue(spTemplate);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((SupplierPerformanceTemplate) getValue()).toString();
	}

}