package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierCqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;

/**
 * @author Vipul
 */
public interface RfqSupplierCqItemService {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @param eventId TODO
	 * @param tenantId TODO
	 */
	void saveSupplierEventCq(String cqId, String eventId, String tenantId);

	/**
	 * @param items
	 */
	void updateCqItems(List<RfqSupplierCqItem> items);

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
	 * @param itemId
	 * @return
	 */
	RfqSupplierCqItem findCqByEventIdAndCqName(String eventId, String itemId);

	List<RfqSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String loggedInUserTenantId);

	/**
	 * @param list
	 * @param eventId
	 * @param draft
	 * @param loggedInUser TODO
	 * @throws NotAllowedException
	 */
	void updateCqItems(List<RfqSupplierCqItem> list, String eventId, SupplierCqStatus draft, User loggedInUser) throws NotAllowedException;

	void saveSupplierCq(String cqId, String eventId, String supplierId);

	/**
	 * @param items
	 * @param eventId
	 * @param status
	 * @param loggedInUser TODO
	 */
	void draftUpdateCqItems(List<RfqSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser);

}
