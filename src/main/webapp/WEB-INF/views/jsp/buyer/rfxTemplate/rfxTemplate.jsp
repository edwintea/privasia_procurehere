<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/forms.css"/>"> --%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxTemplateDesk" code="application.rfx.template" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxTemplateDesk}] });
});
</script>
<style>
ul.token-input-list {
	overflow-x: hidden !important;
}

.sup-category {
	width: 155px;
	float: left;
}

.setleft {
	float: left;
	display: flex;
}

.readOnlyClass {
	pointer-events: none;
	!
	important;
}

.check-wrapper {
	width: 115px;
}

.disp-flex {
	display: flex;
}

.chk-lbl label {
	padding-top: 20px !important;
}

.check-wrapper label {
	padding-top: 10px;
}

.ui-dialog-title {
	overflow: hidden;
	text-overflow: ellipsis;
	color: #fff;
	width: 240px;
}

.dialogBlockLoaded2 {
	border: 1px solid rgba(0, 0, 0, .2)!;
	-webkit-box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
	box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
}

.error {
	color: #ff5757 !important;
}

.help-block {
	color: #ff5757 !important;
}
</style>
<div id="page-content" view-name="rfxTemplate">
	<div id="page-content-wrapper">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
			</a></li>
			<c:url value="/buyer/rfxTemplate/rfxTemplateList" var="createUrl" />
			<li><a id="listLink" href="${createUrl} "> <spring:message code="rfxTemplate.list" />
			</a></li>
			<li class="active"><c:out value='${btnValue}' /> <spring:message code="rfxTemplate.title" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="rfxTemplate.administration" />
			</h2>
			<h2 class="trans-cap pull-right">
				<spring:message code="application.status" />
				: ${rfxTemplate.status}
			</h2>
		</div>
		<div class="clear"></div>
		<jsp:include page="rfxTemplateHeader.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="rfxTemplate.title" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="saveRfxTemplate" value="saveRfxTemplate" />
				<input type="hidden" id="buyerDecimal" value="${decimal}">
				<form:form id="frmRfxTemplate" cssClass="form-horizontal bordered-row" method="post" action="saveRfxTemplate" autocomplete="off" modelAttribute="rfxTemplate">
					<input type="hidden" id="templateId" value="${rfxTemplate.id}">
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="type" cssClass="marg-top-10">
								<spring:message code="application.eventtype" />
							</form:label>
						</div>
						<div class="col-md-5 ${assignedCount > 0 ? 'disabled':''}">
							<form:select path="type" cssClass="form-control chosen-select disablesearch" id="idStatus" data-validation="required">

								<c:forEach items="${rfxTypeList}" var="rfxType">
									<c:if test="${rfxType != 'PO'}">
										<form:option value="${rfxType}">${rfxType.value}</form:option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0 hideSelect">
						<div class="col-md-3">
							<form:label path="type" cssClass="marg-top-10">
								<spring:message code="rfx.auction.type.label" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="templateAuctionType" cssClass="form-control chosen-select disablesearch" id="idAuctionType" data-validation="required">
								<form:options items="${templateAuctionTypeList}" itemLabel="value" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="templateName" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateName" />
							</form:label>
							<form:hidden path="buyer.id" />
						</div>
						<form:hidden path="id" />
						<div class="col-md-5">
							<spring:message code="rfxTemplate.templateDescription.placeHolder" var="desc" />
							<spring:message code="rfxTemplate.templateName.placeHolder" var="name" />
							<form:input path="templateName" data-validation-length="1-128" data-validation="required length" cssClass="form-control" id="idRfxTemplateName" placeholder="${name}" />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="templateDescription" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateDescription" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:textarea path="templateDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" cssClass="form-control" id="idRfxTemplateDescription" placeholder="${desc}" />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="status" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="status" cssClass="form-control chosen-select disablesearch" id="idStatus1" data-validation="required">
								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>
					<div class="clear"></div>
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="prsummary.general.information" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdetails.event.name" /></label>
								</div>
								<div class="col-md-3">
									<spring:message code="eventdetails.event.place.eventname" var="eventname" />
									<form:input path="templateFieldBinding.eventName" placeholder="${eventname}" class="form-control" data-validation-length="0-250" data-validation="length" />
								</div>
								<div class="check-wrapper first" hidden>
									<spring:message code="prtemplate.label.visible" var="visible" />
									<form:checkbox path="templateFieldBinding.eventNameVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="prtemplate.label.read.only" var="read" />
									<form:checkbox path="templateFieldBinding.eventNameDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper" hidden>
									<spring:message code="prtemplate.label.optional" var="optional" />
									<form:checkbox path="templateFieldBinding.eventNameOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdetails.event.category" /></label>
								</div>
								<input type="hidden" id="industryCatArrVal" value='<c:out value="${indusCatList}" escapeXml="true" />'>
								<form:input type="hidden" path="templateFieldBinding.industryCatArr" id="industryCatArr" />
								<div class="col-md-3">
									<input type="text" id="demo-input-local" data-validation="required" name="blah" class="form-control" />
									<div class="col-md-12 selectListAjax"></div>
									<div id="catValErr"></div>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.industryCategoryVisible" class="custom-checkbox indusCatReadCheck" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.industryCategoryDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.industryCategoryOptional" class="custom-checkbox" title="Optional" label="Optional" />
								</div>
							</div>
							<div class="row marg-bottom-10 publishType-group required">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdetails.event.visibility" /></label>
								</div>
								<div class="col-md-2 marg-top-10">
									<span> <form:checkbox path="privateEvent" id="privateEvent" class="custom-checkbox eventPublishType" label="Private Event" title="Private" name="publishType" />
									</span>
								</div>
								<div class="col-md-2 marg-top-10">
									<span> <form:checkbox path="partialEvent" id="partialEvent" class="custom-checkbox eventPublishType" label="Partial Event" title="Partial" name="publishType" />
									</span>
								</div>
								<div class="col-md-2 marg-top-10">
									<span> <form:checkbox path="publicEvent" id="publicEvent" class="custom-checkbox eventPublishType" label="Public Event" title="Public" name="publishType" />
									</span>
								</div>
							</div>

							<div class="row marg-bottom-10 submissionVal">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdetails.event.validity.days" /></label>
								</div>
								<div class="col-md-3">
									<spring:message code="eventdetails.event.place.holder" var="validity" />
									<form:input path="templateFieldBinding.submissionValidityDays" id="idValidityDays" placeholder="${validity}" class="form-control" data-validation-length="1-3" data-validation="length" data-validation-optional="true" />
								</div>
								<div class="check-wrapper first" hidden>
									<form:checkbox path="templateFieldBinding.submissionValidityDaysVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.submissionValidityDaysDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper" hidden>
									<form:checkbox path="templateFieldBinding.submissionValidityDaysOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>

							<div class="row">
								<div class="col-md-3"></div>
								<div class="col-md-9">
									<div id="selectPublishTypeError"></div>
								</div>
							</div>


							<!-- PH-334 -->
							<div class="row marg-bottom-10 rfi_div">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdetails.expected.tender.time" /> </label>
								</div>
								<div class="col-md-3 ">
									<jsp:useBean id="now" class="java.util.Date" />
									<form:input autocomplete="off" name="expectedTenderDateTimeRange" value="${templateFieldBinding.expectedTenderDateTimeRange}" path="templateFieldBinding.expectedTenderDateTimeRange" data-date-start-date="0d" data-startdate="now" class="daterangepickerTime form-control for-clander-view for-clander-view" type="text"
										data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"></form:input>
								</div>

								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.expectedTenderVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.expectedTenderDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.expectedTenderOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>

							</div>

							<div class="row marg-bottom-10 rfi_div">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdetails.fee.startend.time" /> </label>
								</div>
								<div class="col-md-3 ">
									<form:input autocomplete="off" name="feeDateTimeRange" value="${templateFieldBinding.feeDateTimeRange}" path="templateFieldBinding.feeDateTimeRange" data-date-start-date="0d" data-startdate="now" class="daterangepickerTime form-control for-clander-view for-clander-view" type="text"
										data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"></form:input>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.feeVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.feeDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.feeOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>

							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="rfx.supplier.category" /></label>
								</div>
								<div class="col-md-2 marg-top-10 sup-category ">
									<spring:message code="rfxauction.restrict.placeholder" var="restrict" />
									<spring:message code="tooltip.restrict.suppliers" var="restricttip" />
									<span class="supplierBasedOnCategory"> <form:checkbox path="supplierBasedOnCategory" id="supplierBasedOnCategory" class="custom-checkbox" label="${restrict}" title="${restricttip}" />
									</span>
								</div>
								<div class="col-md-2 marg-top-10 sup-category">
									<spring:message code="rfx.templatelist.auto.populate" var="autoplace" />
									<spring:message code="tooltip.auto.populate.suppliers" var="autotool" />
									<span class="autoPopulateSupplier"> <form:checkbox path="autoPopulateSupplier" id="autoPopulateSupplier" class="custom-checkbox" label="${autoplace}" title="${autotool}" />
									</span>
								</div>
								<div class="col-md-2 marg-top-10 sup-category">
									<spring:message code="tooltip.read.only.suppliers" var="readtool" />
									<span class="readOnlySupplier"> <form:checkbox path="readOnlySupplier" id="readOnlySupplier" class="custom-checkbox" label="${read}" title="${readtool}" />
									</span>
								</div>
							</div>
							<div class="row marg-bottom-3">
								<div class="col-md-3">
									<label class="marg-top-10" style="font-size: 100%;"><spring:message code="rfxTemplate.supplier" /></label>
								</div>
								<div class="col-md-1">
									<label class="marg-top-10" style="font-size: 100%;"><spring:message code="rfxTemplate.supplier.ByState" /></label>
								</div>
								<div class="check-wrapper first">
									<span class="restrictSupplierByState"> <form:checkbox path="restrictSupplierByState" id="restrictSupplierByState" class="custom-checkbox" label="Visible" title="Visible supplier based on State" />
									</span>
								</div>
								<div class="col-md-1">
									<label class="marg-top-10" style="font-size: 100%;"><spring:message code="rfxTemplate.supplier.BySupplierTags" /></label>
								</div>
								<div class="check-wrapper">
									<span class="visibleSupplierTags"> <form:checkbox path="visibleSupplierTags" id="visibleSupplierTags" class="custom-checkbox" label="Visible" title="Visible supplier based on SupplierTags" />
									</span>
								</div>
								<div class="col-md-2">
									<label class="marg-top-10" style="font-size: 100%;"><spring:message code="rfxTemplate.supplier.ByGeographicalCoverage" /></label>
								</div>
								<div class="check-wrapper">
									<span class="visibleGeographicalCoverage"> <form:checkbox path="visibleGeographicalCoverage" id="visibleGeographicalCoverage" class="custom-checkbox" label="Visible" title="Visible supplier based on Geographical Coverage" />
									</span>
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-4"></div>
								<div class="check-wrapper first ">
									<span class="supplierBasedOnState"> <form:checkbox path="supplierBasedOnState" id="supplierBasedOnState" class="custom-checkbox" label="Optional" title="Optional supplier based on State" />
									</span>
								</div>
								<div class="col-md-1"></div>
								<div class="check-wrapper first ">
									<span class="optionalSupplierTags"> <form:checkbox path="optionalSupplierTags" id="optionalSupplierTags" class="custom-checkbox" label="Optional" title="Optional supplier based on SupplierTags" />
									</span>
								</div>
								<div class="col-md-2"></div>
								<div class="check-wrapper first ">
									<span class="optionalGeographicalCoverage"> <form:checkbox path="optionalGeographicalCoverage" id="optionalGeographicalCoverage" class="custom-checkbox" label="Optional" title="Optional supplier based on Geographical Coverage" />
									</span>
								</div>
							</div>
						</div>
					</div>
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="eventDetails.event.settings" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-3 marg-top-10 additional-check-wrapper first ">
									<div class="setleft">
										<spring:message code="tooltip.read.only.suppliers" var="readtool" />
										<spring:message code="eventdetails.enable.supplier" var="enablemask" />
										<form:checkbox path="viewSupplerName" id="enableMasking" class="custom-checkbox " title="${enablemask}" label="${enablemask}" />
									</div>
								</div>
								<div class="col-md-3 dd sky">
									<form:select class="chosen-select" id="unMaskedUser" cssClass="chosen-select user-list-normal" path="unMaskedUsers" multiple="multiple" data-placeholder="Select Unmask Owners">
										<c:forEach items="${revertBidUser}" var="revertBidUser">
											<c:if test="${usr.id == '-1' }">
												<form:option value="-1" label="${revertBidUser.name}" disabled="true" />
											</c:if>
											<c:if test="${usr.id != '-1' }">
												<form:option value="${revertBidUser.id}">${revertBidUser.name}</form:option>
											</c:if>
										</c:forEach>
									</form:select>
								</div>

								<div class="check-wrapper first">
									<form:checkbox path="visibleViewSupplierName" class="custom-checkbox " id="visibleViewSupplierName" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="tooltip.read.only.supplier" var="readonlysupp" />
									<form:checkbox path="readOnlyViewSupplierName" class="custom-checkbox" id="readOnlyViewSupplierName" title="${read}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-6 marg-top-10  ">
									<div class="setleft ">
										<spring:message code="rfi.createrfi.allow.close" var="allowclose" />
										<form:checkbox path="closeEnvelope" id="closeEnvelopeView" class="custom-checkbox " title="" label="${allowclose}" />
									</div>
								</div>
								<div class="check-wrapper ">
									<form:checkbox path="visibleCloseEnvelope" class="custom-checkbox" id="visibleCloseEnvelope" title="" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="readOnlyCloseEnvelope" class="custom-checkbox" id="readOnlyCloseEnvelope" title="" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10 hideSupplierSelect">
								<div class="col-md-3"></div>
								<div class="col-md-6 marg-top-10 ">
									<div class="setleft">
										<spring:message code="rfi.createrfi.allow.add" var="addallow" />
										<form:checkbox path="addSupplier" id="addSupplierView" class="custom-checkbox " title="" label="${addallow}" />
									</div>
								</div>
								<div class="check-wrapper ">
									<form:checkbox path="visibleAddSupplier" class="custom-checkbox" id="visibleAddSupplier" title="" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="readOnlyAddSupplier" class="custom-checkbox" id="readOnlyAddSupplier" title="" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-6 marg-top-10">
									<div class="setleft">
										<spring:message code="rfi.createrfi.allow.suspend" var="allowsuspend" />
										<form:checkbox path="allowToSuspendEvent" id="suspendEventView" class="custom-checkbox " title="" label="${allowsuspend}" />
									</div>
								</div>
								<div class="check-wrapper ">
									<form:checkbox path="visibleAllowToSuspendEvent" class="custom-checkbox" id="visibleAllowToSuspendEvent" title="" label="Visible" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="readOnlyAllowToSuspendEvent" class="custom-checkbox" id="readOnlyAllowToSuspendEvent" title="" label="Read Only" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-3 marg-top-10 additional-check-wrapper first">
									<div class="setleft">
										<spring:message code="eventdetails.enable.evaluation.declaration" var="enableEvlDeclare" />
										<form:checkbox path="enableEvaluationDeclaration" id="enableEvlDeclare" class="custom-checkbox " title="" label="${enableEvlDeclare}" />
									</div>
								</div>
								<div class="col-md-3 dd sky chooseEvalDeclaration">
									<form:select class="chosen-select" id="chosenEvaluationDeclaraton" path="templateFieldBinding.evaluationProcessDeclaration">
										<form:option value="">
											<spring:message code="rfxtemplate.select.declaration" />
										</form:option>
										<form:options items="${evaluationDeclaratonList}" itemValue="id" itemLabel="title" />
									</form:select>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.evaluationDeclarationVisible" class="custom-checkbox" id="visibleEvaluationDeclare" title="" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.evaluationDeclarationDisabled" class="custom-checkbox" id="readOnlyEvaluationDeclare" title="${read}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-3 marg-top-10 additional-check-wrapper first">
									<div class="setleft">
										<spring:message code="eventdetails.enable.supplier.declaration" var="enableSupplierDeclare" />
										<form:checkbox path="enableSupplierDeclaration" id="enableSupplierDeclare" class="custom-checkbox " title="" label="${enableSupplierDeclare}" />
									</div>
								</div>
								<div class="col-md-3 dd sky  chooseEvalDeclaration">
									<form:select class="chosen-select" id="choseSupplierDeclaration" path="templateFieldBinding.supplierAcceptanceDeclaration">
										<form:option value="">
											<spring:message code="rfxtemplate.select.declaration" />
										</form:option>
										<form:options items="${supplierDeclaratonList}" itemValue="id" itemLabel="title" />
									</form:select>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.supplierDeclarationVisible" class="custom-checkbox" id="visibleSupplierDeclare" title="" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.supplierDeclarationDisabled" class="custom-checkbox" id="readOnlySupplierDeclare" title="${read}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10 hideSelect">
								<div class="col-md-3"></div>
								<div class="col-md-6 marg-top-10  ">
									<div class="setleft">
										<form:checkbox path="viewAuctionHall" id="auctionHallView" class="custom-checkbox " title="" label="Allow viewing of Auction Hall after event ends" />
									</div>
								</div>
								<div class="check-wrapper ">
									<form:checkbox path="visibleViewAuctionHall" class="custom-checkbox" id="visibleViewAuctionHall" title="" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="readOnlyViewAuctionHall" class="custom-checkbox" id="readOnlyViewAuctionHall" title="" label="${read}" />
								</div>
							</div>

							<div class="row marg-bottom-10 revertBidHide">
								<div class="col-md-3"></div>
								<div class="col-md-3 marg-top-10 additional-check-wrapper first ">
									<div class="setleft">
										<spring:message code="rfa.createrfa.allow.revert.last.bid" var="revertBid" />
										<form:checkbox path="revertLastBid" id="revertLastBid" class="custom-checkbox " title="" label="${revertBid}" />
									</div>
								</div>
								<div class="col-md-3 dd sky revertBidUser">
									<form:select class="chosen-select" id="revertBidUser" path="revertBidUser">
										<form:option value="">
											<spring:message code="rfa.select.allow.revert.bid.owner" />
										</form:option>
										<c:forEach items="${revertBidUser}" var="revertbidUser">
											<form:option value="${revertbidUser}">${revertbidUser.name}</form:option>
										</c:forEach>
									</form:select>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="visibleRevertLastBid" class="custom-checkbox" id="visibleRevertLastBid" title="" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="readOnlyRevertLastBid" class="custom-checkbox" id="readOnlyRevertLastBid" title="${read}" label="${read}" />
								</div>
							</div>

							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-3 marg-top-10 additional-check-wrapper first ">
									<div class="setleft">
										<spring:message code="rfx.template.eval.conclusion.prem.enable" var="enableEvalCon" />
										<form:checkbox path="enableEvaluationConclusionUsers" id="enableEvalCon" class="custom-checkbox " title="" label="${enableEvalCon}" />
									</div>
								</div>
								<div class="col-md-3 dd sky evalConUser">
									<spring:message code="rfx.template.eval.conclusion.prem.user" var="evalConUserPlaceholder" />
									<form:select class="chosen-select" id="evalConUser" path="evaluationConclusionUsers" cssClass="chosen-select user-list-normal" multiple="multiple" data-placeholder="${evalConUserPlaceholder}">
										<c:forEach items="${evaluationConclusionUsers}" var="user">
											<c:if test="${user.id == '-1' }">
												<form:option value="-1" label="${user.name}" disabled="true" />
											</c:if>
											<c:if test="${user.id != '-1' }">
												<form:option value="${user}">${user.name}</form:option>
											</c:if>
										</c:forEach>
									</form:select>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="visibleEvaluationConclusionUsers" class="custom-checkbox" id="visibleEvalCon" title="" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="readOnlyEvaluationConclusionUsers" class="custom-checkbox" id="readOnlyEvalCon" title="${read}" label="${read}" />
								</div>
							</div>


						</div>
					</div>

					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="eventdescription.procuement.label" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">

							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="procurement.info.method" /></label>
								</div>
								<div class="col-md-3 dd sky">
									<form:select class="chosen-select" id="chosenProcurementMethod" path="templateFieldBinding.procurementMethod">
										<form:option value="">
											<spring:message code="procurement.info.method.select" />
										</form:option>
										<c:forEach items="${procurementMethodList}" var="procurementMethod">
											<form:option value="${procurementMethod.id}">${procurementMethod.procurementMethod}</form:option>
										</c:forEach>
									</form:select>
								</div>
								<div class="check-wrapper first">
									<spring:message code="prtemplate.label.visible" var="visible" />
									<form:checkbox id="procurementMethodVisibleId" path="templateFieldBinding.procurementMethodVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="prtemplate.label.read.only" var="read" />
									<form:checkbox id="procurementMethodReadOnlyId" path="templateFieldBinding.procurementMethodDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="prtemplate.label.optional" var="optional" />
									<form:checkbox id="procurementMethodOptional" path="templateFieldBinding.procurementMethodOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="procurement.info.category" /></label>
								</div>
								<div class="col-md-3 dd sky">
									<form:select class="chosen-select" id="chosenProcurementCategory" path="templateFieldBinding.procurementCategory">
										<form:option value="">
											<spring:message code="procurement.info.category.select" />
										</form:option>
										<c:forEach items="${procurementCategoryList}" var="procurementCategory">
											<form:option value="${procurementCategory.id}">${procurementCategory.procurementCategories}</form:option>
										</c:forEach>
									</form:select>
								</div>
								<div class="check-wrapper first">
									<spring:message code="prtemplate.label.visible" var="visible" />
									<form:checkbox id="procurementCategoryVisibleId" path="templateFieldBinding.procurementCategoryVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="prtemplate.label.read.only" var="read" />
									<form:checkbox id="procurementCategoryReadOnlyId" path="templateFieldBinding.procurementCategoryDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="prtemplate.label.optional" var="optional" />
									<form:checkbox id="procurementCategoryOptionalId" path="templateFieldBinding.procurementCategoryOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
						</div>
					</div>

					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="eventdescription.finance.label" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="pr.base.currency" /></label>
								</div>
								<div class="col-md-3 dd sky  ">
									<form:select class="chosen-select" id="chosenCurrency" path="templateFieldBinding.baseCurrency">
										<form:option value="">
											<spring:message code="prtemplate.default.currency" />
										</form:option>
										<form:options items="${currencyList}" itemValue="id" itemLabel="currencyCode" />
									</form:select>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.baseCurrencyVisible" class="custom-checkbox" id="visibleCurrency" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.baseCurrencyDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper" hidden>
									<form:checkbox path="templateFieldBinding.baseCurrencyOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.decimal.label" /></label>
								</div>
								<div class="col-md-3 dd sky  ">
									<form:select path="templateFieldBinding.decimal" id="decimal" cssClass="form-control chosen-select decimalChange">
										<form:option value="">
											<spring:message code="rfx.select.default.decimal" />
										</form:option>
										<form:option value="1"></form:option>
										<form:option value="2"></form:option>
										<form:option value="3"></form:option>
										<form:option value="4"></form:option>
										<form:option value="5"></form:option>
										<form:option value="6"></form:option>
									</form:select>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.decimalVisible" class="custom-checkbox" id="visibleDecimal" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.decimalDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper" hidden>
									<form:checkbox path="templateFieldBinding.decimalOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="label.costcenter" /></label>
								</div>
								<div class="col-md-3 dd sky  ">
									<form:select class="chosen-select" id="chosenCostCenter" path="templateFieldBinding.costCenter">
										<form:option value="">
											<spring:message code="rfx.default.cost.center" />
										</form:option>
										<c:forEach items="${costCenterList}" var="costCenter">
											<form:option value="${costCenter.id}">${costCenter.costCenter} - ${costCenter.description}</form:option>
										</c:forEach>
									</form:select>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.costCenterVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.costCenterDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.costCenterOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="label.businessUnit" /></label>
								</div>
								<div class="col-md-3 dd sky">
									<form:select class="chosen-select" id="chosenBusinessUnit" path="templateFieldBinding.businessUnit">
										<form:option value="">
											<spring:message code="pr.select.business.unit" />
										</form:option>
										<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
									</form:select>
									<div id="businessUnitDisabledError"></div>
								</div>
								<%-- <div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.businessUnitDisabled" data-validation-error-msg-container="#businessUnitDisabledError" data-validation="readonly_data" class="custom-checkbox" title="Read Only" label="Read Only" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="" class="custom-checkbox" title="Optional" label="Optional" value="true" />
								</div>
 --%>
								<div class="check-wrapper first hide">
									<form:checkbox path="" class="custom-checkbox" title="${visible}" label="${visible}" value="true" />
								</div>
								<div class="check-wrapper">
									<form:checkbox id="chosenBusinessUnitReadOnlyCheckbox" path="templateFieldBinding.businessUnitDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper hide">
									<form:checkbox path="" class="custom-checkbox" title="${optional}" label="${optional}" value="true" />
								</div>
							</div>
							
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="label.groupCode" /></label>
								</div>
								<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''} ">
									<form:select class="chosen-select" id="chosenGroupCode" path="templateFieldBinding.groupCode">
										<form:option value=""> <spring:message code="default.select.group.code" /> </form:option>
										<c:forEach items="${groupCodeList}" var="grC">
											<form:option value="${grC.id}">${grC.groupCode} - ${grC.description}</form:option>
										</c:forEach>
									</form:select>
								</div>
								<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
									<spring:message code="prtemplate.label.visible" var="visible" />
									<form:checkbox path="templateFieldBinding.groupCodeVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
									<spring:message code="prtemplate.label.read.only" var="read" />
									<form:checkbox path="templateFieldBinding.groupCodeDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
									<spring:message code="prtemplate.label.optional" var="optional" />
									<form:checkbox path="templateFieldBinding.groupCodeOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.budgetamount.label" /></label>
								</div>
								<div class="col-md-3">
									<c:if test="${not empty rfxTemplate.templateFieldBinding.decimal}">
										<fmt:formatNumber var="budgetAmount" type="number" minFractionDigits="${rfxTemplate.templateFieldBinding.decimal}" maxFractionDigits="${rfxTemplate.templateFieldBinding.decimal}" value="${rfxTemplate.templateFieldBinding.budgetAmount}" />
									</c:if>
									<c:if test="${not empty rfxTemplate.templateFieldBinding &&  empty rfxTemplate.templateFieldBinding.decimal}">
										<fmt:formatNumber var="budgetAmount" type="number" minFractionDigits="${decimal}" maxFractionDigits="${decimal}" value="${rfxTemplate.templateFieldBinding.budgetAmount}" />
									</c:if>
									<spring:message code="rfx.templatelist.budget.amount" var="bugetplace" />
									<form:input path="templateFieldBinding.budgetAmount" value="${budgetAmount}" id="budgetAmount" name="budgetAmount" data-validation="validate_max_13 positive" placeholder="${bugetplace}" class="form-control" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
									<span class="customError"></span>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.budgetAmountVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.budgetAmountDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.budgetAmountOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.estimatedBudget.label" /></label>
								</div>
								<div class="col-md-3">
									<c:if test="${not empty rfxTemplate.templateFieldBinding.decimal}">
										<fmt:formatNumber var="estimatedBudget" type="number" minFractionDigits="${rfxTemplate.templateFieldBinding.decimal}" maxFractionDigits="${rfxTemplate.templateFieldBinding.decimal}" value="${rfxTemplate.templateFieldBinding.estimatedBudget}" />
									</c:if>
									<c:if test="${empty rfxTemplate.templateFieldBinding.decimal}">
										<fmt:formatNumber var="estimatedBudget" type="number" minFractionDigits="${decimal}" maxFractionDigits="${decimal}" value="${rfxTemplate.templateFieldBinding.estimatedBudget}" />
									</c:if>
									<spring:message code="rfx.templatelist.estimated.budget" var="estimatePlace" />
									<form:input path="templateFieldBinding.estimatedBudget" value="${estimatedBudget}" id="estimatedBudget" name="estimatedBudget" data-validation="validate_max_13 positive" placeholder="${estimatePlace}" class="form-control" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
									<span class="customError"></span>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.estimatedBudgetVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.estimatedBudgetDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.estimatedBudgetOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.historicamount.label" /></label>
								</div>
								<div class="col-md-3">
									<c:if test="${not empty rfxTemplate.templateFieldBinding.decimal}">
										<fmt:formatNumber var="historicaAmount" type="number" minFractionDigits="${rfxTemplate.templateFieldBinding.decimal}" maxFractionDigits="${rfxTemplate.templateFieldBinding.decimal}" value="${rfxTemplate.templateFieldBinding.historicAmount}" />
									</c:if>
									<c:if test="${empty rfxTemplate.templateFieldBinding.decimal}">
										<fmt:formatNumber var="historicaAmount" type="number" minFractionDigits="${decimal}" maxFractionDigits="${decimal}" value="${rfxTemplate.templateFieldBinding.historicAmount}" />
									</c:if>
									<spring:message code="rfx.templatelist.historic.amount" var="historicplace" />
									<form:input path="templateFieldBinding.historicAmount" value="${historicaAmount}" id="historicaAmount" name="historicaAmount" data-validation="validate_max_13 positive" placeholder="${historicplace}" class="form-control" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
									<span class="customError"></span>
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.historicAmountVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.historicAmountDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.historicAmountOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.paymentterm.label" /></label>
								</div>
								<div class="col-md-3">
									<spring:message code="pr.place.paymentTerm" var="paymntterm" />
									<form:textarea path="templateFieldBinding.paymentTerms" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" cssClass="form-control" id="idRfxTemplatePaymentTerms" placeholder="${paymntterm}" />
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.paymentTermsVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.paymentTermsDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.paymentTermsOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>

							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="rfxtemplate.participation.fee" /></label>
								</div>
								<div class="col-md-2 dd sky  ">
									<form:select class="chosen-select" id="participationFeeCurrency" path="templateFieldBinding.participationFeeCurrency">
										<form:option value="">
											<spring:message code="currency.select" />
										</form:option>
										<form:options items="${currencyList}" itemValue="id" itemLabel="currencyCode" />
									</form:select>
								</div>
								<div class="col-md-2 partiFees">
									<spring:message code="enter.participation.fees.placeholder" var="participateionfee" />
									<fmt:formatNumber var="participationFees" type="number" minFractionDigits="2" maxFractionDigits="2" value="${rfxTemplate.templateFieldBinding.participationFees}" />
									<form:input path="templateFieldBinding.participationFees" data-validation="validate_custom_length positive" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" name="participationFees" data-validation-error-msg="length should be less then 10 and after decimal 2 digits allowed" cssClass="form-control" id="participationFees"
										placeholder="${participateionfee}" data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" value="${participationFees}" />
									<span class="customError"></span>
								</div>
								<div class="check-wrapper first second ">
									<form:checkbox path="templateFieldBinding.participationFeesVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.participationFeesDisabled" class="custom-checkbox" id="feesDisabled" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.participationFeesOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="rfxTemplate.deposit" /></label>
								</div>
								<div class="col-md-2 dd sky  ">
									<form:select class="chosen-select" id="depositCurrency" path="templateFieldBinding.depositCurrency">
										<form:option value="">
											<spring:message code="currency.select" />
										</form:option>
										<form:options items="${currencyList}" itemValue="id" itemLabel="currencyCode" />
									</form:select>
								</div>
								<div class="col-md-2 deposit">
									<spring:message code="enter.deposite.placeholder" var="Deposite" />
									<fmt:formatNumber var="deposit" type="number" minFractionDigits="2" maxFractionDigits="2" value="${rfxTemplate.templateFieldBinding.deposit}" />
									<form:input path="templateFieldBinding.Deposit" data-validation="validate_custom_length positive" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" name="Deposit" data-validation-error-msg="length should be less then 10 and after decimal 2 digits allowed" cssClass="form-control" id="deposit" placeholder="${Deposite}"
										data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" value="${Deposit}" />
									<span class="customError"></span>
								</div>
								<div class="check-wrapper first second">
									<form:checkbox path="templateFieldBinding.addDepositVisible" class="custom-checkbox" title="Visible" label="Visible" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.addDepositDisabled" class="custom-checkbox" title="Read Only" id="depositDisabled" label="Read Only" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.addDepositOptional" class="custom-checkbox" title="Optional" label="Optional" />
								</div>
							</div>
							<!-- PH-340 -->
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="sourcing.minimumSupplierRating" /></label>
								</div>
								<div class="col-md-3">
									<form:input path="templateFieldBinding.minimumSupplierRating" value="${templateFieldBinding.minimumSupplierRating}" id="minimumSupplierRating" name="minimumSupplierRating" placeholder="Enter Minimum Supplier Rating" class="form-control" data-validation-optional="true" data-validation="number custom validateMin"
										data-validation-allowing="range[0.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$" data-validation-error-msg-number="Input value must be numeric within range from 0 to 9999.99" data-validation-error-msg-custom="Input value must be numeric within range from 0 to 9999.99" />
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.minimumSupplierRatingVisible" class="custom-checkbox" title="Visible" label="Visible" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.minimumSupplierRatingDisabled" class="custom-checkbox" title="Read Only" label="Read Only" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.minimumSupplierRatingOptional" class="custom-checkbox" title="Optional" label="Optional" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="sourcing.maximumSupplierRating" /></label>
								</div>
								<div class="col-md-3">
									<form:input path="templateFieldBinding.maximumSupplierRating" value="${templateFieldBinding.maximumSupplierRating}" id="maximumSupplierRating" name="maximumSupplierRating" placeholder="Enter Maximum Supplier Rating" class="form-control" data-validation-optional="true" data-validation="number custom validateMax"
										data-validation-allowing="range[1.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$" data-validation-error-msg-number="Input value must be numeric within range from 1 to 9999.99" data-validation-error-msg-custom="Input value must be numeric within range from 1 to 9999.99" />
								</div>
								<div class="check-wrapper first">
									<form:checkbox path="templateFieldBinding.maximumSupplierRatingVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.maximumSupplierRatingDisabled" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.maximumSupplierRatingOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
						</div>
					</div>

					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20 notrfi_div">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="bill.of.quantity.control" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="allow.bill.of.quantity.control" /></label>
								</div>
								<div class="check-wrapper">
									<spring:message code="bill.of.quantity.control.editable" var="editable" />
									<form:checkbox path="addBillOfQuantity" class="custom-checkbox" title="" label="${editable}" />
								</div>
							</div>
						</div>

						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="bq.control.disabled.total.amount" /></label>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.disableTotalAmount" class="custom-checkbox" title="" />
								</div>
							</div>
						</div>
					</div>
					<!--NOTE: Please do not delete the code lil bit incmplete  -->
					<!-- Auction Rules -->
					
					<jsp:include page="rfxAuctionRules.jsp" />						
					<!-- Incomplete -->
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
							<jsp:include page="rfxApprovals.jsp" />
						</div>
					</div>

					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info suspendApproval-tab">
							<jsp:include page="rfxSuspendApproval.jsp" />
						</div>
					</div>
					
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info awardApproval-tab">
							<jsp:include page="templateAwardApproval.jsp" />
						</div>
					</div>
					
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
							<jsp:include page="rfxEnvelope.jsp" />
						</div>
					</div>

					<!-- <div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20"> -->
					<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
						<jsp:include page="rfxTemplateTeamMembers.jsp"></jsp:include>
					</div>
					<!-- </div> -->

					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="template.user" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row">
								<div class="col-sm-4 col-md-3">
									<label class="marg-top-10"><spring:message code="assign.template.user" /></label>
								</div>
								<div class="col-md-4 col-sm-6">
									<div class="input-group search_box_gray disp-flex">
										<select class="chosen-select user-list-normal-rfx-template" id="selectedUserList" selected-id="data-value">
											<option value=""><spring:message code="placeholder.search.user.templates" /></option>
											<c:forEach items="${userlistForAssigned}" var="userList">
												<c:if test="${userList.id == '-1'}">
													<option value="-1" disabled>${userList.name}</option>
												</c:if>
												<c:if test="${userList.id != '-1' }">
													<option value="${userList.id}">${userList.name}</option>
												</c:if>
											</c:forEach>
										</select> <span class="col-md-2 col-sm-2">
											<button class="btn btn-info hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" id="addMoreUsers">
												<i class="fa fa-plus" aria-hidden="true"></i>
											</button>
										</span>
									</div>
									<div class="error templateUserError" hidden>
										<spring:message code="please.select.user" />
									</div>
								</div>
							</div>

							<div class="clear"></div>
							<div class="container-fluid">
								<div class="row">
									<div class="col-xs-12">
										<section class="index_table_block marg-top-20">
											<div class="ph_tabel_wrapper scrolableTable_UserList">
												<table id="tableList1" class="ph_table display table table-bordered noarrow" cellspacing="0">
													<thead>
														<tr>
															<th class=""><spring:message code="application.action1" /></th>
															<th class=""><spring:message code="application.username" /></th>
														</tr>
													</thead>
												</table>
											</div>
										</section>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="clear"></div>
					<div class="row marg-bottom-202">
						<!-- <div class="col-md-3">
							<label class="marg-top-10"></label>
						</div> -->
						<div class="col-md-9 btns-lower">
							<sec:authorize access="hasRole('EVENT_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEdit" />
							<form:button type="submit" id="saveRfxTemplate" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10 ${canEdit ? '':'disabled'}"><spring:message code="save.next.button" /></form:button>
							<c:url value="/buyer/rfxTemplate/rfxTemplateList" var="createUrl" />
							<a href="${createUrl}" id="cancelButton" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
							<c:if test="${rfxTemplate.id ne null}">
								<form:button type="button" id="saveAsRfxTemplate" class="btn btn-alt btn-hover ph_btn_midium btn-warning hvr-pop pull-right ${canEdit ? '':'disabled'}">
									<spring:message code="application.saveas" />
								</form:button>
							</c:if>
						</div>
						<input type="hidden" name="userId" id="userId" />
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<!-- TEMPLATE SAVE AS POPUP -->
<div class="flagvisibility dialogBox" id="templateSaveAsPopup" title="Template Save As">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="tempId" name="tempId" />
		<form>
			<div class="marg-top-20 tempaleData">
				<div class="row marg-bottom-10">
					<div class=" col-md-4">
						<label> <spring:message code="rfxTemplate.templateName" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="rfxTemplate.templateDescription.placeHolder" var="desc" />
						<spring:message code="rfxTemplate.templateName.placeHolder" var="name" />
						<input data-validation-length="1-128" data-validation="required length" class="form-control marg-bottom-10" name="tempName" id="tempName" placeholder="${name}" /> <span class="customError"></span>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-md-4">
						<label> <spring:message code="rfxTemplate.templateDescription" />
						</label>
					</div>
					<div class="col-md-8">
						<textarea name="tempDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" id="tempDescription" placeholder="${desc}"></textarea>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_midium btn-tooltip hvr-pop hvr-rectangle-out" id="saveAsTemp" data-original-title="Delete"><spring:message code="application.create" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>

