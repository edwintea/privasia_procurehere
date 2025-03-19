package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.privasia.procurehere.core.dao.RfaSorDao;
import com.privasia.procurehere.core.dao.RfaSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfaSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RfaSupplierSorItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfaCqDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RfaEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RfaEventAuditDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfaEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCqOption;
import com.privasia.procurehere.core.entity.RfaUnMaskedUser;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.pojo.EvaluationBqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationBqPojo;
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
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierCqItemService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfqEnvelopService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

@Service
@Transactional(readOnly = true)
public class RfaEnvelopServiceImpl extends EnvelopServiceImplBase implements RfaEnvelopService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaBqDao rfaEventBqDao;

	@Autowired
	RfaCqDao rfaCqDao;

	@Autowired
	UserService userService;

	@Autowired
	RfaEvaluatorUserDao evaluatorUserDao;

	@Autowired
	RfaSupplierCqItemDao rfaSupplierCqItemDao;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	RfaSupplierBqDao rfaSupplierBqDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfaSupplierCqItemService supplierCqItemService;

	@Autowired
	RfaSupplierSorItemService rfaSupplierSorItemService;

	@Autowired
	UserDao userDao;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfaEvaluatorUserDao rfaEvaluatorUserDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfaEventAuditDao rfaEventAuditDao;

	@Autowired
	RfaSupplierCqItemService rfaSupplierCqItemService;

	@Autowired
	RfaEnvelopService rfaEnvelopService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RfpEnvelopService rfpEnvelopService;

	@Autowired
	RfqEnvelopService rfqEnvelopService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	RfaEvaluatorDeclarationDao rfaEvaluatorDeclarationDao;

	@Autowired
	RfaSupplierCqOptionDao rfaSupplierCqOptionDao;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfaSorDao rfaEventSorDao;

	@Autowired
	RfaSupplierSorItemDao rfaSupplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public RfaEnvelop saveRfaEnvelop(RfaEnvelop rfaEnvelop, String[] bqIds, String[] cqIds,  String[] sorIds) {

		// Fetch Event Obj Instance
		// RfaEvent event = rfaEventDao.findById(rfaEnvelop.getRfxEvent().getId());
		// rfaEnvelop.setRfxEvent(event);
		if (cqIds != null) {
			List<RfaCq> cqList = rfaCqDao.findCqsByIds(cqIds);
			rfaEnvelop.setCqList(cqList);
		}
		if (bqIds != null) {
			// Fetch all BQs matching the IDs
			List<RfaEventBq> bqList = rfaEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rfaEnvelop.setBqList(bqList);
		}
		if (sorIds != null) {
			// Fetch all BQs matching the IDs
			List<RfaEventSor> bqList = rfaEventSorDao.findAllSorsByIds(sorIds);
			LOG.info("bq list :  " + bqList);
			rfaEnvelop.setSorList(bqList);
		}
		LOG.info("Save Envelop   : " + rfaEnvelop);
		return rfaEnvelopDao.saveOrUpdate(rfaEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfaEnvelop(RfaEnvelop rfaEnvelop, String[] bqIds, String[] cqIds,  String[] sorIds) {
		if (cqIds != null) {
			List<RfaCq> cqList = rfaCqDao.findCqsByIds(cqIds);
			rfaEnvelop.setCqList(cqList);
		} else {
			rfaEnvelop.setCqList(null);
		}
		if (bqIds != null) {
			// Fetch all BQs matching the IDs
			List<RfaEventBq> bqList = rfaEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rfaEnvelop.setBqList(bqList);
		} else {
			rfaEnvelop.setBqList(null);
		}

		if (sorIds != null) {
			// Fetch all BQs matching the IDs
			List<RfaEventSor> bqList = rfaEventSorDao.findAllSorsByIds(sorIds);
			LOG.info("bq list :  " + bqList);
			rfaEnvelop.setSorList(bqList);
		} else {
			rfaEnvelop.setSorList(null);
		}

		rfaEnvelopDao.update(rfaEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEnvelop updateEnvelope(RfaEnvelop envelop, String eventId) throws ApplicationException {
		RfaEnvelop dbEnvelop = getRfaEnvelopById(envelop.getId());
		RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
		User dbleadEvaluator = null;
		User dbOpener = null;
		String description = "Envelope \"" + dbEnvelop.getEnvelopTitle() + "\" for Event '" +rfaEvent.getEventId()+ "' is updated.";
		String evaluatorEditRemoved = "";
		boolean isEvaluatorEdit = false;
		for (RfaEvaluatorUser users : dbEnvelop.getEvaluators()) {
			if (envelop.getEvaluators() != null) {
				for (RfaEvaluatorUser Evalusers : envelop.getEvaluators()) {
					if (users.getId().equals(Evalusers.getId())) {
						Evalusers.setEvaluatorSummary(users.getEvaluatorSummary());
						Evalusers.setSummaryDate(users.getSummaryDate());
					}
				}
			}
		}

		for (RfaEnvelopeOpenerUser dbUsers : dbEnvelop.getOpenerUsers()) {
			if (envelop.getOpenerUsers() != null) {
				for (RfaEnvelopeOpenerUser envUsers : envelop.getOpenerUsers()) {
					if (dbUsers.getUser().getId().equals(envUsers.getId())) {
						envUsers.setIsOpen(dbUsers.getIsOpen());
						envUsers.setOpenDate(dbUsers.getOpenDate());
						envUsers.setCloseDate(dbUsers.getCloseDate());
					}
				}
			}
		}

		if (envelop.getEvaluators() != null) {
			for (RfaEvaluatorUser users : envelop.getEvaluators()) {
				if (users.getUser().getId().equals(dbEnvelop.getLeadEvaluater().getId())) {
					throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
				}
			}
		}

		boolean sendOpenerInviteMail = false;
		boolean sendOpenerRemoveMail = false;
		boolean sendLeadMail = false;
		List<User> removeEvalUsers = new ArrayList<User>();
		List<User> addEvalUsers = new ArrayList<User>();

		List<User> removeOpeners = new ArrayList<User>();
		List<User> addOpeners = new ArrayList<User>();

		// Sending notification to lead evaluator
		try {
			if (dbEnvelop.getLeadEvaluater() != null && envelop.getLeadEvaluater() != null) {
				if (!dbEnvelop.getLeadEvaluater().getId().equals(envelop.getLeadEvaluater().getId())) {
					dbleadEvaluator = new User(dbEnvelop.getLeadEvaluater().getId(), dbEnvelop.getLeadEvaluater().getName(), dbEnvelop.getLeadEvaluater().getCommunicationEmail(), dbEnvelop.getLeadEvaluater().getEmailNotifications(), dbEnvelop.getLeadEvaluater().getTenantId());
					sendLeadMail = true;
					description += "Envelope Evaluation Owner changed from " + dbleadEvaluator.getName() + " to " + envelop.getLeadEvaluater().getName() + "<br/>";
				}
			}

			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
				for (RfaEvaluatorUser user : envelop.getEvaluators()) {
					boolean newUsers = true;
					for (RfaEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
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

				for (RfaEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					boolean oldUsers = true;
					for (RfaEvaluatorUser user : envelop.getEvaluators()) {
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
				for (RfaEvaluatorUser user : envelop.getEvaluators()) {
					addEvalUsers.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isEmpty(envelop.getEvaluators())) {
				for (RfaEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					removeEvalUsers.add(dbUser.getUser());
				}
			}

			// Openers
			if (EnvelopType.OPEN == dbEnvelop.getEnvelopType() && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfaEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					addOpeners.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && EnvelopType.OPEN == envelop.getEnvelopType()) {
				for (RfaEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					removeOpeners.add(dbUser.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfaEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					boolean newUsers = true;
					for (RfaEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							newUsers = false;
							break;
						}
					}
					if (newUsers) {
						addOpeners.add(user.getUser());
					}
				}

				for (RfaEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					boolean oldUsers = true;
					for (RfaEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
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
			for (RfaEvaluatorUser user : envelop.getEvaluators()) {
				user.setEnvelope(dbEnvelop);
			}
		}

		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfaEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
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
		// RfaEvent event = rfaEventDao.getPlainEventById(eventId);
		RfaEvent event = dbEnvelop.getRfxEvent();
		if (sendLeadMail) {
			try {
				String url = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
				timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getLeadEvaluater(), timeZone);
				sendLeadEvaluatorInvitedNotification(dbEnvelop.getLeadEvaluater(), event, url, timeZone, RfxTypes.RFA.getValue());
				sendLeadEvaluatorRemovedNotification(dbleadEvaluator, event, url, timeZone, RfxTypes.RFA.getValue());
			} catch (Exception e) {
				LOG.error("Error While sending mail to lead evaluator :" + e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(addEvalUsers)) {
			try {
				for (User user : addEvalUsers) {
					LOG.info("addEvalUsers user.getId() :" + user.getId());
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					String url = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorInvitedNotification(user, event, url, timeZone, RfxTypes.RFA.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorRemovedNotification(user, event, url, timeZone, RfxTypes.RFA.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerInvitedNotification(user, event, url, timeZone, RfxTypes.RFA.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerRemovedNotification(user, event, url, timeZone, RfxTypes.RFA.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been removed as Envelope Opener.<br/>";
			} catch (Exception e) {
				LOG.error("Error While sending mail to remove evaluator :" + e.getMessage(), e);
			}
		}

		Boolean isAllOpen = true;
		Boolean isAllClose = true;

		List<RfaEnvelopeOpenerUser> openersUserList = dbEnvelop.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(openersUserList)) {
			for (RfaEnvelopeOpenerUser opener : openersUserList) {
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

		RfaEnvelop envelope = rfaEnvelopDao.update(dbEnvelop);
		RfaEventAudit audit = new RfaEventAudit();
		audit.setAction(AuditActionType.Update);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setActionDate(new Date());
		audit.setEvent(event);
		description += evaluatorEditRemoved;
		audit.setDescription(description);
		eventAuditService.save(audit);
		
		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, description + "'" +rfaEvent.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
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
		RfaEnvelop dbEnvelop = rfaEnvelopDao.findById(envelopId);
		RfaEvent event = dbEnvelop.getRfxEvent();
		User leadEvaluator = dbEnvelop.getLeadEvaluater();
		String timeZone = "GMT+8:00";
		String url = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
		timeZone = getTimeZoneByBuyerSettings(logedUser, timeZone);
		User eventOwner = rfaEventService.getPlainEventOwnerByEventId(event.getId());
		String msg = "";
		if (dbEnvelop != null && leadEvaluator.getId().equals(logedUser.getId())) {

			if (StringUtils.checkString(dbEnvelop.getLeadEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}

			Boolean allDone = Boolean.TRUE;
			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators())) {
				for (RfaEvaluatorUser evaluator : dbEnvelop.getEvaluators()) {
					if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
						allDone = Boolean.FALSE;
						break;
					}
				}
			}
			if (Boolean.TRUE == allDone) {
				dbEnvelop.setEvaluationDate(new Date());
				dbEnvelop.setEvaluationStatus(EvaluationStatus.COMPLETE);
				dbEnvelop = rfaEnvelopDao.update(dbEnvelop);
			}
			try {
				// sending notification completed evaluation of envelope to lead
				// Evaluator
				msg = "You have complete evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFA.getValue(), msg);
				// sending notification completed evaluation of envelope to
				// Event Owner
				msg = "\"" + leadEvaluator.getName() + "\" has completed evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFA.getValue(), msg);
				try {
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Evaluate);
					audit.setActionBy(leadEvaluator);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + "' is evaluated");
					rfaEventAuditDao.save(audit);
					
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(event.getId());
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.EVALUATE, "Envelope '" + dbEnvelop.getEnvelopTitle()+"' is Evaluated for Event '"+rfaEvent.getEventId()+ "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				
			} catch (Exception e) {
				LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
			}
			int count = rfaEnvelopDao.findCountPendingEnvelopse(event.getId());
			if (count == 0) {
				event.setStatus(EventStatus.COMPLETE);
				// send notification to unMasking User

				Supplier winningSupplier = rfaEventDao.findWinnerSupplier(event.getId());
				if (winningSupplier != null) {
					event.setWinningSupplier(winningSupplier);
					event.setWinningPrice(winningSupplier.getTotalAfterTax());
				}

				event = rfaEventDao.update(event);
				try {
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Complete);
					audit.setActionBy(leadEvaluator);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Evaluation is completed");
					rfaEventAuditDao.save(audit);
					
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(event.getId());
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Evaluation is completed for Event '"+rfaEvent.getEventId()+ "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				
				try {
					// sending notification completed evaluation of envelope to
					// Event Owner
					msg = "The evaluation for the event \"" + event.getReferanceNumber() + "\" has been completed";
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFA.getValue(), msg);

					// sending notification completed evaluation of envelope to
					// buyer team members
					List<User> buyerMembers = rfaEventDao.getUserBuyerTeamMemberByEventId(event.getId());
					if (CollectionUtil.isNotEmpty(buyerMembers)) {
						for (User buyerTeamUser : buyerMembers) {
							sendEnvelopCompletedNotification(buyerTeamUser, event, url, timeZone, RfxTypes.RFA.getValue(), msg);
						}
					}

					// Send notification to unMasking User on all envelops evaluation completed
					if (Boolean.FALSE == event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
						url = APP_URL + "/buyer/RFA/envelopList/" + event.getId();
						msg = "You are assigned as Unmasking Owner for Event: \"" + event.getReferanceNumber() + "\"";
						for (RfaUnMaskedUser um : event.getUnMaskedUsers()) {
							sendEnvelopCompletedNotificationToUnMaskingUser(um.getUser(), event, url, timeZone, RfxTypes.RFA.getValue(), msg);
						}
					}

				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
				tatReportService.updateTatReportEvaluationCompleted(event.getId(), logedUser.getTenantId(), new Date(), event.getStatus());
				
			}
		} else {
			RfaEvaluatorUser evaluatorUser = rfaEnvelopDao.getRfaEvaluatorUserByUserIdAndEnvelopeId(envelopId, logedUser.getId());

			if (StringUtils.checkString(evaluatorUser.getEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}

			if (evaluatorUser != null) {
				evaluatorUser.setEvaluationDate(new Date());
				evaluatorUser.setEvaluationStatus(EvaluationStatus.COMPLETE);
				updateEvaluatorUser(evaluatorUser);
				try {
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Review);
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + " ' is reviewed");
					rfaEventAuditDao.save(audit);
					
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(event.getId());
					BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.REVIEW, "Envelope '" +dbEnvelop.getEnvelopTitle() + "' is reviewed for Event '"+rfaEvent.getEventId()+ "'", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(ownerAuditTrail);
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					// sending notification to review on envelope to loged user
					msg = "You have completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(logedUser, event, url, timeZone, RfxTypes.RFA.getValue(), msg);

					// sending notification to review on envelope to lead
					// Evaluator
					msg = "\"" + logedUser.getName() + "\" has completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFA.getValue(), msg);

					// sending notification to review on envelope to Event Owner
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFA.getValue(), msg);
				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEvaluatorUser(RfaEvaluatorUser evaluatorUser) {
		evaluatorUserDao.update(evaluatorUser);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfaEnvelop(RfaEnvelop rfaEnvelop) {
		rfaEnvelopDao.delete(rfaEnvelop);
	}

	@Override
	public List<RfaEnvelop> getAllRfaEnvelop() {
		return rfaEnvelopDao.findAll(RfaEnvelop.class);
	}

	@Override
	public boolean isExists(RfaEnvelop rfaEnvelop, String eventId) {
		return rfaEnvelopDao.isExists(rfaEnvelop, eventId);
	}

	@Override
	public RfaEnvelop getRfaEnvelopById(String id) {
		RfaEnvelop envelop = rfaEnvelopDao.findById(id);
		if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
			for (RfaEventBq bq : envelop.getBqList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
			for (RfaCq cq : envelop.getCqList()) {
				cq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
			for (RfaEvaluatorUser evalUser : envelop.getEvaluators()) {
				evalUser.getUser().getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
			for (RfaEventSor bq : envelop.getSorList()) {
				bq.getName();
			}
		}
		if (envelop.getLeadEvaluater() != null) {
			envelop.getLeadEvaluater().getLoginId();
		}
		if (envelop.getOpener() != null) {
			envelop.getOpener().getName();
		}
		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfaEnvelopeOpenerUser openerUser : envelop.getOpenerUsers()) {
				openerUser.getUser().getName();
			}
		}
		return envelop;
	}

	@Override
	public RfaEnvelop getEnvelopForEvaluationById(String id, User logedUser) {
		RfaEnvelop envelop = rfaEnvelopDao.findById(id);
		if (envelop == null) {
			return null;
		} else {
			if (envelop.getRfxEvent() != null) {
				envelop.getRfxEvent().getEventName();

				if (envelop.getRfxEvent().getBaseCurrency() != null) {
					envelop.getRfxEvent().getBaseCurrency().getCurrencyCode();
				}
				if (envelop.getRfxEvent().getStatus() != EventStatus.DRAFT && envelop.getRfxEvent().getStatus() != EventStatus.SUSPENDED) {
					Boolean showFinishForLead = Boolean.TRUE;
					// For Evaluators
					if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
						for (RfaEvaluatorUser evaluator : envelop.getEvaluators()) {
							if (envelop.getIsOpen() && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId()) && evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
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
	public List<RfaEnvelop> getAllEnvelopByEventId(String eventId, User logedUser) {
		List<RfaEnvelop> envList = rfaEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFA);
		if (CollectionUtil.isNotEmpty(envList)) {
			for (RfaEnvelop envelop : envList) {
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RfaEventBq bq : envelop.getBqList()) {
						bq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfaCq cq : envelop.getCqList()) {
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
					for (RfaEvaluatorUser evaluator : envelop.getEvaluators()) {
						LOG.info("evaluator : " + evaluator.getUser().getId() + "  --   " + logedUser.getId());
						if (envelop.getIsOpen() && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId())) {
							envelop.setShowView(true);
						}
					}
				}
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfaEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
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
	public List<RfaEnvelop> getAllRfaEnvelopByEventId(String eventId) {
		List<RfaEnvelop> envList = rfaEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFA);

		List<RfaEnvelop> envList2 = rfaEnvelopDao.getAllEnvelopSorByEventId(eventId, RfxTypes.RFA);

		setSorInBqData(envList, envList2);

		if (CollectionUtil.isNotEmpty(envList)) {
			for (RfaEnvelop rfaEnvelop : envList) {
				if (CollectionUtil.isNotEmpty(rfaEnvelop.getBqList())) {
					for (RfaEventBq bq : rfaEnvelop.getBqList()) {
						bq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(rfaEnvelop.getCqList())) {
					for (RfaCq cq : rfaEnvelop.getCqList()) {
						cq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(rfaEnvelop.getEvaluators())) {
					for (RfaEvaluatorUser evaluator : rfaEnvelop.getEvaluators()) {
						evaluator.getUser().getName();
					}
				}

				if (CollectionUtil.isNotEmpty(rfaEnvelop.getOpenerUsers())) {
					for (RfaEnvelopeOpenerUser opener : rfaEnvelop.getOpenerUsers()) {
						opener.getUser().getName();
					}
				}
			}
		}
		return envList;
	}

	@Override
	public List<String> getRfaBqByEnvelopId(List<String> envelopId) {
		return rfaEnvelopDao.getBqsByEnvelopId(envelopId);
	}

	@Override
	public List<RfaEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		return rfaEnvelopDao.findEvaluatorsByEnvelopId(envelopId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException {
		LOG.info("RfaEnvelopeService Impl  addEvaluator: envelopeId " + envelopeId + " userId: " + userId);

		RfaEnvelop rfaEnvelop = getRfaEnvelopById(envelopeId);
		if (userId.equals(rfaEnvelop.getLeadEvaluater().getId())) {
			throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
		}

		List<RfaEvaluatorUser> evaluators = rfaEnvelop.getEvaluators();
		if (evaluators == null) {
			evaluators = new ArrayList<RfaEvaluatorUser>();
		}
		RfaEvaluatorUser rfaEvaluatorUser = new RfaEvaluatorUser();
		rfaEvaluatorUser.setEnvelope(rfaEnvelop);
		User user = userService.getUsersById(userId);
		try {
			rfaEvaluatorUser.setUser((User) user.clone());
		} catch (Exception e) {
			LOG.error("Error :  " + e.getMessage(), e);
		}
		evaluators.add(rfaEvaluatorUser);
		rfaEnvelop.setEvaluators(evaluators);
		rfaEnvelopDao.saveOrUpdate(rfaEnvelop);
		return evaluators;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEvaluator(String eventId, String envelopeId, String userId) {
		LOG.info("RfaEnvelopeService Impl  removeEvaluator: envelopeId " + envelopeId + " userId: " + userId);
		RfaEnvelop rfaEnvelop = getRfaEnvelopById(envelopeId);
		LOG.info(rfaEnvelop.getId());
		List<RfaEvaluatorUser> rfaEvaluatorUser = rfaEnvelop.getEvaluators();
		if (rfaEvaluatorUser == null) {
			rfaEvaluatorUser = new ArrayList<RfaEvaluatorUser>();
		}
		LOG.info("rfaEvaluatorUser.size() :" + rfaEvaluatorUser.size());
		RfaEvaluatorUser dbEvaluatorUser = getRfaEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
		LOG.info(dbEvaluatorUser.getUser().getName());
		rfaEvaluatorUser.remove(dbEvaluatorUser);
		dbEvaluatorUser.setEnvelope(null);
		rfaEnvelop.setEvaluators(rfaEvaluatorUser);
		rfaEnvelopDao.update(rfaEnvelop);
		LOG.info(" rftEnvelop.getEvaluators() :" + rfaEnvelop.getEvaluators().size());
		List<User> userList = new ArrayList<User>();
		try {
			for (RfaEvaluatorUser rfaeval : rfaEnvelop.getEvaluators()) {
				userList.add((User) rfaeval.getUser().clone());
			}
			LOG.info(userList.size() + " Event ID :" + eventId);
		} catch (Exception e) {
			LOG.error("Error constructing list of users after remove operation : " + e.getMessage(), e);
		}
		return userList;

	}

	private RfaEvaluatorUser getRfaEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId) {
		return rfaEnvelopDao.getRfaEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void openEnvelop(RfaEnvelop envelop) {
		rfaEnvelopDao.update(envelop);
	}

	@Override
	@Transactional(readOnly = true)
	public RfaEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		return evaluatorUserDao.findEvaluatorUser(envelopId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException {
		RfaEnvelop envlope = rfaEnvelopDao.findById(envelopeId);
		RfaEvent event = rfaEventDao.getPlainEventById(eventId);

		int count = 1;
		boolean isCqAvailble = false;
		boolean isBqAvailble = false;
		boolean isSorAvailable = false;
		String suppFolderForAllDocs = "";

		try {
			if (envlope != null && CollectionUtil.isNotEmpty(envlope.getCqList())) {
				isCqAvailble = true;
			}
			if (envlope != null && CollectionUtil.isNotEmpty(envlope.getBqList())) {
				isBqAvailble = true;
			}
			if (envlope != null && CollectionUtil.isNotEmpty(envlope.getSorList())) {
				isSorAvailable = true;
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		String zipFileName = event.getReferanceNumber().replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
		LOG.info("====zipFileName====" + zipFileName);
		zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
		String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
		parentFolder += "-" + envlope.getEnvelopTitle().replaceAll(" ", "_").replaceAll("/", "-");
		parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");
		// Get All Supplier List
		LOG.info("====parentFolder========" + zipFileName);
		// List<EventSupplier> supplierList = rfaEventSupplierDao.getAllSuppliersByEventId(eventId);
		List<EventSupplierPojo> supplierList = rfaEventSupplierDao.getSubmitedSuppliers(eventId);
		if (CollectionUtil.isNotEmpty(supplierList)) {
			int i = 1;
			for (EventSupplierPojo supplier : supplierList) {
				// take only submitted ones
				// if (Boolean.TRUE == supplier.getSubmitted())
				if (envlope.getEnvelopSequence() != null && Boolean.FALSE == event.getAllowDisqualifiedSupplierDownload() && Boolean.TRUE == supplier.getDisqualify() && supplier.getDisqualifiedEnvelopSeq() != null && envlope.getEnvelopSequence() > supplier.getDisqualifiedEnvelopSeq()) {
					continue;
				}

				String supplierFolder = null;
				// [eventRef+envelopeName]/[supplierCompanyName]/
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					supplierFolder = parentFolder + Global.PATH_SEPARATOR + (MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId())) + Global.PATH_SEPARATOR;

				} else {
					supplierFolder = parentFolder + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + Global.PATH_SEPARATOR;
				}
				LOG.info("====supplierFolder========" + supplierFolder);

				// BQ
				if (Boolean.TRUE == event.getBillOfQuantity()) {
					if (isBqAvailble) {
						JasperPrint jasperPrint = generateSupplierBqPdfForEnvelope(envelopeId, supplier.getId(), i, virtualizer);
						if (jasperPrint != null) {
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Bill of Quantity" + " - " + (MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_")));
								zos.flush();
							} else {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Bill of Quantity" + " - " + supplier.getCompanyName().replaceAll(" ", "_"));
								zos.flush();
							}
						}
					}

					// Export BQ FOR EACH SUPPLIER
					try {
						bqExportExcelForEachSupplier(envelopeId, zos, event, supplier, supplierFolder, i);
					} catch (Exception e) {
						LOG.error("Error While Export bq for each supplier : " + e.getMessage(), e);
					}
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
						LOG.error("Error While Export bq for each supplier : " + e.getMessage(), e);
					}

				} else {
					LOG.info("No SOR setup for event : " + eventId);
				}

				// CQ
				if (Boolean.TRUE == event.getQuestionnaires()) {
					if (isCqAvailble) {
						JasperPrint jasperPrint = generateSupplierCqPdfForEnvelope(envelopeId, supplier.getId(), i);
						if (jasperPrint != null) {
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Questionnaire" + " - " + (MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_")));
								zos.flush();
							} else {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Questionnaire" + " - " + supplier.getCompanyName().replaceAll(" ", "_"));
								zos.flush();
							}

						}
					}
					if (CollectionUtil.isNotEmpty(envlope.getCqList())) {
						for (RfaCq cq : envlope.getCqList()) {
							String attachmentFolder = "";
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;
							} else {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;

							}
							List<RfaSupplierCqItem> supplierCqItems = rfaSupplierCqItemDao.getSupplierCqItemsbySupplier(supplier.getId(), cq.getId());
							if (CollectionUtil.isNotEmpty(supplierCqItems)) {
								for (RfaSupplierCqItem supCqItem : supplierCqItems) {
									if (supCqItem.getFileData() != null) {

										// String extension =
										// FilenameUtils.getExtension(StringUtils.checkString(supCqItem.getFileName()));
										String fileName = supCqItem.getCqItem().getLevel() + "-" + supCqItem.getCqItem().getOrder() + "-" + supCqItem.getFileName();
										// [attachment name will be the CQ
										// item number and file name e.g.
										// 1-2-FileName]
										FileUtil.writeFileToZip(zos, supCqItem.getFileData(), attachmentFolder, fileName);
										zos.flush();
									}
								}
							}

						}
					}

				} else {
					LOG.info("No CQ setup for event : " + eventId);
				}
				suppFolderForAllDocs = supplierFolder;
				i++;
			}
		}
		if (isForAllReports) {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			LOG.info("short summary");

			JasperPrint jasperPrint = rftEventService.generateShortEvaluationSummaryReport("RFA", eventId, envelopeId, timeZone, virtualizer);
			if (jasperPrint != null) {
				LOG.info("short summary");
				FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Short Submission Summary for " + envlope.getEnvelopTitle());
				zos.flush();
			}

			try {
				jasperPrint = rfaEventService.getEvaluationReport(eventId, envelopeId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Evaluation Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}
			} catch (IOException e) {
			} catch (JRException e) {
			}

			try {
				jasperPrint = rfaEventService.generateSubmissionReport(envelopeId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Submission Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}
			} catch (IOException e) {
			} catch (JRException e) {
			}

			List<EventEvaluationPojo> list = null;

			list = rfaSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, envelopeId);
			if (CollectionUtil.isNotEmpty(list)) {
				XSSFWorkbook workbookBq = new XSSFWorkbook();
				workbookBq = rfaEventService.buildBqComparisionFile(workbookBq, list, null, eventId, envelopeId, RfxTypes.RFA);
				if (workbookBq != null) {
					FileUtil.writeXssfExcelToZip(zos, workbookBq, parentFolder + "/", "BQ Comparison Table.xlsx");
					zos.flush();
				}

			}

			XSSFWorkbook workbookCq = new XSSFWorkbook();
			list = rfaSupplierCqItemService.getEvaluationDataForCqComparison(eventId, envelopeId);
			if (CollectionUtil.isNotEmpty(list)) {
				workbookCq = rfaEventService.buildCqComparisionFile(workbookCq, list, null);
				if (workbookCq != null) {
					FileUtil.writeXssfExcelToZip(zos, workbookCq, parentFolder + "/", "CQ Comparison Table.xlsx");
					zos.flush();
				}

			}
			count++;
		}

		return zipFileName;

	}


	@Override
	public JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationSorPojo> bqSummary = new ArrayList<EvaluationSorPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			LOG.info("Generating RFQ Supplier SOR Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			List<SorPojo> bqList = rfaEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierSors.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (SorPojo bqPojo : bqList) {
					EvaluationSorPojo bq = new EvaluationSorPojo();
					String bqId = bqPojo.getId();
					RfaEventSor bqDetail = rfaEventSorDao.findById(bqId);
					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRfaEnvelop())) {
						for (RfaEnvelop env : bqDetail.getRfxEvent().getRfaEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					List<RfaSupplierSorItem> supBqItem = rfaSupplierSorItemDao.findSupplierSorItemByListBySorIdAndSupplierId(bqId, supplierId);
					List<EvaluationSorItemPojo> bqItems = new ArrayList<EvaluationSorItemPojo>();
					for (RfaSupplierSorItem item : supBqItem) {
						EvaluationSorItemPojo bqItem = new EvaluationSorItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RfaSupplierSorItem childBqItem : item.getChildren()) {
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
			RfaEvent event = rfaEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
			RfaEnvelop rfqEnvelop = getRfaEnvelopById(envelopeId);
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

	private void bqExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RfaEvent event, EventSupplierPojo supplier, String supplierFolder, int i2) throws IOException {
		List<BqPojo> bqPojos = rfaEnvelopDao.getBqNameAndIdsByEnvelopId(Arrays.asList(envelopeId));
		RfaEnvelop envelope = getRfaEnvelopById(envelopeId);
		if (CollectionUtil.isNotEmpty(bqPojos)) {

			// For Financial Standard
			/*
			 * DecimalFormat df = null; if (event.getDecimal().equals("1")) { df = new DecimalFormat("#,###,###,##0.0");
			 * } else if (event.getDecimal().equals("2")) { df = new DecimalFormat("#,###,###,##0.00"); } else if
			 * (event.getDecimal().equals("3")) { df = new DecimalFormat("#,###,###,##0.000"); } else if
			 * (event.getDecimal().equals("4")) { df = new DecimalFormat("#,###,###,##0.0000"); }
			 */

			for (BqPojo bqPojo : bqPojos) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("BQ ITEM");

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

				// Creating Headings
				XSSFRow rowHeading = sheet.createRow(1);
				XSSFRow rowSupplierName = sheet.createRow(0);
				XSSFCell cellS2name = rowSupplierName.createCell(5);
				int i = 0;
				int cellNoByerHeading = 0;
				int fixedheadingCount = 0;
				for (String column : Global.BQ_EVALUATION_EXCEL_COLUMNS) {
					XSSFCell cell = rowHeading.createCell(i++);
					cell.setCellValue(column);
					// cell.setCellStyle(blueRightBoldStyle);
					cellNoByerHeading = i;
					fixedheadingCount = i;

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
				List<RfaSupplierBqItem> bqItems = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int decimal = 2;
					// Write Data into Excel
					int bqItemLoopCount = 1;

					for (RfaSupplierBqItem item : bqItems) {
						LOG.info("item" + item.toLogString());
						XSSFRow row = sheet.createRow(r++);
						int cellNum = 0;
						row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
						row.createCell(cellNum++).setCellValue(item.getItemName() != null ? item.getItemName() : "");
						row.createCell(cellNum++).setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
						// merge column for section
						if (bqItemLoopCount == 1) {
							setBuyerSupplierHeadings(buyerHeadingList, supplierHeadingList, item.getBqItem());
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
						LOG.info("++++++++++++++++columnCount" + columnCount);
						sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, columnCount));
						if (CollectionUtil.isNotEmpty(item.getChildren())) {
							for (RfaSupplierBqItem child : item.getChildren()) {
								row = sheet.createRow(r++);
								cellNum = 0;

								row.createCell(cellNum++).setCellValue(child.getLevel() + "." + child.getOrder());
								row.createCell(cellNum++).setCellValue(child.getItemName() != null ? child.getItemName() : "");
								row.createCell(cellNum++).setCellValue(child.getItemDescription() != null ? child.getItemDescription() : "");
								row.createCell(cellNum++).setCellValue(child.getUom() != null ? child.getUom().getUom() : "");

								// row.createCell(cellNum++).setCellValue(child.getQuantity()
								// != null ?
								// child.getQuantity() + "" : "");

								XSSFCell quantityCell = row.createCell(cellNum++);
								quantityCell.setCellValue(child.getQuantity() != null ? child.getQuantity().doubleValue() : 0);
								// quantityCell.setCellStyle(formatNumberStyle);
								quantityCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
								quantityCell.setCellStyle(formatNumberStyle);
								BigDecimal unitPrice = BigDecimal.ZERO;
								XSSFCell unitPriceCell = row.createCell(cellNum++);
								if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
									unitPrice = (child.getUnitPrice() != null ? child.getUnitPrice().negate() : BigDecimal.ZERO);
								} else {
									unitPrice = (child.getUnitPrice() != null ? child.getUnitPrice() : BigDecimal.ZERO);
								}
								unitPriceCell.setCellValue(unitPrice.doubleValue());
								unitPriceCell.setCellStyle(formatNumberStyle);
								unitPriceCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);

								String totalAmountFormula = "PRODUCT(E" + r + " , F" + r + ")";
								LOG.info("======totalAmountFormula==========>" + totalAmountFormula);
								XSSFCell totalAmountCell = row.createCell(cellNum++);
								totalAmountCell.setCellValue(child.getTotalAmount() != null ? child.getTotalAmount().doubleValue() : 0);
								totalAmountCell.setCellStyle(formatNumberStyle);
								totalAmountCell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
								totalAmountCell.setCellFormula(totalAmountFormula);

								XSSFCell taxCell = row.createCell(cellNum++);
								if (TaxType.Percent == child.getTaxType()) {
									BigDecimal taxVlaue = BigDecimal.ZERO;
									if (child.getTotalAmount() != null && child.getTax() != null) {
										LOG.info(">>>>>>>>>>>>>>>>>>>>>>>" + child.getTotalAmount() + " : " + child.getTax());
										taxVlaue = child.getTotalAmount().multiply(child.getTax()).divide(new BigDecimal(100), (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP);
									}
									taxCell.setCellValue(taxVlaue.doubleValue());
								} else {
									taxCell.setCellValue(child.getTax() != null ? child.getTax().doubleValue() : 0);
								}
								taxCell.setCellStyle(formatNumberStyle);
								taxCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);

								String totalAmountWithTaxFormula = "SUM(G" + r + ",H" + r + ")";

								XSSFCell totalAmountWithTaxCell = row.createCell(cellNum++);
								totalAmountWithTaxCell.setCellValue(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax().doubleValue() : 0);
								totalAmountWithTaxCell.setCellStyle(formatNumberStyle);
								totalAmountWithTaxCell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
								totalAmountWithTaxCell.setCellFormula(totalAmountWithTaxFormula);

								getBuyerSupplierExtraColumnData(item, row, cellNum, child);

								// sheet.addMergedRegion(new CellRangeAddress(0, // first
								// // row
								// // (0-based)
								// 0, // last row (0-based)
								// 5, // first column (0-based)
								// 8 // last column (0-based)
								// ));
								if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
									cellS2name.setCellValue(MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelopeId));
								} else {
									cellS2name.setCellValue(supplier.getCompanyName());
								}
								XSSFCellStyle myCellStyle3 = workbook.createCellStyle();
								myCellStyle3.setFont(font);
								myCellStyle3.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
								myCellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
								myCellStyle3.setAlignment(HorizontalAlignment.CENTER);
								cellS2name.setCellStyle(myCellStyle3);

								// for (int p = 6; p <= 8; p++) {
								// cellS2name = rowSupplierName.createCell(p);
								// cellS2name.setCellValue("");
								// cellS2name.setCellStyle(myCellStyle3);
								// }


							}
						}
					}

					sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 8));

					if (CollectionUtil.isNotEmpty(supplierHeadingList)) {
						int cellNub = fixedheadingCount + buyerHeadingList.size();
						/* Row rowSupplierName = sheet.createRow(0); */
						XSSFCell cellSname = rowSupplierName.createCell(cellNub);

						if (supplierHeadingList.size() > 0) {
							sheet.addMergedRegion(new CellRangeAddress(0, // first
																			// row
																			// (0-based)
									0, // last row (0-based)
									9, // first column (0-based)
									8 + supplierHeadingList.size() // last
																	// column
																	// (0-based)
							));
						}
						// cellSname.setCellValue(supplier.getSupplier().getCompanyName());
						cellSname.setCellValue("Additional Columns");
						// cellSname.setCellStyle(styleHeading);
						XSSFCellStyle myCellStyle = workbook.createCellStyle();
						myCellStyle.setFont(font);
						myCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
						myCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						cellSname.setCellStyle(myCellStyle);

						// for (int p = 10; p <= 8 + supplierHeadingList.size(); p++) {
						// cellSname = rowSupplierName.createCell(p);
						// cellSname.setCellValue("");
						// cellSname.setCellStyle(myCellStyle);
						// }

					}

					if (CollectionUtil.isNotEmpty(buyerHeadingList)) {
						for (String buyerHeading : buyerHeadingList) {
							XSSFRow row = sheet.getRow(1);
							XSSFCell cell = row.createCell(cellNoByerHeading++);
							cell.setCellValue(buyerHeading);
							cell.setCellStyle(styleHeading);
						}
					}

					if (CollectionUtil.isNotEmpty(supplierHeadingList)) {
						for (String supplierHeading : supplierHeadingList) {
							XSSFRow row = sheet.getRow(1);
							XSSFCell cell = row.createCell(cellNoByerHeading++);
							cell.setCellValue(supplierHeading);
							XSSFCellStyle myCellStyle = workbook.createCellStyle();
							myCellStyle.setFont(font);
							myCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
							myCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							cell.setCellStyle(myCellStyle);
						}
					}

					RfaSupplierBq supplierBq = rfaSupplierBqDao.findBqByBqId(bqPojo.getId(), supplier.getId());
					String subTotalFormula = "SUM(I4:I" + r + ")";
					r++;
					XSSFRow row = sheet.createRow(r++);
					row.createCell(4).setCellValue("Sub Total");
					XSSFCell subTotalCell = row.createCell(8);
					subTotalCell.setCellValue(supplierBq.getGrandTotal() != null ? supplierBq.getGrandTotal().doubleValue() : 0);
					subTotalCell.setCellStyle(formatNumberStyle);
					subTotalCell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					subTotalCell.setCellFormula(subTotalFormula);

					String grandTotalFormula = "SUM(I" + r + ",I";
					row = sheet.createRow(r++);
					row.createCell(4).setCellValue("Additional Tax");
					row.createCell(7).setCellValue(supplierBq.getTaxDescription() != null ? supplierBq.getTaxDescription() : "");
					XSSFCell addtaxCell = row.createCell(8);
					addtaxCell.setCellValue(supplierBq.getAdditionalTax() != null ? supplierBq.getAdditionalTax().doubleValue() : 0);
					addtaxCell.setCellStyle(formatNumberStyle);
					addtaxCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
					grandTotalFormula += r + ")";
					row = sheet.createRow(r++);
					XSSFCell grandTotalCell = row.createCell(4);
					grandTotalCell.setCellValue("Grand Total (" + event.getBaseCurrency() + ")");
					grandTotalCell.setCellStyle(styleHeading);

					XSSFCell totalAfetTaxCell = row.createCell(8);
					totalAfetTaxCell.setCellValue(supplierBq.getTotalAfterTax() != null ? supplierBq.getTotalAfterTax().doubleValue() : 0);
					totalAfetTaxCell.setCellFormula(grandTotalFormula);
					totalAfetTaxCell.setCellStyle(numberBoldRight);
					totalAfetTaxCell.setCellType(XSSFCell.CELL_TYPE_FORMULA);

				}
				// Auto Fit
				for (int k = 0; k < 26; k++) {
					sheet.autoSizeColumn(k, true);
				}
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					FileUtil.writeXssfExcelToZip(zos, workbook, supplierFolder, (MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()).replaceAll(" ", "_")) + " - " + bqPojo.getBqName() + ".xlsx");
				} else {
					FileUtil.writeXssfExcelToZip(zos, workbook, supplierFolder, supplier.getCompanyName().replaceAll(" ", "_") + " - " + bqPojo.getBqName() + ".xlsx");
				}

			}
		} else {
			LOG.info("NO BQ for this envelop id : " + envelopeId);
		}
	}

	private void getBuyerSupplierExtraColumnData(RfaSupplierBqItem item, Row row, int cellNum, RfaSupplierBqItem child) {
		if (StringUtils.checkString(item.getSupplierBq().getField1Label()).length() > 0) {
			if (item.getSupplierBq().getField1FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField1());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField1());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField2Label()).length() > 0) {
			if (item.getSupplierBq().getField2FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField2());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField2());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField3Label()).length() > 0) {
			if (item.getSupplierBq().getField3FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField3());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField3());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField4Label()).length() > 0) {
			if (item.getSupplierBq().getField4FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField4());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField4());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField5Label()).length() > 0) {
			if (item.getSupplierBq().getField5FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField5());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField5());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField6Label()).length() > 0) {
			if (item.getSupplierBq().getField6FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField6());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField6());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField7Label()).length() > 0) {
			if (item.getSupplierBq().getField7FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField7());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField7());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField8Label()).length() > 0) {
			if (item.getSupplierBq().getField8FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField8());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField8());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField9Label()).length() > 0) {
			if (item.getSupplierBq().getField9FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField9());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField9());
			}
		}
		if (StringUtils.checkString(item.getSupplierBq().getField10Label()).length() > 0) {
			if (item.getSupplierBq().getField10FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField10());
			} else {
				row.createCell(cellNum++).setCellValue(child.getField10());
			}
		}
	}

	private void setBuyerSupplierHeadings(List<String> buyerHeadingList, List<String> supplierHeadingList, RfaBqItem bqItem) {
		if (StringUtils.checkString(bqItem.getBq().getField1Label()).length() > 0) {
			if (bqItem.getBq().getField1FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField1Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField1Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField2Label()).length() > 0) {
			if (bqItem.getBq().getField2FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField2Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField2Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField3Label()).length() > 0) {
			if (bqItem.getBq().getField3FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField3Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField3Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField4Label()).length() > 0) {
			if (bqItem.getBq().getField4FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField4Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField4Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField5Label()).length() > 0) {
			if (bqItem.getBq().getField5FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField5Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField5Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField6Label()).length() > 0) {
			if (bqItem.getBq().getField6FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField6Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField6Label());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField7Label()).length() > 0) {
			if (bqItem.getBq().getField7FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField7Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField7Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField8Label()).length() > 0) {
			if (bqItem.getBq().getField8FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField8Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField8Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField9Label()).length() > 0) {
			if (bqItem.getBq().getField9FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField9Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField9Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField10Label()).length() > 0) {
			if (bqItem.getBq().getField10FilledBy() == BqUserTypes.BUYER) {
				buyerHeadingList.add(bqItem.getBq().getField10Label());
			} else {
				supplierHeadingList.add(bqItem.getBq().getField10Label());
			}
		}

	}

	@Override
	public JasperPrint generateSupplierBqPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationBqPojo> bqSummary = new ArrayList<EvaluationBqPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			LOG.info("Generating RFP Supplier BQ Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			List<String> bqList = rfaEnvelopDao.getBqsByEnvelopId(Arrays.asList(envelopeId));
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierBqs.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (String envlop : bqList) {
					EvaluationBqPojo bq = new EvaluationBqPojo();
					String bqId = envlop;
					RfaEventBq bqDetail = rfaEventBqDao.findById(bqId);

					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRfaEnvelop())) {
						for (RfaEnvelop env : bqDetail.getRfxEvent().getRfaEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);

					List<RfaSupplierBqItem> supBqItem = rfaSupplierBqItemDao.findSupplierBqItemListByBqIdAndSupplierId(bqId, supplierId);
					RfaSupplierBq supplierBq = rfaSupplierBqDao.findBqByBqId(bqId, supplierId);
					// Supplier supplier = supplierBq.getSupplier();
					BigDecimal additionalTax = null, grandTotal = null, totalAfterTax = null;
					String additionalTaxDesc = "";
					if (supplierBq != null) {
						additionalTax = supplierBq.getAdditionalTax();
						additionalTaxDesc = supplierBq.getTaxDescription();
						grandTotal = supplierBq.getGrandTotal();
						totalAfterTax = supplierBq.getTotalAfterTax();
					}
					List<EvaluationBqItemPojo> bqItems = new ArrayList<EvaluationBqItemPojo>();
					for (RfaSupplierBqItem item : supBqItem) {
						EvaluationBqItemPojo bqItem = new EvaluationBqItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItem.setAdditionalTax(item.getAdditionalTax());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RfaSupplierBqItem childBqItem : item.getChildren()) {
								EvaluationBqItemPojo evlBqChilItem = new EvaluationBqItemPojo();
								evlBqChilItem.setDescription(childBqItem.getItemName());
								evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
								evlBqChilItem.setQuantity(childBqItem.getQuantity());
								evlBqChilItem.setUom(childBqItem.getUom().getUom());
								evlBqChilItem.setTaxAmt(childBqItem.getTax());
								evlBqChilItem.setUnitPrice(childBqItem.getUnitPrice());
								evlBqChilItem.setTotalAmt(childBqItem.getTotalAmountWithTax());
								evlBqChilItem.setAmount(childBqItem.getTotalAmount());
								evlBqChilItem.setAdditionalTax(additionalTax);
								evlBqChilItem.setAdditionalTaxDesc(additionalTaxDesc);
								evlBqChilItem.setTotalAfterTax(totalAfterTax);
								evlBqChilItem.setGrandTotal(grandTotal != null ? grandTotal : BigDecimal.ZERO);
								/*
								 * evlBqChilItem.setSupplierName(StringUtils. checkString(supplier.getCompanyName()).
								 * length() > 0 ? supplier.getCompanyName() : "");
								 */evlBqChilItem.setDecimal(childBqItem.getEvent() != null ? childBqItem.getEvent().getDecimal() : "");
								bqItems.add(evlBqChilItem);
							}
						}
					}
					bq.setBqItems(bqItems);
					bqSummary.add(bq);
				}
			}
			parameters.put("SUPPLIER_BQS", bqSummary);

			RfaEvent event = rfaEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);
			RfaEnvelop envelope = getRfaEnvelopById(envelopeId);
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			parameters.put("eventType", RfxTypes.RFA.getValue() + "(" + event.getAuctionType().getValue() + ")");
			parameters.put("eventId", StringUtils.checkString(event.getEventId()));
			parameters.put("referenceNumber", StringUtils.checkString(event.getReferanceNumber()));
			parameters.put("eventCreationDate", (event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : ""));
			parameters.put("eventPublishDate", (event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : ""));
			parameters.put("eventStartDate", (event.getEventStart() != null ? sdf.format(event.getEventStart()) : ""));
			parameters.put("eventEndDate", (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : ""));
			parameters.put("currencyCode", StringUtils.checkString(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : ""));
			parameters.put("generatedOn", sdf.format(new Date()));
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking() && i != 0) {
				parameters.put("supplierName", MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()));
			} else {
				parameters.put("supplierName", StringUtils.checkString(supplier.getCompanyName()));
			}
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(bqSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Error generating Supplier BQ PDF for envelope : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int i) {
		List<EvaluationCqPojo> cqSummary = new ArrayList<EvaluationCqPojo>();
		RfaEnvelop envolpe = rfaEnvelopDao.findById(envelopeId);
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierCqs.jasper");
			File jasperfile = resource.getFile();
			// List<String> cqList = rfaEnvelopDao.getCqsByEnvelopId(Arrays.asList(envelopeId));
			List<CqPojo> cqList = rfaEnvelopDao.getCqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			if (CollectionUtil.isNotEmpty(cqList)) {
				for (CqPojo cqPojo : cqList) {
					EvaluationCqPojo cq = new EvaluationCqPojo();
					String cqId = cqPojo.getId();
					RfaCq cqDetail = rfaCqDao.findById(cqId);
					String title = "";
					if (CollectionUtil.isNotEmpty(cqDetail.getRfxEvent().getRfaEnvelop())) {
						for (RfaEnvelop env : cqDetail.getRfxEvent().getRfaEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					cq.setTitle(title);
					cq.setName(cqDetail.getName());
					// List<RfaSupplierCqItem> supCqItem = rfaSupplierCqItemDao.findSupplierCqItemListByCqId(cqId,
					// supplierId);
					List<RfaSupplierCqItem> supCqItem = rfaSupplierCqItemDao.findSupplierCqItemByCqIdandSupplierId(cqId, supplierId);
					List<EvaluationCqItemPojo> cqItems = new ArrayList<EvaluationCqItemPojo>();
					for (RfaSupplierCqItem item : supCqItem) {
						List<RfaSupplierCqOption> listAnswers = rfaSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
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
								for (RfaSupplierCqOption cqOption : listAnswers) {
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
							for (RfaSupplierCqOption cqOption : listAnswers) {
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

			RfaEvent event = rfaEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			parameters.put("eventType", RfxTypes.RFA.getValue() + "(" + event.getAuctionType().getValue() + ")");
			parameters.put("eventId", StringUtils.checkString(event.getEventId()));
			parameters.put("referenceNumber", StringUtils.checkString(event.getReferanceNumber()));
			parameters.put("eventCreationDate", (event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : ""));
			parameters.put("eventPublishDate", (event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : ""));
			parameters.put("eventStartDate", (event.getEventStart() != null ? sdf.format(event.getEventStart()) : ""));
			parameters.put("eventEndDate", (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : ""));
			parameters.put("currencyCode", StringUtils.checkString(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : ""));
			parameters.put("generatedOn", sdf.format(new Date()));
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking() && i != 0) {
				parameters.put("supplierName", MaskUtils.maskName(envolpe.getPreFix(), supplier.getId(), envolpe.getId()));
			} else {
				parameters.put("supplierName", StringUtils.checkString(supplier.getCompanyName()));
			}

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(cqSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Error generating Supplier CQ PDF for envelope : " + e.getMessage(), e);
		}
		return jasperPrint;
	}


	private void sorExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RfaEvent event, EventSupplierPojo supplier, String supplierFolder, int supplierNo) throws IOException {
		List<SorPojo> bqPojos = rfaEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		RfaEnvelop envelope = getRfaEnvelopById(envelopeId);
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
				List<RfaSupplierSorItem> bqItems = rfaSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int bqItemLoopCount = 1;

					for (RfaSupplierSorItem item : bqItems) {

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

							for (RfaSupplierSorItem child : item.getChildren()) {
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

	@Override
	public JasperPrint generateEvaluationSubmissionReport(String evenvelopId, String eventId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = null;

		List<EvaluationPojo> submissionSummary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		RfaEnvelop envlope = getRfaEnvelopById(evenvelopId);

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
			RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
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
						for (RfaCq cq : envlope.getCqList()) {
							EvaluationCqPojo item = new EvaluationCqPojo();
							item.setName(cq.getName());
							item.setDescription(cq.getDescription());
							cqs.add(item);
						}
					}
					eventDetails.setCqs(cqs);
					List<EvaluationBqPojo> bqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envlope.getBqList())) {
						for (RfaEventBq item : envlope.getBqList()) {
							EvaluationBqPojo bq = new EvaluationBqPojo();
							bq.setName(item.getName());
							bqs.add(bq);
						}
					}
					eventDetails.setBqs(bqs);
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
			LOG.error("Could not generate RFT Evaluation Submission Summary PDF Report. " + e.getMessage(), e);
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
		List<EvaluationEnvelopSuppliersPojo> supplierCqBqList = new ArrayList<EvaluationEnvelopSuppliersPojo>();
		List<EventSupplier> suppliers = rfaEventSupplierDao.getAllSuppliersByEventId(eventId);
		if (CollectionUtil.isNotEmpty(suppliers)) {
			for (EventSupplier item : suppliers) {
				EvaluationSuppliersPojo eventSup = new EvaluationSuppliersPojo();
				eventSup.setSupplierName(item.getSupplierCompanyName());
				eventSup.setContactName(item.getSupplier().getFullName());
				eventSup.setEmail(item.getSupplier().getCommunicationEmail());
				eventSup.setContactNo(item.getSupplier().getCompanyContactNumber());
				eventSup.setStatus(item.getSubmissionStatus().name());
				supplierList.add(eventSup);

				// Fetch Supplier CQ and BQ details

				List<RfaCq> suppCqItem = null;
				List<RfaSupplierBq> suppBqItem = null;
				if (item.getSupplier() != null) {
					suppCqItem = rfaSupplierCqItemDao.getAllCqsBySupplierId(eventId, item.getSupplier().getId());
					suppBqItem = rfaSupplierBqItemDao.getAllBqsBySupplierId(eventId, item.getSupplier().getId());
				}

				List<EvaluationCqPojo> cqs = new ArrayList<EvaluationCqPojo>();
				List<EvaluationBqPojo> bqs = new ArrayList<EvaluationBqPojo>();

				EvaluationEnvelopSuppliersPojo envelopSuppliers = new EvaluationEnvelopSuppliersPojo();
				envelopSuppliers.setSupplierName(item.getSupplierCompanyName());
				if (item.getSupplierSubmittedTime() != null) {
					envelopSuppliers.setSubmissionDate(new Date(sdf.format(item.getSupplierSubmittedTime()).toUpperCase()));
				}
				envelopSuppliers.setSubmittedBy(item.getSubbmitedBy() != null ? item.getSubbmitedBy().getName() : "");
				if (CollectionUtil.isNotEmpty(suppCqItem)) {
					for (RfaCq cqItem : suppCqItem) {
						EvaluationCqPojo cqList = new EvaluationCqPojo();
						cqList.setName(cqItem.getName());
						cqs.add(cqList);
					}
				}
				if (CollectionUtil.isNotEmpty(suppBqItem)) {
					for (RfaSupplierBq bqItem : suppBqItem) {
						EvaluationBqPojo bqList = new EvaluationBqPojo();
						bqList.setName(bqItem.getBq().getName());
						bqs.add(bqList);
					}
				}
				envelopSuppliers.setCqs(cqs);
				envelopSuppliers.setBqs(bqs);
				supplierCqBqList.add(envelopSuppliers);
			}
		}
		eventDetails.setEnvlopSuppliers(supplierCqBqList);
		return supplierList;
	}

	@Override
	public Integer getAllEnvelopCountByEventId(String eventId) {
		return rfaEnvelopDao.getAllEnvelopCountByEventId(eventId);
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		return rfaEnvelopDao.getcountClosedEnvelop(eventId);
	}

	@Override
	public List<RfaEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		return rfaEnvelopDao.getAllClosedEnvelopAndOpener(eventId);
	}

	@Override
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		return evaluatorUserDao.getAllEnvelopEvaluatorUsers(eventId);
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFA)));
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFA)));
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
		case RFA:
			displayName = rfaEventDao.findBusinessUnitName(eventId);
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


	private void setSorInBqData(List<RfaEnvelop> envList, List<RfaEnvelop> envList2) {
		Map<String, RfaEnvelop> envelopMap = envList2.stream().collect(Collectors.toMap(RfaEnvelop::getId, Function.identity()));
		envList.forEach(event -> {
			RfaEnvelop matchingEvent = envelopMap.get(event.getId());
			if(matchingEvent != null) {
				event.setSorList(matchingEvent.getSorList());
			}
		});
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean openEnvelope(String envelopeId, String eventId, String key, User loggedInUser) {
		loggedInUser = userDao.getPlainUserByLoginId(loggedInUser.getLoginId());
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		Boolean success = false;
		RfaEnvelop envelop = rfaEnvelopDao.findById(envelopeId);
		if (envelop.getOpener() != null) {
			if (enc.matches(key, envelop.getOpener().getPassword())) {
				envelop.setIsOpen(Boolean.TRUE);
				envelop.setOpenDate(new Date());
				envelop = rfaEnvelopDao.update(envelop);
				success = envelop.getIsOpen();
				LOG.info("updated successfully.....");
				try {
					super.sendEnvelopOpenNotification(envelop, RfxTypes.RFA, eventId, loggedInUser, true);
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
	public List<RfaEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User logedInUser) {
		return rfaEvaluatorUserDao.getEvaluationSummaryRemarks(evelopId, logedInUser);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEnvelop updateLeadEvaluatorSummary(RfaEnvelop envelop) {
		return rfaEnvelopDao.update(envelop);
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		return rfaEnvelopDao.getCountOfAssignedSupplier(leadUserId, envelopId, eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void closeEnvelop(RfaEnvelop envelop) {
		rfaEnvelopDao.update(envelop);
	}

	protected XSSFWorkbook buildCqComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list) throws IOException {
		if (CollectionUtil.isNotEmpty(list)) {
			for (EventEvaluationPojo evaluationPojo : list) {
				XSSFSheet sheet = workbook.createSheet();
				Row rowHeading = sheet.createRow(0);
				CellStyle styleHeading = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				styleHeading.setFont(font);
				// styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
				styleHeading.setAlignment(CellStyle.ALIGN_CENTER);
				int r = 1;

				int i = 3;
				int cellMerge = 2;
				for (Supplier column : evaluationPojo.getColumns()) {
					cellMerge++;
					Cell cell = rowHeading.createCell(i++);
					cell.setCellValue(column.getCompanyName());
					cell.setCellStyle(styleHeading);
					cell = rowHeading.createCell(i++);
					cell.setCellStyle(styleHeading);
					// sheet.addMergedRegion(new CellRangeAddress(0, 0, cellMerge, ++cellMerge));
				}

				i = 0;
				Row row = sheet.createRow(r++);
				Cell cell = row.createCell(i++);
				cell.setCellValue("No");
				// cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Question");
				// cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Scoring Scale");
				// cell.setCellStyle(styleHeading);

				List<Supplier> columns = evaluationPojo.getColumns();
				for (int j = 0; j < columns.size(); j++) {
					cell = row.createCell(i++);
					cell.setCellValue("Answer");
					cell.getCellStyle().setWrapText(true);
					// cell.setCellStyle(styleHeading);
					cell = row.createCell(i++);
					cell.setCellValue("Score");
					// cell.setCellStyle(styleHeading);
				}

				for (List<String> data : evaluationPojo.getData()) {
					row = sheet.createRow(r++);
					int cellNum = 0;
					for (String answers : data) {
						Cell anserwCell = row.createCell(cellNum++);
						anserwCell.setCellValue(answers);
						anserwCell.getCellStyle().setWrapText(true);
					}
				}

				r++;
				int cellNum = 1;
				Row totalRow = sheet.createRow(r++);
				cell = totalRow.createCell(cellNum++);
				cell.setCellValue("Total Scoring");
				cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
				for (String score : evaluationPojo.getScoring()) {
					if (cellNum == 2) {
						cell = totalRow.createCell(cellNum++);
						cell.setCellValue(score);
						cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
					} else {
						cell = totalRow.createCell(cellNum++);
						cell = totalRow.createCell(cellNum++);
						cell.setCellValue(score);
						cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
					}

				}

				for (int columnPosition = 0; columnPosition < 25; columnPosition++) {
					sheet.autoSizeColumn((short) (columnPosition));
				}
			}

			String downloadFolder = System.getProperty("user.home");
			String fileName = "CqComparisonTable.xlsx";
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();

		}
		return workbook;
	}

	@Override
	public RfaEnvelop getRfaEnvelopBySeq(int i, String eventId) {
		return rfaEnvelopDao.getRfaEnvelopBySeq(i, eventId);
	}

	@Override
	public boolean isAcceptedEvaluationDeclaration(String envelopeId, String loggedInUser, String eventId) {
		return rfaEvaluatorDeclarationDao.isAcceptedEvaluationDeclaration(envelopeId, loggedInUser, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvaluatorDeclaration saveEvaluatorDeclaration(RfaEvaluatorDeclaration evaluationDeclarationObj) {
		return rfaEvaluatorDeclarationDao.save(evaluationDeclarationObj);
	}

	@Override
	public RfaEnvelop getEmptyEnvelopByEventId(String eventId) {
		return rfaEnvelopDao.getEmptyEnvelopByEventId(eventId);
	}

	@Override
	public List<RfaCq> getCqsByEnvelopIdByOrder(String envelopId) {
		return rfaEnvelopDao.getCqsByEnvelopIdByOrder(envelopId);
	}

	@Override
	public RfaEvaluatorUser getRfaEvaluationDocument(String eventId, String evelopId, User loggedInUser) {
		return rfaEvaluatorUserDao.getEvaluationDocument(evelopId);
	}

	@Override
	public List<RfaEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		return rfaEnvelopDao.getSorsByEnvelopIdByOrder(envelopId);
	}

	@Override
	public void sendEnvelopOpenNotification(Envelop envelop, RfxTypes eventType, String eventId, User loggedInUser, Boolean isAllOpen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEnvelopCloseNotification(Envelop envelop, RfxTypes eventType, String eventId, User loggedInUser, Boolean isAllClosed) {
		// TODO Auto-generated method stub
		
	}

	// protected void buildCqComparisionFile(HSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse
	// response) throws IOException {
	// if (CollectionUtil.isNotEmpty(list)) {
	// for (EventEvaluationPojo evaluationPojo : list) {
	// HSSFSheet sheet = workbook.createSheet();
	// Row rowHeading = sheet.createRow(0);
	// CellStyle styleHeading = workbook.createCellStyle();
	// Font font = workbook.createFont();
	// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	// styleHeading.setFont(font);
	// // styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
	// styleHeading.setAlignment(CellStyle.ALIGN_CENTER);
	// int r = 1;
	//
	// int i = 3;
	// int cellMerge = 2;
	// for (Supplier column : evaluationPojo.getColumns()) {
	// cellMerge++;
	// Cell cell = rowHeading.createCell(i++);
	// cell.setCellValue(column.getCompanyName());
	// cell.setCellStyle(styleHeading);
	// cell = rowHeading.createCell(i++);
	// cell.setCellStyle(styleHeading);
	// sheet.addMergedRegion(new CellRangeAddress(0, 0, cellMerge, ++cellMerge));
	// }
	//
	// i = 0;
	// Row row = sheet.createRow(r++);
	// Cell cell = row.createCell(i++);
	// cell.setCellValue("No");
	// // cell.setCellStyle(styleHeading);
	//
	// cell = row.createCell(i++);
	// cell.setCellValue("Question");
	// // cell.setCellStyle(styleHeading);
	//
	// cell = row.createCell(i++);
	// cell.setCellValue("Scoring Scale");
	// // cell.setCellStyle(styleHeading);
	//
	// List<Supplier> columns = evaluationPojo.getColumns();
	// for (int j = 0; j < columns.size(); j++) {
	// cell = row.createCell(i++);
	// cell.setCellValue("Answer");
	// cell.getCellStyle().setWrapText(true);
	// // cell.setCellStyle(styleHeading);
	// cell = row.createCell(i++);
	// cell.setCellValue("Score");
	// // cell.setCellStyle(styleHeading);
	// }
	//
	// for (List<String> data : evaluationPojo.getData()) {
	// row = sheet.createRow(r++);
	// int cellNum = 0;
	// for (String answers : data) {
	// Cell anserwCell = row.createCell(cellNum++);
	// anserwCell.setCellValue(answers);
	// anserwCell.getCellStyle().setWrapText(true);
	// }
	// }
	//
	// r++;
	// int cellNum = 1;
	// Row totalRow = sheet.createRow(r++);
	// cell = totalRow.createCell(cellNum++);
	// cell.setCellValue("Total Scoring");
	// cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
	// for (String score : evaluationPojo.getScoring()) {
	// if (cellNum == 2) {
	// cell = totalRow.createCell(cellNum++);
	// cell.setCellValue(score);
	// cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
	// } else {
	// cell = totalRow.createCell(cellNum++);
	// cell = totalRow.createCell(cellNum++);
	// cell.setCellValue(score);
	// cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	// }
	//
	// }
	//
	// for (int columnPosition = 0; columnPosition < 25; columnPosition++) {
	// sheet.autoSizeColumn((short) (columnPosition));
	// }
	// }
	//
	// String downloadFolder = System.getProperty("user.home");
	// String fileName = "CqComparisonTable.xls";
	// FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
	// workbook.write(out);
	// out.close();
	// Path file = Paths.get(downloadFolder, fileName);
	//
	// }
	// }

	// protected HSSFWorkbook buildBqComparisionFile(HSSFWorkbook workbook, List<EventEvaluationPojo> list, String
	// eventId, String evelopId, RfxTypes eventType) throws IOException {
	// LOG.info("Event Id " + eventId);
	// Event event = null;
	// Envelop envolope = null;
	// switch (eventType) {
	// case RFA:
	// event = rfaEventService.getRfaEventByeventId(eventId);
	// envolope = rfaEnvelopService.getRfaEnvelopById(evelopId);
	// break;
	// case RFT:
	// event = rftEventService.getRftEventById(eventId);
	// envolope = rftEnvelopService.getRftEnvelopById(evelopId);
	// break;
	// case RFP:
	// event = rfpEventService.getRfpEventByeventId(eventId);
	// envolope = rfpEnvelopService.getRfpEnvelopById(evelopId);
	// break;
	// case RFQ:
	// event = rfqEventService.getEventById(eventId);
	// envolope = rfqEnvelopService.getRfqEnvelopById(evelopId);
	// break;
	// case RFI:
	// event = rfiEventService.getRfiEventByeventId(eventId);
	// envolope = rfiEnvelopService.getRfiEnvelopById(evelopId);
	// break;
	// default:
	// break;
	// }
	// if (CollectionUtil.isNotEmpty(list)) {
	// int k = 1;
	// for (EventEvaluationPojo evaluationPojo : list) {
	// LOG.info("decimal :" + evaluationPojo.getDecimal() + " With Tax : " + evaluationPojo.getWithTax());
	// // For Financial Standard
	// DecimalFormat df = null;
	// if (evaluationPojo.getDecimal().equals("1")) {
	// df = new DecimalFormat("#,###,###,##0.0");
	// } else if (evaluationPojo.getDecimal().equals("2")) {
	// df = new DecimalFormat("#,###,###,##0.00");
	// } else if (evaluationPojo.getDecimal().equals("3")) {
	// df = new DecimalFormat("#,###,###,##0.000");
	// } else if (evaluationPojo.getDecimal().equals("4")) {
	// df = new DecimalFormat("#,###,###,##0.0000");
	// } else if (evaluationPojo.getDecimal().equals("5")) {
	// df = new DecimalFormat("#,###,###,##0.00000");
	// } else if (evaluationPojo.getDecimal().equals("6")) {
	// df = new DecimalFormat("#,###,###,##0.000000");
	// }
	//
	// HSSFSheet sheet = workbook.createSheet("BQ price comparison" + k);
	// k++;
	// Row rowHeading = sheet.createRow(0);
	// CellStyle styleHeading = workbook.createCellStyle();
	// Font font = workbook.createFont();
	// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	// styleHeading.setFont(font);
	// // styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
	// styleHeading.setAlignment(CellStyle.ALIGN_CENTER);
	// int r = 1;
	// short[] arr = { HSSFColor.LIGHT_ORANGE.index, HSSFColor.LIGHT_GREEN.index, HSSFColor.PINK.index,
	// HSSFColor.LIGHT_TURQUOISE.index, HSSFColor.LIGHT_YELLOW.index, HSSFColor.LIGHT_CORNFLOWER_BLUE.index };
	// int colorIndex = 0;
	// int i = 5;
	//
	// colorIndex = 0;
	// int x = 1;
	// for (Supplier column : evaluationPojo.getColumns()) {
	// int cellFirstMerge = 0;
	// int lastCellForMerge = 0;
	// cellFirstMerge = i;
	// i = i + 2;
	// if (Boolean.TRUE == evaluationPojo.getWithTax()) {
	// i = i + 2;
	// }
	// lastCellForMerge = i - 1;
	// sheet.addMergedRegion(new CellRangeAddress(0, 0, cellFirstMerge, lastCellForMerge));
	// Cell cell = rowHeading.createCell(cellFirstMerge);
	// CellStyle styleHeadingb = workbook.createCellStyle();
	// styleHeadingb.setFont(font);
	// styleHeadingb.setAlignment(CellStyle.ALIGN_CENTER);
	// styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
	// styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
	// if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
	// cell.setCellValue(MaskUtils.maskName(envolope.getPreFix(), column.getId(), envolope.getId()));
	// } else {
	// cell.setCellValue(column.getCompanyName());
	// }
	//
	// cell.setCellStyle(styleHeadingb);
	// colorIndex++;
	// cellFirstMerge = i;
	// x++;
	// }
	//
	// i = 0;
	// Row row = sheet.createRow(r++);
	// Cell cell = row.createCell(i++);
	// cell.setCellValue("No");
	// cell.setCellStyle(styleHeading);
	//
	// cell = row.createCell(i++);
	// cell.setCellValue("Item");
	// cell.setCellStyle(styleHeading);
	//
	// cell = row.createCell(i++);
	// cell.setCellValue("Description");
	// cell.setCellStyle(styleHeading);
	//
	// cell = row.createCell(i++);
	// cell.setCellValue("UOM");
	// cell.setCellStyle(styleHeading);
	//
	// cell = row.createCell(i++);
	// cell.setCellValue("Quantity");
	// cell.setCellStyle(styleHeading);
	//
	// List<Supplier> columns = evaluationPojo.getColumns();
	// colorIndex = 0;
	// for (int j = 0; j < columns.size(); j++) {
	// CellStyle styleHeadingb = workbook.createCellStyle();
	// styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
	// styleHeadingb.setFont(font);
	// styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
	// cell = row.createCell(i++);
	// cell.setCellValue("Unit Price");
	// cell.setCellStyle(styleHeadingb);
	// cell = row.createCell(i++);
	// cell.setCellValue("Amount");
	// cell.setCellStyle(styleHeadingb);
	//
	// if (Boolean.TRUE == evaluationPojo.getWithTax()) {
	// cell = row.createCell(i++);
	// cell.setCellValue("Tax");
	// cell.setCellStyle(styleHeadingb);
	// cell = row.createCell(i++);
	// cell.setCellValue("Amount Inc Tax");
	// cell.setCellStyle(styleHeadingb);
	// }
	//
	// // cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
	// colorIndex++;
	// }
	//
	// for (List<String> data : evaluationPojo.getData()) {
	// row = sheet.createRow(r++);
	// int cellNum = 0;
	// for (String answers : data) {
	// if (cellNum <= 3) {
	// row.createCell(cellNum++).setCellValue(answers);
	// } else {
	// if (answers != null && StringUtils.checkString(answers).length() > 0) {
	// Cell cell1 = row.createCell(cellNum++);
	// cell1.setCellValue(StringUtils.checkString(answers).length() > 0 ? df.format(new BigDecimal(answers)) : "");
	//
	// cell1.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	//
	// } else {
	// row.createCell(cellNum++).setCellValue(answers);
	// }
	// }
	// }
	// }
	//
	// r++;
	// int cellNum = 4;
	// Row totalRow = sheet.createRow(r++);
	// Cell totalCell = totalRow.createCell(cellNum++);
	// totalCell.setCellValue("Sub Total");
	// for (BigDecimal score : evaluationPojo.getTotalAmount()) {
	// totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
	// totalCell = totalRow.createCell(cellNum++);
	// totalCell.setCellValue(score != null ? df.format(score) : "");
	// totalCell.setCellStyle(styleHeading);
	// totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	// if (score == null)
	// totalCell.setCellType(Cell.CELL_TYPE_BLANK);
	// totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	// }
	//
	// if (Boolean.TRUE == evaluationPojo.getWithTax()) {
	// cellNum = 4;
	// totalRow = sheet.createRow(r++);
	// totalCell = totalRow.createCell(cellNum++);
	// totalCell.setCellValue("Additional Tax");
	//
	// for (String taxInfo : evaluationPojo.getAddtionalTaxInfo()) {
	// LOG.info("taxInfo : " + taxInfo);
	// totalCell = totalRow.createCell(cellNum++);
	// if (taxInfo == null || StringUtils.checkString(taxInfo).length() == 0) {
	// totalCell.setCellType(Cell.CELL_TYPE_BLANK);
	// } else {
	// totalCell.setCellValue(StringUtils.checkString(taxInfo));
	// totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	// }
	// }
	// }
	//
	// cellNum = 4;
	// totalRow = sheet.createRow(r++);
	// totalCell = totalRow.createCell(cellNum++);
	// totalCell.setCellValue("Grand Total (" + (event.getBaseCurrency() != null ?
	// event.getBaseCurrency().getCurrencyCode() : "") + ")");
	// totalCell.setCellStyle(styleHeading);
	// totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
	// for (BigDecimal score : evaluationPojo.getGrandTotals()) {
	// totalCell = totalRow.createCell(cellNum++);
	// totalCell.setCellValue(score != null ? df.format(score) : "");
	// totalCell.setCellStyle(styleHeading);
	// totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	// if (score == null)
	// totalCell.setCellType(Cell.CELL_TYPE_BLANK);
	// totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	// }
	//
	// for (int columnPosition = 0; columnPosition < 50; columnPosition++) {
	// sheet.autoSizeColumn((short) (columnPosition));
	// }
	//
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading()) ||
	// CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
	//
	// createSheet(workbook, font, styleHeading, evaluationPojo, colorIndex, arr);
	// }
	//
	// }
	// String downloadFolder = System.getProperty("user.home");
	// String fileName = "BqComparisonTable.xls";
	// FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
	// workbook.write(out);
	// out.close();
	// Path file = Paths.get(downloadFolder, fileName);
	//
	// }
	// return workbook;
	//
	// }

	// private void createSheet(HSSFWorkbook workbook, Font font, CellStyle styleHeading, EventEvaluationPojo
	// evaluationPojo, int colorIndex, short[] arr) {
	//
	// HSSFSheet sheet2 = workbook.createSheet("BQ non-price comparison");
	//
	// CellStyle styleHeading1 = workbook.createCellStyle();
	// styleHeading1.setFont(font);
	// int r2 = 1;
	// int i2 = 0;
	// Row row2 = sheet2.createRow(r2++);
	// Cell cell2 = row2.createCell(i2++);
	// cell2.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	// cell2.setCellValue("No");
	// cell2.setCellStyle(styleHeading);
	//
	// cell2 = row2.createCell(i2++);
	// cell2.setCellValue("Item");
	// cell2.setCellStyle(styleHeading1);
	//
	// cell2 = row2.createCell(i2++);
	// cell2.setCellValue("Description");
	// cell2.setCellStyle(styleHeading1);
	//
	// Cell cell21 = row2.createCell(i2++);
	// cell21.setCellValue("UOM");
	// cell21.setCellStyle(styleHeading1);
	//
	// cell2 = row2.createCell(i2++);
	// cell2.setCellValue("Quantity");
	// cell2.setCellStyle(styleHeading1);
	//
	// LOG.info("--------------------------------------------------------");
	//
	// if (evaluationPojo.getBqNonPriceComprision() != null) {
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
	// for (String buyerHeading : evaluationPojo.getBqNonPriceComprision().getBuyerHeading()) {
	// CellStyle styleHeadingb = workbook.createCellStyle();
	// Font font1 = workbook.createFont();
	// styleHeadingb.setFont(font1);
	// font1.setColor(HSSFColor.WHITE.index);
	// font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	// styleHeadingb.setFillForegroundColor(HSSFColor.OLIVE_GREEN.index);
	// styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
	// cell2 = row2.createCell(i2++);
	// cell2.setCellValue(buyerHeading);
	// cell2.setCellStyle(styleHeadingb);
	//
	// }
	// }
	// colorIndex = 0;
	// LOG.info("i2 after printing buyer header count" + i2);
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierName())) {
	// for (int supp = 0; supp < evaluationPojo.getBqNonPriceComprision().getSupplierName().size(); supp++) {
	// CellStyle styleHeadingb = workbook.createCellStyle();
	// styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
	// styleHeadingb.setFont(font);
	// styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
	// for (String supplierHeading : evaluationPojo.getBqNonPriceComprision().getSupplierHeading()) {
	// cell2 = row2.createCell(i2++);
	// cell2.setCellValue(supplierHeading);
	// cell2.setCellStyle(styleHeadingb);
	// }
	// }
	// colorIndex++;
	// }
	// }
	// }
	//
	// LOG.info("i2 after printing supplier header count" + i2);
	// int storeI2ForSupplier = i2;
	//
	// LOG.info("storeI2ForSupplier : " + storeI2ForSupplier);
	// int storeCellNum1 = 0;
	// int storeCellNum1ForSupplier = 0;
	// int storeIndex = 0;
	//
	// for (List<String> data : evaluationPojo.getData()) {
	// row2 = sheet2.createRow(r2++);
	// int cellNum1 = 0;
	// for (String answers : data) {
	// if (cellNum1 <= 4) {
	// row2.createCell(cellNum1++).setCellValue(answers);
	// storeCellNum1 = cellNum1;
	//
	// }
	// }
	//
	// }
	// int rowIndex = 2;
	// if (evaluationPojo.getBqNonPriceComprision() != null) {
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
	// for (List<String> buyerDataList : evaluationPojo.getBqNonPriceComprision().getBuyerFeildData()) {
	// row2 = sheet2.getRow(rowIndex++);
	// int buyerCell = storeCellNum1;
	// for (String data : buyerDataList) {
	// row2.createCell(buyerCell++).setCellValue(data);
	// storeCellNum1ForSupplier = buyerCell;
	// }
	// }
	// } else {
	// storeCellNum1ForSupplier = storeCellNum1;
	// }
	// }
	//
	// LOG.info("==storeIndex==" + storeIndex);
	//
	// rowIndex = 2;
	// if (evaluationPojo.getBqNonPriceComprision().getSupplierData() != null &&
	// evaluationPojo.getBqNonPriceComprision().getSupplierData().size() > 0) {
	// for (Entry<String, List<String>> entry : evaluationPojo.getBqNonPriceComprision().getSupplierData().entrySet()) {
	// row2 = sheet2.getRow(rowIndex++);
	// int cellNumber = storeCellNum1ForSupplier;
	// for (String supplierData : entry.getValue()) {
	// row2.createCell(cellNumber++).setCellValue(supplierData);
	//
	// }
	// }
	// }
	// row2 = sheet2.getRow(0);
	// if (row2 == null) {
	// row2 = sheet2.createRow(0);
	// }
	//
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
	// for (int colcount = 5; colcount < (5 + evaluationPojo.getBqNonPriceComprision().getBuyerHeading().size());
	// colcount++) {
	// CellStyle styleHeadingb = workbook.createCellStyle();
	// styleHeadingb.setFont(font);
	// styleHeadingb.setAlignment(CellStyle.ALIGN_CENTER);
	// styleHeadingb.setFillForegroundColor(HSSFColor.OLIVE_GREEN.index);
	// styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
	// cell2 = row2.createCell(colcount);
	// cell2.setCellValue("");
	// cell2.setCellStyle(styleHeadingb);
	// }
	//
	// }
	//
	// int fixedheadingCount = 4;
	// int buyerheadingCount = 0;
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
	// buyerheadingCount = evaluationPojo.getBqNonPriceComprision().getBuyerHeading().size();
	// }
	// int supplierheadingCount = 0;
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
	// supplierheadingCount = evaluationPojo.getBqNonPriceComprision().getSupplierHeading().size();
	// }
	//
	// colorIndex = 0;
	// int cellNub = fixedheadingCount + buyerheadingCount + 1;
	//
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
	// if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierName())) {
	// for (String supName : evaluationPojo.getBqNonPriceComprision().getSupplierName()) {
	// LOG.info("cellNub" + cellNub);
	// CellStyle styleHeadingb = workbook.createCellStyle();
	// styleHeadingb.setFont(font);
	// styleHeadingb.setAlignment(CellStyle.ALIGN_CENTER);
	// styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
	// styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
	// sheet2.addMergedRegion(new CellRangeAddress(0, 0, cellNub, cellNub + supplierheadingCount - 1));
	// cell2 = row2.createCell(cellNub);
	// cell2.setCellValue(supName);
	// cell2.setCellStyle(styleHeadingb);
	// // row2.createCell(cellNub).setCellValue(supName);
	// cellNub = cellNub + supplierheadingCount;
	// colorIndex++;
	// }
	// }
	// }
	//
	// for (int columnPosition = 0; columnPosition < 50; columnPosition++) {
	// sheet2.autoSizeColumn((short) (columnPosition));
	// }
	// }
}
