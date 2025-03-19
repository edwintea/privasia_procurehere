package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqItem;

/**
 * @author RT-Kapil
 */
public interface RfaCqItemDao extends GenericCqItemDao<RfaCqItem, String> {

	int CountCqItemsbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaCq> rfaMandatoryCqNamesbyEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> findAllSupplierAttachRequiredId(String eventId);

}
