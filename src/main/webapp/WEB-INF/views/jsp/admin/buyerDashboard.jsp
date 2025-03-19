<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication var="lastLoginTime" property="principal.lastLoginTime" />
<spring:message var="buyerDashboardDesk" code="application.buyer.dashboard" />
<sec:authentication property="principal.isBuyerErpEnable" var="isBuyerErpEnable" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />

<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerDashboardDesk}] });
});
<c:if test="${empty lastLoginTime}">	
zE(function() {
	  zE.activate();
	});
</c:if>
</script>
<style>
.border-all-side.document-table span {
	display: block;
	width: 100%;
}

.w20 {
	border: 1px solid #adadad;
	border-radius: 3px;
	height: 23px;
	width: 40px;
}

.marg-bottom-10 {
	margin-bottom: 9px !important;
}

.display-flex {
	display: inline-flex;
}

.nav>li>a {
    padding: 10px 10px;
}

</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/chardinjs.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chardin/chardinjs.min.js"/>"></script>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap Events-Listing-heading">
					<spring:message code="application.dashboard" />
				</h2>
			</div>
			<!-- page title block -->
			<div class="row clearfix">
				<div class="col-sm-12"><jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp"></jsp:include>
				</div>
			</div>
			<div class="Section-title title_border white-bg pad_all_15">
				<!-- <div class="Section-title title_border gray-bg">
					<h2 class="trans-cap Events-Listing-heading">Events Listing</h2>
				</div> -->

				<div class="Invited-Supplier-List dashboard-main">
					<jsp:include page="/WEB-INF/views/jsp/buyer/buyerDasboard/dashboard.jsp" />
				</div>

				<!-- ~~~~~~~~~~~~~~~~~~~ -->
				<!-- My Tasks -->
				<!-- ~~~~~~~~~~~~~~~~~~~ -->
				
				<div class="Invited-Supplier-List dashboard-main tabulerDataList myTaskData">

					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#tab1" data-toggle="tab">Event Approvals (${eventAppCount})</a></li>
						<li><a href="#tab9" data-toggle="tab">Award Approvals (${awardAppCount})</a></li>
						<li><a href="#tab2" data-toggle="tab">Sourcing Approvals (${rfsAppCount})</a></li>
						<li><a href="#tab3" data-toggle="tab">Budget Approvals (${budgetAppCount})</a></li>
						<li><a href="#tab4" data-toggle="tab">PR Approval (${prAppCount})</a></li>

						<!-- USECASE 1.3.12 FROM PH-4106 -->
						<li><a href="#tab11" data-toggle="tab">PO Approval (${myPoApprovalCount})</a></li>
						<li><a href="#tab7" data-toggle="tab">Revise PO Approval (${revisePoAppCount})</a></li>
						<li><a href="#tab12" data-toggle="tab">Cancel PO Approval (${myCancelPoApprovalCount})</a></li>
						<li><a href="#tab13" data-toggle="tab">GR Approval  </a></li>
						<li><a href="#tab14" data-toggle="tab">Invoice Approval </a></li>
						<li><a href="#tab15" data-toggle="tab">CN & DN Approval </a></li>
						<!-- USECASE 1.3.12 FROM PH-4106 -->
						<li><a href="#tab5" data-toggle="tab">Supplier Forms Approvals (${sfAppCount})</a></li>
						<li><a href="#tab6" data-toggle="tab">Suspend Event Approval (${suspEvntAppCount})</a></li>
						<li><a href="#tab8" data-toggle="tab">Supplier Performance Approval (${spEvaluationAppCount})</a></li>
						<li><a href="#tab10" data-toggle="tab">Contract Approval (${contractListAppCount})</a></li>
					
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="tab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.event.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingEventList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab2">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.sourcing.awaiting" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingSourcingRequestApprovalList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.nameofrequest" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestowner" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab3">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">Budget Awaiting Approval</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myBudgetApprovalList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="width_100 width_100_fix"><spring:message code="application.action" /></th>
													<!--<spring:message code="application.action" />  -->
													<th search-type="text" class="align-left width_200 width_200_fix">Budget Id</th>
													<th search-type="text" class="align-left width_200 width_200_fix">Budget Name</th>
													<th search-type="text" class="align-left width_200 width_200_fix">Business Unit</th>
													<th search-type="text" class="align-left width_200 width_200_fix">Cost Center</th>
													<th search-type="" class="align-left width_200 width_200_fix">Valid From</th>
													<th search-type="" class="align-left width_200 width_200_fix">Valid To</th>
													<th search-type="" class="align-right width_200 width_200_fix">Total Amount</th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab7">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">Revise PO Awaiting Approval</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="revisePoApprovalList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="width_100 width_100_fix"><spring:message code="application.action" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix">PO Number</th>
													<th search-type="text" class="align-left width_200 width_200_fix">PO Name</th>
													<th search-type="text" class="align-left width_200 width_200_fix">PO Created By</th>
													<th search-type="" class="align-left width_200 width_200_fix">PO Created Date</th>
													<th search-type="" class="align-left width_200 width_200_fix">PO Currency</th>
													<th search-type="" class="align-right width_200 width_200_fix">PO Grand Total</th>
													<th search-type="text" class="align-left width_200 width_200_fix">Business Unit</th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab4">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.pr.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingPrList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<%-- 												<form id="idPrApprove" method="post" ModelAttribute="prPojo"> --%>
												<input type="hidden" name="prId" value="${pr.id}" id="prId">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<div id="prApproval1"></div>
												<div style="width: 200px; margin-top: 23px; z-index: 999;" class="col-md-2 col-sm-2 col-xs-12 col-lg-2 col-2-div">
													<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="approvePr">
														<spring:message code="application.approve.pr" />
													</button>
												</div>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="checkbox-stylling"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
													<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.prname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="pr.id" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.pr.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="pr.created.date" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.prowner" /></th>
													<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.pr.grandtotal" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
												<%-- 												</form> --%>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>

						<div class="tab-pane" id="tab5">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.sf.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingSupplierFormList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplierForm.name.label" /></th>
													<th search-type="text" class="align-left width_200"><spring:message code="supplierForm.description.label" /></th>
													<th search-type="text" class="align-left"><spring:message code="application.supplier" /></th>
													<th search-type="" class="align-left"><spring:message code="supplier.form.submitted.date" /></th>
												</tr>

											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>

						<div class="tab-pane" id="tab6">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.event.suspend.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="suspendedEventPendingList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab10">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.contract.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="contractPendingList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_50 width_200_fix"><spring:message code="product.contract.id" /></th>
													<th search-type="text" class="align-left width_50 width_200_fix"><spring:message code="product.contract.name" /></th>
													<th search-type="text" class="align-left width_50 width_200_fix"><spring:message code="product.contract.event.id" /></th>
													<th search-type="" class="align-left width_50 width_200_fix"><spring:message code="product.Contract.code" /></th>
													<th search-type="" class="align-left width_50 width_200_fix"><spring:message code="application.supplier" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
													<th search-type="" class="align-center width_200_fix"><spring:message code="contract.start.date" /></th>
													<th search-type="" class="align-center width_200_fix"><spring:message code="contract.end.date" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="product.Contract.groupCode" /></th>
													<th search-type="" class="align-center width_150_fix"><spring:message code="label.currency" /></th>
													<th search-type="" class="align-center width_150_fix"><spring:message code="product.Contract.totalContractValue" /></th>
													<th search-type="" class="align-center width_150_fix"><spring:message code="product.Contract.creator" /></th>
													<th search-type="" class="align-center width_150_fix"><spring:message code="application.createddate" /></th>
													<th search-type="" class="align-center width_150_fix"><spring:message code="application.status" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
							</div>
							
						<div class="tab-pane" id="tab8">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.event.sp.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="spEvaluationApprovalList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="defultMenu.sp.form.id" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.performance.reference.number" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.name" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="defultMenu.sp.evaluation.start.date" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="defultMenu.sp.evaluation.end.date" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventsummary.envelopes.evaluator" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="rfs.from.owner2" /></th>
													<th search-type="text" class="align-center width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						
						<div class="tab-pane" id="tab9">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.award.awaiting" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingAwardList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_200 width_200_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<!-- PH-4106 USECASE 1.3 -->
						<div class="tab-pane" id="tab11">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.poAwaitingApproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPoAwaitingApproval" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
											<tr class="tableHeaderWithSearch">
												<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.number" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.name" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.createdby" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.createddate" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.poCurrency" /></th>
												<th search-type=""  class="align-center width_100 width_100_fix"><spring:message code="buyer.dashboard.poGrandTotal" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.businessUnit" /></th>
											</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab12">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.cancelPOAwaitingApproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myCancelPoAwaitingApproval" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
											<tr class="tableHeaderWithSearch">
												<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.number" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.name" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.createdby" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.po.createddate" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.poCurrency" /></th>
												<th search-type=""  class="align-center width_100 width_100_fix"><spring:message code="buyer.dashboard.poGrandTotal" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.businessUnit" /></th>
											</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab13">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.grAwaitingApproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myGRAwaitingApproval" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
											<tr class="tableHeaderWithSearch">
												<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.grNumber" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.grName" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.grCreatedBy" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.grCreatedDate" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.grCurrency" /></th>
												<th search-type=""  class="align-center width_100 width_100_fix"><spring:message code="buyer.dashboard.grGrandTotal" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.businessUnit" /></th>
											</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab14">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.invoiceAwaitingApproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myInvoiceApproval" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
											<tr class="tableHeaderWithSearch">
												<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.invoiceNumber" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.invoiceName" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.invoiceCreatedBy" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.invoiceCreatedDate" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.invoiceCurrency" /></th>
												<th search-type=""  class="align-center width_100 width_100_fix"><spring:message code="buyer.dashboard.invoiceGrandTotal" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.businessUnit" /></th>
											</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="tab15">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.cndnAwaitingApproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myCNDNAwaitingApproval" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
											<tr class="tableHeaderWithSearch">
												<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
												<th search-type="select" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.type" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.cndnNumber" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.cndnDate" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.invoiceNumber" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.currency" /></th>
												<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.grandTotal" /></th>
												<th search-type="text" class="align-left width_100 width_100_fix"><spring:message code="buyer.dashboard.businessUnit" /></th>
											</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<!-- PH-4106 USECASE 1.3 -->
				</div>
				</div>

				<!-- ~~~~~~~~~~~~~~~~~~~ -->
				<!-- Pending Evaluation -->
				<!-- ~~~~~~~~~~~~~~~~~~~ -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myEvaluationData">
					<ul class="nav-responsive nav nav-tabs">
							<li class="active"><a href="#evalTab1" data-toggle="tab">Pending Evaluation (${myEvaluationCount})</a></li>
							<li><a href="#evalTab2" data-toggle="tab">Supplier Performance Evaluation (${mySpEvaluationCount})</a></li>
		           </ul>
					<div class="tab-content">
						<div class="tab-pane active" id="evalTab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-6">
											<spring:message code="buyer.dashboard.awaiting.evaluation" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingEvaluationList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-left width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="evalTab2">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-6">
											<spring:message code="sp.evaluation" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="spEvaluationList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="defultMenu.sp.form.id" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.performance.reference.number" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.name" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="defultMenu.sp.evaluation.start.date" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="defultMenu.sp.evaluation.end.date" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="rfs.from.owner2" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>




				<!-- ***************** -->
				<!-- EVENT DRAFTS -->
				<!-- ***************** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility draftsEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#drafttab1" data-toggle="tab">Draft Events (${draftEventCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="drafttab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.draft.events" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="draftList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.event.created" /></th>
													<th search-type="select" search-options="rfxList" class="align-left width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-left width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>



				<!-- ***************** -->
				<!-- PR/REQUEST DRAFTS -->
				<!-- ***************** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility draftsPrsData">
					<ul class="nav-responsive nav nav-tabs">
						<!--						<li><a href="#draftprtab1" data-toggle="tab">Draft PRs (${draftPr})</a></li>-->
						<li  class="active"><a href="#draftprtab2" data-toggle="tab">Draft Request Forms (${formDraftCount})</a></li>
					</ul>
					<div class="tab-content">
						<!--<div class="tab-pane active" id="draftprtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.prdraft" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="prDraftList" class="data  display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithPrDraftSearch">
													<th search-type="" class="for-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.name" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.name" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.description" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="pr.id" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.modifiedby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.modifieddate" /></th>
													<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.prgrandtotal" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>-->
						<div class="tab-pane  active" id="draftprtab2">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.reqform.draft" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myDraftRequestList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.nameofrequest" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestowner" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>



				<!-- ********************** -->
				<!-- EVENTS PENDING APPROVAL -->
				<!-- ********************** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility eventsWaitingData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#pendingtab1" data-toggle="tab">Pending Events (${pendingEventCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="pendingtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.event.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="pendingEventList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>


				<!-- **************** -->
				<!-- PR/FORM PENDING APPROVAL -->
				<!-- **************** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility prWaitingData">
					<ul class="nav-responsive nav nav-tabs">
						<!--<li class="active"><a href="#prpendingtab1" data-toggle="tab">Pending PRs (${pendingPr})</a></li>
						<li><a href="#prpendingtab3" data-toggle="tab">Pending Revise POs (${pendingPoCount})</a></li>-->
						<li class="active"><a href="#prpendingtab2" data-toggle="tab">Pending Request Forms (${formPendingCount})</a></li>
					</ul>
					<div class="tab-content">
						<!--<div class="tab-pane active" id="prpendingtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.pr.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="pendingPrList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.prname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="pr.id" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.pr.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="pr.created.date" /></th>
													<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.pr.grandtotal" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div> -->
						<div class="tab-pane active" id="prpendingtab2">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.requestform.awaitapproval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingRequestList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.nameofrequest" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestowner" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<!--<div class="tab-pane" id="prpendingtab3">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">Revise PO Awaiting Approval</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myPendingPoList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100 width_100_fix"><spring:message code="application.action" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix">PO Number</th>
													<th search-type="text" class="align-left width_200 width_200_fix">PO Name</th>
													<th search-type="text" class="align-left width_200 width_200_fix">PO Created By</th>
													<th search-type="" class="align-left width_200 width_200_fix">PO Created Date</th>
													<th search-type="text" class="align-left width_200 width_200_fix">PO Currency</th>
													<th search-type="" class="align-right width_200 width_200_fix">PO Grand Total</th>
													<th search-type="text" class="align-left width_200 width_200_fix">Business Unit</th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div> -->
					</div>
				</div>


				<!-- ********* -->
				<!-- SCHEDULED -->
				<!-- ********* -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility scheduledEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#scheduledtab1" data-toggle="tab">Scheduled Events (${scheduledEventCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="scheduledtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.scheduledevent" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="scheduledEventList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>



				<!-- ******* -->
				<!-- ONGOING -->
				<!-- ******* -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility activeEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#activetab1" data-toggle="tab">Ongoing Events (${ongoingEventCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="activetab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.ongoingevents" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="ongoingEventList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="" class="align-center width_100_fix"><spring:message code="buyer.eventReport.messagetab" /></th>
													<th search-type="" class="align-center width_150_fix"><spring:message code="buyer.dashboard.submissions" /></th>
													<th search-type="" class="align-center width_200 width_200_fix"><spring:message code="buyer.dashboard.accepted.supplier" /></th>
													<th search-type="" class="align-center width_150 width_150_fix"><spring:message code="buyer.dashboard.timeleft.label" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_200 width_200_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>


				<!-- ********* -->
				<!-- SUSPENDED -->
				<!-- ********* -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility suspendedEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#suspendedtab1" data-toggle="tab">Suspended Events (${suspendedEventCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="suspendedtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.suspended.events" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="suspendedList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- ****** -->
				<!-- CLOSED -->
				<!-- ****** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility closedEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#closedtab1" data-toggle="tab">Closed Events (${closedEventCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="closedtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.eventsended" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="closedList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.eventstart" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.eventend" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- ********* -->
				<!-- COMPLETED -->
				<!-- ********* -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility completedEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#completedtab1" data-toggle="tab">Completed Events (${completedEventCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="completedtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.evaluation.completed" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="completedList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- ******** -->
				<!-- FINISHED -->
				<!-- ******** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility finishedEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#finishedtab1" data-toggle="tab">Finished Events (${finishedEvent})</a></li>
						<li><a href="#finishedtab2" data-toggle="tab">Approved Request Forms (${approvedRequestCount})</a></li>
						<li><a href="#finishedtab3" data-toggle="tab">Finished Request Forms (${finishedRequestCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="finishedtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.completed.events" />
										</div>
										<!-- 								regarding PH 69 we have use type-hidden and search button  not show on dash-board 		 -->
										<!-- 								<div class="nopad col-md-8"> -->
										<input type="hidden" name="days" class="form-control width_100_fix" type="search" value="30" placeholder="e.g. 30" aria-controls="datatable-example" style="float: left; margin: 0 10px;" id="selectDays">
										<!-- 									<label style="padding-top: 10px; float: left;">Days</label> -->
										<!-- 									<button type="submit" class="btn btn-info ph_btn_small getnextdata hvr-pop hvr-rectangle-out" id="searchFinishedEvent" name="searchFinishedEvent" style="margin-left: 20px;">Search</button> -->
										<!-- 								</div> -->
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="finishedEventList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="finishedtab2">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.request.approval" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myCompleteRequestList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.nameofrequest" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestowner" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="finishedtab3">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.finished.request" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myFinishRequestList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.nameofrequest" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestowner" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- ********* -->
				<!-- CANCELLED -->
				<!-- ********* -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility cancelledEventsData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#cancelledtab1" data-toggle="tab">Cancelled Events (${cancelledEvent})</a></li>
						<li><a href="#cancelledtab2" data-toggle="tab">Cancelled Requests Forms (${cancelRequestCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="cancelledtab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.cancelled.events" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="cancelEventList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate" /></th>
													<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventowner" /></th>
													<th search-type="text" class="align-center width_150_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane" id="cancelledtab2">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class="ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.cancelled.request" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="myCancelRequestList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.nameofrequest" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestid" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestowner" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- ****** -->
				<!-- READY PO -->
				<!-- ****** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility poReadyData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#readypotab1" data-toggle="tab">Ready POs (${readyPoCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="readypotab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.poList.ready" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="readyPoList" class="data display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_150 width_150_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.number" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.name" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.supplier" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.currency" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.po.summary.business.unit" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- ********** -->
				<!-- ORDERED PO -->
				<!-- ********** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility poOrdersData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#orderedpotab1" data-toggle="tab">Ordered POs (${orderedPoCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="orderedpotab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.poList.ordered" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="orderedPoList" class="data display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_150 width_150_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.number" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.name" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.supplier" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.buyer.poList.orderby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="label.buyer.poList.orderDate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.currency" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.po.summary.business.unit" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>


				<!-- *********** -->
				<!-- Approved PR -->
				<!-- *********** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility prcheseOrdersData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#potab1" data-toggle="tab">Approved PRs (${prCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="potab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.pr.approved" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="poList" class="data display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_150 width_150_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.name" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.description" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.supplier" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.createddate" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.prgrandtotal" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- Approve Pr -->
				<div class="modal fade" id="modal-prApprove" role="dialog">
					<div class="modal-dialog for-delete-all reminder">
						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-header">
								<h3>
									<spring:message code="pr.approve.confirm" />
								</h3>
								<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
							</div>
							<form id="idPrMultipleApprove" method="post">
								<input type="hidden" name="prId" id="prId" value="${pr.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<div class="col-md-12">
									<div class="row">
										<div class="modal-body col-md-12">
											<label> <spring:message code="pr.remark" />
											</label>
										</div>
										<div class="form-group col-md-6">
											<textarea class="width-100" rows="3" name="remarks" id="remarks" maxlength="500"></textarea>
										</div>
									</div>
								</div>
								<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
									<input type="button" id="approveAllPr" class="btn btn-info" value="Approve">
								</div>
							</form>
						</div>
					</div>
				</div>


				<!-- *********** -->
				<!-- Transferred PR -->
				<!-- *********** -->
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility transferredPrData">
					<ul class="nav-responsive nav nav-tabs">
						<li class="active"><a href="#potransfertab1" data-toggle="tab">Transferred PRs (${prTransferCount})</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="potransfertab1">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="col-md-12">
										<div class="nopad table-heading col-md-4">
											<spring:message code="buyer.dashboard.pr.transfered" />
										</div>
									</div>
									<div class="main_table_wrapper ph_table_border pad_all_15">
										<table id="poTransferList" class="data display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-left width_150 width_150_fix"><spring:message code="application.action1" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.name" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.description" /></th>
													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.supplier" /></th>

													<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.createdby" /></th>
													<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.createddate" /></th>
													<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.prgrandtotal" /></th>
													<th search-type="text" class="align-right width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>



			</div>
		</div>

	</div>
</div>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>

<script type="text/javascript">

<%--<c:if test="${draftPrCount > 0 }">--%>
<%--	var draftsPrsData;--%>
<%--	$('document').ready(function() {--%>
<%--		draftsPrsData = $('#prDraftList').DataTable({--%>
<%--			 "oLanguage":{--%>
<%-- 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"--%>
<%--			}, --%>
<%--			"processing" : true,--%>
<%--			"serverSide" : true,--%>
<%--			"deferLoading": 0,--%>
<%--			"pageLength" : 10,--%>
<%--			"searching" : true,--%>
<%--			"language": {--%>
<%--				"emptyTable": "No data"--%>
<%--			},--%>
<%--			"preDrawCallback" : function(settings) {--%>
<%--			//	$('div[id=idGlobalError]').hide();--%>
<%--				$('#loading').show();--%>
<%--				return true;--%>
<%--			},--%>
<%--			"drawCallback" : function() {--%>
<%--				$('#loading').hide();--%>
<%--			},--%>
<%--			"ajax" : {--%>
<%--				"url" : getContextPath() + "/buyer/prDraftData",--%>
<%--				"data" : function(d) {--%>
<%--				},--%>
<%--				"error" : function(request, textStatus, errorThrown) {--%>
<%--					var error = request.getResponseHeader('error');--%>
<%--					if (error != undefined) {--%>
<%--						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");--%>
<%--						$('div[id=idGlobalError]').show();--%>
<%--					}--%>
<%--					$('#loading').hide();--%>
<%--				}--%>
<%--			},--%>
<%--			"order" : [[ 7, "desc" ]],--%>
<%--			"columns" : [ {--%>
<%--				"mData" : "id",--%>
<%--				"searchable" : false,--%>
<%--				"orderable" : false,--%>
<%--				"className" : "align-left",--%>
<%--				"mRender" : function(data, type, row) {--%>
<%--					return '<a href="createPrDetails/' + row.id + '" data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';--%>

<%--				}--%>
<%--			}, {--%>
<%--				"data" : "referenceNumber",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			},{--%>
<%--				"data" : "name",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "supplier.fullName",--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "description",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			},{--%>
<%--				"data" : "prId",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "createdBy.name",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "prCreatedDate",--%>
<%--				"searchable" : false,--%>
<%--				"className" : "align-left",--%>
<%--				"type": 'custom-date',--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "modifiedBy.name",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "modifiedDate",--%>
<%--				"searchable" : false,--%>
<%--				"className" : "align-left",--%>
<%--				"type": 'custom-date',--%>
<%--				"defaultContent" : ""--%>
<%--			--%>
<%--			}, {--%>
<%--				"data" : "grandTotal",--%>
<%--				"className" : "align-right",--%>
<%--				"defaultContent" : "",--%>
<%--				"mRender" : function(data, type, row) {--%>
<%--					return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));--%>
<%--				}--%>
<%--			}, {--%>
<%--				"data" : "businessUnit.unitName",--%>
<%--				"defaultContent" : ""--%>
<%--			} ],--%>
<%--			--%>
<%--			"initComplete": function(settings, json) {--%>
<%--				var htmlPrDraftSearch = '<tr class="tableHeaderWithSearch">';--%>
<%--				$('#prDraftList thead tr:nth-child(1) th').each(function(i) {--%>
<%--					var title = $(this).text();--%>
<%--					var classStyle =  $(this).attr("class");--%>
<%--					if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){--%>
<%--						classStyle = classStyle.replace('sorting','');--%>
<%--					}--%>
<%--					if (!(title == "Actions") && $(this).attr('search-type') != '') {--%>
<%--						htmlPrDraftSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';--%>
<%--					} else {--%>
<%--						htmlPrDraftSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';--%>
<%--					}--%>
<%--				});--%>
<%--				htmlPrDraftSearch += '</tr>';--%>
<%--				$('#prDraftList thead').append(htmlPrDraftSearch);--%>
<%--				--%>
<%--				$(draftsPrsData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {--%>
<%--					// ignore arrow keys--%>
<%--					switch (e.keyCode) {--%>
<%--					case 17: // CTRL--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 18: // ALT--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 37: // Arrow Left--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 38: // Arrow Up--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 39: // Arrow Right--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 40: // Arrow Down--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 32: // Space--%>
<%--						if($.trim(this.value).length <= 2) {--%>
<%--							return false;--%>
<%--						}--%>
<%--						break;--%>
<%--					}		--%>
<%--					if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {--%>
<%--						draftsPrsData.column($(this).data('index')).search($.trim(this.value)).draw();--%>
<%--					}--%>
<%--				}, 650));--%>
<%--			/* 	$(draftsPrsData.table().container()).on('keyup', 'thead input', function() {--%>
<%--					if ($.trim(this.value).length > 2 || this.value.length == 0) {--%>
<%--						draftsPrsData.column($(this).data('index')).search(this.value).draw();--%>
<%--					}--%>
<%--				}); */--%>
<%--				$(draftsPrsData.table().container()).on('change', 'thead select', function() {--%>
<%--					draftsPrsData.column($(this).data('index')).search(this.value).draw();--%>
<%--				}); --%>
<%--			}--%>
<%--		});--%>
<%--		--%>
<%--});--%>
<%--</c:if>--%>
<%--	--%>
	function ReplaceNumberWithCommas(yourNumber) {
		var n = yourNumber.toString().split(".");
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		return n.join(".");
	}


