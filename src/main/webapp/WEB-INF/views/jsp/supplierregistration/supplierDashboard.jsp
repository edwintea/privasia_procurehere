<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:message code="supplier.onboarding.only" var="display" />
<spring:message var="supplierDashboardDesk" code="application.supplier.dashboard" />
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierDashboardDesk}] });
});
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">
<jsp:useBean id="now" class="java.util.Date" />
<div id="page-content-wrapper">
	<div id="page-content">
		<c:set var="isTrial" value="${supplier.supplierSubscription.supplierPlan == null || supplier.supplierSubscription.supplierPlan.planStatus == 'FREE_TRIAL'}" />
		<c:set var="isExpired" value="${now > supplier.supplierPackage.endDate}" />
		<c:set var="isSingleBuyer" value="${!empty supplier.supplierSubscription && supplier.supplierSubscription.supplierPlan.buyerLimit == 1}" />
		<div class="container" ${display == 'true' ? 'style="display:none"' : ''}>
			<!-- page title block -->
			<div class="page-title" style="display: ${(isTrial || isSingleBuyer || isExpired) ? 'block' : 'none'}">
				<p class="float_r_web">
					<span class="title-info_massage info_default"> 
						<c:if test="${isSingleBuyer}">
							<spring:message code="supplier.dashboard.common.unpaid.message"/> 
							<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info btn-sm hvr-pop marg-left-20"><spring:message code="supplier.dashboard.upgrade"/></a>
						</c:if> 
						<c:if test="${isTrial && !isExpired}">
							<spring:message code="supplier.dashboard.common.unpaid.message"/>
							<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info btn-sm hvr-pop marg-left-20"><spring:message code="supplier.dashboard.subscribe"/></a>
						</c:if>
						<c:if test="${isExpired}">
							<spring:message code="supplier.dashboard.common.expired.message"/>
							<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info btn-sm hvr-pop marg-left-20"><spring:message code="account.overview.renew"/></a>
						</c:if>
					</span>
				</p>
			</div>


			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap">
					<i class="glyph-icon icon-calendar"></i>
					<spring:message code="supplierdashboard.eventlist" />
				</h2>
				<div class="spd_top">
					<form id="searchValue" method="get" action="${pageContext.request.contextPath}/supplier/searchSupplierEvent">
						<div class="spd_filter dropdown">
							<a href="#" class="hvr-pop dropdown-toggle" id="dropdownMenuFilter" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true"> <img src="${pageContext.request.contextPath}/resources/assets/images/filter_icon.png" alt="filter"
								style="width: 40px;">
							</a>
							<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuFilter" id="supplierFilter" style="width: 300px;">
								<li>&nbsp;</li>
								<li class="filterDashbord"><input path="eventVisibilityDates" id="daterangepicker-date" name="daterangepicker-date" class="form-control for-clander-view for-clander-view pull-right" type="text" data-validation="required"
									data-validation-format="mm/dd/yyyy H:i A - mm/dd/yyyy hh:ii P" style="float: none !important;" /></li>
								<li class="dropdown-header">Status</li>
								<!-- <input type="checkbox" class="flagvisibility" />-->
								<li class="filterDashbord"><label> <input type="checkbox" value="ACTIVE" class="custom-checkbox" id="Status" name="Status"> <spring:message code="supplier.dashboard.active"/>
								</label></li>
								<li class="filterDashbord"><label> <input type="checkbox" value="SUSPENDED" class="custom-checkbox" id="Status" name="Status"> <spring:message code="supplier.dashboard.suspended"/>
								</label></li>
								<li class="filterDashbord"><label> <input type="checkbox" value="CLOSED" class="custom-checkbox" id="Status" name="Status"> <spring:message code="supplier.dashboard.closed"/>
								</label></li>
								<li class="filterDashbord"><label> <input type="checkbox" value="REJECTED" class="custom-checkbox" id="Status" name="Status"> <spring:message code="supplier.dashboard.rejected"/>
								</label></li>
								<li role="separator" class="divider"></li>
								<li class="dropdown-header">Type</li>
								<li class="filterDashbord"><label> <label> <input type="checkbox" value="RFT" class="custom-checkbox" id="Type" name="Type"> <spring:message code="supplier.dashboard.rft"/>
									</label></li>
								<li class="filterDashbord"><label> <input type="checkbox" value="RFP" class="custom-checkbox" id="Type" name="Type"> <spring:message code="supplier.dashboard.rfp"/>
								</label></li>
								<li class="filterDashbord"><label> <input type="checkbox" value="RFA" class="custom-checkbox" id="Type" name="Type"> <spring:message code="supplier.dashboard.rfa"/>
								</label></li>
								<li class="filterDashbord"><label> <input type="checkbox" value="RFI" class="custom-checkbox" id="Type" name="Type"> <spring:message code="supplier.dashboard.rfi"/>
								</label></li>
								<li class="filterDashbord"><label> <input type="checkbox" value="RFQ" class="custom-checkbox" id="Type" name="Type"> <spring:message code="supplier.dashboard.rfq"/>
								</label></li>
							</ul>
							<button class="btn btn-default" type="submit"><spring:message code="supplier.dashboard.go"/></button>
						</div>
						<div class="spd_search">
							<spring:message code="supplierdashboard.search" var="search" />
							<input type="text" class="form-control" name="opVal" value="${opVal}" placeholder="${search}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						</div>
					</form>
				</div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

			<c:if test="${!isFilter}">
				<div class="Invited-Supplier-List dashboard-main">
					<jsp:include page="/WEB-INF/views/jsp/supplierregistration/supplierCountDashboard.jsp" />
				</div>
			</c:if>


			<!-- ********* -->
			<!--  PENDING -->
			<!-- ********* -->
			<c:if test="${eventPendingCount > 0 }">
				<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility pendingEventsData">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.invited.events"/></div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="pendingEventsList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
											<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${ eventAccseptCount > 0}">
				<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility pendingEventsData">

					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.accepted.events"/></div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="pendingEventsList1" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
											<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>




			</c:if>
		</div>









		<!-- ********* -->
		<!-- SCHEDULED -->
		<!-- ********* -->
		<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility suspendedEventsData">
			<c:if test="${eventSuspendedCount > 0}">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.suspended.events"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="suspendedEventList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
										<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</c:if>
		</div>

		<!-- ********* -->
		<!--  ACTIVE -->
		<!-- ********* -->
