package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Announcement;
import com.privasia.procurehere.core.pojo.AnnouncementPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface AnnouncementService {
	/**
	 * @param announcement
	 */
	void saveAnnouncement(Announcement announcement);

	/**
	 * @param id
	 * @param tenantId TODO
	 * @return
	 */
	Announcement getAnnouncementById(String id, String tenantId);

	/**
	 * @param persistObj
	 */
	void updateAnnouncement(Announcement persistObj);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<Announcement> findAnnouncementsByTeanantId(String tenantId, TableDataInput input);

	/**
	 * @param announcement
	 */
	void deleteAnnouncement(Announcement announcement);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredAnnouncementForTenant(String tenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalActiveAnnouncementForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<Announcement> getAnnouncementListByBuyerId(String tenantId);

	List<AnnouncementPojo> getAnnouncementList();

	void updateAnnouncementSMSSentFlag(String id);

	void updateAnnouncementFaxSentFlag(String id);

	void updateAnnouncementEmailSentFlag(String id);

}
