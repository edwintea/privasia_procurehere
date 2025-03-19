package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class CurrencyEditor extends PropertyEditorSupport {

	@Autowired
	CurrencyDao currencyDao;
	
	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Currency currency = currencyDao.findById(value);
			this.setValue(currency);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Currency) getValue()).toString();
	}
}
