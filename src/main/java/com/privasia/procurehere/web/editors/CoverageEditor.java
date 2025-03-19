/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.dao.StateDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Component
public class CoverageEditor extends PropertyEditorSupport {

	@Autowired
	CountryDao countryDao;

	@Autowired
	StateDao stateDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Coverage coverage = null;
			Country country = countryDao.findById(value);
			if (country != null) {
				coverage = new Coverage(country);
			} else {
				State state = stateDao.findById(value);
				coverage = new Coverage(state);
			}
			this.setValue(coverage);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Coverage) getValue()).toString();
	}
}
