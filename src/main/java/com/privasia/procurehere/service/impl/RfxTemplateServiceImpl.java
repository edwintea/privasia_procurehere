/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.dao.RfxTemplateDocumentDao;
import com.privasia.procurehere.core.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfxTemplateDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.RfxTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.UserService;
import org.springframework.util.FileCopyUtils;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nitin Otageri
 */
@Service
@Transactional(readOnly = true)
public class RfxTemplateServiceImpl implements RfxTemplateService {

	private static Logger LOG = LogManager.getLogger(RfxTemplateServiceImpl.class);

	@Autowired
	RfxTemplateDao rfxTemplateDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	UserDao userDao;

	@Autowired
	UserService userService;

	@Autowired
	RfxTemplateService rfxTemplateService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfxTemplateDocumentDao rfxTemplateDocumentDao;

	@Override
	public List<RfxTemplate> findTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {
		List<RfxTemplate> retList = new ArrayList<RfxTemplate>();

		// checking user is admin
		// boolean isAdmin = false;
		// List<User> adminUserList = userDao.getAllAdminUsersForBuyer(SecurityLibrary.getLoggedInUserTenantId());
		// for (User user : adminUserList) {
		// if (user.getId().equals(userId)) {
		// isAdmin = true;
		// break;
		// }
		// }
		// List<RfxTemplate> list = rfxTemplateDao.findTemplatesForTenant((isAdmin ? tenantId : null), tableParams,
		// userId);
		List<RfxTemplate> list = rfxTemplateDao.findTemplatesForTenant(tenantId, tableParams, userId);
		for (RfxTemplate rt : list) {
			retList.add(rt.createShallowCopy());
		}
		return retList;
	}

	@Override
	public boolean isExists(RfxTemplate template) {
		return rfxTemplateDao.isExists(template);
	}