<!--Template user Delete  popup-->
<div class="flagvisibility dialogBox" id="removeTemplateUserListPopup" title='<spring:message code="tooltip.remove.template.user" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<!-- <input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListName" name="approverListName" value=""> -->
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock2">
				<spring:message code="template.confirm.to.remove" />
				<span></span>
				<spring:message code="template.confirm.to.remove.userlist" />
				<span></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" id="deleteUser" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out">
						<spring:message code="application.remove" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script>

<c:if test="${assignedCount > 0}">
// Remove click event handler on checkbox labels. It affects disabled checkboxes
$('.check-wrapper').on('click', function(e){
	e.preventDefault();
	return false; 
});

$(window).bind('load', function() {
	var allowedFields = '#idRfxTemplateName, #idRfxTemplateDescription, #idStatus1, #saveAsRfxTemplate, #saveRfxTemplate, #cancelButton, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	// remove click handlers from the Event Category selected items.
	$(".token-input-delete-token").html('');
	$(".token-input-delete-token").off("click");
});
</c:if>


<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '#cancelButton, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>


	$.validate({
		lang : 'en',
		validateOnBlur : true,
	});
 
$.formUtils.addValidator({
	name : 'validate_custom_length',
	validatorFunction : function(value, $el, config, language, $form) {
		var val = value.split(".");
		if (val[0].replace(/,/g, '').length > 10) {
			return false;
		} else {
			return true;
		}
	},
	errorMessage : 'The input value is longer than 10 characters',
	errorMessageKey : 'validateLengthCustom'
});

