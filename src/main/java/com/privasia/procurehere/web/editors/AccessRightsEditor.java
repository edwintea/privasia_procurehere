package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class AccessRightsEditor extends PropertyEditorSupport {

	@Autowired
	AccessRightsDao accessRightsDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			AccessRights accessRights = accessRightsDao.findById(value);
			this.setValue(accessRights);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((AccessRights) getValue()).toString();
	}

}