<c:if test="${completedEventCount > 0 }">
	var completedEventsData;
	$('document').ready(function() {
		completedEventsData = $('#completedList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/completedList",
				"data" : function(d) {
				}
			},
			"order" : [ [ 5, "desc" ] ],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" title="View" ><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "sysEventId",
				"className" : "align-left",
				"defaultContent" : ""
			},  {
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"className" : "align-center",
				"defaultContent" : "",
				"orderable" : false
			},{
				"data" : "eventUser",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 }],
		
			 "initComplete": function(settings, json) {
		 var htmlEventCompletedSearch = '<tr class="tableHeaderWithSearch">';
			$('#completedList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				//console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventCompletedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventCompletedSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventCompletedSearch += '</select></th>';
					} else {
						htmlEventCompletedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventCompletedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventCompletedSearch += '</tr>';
			//console.log("htmlEventCompletedSearch : " + htmlEventCompletedSearch);
			$('#completedList thead').append(htmlEventCompletedSearch);
			$(completedEventsData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					completedEventsData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			/* $(completedEventsData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					completedEventsData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(completedEventsData.table().container()).on('change', 'thead select', function() {
				completedEventsData.column($(this).data('index')).search(this.value).draw();
			}); 
			 }
		});
	});
</c:if>
	
<c:if test="${closedEventCount > 0 }">

	var closedEventsData;
	$('document').ready(function() {
		closedEventsData = $('#closedList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/closedList",
				"data" : function(d) {
					
				}
			},
			"order" : [ [ 5, "desc" ] ],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "referenceNumber",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "sysEventId",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"className" : "align-center",
				"defaultContent" : "",
				"orderable" : false
			}, {
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
		
		"initComplete": function(settings, json) {
		 var htmlEventClosedSearch = '<tr class="tableHeaderWithSearch">';
			$('#closedList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
		//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventClosedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventClosedSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventClosedSearch += '</select></th>';
					} else {
						htmlEventClosedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventClosedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventClosedSearch += '</tr>';
		//	console.log("htmlEventClosedSearch : " + htmlEventClosedSearch);
			$('#closedList thead').append(htmlEventClosedSearch);
			
			$(closedEventsData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					closedEventsData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(closedEventsData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					closedEventsData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(closedEventsData.table().container()).on('change', 'thead select', function() {
				closedEventsData.column($(this).data('index')).search(this.value).draw();
			}); 
		}
		});
	});
</c:if>

<c:if test="${ongoingEventCount > 0}">
	var activeEventsData;
	$('document').ready(function() {
		activeEventsData = $('#ongoingEventList').DataTable({
			 "oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			}, 
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/ongoingEventList",
				"data" : function(d) {
				}
			},
			"order" : [ [ 10, "desc" ] ],
			"columns" : [ {
				"data" : "id",
				"className" : "align-left pad-left-20",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var type = row.type;
					<c:if test="${buyerReadOnlyAdmin or isAdmin}">
						return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/viewSummary/'+row.id+'"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
					</c:if>
					<c:if test="${!(buyerReadOnlyAdmin or isAdmin)}">
						return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventProgress/'+row.id+'"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
					</c:if>
					
				},
			}, {
				"data" : "eventName",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "unreadMessageCount",
				"orderable" : false,
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var retVal = '<div class="for-relative noti-icon-messagefix">';
					retVal += '<a href="${pageContext.request.contextPath}/buyer/'+row.type+'/viewMailBox/'+row.id+'">';
					retVal += '<img src="${pageContext.request.contextPath}/resources/images/message.png">';
					if (row.unreadMessageCount > 0) {
						retVal += '<i class="noti-round-absolute bs-badge badge-absolute badge-blue-alt">' + row.unreadMessageCount + '</i></a>';
					}
					retVal += '</div>';
					return retVal;
				}
			}, {
				"data" : "totalBidCount",
				"orderable" : false,
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var retVal = '<div class="for-relative noti-icon-inputfix">';
					retVal += '<input type="text" value="' + row.totalBidCount + '" readonly="" class="w20">';
					if (row.unreadBidCount > 0) {
						retVal += '<i class="noti-round-absolute1 bs-badge badge-absolute bg-red">' + row.unreadBidCount + '</i>';
					}
					retVal += '</div>';
					return retVal;
				}
			}, {
				"data" : "totalSupplierCount",
				"orderable" : false,
				"defaultContent" : "",
				"render" : function(data, type, row) {
					return row.readSupplierCount + '/' + row.totalSupplierCount;
				}
			}, {
				"data" : "timeLeft",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "type",
				"defaultContent" : "",
				"orderable" : false
			},{
				"data" : "referenceNumber",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "sysEventId",
				"defaultContent" : "",
				"className" : "align-left"
			},{
				"data" : "eventStart",
				"defaultContent" : "",
				"type": 'custom-date',
				"className" : "align-left"
			},{
				"data" : "eventEnd",
				"defaultContent" : "",
				"type": 'custom-date',
				"className" : "align-left"
			},{
				"data" : "eventUser",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 }],
			"createdRow" : function(row, data, rowIndex) {
				// Per-cell function to do whatever needed with cells
				$.each($('td:not(:first)', row), function(colIndex) {
					// For example, adding data-* attributes to the cell
					$(this).attr('align', "center");
				});
			},

		"initComplete": function(settings, json) {
		 var htmlEventOngoingSearch = '<tr class="tableHeaderWithSearch">';
			$('#ongoingEventList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
			//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventOngoingSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventOngoingSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventOngoingSearch += '</select></th>';
					} else {
						htmlEventOngoingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventOngoingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventOngoingSearch += '</tr>';
		//	console.log("htmlEventDraftSearch : " + htmlEventOngoingSearch);
			$('#ongoingEventList thead').append(htmlEventOngoingSearch);
			
			$(activeEventsData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8 ) {
					activeEventsData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			$(activeEventsData.table().container()).on('change', 'thead select', function() {
				activeEventsData.column($(this).data('index')).search(this.value).draw();
			}); 
		}
		});
		
	});
</c:if>
	

<c:if test="${suspendedEventCount > 0}">
	var suspendedEventsData;
	$('document').ready(function() {
		suspendedEventsData = $('#suspendedList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/suspendedList?days=",
				"data" : function(d) {
				}
			},
			"order" : [ [ 5, "desc" ] ],
			"columns" : [ {
				"data" : "id",
				"className" : "align-left pad-left-20",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" title="Edit" ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "referenceNumber",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "sysEventId",
				"defaultContent" : "",
				"className" : "align-left"
			},{
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},  {
				"data" : "type",
				"className" : "align-center",
				"defaultContent" : "",
				"orderable" : false
			}, {
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			}, {
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 }

			],

		"initComplete": function(settings, json) {
		
		 var htmlEventSuspendedSearch = '<tr class="htmlEventSuspendedSearch">';
			$('#suspendedList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
			//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventSuspendedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventSuspendedSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventSuspendedSearch += '</select></th>';
					} else {
						htmlEventSuspendedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventSuspendedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventSuspendedSearch += '</tr>';
			//console.log("htmlEventSuspendedSearch : " + htmlEventSuspendedSearch);
			$('#suspendedList thead').append(htmlEventSuspendedSearch);
			
			$(suspendedEventsData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					suspendedEventsData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
		/* 	$(suspendedEventsData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					suspendedEventsData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(suspendedEventsData.table().container()).on('change', 'thead select', function() {
				suspendedEventsData.column($(this).data('index')).search(this.value).draw();
			});
		}
		});
	});
</c:if>
	
	
<c:if test="${scheduledEventCount > 0}">
	
	var scheduledEventsData;
	$('document').ready(function() {
		scheduledEventsData = $('#scheduledEventList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/activeEventList?days=",
				"data" : function(d) {
				}
			},
			"order" : [ [ 4, "asc" ] ],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"sClass" : "for-left pad-left-10",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "sysEventId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "type",
				"className" : "align-center",
				"defaultContent" : ""
			}, {
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
	
		"initComplete": function(settings, json) {
		 var htmlEventScheduledSearch = '<tr class="tableHeaderWithSearch">';
			$('#scheduledEventList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
		//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventScheduledSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventScheduledSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventScheduledSearch += '</select></th>';
					} else {
						htmlEventScheduledSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventScheduledSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventScheduledSearch += '</tr>';
		//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
			$('#scheduledEventList thead').append(htmlEventScheduledSearch);
			
			$(scheduledEventsData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					scheduledEventsData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
		/* 	$(scheduledEventsData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					scheduledEventsData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(scheduledEventsData.table().container()).on('change', 'thead select', function() {
				scheduledEventsData.column($(this).data('index')).search(this.value).draw();
			});
		}
		});
	});
</c:if>
	
	


<c:if test="${cancelledEventCount >0 }">
	var cancelledEventsData;
	$('document').ready(function() {
		cancelledEventsData = $('#cancelEventList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/cancelledList",
				"data" : function(d) {
					d.days = $("input[name='days']").val();
				}
			},
			"order" : [ [ 5, "desc" ] ],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"sClass" : "for-left pad-left-10",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referenceNumber",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "sysEventId",
				"defaultContent" : "",
				"className" : "align-left"
			},  {
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"className" : "align-center",
				"defaultContent" : "",
				"orderable" : false
			},{
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			}, {
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
		
		"initComplete": function(settings, json) {
		 var htmlEventCancelledSearch = '<tr class="tableHeaderWithSearch">';
			$('#cancelEventList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
			//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventCancelledSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventCancelledSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventCancelledSearch += '</select></th>';
					} else {
						htmlEventCancelledSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
					
				} else {
					htmlEventCancelledSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventCancelledSearch += '</tr>';
		//	console.log("htmlEventCancelledSearch : " + htmlEventCancelledSearch);
			$('#cancelEventList thead').append(htmlEventCancelledSearch);
			
			$(cancelledEventsData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					cancelledEventsData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(finishedEventsData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					finishedEventsData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(cancelledEventsData.table().container()).on('change', 'thead select', function() {
				cancelledEventsData.column($(this).data('index')).search(this.value).draw();
			});
		}
		});
	});
</c:if>


	

	<c:if test="${pendingEventCount > 0 }">
	var eventsWaitingData;
	$('document').ready(function() {
		eventsWaitingData = $('#pendingEventList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/pendingEventList?days=",
				"data" : function(d) {
				}
			},
			"order" : [ [ 4, "asc" ] ],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"sClass" : "for-left pad-left-10",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"className" : "align-center",
				"defaultContent" : "",
				"orderable" : false
			}, {
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			}, {
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 }],
			 
			 "initComplete": function(settings, json) {
				 var htmlEventPendingSearch = '<tr class="tableHeaderWithSearch">';
					$('#pendingEventList thead tr:nth-child(1) th').each(function(i) {
						var title = $(this).text();
					//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
						var classStyle =  $(this).attr("class");
						if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
							classStyle = classStyle.replace('sorting','');
						}
						if (!(title == "Actions") && $(this).attr('search-type') != '') {
							if ($(this).attr('search-type') == 'select') {
								var optionsType = $(this).attr('search-options');
								htmlEventPendingSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
								if (optionsType == 'rfxList') {
									<c:forEach items="${rfxList}" var="item">
									htmlEventPendingSearch += '<option value="${item}">${item}</option>';
									</c:forEach>
								}
								htmlEventPendingSearch += '</select></th>';
							} else {
								htmlEventPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
							}
						} else {
							htmlEventPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
						}
					});
					htmlEventPendingSearch += '</tr>';
				//	console.log("htmlEventPendingSearch : " + htmlEventPendingSearch);
					$('#pendingEventList thead').append(htmlEventPendingSearch);
					
					$(eventsWaitingData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
						// ignore arrow keys
						switch (e.keyCode) {
						case 17: // CTRL
							return false;
							break;
						case 18: // ALT
							return false;
							break;
						case 37: // Arrow Left
							return false;
							break;
						case 38: // Arrow Up
							return false;
							break;
						case 39: // Arrow Right
							return false;
							break;
						case 40: // Arrow Down
							return false;
							break;
						case 32: // Space
							if($.trim(this.value).length <= 2) {
								return false;
							}
							break;
						}		
						if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
							eventsWaitingData.column($(this).data('index')).search($.trim(this.value)).draw();
						}
					}, 650));
					
					/* $(eventsWaitingData.table().container()).on('keyup', 'thead input', function() {
						if ($.trim(this.value).length > 2 || this.value.length == 0) {
							eventsWaitingData.column($(this).data('index')).search(this.value).draw();
						}
					}); */
					$(eventsWaitingData.table().container()).on('change', 'thead select', function() {
						eventsWaitingData.column($(this).data('index')).search(this.value).draw();
					}); 
			 }
		});
	});
