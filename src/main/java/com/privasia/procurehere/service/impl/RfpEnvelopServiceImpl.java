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

import com.privasia.procurehere.core.dao.RfpSorDao;
import com.privasia.procurehere.core.dao.RfpSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.RfpSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RfpSupplierSorItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpCqDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RfpEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RfpEventAuditDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfpEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCqOption;
import com.privasia.procurehere.core.entity.RfpUnMaskedUser;
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
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfpSupplierBqItemService;
import com.privasia.procurehere.service.RfpSupplierCqItemService;
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
public class RfpEnvelopServiceImpl extends EnvelopServiceImplBase implements RfpEnvelopService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	RfpEnvelopDao rfpEnvelopDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfpBqDao rfpEventBqDao;

	@Autowired
	RfpCqDao rfpCqDao;

	@Autowired
	RfpSupplierBqDao rfpSupplierBqDao;

	@Autowired
	UserService userService;

	@Autowired
	RfpEvaluatorUserDao evaluatorUserDao;

	@Autowired
	RfpSupplierCqItemDao rfpSupplierCqItemDao;

	@Autowired
	RfpEventSupplierDao rfpEventSupplierDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfpSupplierSorItemService rfpSupplierSorItemService;

	@Autowired
	UserDao userDao;

	@Autowired
	RfpSupplierBqItemService rfpSupplierBqItemService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfpEvaluatorUserDao rfpEvaluatorUserDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfpEventAuditDao rfpEventAuditDao;

	@Autowired
	RfpSupplierCqItemService rfpSupplierCqItemService;

	@Autowired
	RfpEvaluatorDeclarationDao rfpEvaluatorDeclarationDao;

	@Autowired
	RfpSupplierCqOptionDao rfpSupplierCqOptionDao;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfpSorDao rfpEventSorDao;

	@Autowired
	RfpSupplierSorItemDao rfpSupplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public RfpEnvelop saveEnvelop(RfpEnvelop rfpEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {

		// Fetch Event Obj Instance
		// RfpEvent event = rfpEventDao.findById(rfpEnvelop.getRfxEvent().getId());
		// rfpEnvelop.setRfxEvent(event);
		if (cqIds != null) {
			List<RfpCq> cqList = rfpCqDao.findCqsByIds(cqIds);
			rfpEnvelop.setCqList(cqList);
		}
		if (bqIds != null) {
			// Fetch all BQs matching the IDs
			List<RfpEventBq> bqList = rfpEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rfpEnvelop.setBqList(bqList);
		}
		if (sorIds != null) {
			// Fetch all BQs matching the IDs
			List<RfpEventSor> bqList = rfpEventSorDao.findAllSorsByIds(sorIds);
			LOG.info("bq list :  " + bqList);
			rfpEnvelop.setSorList(bqList);
		}
		LOG.info("Save Envelop   : " + rfpEnvelop);
		return rfpEnvelopDao.saveOrUpdate(rfpEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEnvelop(RfpEnvelop rfpEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {
		if (cqIds != null) {
			List<RfpCq> cqList = rfpCqDao.findCqsByIds(cqIds);
			rfpEnvelop.setCqList(cqList);
		} else {
			rfpEnvelop.setCqList(null);
		}
		if (bqIds != null) {
			// Fetch all BQs matching the IDs
			List<RfpEventBq> bqList = rfpEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rfpEnvelop.setBqList(bqList);
		} else {
			rfpEnvelop.setBqList(null);
		}

		if (sorIds != null) {
			List<RfpEventSor> sorList = rfpEventSorDao.findAllSorsByIds(sorIds);
			LOG.info("sor list :  " + sorList);
			rfpEnvelop.setSorList(sorList);
		} else {
			rfpEnvelop.setSorList(null);
		}

		rfpEnvelopDao.update(rfpEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEnvelop updateEnvelope(RfpEnvelop envelop, String eventId) throws ApplicationException {
		RfpEnvelop dbEnvelop = getEnvelopById(envelop.getId());
		User dbleadEvaluator = null;
		User dbOpener = null;
		String description = "Envelope \"" + dbEnvelop.getEnvelopTitle() + "\" is updated.";
		String evaluatorEditRemoved = "";
		boolean isEvaluatorEdit = false;
		for (RfpEvaluatorUser users : dbEnvelop.getEvaluators()) {
			if (envelop.getEvaluators() != null) {
				for (RfpEvaluatorUser Evalusers : envelop.getEvaluators()) {
					if (users.getId().equals(Evalusers.getId())) {
						Evalusers.setEvaluatorSummary(users.getEvaluatorSummary());
						Evalusers.setSummaryDate(users.getSummaryDate());
					}
				}
			}
		}

		for (RfpEnvelopeOpenerUser dbUsers : dbEnvelop.getOpenerUsers()) {
			if (envelop.getOpenerUsers() != null) {
				for (RfpEnvelopeOpenerUser envUsers : envelop.getOpenerUsers()) {
					if (dbUsers.getUser().getId().equals(envUsers.getId())) {
						envUsers.setIsOpen(dbUsers.getIsOpen());
						envUsers.setOpenDate(dbUsers.getOpenDate());
						envUsers.setCloseDate(dbUsers.getCloseDate());
					}
				}
			}
		}

		if (envelop.getEvaluators() != null) {
			for (RfpEvaluatorUser users : envelop.getEvaluators()) {
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
				for (RfpEvaluatorUser user : envelop.getEvaluators()) {
					boolean newUsers = true;
					for (RfpEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
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

				for (RfpEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					boolean oldUsers = true;
					for (RfpEvaluatorUser user : envelop.getEvaluators()) {
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
				for (RfpEvaluatorUser user : envelop.getEvaluators()) {
					addEvalUsers.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isEmpty(envelop.getEvaluators())) {
				for (RfpEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					removeEvalUsers.add(dbUser.getUser());
				}
			}
			// Openers
			if (EnvelopType.OPEN == dbEnvelop.getEnvelopType() && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfpEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					addOpeners.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && EnvelopType.OPEN == envelop.getEnvelopType()) {
				for (RfpEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					removeOpeners.add(dbUser.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfpEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					boolean newUsers = true;
					for (RfpEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							newUsers = false;
							break;
						}
					}
					if (newUsers) {
						addOpeners.add(user.getUser());
					}
				}
				for (RfpEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					boolean oldUsers = true;
					for (RfpEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
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
			for (RfpEvaluatorUser user : envelop.getEvaluators()) {
				user.setEnvelope(dbEnvelop);
			}
		}

		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfpEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
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

		// if (isEvaluatorEdit) {
		// evaluatorEditRemoved += "Evaluator changed";
		// int i = 0;
		// for (RfpEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
		// evaluatorEditRemoved += (i == 0 ? " to " : "");
		// evaluatorEditRemoved += dbUser.getUser().getName() + ",";
		// i++;
		// }
		// evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
		// }

		String timeZone = "GMT+8:00";
		RfpEvent event = dbEnvelop.getRfxEvent();
		if (sendLeadMail) {
			try {
				String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
				timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getLeadEvaluater(), timeZone);
				sendLeadEvaluatorInvitedNotification(dbEnvelop.getLeadEvaluater(), event, url, timeZone, RfxTypes.RFP.getValue());
				sendLeadEvaluatorRemovedNotification(dbleadEvaluator, event, url, timeZone, RfxTypes.RFP.getValue());
			} catch (Exception e) {
				LOG.error("Error While sending mail to lead evaluator :" + e.getMessage(), e);
			}
		}

		// if (sendOpenerInviteMail && dbEnvelop.getOpener() != null) {
		// try {
		// String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
		// timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getOpener(), timeZone);
		// sendOpenerInvitedNotification(dbEnvelop.getOpener(), event, url, timeZone, RfxTypes.RFP.getValue());
		// } catch (Exception e) {
		// LOG.error("Error While sending mail to invite opener :" + e.getMessage(), e);
		// }
		// }
		//
		// if (sendOpenerRemoveMail && dbOpener != null) {
		// try {
		// String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
		// timeZone = getTimeZoneByBuyerSettings(dbOpener, timeZone);
		// sendOpenerRemovedNotification(dbOpener, event, url, timeZone, RfxTypes.RFP.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorInvitedNotification(user, event, url, timeZone, RfxTypes.RFP.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorRemovedNotification(user, event, url, timeZone, RfxTypes.RFP.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerInvitedNotification(user, event, url, timeZone, RfxTypes.RFP.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerRemovedNotification(user, event, url, timeZone, RfxTypes.RFP.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been removed as Envelope Opener.<br/>";
			} catch (Exception e) {
				LOG.error("Error While sending mail to remove evaluator :" + e.getMessage(), e);
			}
		}

		Boolean isAllOpen = true;
		Boolean isAllClose = true;

		List<RfpEnvelopeOpenerUser> openersUserList = dbEnvelop.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(openersUserList)) {
			for (RfpEnvelopeOpenerUser opener : openersUserList) {
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

		RfpEnvelop envelope = rfpEnvelopDao.update(dbEnvelop);
		if (isEvaluatorEdit) {
			evaluatorEditRemoved += "Evaluator changed";
			int i = 0;
			for (RfpEvaluatorUser dbUser : envelope.getEvaluators()) {
				evaluatorEditRemoved += (i == 0 ? " to " : "");
				evaluatorEditRemoved += dbUser.getUser().getName() + ",";
				i++;
			}
			evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
		}

		RfpEventAudit audit = new RfpEventAudit();
		audit.setAction(AuditActionType.Update);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setEvent(event);
		audit.setActionDate(new Date());
		description += evaluatorEditRemoved;
		audit.setDescription(description);
		eventAuditService.save(audit);
		
		try {
			RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, description +"'for Event '"+rfpEvent.getEventId()+"' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
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
		RfpEnvelop dbEnvelop = rfpEnvelopDao.findById(envelopId);
		RfpEvent event = dbEnvelop.getRfxEvent();
		User leadEvaluator = dbEnvelop.getLeadEvaluater();
		String timeZone = "GMT+8:00";
		String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
		timeZone = getTimeZoneByBuyerSettings(logedUser, timeZone);
		User eventOwner = rfpEventService.getPlainEventOwnerByEventId(event.getId());
		String msg = "";
		if (dbEnvelop != null && leadEvaluator.getId().equals(logedUser.getId())) {

			if (StringUtils.checkString(dbEnvelop.getLeadEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}

			Boolean allDone = Boolean.TRUE;
			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators())) {
				for (RfpEvaluatorUser evaluator : dbEnvelop.getEvaluators()) {
					if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
						allDone = Boolean.FALSE;
						break;
					}
				}
			}

			if (Boolean.TRUE == allDone) {
				dbEnvelop.setEvaluationDate(new Date());
				dbEnvelop.setEvaluationStatus(EvaluationStatus.COMPLETE);
				dbEnvelop = rfpEnvelopDao.update(dbEnvelop);
			}
			try {
				// sending notification completed evaluation of envelope to lead
				// Evaluator
				msg = "You have complete evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFP.getValue(), msg);
				// sending notification completed evaluation of envelope to
				// Event Owner
				msg = "\"" + leadEvaluator.getName() + "\" has completed evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFP.getValue(), msg);

				try {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setAction(AuditActionType.Evaluate);
					audit.setActionBy(leadEvaluator);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + "' is evaluated");
					rfpEventAuditDao.save(audit);
					
					RfpEvent rfpEvent = rfpEventService.getPlainEventById(event.getId());
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.EVALUATE, "Envelope '" + dbEnvelop.getEnvelopTitle()+"' is Evaluated for event '"+rfpEvent.getEventId()+ "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				
			} catch (Exception e) {
				LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
			}
			// set Event status complete
			int count = rfpEnvelopDao.findCountPendingEnvelopse(event.getId());
			if (count == 0) {
				event.setStatus(EventStatus.COMPLETE);

				Supplier winningSupplier = rfpEventDao.findWinnerSupplier(event.getId());
				if (winningSupplier != null) {
					event.setWinningSupplier(winningSupplier);
					event.setWinningPrice(winningSupplier.getTotalAfterTax());
				}

				event = rfpEventDao.update(event);
				try {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setAction(AuditActionType.Complete);
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Evaluation is completed");
					rfpEventAuditDao.save(audit);
					
					RfpEvent rfpEvent = rfpEventService.getPlainEventById(event.getId());
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Evaluation is completed for Event '"+rfpEvent.getEventId()+ "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				
				try {
					// sending notification completed evaluation of envelope to
					// Event Owner
					msg = "The evaluation for the event \"" + event.getReferanceNumber() + "\" has been completed";
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFP.getValue(), msg);
					// sending notification completed evaluation of envelope to
					// buyer team members
					List<User> buyerMembers = rfpEventDao.getUserBuyerTeamMemberByEventId(event.getId());
					if (CollectionUtil.isNotEmpty(buyerMembers)) {
						for (User buyerTeamUser : buyerMembers) {
							sendEnvelopCompletedNotification(buyerTeamUser, event, url, timeZone, RfxTypes.RFP.getValue(), msg);
						}
					}

					// Send notification to unMasking User on all envelops evaluation completed
					if (Boolean.FALSE == event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
						url = APP_URL + "/buyer/RFP/envelopList/" + event.getId();
						msg = "You are assigned as Unmasking Owner for Event: \"" + event.getReferanceNumber() + "\"";
						for (RfpUnMaskedUser um : event.getUnMaskedUsers()) {
							sendEnvelopCompletedNotificationToUnMaskingUser(um.getUser(), event, url, timeZone, RfxTypes.RFP.getValue(), msg);
						}
					}

				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
				
				tatReportService.updateTatReportEvaluationCompleted(event.getId(), logedUser.getTenantId(), new Date(), event.getStatus());
			}
		} else {
			RfpEvaluatorUser evaluatorUser = rfpEnvelopDao.getRfpEvaluatorUserByUserIdAndEnvelopeId(envelopId, logedUser.getId());

			if (StringUtils.checkString(evaluatorUser.getEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}
			if (evaluatorUser != null) {
				evaluatorUser.setEvaluationDate(new Date());
				evaluatorUser.setEvaluationStatus(EvaluationStatus.COMPLETE);
				updateEvaluatorUser(evaluatorUser);
				try {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setAction(AuditActionType.Review);
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + " ' is reviewed");
					rfpEventAuditDao.save(audit);
					
					RfpEvent rfpEvent = rfpEventService.getPlainEventById(event.getId());
					BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.REVIEW, "Envelope '" +dbEnvelop.getEnvelopTitle() + "' is reviewed for Event '"+rfpEvent.getEventId()+ "'", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(ownerAuditTrail);
					
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					// sending notification to review on envelope to loged user
					msg = "You have completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(logedUser, event, url, timeZone, RfxTypes.RFP.getValue(), msg);
					// sending notification to review on envelope to lead
					// Evaluator
					msg = "\"" + logedUser.getName() + "\" has completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFP.getValue(), msg);
					// sending notification to review on envelope to Event Owner
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFP.getValue(), msg);
				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteEnvelop(RfpEnvelop rfpEnvelop) {
		rfpEnvelopDao.delete(rfpEnvelop);
	}

	@Override
	public List<RfpEnvelop> getAllEnvelop() {
		return rfpEnvelopDao.findAll(RfpEnvelop.class);
	}

	@Override
	public boolean isExists(RfpEnvelop rfpEnvelop, String eventId) {
		return rfpEnvelopDao.isExists(rfpEnvelop, eventId);
	}

	@Override
	public RfpEnvelop getEnvelopById(String id) {
		RfpEnvelop rfpEnvelop = rfpEnvelopDao.findById(id);
		if (CollectionUtil.isNotEmpty(rfpEnvelop.getBqList())) {
			for (RfpEventBq bq : rfpEnvelop.getBqList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfpEnvelop.getSorList())) {
			for (RfpEventSor bq : rfpEnvelop.getSorList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfpEnvelop.getEvaluators())) {
			for (RfpEvaluatorUser evalUser : rfpEnvelop.getEvaluators()) {
				evalUser.getUser().getName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfpEnvelop.getCqList())) {
			for (RfpCq cq : rfpEnvelop.getCqList()) {
				cq.getName();
			}
		}

		if (rfpEnvelop.getOpener() != null) {
			rfpEnvelop.getOpener().getName();
		}
		if (CollectionUtil.isNotEmpty(rfpEnvelop.getOpenerUsers())) {
			for (RfpEnvelopeOpenerUser openerUser : rfpEnvelop.getOpenerUsers()) {
				openerUser.getUser().getName();
			}
		}
		return rfpEnvelop;
	}

	@Override
	public RfpEnvelop getEnvelopForEvaluationById(String id, User logedUser) {
		RfpEnvelop envelop = rfpEnvelopDao.findById(id);
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
						for (RfpEvaluatorUser evaluator : envelop.getEvaluators()) {
							if (envelop.getIsOpen() && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId()) && evaluator.getEvaluationStatus() == EvaluationStatus.PENDING && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
								envelop.setShowFinish(true);
							}
							if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
								showFinishForLead = Boolean.FALSE;
							}
						}
					}
					if (envelop.getIsOpen() && envelop.getLeadEvaluater().getId().equals(logedUser.getId()) && showFinishForLead && EvaluationStatus.COMPLETE != envelop.getEvaluationStatus() && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
						envelop.setShowFinish(true);
					}

				}
			}
		}

		return envelop;
	}

	@Override
	public List<RfpEnvelop> getAllEnvelopByEventId(String eventId, User logedUser) {
		List<RfpEnvelop> envList = rfpEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFP);
		if (CollectionUtil.isNotEmpty(envList)) {
			for (RfpEnvelop envelop : envList) {
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RfpEventBq bq : envelop.getBqList()) {
						bq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfpCq cq : envelop.getCqList()) {
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
				// if (envelop.getIsOpen() && envelop.getOpener() != null &&
				// envelop.getOpener().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() ==
				// EventStatus.CLOSED) {
				// envelop.setShowOpen(true);
				// }
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RfpEvaluatorUser evaluator : envelop.getEvaluators()) {
						LOG.info("evaluator : " + evaluator.getUser().getId() + "  --   " + logedUser.getId());
						if (envelop.getIsOpen() && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId())) {
							envelop.setShowView(true);
						}
					}
				}
				// }
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfpEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
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
	public List<RfpEnvelop> getAllEnvelopByEventId(String eventId) {
		List<RfpEnvelop> envList = rfpEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFP);

		// Here another call because we want to retreive the SOr List also , as hibernate does n't support to extract multiple list retreival
		List<RfpEnvelop> envList2 = rfpEnvelopDao.getAllEnvelopSorByEventId(eventId, RfxTypes.RFQ);

		setSorInBqData(envList, envList2);

		if (CollectionUtil.isNotEmpty(envList)) {
			for (RfpEnvelop rfpEnvelop : envList) {
				if (CollectionUtil.isNotEmpty(rfpEnvelop.getBqList())) {
					for (RfpEventBq bq : rfpEnvelop.getBqList()) {
						bq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(rfpEnvelop.getCqList())) {
					for (RfpCq cq : rfpEnvelop.getCqList()) {
						cq.getName();
					}
				}
				for (RfpEvaluatorUser evaluators : rfpEnvelop.getEvaluators())
					evaluators.getUser().getName();

				if (CollectionUtil.isNotEmpty(rfpEnvelop.getOpenerUsers())) {
					for (RfpEnvelopeOpenerUser opener : rfpEnvelop.getOpenerUsers()) {
						opener.getUser().getName();
					}
				}
			}
		}
		return envList;
	}

	private void setSorInBqData(List<RfpEnvelop> envList, List<RfpEnvelop> envList2) {
		Map<String, RfpEnvelop> envelopMap = envList2.stream().collect(Collectors.toMap(RfpEnvelop::getId, Function.identity()));
		envList.forEach(event -> {
			RfpEnvelop matchingEvent = envelopMap.get(event.getId());
			if(matchingEvent != null) {
				event.setSorList(matchingEvent.getSorList());
			}
		});
	}

	@Override
	public List<RfpEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		LOG.info("findEvaluatorsByEnvelopId in RFP ISSUE 35");
		return rfpEnvelopDao.findEvaluatorsByEnvelopId(envelopId);
	}

	@Override
	public Integer getAllEnvelopCountByEventId(String eventId) {
		return rfpEnvelopDao.getAllEnvelopCountByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfpEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException {
		LOG.info("RfpEnvelopeService Impl  addEvaluator: envelopeId " + envelopeId + " userId: " + userId);

		RfpEnvelop rfpEnvelop = getEnvelopById(envelopeId);
		if (userId.equals(rfpEnvelop.getLeadEvaluater().getId())) {
			throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
		}
		LOG.info(rfpEnvelop.getId());
		List<RfpEvaluatorUser> evaluators = rfpEnvelop.getEvaluators();
		if (evaluators == null) {
			evaluators = new ArrayList<RfpEvaluatorUser>();
		}
		RfpEvaluatorUser rfpEvaluatorUser = new RfpEvaluatorUser();
		rfpEvaluatorUser.setEnvelope(rfpEnvelop);
		User user = userService.getUsersById(userId);
		try {
			rfpEvaluatorUser.setUser((User) user.clone());
		} catch (Exception e) {
			LOG.error("Error :  " + e.getMessage(), e);
		}

		evaluators.add(rfpEvaluatorUser);
		rfpEnvelop.setEvaluators(evaluators);
		rfpEnvelopDao.saveOrUpdate(rfpEnvelop);
		LOG.info("evaluators.size() :" + evaluators.size());
		return evaluators;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEvaluator(String envelopeId, String userId) {
		LOG.info("RfpEnvelopeService Impl  removeEvaluator: envelopeId " + envelopeId + " userId: " + userId);
		RfpEnvelop rfpEnvelop = getEnvelopById(envelopeId);
		LOG.info(rfpEnvelop.getId());
		List<RfpEvaluatorUser> rfpEvaluatorUser = rfpEnvelop.getEvaluators();
		if (rfpEvaluatorUser == null) {
			rfpEvaluatorUser = new ArrayList<RfpEvaluatorUser>();
		}
		LOG.info("rfpEvaluatorUser.size() :" + rfpEvaluatorUser.size());
		RfpEvaluatorUser dbEvaluatorUser = getRfpEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
		LOG.info(dbEvaluatorUser.getUser().getName());
		rfpEvaluatorUser.remove(dbEvaluatorUser);
		dbEvaluatorUser.setEnvelope(null);
		rfpEnvelop.setEvaluators(rfpEvaluatorUser);
		rfpEnvelopDao.update(rfpEnvelop);
		LOG.info(" rfpEnvelop.getEvaluators() :" + rfpEnvelop.getEvaluators().size());
		List<User> userList = new ArrayList<User>();
		try {
			for (RfpEvaluatorUser rfpeval : rfpEnvelop.getEvaluators()) {
				userList.add((User) rfpeval.getUser().clone());
			}
			LOG.info(userList.size());
		} catch (Exception e) {
			LOG.error("Error constructing list of users after remove operation : " + e.getMessage(), e);
		}
		return userList;

	}

	@Override
	public RfpEvaluatorUser getRfpEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId) {
		return rfpEnvelopDao.getRfpEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEvaluatorUser(RfpEvaluatorUser evaluatorUser) {
		evaluatorUserDao.update(evaluatorUser);
	}

	@Override
	@Transactional(readOnly = false)
	public void openEnvelop(RfpEnvelop envelop) {
		rfpEnvelopDao.update(envelop);
	}

	@Override
	@Transactional(readOnly = true)
	public RfpEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		return evaluatorUserDao.findEvaluatorUser(envelopId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException {
		RfpEnvelop envlope = rfpEnvelopDao.findById(envelopeId);
		RfpEvent event = rfpEventDao.findByEventId(eventId);
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

		// List<EventSupplier> supplierList = rfpEventSupplierDao.getAllSuppliersByEventId(eventId);
		List<EventSupplierPojo> supplierList = rfpEventSupplierDao.getSubmitedSuppliers(eventId);
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
						bqExportExcelForEachSupplier(envelopeId, zos, event, supplier, supplierFolder, i);
					} catch (Exception e) {
						LOG.error("Error While Export bq for each supplier : " + e.getMessage(), e);
					}

				} else {
					LOG.info("No BQ setup for event : " + eventId);
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
						for (RfpCq cq : envlope.getCqList()) {
							String attachmentFolder = "";
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;
							} else {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;
							}
							// List<RfpSupplierCqItem> supplierCqItems =
							// rfpSupplierCqItemDao.getSupplierCqItemsbySupplierId(eventId, supplier.getId(),
							// cq.getId());
							List<RfpSupplierCqItem> supplierCqItems = rfpSupplierCqItemDao.getSupplierCqItemsbySupplier(supplier.getId(), cq.getId());
							if (CollectionUtil.isNotEmpty(supplierCqItems)) {
								for (RfpSupplierCqItem supCqItem : supplierCqItems) {
									if (supCqItem.getFileData() != null) {
										// [eventRef+envelopeName]/[supplierCompanyName]/[attachments]/[cqs]

										// String extension =
										// FilenameUtils.getExtension(StringUtils.checkString(supCqItem.getFileName()));
										String fileName = supCqItem.getCqItem().getLevel() + "-" + supCqItem.getCqItem().getOrder() + "-" + supCqItem.getFileName();
										// [attachment name will be the CQ
										// item number and file name
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
		}

		if (isForAllReports) {
			int count = 1;
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			LOG.info("short summary");
			JasperPrint jasperPrint = rftEventService.generateShortEvaluationSummaryReport("RFP", eventId, envelopeId, timeZone, virtualizer);
			if (jasperPrint != null) {
				LOG.info("short summary");
				FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Short Submission Summary for " + envlope.getEnvelopTitle());
				zos.flush();
			}

			try {
				jasperPrint = rfpEventService.getEvaluationReport(eventId, envelopeId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Evaluation Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}
			} catch (IOException e) {
			} catch (JRException e) {
			}

			try {
				jasperPrint = rfpEventService.generateSubmissionReport(envelopeId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Submission Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}
			} catch (IOException e) {
			} catch (JRException e) {
			}

			List<EventEvaluationPojo> list = null;

			list = rfpSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, envelopeId);
			if (CollectionUtil.isNotEmpty(list)) {
				XSSFWorkbook workbookBq = new XSSFWorkbook();
				workbookBq = rfaEventService.buildBqComparisionFile(workbookBq, list, null, eventId, envelopeId, RfxTypes.RFP);
				if (workbookBq != null) {
					FileUtil.writeXssfExcelToZip(zos, workbookBq, parentFolder + "/", "BQ Comparison Table.xlsx");
					zos.flush();
				}

			}

			XSSFWorkbook workbookCq = new XSSFWorkbook();
			list = rfpSupplierCqItemService.getEvaluationDataForCqComparison(eventId, envelopeId);
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

	private void bqExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RfpEvent event, EventSupplierPojo supplier, String supplierFolder, int i2) throws IOException {
		// List<BqPojo> bqPojos = rfpEnvelopDao.getBqNameAndIdsByEnvelopId(Arrays.asList(envelopeId));
		List<BqPojo> bqPojos = rfpEnvelopDao.getBqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		RfpEnvelop envelope = getEnvelopById(envelopeId);
		if (CollectionUtil.isNotEmpty(bqPojos)) {

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

				XSSFCellStyle alignRightBoldStyle = workbook.createCellStyle();
				alignRightBoldStyle.setAlignment(HorizontalAlignment.RIGHT);
				alignRightBoldStyle.setFont(font);

				XSSFCellStyle blueRightBoldStyle = workbook.createCellStyle();
				blueRightBoldStyle.setFont(font);
				blueRightBoldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
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

				// Style of amount Cells
				XSSFCellStyle alignRightStyle = workbook.createCellStyle();
				alignRightStyle.setAlignment(CellStyle.ALIGN_RIGHT);

				XSSFCellStyle formatNumberStyle = workbook.createCellStyle();
				formatNumberStyle.setAlignment(HorizontalAlignment.RIGHT);

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
				List<RfpSupplierBqItem> bqItems = rfpSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int decimal = 2;
					// Write Data into Excel
					int bqItemLoopCount = 1;

					for (RfpSupplierBqItem item : bqItems) {
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
							for (RfpSupplierBqItem child : item.getChildren()) {
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
								Cell totalAmountCell = row.createCell(cellNum++);
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

								Cell totalAmountWithTaxCell = row.createCell(cellNum++);
								totalAmountWithTaxCell.setCellValue(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax().doubleValue() : 0);
								totalAmountWithTaxCell.setCellStyle(formatNumberStyle);
								totalAmountWithTaxCell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
								totalAmountWithTaxCell.setCellFormula(totalAmountWithTaxFormula);

								getBuyerSupplierExtraColumnData(item, row, cellNum, child);

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
						// Row rowSupplierName = sheet.createRow(0);
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
						// cellSname.setCellStyle(styleHeading);
						cellSname.setCellValue("Additional Columns");
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

					RfpSupplierBq supplierBq = rfpSupplierBqDao.findBqByBqId(bqPojo.getId(), supplier.getId());
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

	private void setBuyerSupplierHeadings(List<String> buyerHeadingList, List<String> supplierHeadingList, RfpBqItem bqItem) {
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


	private void sorExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RfpEvent event, EventSupplierPojo supplier, String supplierFolder, int supplierNo) throws IOException {
		List<SorPojo> bqPojos = rfpEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		RfpEnvelop envelope = getEnvelopById(envelopeId);
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
				List<RfpSupplierSorItem> bqItems = rfpSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int bqItemLoopCount = 1;

					for (RfpSupplierSorItem item : bqItems) {

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

							for (RfpSupplierSorItem child : item.getChildren()) {
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
	public JasperPrint generateSupplierBqPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationBqPojo> bqSummary = new ArrayList<EvaluationBqPojo>();
		RfpEnvelop envelope = getEnvelopById(envelopeId);
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			LOG.info("Generating RFP Supplier BQ Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			// List<String> bqList = rfpEnvelopDao.getBqsByEnvelopId(Arrays.asList(envelopeId));
			List<BqPojo> bqList = rfpEnvelopDao.getBqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierBqs.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {

				for (BqPojo bqPojo : bqList) {
					EvaluationBqPojo bq = new EvaluationBqPojo();
					String bqId = bqPojo.getId();
					RfpEventBq bqDetail = rfpEventBqDao.findById(bqId);
					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRfxEnvelop())) {
						for (RfpEnvelop env : bqDetail.getRfxEvent().getRfxEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					List<RfpSupplierBqItem> supBqItem = rfpSupplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bqId, supplierId);
					RfpSupplierBq supplierBq = rfpSupplierBqDao.findBqByBqId(bqId, supplierId);
					BigDecimal additionalTax = null, grandTotal = null, totalAfterTax = null;
					String additionalTaxDesc = "";
					if (supplierBq != null) {
						additionalTax = supplierBq.getAdditionalTax();
						additionalTaxDesc = supplierBq.getTaxDescription();
						grandTotal = supplierBq.getGrandTotal();
						totalAfterTax = supplierBq.getTotalAfterTax();
					}
					List<EvaluationBqItemPojo> bqItems = new ArrayList<EvaluationBqItemPojo>();
					for (RfpSupplierBqItem item : supBqItem) {
						EvaluationBqItemPojo bqItem = new EvaluationBqItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItem.setAdditionalTax(item.getAdditionalTax());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RfpSupplierBqItem childBqItem : item.getChildren()) {
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
			parameters.put("SUPPLIER_BQS", bqSummary);
			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			RfpEvent event = rfpEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			parameters.put("eventType", RfxTypes.RFP.getValue());
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
				LOG.info("Supplier");
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
		RfpEnvelop envelope = getEnvelopById(envelopeId);

		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierCqs.jasper");
			File jasperfile = resource.getFile();
			// List<String> cqList = rfpEnvelopDao.getCqsByEnvelopId(Arrays.asList(envelopeId));
			List<CqPojo> cqList = rfpEnvelopDao.getCqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			if (CollectionUtil.isNotEmpty(cqList)) {
				for (CqPojo cqPojo : cqList) {
					EvaluationCqPojo cq = new EvaluationCqPojo();
					String cqId = cqPojo.getId();
					RfpCq cqDetail = rfpCqDao.findById(cqId);
					String title = "";
					if (CollectionUtil.isNotEmpty(cqDetail.getRfxEvent().getRfxEnvelop())) {
						for (RfpEnvelop env : cqDetail.getRfxEvent().getRfxEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					cq.setTitle(title);
					cq.setName(cqDetail.getName());
					// List<RfpSupplierCqItem> supCqItem = rfpSupplierCqItemDao.findSupplierCqItemListByCqId(cqId,
					// supplierId);

					List<RfpSupplierCqItem> supCqItem = rfpSupplierCqItemDao.findSupplierCqItemByCqIdandSupplierId(cqId, supplierId);

					List<EvaluationCqItemPojo> cqItems = new ArrayList<EvaluationCqItemPojo>();
					for (RfpSupplierCqItem item : supCqItem) {
						List<RfpSupplierCqOption> listAnswers = rfpSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
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
								for (RfpSupplierCqOption cqOption : listAnswers) {
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
							for (RfpSupplierCqOption cqOption : listAnswers) {
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

			RfpEvent event = rfpEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			parameters.put("eventName", StringUtils.checkString(event.getEventName()));
			parameters.put("eventCreator", event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + ", Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			parameters.put("eventType", RfxTypes.RFP.getValue());
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
		RfpEnvelop envlope = getEnvelopById(evenvelopId);

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
			RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
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
						for (RfpCq cq : envlope.getCqList()) {
							EvaluationCqPojo item = new EvaluationCqPojo();
							item.setName(cq.getName());
							item.setDescription(cq.getDescription());
							cqs.add(item);
						}
					}
					eventDetails.setCqs(cqs);
					List<EvaluationBqPojo> bqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envlope.getBqList())) {
						for (RfpEventBq item : envlope.getBqList()) {
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
		// List<EventSupplier> suppliers =
		// rfpEventSupplierService.getAllSuppliersByEventId(eventId);
		List<EventSupplier> suppliers = rfpEventSupplierDao.getAllSuppliersByEventId(eventId);
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

				List<RfpCq> suppCqItem = null;
				List<RfpSupplierBq> suppBqItem = null;
				if (item.getSupplier() != null) {
					suppCqItem = rfpSupplierCqItemDao.getAllCqsBySupplierId(eventId, item.getSupplier().getId());
					suppBqItem = rfpSupplierBqItemDao.getAllBqsBySupplierId(eventId, item.getSupplier().getId());
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
					for (RfpCq cqItem : suppCqItem) {
						EvaluationCqPojo cqList = new EvaluationCqPojo();
						cqList.setName(cqItem.getName());
						cqs.add(cqList);
					}
				}
				if (CollectionUtil.isNotEmpty(suppBqItem)) {
					for (RfpSupplierBq bqItem : suppBqItem) {
						EvaluationBqPojo bqList = new EvaluationBqPojo();
						bqList.setName(bqItem.getBq().getName());
						bqs.add(bqList);
					}
				}
				envelopSuppliers.setCqs(cqs);
				envelopSuppliers.setBqs(bqs);
				supplierCqBqList.add(envelopSuppliers);
				eventDetails.setEnvlopSuppliers(supplierCqBqList);
			}
		}
		return supplierList;
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		return rfpEnvelopDao.getcountClosedEnvelop(eventId);
	}

	@Override
	public List<RfpEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		return rfpEnvelopDao.getAllClosedEnvelopAndOpener(eventId);
	}

	@Override
	public List<User> getAllEnvelopEvaluatorUsers(String eventId) {
		LOG.info("getAllEnvelopEvaluatorUsers get envelop user in rfp?");
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFP)));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			notificationService.sendEmail(mailTo, subject, map, Global.ENVELOPE_COMPLETED_TEMPLATE);
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFP)));
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
		case RFP:
			displayName = rfpEventDao.findBusinessUnitName(eventId);
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
			notificationService.sendEmail(mailTo, subject, map, Global.LEAD_EVALUATOR_INVITED_TEMPLATE);
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
			notificationService.sendEmail(mailTo, subject, map, Global.LEAD_EVALUATOR_REMOVED_TEMPLATE);
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
			notificationService.sendEmail(mailTo, subject, map, Global.OPENER_REMOVED_TEMPLATE);
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
			notificationService.sendEmail(mailTo, subject, map, Global.OPENER_INVITED_TEMPLATE);
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
			notificationService.sendEmail(mailTo, subject, map, Global.EVALUATOR_REMOVED_TEMPLATE);
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
			notificationService.sendEmail(mailTo, subject, map, Global.EVALUATOR_INVITED_TEMPLATE);
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
		RfpEnvelop envelop = rfpEnvelopDao.findById(envelopeId);

		if (envelop.getOpener() != null) {
			if (enc.matches(key, envelop.getOpener().getPassword())) {
				envelop.setIsOpen(Boolean.TRUE);
				envelop.setOpenDate(new Date());
				envelop = rfpEnvelopDao.update(envelop);
				success = envelop.getIsOpen();
				LOG.info("updated successfully.....");
				try {
					super.sendEnvelopOpenNotification(envelop, RfxTypes.RFP, eventId, loggedInUser, true);
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

	private void getBuyerSupplierExtraColumnData(RfpSupplierBqItem item, Row row, int cellNum, RfpSupplierBqItem child) {
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

	@Override
	public JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationSorPojo> bqSummary = new ArrayList<EvaluationSorPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			LOG.info("Generating RFP Supplier SOR Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			List<SorPojo> bqList = rfpEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierSors.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (SorPojo bqPojo : bqList) {
					EvaluationSorPojo bq = new EvaluationSorPojo();
					String bqId = bqPojo.getId();
					RfpEventSor bqDetail = rfpEventSorDao.findById(bqId);
					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRfxEnvelop())) {
						for (RfpEnvelop env : bqDetail.getRfxEvent().getRfxEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					List<RfpSupplierSorItem> supBqItem = rfpSupplierSorItemDao.findSupplierSorItemByListBySorIdAndSupplierId(bqId, supplierId);
					List<EvaluationSorItemPojo> bqItems = new ArrayList<EvaluationSorItemPojo>();
					for (RfpSupplierSorItem item : supBqItem) {
						EvaluationSorItemPojo bqItem = new EvaluationSorItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RfpSupplierSorItem childBqItem : item.getChildren()) {
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
			RfpEvent event = rfpEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
			RfpEnvelop rfqEnvelop = getEnvelopById(envelopeId);
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
	@Transactional(readOnly = false)
	public void updateLeadEvaluatorSummary(RfpEnvelop envelop) {
		rfpEnvelopDao.update(envelop);
	}

	@Override
	public List<RfpEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loginUser) {
		return rfpEvaluatorUserDao.getEvaluationSummaryRemarks(evelopId, loginUser);

	}

	@Override
	public RfpEnvelop getRfpEnvelopById(String evenvelopId) {

		RfpEnvelop rfpEnvelop = rfpEnvelopDao.findById(evenvelopId);
		if (rfpEnvelop == null) {
			return null;
		}
		if (rfpEnvelop.getRfxEvent() != null) {
			rfpEnvelop.getRfxEvent().getEventName();
		}
		if (CollectionUtil.isNotEmpty(rfpEnvelop.getBqList())) {
			for (RfpEventBq bq : rfpEnvelop.getBqList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfpEnvelop.getCqList())) {
			for (RfpCq cq : rfpEnvelop.getCqList()) {
				cq.getName();
			}
		}

		if (CollectionUtil.isNotEmpty(rfpEnvelop.getEvaluators())) {
			for (RfpEvaluatorUser evalUser : rfpEnvelop.getEvaluators()) {
				evalUser.getUser().getName();
			}
		}

		if (rfpEnvelop.getLeadEvaluater() != null) {
			rfpEnvelop.getLeadEvaluater().getLoginId();
		}
		if (rfpEnvelop.getOpener() != null) {
			rfpEnvelop.getOpener().getName();
		}
		return rfpEnvelop;

	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		return rfpEnvelopDao.getCountOfAssignedSupplier(leadUserId, envelopId, eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void closeEnvelop(RfpEnvelop envelop) {
		rfpEnvelopDao.update(envelop);
	}

	@Override
	public RfpEnvelop getRfpEnvelopBySeq(int i, String eventId) {

		return rfpEnvelopDao.getRfpEnvelopBySeq(i, eventId);
	}

	@Override
	public boolean isAcceptedEvaluationDeclaration(String envelopId, String loggedInUser, String eventId) {
		return rfpEvaluatorDeclarationDao.isAcceptedEvaluationDeclaration(envelopId, loggedInUser, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvaluatorDeclaration saveEvaluatorDeclaration(RfpEvaluatorDeclaration evaluationDeclarationObj) {
		return rfpEvaluatorDeclarationDao.save(evaluationDeclarationObj);
	}

	@Override
	public RfpEnvelop getEmptyEnvelopByEventId(String eventId) {
		return rfpEnvelopDao.getEmptyEnvelopByEventId(eventId);
	}

	@Override
	public List<RfpEventBq> getBqsByEnvelopIdByOrder(String envelopId) {
		return rfpEnvelopDao.getBqsByEnvelopIdByOrder(envelopId);
	}

	@Override
	public List<RfpCq> getCqsByEnvelopIdByOrder(String envelopId) {
		return rfpEnvelopDao.getCqsByEnvelopIdByOrder(envelopId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLeadEvaluatorDocument(RfpEnvelop envelop) {
		rfpEnvelopDao.update(envelop);
	}

	@Override
	public List<RfpEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		return rfpEnvelopDao.getSorsByEnvelopIdByOrder(envelopId);
	}
}
