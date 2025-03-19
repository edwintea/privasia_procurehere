<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<script type="text/javascript" src="<c:url value="/resources/js/view/addSupplier.js?2"/>"></script>
<spring:message var="rfxCreateSuppliers" code="application.rfx.create.suppliers" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreateSuppliers}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard">
						<spring:message code="application.dashboard" />
					</a></li>
				<li class="active">${eventType.value}</li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap tender-request-heading">
					<spring:message code="supplier.add" />
				</h2>
				<h2 class="trans-cap pull-right">
					<spring:message code="application.status" />
					: ${event.status}
				</h2>
			</div>
			<jsp:include page="eventHeader.jsp" />
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<c:set var="readOnlySupplier" value="${ event.template != null && event.template.readOnlySupplier ? true : false }" />
			<c:set var="supplierBasedOnState" value="${ event.template != null && event.template.supplierBasedOnState ? true : false }" />
			<c:set var="restrictSupplierByState" value="${ event.template != null && event.template.restrictSupplierByState ? true : false }" />
			<c:set var="visibleSupplierTags" value="${ event.template != null && event.template.visibleSupplierTags ? true : false }" />
			<c:set var="optionalSupplierTags" value="${ event.template != null && event.template.optionalSupplierTags ? true : false }" />
			<c:set var="visibleGeographicalCoverage" value="${ event.template != null && event.template.visibleGeographicalCoverage ? true : false }" />
			<c:set var="optionalGeographicalCoverage" value="${ event.template != null && event.template.optionalGeographicalCoverage ? true : false }" />

			<c:if test="${!readOnlySupplier}">
				<form:form class="form-horizontal" commandName="supplierSearchPojo" id="supplierForm">
					<input type="hidden" name="eventId" id="eventId" value="${event.id}" />
					<input type="hidden" name="eventStatus" id="eventStatus" value="${event.status}" />
					<div class="Invited-Supplier-List white-bg pad_all_15">
						<div class="row">
							<div class="col-sm-4 col-md-4 col-xs-4">
								<label class="marg-top-10"><spring:message code="import.search.global.list" />:</label>
							</div>
							<div class="col-md-4 col-sm-5">
								<div class="input-group search_box_gray">
									<select class="chosenCategoryAll chosen-select" id="chosenCategoryAll">
										<option value=""><spring:message code="eventsummary.invitedsupplier.search.supplier" /></option>
										<c:forEach items="${sppliers}" var="supplier">
											<c:if test="${supplier.id == '-1'}">
												<option value="" disabled>${supplier.companyName}</option>
											</c:if>
											<c:if test="${ supplier.id != '-1'}">
												<option value="${supplier.id}">${supplier.companyName}</option>
											</c:if>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="col-md-2 col-sm-2 ">
								<button class="btn btn-info submitCompanyName hvr-pop hvr-rectangle-out" type="submit" data-placement="top" data-toggle="tooltip" data-placement="top" title='<spring:message code="tooltip.addSupplier.more" />' id="addMore">
									<i id="addMoreSupplier" class="fa fa-plus" aria-hidden="true"></i>
								</button>
							</div>
							<c:if test="${restrictSupplierByState || visibleSupplierTags || visibleGeographicalCoverage}">
								<button class="btn btn-info hvr-pop hvr-rectangle-out filterBy_State" type="button" data-toggle="tooltip" data-placement="top" data-original-title="Advance Filter">
       								 <span class="button-content">
            							<spring:message code="supplier.advanceFilter" />
       								 </span>
								</button>
							</c:if>

						</div>
					</div>
				</form:form>
			</c:if>

			<%-- 			
			as per PH 179 comment when this will required please uncomment CSS below
			<c:if test="${!readOnlySupplier}">
				<form:form class="form-horizontal" commandName="supplierSearchPojo" id="supplierForm">
					<input type="hidden" name="eventId" id="eventId" value="${event.id}" />
					<input type="hidden" name="eventStatus" id="eventStatus" value="${event.status}" />
					<div class="Invited-Supplier-List white-bg pad_all_15">
						<div class="d-flex content-bet">
							<div>
								<label class="marg-top-10">Search Suppliers:</label>
							</div>							
							<div class="input-group search_box_gray d-flex">
								<select class="chosen-select" id="chosenCategoryAll">
									<option value="">Search your favorite Suppliers</option>
									<c:forEach items="${sppliers}" var="supplier">
										<option value="${supplier.supplier.id}">${supplier.supplier.companyName}</option>
									</c:forEach>
								</select>
								<button class="m-l-15 btn btn-info submitCompanyName hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" title="Add More" id="addMore">
									<i class="fa fa-plus" aria-hidden="true"></i>
								</button>
							</div>																					
							<div>
								<a href="${pageContext.request.contextPath}/buyer/importSupplier" class="btn btn-info align">Register New Supplier</a>
							</div>
						</div>
					</div>
				</form:form>
			</c:if>
 --%>
		</div>
		<div style="position: relative">
			<c:if test="${!readOnlySupplier && !buyerReadOnlyAdmin}">
			<button data-toggle="modal" data-target="#myModalDelConfirm" type="submit" style="float: right; position: absolute; right: 15px; top: 20px; z-index: 10 !important;" class="disabled btn btn-black ph_btn_midium ph_btn_midium hvr-pop hvr-rectangle-out1 previousStep1 del-supplier" value="Delete"
				name="delete" id="deleteSuppliers">
				<spring:message code="application.delete" />
			</button>
			</c:if>
			<div class="clear"></div>
			<div class="container-fluid">
				<div class="row">
					<div class="col-xs-12">
						<section class="index_table_block marg-top-20">
							<div class="ph_tabel_wrapper scrolableTable_UserList">
								<table id="supplierDataTable" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr>
											<c:if test="${!readOnlySupplier}">
												<th class="align-left header-center width_50 width_50_fix"><input type="checkbox" id="select-all" class="allInvitedsupp zeroTopMargin"></th>
											</c:if>
											<th class="width_200  width_200_fix"><spring:message code="eventsummary.invitedsupplier.companyname" /></th>
											<th class="width_200 width_200_fix"><spring:message code="invitedSupplier.contactEmailAddress" /></th>
											<th class="width_200 width_200_fix"><spring:message code="invitedSupplier.contactNumber" /></th>
										</tr>
									</thead>
									<tbody id="tableBody">
									</tbody>
								</table>
							</div>
						</section>
					</div>
				</div>

				<div class="row">
					<div class="col-md-12 marg-top-20">
						<form:form class="bordered-row" id="submitPriviousForm" method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/eventSupplierPrevious" style="float:left;">
							<form:hidden path="id" />
							<button type="submit" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1 previousStep1" value="Previous" name="previous" id="priviousStep">
								<spring:message code="application.previous" />
							</button>
						</form:form>
						<form:form class="bordered-row" id="submitNextForm" method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/eventSupplierNext" style="float:left;">
							<form:hidden path="id" />
							<form:button type="submit" class="btn btn-info ph_btn marg-left-10 hvr-pop hvr-rectangle-out" id="nextStep">
								<spring:message code="event.document.next" />
							</form:button>
						</form:form>
						<spring:message code="application.draft" var="draft" />
						<form action="${pageContext.request.contextPath}/buyer/${eventType}/inviteSupplierSaveDraft" method="post" style="float: right;">
							<input type="hidden" id="eventId" value="${event.id}" name="eventId">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<c:if test="${event.status eq 'DRAFT'}">
								<input type="submit" id="idMeetingSaveDraft" class="step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop pull-right" value="${draft}" />
							</c:if>
						</form>
						<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') && (isAdmin or eventPermissions.owner)}">
							<spring:message code="event.cancel.draft" var="cancelDraftLabel" />
							<spring:message code="cancel.event.button" var="cancelEventLabel" />
							<a href="javascript:void(0);" role="button" class="btn btn-danger marg-right-10 ph_btn hvr-pop right-header-button cancelEvent" id="cancelEve" data-toggle="modal">${event.status eq 'DRAFT' ? cancelDraftLabel : cancelEventLabel}</a>
						</c:if>
					</div>
				</div>

			</div>
		</div>
		<div class="clear"></div>
	</div>
