package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.StringUtils;
/*
 * Author parveen
 */

@Component
public class SupplierEditor extends PropertyEditorSupport {

	@Autowired
	SupplierDao supplierDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Supplier supplier = supplierDao.findById(value);
			this.setValue(supplier);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Supplier) getValue()).toString();
	}
}