<!-- 		<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility activeEventsData"> -->
<%-- 			<c:if test="${eventActiveCount > 0}"> --%>
<!-- 				<div class="Invited-Supplier-List-table add-supplier "> -->
<!-- 					<div class=" ph_tabel_wrapper"> -->
<!-- 						<div class="col-md-12"> -->
<%-- 							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.active.events"/></div> --%>
<!-- 						</div> -->
<!-- 						<div class="main_table_wrapper ph_table_border "> -->
<!-- 							<table id="activeEventsList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0"> -->
<!-- 								<thead> -->
<!-- 									<tr class="tableHeaderWithSearch"> -->
<%-- 										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th> --%>
<%-- 										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th> --%>
<%-- 										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th> --%>
<%-- 										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th> --%>
<%-- 										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th> --%>
<%-- 										<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th> --%>
<!-- 									</tr> -->
<!-- 								</thead> -->
<!-- 							</table> -->
<!-- 						</div> -->
<!-- 					</div> -->
<!-- 				</div> -->
<%-- 			</c:if> --%>
<!-- 		</div> -->

		<!-- ********* -->
		<!--  CLOSED -->
		<!-- ********* -->
		<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility closedEventsData">
			<c:if test="${eventClosedCount > 0}">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.closed.events"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="closedEventsList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
										<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</c:if>
		</div>

		<!-- ********* -->
		<!--  COMPLETE -->
		<!-- ********* -->
		<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility completeEventsData">
			<c:if test="${eventCompletedCount > 0}">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.completed.events"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="completedEventsList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
										<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</c:if>
		</div>

		<!-- ********* -->
		<!--  REJECTED -->
		<!-- ********* -->
		<c:if test="${eventRejectedCount > 0}">
			<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility rejectedEventsData">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.rejected.events"/></div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="rejectedEventsList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
											<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
			</div>
		</c:if>


		<!-- ********* -->
		<!--  PO -->
		<!-- ********* -->
		
		<c:if test="${orderedPoCount > 0}">
			<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility prcheseOrdersData">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.po.new"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="poList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.number"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.poName"/></th>
										<%-- <th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.description"/></th> --%>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.buyer"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.businessUnit"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.currency"/></th>
										<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.status"/></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${acceptedPoCount > 0}">
			<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility prcheseOrdersData">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.po.accepted"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="acceptedPoList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.number"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.poName"/></th>
										<%-- <th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.description"/></th> --%>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.buyer"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.businessUnit"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.acceptDate"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.currency"/></th>
										<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.status"/></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</c:if>

        <c:if test="${pendingFormCount > 0}">
			<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility supplierFormsData">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.pending.form"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="pendingFormList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.buyer.name"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="sourcing.Form.Template"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.form.requested.date"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.form.request.owner"/></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</c:if>
			<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility supplierFormsData">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.submitted.form"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="submittedFormList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.buyer.name"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="sourcing.Form.Template"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.form.requested.date"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.form.request.owner"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.form.submitted.date"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.form.submit.owner"/></th>
										<th search-type="select" search-options="supplierFormStatusList"class="align-center width_200 width_200_fix"><spring:message code="application.status"/></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			
		<!-- ********* -->
		<!--  ACTIVE PENDING-->
		<!-- ********* -->
		<c:if test="${eventActivePendingCount > 0}">
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility activeEventsData">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.active.pending.events"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="activePendingList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
										<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		
		<!-- ********* -->
		<!--  ACTIVE SUBMITTED-->
		<!-- ********* -->
		<c:if test="${eventActiveSubmittedCount > 0}">
			<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility activeEventsData">
				<div class="Invited-Supplier-List-table add-supplier ">
					<div class=" ph_tabel_wrapper">
						<div class="col-md-12">
							<div class="nopad table-heading col-md-4"><spring:message code="supplier.dashboard.active.submitted.events"/></div>
						</div>
						<div class="main_table_wrapper ph_table_border ">
							<table id="activeSubmittedEventsList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.eventname"/></th>
										<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="eventdetails.event.referencenumber" /></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventstartdate"/></th>
										<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="application.eventenddate"/></th>
										<th search-type="select" search-options="rfxList" class="align-center width_150_fix"><spring:message code="application.eventtype" /></th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		

		<c:if test="${isFilter}">
			<div class="row event_listing_section supplierEventsListing">
				<c:forEach items="${publicEventList}" var="event">
					<form name="form" method="get" action="${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}">
						<input type="hidden" id="eventId" value="${event.id}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="col-sm-6 col-md-4 col-xs-6">
							<article class="event_box">
								<header class="event_box_head">
									<h4>
										${event.eventOwner.buyer.companyName} <span class="closed_grp_txt"><spring:message code="supplier.dashboard.closed.group"/></span>
									</h4>
								</header>
								<div class="box-boder">
									<div class="box_content">
										<h4>${event.type}
											<span style="color: #14a2e7" class="closed_grp_txt_mod">${event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED' ? 'CLOSED' : event.status}</span>
										</h4>
										<p>${event.eventName}</p>
										<div class="date_box bg_aqua">
											<span class="start_d_txt"><spring:message code="application.startdate"/></span> <span class="date_event"> <fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${event.eventStart}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /> <%-- </span> <span class="end_t_txt"><fmt:formatDate type="time" timeStyle="short" value="${event.eventStart}" /></span> --%>
										</div>
										<div class="date_box bg_blue last_element">
											<span class="start_d_txt"><spring:message code="rfaevent.end.date"/></span> <span class="date_event"> <fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${event.eventEnd}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</span>
											<%-- <span class="end_t_txt"><fmt:formatDate type="time" timeStyle="short" value="${event.eventEnd}" /></span> --%>
										</div>
									</div>
									<c:if test="${event.type == 'RFT'}">
										<footer class="event_box_footer">
											<span> <spring:message code="supplier.dashboard.event.fees"/> : ${event.participationFeeCurrency.currencyCode} <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.participationFees}" />
											</span>
										</footer>
									</c:if>
								</div>
								<span class="box_overlay"> <a class="btn btn-info hvr-pop hvr-rectangle-out" href="#" onclick="$(this).closest('form').submit()" id="idKnowMore"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
								</a>
								</span>
							</article>
						</div>
					</form>
				</c:forEach>
			</div>
		</c:if>
	</div>
