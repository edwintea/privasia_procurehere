<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/multiselect/multi-select.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxCreateDetails" code="application.rfx.create.details" />
<spring:message code="unmask.search.user" var="unMasUserPlace" />
<jsp:useBean id="now" class="java.util.Date" />


<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form">
	<h4>
		<spring:message code="eventDetails.event.settings" />
	</h4>

	<div class="form_field">
		<div>
			<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="rfi.createrfi.urgent.event" /></label>
			<div class="col-sm-1 col-md-1 col-xs-1">
				<form:checkbox path="urgentEvent" cssClass="custom-checkbox autoSave" />
			</div>
		</div>
	</div>
	<c:if test="${empty rfxTemplate or rfxTemplate.visibleViewSupplierName}">
		<div class="form_field">
			<div class="${!empty rfxTemplate and rfxTemplate.readOnlyViewSupplierName ?'disabled':''  }">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label "><spring:message code="eventdetails.enable.supplier" /></label>
				<div class="col-sm-1 col-md-1 col-xs-1 ">
					<div class="input-prepend input-group d-flex-line">
						<form:checkbox path="viewSupplerName" id="viewSupplerName" cssClass="custom-checkbox autoSave ${!empty rfxTemplate and rfxTemplate.readOnlyViewSupplierName ?'readOnlyClass':''  }" />
					</div>
				</div>
			</div>

			<div class="col-md-4 col-sm-4 col-xs-5 ${!empty rfxTemplate and rfxTemplate.readOnlyViewSupplierName ?'disabled':''  }" id="unMaskedUserDiv" style="margin-top: -10px;">
				<form:select path="unMaskedUsers" data-validation="required" cssClass="form-control user-list-normal chosen-select autoSave" id="unMaskedUser" multiple="multiple" data-placeholder="Select Unmask Owners">
					<c:forEach items="${maskingUserList}" var="usr">
						<c:if test="${usr.id == '-1'}">
							<form:option value="-1" label="${usr.name}" disabled="true" />
						</c:if>
						<c:if test="${usr.id != '-1' }">
							<form:option value="${usr.id}" label="${usr.name}" />
						</c:if>
					</c:forEach>
				</form:select>
			</div>
		</div>
	</c:if>
	<c:if test="${empty rfxTemplate or rfxTemplate.visibleCloseEnvelope}">
		<div class="form_field">
			<div class="${!empty rfxTemplate and rfxTemplate.readOnlyCloseEnvelope ?'disabled':''  }">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="rfi.createrfi.allow.close" /></label>
				<div class="col-sm-1 col-md-1 col-xs-1 ">
					<form:checkbox path="closeEnvelope" cssClass="custom-checkbox autoSave ${!empty rfxTemplate and rfxTemplate.readOnlyCloseEnvelope ?'readOnlyClass':''  }" />
				</div>
			</div>
		</div>
	</c:if>
	<c:if test="${(empty rfxTemplate or rfxTemplate.visibleAddSupplier) and (eventType ne 'RFA')}">
		<div class="form_field">
			<div class="${!empty rfxTemplate and rfxTemplate.readOnlyAddSupplier ?'disabled':''  }">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="rfi.createrfi.allow.add" /></label>
				<div class="col-sm-1 col-md-1 col-xs-1 ">
					<form:checkbox path="addSupplier" cssClass="custom-checkbox autoSave ${!empty rfxTemplate and rfxTemplate.readOnlyAddSupplier ?'readOnlyClass':''  }" />
				</div>
			</div>
		</div>
	</c:if>




	<c:if test="${eventType eq'RFA'and (empty rfxTemplate or rfxTemplate.visibleViewAuctionHall)}">
		<div class="form_field">
			<div class="${!empty rfxTemplate and rfxTemplate.readOnlyViewAuctionHall ?'disabled':''  }">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label">Allow viewing of Auction Hall after event ends</label>
				<div class="col-sm-1 col-md-1 col-xs-1">
					<form:checkbox path="viewAuctionHall" cssClass="custom-checkbox autoSave ${!empty rfxTemplate and rfxTemplate.readOnlyViewAuctionHall ?'readOnlyClass':''  }" />
				</div>
			</div>
		</div>
	</c:if>

	<c:if test="${eventType eq'RFA'and event.auctionType != 'FORWARD_DUTCH' and event.auctionType != 'REVERSE_DUTCH' and event.auctionType != 'FORWARD_SEALED_BID' and event.auctionType != 'REVERSE_SEALED_BID' }">
		<c:if test="${eventType eq'RFA'and (empty rfxTemplate or rfxTemplate.visibleRevertLastBid)}">
			<div class="${!empty rfxTemplate and rfxTemplate.readOnlyRevertLastBid ? 'disabled':''  } form_field">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="rfa.createrfa.allow.revert.last.bid" /></label>
				<div class="col-sm-1 col-md-1 col-xs-1 ">
					<form:checkbox path="revertLastBid" id="revertLastBid" cssClass="custom-checkbox autoSave ${!empty rfxTemplate and rfxTemplate.readOnlyRevertLastBid ? 'readOnlyClasse' : ''  }" />
				</div>
				<div class="col-md-4 col-sm-4 col-xs-5 ${!empty rfxTemplate and rfxTemplate.readOnlyRevertLastBid ? 'disabled' : ''  }" id="revertBidUserDiv" style="margin-top: -10px;">
					<form:select path="revertBidUser" data-validation="required" cssClass="user-list-normal form-control chosen-select" id="revertBidUser">
						<form:option value="">
							<spring:message code="rfa.select.allow.revert.bid.owner" />
						</form:option>
						<c:forEach items="${userList2}" var="usr">
							<c:if test="${usr.id != '-1'}">
								<form:option value="${usr.id}" label="${usr.name}" />
							</c:if>
							<c:if test="${usr.id == '-1'}">
								<form:option value="-1" label="${usr.name}" disabled="true" />
							</c:if>
						</c:forEach>
					</form:select>
				</div>
			</div>
		</c:if>
	</c:if>



	<c:if test="${empty rfxTemplate or rfxTemplate.visibleAllowToSuspendEvent}">
		<div class="form_field">
			<div class="${!empty rfxTemplate and rfxTemplate.readOnlyAllowToSuspendEvent ?'disabled':''  }">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="rfi.createrfi.allow.suspend" /></label>
				<div class="col-sm-1 col-md-1 col-xs-1">
					<form:checkbox path="allowToSuspendEvent" cssClass="custom-checkbox autoSave ${!empty rfxTemplate and rfxTemplate.readOnlyAllowToSuspendEvent ?'readOnlyClass':''  }" />
				</div>
			</div>
		</div>
	</c:if>

	<div class="form_field">
		<div class="${event.rfxEnvelopeReadOnly?'disabled':''}">
			<spring:message code="envelope.sequence.opening" var="visible" />
			<label class="col-sm-4 col-md-3 col-xs-6 control-label">${visible}</label>
			<div class="col-md-1">
				<div class="d-flex" style="width: 300px; display: flex;">
					<form:checkbox path="rfxEnvelopeOpening" id="openingSeq" class="custom-checkbox" title="" label="" />
				</div>
			</div>	
		</div>
		<div class="btn-radio" id="allowEvaluation">
			<div class="${event.rfxEnvelopeReadOnly?'disabled':''}">
				<spring:message code="envelope.sequence.opening.allow" var="visible1" />
				<label class="col-sm-4 col-md-3 col-xs-6 control-label">${visible1}</label>
				<div class="col-md-1">
					<div class="d-flex" style="width: 300px; display: flex;">
						<form:checkbox path="allowDisqualifiedSupplierDownload" id="openingSeq1" class="custom-checkbox" title="" label="" />
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-4 col-md-4 col-xs-5" style="padding-left: 0px;">
			<div class="btn-radio">
				<div class="col-md-9 ${event.rfxEnvelopeReadOnly?'disabled':''} ">
					<div class="check-wrapper d-flex" style="width: 300px; display: flex; align-items: center; margin-top: -26%;">
						<label class="select-radio-lineHgt"> <form:radiobutton path="rfxEnvOpeningAfter" id="rfxEnvOpeningAfter" checked="checked" value="OPENING" class="custom-radio showSupplierBlocks" /> After each Opening
						</label>
					</div>
				</div>
			</div>
			<div class="btn-radio">
				<div class="col-md-9  ${event.rfxEnvelopeReadOnly?'disabled':''} ">
					<div class="check-wrapper d-flex" style="width: 300px; display: flex; align-items: center; margin-top: -12%;">
						<label class="select-radio-lineHgt"> <form:radiobutton path="rfxEnvOpeningAfter" name="rfxEnvOpeningAfter" id="rfxEnvOpeningAfter" value="EVALUATION" class="custom-radio showSupplierBlocks" /> After each Evaluation
						</label>
					</div>
				</div>
			</div>
		</div>
	</div>


	<c:if test="${empty rfxTemplate or rfxTemplate.visibleEvaluationConclusionUsers}">
		<div class="form_field">
			<div class="${!empty rfxTemplate and rfxTemplate.readOnlyEvaluationConclusionUsers ?'disabled':''  }">
				<spring:message code="rfx.template.eval.conclusion.prem.enable" var="enableEvalCon" />
				<label class="col-sm-4 col-md-3 col-xs-6 control-label ">${enableEvalCon}</label>
				<div class="col-sm-1 col-md-1 col-xs-1">
					<div class="input-prepend input-group d-flex-line">
						<form:checkbox path="enableEvaluationConclusionUsers" id="enableEvaluationConclusionUsers" cssClass="custom-checkbox autoSave ${!empty rfxTemplate and rfxTemplate.readOnlyEvaluationConclusionUsers ?'readOnlyClass':''  }" />
					</div>
				</div>
			</div>
			<div class="col-sm-4 col-md-4 col-xs-5 ${!empty rfxTemplate and rfxTemplate.readOnlyEvaluationConclusionUsers ? 'disabled' : '' }" id="evaluationConclusionUsersDiv" style="margin-top: -10px;">
				<spring:message code="rfx.template.eval.conclusion.prem.user" var="evalConUserPlaceholder" />
				<form:select path="evaluationConclusionUsers" data-validation="required" cssClass="form-control user-list-normal chosen-select autoSave" id="evalConUser" multiple="multiple" data-placeholder="${evalConUserPlaceholder}">
					<c:forEach items="${evaluationConclusionUsers}" var="usr">
						<c:if test="${usr.id == '-1'}">
							<form:option value="-1" label="${usr.name}" disabled="true" />
						</c:if>
						<c:if test="${usr.id != '-1' }">
							<form:option value="${usr.id}" label="${usr.name}" />
						</c:if>
					</c:forEach>
				</form:select>
			</div>
		</div>
	</c:if>

	<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'EVALUATION_PROCESS_DECLARATION' )) : 'true' }">
		<div class="form_field">
			<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="eventdetails.enable.evaluation.declaration" /></label>
			<div class="col-sm-1 col-md-1 col-xs-1 ">
				<div class="input-prepend input-group d-flex-line">
					<div class="${ (!empty templateFields ? (tf:readonly( templateFields, 'EVALUATION_PROCESS_DECLARATION' ) ? ' disabled': '') : '')}">
						<form:checkbox path="enableEvaluationDeclaration" id="enableEvlDeclare" cssClass="custom-checkbox" />
					</div>
				</div>
			</div>

			<div class="col-sm-4 col-md-4 col-xs-5 ${ (!empty templateFields ? (tf:readonly( templateFields, 'EVALUATION_PROCESS_DECLARATION' ) ? ' disabled': '') : '')}" id="evaluationProcessDiv" style="margin-top: -10px;">
				<form:select path="evaluationProcessDeclaration" cssClass="form-control chosen-select" id="chosenEvaluationDeclaraton" data-validation='requiredEvlDeclaration'>
					<form:option value="">
						<spring:message code="rfxtemplate.select.declaration" />
					</form:option>
					<form:options items="${evaluationDeclaratonList}" itemValue="id" itemLabel="title" />
				</form:select>
			</div>
		</div>
	</c:if>

	<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'SUPPLIER_ACCEPTANCE_DECLARATION' )) : 'true' }">
		<div class="form_field">
			<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="eventdetails.enable.supplier.declaration" /></label>
			<div class="col-sm-1 col-md-1 col-xs-1 ">
				<div class="input-prepend input-group d-flex-line">
					<div class="${ (!empty templateFields ? (tf:readonly( templateFields, 'SUPPLIER_ACCEPTANCE_DECLARATION' ) ? ' disabled': '') : '')}">
						<form:checkbox path="enableSupplierDeclaration" id="enableSupplierDeclare" cssClass="custom-checkbox" />
					</div>
				</div>
			</div>

			<div class="col-sm-4 col-md-4 col-xs-5 ${ (!empty templateFields ? (tf:readonly( templateFields, 'SUPPLIER_ACCEPTANCE_DECLARATION' ) ? ' disabled': '') : '')}" id="evaluationSupplierDiv" style="margin-top: -10px;">
				<form:select path="supplierAcceptanceDeclaration" cssClass="form-control chosen-select" id="choseSupplierDeclaration" data-validation='requiredSupplierDeclaration'>
					<form:option value="">
						<spring:message code="rfxtemplate.select.declaration" />
					</form:option>
					<form:options items="${supplierDeclaratonList}" itemValue="id" itemLabel="title" />
				</form:select>
			</div>
		</div>
	</c:if>

</div>