</c:if>



	

<%--<c:if test="${pendingPrCount > 0}">	--%>
<%--	var prWaitingData;--%>
<%--	$('document').ready(function() {--%>
<%--		prWaitingData = $('#pendingPrList').DataTable({--%>
<%--			"oLanguage":{--%>
<%--				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"--%>
<%--		},--%>
<%--		"processing" : true,--%>
<%--		"serverSide" : true,--%>
<%--		"deferLoading": 0,--%>
<%--		"pageLength" : 10,--%>
<%--		"searching" : true,--%>
<%--			"ajax" : {--%>
<%--				"url" : getContextPath() + "/buyer/pendingPrList",--%>
<%--				"data" : function(d) {--%>
<%--					/* var table = $('#pendingPrList').DataTable();--%>
<%--					d.page = (table != undefined && table.page.info() != undefined) ? table.page.info().page : 0;--%>
<%--					d.size = (table != undefined && table.page.info() != undefined) ? table.page.info().length : 10;--%>
<%--					d.sort = d.columns[d.order[0].column].data + ',' + d.order[0].dir; */--%>
<%--				}--%>
<%--			},--%>
<%--			"order" : [ [ 5, "desc" ] ],--%>
<%--			"columns" : [ {--%>
<%--				"data" : "id",--%>
<%--				"searchable" : false,--%>
<%--				"orderable" : false,--%>
<%--				"render" : function(data, type, row) {--%>
<%--					var type = row.type;--%>
<%--					var lockBudget=row.lockBudget;--%>
<%--						return '<a href="${pageContext.request.contextPath}/buyer/prView/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png">  </a>'--%>
<%--					}--%>
<%--			}, {--%>
<%--				"data" : "name",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : "",--%>
<%--				"render" : function(data, type, row) {--%>
<%--					var type = row.type;--%>
<%--					var lockBudget=row.lockBudget;--%>
<%--					if(lockBudget === true){--%>
<%--					return '<img class="marg-bottom-10" height="20" width="20" title="Locked"  src="${pageContext.request.contextPath}/resources/images/lock.png">'+ ' '+ row.name;--%>
<%--					}else{--%>
<%--						return row.name;--%>
<%--					}--%>
<%--				},--%>
<%--			},{--%>
<%--				"data" : "referenceNumber",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			},{--%>
<%--				"data" : "prId",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "createdBy.name",--%>
<%--				"className" : "align-left",--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "prCreatedDate",--%>
<%--				"className" : "align-left",--%>
<%--				"type": 'custom-date',--%>
<%--				"defaultContent" : ""--%>
<%--			}, {--%>
<%--				"data" : "grandTotal",--%>
<%--				"className" : "align-right",--%>
<%--				"defaultContent" : "",--%>
<%--				"mRender" : function(data, type, row) {--%>
<%--					return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));--%>
<%--				}--%>
<%--			},{--%>
<%--				"data" : "businessUnit.unitName",--%>
<%--				"className" : "align-center",--%>
<%--				"defaultContent" : ""--%>
<%--			 } ],--%>
<%--			 --%>
<%--			 "initComplete": function(settings, json) {--%>
<%--				 --%>
<%--				 var htmlEventPendingPrSearch = '<tr class="tableHeaderWithSearch">';--%>
<%--					$('#pendingPrList thead tr:nth-child(1) th').each(function(i) {--%>
<%--						var title = $(this).text();--%>
<%--					//	console.log("Title : " + title + " Class : " + $(this).attr("class"));--%>
<%--						var classStyle =  $(this).attr("class");--%>
<%--						if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){--%>
<%--							classStyle = classStyle.replace('sorting','');--%>
<%--						}--%>
<%--						if (!(title == "Actions") && $(this).attr('search-type') != '') {--%>
<%--							htmlEventPendingPrSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';--%>
<%--						} else {--%>
<%--							htmlEventPendingPrSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';--%>
<%--						}--%>
<%--					});--%>
<%--					htmlEventPendingPrSearch += '</tr>';--%>
<%--				//	console.log("htmlEventPendingPrSearch : " + htmlEventPendingPrSearch);--%>
<%--					$('#pendingPrList thead').append(htmlEventPendingPrSearch);--%>
<%--					--%>
<%--					$(prWaitingData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {--%>
<%--						// ignore arrow keys--%>
<%--						switch (e.keyCode) {--%>
<%--						case 17: // CTRL--%>
<%--							return false;--%>
<%--							break;--%>
<%--						case 18: // ALT--%>
<%--							return false;--%>
<%--							break;--%>
<%--						case 37: // Arrow Left--%>
<%--							return false;--%>
<%--							break;--%>
<%--						case 38: // Arrow Up--%>
<%--							return false;--%>
<%--							break;--%>
<%--						case 39: // Arrow Right--%>
<%--							return false;--%>
<%--							break;--%>
<%--						case 40: // Arrow Down--%>
<%--							return false;--%>
<%--							break;--%>
<%--						case 32: // Space--%>
<%--							if($.trim(this.value).length <= 2) {--%>
<%--								return false;--%>
<%--							}--%>
<%--							break;--%>
<%--						}		--%>
<%--						if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {--%>
<%--							prWaitingData.column($(this).data('index')).search($.trim(this.value)).draw();--%>
<%--						}--%>
<%--					}, 650));--%>
<%--					--%>
<%--					/* $(prWaitingData.table().container()).on('keyup', 'thead input', function() {--%>
<%--						if ($.trim(this.value).length > 2 || this.value.length == 0) {--%>
<%--							prWaitingData.column($(this).data('index')).search(this.value).draw();--%>
<%--						}--%>
<%--					}); */--%>
<%--					$(prWaitingData.table().container()).on('change', 'thead select', function() {--%>
<%--						prWaitingData.column($(this).data('index')).search(this.value).draw();--%>
<%--					});--%>
<%--			 }--%>
<%--		});--%>
<%--	});--%>
<%--</c:if>--%>