</div>
</div>
<style>
li.filterDashbord {
	margin-left: 10px;
}

li.filterDashbord label {
	line-height: 1;
}
</style>
<script type="text/javascript">	
<c:if test="${eventPendingCount > 0}">
var pendingEventsData;
$('document').ready(function() {
	pendingEventsData = $('#pendingEventsList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/pendingEventList",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title="View"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"data" : "type",
			"orderable" : false,
			"className" : "align-center",
			"defaultContent" : ""
		} ],

	"initComplete": function(settings, json) {
	 var htmlEventPendingSearch = '<tr class="tableHeaderWithSearch">';
		$('#pendingEventsList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPendingSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">All ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventPendingSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPendingSearch += '</select></th>';
				} else {
					htmlEventPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventPendingSearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#pendingEventsList thead').append(htmlEventPendingSearch);
		$(pendingEventsData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				pendingEventsData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(pendingEventsData.table().container()).on('change', 'thead select', function() {
			pendingEventsData.column($(this).data('index')).search(this.value).draw();
		});
	
	}
	});
		
	
});
</c:if>


<c:if test="${eventAccseptCount > 0}">

var pendingEventsData1;
$('document').ready(function() {
	pendingEventsData1 = $('#pendingEventsList1').DataTable({
			"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : false,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/acceptedEventList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 3, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
		
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"data" : "type",
			"orderable" : false,
			"className" : "align-center",
			"defaultContent" : ""
		} ],
	
	"initComplete": function(settings, json) {
	 var htmlEventPendingSearch = '<tr class="tableHeaderWithSearch">';
		$('#pendingEventsList1 thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPendingSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventPendingSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPendingSearch += '</select></th>';
				} else {
					htmlEventPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventPendingSearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#pendingEventsList1 thead').append(htmlEventPendingSearch);
		$(pendingEventsData1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				pendingEventsData1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(pendingEventsData1.table().container()).on('change', 'thead select', function() {
			pendingEventsData1.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
});
</c:if>



<c:if test="${eventSuspendedCount > 0}">
	
	var suspendedEventsData;
	$('document').ready(function() {
		suspendedEventsData = $('#suspendedEventList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/supplier/suspendedEventList",
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
					//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
					
					return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" /> ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
				},
			}, {
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "referanceNumber",
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
			},{
				"data" : "type",
				"orderable" : false,
				"className" : "align-center",
				"defaultContent" : ""
			} ],
		
		"initComplete": function(settings, json) {
		 var htmlEventSuspendedSearch = '<tr class="tableHeaderWithSearch">';
			$('#suspendedEventList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
		//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
				var classStyle =  $(this).attr("class");
				if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
					classStyle = classStyle.replace('sorting','');
				}
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
					 if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlEventSuspendedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
						if (optionsType == 'rfxList') {
							<c:forEach items="${rfxList}" var="item">
							htmlEventSuspendedSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlEventSuspendedSearch += '</select></th>';
					} else  {
						htmlEventSuspendedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlEventSuspendedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				}
			});
			htmlEventSuspendedSearch += '</tr>';
		//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
			$('#suspendedEventList thead').append(htmlEventSuspendedSearch);
			$(suspendedEventsData.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					suspendedEventsData.column($(this).data('index')).search(this.value).draw();
				}
			});
			$(suspendedEventsData.table().container()).on('change', 'thead select', function() {
				suspendedEventsData.column($(this).data('index')).search(this.value).draw();
			});
		}
		});
	});
