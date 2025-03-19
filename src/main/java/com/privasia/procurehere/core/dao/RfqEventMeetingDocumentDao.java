package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEventMeetingDocument;

public interface RfqEventMeetingDocumentDao extends GenericDao<RfqEventMeetingDocument, String> {

	/**
	 * @param id
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String id);

}
