package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class FavouriteSupplierEditor extends PropertyEditorSupport {


	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			FavouriteSupplier favouriteSupplier = favoriteSupplierDao.findById(value);
			this.setValue(favouriteSupplier);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((FavouriteSupplier) getValue()).getId();
	}
}
