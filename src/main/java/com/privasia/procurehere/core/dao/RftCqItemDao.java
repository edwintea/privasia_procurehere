package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqItem;

/**
 * @author Ravi
 */
public interface RftCqItemDao extends GenericCqItemDao<RftCqItem, String> {

	int CountCqItemsbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftCq> rftMandatoryCqNamesbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> findAllSupplierAttachRequiredId(String eventId);

}
