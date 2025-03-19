package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sana
 */
@Component
public class SupplierFormItemOptionEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			SupplierFormItemOption cqOption = new SupplierFormItemOption();
			cqOption.setId(value);
			this.setValue(cqOption);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((SupplierFormItemOption) getValue()).getId();
	}
}
