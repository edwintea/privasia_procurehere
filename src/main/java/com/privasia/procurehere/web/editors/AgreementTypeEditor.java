/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.AgreementTypeDao;
import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Aishwarya
 *
 */
@Component
public class AgreementTypeEditor extends PropertyEditorSupport{

	@Autowired
	AgreementTypeDao agreementTypeDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			AgreementType agreementType = agreementTypeDao.findById(value);
			this.setValue(agreementType);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((AgreementType) getValue()).getId();
	}

}
