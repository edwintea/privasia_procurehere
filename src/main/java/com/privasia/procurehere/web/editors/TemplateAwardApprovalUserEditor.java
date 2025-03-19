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
import com.privasia.procurehere.core.entity.TemplateAwardApprovalUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Aishwarya
 *
 */
@Component
public class TemplateAwardApprovalUserEditor extends PropertyEditorSupport {
	
	private static final Logger LOG = LogManager.getLogger(TemplateAwardApprovalUserEditor.class);

	@Autowired
	UserDao userDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			LOG.info("Set As Text Appro :" + value);
			User user = userDao.findById(value);
			this.setValue(new TemplateAwardApprovalUser(user));
		}
	}

	@Override
	public String getAsText() {
		LOG.info("Get As Text Appro :" + ((TemplateAwardApprovalUser) getValue()));
		return getValue() == null ? "" : ((TemplateAwardApprovalUser) getValue()).getUser().getId();
	}

}
