package com.privasia.procurehere.service;

import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author Ravi
 */

public interface RfqCqService {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqCq> findCqForEvent(String eventId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfqCqItem> findCqItemsForCq(String cqId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent findEventForCqByEventId(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfqCq stroreCq(RfqCq cq);

	/**
	 * @param id
	 * @return
	 */
	RfqCq getCqById(String id);

	/**
	 * @param rftCqItem
	 * @return
	 */
	RfqCqItem saveCqItem(RfqCqItem rftCqItem);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfqCqItem> findCqbyCqId(String cqId);

	/**
	 * @param parent
	 * @return
	 */
	RfqCqItem getCqItembyCqItemId(String parent);

	/**
	 * @param rftCqItem
	 * @param cqOptions
	 * @throws ApplicationException
	 */
	void updateCqItem(CqItemPojo item) throws ApplicationException;

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqCq> getNotAssignedCqIdsByEventId(String eventId);

	/**
	 * @param cq
	 */
	void deleteCq(RfqCq cq);

	/**
	 * @param cqItemIds
	 * @param cqId TODO
	 * @throws NotAllowedException
	 */
	void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException;

	/**
	 * @param id
	 * @param cqIds
	 * @return
	 */
	List<RfqCq> getCqByEventId(String id, List<String> cqIds);

	/**
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void isAllowToDeleteCq(String cqId) throws NotAllowedException;

	/**
	 * @param rftCqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderCqItems(CqItemPojo cqItemPojo) throws NotAllowedException;

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllCqs(String eventId, String eventRequirement);

	/**
	 * @param eventId
	 * @return
	 */
	RfqCqItem findCqItemForCqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent getEventById(String eventId);

	/**
	 * @param rftCq
	 * @return
	 */
	boolean isExists(RfqCq rftCq);

	/**
	 * @param rftCqItem
	 * @param cqId
	 * @param parent
	 * @return
	 */
	boolean isExists(RfqCqItem rftCqItem, String cqId, String parent);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfqCqItem> getAllCqitemsbyCqId(String cqId);

	/**
	 * @param rfqData
	 * @param eventId
	 * @param cqId
	 * @return
	 * @throws Exception
	 */
	String doExcelDataSave(Map<Integer, Map<Integer, RfqCqItem>> rfqData, String eventId, String cqId) throws Exception;

	/**
	 * @param eventId
	 * @return
	 */
	int CountAllMandatoryCqByEventId(String eventId);

	List<String> getNotSectionAddedRfqCqIdsByEventId(String id);

	String getLeadEvaluatorComment(String itemId);

	boolean updateLeadEvaluatorComment(String itemId, String leadEvaluationComment);

	List<String> getNotSectionItemAddedRfqCqIdsByEventId(String id);

	List<RfqCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqCq> findCqForEventByOrder(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfqCq updateCq(RfqCq cq);

}