<%--<c:if test="${pendingPrCount > 0}">	--%>
<%--var prWaitingData2; --%>
<%--$('document').ready(function() {--%>
<%--	prWaitingData2 = $('#myPendingPoList').DataTable({--%>
<%--		"oLanguage":{--%>
<%--			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"--%>
<%--	},--%>
<%--	"processing" : true,--%>
<%--	"serverSide" : true,--%>
<%--	"deferLoading": 0,--%>
<%--	"pageLength" : 10,--%>
<%--	"searching" : true,--%>
<%--		"ajax" : {--%>
<%--			"url" : getContextPath() + "/buyer/pendingPoList",--%>
<%--			"data" : function(d) {--%>
<%--			}--%>
<%--		},--%>
<%--		"order" : [ [ 4, "desc" ] ],--%>
<%--		"columns" : [ {--%>
<%--			"data" : "id",--%>
<%--			"searchable" : false,--%>
<%--			"orderable" : false,--%>
<%--			"render" : function(data, type, row) {--%>
<%--				var type = row.type;--%>
<%--				var lockBudget=row.lockBudget;--%>
<%--					return '<a href="${pageContext.request.contextPath}/buyer/poView/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png">  </a>'--%>
<%--				}--%>
<%--		}, {--%>
<%--			"data" : "poNumber",--%>
<%--			"className" : "align-left",--%>
<%--			"defaultContent" : ""--%>
<%--		},{--%>
<%--			"data" : "name",--%>
<%--			"className" : "align-left",--%>
<%--			"defaultContent" : ""--%>
<%--		},{--%>
<%--			"data" : "createdBy.name",--%>
<%--			"className" : "align-left",--%>
<%--			"defaultContent" : ""--%>
<%--		}, {--%>
<%--			"data" : "createdDate",--%>
<%--			"className" : "align-left",--%>
<%--			"type": 'custom-date',--%>
<%--			"defaultContent" : ""--%>
<%--		}, {--%>
<%--			"data" : "currency.currencyCode",--%>
<%--			"className" : "align-left",--%>
<%--			"defaultContent" : ""--%>
<%--		}, {--%>
<%--			"data" : "grandTotal",--%>
<%--			"className" : "align-right",--%>
<%--			"defaultContent" : "",--%>
<%--			"mRender" : function(data, type, row) {--%>
<%--				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));--%>
<%--			}--%>
<%--		},{--%>
<%--			"data" : "businessUnit.unitName",--%>
<%--			"className" : "align-center",--%>
<%--			"defaultContent" : ""--%>
<%--		 } ],--%>
<%--		 --%>
<%--		 "initComplete": function(settings, json) {--%>
<%--			 --%>
<%--			 var htmlPoPendingSearch = '<tr class="tableHeaderWithSearch">';--%>
<%--				$('#myPendingPoList thead tr:nth-child(1) th').each(function(i) {--%>
<%--					var title = $(this).text();--%>
<%--				//	console.log("Title : " + title + " Class : " + $(this).attr("class"));--%>
<%--					var classStyle =  $(this).attr("class");--%>
<%--					if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){--%>
<%--						classStyle = classStyle.replace('sorting','');--%>
<%--					}--%>
<%--					if (!(title == "Actions") && $(this).attr('search-type') != '') {--%>
<%--						htmlPoPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';--%>
<%--					} else {--%>
<%--						htmlPoPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';--%>
<%--					}--%>
<%--				});--%>
<%--				htmlPoPendingSearch += '</tr>';--%>
<%--			//	console.log("htmlPoPendingSearch : " + htmlPoPendingSearch);--%>
<%--				$('#myPendingPoList thead').append(htmlPoPendingSearch);--%>
<%--				--%>
<%--				$(prWaitingData2.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {--%>
<%--					// ignore arrow keys--%>
<%--					switch (e.keyCode) {--%>
<%--					case 17: // CTRL--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 18: // ALT--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 37: // Arrow Left--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 38: // Arrow Up--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 39: // Arrow Right--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 40: // Arrow Down--%>
<%--						return false;--%>
<%--						break;--%>
<%--					case 32: // Space--%>
<%--						if($.trim(this.value).length <= 2) {--%>
<%--							return false;--%>
<%--						}--%>
<%--						break;--%>
<%--					}		--%>
<%--					if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {--%>
<%--						prWaitingData2.column($(this).data('index')).search($.trim(this.value)).draw();--%>
<%--					}--%>
<%--				}, 650));--%>
<%--				--%>
<%--				/* $(prWaitingData2.table().container()).on('keyup', 'thead input', function() {--%>
<%--					if ($.trim(this.value).length > 2 || this.value.length == 0) {--%>
<%--						prWaitingData2.column($(this).data('index')).search(this.value).draw();--%>
<%--					}--%>
<%--				}); */--%>
<%--				$(prWaitingData2.table().container()).on('change', 'thead select', function() {--%>
<%--					prWaitingData2.column($(this).data('index')).search(this.value).draw();--%>
<%--				});--%>
<%--		 }--%>
<%--	});--%>
<%--});--%>
<%--</c:if>--%>





