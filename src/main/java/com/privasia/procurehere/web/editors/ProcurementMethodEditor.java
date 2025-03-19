package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.ProcurementMethodDao;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class ProcurementMethodEditor extends PropertyEditorSupport {
	@Autowired
	ProcurementMethodDao procurementMethodDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			ProcurementMethod procurementMethod = procurementMethodDao.findById(value);
			this.setValue(procurementMethod);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((ProcurementMethod) getValue()).getProcurementMethod();
	}

}
