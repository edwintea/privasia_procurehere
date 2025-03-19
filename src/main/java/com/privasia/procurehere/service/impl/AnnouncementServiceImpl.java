package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Security;
import com.privasia.procurehere.core.dao.AnnouncementDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.entity.Announcement;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.AnnouncementPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AnnouncementService;

@Service
@Transactional(readOnly = true)
public class AnnouncementServiceImpl implements AnnouncementService {

	private static final Logger LOG = LogManager.getLogger(AnnouncementServiceImpl.class);

	@Autowired(required = true)
	AnnouncementDao announcementDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	ServletContext context;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Transactional(readOnly = false)
	@Override
	public void saveAnnouncement(Announcement announcement) {
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Announcement '"+announcement.getTitle()+ "' is created", announcement.getCreatedBy().getTenantId(), announcement.getCreatedBy(), new Date(), ModuleType.Announcement);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		announcementDao.save(announcement);

	}
	
	@PostConstruct
	void init() {
		
	}

	@Override
	public Announcement getAnnouncementById(String id, String tenantId) {
		Announcement announcement = announcementDao.findById(id);
		if (announcement != null) {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimerZone = buyerSettingsDao.getBuyerTimeZoneByTenantId(tenantId);
			if (StringUtils.checkString(strTimerZone).length() > 0) {
				timeZone = TimeZone.getTimeZone(strTimerZone);
			}
			SimpleDateFormat tf = new SimpleDateFormat("HH:mm a");
			tf.setTimeZone(timeZone);
			if (announcement.getAnnouncementStart() != null) {
				announcement.setAnnouncementStartTime(announcement.getAnnouncementStart());
			}
			if (announcement.getAnnouncementEnd() != null) {
				announcement.setAnnouncementEndTime(announcement.getAnnouncementEnd());
			}
		}
		return announcement;
	}

	@Transactional(readOnly = false)
	@Override
	public void updateAnnouncement(Announcement persistObj) {
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Announcement '"+persistObj.getTitle()+ "' is updated ", persistObj.getModifiedBy().getTenantId(), persistObj.getModifiedBy(), new Date(), ModuleType.Announcement);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		announcementDao.update(persistObj);
	}

	@Override
	public List<Announcement> findAnnouncementsByTeanantId(String tenantId, TableDataInput input) {
		LOG.info("getting announcement list by tenantId........");
		return announcementDao.findAnnouncementsByTeanantId(tenantId, input);
	}

	@Transactional(readOnly = false)
	@Override
	public void deleteAnnouncement(Announcement announcement) {
		announcementDao.delete(announcement);
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "Announcement '"+announcement.getTitle()+ "' is deleted ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Announcement);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
	}

	@Override
	public long findTotalFilteredAnnouncementForTenant(String tenantId, TableDataInput input) {
		return announcementDao.findTotalFilteredAnnouncementForTenant(tenantId, input);
	}

	@Override
	public long findTotalActiveAnnouncementForTenant(String tenantId) {
		return announcementDao.findTotalActiveAnnouncementForTenant(tenantId);
	}

	@Override
	public List<Announcement> getAnnouncementListByBuyerId(String tenantId) {
		return announcementDao.getAnnouncementListByBuyerId(tenantId);
	}

	@Override
	public List<AnnouncementPojo> getAnnouncementList() {
		return announcementDao.getAnnouncementList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAnnouncementSMSSentFlag(String id) {
		announcementDao.updateAnnouncementSMSSentFlag(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAnnouncementFaxSentFlag(String id) {
		announcementDao.updateAnnouncementFaxSentFlag(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAnnouncementEmailSentFlag(String id) {
		announcementDao.updateAnnouncementEmailSentFlag(id);
	}

}
