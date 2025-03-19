<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prTemplateDesk" code="application.pr.template" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${prTemplateDesk}] });
});
</script>
<div id="page-content" view-name="rfxTemplate">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
			</a></li>
			<c:url value="/buyer/prTemplateList" var="createUrl" />
			<li><a id="listLink" href="sourceTemplateList"> <spring:message code="sourcingtemplate.list" /> </a></li>
			<li class="active"><c:out value='${btnValue}' /><spring:message code="defaultmenu.sourcing.template" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="sourcingtemplates.administration" /></h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="defaultmenu.sourcing.template" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="saveRfxTemplate" value="saveRfxTemplate" />
				<form:form id="frmRfxTemplate" cssClass="form-horizontal bordered-row" method="post" action="updateSourcingForm/" modelAttribute="sourceForm">
					<input type="hidden" id="templateId" value="${prTemplate.id}">
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<input type="hidden" name="id" value="${sourceForm.id }">
							<form:label path="formName" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateName" />
							</form:label>

						</div>

						<div class="col-md-5">
							<spring:message code="prtemplate.enter.template" var="tempnameplace"/>
							<form:input path="formName" data-validation-length="1-128" data-validation="required length" cssClass="form-control" id="idRfxTemplateName" placeholder="${tempnameplace}" />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="description" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateDescription" />
							</form:label>
						</div>
						<div class="col-md-5">\
							<spring:message code="prtemplate.template.description" var="templatedesc"/>
							<form:textarea path="description" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" cssClass="form-control" id="idRfxTemplateDescription" placeholder="${templatedesc}" />
							<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="status" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="status" cssClass="form-control chosen-select disablesearch" id="idStatus" data-validation="required">
								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>
					<div class="clear"></div>
					<%-- 	<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>General Information</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">PR Name</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<spring:message var="placeName" code="pr.place.name" />
									<form:input path="templateFieldBinding.prName" data-validation="length" data-validation-length="0-128" placeholder="${placeName}" class="form-control" />
									<div id="prNameDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.prNameDisabled" class="custom-checkbox" data-validation-error-msg-container="#prNameDisabledError" data-validation="readonly_data" title="Read Only" label="Read Only" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">
										<spring:message code="pr.requester" />
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<spring:message var="placeRequester" code="pr.place.requester" />
									<form:textarea path="templateFieldBinding.requester" rows="5" placeholder="${placeRequester}" data-validation="length" data-validation-length="0-500" class="form-control"></form:textarea>
									<div id="requesterDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.requesterDisabled" class="custom-checkbox" data-validation-error-msg-container="#requesterDisabledError" data-validation="readonly_data" title="Read Only" label="Read Only" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10 control-label">
										<spring:message code="pr.correspondence.address" />
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<div class="input-prepend input-group">
										<label class="physicalCriterion pull-left"> </label>
										<span class="pull-left buyerAddressRadios <c:if test="${not empty prTemplate.templateFieldBinding.correspondenceAddress}">active</c:if>">
											<span class="phisicalArressBlock pull-left marg-top-10">
												<c:if test="${not empty buyerAddress}">
													<div class="">
														<h5>${buyerAddress.title}</h5>
														<span class='desc'>${buyerAddress.line1} ${buyerAddress.line2}, ${buyerAddress.city}, ${buyerAddress.zip}, ${buyerAddress.state.stateName} , ${buyerAddress.state.country.countryName} </span>
													</div>
												</c:if>
											</span>
											<div class=" align-right">
												<a id="deletecorpAddress" class="pull-right" style="font-size: 18px; line-height: 1; padding: 0px; color: rgb(127, 127, 127); margin-top: 8px;"> <i class="fa fa-times-circle"></i>
												</a>
											</div>
										</span>
									</div>
									<div id="sub-credit" class="invite-supplier delivery-address margin-top-10" ${not empty prTemplate.templateFieldBinding.correspondenceAddress ? 'style="display: none;"' : ''}>
										<div class="role-upper ">
											<div class="col-sm-12 col-md-12 col-xs-12 float-left">
												<input type="text" placeholder="Search Address" class="form-control delivery_add">
											</div>
										</div>
										<div class="chk_scroll_box">
											<div class="scroll_box_inner overscrollInnerBox">
												<div class="role-main">
													<div class="role-bottom small-radio-btn">
														<ul class="role-bottom-ul">
															<c:forEach var="address" items="${addressList}">
																<li>
																	<div class="radio-info">
																		<label>
																			<form:radiobutton path="templateFieldBinding.correspondenceAddress" value="${address.k}" class="custom-radio" />
																		</label>
																	</div>
																	<div class="del-add">
																		<h5>${address.title}</h5>
																		<span class='desc'>${address.line1} ${address.line2}, ${address.city}, ${address.zip}, ${address.state.stateName}, ${address.country}</span>
																	</div>
																</li>
															</c:forEach>
														</ul>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div id="correspondenceAddressError"></div>
									<!-- 	<div id="address-buyer-dialog"></div> -->
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.correspondenceAddressDisabled" class="custom-checkbox" data-validation-error-msg-container="#correspondenceAddressError" data-validation="readonly_data_address" title="Read Only" label="Read Only" />
								</div>
							</div>
						</div>
					</div> --%>
					<div class="Invited-Supplier-List import-supplier white-bg">

						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
							<jsp:include page="updateSourcingApprovals.jsp" />
						</div>




					</div>
					<div class="row marg-bottom-202">
						<!-- <div class="col-md-3">
							<label class="marg-top-10"></label>
						</div> -->
						<div class="col-md-9 btns-lower marg-top-20">
							<sec:authorize access="hasRole('PR_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEdit" />
							<form:button type="submit" id="saveRfxTemplate" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" disabled="${!canEdit}">${btnValue}Update</form:button>
							<c:url value="/buyer/prTemplateList" var="createUrl" />
							<a href="${createUrl}" id="cancelId" class="btn btn-black hvr-pop marg-left-10 hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
							<c:if test="${sourceForm.id ne null}">
								<form:button type="button" id="saveAsPrTemplate" class="btn btn-alt btn-hover ph_btn_midium btn-warning hvr-pop pull-right ${canEdit ? '':'disabled'}"><spring:message code="application.saveas" /></form:button>
							</c:if>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<!-- TEMPLATE SAVE AS POPUP -->
