package com.privasia.procurehere.service;

import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author Ravi
 */

public interface RfiCqService {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiCq> findRfiCqForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent findEventForCqByEventId(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfiCq stroreCq(RfiCq cq);

	/**
	 * @param id
	 * @return
	 */
	RfiCq getCqById(String id);

	/**
	 * @param rftCqItem
	 * @return
	 */
	RfiCqItem saveCqItem(RfiCqItem rftCqItem);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfiCqItem> findCqItembyCqId(String cqId);

	/**
	 * @param item
	 */
	void updateCqItem(CqItemPojo item) throws Exception;

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiCq> getNotAssignedCqIdsByEventId(String eventId);

	/**
	 * @param cq
	 */
	void deleteCq(RfiCq cq);

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
	List<RfiCq> getCqByEventId(String id, List<String> cqIds);

	/**
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void isAllowToDeleteCq(String cqId) throws NotAllowedException;

	/**
	 * @param cqItemPojo
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
	RfiCqItem findCqItemForCqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent getRfiEventById(String eventId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfiCqItem> findCqItemsByCqId(String cqId);

	/**
	 * @param parent
	 * @return
	 */
	RfiCqItem getCqItembyCqItemId(String parent);

	/**
	 * @param item
	 * @param cq
	 * @param parent
	 * @return
	 */
	boolean isExists(RfiCqItem item, String cq, String parent);

	/**
	 * @param rftCq
	 * @return
	 */
	boolean isExists(RfiCq rftCq);

	/**
	 * @param cqId
	 * @param level
	 * @return
	 */
	RfiCqItem getParentbyLevelId(String cqId, Integer level);

	/**
	 * @param rfiData
	 * @param eventId
	 * @param cqId
	 * @return
	 * @throws Exception
	 */
	String doExcelDataSave(Map<Integer, Map<Integer, RfiCqItem>> rfiData, String eventId, String cqId) throws Exception;

	/**
	 * @param cqId
	 * @return
	 */
	List<RfiCqItem> getAllCqitemsbyCqId(String cqId);

	/**
	 * @param eventId
	 * @return
	 */
	int CountAllMandatoryCqByEventId(String eventId);

	List<String> getNotSectionAddedRfiCqIdsByEventId(String id);

	String getLeadEvaluatorComment(String itemId);

	boolean updateLeadEvaluatorComment(String itemId, String leadEvaluationComment);

	List<String> getNotSectionItemAddedRfiCqIdsByEventId(String id);

	List<RfiCq> findRfiCqForEventByEnvelopeId(String eventId, String envelopeId);

	List<RfiCq> findRfiCqForEventByEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiCq> findRfiCqForEventByOrder(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RfiCq updateCq(RfiCq cq);

}
