/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.PlanDao;
import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@Component
public class PlanEditor extends PropertyEditorSupport {

	@Autowired
	PlanDao planDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Plan plan = planDao.findById(value);
			this.setValue(plan);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Plan) getValue()).getId();
	}
}
