/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.PaymentTermsDao;
import com.privasia.procurehere.core.entity.PaymentTermes;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 *
 */

@Component
public class PaymentTermesEditor extends PropertyEditorSupport {

	@Autowired
	PaymentTermsDao paymentTermesDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			PaymentTermes country = paymentTermesDao.findById(value);
			this.setValue(country);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((PaymentTermes) getValue()).toString();
	}
}
