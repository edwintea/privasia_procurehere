package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;

/**
 * @author Vipul
 */
public interface RftSupplierCqItemService {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RftSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId);

	void saveSupplierEventCq(String cqId, String eventId, String tenantID);

	/**
	 * @param eventId
	 * @param envelopId
	 * @param selectedSuppliers TODO
	 * @param logedUser TODO
	 * @return
	 */
	List<EventEvaluationPojo> getCqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser);

	/**
	 * @param items
	 */
	void updateCqItems(List<RftSupplierCqItem> items);

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
	List<RftSupplierCqItem> getAllRequiredSupplierCqItemByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RftSupplierCqOption> getRequiredOptionValueByCqItemId(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int CountAllMandatorySupplierCqItemByEventId(String supplierId, String eventId);

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
	RftSupplierCqItem findCqByEventIdAndCqName(String eventId, String itemId);

	List<RftSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String loggedInUserTenantId);

	/**
	 * @param list
	 * @param eventId
	 * @param status
	 * @param loggedInUser TODO
	 * @throws NotAllowedException 
	 */
	void updateCqItems(List<RftSupplierCqItem> list, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException;

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
	void draftUpdateCqItems(List<RftSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser);

}