$.formUtils.addValidator({
	name : 'validate_max_13',
	validatorFunction : function(value, $el, config, language, $form) {
		var val = value.split(".");
		if (val[0].replace(/,/g, '').length > 13) {
			return false;
		} else {
			return true;
		}
	},
	errorMessage : 'The input value is longer than 13 characters',
	errorMessageKey : 'validateMaxLength'
});

$('#idTypeExtension').on('change', function() {
	if ($('#idTypeExtension').val() == 'AUTOMATIC') {
		$(".hideDiv").show();
	} else {
		$(".hideDiv").hide();
	}
});


	$(document).delegate('#deleteUser', 'click', function(e){
		e.preventDefault();
	    var id=id2;
	    var index = userId2.indexOf(id);
	    if (index !== -1) userId2.splice(index, 1);
	    
	    $('#userId').val(userId2);
	    var text = text2;
	    var index2 = userList2.indexOf(text);
	    if (index2 !== -1) userList2.splice(index2, 1);
	    
	    $('#tableList1 tbody').empty();
	    var i;
	    var html="";
	    for (i = 0; i < userList2.length; i++) {
	    	var userNameFromArray=userList2[i];
	    	var userIdFromArray=userId2[i];
	      html = "<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' id="+userIdFromArray.toString()+" data-value="+userIdFromArray.toString()+" title=<spring:message code='application.delete'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+userNameFromArray.toString()+"</td></tr>";
	      $('#tableList1').append(html);
	    }
	    $("#removeTemplateUserListPopup").dialog('close');
		var newRow='<option value="' + id + '" data-name="' + text + '">' + text + '</option>';
		$('#selectedUserList').append(newRow);
		updateUserList('', $("#selectedUserList"), 'NORMAL_USER');
	    $('#selectedUserList').trigger("chosen:updated");
	});
	var id2;
	var text2;
	//remove approvers list
	$(document).delegate('.deleteUserForTemplate', 'click', function(e) {
		e.preventDefault();
		text2= $(this).parent().next('td').text();
		id2=$(this).data("value");

		$("#removeTemplateUserListPopup").dialog({
			modal : true,
			maxWidth : 400,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded2"
		});
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		$('.ui-dialog-title').addClass('title-ellipsis');
		$("#removeTemplateUserListPopup").find('.approverInfoBlock2').find('span:first-child').text("\""+text2+"\"");
		$('.title-ellipsis').text('Remove Template User');
	});
	
	var userList2=[];
	var userId2=[];
