package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.utils.StringUtils;

@Component
public class CqOptionEditor extends PropertyEditorSupport {

	/*
	 * @Autowired RftCqOptionDao rftCqOptionDao;
	 */
	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			CqOption cqOption = new CqOption();
			cqOption.setId(value);
			this.setValue(cqOption);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((CqOption) getValue()).getId();
	}
}
