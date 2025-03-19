package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.pojo.GlobalSearchPojo;

public interface RfxViewDao extends GenericDao<RfxView, String> {

	/**
	 * @return
	 */
	List<RfxView> getAllApprovedEvents();

	/**
	 * @return
	 */
	List<RfxView> getAllActiveEvents();

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param isSupplier
	 * @param userId TODO
	 * @return
	 */
	List<GlobalSearchPojo> getAllRfxEventFromGlobalSearch(String searchVal, String tenantId, boolean isSupplier, String userId);

	/**
	 * @param suppId
	 * @param buyerId TODO
	 * @return
	 */
	long totalEventInvitedSupplier(String suppId, String buyerId);

	/**
	 * @param suppId
	 * @param buyerId TODO
	 * @return
	 */
	long totalEventParticipatedSupplier(String suppId, String buyerId);

	/**
	 * @param suppId
	 * @return
	 */
	long totalEventAwardedSupplier(String suppId);

	/**
	 * @return
	 */
	List<RfxView> getEventRemindersForNotification();

	List<RfxView> getAllApprovedEventsforJob();

	/**
	 * @param id
	 * @return
	 */
	RfxView getRfxViewById(String id);

	long getTotalAwardedSupplirForBuyer(String supplierId, String tenantId);

	/**
	 * @param suppId
	 * @param buyerId
	 * @return
	 */
	long totalEventAwardedSupplierAndBuyer(String suppId, String buyerId);

}