	@Override
	public List<RfxTemplate> findAllActiveTemplatesForTenant(String tenantId) {
		return rfxTemplateDao.findAllActiveTemplatesForTenant(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfxTemplate save(RfxTemplate template) {
		LOG.info("===============temp Name=============" + template.getTemplateName());
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, ""+template.getType().getValue()+ " Template '"+template.getTemplateName()+ "' created ", template.getCreatedBy().getTenantId(), template.getCreatedBy(), new Date(), ModuleType.RFXAuctionTemplate);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return rfxTemplateDao.saveOrUpdate(template);
	}

	@Override
	@Transactional(readOnly = false)
	public void update(RfxTemplate template) {
		rfxTemplateDao.update(template);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(RfxTemplate template) {
		rfxTemplateDao.delete(template);
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, " "+template.getType()+ "' Template '"+template.getTemplateName()+"' deleted ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFXAuctionTemplate);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
	}

	@Override
	public RfxTemplate getRfxTemplateById(String id) {
		RfxTemplate template = rfxTemplateDao.findById(id);
		if (CollectionUtil.isNotEmpty(template.getDocuments())) {
			for (RfxTemplateDocument rfx : template.getDocuments()) {
				if (rfx.getUploadBy() != null) {
					rfx.getUploadBy().getLoginId();
				}
				rfx.getId();
			}
		}
		if (CollectionUtil.isNotEmpty(template.getApprovals())) {
			for (TemplateEventApproval approval : template.getApprovals()) {
				for (TemplateApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(template.getUnMaskedUsers())) {
			for (TemplateUnmaskUser unmask : template.getUnMaskedUsers()) {
				unmask.getUser();
			}
		}

		if (CollectionUtil.isNotEmpty(template.getEvaluationConclusionUsers())) {
			for (User user : template.getEvaluationConclusionUsers()) {
				user.getLoginId();
			}
		}

		if (CollectionUtil.isNotEmpty(template.getTeamMembers())) {
			for (TemplateEventTeamMembers team : template.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getLoginId();
				}
			}
		}
		if (template.getRevertBidUser() != null) {
			template.getRevertBidUser();
		}
		if (template.getUnMaskedUser() != null) {
			template.getUnMaskedUser();
		}
		
		if (CollectionUtil.isNotEmpty(template.getSuspensionApprovals())) {
			for (TemplateSuspensionApproval approval : template.getSuspensionApprovals()) {
				for (TemplateSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}
		
		if (CollectionUtil.isNotEmpty(template.getAwardApprovals())) {
			for (TemplateAwardApproval approval : template.getAwardApprovals()) {
				for (TemplateAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}
		// Initialize createdBy
		if(template.getCreatedBy() != null){
			template.getCreatedBy().getLoginId();
		}

		if(template.getBuyer() != null){
			template.getBuyer().getId();
		}

		if(template.getFields() != null){
			Hibernate.initialize(template.getFields());
		}
		if(template.getTemplateUsed() != null){
			template.getTemplateUsed();
		}

		return template;
	}

	@Override
	public long findTotalTemplatesForTenant(String tenantId) {
		return rfxTemplateDao.findTotalTemplatesForTenant(tenantId);
	}

	@Override
	public long findTotalFilteredTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {
		// checking user is admin
		/*
		 * boolean isAdmin = false; List<User> adminUserList =
		 * userDao.getAllAdminUsersForBuyer(SecurityLibrary.getLoggedInUserTenantId()); for (User user : adminUserList)
		 * { if (user.getId().equals(userId)) { isAdmin = true; break; } }
		 */
		// return rfxTemplateDao.findTotalFilteredTemplatesForTenant((isAdmin ? tenantId : null), tableParams, userId);
		return rfxTemplateDao.findTotalFilteredTemplatesForTenant(tenantId, tableParams, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfxTemplate getRfxTemplateForEditById(String templateId, String tenantId) {
		RfxTemplate template = rfxTemplateDao.getRfxTemplateForEditById(templateId, tenantId);
		if (CollectionUtil.isNotEmpty(template.getDocuments())) {
			for (RfxTemplateDocument rfx : template.getDocuments()) {
				if (rfx.getUploadBy() != null) {
					rfx.getUploadBy().getLoginId();
				}
				rfx.getId();
			}
		}
		if (CollectionUtil.isNotEmpty(template.getApprovals())) {
			for (TemplateEventApproval approval : template.getApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (TemplateApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(template.getSuspensionApprovals())) {
			for (TemplateSuspensionApproval approval : template.getSuspensionApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (TemplateSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
					}
				}
			}
		}
		
		if (CollectionUtil.isNotEmpty(template.getAwardApprovals())) {
			for (TemplateAwardApproval approval : template.getAwardApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (TemplateAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
					}
				}
			}
		}
		
		if (CollectionUtil.isNotEmpty(template.getTeamMembers())) {
			for (TemplateEventTeamMembers team : template.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getLoginId();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(template.getUnMaskedUsers())) {
			for (TemplateUnmaskUser unmask : template.getUnMaskedUsers()) {
				unmask.getUser();
			}
		}

		if (template.getRevertBidUser() != null) {
			template.getRevertBidUser().getId();
			template.getRevertBidUser().getName();
		}

		if (template.getUnMaskedUser() != null) {
			template.getUnMaskedUser().getId();
			template.getUnMaskedUser().getName();
		}

		if (CollectionUtil.isNotEmpty(template.getEvaluationConclusionUsers())) {
			for (User user : template.getEvaluationConclusionUsers()) {
				user.getLoginId();
			}
		}

		return template;

	}

	@Override
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String tenantId, RfxTypes rfxTypes, String userId) {
		return rfxTemplateDao.findAllActiveTemplatesByRfxTypeForTenant(tenantId, rfxTypes, userId);
	}

	@Override
	public List<RfxTemplate> findByTemplateNameForTenant(String templateName, String tenantId, RfxTypes eventType, String userId) {
		return rfxTemplateDao.findByTemplateNameForTenant(templateName, tenantId, eventType, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfxTemplate copyTemplate(RfxTemplate rfxTemplate, RfxTemplate newTemplate, User createdBy) {

		if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {
			List<TemplateField> templateFields = new ArrayList<TemplateField>();
			for (TemplateField tempField : rfxTemplate.getFields()) {
				TemplateField temp = new TemplateField();
				temp.setDefaultValue(tempField.getDefaultValue());
				temp.setFieldName(tempField.getFieldName());
				temp.setOptional(tempField.getOptional());
				temp.setReadOnly(tempField.getReadOnly());
				temp.setTemplate(newTemplate);
				temp.setVisible(tempField.getVisible());
				temp.setBuyer(tempField.getBuyer());
				templateFields.add(temp);
			}
			newTemplate.setFields(templateFields);
		}
		newTemplate.setTemplateFieldBinding(rfxTemplate.getTemplateFieldBinding());
		newTemplate.setApprovalVisible(rfxTemplate.getApprovalVisible());
		newTemplate.setApprovalReadOnly(rfxTemplate.getApprovalReadOnly());
		newTemplate.setReadOnlyTeamMember(rfxTemplate.getReadOnlyTeamMember());
		newTemplate.setApprovalOptional(rfxTemplate.getApprovalOptional());
		newTemplate.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newTemplate.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		
		// copy approval from template
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<TemplateEventApproval> approvalList = new ArrayList<TemplateEventApproval>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				TemplateEventApproval newTemplateApproval = new TemplateEventApproval();
				newTemplateApproval.setApprovalType(templateApproval.getApprovalType());
				newTemplateApproval.setLevel(templateApproval.getLevel());
				newTemplateApproval.setRfxTemplate(newTemplate);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<TemplateApprovalUser> tempApprovalList = new ArrayList<TemplateApprovalUser>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						TemplateApprovalUser approvalUser = new TemplateApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(newTemplateApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						tempApprovalList.add(approvalUser);
					}
					newTemplateApproval.setApprovalUsers(tempApprovalList);
				}
				approvalList.add(newTemplateApproval);
			}
			newTemplate.setApprovals(approvalList);
		}
		
		if (CollectionUtil.isNotEmpty(rfxTemplate.getSuspensionApprovals())) {
			List<TemplateSuspensionApproval> approvalList = new ArrayList<TemplateSuspensionApproval>();
			for (TemplateSuspensionApproval templateApproval : rfxTemplate.getSuspensionApprovals()) {
				TemplateSuspensionApproval newTemplateApproval = new TemplateSuspensionApproval();
				newTemplateApproval.setApprovalType(templateApproval.getApprovalType());
				newTemplateApproval.setLevel(templateApproval.getLevel());
				newTemplateApproval.setRfxTemplate(newTemplate);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<TemplateSuspensionApprovalUser> tempApprovalList = new ArrayList<TemplateSuspensionApprovalUser>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						TemplateSuspensionApprovalUser approvalUser = new TemplateSuspensionApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setSuspensionApproval(newTemplateApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						tempApprovalList.add(approvalUser);
					}
					newTemplateApproval.setApprovalUsers(tempApprovalList);
				}
				approvalList.add(newTemplateApproval);
			}
			newTemplate.setSuspensionApprovals(approvalList);
		}
		
		//Copy Award Approval
		if (CollectionUtil.isNotEmpty(rfxTemplate.getAwardApprovals())) {
			List<TemplateAwardApproval> approvalList = new ArrayList<TemplateAwardApproval>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				TemplateAwardApproval newTemplateApproval = new TemplateAwardApproval();
				newTemplateApproval.setApprovalType(templateApproval.getApprovalType());
				newTemplateApproval.setLevel(templateApproval.getLevel());
				newTemplateApproval.setRfxTemplate(newTemplate);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<TemplateAwardApprovalUser> tempApprovalList = new ArrayList<TemplateAwardApprovalUser>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						TemplateAwardApprovalUser approvalUser = new TemplateAwardApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setAwardApproval(newTemplateApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						tempApprovalList.add(approvalUser);
					}
					newTemplateApproval.setApprovalUsers(tempApprovalList);
				}
				approvalList.add(newTemplateApproval);
			}
			newTemplate.setAwardApprovals(approvalList);
		}

		List<TemplateEventTeamMembers> teamMembers = new ArrayList<TemplateEventTeamMembers>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				TemplateEventTeamMembers newTeamMembers = new TemplateEventTeamMembers();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setRfxTemplate(newTemplate);
				teamMembers.add(newTeamMembers);
			}
			newTemplate.setTeamMembers(teamMembers);
		}
		newTemplate.setCreatedBy(createdBy);
		newTemplate.setCreatedDate(new Date());
		if (rfxTemplate.getType().equals(RfxTypes.RFA)) {
			newTemplate.setTemplateAuctionType(rfxTemplate.getTemplateAuctionType());
		}
		newTemplate.setSupplierBasedOnCategory(rfxTemplate.getSupplierBasedOnCategory());
		newTemplate.setAutoPopulateSupplier(rfxTemplate.getAutoPopulateSupplier());
		newTemplate.setReadOnlySupplier(rfxTemplate.getReadOnlySupplier());
		newTemplate.setSupplierBasedOnState(rfxTemplate.getSupplierBasedOnState());
		newTemplate.setRestrictSupplierByState(rfxTemplate.getRestrictSupplierByState());
		newTemplate.setVisibleSupplierTags(rfxTemplate.getVisibleSupplierTags());
		newTemplate.setOptionalSupplierTags(rfxTemplate.getOptionalSupplierTags());
		newTemplate.setVisibleGeographicalCoverage(rfxTemplate.getVisibleGeographicalCoverage());
		newTemplate.setOptionalGeographicalCoverage(rfxTemplate.getOptionalGeographicalCoverage());
		newTemplate.setPrivateEvent(rfxTemplate.getPrivateEvent());
		newTemplate.setPartialEvent(rfxTemplate.getPartialEvent());
		newTemplate.setPublicEvent(rfxTemplate.getPublicEvent());

		newTemplate.setRevertBidUser(rfxTemplate.getRevertBidUser());
		newTemplate.setRevertLastBid(rfxTemplate.getRevertLastBid());
		newTemplate.setReadOnlyRevertLastBid(rfxTemplate.getReadOnlyRevertLastBid());
		newTemplate.setVisibleRevertLastBid(rfxTemplate.getVisibleRevertLastBid());

		newTemplate.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
		newTemplate.setViewSupplerName(rfxTemplate.getViewSupplerName());
		newTemplate.setVisibleViewSupplierName(rfxTemplate.getVisibleViewSupplierName());
		newTemplate.setReadOnlyViewSupplierName(rfxTemplate.getReadOnlyViewSupplierName());
		newTemplate.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		newTemplate.setEnableSuspendApproval(rfxTemplate.getEnableSuspendApproval());
		newTemplate.setOptionalSuspendApproval(rfxTemplate.getOptionalSuspendApproval());
		newTemplate.setReadOnlySuspendApproval(rfxTemplate.getReadOnlySuspendApproval());
		newTemplate.setVisibleSuspendApproval(rfxTemplate.getVisibleSuspendApproval());
		newTemplate.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());
		newTemplate.setOptionalAwardApproval(rfxTemplate.getOptionalAwardApproval());
		newTemplate.setVisibleAwardApproval(rfxTemplate.getVisibleAwardApproval());
		newTemplate.setReadOnlyAwardApproval(rfxTemplate.getReadOnlyAwardApproval());

		List<TemplateUnmaskUser> unmaskUsers = new ArrayList<TemplateUnmaskUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser user : rfxTemplate.getUnMaskedUsers()) {
				TemplateUnmaskUser newuser = new TemplateUnmaskUser();
				newuser.setUser(user.getUser());
				newuser.setRfxTemplate(newTemplate);
				unmaskUsers.add(newuser);
			}
			newTemplate.setUnMaskedUsers(unmaskUsers);
		}

		List<User> evalConUsers = new ArrayList<User>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				evalConUsers.add(user);
			}
			newTemplate.setEvaluationConclusionUsers(evalConUsers);
		}
		newTemplate.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		newTemplate.setReadOnlyEvaluationConclusionUsers(rfxTemplate.getReadOnlyEvaluationConclusionUsers());
		newTemplate.setVisibleEvaluationConclusionUsers(rfxTemplate.getVisibleEvaluationConclusionUsers());

		newTemplate.setViewAuctionHall(rfxTemplate.getViewAuctionHall());
		newTemplate.setVisibleViewAuctionHall(rfxTemplate.getVisibleViewAuctionHall());
		newTemplate.setReadOnlyViewAuctionHall(rfxTemplate.getReadOnlyViewAuctionHall());

		newTemplate.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newTemplate.setVisibleCloseEnvelope(rfxTemplate.getVisibleCloseEnvelope());
		newTemplate.setReadOnlyCloseEnvelope(rfxTemplate.getReadOnlyCloseEnvelope());

		newTemplate.setAddSupplier(rfxTemplate.getAddSupplier());
		newTemplate.setVisibleAddSupplier(rfxTemplate.getVisibleAddSupplier());
		newTemplate.setReadOnlyAddSupplier(rfxTemplate.getReadOnlyAddSupplier());

		newTemplate.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
		newTemplate.setVisibleAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
		newTemplate.setReadOnlyAllowToSuspendEvent(rfxTemplate.getReadOnlyAllowToSuspendEvent());

		newTemplate.setRfxEnvelope1(rfxTemplate.getRfxEnvelope1());
		newTemplate.setRfxEnvelope2(rfxTemplate.getRfxEnvelope2());
		newTemplate.setRfxEnvelope3(rfxTemplate.getRfxEnvelope3());
		newTemplate.setRfxEnvelope4(rfxTemplate.getRfxEnvelope4());
		newTemplate.setRfxEnvelope5(rfxTemplate.getRfxEnvelope5());
		newTemplate.setRfxEnvelope6(rfxTemplate.getRfxEnvelope6());
		newTemplate.setRfxSequence1(rfxTemplate.getRfxSequence1());
		newTemplate.setRfxSequence2(rfxTemplate.getRfxSequence2());
		newTemplate.setRfxSequence3(rfxTemplate.getRfxSequence3());
		newTemplate.setRfxSequence4(rfxTemplate.getRfxSequence4());
		newTemplate.setRfxSequence5(rfxTemplate.getRfxSequence5());
		newTemplate.setRfxSequence6(rfxTemplate.getRfxSequence6());
		newTemplate.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newTemplate.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newTemplate.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newTemplate.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());

		newTemplate = rfxTemplateDao.saveOrUpdate(newTemplate);
		
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, ""+newTemplate.getType().getValue()+ " Template '"+newTemplate.getTemplateName()+ "' is created ", newTemplate.getCreatedBy().getTenantId(), newTemplate.getCreatedBy(), new Date(), ModuleType.RFXAuctionTemplate);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		List<String> assignedUserId = rfxTemplateService.getTemplateByUserIdAndTemplateId(rfxTemplate.getId(), SecurityLibrary.getLoggedInUserTenantId());
		if (CollectionUtil.isNotEmpty(assignedUserId)) {
			for (String assgnedUser : assignedUserId) {
				User user = userService.getUsersForRfxById(assgnedUser);
				List<RfxTemplate> assignedTemplateList = user.getAssignedTemplates();
				assignedTemplateList.add(newTemplate);
				user.setAssignedTemplates(assignedTemplateList);
				userService.updateUser(user);
			}
		}
		return newTemplate;
	}

	@Override
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantId(String tenantId, RfxTypes rfxTypes) {
		return rfxTemplateDao.findAllActiveTemplatesByRfxTypeForTenantId(tenantId, rfxTypes);
	}

	@Override
	public List<RfxTemplate> findAllActiveTemplatesByAuctionTypeForTenantId(String tenantId, AuctionType auctionType, String userId) {
		return rfxTemplateDao.findAllActiveTemplatesByAuctionTypeForTenantId(tenantId, auctionType, userId);
	}

	@Override
	public RfxTemplate getRfxTemplateByIdForBU(String templateId) {
		RfxTemplate template = rfxTemplateDao.findById(templateId);
		for (TemplateField field : template.getFields()) {
			field.getDefaultValue();
		}
		return template;

	}

	@Override
	public Boolean findTemplateIndustryCategoryFlagByEventId(String eventId, RfxTypes rfxTypes) {
		return rfxTemplateDao.findTemplateIndustryCategoryFlagByEventId(eventId, rfxTypes);
	}

	@Override
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String loggedInUserTenantId, RfxTypes valueOf, String id, String pageNo) {
		return rfxTemplateDao.findAllActiveTemplatesByRfxTypeForTenant(loggedInUserTenantId, valueOf, id, pageNo);
	}

	@Override
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantInitial(String loggedInUserTenantId, RfxTypes rfxTypes, String userId) {
		return rfxTemplateDao.findAllActiveTemplatesByRfxTypeForTenantInitial(loggedInUserTenantId, rfxTypes, userId);
	}

	@Override
	public List<RfxTemplatePojo> findTemplatesForTenantId(String tenantId, TableDataInput tableParams, String userId) {
		return rfxTemplateDao.findTemplatesForTenantId(tenantId, tableParams, userId);
	}

	@Override
	public List<User> getAllUsers(String tempId) {
		return rfxTemplateDao.getAllUsers(tempId);
	}

	@Override
	public RfxTemplate getTemplateByTenantIdAndTemplateId(String id, String tenantId) {
		return rfxTemplateDao.getTemplateByTenantIdAndTemplateId(id, tenantId);

	}

	@Override
	public List<String> getTemplateByUserIdAndTemplateId(String userId, String loggedInUserTenantId) {
		return rfxTemplateDao.getTemplateByUserIdAndTemplateId(userId, loggedInUserTenantId);
	}

	@Override
	public void deleteAssociatedUserForTemplate(String rfxTemplateId) {
		rfxTemplateDao.deleteAssociatedUserForTemplate(rfxTemplateId);
	}

	@Override
	public long findActiveTemplateCountByRfxTypeForTenantId(String searchValue, String tenantId, RfxTypes rfxType, String userId) {
		return rfxTemplateDao.findActiveTemplateCountByRfxTypeForTenantId(searchValue, tenantId, rfxType, userId);
	}

	@Override
	public List<RfxTemplate> findTemplateByTemplateNameForTenant(String searchValue, Integer pageNo, Integer pageLength, RfxTypes eventType, String tenantId, String userId) {
		Integer start = 0;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}
		LOG.info(" start  : " + start);
		return rfxTemplateDao.findTemplateByTemplateNameForTenant(searchValue, pageNo, pageLength, start, eventType, tenantId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfxTemplateDocument saveRfxTemplateDocument(RfxTemplateDocument document) {
		return rfxTemplateDocumentDao.saveOrUpdate(document);
	}

	@Override
	public List<RfxTemplateDocument> findAllTemplateDocsBytemplateId(String templateId) {
		return rfxTemplateDocumentDao.findAllTemplateDocsBytemplateId(templateId);
	}

	@Override
	public void downloadRfxTemplateDocument(String docId, HttpServletResponse response) throws Exception {
		RfxTemplateDocument docs = rfxTemplateDocumentDao.findById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeDocument(RfxTemplateDocument removeDoc) {
		rfxTemplateDocumentDao.delete(removeDoc);
	}

	@Override
	public RfxTemplateDocument findDocsById(String id) {
		return rfxTemplateDocumentDao.findDocsById(id);
	}

	@Override
	@Transactional
	public void updateDocument(String docId, String docDesc, String formId, Boolean internal) {
		RfxTemplateDocument rfxTemplateDocument = rfxTemplateDocumentDao.findDocsById(docId);
		if (rfxTemplateDocument == null) {
			throw new EntityNotFoundException("Document with ID " + docId + " not found");
		}
		rfxTemplateDocument.setDescription(docDesc);
		rfxTemplateDocument.setInternal(internal);
		RfxTemplate rfxTemplate = getRfxTemplateById(formId);
		rfxTemplateDocument.setRfxTemplate(rfxTemplate);
		saveRfxTemplateDocument(rfxTemplateDocument);
	}

	@Override
	@Transactional
	public void updateTemplateUsed(String templateId) {
		RfxTemplate template = getRfxTemplateById(templateId);
		template.setTemplateUsed(Boolean.TRUE);
		update(template);
	}

}
