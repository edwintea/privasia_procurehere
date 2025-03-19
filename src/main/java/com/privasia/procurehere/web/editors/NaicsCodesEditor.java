package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class NaicsCodesEditor extends PropertyEditorSupport {

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			NaicsCodes industryCategory = naicsCodesDao.findById(value);
			this.setValue(industryCategory);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((NaicsCodes) getValue()).toString();
	}

}
