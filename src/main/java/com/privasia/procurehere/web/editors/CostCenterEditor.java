package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class CostCenterEditor extends PropertyEditorSupport {

	@Autowired
	CostCenterDao costCenterDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			CostCenter costCenter = costCenterDao.findById(value);
			this.setValue(costCenter);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((CostCenter) getValue()).getId();
	}

}