</c:if>



<c:if test="${eventActiveCount > 0}">
var  activeEventsData;
$('document').ready(function() {
	
	activeEventsData = $('#activeEventsList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/activeEventList",
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
				//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
				
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" /> ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"orderable" : false,
			"data" : "type",
			"className" : "align-center",
			"defaultContent" : ""
		} ],
	
		"initComplete": function(settings, json) {
	 var htmlEventClosedSearch = '<tr class="tableHeaderWithSearch">';
		$('#activeEventsList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				 if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventClosedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventClosedSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventClosedSearch += '</select></th>';
				} else  {
					htmlEventClosedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventClosedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventClosedSearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#activeEventsList thead').append(htmlEventClosedSearch);
		$(activeEventsData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				activeEventsData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(activeEventsData.table().container()).on('change', 'thead select', function() {
			activeEventsData.column($(this).data('index')).search(this.value).draw();
		});
		}
});
});
</c:if>

/* 
<c:if test="${eventActiveCount > 0}">
var activeEventsData;
$('document').ready(function() {
	
	activeEventsData = $('#activeEventsList').DataTable({
		"processing" : true,
		"serverSide" : false,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/activeEventList",
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
				//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
				
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"data" : "type",
			"orderable" : false,
			"className" : "align-center",
			"defaultContent" : ""
		} ]
	});
	
	 var htmlEventActiveSearch = '<tr class="tableHeaderWithSearch">';
		$('#activeEventsList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				 if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventActiveSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventActiveSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventActiveSearch += '</select></th>';
				} else  {
					htmlEventActiveSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventActiveSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventActiveSearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#activeEventsList thead').append(htmlEventActiveSearch);
		$(activeEventsData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				activeEventsData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(activeEventsData.table().container()).on('change', 'thead select', function() {
			activeEventsData.column($(this).data('index')).search(this.value).draw();
		});
});
</c:if> */