</div>
</div>
<!-- supplier delete popup  -->
<div class="modal fade" id="myModalDelConfirm" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content" style="width: 113%;">
			<div class="modal-header" style="align-items: center; display: flex; justify-content: space-between;">
				<div style="width: 100%;">
					<h3>
						<spring:message code="application.confirm.delete" />
					</h3>
				</div>
				<div>
					<button type="button" class="close cancelPopUpBtn" aria-label="Close" style="margin-top: 0 !important;">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
			</div>
			<div class="modal-body">
				<div style="display: flex">
					<div>
						<span id="labelId">
							<spring:message code="suppliers.sure.delete.all.supplier" />
						</span>
						<span type="text" id="supplierInput"></span>
						<span>
							<spring:message code="suppliers.selected" />
						</span>
					</div>
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 cancelPopUpBtn" href="javascript:void(0);" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</a>
				<button style="margin-left: 0" id="idConfirmDeleteSelectedSupplier" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
					<spring:message code="application.delete" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form:form modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/cancelEvent" method="post">
				<form:hidden path="id" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">

							<label> <spring:message code="eventsummary.confirm.to.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<form:textarea path="cancelReason" id="cancelReason" class="width-100" placeholder="${reasonCancel}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form:form>
		</div>
	</div>
</div>

