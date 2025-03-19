package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.ProductContractNotifyUsers;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
@Component
public class ProductContractNotifyUsersEditor extends PropertyEditorSupport {

	@Autowired
	UserDao userDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			User user = userDao.findById(value);
			this.setValue(new ProductContractNotifyUsers(user));
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((ProductContractNotifyUsers) getValue()).getUser().getId();
	}
}
