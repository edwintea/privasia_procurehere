/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.privasia.procurehere.core.entity.EventAudit;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Ravi
 */
@Controller
@Scope("view")
public class AuditTrailController implements Serializable {

	private static final long serialVersionUID = 471631057428737918L;

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	private List<EventAudit> auditTrailList = null;

	private EventAudit auditTrail;

	private List<EventAudit> lazymodel;

	private Date dateFrom;

	private Date dateTo;

	private User user;

	private String searchActivity;

	@PostConstruct
	public void init() {
		if (CollectionUtil.isEmpty(auditTrailList)) {
			getAuditTrails();
		}
	}

	public void getAuditTrails() {
	}

	public void searchAuditTrail() {
		getAuditTrails();
	}

	public void reset() {
		dateFrom = null;
		dateTo = null;
		user = null;
		searchActivity = null;
		getAuditTrails();
	}

	public void loadForEdit() throws IOException {

	}

	/**
	 * @return the auditTrailList
	 */
	public List<EventAudit> getAuditTrailList() {
		return auditTrailList;
	}

	/**
	 * @param auditTrailList the auditTrailList to set
	 */
	public void setAuditTrailList(List<EventAudit> auditTrailList) {
		this.auditTrailList = auditTrailList;
	}

	/**
	 * @return the auditTrail
	 */
	public EventAudit getAuditTrail() {
		return auditTrail;
	}

	/**
	 * @param auditTrail the auditTrail to set
	 */
	public void setAuditTrail(EventAudit auditTrail) {
		this.auditTrail = auditTrail;
	}

	/**
	 * @return the lazymodel
	 */
	public List<EventAudit> getLazymodel() {
		return lazymodel;
	}

	/**
	 * @param lazymodel the lazymodel to set
	 */
	public void setLazymodel(List<EventAudit> lazymodel) {
		this.lazymodel = lazymodel;
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the searchActivity
	 */
	public String getSearchActivity() {
		return searchActivity;
	}

	/**
	 * @param searchActivity the searchActivity to set
	 */
	public void setSearchActivity(String searchActivity) {
		this.searchActivity = searchActivity;
	}

}
