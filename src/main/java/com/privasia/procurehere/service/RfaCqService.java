package com.privasia.procurehere.service;

import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author RT-Kapil
 */

public interface RfaCqService {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaCq> findRfaCqForEvent(String eventId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfaCqItem> findRfaCqItemsForCq(String cqId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent findEventForCqByEventId(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfaCq stroreRfaCq(RfaCq cq);

	/**
	 * @param id
	 * @return
	 */
	RfaCq getRfaCqById(String id);

	/**
	 * @param rftCqItem
	 * @return
	 */
	RfaCqItem saveRfaCqItem(RfaCqItem rfaCqItem);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfaCqItem> findRfaCqbyCqId(String cqId);

	/**
	 * @param parent
	 * @return
	 */
	RfaCqItem getCqItembyCqItemId(String parent);

	/**
	 * @param rfaCqItem
	 * @param cqOptions
	 */
	void updateCqItem(CqItemPojo item);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaCq> getNotAssignedRfaCqIdsByEventId(String eventId);

	/**
	 * @param cq
	 */
	void deleteCq(RfaCq cq);

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
	List<RfaCq> getRfaCqByEventId(String id, List<String> cqIds);

	/**
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void isAllowToDeleteCq(String cqId) throws NotAllowedException;

	/**
	 * @param rfaCqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderCqItems(CqItemPojo rfaCqItemPojo) throws NotAllowedException;

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllCqs(String eventId, String eventRequirement);

	/**
	 * @param eventId
	 * @return
	 */
	RfaCqItem findRfaCqItemForCqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent getRfaEventById(String eventId);

	/**
	 * @param rfaCq
	 * @return
	 */
	boolean isExists(RfaCq rfaCq);

	/**
	 * @param item
	 * @param cq
	 * @param parent
	 * @return
	 */
	boolean isExists(RfaCqItem item, String cq, String parent);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfaCqItem> getAllCqitemsbyCqId(String cqId);

	/**
	 * @param cqId
	 * @param level
	 * @return
	 */
	RfaCqItem getParentbyLevelId(String cqId, Integer level);

	/**
	 * @param rfaData
	 * @param eventId
	 * @param cqId
	 * @return TODO
	 * @throws Exception
	 */
	String doExcelDataSave(Map<Integer, Map<Integer, RfaCqItem>> rfaData, String eventId, String cqId) throws Exception;

	/**
	 * @param cqId
	 * @return
	 */
	List<RfaCqItem> findCqItemsForCq(String cqId);

	/**
	 * @param id
	 * @return
	 */
	RfaCq getCqById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	int CountAllMandatoryCqByEventId(String eventId);

	List<String> getNotSectionAddedRfaCqIdsByEventId(String id);

	/**
	 * @param cqItemId
	 * @param leadComment
	 * @return
	 */
	boolean updateLeadEvaluatorComment(String cqItemId, String leadComment);

	String getLeadEvaluatorComment(String itemId);

	List<String> getNotSectionItemAddedRfaCqIdsByEventId(String id);

	List<RfaCq> findRfaCqForEventByEnvelopeId(String eventId, String envelopeId);

	List<RfaCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaCq> findRfaCqForEventByOrder(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfaCq updatCq(RfaCq cq);

}
