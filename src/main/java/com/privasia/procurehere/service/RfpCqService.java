package com.privasia.procurehere.service;

import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author Ravi
 */

public interface RfpCqService {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpCq> findCqForEvent(String eventId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfpCqItem> findCqItemsForCq(String cqId);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent findEventForCqByEventId(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfpCq stroreCq(RfpCq cq);

	/**
	 * @param id
	 * @return
	 */
	RfpCq getCqById(String id);

	/**
	 * @param rftCqItem
	 * @return
	 */
	RfpCqItem saveCqItem(RfpCqItem rftCqItem);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfpCqItem> findCqbyCqId(String cqId);

	/**
	 * @param parent
	 * @return
	 */
	RfpCqItem getCqItembyCqItemId(String parent);

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
	List<RfpCq> getNotAssignedCqIdsByEventId(String eventId);

	/**
	 * @param cq
	 */
	void deleteCq(RfpCq cq);

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
	List<RfpCq> getCqByEventId(String id, List<String> cqIds);

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
	RfpCqItem findCqItemForCqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent getEventById(String eventId);

	boolean isExists(RfpCq rftCq);

	boolean isExists(RfpCqItem rftCqItem, String cqId, String parent);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfpCqItem> getAllCqitemsbyCqId(String cqId);

	/**
	 * @param cqId
	 * @param level
	 * @return
	 */
	RfpCqItem getParentbyLevelId(String cqId, Integer level);

	/**
	 * @param data
	 * @param eventId
	 * @param cqId
	 * @return
	 * @throws Exception
	 */
	String doExcelDataSave(Map<Integer, Map<Integer, RfpCqItem>> data, String eventId, String cqId) throws Exception;

	/**
	 * @param eventId
	 * @return
	 */
	int CountAllMandatoryCqByEventId(String eventId);

	List<String> getNotSectionAddedRfpCqIdsByEventId(String eventId);

	boolean updateLeadEvaluatorComment(String itemId, String leadEvaluationComment);

	String getLeadEvaluatorComment(String itemId);

	List<String> getNotSectionItemAddedRfpCqIdsByEventId(String id);

	List<RfpCq> findCqForEventByEnvelopeId(String eventId, String envelopeId);

	List<RfpCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpCq> findCqForEventByOrder(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfpCq updateCq(RfpCq cq);

}