<c:if test="${eventClosedCount > 0}">
var  closedEventsData;
$('document').ready(function() {
	
	closedEventsData = $('#closedEventsList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/closedEventList",
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
				//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
				
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"data" : "type",
			"orderable" : false,
			"className" : "align-center",
			"defaultContent" : ""
		} ],
	
		"initComplete": function(settings, json) {
	 var htmlEventClosedSearch = '<tr class="tableHeaderWithSearch">';
		$('#closedEventsList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				 if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventClosedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventClosedSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventClosedSearch += '</select></th>';
				} else  {
					htmlEventClosedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventClosedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventClosedSearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#closedEventsList thead').append(htmlEventClosedSearch);
		$(closedEventsData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				closedEventsData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(closedEventsData.table().container()).on('change', 'thead select', function() {
			closedEventsData.column($(this).data('index')).search(this.value).draw();
		});
		}
});
});
</c:if>


<c:if test="${eventCompletedCount > 0}">
var  completeEventsData;
$('document').ready(function() {
	
	completeEventsData = $('#completedEventsList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/completedEventList",
			"data" : function(d) {
			}
		},
		"order" : [ [ 3, "desc" ] ],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
				//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
				
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"data" : "type",
			"orderable" : false,
			"className" : "align-center",
			"defaultContent" : ""
		} ],
	
		"initComplete": function(settings, json) {
	 var htmlEventCompletedSearch = '<tr class="tableHeaderWithSearch">';
		$('#completedEventsList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				 if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventCompletedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventCompletedSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventCompletedSearch += '</select></th>';
				} else  {
					htmlEventCompletedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventCompletedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventCompletedSearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#completedEventsList thead').append(htmlEventCompletedSearch);
		$(completeEventsData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				completeEventsData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(completeEventsData.table().container()).on('change', 'thead select', function() {
			completeEventsData.column($(this).data('index')).search(this.value).draw();
		});
		}
});
});
</c:if>

<c:if test="${eventRejectedCount > 0}">

var rejectedEventsData;
$('document').ready(function() {
	rejectedEventsData = $('#rejectedEventsList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/rejectedEventList",
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
				//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
				
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"data" : "type",
			"orderable" : false,
			"className" : "align-center",
			"defaultContent" : ""
		} ],
	
		"initComplete": function(settings, json) {
	 var htmlEventRejectedSearch = '<tr class="tableHeaderWithSearch">';
		$('#rejectedEventsList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
			 if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventRejectedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventRejectedSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventRejectedSearch += '</select></th>';
				} else  {
					htmlEventRejectedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventRejectedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventRejectedSearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#rejectedEventsList thead').append(htmlEventRejectedSearch);
		$(rejectedEventsData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				rejectedEventsData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(rejectedEventsData.table().container()).on('change', 'thead select', function() {
			rejectedEventsData.column($(this).data('index')).search(this.value).draw();
		});
		}
	});
});
</c:if>

