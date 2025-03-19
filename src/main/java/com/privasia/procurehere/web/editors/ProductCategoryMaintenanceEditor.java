package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.ProductCategoryMaintenanceDao;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class ProductCategoryMaintenanceEditor extends PropertyEditorSupport {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);
	@Autowired
	ProductCategoryMaintenanceDao productCategoryDao;

	@Override
	public void setAsText(String value) {

		LOG.info("ProductCategory ==========================================" + value);

		if (StringUtils.checkString(value).length() > 0) {
			ProductCategory productCategoryMaintenance = productCategoryDao.findById(value);
			this.setValue(productCategoryMaintenance);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((ProductCategory) getValue()).toString();
	}
}