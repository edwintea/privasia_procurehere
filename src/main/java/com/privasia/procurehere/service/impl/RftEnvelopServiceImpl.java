package com.privasia.procurehere.service.impl;

import java.io.File;
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

import com.privasia.procurehere.core.dao.RftSorDao;
import com.privasia.procurehere.core.dao.RftSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RftEventSor;
import com.privasia.procurehere.core.entity.RftSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RftSupplierSorItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
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
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftCqDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RftEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RftEventAuditDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RftEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqOption;
import com.privasia.procurehere.core.entity.RftUnMaskedUser;
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
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RftSupplierBqItemService;
import com.privasia.procurehere.service.RftSupplierCqItemService;
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
public class RftEnvelopServiceImpl extends EnvelopServiceImplBase implements RftEnvelopService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftBqDao rftEventBqDao;

	@Autowired
	RftCqDao rftCqDao;

	@Autowired
	UserService userService;

	@Autowired
	RftEvaluatorUserDao evaluatorUserDao;

	@Autowired
	RftSupplierCqItemDao rftSupplierCqItemDao;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	RftSupplierBqDao rftSupplierBqDao;

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RftSupplierCqItemService supplierCqItemService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	UserDao userDao;

	@Autowired
	RftSupplierBqItemService rftSupplierBqItemService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RftEvaluatorUserDao rftEvaluatorUserDao;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RftEventAuditDao rftEventAuditDao;
	@Autowired
	RftSupplierCqItemService rftSupplierCqItemService;

	@Autowired
	RftEvaluatorDeclarationDao rftEvaluatorDeclarationDao;

	@Autowired
	RftSupplierCqOptionDao rftSupplierCqOptionDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RftSorDao rftSorDao;

	@Autowired
	RftSupplierSorItemDao rftSupplierSorItemDao;

	@Autowired
	RftSupplierSorItemService rftSupplierSorItemService;

	@Override
	@Transactional(readOnly = false)
	public RftEnvelop saveRftEnvelop(RftEnvelop rftEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {

		// Fetch Event Obj Instance
		// RftEvent event = rftEventDao.findById(rftEnvelop.getRfxEvent().getId());
		// rftEnvelop.setRfxEvent(event);
		if (cqIds != null) {
			List<RftCq> cqList = rftCqDao.findCqsByIds(cqIds);
			rftEnvelop.setCqList(cqList);
		}
		if (bqIds != null) {
			List<RftEventBq> bqList = rftEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rftEnvelop.setBqList(bqList);
		}
		if(sorIds != null) {
			List<RftEventSor> sorList = rftSorDao.findAllSorsByIds(sorIds);
			LOG.info("Sor list :  " + sorList);
			rftEnvelop.setSorList(sorList);
		}
		LOG.info("Save Envelop   : " + rftEnvelop);
		return rftEnvelopDao.saveOrUpdate(rftEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRftEnvelop(RftEnvelop rftEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {
		if (cqIds != null) {
			List<RftCq> cqList = rftCqDao.findCqsByIds(cqIds);
			rftEnvelop.setCqList(cqList);
		} else {
			rftEnvelop.setCqList(null);
		}
		if (bqIds != null) {
			List<RftEventBq> bqList = rftEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rftEnvelop.setBqList(bqList);
		} else {
			rftEnvelop.setBqList(null);
		}

		if(sorIds != null) {
			List<RftEventSor> sorList = rftSorDao.findAllSorsByIds(sorIds);
			LOG.info("Sor list :  " + sorList);
			rftEnvelop.setSorList(sorList);
		} else {
			rftEnvelop.setSorList(null);
		}
		rftEnvelopDao.update(rftEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEnvelop updateRftEnvelopeTeam(RftEnvelop rftEnvelop, String eventId, boolean isUserControl) throws NoSuchMessageException, ApplicationException {
		RftEnvelop dbEnvelop = getRftEnvelopById(rftEnvelop.getId());

		String ownerID = rftEventService.getEventOwnerId(eventId);
		User dbleadEvaluator = null;
		User dbOpener = null;
		String description = "Envelope \"" + dbEnvelop.getEnvelopTitle() + "\" is updated. ";
		String evaluatorEditRemoved = "";
		for (RftEvaluatorUser users : dbEnvelop.getEvaluators()) {
			if (rftEnvelop.getEvaluators() != null) {
				for (RftEvaluatorUser evalusers : rftEnvelop.getEvaluators()) {

					if (isUserControl && evalusers.getUser() != null && evalusers.getUser().getId().equals(ownerID)) {
						throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Envelope Evaluation" }, Global.LOCALE));
					}

					if (users.getId().equals(evalusers.getId())) {
						evalusers.setEvaluatorSummary(users.getEvaluatorSummary());
						evalusers.setSummaryDate(users.getSummaryDate());
					}
				}
			}
		}

		if (rftEnvelop.getEvaluators() != null) {
			for (RftEvaluatorUser users : rftEnvelop.getEvaluators()) {
				if (users.getUser().getId().equals(dbEnvelop.getLeadEvaluater().getId())) {
					throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
				}
			}
		}

		for (RftEnvelopeOpenerUser dbUsers : dbEnvelop.getOpenerUsers()) {
			if (rftEnvelop.getOpenerUsers() != null) {
				for (RftEnvelopeOpenerUser envUsers : rftEnvelop.getOpenerUsers()) {

					if (isUserControl && envUsers.getUser() != null && envUsers.getUser().getId().equals(ownerID)) {
						throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Envelope Opener" }, Global.LOCALE));
					}

					if (dbUsers.getUser().getId().equals(envUsers.getId())) {
						envUsers.setIsOpen(dbUsers.getIsOpen());
						envUsers.setOpenDate(dbUsers.getOpenDate());
						envUsers.setCloseDate(dbUsers.getCloseDate());
					}
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

		// Sending notification to lead evaluator

		if (isUserControl && rftEnvelop.getLeadEvaluater() != null && rftEnvelop.getLeadEvaluater().getId().equals(ownerID)) {
			throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Envelope Evaluation Owner" }, Global.LOCALE));
		}

		if (dbEnvelop.getLeadEvaluater() != null && rftEnvelop.getLeadEvaluater() != null) {
			if (!dbEnvelop.getLeadEvaluater().getId().equals(rftEnvelop.getLeadEvaluater().getId())) {
				dbleadEvaluator = new User(dbEnvelop.getLeadEvaluater().getId(), dbEnvelop.getLeadEvaluater().getName(), dbEnvelop.getLeadEvaluater().getCommunicationEmail(), dbEnvelop.getLeadEvaluater().getEmailNotifications(), dbEnvelop.getLeadEvaluater().getTenantId());
				sendLeadMail = true;
				description += " Envelope Evaluation Owner changed from " + dbleadEvaluator.getName() + " to " + rftEnvelop.getLeadEvaluater().getName() + "<br/>";
			}
		}

		if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isNotEmpty(rftEnvelop.getEvaluators())) {
			for (RftEvaluatorUser user : rftEnvelop.getEvaluators()) {
				boolean newUsers = true;
				for (RftEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					if (user.getUser().getId().equals(dbUser.getUser().getId())) {
						newUsers = false;
						break;
					}
				}
				if (newUsers) {
					addEvalUsers.add(user.getUser());
				}
			}

			for (RftEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
				boolean oldUsers = true;
				for (RftEvaluatorUser user : rftEnvelop.getEvaluators()) {
					if (user.getUser().getId().equals(dbUser.getUser().getId())) {
						oldUsers = false;
						break;
					}
				}
				if (oldUsers) {
					removeEvalUsers.add(dbUser.getUser());
				}
			}

		} else if (CollectionUtil.isEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isNotEmpty(rftEnvelop.getEvaluators())) {
			for (RftEvaluatorUser user : rftEnvelop.getEvaluators()) {
				addEvalUsers.add(user.getUser());
			}
		} else if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isEmpty(rftEnvelop.getEvaluators())) {
			for (RftEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
				removeEvalUsers.add(dbUser.getUser());
			}
		}

		// Openers
		if (EnvelopType.OPEN == dbEnvelop.getEnvelopType() && CollectionUtil.isNotEmpty(rftEnvelop.getOpenerUsers())) {
			for (RftEnvelopeOpenerUser user : rftEnvelop.getOpenerUsers()) {
				addOpeners.add(user.getUser());
			}
		} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && EnvelopType.OPEN == rftEnvelop.getEnvelopType()) {
			for (RftEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
				removeOpeners.add(dbUser.getUser());
			}
		} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && CollectionUtil.isNotEmpty(rftEnvelop.getOpenerUsers())) {
			for (RftEnvelopeOpenerUser user : rftEnvelop.getOpenerUsers()) {
				boolean newUsers = true;
				for (RftEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					if (user.getUser().getId().equals(dbUser.getUser().getId())) {
						newUsers = false;
						break;
					}
				}
				if (newUsers) {
					addOpeners.add(user.getUser());
				}
			}

			for (RftEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
				boolean oldUsers = true;
				for (RftEnvelopeOpenerUser user : rftEnvelop.getOpenerUsers()) {
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

		if (CollectionUtil.isNotEmpty(rftEnvelop.getEvaluators())) {
			for (RftEvaluatorUser user : rftEnvelop.getEvaluators()) {
				user.setEnvelop(dbEnvelop);
			}
		}
		if (CollectionUtil.isNotEmpty(rftEnvelop.getOpenerUsers())) {
			for (RftEnvelopeOpenerUser user : rftEnvelop.getOpenerUsers()) {
				user.setEnvelope(dbEnvelop);
				user.setEvent(dbEnvelop.getRfxEvent());
			}
		}

		if (dbEnvelop.getOpener() != null) {
			dbEnvelop.setOpener(null);
		}

		dbEnvelop.setOpenerUsers(rftEnvelop.getOpenerUsers());
		dbEnvelop.setEvaluators(rftEnvelop.getEvaluators());
		dbEnvelop.setLeadEvaluater(rftEnvelop.getLeadEvaluater());
		dbEnvelop.setEnvelopType(rftEnvelop.getEnvelopType());

		// if (rftEnvelop.getOpener() != null) {
		// dbEnvelop.setOpener(rftEnvelop.getOpener());
		// }

		if (EnvelopType.OPEN == rftEnvelop.getEnvelopType()) {
			dbEnvelop.setOpener(null);
			dbEnvelop.setIsOpen(Boolean.TRUE);
			dbEnvelop.setOpenerUsers(new ArrayList<RftEnvelopeOpenerUser>());
		}

		if (dbEnvelop.getEnvelopType() == EnvelopType.CLOSED && dbEnvelop.getOpenDate() == null) {
			dbEnvelop.setIsOpen(Boolean.FALSE);
		}

		String timeZone = "GMT+8:00";
		RftEvent event = dbEnvelop.getRfxEvent();
		if (sendLeadMail) {
			try {
				String url = APP_URL + "/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
				timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getLeadEvaluater(), timeZone);
				sendLeadEvaluatorInvitedNotification(dbEnvelop.getLeadEvaluater(), event, url, timeZone, RfxTypes.RFT.getValue());
				sendLeadEvaluatorRemovedNotification(dbleadEvaluator, event, url, timeZone, RfxTypes.RFT.getValue());
			} catch (Exception e) {
				LOG.error("Error While sending mail to lead evaluator :" + e.getMessage(), e);
			}
		}
		
		if (CollectionUtil.isNotEmpty(addEvalUsers)) {
			try {
				for (User user : addEvalUsers) {
					LOG.info("addEvalUsers user.getId() :" + user.getId() + "Comunication Email : " + user.getCommunicationEmail());
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					String url = APP_URL + "/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorInvitedNotification(user, event, url, timeZone, RfxTypes.RFT.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorRemovedNotification(user, event, url, timeZone, RfxTypes.RFT.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerInvitedNotification(user, event, url, timeZone, RfxTypes.RFT.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been added as Envelope Opener.";
			} catch (Exception e) {
				LOG.error("Error While sending mail to envelope opener :" + e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(removeOpeners)) {
			try {
				for (User user : removeOpeners) {
					LOG.info("removeOpeners user.getId() :" + user.getId());
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					String url = APP_URL + "/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerRemovedNotification(user, event, url, timeZone, RfxTypes.RFT.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been removed as Envelope Opener.<br/>";
			} catch (Exception e) {
				LOG.error("Error While sending mail to removed envelope opener :" + e.getMessage(), e);
			}
		}

		Boolean isAllOpen = true;
		Boolean isAllClose = true;

		List<RftEnvelopeOpenerUser> openersUserList = dbEnvelop.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(openersUserList)) {
			for (RftEnvelopeOpenerUser opener : openersUserList) {
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

		RftEnvelop envelope = rftEnvelopDao.update(dbEnvelop);
		RftEventAudit audit = new RftEventAudit();
		audit.setAction(AuditActionType.Update);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setActionDate(new Date());
		description += evaluatorEditRemoved;
		audit.setDescription(description);
		audit.setEvent(event);
		eventAuditService.save(audit);

		try {
			RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "" + description  +"'for Event '"+rftEvent.getEventId()+"' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return envelope;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEnvelopeStatus(String envelopId, User logedUser) throws ApplicationException {
		RftEnvelop dbEnvelop = rftEnvelopDao.findById(envelopId);
		RftEvent event = dbEnvelop.getRfxEvent();
		User leadEvaluator = dbEnvelop.getLeadEvaluater();
		String timeZone = "GMT+8:00";
		String url = APP_URL + "/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
		timeZone = getTimeZoneByBuyerSettings(logedUser, timeZone);
		User eventOwner = rftEventService.getPlainEventOwnerByEventId(event.getId());
		String msg = "";
		if (dbEnvelop != null && leadEvaluator.getId().equals(logedUser.getId())) {

			if (StringUtils.checkString(dbEnvelop.getLeadEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}
			Boolean allDone = Boolean.TRUE;
			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators())) {
				for (RftEvaluatorUser evaluator : dbEnvelop.getEvaluators()) {
					if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
						allDone = Boolean.FALSE;
						break;
					}
				}
			}
			if (Boolean.TRUE == allDone) {
				dbEnvelop.setEvaluationDate(new Date());
				dbEnvelop.setEvaluationStatus(EvaluationStatus.COMPLETE);
				dbEnvelop = rftEnvelopDao.update(dbEnvelop);
			}
			try {
				// sending notification completed evaluation of envelope to lead
				// Evaluator
				msg = "You have complete evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFT.getValue(), msg);
				// sending notification completed evaluation of envelope to
				// Event Owner
				msg = "\"" + leadEvaluator.getName() + "\" has completed evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFT.getValue(), msg);

				try {
					RftEventAudit audit = new RftEventAudit();
					audit.setAction(AuditActionType.Evaluate);
					audit.setActionBy(leadEvaluator);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + "' is evaluated");
					rftEventAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RftEvent rftEvent = rftEventService.getPlainEventById(event.getId());
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.EVALUATE, "Envelope '" + dbEnvelop.getEnvelopTitle() + "' is Evaluated for Event '"+rftEvent.getEventId()+ "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

			} catch (Exception e) {
				LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
			}
			int count = rftEnvelopDao.findCountPendingEnvelopse(event.getId());
			if (count == 0) {
				event.setStatus(EventStatus.COMPLETE);

				Supplier winningSupplier = rftEventDao.findWinnerSupplier(event.getId());
				if (winningSupplier != null) {
					event.setWinningSupplier(winningSupplier);
					event.setWinningPrice(winningSupplier.getTotalAfterTax());
				}

				event = rftEventDao.update(event);

				try {
					// sending notification completed evaluation of envelope to
					// Event Owner
					msg = "The evaluation for the event \"" + event.getReferanceNumber() + "\" has been completed";
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFT.getValue(), msg);
					// sending notification completed evaluation of envelope to
					// buyer team members
					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setAction(AuditActionType.Complete);
						audit.setActionBy(logedUser);
						audit.setActionDate(new Date());
						audit.setEvent(event);
						audit.setDescription("Evaluation is completed");
						rftEventAuditDao.save(audit);
						
						RftEvent rftEvent = rftEventService.getPlainEventById(event.getId());
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Evaluation is completed for Event '"+rftEvent.getEventId()+ "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					List<User> buyerMembers = rftEventDao.getUserBuyerTeamMemberByEventId(event.getId());
					if (CollectionUtil.isNotEmpty(buyerMembers)) {
						for (User buyerTeamUser : buyerMembers) {
							sendEnvelopCompletedNotification(buyerTeamUser, event, url, timeZone, RfxTypes.RFT.getValue(), msg);
						}
					}

					// Send notification to unMasking User on all envelops evaluation completed
					if (Boolean.FALSE == event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
						url = APP_URL + "/buyer/RFT/envelopList/" + event.getId();
						msg = "You are assigned as Unmasking Owner for Event: \"" + event.getReferanceNumber() + "\"";
						for (RftUnMaskedUser um : event.getUnMaskedUsers()) {
							sendEnvelopCompletedNotificationToUnMaskingUser(um.getUser(), event, url, timeZone, RfxTypes.RFT.getValue(), msg);
						}
					}

				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
				tatReportService.updateTatReportEvaluationCompleted(event.getId(), logedUser.getTenantId(), new Date(), event.getStatus());
			}

		} else {
			RftEvaluatorUser evaluatorUser = rftEnvelopDao.getRftEvaluatorUserByUserIdAndEnvelopeId(envelopId, logedUser.getId());

			if (StringUtils.checkString(evaluatorUser.getEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}
			if (evaluatorUser != null) {
				evaluatorUser.setEvaluationDate(new Date());
				evaluatorUser.setEvaluationStatus(EvaluationStatus.COMPLETE);
				updateEvaluatorUser(evaluatorUser);
				try {
					RftEventAudit audit = new RftEventAudit();
					audit.setAction(AuditActionType.Review);
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + " ' is reviewed");
					rftEventAuditDao.save(audit);
					
					RftEvent rftEvent = rftEventService.getPlainEventById(event.getId());
					BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.REVIEW, "Envelope '" +dbEnvelop.getEnvelopTitle() + "' is reviewed for Event '"+rftEvent.getEventId()+ "'", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(ownerAuditTrail);
					
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					// sending notification to review on envelope to loged user
					msg = "You have completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(logedUser, event, url, timeZone, RfxTypes.RFT.getValue(), msg);
					// sending notification to review on envelope to lead
					// Evaluator
					msg = "\"" + logedUser.getName() + "\" has completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFT.getValue(), msg);
					// sending notification to review on envelope to Event Owner
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFT.getValue(), msg);
				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}

			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRftEnvelop(RftEnvelop rftEnvelop) {
		rftEnvelopDao.delete(rftEnvelop);
	}

	@Override
	public List<RftEnvelop> getAllRftEnvelop() {
		return rftEnvelopDao.findAll(RftEnvelop.class);
	}

	@Override
	public boolean isExists(RftEnvelop rftEnvelop, String eventId) {
		return rftEnvelopDao.isExists(rftEnvelop, eventId);
	}

	@Override
	public RftEnvelop getRftEnvelopById(String id) {
		RftEnvelop rftEnvelop = rftEnvelopDao.findById(id);
		if (rftEnvelop == null) {
			return null;
		}
		if (rftEnvelop.getRfxEvent() != null) {
			rftEnvelop.getRfxEvent().getEventName();
		}
		if (CollectionUtil.isNotEmpty(rftEnvelop.getBqList())) {
			for (RftEventBq bq : rftEnvelop.getBqList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(rftEnvelop.getCqList())) {
			for (RftCq cq : rftEnvelop.getCqList()) {
				cq.getName();
			}
		}

		if (CollectionUtil.isNotEmpty(rftEnvelop.getEvaluators())) {
			for (RftEvaluatorUser evalUser : rftEnvelop.getEvaluators()) {
				evalUser.getUser().getName();
			}
		}

		if (rftEnvelop.getLeadEvaluater() != null) {
			rftEnvelop.getLeadEvaluater().getLoginId();
		}
		if (rftEnvelop.getOpener() != null) {
			rftEnvelop.getOpener().getName();
		}
		if (CollectionUtil.isNotEmpty(rftEnvelop.getOpenerUsers())) {
			for (RftEnvelopeOpenerUser openerUser : rftEnvelop.getOpenerUsers()) {
				openerUser.getUser().getName();
			}
		}

		if (CollectionUtil.isNotEmpty(rftEnvelop.getSorList())) {
			for (RftEventSor bq : rftEnvelop.getSorList()) {
				bq.getName();
			}
		}
		return rftEnvelop;
	}

	@Override
	public RftEnvelop getRftEnvelopForEvaluationById(String id, User logedUser) {
		RftEnvelop envelop = rftEnvelopDao.findById(id);
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
					if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
						for (RftEvaluatorUser evaluator : envelop.getEvaluators()) {
							if (envelop.getIsOpen() && evaluator.getUser() != null //
									&& evaluator.getUser().getId().equals(logedUser.getId()) //
									&& evaluator.getEvaluationStatus() == EvaluationStatus.PENDING //
									&& envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
								envelop.setShowFinish(true);
							}
							if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
								showFinishForLead = Boolean.FALSE;
							}
						}
					}
					if (envelop.getIsOpen() //
							&& envelop.getLeadEvaluater().getId().equals(logedUser.getId()) //
							&& showFinishForLead && EvaluationStatus.COMPLETE != envelop.getEvaluationStatus() //
							&& envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
						envelop.setShowFinish(true);
					}

				}
			}
		}

		return envelop;
	}

	@Override
	public List<RftEnvelop> getAllRftEnvelopByEventId(String eventId, User logedUser) {
		List<RftEnvelop> envList = rftEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFT);

		List<RftEnvelop> envList2 = rftEnvelopDao.getAllEnvelopSorByEventId(eventId, RfxTypes.RFT);

		setSorInBqData(envList, envList2);

		if (CollectionUtil.isNotEmpty(envList)) {
			for (RftEnvelop envelop : envList) {
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RftEventBq bq : envelop.getBqList()) {
						bq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RftCq cq : envelop.getCqList()) {
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
				if (envelop.getIsOpen() && envelop.getLeadEvaluater().getId().equals(logedUser.getId())) {
					envelop.setShowView(true);
				}
				// if (envelop.getIsOpen() && envelop.getOpener() != null &&
				// envelop.getOpener().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() ==
				// EventStatus.CLOSED) {
				// envelop.setShowOpen(true);
				// }
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RftEvaluatorUser evaluator : envelop.getEvaluators()) {
						evaluator.getUser().getName();
						LOG.info("evaluator : " + evaluator.getUser().getId() + "  --   " + logedUser.getId());
						if (envelop.getIsOpen() && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId())) {
							envelop.setShowView(true);
						}
					}
				}
				// }
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RftEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
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

	private void setSorInBqData(List<RftEnvelop> envList, List<RftEnvelop> envList2) {
		Map<String, RftEnvelop> envelopMap = envList2.stream().collect(Collectors.toMap(RftEnvelop::getId, Function.identity()));
		envList.forEach(event -> {
			RftEnvelop matchingEvent = envelopMap.get(event.getId());
			if(matchingEvent != null) {
				event.setSorList(matchingEvent.getSorList());
			}
		});
	}

	@Override
	public List<String> getRftBqByEnvelopId(List<String> envelopId) {
		return rftEnvelopDao.getBqsByEnvelopId(envelopId);
	}

	@Override
	public List<RftEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		return rftEnvelopDao.findEvaluatorsByEnvelopId(envelopId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RftEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException {
		LOG.info("RftEnvelopeService Impl  addEvaluator: envelopeId " + envelopeId + " userId: " + userId);

		RftEnvelop rftEnvelop = getRftEnvelopById(envelopeId);
		if (StringUtils.checkString(userId).equals(rftEnvelop.getLeadEvaluater().getId())) {
			throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
		}

		LOG.info(rftEnvelop.getId());
		List<RftEvaluatorUser> evaluators = rftEnvelop.getEvaluators();
		if (evaluators == null) {
			evaluators = new ArrayList<RftEvaluatorUser>();
		}
		RftEvaluatorUser rftEvaluatorUser = new RftEvaluatorUser();
		rftEvaluatorUser.setEnvelop(rftEnvelop);
		User user = userService.getUsersById(userId);
		try {
			rftEvaluatorUser.setUser((User) user.clone());
		} catch (Exception e) {
			LOG.error("Error :  " + e.getMessage(), e);
		}

		evaluators.add(rftEvaluatorUser);
		rftEnvelop.setEvaluators(evaluators);
		rftEnvelopDao.saveOrUpdate(rftEnvelop);
		return evaluators;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEvaluator(String eventId, String envelopeId, String userId) {
		LOG.info("RftEnvelopeService Impl  removeEvaluator: envelopeId " + envelopeId + " userId: " + userId);
		RftEnvelop rftEnvelop = getRftEnvelopById(envelopeId);
		LOG.info(rftEnvelop.getId());
		List<RftEvaluatorUser> rftEvaluatorUser = rftEnvelop.getEvaluators();
		if (rftEvaluatorUser == null) {
			rftEvaluatorUser = new ArrayList<RftEvaluatorUser>();
		}
		LOG.info("rftEvaluatorUser.size() :" + rftEvaluatorUser.size());
		RftEvaluatorUser dbEvaluatorUser = getRftEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
		LOG.info(dbEvaluatorUser.getUser().getName());
		rftEvaluatorUser.remove(dbEvaluatorUser);
		dbEvaluatorUser.setEnvelop(null);
		rftEnvelop.setEvaluators(rftEvaluatorUser);
		rftEnvelopDao.update(rftEnvelop);
		LOG.info(" rftEnvelop.getEvaluators() :" + rftEnvelop.getEvaluators().size());
		List<User> userList = new ArrayList<User>();
		try {
			for (RftEvaluatorUser rfteval : rftEnvelop.getEvaluators()) {
				userList.add((User) rfteval.getUser().clone());
			}
			LOG.info(userList.size());
		} catch (Exception e) {
			LOG.error("Error constructing list of users after remove operation : " + e.getMessage(), e);
		}
		return userList;

	}

	@Override
	public RftEvaluatorUser getRftEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId) {
		return rftEnvelopDao.getRftEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvaluatorUser updateEvaluatorUser(RftEvaluatorUser evaluatorUser) {
		return evaluatorUserDao.update(evaluatorUser);
	}

	@Override
	@Transactional(readOnly = false)
	public void openEnvelop(RftEnvelop envelop) {
		rftEnvelopDao.update(envelop);
	}

	@Override
	@Transactional(readOnly = true)
	public RftEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		return evaluatorUserDao.findEvaluatorUser(envelopId, userId);
	}

	@Override
	public boolean findClosedStatusForLeadEvaluator(String envelopId) {
		return evaluatorUserDao.findClosedStatusForLeadEvaluator(envelopId);
	}

	/**
	 * @param eventId
	 * @param zos
	 * @param envelopeId
	 * @throws IOException
	 * @throws JRException
	 */
	@Override
	@Transactional(readOnly = true)
	public String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException {
		int count = 1;
		RftEnvelop envlope = rftEnvelopDao.findById(envelopeId);
		RftEvent event = rftEventDao.getPlainEventById(eventId);
		boolean isCqAvailble = false;
		boolean isBqAvailble = false;
		boolean isSorAvailable = false;
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
		zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
		String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
		parentFolder += "-" + envlope.getEnvelopTitle().replaceAll(" ", "_").replaceAll("/", "-");
		parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");
		// Get All Supplier List

		// List<EventSupplier> supplierList = rftEventSupplierDao.getAllSuppliersByEventId(eventId);
		List<EventSupplierPojo> supplierList = rftEventSupplierDao.getSubmitedSuppliers(eventId);
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
					supplierFolder = parentFolder + Global.PATH_SEPARATOR + (MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_")) + Global.PATH_SEPARATOR;
				} else {
					supplierFolder = parentFolder + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + Global.PATH_SEPARATOR;
				}
				// BQ
				if (Boolean.TRUE == event.getBillOfQuantity()) {
					if (isBqAvailble) {
						JasperPrint jasperPrint = generateSupplierBqPdfForEnvelope(envelopeId, supplier.getId(), i, virtualizer);
						if (jasperPrint != null) {
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Bill of Quantity" + " - " + MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_"));
								zos.flush();
							} else {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Bill of Quantity" + " - " + supplier.getCompanyName().replaceAll(" ", "_"));
								zos.flush();
							}
						}
					}
					// Export BQ FOR EACH SUPPLIER
					try {
						bqExportExcelForEachSupplier(envelopeId, zos, event, supplier, supplierFolder);
					} catch (Exception e) {
						LOG.error("Error While Export bq for each supplier : " + e.getMessage(), e);
					}
				} else {
					LOG.info("No BQ setup for event : " + eventId);
				}

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
					if (isCqAvailble) {
						JasperPrint jasperPrint = generateSupplierCqPdfForEnvelope(envelopeId, supplier.getId(), i);
						if (jasperPrint != null) {
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Questionnaire" + " - " + MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_"));
								zos.flush();
							} else {
								FileUtil.writePdfToZip(zos, jasperPrint, supplierFolder, "Questionnaire" + " - " + supplier.getCompanyName().replaceAll(" ", "_"));
								zos.flush();
							}
						}

					}
					if (CollectionUtil.isNotEmpty(envlope.getCqList())) {
						for (RftCq cq : envlope.getCqList()) {
							String attachmentFolder = "";
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;
							} else {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;

							}
							// List<RftSupplierCqItem> supplierCqItems =
							// rftSupplierCqItemDao.getSupplierCqItemsbySupplierId(eventId, supplier.getId(),
							// cq.getId());
							List<RftSupplierCqItem> supplierCqItems = rftSupplierCqItemDao.getSupplierCqItemsbySupplier(supplier.getId(), cq.getId());
							if (CollectionUtil.isNotEmpty(supplierCqItems)) {
								for (RftSupplierCqItem supCqItem : supplierCqItems) {
									if (supCqItem.getFileData() != null) {
										// [eventRef+envelopeName]/[supplierCompanyName]/[attachments]/[cqs]

										// String extension =
										// FilenameUtils.getExtension(StringUtils.checkString(supCqItem.getFileName()));
										String fileName = supCqItem.getCqItem().getLevel() + "-" + supCqItem.getCqItem().getOrder() + "-" + supCqItem.getFileName();
										// [attachment name will be the CQ item number and file name
										// e.g. 1-2-FileName]
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

				i++;
			}

			if (isForAllReports) {

				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				LOG.info("short summary");
				JasperPrint jasperPrint = rftEventService.generateShortEvaluationSummaryReport("RFT", eventId, envelopeId, timeZone, virtualizer);
				if (jasperPrint != null) {
					LOG.info("short summary");
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Short Submission Summary for " + envlope.getEnvelopTitle());
					zos.flush();
				}

				jasperPrint = rftEventService.getEvaluationReport(eventId, envelopeId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Evaluation Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}

				jasperPrint = rftEventService.generateSubmissionReport(envelopeId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Submission Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}

				List<EventEvaluationPojo> list = null;

				list = rftSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, envelopeId);
				if (CollectionUtil.isNotEmpty(list)) {
					XSSFWorkbook workbookBq = new XSSFWorkbook();
					workbookBq = rfaEventService.buildBqComparisionFile(workbookBq, list, null, eventId, envelopeId, RfxTypes.RFT);
					if (workbookBq != null) {
						FileUtil.writeXssfExcelToZip(zos, workbookBq, parentFolder + "/", "BQ Comparison Table.xlsx");
						zos.flush();
					}

				}

				XSSFWorkbook workbookCq = new XSSFWorkbook();
				list = rftSupplierCqItemService.getEvaluationDataForCqComparison(eventId, envelopeId);
				if (CollectionUtil.isNotEmpty(list)) {
					workbookCq = rfaEventService.buildCqComparisionFile(workbookCq, list, null);
					if (workbookCq != null) {
						FileUtil.writeXssfExcelToZip(zos, workbookCq, parentFolder + "/", "CQ Comparison Table.xlsx");
						zos.flush();
					}

				}
				count++;

			}
		}

		return zipFileName;
	}

	private void sorExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RftEvent event, EventSupplierPojo supplier, String supplierFolder, int supplierNo) throws IOException {
		List<SorPojo> bqPojos = rftEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		RftEnvelop envelope = getRftEnvelopById(envelopeId);
		if (CollectionUtil.isNotEmpty(bqPojos)) {
			for (SorPojo bqPojo : bqPojos) {
				LOG.info("SOR ID : " + bqPojo.getId());
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
				List<RftSupplierSorItem> bqItems = rftSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int bqItemLoopCount = 1;

					for (RftSupplierSorItem item : bqItems) {

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

							for (RftSupplierSorItem child : item.getChildren()) {
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

	private void bqExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RftEvent event, EventSupplierPojo supplier, String supplierFolder) throws IOException {
		RftEnvelop envelope = rftEnvelopService.getRftEnvelopById(envelopeId);
		int x = 1;
		List<BqPojo> bqPojos = rftEnvelopDao.getBqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		if (CollectionUtil.isNotEmpty(bqPojos)) {

			for (BqPojo bqPojo : bqPojos) {
				LOG.info("BQ ID : " + bqPojo.getId());
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
					// cell.setCellStyle(styleHeading);
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
				List<RftSupplierBqItem> bqItems = rftSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int decimal = 2;
					// Write Data into Excel
					int bqItemLoopCount = 1;

					for (RftSupplierBqItem item : bqItems) {
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
						sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, columnCount));
						if (CollectionUtil.isNotEmpty(item.getChildren())) {
							int j = 1;
							for (RftSupplierBqItem child : item.getChildren()) {
								row = sheet.createRow(r++);
								cellNum = 0;
								row.createCell(cellNum++).setCellValue(child.getLevel() + "." + child.getOrder());
								row.createCell(cellNum++).setCellValue(child.getItemName() != null ? child.getItemName() : "");
								row.createCell(cellNum++).setCellValue(child.getItemDescription() != null ? child.getItemDescription() : "");
								row.createCell(cellNum++).setCellValue(child.getUom() != null ? child.getUom().getUom() : "");
								// row.createCell(cellNum++).setCellValue(child.getQuantity() != null ?
								// child.getQuantity() + "" : "");

								XSSFCell quantityCell = row.createCell(cellNum++);
								quantityCell.setCellValue(child.getQuantity() != null ? child.getQuantity().doubleValue() : 0);
								// quantityCell.setCellStyle(formatNumberStyle);
								quantityCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
								quantityCell.setCellStyle(formatNumberStyle);

								XSSFCell unitPriceCell = row.createCell(cellNum++);
								BigDecimal unitPrice = BigDecimal.ZERO;
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

								// sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
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
								x = j;
								j++;
							}
						}
					}

					sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 8));

					if (CollectionUtil.isNotEmpty(supplierHeadingList)) {
						int cellNub = fixedheadingCount + buyerHeadingList.size();
						// Row rowSupplierName = sheet.createRow(0);
						XSSFCell cellSname = rowSupplierName.createCell(cellNub);
						// cellSname.setCellValue(supplier.getSupplier().getCompanyName());
						// cellSname.setCellStyle(styleHeading);
						if (supplierHeadingList.size() > 0) {
							sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
									0, // last row (0-based)
									9, // first column (0-based)
									8 + supplierHeadingList.size() // last column (0-based)
							));
						}

						cellSname.setCellValue("Additional Columns");
						XSSFCellStyle myCellStyle = workbook.createCellStyle();
						myCellStyle.setFont(font);
						myCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
						myCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						cellSname.setCellStyle(myCellStyle);

						for (int p = 10; p <= 8 + supplierHeadingList.size(); p++) {
							cellSname = rowSupplierName.createCell(p);
							cellSname.setCellValue("");
							cellSname.setCellStyle(myCellStyle);
						}

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
							// cell.setCellStyle(styleHeading);
							XSSFCellStyle myCellStyle = workbook.createCellStyle();
							myCellStyle.setFont(font);
							myCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
							myCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							cell.setCellStyle(myCellStyle);
						}
					}

					RftSupplierBq supplierBq = rftSupplierBqDao.findBqByBqId(bqPojo.getId(), supplier.getId());
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
					FileUtil.writeXssfExcelToZip(zos, workbook, supplierFolder, MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()) + " - " + bqPojo.getBqName() + ".xlsx");
					zos.flush();
				} else {
					FileUtil.writeXssfExcelToZip(zos, workbook, supplierFolder, supplier.getCompanyName().replaceAll(" ", "_") + " - " + bqPojo.getBqName() + ".xlsx");
					zos.flush();
				}
			}
		} else {
			LOG.info("NO BQ for this envelop id : " + envelopeId);
		}
	}

	private void getBuyerSupplierExtraColumnData(RftSupplierBqItem item, Row row, int cellNum, RftSupplierBqItem child) {
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

	private void setBuyerSupplierHeadings(List<String> buyerHeadingList, List<String> supplierHeadingList, RftBqItem bqItem) {
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
	public JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationSorPojo> bqSummary = new ArrayList<EvaluationSorPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			LOG.info("Generating RFQ Supplier SOR Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			List<SorPojo> bqList = rftEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierSors.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (SorPojo bqPojo : bqList) {
					EvaluationSorPojo bq = new EvaluationSorPojo();
					String bqId = bqPojo.getId();
					RftEventSor bqDetail = rftSorDao.findById(bqId);
					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRftEnvelop())) {
						for (RftEnvelop env : bqDetail.getRfxEvent().getRftEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					List<RftSupplierSorItem> supBqItem = rftSupplierSorItemDao.findSupplierSorItemByListBySorIdAndSupplierId(bqId, supplierId);
					List<EvaluationSorItemPojo> bqItems = new ArrayList<EvaluationSorItemPojo>();
					for (RftSupplierSorItem item : supBqItem) {
						EvaluationSorItemPojo bqItem = new EvaluationSorItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RftSupplierSorItem childBqItem : item.getChildren()) {
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
			RftEvent event = rftEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
			RftEnvelop rfqEnvelop = getRftEnvelopById(envelopeId);
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
	public JasperPrint generateSupplierBqPdfForEnvelope(String envelopeId, String supplierId, int SupplierNo, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationBqPojo> bqSummary = new ArrayList<EvaluationBqPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			// List<String> bqList = rftEnvelopDao.getBqsByEnvelopId(Arrays.asList(envelopeId));
			List<BqPojo> bqList = rftEnvelopDao.getBqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierBqs.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {

				for (BqPojo bqPojo : bqList) {
					EvaluationBqPojo bq = new EvaluationBqPojo();
					String bqId = bqPojo.getId();
					RftEventBq bqDetail = rftEventBqDao.findById(bqId);
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRftEnvelop())) {
						for (RftEnvelop env : bqDetail.getRfxEvent().getRftEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					bq.setName(bqDetail.getName());
					List<RftSupplierBqItem> supBqItem = rftSupplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bqId, supplierId);
					RftSupplierBq supplierBq = rftSupplierBqDao.findBqByBqId(bqId, supplierId);
					BigDecimal additionalTax = null, grandTotal = null, totalAfterTax = null;
					String additionalTaxDesc = "";
					if (supplierBq != null) {
						additionalTax = supplierBq.getAdditionalTax();
						additionalTaxDesc = supplierBq.getTaxDescription();
						grandTotal = supplierBq.getGrandTotal();
						totalAfterTax = supplierBq.getTotalAfterTax();
					}
					List<EvaluationBqItemPojo> bqItems = new ArrayList<EvaluationBqItemPojo>();
					for (RftSupplierBqItem item : supBqItem) {
						EvaluationBqItemPojo bqItem = new EvaluationBqItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RftSupplierBqItem childBqItem : item.getChildren()) {
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
								evlBqChilItem.setGrandTotal(grandTotal);
								evlBqChilItem.setDecimal(childBqItem.getEvent() != null ? childBqItem.getEvent().getDecimal() : "");
								evlBqChilItem.setTotalAfterTax(totalAfterTax);
								bqItems.add(evlBqChilItem);
							}
						}
					}
					bq.setBqItems(bqItems);
					bqSummary.add(bq);
				}
			}
			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			parameters.put("SUPPLIER_BQS", bqSummary);

			RftEvent event = rftEnvelopDao.getEventbyEnvelopeId(envelopeId);
			RftEnvelop envelope = rftEnvelopService.getRftEnvelopById(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");

			parameters.put("eventType", RfxTypes.RFT.getValue());
			parameters.put("eventId", StringUtils.checkString(event.getEventId()));
			parameters.put("referenceNumber", StringUtils.checkString(event.getReferanceNumber()));
			parameters.put("eventCreationDate", (event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : ""));
			parameters.put("eventPublishDate", (event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : ""));
			parameters.put("eventStartDate", (event.getEventStart() != null ? sdf.format(event.getEventStart()) : ""));
			parameters.put("eventEndDate", (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : ""));
			parameters.put("currencyCode", StringUtils.checkString(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : ""));
			parameters.put("generatedOn", sdf.format(new Date()));
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking() && SupplierNo != 0) {
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
	public JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int supplierNo) {
		List<EvaluationCqPojo> cqSummary = new ArrayList<EvaluationCqPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierCqs.jasper");
			File jasperfile = resource.getFile();
			// List<String> cqList = rftEnvelopDao.getCqsByEnvelopId(Arrays.asList(envelopeId));
			List<CqPojo> cqList = rftEnvelopDao.getCqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			RftEnvelop envelope = rftEnvelopDao.findById(envelopeId);
			if (CollectionUtil.isNotEmpty(cqList)) {
				for (CqPojo cqPojo : cqList) {
					EvaluationCqPojo cq = new EvaluationCqPojo();
					String cqId = cqPojo.getId();
					RftCq cqDetail = rftCqDao.findById(cqId);
					cq.setTitle(envelope.getEnvelopTitle());
					cq.setName(cqDetail.getName());

					// List<RftSupplierCqItem> supCqItem = rftSupplierCqItemDao.findSupplierCqItemListByCqId(cqId,
					// supplierId);
					List<RftSupplierCqItem> supCqItem = rftSupplierCqItemDao.findSupplierCqItemByCqIdandSupplierId(cqId, supplierId);
					List<EvaluationCqItemPojo> cqItems = new ArrayList<EvaluationCqItemPojo>();
					for (RftSupplierCqItem item : supCqItem) {
						List<RftSupplierCqOption> listAnswers = rftSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
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
								for (RftSupplierCqOption cqOption : listAnswers) {
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
							for (RftSupplierCqOption cqOption : listAnswers) {
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

			RftEvent event = rftEnvelopDao.getEventbyEnvelopeId(envelopeId);

			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			parameters.put("eventType", RfxTypes.RFT.getValue());
			parameters.put("eventId", StringUtils.checkString(event.getEventId()));
			parameters.put("referenceNumber", StringUtils.checkString(event.getReferanceNumber()));
			parameters.put("eventCreationDate", (event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : ""));
			parameters.put("eventPublishDate", (event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : ""));
			parameters.put("eventStartDate", (event.getEventStart() != null ? sdf.format(event.getEventStart()) : ""));
			parameters.put("eventEndDate", (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : ""));
			parameters.put("currencyCode", StringUtils.checkString((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "")));
			parameters.put("generatedOn", sdf.format(new Date()));
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking() && supplierNo != 0) {
				parameters.put("supplierName", MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()));
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

	@Override
	public JasperPrint generateEvaluationSubmissionReport(String evenvelopId, String eventId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> submissionSummary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		RftEnvelop envlope = getRftEnvelopById(evenvelopId);

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSubmissionReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo eventDetails = new EvaluationPojo();
			// RftEvent event = rftEventService.getRftEventByeventId(eventId);
			RftEvent event = rftEventDao.findByEventId(eventId);
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
						for (RftCq cq : envlope.getCqList()) {
							EvaluationCqPojo item = new EvaluationCqPojo();
							item.setName(cq.getName());
							item.setDescription(cq.getDescription());
							cqs.add(item);
						}
					}
					eventDetails.setCqs(cqs);
					List<EvaluationBqPojo> bqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envlope.getBqList())) {
						for (RftEventBq item : envlope.getBqList()) {
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
		// List<EventSupplier> suppliers =
		// rftEventSupplierService.getAllSuppliersByEventId(eventId);
		try {
			List<EventSupplier> suppliers = rftEventSupplierDao.getAllSuppliersByEventId(eventId);
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
					List<RftCq> suppCqItem = null;
					List<RftSupplierBq> suppBqItem = null;
					if (item.getSupplier() != null) {
						suppCqItem = rftSupplierCqItemDao.getAllCqsBySupplierId(eventId, item.getSupplier().getId());
						suppBqItem = rftSupplierBqItemDao.getAllBqsBySupplierId(eventId, item.getSupplier().getId());
					}

					List<EvaluationCqPojo> cqs = new ArrayList<EvaluationCqPojo>();
					List<EvaluationBqPojo> bqs = new ArrayList<EvaluationBqPojo>();

					EvaluationEnvelopSuppliersPojo envelopSuppliers = new EvaluationEnvelopSuppliersPojo();
					envelopSuppliers.setSupplierName(item.getSupplierCompanyName());
					if (item.getSupplierSubmittedTime() != null) {
						envelopSuppliers.setSubmissionDate(new Date(sdf.format(item.getSupplierSubmittedTime()).toUpperCase()));
					}
					if (item.getSubbmitedBy() != null) {
						envelopSuppliers.setSubmittedBy(item.getSubbmitedBy() != null ? item.getSubbmitedBy().getName() : "");
					}
					if (CollectionUtil.isNotEmpty(suppCqItem)) {
						for (RftCq cqItem : suppCqItem) {
							EvaluationCqPojo cqList = new EvaluationCqPojo();
							cqList.setName(cqItem.getName());
							cqs.add(cqList);
						}
					}
					if (CollectionUtil.isNotEmpty(suppBqItem)) {
						for (RftSupplierBq bqItem : suppBqItem) {
							EvaluationBqPojo bqList = new EvaluationBqPojo();
							bqList.setName(bqItem.getName());
							bqs.add(bqList);
						}
					}

					envelopSuppliers.setCqs(cqs);
					envelopSuppliers.setBqs(bqs);
					supplierCqBqList.add(envelopSuppliers);
				}
			}
		} catch (Exception e) {
			LOG.error("Could not get Supplier List of Envelop Submission. " + e.getMessage(), e);
		}

		eventDetails.setEnvlopSuppliers(supplierCqBqList);
		return supplierList;
	}

	@Override
	public Integer getAllEnvelopCountByEventId(String eventId) {
		return rftEnvelopDao.getAllEnvelopCountByEventId(eventId);
	}

	@Override
	public List<RftEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		return rftEnvelopDao.getAllClosedEnvelopAndOpener(eventId);
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		return rftEnvelopDao.getcountClosedEnvelop(eventId);
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFT)));
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFT)));
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
		case RFT:
			displayName = rftEventDao.findBusinessUnitName(eventId);
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
		RftEnvelop envelop = rftEnvelopDao.findById(envelopeId);
		if (envelop.getOpener() != null) {
			if (enc.matches(key, envelop.getOpener().getPassword())) {
				envelop.setIsOpen(Boolean.TRUE);
				envelop.setOpenDate(new Date());
				envelop = rftEnvelopDao.update(envelop);
				success = envelop.getIsOpen();
				LOG.info("updated successfully.....");

				try {
					super.sendEnvelopOpenNotification(envelop, RfxTypes.RFT, eventId, loggedInUser, true);
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
	public void updateLeadEvaluatorSummary(RftEnvelop envelop) {
		rftEnvelopDao.update(envelop);
	}

	@Override
	public List<RftEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loginUser) {
		return rftEvaluatorUserDao.getEvaluationSummaryRemarks(evelopId, loginUser);
	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		return rftEnvelopDao.getCountOfAssignedSupplier(leadUserId, envelopId, eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void closeEnvelop(RftEnvelop envelop) {
		rftEnvelopDao.update(envelop);
	}

	@Override
	public String getEnvelipeTitleById(String evenvelopId, String eventType) {
		return rftEnvelopDao.getEnvelipeTitleById(evenvelopId, eventType);
	}

	@Override
	public RftEnvelop getRftEnvelopBySeqId(int i, String eventId) {
		return rftEnvelopDao.getRftEnvelopBySeqId(i, eventId);
	}

	@Override
	public boolean isAcceptedEvaluationDeclaration(String envelopId, String loggedInUser, String eventId) {
		return rftEvaluatorDeclarationDao.isAcceptedEvaluationDeclaration(envelopId, loggedInUser, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvaluatorDeclaration saveEvaluatorDeclaration(RftEvaluatorDeclaration evaluationDeclarationObj) {
		return rftEvaluatorDeclarationDao.save(evaluationDeclarationObj);
	}

	@Override
	public RftEnvelop getEmptyEnvelopByEventId(String eventId) {
		return rftEnvelopDao.getEmptyEnvelopByEventId(eventId);
	}

	@Override
	public List<RftEventBq> getBqsByEnvelopIdByOrder(String envelopId) {
		return rftEnvelopDao.getBqIdlistByEnvelopIdByOrder(envelopId);
	}

	@Override
	public List<RftCq> getCqsByEnvelopIdByOrder(String envelopId) {
		return rftEnvelopDao.getCqIdlistByEnvelopIdByOrder(envelopId);
	}

	@Override
	public List<RftEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		return rftEnvelopDao.getSorsByEnvelopIdByOrder(envelopId);
	}

}
