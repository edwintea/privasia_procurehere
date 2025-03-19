package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.enums.EventVisibilityType;

public class RfiBase {

	public RfiBase() {
	}

	/**
	 * @param persistObj
	 * @return
	 */
	protected String doNavigation(RfiEvent persistObj) {
		if (Boolean.TRUE == persistObj.getDocumentReq()) {
			return "redirect:createEventDocuments/" + persistObj.getId();
		} else if (persistObj.getEventVisibility() != EventVisibilityType.PUBLIC) {
			return "redirect:addSupplier/" + persistObj.getId();
		} else if (Boolean.TRUE == persistObj.getMeetingReq()) {
			return "redirect:meetingList/" + persistObj.getId();
		} else if (Boolean.TRUE == persistObj.getQuestionnaires()) {
			return "redirect:eventCqList/" + persistObj.getId();
		} else {
			return "redirect:envelopList/" + persistObj.getId();
		}
	}

}