<c:if test="${prCount > 0 and isBuyerErpEnable }">
var prcheseOrdersData;
$('document').ready(function() {
	prcheseOrdersData = $('#poList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
		//	$('div[id=idGlobalError]').hide();
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$('#loading').hide();
		},
		"ajax" : {
			"url" : getContextPath() + "/buyer/prDashboardData",
			"data" : function(d) {
			},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		//"order" : [[ 6, "desc" ]],
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="prView/' + row.id + '" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>&nbsp;&nbsp;';
				action += '<a href="downlaodPrSummary/' + row.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "description",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "supplier.fullName",
			"defaultContent" : ""
		},
		{
			"data" : "createdBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "poCreatedDate",
			"className" : "align-left",
			"searchable" : false,
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "grandTotal",
			"className" : "align-left",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		},{
			"data" : "businessUnit.unitName",
			"className" : "align-left",
			"defaultContent" : ""
		 } ],
	
	
	"initComplete": function(settings, json) {
	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#poList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} */
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
/* 				if ($(this).attr('search-type') == 'select') {
 					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>'; 
				} else {
				}
	*/			
					htmlEventPoSearch += '<th class="align-left" ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				 
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#poList thead').append(htmlEventPoSearch);
		$(prcheseOrdersData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				prcheseOrdersData.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		/* $(prcheseOrdersData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				prcheseOrdersData.column($(this).data('index')).search(this.value).draw();
			}
		}); */
		$(prcheseOrdersData.table().container()).on('change', 'thead select', function() {
			prcheseOrdersData.column($(this).data('index')).search(this.value).draw();
		});
	}
	});
});
</c:if>

<c:if test="${prTransferCount > 0 and isBuyerErpEnable }">
var transferredPrData;
$('document').ready(function() {
	transferredPrData = $('#poTransferList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$('#loading').hide();
		},
		"ajax" : {
			"url" : getContextPath() + "/buyer/prTransferDashboardData",
			"data" : function(d) {
			},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="prView/' + row.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				if(row.isPo)
				action += '<a href="downlaodPrSummary/' + row.id + '"   data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				else
				action += '<a href="downlaodPrSummary/' + row.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "description",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "supplier.fullName",
			"defaultContent" : ""
		},{
			"data" : "createdBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "prCreatedDate",
			"className" : "align-left",
			"searchable" : false,
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "grandTotal",
			"className" : "align-left",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		},{
			"data" : "businessUnit.unitName",
			"className" : "align-left",
			"defaultContent" : ""
		 } ],
	
	
	"initComplete": function(settings, json) {
	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#poTransferList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
		
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			}  else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
		$('#poTransferList thead').append(htmlEventPoSearch);
		$(transferredPrData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				transferredPrData.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		$(transferredPrData.table().container()).on('change', 'thead select', function() {
			transferredPrData.column($(this).data('index')).search(this.value).draw();
		});
	}
	});
});
</c:if>


<c:if test="${formDraftCount > 0}">
var draftsPrsData1;
$('document').ready(function() {
	draftsPrsData1 = $('#myDraftRequestList').DataTable({
	 	/* "oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},  */
		"processing" : true,
		"serverSide" : true,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax": {
			"type": "GET",
			"url" : getContextPath() + "/buyer/myDraftRequestList",
			"data" : function(d) {}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className" : "pad-left-10 align-left",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/createSourcingFormDetails/'+row.id+'" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "sourcingFormName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "formId",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdBy",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "formOwner",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
		"initComplete": function(settings, json) {
 		 	var htmlDraftRequestSearch = '<tr class="tableHeaderWithSearch">';
			$('#myDraftRequestList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					htmlDraftRequestSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				} else {
					htmlDraftRequestSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlDraftRequestSearch += '</tr>';
			$('#myDraftRequestList thead').append(htmlDraftRequestSearch);
			
			$(draftsPrsData1.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					draftsPrsData1.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(draftsPrsData1.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					draftsPrsData1.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(draftsPrsData1.table().container()).on('change', 'thead select', function() {
				draftsPrsData1.column($(this).data('index')).search(this.value).draw();
			});
 		}
	});
});
</c:if>



<c:if test="${draftEventCount > 0}">
var draftsEventsData1;
$('document').ready(function() {
	draftsEventsData1 = $('#draftList').DataTable({
		"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/draftList?days=",
			"data" : function(d) {
			}
		},
		"order" : [[ 5, "desc" ], [ 6, "desc" ]],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"render" : function(data, type, row) {
				var type = row.type;
				return '<form id=' + row.id + ' action="'+type+'/editEventDetails" method="POST" data-placement="top" title="Edit">' + '<input type="hidden" name="eventId" value="' + row.id + '" >' + '<input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />' + '<a href="javascript:void(0);" onclick="document.getElementById(\'' + row.id + '\').submit();" ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>' + '</form>';
			},
		},{
			"data" : "eventName",
			"defaultContent" : "",
			"className" : "align-left"
		}, {
			"data" : "referenceNumber",
			"defaultContent" : "",
			"className" : "align-left"
		}, {
			"data" : "sysEventId",
			"defaultContent" : "",
			"className" : "align-left"
		},{
			"data" : "eventStart",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "eventEnd",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "type",
			"className" : "align-center",
			"defaultContent" : "",
			"orderable" : false
		}, {
			"data" : "eventUser",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "unitName",
			"className" : "align-center",
			"defaultContent" : ""
		 }],
	
	
	"initComplete": function(settings, json) {
	 var htmlEventDraftSearch = '<tr class="tableHeaderWithSearch">';
		$('#draftList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventDraftSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventDraftSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventDraftSearch += '</select></th>';
				} else {
					htmlEventDraftSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventDraftSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventDraftSearch += '</tr>';
		//console.log("htmlEventDraftSearch : " + htmlEventDraftSearch);
		$('#draftList thead').append(htmlEventDraftSearch);
		
		$(draftsEventsData1.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				draftsEventsData1.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		/* $(draftsEventsData1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				draftsEventsData1.column($(this).data('index')).search(this.value).draw();
			}
		}); */
		$(draftsEventsData1.table().container()).on('change', 'thead select', function() {
			draftsEventsData1.column($(this).data('index')).search(this.value).draw();
		}); 
	}
	});

});
</c:if>


<c:if test="${formPendingCount > 0}">
var prWaitingData1;
$('document').ready(function() {
	prWaitingData1 = $('#myPendingRequestList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myPendingRequestList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className" : "pad-left-10 align-left",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/viewSourcingSummary/'+row.id+'" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "sourcingFormName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "formId",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdBy",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "formOwner",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
		 "initComplete": function(settings, json) {

			 var htmlPendingRequestSearch = '<tr class="tableHeaderWithSearch">';
				$('#myPendingRequestList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
				//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
					var classStyle =  $(this).attr("class");
					if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
						classStyle = classStyle.replace('sorting','');
					}
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						htmlPendingRequestSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					} else {
						htmlPendingRequestSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
					}
				});
				htmlPendingRequestSearch += '</tr>';
			//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
				$('#myPendingRequestList thead').append(htmlPendingRequestSearch);
				
				$(prWaitingData1.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
					// ignore arrow keys
					switch (e.keyCode) {
					case 17: // CTRL
						return false;
						break;
					case 18: // ALT
						return false;
						break;
					case 37: // Arrow Left
						return false;
						break;
					case 38: // Arrow Up
						return false;
						break;
					case 39: // Arrow Right
						return false;
						break;
					case 40: // Arrow Down
						return false;
						break;
					case 32: // Space
						if($.trim(this.value).length <= 2) {
							return false;
						}
						break;
					}		
					if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
						prWaitingData1.column($(this).data('index')).search($.trim(this.value)).draw();
					}
				}, 650));
				
				/* $(prWaitingData1.table().container()).on('keyup', 'thead input', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						prWaitingData1.column($(this).data('index')).search(this.value).draw();
					}
				}); */
				$(prWaitingData1.table().container()).on('change', 'thead select', function() {
					prWaitingData1.column($(this).data('index')).search(this.value).draw();
				});
			 
		 }
	});
	
});
</c:if>





<c:if test="${finishedEventCount >0 }">
var regDate = '';
var finishedEventsData1;
//	regarding PH 69 
//	$("#searchFinishedEvent").click(function() {
//		regDate = $('#selectDays').val();
//		if(regDate!=null && regDate != '')
//		finishedEventsData.ajax.reload();
//	});
$('document').ready(function() {
	finishedEventsData1 = $('#finishedEventList').DataTable({
		"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/finishedEventList",
			"data" : function(d) {
				    var daysValue = $("input[name='days']").val();
                    console.log("Days Value:", daysValue);  // Check if the value is what you expect
                    d.days = daysValue;

			}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="View"><img hieght="25" width="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referenceNumber",
			"defaultContent" : "",
			"className" : "align-left"
		}, {
			"data" : "sysEventId",
			"defaultContent" : "",
			"className" : "align-left"
		}, {
			"data" : "eventStart",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "eventEnd",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "type",
			"className" : "align-center",
			"defaultContent" : "",
			"orderable" : false
		}, {
			"data" : "eventUser",
			"className" : "align-center",
			"defaultContent" : ""
		},{
			"data" : "unitName",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
	
	 "initComplete": function(settings, json) {
	 var htmlEventFinishedSearch = '<tr class="tableHeaderWithSearch">';
		$('#finishedEventList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			console.log("Header " + i + ": " + title);  // Log each header to see if they match your columns
	        //console.log(" HI Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventFinishedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventFinishedSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventFinishedSearch += '</select></th>';
				} else {
					htmlEventFinishedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventFinishedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventFinishedSearch += '</tr>';
//		console.log("htmlEventFinishedSearch : " + htmlEventFinishedSearch);
		$('#finishedEventList thead').append(htmlEventFinishedSearch);
		
		$(finishedEventsData1.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {

		console.log("Keyup event triggered for filter");
            var value = $.trim(this.value);
            console.log("Value entered:", value);
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				finishedEventsData1.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		/* $(finishedEventsData1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				finishedEventsData1.column($(this).data('index')).search(this.value).draw();
			}
		}); */
		$(finishedEventsData1.table().container()).on('change', 'thead select', function() {
			finishedEventsData1.column($(this).data('index')).search(this.value).draw();
		});
	 }
	});
});
</c:if>

<c:if test="${finishedRequestCount > 0}">
var finishedEventsData2;
$('document').ready(function() {
	finishedEventsData2 = $('#myFinishRequestList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myFinishRequestList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className" : "pad-left-10 align-left",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/createSourcingFormDetails/'+row.id+'" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "sourcingFormName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "formId",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdBy",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "formOwner",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
		 "initComplete": function(settings, json) {
			 var htmlDraftRequestSearch = '<tr class="tableHeaderWithSearch">';
				$('#myFinishRequestList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
					var classStyle =  $(this).attr("class");
					if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
						classStyle = classStyle.replace('sorting','');
					}
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						htmlDraftRequestSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					} else {
						htmlDraftRequestSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
					}
				});
				htmlDraftRequestSearch += '</tr>';
				$('#myFinishRequestList thead').append(htmlDraftRequestSearch);
				
				$(finishedEventsData2.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
					// ignore arrow keys
					switch (e.keyCode) {
					case 17: // CTRL
						return false;
						break;
					case 18: // ALT
						return false;
						break;
					case 37: // Arrow Left
						return false;
						break;
					case 38: // Arrow Up
						return false;
						break;
					case 39: // Arrow Right
						return false;
						break;
					case 40: // Arrow Down
						return false;
						break;
					case 32: // Space
						if($.trim(this.value).length <= 2) {
							return false;
						}
						break;
					}		
					if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
						finishedEventsData2.column($(this).data('index')).search($.trim(this.value)).draw();
					}
				}, 650));
				
				/* $(finishedEventsData2.table().container()).on('keyup', 'thead input', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						finishedEventsData2.column($(this).data('index')).search(this.value).draw();
					}
				}); */
				$(finishedEventsData2.table().container()).on('change', 'thead select', function() {
					finishedEventsData2.column($(this).data('index')).search(this.value).draw();
				});
		 }
	});

});
</c:if>


