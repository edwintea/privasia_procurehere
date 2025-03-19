package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.UomDao;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class UomEditor extends PropertyEditorSupport {

	@Autowired
	UomDao uomDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Uom uom = uomDao.findById(value);
			this.setValue(uom);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Uom) getValue()).toString();
	}

}
