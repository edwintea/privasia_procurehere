/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ErpIntegrationException;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.pojo.UserPojo;

/**
 * @author anshul
 */
public interface SupplierPerformanceFormService {

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceForm getSupplierPerformanceFormById(String formId);

	/**
	 * @param form
	 * @return
	 */
	SupplierPerformanceForm saveSupplierPerformanceForm(SupplierPerformanceForm form);

	/**
	 * @param form
	 * @return
	 */
	SupplierPerformanceForm updateSupplierPerformanceForm(SupplierPerformanceForm form);

	/**
	 * @param templateId
	 * @return
	 */
	List<String> getSPFormIdListByTemplateId(String templateId);

	/**
	 * @param form
	 * @param updatedBy
	 * @param timeZone TODO
	 * @param reminderSent TODO
	 * @return
	 * @throws ApplicationException
	 */
	SupplierPerformanceForm updateSupplierPerformanceForm(SupplierPerformanceForm form, User updatedBy, TimeZone timeZone, String[] remindMeDays, Boolean[] reminderSent) throws ApplicationException;

	/**
	 * @param spFormId
	 * @param userId
	 * @return
	 * @throws ApplicationException
	 */
	List<SupplierPerformanceEvaluatorUser> addEvaluator(String spFormId, String userId) throws ApplicationException;

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierPerformanceEvaluatorUser> findEvaluatorsByFormId(String formId);

	/**
	 * @param formId
	 * @param user
	 * @return
	 * @throws ApplicationException
	 */
	SupplierPerformanceForm finishSupplierPerformanceForm(String formId, User user, HttpSession session) throws ApplicationException;

	/**
	 * @param formId
	 * @return
	 */
	EventTimerPojo getTimeByFormId(String formId);

	/**
	 * @param spFormId
	 * @param userId
	 * @return
	 */
	List<User> removeEvaluator(String spFormId, String userId);

	/**
	 * @param formId
	 * @param formStatus
	 */
	void updateImmediately(String formId, SupplierPerformanceFormStatus formStatus);

	/**
	 * @param formId
	 * @param model
	 * @param tenandtId TODO
	 */
	void getScoreCardList(String formId, Model model, String tenandtId);

	/**
	 * @param formId
	 * @param remarks
	 * @param ratingId TODO
	 * @param session TODO
	 * @param loggedInUser TODO
	 * @return
	 * @throws ApplicationException
	 * @throws ErpIntegrationException
	 */
	SupplierPerformanceForm concludeSupplierPerformanceForm(String formId, String remarks, String ratingId, HttpSession session, User loggedInUser) throws ErpIntegrationException, ApplicationException;

	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceForm cancelSPForm(String formId);

	/**
	 * @return TODO
	 * @throws ApplicationException
	 */
	List<String> createRecurrenceForm() throws ApplicationException;

	/**
	 * @param startDate
	 * @param endDate
	 * @param supplierId
	 * @param tenantId
	 * @return
	 */
	List<SupplierPerformanceEvaluationPojo> getSpFormIdListForSupplierIdAndTenantId(Date startDate, Date endDate, String supplierId, String tenantId);
	
	/**
	 * @param formId
	 * @return
	 */
	List<UserPojo> findEvaluatorsUserByFormId(String formId);
	
	/**
	 * @param formId
	 * @return
	 */
	SupplierPerformanceFormStatus getFormStatusByFormId(String formId);
}
