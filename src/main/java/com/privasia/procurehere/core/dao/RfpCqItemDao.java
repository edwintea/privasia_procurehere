package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqItem;

/**
 * @author Ravi
 */
public interface RfpCqItemDao extends GenericCqItemDao<RfpCqItem, String> {

	int CountCqItemsbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpCq> rfpMandatoryCqNamesbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> findAllSupplierAttachRequiredId(String eventId);

}
