package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;

/**
 * @author Vipul
 */
public interface RfiSupplierCqItemService {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfiSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @param eventId TODO
	 * @param supplierId TODO
	 */
	void saveSupplierEventCq(String cqId, String eventId, String supplierId);

	/**
	 * @param items
	 */
	void updateCqItems(List<RfiSupplierCqItem> items);

	/**
	 * @param eventId
	 * @param envelopId
	 * @param selectedSuppliers TODO
	 * @param logedUser TODO
	 * @return
	 */
	List<EventEvaluationPojo> getCqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser);

	/**
	 * @param eventId
	 * @param envelopId
	 * @return
	 */
	List<EventEvaluationPojo> getEvaluationDataForCqComparison(String eventId, String envelopId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersForEvaluation(String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	Boolean checkMandatoryToFinishEvent(String supplierId, String eventId) throws NotAllowedException, Exception;

	/**
	 * @param itemId
	 * @param eventId
	 * @param tenantId TODO
	 * @return
	 */
	boolean resetAttachement(String itemId, String eventId, String tenantId);

	/**
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	RfiSupplierCqItem findCqByEventIdAndCqItem(String eventId, String cqId);

	List<RfiSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId);

	/**
	 * @param list
	 * @param eventId
	 * @param status
	 * @param loggedInUser
	 * @throws NotAllowedException
	 */
	void updateCqItems(List<RfiSupplierCqItem> list, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException;

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 */
	void saveSupplierCq(String cqId, String eventId, String supplierId);

	/**
	 * @param items
	 * @param eventId
	 * @param status
	 * @param loggedInUser TODO
	 */
	void draftUpdateCqItems(List<RfiSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser);
}
