package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.RfiSorDao;
import com.privasia.procurehere.core.dao.RfiSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfiSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RfiSupplierSorItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfiCqDao;
import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RfiEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RfiEventAuditDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfiEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.RfiSupplierCqOption;
import com.privasia.procurehere.core.entity.RfiUnMaskedUser;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqPojo;
import com.privasia.procurehere.core.pojo.EvaluationEnvelopSuppliersPojo;
import com.privasia.procurehere.core.pojo.EvaluationPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfiSupplierCqItemService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;


@Service
@Transactional(readOnly = true)
public class RfiEnvelopServiceImpl extends EnvelopServiceImplBase implements RfiEnvelopService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	private static final Logger DLOG = LogManager.getLogger(Global.DOWNLOAD_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	RfiEnvelopDao rfiEnvelopDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfiCqDao rfiCqDao;

	@Autowired
	UserService userService;

	@Autowired
	RfiEvaluatorUserDao evaluatorUserDao;

	@Autowired
	RfiSupplierCqItemDao rfiSupplierCqItemDao;

	@Autowired
	RfiEventSupplierDao rfiEventSupplierDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	UserDao userDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfiEvaluatorUserDao rfiEvaluatorUserDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfiEventAuditDao rfiEventAuditDao;

	@Autowired
	RfiSupplierCqItemService rfiSupplierCqItemService;

	@Autowired
	RfiEvaluatorDeclarationDao rfiEvaluatorDeclarationDao;

	@Autowired
	RfiSupplierCqOptionDao rfiSupplierCqOptionDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfiSupplierSorItemDao rfiSupplierSorItemDao;


	@Autowired
	RfiSorDao rfiSorDao;

	@Autowired
	RfiSupplierSorItemService rfiSupplierSorItemService;

	@Override
	@Transactional(readOnly = false)
	public RfiEnvelop saveRfiEnvelop(RfiEnvelop rfiEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {
		LOG.info("Save Envelop   : " + rfiEnvelop.getRfxEvent().getId());
		// Fetch Event Obj Instance
		RfiEvent event = rfiEventDao.findById(rfiEnvelop.getRfxEvent().getId());
		rfiEnvelop.setRfxEvent(event);
		if (cqIds != null) {
			List<RfiCq> cqList = rfiCqDao.findCqsByIds(cqIds);
			rfiEnvelop.setCqList(cqList);
		}
		if (sorIds != null) {
			// Fetch all SORs matching the IDs
			List<RfiEventSor> bqList = rfiSorDao.findAllSorsByIds(sorIds);
			LOG.info("bq list :  " + bqList);
			rfiEnvelop.setSorList(bqList);
		}
		LOG.info("Save Envelop   : " + rfiEnvelop);
		return rfiEnvelopDao.saveOrUpdate(rfiEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfiEnvelop(RfiEnvelop rftEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {
		if (cqIds != null) {
			List<RfiCq> cqList = rfiCqDao.findCqsByIds(cqIds);
			rftEnvelop.setCqList(cqList);
		} else {
			rftEnvelop.setCqList(null);
		}

		if (sorIds != null) {
			// Fetch all SORs matching the IDs
			List<RfiEventSor> sorList = rfiSorDao.findAllSorsByIds(sorIds);
			LOG.info("sor list :  " + sorList);
			rftEnvelop.setSorList(sorList);
		} else {
			rftEnvelop.setSorList(null);
		}
		rfiEnvelopDao.update(rftEnvelop);
	}

	@SuppressWarnings("unused")
	@Override
	@Transactional(readOnly = false)
	public RfiEnvelop updateEnvelope(RfiEnvelop envelop, String eventId) throws ApplicationException {
		RfiEnvelop dbEnvelop = getRfiEnvelopById(envelop.getId());
		User dbleadEvaluator = null;
		// User dbOpener = null;
		RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
		String description = "Envelope \"" + dbEnvelop.getEnvelopTitle() + "\" for Event '" + rfiEvent.getEventId() + "' is updated.";
		String evaluatorEditRemoved = "";
		boolean isEvaluatorEdit = false;
		for (RfiEvaluatorUser users : dbEnvelop.getEvaluators()) {
			if (envelop.getEvaluators() != null) {
				for (RfiEvaluatorUser Evalusers : envelop.getEvaluators()) {
					if (users.getId().equals(Evalusers.getId())) {
						Evalusers.setEvaluatorSummary(users.getEvaluatorSummary());
						Evalusers.setSummaryDate(users.getSummaryDate());
					}
				}
			}
		}

		for (RfiEnvelopeOpenerUser dbUsers : dbEnvelop.getOpenerUsers()) {
			if (envelop.getOpenerUsers() != null) {
				for (RfiEnvelopeOpenerUser envUsers : envelop.getOpenerUsers()) {
					if (dbUsers.getUser().getId().equals(envUsers.getId())) {
						envUsers.setIsOpen(dbUsers.getIsOpen());
						envUsers.setOpenDate(dbUsers.getOpenDate());
						envUsers.setCloseDate(dbUsers.getCloseDate());
					}
				}
			}
		}

		if (envelop.getEvaluators() != null) {
			for (RfiEvaluatorUser users : envelop.getEvaluators()) {
				if (users.getUser().getId().equals(dbEnvelop.getLeadEvaluater().getId())) {
					throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
				}
			}
		}

		boolean sendLeadMail = false;
		boolean sendOpenerInviteMail = false;
		boolean sendOpenerRemoveMail = false;
		List<User> removeEvalUsers = new ArrayList<User>();
		List<User> addEvalUsers = new ArrayList<User>();

		List<User> removeOpeners = new ArrayList<User>();
		List<User> addOpeners = new ArrayList<User>();

		try {
			// Sending notification to lead evaluator
			if (dbEnvelop.getLeadEvaluater() != null && envelop.getLeadEvaluater() != null) {
				if (!dbEnvelop.getLeadEvaluater().getId().equals(envelop.getLeadEvaluater().getId())) {
					dbleadEvaluator = new User(dbEnvelop.getLeadEvaluater().getId(), dbEnvelop.getLeadEvaluater().getName(), dbEnvelop.getLeadEvaluater().getCommunicationEmail(), dbEnvelop.getLeadEvaluater().getEmailNotifications(),  dbEnvelop.getLeadEvaluater().getTenantId());
					sendLeadMail = true;
					description += "Envelope Evaluation Owner changed from " + dbleadEvaluator.getName() + " to " + envelop.getLeadEvaluater().getName() + "<br/>";
				}
			}

			// Sending notification to opener
			// if (dbEnvelop.getOpener() != null && EnvelopType.OPEN == envelop.getEnvelopType()) {
			// dbOpener = new User(dbEnvelop.getOpener().getId(), dbEnvelop.getOpener().getName(),
			// dbEnvelop.getOpener().getCommunicationEmail(), dbEnvelop.getOpener().getTenantId());
			// sendOpenerRemoveMail = true;
			// LOG.info("sendOpenerRemoveMail");
			// } else if (dbEnvelop.getOpener() == null && envelop.getOpener() != null) {
			// sendOpenerInviteMail = true;
			// LOG.info("sendOpenerInviteMail");
			// } else if (dbEnvelop.getOpener() != null && envelop.getOpener() != null) {
			// if (!dbEnvelop.getOpener().getId().equals(envelop.getOpener().getId())) {
			// dbOpener = new User(dbEnvelop.getOpener().getId(), dbEnvelop.getOpener().getName(),
			// dbEnvelop.getOpener().getCommunicationEmail(), dbEnvelop.getOpener().getTenantId());
			// sendOpenerInviteMail = true;
			// sendOpenerRemoveMail = true;
			// description += " Envelope Opener changed from " + dbEnvelop.getOpener().getName() + " to " +
			// envelop.getOpener().getName() + "<br/>";
			// LOG.info("sendOpenerInviteMail === sendOpenerRemoveMail");
			// }
			// }

			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
				for (RfiEvaluatorUser user : envelop.getEvaluators()) {
					boolean newUsers = true;
					for (RfiEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							newUsers = false;
							break;
						}
					}
					if (newUsers) {
						isEvaluatorEdit = true;
						addEvalUsers.add(user.getUser());
					}
				}

				for (RfiEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					boolean oldUsers = true;
					for (RfiEvaluatorUser user : envelop.getEvaluators()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							oldUsers = false;
							break;
						}
					}
					if (oldUsers) {
						isEvaluatorEdit = true;
						removeEvalUsers.add(dbUser.getUser());
					}
				}

			} else if (CollectionUtil.isEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
				for (RfiEvaluatorUser user : envelop.getEvaluators()) {
					addEvalUsers.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isEmpty(envelop.getEvaluators())) {
				for (RfiEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					removeEvalUsers.add(dbUser.getUser());
				}
			}

			// Openers
			if (EnvelopType.OPEN == dbEnvelop.getEnvelopType() && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfiEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					addOpeners.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && EnvelopType.OPEN == envelop.getEnvelopType()) {
				for (RfiEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					removeOpeners.add(dbUser.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfiEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					boolean newUsers = true;
					for (RfiEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							newUsers = false;
							break;
						}
					}
					if (newUsers) {
						addOpeners.add(user.getUser());
					}
				}

				for (RfiEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					boolean oldUsers = true;
					for (RfiEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							oldUsers = false;
							break;
						}
					}
					if (oldUsers) {
						removeOpeners.add(dbUser.getUser());
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail for envelop :" + e.getMessage(), e);
		}
		if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
			for (RfiEvaluatorUser user : envelop.getEvaluators()) {
				user.setEnvelop(dbEnvelop);
			}
		}

		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfiEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
				user.setEnvelope(dbEnvelop);
				user.setEvent(dbEnvelop.getRfxEvent());
			}
		}

		if (dbEnvelop.getOpener() != null) {
			dbEnvelop.setOpener(null);
		}

		dbEnvelop.setOpenerUsers(envelop.getOpenerUsers());
		dbEnvelop.setEvaluators(envelop.getEvaluators());
		dbEnvelop.setLeadEvaluater(envelop.getLeadEvaluater());
		dbEnvelop.setEnvelopType(envelop.getEnvelopType());
		// if (envelop.getOpener() != null) {
		// dbEnvelop.setOpener(envelop.getOpener());
		// }
		if (EnvelopType.OPEN == envelop.getEnvelopType()) {
			dbEnvelop.setOpener(null);
			dbEnvelop.setIsOpen(Boolean.TRUE);
			dbEnvelop.setOpenerUsers(null);
		}
		if (dbEnvelop.getEnvelopType() == EnvelopType.CLOSED && dbEnvelop.getOpenDate() == null) {
			dbEnvelop.setIsOpen(Boolean.FALSE);
		}

		String timeZone = "GMT+8:00";
		RfiEvent event = dbEnvelop.getRfxEvent();
		if (sendLeadMail) {
			try {
				String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
				timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getLeadEvaluater(), timeZone);
				sendLeadEvaluatorInvitedNotification(dbEnvelop.getLeadEvaluater(), event, url, timeZone, RfxTypes.RFI.getValue());
				sendLeadEvaluatorRemovedNotification(dbleadEvaluator, event, url, timeZone, RfxTypes.RFI.getValue());
			} catch (Exception e) {
				LOG.error("Error While sending mail to lead evaluator :" + e.getMessage(), e);
			}
		}

		// if (sendOpenerInviteMail && dbEnvelop.getOpener() != null) {
		// try {
		// String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
		// timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getOpener(), timeZone);
		// sendOpenerInvitedNotification(dbEnvelop.getOpener(), event, url, timeZone, RfxTypes.RFI.getValue());
		// } catch (Exception e) {
		// LOG.error("Error While sending mail to invite opener :" + e.getMessage(), e);
		// }
		// }
		//
		// if (sendOpenerRemoveMail && dbOpener != null) {
		// try {
		// String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
		// timeZone = getTimeZoneByBuyerSettings(dbOpener, timeZone);
		// sendOpenerRemovedNotification(dbOpener, event, url, timeZone, RfxTypes.RFI.getValue());
		// } catch (Exception e) {
		// LOG.error("Error While sending mail to remove opener :" + e.getMessage(), e);
		// }
		// }

		if (CollectionUtil.isNotEmpty(addEvalUsers)) {
			try {
				for (User user : addEvalUsers) {
					LOG.info("addEvalUsers user.getId() :" + user.getId());
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorInvitedNotification(user, event, url, timeZone, RfxTypes.RFI.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been added as Evaluator.";
			} catch (Exception e) {
				LOG.error("Error While sending mail to add evaluator :" + e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(removeEvalUsers)) {
			try {
				for (User user : removeEvalUsers) {
					LOG.info("removeEvalUsers user.getId() :" + user.getId());
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorRemovedNotification(user, event, url, timeZone, RfxTypes.RFI.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been removed from Evaluator.<br/>";
			} catch (Exception e) {
				LOG.error("Error While sending mail to remove evaluator :" + e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(addOpeners)) {
			try {
				for (User user : addOpeners) {
					LOG.info("addOpeners user.getId() :" + user.getId());
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerInvitedNotification(user, event, url, timeZone, RfxTypes.RFI.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been added as Envelope Opener.";
			} catch (Exception e) {
				LOG.error("Error While sending mail to add evaluator :" + e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(removeOpeners)) {
			try {
				for (User user : removeOpeners) {
					LOG.info("removeOpeners user.getId() :" + user.getId());
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerRemovedNotification(user, event, url, timeZone, RfxTypes.RFI.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been removed as Envelope Opener.<br/>";
			} catch (Exception e) {
				LOG.error("Error While sending mail to remove evaluator :" + e.getMessage(), e);
			}
		}

		Boolean isAllOpen = true;
		Boolean isAllClose = true;

		List<RfiEnvelopeOpenerUser> openersUserList = dbEnvelop.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(openersUserList)) {
			for (RfiEnvelopeOpenerUser opener : openersUserList) {
				for (User user : addOpeners) {
					if (user.getId().equals(opener.getUser().getId())) {
						if (Boolean.TRUE == dbEnvelop.getIsOpen()) {
							opener.setIsOpen(Boolean.TRUE);
							opener.setOpenDate(new Date());
						}
					}
				}

				if (opener.getIsOpen() == null || Boolean.FALSE == opener.getIsOpen()) {
					isAllOpen = false;
				}
				if (Boolean.TRUE == opener.getIsOpen()) {
					isAllClose = false;
				}
			}
			if (isAllOpen) {
				dbEnvelop.setIsOpen(Boolean.TRUE);
				dbEnvelop.setOpenDate(new Date());
			}
			if (isAllClose) {
				dbEnvelop.setIsOpen(Boolean.FALSE);
				dbEnvelop.setCloseDate(new Date());
			}

			dbEnvelop.setOpenerUsers(openersUserList);
		}

		RfiEnvelop envelope = rfiEnvelopDao.update(dbEnvelop);
		// if (isEvaluatorEdit) {
		// evaluatorEditRemoved += "Evaluator changed";
		// int i = 0;
		// for (RfiEvaluatorUser dbUser : envelope.getEvaluators()) {
		// evaluatorEditRemoved += (i == 0 ? " to " : "");
		// evaluatorEditRemoved += dbUser.getUser().getName() + ",";
		// i++;
		// }
		// evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
		// }
		RfiEventAudit audit = new RfiEventAudit();
		audit.setAction(AuditActionType.Update);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setActionDate(new Date());
		description += evaluatorEditRemoved;
		audit.setDescription(description);
		audit.setEvent(event);
		eventAuditService.save(audit);

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, description + "'for Event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return envelop;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEnvelopeStatus(String envelopId, User logedUser) throws ApplicationException {
		RfiEnvelop dbEnvelop = rfiEnvelopDao.findById(envelopId);
		RfiEvent event = dbEnvelop.getRfxEvent();
		User leadEvaluator = dbEnvelop.getLeadEvaluater();
		String timeZone = "GMT+8:00";
		String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
		timeZone = getTimeZoneByBuyerSettings(logedUser, timeZone);
		User eventOwner = rfiEventService.getPlainEventOwnerByEventId(event.getId());
		String msg = "";
		if (dbEnvelop != null && leadEvaluator.getId().equals(logedUser.getId())) {

			if (StringUtils.checkString(dbEnvelop.getLeadEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}

			Boolean allDone = Boolean.TRUE;
			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators())) {
				for (RfiEvaluatorUser evaluator : dbEnvelop.getEvaluators()) {
					if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
						allDone = Boolean.FALSE;
						break;
					}
				}
			}

			if (Boolean.TRUE == allDone) {
				dbEnvelop.setEvaluationDate(new Date());
				dbEnvelop.setEvaluationStatus(EvaluationStatus.COMPLETE);
				rfiEnvelopDao.update(dbEnvelop);
			}
			try {
				// sending notification completed evaluation of envelope to lead
				// Evaluator
				msg = "You have complete evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFI.getValue(), msg);
				// sending notification completed evaluation of envelope to
				// Event Owner
				msg = "\"" + leadEvaluator.getName() + "\" has completed evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFI.getValue(), msg);
				try {
					RfiEventAudit audit = new RfiEventAudit();
					audit.setAction(AuditActionType.Evaluate);
					audit.setActionBy(leadEvaluator);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + "' is evaluated");
					rfiEventAuditDao.save(audit);

					RfiEvent rfiEvent = rfiEventService.getPlainEventById(event.getId());
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.EVALUATE, "Envelope '" + dbEnvelop.getEnvelopTitle() + "' is Evaluated for event '" + rfiEvent.getEventId() + "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			} catch (Exception e) {
				LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
			}
			// set Event Status Complete
			int count = rfiEnvelopDao.findCountPendingEnvelopse(event.getId());
			if (count == 0) {
				event.setStatus(EventStatus.COMPLETE);
				event = rfiEventDao.update(event);
				try {
					RfiEventAudit audit = new RfiEventAudit();
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Evaluation is completed");
					audit.setAction(AuditActionType.Complete);
					rfiEventAuditDao.save(audit);

					RfiEvent rfiEvent = rfiEventService.getPlainEventById(event.getId());
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Evaluation is completed for Event '" + rfiEvent.getEventId() + "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					// sending notification completed evaluation of envelope to
					// Event Owner
					msg = "The evaluation for the event \"" + event.getReferanceNumber() + "\" has been completed";
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFI.getValue(), msg);
					// sending notification completed evaluation of envelope to
					// buyer team members
					List<User> buyerMembers = rfiEventDao.getUserBuyerTeamMemberByEventId(event.getId());
					if (CollectionUtil.isNotEmpty(buyerMembers)) {
						for (User buyerTeamUser : buyerMembers) {
							sendEnvelopCompletedNotification(buyerTeamUser, event, url, timeZone, RfxTypes.RFI.getValue(), msg);
						}
					}

					// Send notification to unMasking User on all envelops evaluation completed
					if (Boolean.FALSE == event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
						url = APP_URL + "/buyer/RFI/envelopList/" + event.getId();
						msg = "You are assigned as Unmasking Owner for Event: \"" + event.getReferanceNumber() + "\"";
						for (RfiUnMaskedUser um : event.getUnMaskedUsers()) {
							sendEnvelopCompletedNotificationToUnMaskingUser(um.getUser(), event, url, timeZone, RfxTypes.RFI.getValue(), msg);
						}
					}

				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
				tatReportService.updateTatReportEvaluationCompleted(event.getId(), logedUser.getTenantId(), new Date(), event.getStatus());
			}
		} else {
			RfiEvaluatorUser evaluatorUser = rfiEnvelopDao.getRfiEvaluatorUserByUserIdAndEnvelopeId(envelopId, logedUser.getId());

			if (StringUtils.checkString(evaluatorUser.getEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}
			if (evaluatorUser != null) {
				evaluatorUser.setEvaluationDate(new Date());
				evaluatorUser.setEvaluationStatus(EvaluationStatus.COMPLETE);
				updateEvaluatorUser(evaluatorUser);
				try {
					RfiEventAudit audit = new RfiEventAudit();
					audit.setAction(AuditActionType.Review);
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + " ' is reviewed");
					rfiEventAuditDao.save(audit);

					RfiEvent rfiEvent = rfiEventService.getPlainEventById(event.getId());
					BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.REVIEW, "Envelope '" + dbEnvelop.getEnvelopTitle() + "' is reviewed for Event '" + rfiEvent.getEventId() + "'", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(ownerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					// sending notification to review on envelope to loged user
					msg = "You have completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(logedUser, event, url, timeZone, RfxTypes.RFI.getValue(), msg);
					// sending notification to review on envelope to lead
					// Evaluator
					msg = "\"" + logedUser.getName() + "\" has completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFI.getValue(), msg);
					// sending notification to review on envelope to Event Owner
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFI.getValue(), msg);
				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfiEnvelop(RfiEnvelop rftEnvelop) {
		rfiEnvelopDao.delete(rftEnvelop);
	}

	@Override
	public List<RfiEnvelop> getAllRfiEnvelop() {
		return rfiEnvelopDao.findAll(RfiEnvelop.class);
	}

	@Override
	public boolean isExists(RfiEnvelop rftEnvelop, String eventId) {
		return rfiEnvelopDao.isExists(rftEnvelop, eventId);
	}

	@Override
	public RfiEnvelop getRfiEnvelopById(String id) {
		RfiEnvelop envelop = rfiEnvelopDao.findById(id);
		// if (envelop.getOpener() != null) {
		// envelop.getOpener().getName();
		// }
		if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
			for (RfiEvaluatorUser evalUser : envelop.getEvaluators()) {
				evalUser.getUser().getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
			for (RfiCq cq : envelop.getCqList()) {
				cq.getName();
			}
		}
		if (envelop.getOpener() != null) {
			envelop.getOpener().getName();
		}
		if (envelop.getLeadEvaluater() != null) {
			envelop.getLeadEvaluater().getLoginId();
		}
		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfiEnvelopeOpenerUser openerUser : envelop.getOpenerUsers()) {
				openerUser.getUser().getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
			for (RfiEventSor bq : envelop.getSorList()) {
				bq.getName();
			}
		}
		return envelop;
	}

	@Override
	public RfiEnvelop getEnvelopForEvaluationById(String id, User logedUser) {
		RfiEnvelop envelop = rfiEnvelopDao.findById(id);
		if (envelop == null) {
			return null;
		} else {
			if (envelop.getRfxEvent() != null) {
				envelop.getRfxEvent().getEventName();
				if (envelop.getRfxEvent().getBaseCurrency() != null) {
					envelop.getRfxEvent().getBaseCurrency().getCurrencyCode();
				}
				// For Evaluator
				if (envelop.getRfxEvent().getStatus() != EventStatus.DRAFT && envelop.getRfxEvent().getStatus() != EventStatus.SUSPENDED) {
					Boolean showFinishForLead = Boolean.TRUE;
					if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
						for (RfiEvaluatorUser evaluator : envelop.getEvaluators()) {
							if (envelop.getIsOpen() && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId()) && evaluator.getEvaluationStatus() == EvaluationStatus.PENDING && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
								envelop.setShowFinish(true);
							}
							if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
								showFinishForLead = Boolean.FALSE;
							}
						}
					}
					// For Lead Evaluator
					if (envelop.getIsOpen() && envelop.getLeadEvaluater().getId().equals(logedUser.getId()) && showFinishForLead && EvaluationStatus.COMPLETE != envelop.getEvaluationStatus() && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
						envelop.setShowFinish(true);
					}

				}
			}
		}

		return envelop;
	}

	@Override
	public List<RfiEnvelop> getAllEnvelopByEventId(String eventId, User logedUser) {
		List<RfiEnvelop> envList = rfiEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFI);
		if (CollectionUtil.isNotEmpty(envList)) {
			for (RfiEnvelop envelop : envList) {

				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfiCq cq : envelop.getCqList()) {
						cq.getName();
					}
				}

				// if (envelop.getRfxEvent().getStatus() != EventStatus.DRAFT &&
				// envelop.getRfxEvent().getStatus() !=
				// EventStatus.SUSPENDED) {
				// if (!envelop.getIsOpen() && envelop.getOpener() != null &&
				// envelop.getOpener().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() ==
				// EventStatus.CLOSED) {
				// envelop.setShowOpen(true);
				// }
				// if (envelop.getIsOpen() && envelop.getOpener() != null &&
				// envelop.getOpener().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() ==
				// EventStatus.CLOSED) {
				// envelop.setShowOpen(true);
				// }
				if (envelop.getIsOpen() && envelop.getLeadEvaluater().getId().equals(logedUser.getId())) {
					envelop.setShowView(true);
				}
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RfiEvaluatorUser evaluator : envelop.getEvaluators()) {
						LOG.info("evaluator : " + evaluator.getUser().getId() + "  --   " + logedUser.getId());
						if (envelop.getIsOpen() && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId())) {
							envelop.setShowView(true);
						}
					}
				}
				// }
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfiEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
						if (!envelop.getIsOpen() && opener.getUser().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
							envelop.setShowOpen(true);
						}
						if (envelop.getIsOpen() && opener.getUser().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
							envelop.setShowOpen(true);
						}
					}
				}
			}
		}
		return envList;
	}

	@Override
	public List<RfiEnvelop> getAllRfiEnvelopByEventId(String eventId) {
		List<RfiEnvelop> list = rfiEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFI);

		List<RfiEnvelop> envList2 = rfiEnvelopDao.getAllEnvelopSorByEventId(eventId, RfxTypes.RFI);

		setSorInBqData(list, envList2);

		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiEnvelop envelop : list) {
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfiCq cq : envelop.getCqList()) {
						cq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RfiEvaluatorUser evaluators : envelop.getEvaluators()) {
						evaluators.getUser().getName();
					}

				}
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfiEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
						opener.getUser().getName();
					}
				}
			}

		}
		return list;
	}

	@Override
	public List<RfiCq> getAllRfiCqByEnvelopId(String envelopId) {
		return rfiEnvelopDao.getAllCqByEnvelopId(envelopId);
	}

	@Override
	public List<RfiEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		return rfiEnvelopDao.findEvaluatorsByEnvelopId(envelopId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfiEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException {
		LOG.info("RfiEnvelopeService Impl  addEvaluator: envelopeId " + envelopeId + " userId: " + userId);

		RfiEnvelop rfiEnvelop = getRfiEnvelopById(envelopeId);
		if (userId.equals(rfiEnvelop.getLeadEvaluater().getId())) {
			throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
		}

		List<RfiEvaluatorUser> evaluators = rfiEnvelop.getEvaluators();
		if (evaluators == null) {
			evaluators = new ArrayList<RfiEvaluatorUser>();
		}
		RfiEvaluatorUser rfiEvaluatorUser = new RfiEvaluatorUser();
		rfiEvaluatorUser.setEnvelop(rfiEnvelop);
		User user = userService.getUsersById(userId);
		try {
			rfiEvaluatorUser.setUser((User) user.clone());
		} catch (Exception e) {
			LOG.error("Error :  " + e.getMessage(), e);
		}

		evaluators.add(rfiEvaluatorUser);

		rfiEnvelop.setEvaluators(evaluators);
		rfiEnvelopDao.saveOrUpdate(rfiEnvelop);
		return evaluators;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEvaluator(String eventId, String envelopeId, String userId) {
		LOG.info("RfiEnvelopeService Impl  removeEvaluator: envelopeId " + envelopeId + " userId: " + userId);
		RfiEnvelop rfiEnvelop = getRfiEnvelopById(envelopeId);
		LOG.info(rfiEnvelop.getId());
		List<RfiEvaluatorUser> rfiEvaluatorUser = rfiEnvelop.getEvaluators();
		if (rfiEvaluatorUser == null) {
			rfiEvaluatorUser = new ArrayList<RfiEvaluatorUser>();
		}
		LOG.info("rftEvaluatorUser.size() :" + rfiEvaluatorUser.size());
		RfiEvaluatorUser dbEvaluatorUser = getRfiEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
		LOG.info(dbEvaluatorUser.getUser().getName());
		rfiEvaluatorUser.remove(dbEvaluatorUser);
		dbEvaluatorUser.setEnvelop(null);
		rfiEnvelop.setEvaluators(rfiEvaluatorUser);
		rfiEnvelopDao.update(rfiEnvelop);
		LOG.info(" rfiEnvelop.getEvaluators() :" + rfiEnvelop.getEvaluators().size());
		List<User> userList = new ArrayList<User>();
		try {
			for (RfiEvaluatorUser rfieval : rfiEnvelop.getEvaluators()) {
				userList.add((User) rfieval.getUser().clone());
			}
			LOG.info(userList.size() + " Event ID :" + eventId);
		} catch (Exception e) {
			LOG.error("Error constructing list of users after remove operation : " + e.getMessage(), e);
		}
		return userList;

	}

	@Override
	public RfiEvaluatorUser getRfiEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId) {
		return rfiEnvelopDao.getRfiEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEvaluatorUser(RfiEvaluatorUser evaluatorUser) {
		evaluatorUserDao.update(evaluatorUser);
	}

	@Override
	@Transactional(readOnly = false)
	public void openEnvelop(RfiEnvelop envelop) {
		rfiEnvelopDao.update(envelop);
	}

	@Override
	@Transactional(readOnly = true)
	public RfiEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		return evaluatorUserDao.findEvaluatorUser(envelopId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer, String timeZoneStr) throws IOException, JRException {
		DLOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> Download started for Event : " + eventId + " And Envelop : " + envelopeId);
		RfiEnvelop envlope = rfiEnvelopDao.findById(envelopeId);
		RfiEvent event = rfiEventDao.getPlainEventById(eventId);

		String zipFileName = event.getReferanceNumber().replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
		zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
		String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
		parentFolder += "-" + envlope.getEnvelopTitle().replaceAll(" ", "_").replaceAll("/", "-");
		parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");


		boolean isSorAvailable = false;
		try {
			if (envlope != null && CollectionUtil.isNotEmpty(envlope.getSorList())) {
				isSorAvailable = true;
			}
		} catch (Exception e) {
			LOG.error(e);
		}

		// Get All Supplier List

		List<EventSupplierPojo> supplierList = rfiEventSupplierDao.getSubmitedSuppliers(eventId);
		if (CollectionUtil.isNotEmpty(supplierList)) {
			DLOG.info("SUppliers : " + supplierList.size());
			int i = 1;
			for (EventSupplierPojo supplier : supplierList) {
				String supplierFolder = null;
				// take only submitted ones
				// if (Boolean.TRUE == supplier.getSubmitted())
				if (envlope.getEnvelopSequence() != null && Boolean.FALSE == event.getAllowDisqualifiedSupplierDownload() && Boolean.TRUE == supplier.getDisqualify() && supplier.getDisqualifiedEnvelopSeq() != null && envlope.getEnvelopSequence() > supplier.getDisqualifiedEnvelopSeq()) {
					continue;
				}

				// [eventRef+envelopeName]/[supplierCompanyName]/
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					supplierFolder = parentFolder + Global.PATH_SEPARATOR + (MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_")) + Global.PATH_SEPARATOR;
				} else {
					supplierFolder = parentFolder + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + Global.PATH_SEPARATOR;
				}


				// SOR
				if (Boolean.TRUE == event.getScheduleOfRate()) {
					if (isSorAvailable) {
						JasperPrint jasperPrint = generateSupplierSorPdfForEnvelope(envelopeId, supplier.getId(), i, virtualizer);
						if (jasperPrint != null) {
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Schedule of Rate" + " - " + MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_"));
								zos.flush();
							} else {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Schedule of Rate" + " - " + supplier.getCompanyName().replaceAll(" ", "_"));
								zos.flush();
							}
						}
					}

					// Export SOR FOR EACH SUPPLIER
					try {
						sorExportExcelForEachSupplier(envelopeId, zos, event, supplier, supplierFolder, i);
					} catch (Exception e) {
						LOG.error("Error While Export sor for each supplier : " + e.getMessage(), e);
					}

				} else {
					LOG.info("No SOR setup for event : " + eventId);
				}

				// CQ
				if (Boolean.TRUE == event.getQuestionnaires()) {
					DLOG.info("Generating CQ PDF ....");
					JasperPrint jasperPrint = generateSupplierCqPdfForEnvelope(envelopeId, supplier.getId(), i);
					DLOG.info("Completed Generating CQ PDF ....");
					if (jasperPrint != null) {
						if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
							FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Questionnaire" + " - " + (MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_")));
							zos.flush();
						} else {
							FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Questionnaire" + " - " + supplier.getCompanyName().replaceAll(" ", "_"));
							zos.flush();
						}
					}

					if (CollectionUtil.isNotEmpty(envlope.getCqList())) {
						for (RfiCq cq : envlope.getCqList()) {
							String attachmentFolder = "";
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + (MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_")) + " - " + cq.getName() + Global.PATH_SEPARATOR;
							} else {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;
							}
							DLOG.info("Getting attachments......");
							List<RfiSupplierCqItem> supplierCqItems = rfiSupplierCqItemDao.getSupplierCqItemsbySupplier(supplier.getId(), cq.getId());
							if (CollectionUtil.isNotEmpty(supplierCqItems)) {
								for (RfiSupplierCqItem supCqItem : supplierCqItems) {
									if (supCqItem.getFileData() != null) {
										String fileName = supCqItem.getCqItem().getLevel() + "-" + supCqItem.getCqItem().getOrder() + "-" + supCqItem.getFileName();
										FileUtil.writeFileToZip(zos, supCqItem.getFileData(), attachmentFolder, fileName);
										zos.flush();
									}
								}
							}
							DLOG.info("Attachments completed...");

						}
					}
				} else {
					DLOG.info("No CQ setup for event : " + eventId);
				}
				i++;
			}
		}

		if (isForAllReports) {
			@SuppressWarnings("unused")
			int count = 1;
			TimeZone timeZone = TimeZone.getDefault();
			// String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (timeZoneStr != null) {
				timeZone = TimeZone.getTimeZone(timeZoneStr);
			}
			DLOG.info("short summary");
			JasperPrint jasperPrint = rftEventService.generateShortEvaluationSummaryReport("RFI", eventId, envelopeId, timeZone, virtualizer);
			if (jasperPrint != null) {
				DLOG.info("short summary");
				FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Short Submission Summary for " + envlope.getEnvelopTitle());
				zos.flush();
			}
			try {
				DLOG.info("Evaluation Report");
				jasperPrint = rfiEventService.getEvaluationReport(eventId, envelopeId, timeZoneStr, virtualizer);
				if (jasperPrint != null) {
					DLOG.info(">>>>> Evaluation Report");
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Evaluation Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}
			} catch (Exception e) {
			}

			try {
				DLOG.info("Submission Report");
				jasperPrint = rfiEventService.generateSubmissionReport(envelopeId, eventId, timeZoneStr, virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Submission Report for " + envlope.getEnvelopTitle());
					zos.flush();
					DLOG.info(">>>>>>>Evaluation Report");
				}
			} catch (Exception e) {
			}

			List<EventEvaluationPojo> list = null;

			XSSFWorkbook workbookCq = new XSSFWorkbook();
			list = rfiSupplierCqItemService.getEvaluationDataForCqComparison(eventId, envelopeId);
			if (CollectionUtil.isNotEmpty(list)) {
				DLOG.info("CQ Comparision Table");
				workbookCq = rfaEventService.buildCqComparisionFile(workbookCq, list, null);
				if (workbookCq != null) {
					FileUtil.writeXssfExcelToZip(zos, workbookCq, parentFolder + "/", "CQ Comparison Table.xlsx");
					zos.flush();
				}
				LOG.info(">>>> CQ Comparision Table");
			}
			count++;

		}
		DLOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>File Generated  Event : " + eventId + " And Envelop : " + envelopeId);
		return zipFileName;
	}

	@Override
	public String getEnvelipeTitleById(String evenvelopId, String eventType) {
		return rfiEnvelopDao.getEnvelipeTitleById(evenvelopId, eventType);
	}

	@Override
	public JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationSorPojo> bqSummary = new ArrayList<EvaluationSorPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			LOG.info("Generating RFQ Supplier SOR Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			List<SorPojo> bqList = rfiEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierSors.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (SorPojo bqPojo : bqList) {
					EvaluationSorPojo bq = new EvaluationSorPojo();
					String bqId = bqPojo.getId();
					RfiEventSor bqDetail = rfiSorDao.findById(bqId);
					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRfiEnvelop())) {
						for (RfiEnvelop env : bqDetail.getRfxEvent().getRfiEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					List<RfiSupplierSorItem> supBqItem = rfiSupplierSorItemDao.findSupplierSorItemByListBySorIdAndSupplierId(bqId, supplierId);
					List<EvaluationSorItemPojo> bqItems = new ArrayList<EvaluationSorItemPojo>();
					for (RfiSupplierSorItem item : supBqItem) {
						EvaluationSorItemPojo bqItem = new EvaluationSorItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RfiSupplierSorItem childBqItem : item.getChildren()) {
								EvaluationSorItemPojo evlBqChilItem = new EvaluationSorItemPojo();
								evlBqChilItem.setDescription(childBqItem.getItemName());
								evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
								evlBqChilItem.setUom(childBqItem.getUom().getUom());
								evlBqChilItem.setAmount(childBqItem.getTotalAmount());
								evlBqChilItem.setDecimal(childBqItem.getEvent() != null ? childBqItem.getEvent().getDecimal() : "");
								bqItems.add(evlBqChilItem);
							}
						}
					}
					bq.setBqItems(bqItems);
					bqSummary.add(bq);
				}
			}
			parameters.put("SUPPLIER_SORS", bqSummary);
			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			RfiEvent event = rfiEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
			RfiEnvelop rfqEnvelop = getRfiEnvelopById(envelopeId);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			parameters.put("eventType", RfxTypes.RFQ.getValue());
			parameters.put("eventId", StringUtils.checkString(event.getEventId()));
			parameters.put("referenceNumber", StringUtils.checkString(event.getReferanceNumber()));
			parameters.put("eventCreationDate", (event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : ""));
			parameters.put("eventPublishDate", (event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : ""));
			parameters.put("eventStartDate", (event.getEventStart() != null ? sdf.format(event.getEventStart()) : ""));
			parameters.put("eventEndDate", (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : ""));
			parameters.put("currencyCode", StringUtils.checkString(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : ""));
			parameters.put("generatedOn", sdf.format(new Date()));
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking() && i != 0) {
				parameters.put("supplierName", MaskUtils.maskName(rfqEnvelop.getPreFix(), supplier.getId(), rfqEnvelop.getId()));
			} else {
				parameters.put("supplierName", StringUtils.checkString(supplier.getCompanyName()));
			}

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(bqSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Error generating Supplier SOR PDF for envelope : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int supplierNo) {
		List<EvaluationCqPojo> cqSummary = new ArrayList<EvaluationCqPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		RfiEnvelop envelope = rfiEnvelopDao.findById(envelopeId);
		JasperPrint jasperPrint = null;
		try {
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierCqs.jasper");
			File jasperfile = resource.getFile();
			// List<String> cqList = rfiEnvelopDao.getCqsByEnvelopId(Arrays.asList(envelopeId));
			List<CqPojo> cqList = rfiEnvelopDao.getCqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			if (CollectionUtil.isNotEmpty(cqList)) {
				for (CqPojo cqPojo : cqList) {
					EvaluationCqPojo cq = new EvaluationCqPojo();
					String cqId = cqPojo.getId();
					RfiCq cqDetail = rfiCqDao.findById(cqId);

					cq.setTitle(envelope.getEnvelopTitle());
					cq.setName(cqDetail.getName());
					// List<RfiSupplierCqItem> supCqItem = rfiSupplierCqItemDao.findSupplierCqItemListByCqId(cqId,
					// supplierId);
					DLOG.info("Started CQ >>>>>>>  " + cq.getTitle());
					List<RfiSupplierCqItem> supCqItem = rfiSupplierCqItemDao.findSupplierCqItemByCqIdandSupplierId(cqId, supplierId);
					List<EvaluationCqItemPojo> cqItems = new ArrayList<EvaluationCqItemPojo>();
					if (supCqItem != null) {
						DLOG.info("supCqItem   " + supCqItem.size());
					}
					for (RfiSupplierCqItem item : supCqItem) {
						List<RfiSupplierCqOption> listAnswers = rfiSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
						EvaluationCqItemPojo ec = new EvaluationCqItemPojo();
						ec.setItemName(item.getCqItem().getItemName());
						ec.setItemDescription(item.getCqItem().getItemDescription());
						ec.setLevel(item.getCqItem().getLevel() + "." + item.getCqItem().getOrder());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							ec.setAnswer(item.getTextAnswers());
						} else if (item.getCqItem().getCqType() == CqType.LIST || item.getCqItem().getCqType() == CqType.CHECKBOX) {
							if (CollectionUtil.isNotEmpty(listAnswers)) {
								String str = "";
								int ii = 1;
								for (RfiSupplierCqOption cqOption : listAnswers) {
									if (StringUtils.checkString(cqOption.getValue()).length() > 0) {
										str += String.valueOf(ii) + "." + cqOption.getValue() + "\n";
										ii++;
									} else {
										str += cqOption.getValue() + "\n";
									}
								}
								ii = 1;
								ec.setAnswer(str);
							}
						} else if (CollectionUtil.isNotEmpty(listAnswers)) {
							for (RfiSupplierCqOption cqOption : listAnswers) {
								String str = "";
								str += cqOption.getValue() + (cqOption.getScoring() != null ? "/" + cqOption.getScoring() : "");
								ec.setAnswer(str);
							}
						}
						ec.setAttachments(item.getFileName() != null ? item.getFileName() : "");
						cqItems.add(ec);
					}
					cq.setCqItem(cqItems);
					cqSummary.add(cq);
				}
			}
			parameters.put("SUPPLIER_CQS", cqSummary);

			RfiEvent event = envelope.getRfxEvent(); // rfiEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			parameters.put("eventType", RfxTypes.RFI.getValue());
			parameters.put("eventId", StringUtils.checkString(event.getEventId()));
			parameters.put("referenceNumber", StringUtils.checkString(event.getReferanceNumber()));
			parameters.put("eventCreationDate", (event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : ""));
			parameters.put("eventPublishDate", (event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : ""));
			parameters.put("eventStartDate", (event.getEventStart() != null ? sdf.format(event.getEventStart()) : ""));
			parameters.put("eventEndDate", (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : ""));
			parameters.put("currencyCode", StringUtils.checkString(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : ""));
			parameters.put("generatedOn", sdf.format(new Date()));
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking() && supplierNo != 0) {
				parameters.put("supplierName", MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()));
			} else {
				parameters.put("supplierName", StringUtils.checkString(supplier.getCompanyName()));
			}
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(cqSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			DLOG.error("Error generating Supplier CQ PDF for envelope : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public JasperPrint generateEvaluationSubmissionReport(String evenvelopId, String eventId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> submissionSummary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		RfiEnvelop envlope = getRfiEnvelopById(evenvelopId);

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSubmissionReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo eventDetails = new EvaluationPojo();
			RfiEvent event = rfiEventService.getRfiEventByeventId(eventId);
			String owner = "";
			if (event != null) {
				eventDetails.setBuyerName(event.getEventOwner() != null ? event.getEventOwner().getBuyer().getCompanyName() : "");
				eventDetails.setEventId(event.getEventId());
				eventDetails.setReferenceNo(event.getReferanceNumber());
				eventDetails.setEventName(event.getEventName());
				if (event.getEventOwner() != null) {
					owner += event.getEventOwner().getName() + "\r\n" + event.getEventOwner().getCommunicationEmail() + "\r\n";
					// owner += "Tel: " +
					// event.getEventOwner().getOwner().getCompanyContactNumber();
					if (event.getEventOwner().getPhoneNumber() != null) {
						owner += "HP: " + event.getEventOwner().getPhoneNumber();
					}
				}
				eventDetails.setOwner(owner);
				if (event.getEventStart() != null) {
					eventDetails.setEventStart(sdf.format(event.getEventStart()).toUpperCase());
				}
				if (event.getEventEnd() != null) {
					eventDetails.setEventEnd(sdf.format(event.getEventEnd()).toUpperCase());
				}

				// Envelop Details.
				if (envlope != null) {
					eventDetails.setEnvlopName(envlope.getEnvelopTitle());
					eventDetails.setEnvDescription(envlope.getDescription());
					eventDetails.setOpenType(envlope.getEnvelopType().name());
					eventDetails.setEnvelopOwner(envlope.getLeadEvaluater().getName());
					if (envlope.getEnvelopType() != EnvelopType.OPEN) {
						eventDetails.setEnvelopOpener(envlope.getOpener().getName());
					}
					if (envlope.getOpenDate() != null) {
						eventDetails.setOpenDate(sdf.format(envlope.getOpenDate()).toUpperCase());
					}
					String location = "";
					if (envlope.getRfxEvent().getDeliveryAddress() != null) {
						location += envlope.getRfxEvent().getDeliveryAddress().getTitle() + "\r\n" + envlope.getRfxEvent().getDeliveryAddress().getLine1() + "\r\n" + envlope.getRfxEvent().getDeliveryAddress().getCity() + "\r\n";
						location += envlope.getRfxEvent().getDeliveryAddress().getZip();
					}
					eventDetails.setLocation(location);
					List<EvaluationCqPojo> cqs = new ArrayList<EvaluationCqPojo>();
					if (CollectionUtil.isNotEmpty(envlope.getCqList())) {
						for (RfiCq cq : envlope.getCqList()) {
							EvaluationCqPojo item = new EvaluationCqPojo();
							item.setName(cq.getName());
							item.setDescription(cq.getDescription());
							cqs.add(item);
						}
					}
					eventDetails.setCqs(cqs);
				}
				// Supplier List
				List<EvaluationSuppliersPojo> supplierList = supplierContactCqBqDetails(eventId, sdf, eventDetails);
				eventDetails.setSuppliers(supplierList);
			}
			submissionSummary.add(eventDetails);
			parameters.put("EVALUATION_SUBMISSION", submissionSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(submissionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate RFI Evaluation Submission Summary PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	/**
	 * @param eventId
	 * @param sdf
	 * @param eventDetails
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private List<EvaluationSuppliersPojo> supplierContactCqBqDetails(String eventId, SimpleDateFormat sdf, EvaluationPojo eventDetails) {
		List<EvaluationSuppliersPojo> supplierList = new ArrayList<EvaluationSuppliersPojo>();
		List<EventSupplier> suppliers = rfiEventSupplierDao.getAllSuppliersByEventId(eventId);
		if (CollectionUtil.isNotEmpty(suppliers)) {
			for (EventSupplier item : suppliers) {
				EvaluationSuppliersPojo eventSup = new EvaluationSuppliersPojo();
				eventSup.setSupplierName(item.getSupplierCompanyName());
				eventSup.setContactName(item.getSupplier().getFullName());
				eventSup.setEmail(item.getSupplier().getCommunicationEmail());
				eventSup.setContactNo(item.getSupplier().getCompanyContactNumber());
				eventSup.setStatus(item.getSubmissionStatus().name());
				supplierList.add(eventSup);

				// Fetch Supplier CQ and BQ
				List<EvaluationEnvelopSuppliersPojo> supplierCqBqList = new ArrayList<EvaluationEnvelopSuppliersPojo>();

				List<RfiCq> suppCqItem = null;
				if (item.getSupplier() != null) {
					suppCqItem = rfiSupplierCqItemDao.getAllCqsBySupplierId(eventId, item.getSupplier().getId());
				}

				List<EvaluationCqPojo> cqs = new ArrayList<EvaluationCqPojo>();
				// List<EvaluationBqPojo> bqs = new
				// ArrayList<EvaluationBqPojo>();

				EvaluationEnvelopSuppliersPojo envelopSuppliers = new EvaluationEnvelopSuppliersPojo();
				envelopSuppliers.setSupplierName(item.getSupplierCompanyName());
				if (item.getSupplierSubmittedTime() != null) {
					envelopSuppliers.setSubmissionDate(new Date(sdf.format(item.getSupplierSubmittedTime()).toUpperCase()));
				}
				if (item.getSubbmitedBy() != null) {
					envelopSuppliers.setSubmittedBy(item.getSubbmitedBy().getName());
				}
				if (CollectionUtil.isNotEmpty(suppCqItem)) {
					for (RfiCq cqItem : suppCqItem) {
						EvaluationCqPojo cqList = new EvaluationCqPojo();
						cqList.setName(cqItem.getName());
						cqs.add(cqList);
					}
				}
				envelopSuppliers.setCqs(cqs);
				supplierCqBqList.add(envelopSuppliers);
				eventDetails.setEnvlopSuppliers(supplierCqBqList);
			}
		}
		return supplierList;
	}

	@Override
	public Integer getAllEnvelopCountByEventId(String eventId) {
		return rfiEnvelopDao.getAllEnvelopCountByEventId(eventId);
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		return rfiEnvelopDao.getcountClosedEnvelop(eventId);
	}

	@Override
	public List<RfiEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		return rfiEnvelopDao.getAllClosedEnvelopAndOpener(eventId);
	}

	@Override
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		return evaluatorUserDao.getAllEnvelopEvaluatorUsers(eventId);
	}

	private void sorExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RfiEvent event, EventSupplierPojo supplier, String supplierFolder, int supplierNo) throws IOException {
		List<SorPojo> bqPojos = rfiEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		RfiEnvelop envelope = getRfiEnvelopById(envelopeId);
		if (CollectionUtil.isNotEmpty(bqPojos)) {
			for (SorPojo bqPojo : bqPojos) {
				LOG.info("BQ ID : " + bqPojo.getId());
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("SOR ITEM");

				// Style of Heading Cells
				XSSFCellStyle styleHeading = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				styleHeading.setFont(font);
				styleHeading.setVerticalAlignment(VerticalAlignment.CENTER);

				XSSFCellStyle myCellStyle1 = workbook.createCellStyle();
				myCellStyle1.setFont(font);
				myCellStyle1.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
				myCellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				// Style of amount Cells
				XSSFCellStyle alignRightStyle = workbook.createCellStyle();
				alignRightStyle.setAlignment(HorizontalAlignment.RIGHT);

				XSSFCellStyle formatNumberStyle = workbook.createCellStyle();
				formatNumberStyle.setAlignment(HorizontalAlignment.RIGHT);

				XSSFCellStyle blueRightBoldStyle = workbook.createCellStyle();
				blueRightBoldStyle.setFont(font);
				blueRightBoldStyle.setAlignment(HorizontalAlignment.RIGHT);
				blueRightBoldStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
				blueRightBoldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				XSSFCellStyle blueLeftBoldStyle = workbook.createCellStyle();
				blueLeftBoldStyle.setFont(font);
				blueLeftBoldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				blueLeftBoldStyle.setAlignment(HorizontalAlignment.LEFT);
				blueLeftBoldStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
				blueLeftBoldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				XSSFCellStyle numberBoldRight = workbook.createCellStyle();
				numberBoldRight.setFont(font);
				numberBoldRight.setVerticalAlignment(VerticalAlignment.CENTER);
				numberBoldRight.setAlignment(HorizontalAlignment.RIGHT);

				// cell style right and bold
				XSSFCellStyle alignRightBoldStyle = workbook.createCellStyle();
				alignRightBoldStyle.setAlignment(HorizontalAlignment.RIGHT);
				alignRightBoldStyle.setFont(font);

				DataFormat format = workbook.createDataFormat();
				if (event.getDecimal().equals("1")) {
					formatNumberStyle.setDataFormat(format.getFormat("#,###,###,##0.0"));
				} else if (event.getDecimal().equals("2")) {
					formatNumberStyle.setDataFormat(format.getFormat("#,###,###,##0.00"));
				} else if (event.getDecimal().equals("3")) {
					formatNumberStyle.setDataFormat(format.getFormat("#,###,###,##0.000"));
				} else if (event.getDecimal().equals("4")) {
					formatNumberStyle.setDataFormat(format.getFormat("#,###,###,##0.0000"));
				} else if (event.getDecimal().equals("5")) {
					formatNumberStyle.setDataFormat(format.getFormat("#,###,###,##0.00000"));
				} else if (event.getDecimal().equals("6")) {
					formatNumberStyle.setDataFormat(format.getFormat("#,###,###,##0.000000"));
				} else {
					formatNumberStyle.setDataFormat(format.getFormat("#,###,###,##0.00"));
				}

				DataFormat format1 = workbook.createDataFormat();
				if (event.getDecimal().equals("1")) {
					numberBoldRight.setDataFormat(format1.getFormat("#,###,###,##0.0"));
				} else if (event.getDecimal().equals("2")) {
					numberBoldRight.setDataFormat(format1.getFormat("#,###,###,##0.00"));
				} else if (event.getDecimal().equals("3")) {
					numberBoldRight.setDataFormat(format1.getFormat("#,###,###,##0.000"));
				} else if (event.getDecimal().equals("4")) {
					numberBoldRight.setDataFormat(format1.getFormat("#,###,###,##0.0000"));
				} else if (event.getDecimal().equals("5")) {
					numberBoldRight.setDataFormat(format1.getFormat("#,###,###,##0.00000"));
				} else if (event.getDecimal().equals("6")) {
					numberBoldRight.setDataFormat(format1.getFormat("#,###,###,##0.000000"));
				} else {
					numberBoldRight.setDataFormat(format1.getFormat("#,###,###,##0.00"));
				}
				XSSFRow rowHeading = sheet.createRow(1);
				XSSFRow rowSupplierName = sheet.createRow(0);
				XSSFCell cellS2name = rowSupplierName.createCell(4);
				int i = 0;
				for (String column : Global.SOR_EVALUATION_EXCEL_COLUMNS) {
					XSSFCell cell = rowHeading.createCell(i++);
					cell.setCellValue(column);
					switch (column) {
						case "SR No":
							cell.setCellStyle(blueLeftBoldStyle);
							break;
						case "ITEM NAME":
							cell.setCellStyle(blueLeftBoldStyle);
							break;
						case "ITEM DESCRIPTION":
							cell.setCellStyle(blueLeftBoldStyle);
							break;
						case "UOM":
							cell.setCellStyle(blueLeftBoldStyle);
							break;
						default:
							cell.setCellStyle(blueRightBoldStyle);
							break;
					}
				}
				List<String> buyerHeadingList = new ArrayList<>();
				List<String> supplierHeadingList = new ArrayList<>();
				List<RfiSupplierSorItem> bqItems = rfiSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int bqItemLoopCount = 1;

					for (RfiSupplierSorItem item : bqItems) {

						XSSFRow row = sheet.createRow(r++);
						int cellNum = 0;
						row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
						row.createCell(cellNum++).setCellValue(item.getItemName() != null ? item.getItemName() : "");
						row.createCell(cellNum++).setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
						if (bqItemLoopCount == 1) {
							//setBuyerSupplierHeadingsForSor(buyerHeadingList, supplierHeadingList, item.getSorItem());
							bqItemLoopCount++;
						}
						int buyerColumCount = 0;
						int supplierColumnCount = 0;
						if (CollectionUtil.isNotEmpty(buyerHeadingList)) {
							buyerColumCount = buyerHeadingList.size();
						}
						if (CollectionUtil.isNotEmpty(supplierHeadingList)) {
							supplierColumnCount = supplierHeadingList.size();
						}

						int columnCount = 8 + (supplierColumnCount + buyerColumCount);
						sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, columnCount));

						if (CollectionUtil.isNotEmpty(item.getChildren())) {

							for (RfiSupplierSorItem child : item.getChildren()) {
								row = sheet.createRow(r++);
								cellNum = 0;
								row.createCell(cellNum++).setCellValue(child.getLevel() + "." + child.getOrder());
								row.createCell(cellNum++).setCellValue(child.getItemName() != null ? child.getItemName() : "");
								row.createCell(cellNum++).setCellValue(child.getItemDescription() != null ? child.getItemDescription() : "");
								row.createCell(cellNum++).setCellValue(child.getUom() != null ? child.getUom().getUom() : "");

								XSSFCell quantityCell = row.createCell(cellNum++);
								quantityCell.setCellValue(child.getTotalAmount() != null ? child.getTotalAmount().doubleValue() : 0);
								quantityCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
								quantityCell.setCellStyle(formatNumberStyle);

								if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
									cellS2name.setCellValue(MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()));
								} else {
									cellS2name.setCellValue(supplier.getCompanyName());
								}

								XSSFCellStyle myCellStyle3 = workbook.createCellStyle();
								myCellStyle3.setFont(font);
								myCellStyle3.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
								myCellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
								myCellStyle3.setAlignment(HorizontalAlignment.CENTER);
								cellS2name.setCellStyle(myCellStyle3);
							}

						}
					}
				}
				for (int k = 0; k < 5; k++) {
					sheet.autoSizeColumn(k, true);
				}
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					FileUtil.writeXssfExcelToZip(zos, workbook, supplierFolder, MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()).replaceAll(" ", "_") + "-" + bqPojo.getSorName() + ".xlsx");
					zos.flush();
				} else {
					FileUtil.writeXssfExcelToZip(zos, workbook, supplierFolder, supplier.getCompanyName().replaceAll(" ", "_") + " - " + bqPojo.getSorName() + ".xlsx");
					zos.flush();
				}
			}
		} else {
			LOG.info("NO SOR for this envelop id : " + envelopeId);
		}
	}

	private void sendEnvelopCompletedNotification(User user, Event event, String url, String timeZone, String eventType, String msg) {
		String mailTo = "";
		String subject = "Envelope evaluation";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("msg", msg);
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFI)));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.ENVELOPE_COMPLETED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = msg;
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
		}
	}

	private void sendEnvelopCompletedNotificationToUnMaskingUser(User user, Event event, String url, String timeZone, String eventType, String msg) {
		String mailTo = "";
		String subject = "Evaluations have been completed";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("msg", msg);
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFI)));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.ENVELOPE_COMPLETED_TO_UN_MASKING_USR_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
		}
		try {
			sendDashboardNotification(user, url, subject, msg);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
		}
	}

	private String findBusinessUnit(String eventId, RfxTypes rfxTypes) {
		String displayName = null;
		switch (rfxTypes) {
		case RFI:
			displayName = rfiEventDao.findBusinessUnitName(eventId);
			break;
		default:
			break;
		}
		return displayName;
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	/**
	 * @param user
	 * @param timeZone
	 * @return timeZone
	 */
	private String getTimeZoneByBuyerSettings(User user, String timeZone) {
		try {
			if (StringUtils.checkString(user.getTenantId()).length() > 0) {
				String time = buyerSettingsDao.getBuyerTimeZoneByTenantId(user.getTenantId());
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private void sendLeadEvaluatorInvitedNotification(User user, Event event, String url, String timeZone, String eventType) {
		String mailTo = "";
		String subject = "Envelope evaluation";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", event.getBusinessUnit() != null ? event.getBusinessUnit().getDisplayName() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.LEAD_EVALUATOR_INVITED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for adding lead evaluator : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("leadEvaluator.invited.notification.message", new String[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
		}
	}

	private void sendLeadEvaluatorRemovedNotification(User user, Event event, String url, String timeZone, String eventType) {
		String mailTo = "";
		String subject = "Envelope evaluation";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", event.getBusinessUnit() != null ? event.getBusinessUnit().getDisplayName() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.LEAD_EVALUATOR_REMOVED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for adding lead evaluator : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("leadEvaluator.removed.notification.message", new String[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
		}
	}

	private void sendOpenerRemovedNotification(User user, Event event, String url, String timeZone, String eventType) {
		String mailTo = "";
		String subject = "Envelope Opener";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", event.getBusinessUnit() != null ? event.getBusinessUnit().getDisplayName() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.OPENER_REMOVED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for removing opener : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("opener.removed.notification.message", new String[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for removing opener : " + e.getMessage(), e);
		}
	}

	private void sendOpenerInvitedNotification(User user, Event event, String url, String timeZone, String eventType) {
		String mailTo = "";
		String subject = "Envelope Opener";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", event.getBusinessUnit() != null ? event.getBusinessUnit().getDisplayName() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.OPENER_INVITED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for adding opener : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("opener.invited.notification.message", new String[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for adding opener : " + e.getMessage(), e);
		}
	}

	private void sendEvaluatorRemovedNotification(User user, Event event, String url, String timeZone, String eventType) {
		String mailTo = "";
		String subject = "Envelope Evaluator";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", event.getBusinessUnit() != null ? event.getBusinessUnit().getDisplayName() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.EVALUATOR_REMOVED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for removing evaluator : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("evaluator.removed.notification.message", new String[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for removing evaluator : " + e.getMessage(), e);
		}
	}

	private void sendEvaluatorInvitedNotification(User user, Event event, String url, String timeZone, String eventType) {
		String mailTo = "";
		String subject = "Envelope Evaluator";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", event.getBusinessUnit() != null ? event.getBusinessUnit().getDisplayName() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.EVALUATOR_INVITED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending notification for adding evaluator : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("evaluator.invited.notification.message", new String[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for adding evaluator : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean openEnvelope(String envelopeId, String eventId, String key, User loggedInUser) {
		loggedInUser = userDao.getPlainUserByLoginId(loggedInUser.getLoginId());
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		Boolean success = false;
		RfiEnvelop envelop = rfiEnvelopDao.findById(envelopeId);
		if (envelop.getOpener() != null) {
			if (enc.matches(key, envelop.getOpener().getPassword())) {
				envelop.setIsOpen(Boolean.TRUE);
				envelop.setOpenDate(new Date());
				envelop = rfiEnvelopDao.update(envelop);
				success = envelop.getIsOpen();
				LOG.info("updated successfully.....");
				try {
					super.sendEnvelopOpenNotification(envelop, RfxTypes.RFI, eventId, loggedInUser, true);
				} catch (Exception e) {
					LOG.error("Error while Sending mail to opener : " + e.getMessage(), e);
				}
			} else {
				LOG.error("Password mismatch.....");
			}
		} else {
			LOG.error("Opener cannot be null");
		}
		return success;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLeadEvaluatorSummary(RfiEnvelop envelop) {
		rfiEnvelopDao.update(envelop);
	}

	@Override
	public List<RfiEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loginUser) {
		return rfiEvaluatorUserDao.getEvaluationSummaryRemarks(evelopId, loginUser);
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		return rfiEnvelopDao.getCountOfAssignedSupplier(leadUserId, envelopId, eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void closeEnvelop(RfiEnvelop envelop) {
		rfiEnvelopDao.update(envelop);
	}

	@Override
	public RfiEnvelop getRfiEnvelopBySeq(Integer seq, String eventId) {
		return rfiEnvelopDao.getRfiEnvelopBySeq(seq, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfiEnvelope(RfiEnvelop rfiEnvelop) {
		rfiEnvelopDao.saveOrUpdate(rfiEnvelop);

	}

	@Override
	public boolean isAcceptedEvaluationDeclaration(String envelopId, String loggedInUser, String eventId) {
		return rfiEvaluatorDeclarationDao.isAcceptedEvaluationDeclaration(envelopId, loggedInUser, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEvaluatorDeclaration saveEvaluatorDeclaration(RfiEvaluatorDeclaration evaluationDeclarationObj) {
		return rfiEvaluatorDeclarationDao.save(evaluationDeclarationObj);
	}

	@Override
	public RfiEnvelop getEmptyEnvelopByEventId(String eventId) {
		return rfiEnvelopDao.getEmptyEnvelopByEventId(eventId);
	}

	@Override
	public List<RfiCq> getCqsByEnvelopIdByOrder(String envelopId) {
		return rfiEnvelopDao.getCqsByEnvelopIdByOrder(envelopId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLeadEvaluatorDocument(RfiEnvelop envelop) {
		LOG.info("Update RFI ENVELOPE");
		rfiEnvelopDao.update(envelop);
	}

	@Override
	public RfiEvaluatorUser getRfiEvaluationDocument(String eventId, String envelopId, User loggedInUser) {
		return rfiEvaluatorUserDao.getEvaluationDocument(envelopId);
	}


	@Override
	public List<RfiEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		return rfiEnvelopDao.getSorsByEnvelopIdByOrder(envelopId);
	}

	private void setSorInBqData(List<RfiEnvelop> envList, List<RfiEnvelop> envList2) {
		Map<String, RfiEnvelop> envelopMap = envList2.stream().collect(Collectors.toMap(RfiEnvelop::getId, Function.identity()));
		envList.forEach(event -> {
			RfiEnvelop matchingEvent = envelopMap.get(event.getId());
			if(matchingEvent != null) {
				event.setSorList(matchingEvent.getSorList());
			}
		});
	}

}
