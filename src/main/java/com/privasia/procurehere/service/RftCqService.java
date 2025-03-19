package com.privasia.procurehere.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author Ravi
 */

public interface RftCqService {

	/**
	 * @param eventId
	 * @return
	 */
	List<RftCq> findRftCqForEvent(String eventId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RftCqItem> findRftCqItemsForCq(String cqId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent findEventForCqByEventId(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RftCq stroreRftCq(RftCq cq);

	/**
	 * @param id
	 * @return
	 */
	RftCq getRftCqById(String id);

	/**
	 * @param rftCqItem
	 * @return
	 */
	RftCqItem saveRftCqItem(RftCqItem rftCqItem);

	/**
	 * @param cqId
	 * @return
	 */
	List<RftCqItem> findRftCqbyCqId(String cqId);

	/**
	 * @param parent
	 * @return
	 */
	RftCqItem getCqItembyCqItemId(String parent);

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
	List<RftCq> getNotAssignedRftCqIdsByEventId(String eventId);

	/**
	 * @param cq
	 */
	void deleteCq(RftCq cq);

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
	List<RftCq> getRftCqByEventId(String id, List<String> cqIds);

	/**
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void isAllowToDeleteCq(String cqId) throws NotAllowedException;

	/**
	 * @param rftCqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderCqItems(CqItemPojo rftCqItemPojo) throws NotAllowedException;

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllCqs(String eventId, String eventRequirement);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent getRftEventById(String eventId);

	/**
	 * @param rftCq
	 * @return
	 */
	boolean isExists(RftCq rftCq);

	/**
	 * @param rftCqItem
	 * @param cqId
	 * @param parent
	 * @return
	 */
	boolean isExists(RftCqItem rftCqItem, String cqId, String parent);

	/**
	 * @param bqId
	 * @return
	 */
	List<RftCqItem> getAllCqitemsbyCqId(String cqId);

	/**
	 * @param cqId
	 * @param level
	 * @return
	 */
	RftCqItem getParentbyLevelId(String cqId, Integer level);

	/**
	 * @param dataMap
	 * @param eventId
	 * @param cqId
	 * @return
	 * @throws Exception
	 */
	String doExcelDataSave(Map<Integer, Map<Integer, RftCqItem>> dataMap, String eventId, String cqId) throws Exception;

	/**
	 * @param eventId
	 * @return
	 */
	int CountAllMandatoryCqByEventId(String eventId);

	List<String> getNotSectionAddedRftCqIdsByEventId(String eventId);

	/**
	 * @param itemId
	 * @param leadEvaluationComment
	 * @return
	 */
	boolean updateLeadEvaluatorComment(String itemId, String leadEvaluationComment);

	String getLeadEvaluatorComment(String itemId);

	/**
	 * @param cqid
	 * @param type
	 * @return
	 * @throws IOException
	 */
	XSSFWorkbook eventcqDownloader(String cqid, RfxTypes type) throws IOException;

	List<String> getNotSectionItemAddedRftCqIdsByEventId(String id);

	List<RftCq> findRftCqForEventByEnvelopeId(String eventId, String envelopeId);

	List<RftCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftCq> findRftCqForEventByOrder(String eventId);

	/**
	 * @param cq
	 * @return
	 */
	RftCq updateCq(RftCq cq);

}
