/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.CompanyStatusDao;
import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Component
public class CompanyStatusEditor extends PropertyEditorSupport {

	
	@Autowired
	CompanyStatusDao statusDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			CompanyStatus companyStatus = statusDao.findById(value);
			this.setValue(companyStatus);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((CompanyStatus) getValue()).getId();
	}
}