<c:if test="${orderedPoCount > 0}">
$('document').ready(function() {

	// Setup - add a text input to each footer cell
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var prcheseOrdersData = $('#poList').DataTable({
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
			"url" : getContextPath() + "/supplier/orderedPoListData",
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
				var action = '<a href="supplierPrView/' + row.id + '" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				return action;
			}
		},{
			"data" : "poNumber",
			"defaultContent" : ""
		},{
			"data" : "name",
			"defaultContent" : ""
		},{
			"data" : "buyerCompanyName",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "currency",
			"defaultContent" : ""
		},{
			"data" : "grandTotal",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		},{
			"data" : "status",
			"searchable" : false,
			"orderable" : false,
			"defaultContent" : ""
		}],
		"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#poList thead tr:nth-child(1) th').each(function(i) {
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
	$('#poList thead').append(htmlSearch);
	$(prcheseOrdersData.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 2 || this.value.length == 0) {
			prcheseOrdersData.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(prcheseOrdersData.table().container()).on('change', 'thead select', function() {
		prcheseOrdersData.column($(this).data('index')).search(this.value).draw();
	});
	}
	});

});
function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}
</c:if>

<c:if test="${acceptedPoCount > 0}">
$('document').ready(function() {

	// Setup - add a text input to each footer cell
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var prcheseOrdersData1 = $('#acceptedPoList').DataTable({
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
			"url" : getContextPath() + "/supplier/acceptedPoListData",
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
				var action = '<a href="supplierPrView/' + row.id + '" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="supplierPoReport/' + row.id + '" data-placement="top" title=<spring:message code="tooltip.download" />><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		},{
			"data" : "poNumber",
			"defaultContent" : ""
		},{
			"data" : "name",
			"defaultContent" : ""
		},{
			"data" : "buyerCompanyName",
			"defaultContent" : ""
		},{
			"data" : "businessUnit",
			"defaultContent" : ""
		}, {
			"data" : "createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "acceptRejectDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "currency",
			"defaultContent" : ""
		},{
			"data" : "grandTotal",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
			}
		},{
			"data" : "status",
			"searchable" : false,
			"orderable" : false,
			"defaultContent" : ""
		}],
		"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#acceptedPoList thead tr:nth-child(1) th').each(function(i) {
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
	$('#acceptedPoList thead').append(htmlSearch);
	$(prcheseOrdersData1.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 2 || this.value.length == 0) {
			prcheseOrdersData1.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(prcheseOrdersData1.table().container()).on('change', 'thead select', function() {
		prcheseOrdersData1.column($(this).data('index')).search(this.value).draw();
	});
	}
	});

});
function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}
</c:if>

<c:if test="${pendingFormCount > 0}">
$('document').ready(function() {

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var supplierFormsData = $('#pendingFormList').DataTable({
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
			"url" : getContextPath() + "/supplier/pendingFormListData",
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
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/supplier/supplierFormView/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		},{
			"data" : "companyName",
			"defaultContent" : ""
		},{
			"data" : "name",
			"defaultContent" : ""
		}, {
			"data" : "requestedDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "requestedBy",
			"searchable" : false,
			"defaultContent" : ""
		}],
		"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#pendingFormList thead tr:nth-child(1) th').each(function(i) {
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
	$('#pendingFormList thead').append(htmlSearch);
	$(supplierFormsData.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 2 || this.value.length == 0) {
			supplierFormsData.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(supplierFormsData.table().container()).on('change', 'thead select', function() {
		supplierFormsData.column($(this).data('index')).search(this.value).draw();
	});
	}
	});

});
</c:if>

