/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceFormDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;
import com.privasia.procurehere.core.entity.SupplierPerformanceCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ErpIntegrationException;
import com.privasia.procurehere.core.pojo.ConsolidateScorePojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.ScoreCardPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.ScoreRatingService;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceFormCriteriaService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;

import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author anshul
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceFormServiceImpl implements SupplierPerformanceFormService {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceFormServiceImpl.class);

	@Autowired
	SupplierPerformanceFormDao supplierPerformanceFormDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	BuyerService buyerService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierPerformanceAuditService formAuditService;

	@Autowired
	SupplierPerformanceFormCriteriaService spFormCriteriaService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	ScoreRatingService scoreRatingService;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Override
	public SupplierPerformanceFormStatus getFormStatusByFormId(String formId) {
		return supplierPerformanceFormDao.getFormStatusByFormId(formId);
	}

	@Override
	public SupplierPerformanceForm getSupplierPerformanceFormById(String formId) {
		LOG.info("Supplier Performance Form Id................" + formId);
		SupplierPerformanceForm spForm = supplierPerformanceFormDao.getFormAndCriteriaByFormId(formId);
		if (spForm != null) {
			if (spForm.getFormOwner() != null) {
				spForm.getFormOwner().getName();
				spForm.getFormOwner().getCommunicationEmail();
			}
			if (spForm.getAwardedSupplier() != null) {
				spForm.getAwardedSupplier().getCompanyName();
			}
			if (spForm.getProcurementCategory() != null) {
				spForm.getProcurementCategory().getProcurementCategories();
			}
			if (spForm.getBusinessUnit() != null) {
				spForm.getBusinessUnit().getDisplayName();
			}
			if (spForm.getEvaluators() != null) {
				for (SupplierPerformanceEvaluatorUser user : spForm.getEvaluators()) {
					user.getEvaluator().getName();
				}
			}
			if (CollectionUtil.isNotEmpty(spForm.getCriteria())) {
				for (SupplierPerformanceFormCriteria c : spForm.getCriteria()) {
					c.getAllowToUpdateSectionWeightage();
				}
			}
			if (spForm.getTemplate() != null) {
				spForm.getTemplate().getProcurementCategoryVisible();
				spForm.getTemplate().getProcurementCategoryDisabled();
				spForm.getTemplate().getProcurementCategoryOptional();
			}

		}
		return spForm;
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceForm saveSupplierPerformanceForm(SupplierPerformanceForm form) {
		return supplierPerformanceFormDao.save(form);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = ApplicationException.class)
	public SupplierPerformanceForm updateSupplierPerformanceForm(SupplierPerformanceForm form, User updatedBy, TimeZone timeZone, String[] remindMeDays, Boolean[] reminderSent) throws ApplicationException {

		SupplierPerformanceForm spFormObj = supplierPerformanceFormDao.findById(form.getId());

		if (spFormObj.getFormStatus() != SupplierPerformanceFormStatus.SUSPENDED) {
			spFormObj.setAwardedSupplier(form.getAwardedSupplier());
			spFormObj.setProcurementCategory(form.getProcurementCategory());
		}

		Date startDateTime = null;
		if (spFormObj.getFormStatus() == SupplierPerformanceFormStatus.SUSPENDED && spFormObj.getOldFormStatus() == SupplierPerformanceFormStatus.ACTIVE) {
			startDateTime = spFormObj.getEvaluationStartDate();
		} else {
			startDateTime = DateUtil.combineDateTime(form.getEvaluationStartDate(), form.getEvaluationStartTime(), timeZone);
		}

		Date endDateTime = DateUtil.combineDateTime(form.getEvaluationEndDate(), form.getEvaluationEndTime(), timeZone);

		// Dont validate start time if the form was suspended when it was in Active status as we dont allow start date
		// to be changed in such cases
		// Only validate start time if the form status is in DRAFT status or SUSPENDED when the old status was SCHEDULED
		if (!(spFormObj.getFormStatus() == SupplierPerformanceFormStatus.SUSPENDED && spFormObj.getOldFormStatus() == SupplierPerformanceFormStatus.ACTIVE)) {
			if (startDateTime.before(new Date())) {
				throw new ApplicationException("Evaluation start date can't be in past");
			}
		}

		if (endDateTime.before(startDateTime)) {
			throw new ApplicationException("Evaluation End Date can't be before Evaluation Start Date");
		}

		String pattern = "MM/dd/yyyy HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);

		if (df.format(endDateTime).equals(df.format(startDateTime))) {
			throw new ApplicationException("Evaluation End Date can't be same as Evaluation Start Date");
		}

		// Dont update start date time if the form was ACTIVE when it was suspended
		if (!(spFormObj.getFormStatus() == SupplierPerformanceFormStatus.SUSPENDED && spFormObj.getOldFormStatus() == SupplierPerformanceFormStatus.ACTIVE)) {
			spFormObj.setEvaluationStartDate(startDateTime);
		}
		spFormObj.setEvaluationEndDate(endDateTime);
		// TODO: check if we need to compute this value at backend even if it is being sent from FE
		spFormObj.setEvaluationDuration(form.getEvaluationDuration());

		if (spFormObj.getFormStatus() != SupplierPerformanceFormStatus.SUSPENDED) {
			spFormObj.setIsRecurrenceEvaluation(form.getIsRecurrenceEvaluation());
			if (Boolean.FALSE == spFormObj.getIsRecurrenceEvaluation()) {
				spFormObj.setRecurrenceEvaluation(null);
				spFormObj.setNoOfRecurrence(null);
			} else {
				spFormObj.setRecurrenceEvaluation(form.getRecurrenceEvaluation());
				spFormObj.setNoOfRecurrence(form.getNoOfRecurrence());
			}
		}

		spFormObj.setSupDetailCompleted(true);

		List<SupplierPerformanceReminder> remindMeDaysList = new ArrayList<>();

		if (remindMeDays != null) {
			int index = 0;
			for (String remindMe : remindMeDays) {
				LOG.info("reminder days : " + remindMe);
				SupplierPerformanceReminder spEvaluationReminder = new SupplierPerformanceReminder();
				spEvaluationReminder.setForm(spFormObj);
				spEvaluationReminder.setInterval(Integer.parseInt(remindMe));
				Calendar cal = Calendar.getInstance(timeZone);
				cal.setTime(spFormObj.getEvaluationEndDate());
				cal.add(Calendar.DATE, -Integer.parseInt(remindMe));
				spEvaluationReminder.setReminderDate(cal.getTime());
				spEvaluationReminder.setReminderSent(reminderSent[index]);
				remindMeDaysList.add(spEvaluationReminder);
				index++;
			}
		}
		spFormObj.setFormReminder(remindMeDaysList);

		if (spFormObj.getFormStatus() != SupplierPerformanceFormStatus.SUSPENDED) {
			// Update the weightage where required
			BigDecimal grandTotal = BigDecimal.ZERO;
			for (SupplierPerformanceFormCriteria fc : spFormObj.getCriteria()) {
				for (SupplierPerformanceFormCriteria ffc : form.getCriteria()) {
					if (fc.getId().equals(ffc.getId()) && Boolean.TRUE == fc.getAllowToUpdateSectionWeightage() && fc.getOrder() == 0) {
						fc.setWeightage(ffc.getWeightage());
						break;
					}
				}
				if (fc.getOrder() == 0) {
					grandTotal = grandTotal.add(fc.getWeightage());
				}
			}
			grandTotal = grandTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
			LOG.info("Grand Total is : " + grandTotal);

			if (grandTotal.intValue() != 100) {
				throw new ApplicationException("Weightage total should be 100.");
			}
		}

		spFormObj = supplierPerformanceFormDao.update(spFormObj);
		LOG.info("Supplier Form Id : " + spFormObj.getId());

		return spFormObj;
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceForm updateSupplierPerformanceForm(SupplierPerformanceForm form) {
		return supplierPerformanceFormDao.saveOrUpdate(form);
	}

	@Override
	public List<String> getSPFormIdListByTemplateId(String templateId) {
		return supplierPerformanceFormDao.getSPFormIdListByTemplateId(templateId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<SupplierPerformanceEvaluatorUser> addEvaluator(String spFormId, String userId) throws ApplicationException {
		LOG.info("SP Form Service Impl  addEvaluator: FormId " + spFormId + " userId: " + userId);

		SupplierPerformanceForm form = getSupplierPerformanceFormById(spFormId);

		LOG.info(form.getId());
		List<SupplierPerformanceEvaluatorUser> evaluators = form.getEvaluators();
		if (evaluators == null) {
			evaluators = new ArrayList<SupplierPerformanceEvaluatorUser>();
		}
		SupplierPerformanceEvaluatorUser formEvaluatorUser = new SupplierPerformanceEvaluatorUser();
		formEvaluatorUser.setForm(form);
		formEvaluatorUser.setEvaluationStatus(SupperPerformanceEvaluatorStatus.DRAFT);
		formEvaluatorUser.setCreatedDate(new Date());
		formEvaluatorUser.setBuyer(form.getBuyer());
		User user = new User();
		user.setId(userId);

		try {
			formEvaluatorUser.setEvaluator(user);
		} catch (Exception e) {
			LOG.error("Error :  " + e.getMessage(), e);
		}

		evaluators.add(formEvaluatorUser);
		form.setEvaluators(evaluators);
		supplierPerformanceFormDao.saveOrUpdate(form);
		return evaluators;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEvaluator(String spFormId, String userId) {
		List<User> userList = new ArrayList<User>();
		try {

			LOG.info("SupplierPerformanceEvaluationServiceImpl  removeEvaluator: spFormId " + spFormId + " userId: " + userId);
			SupplierPerformanceForm form = getSupplierPerformanceFormById(spFormId);
			if (form.getFormStatus() == SupplierPerformanceFormStatus.SUSPENDED) {
				throw new ApplicationException("Evaluators cannot be removed when the Form is SUSPENDED");
			}
			LOG.info(form.getId());
			List<SupplierPerformanceEvaluatorUser> formEvaluatorUser = form.getEvaluators();
			if (formEvaluatorUser == null) {
				formEvaluatorUser = new ArrayList<SupplierPerformanceEvaluatorUser>();
			}

			int toRemove = -1;

			for (int i = 0; i < formEvaluatorUser.size(); i++) {
				if (formEvaluatorUser.get(i).getEvaluator().getId().equals(userId)) {
					toRemove = i;
					break;
				}
			}

			if (toRemove != -1) {
				formEvaluatorUser.remove(toRemove);
			}

			supplierPerformanceFormDao.update(form);
			LOG.info("SupplierPerformanceEvaluatorUser after removing :" + form.getEvaluators().size());
			for (SupplierPerformanceEvaluatorUser spfEval : form.getEvaluators()) {
				userList.add((User) spfEval.getEvaluator().clone());
			}
			LOG.info(userList.size() + " SpForm ID :" + spFormId);
		} catch (Exception e) {
			LOG.error("Error constructing list of users after remove operation : " + e.getMessage(), e);
		}
		return userList;
	}

	@Override
	public List<SupplierPerformanceEvaluatorUser> findEvaluatorsByFormId(String formId) {
		return supplierPerformanceFormDao.findEvaluatorsByFormId(formId);
	}

	@Override
	public List<UserPojo> findEvaluatorsUserByFormId(String formId) {
		return supplierPerformanceFormDao.findEvaluatorsUserByFormId(formId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceForm finishSupplierPerformanceForm(String formId, User user, HttpSession session) throws ApplicationException {
		SupplierPerformanceForm spForm = supplierPerformanceFormDao.findById(formId);
		SupplierPerformanceFormStatus status = spForm.getFormStatus();
		JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

		if (spForm != null) {
			if (CollectionUtil.isNotEmpty(spForm.getEvaluators())) {
				for (SupplierPerformanceEvaluatorUser evalUser : spForm.getEvaluators()) {
					evalUser.copyCriteriaDetails(evalUser, spForm);
					if (status == SupplierPerformanceFormStatus.SUSPENDED) {
						evalUser.setEvaluationStatus(evalUser.getOldEvaluationStatus() != null ? evalUser.getOldEvaluationStatus() : SupperPerformanceEvaluatorStatus.DRAFT);
					} else {
						evalUser.setEvaluationStatus(SupperPerformanceEvaluatorStatus.DRAFT);
					}
					if (CollectionUtil.isNotEmpty(evalUser.getCriteria()) && evalUser.getEvaluationStatus() == SupperPerformanceEvaluatorStatus.DRAFT) {
						SupplierPerformanceCriteria parent = null;
						for (SupplierPerformanceCriteria criteria : evalUser.getCriteria()) {
							criteria.setForm(spForm);
							criteria.setEvaluationUser(evalUser);
							if (criteria.getOrder() == 0) {
								parent = criteria;
							}
							if (criteria.getOrder() != 0) {
								criteria.setParent(parent);
							}
						}
					}
				}
			} else {
				throw new ApplicationException("Please add evaluators to perform evaluation");
			}

			// spForm.setOldFormStatus(spForm.getFormStatus());
			spForm.setFormStatus(SupplierPerformanceFormStatus.SCHEDULED);
			spForm.setSummaryCompleted(Boolean.TRUE);
			spForm = supplierPerformanceFormDao.update(spForm);

			String queue = "QUEUE.FORM.FINISH.NOTIFICATION";
			if (status == SupplierPerformanceFormStatus.SUSPENDED) {
				queue = "QUEUE.FORM.RESUME.NOTIFICATION";
				try {
					snapShotAuditService.doSupplierPerformanceFormAudit(spForm, session, user, SupplierPerformanceAuditActionType.Resume, "spForm.audit.resume", virtualizer);
				} catch (Exception e) {
					LOG.info("Error while Auditing : " + e.getMessage(), e);
				}

				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Supplier Performance Evaluation Form \"" + spForm.getFormId() + "\" Resumed.", spForm.getCreatedBy().getTenantId(), user, new Date(), ModuleType.PerformanceEvaluation);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording Finish audit " + e.getMessage(), e);
				}
			} else {
				try {
					snapShotAuditService.doSupplierPerformanceFormAudit(spForm, session, user, SupplierPerformanceAuditActionType.Finish, "spForm.audit.finished", virtualizer);
				} catch (Exception e) {
					LOG.info("Error while Auditing : " + e.getMessage(), e);
				}
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH, "Supplier Performance Evaluation Form \"" + spForm.getFormId() + "\" Finished.", spForm.getCreatedBy().getTenantId(), user, new Date(), ModuleType.PerformanceEvaluation);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording Finish audit " + e.getMessage(), e);
				}

			}

			try {
				try {
					jmsTemplate.send(queue, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(formId);
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event suspend " + e.getMessage(), e);
			}

		}

		return spForm;
	}

	@Override
	public EventTimerPojo getTimeByFormId(String formId) {
		return supplierPerformanceFormDao.getTimeByFormId(formId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String formId, SupplierPerformanceFormStatus formStatus) {
		supplierPerformanceFormDao.updateImmediately(formId, formStatus);
	}

	@Override
	public void getScoreCardList(String formId, Model model, String tenantId) {
		SupplierPerformanceForm spForm = getSupplierPerformanceFormById(formId);
		model.addAttribute("spForm", spForm);

		List<SupplierPerformanceEvaluatorUser> evaluators = spForm.getEvaluators();
		List<String> evaluatorNames = new ArrayList<String>();
		for (SupplierPerformanceEvaluatorUser user : evaluators) {
			if (SupperPerformanceEvaluatorStatus.APPROVED == user.getEvaluationStatus()) {
				evaluatorNames.add(user.getEvaluator().getName());
			}
		}
		model.addAttribute("evaluators", evaluatorNames);
		List<ScoreCardPojo> scoreCardList = new ArrayList<ScoreCardPojo>();
		List<SupplierPerformanceFormCriteria> criteriaList = spFormCriteriaService.getSPFormCriteriaByFormId(formId);
		SupplierPerformanceFormCriteria parentCriteria = null;
		ScoreCardPojo parent = null;
		BigDecimal sectionTotalAvg = BigDecimal.ZERO;
		for (SupplierPerformanceFormCriteria c : criteriaList) {
			ScoreCardPojo sc = new ScoreCardPojo();
			sc.setCriteria(c.getName());
			sc.setOrder(c.getOrder());
			sc.setLevel(c.getLevel());
			sc.setScore(new ArrayList<String>());
			scoreCardList.add(sc);
			BigDecimal avgScore = BigDecimal.ZERO;
			for (SupplierPerformanceEvaluatorUser user : evaluators) {
				if (SupperPerformanceEvaluatorStatus.APPROVED != user.getEvaluationStatus()) {
					continue;
				}
				for (SupplierPerformanceCriteria uCriteria : user.getCriteria()) {
					if (uCriteria.getLevel() == c.getLevel() && uCriteria.getOrder() == c.getOrder() && uCriteria.getEvaluatorTotalScore() != null) {
						sc.getScore().add(uCriteria.getEvaluatorTotalScore().toString());
						avgScore = avgScore.add(uCriteria.getEvaluatorTotalScore());
						break;
					}
				}
			}
			// Only compute the average if the form is not concluded yet.
			if (evaluatorNames.size() > 0 && spForm.getFormStatus() != SupplierPerformanceFormStatus.CONCLUDED) {
				avgScore = avgScore.divide(new BigDecimal(evaluatorNames.size()), 2, RoundingMode.HALF_UP);
				sc.setAverage(avgScore.toString());
				c.setAverageScore(avgScore);
				if (c.getOrder() > 0) {
					sectionTotalAvg = sectionTotalAvg.add(avgScore);
					LOG.info("Sec Total - Order : " + c.getOrder() + ", AVG Score : " + avgScore + ", Sec Tot Avg : " + sectionTotalAvg);
				}
			} else {
				if (evaluatorNames.size() == 0) {
					sc.setAverage(BigDecimal.ZERO.setScale(2).toString());
				} else {
					if (c.getOrder() == 0) {
						sc.setAverage(c.getAverageScore().toString());
					} else {
						sc.setAverage(c.getAverageScore().setScale(2).toString());
					}
				}
			}

			// Only compute the average if the form is not concluded yet.
			if (spForm.getFormStatus() != SupplierPerformanceFormStatus.CONCLUDED) {
				if (parent == null) {
					parent = sc;
					parentCriteria = c;
				} else if (parent.getLevel() != sc.getLevel()) {
					sectionTotalAvg = sectionTotalAvg.setScale(0, RoundingMode.HALF_UP);
					parent.setAverage(sectionTotalAvg.toString());
					parentCriteria.setAverageScore(sectionTotalAvg);
					LOG.info(">>>>> Sec Total - Order : " + parent.getOrder() + ", AVG Score : " + avgScore + ", Sec Tot Avg : " + sectionTotalAvg);
					sectionTotalAvg = BigDecimal.ZERO;
					parent = sc;
					parentCriteria = c;
				}
			}
		}

		if (parent != null && spForm.getFormStatus() != SupplierPerformanceFormStatus.CONCLUDED) {
			parent.setAverage(sectionTotalAvg.setScale(0, RoundingMode.HALF_UP).toString());
			parentCriteria.setAverageScore(sectionTotalAvg.setScale(0, RoundingMode.HALF_UP));
			LOG.info("Sec Total - Order : " + parent.getOrder() + ", Sec Tot Avg : " + sectionTotalAvg);
		}

		for (ScoreCardPojo sc : scoreCardList) {
			LOG.info(sc.toString());
		}
		List<ConsolidateScorePojo> conScoreList = new ArrayList<ConsolidateScorePojo>();
		for (SupplierPerformanceFormCriteria c : criteriaList) {
			if (c.getOrder() == 0) {
				ConsolidateScorePojo scp = new ConsolidateScorePojo();
				scp.setOrder(c.getOrder());
				scp.setLevel(c.getLevel());
				scp.setCriteria(c.getName());
				scp.setWeightage(c.getWeightage());
				scp.setAverageScore(c.getAverageScore());

				// If the form is already concluded then dont compute the scores
				if (spForm.getFormStatus() == SupplierPerformanceFormStatus.CONCLUDED) {
					scp.setTotalScore(c.getTotalScore().setScale(2));
				} else {
					BigDecimal total = BigDecimal.ZERO;
					for (SupplierPerformanceEvaluatorUser user : evaluators) {
						if (SupperPerformanceEvaluatorStatus.APPROVED != user.getEvaluationStatus()) {
							continue;
						}

						for (SupplierPerformanceCriteria uCriteria : user.getCriteria()) {
							if (uCriteria.getLevel() == c.getLevel() && uCriteria.getOrder() == 0 && c.getOrder() == 0 && uCriteria.getEvaluatorTotalScore() != null) {
								total = total.add(uCriteria.getEvaluatorTotalScore());
								break;
							}
						}
					}

					if (scp.getAverageScore() != null) {
						LOG.info("Setting total score at section : " + scp.getAverageScore().multiply(scp.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
						scp.setTotalScore(scp.getAverageScore().multiply(scp.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
					} else {
						scp.setTotalScore(BigDecimal.ZERO);
					}
					c.setTotalScore(scp.getTotalScore());

					// If no evaluators have submitted then set the total score as zero.
					// if (evaluatorNames.size() > 0) {
					// scp.setTotalScore(total.divide(new BigDecimal(evaluatorNames.size()), 0, RoundingMode.HALF_UP));
					// } else {
					// scp.setTotalScore(BigDecimal.ZERO);
					// }
				}
				LOG.info(scp.toString());
				conScoreList.add(scp);
			}
		}

		BigDecimal overallScore = BigDecimal.ZERO;
		ScoreRating scoreRating = null;
		// If the form is already concluded then dont compute the scores
		if (spForm.getFormStatus() == SupplierPerformanceFormStatus.CONCLUDED) {
			overallScore = spForm.getOverallScore();

			if (spForm.getScoreRating() != null) {
				scoreRating = spForm.getScoreRating();
				spForm.getScoreRating().getRating();
			}
		} else {
			for (ConsolidateScorePojo c : conScoreList) {
				if (c.getTotalScore() != null) {
					overallScore = overallScore.add(c.getTotalScore());
				}
			}
			overallScore = overallScore.setScale(0, RoundingMode.HALF_UP);
			scoreRating = scoreRatingService.getScoreRatingForScoreAndTenant(tenantId, overallScore);
		}

		// overallScore = overallScore.divide(new BigDecimal(conScoreList.size()), 2, RoundingMode.HALF_UP);
		model.addAttribute("scoreRating", scoreRating);
		model.addAttribute("overallScore", overallScore);
		model.addAttribute("conScoreList", conScoreList);
		model.addAttribute("scoreCardList", scoreCardList);
		model.addAttribute("showScoreTab", true);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { ErpIntegrationException.class, ApplicationException.class })
	public SupplierPerformanceForm concludeSupplierPerformanceForm(String formId, String remarks, String ratingId, HttpSession session, User loggedInUser) throws ErpIntegrationException, ApplicationException {

		SupplierPerformanceForm spForm = null;
		try {

			spForm = getSupplierPerformanceFormById(formId);
			spForm.setConcludeDate(new Date());
			spForm.setRemarks(remarks);

			// Calculate overall score
			List<SupplierPerformanceEvaluatorUser> evaluators = spForm.getEvaluators();
			List<String> evaluatorNames = new ArrayList<String>();
			for (SupplierPerformanceEvaluatorUser user : evaluators) {
				if (SupperPerformanceEvaluatorStatus.APPROVED == user.getEvaluationStatus()) {
					evaluatorNames.add(user.getEvaluator().getName());
				}
			}

			List<SupplierPerformanceFormCriteria> criteriaList = spForm.getCriteria();

			SupplierPerformanceFormCriteria parent = null;
			BigDecimal sectionTotal = BigDecimal.ZERO;
			BigDecimal overallTotal = BigDecimal.ZERO;
			for (SupplierPerformanceFormCriteria formCriteria : criteriaList) {
				BigDecimal total = BigDecimal.ZERO;
				if (formCriteria.getOrder() > 0) {
					// Get the all evaluator scores for the sub criteria to average it out
					for (SupplierPerformanceEvaluatorUser user : evaluators) {
						if (SupperPerformanceEvaluatorStatus.APPROVED != user.getEvaluationStatus()) {
							continue;
						}

						for (SupplierPerformanceCriteria uCriteria : user.getCriteria()) {
							if (uCriteria.getLevel() == formCriteria.getLevel() && uCriteria.getOrder() == formCriteria.getOrder() && uCriteria.getEvaluatorTotalScore() != null) {
								total = total.add(uCriteria.getEvaluatorTotalScore());
								break;
							}
						}
					}
					// If no evaluators have submitted then set the total score as zero.
					if (evaluatorNames.size() > 0) {
						total = total.divide(new BigDecimal(evaluatorNames.size()), 2, RoundingMode.HALF_UP);
					} else {
						total = BigDecimal.ZERO;
					}

					// Setting average score to Form Sub Criteria
					formCriteria.setAverageScore(total);
					sectionTotal = sectionTotal.add(total);

				} else {
					if (parent != null) {
						if (parent.getId() != formCriteria.getId()) {
							parent.setAverageScore(sectionTotal.setScale(0, RoundingMode.HALF_UP));
							// Compute the weighted score for the section total score.
							parent.setTotalScore(parent.getAverageScore().multiply(parent.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
							LOG.info("Section : " + parent.getName() + " Avg : " + parent.getAverageScore() + " Total : " + parent.getTotalScore() + " Weightage : " + parent.getWeightage());
							overallTotal = overallTotal.add(parent.getTotalScore());
						}
					}
					parent = formCriteria;
					sectionTotal = BigDecimal.ZERO;
				}
			}

			// Do the computation for last section
			if (parent != null) {
				parent.setAverageScore(sectionTotal.setScale(0, RoundingMode.HALF_UP));
				// Compute the weighted score for the section total score.
				parent.setTotalScore(parent.getAverageScore().multiply(parent.getWeightage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
				overallTotal = overallTotal.add(parent.getTotalScore());
				LOG.info("Section : " + parent.getName() + " Avg : " + parent.getAverageScore() + " Total : " + parent.getTotalScore() + " Weightage : " + parent.getWeightage());
			}

			spForm.setOverallScore(overallTotal.setScale(0, RoundingMode.HALF_UP));

			if (ratingId != null && !ratingId.isEmpty()) {
				ScoreRating scoreRating = new ScoreRating();
				scoreRating.setId(ratingId);
				spForm.setScoreRating(scoreRating);
			}

			spForm.setFormStatus(SupplierPerformanceFormStatus.CONCLUDED);
			spForm = supplierPerformanceFormDao.update(spForm);

			// Audit without snapshot
			try {
				SupplierPerformanceAudit formAudit = new SupplierPerformanceAudit(spForm, null, loggedInUser, new java.util.Date(), SupplierPerformanceAuditActionType.Conclude, messageSource.getMessage("spForm.audit.concluded", new Object[] { spForm.getFormId() }, Global.LOCALE), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
				formAuditService.save(formAudit);
			} catch (Exception e) {
				LOG.info("Error while saving Audit : " + e.getMessage(), e);
			}

			// Send the SPM details to SAP only if the SPM integration is enabled at the Business Unit level
			if (spForm.getBusinessUnit() != null && Boolean.TRUE == spForm.getBusinessUnit().getSpmIntegration() && spForm.getOverallScore() != null && spForm.getOverallScore().floatValue() > 0) {
				/**
				 * Send the Supplier Performance details to ERP. This method will throw exception if there is any error,
				 * else return the message returned by SAP.
				 */
				String successMessage = erpIntegrationService.transferSupplierPerformanceToErp(formId);
				if (successMessage != null) {
					SupplierPerformanceAudit audit = new SupplierPerformanceAudit(spForm, null, loggedInUser, new java.util.Date(), SupplierPerformanceAuditActionType.Transfer, "ERP Transfer Success: " + successMessage, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
					formAuditService.save(audit);
				}
			} else {
				LOG.warn("Not sending SPM to SAP for SPM form Id " + formId);
			}

		} catch (ErpIntegrationException e) {
			LOG.info("Error while sending Supplier Performace to ERP : " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.info("Error while concluding Supplier Performace : " + e.getMessage(), e);
			throw new ApplicationException("Error while concluding Supplier Performace : " + e.getMessage(), e);
		}

		return spForm;
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceForm cancelSPForm(String formId) {
		SupplierPerformanceForm form = supplierPerformanceFormDao.findById(formId);
		LOG.info("req-------------- " + form.getId());
		if (form != null) {
			form.setFormStatus(SupplierPerformanceFormStatus.CANCELED);
			form = supplierPerformanceFormDao.update(form);
		}
		return form;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = ApplicationException.class)
	public List<String> createRecurrenceForm() throws ApplicationException {
		List<String> newFormIds = new ArrayList<>();
		try {
			List<SupplierPerformanceForm> formList = supplierPerformanceFormDao.getAllConcludedFormsForRecurrenceJob();
			if (CollectionUtil.isNotEmpty(formList)) {
				Date now = new Date();
				for (SupplierPerformanceForm form : formList) {
					// LOG.info("Checking recurrence for Form : " + form.getFormId());
					// (recurrenceExecuted * recurrenceEcaluation) + recurrenceEvaluation
					int exectutedCount = form.getRecurrenceExecuted() == null ? 0 : form.getRecurrenceExecuted();
					int recurDuration = form.getRecurrenceEvaluation() == null ? 0 : form.getRecurrenceEvaluation();
					int days = (exectutedCount * recurDuration) + recurDuration;

					// LOG.info("Original Form Executed Count : " + exectutedCount + " Recr Duration : " + recurDuration
					// + ", Next recur in days : " + days);

					Calendar formStartDate = Calendar.getInstance();
					formStartDate.setTime(form.getEvaluationStartDate());
					formStartDate.add(Calendar.DATE, days);
					Date recurrenceDate = formStartDate.getTime();

					if (form.getNoOfRecurrence() != null && form.getRecurrenceExecuted() != null && recurrenceDate != null && recurrenceDate.before(now) && form.getNoOfRecurrence() > form.getRecurrenceExecuted()) {
						LOG.info("Creating recurring form from : " + form.getId() + " - " + form.getFormId());
						SupplierPerformanceForm formObj = getSupplierPerformanceFormById(form.getId());

						// Create copy of the existing form
						SupplierPerformanceForm newForm = new SupplierPerformanceForm();
						String formId = eventIdSettingsDao.generateEventIdByBusinessUnit(formObj.getCreatedBy().getTenantId(), "SP", formObj.getBusinessUnit());
						LOG.info("New Form Id : " + formId);
						newForm.setFormId(formId);
						newForm.setFormName(formObj.getFormName());
						newForm.setReferenceNumber(formObj.getReferenceNumber());
						newForm.setReferenceName(formObj.getReferenceName());
						newForm.setTemplate(formObj.getTemplate());
						newForm.setFormOwner(formObj.getFormOwner());
						newForm.setCreatedBy(formObj.getCreatedBy());
						newForm.setCreatedDate(now);
						newForm.setEventId(formObj.getEventId());
						newForm.setEventType(formObj.getEventType());
						newForm.setAwardedSupplier(formObj.getAwardedSupplier());
						newForm.setFormStatus(SupplierPerformanceFormStatus.SCHEDULED);
						newForm.setProcurementCategory(formObj.getProcurementCategory());
						newForm.setBusinessUnit(formObj.getBusinessUnit());
						newForm.setEvaluationStartDate(recurrenceDate);
						newForm.setBuyer(formObj.getBuyer());
						newForm.setCreatedFromRef(formObj);
						// Compute the duration based on original form start and end date. Use the same duration for
						// recur form.
						int min = DateUtil.getDiffMinInInteger(formObj.getEvaluationEndDate(), formObj.getEvaluationStartDate());
						LOG.info("Original Form Duration in Minutes : " + min + " Start : " + formObj.getEvaluationStartDate() + ", End : " + formObj.getEvaluationEndDate());
						formStartDate.add(Calendar.MINUTE, min);
						newForm.setEvaluationEndDate(formStartDate.getTime());

						// Copy Criteria
						newForm.copyCriteriaDetails(newForm, formObj.getTemplate());

						if (CollectionUtil.isNotEmpty(newForm.getCriteria())) {
							SupplierPerformanceFormCriteria parent = null;
							for (SupplierPerformanceFormCriteria criteria : newForm.getCriteria()) {
								criteria.setForm(newForm);
								if (criteria.getOrder() == 0) {
									parent = criteria;
								}
								if (criteria.getOrder() != 0) {
									criteria.setParent(parent);
								}
							}
						}

						List<SupplierPerformanceEvaluatorUser> evalList = new ArrayList<SupplierPerformanceEvaluatorUser>();
						if (CollectionUtil.isNotEmpty(formObj.getEvaluators())) {
							for (SupplierPerformanceEvaluatorUser e : formObj.getEvaluators()) {
								SupplierPerformanceEvaluatorUser newEval = new SupplierPerformanceEvaluatorUser();
								newEval.setEvaluator(e.getEvaluator());
								newEval.setEvaluationStatus(SupperPerformanceEvaluatorStatus.DRAFT);
								newEval.setForm(newForm);
								newEval.setCreatedDate(now);
								newEval.setBuyer(formObj.getBuyer());
								evalList.add(newEval);
							}
						}
						newForm.setEvaluators(evalList);

						// Compute reminders based on original evaluation interval and new End Date
						List<SupplierPerformanceReminder> reminders = new ArrayList<SupplierPerformanceReminder>();
						if (CollectionUtil.isNotEmpty(formObj.getFormReminder())) {
							for (SupplierPerformanceReminder rem : formObj.getFormReminder()) {

								SupplierPerformanceReminder newReminder = new SupplierPerformanceReminder();
								newReminder.setForm(newForm);
								newReminder.setInterval(rem.getInterval());
								Calendar reminderDate = Calendar.getInstance();
								reminderDate.setTime(newForm.getEvaluationEndDate());
								reminderDate.add(Calendar.DATE, -rem.getInterval());
								newReminder.setReminderDate(reminderDate.getTime());
								reminders.add(newReminder);
							}
						}
						newForm.setFormReminder(reminders);

						newForm = supplierPerformanceFormDao.save(newForm);

						for (SupplierPerformanceEvaluatorUser e : newForm.getEvaluators()) {
							// Copy criteria from Form to Evaluator
							e.copyCriteriaDetails(e, newForm);

							if (CollectionUtil.isNotEmpty(e.getCriteria())) {
								SupplierPerformanceCriteria parent = null;
								for (SupplierPerformanceCriteria criteria : e.getCriteria()) {
									criteria.setForm(newForm);
									criteria.setEvaluationUser(e);
									if (criteria.getOrder() == 0) {
										parent = criteria;
									}
									if (criteria.getOrder() != 0) {
										criteria.setParent(parent);
									}
									LOG.info("Recurrence Form : Evaluator Criteria - Evaluator User " + criteria.getEvaluationUser().getId());
								}
							}
						}

						newForm = supplierPerformanceFormDao.save(newForm);
						newFormIds.add(newForm.getId());

						LOG.info("Recurence Supplier Performance Form created : " + newForm.getFormId());

						// Update Recurrence Executed Count by 1 in parent form
						formObj.setRecurrenceExecuted(formObj.getRecurrenceExecuted() + 1);
						supplierPerformanceFormDao.saveOrUpdate(formObj);

						// Send Notification
						try {
							String url = APP_URL + "/buyer/editSupplierPerformanceEvaluation/" + newForm.getId();
							sendFormCreatedNotification(newForm.getFormOwner(), url, newForm);
						} catch (Exception e) {
							LOG.error("Error Sending Notification : " + e.getMessage(), e);
						}

						try {
							SupplierPerformanceAudit audit = new SupplierPerformanceAudit();
							audit.setAction(SupplierPerformanceAuditActionType.Create);
							audit.setActionDate(new Date());
							audit.setActionBy(formObj.getFormOwner());
							audit.setForm(newForm);
							audit.setDescription(messageSource.getMessage("sp.form.audit.recurrence.create", new Object[] { formObj.getFormId() }, Global.LOCALE));
							formAuditService.save(audit);
						} catch (Exception e) {
							LOG.error("Error while saving audit trail: " + e.getMessage(), e);
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error creating Recurrence Form : " + e.getMessage(), e);
			throw new ApplicationException("Error creating Recurrence Form : " + e.getMessage(), e);
		}
		return newFormIds;
	}

	/**
	 * @param formOwner
	 * @param url
	 * @param form
	 */
	private void sendFormCreatedNotification(User formOwner, String url, SupplierPerformanceForm form) {

		String mailTo = formOwner.getCommunicationEmail();
		String subject = "Supplier Performance Evaluation Form Created";
		HashMap<String, Object> map = new HashMap<String, Object>();
		String timeZoneStr = "GMT+8:00";
		TimeZone timeZone = TimeZone.getDefault();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a (z)");
		try {
			BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(formOwner.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZoneStr = settings.getTimeZone().getTimeZone();
			} else {
				timeZoneStr = "GMT+8:00";
			}
			timeZone = TimeZone.getTimeZone(timeZoneStr);
			sdf.setTimeZone(timeZone);
			df.setTimeZone(timeZone);
		} catch (Exception e) {
		} finally {
			sdf.setTimeZone(timeZone);
			df.setTimeZone(timeZone);
		}
		map.put("date", df.format(new Date()));
		map.put("userName", formOwner.getName());
		map.put("formId", form.getFormId());
		map.put("referenceNumber", form.getReferenceNumber());
		map.put("referenceName", form.getReferenceName());
		map.put("startDate", sdf.format(form.getEvaluationStartDate()));
		map.put("endDate", sdf.format(form.getEvaluationEndDate()));
		map.put("supplier", form.getAwardedSupplier().getCompanyName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(formOwner.getEmailNotifications()) {
			notificationService.sendEmail(mailTo, subject, map, Global.EVALUATION_FORM_CREATED);
		}
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> getSpFormIdListForSupplierIdAndTenantId(Date startDate, Date endDate, String supplierId, String tenantId) {
		return supplierPerformanceFormDao.getSpFormIdListForSupplierIdAndTenantId(startDate, endDate, supplierId, tenantId);
	}

	public static void main(String[] args) {
		BigDecimal d = new BigDecimal("2");

		d = d.divide(new BigDecimal(1), 2, RoundingMode.HALF_UP);

		System.out.println("Value : " + d);
	}

}
