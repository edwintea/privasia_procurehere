package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierCqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;

/**
 * @author Vipul
 */
public interface RfpSupplierCqItemService {

	List<RfpSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @param eventId TODO
	 * @param supplierId TODO
	 */
	void saveSupplierEventCq(String cqId, String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param cqId
	 * @param selectedSuppliers TODO
	 * @param logedUser TODO
	 * @return
	 */
	List<EventEvaluationPojo> getCqEvaluationData(String eventId, String cqId, List<Supplier> selectedSuppliers, User logedUser);

	/**
	 * @param items
	 */
	void updateCqItems(List<RfpSupplierCqItem> items);

	/**
	 * @param eventId
	 * @param envelopId
	 * @return
	 */
	List<EventEvaluationPojo> getEvaluationDataForCqComparison(String eventId, String envelopId);

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
	RfpSupplierCqItem findCqByEventIdAndCqName(String eventId, String cqId);

	List<RfpSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String loggedInUserTenantId);

	/**
	 * @param list
	 * @param eventId
	 * @param status
	 * @param loggedInUser TODO
	 * @throws NotAllowedException 
	 */
	void updateCqItems(List<RfpSupplierCqItem> list, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException;

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
	void draftUpdateCqItems(List<RfpSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser);

}