$('document').ready(function() {

	// Setup - add a text input to each footer cell
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var supplierFormsData1 = $('#submittedFormList').DataTable({
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
			"url" : getContextPath() + "/supplier/submittedFormListData",
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
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
				return '<a href="${pageContext.request.contextPath}/supplier/supplierFormView/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		},{
			"data" : "companyName",
			"defaultContent" : ""
		},{
			"data" : "name",
			"defaultContent" : ""
		}, {
			"data" : "requestedDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "requestedBy",
			"searchable" : false,
			"defaultContent" : ""
		},{
			"data" : "submitedDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "submittedBy",
			"searchable" : false,
			"defaultContent" : ""
		}, {
			"data" : "status",
			"defaultContent" : "",
			"className" : "align-center"
		}],
		"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#submittedFormList thead tr:nth-child(1) th').each(function(i) {
		var title = $(this).text();
		if (!(title == "Actions") && $(this).attr('search-type') != '') {
			if ($(this).attr('search-type') == 'select') {
				var optionsType = $(this).attr('search-options');
				htmlSearch += '<th style="' + $(this).attr("style") + ';text-align: center"><select data-index="'+i+'">';
				if (optionsType == 'supplierFormStatusList') {
					htmlSearch += '<option value="ALL">Search Status</option>';
					<c:forEach items="${supplierFormStatusList}" var="item">
					htmlSearch += '<option value="${item}">${item}</option>';
					</c:forEach>
				}
				htmlSearch += '</select></th>';
			} else {
				htmlSearch += '<th ><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
			}
		} else {
			htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
		}
	});
	
	
	htmlSearch += '</tr>';
	$('#submittedFormList thead').append(htmlSearch);
	$(supplierFormsData1.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 2 || this.value.length == 0) {
			supplierFormsData1.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(supplierFormsData1.table().container()).on('change', 'thead select', function() {
		supplierFormsData1.column($(this).data('index')).search(this.value).draw();
	});
	}
	});

});


<c:if test="${registrationIncompleteFlag == true}">
$('document').ready(function() {
	$('.incomp-icon').show();
});

</c:if>


<c:if test="${eventActivePendingCount > 0}">
console.log("fghjkl*************");

var  activeEventsData;

$('document').ready(function() {
	
	activeEventsData = $('#activePendingList').DataTable({
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
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/activePendingData",
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
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
				//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
				
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" /> ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"orderable" : false,
			"data" : "type",
			"className" : "align-center",
			"defaultContent" : ""
		} ],
	
		"initComplete": function(settings, json) {
	 var htmlEventPendingearch = '<tr class="tableHeaderWithSearch">';
		$('#activePendingList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				 if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPendingearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventPendingearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPendingearch += '</select></th>';
				} else  {
					htmlEventPendingearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPendingearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventPendingearch += '</tr>';
	//	console.log("htmlEventScheduledSearch : " + htmlEventScheduledSearch);
		$('#activePendingList thead').append(htmlEventPendingearch);
		$(activeEventsData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				activeEventsData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(activeEventsData.table().container()).on('change', 'thead select', function() {
			activeEventsData.column($(this).data('index')).search(this.value).draw();
		});
		}
});
});
</c:if>


<c:if test="${eventActiveSubmittedCount > 0}">
var  activeEventsData1;
$('document').ready(function() {
	
	activeEventsData1 = $('#activeSubmittedEventsList').DataTable({
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
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/activeSubmittedList",
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
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				var type = row.type;
				//${pageContext.request.contextPath}/supplier/supplierEvent/${event.type}/${event.id}
				
				return '<a href="${pageContext.request.contextPath}/supplier/supplierEvent/'+type+'/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" /> ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
			},
		}, {
			"data" : "eventName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "referanceNumber",
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
		},{
			"orderable" : false,
			"data" : "type",
			"className" : "align-center",
			"defaultContent" : ""
		} ],
	
		"initComplete": function(settings, json) {
	 var htmlEventSubmittedSearch = '<tr class="tableHeaderWithSearch">';
		$('#activeSubmittedEventsList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
	//		console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
			
				 if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventSubmittedSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlEventSubmittedSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventSubmittedSearch += '</select></th>';
				} else  {
					htmlEventSubmittedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventSubmittedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlEventSubmittedSearch += '</tr>';
		$('#activeSubmittedEventsList thead').append(htmlEventSubmittedSearch);
		$(activeEventsData1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				activeEventsData1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(activeEventsData1.table().container()).on('change', 'thead select', function() {
			activeEventsData1.column($(this).data('index')).search(this.value).draw();
		});
		}
});
});
</c:if>


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

</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/chardinjs.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chardin/chardinjs.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>