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

import com.privasia.procurehere.core.dao.RfqSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfqSorItem;
import com.privasia.procurehere.core.entity.RfqSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RfqSupplierSorItemService;
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
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqCqDao;
import com.privasia.procurehere.core.dao.RfqSorDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RfqEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RfqEventAuditDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.RfqEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfqEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCqOption;
import com.privasia.procurehere.core.entity.RfqUnMaskedUser;
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
import com.privasia.procurehere.service.RfqEnvelopService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RfqSupplierBqItemService;
import com.privasia.procurehere.service.RfqSupplierCqItemService;
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
public class RfqEnvelopServiceImpl extends EnvelopServiceImplBase implements RfqEnvelopService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	RfqEnvelopDao rfqEnvelopDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfqBqDao rfqEventBqDao;

	@Autowired
	RfqSorDao rfqEventSorDao;

	@Autowired
	RfqSupplierSorItemDao rfqSupplierSorItemDao;

	@Autowired
	RfqSorDao rfqSorDao;

	@Autowired
	RfqSupplierBqDao rfqSupplierBqDao;

	@Autowired
	RfqCqDao rfqCqDao;

	@Autowired
	UserService userService;

	@Autowired
	RfqEvaluatorUserDao evaluatorUserDao;

	@Autowired
	RfqSupplierCqItemDao rfqSupplierCqItemDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RfqEventSupplierDao rfqEventSupplierDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RfqSupplierBqItemService rfqSupplierBqItemService;

	@Autowired
	RfqSupplierSorItemService rfqSupplierSorItemService;

	@Autowired
	UserDao userDao;

	@Autowired
	SupplierService supplierService;
	@Autowired
	RfqEvaluatorUserDao rfqEvaluatorUserDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfqEventAuditDao rfqEventAuditDao;

	@Autowired
	RfqSupplierCqItemService rfqSupplierCqItemService;

	@Autowired
	RfqEvaluatorDeclarationDao rfqEvaluatorDeclarationDao;

	@Autowired
	RfqSupplierCqOptionDao rfqSupplierCqOptionDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public RfqEnvelop saveEnvelop(RfqEnvelop rfqEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {

		// Fetch Event Obj Instance
		// RfqEvent event = rfqEventDao.findById(rfqEnvelop.getRfxEvent().getId());
		// rfqEnvelop.setRfxEvent(event);
		if (cqIds != null) {
			List<RfqCq> cqList = rfqCqDao.findCqsByIds(cqIds);
			rfqEnvelop.setCqList(cqList);
		}
		if (bqIds != null) {
			// Fetch all BQs matching the IDs
			List<RfqEventBq> bqList = rfqEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rfqEnvelop.setBqList(bqList);
		}

		if(sorIds != null) {
			List<RfqEventSor> sorList = rfqSorDao.findAllSorsByIds(sorIds);
			LOG.info("Sor list :  " + sorList);
			rfqEnvelop.setSorList(sorList);
		}

		LOG.info("Save Envelop   : " + rfqEnvelop);
		return rfqEnvelopDao.saveOrUpdate(rfqEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEnvelop(RfqEnvelop rfqEnvelop, String[] bqIds, String[] cqIds, String[] sorIds) {
		if (cqIds != null) {
			List<RfqCq> cqList = rfqCqDao.findCqsByIds(cqIds);
			rfqEnvelop.setCqList(cqList);
		} else {
			rfqEnvelop.setCqList(null);
		}
		if (bqIds != null) {
			// Fetch all BQs matching the IDs
			List<RfqEventBq> bqList = rfqEventBqDao.findAllBqsByIds(bqIds);
			LOG.info("bq list :  " + bqList);
			rfqEnvelop.setBqList(bqList);
		} else {
			rfqEnvelop.setBqList(null);
		}

		if(sorIds != null) {
			List<RfqEventSor> sorList = rfqSorDao.findAllSorsByIds(sorIds);
			LOG.info("Sor list :  " + sorList);
			rfqEnvelop.setSorList(sorList);
		} else {
			rfqEnvelop.setSorList(null);
		}


		rfqEnvelopDao.update(rfqEnvelop);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEnvelop updateEnvelope(RfqEnvelop envelop, String eventId) throws ApplicationException {
		RfqEnvelop dbEnvelop = getEnvelopById(envelop.getId());
		String description = "Envelope \"" + dbEnvelop.getEnvelopTitle() + "\" is updated.";
		String evaluatorEditRemoved = "";
		boolean isEvaluatorEdit = false;
		User dbleadEvaluator = null;
		User dbOpener = null;

		for (RfqEvaluatorUser users : dbEnvelop.getEvaluators()) {
			if (envelop.getEvaluators() != null) {
				for (RfqEvaluatorUser Evalusers : envelop.getEvaluators()) {
					if (users.getId().equals(Evalusers.getId())) {
						Evalusers.setEvaluatorSummary(users.getEvaluatorSummary());
						Evalusers.setSummaryDate(users.getSummaryDate());
					}
				}
			}
		}

		for (RfqEnvelopeOpenerUser dbUsers : dbEnvelop.getOpenerUsers()) {
			if (envelop.getOpenerUsers() != null) {
				for (RfqEnvelopeOpenerUser envUsers : envelop.getOpenerUsers()) {
					if (dbUsers.getUser().getId().equals(envUsers.getId())) {
						envUsers.setIsOpen(dbUsers.getIsOpen());
						envUsers.setOpenDate(dbUsers.getOpenDate());
						envUsers.setCloseDate(dbUsers.getCloseDate());
					}
				}
			}
		}

		if (envelop.getEvaluators() != null) {
			for (RfqEvaluatorUser users : envelop.getEvaluators()) {
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

		for (RfqEvaluatorUser users : dbEnvelop.getEvaluators()) {
			if (envelop.getEvaluators() != null) {
				for (RfqEvaluatorUser Evalusers : envelop.getEvaluators()) {
					if (users != null && Evalusers != null) {
						if (users.getId().equals(Evalusers.getId())) {
							Evalusers.setEvaluatorSummary(users.getEvaluatorSummary());
							Evalusers.setSummaryDate(users.getSummaryDate());
						}
					}
				}
			}
		}
		// Sending notification to lead evaluator
		try {
			if (dbEnvelop.getLeadEvaluater() != null && envelop.getLeadEvaluater() != null && dbEnvelop.getOpener() != null) {
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
			// envelop.getOpener().getName() + "." + "<br/>";
			// LOG.info("sendOpenerInviteMail === sendOpenerRemoveMail");
			// }
			// }

			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
				for (RfqEvaluatorUser user : envelop.getEvaluators()) {
					boolean newUsers = true;
					for (RfqEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							newUsers = false;
							break;
						}
					}
					if (newUsers) {
						isEvaluatorEdit = true;
						LOG.info("Evaluator Added");
						addEvalUsers.add(user.getUser());
					}
				}

				for (RfqEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					boolean oldUsers = true;
					for (RfqEvaluatorUser user : envelop.getEvaluators()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							oldUsers = false;
							break;
						}
					}
					if (oldUsers) {
						isEvaluatorEdit = true;
						LOG.info("Evaluator Removed");
						removeEvalUsers.add(dbUser.getUser());
					}
				}

			} else if (CollectionUtil.isEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
				for (RfqEvaluatorUser user : envelop.getEvaluators()) {
					isEvaluatorEdit = true;
					addEvalUsers.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators()) && CollectionUtil.isEmpty(envelop.getEvaluators())) {
				for (RfqEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
					isEvaluatorEdit = true;
					removeEvalUsers.add(dbUser.getUser());
				}
			}

			// Openers
			if (EnvelopType.OPEN == dbEnvelop.getEnvelopType() && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfqEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					addOpeners.add(user.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && EnvelopType.OPEN == envelop.getEnvelopType()) {
				for (RfqEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					removeOpeners.add(dbUser.getUser());
				}
			} else if (CollectionUtil.isNotEmpty(dbEnvelop.getOpenerUsers()) && CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
				for (RfqEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
					boolean newUsers = true;
					for (RfqEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
						if (user.getUser().getId().equals(dbUser.getUser().getId())) {
							newUsers = false;
							break;
						}
					}
					if (newUsers) {
						addOpeners.add(user.getUser());
					}
				}

				for (RfqEnvelopeOpenerUser dbUser : dbEnvelop.getOpenerUsers()) {
					boolean oldUsers = true;
					for (RfqEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
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
			for (RfqEvaluatorUser user : envelop.getEvaluators()) {
				user.setEnvelope(dbEnvelop);
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfqEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
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
		// for (RfqEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
		// evaluatorEditRemoved += (i == 0 ? " to " : "");
		// dbUser.getEvalUser().getName();
		// evaluatorEditRemoved += dbUser.getEvalUser().getName() + ",";
		// i++;
		// }
		// LOG.info("evaluator updated");
		// evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1)).concat(".");
		// }

		String timeZone = "GMT+8:00";
		RfqEvent event = dbEnvelop.getRfxEvent();
		if (sendLeadMail) {
			try {
				String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
				timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getLeadEvaluater(), timeZone);
				sendLeadEvaluatorInvitedNotification(dbEnvelop.getLeadEvaluater(), event, url, timeZone, RfxTypes.RFQ.getValue());
				sendLeadEvaluatorRemovedNotification(dbleadEvaluator, event, url, timeZone, RfxTypes.RFQ.getValue());
			} catch (Exception e) {
				LOG.error("Error While sending mail to lead evaluator :" + e.getMessage(), e);
			}
		}

		// if (sendOpenerInviteMail && dbEnvelop.getOpener() != null) {
		// try {
		// String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
		// timeZone = getTimeZoneByBuyerSettings(dbEnvelop.getOpener(), timeZone);
		// sendOpenerInvitedNotification(dbEnvelop.getOpener(), event, url, timeZone, RfxTypes.RFQ.getValue());
		// } catch (Exception e) {
		// LOG.error("Error While sending mail to invite opener :" + e.getMessage(), e);
		// }
		// }
		//
		// if (sendOpenerRemoveMail && dbOpener != null) {
		// try {
		// String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
		// timeZone = getTimeZoneByBuyerSettings(dbOpener, timeZone);
		// sendOpenerRemovedNotification(dbOpener, event, url, timeZone, RfxTypes.RFQ.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorInvitedNotification(user, event, url, timeZone, RfxTypes.RFQ.getValue());
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
					user = userDao.findUserById(user.getId());
					evaluatorEditRemoved += user.getName() + ",";
					LOG.info("removeEvalUsers user.getId() :" + user.getId());
					String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendEvaluatorRemovedNotification(user, event, url, timeZone, RfxTypes.RFQ.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerInvitedNotification(user, event, url, timeZone, RfxTypes.RFQ.getValue());
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
					String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
					timeZone = getTimeZoneByBuyerSettings(user, timeZone);
					sendOpenerRemovedNotification(user, event, url, timeZone, RfxTypes.RFQ.getValue());
				}
				evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
				evaluatorEditRemoved += " has been removed as Envelope Opener.<br/>";
			} catch (Exception e) {
				LOG.error("Error While sending mail to remove evaluator :" + e.getMessage(), e);
			}
		}

		Boolean isAllOpen = true;
		Boolean isAllClose = true;

		List<RfqEnvelopeOpenerUser> openersUserList = dbEnvelop.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(openersUserList)) {
			for (RfqEnvelopeOpenerUser opener : openersUserList) {
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

		dbEnvelop = rfqEnvelopDao.update(dbEnvelop);
		// if (isEvaluatorEdit) {
		// evaluatorEditRemoved += "Evaluator changed";
		// int i = 0;
		// for (RfqEvaluatorUser dbUser : dbEnvelop.getEvaluators()) {
		// evaluatorEditRemoved += (i == 0 ? " to " : "");
		// evaluatorEditRemoved += dbUser.getUser().getName() + ",";
		// i++;
		// }
		// LOG.info("evaluator updated");
		// evaluatorEditRemoved = evaluatorEditRemoved.substring(0, (evaluatorEditRemoved.length() - 1));
		// }

		RfqEventAudit audit = new RfqEventAudit();
		audit.setAction(AuditActionType.Update);
		audit.setEvent(event);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setActionDate(new Date());
		description += evaluatorEditRemoved;
		audit.setDescription(description);
		eventAuditService.save(audit);

		try {
			RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, description + "'for Event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
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
		RfqEnvelop dbEnvelop = rfqEnvelopDao.findById(envelopId);
		RfqEvent event = dbEnvelop.getRfxEvent();
		User leadEvaluator = dbEnvelop.getLeadEvaluater();
		String timeZone = "GMT+8:00";
		String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
		timeZone = getTimeZoneByBuyerSettings(logedUser, timeZone);
		User eventOwner = rfqEventService.getPlainEventOwnerByEventId(event.getId());
		String msg = "";
		if (dbEnvelop != null && leadEvaluator.getId().equals(logedUser.getId())) {

			if (StringUtils.checkString(dbEnvelop.getLeadEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}

			Boolean allDone = Boolean.TRUE;
			if (CollectionUtil.isNotEmpty(dbEnvelop.getEvaluators())) {
				for (RfqEvaluatorUser evaluator : dbEnvelop.getEvaluators()) {
					if (evaluator.getEvaluationStatus() == EvaluationStatus.PENDING) {
						allDone = Boolean.FALSE;
						break;
					}
				}
			}

			if (Boolean.TRUE == allDone) {
				dbEnvelop.setEvaluationDate(new Date());
				dbEnvelop.setEvaluationStatus(EvaluationStatus.COMPLETE);
				dbEnvelop = rfqEnvelopDao.update(dbEnvelop);
			}

			try {
				// sending notification completed evaluation of envelope to lead
				// Evaluator
				msg = "You have complete evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
				// sending notification completed evaluation of envelope to
				// Event Owner
				msg = "\"" + leadEvaluator.getName() + "\" has completed evaluation of envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
				sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
				try {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setAction(AuditActionType.Evaluate);
					audit.setActionBy(leadEvaluator);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + "' is evaluated");
					rfqEventAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RfqEvent rfqEvent = rfqEventService.getPlainEventById(event.getId());
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.EVALUATE, "Envelope '" + dbEnvelop.getEnvelopTitle() + "' is Evaluated for Event '" + rfqEvent.getEventId() + "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

			} catch (Exception e) {
				LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
			}
			// set Event Status
			int count = rfqEnvelopDao.findCountPendingEnvelopse(event.getId());
			if (count == 0) {
				event.setStatus(EventStatus.COMPLETE);

				Supplier winningSupplier = rfqEventDao.findWinnerSupplier(event.getId());
				if (winningSupplier != null) {
					event.setWinningSupplier(winningSupplier);
					event.setWinningPrice(winningSupplier.getTotalAfterTax());
				}
				event = rfqEventDao.update(event);
				try {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Evaluation is completed");
					audit.setAction(AuditActionType.Complete);
					rfqEventAuditDao.save(audit);

					RfqEvent rfqEvent = rfqEventService.getPlainEventById(event.getId());
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Evaluation is completed for Event '" + rfqEvent.getEventId() + "' ", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					// sending notification completed evaluation of envelope to
					// Event Owner
					msg = "The evaluation for the event \"" + event.getReferanceNumber() + "\" has been completed";
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
					// sending notification completed evaluation of envelope to
					// buyer team members
					List<User> buyerMembers = rfqEventDao.getUserBuyerTeamMemberByEventId(event.getId());
					if (CollectionUtil.isNotEmpty(buyerMembers)) {
						for (User buyerTeamUser : buyerMembers) {
							sendEnvelopCompletedNotification(buyerTeamUser, event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
						}
					}

					// Send notification to unMasking User on all envelops evaluation completed
					if (Boolean.FALSE == event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
						url = APP_URL + "/buyer/RFQ/envelopList/" + event.getId();
						msg = "You are assigned as Unmasking Owner for Event: \"" + event.getReferanceNumber() + "\"";
						for (RfqUnMaskedUser um : event.getUnMaskedUsers()) {
							sendEnvelopCompletedNotificationToUnMaskingUser(um.getUser(), event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
						}
					}

				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}

				tatReportService.updateTatReportEvaluationCompleted(event.getId(), logedUser.getTenantId(), new Date(), event.getStatus());

			}
		} else {
			RfqEvaluatorUser evaluatorUser = rfqEnvelopDao.getRfqEvaluatorUserByUserIdAndEnvelopeId(envelopId, logedUser.getId());

			if (StringUtils.checkString(evaluatorUser.getEvaluatorSummary()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			}
			if (evaluatorUser != null) {
				evaluatorUser.setEvaluationDate(new Date());
				evaluatorUser.setEvaluationStatus(EvaluationStatus.COMPLETE);
				updateEvaluatorUser(evaluatorUser);
				try {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setAction(AuditActionType.Review);
					audit.setActionBy(logedUser);
					audit.setActionDate(new Date());
					audit.setEvent(event);
					audit.setDescription("Envelope '" + dbEnvelop.getEnvelopTitle() + " ' is reviewed");
					rfqEventAuditDao.save(audit);

					RfqEvent rfqEvent = rfqEventService.getPlainEventById(event.getId());
					BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.REVIEW, "Envelope '" + dbEnvelop.getEnvelopTitle() + "' is reviewed for Event '" + rfqEvent.getEventId() + "'", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(ownerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					// sending notification to review on envelope to loged user
					msg = "You have completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(logedUser, event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
					// sending notification to review on envelope to lead
					// Evaluator
					msg = "\"" + logedUser.getName() + "\" has completed the review for the envelope \"" + dbEnvelop.getEnvelopTitle() + "\"";
					sendEnvelopCompletedNotification(leadEvaluator, event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
					// sending notification to review on envelope to Event Owner
					sendEnvelopCompletedNotification(eventOwner, event, url, timeZone, RfxTypes.RFQ.getValue(), msg);
				} catch (Exception e) {
					LOG.error("Error While Sending notification for complete evaluation : " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteEnvelop(RfqEnvelop rfqEnvelop) {
		rfqEnvelopDao.delete(rfqEnvelop);
	}

	@Override
	public List<RfqEnvelop> getAllEnvelop() {
		return rfqEnvelopDao.findAll(RfqEnvelop.class);
	}

	@Override
	public boolean isExists(RfqEnvelop rfqEnvelop, String eventId) {
		return rfqEnvelopDao.isExists(rfqEnvelop, eventId);
	}

	@Override
	public RfqEnvelop getEnvelopById(String id) {
		RfqEnvelop envelop = rfqEnvelopDao.findById(id);
		if (envelop == null)
			return null;
		if (envelop.getRfxEvent() != null) {
			envelop.getRfxEvent().getEventName();
		}
		if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
			for (RfqEventBq bq : envelop.getBqList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
			for (RfqEventSor bq : envelop.getSorList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
			for (RfqEvaluatorUser evalUser : envelop.getEvaluators()) {
				evalUser.getUser().getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
			for (RfqCq cq : envelop.getCqList()) {
				cq.getName();
			}
		}
		if (envelop.getOpener() != null) {
			envelop.getOpener().getName();
		}
		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfqEnvelopeOpenerUser openerUser : envelop.getOpenerUsers()) {
				openerUser.getUser().getName();
			}
		}
		return envelop;
	}

	@Override
	public RfqEnvelop getEnvelopForEvaluationById(String id, User logedUser) {
		LOG.info("*****");
		RfqEnvelop envelop = rfqEnvelopDao.findById(id);
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
						for (RfqEvaluatorUser evaluator : envelop.getEvaluators()) {
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
	public List<RfqEnvelop> getAllEnvelopByEventId(String eventId, User logedUser) {
		List<RfqEnvelop> envList = rfqEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFQ);
		if (CollectionUtil.isNotEmpty(envList)) {
			for (RfqEnvelop envelop : envList) {
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RfqEventBq bq : envelop.getBqList()) {
						bq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfqCq cq : envelop.getCqList()) {
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
					for (RfqEvaluatorUser evaluator : envelop.getEvaluators()) {
						evaluator.getUser().getName();
						if (envelop.getIsOpen() && evaluator.getUser() != null && evaluator.getUser().getId().equals(logedUser.getId())) {
							envelop.setShowView(true);
						}
					}
				}

				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfqEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
						if (!envelop.getIsOpen() && opener.getUser().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
							envelop.setShowOpen(true);
						}
						if (envelop.getIsOpen() && opener.getUser().getId().equals(logedUser.getId()) && envelop.getRfxEvent().getStatus() == EventStatus.CLOSED) {
							envelop.setShowOpen(true);
						}
					}
				}
				// }
				LOG.info("===============================================================================");
			}
		}
		return envList;
	}

	@Override
	public List<RfqEnvelop> getAllEnvelopByEventId(String eventId) {
		List<RfqEnvelop> envList = rfqEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFQ);
		// Here another call because we want to retreive the SOr List also , as hibernate does n't support to extract multiple list retreival
		List<RfqEnvelop> envList2 = rfqEnvelopDao.getAllEnvelopSorByEventId(eventId, RfxTypes.RFQ);

		setSorInBqData(envList, envList2);

		if (CollectionUtil.isNotEmpty(envList)) {
			for (RfqEnvelop rfqEnvelop : envList) {
				if (CollectionUtil.isNotEmpty(rfqEnvelop.getBqList())) {
					for (RfqEventBq bq : rfqEnvelop.getBqList()) {
						bq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(rfqEnvelop.getCqList())) {
					for (RfqCq cq : rfqEnvelop.getCqList()) {
						cq.getName();
					}
				}
				if (CollectionUtil.isNotEmpty(rfqEnvelop.getEvaluators())) {
					for (RfqEvaluatorUser evaluators : rfqEnvelop.getEvaluators()) {
						evaluators.getUser().getName();
					}
				}
				if (CollectionUtil.isNotEmpty(rfqEnvelop.getOpenerUsers())) {
					for (RfqEnvelopeOpenerUser opener : rfqEnvelop.getOpenerUsers()) {
						opener.getUser().getName();
					}
				}
			}

		}
		return envList;
	}

	@Override
	public List<String> getBqByEnvelopId(List<String> envelopId) {
		return rfqEnvelopDao.getBqsByEnvelopId(envelopId);
	}

	@Override
	public List<RfqEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId) {
		return rfqEnvelopDao.findEvaluatorsByEnvelopId(envelopId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfqEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException {
		LOG.info("RfqEnvelopeService Impl  addEvaluator: envelopeId " + envelopeId + " userId: " + userId);

		RfqEnvelop rfqEnvelop = getEnvelopById(envelopeId);
		if (userId.equals(rfqEnvelop.getLeadEvaluater().getId())) {
			throw new ApplicationException(messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
		}

		LOG.info(rfqEnvelop.getId());
		List<RfqEvaluatorUser> evaluators = rfqEnvelop.getEvaluators();
		if (evaluators == null) {
			evaluators = new ArrayList<RfqEvaluatorUser>();
		}
		RfqEvaluatorUser rfqEvaluatorUser = new RfqEvaluatorUser();
		rfqEvaluatorUser.setEnvelope(rfqEnvelop);
		User user = userService.getUsersById(userId);

		try {
			rfqEvaluatorUser.setUser((User) user.clone());
		} catch (Exception e) {
			LOG.error("Error :  " + e.getMessage(), e);
		}

		evaluators.add(rfqEvaluatorUser);

		rfqEnvelop.setEvaluators(evaluators);
		rfqEnvelopDao.saveOrUpdate(rfqEnvelop);
		return evaluators;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEvaluator(String eventId, String envelopeId, String userId) {
		LOG.info("RfqEnvelopeService Impl  removeEvaluator: envelopeId " + envelopeId + " userId: " + userId);
		RfqEnvelop rfqEnvelop = getEnvelopById(envelopeId);
		LOG.info(rfqEnvelop.getId());
		List<RfqEvaluatorUser> rfqEvaluatorUser = rfqEnvelop.getEvaluators();
		if (rfqEvaluatorUser == null) {
			rfqEvaluatorUser = new ArrayList<RfqEvaluatorUser>();
		}
		LOG.info("rfqEvaluatorUser.size() :" + rfqEvaluatorUser.size());
		RfqEvaluatorUser dbEvaluatorUser = getRfqEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
		LOG.info(dbEvaluatorUser.getUser().getName());
		rfqEvaluatorUser.remove(dbEvaluatorUser);
		dbEvaluatorUser.setEnvelope(rfqEnvelop);
		rfqEnvelop.setEvaluators(rfqEvaluatorUser);
		rfqEnvelopDao.update(rfqEnvelop);
		LOG.info(" rfqEnvelop.getEvaluators() :" + rfqEnvelop.getEvaluators().size());
		List<User> userList = new ArrayList<User>();
		try {
			for (RfqEvaluatorUser rfqeval : rfqEnvelop.getEvaluators()) {
				userList.add((User) rfqeval.getUser().clone());
			}
			LOG.info(userList.size() + " Event ID :" + eventId);
		} catch (Exception e) {
			LOG.error("Error constructing list of users after remove operation : " + e.getMessage(), e);
		}
		return userList;

	}

	@Override
	public RfqEvaluatorUser getRfqEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId) {
		return rfqEnvelopDao.getRfqEvaluatorUserByUserIdAndEnvelopeId(envelopeId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvaluatorUser updateEvaluatorUser(RfqEvaluatorUser evaluatorUser) {
		return evaluatorUserDao.update(evaluatorUser);
	}

	@Override
	@Transactional(readOnly = false)
	public void openEnvelop(RfqEnvelop envelop) {
		rfqEnvelopDao.update(envelop);
	}

	@Override
	@Transactional(readOnly = true)
	public RfqEvaluatorUser findEvaluatorUser(String envelopId, String userId) {
		return evaluatorUserDao.findEvaluatorUser(envelopId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException {
		RfqEnvelop envlope = rfqEnvelopDao.findById(envelopeId);
		RfqEvent event = rfqEventDao.getPlainEventById(eventId);
		boolean isBqAvailble = false;
		boolean isCqAvailble = false;
		boolean isSorAvailable = false;
		int count = 1;
		try {
			if (envlope != null && CollectionUtil.isNotEmpty(envlope.getBqList())) {
				isBqAvailble = true;
			}
			if (envlope != null && CollectionUtil.isNotEmpty(envlope.getCqList())) {
				isCqAvailble = true;
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
		LOG.info("parentFolder " + parentFolder);
		// List<EventSupplier> supplierList = rfqEventSupplierDao.getAllSuppliersByEventId(eventId);
		List<EventSupplierPojo> supplierList = rfqEventSupplierDao.getSubmitedSuppliers(eventId);
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
						for (RfqCq cq : envlope.getCqList()) {
							String attachmentFolder = "";
							if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + MaskUtils.maskName(envlope.getPreFix(), supplier.getId(), envlope.getId()).replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;
							} else {
								attachmentFolder = supplierFolder + "Attachments" + Global.PATH_SEPARATOR + supplier.getCompanyName().replaceAll(" ", "_") + " - " + cq.getName() + Global.PATH_SEPARATOR;
							}
							// List<RfqSupplierCqItem> supplierCqItems =
							// rfqSupplierCqItemDao.getSupplierCqItemsbySupplierId(eventId, supplier.getId(),
							// cq.getId());
							List<RfqSupplierCqItem> supplierCqItems = rfqSupplierCqItemDao.getSupplierCqItemsbySupplier(supplier.getId(), cq.getId());
							if (CollectionUtil.isNotEmpty(supplierCqItems)) {
								for (RfqSupplierCqItem supCqItem : supplierCqItems) {
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

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			LOG.info("short summary");
			JasperPrint jasperPrint = rftEventService.generateShortEvaluationSummaryReport("RFQ", eventId, envelopeId, timeZone, virtualizer);
			if (jasperPrint != null) {
				LOG.info("short summary");
				FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Short Submission Summary for " + envlope.getEnvelopTitle());
				zos.flush();
			}
			try {
				jasperPrint = rfqEventService.getEvaluationReport(eventId, envelopeId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Evaluation Report for " + envlope.getEnvelopTitle());
					zos.flush();
				}
			} catch (Exception e) {
			}

			jasperPrint = rfqEventService.generateSubmissionReport(envelopeId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				FileUtil.writePdfToZip(zos, jasperPrint, parentFolder + "/", "Submission Report for " + envlope.getEnvelopTitle());
				zos.flush();
			}

			List<EventEvaluationPojo> list = null;

			list = rfqSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, envelopeId);
			if (CollectionUtil.isNotEmpty(list)) {
				XSSFWorkbook workbookBq = new XSSFWorkbook();
				workbookBq = rfaEventService.buildBqComparisionFile(workbookBq, list, null, eventId, envelopeId, RfxTypes.RFQ);
				if (workbookBq != null) {
					FileUtil.writeXssfExcelToZip(zos, workbookBq, parentFolder + "/", "BQ Comparison Table.xlsx");
					zos.flush();
				}

			}

			XSSFWorkbook workbookCq = new XSSFWorkbook();
			list = rfqSupplierCqItemService.getEvaluationDataForCqComparison(eventId, envelopeId);
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

	private void bqExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RfqEvent event, EventSupplierPojo supplier, String supplierFolder, int supplierNo) throws IOException {
		// List<BqPojo> bqPojos = rfqEnvelopDao.getBqNameAndIdsByEnvelopId(Arrays.asList(envelopeId));
		List<BqPojo> bqPojos = rfqEnvelopDao.getBqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		RfqEnvelop envelope = getEnvelopById(envelopeId);
		if (CollectionUtil.isNotEmpty(bqPojos)) {

			// For Financial Standard
			/*
			 * DecimalFormat df = null; if (event.getDecimal().equals("1")) { df = new DecimalFormat("#,###,###,##0.0");
			 * } else if (event.getDecimal().equals("2")) { df = new DecimalFormat("#,###,###,##0.00"); } else if
			 * (event.getDecimal().equals("3")) { df = new DecimalFormat("#,###,###,##0.000"); } else if
			 * (event.getDecimal().equals("4")) { df = new DecimalFormat("#,###,###,##0.0000"); }
			 */

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
					// cell.setCellStyle(blueRightBoldStyle);
					/* cell.setCellStyle(styleHeading); */
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
				List<RfqSupplierBqItem> bqItems = rfqSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int decimal = 2;
					// Write Data into Excel
					int bqItemLoopCount = 1;

					for (RfqSupplierBqItem item : bqItems) {

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

							for (RfqSupplierBqItem child : item.getChildren()) {

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

								getMethodBuyerSupplierExtraColumnData(item, row, cellNum, child);

								// sheet.addMergedRegion(new CellRangeAddress(0, // first
								// // row
								// // (0-based)
								// 0, // last row (0-based)
								// 5, // first column (0-based)
								// 8 // last column (0-based)
								// ));
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
						// cellSname.setCellValue(supplier.getSupplier().getCompanyName());
						/* cellSname.setCellStyle(styleHeading); */

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
							// cell.setCellStyle(styleHeading);
							XSSFCellStyle myCellStyle = workbook.createCellStyle();
							myCellStyle.setFont(font);
							myCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
							myCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							cell.setCellStyle(myCellStyle);
						}
					}

					RfqSupplierBq supplierBq = rfqSupplierBqDao.findBqByBqId(bqPojo.getId(), supplier.getId());
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
				for (int k = 0; k < 9; k++) {
					sheet.autoSizeColumn(k, true);
				}
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					FileUtil.writeXssfExcelToZip(zos, workbook, supplierFolder, MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()).replaceAll(" ", "_") + "-" + bqPojo.getBqName() + ".xlsx");
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


	private void sorExportExcelForEachSupplier(String envelopeId, ZipOutputStream zos, RfqEvent event, EventSupplierPojo supplier, String supplierFolder, int supplierNo) throws IOException {
		List<SorPojo> bqPojos = rfqEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
		RfqEnvelop envelope = getEnvelopById(envelopeId);
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
				List<RfqSupplierSorItem> bqItems = rfqSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bqPojo.getId(), supplier.getId());
				if (CollectionUtil.isNotEmpty(bqItems)) {
					int r = 2;
					int bqItemLoopCount = 1;

					for (RfqSupplierSorItem item : bqItems) {

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

							for (RfqSupplierSorItem child : item.getChildren()) {
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

	private void getMethodBuyerSupplierExtraColumnData(RfqSupplierBqItem item, Row row, int cellNum, RfqSupplierBqItem child) {
		if (StringUtils.checkString(item.getBq().getField1Label()).length() > 0) {
			if (item.getBq().getField1FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField1());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField1());
			}
		}
		if (StringUtils.checkString(item.getBq().getField2Label()).length() > 0) {
			if (item.getBq().getField2FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField2());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField2());
			}
		}
		if (StringUtils.checkString(item.getBq().getField3Label()).length() > 0) {
			if (item.getBq().getField3FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField3());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField3());
			}
		}
		if (StringUtils.checkString(item.getBq().getField4Label()).length() > 0) {
			if (item.getBq().getField4FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField4());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField4());
			}
		}
		if (StringUtils.checkString(item.getBq().getField5Label()).length() > 0) {
			if (item.getBq().getField5FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField5());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField5());
			}
		}
		if (StringUtils.checkString(item.getBq().getField6Label()).length() > 0) {
			if (item.getBq().getField6FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField6());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField6());
			}
		}
		if (StringUtils.checkString(item.getBq().getField7Label()).length() > 0) {
			if (item.getBq().getField7FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField7());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField7());
			}
		}
		if (StringUtils.checkString(item.getBq().getField8Label()).length() > 0) {
			if (item.getBq().getField8FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField8());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField8());
			}
		}
		if (StringUtils.checkString(item.getBq().getField9Label()).length() > 0) {
			if (item.getBq().getField9FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField9());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField9());
			}
		}
		if (StringUtils.checkString(item.getBq().getField10Label()).length() > 0) {
			if (item.getBq().getField10FilledBy() == BqUserTypes.BUYER) {
				row.createCell(cellNum++).setCellValue(child.getBqItem().getField10());

			} else {
				row.createCell(cellNum++).setCellValue(child.getField10());
			}
		}
	}

	@Override
	public JasperPrint generateSupplierBqPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationBqPojo> bqSummary = new ArrayList<EvaluationBqPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			LOG.info("Generating RFP Supplier BQ Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			// List<String> bqList = rfqEnvelopDao.getBqsByEnvelopId(Arrays.asList(envelopeId));
			List<BqPojo> bqList = rfqEnvelopDao.getBqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierBqs.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (BqPojo bqPojo : bqList) {
					EvaluationBqPojo bq = new EvaluationBqPojo();
					String bqId = bqPojo.getId();
					RfqEventBq bqDetail = rfqEventBqDao.findById(bqId);
					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRfxEnvelop())) {
						for (RfqEnvelop env : bqDetail.getRfxEvent().getRfxEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					List<RfqSupplierBqItem> supBqItem = rfqSupplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bqId, supplierId);
					RfqSupplierBq supplierBq = rfqSupplierBqDao.findBqByBqId(bqId, supplierId);
					BigDecimal additionalTax = null, grandTotal = null, totalAfterTax = null;
					String additionalTaxDesc = "";
					if (supplierBq != null) {
						additionalTax = supplierBq.getAdditionalTax();
						additionalTaxDesc = supplierBq.getTaxDescription();
						grandTotal = supplierBq.getGrandTotal();
						totalAfterTax = supplierBq.getTotalAfterTax();
					}
					List<EvaluationBqItemPojo> bqItems = new ArrayList<EvaluationBqItemPojo>();
					for (RfqSupplierBqItem item : supBqItem) {
						EvaluationBqItemPojo bqItem = new EvaluationBqItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItem.setAdditionalTax(item.getAdditionalTax());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RfqSupplierBqItem childBqItem : item.getChildren()) {
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
			RfqEvent event = rfqEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
			RfqEnvelop rfqEnvelop = getEnvelopById(envelopeId);
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
			LOG.error("Error generating Supplier BQ PDF for envelope : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer) {
		List<EvaluationSorPojo> bqSummary = new ArrayList<EvaluationSorPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;
		try {
			LOG.info("Generating RFQ Supplier SOR Envelope PDF for supplier : " + supplierId + " Envelope Id : " + envelopeId);

			List<SorPojo> bqList = rfqEnvelopDao.getSorsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierSors.jasper");
			File jasperfile = resource.getFile();
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (SorPojo bqPojo : bqList) {
					EvaluationSorPojo bq = new EvaluationSorPojo();
					String bqId = bqPojo.getId();
					RfqEventSor bqDetail = rfqEventSorDao.findById(bqId);
					bq.setName(bqDetail.getName());
					String title = "";
					if (CollectionUtil.isNotEmpty(bqDetail.getRfxEvent().getRfxEnvelop())) {
						for (RfqEnvelop env : bqDetail.getRfxEvent().getRfxEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					bq.setTitle(title);
					List<RfqSupplierSorItem> supBqItem = rfqSupplierSorItemDao.findSupplierSorItemByListBySorIdAndSupplierId(bqId, supplierId);
					List<EvaluationSorItemPojo> bqItems = new ArrayList<EvaluationSorItemPojo>();
					for (RfqSupplierSorItem item : supBqItem) {
						EvaluationSorItemPojo bqItem = new EvaluationSorItemPojo();
						bqItem.setLevel(item.getLevel() + "." + item.getOrder());
						bqItem.setDescription(item.getItemName());
						bqItems.add(bqItem);

						if (item.getChildren() != null) {
							for (RfqSupplierSorItem childBqItem : item.getChildren()) {
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
			RfqEvent event = rfqEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);
			RfqEnvelop rfqEnvelop = getEnvelopById(envelopeId);
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
	public JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int i) {
		List<EvaluationCqPojo> cqSummary = new ArrayList<EvaluationCqPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		RfqEnvelop envelope = getEnvelopById(envelopeId);
		JasperPrint jasperPrint = null;
		try {
			Resource resource = applicationContext.getResource("classpath:reports/EvaluationSupplierCqs.jasper");
			File jasperfile = resource.getFile();
			// List<String> cqList = rfqEnvelopDao.getCqsByEnvelopId(Arrays.asList(envelopeId));
			List<CqPojo> cqList = rfqEnvelopDao.getCqsIdListByEnvelopIdByOrder(Arrays.asList(envelopeId));

			if (CollectionUtil.isNotEmpty(cqList)) {
				for (CqPojo cqPojo : cqList) {
					EvaluationCqPojo cq = new EvaluationCqPojo();
					String cqId = cqPojo.getId();
					RfqCq cqDetail = rfqCqDao.findById(cqId);
					String title = "";
					if (CollectionUtil.isNotEmpty(cqDetail.getRfxEvent().getRfxEnvelop())) {
						for (RfqEnvelop env : cqDetail.getRfxEvent().getRfxEnvelop()) {
							title = env.getEnvelopTitle();
						}
					}
					cq.setTitle(title);
					cq.setName(cqDetail.getName());
					// List<RfqSupplierCqItem> supCqItem = rfqSupplierCqItemDao.findSupplierCqItemListByCqId(cqId,
					// supplierId);
					List<RfqSupplierCqItem> supCqItem = rfqSupplierCqItemDao.findSupplierCqItemByCqIdandSupplierId(cqId, supplierId);
					List<EvaluationCqItemPojo> cqItems = new ArrayList<EvaluationCqItemPojo>();
					for (RfqSupplierCqItem item : supCqItem) {
						List<RfqSupplierCqOption> listAnswers = rfqSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
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
								for (RfqSupplierCqOption cqOption : listAnswers) {
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
							for (RfqSupplierCqOption cqOption : listAnswers) {
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

			RfqEvent event = rfqEnvelopDao.getEventbyEnvelopeId(envelopeId);
			Supplier supplier = supplierService.findSuppById(supplierId);

			String timeZone = "GMT+8:00";
			// String msg = "";
			timeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), timeZone);

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
		RfqEnvelop envlope = getEnvelopById(evenvelopId);

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
			RfqEvent event = rfqEventService.getRfqEventByeventId(eventId);
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
						for (RfqCq cq : envlope.getCqList()) {
							EvaluationCqPojo item = new EvaluationCqPojo();
							item.setName(cq.getName());
							item.setDescription(cq.getDescription());
							cqs.add(item);
						}
					}
					eventDetails.setCqs(cqs);
					List<EvaluationBqPojo> bqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envlope.getBqList())) {
						for (RfqEventBq item : envlope.getBqList()) {
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
			LOG.error("Could not generate RFQ Evaluation Submission Summary PDF Report. " + e.getMessage(), e);
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
		List<EventSupplier> suppliers = rfqEventSupplierDao.getAllSuppliersByEventId(eventId);
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

				List<RfqCq> suppCqItem = null;
				List<RfqSupplierBq> suppBqItem = null;
				if (item.getSupplier() != null) {
					suppCqItem = rfqSupplierCqItemDao.getAllCqsBySupplierId(eventId, item.getSupplier().getId());
					suppBqItem = rfqSupplierBqItemDao.getAllBqsBySupplierId(eventId, item.getSupplier().getId());
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
					for (RfqCq cqItem : suppCqItem) {
						EvaluationCqPojo cqList = new EvaluationCqPojo();
						cqList.setName(cqItem.getName());
						cqs.add(cqList);
					}
				}
				if (CollectionUtil.isNotEmpty(suppBqItem)) {
					for (RfqSupplierBq bqItem : suppBqItem) {
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
	public Integer getAllEnvelopCountByEventId(String eventId) {
		return rfqEnvelopDao.getAllEnvelopCountByEventId(eventId);
	}

	@Override
	public int getcountClosedEnvelop(String eventId) {
		return rfqEnvelopDao.getcountClosedEnvelop(eventId);
	}

	@Override
	public List<RfqEnvelop> getAllClosedEnvelopAndOpener(String eventId) {
		return rfqEnvelopDao.getAllClosedEnvelopAndOpener(eventId);
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFQ)));
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
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), RfxTypes.RFQ)));
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
		case RFQ:
			displayName = rfqEventDao.findBusinessUnitName(eventId);
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

	private void setSorInBqData(List<RfqEnvelop> envList, List<RfqEnvelop> envList2) {
		Map<String, RfqEnvelop> envelopMap = envList2.stream().collect(Collectors.toMap(RfqEnvelop::getId, Function.identity()));
		envList.forEach(event -> {
			RfqEnvelop matchingEvent = envelopMap.get(event.getId());
			if(matchingEvent != null) {
				event.setSorList(matchingEvent.getSorList());
			}
		});
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
		RfqEnvelop envelop = rfqEnvelopDao.findById(envelopeId);
		if (envelop.getOpener() != null) {
			if (enc.matches(key, envelop.getOpener().getPassword())) {
				envelop.setIsOpen(Boolean.TRUE);
				envelop.setOpenDate(new Date());
				envelop = rfqEnvelopDao.update(envelop);
				success = envelop.getIsOpen();
				LOG.info("updated successfully.....");
				try {
					super.sendEnvelopOpenNotification(envelop, RfxTypes.RFQ, eventId, loggedInUser, true);
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


	private void setBuyerSupplierHeadingsForSor(List<String> buyerHeading, List<String> supplierHeading, RfqSorItem bqItem) {
		System.out.println(bqItem.getSor());
		if (bqItem.getSor().getField1Label() != null && StringUtils.checkString(bqItem.getSor().getField1Label()).length() > 0) {
			if (bqItem.getSor().getField1FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getSor().getField1Label());
			} else {
				supplierHeading.add(bqItem.getSor().getField1Label());
			}
		}
	}


	private void setBuyerSupplierHeadings(List<String> buyerHeading, List<String> supplierHeading, RfqBqItem bqItem) {
		if (StringUtils.checkString(bqItem.getBq().getField1Label()).length() > 0) {
			if (bqItem.getBq().getField1FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField1Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField1Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField2Label()).length() > 0) {
			if (bqItem.getBq().getField2FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField2Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField2Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField3Label()).length() > 0) {
			if (bqItem.getBq().getField3FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField3Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField3Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField4Label()).length() > 0) {
			if (bqItem.getBq().getField4FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField4Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField4Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField5Label()).length() > 0) {
			if (bqItem.getBq().getField5FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField5Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField5Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField6Label()).length() > 0) {
			if (bqItem.getBq().getField6FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField6Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField6Label());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField7Label()).length() > 0) {
			if (bqItem.getBq().getField7FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField7Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField7Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField8Label()).length() > 0) {
			if (bqItem.getBq().getField8FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField8Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField8Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField9Label()).length() > 0) {
			if (bqItem.getBq().getField9FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField9Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField9Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField10Label()).length() > 0) {
			if (bqItem.getBq().getField10FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField10Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField10Label());
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void updateLeadEvaluatorSummary(RfqEnvelop envelop) {
		rfqEnvelopDao.update(envelop);
	}

	@Override
	public List<RfqEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loginUser) {
		return rfqEvaluatorUserDao.getEvaluationSummaryRemarks(evelopId, loginUser);
	}

	@Override
	public RfqEnvelop getRfqEnvelopById(String evenvelopId) {

		RfqEnvelop envelop = rfqEnvelopDao.findById(evenvelopId);
		if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
			for (RfqEventBq bq : envelop.getBqList()) {
				bq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
			for (RfqCq cq : envelop.getCqList()) {
				cq.getName();
			}
		}
		if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
			for (RfqEvaluatorUser evalUser : envelop.getEvaluators()) {
				evalUser.getUser().getName();
			}
		}
		if (envelop.getLeadEvaluater() != null) {
			envelop.getLeadEvaluater().getLoginId();
		}
		if (envelop.getOpener() != null) {
			envelop.getOpener().getName();
		}
		if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
			for (RfqEnvelopeOpenerUser openerUser : envelop.getOpenerUsers()) {
				openerUser.getUser().getName();
			}
		}
		return envelop;

	}

	@Override
	public boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId) {
		return rfqEnvelopDao.getCountOfAssignedSupplier(leadUserId, envelopId, eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void closeEnvelop(RfqEnvelop envelop) {
		rfqEnvelopDao.update(envelop);
	}

	@Override
	public RfqEnvelop getRfiEnvelopBySeq(int i, String eventId) {
		return rfqEnvelopDao.getRfiEnvelopBySeq(i, eventId);
	}

	@Override
	public boolean isAcceptedEvaluationDeclaration(String envelopId, String loggedInUser, String eventId) {
		return rfqEvaluatorDeclarationDao.isAcceptedEvaluationDeclaration(envelopId, loggedInUser, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvaluatorDeclaration saveEvaluatorDeclaration(RfqEvaluatorDeclaration evaluationDeclarationObj) {
		return rfqEvaluatorDeclarationDao.save(evaluationDeclarationObj);
	}

	@Override
	public RfqEnvelop getEmptyEnvelopByEventId(String eventId) {
		return rfqEnvelopDao.getEmptyEnvelopByEventId(eventId);
	}

	@Override
	public List<RfqEventBq> getBqsByEnvelopIdByOrder(String envelopId) {
		return rfqEnvelopDao.getBqsByEnvelopIdByOrder(envelopId);
	}

	@Override
	public List<RfqCq> getCqsByEnvelopIdByOrder(String envelopId) {
		return rfqEnvelopDao.getCqIdlistByEnvelopIdByOrder(envelopId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLeadEvaluatorDocument(RfqEnvelop envelop) {
		rfqEnvelopDao.update(envelop);
	}

	@Override
	public List<RfqEventSor> getSorsByEnvelopIdByOrder(String envelopId) {
		return rfqEnvelopDao.getSorsByEnvelopIdByOrder(envelopId);
	}
}