<c:if test="${approvedRequestCount > 0}">
var finishedEventsData3;
$('document').ready(function() {
	finishedEventsData3 = $('#myCompleteRequestList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myApprvedRequestList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className" : "pad-left-10 align-left",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/viewSourcingSummary/'+row.id+'" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "sourcingFormName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "formId",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdBy",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "formOwner",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
		 "initComplete": function(settings, json) {

			 var htmlCompleteRequestSearch = '<tr class="tableHeaderWithSearch">';
				$('#myCompleteRequestList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
				//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
					var classStyle =  $(this).attr("class");
					if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
						classStyle = classStyle.replace('sorting','');
					}
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						htmlCompleteRequestSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					} else {
						htmlCompleteRequestSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
					}
				});
				htmlCompleteRequestSearch += '</tr>';
			//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
				$('#myCompleteRequestList thead').append(htmlCompleteRequestSearch);
				
				$(finishedEventsData3.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
					// ignore arrow keys
					switch (e.keyCode) {
					case 17: // CTRL
						return false;
						break;
					case 18: // ALT
						return false;
						break;
					case 37: // Arrow Left
						return false;
						break;
					case 38: // Arrow Up
						return false;
						break;
					case 39: // Arrow Right
						return false;
						break;
					case 40: // Arrow Down
						return false;
						break;
					case 32: // Space
						if($.trim(this.value).length <= 2) {
							return false;
						}
						break;
					}		
					if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
						finishedEventsData3.column($(this).data('index')).search($.trim(this.value)).draw();
					}
				}, 650));
				
				/* $(finishedEventsData3.table().container()).on('keyup', 'thead input', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						finishedEventsData3.column($(this).data('index')).search(this.value).draw();
					}
				}) */
				$(finishedEventsData3.table().container()).on('change', 'thead select', function() {
					finishedEventsData3.column($(this).data('index')).search(this.value).draw();
				}); 
		 }
	});
	
});
</c:if>

<c:if test="${cancelRequestCount > 0}">
var cancelledEventsData1;
$('document').ready(function() {
	cancelledEventsData1 = $('#myCancelRequestList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myCancelRequestList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className" : "pad-left-10 align-left",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/createSourcingFormDetails/'+row.id+'" title="View "><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "sourcingFormName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "formId",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdBy",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "formOwner",
			"className" : "	align-left",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
		 "initComplete": function(settings, json) {
//			myTaskData2.ajax.reload();
			 var htmlDraftRequestSearch = '<tr class="tableHeaderWithSearch">';
				$('#myCancelRequestList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
				//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
					var classStyle =  $(this).attr("class");
					if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
						classStyle = classStyle.replace('sorting','');
					}
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						htmlDraftRequestSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					} else {
						htmlDraftRequestSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
					}
				});
				htmlDraftRequestSearch += '</tr>';
			//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
				$('#myCancelRequestList thead').append(htmlDraftRequestSearch);
				
				$(cancelledEventsData1.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
					// ignore arrow keys
					switch (e.keyCode) {
					case 17: // CTRL
						return false;
						break;
					case 18: // ALT
						return false;
						break;
					case 37: // Arrow Left
						return false;
						break;
					case 38: // Arrow Up
						return false;
						break;
					case 39: // Arrow Right
						return false;
						break;
					case 40: // Arrow Down
						return false;
						break;
					case 32: // Space
						if($.trim(this.value).length <= 2) {
							return false;
						}
						break;
					}		
					if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
						cancelledEventsData1.column($(this).data('index')).search($.trim(this.value)).draw();
					}
				}, 650));
				
				/* $(cancelledEventsData1.table().container()).on('keyup', 'thead input', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						cancelledEventsData1.column($(this).data('index')).search(this.value).draw();
					}
				}); */
				$(cancelledEventsData1.table().container()).on('change', 'thead select', function() {
					cancelledEventsData1.column($(this).data('index')).search(this.value).draw();
				});
		 }
	});
});
</c:if>
	
	
		