<div class="modal fade" id="filterBy_State" role="dialog">
	<div class="modal-dialog" style="width: 90%; max-width: 800px;">
		<!-- Modal content-->
		<form:form modelAttribute="buyer">
			<input type="hidden" name="eventId" id="eventId" value="${event.id}" />
			<input type="hidden" name="eventStatus" id="eventStatus" value="${event.status}" />
			<input type="hidden" id="eventTypeSearch" value="${eventType}" name="eventType">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-content">
				<div class="modal-header">
					<h3>
						<spring:message code="add.supplierby.state" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
				</div>

				<div class="modal-body">
					<div class="row">
						<div class="col-sm-10 col-md-10 col-xs-10">
							<c:if test="${visibleSupplierTags}">
							<div class="rec_form_row" style="margin-top: 1.5%;">
								<div class="col-sm-3 col-md-3 col-xs-3">
									<label class="marg-top-10"> <spring:message code="label.suppliertags" /></label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-6">
									<form:select multiple="multiple" path="supplierTagName" autocomplete="off" data-placeholder="Select Supplier Tags" cssClass="form-control chosen-select" class="chosen-select rec_inp_style2" id="supplierTagName">
										<form:options items="${supplierTagsList}" itemValue="id" itemLabel="supplierTags"></form:options>
									</form:select>
								</div>
								<div class="input-group-btn">
									<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown" style="min-width: 45px; margin-left: -6px;">
										<i class="fa fa-user" aria-hidden='true'></i>
									</button>
									<ul class="dropdown-menu dropup">
										<li><a href="javascript:void(0);" class="small " tabIndex="-1">
												<input id="access_inclusive" value="Inclusive" class="access_check" type="checkbox" />
												&nbsp;
												<spring:message code="application.inclusive" />
											</a></li>
										<li><a href="javascript:void(0);" class="small " tabIndex="-1">
												<input id="access_Exclusive" value="Exclusive" class="access_check" type="checkbox" />
												&nbsp;
												<spring:message code="application.exclusive" />
											</a></li>

									</ul>
								</div>
							</div>
							</c:if>
							<c:if test="${restrictSupplierByState}">
							<div class="rec_form_row">
								<div class="col-sm-3 col-md-3 col-xs-3">
									<label class="marg-top-10"><spring:message code="application.country" /></label>
								</div>
								<div class="col-sm-7 col-md-7 col-xs-7" id="rm-border">
									<form:select path="registrationOfCountry" id="registrationOfCountry" autocomplete="off" class="chosen-select rec_inp_style2 ">
										<form:option value="">
											<spring:message code="application.country" />
										</form:option>
										<form:options items="${registeredCountry}" itemValue="id" itemLabel="countryName" />
									</form:select>
								</div>
							</div>
							<div class="rec_form_row">
								<div class="col-sm-3 col-md-3 col-xs-3">
									<label class="marg-top-10"> <spring:message code="application.state" /></label>
								</div>
								<div class="col-sm-7 col-md-7 col-xs-7" id="selectState">
									<form:select multiple="multiple" path="state" data-placeholder="Select State" autocomplete="off" cssClass="form-control chosen-select" class="chosen-select rec_inp_style2" id="stateList">
										<form:options items="${countryStates}" itemValue="id" itemLabel="stateName"></form:options>
									</form:select>
								</div>
							</div>
					</c:if>
							<c:if test="${visibleGeographicalCoverage}">
							<div class="rec_form_row">
								<div class="col-sm-3 col-md-3 col-xs-3">
									<label class="marg-top-10"> <spring:message code="supplier.geo.coverage" /></label>
								</div>
								<div class="col-sm-7 col-md-7 col-xs-7" id="geo">
									<form:select multiple="multiple" path="coverage" data-placeholder="Select Geographical Coverage" autocomplete="off" cssClass="form-control chosen-select" class="chosen-select rec_inp_style2" id="coverageList">
										<form:options items="${coverageList}" itemValue="id" itemLabel="countryName"></form:options>
									</form:select>
								</div>
							</div>
							</c:if>
							<div class="searchStateAndTag col-sm-4 col-md-4 col-xs-4"></div>
						</div>
						<div class="col-sm-2 col-md-2 col-xs-2">
							<button value="Search" class="searchCatAndState btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" style="margin-top: 160%; margin-left: -65%;" title="Search">
								<i class="fa fa-search" aria-hidden="true"></i>
								<spring:message code="application.search.stateAndtag" />
							</button>
						</div>
						<div class="col-xs-12">
							<div class="ph_tabel_wrapper scrolableTable_UserList">
								<div class="table-responsive width100 borderAllData">
									<table class="display table table-bordered noarrow" id="tableList2" cellpadding="0" cellspacing="0" border="0">
										<thead>
											<tr>
												<th class="align-left header-center width_50 width_50_fix"><input type="checkbox" id="example-select-all" class="allInvitedsupp zeroTopMargin"></th>
												<th class="align-left header-center width_200 width_200_fix"><spring:message code="invitedSupplier.companyName" /></th>
												<th class="align-left header-center width_200 width_200_fix"><spring:message code="invitedSupplier.contactEmailAddress" /></th>
												<th class="align-left header-center width_200 width_200_fix"><spring:message code="invitedSupplier.contactNumber" /></th>
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

				<div class="modal-footer text-center">
					<button value="Search" class="addFavSupplierList btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" title="Search">
						<spring:message code="invitedSupplier.addsupplier" />
					</button>
					<a class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</a>
				</div>
			</div>
		</form:form>
	</div>
