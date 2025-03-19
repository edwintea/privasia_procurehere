/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.GroupCodeDao;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 *
 */
@Component
public class GroupCodeEditor extends PropertyEditorSupport{

	@Autowired
	GroupCodeDao groupCodeDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			GroupCode groupCode = groupCodeDao.findById(value);
			this.setValue(groupCode);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((GroupCode) getValue()).getId();
	}

}
