package com.privasia.procurehere.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.privasia.procurehere.service.GenericSorService;
import com.privasia.procurehere.service.RfaSorService;
import com.privasia.procurehere.service.RfaSupplierSorItemService;
import com.privasia.procurehere.service.RfiSorService;
import com.privasia.procurehere.service.RfiSupplierSorItemService;
import com.privasia.procurehere.service.RfpSorService;
import com.privasia.procurehere.service.RfpSupplierSorItemService;
import com.privasia.procurehere.service.RfqSorService;
import com.privasia.procurehere.service.RfqSupplierSorItemService;
import com.privasia.procurehere.service.RftSorService;
import com.privasia.procurehere.service.RftSupplierSorItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.privasia.procurehere.core.dao.RftCqOptionDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.GenericBqService;
import com.privasia.procurehere.service.GenericEventService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierCommentService;
import com.privasia.procurehere.service.RfaSupplierCqItemService;
import com.privasia.procurehere.service.RfaSupplierCqService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfiSupplierCqItemService;
import com.privasia.procurehere.service.RfiSupplierCqService;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.RfpSupplierBqItemService;
import com.privasia.procurehere.service.RfpSupplierCommentService;
import com.privasia.procurehere.service.RfpSupplierCqItemService;
import com.privasia.procurehere.service.RfpSupplierCqService;
import com.privasia.procurehere.service.RfqBqService;
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RfqDocumentService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RfqMeetingService;
import com.privasia.procurehere.service.RfqSupplierBqItemService;
import com.privasia.procurehere.service.RfqSupplierCommentService;
import com.privasia.procurehere.service.RfqSupplierCqItemService;
import com.privasia.procurehere.service.RfqSupplierCqService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.RftSupplierBqItemService;
import com.privasia.procurehere.service.RftSupplierBqService;
import com.privasia.procurehere.service.RftSupplierCommentService;
import com.privasia.procurehere.service.RftSupplierCqItemService;
import com.privasia.procurehere.service.RftSupplierCqService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SupplierRfaAttendanceService;
import com.privasia.procurehere.service.SupplierRfiAttendanceService;
import com.privasia.procurehere.service.SupplierRfpAttendanceService;
import com.privasia.procurehere.service.SupplierRfqAttendanceService;
import com.privasia.procurehere.service.SupplierRftMeetingAttendanceService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.CqOptionEditor;
import com.privasia.procurehere.web.editors.UomEditor;

public class SupplierEventBase {

	protected static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	UomEditor uomEditor;

	@Autowired
	CqOptionEditor cqOptionEditor;

	@Autowired
	RftCqOptionDao rftCqOptionDao;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfqMeetingService rfqMeetingService;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	RftSupplierCommentService rftSupplierCommentService;

	@Autowired
	RfpSupplierCommentService rfpSupplierCommentService;

	@Autowired
	RfqSupplierCommentService rfqSupplierCommentService;

	@Autowired
	SupplierRfaAttendanceService supplierRfaMeetingAttendanceService;

	@Autowired
	SupplierRftMeetingAttendanceService supplierRftEventMeetingAttendanceService;

	@Autowired
	SupplierRfpAttendanceService supplierRfpMeetingAttendanceService;

	@Autowired
	SupplierRfqAttendanceService supplierRfqMeetingAttendanceService;

	@Autowired
	SupplierRfiAttendanceService supplierRfiMeetingAttendanceService;

	@Autowired
	RftSupplierBqItemService rftSupplierBqItemService;

	@Autowired
	RfpSupplierBqItemService rfpSupplierBqItemService;

	@Autowired
	RfqSupplierBqItemService rfqSupplierBqItemService;

	@Autowired
	RfqSupplierSorItemService rfqSupplierSorItemService;

	@Autowired
	RfpSupplierSorItemService rfpSupplierSorItemService;

	@Autowired
	RfaSupplierSorItemService rfaSupplierSorItemService;

	@Autowired
	RfiSupplierSorItemService rfiSupplierSorItemService;

	@Autowired
	RftSupplierSorItemService rftSupplierSorItemService;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	RftSupplierCqItemService rftSupplierCqItemService;

	@Autowired
	RfpSupplierCqItemService rfpSupplierCqItemService;

	@Autowired
	RfqSupplierCqItemService rfqSupplierCqItemService;

	@Autowired
	RfiSupplierCqItemService rfiSupplierCqItemService;

	@Autowired
	RfaSupplierCqItemService rfaSupplierCqItemService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	RfqBqService rfqBqService;

	@Autowired
	RfqSorService rfqSorService;

	@Autowired
	RfpSorService rfpSorService;

	@Autowired
	RfaSorService rfaSorService;

	@Autowired
	RftSorService rftSorService;

	@Autowired
	RfiSorService rfiSorService;


	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	GenericEventService genericEventService;

	@Autowired
	GenericBqService genericBqService;

	@Autowired
	GenericSorService genericSorService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfqCqService rfqCqService;

	@Autowired
	RfiCqService rfiCqService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfaSupplierCommentService rfaSupplierCommentService;

	@Autowired
	UserService userService;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	RfaSupplierCqService rfaSupplierCqService;
	
	@Autowired
	RfiSupplierCqService rfiSupplierCqService;
	
	@Autowired
	RfpSupplierCqService rfpSupplierCqService;
	
	@Autowired
	RfqSupplierCqService rfqSupplierCqService;
	
	@Autowired
	RftSupplierCqService rftSupplierCqService;

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	/**
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, "rejectionDate", new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy HH:mm a z"), true));
		binder.registerCustomEditor(CqOption.class, cqOptionEditor);

		binder.registerCustomEditor(List.class, "listOptAnswers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					LOG.info("ID : " + id);
					RftCqOption group = rftCqOptionDao.findById(id);
					CqOption op = new CqOption();
					op.setId(group.getId());
					op.setScoring(group.getScoring());
					op.setOrder(group.getOrder());
					op.setValue(group.getValue());
					return op;
				}
				return null;
			}
		});

		binder.registerCustomEditor(Uom.class, uomEditor);
	}

	public boolean checkSupplierExpireSubscription(String loggedInUserTenantId) throws SubscriptionException {
		return subscriptionService.checkSupplierExpireSubscription(loggedInUserTenantId);
	}

	/**
	 * @param pageLength
	 */
	public void updateSecurityLibraryUser(Integer pageLength) {
		/*
		 * UPDATE THE SECURITY CONTEXT AS THE BQ Page Length IS NOW CHANGED.
		 */
		// gonna need this to get user from Acegi
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		// get user obj
		AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
		// update the bq Page length on the user obj
		authUser.setBqPageLength(pageLength);
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
		upat.setDetails(auth.getDetails());
		ctx.setAuthentication(upat);
	}

	protected boolean associateBuyerWithSupplier(boolean accepted, String loggedInUserTenantId, String buyerId) {
		return subscriptionService.associateBuyerWithSupplier(accepted, loggedInUserTenantId, buyerId);
	}
}