package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;

public interface RfiEventMeetingDocumentDao extends GenericDao<RfiEventMeetingDocument, String> {
	/**
	 * @param id
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String id);

}
