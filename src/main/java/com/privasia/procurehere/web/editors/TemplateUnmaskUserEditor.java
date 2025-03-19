package com.privasia.procurehere.web.editors;

/**
 * @author pavan 
 */
import java.beans.PropertyEditorSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.TemplateUnmaskUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class TemplateUnmaskUserEditor  extends PropertyEditorSupport {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TemplateUnmaskUser.class);

	@Autowired
	UserDao userDao;

	@Override
	public void setAsText(String value) {
		
		LOG.info("Set As Text : " + value);
		
		if (StringUtils.checkString(value).length() > 0) {
			User user = userDao.findById(value);
			this.setValue(new TemplateUnmaskUser(user));
		}
	}

	@Override
	public String getAsText() {
		LOG.info("Get As Text : " + ((TemplateUnmaskUser) getValue()));
		return getValue() == null ? "" : ((TemplateUnmaskUser) getValue()).getUser().getId();
	}
}
