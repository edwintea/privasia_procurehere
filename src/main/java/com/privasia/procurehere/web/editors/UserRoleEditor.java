package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class UserRoleEditor extends PropertyEditorSupport {

	@Autowired
	UserRoleDao userRoleDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			UserRole userRole = userRoleDao.findById(value);
			this.setValue(userRole);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((UserRole) getValue()).toString();
	}

}