<c:if test="${myApprovalsCount > 0}">
	var myTaskData;
	$('document').ready(function() {
		myTaskData = $('#myPendingEventList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/myPendingEventApprovalList",
				"data" : function(d) {
				}
			},
			 "order" : [ [ 4, "asc" ] ], 
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"sClass" : "pad-left-10 align-left",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"defaultContent" : "",
				"orderable" : false
			},{
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			 },{
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
		
		
		 "initComplete": function(settings, json) {
			var htmlEventMyPendingApprovalSearch = '<tr class="tableHeaderWithSearch">';
			$('#myPendingEventList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventMyPendingApprovalSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventMyPendingApprovalSearch += '</select></th>';
					} else {
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventMyPendingApprovalSearch += '</tr>';
			$('#myPendingEventList thead').append(htmlEventMyPendingApprovalSearch);
			
			$(myTaskData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					myTaskData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(myTaskData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					myTaskData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(myTaskData.table().container()).on('change', 'thead select', function() {
				myTaskData.column($(this).data('index')).search(this.value).draw();
			});
		 }
		});

	});
	</c:if>
	
	<c:if test="${myApprovalsCount > 0}">
	var myTaskData;
	$('document').ready(function() {
		myTaskData = $('#myPendingAwardList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/myPendingAwardApprovalList",
				"data" : function(d) {
				}
			},
			 "order" : [ [ 4, "asc" ] ], 
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"sClass" : "pad-left-10 align-left",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventAward/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"defaultContent" : "",
				"orderable" : false
			},{
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			 },{
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
		
		
		 "initComplete": function(settings, json) {
			var htmlEventMyPendingApprovalSearch = '<tr class="tableHeaderWithSearch">';
			$('#myPendingAwardList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventMyPendingApprovalSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventMyPendingApprovalSearch += '</select></th>';
					} else {
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventMyPendingApprovalSearch += '</tr>';
			$('#myPendingAwardList thead').append(htmlEventMyPendingApprovalSearch);
			
			$(myTaskData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					myTaskData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(myTaskData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					myTaskData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(myTaskData.table().container()).on('change', 'thead select', function() {
				myTaskData.column($(this).data('index')).search(this.value).draw();
			});
		 }
		});

	});
	</c:if>
	
<c:if test="${myApprovalsCount > 0 || myEvaluationCount > 0 }">
	var myPendingEvaluation;
	$('document').ready(function() {
		myPendingEvaluation= $('#myPendingEvaluationList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"deferRender" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/myPendingEvaluationList",
				"data" : function(d) {
				}
			},
			"order" : [ [ 5, "desc" ] ],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"className" : "pad-left-10 align-left",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"className" : "align-left",
				"defaultContent" : ""
			},  {
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"className" : "align-center",
				"defaultContent" : "",
				"orderable" : false
			},{
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			 },{
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
		
		
	//	myPendingEvaluation.ajax.reload();
		"initComplete": function(settings, json) {
		var htmlEventMyPendingSearch = '<tr class="tableHeaderWithSearch">';
		$('#myPendingEvaluationList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventMyPendingSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventMyPendingSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventMyPendingSearch += '</select></th>';
				} else {
					htmlEventMyPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventMyPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventMyPendingSearch += '</tr>';
		console.log("htmlEventMyPendingSearch : " + htmlEventMyPendingSearch);
		$('#myPendingEvaluationList thead').append(htmlEventMyPendingSearch);
		
		$(myPendingEvaluation.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				myPendingEvaluation.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		/* $(myPendingEvaluation.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myPendingEvaluation.column($(this).data('index')).search(this.value).draw();
			}
		}); */
		$(myPendingEvaluation.table().container()).on('change', 'thead select', function() {
			myPendingEvaluation.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
	});
</c:if>

<c:if test="${myApprovalsCount > 0 || mySpEvaluationCount > 0 }">
var myEvaluationData;
$('document').ready(function() {
	myEvaluationData = $('#spEvaluationList').DataTable({
		"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/spEvaluationList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className" : "pad-left-10 align-left",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/viewEvaluatorFormSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "formId",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "referenceNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "supplierName",
			"className" : "align-left",
			"defaultContent" : ""
		},  {
			"data" : "evaluationStartDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "evaluationEndDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "formOwner",
			"className" : "align-center",
			"defaultContent" : ""
		 },{
			"data" : "unitName",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
	
	
//	myEvaluationData.ajax.reload();
	"initComplete": function(settings, json) {
	var htmlEventMyPendingSearch = '<tr class="tableHeaderWithSearch">';
	$('#spEvaluationList thead tr:nth-child(1) th').each(function(i) {
		var title = $(this).text();
	//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
		var classStyle =  $(this).attr("class");
		if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
			classStyle = classStyle.replace('sorting','');
		}
		if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventMyPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
		} else {
			htmlEventMyPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
		}
	});
	htmlEventMyPendingSearch += '</tr>';
//	console.log("htmlEventMyPendingSearch : " + htmlEventMyPendingSearch);
	$('#spEvaluationList thead').append(htmlEventMyPendingSearch);
	
	$(myEvaluationData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
		// ignore arrow keys
		switch (e.keyCode) {
		case 17: // CTRL
			return false;
			break;
		case 18: // ALT
			return false;
			break;
		case 37: // Arrow Left
			return false;
			break;
		case 38: // Arrow Up
			return false;
			break;
		case 39: // Arrow Right
			return false;
			break;
		case 40: // Arrow Down
			return false;
			break;
		case 32: // Space
			if($.trim(this.value).length <= 2) {
				return false;
			}
			break;
		}		
		if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
			myEvaluationData.column($(this).data('index')).search($.trim(this.value)).draw();
		}
	}, 650));
	
	/* $(myEvaluationData.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 2 || this.value.length == 0) {
			myEvaluationData.column($(this).data('index')).search(this.value).draw();
		}
	}); */
	$(myEvaluationData.table().container()).on('change', 'thead select', function() {
		myEvaluationData.column($(this).data('index')).search(this.value).draw();
	});
	}
	});
});
</c:if>
	
<c:if test="${myApprovalsCount > 0}">
	var myTaskData2;
	$('document').ready(function() {
		myTaskData2 = $('#myPendingPrList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/myPendingPrApprovalList",
				"data" : function(d) {
				}
			},
			"order" : [],
			"columns" : [ 
				{
		         	'searchable': false,
		         	'orderable': false,
		         	'className': 'checkbox-stylling',
		         	'render': function (data, type, row){
		             return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="prId" name="prId">';
		        	 }
				},{
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"className" : "align-center",
				"render" : function(data, type, row) {
					var ret = '';
					if(row.urgentPr == true){
					      ret += '<a href="${pageContext.request.contextPath}/buyer/prView/'+row.id+'" data-placement="top" title="View" ><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
					      ret += '<i class="fa fa-exclamation fa-lg" aria-hidden="true" style="color : red" title="Priority"></i>';
						return ret;
					} else {
					       ret += '<a href="${pageContext.request.contextPath}/buyer/prView/'+row.id+'" data-placement="top" title="View" ><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				           return ret;
					}
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "createdBy.name",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "prUserName",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "grandTotal",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
				}
			},{
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
	
			//	myTaskData2.ajax.reload();
			"initComplete": function(settings, json) {
			 var htmlEventMyPendingPrSearch = '<tr class="tableHeaderWithSearch">';
			$('#myPendingPrList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
			//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					htmlEventMyPendingPrSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				} else {
					htmlEventMyPendingPrSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventMyPendingPrSearch += '</tr>';
		//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
			$('#myPendingPrList thead').append(htmlEventMyPendingPrSearch);
			
			$(myTaskData2.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					myTaskData2.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(myTaskData2.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					myTaskData2.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(myTaskData2.table().container()).on('change', 'thead select', function() {
				myTaskData2.column($(this).data('index')).search(this.value).draw();
			});
		}
		});
	});
	</c:if>
	
<c:if test="${myApprovalsCount > 0}">
	var myTaskData3;
	$('document').ready(function() {
	myTaskData3 = $('#myPendingSourcingRequestApprovalList').DataTable({
		"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myPendingRequestAppList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 5, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className" : "pad-left-10 align-left",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/buyer/viewSourcingSummary/'+row.id+'" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "sourcingFormName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "formId",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdBy",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "formOwner",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "businessUnit",
			"className" : "align-center",
			"defaultContent" : ""
		 } ],
	
	"initComplete": function(settings, json) {
	 var myPendingSourcingRequestApprovalHtml = '<tr class="tableHeaderWithSearch">';
		$('#myPendingSourcingRequestApprovalList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				myPendingSourcingRequestApprovalHtml += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				myPendingSourcingRequestApprovalHtml += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		myPendingSourcingRequestApprovalHtml += '</tr>';
	//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
		$('#myPendingSourcingRequestApprovalList thead').append(myPendingSourcingRequestApprovalHtml);
		
		$(myTaskData3.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				myTaskData3.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		/* $(myTaskData3.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myTaskData3.column($(this).data('index')).search(this.value).draw();
			}
		}); */
		$(myTaskData3.table().container()).on('change', 'thead select', function() {
			myTaskData3.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
		
	});
</c:if>

<c:if test="${readyPoCount > 0 }">
var poReadyData;
$('document').ready(function() {
	poReadyData = $('#readyPoList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$('#loading').hide();
		},
		"ajax" : {
			"url" : getContextPath() + "/buyer/readyPoData",
			"data" : function(d) {
			},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="poView/' + row.id + '" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>&nbsp;&nbsp;';
				action += '<a href="downloadPoReport/' + row.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		},{
			"data" : "poNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "supplier.fullName",
			"defaultContent" : ""
		} , {
			"data" : "createdBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"searchable" : false,
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "currency.currencyCode",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "businessUnit.unitName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "grandTotal",
			"className" : "align-left",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		}],
	
	
	"initComplete": function(settings, json) {
	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#readyPoList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
					htmlEventPoSearch += '<th class="align-left" ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
		$('#readyPoList thead').append(htmlEventPoSearch);
		$(poReadyData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				poReadyData.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		$(poReadyData.table().container()).on('change', 'thead select', function() {
			poReadyData.column($(this).data('index')).search(this.value).draw();
		});
	}
	});
});
</c:if>

<c:if test="${orderedPoCount > 0 }">
var poOrdersData;
$('document').ready(function() {
	poOrdersData = $('#orderedPoList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"serverSide" : true,
	"deferLoading": 0,
	"pageLength" : 10,
	"searching" : true,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$('#loading').hide();
		},
		"ajax" : {
			"url" : getContextPath() + "/buyer/orderedPoData",
			"data" : function(d) {
			},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="poView/' + row.id + '" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>&nbsp;&nbsp;';
				action += '<a href="downloadPoReport/' + row.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		},{
			"data" : "poNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "supplier.fullName",
			"defaultContent" : ""
		} , {
			"data" : "createdBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"searchable" : false,
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "orderedBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "orderedDate",
			"className" : "align-left",
			"searchable" : false,
			"type": 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "currency.currencyCode",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "businessUnit.unitName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "grandTotal",
			"className" : "align-left",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		}],
	
	
	"initComplete": function(settings, json) {
	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#orderedPoList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
					htmlEventPoSearch += '<th class="align-left" ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
		$('#orderedPoList thead').append(htmlEventPoSearch);
		$(poOrdersData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				poOrdersData.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		$(poOrdersData.table().container()).on('change', 'thead select', function() {
			poOrdersData.column($(this).data('index')).search(this.value).draw();
		});
	}
	});
});
</c:if>
	
	
	
	
	

<c:if test="${myApprovalsCount > 0}">
	var myTaskData4;
	$('document').ready(function() {
	myTaskData4 = $('#myBudgetApprovalList').DataTable({
		"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"deferRender" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/budgetData",
			"data" : function(d) {
			}
		},
		"order" : [ [ 2, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"render" : function(data, type, row) {
				var ret = '<a href="'+getContextPath()+'/admin/budgets/budgetSummary/' + row.id + '"  title=<spring:message code="application.edit"/>><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
				return ret;
			}
		}, {
			"data" : "budgetId",
			"defaultContent" : ""
		}, {
			"data" : "budgetName",
			"defaultContent" : ""
		}, {
			"data" : "businessUnitName",
			"defaultContent" : ""
		}, {
			"data" : "costCenterName",
			"defaultContent" : ""
		},{
			"data" : "validFrom",
			"searchable" : false,
			"defaultContent" : ""
		},{
			"data" : "validTo",
			"searchable" : false,
			"defaultContent" : ""
		}, {
			"data" : "totalAmount",
			"className" : "align-right",
			"searchable" : false,
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommasFormat(row.totalAmount);
			}
		} ],
	
	"initComplete": function(settings, json) {
	 var myBudgetApprovalHtml = '<tr class="tableHeaderWithSearch">';
		$('#myBudgetApprovalList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				myBudgetApprovalHtml += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				myBudgetApprovalHtml += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		myBudgetApprovalHtml += '</tr>';
	//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
		$('#myBudgetApprovalList thead').append(myBudgetApprovalHtml);
		
		$(myTaskData4.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				myTaskData4.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		/* $(myTaskData3.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myTaskData3.column($(this).data('index')).search(this.value).draw();
			}
		}); */
		$(myTaskData4.table().container()).on('change', 'thead select', function() {
			myTaskData4.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
	
	function ReplaceNumberWithCommasFormat(yourNumber) {
		if(yourNumber!='' && yourNumber!=undefined){
			yourNumber = parseFloat(yourNumber).toFixed(2);
			var n = yourNumber.toString().split(".");
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			return n.join(".");
		}
		return yourNumber; 
	}
		
	});
	
	
	var revisePoApprovalData;
	$('document').ready(function() {
	revisePoApprovalData = $('#revisePoApprovalList').DataTable({
		"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"deferRender" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myRevisePoApprovalData",
			"data" : function(d) {
			}
		},
		"order" : [ [ 2, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className": "align-center",
			"render" : function(data, type, row) {
				var type = row.type;
				var lockBudget=row.lockBudget;
				return '<a href="${pageContext.request.contextPath}/buyer/poView/'+row.id+'?prId='+row.pr.id+'"	 data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png">  </a>'
			}
		}, {
			"data" : "poNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "createdBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "currency.currencyCode",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "grandTotal",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommasFormat(row.grandTotal.toFixed(row.decimal));
			}
		},{
			"data" : "businessUnit.unitName",
			"className" : "align-left",
			"defaultContent" : ""
		} ],
	
	"initComplete": function(settings, json) {
	 var revisePoApprovalHtml = '<tr class="tableHeaderWithSearch">';
		$('#revisePoApprovalList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				revisePoApprovalHtml += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				revisePoApprovalHtml += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		revisePoApprovalHtml += '</tr>';
	//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
		$('#revisePoApprovalList thead').append(revisePoApprovalHtml);
		
		$(revisePoApprovalData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				revisePoApprovalData.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		$(revisePoApprovalData.table().container()).on('change', 'thead select', function() {
			revisePoApprovalData.column($(this).data('index')).search(this.value).draw();
		});
	}
	});

	function ReplaceNumberWithCommasFormat(yourNumber) {
		if(yourNumber!='' && yourNumber!=undefined){
			yourNumber = parseFloat(yourNumber).toFixed(2);
			var n = yourNumber.toString().split(".");
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			return n.join(".");
		}
		return yourNumber; 
	}
});

var myPoAwaitingApprovalData;
$('document').ready(function() {
	myPoAwaitingApprovalData = $('#myPoAwaitingApproval').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"deferRender" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myPendingPoApprovalList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 2, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className": "align-center",
			"render" : function(data, type, row) {
				var type = row.type;
				var lockBudget=row.lockBudget;
				return '<a href="${pageContext.request.contextPath}/buyer/poView/'+row.id+'?prId='+row.pr.id+'"	 data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png">  </a>'
			}
		}, {
			"data" : "poNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "createdBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "currency.currencyCode",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "grandTotal",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		},{
			"data" : "businessUnit.unitName",
			"className" : "align-left",
			"defaultContent" : ""
		} ],


		"initComplete": function(settings, json) {
			var myPoAwaitingApprovalHtml = '<tr class="tableHeaderWithSearch">';
			$('#myPoAwaitingApproval thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					myPoAwaitingApprovalHtml += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				} else {
					myPoAwaitingApprovalHtml += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			myPoAwaitingApprovalHtml += '</tr>';
			//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
			$('#myPoAwaitingApproval thead').append(myPoAwaitingApprovalHtml);

			$(myPoAwaitingApprovalData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
					case 17: // CTRL
						return false;
						break;
					case 18: // ALT
						return false;
						break;
					case 37: // Arrow Left
						return false;
						break;
					case 38: // Arrow Up
						return false;
						break;
					case 39: // Arrow Right
						return false;
						break;
					case 40: // Arrow Down
						return false;
						break;
					case 32: // Space
						if($.trim(this.value).length <= 2) {
							return false;
						}
						break;
				}
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					myPoAwaitingApprovalData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));

			$(myPoAwaitingApprovalData.table().container()).on('change', 'thead select', function() {
				myPoAwaitingApprovalData.column($(this).data('index')).search(this.value).draw();
			});
		}
	});

	function ReplaceNumberWithCommasFormat(yourNumber) {
		if(yourNumber!='' && yourNumber!=undefined){
			yourNumber = parseFloat(yourNumber).toFixed(2);
			var n = yourNumber.toString().split(".");
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			return n.join(".");
		}
		return yourNumber;
	}
});



var myCancelPoAwaitingApprovalData;
$('document').ready(function() {
	myCancelPoAwaitingApprovalData = $('#myCancelPoAwaitingApproval').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"deferRender" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myPendingCancelPoApprovalList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 2, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"className": "align-center",
			"render" : function(data, type, row) {
				var type = row.type;
				var lockBudget=row.lockBudget;
				return '<a href="${pageContext.request.contextPath}/buyer/poView/'+row.id+'?prId='+row.pr.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png">  </a>'
			}
		}, {
			"data" : "poNumber",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "createdBy.name",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "currency.currencyCode",
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "grandTotal",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		},{
			"data" : "businessUnit.unitName",
			"className" : "align-left",
			"defaultContent" : ""
		} ],


		"initComplete": function(settings, json) {
			var myCancelPoAwaitingApprovalHtml = '<tr class="tableHeaderWithSearch">';
			$('#myCancelPoAwaitingApproval thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					myCancelPoAwaitingApprovalHtml += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				} else {
					myCancelPoAwaitingApprovalHtml += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			myCancelPoAwaitingApprovalHtml += '</tr>';
			//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
			$('#myCancelPoAwaitingApproval thead').append(myCancelPoAwaitingApprovalHtml);

			$(myPoAwaitingApprovalData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
					case 17: // CTRL
						return false;
						break;
					case 18: // ALT
						return false;
						break;
					case 37: // Arrow Left
						return false;
						break;
					case 38: // Arrow Up
						return false;
						break;
					case 39: // Arrow Right
						return false;
						break;
					case 40: // Arrow Down
						return false;
						break;
					case 32: // Space
						if($.trim(this.value).length <= 2) {
							return false;
						}
						break;
				}
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					myCancelPoAwaitingApprovalData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));

			$(myCancelPoAwaitingApprovalData.table().container()).on('change', 'thead select', function() {
				myCancelPoAwaitingApprovalData.column($(this).data('index')).search(this.value).draw();
			});
		}
	});

	function ReplaceNumberWithCommasFormat(yourNumber) {
		if(yourNumber!='' && yourNumber!=undefined){
			yourNumber = parseFloat(yourNumber).toFixed(2);
			var n = yourNumber.toString().split(".");
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			return n.join(".");
		}
		return yourNumber;
	}
});





</c:if>
var mySupplierFormSubData='';
$('document').ready(function() {

	// Setup - add a text input to each footer cell
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	mySupplierFormSubData = $('#myPendingSupplierFormList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"deferRender" : true,
		"preDrawCallback" : function(settings) {
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$("#poReportId").val(false);
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/myPendingSupplierList",
			"data" : {},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		"order" : [],
		"columns" : [ {
			"mData":"id",
			"searchable":false,
			"orderable":false,
			"mRender":function(data,type,row){
				var tImg="";
				var ret='';
				tImg='<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
				ret += '<a href="supplierFormSubView/' + row.id + '"  title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>'; 
				return ret;
			}
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "description",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "companyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "submitedDate",
			"searchable" : false,
			"className" : "align-left",
			"type" : 'custom-date',
			"defaultContent" : ""
		}],
		"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#myPendingSupplierFormList thead tr:nth-child(1) th').each(function(i) {
		var title = $(this).text();
		var classStyle =  $(this).attr("class");
		if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
			classStyle = classStyle.replace('sorting','');
		}
		if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
		} else {
			htmlSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
		}
	});
	htmlSearch += '</tr>';
	$('#myPendingSupplierFormList thead').append(htmlSearch);
	$(mySupplierFormSubData.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length >= 2 || this.value.length == 0) {
			mySupplierFormSubData.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(mySupplierFormSubData.table().container()).on('change', 'thead select', function() {
		mySupplierFormSubData.column($(this).data('index')).search(this.value).draw();
	});
	}
	});

	
	<c:if test="${myApprovalsCount > 0}">
	var myTaskData;
	$('document').ready(function() {
		myTaskData = $('#suspendedEventPendingList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/suspendedEventPendingApprList",
				"data" : function(d) {
				}
			},
			 "order" : [ [ 4, "asc" ] ], 
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"sClass" : "pad-left-10 align-left",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/'+type+'/eventSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "type",
				"defaultContent" : "",
				"orderable" : false
			},{
				"data" : "eventUser",
				"className" : "align-center",
				"defaultContent" : ""
			 },{
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
		
		
		 "initComplete": function(settings, json) {
			var htmlEventMyPendingApprovalSearch = '<tr class="tableHeaderWithSearch">';
			$('#suspendedEventPendingList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventMyPendingApprovalSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventMyPendingApprovalSearch += '</select></th>';
					} else {
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventMyPendingApprovalSearch += '</tr>';
			$('#suspendedEventPendingList thead').append(htmlEventMyPendingApprovalSearch);
			
			$(myTaskData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					myTaskData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(myTaskData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					myTaskData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(myTaskData.table().container()).on('change', 'thead select', function() {
				myTaskData.column($(this).data('index')).search(this.value).draw();
			});
		 }
		});

	});
	</c:if>
	
	<c:if test="${myApprovalsCount > 0}">
	var myTaskData;
	$('document').ready(function() {
		myTaskData = $('#spEvaluationApprovalList').DataTable({
			"oLanguage":{
 				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/spEvaluationApprovalList",
				"data" : function(d) {
				}
			},
			 "order" : [ [ 4, "asc" ] ], 
			"columns" : [{
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"className" : "pad-left-10 align-left",
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/spfEvaluatorApprovalSummary/'+row.id+'" data-placement="top" title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				},
			}, {
				"data" : "formId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "supplierName",
				"className" : "align-left",
				"defaultContent" : ""
			},  {
				"data" : "evaluationStartDate",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "evaluationEndDate",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "evaluator",
				"className" : "align-center",
				"defaultContent" : ""
			 },{
				"data" : "formOwner",
				"className" : "align-center",
				"defaultContent" : ""
			 },{
				"data" : "unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ],
		
		
		 "initComplete": function(settings, json) {
			var htmlEventMyPendingApprovalSearch = '<tr class="tableHeaderWithSearch">';
			$('#spEvaluationApprovalList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All Events</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventMyPendingApprovalSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventMyPendingApprovalSearch += '</select></th>';
					} else {
						htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventMyPendingApprovalSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventMyPendingApprovalSearch += '</tr>';
			$('#spEvaluationApprovalList thead').append(htmlEventMyPendingApprovalSearch);
			
			$(myTaskData.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37: // Arrow Left
					return false;
					break;
				case 38: // Arrow Up
					return false;
					break;
				case 39: // Arrow Right
					return false;
					break;
				case 40: // Arrow Down
					return false;
					break;
				case 32: // Space
					if($.trim(this.value).length <= 2) {
						return false;
					}
					break;
				}		
				if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
					myTaskData.column($(this).data('index')).search($.trim(this.value)).draw();
				}
			}, 650));
			
			/* $(myTaskData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					myTaskData.column($(this).data('index')).search(this.value).draw();
				}
			}); */
			$(myTaskData.table().container()).on('change', 'thead select', function() {
				myTaskData.column($(this).data('index')).search(this.value).draw();
			});
		 }
		});

	});
	</c:if>
	
	<c:if test="${myApprovalsCount > 0}">
	var myTaskData4;
	$('document').ready(function() {
	myTaskData4 = $('#contractPendingList').DataTable({
		"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"deferRender" : true,
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/contractApprovalsData",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"render" : function(data, type, row) {
				var ret = '<a href="'+getContextPath()+'/buyer/contractView/' + row.id + '"  title="View"><img height="25" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
				return ret;
			}
		}, {
			"data" : "contractId",
			"defaultContent" : ""
		}, {
			"data" : "contractName",
			"defaultContent" : ""
		}, {
			"data" : "eventId",
			"defaultContent" : ""
		}, {
			"data" : "contractReferenceNumber",
			"defaultContent" : ""
		},{
			"data" : "vendorCode",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"defaultContent" : ""
		},{
			"data" : "contractStartDate",
			"defaultContent" : "",
		},{
			"data" : "contractEndDate",
			"defaultContent" : "",
		},{
			"data" : "groupCodeStr",
			"defaultContent" : ""
		},{
			"data" : "currencyCode",
			"defaultContent" : ""
		},{
			"data" : "contractValue",
			"defaultContent" : ""
		},{
			"data" : "contractCreator",
			"defaultContent" : ""
		},{
			"data" : "createdDate",
			"defaultContent" : ""
		},{
			"data" : "status",
			"defaultContent" : ""
		}],
	
	"initComplete": function(settings, json) {
	 var myBudgetApprovalHtml = '<tr class="tableHeaderWithSearch">';
		$('#contractPendingList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				myBudgetApprovalHtml += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				myBudgetApprovalHtml += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		myBudgetApprovalHtml += '</tr>';
	//	console.log("htmlEventMyPendingPrSearch : " + htmlEventMyPendingPrSearch);
		$('#contractPendingList thead').append(myBudgetApprovalHtml);
		
		$(myTaskData4.table().container()).on('keyup', 'thead input', keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37: // Arrow Left
				return false;
				break;
			case 38: // Arrow Up
				return false;
				break;
			case 39: // Arrow Right
				return false;
				break;
			case 40: // Arrow Down
				return false;
				break;
			case 32: // Space
				if($.trim(this.value).length <= 2) {
					return false;
				}
				break;
			}		
			if ($.trim(this.value).length > 2 || $.trim(this.value).length == 0 || e.keyCode == 8) {
				myTaskData4.column($(this).data('index')).search($.trim(this.value)).draw();
			}
		}, 650));
		
		$(myTaskData4.table().container()).on('change', 'thead select', function() {
			myTaskData4.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
	
	function ReplaceNumberWithCommasFormat(yourNumber) {
		if(yourNumber!='' && yourNumber!=undefined){
			yourNumber = parseFloat(yourNumber).toFixed(2);
			var n = yourNumber.toString().split(".");
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			return n.join(".");
		}
		return yourNumber; 
	}
		
	});
