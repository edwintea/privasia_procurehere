/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.BuyerPlanDao;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Component
public class BuyerPlanEditor extends PropertyEditorSupport {

	@Autowired
	BuyerPlanDao planDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			BuyerPlan plan = planDao.findById(value);
			this.setValue(plan);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((BuyerPlan) getValue()).getId();
	}
}