$(document).ready(function() {
	
	if($('#idEnableSuspendApproval').is(':checked')){
		$('#suspendAddSelect').removeClass('disabled');
	}else{
		$('#suspendAddSelect').addClass('disabled');
	}
	
	if($('#idEnableAwardApproval').is(':checked')){
		$('#awardAddSelect').removeClass('disabled');
	}else{
		$('#awardAddSelect').addClass('disabled');
	}
	
	$('#idEnableAwardApproval').change(function() {
	     if(this.checked) {
	    	 $('#awardAddSelect').removeClass('disabled');
	    	 $('#idVisibleAwardApproval').prop("checked", true );
	    	 $('.visibleAwardCheckB').find('span').addClass('checked');
	     }else {
	    	 $('#awardAddSelect').addClass('disabled');
	    	 $('#idVisibleAwardApproval').prop( "checked", false);
	    	 $('.visibleAwardCheckB').find('span').removeClass('checked');
	    	 $('#idOptionalAwardApproval').prop( "checked", false);
	    	 $('.optionalOnlyAwardCheckB').find('span').removeClass('checked');
	    	 $('#idReadOnlyAwardApproval').prop( "checked", false);
	    	 $('.readOnlyAwardCheckB').find('span').removeClass('checked');
			 $('#awardAddSelect').parent().removeClass('has-error').find('.form-error').remove();
	    	 
	    	 console.log("acnt be4 removal "+acnt);
			 $('.removeAwardApproval').closest(".newAward-approval").remove();

			 $(".newAward-approval").each(function(i, v) {
				i++;
				$('.removeAwardApproval').attr("id", "newAward-approval-" + i);
				$('.removeAwardpApproval').find(".level").text('Level ' + i);
					
				$('.removeAwardApproval').find(".approval_condition").each(function(){
					$('.removeAwardApproval').attr("name",'awardApprovals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
					
				$('.removeAwardApproval').find("input[name='awardApprovals[" + i + "].approvalType']").each(function(){
					$('.removeAwardApproval').attr('name','awardApprovals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
					
					
				$('.removeAwardApproval').find(".tagTypeMultiSelect").each(function(){
					$('.removeAwardApproval').attr("name",'awardApprovals[' +(i-1) + '].approvalUsers');
					$('.removeAwardApproval').attr("id", "multipleSelectExample-" + ((i-1)) + "");	
				}) //select name reindex
					
				$('.removeAwardApproval').find("input[name='awardApprovals[" + i + "].approvalUsers']").each(function(){
					$('.removeAwardApproval').attr("name",'awardApprovals[' +(i-1) + '].approvalUsers');
				}) //select name reindex hidden
			});
//			cnt--;
			acnt = 1;
			console.log("cnt after removal "+acnt);
	     }
	 });
	
	$('#idVisibleAwardApproval').change(function() {
	     if(this.checked) {
	    	 $('#awardAddSelect').removeClass('disabled');
	    	 $('#idEnableAwardApproval').prop("checked", true );
	    	 $('.awardApprChB').find('span').addClass('checked');
	     }else {
	    	 $('#awardAddSelect').addClass('disabled');
	    	 $('#idEnableAwardApproval').prop( "checked", false);
	    	 $('.awardApprChB').find('span').removeClass('checked');
	    	
	    	 console.log("cnt be4 removal "+cnt);
			 $('.removeAwardApproval').closest(".newAward-approval").remove();

			 $(".newAward-approval").each(function(i, v) {
				i++;
				$('.removeAwardApproval').attr("id", "newAward-approval-" + i);
				$('.removeAwardApproval').find(".level").text('Level ' + i);
					
				$('.removeAwardApproval').find(".approval_condition").each(function(){
					$('.removeAwardApproval').attr("name",'awardApprovals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
					
				$('.removeAwardApproval').find("input[name='_awardApprovals[" + i + "].approvalType']").each(function(){
					$('.removeAwardApproval').attr('name','_awardApprovals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
					
					
				$('.removeAwardApproval').find(".tagTypeMultiSelect").each(function(){
					$('.removeAwardApproval').attr("name",'awardApprovals[' +(i-1) + '].approvalUsers');
					$('.removeAwardApproval').attr("id", "multipleSelectExample-" + ((i-1)) + "");	
				}) //select name reindex
					
				$('.removeAwardApproval').find("input[name='_awardApprovals[" + i + "].approvalUsers']").each(function(){
					$('.removeAwardApproval').attr("name",'_awardApprovals[' +(i-1) + '].approvalUsers');
				}) //select name reindex hidden
			});
//			cnt--;
			cnt = 1;
			console.log("cnt after removal "+cnt);
	     }
	 });
	
	
	 $('#idEnableSuspendApproval').change(function() {
	     
		 if(this.checked) {
	    	 $('#suspendAddSelect').removeClass('disabled');
	    	 $('#idVisibleSuspendApproval').prop("checked", true );
	    	 $('.visibleCheckB').find('span').addClass('checked');
	     }else {
	    	 $('#suspendAddSelect').addClass('disabled');
	    	 $('#idVisibleSuspendApproval').prop( "checked", false);
	    	 $('.visibleCheckB').find('span').removeClass('checked');
	    	 $('#idReadOnlySuspendApproval').prop( "checked", false);
	    	 $('.readOnlyCheckB').find('span').removeClass('checked');
	    	 
	    	 console.log("cnt be4 removal "+cnt);
			 $('.removeSuspApproval').closest(".newSusp-approval").remove();

			 $(".newSusp-approval").each(function(i, v) {
				i++;
				$('.removeSuspApproval').attr("id", "newSusp-approval-" + i);
				$('.removeSuspApproval').find(".level").text('Level ' + i);
					
				$('.removeSuspApproval').find(".approval_condition").each(function(){
					$('.removeSuspApproval').attr("name",'suspensionApprovals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
					
				$('.removeSuspApproval').find("input[name='_suspensionApprovals[" + i + "].approvalType']").each(function(){
					$('.removeSuspApproval').attr('name','_suspensionApprovals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
					
					
				$('.removeSuspApproval').find(".tagTypeMultiSelect").each(function(){
					$('.removeSuspApproval').attr("name",'suspensionApprovals[' +(i-1) + '].approvalUsers');
					$('.removeSuspApproval').attr("id", "multipleSelectExample-" + ((i-1)) + "");	
				}) //select name reindex
					
				$('.removeSuspApproval').find("input[name='_suspensionApprovals[" + i + "].approvalUsers']").each(function(){
					$('.removeSuspApproval').attr("name",'_suspensionApprovals[' +(i-1) + '].approvalUsers');
				}) //select name reindex hidden
			});
// 			cnt--;
			cnt = 1;
			console.log("cnt after removal "+cnt);
	     }
	 });
	 
	 $('#idVisibleSuspendApproval').change(function() {
	     if(this.checked) {
	    	 $('#suspendAddSelect').removeClass('disabled');
	    	 $('#idEnableSuspendApproval').prop("checked", true );
	    	 $('.suspApprChB').find('span').addClass('checked');
	     }else {
	    	 $('#suspendAddSelect').addClass('disabled');
	    	 $('#idEnableSuspendApproval').prop( "checked", false);
	    	 $('.suspApprChB').find('span').removeClass('checked');
	    	
	    	 console.log("cnt be4 removal "+cnt);
			 $('.removeSuspApproval').closest(".newSusp-approval").remove();

			 $(".newSusp-approval").each(function(i, v) {
				i++;
				$('.removeSuspApproval').attr("id", "newSusp-approval-" + i);
				$('.removeSuspApproval').find(".level").text('Level ' + i);
					
				$('.removeSuspApproval').find(".approval_condition").each(function(){
					$('.removeSuspApproval').attr("name",'suspensionApprovals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
					
				$('.removeSuspApproval').find("input[name='_suspensionApprovals[" + i + "].approvalType']").each(function(){
					$('.removeSuspApproval').attr('name','_suspensionApprovals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
					
					
				$('.removeSuspApproval').find(".tagTypeMultiSelect").each(function(){
					$('.removeSuspApproval').attr("name",'suspensionApprovals[' +(i-1) + '].approvalUsers');
					$('.removeSuspApproval').attr("id", "multipleSelectExample-" + ((i-1)) + "");	
				}) //select name reindex
					
				$('.removeSuspApproval').find("input[name='_suspensionApprovals[" + i + "].approvalUsers']").each(function(){
					$('.removeSuspApproval').attr("name",'_suspensionApprovals[' +(i-1) + '].approvalUsers');
				}) //select name reindex hidden
			});
// 			cnt--;
			cnt = 1;
			console.log("cnt after removal "+cnt);
	     }
	 });
	
	$('#idValidityDays').mask('999');
	
	$("#addMoreUsers").on('click', function(){
		var selectedName=$("#selectedUserList option:selected").text();
		var selectedId=$("#selectedUserList option:selected").val();
		if(selectedId.length > 0){
		$('.templateUserError').attr('hidden','');
		var isPresentId=userId2.indexOf(selectedId.toString());
		var isPresentName=userList2.indexOf(selectedName.toString());
		if(isPresentId == -1){
		userList2.push(selectedName.toString());
		userId2.push(selectedId.toString().trim());
		var markup = "<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' id="+selectedId.toString()+" data-value="+selectedId.toString()+" title=<spring:message code='application.remove'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+selectedName.toString()+"</td></tr>"
		//index++;
		$('#tableList1').append(markup);
		$('#userId').val(userId2);
		$('#selectedUserList').find('option[value="' + selectedId + '"]').remove();
		$('#selectedUserList').trigger("chosen:updated");
		}
		}else {
			$('.templateUserError').removeAttr('hidden');
		}
		});

		<c:forEach items="${assignedUserList}" var="user">
		$('#tableList1').append("<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' data-value='${user.id}' title=<spring:message code='application.delete'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+'${user.name}'+"</td></tr>");
		var isAvailableUser=userId2.indexOf('${user.id}');
		var isAvailableUserName=userList2.indexOf('${user.name}');
		if(isAvailableUser==-1){
		userId2.push('${user.id}');
		userList2.push('${user.name}');
		}
		$('#userId').val('');
		$('#userId').val(userId2);
		</c:forEach>
	
	$('#idStatus').on('change', function() {
		console.log("hete");
		auctionChange();
		revertBidHide();
	});

	$('#idAuctionType').on('change', function() {
		auctionChange();
		revertBidHide();
	});
	
	auctionChange();
	
	$(".revertBidHide").hide();
	
	
	function auctionChange() {
		console.log("hete 1");
		var eventType = $('#idStatus').val();
		console.log("hete 2 " + eventType);
		if (eventType == 'RFA') {
			$(".submissionVal").hide();
			console.log("hete 3 ");
			var auctionType = $('#idAuctionType').val();
			console.log("hete 4 " + auctionType);
			if (auctionType == "FORWARD_ENGISH" || auctionType == "REVERSE_ENGISH" || auctionType == "FORWARD_SEALED_BID" || auctionType == "REVERSE_SEALED_BID") {
				console.log("hete 5  show");
				$(".auctionRulesTemp").show();
				sealedBidHide();
			} else {
				console.log("hete 6 hide ");
				$(".auctionRulesTemp").hide();
			}
		} else {
			$(".auctionRulesTemp").hide();
			$(".revertBidHide").hide();
			$(".submissionVal").show();
		}
	}

	function sealedBidHide() {
		var eventType = $('#idStatus').val();
		if (eventType == 'RFA') {
			var auctionType = $('#idAuctionType').val();
			if (auctionType == "FORWARD_SEALED_BID" || auctionType == "REVERSE_SEALED_BID") {
				$(".sealedBidHide").hide();
			}else if (auctionType == "FORWARD_ENGISH" || auctionType == "REVERSE_ENGISH") {
				$(".sealedBidHide").show();
			}
			if (auctionType == 'REVERSE_ENGISH' || auctionType == 'REVERSE_SEALED_BID') {
				$('#labelchange1').text("Decrement From own previous");
				$('#labelchange2').text("Price lower then the Leading Bid");
				$('#labelchange3').text("Supplier must provide Lower price or same price");
			} else {
				$('#labelchange1').text("Increment From own previous");
				$('#labelchange2').text("Price higher than the Leading Bid");
				$('#labelchange3').text("Supplier must provide Higher price or same price");
			}
		}
	}
	
	function revertBidHide() {
	var eventType = $('#idStatus').val();
	if (eventType == 'RFA') {
		var auctionType = $('#idAuctionType').val();
		 if (auctionType == "FORWARD_ENGISH" || auctionType == "REVERSE_ENGISH") {
			$(".revertBidHide").show();
		}else
			{
			$(".revertBidHide").hide();
			}
     	}
	}
	
	$('#chkPrevious').change(function(e) {
		//e.preventDefault();
		//alert("hello : " + $(this).prop('id') );
		if ($('#chkPrevious').prop('checked')){
			$('.fadeChkPrevious').show();
			$('#idIsBiddingMinValueFromPreviousDisabled').prop('disabled', false);
		}
		else{
			$('.fadeChkPrevious').hide();
			$('#idIsBiddingMinValueFromPreviousDisabled').prop('checked', false);
			$('#idIsBiddingMinValueFromPreviousDisabled').prop('disabled', true);
			$('#idBiddingMinValue').val('');
		}
		$.uniform.update();
	}); 
	
	if ($('#chkPrevious').prop('checked')){
		$('.fadeChkPrevious').show();
	}
	else{
		$('.fadeChkPrevious').hide();
	}
	
	$(function() {
		$('#idPreBidBy').on('change', function() {
			preBidByOnchange();
		});
	});
	preBidByOnchange();
	function preBidByOnchange() {
		if ($('#idPreBidBy').val() == 'BUYER') {
			$(".lowerPriceSetting").show();
			$(".allowSamePreSetBidForAllSuppliers").show();

		} else {
			$(".lowerPriceSetting").hide();
			$('#idSamePreBidForAllSuppliers').prop('checked', false).change().uniform();
			$('#readOnlyPreSetAllSupp').prop('checked', false).change().uniform();
			$('#optionalPreSetAllSupp').prop('checked', false).change().uniform();
			$(".allowSamePreSetBidForAllSuppliers").hide();
		}
	}

	$(function() {
		$("#idBidFromPre").click(function() {
			hideBidFromPre();
		});
	});
	hideBidFromPre();
	function hideBidFromPre() {
		if ($('#idBidFromPre').is(":checked")) {
			$(".bidFromPreHide").show();
		} else {
			$(".bidFromPreHide").hide();
		}
	}

	$('#idSamebidsupplier').change(function(e) {
		//e.preventDefault();
		//alert("hello : " + $(this).prop('id') );
		if ($(this).prop('checked')) {
			$('#chkOther').prop('checked', false);
			$(this).prop('checked', true);
		}
		if ($('#chkOther').prop('checked'))
			$('.fadeChkOther').show();
		else
			$('.fadeChkOther').hide();
		$.uniform.update();
	}); 
	
	$('#chkOther').change(function(e) {
		//e.preventDefault();
		//alert("hello : " + $(this).prop('id') );
		if ($(this).prop('checked')) {
			$('#idSamebidsupplier').prop('checked', false);
			$(this).prop('checked', true);
		}
		if ($('#chkOther').prop('checked'))
			$('.fadeChkOther').show();
		else
			$('.fadeChkOther').hide();
		$.uniform.update();
	}); 
	
	
	if ($('#chkOther').prop('checked')) {
		$('.fadeChkOther').show();
	} else {
		$('.fadeChkOther').hide();
	}
	
	$('#idBidderDisqualify').change(function(e) {
		bidderDisqua();
	});
	
	bidderDisqua();
	function bidderDisqua() {
		if ($('#idBidderDisqualify').prop('checked')) {
			$(".bidderCountDiv").show();
			$('#idBidderDisRadio').prop('disabled', false);
		} else {
			$(".bidderCountDiv").hide();
			$('#idBidderDisRadio').prop('checked', false);
			$('#idBidderDisRadio').prop('disabled', true);
			$('#child4').val('');
		}
		$.uniform.update();
	}

	$.formUtils.addValidator({
		name : 'validateMax',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#minimumSupplierRating").val();
			 var maxValue=$("#maximumSupplierRating").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#maximumSupplierRating").val()) < parseFloat($("#minimumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Maximum supplier Rating/Grade must be greater than Minimum Supplier Rating/Grade',
		errorMessageKey : 'badMinMax'
	});

	$.formUtils.addValidator({
		name : 'validateMin',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#minimumSupplierRating").val();
			 var maxValue=$("#maximumSupplierRating").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#minimumSupplierRating").val()) > parseFloat($("#maximumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Minimum supplier Rating/Grade must be smaller than Maximum Supplier Rating/Grade',
		errorMessageKey : 'badMinMax'
	});

	$('#participationFeeCurrency').change(function() {
		$('#participationFeeCurrency').parent().find('.error-range.text-danger').remove();
		$('#participationFees').parent().find('.error-range.text-danger').remove();
		var participationFees = ($('#participationFees').val());
		var participationFeeCurrency = ($('#participationFeeCurrency').val());
		
		if((participationFees == '' &&  participationFeeCurrency != '' ) ){
			$('#participationFees').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#participationFees').parent().find('.error-range.text-danger').remove();
		}
		
		if((participationFees != '' &&  participationFeeCurrency == '' )){
			$('#participationFeeCurrency').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#participationFeeCurrency').parent().find('.error-range.text-danger').remove();
		}
	});

	$('#participationFees').change(function() {
		var participationFeeCurrency = ($('#participationFeeCurrency').val());
		var participationFees = ($('#participationFees').val());
		$('#participationFeeCurrency').parent().find('.error-range.text-danger').remove();
		$('#participationFees').parent().find('.error-range.text-danger').remove();
		
		if((participationFeeCurrency == '' &&  participationFees != '' ) ){
			$('#participationFeeCurrency').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#participationFeeCurrency').parent().find('.error-range.text-danger').remove();
		}
		
		if((participationFeeCurrency != '' &&  participationFees == '' )){
			$('#participationFees').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#participationFees').parent().find('.error-range.text-danger').remove();
		}
	});
	
	$('#maximumSupplierRating').on('keyup', function() {
		$('#maximumSupplierRating').validate(function(valid, elem) {});
		$('#minimumSupplierRating').validate(function(valid, elem) {});
	});
		  
	$('#minimumSupplierRating').on('keyup', function() {
		$('#minimumSupplierRating').validate(function(valid, elem) {});
		$('#maximumSupplierRating').validate(function(valid, elem) {});
	});
	
	
	$('#depositCurrency').change(function() {
		$('#deposit').parent().find('.error-range.text-danger').remove();
		$('#depositCurrency').parent().find('.error-range.text-danger').remove();
		var depositCurrency = ($('#depositCurrency').val());
		var deposit = ($('#deposit').val());
		
		if((deposit == '' &&  depositCurrency != '' ) ){
			$('#deposit').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#deposit').parent().find('.error-range.text-danger').remove();
		}
		
		if((deposit != '' &&  depositCurrency == '' )){
			$('#depositCurrency').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#depositCurrency').parent().find('.error-range.text-danger').remove();
		}
	});

	$('#deposit').change(function() {
		var depositCurrency = ($('#depositCurrency').val());
		var deposit = ($('#deposit').val());
		$('#deposit').parent().find('.error-range.text-danger').remove();
		$('#depositCurrency').parent().find('.error-range.text-danger').remove();
		if((depositCurrency == '' &&  deposit != '' ) ){
			$('#depositCurrency').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#depositCurrency').parent().find('.error-range.text-danger').remove();
		}
		
		if((depositCurrency != '' &&  deposit == '' )){
			$('#deposit').parent().append('<span class="error-range text-danger">This is a required field</span>');
		}else{
			$('#deposit').parent().find('.error-range.text-danger').remove();
		}
	});

	$('#saveRfxTemplate').click(function(e) {
		e.preventDefault();
		var i = 0;
		
		$('.chosen-select').each(function (){
			$(this).show();
		});
		
		$('#teamMember').parent().removeClass('has-error').find('.form-error').remove();
		if (assignedUsers == "" && ($('#teamMemberReadOnly').is(':checked'))) {
			$('#teamMember').parent().addClass('has-error').append('<span style="margin-left: 3%;"  class="help-block form-error">This is a required field</span>');
	        return false;
		}
		
		if($('div.publishType-group.required :checkbox:checked').length == 0){
			 $("#selectPublishTypeError").text("Please select atleast one event publish type.").css("color", "#ff5757");
			 return false;
		}	
		var decimalLimit = $('#decimal').val();
		$('#decimal').parent().find('.error-range.text-danger').remove();
		if (($("#visibleDecimal").prop("checked") == false) && (decimalLimit == '')) {
			$('#decimal').parent().addClass('has-error').append('<span class="error-range text-danger">This is a required field</span>');
			return false;
		}
		
		var chosenCurrency = $('#chosenCurrency').val();
		$('#chosenCurrency').parent().find('.error-range.text-danger').remove();
		if (($("#visibleCurrency").prop("checked") == false) && (chosenCurrency == '')) {
			$('#chosenCurrency').parent().addClass('has-error').append('<span class="error-range text-danger">This is a required field</span>');
			return false;
		}
		
		var sumi = 0;
		var sumValue = 0;
		var test ;
		var isFill = 1 ;
var isZero = false;
var isblank = false;
var max = false;
var denvelope = [] ;
var found = false;

		$(".new-envelope").each(function(i, v) {
			i++;
			var value = $("input[name='rfxSequence"+i+"']").val() ;
			denvelope.push(value);
 			test=value.length;

 			if(value == '0' )
			{
    			$("#idNumber"+i).val(null)
    			isZero = true ;
			}

			if(test > 2 )
			{
				max = true;
			}
		
            if(value == '')
	       {
    			$("#idNumber"+i).val(null)
    			isblank = true;
         	}
			if(test == 0)
			{
				isFill = 0 	
			}
			sumi = sumi+i;
			sumValue = sumValue+Number(value);
		});
		
	  for(var i = 0; i <= denvelope.length; i++) {
        for(var j = i; j <= denvelope.length; j++) {
            if(i != j && denvelope[i] == denvelope[j] && denvelope[i]  ) {
       	    	found = true		
            }
        }
     }
		    
		console.log(denvelope);
		if(isZero  ){ 
			 $("#envelopeError").text("0 not allowed").css("color", "#ff5757");
	     	return false;
		}
		
		if(max){ 
			 $("#envelopeError").text("Only two digits are allowed").css("color", "#ff5757");
	     	return false;
		}
		
		if(isFill == 0 && sumValue != 0 ){ 
			 $("#envelopeError").text("Envelope sequence is missing").css("color", "#ff5757");
	     	return false;
		}
		
		
	 	if(found && sumi != 0 )
		{
			 $("#envelopeError").text("Duplicate sequence not allowed").css("color", "#ff5757");
			return false;
		} 
	 	
	 	if(sumValue!=0 &&  sumi != sumValue  && sumi< sumValue)
		{
			 $("#envelopeError").text("Sequence must contain numeric values in ascending order ").css("color", "#ff5757");
			return false;
		} 

	 	 console.log($('#preSet').val());
		var sequenceEmpty = false;
	 	 if($('#preSet').is(':checked') && $('#openingSeq').is(':checked')){
			$(".new-envelope").each(function(i, v) {
				i++;
			 	 console.log($('#preSet').val() + " -- " + $("input[name='rfxSequence"+i+"']").val().length);
				if($("input[name='rfxSequence"+i+"']").val().length == 0){
					sequenceEmpty = true;
 					 return false;
				}
			});
		}
		if(sequenceEmpty){
			$("#envelopeError").text("Envelope sequence is missing").css("color", "#ff5757");
			 return false;
		}	 	 
	 	 $("#envelopeError").text("").css("color", "#ff5757");

		
		if($("#frmRfxTemplate").isValid()){
			$('.chosen-select').each(function (){
				$(this).hide();
			});
			
			$("#frmRfxTemplate").submit();
		}
	});
	
	$(document).on('change', '.eventPublishType', function () {
	    if (this.checked) {
	    	 $("#selectPublishTypeError").text('');
	    }
	});	
	
});
	

	
</script>
<script>
$(document).ready(function() {
$('#chosenBusinessUnit').on('change', function(){
		var value=this.value;
		if(value != undefined && value != ''){
		<c:if test="${isEnableUnitAndCostCorrelation}">
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var url = getContextPath() +  "/buyer/getAssingedCostCenterList";
			$.ajax({
				type : "GET",
				url : url,
				data : {
					unitId : value,
				},
				beforeSend : function(xhr) {
					var html='';
					$('#chosenCostCenter').html(html);
				    $("#chosenCostCenter").trigger("chosen:updated");
					$('#loading').show();
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				complete : function() {
					$('#loading').hide();
				},
				success : function(data) {
					var html='';
					html += '<option value="" selected="selected">'+ 'Select Cost Center' +'</option>';
					$.each(data, function(i, item) {
						if(item.description.length > 0){
							html += '<option value="' + item.id + '">' + item.costCenter + ' - ' + item.description + '</option>';
						}else{
							html += '<option value="' + item.id + '">' + item.costCenter + '</option>';
						}
					});
					$('#chosenCostCenter').append(html);
					$("#chosenCostCenter").trigger("chosen:updated");
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
			});
		</c:if>
				
		<c:if test="${isEnableUnitAndGroupCodeCorrelation}">
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var url = getContextPath() +  "/buyer/getAssingedGroupCodeList";
			$.ajax({
				type : "GET",
				url : url,
				data : {
					buId : value,
				},
				beforeSend : function(xhr) {
					var html='';
					$('#chosenGroupCode').html(html);
				    $("#chosenGroupCode").trigger("chosen:updated");
					$('#loading').show();
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				complete : function() {
					$('#loading').hide();
				},
				success : function(data) {
					var html='';
					html += '<option value="" selected="selected">'+ 'Select Group Code' +'</option>';
					$.each(data, function(i, item) {
						if(item.description.length > 0){
							html += '<option value="' + item.id + '">' + item.groupCode + ' - ' + item.description + '</option>';
						}else{
							html += '<option value="' + item.id + '">' + item.groupCode + '</option>';
						}
					});
					$('#chosenGroupCode').append(html);
					$("#chosenGroupCode").trigger("chosen:updated");
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
			});
		</c:if>
		}
	})
});
</script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/js/view/rfxTemplate.js?8"/>"></script>

<!--datePicker js -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
