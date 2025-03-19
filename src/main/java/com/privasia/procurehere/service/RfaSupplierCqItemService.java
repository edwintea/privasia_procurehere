package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierCqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;

/**
 * @author Vipul
 */
public interface RfaSupplierCqItemService {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @param eventId TODO
	 * @param supplierId TODO
	 */
	void saveSupplierEventCq(String cqId, String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	EventEvaluationPojo getEvaluationData(String eventId, String cqId);

	/**
	 * @param items
	 */
	void updateCqItems(List<RfaSupplierCqItem> items);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfaSupplierCqItem> getAllRequiredSupplierCqItemByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param id
	 * @param supplierId
	 * @return
	 */

	List<RfaSupplierCqOption> getRequiredOptionValueByCqItemId(String id);

	/**
	 * @param itemId
	 * @param eventId
	 * @param tenantId TODO
	 * @return
	 */
	boolean resetAttachement(String itemId, String eventId, String tenantId);

	/**
	 * @param eventId
	 * @param itemId
	 * @return
	 */
	RfaSupplierCqItem findCqByEventIdAndCqName(String eventId, String itemId);

	List<EventEvaluationPojo> getCqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser);

	List<EventEvaluationPojo> getEvaluationDataForCqComparison(String eventId, String envelopId);

	List<RfaSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String loggedInUserTenantId);

	/**
	 * @param items
	 * @param eventId
	 * @param status
	 * @param loggedInUser TODO
	 * @throws NotAllowedException 
	 */
	void updateCqItems(List<RfaSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException;

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 */
	void saveSupplierCq(String cqId, String eventId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int findRfaRequiredCqCountBySupplierId(String supplierId, String eventId);

	/**
	 * 
	 * @param items
	 * @param eventId
	 * @param status
	 * @param loggedInUser TODO
	 */
	void draftUpdateCqItems(List<RfaSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser);
}
