package com.privasia.procurehere.web.editors;

import com.privasia.procurehere.core.dao.PoTeamMemberDao;
import com.privasia.procurehere.core.entity.PoTeamMember;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * @author parveen
 */
@Component
public class PoTeamMemberEditor extends PropertyEditorSupport {

	@Autowired
	PoTeamMemberDao poTeamMemberDao;

	@Override
	public void setAsText(String value) {
		if (StringUtils.checkString(value).length() > 0) {
			PoTeamMember poTeamMember = poTeamMemberDao.findById(value);
			this.setValue(poTeamMember);
		}
	}

	@Override
	public String getAsText() {
		return getValue() == null ? "" : ((PoTeamMember) getValue()).toString();
	}

}