</div>

<style>
@media only screen and (min-width: 991px) and (max-width: 1550px) {
	.addsupplier {
		width: 40%;
		text-align: left;
	}
}

.custom-checkbox1 {
	position: relative;
	top: -5px;
	left: 4px;
}

.vert-center {
	vertical-align: middle !important;
	padding: 8px !important;
}

.table>thead>tr>th {
	padding: 16px 12px !important;
}

.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #dddddd !important;
}

.ph_tabel_wrapper>div.dataTables_wrapper>div.row {
	display: block;
}

.ph_table td, .ph_table th {
	text-align: left;
}

.mt-3 {
	margin-top: 10%;
}

.suppli-button {
	background-color: Transparent;
	border: none;
}
/*  when add suppier button requerd please un comment this PH 179 */
/* .d-flex {
	display: flex;
} 
.content-bet {
	justify-content: space-between;
}
.search_box_gray {
	width:20%;	
}
.m-l-15 {
	margin-left: 15px;
} */
/* .del-supplier {
	height: auto !important;
	min-width: auto !important;
	font-size: 16px !important;
	line-height: 35px !important;
} */
</style>
<script type="text/javascript">
	var eventDraft = "${event.status eq 'DRAFT'}";
	var supplierDataTable;
	$('document').ready(function() {
		supplierDataTable=$('#supplierDataTable').DataTable({ 
		 "oLanguage":{
	 			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json",
	 			"sProccessing" :"Loading..."
			}, 
			"processing" : true,
			"serverSide" : true,
			"deferLoading": 0,
			"pageLength" : 10,
			"searching" : false,
			"ordering" : false,
			"ajax" : {
				"url" : getContextPath() + "/buyer/${eventType}/getEventSuppliers/${event.id}",
				"data" : function(d) {
 				}
			},
			"columns" : [ 
			<c:if test="${!readOnlySupplier}">
			{
				"searchable" : false,
				"orderable" : false,
				"className" : "pad-left-10 align-left",
				"render" : function(data, type, row) {
				var del = '';
				if (eventDraft === "true") {
					<c:if test="${!eventPermissions.viewer && !buyerReadOnlyAdmin}">
					//del = '<a href="" data-id="'+row.id+'" data-suppId="'+row.id+'" class="deleteSupplier" title=<spring:message code="application.delete" />><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
					del = '<th class="align-left header-center width_50 width_50_fix"><input type="checkbox" value="'+row.id+'" id="'+row.id+'" class="selectedSupplier allInvitedsupp zeroTopMargin">';
					</c:if>
				} 
					return del;
			  },
			},
			</c:if>
			{ 
				"data" : "companyName",
				"orderable" : false,
			},
			{
				"data" : "communicationEmail",
				"orderable" : false,
			}, 
			{ 
				"data" : "companyContactNumber",
				"orderable" : false,
			}],
			"initComplete" : function(settings, json){
				console.log('Data render completed....');
				$('#select-all').prop('checked', false);
				if($("#supplierDataTable .selectedSupplier:checked").length == 0) {
					$('#deleteSuppliers').addClass("disabled");
				}
			}
		});
		
		$('#supplierDataTable').on( 'draw.dt', function () {
			console.log('Data render completed....');
			$('#select-all').prop('checked', false);
			if($("#supplierDataTable .selectedSupplier:checked").length == 0) {
				$('#deleteSuppliers').addClass("disabled");
			}
		});
		
		$('#select-all').prop('checked', false);
		supplierDataTable.ajax.reload();
	});
