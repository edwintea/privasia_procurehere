package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqItem;

/**
 * @author Ravi
 */
public interface RfqCqItemDao extends GenericCqItemDao<RfqCqItem, String> {

	int CountCqItemsbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqCq> rfqMandatoryCqNamesbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> findAllSupplierAttachRequiredId(String eventId);

}
