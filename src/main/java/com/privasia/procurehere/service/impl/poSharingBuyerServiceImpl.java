package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PoSharingBuyerDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.FinanceNotificationMessage;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoAudit;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PoAuditType;
import com.privasia.procurehere.core.enums.PoAuditVisibilityType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.FinanceSettingsService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PoAuditService;
import com.privasia.procurehere.service.PoFinanceService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.service.PoSharingBuyerService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.UserService;

@Service
@Transactional(readOnly = true)
public class poSharingBuyerServiceImpl implements PoSharingBuyerService {

	protected static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	PoSharingBuyerDao poSharingBuyerDao;

	@Autowired
	PrService prService;

	@Autowired
	PoFinanceService poFinanceService;

	@Autowired
	UserService userService;
	@Autowired
	FinanceSettingsService financeSettingsService;
	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	PoService poService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PoAuditService poAuditService;

	@Override
	@Transactional(readOnly = false)
	public void clearBuyerSetting(String supplierId) {
		poSharingBuyerDao.clearBuyerSetting(supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void shareAllPotoFinaceCompany(Supplier supplier, FinanceCompany financeCompany, Buyer buyer, User loggedInUser) {

		List<Po> poList = poService.findAllPoforSharingAll(supplier.getId(), buyer != null ? buyer.getId() : null);

		LOG.info("list size for share PO to finance---->" + poList.size());

		if (CollectionUtil.isNotEmpty(poList)) {
			for (Po po : poList) {
				FinancePo financeCompanyPo = poFinanceService.getPoFinanceByPrIdAndSupID(po.getId(), supplier.getId(), financeCompany.getId());
				LOG.info("poId---->" + po.getId());
				if (StringUtils.checkString(financeCompanyPo.getId()).length() == 0) {
					FinancePo financePo = new FinancePo();
					LOG.info("Sharing Po:" + po.getId());

					financePo.setPo(po);
					financePo.setFinanceCompany(financeCompany);
					financePo.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					financePo.setReferenceNum(referenceNumber(financeCompany));
					financePo.setCreatedDate(new Date());
					financePo.setFinancePoStatus(FinancePoStatus.NEW);
					financePo.setFinancePoType(FinancePoType.SHARED);
					financePo.setSharedDate(new Date());
					financePo = poFinanceService.saveFinancePo(financePo);
					sendPoShareEmailsToFinance(userService.getAdminUserForFinance(financeCompany), po);

					try {
						PoAudit poAudit = new PoAudit();
						poAudit.setAction(PoAuditType.SHARED);
						poAudit.setActionBy(loggedInUser);
						poAudit.setActionDate(new Date());
						poAudit.setSupplier(loggedInUser.getSupplier());
						poAudit.setDescription(messageSource.getMessage("po.supplierAudit.sharedPo", new Object[] { po.getPoNumber(), financeCompany.getCompanyName() }, Global.LOCALE));
						poAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
						poAudit.setPo(po);
						poAuditService.save(poAudit);
					} catch (Exception e) {
						LOG.error("Error while saving po audit:" + e.getMessage(), e);
					}
				}

			}
		}
	}

	private void sendPoShareEmailsToFinance(User mailTo, Po po) {

		LOG.info("Sending PO share email to--------------------------------> (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
		try {
			String subject = "PO Share";
			String url = APP_URL + "/finance/financePOView/" + po.getId();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", mailTo.getName());
			map.put("message", "");
			map.put("pr", po);
			map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(po.getId())));
			map.put("prReferanceNumber", (po.getPoNumber() == null ? "" : po.getPoNumber()));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByFinanceSettings(mailTo.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("supplierName", "shared by " + SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName());
			if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
				sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.FINANCEPO_SHARE_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
			}

			sendDashboardNotificationForFinance(mailTo, url, subject, "New PO shared by " + SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), NotificationType.CREATED_MESSAGE);
		} catch (Exception e) {
			LOG.error("Error while Sending PO Created Email:" + e.getMessage(), e);
		}

	}

	private String referenceNumber(FinanceCompany financeCompany) {
		String referenceNumber = "";
		Integer length = 6;
		String seqNo = "1";
		if (financeCompany != null) {
			FinanceCompanySettings financeCompanySettings = financeSettingsService.getFinanceSettingsByTenantId(financeCompany.getId());
			if (financeCompanySettings != null) {
				if (StringUtils.checkString(financeCompanySettings.getPoSequencePrefix()).length() > 0) {
					referenceNumber += financeCompanySettings.getPoSequencePrefix();
				}
				if (StringUtils.checkString(financeCompanySettings.getPoSequenceNumber()).length() > 0) {
					seqNo = financeCompanySettings.getPoSequenceNumber();
				}
				if (financeCompanySettings.getPoSequenceLength() != null && financeCompanySettings.getPoSequenceLength() != 0) {
					length = financeCompanySettings.getPoSequenceLength();
				}

				referenceNumber += StringUtils.lpad(seqNo, length, '0');
				LOG.info("-----Updating settings-----------");
				int sequanceNum = Integer.parseInt((seqNo)) + 1;
				financeCompanySettings.setPoSequenceNumber("" + sequanceNum);
				financeSettingsService.updateFinanceSettingsSeqNumber(financeCompanySettings);
			} else {
				LOG.info("Finance Setting is null");
			}

		} else {
			LOG.info("Finance  is null");
		}
		LOG.info("-----referenceNumber----------->" + referenceNumber);
		return referenceNumber;
	}

	private String getTimeZoneByFinanceSettings(String tenantId, String timeZone) {

		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = financeSettingsService.getFinanceTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;

	}

	private String getBusinessUnitname(String poId) {
		String displayName = null;
		displayName = poService.getBusinessUnitname(poId);
		return StringUtils.checkString(displayName);
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private void sendDashboardNotificationForFinance(User messageTo, String url, String subject, String notificationMessage, NotificationType notificationType) {
		FinanceNotificationMessage message = new FinanceNotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(notificationType);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.saveFinanceNotification(message);
	}

}
