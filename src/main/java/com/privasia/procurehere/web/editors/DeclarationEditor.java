package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.DeclarationDao;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class DeclarationEditor extends PropertyEditorSupport {

	@Autowired
	DeclarationDao declarationDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			Declaration declaration = declarationDao.findById(value);
			this.setValue(declaration);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((Declaration) getValue()).toString();
	}
}
