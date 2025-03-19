package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class SupplierFormEditor extends PropertyEditorSupport {

	@Autowired
	SupplierFormDao supplierFormDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			SupplierForm supplierForm = supplierFormDao.findById(value);
			this.setValue(supplierForm);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((SupplierForm) getValue()).toString();
	}
}
