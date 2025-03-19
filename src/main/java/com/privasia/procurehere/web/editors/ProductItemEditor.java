package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.ProductListMaintenanceDao;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Component
public class ProductItemEditor extends PropertyEditorSupport {

	@Autowired
	ProductListMaintenanceDao productListMaintenanceDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			ProductItem productItem = productListMaintenanceDao.findById(value);
			this.setValue(productItem);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((ProductItem) getValue()).toString();
	}
}