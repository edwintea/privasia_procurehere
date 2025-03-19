/**
 *  
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.ActiveEventPojo;
import com.privasia.procurehere.core.pojo.BudgetPojo;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.DynamicColumnsData;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.SourcingFormRequestPojo;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.core.pojo.ProductContractPojo;


/**
 * @author Najeer
 */
@Controller
@RequestMapping(value = "/buyer")
public class BuyerDashboardController extends DashboardBase implements Serializable {

	private static final long serialVersionUID = 4723921253275442116L;

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	PrService prService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	PoService poService;

	@Autowired
	SupplierFormSubmissionService supplierFormSubmissionService;

	@Autowired
	SupplierPerformanceEvaluationService spEvaluationService;
	
	@Autowired
	ProductContractService productContractService;
	@Autowired
	BusinessUnitService businessUnitService;

	@ModelAttribute("poStatusList")
	public List<PrStatus> getPoStatusList() {
		List<PrStatus> poStatusList = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED);
		return poStatusList;
	}

	@RequestMapping(value = "/buyerDashboard", method = RequestMethod.GET)
	public String buyerDashboard(Model model) throws JsonProcessingException {
		model.addAttribute("days", 30);

		List<String> bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
		long pendingEventCount = rftEventService.findTotalPendingEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null, bizUnitIds);
		long scheduledEventCount = rftEventService.findTotalActiveEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null, bizUnitIds);
		long ongoingEventCount = rftEventService.findTotalOngoingEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null, bizUnitIds);
		long finishedEventCount = rftEventService.findTotalFinishedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), -1, null,bizUnitIds);
		long suspendedEventCount = rftEventService.findTotalSuspendedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null,bizUnitIds);
		long completedEventCount = rftEventService.findTotalCompletedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null,bizUnitIds);
		long cancelledEventCount = rftEventService.findTotalCancelledEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null,bizUnitIds);
		long closedEventCount = rftEventService.findTotalClosedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null,bizUnitIds);
		long approvedRequestCount = sourcingFormRequestService.myApprovedRequestListCountBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null,bizUnitIds);
		long finishedRequestCount = sourcingFormRequestService.finishedRequestCountBizUnit(SecurityLibrary.getLoggedInUserTenantId(), null, getAdminUserId(),bizUnitIds);
		long cancelRequestCount = sourcingFormRequestService.getCancelRequestCountBizUnit(SecurityLibrary.getLoggedInUserTenantId(), null, getAdminUserId(),bizUnitIds);

		long draftEventCount = rftEventService.findTotalDraftEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null);
		long myApprovalsCount = rftEventService.findTotalMyPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long myPendingEvalCount = rftEventService.findTotalMyPendingEvaluation(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null);
		long mySpEvaluationCount = spEvaluationService.findTotalPendingSPEvaluation(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null);
		long spEvaluationAppCount = spEvaluationService.findCountOfSPEvaluationPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null);
		long myAwardApprovalsCount = rftEventService.findTotalMyPendingAwardApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long formDraftCount = sourcingFormRequestService.myDraftRequestListCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null);
		long formPendingCount = sourcingFormRequestService.myPendingRequestListCount(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), null,bizUnitIds);
		long completeRequestCount = sourcingFormRequestService.myPendingRequestListCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null,bizUnitIds);
		long readyPoCount = poService.findPoCountBasedOnStatusAndTenant(getAdminUserId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.READY);
		long orderedPoCount = poService.findPoCountBasedOnStatusAndTenant(getAdminUserId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.ORDERED);
		long totalFilteredBudgetCount = budgetService.findTotalBudgetForApproval(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null);
		long suspEvntAppCount = rftEventService.findCountOfSuspendedEventPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long revisePoAppCount = poService.countRevisePo(getAdminUserId(), SecurityLibrary.getLoggedInUserTenantId(), new TableDataInput());
		long myPoApprovalCount = poService.countPendingPo(getAdminUserId(), SecurityLibrary.getLoggedInUserTenantId(), new TableDataInput());
		long myCancelPoApprovalCount = poService.countPendingCancelPo(getAdminUserId(), SecurityLibrary.getLoggedInUserTenantId(), new TableDataInput());
		long contractListAppCount = productContractService.findCountOfContractPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), null);
		long eventAppCount = rftEventService.findTotalEventMyPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long awardAppCount = rftEventService.findTotalMyPendingAwardApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long prAppCount = rftEventService.findTotalPrMyPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long poAppCount = rftEventService.findTotalPrMyPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long rfsAppCount = rftEventService.findTotalRfsMyPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long sfAppCount = rftEventService.findTotalSupplierFormMyPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		long myApprovalsTotal =  myApprovalsCount + totalFilteredBudgetCount + suspEvntAppCount + revisePoAppCount +
				spEvaluationAppCount + contractListAppCount + myPoApprovalCount + myCancelPoApprovalCount;

		model.addAttribute("draftPrCount",  formDraftCount);
		model.addAttribute("pendingPrCount",  formPendingCount);
		model.addAttribute("completeRequestCount", completeRequestCount);
		model.addAttribute("formDraftCount", formDraftCount);
		model.addAttribute("formPendingCount", formPendingCount);
		model.addAttribute("finishedRequestCount", finishedRequestCount);
		model.addAttribute("ongoingEventCount", ongoingEventCount);
		model.addAttribute("draftEventCount", draftEventCount);
		model.addAttribute("pendingEventCount", pendingEventCount);
		model.addAttribute("scheduledEventCount", scheduledEventCount);
		model.addAttribute("finishedEventCount", finishedEventCount + finishedRequestCount + approvedRequestCount);
		model.addAttribute("finishedEvent", finishedEventCount);
		model.addAttribute("myAwardApprovalsCount", myAwardApprovalsCount);
		model.addAttribute("suspendedEventCount", suspendedEventCount);
		model.addAttribute("cancelledEventCount", cancelledEventCount + cancelRequestCount);
		model.addAttribute("closedEventCount", closedEventCount);
		model.addAttribute("completedEventCount", completedEventCount);
		model.addAttribute("cancelledEvent", cancelledEventCount);
		model.addAttribute("myApprovalsCount", myApprovalsTotal);
		model.addAttribute("myPoApprovalCount", myPoApprovalCount);
		model.addAttribute("myCancelPoApprovalCount", myCancelPoApprovalCount);
		model.addAttribute("myEvaluationCount", myPendingEvalCount + mySpEvaluationCount);
		model.addAttribute("myPendingEvalCount", myPendingEvalCount);
		model.addAttribute("approvedRequestCount", approvedRequestCount);
		model.addAttribute("cancelRequestCount", cancelRequestCount);
		model.addAttribute("readyPoCount", readyPoCount);
		model.addAttribute("orderedPoCount", orderedPoCount);
		model.addAttribute("eventAppCount", eventAppCount);
		model.addAttribute("awardAppCount", awardAppCount);
		model.addAttribute("prAppCount", prAppCount);
		model.addAttribute("poAppCount",poAppCount);
		model.addAttribute("rfsAppCount", rfsAppCount);
		model.addAttribute("sfAppCount", sfAppCount);
		model.addAttribute("budgetAppCount", totalFilteredBudgetCount);
		model.addAttribute("suspEvntAppCount", suspEvntAppCount);
		model.addAttribute("revisePoAppCount", revisePoAppCount);
		model.addAttribute("mySpEvaluationCount", mySpEvaluationCount);
		model.addAttribute("spEvaluationAppCount", spEvaluationAppCount);
		model.addAttribute("contractListAppCount" , contractListAppCount);

		LOG.info(">>>> PENDING EVAL COUNT " + myPendingEvalCount+">>>>> MY SP EVAL"+mySpEvaluationCount);
		logModelContent("buyerDashboard", model);
		return "buyerDashboard";
	}

	private String getAdminUserId() {
		return SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ?
				null : SecurityLibrary.getLoggedInUser().getId();
	}

	private void logModelContent(String callerMethodName, Model model) {
		if ( model != null ) {
			for ( Map.Entry<String, Object> entry : model.asMap().entrySet() ) {
				LOG.info("{} -> {} = {}", callerMethodName, entry.getKey(), entry.getValue());
			}
		}
	}

	@RequestMapping(value = "/extendSession", method = RequestMethod.GET)
	public ResponseEntity<String> extendSession(Model model) {
		return new ResponseEntity<String>("{\"id\" : \"Success\"}", HttpStatus.OK);
	}

	@RequestMapping(value = "/dynamic", method = RequestMethod.GET)
	public ResponseEntity<List<DynamicColumnsData>> dynamic() {

		List<DynamicColumnsData> tables = new ArrayList<DynamicColumnsData>();

		for (int i = 1; i <= 3; i++) {
			DynamicColumnsData data = new DynamicColumnsData();
			List<List<String>> columns = new ArrayList<List<String>>();
			for (int j = 1; j <= 3; j++) {
				List<String> cols = new ArrayList<String>();
				cols.add("Column " + j);
				columns.add(cols);
			}

			List<List<String>> values = new ArrayList<List<String>>();
			List<String> rowData = new ArrayList<String>();
			rowData.add("value 1");
			rowData.add("value 2");
			rowData.add("value 3");

			values.add(rowData);
			data.setColumns(columns);
			data.setData(values);
			data.setTableId("table" + i);

			tables.add(data);
		}

		return new ResponseEntity<List<DynamicColumnsData>>(tables, HttpStatus.OK);
	}

	@ModelAttribute("rfxList")
	public List<RfxTypes> getStatusList() {
		return Arrays.stream(RfxTypes.values())
				.filter(type -> type != RfxTypes.PO) // Exclude PO
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/suspendedList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> buyerSuspendedList(TableDataInput input) {
		TableData<DraftEventPojo> data = null;
		List<String> bizUnitIds =null;
		try {
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());

			data = new TableData<DraftEventPojo>(rftEventService.getAllSuspendedEventsForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(),bizUnitIds));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalSuspendedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

			LOG.info("  suspendedList  :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/cancelledList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> buyerCancelledList(TableDataInput input) {
		LOG.info("/cancelledList LOADEDDDD ");
		TableData<DraftEventPojo> data = null;
		List<String> bizUnitIds =null;
		try {
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);
			//data = new TableData<DraftEventPojo>(rftEventService.getAllCancelledEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId()));
			data = new TableData<DraftEventPojo>(rftEventService.getAllCancelledEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(),bizUnitIds));
			data.setDraw(input.getDraw());

			//long totalCount = rftEventService.findTotalCancelledEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input);
			long totalCount = rftEventService.findTotalCancelledEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

			LOG.info(" cancelledList totalCount  :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading cancelled list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/closedList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> buyerClosedList(TableDataInput input) {

		TableData<DraftEventPojo> data = null;
		List<String> bizUnitIds =null;
		try {
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());

			data = new TableData<>(rftEventService.getAllClosedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(),bizUnitIds));

			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalClosedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

			LOG.info(" closed List totalCount  :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading closed list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/completedList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> buyerCompletedList(TableDataInput input) {
		LOG.info(" EVALUATED>>>completed list:>>>>>>>> ");
		TableData<DraftEventPojo> data = null;
		List<String> bizUnitIds =null;
		try {
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);
			//data = new TableData<DraftEventPojo>(rftEventService.getAllCompletedEventsForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId()));
			data = new TableData<DraftEventPojo>(rftEventService.getAllCompletedEventsForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(),bizUnitIds));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalCompletedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);
			//long totalCount = rftEventService.findTotalCompletedEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

			LOG.info(" Complete List totalCount  :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading Complete list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/draftList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> buyerDraftList(TableDataInput input) {
		TableData<DraftEventPojo> data = null;
		try {
			data = new TableData<DraftEventPojo>(rftEventService.getAllDraftEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());
			long totalCount = rftEventService.findTotalDraftEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
			// LOG.info(" totalCount :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/ongoingEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<OngoingEventPojo>> buyerOngoingList(TableDataInput input) {
		List<OngoingEventPojo> ongoingEventList = null;
		TableData<OngoingEventPojo> data = null;
		List<String> bizUnitIds = null;
		long totalCount = 0;
		try {
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			 ongoingEventList = rftEventService.getAllOngoingEventsForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getLastLoginTime(), input, getAdminUserId(), bizUnitIds);
			 totalCount = rftEventService.findTotalOngoingEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), input, bizUnitIds);
			data = new TableData<OngoingEventPojo>(ongoingEventList, totalCount);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<OngoingEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/finishedEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> buyerFinishedList(@RequestParam int days, TableDataInput input) {
		LOG.info(">>> FINISHED STATUS: is this loaded for dashboard?");
		List<DraftEventPojo> finishedEventList = null;
		TableData<DraftEventPojo> data = null;
		long totalCount = 0;
		try {
			List<String> bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());

			finishedEventList = rftEventService.getAllFinishedEventsForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getLastLoginTime(), days, input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(),bizUnitIds);
			totalCount = rftEventService.findTotalFinishedEventForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), days, input,bizUnitIds);

			data = new TableData<DraftEventPojo>(finishedEventList, totalCount);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/activeEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<ActiveEventPojo>> buyerActiveEventList(TableDataInput input) {
		TableData<ActiveEventPojo> data = null;
		List<String> bizUnitIds = null;
		try {
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("TableDataInput " + input);
//			data = new TableData<ActiveEventPojo>(rftEventService.getAllActiveEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getLastLoginTime(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId()));
			 data = new TableData<ActiveEventPojo>(rftEventService.getAllActiveEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getLastLoginTime(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), bizUnitIds));
			data.setDraw(input.getDraw());
//			long totalCount = rftEventService.findTotalActiveEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input);
			 long totalCount = rftEventService.findTotalActiveEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<ActiveEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/pendingPrList", method = RequestMethod.GET)
	public ResponseEntity<TableData<Pr>> buyerPendingPrList(TableDataInput input) {
		// LOG.info(" input : " + input + " SecurityLibrary.getLoggedInUserTenantId() :" +
		// SecurityLibrary.getLoggedInUserTenantId());
		TableData<Pr> data = null;
		try {
			data = new TableData<Pr>(prService.findAllPendingPr(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long recordFiltered = prService.findTotalFilteredPendingPr(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input);
			// long totalCount = prService.findTotalPr(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
		} catch (Exception e) {
			LOG.error("Error while loading Pr pending list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<Pr>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/pendingEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<PendingEventPojo>> pendingEventList(TableDataInput input) {
		TableData<PendingEventPojo> data = null;
		List<String> bizUnitIds = null;
		try {
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			data = new TableData<PendingEventPojo>(rftEventService.getAllPendingEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getLastLoginTime(), input, getAdminUserId(), bizUnitIds));
			data.setDraw(input.getDraw());
			long totalCount = rftEventService.findTotalPendingEventForBuyerBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), input, bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<PendingEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myPendingEventApprovalList", method = RequestMethod.GET)
	public ResponseEntity<TableData<PendingEventPojo>> myPendingEventApprovalList(TableDataInput input) {
		TableData<PendingEventPojo> data = null;
		try {
			data = new TableData<PendingEventPojo>(rftEventService.findMyPendingRfxApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = rftEventService.findTotalMyPendingRfxApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my pending event approval list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<PendingEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myPendingPrApprovalList", method = RequestMethod.GET)
	public ResponseEntity<TableData<PendingEventPojo>> myPendingPrApprovalList(TableDataInput input) {

		LOG.info("myPendingPrApprovalList input : " + input + " SecurityLibrary.getLoggedInUserTenantId() :" + SecurityLibrary.getLoggedInUserTenantId());
		TableData<PendingEventPojo> data = null;
		try {
			data = new TableData<PendingEventPojo>(rftEventService.findMyPendingPrApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = rftEventService.findTotalMyPendingPrApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading mt pending pr approval list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<PendingEventPojo>>(data, HttpStatus.OK);
	}


	@RequestMapping(value = "/myPendingPoApprovalList", method = RequestMethod.GET)
	public ResponseEntity<TableData<Po>> myPendingPoList(TableDataInput input) {
		LOG.info("myPendingPoList input : " + input + " SecurityLibrary.getLoggedInUserTenantId() :" + SecurityLibrary.getLoggedInUserTenantId());
		TableData<Po> myPendingPoList = null;
		try {
			myPendingPoList = new TableData<Po>(poService.findPendingPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input));
			myPendingPoList.setDraw(input.getDraw());
			long recordFiltered = poService.countPendingPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input);
			// long totalCount = prService.findTotalPr(SecurityLibrary.getLoggedInUserTenantId());
			myPendingPoList.setRecordsFiltered(recordFiltered);
			myPendingPoList.setRecordsTotal(recordFiltered);
		} catch (Exception e) {
			LOG.error("Error while loading Po pending list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<Po>>(myPendingPoList, HttpStatus.OK);
	}

	@RequestMapping(value = "/myPendingCancelPoApprovalList", method = RequestMethod.GET)
	public ResponseEntity<TableData<Po>> myPendingCancelPoApprovalList(TableDataInput input) {
		LOG.info("myPendingCancelPoApprovalList input : " + input + " SecurityLibrary.getLoggedInUserTenantId() :" + SecurityLibrary.getLoggedInUserTenantId());
		TableData<Po> myPendingCancelPoApprovalList = null;
		try {
			myPendingCancelPoApprovalList = new TableData<>(
					poService.findPendingCancelPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input));
			long recordFiltered = poService.countPendingCancelPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input);
			myPendingCancelPoApprovalList.setRecordsFiltered(recordFiltered);
			myPendingCancelPoApprovalList.setRecordsTotal(recordFiltered);
		} catch (Exception e) {
			LOG.error("Error while loading Po Cancel list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<Po>>(myPendingCancelPoApprovalList, HttpStatus.OK);
	}

	@RequestMapping(value = "/myPendingEvaluationList", method = RequestMethod.GET)
	public ResponseEntity<TableData<PendingEventPojo>> myPendingEvaluationList(TableDataInput input) {
		TableData<PendingEventPojo> data = null;
		try {
			data = new TableData<PendingEventPojo>(rftEventService.findMyPendingEvaluation(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = rftEventService.findTotalMyPendingEvaluation(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my pending evaluation list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<PendingEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myPendingRequestApprovalList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myPendingRequestApprovalList(TableDataInput input) {
		TableData<SourcingFormRequestPojo> data = null;
		try {
			data = new TableData<SourcingFormRequestPojo>(sourcingFormRequestService.findTotalMyPendingRequestList(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.findTotalMyPendingRequestCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my pending Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myDraftRequestList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myDraftRequestList(TableDataInput input) {
		TableData<SourcingFormRequestPojo> data = null;
		try {

			List<SourcingFormRequestPojo> list = sourcingFormRequestService.myDraftRequestPojoList(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data = new TableData<SourcingFormRequestPojo>(list);
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.myDraftRequestListCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Draft Request ListCount Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myApprvedRequestList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myApprvedRequestList(TableDataInput input) {
		TableData<SourcingFormRequestPojo> data = null;
		try {
			LOG.info("APPROVED RQUEST FORMS TAB >>>. ");
			List<String> bizUnitIds =null;
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("MY APPRVEDLIST:  RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);
			List<SourcingFormRequestPojo> list = sourcingFormRequestService.myApprvedRequestListBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);

			data = new TableData<>(list);
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.myApprovedRequestListCountBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);
			LOG.info("Total count +++++++++++++++++++++++++++ " + totalCount);

			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Draft Request ListCount Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myPendingRequestAppList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myPendingRequestList(TableDataInput input) {
		TableData<SourcingFormRequestPojo> data = null;
		try {
			data = new TableData<SourcingFormRequestPojo>(sourcingFormRequestService.myPendingRequestAppList(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.myPendingRequestAppListCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Pending RequestList Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myPendingRequestList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myPendingApproveRequestList(TableDataInput input) {
		TableData<SourcingFormRequestPojo> data = null;

		List<String> bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
		try {
			data = new TableData<SourcingFormRequestPojo>(sourcingFormRequestService.myPendingRequestList(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds));
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.myPendingRequestListCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Pending RequestList Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myCompleteRequestList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myCompleteRequestList(TableDataInput input) {
		TableData<SourcingFormRequestPojo> data = null;
		List<String> bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
		try {
			data = new TableData<SourcingFormRequestPojo>(sourcingFormRequestService.myCompletedRequestList(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_REQUEST_ALL") ? null : SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.myPendingRequestListCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_REQUEST_ALL") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
			LOG.info(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Pending RequestList Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myCancelRequestList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myCancelRequestList(TableDataInput input) {
		TableData<SourcingFormRequestPojo> data = null;
		try {
			LOG.info("myCancelRequestList TAB >>>. ");
			List<String> bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("myCancelRequestList:  RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);

			List<SourcingFormRequestPojo> list = sourcingFormRequestService.myCancelRequestListBizUnit(SecurityLibrary.getLoggedInUserTenantId(), getAdminUserId(), input ,bizUnitIds);

			data = new TableData<SourcingFormRequestPojo>(list);
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.getCancelRequestCountBizUnit(SecurityLibrary.getLoggedInUserTenantId(), input, getAdminUserId(),bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Draft Request ListCount Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myFinishRequestList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> myFinishRequestList(TableDataInput input) {

		TableData<SourcingFormRequestPojo> data = null;
		try {
			List<String> bizUnitIds =null;
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			List<SourcingFormRequestPojo> list = sourcingFormRequestService.myFinishRequestListBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input,bizUnitIds);

			data = new TableData<SourcingFormRequestPojo>(list);
			data.setDraw(input.getDraw());
			long totalCount = sourcingFormRequestService.finishedRequestCountBizUnit(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(),bizUnitIds);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Draft Request ListCount Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/budgetData", method = RequestMethod.GET)
	public ResponseEntity<TableData<BudgetPojo>> budgetTemplateData(TableDataInput input) {
		TableData<BudgetPojo> data = null;
		try {
			data = new TableData<BudgetPojo>(budgetService.getAllBudgetForApproval(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			LOG.info("***************budget query data" + data.getRecordsTotal());
			long totalFilteredCount = budgetService.findTotalBudgetForApproval(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsFiltered(totalFilteredCount);
			LOG.info("***************totalFilteredCount" + totalFilteredCount);
			long totalCount = budgetService.findTotalCountBudgetForApproval(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalFilteredCount);
			LOG.info(" totalCount :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading budget list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<BudgetPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/readyPoData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Po>> readyPoData(TableDataInput input, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			List<Po> poList = poService.findAllPoByStatus(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.READY);
			TableData<Po> data = new TableData<Po>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = poService.findTotalFilteredPoByStatus(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.READY);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<Po>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching ready po list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching ready PO list : " + e.getMessage());
			return new ResponseEntity<TableData<Po>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/orderedPoData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Po>> orderedPoData(TableDataInput input, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			List<Po> poList = poService.findAllPoByStatus(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.ORDERED);
			TableData<Po> data = new TableData<Po>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = poService.findTotalFilteredPoByStatus(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.ORDERED);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<Po>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching orders po list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching orders PO list : " + e.getMessage());
			return new ResponseEntity<TableData<Po>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/myPendingSupplierList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierFormSubmissionPojo>> myPendingSupplierFormList(TableDataInput input) {
		TableData<SupplierFormSubmissionPojo> data = null;
		try {
			data = new TableData<SupplierFormSubmissionPojo>(supplierFormSubmissionService.myPendingRequestList(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = supplierFormSubmissionService.myPendingSupplierFormListCount(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my Pending Supplier form Request list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/suspendedEventPendingApprList", method = RequestMethod.GET)
	public ResponseEntity<TableData<PendingEventPojo>> suspendedEventPendingApprovalList(TableDataInput input) {
		TableData<PendingEventPojo> data = null;
		try {
			data = new TableData<PendingEventPojo>(rftEventService.findSuspendedEventsPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = rftEventService.findCountOfSuspendedEventsPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading Suspended events Pending approval list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<PendingEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/myRevisePoApprovalData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Po>> revisePoApprovalList(TableDataInput input) {

		LOG.info("revisePoApprovalData input : " + input + " SecurityLibrary.getLoggedInUserTenantId() :" + SecurityLibrary.getLoggedInUserTenantId());
		TableData<Po> data = null;
		try {
			data = new TableData<>(poService.findRevisePo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalCount = poService.countRevisePo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading revise Po approval list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<Po>>(data, HttpStatus.OK);
	}
	@RequestMapping(value = "/pendingPoList", method = RequestMethod.GET)
	public ResponseEntity<TableData<Po>> buyerPendingPoList(TableDataInput input) {
		 LOG.info(" input : " + input + " SecurityLibrary.getLoggedInUserTenantId() :" + SecurityLibrary.getLoggedInUserTenantId());
		TableData<Po> data = null;
		try {
			data = new TableData<Po>(poService.findAllPendingPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long recordFiltered = poService.findTotalFilteredPendingPo(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input);
			// long totalCount = prService.findTotalPr(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
		} catch (Exception e) {
			LOG.error("Error while loading Po pending list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<Po>>(data, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/spEvaluationList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPerformanceEvaluationPojo>> spEvaluationList(TableDataInput input) {
		TableData<SupplierPerformanceEvaluationPojo> data = null;
		try {
			data = new TableData<SupplierPerformanceEvaluationPojo>(spEvaluationService.findPendingSPEvaluation(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = spEvaluationService.findTotalPendingSPEvaluation(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading supplier performance evaluation list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/spEvaluationApprovalList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPerformanceEvaluationPojo>> spEvaluationApprovalList(TableDataInput input) {
		TableData<SupplierPerformanceEvaluationPojo> data = null;
		try {
			data = new TableData<SupplierPerformanceEvaluationPojo>(spEvaluationService.findSPEvaluationPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = spEvaluationService.findCountOfSPEvaluationPendingApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading supplier performance Pending approval list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/myPendingAwardApprovalList", method = RequestMethod.GET)
	public ResponseEntity<TableData<PendingEventPojo>> myPendingAwardApprovalList(TableDataInput input) {
		TableData<PendingEventPojo> data = null;
		try {
			data = new TableData<PendingEventPojo>(rftEventService.findMyPendingRfxAwardApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalCount = rftEventService.findTotalMyPendingRfxAwardApprovals(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading my pending event approval list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<PendingEventPojo>>(data, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/contractApprovalsData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> contractData(TableDataInput input) {
		TableData<ProductContractPojo> data = null;
		try {
			data = new TableData<ProductContractPojo>(productContractService.getAllContractForApproval(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input));
			data.setDraw(input.getDraw());
			long totalFilteredCount = productContractService.findTotalFilteredContracForApproval(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsFiltered(totalFilteredCount);
			long totalCount = productContractService.findTotalCountContractForApproval(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input);
			data.setRecordsTotal(totalFilteredCount);
			LOG.info(" totalCount :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading contract list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
	}
}