/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Component
public class CountryEditor extends PropertyEditorSupport {

	@Autowired
	CountryDao countryDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Country country = countryDao.findById(value);
			this.setValue(country);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Country) getValue()).toString();
	}
}
