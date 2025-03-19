package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Announcement;
import com.privasia.procurehere.core.pojo.AnnouncementPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface AnnouncementDao extends GenericDao<Announcement, String> {
	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Announcement> findAnnouncementsByTeanantId(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredAnnouncementForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalActiveAnnouncementForTenant(String tenantId);

	/**
	 * @param buyerId
	 * @return
	 */
	List<Announcement> getAnnouncementListByBuyerId(String tenantId);

	List<AnnouncementPojo> getAnnouncementList();

	void updateAnnouncementSMSSentFlag(String id);

	void updateAnnouncementFaxSentFlag(String id);

	void updateAnnouncementEmailSentFlag(String id);

	Announcement getAnnouncementById(String id);

	void deleteBuyerAllAoucment(String buyerID);

}
