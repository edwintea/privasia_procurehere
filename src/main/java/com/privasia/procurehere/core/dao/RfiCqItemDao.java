package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqItem;

/**
 * @author Ravi
 */
public interface RfiCqItemDao extends GenericCqItemDao<RfiCqItem, String> {

	int CountCqItemsbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiCq> rfiMandatoryCqNamesbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> findAllSupplierAttachRequiredId(String eventId);

}
