/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.RfaSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 *
 */
@Component
public class RfaSuspensionApprovalEditor extends PropertyEditorSupport {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(RfaSuspensionApprovalEditor.class);

	@Autowired
	UserDao userDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			User user = userDao.findById(value);
			this.setValue(new RfaSuspensionApprovalUser(user));
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((RfaSuspensionApprovalUser) getValue()).getUser().getId();
	}
}
