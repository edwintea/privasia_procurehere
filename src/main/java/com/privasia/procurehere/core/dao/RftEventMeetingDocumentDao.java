package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEventMeetingDocument;

public interface RftEventMeetingDocumentDao extends GenericDao<RftEventMeetingDocument, String> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String id);

}
