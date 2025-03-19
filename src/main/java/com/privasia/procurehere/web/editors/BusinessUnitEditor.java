package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Component
public class BusinessUnitEditor extends PropertyEditorSupport {

	@Autowired
	BusinessUnitDao businessUnitDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			BusinessUnit businessUnit = businessUnitDao.findById(value);
			this.setValue(businessUnit);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((BusinessUnit) getValue()).getUnitName();
	}

}
