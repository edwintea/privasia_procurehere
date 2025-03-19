package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEventMeetingDocument;

public interface RfpEventMeetingDocumentDao extends GenericDao<RfpEventMeetingDocument, String> {

	/**
	 * @param id
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String id);

}