<div class="flagvisibility dialogBox" id="prTemplateSaveAsPopup" title="Template Save As">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="tempId" name="tempId" />
		<form>
			<div class="marg-top-20 tempaleData">
				<div class="row marg-bottom-10">
					<div class=" col-md-4">
						<label>
							<spring:message code="rfxTemplate.templateName" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="rfxTemplate.templateDescription.placeHolder" var="desc" />
						<spring:message code="rfxTemplate.templateName.placeHolder" var="name" />
						<input data-validation-length="1-128" data-validation="required length" class="form-control marg-bottom-10" name="tempName" id="tempName" placeholder='<spring:message code="prtemplate.enter.template" />' />
						<span class="customError"></span>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-md-4">
						<label>
							<spring:message code="rfxTemplate.templateDescription" />
						</label>
					</div>
					<div class="col-md-8">
						<textarea name="tempDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" id="tempDescription" placeholder='<spring:message code="prtemplate.template.description" />'></textarea>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_midium btn-tooltip hvr-pop hvr-rectangle-out" id="saveAsSourcingTemp" data-original-title="Delete"><spring:message code="application.create" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
.radio>span {
	padding: 0 !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
<c:if test="${assignedCount > 0}">
$(window).bind('load', function() {
	var allowedFields = '#idRfxTemplateName, #idRfxTemplateDescription, #idStatus, #saveAsPrTemplate, #saveRfxTemplate, #cancelId, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '#cancelId, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>

	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/sourcinggTemplate.js"/>"></script>