</script>
<c:if test="${eventType eq 'RFA'}">
	<script>
	<jsp:useBean id="today" class="java.util.Date" />
	<c:if test="${event.status =='SUSPENDED'}">
	<c:if test="${event.eventStart lt today}">
	$(window).bind('load', function() {
		var allowedFields = '#nextStep,#priviousStep,#supplierDataTable_length,#supplierDataTable_paginate,.form-control,#bubble,#cancelEve,#idMeetingSaveDraft';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${event.eventStart gt today}">
	$(window).bind('load', function() {
		var allowedFields = '#nextStep,#priviousStep,#supplierDataTable_length,#supplierDataTable_paginate,.form-control,#bubble,#cancelEve,#addMore,#chosenCategoryAll';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	</c:if>
</script>
</c:if>
<!-- WIDGETS -->
<script>
<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
$(window).bind('load', function() {
	var allowedFields = '#nextStep,#priviousStep,#bubble,#dashboardLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});
</script>

<script type="text/javascript">




$(document).ready(function() {

	$('#rfxCancelEvent').click(function() {
		var cancelRequest = $('#cancelReason').val();
		if (cancelRequest != '') {
			$(this).addClass('disabled');
		}
	});

	$(document).delegate('.access_check', 'click', function(e) {
		$('.access_check').prop('checked', false);
		$(this).prop('checked', true);
		tempId = $(this).attr("id");
		selector = $("#" + tempId).closest("div").find(".dropdown-toggle");
		console.log(selector);
		if ($(this).val() == "Inclusive") {

			selector.html("<i class='fa fa-user' aria-hidden='true '></i>");
		}
		if ($(this).val() == "Exclusive") {
			selector.html("<i class='fa fa-user' aria-hidden='true '></i>");
		}

	});
	
		$('#supplierTagName').change(function() {
			$('.searchStateAndTag').parent().removeClass('has-error').find('.form-error').remove();
			$('.error-range.text-danger').remove();
			if (($('#access_inclusive').prop("checked") == false) && ($('#access_Exclusive').prop("checked") == false)) {
				$('#access_inclusive').prop('checked', true);
			}
	});

		
			$(document).delegate('#selectState', 'click', function(e) {
				var supplierByCountry =($('#registrationOfCountry').val());
				$('#registrationOfCountry').parent().removeClass('has-error').find('.form-error').remove();
				$('#stateList').parent().removeClass('has-error').find('.form-error').remove();	
				if(supplierByCountry == null || supplierByCountry == ''){
						$('#registrationOfCountry').parent().addClass('has-error').append('<span class="help-block form-error">Please Select Country</span>');
					return false;
					}else{
						$('#registrationOfCountry').parent().removeClass('has-error').find('.form-error').remove();
					}
		});
	
	$(document).delegate('.filterBy_State', 'click', function(e) {

		var countryBy = ($('#stateList').val());
		var stateBy = ($('#registrationOfCountry').val());
		$('#filterBy_State').modal();
	 	$('#filterBy_State').find('#registrationOfCountry').val('');
		$('#filterBy_State').find('#registrationOfCountry').trigger("chosen:updated");
		$('#filterBy_State').find('#stateList').val('');
		$('#filterBy_State').find('#coverageList').val('');
		$('#filterBy_State').find('#coverageList').trigger("chosen:updated");
		$('#stateList').empty(); 
		$('#filterBy_State').find('#stateList').trigger("chosen:updated"); 
		$('#filterBy_State').find('#supplierTagName').val('');
		$('#filterBy_State').find('#supplierTagName').trigger("chosen:updated");
		$('#tableList2 tbody').html('');
		$('#tableList2').DataTable().rows().remove().draw();
		$('.error-range.text-danger')
		.remove();
		$('#stateList').parent().removeClass('has-error').find('.form-error').remove();
		$('#coverageList').parent().removeClass('has-error').find('.form-error').remove();
		$('#registrationOfCountry').parent().removeClass('has-error').find('.form-error').remove();
		$("#example-select-all").prop("checked", false);
		$("#access_inclusive").prop("checked", false);
		$("#access_Exclusive").prop("checked", false);
	 	$('#rm-border').removeClass('has-error');
		$('#geo').removeClass('has-error');
	 	$('.searchStateAndTag').parent().removeClass('has-error').find('.form-error').remove();
	});
	
	
	$(".searchCatAndState").click( function(e) {
		e.preventDefault();

		$("#example-select-all").prop("checked", false);
		var supplierBysuppTag =($('#supplierTagName').val());
		if(supplierBysuppTag == null || supplierBysuppTag == ''){
			$("#access_inclusive").prop("checked", false);
			$("#access_Exclusive").prop("checked", false);
		}
	
		var supplierByState =($('#stateList').val());
		var supplierByCountry =($('#registrationOfCountry').val());
		var suppliertags =  $.trim($('#supplierTagName').val());
		var coverage = ($('#coverageList').val());
		
		$('.error-range.text-danger').remove();
		$('#stateList').parent().removeClass('has-error').find('.form-error').remove();
		$('#coverageList').parent().removeClass('has-error').find('.form-error').remove();
		$('#registrationOfCountry').parent().removeClass('has-error').find('.form-error').remove();
		$('.searchStateAndTag').parent().removeClass('has-error').find('.form-error').remove();
		
		if(${restrictSupplierByState == true && !supplierBasedOnState}){
			if(supplierByCountry == null || supplierByCountry == ''){
				$('#registrationOfCountry').parent().addClass('has-error').append('<span class="help-block form-error">Please Select Country</span>');
			return false;
			}else{
				$('#registrationOfCountry').parent().removeClass('has-error').find('.form-error').remove();
			}
		}  
	
		if(${restrictSupplierByState == true && !supplierBasedOnState}){
			if (supplierByState == null || supplierByState == '') {
				$('#stateList').parent().addClass('has-error').append('<span class="help-block form-error">Please Select at least one state</span>');
				return false;
			}else{
				$('#stateList').parent().removeClass('has-error').find('.form-error').remove();
			}
		}
        if(${visibleSupplierTags == true && !optionalSupplierTags}) {
            if (suppliertags == null || suppliertags == '') {
                $('.searchStateAndTag').parent().addClass('has-error').append('<span class="help-block form-error"> Please select at least one Supplier Tag</span>');
                return false;
            } else {
                $('.searchStateAndTag').parent().removeClass('has-error').find('.form-error').remove();
            }
        }
		if(${visibleGeographicalCoverage == true && !optionalGeographicalCoverage }){
			if (coverage == null || coverage == '') {
				$('#coverageList').parent().addClass('has-error').append('<span class="help-block form-error">Please Select at least one Geographical Coverage</span>');
				return false;
			}else{
				$('#coverageList').parent().removeClass('has-error').find('.form-error').remove();
			}
		}
	});
	
	
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$('#registrationOfCountry').change(function() {
		var countryId = $('#registrationOfCountry').val();
		$('#registrationOfCountry').parent().removeClass('has-error').find('.form-error').remove();
		$('.searchStateAndTag').parent().removeClass('has-error').find('.form-error').remove();
		$('#stateList').parent().removeClass('has-error').find('.form-error').remove();
		$('.error-range.text-danger').remove();
		console.log("countryId"+countryId);
		$.ajax({ type : "GET", url : getContextPath() + "/buyer/countryStatesList", data : { countryId : countryId }, beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		}, success : function(data) {
 			$('#stateList').empty(); 
 			var stateList1;
			$.each(data, function(i, obj) {
				stateList1+= '<option value="' + obj.id + '">' + obj.stateName + '</option>';
			});
			$('#loading').hide();
			if(stateList1 !=undefined){
			$('#stateList').html(stateList1);				
			}
			$('#stateList').trigger("chosen:updated");
		}, error : function(e) {
			console.log("Error");
			$('#loading').hide();
		}, });
	});

});



$('#chosenCategoryAll').change(function() {
	var addSupplierToEvent =  $.trim($('#chosenCategoryAll').val());
	if (addSupplierToEvent == null || addSupplierToEvent == '') {
		$("#addMoreSupplier").addClass("disabled", true);
		$("#addMore").removeAttr("title");
		
	}else{
		$("#addMoreSupplier").removeClass("disabled");
		  $("#addMore").attr('title','<spring:message code="tooltip.addSupplier.more" />');
	}
	
});

$("#addMore").removeAttr("title");
$("#addMoreSupplier").addClass("disabled", true);


$(".addFavSupplierList")
.click(
		function(e) {
			e.preventDefault();

			$('.error-range.text-danger')
					.remove();
			var val = [];
			$('.custom-checkbox1:checked').each(
					function(i) {
						val[i] = $(this).val();
					});
			console.log(val + "val");
			if (typeof val === 'undefined'
				|| val == '') {
			console.log("Error");
			$('.addFavSupplierList')
					.before(
							'<p style="margin-top:10px; margin-right: 73%;" class=" addsupplier error-range text-danger">Please Select atleast one supplier</p>');
			return false;
		} 
		}); 
		
 
$('#select-all').click(function(){
    if($(this).prop("checked") == true) {
    	$('.selectedSupplier').prop('checked', true);
    } else if($(this).prop("checked") == false){
    	$('.selectedSupplier').prop('checked', false);
    }
});

		
$('#test').prop('checked', true);

$("#deleteSuppliers").click(function(){
	  //Create an Array.
    var selectedSupp = new Array();
    //Reference the CheckBoxes and insert the checked CheckBox value in Array.
    $("#tableBody input[type=checkbox]:checked").each(function () {
    	selectedSupp.push(this.value);
    });
    	$("#supplierInput").text(selectedSupp.length);
	
});

$('#select-all').change(function () {
	if($("#select-all").prop('checked') == true){
		$('#deleteSuppliers').removeClass("disabled");
	} else {
		$('#deleteSuppliers').addClass("disabled");
	}
});
 
$(document).delegate('.selectedSupplier','change',function(e) {
	var id = $(this).attr('id');
	if($("#select-all").prop('checked') == false){
		$('#deleteSuppliers').removeClass("disabled");
	}
	if($("#"+id).prop('checked') == false){
		$('#select-all').prop('checked', false);
		if(selectedSuppIds.length == 0){
			$('#deleteSuppliers').addClass("disabled");
		}
	} else if($("#"+id).prop('checked') == true){
		$('#deleteSuppliers').removeClass("disabled");
	} else{
		$('#deleteSuppliers').addClass("disabled");
	}
    $("#supplierDataTable  input[type=checkbox]:checked").each(function () {
   		$('#deleteSuppliers').removeClass("disabled");
   	});
    
    if($("#supplierDataTable .selectedSupplier:checked").length > 0 && $("#supplierDataTable .selectedSupplier:checked").length == $("#supplierDataTable .selectedSupplier").length) {
		$('#select-all').prop('checked', true);
		$('#deleteSuppliers').removeClass("disabled")
    }
    
});
//Create an Array.
var selectedSuppIds = new Array();

$(".cancelPopUpBtn").click(function(){
	selectedSuppIds = new Array();
	 $('#myModalDelConfirm').modal('toggle');
});

		$("#idConfirmDeleteSelectedSupplier").click(function(){
		
            //Reference the CheckBoxes and insert the checked CheckBox value in Array.
            $("#supplierDataTable  input[type=checkbox]:checked").each(function () {
            	if(this.value !== 'on'){
            		selectedSuppIds.push(this.value);
            	}
            });
            if (selectedSuppIds.length > 0) {
       			$('#loading').show();
            	$.ajax({
                       type : "POST",
                       "url" : getContextPath() + "/buyer/${eventType}/removeSupplier/${event.id}",
                       data : {
                       	selectedSuppIds: selectedSuppIds 
                       },
                       success : function(response) {
                    	   $('#deleteSuppliers').addClass("disabled");
                    	   var success='Event Supplier deleted successfully';
                    	   $("#idGlobalSuccessMessage").html(success);
   						$("#idGlobalSuccess").show();
   						$("#idGlobalError").hide();
                    },
        			error : function(request, textStatus, errorThrown) {
        				var error = request.getResponseHeader('error');
        				if (error != null) {
        					$("#idGlobalErrorMessage").html(error);
        					$("#idGlobalError").show();
        					$("#idGlobalSuccess").hide();
        				}
                    },
			        complete: function() {
			        	selectedSuppIds = new Array();
			        	$('#myModalDelConfirm').modal('toggle');
	                	$('#select-all').prop('checked', false);
						supplierDataTable.ajax.reload();
						reloadSupplierList();
			        	$('#loading').hide();
			        }
                   }); 
            }
         
		});
		
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script>
	$.validate({ lang : 'en',
	modules : 'date' });
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
