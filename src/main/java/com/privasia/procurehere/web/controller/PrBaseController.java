package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;

import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;

/**
 * @author parveen
 */
public class PrBaseController {

	protected static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	PrService prService;

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	UomService uomService;


	@Autowired
	CurrencyService currencyService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	protected FavoriteSupplierService favoriteSupplierService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	UserService userService;

	@Autowired
	PoService poService;
	
	@Autowired
	PaymentTermsService paymentTermesService;

	/**
	 * @param model
	 * @param pr
	 */
	public Boolean validatePr(Pr pr, Model model, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<Pr>> constraintViolations = validator.validate(pr, validations);
		for (ConstraintViolation<Pr> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		model.addAttribute("error", errorList);
		if (errorList.isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	/**
	 * @param model
	 */
	public void constructPrAttributes(Model model) {
		model.addAttribute("currencyList", currencyService.getAllActiveCurrencies());
		model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("buyer", buyerService.findBuyerGeneralDetailsById(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("addressList", buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("paymentTermsList", paymentTermesService.getActivePaymentTermesByTenantId(SecurityLibrary.getLoggedInUserTenantId()));
	}

	/**
	 * @param model
	 */
	public void constructPrSupplierAttributes(Model model) {
		model.addAttribute("prContact", new PrContact());
		List<FavouriteSupplier> supplierList = favoriteSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), null, null, null);
		model.addAttribute("supplierList", supplierList);
	}

	/**
	 * @param model
	 * @param pr
	 */
	public void constructPrTeamAttributes(Model model, Pr pr) {
		// LOG.info(" PR :" + pr.toLogString());
		if (pr.getCorrespondenceAddress() != null) {
			pr.setCorrespondenceAddress(buyerAddressService.getBuyerAddress(pr.getCorrespondenceAddress().getId()));
		}
		List<User> assignedTeamMembers = new ArrayList<>();

		if (CollectionUtil.isNotEmpty(pr.getPrTeamMembers()))
			for (PrTeamMember tm : pr.getPrTeamMembers()) {
				try {
					assignedTeamMembers.add((User) tm.getUser().clone());
				} catch (CloneNotSupportedException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);

		List<User> tmList = new ArrayList<User>();
		// List<User> activeUserList =
		// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		for (UserPojo user : userListSumm) {
			try {
				tmList.add(new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted()));
			} catch (Exception e) {
				LOG.info("Error while cloning user List :" + e.getMessage());
			}
		}

		// Pr users for Approval
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null); // userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		List<PrApprovalUser> approvalUserList = new ArrayList<PrApprovalUser>();

		if (CollectionUtil.isNotEmpty(pr.getPrApprovals())) {
			for (PrApproval approval : pr.getPrApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (PrApprovalUser approvalUser : approval.getApprovalUsers()) {
						User user = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
						if (!approvalUserList.contains(new PrApprovalUser(user))) {
							approvalUserList.add(new PrApprovalUser(user));
						}
					}
				}
			}
		}
		for (UserPojo userPojo : userList) {
			User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(), userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
			if (!approvalUserList.contains(new PrApprovalUser(user))) {
				approvalUserList.add(new PrApprovalUser(user));
			}
		}

		model.addAttribute("userList", approvalUserList);
		model.addAttribute("userList1", userList);

		// Remove all users that are already added as editors.
		tmList.removeAll(assignedTeamMembers);
		model.addAttribute("prTeamMembers", tmList);
		model.addAttribute("assignedTeamMembers", assignedTeamMembers);
	}

	public void buildDocumentFile(HttpServletResponse response, PrDocument docs) throws IOException {
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}
}
