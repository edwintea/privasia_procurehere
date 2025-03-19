package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class BuyerEditorFinance extends PropertyEditorSupport {

	@Autowired
	BuyerDao buyerDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Buyer buyer = buyerDao.findById(value);
			this.setValue(buyer);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Buyer) getValue()).getCompanyName();
	}

}