</c:if>
	

});	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('input[name="days"]').mask('000');
		/* $("input[name='days']").keypress(function (e) {
		   if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		      //$("#errmsg").html("Digits Only").show().fadeOut("slow");
		      return false;
		  } else if($(this).val().length > 2){
		  	return false;
		  }
		 }); */
		 
	});
	
	
	<c:if test="${empty lastLoginTime}">	
	 $('body').chardinJs('start')
	</c:if>
	 
</script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/tabs-ui/tabs.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/tabs/tabs.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/tabs/tabs-responsive.js"/>"></script>

<!-- Tabdrop Responsive -->
<script type="text/javascript">
    /* Responsive tabs 
    $(function() { "use strict";
        $('.nav-responsive').tabdrop();
    });*/
</script>
<script>

$(document).ready(function() {
	$('#approvePr').click(function() {
		$('.error-range.text-danger').remove();
		var val = [];
		$('.custom-checkbox1:checked').each(function(i) {
					val[i] = $(this).val();
				});
		console.log(val + "val");

		if (typeof val === 'undefined'
				|| val == '') {
			console.log("Error");
			$('#myPendingPrList_length').after('<div id="prErrorMessage" class="error-range text-danger"> Please select atleast one PR for approval</div>');
			return false;
		} else {
			$('.error-range.text-danger').remove();
			$('#modal-prApprove').modal();
		}
		
	});
	
	$('.custom-checkAllbox').on('change', function() {
		var check = this.checked;
		$("[type=checkbox]").each(function() {
			$(".custom-checkbox1").prop('checked', check);
			$.uniform.update($(this));
		});
	});
	$('#approveAllPr').on('click', function(e) {
		console.log('Pr clicked....');
		var url = getContextPath() +  "/buyer/prMultipleApproved";
		var val = [];
		var remarks = $('#remarks').val();
		
		$('.custom-checkbox1:checked').each(function(i) {
			val[i] = $(this).val();
		});
		
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		console.log(val + "val");
		$.ajax({
			type : "POST",
			url : url,
			data : {
				'prId' : val,
				'remarks' : remarks
			},
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
			var len = val.length;
				console.log("success len..."+val.length);
			myTaskData2.ajax.reload();
 			location.reload();
			
			console.log("success")
// 			var success = request.getResponseHeader('success');
			
			var msg = data;
			$('p[id=idGlobalSuccessMessage]').html(msg);
			$('div[id=idGlobalSuccess]').show();
			$('div[id=idGlobalError]').hide();
			$('#modal-prApprove').modal('hide');
			
			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			$('#modal-prApprove').modal('hide');
			
			
		},
		complete : function() {
			$('#loading').hide();
			$('#modal-prApprove').modal('hide');
		}
			});
	});
	
	
});

</script>

<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

.ph_table td, .ph_table th {
	text-align: -moz-center;
}

.nopad {
	padding: 10px 0 10px 0 !important;
}

.noti-icon-inputfix, .noti-icon-messagefix {
	width: auto;
}

#prDraftList th {
	text-align: left;
}

.ml-5 {
	margin-left: 5px;
}

#approvePr {
	margin-left: 120%;
	margin-top: -14%;
}

#myPendingPrList_length {
	margin-left: -214px;
}

@media ( max-width : 768px) {
	.col-sm-6 {
		width: fit-content !important;
	}
}

@media ( max-width : 360px) {
	.col-sm-6 {
		width: fit-content !important;
	}
	#myPendingPrList_length {
		margin-left: 0px;
	}
	#approvePr {
		margin-left: 0px;
		margin-top: 0px;
	}
	#prErrorMessage {
		margin-left: 0px;
		margin-top: 0px;
	}
}

@media ( max-width : 411px) {
	#myPendingPrList_length {
		margin-left: 0px;
	}
	#approvePr {
		margin-left: 0px;
		margin-top: 0px;
	}
	#prErrorMessage {
		margin-left: 0px;
		margin-top: 0px;
	}
}

@media ( max-width : 414px) {
	#myPendingPrList_length {
		margin-left: 0px;
	}
	#approvePr {
		margin-left: 0px;
		margin-top: 0px;
	}
	#prErrorMessage {
		margin-left: 0px;
		margin-top: 0px;
	}
}

@media ( max-width : 540px) {
	#myPendingPrList_length {
		margin-left: 0px;
	}
	#approvePr {
		margin-left: 0px;
		margin-top: 0px;
	}
	#prErrorMessage {
		margin-left: 0px;
		margin-top: 0px;
	}
}

@media ( max-width : 280px) {
	#prErrorMessage {
		margin-left: 0px;
		margin-top: 0px;
	}
}

#approveAllPr {
	margin-right: 41%;
	color: white;
}

#prErrorMessage {
	margin-left: 118px;
	margin-top: -28px;
}
</style>
