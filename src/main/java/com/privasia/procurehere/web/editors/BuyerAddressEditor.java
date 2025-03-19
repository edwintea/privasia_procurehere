package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author parveen
 */

import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.BuyerAddressDao;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class BuyerAddressEditor extends PropertyEditorSupport {

	@Autowired
	BuyerAddressDao buyerAddressDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			BuyerAddress buyerAddress = buyerAddressDao.getBuyerAddressAndStateAndCountryById(value);
			buyerAddress.getState().getStateName();
			buyerAddress.getState().getCountry().getCountryName();
			this.setValue(buyerAddress);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((BuyerAddress) getValue()).getId();
	}

}
