package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.FinanceCompanyDao;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class FinanceCompanyEditor extends PropertyEditorSupport {

	@Autowired
	FinanceCompanyDao financeCompanyDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			FinanceCompany financeCompany = financeCompanyDao.findById(value);
			this.setValue(financeCompany);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((FinanceCompany) getValue()).getCompanyName();
	}

}
