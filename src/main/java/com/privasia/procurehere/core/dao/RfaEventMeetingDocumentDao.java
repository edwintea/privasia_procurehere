package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;

public interface RfaEventMeetingDocumentDao extends GenericDao<RfaEventMeetingDocument, String> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String id);

}
