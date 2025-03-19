/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.SupplierPlanDao;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Component
public class SupplierPlanEditor extends PropertyEditorSupport {

	@Autowired
	SupplierPlanDao planDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			SupplierPlan plan = planDao.findById(value);
			this.setValue(plan);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((SupplierPlan) getValue()).getId();
	}
}
