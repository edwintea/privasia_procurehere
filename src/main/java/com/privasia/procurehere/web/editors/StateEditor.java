/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.StateDao;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Component
public class StateEditor extends PropertyEditorSupport {

	@Autowired
	StateDao stateDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			State state = stateDao.findById(value);
			this.setValue(state);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((State) getValue()).toString();
	}
}
