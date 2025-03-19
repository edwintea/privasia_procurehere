/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RegionsDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Regions;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Component
public class RegionEditor extends PropertyEditorSupport {

	@Autowired
	RegionsDao regionsDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Regions region = regionsDao.findById(value);
			this.setValue(region);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Country) getValue()).toString();
	}
}
