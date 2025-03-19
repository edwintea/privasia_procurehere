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
			<li><a id="listLink" href="${createUrl} "> <spring:message code="prtemplate.template.list" /> </a></li>
			<li class="active"><c:out value='${btnValue}' /><spring:message code="eventawards.pr.template" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="prtemplate.template.list" /><spring:message code="prtemplate.template.administration" /></h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="eventawards.pr.template" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="saveRfxTemplate" value="saveRfxTemplate" />
				<form:form id="frmRfxTemplate" cssClass="form-horizontal bordered-row" method="post" action="savePrTemplate" modelAttribute="prTemplate">
					<input type="hidden" id="templateId" value="${prTemplate.id}">
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="templateName" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateName" />
							</form:label>
							<form:hidden path="buyer.id" />
						</div>
						<form:hidden path="id" />
						<div class="col-md-5">
							<spring:message code="prtemplate.enter.template" var="tempnameplace"/>
							<form:input path="templateName" data-validation-length="1-128" data-validation="required length" cssClass="form-control" id="idRfxTemplateName" placeholder="${tempnameplace}" />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="templateDescription" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateDescription" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="prtemplate.template.description" var="templatedesc"/>
							<form:textarea path="templateDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" cssClass="form-control" id="idRfxTemplateDescription" placeholder="${templatedesc}" />
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
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3><spring:message code="prsummary.general.information" /></h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="pr.name" /></label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<spring:message var="placeName" code="pr.place.name" />
									<form:input path="templateFieldBinding.prName" data-validation="length" data-validation-length="0-128" placeholder="${placeName}" class="form-control" />
									<div id="prNameDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<spring:message  code="tooltip.read.only"  var="tipreadonly"/>
									<spring:message code="prtemplate.label.read.only" var="read" />
									<form:checkbox path="templateFieldBinding.prNameDisabled" class="custom-checkbox" data-validation-error-msg-container="#prNameDisabledError" data-validation="readonly_data" title="${tipreadonly}" label="${read}" />
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
									<form:checkbox path="templateFieldBinding.requesterDisabled" class="custom-checkbox" data-validation-error-msg-container="#requesterDisabledError" data-validation="readonly_data" title="${tipreadonly}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10 control-label">
										<spring:message code="pr.correspondence.address" />**
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
												<input type="text" placeholder='<spring:message code="event.search.address.placeholder" />' class="form-control delivery_add">
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
																			<form:radiobutton path="templateFieldBinding.correspondenceAddress" value="${address.id}" class="custom-radio" />
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
									<form:checkbox path="templateFieldBinding.correspondenceAddressDisabled" class="custom-checkbox" data-validation-error-msg-container="#correspondenceAddressError" data-validation="readonly_data_address" title="${tipreadonly}" label="${read}" />
								</div>
							</div>
						</div>
					</div>
					<div class="Invited-Supplier-List import-supplier white-bg">
						<div class="meeting2-heading">
							<h3><spring:message code="pr.finance" /></h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="pr.base.currency" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<form:select class="chosen-select" id="chosenCurrency" path="templateFieldBinding.baseCurrency">
										<form:option value=""><spring:message code="prtemplate.default.currency" /></form:option>
										<form:options items="${currencyList}" itemValue="id" itemLabel="currencyCode" />
									</form:select>
									<div id="baseCurrencyDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.baseCurrencyDisabled" class="custom-checkbox" data-validation-error-msg-container="#baseCurrencyDisabledError" data-validation="readonly_data" title="${tipreadonly}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.decimal.label" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<form:select path="templateFieldBinding.decimal" cssClass="form-control chosen-select">
										<form:option value=""><spring:message code="buyersettings.selectdacimal" /></form:option>
										<form:option value="1"></form:option>
										<form:option value="2"></form:option>
										<form:option value="3"></form:option>
										<form:option value="4"></form:option>
									</form:select>
									<div id="decimalDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.decimalDisabled" data-validation-error-msg-container="#decimalDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${tipreadonly}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.costcenter.label" /></label>
								</div>
								<div class="col-md-3 dd sky ${assignedCount > 0 ? 'disabled':''} ">
									<form:select class="chosen-select" id="chosenCostCenter" path="templateFieldBinding.costCenter">
										<form:option value="">
											<spring:message code="pr.select.cost.center" />
										</form:option>
											<c:forEach items="${costCenterList}" var="costCenter">
												<form:option value="${costCenter.id}">${costCenter.costCenter} - ${costCenter.description}</form:option>
											</c:forEach>
									</form:select>
									<div id="costCenterDisabledError"></div>
								</div>
								<div class="check-wrapper first">
									<spring:message code="tooltip.visible" var="tipvisible"/>
									<spring:message code="prtemplate.label.visible" var="visible" />
									<form:checkbox path="templateFieldBinding.costCenterVisible" class="custom-checkbox" title="${tipvisible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.costCenterDisabled" data-validation-error-msg-container="#costCenterDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${tipreadonly}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="tooltip.optional" var="tipoptional"/>
									<spring:message code="prtemplate.label.optional" var="optional" />
									<form:checkbox path="templateFieldBinding.costCenterOptional" class="custom-checkbox" title="${tipoptional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="label.businessUnit" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<form:select class="chosen-select" id="chosenBusinessUnit" path="templateFieldBinding.businessUnit">
										<form:option value="">
											<spring:message code="pr.select.business.unit" />
										</form:option>
										<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
									</form:select>
									<div id="businessUnitDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.businessUnitDisabled" data-validation-error-msg-container="#businessUnitDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${tipreadonly}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="eventdescription.paymentterm.label" /></label>
								</div>
								<div class="col-md-3">
									<spring:message var="placePaymentTerm" code="pr.place.paymentTerm" />
									<form:textarea path="templateFieldBinding.paymentTerms" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" cssClass="form-control" id="idRfxTemplatePayment" placeholder="${placePaymentTerm}" />
									<div id="paymentTermsDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.paymentTermsDisabled" data-validation-error-msg-container="#paymentTermsDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${tipreadonly}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="prtemplate.terms.condition" /> </label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<spring:message code="prtemplate.terms.conditions" var="termscondition"/>
									<form:textarea path="templateFieldBinding.termAndCondition" rows="5" placeholder="${termscondition}" data-validation="length" data-validation-length="0-850" class="form-control"></form:textarea>
									<div id="termAndConditionError"></div>
									<span class="sky-blue"><spring:message code="terms.max.characters.only" /></span>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.termAndConditionDisabled" data-validation-error-msg-container="#termAndConditionError" class="custom-checkbox" data-validation="readonly_data" title="${tipreadonly}" label="${read}" />
								</div>
							</div>
						</div>
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
							<jsp:include page="prTemplateApprovals.jsp" />
						</div>


						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
							<jsp:include page="/WEB-INF/views/jsp/buyer/rfxTemplate/rfxTemplateTeamMembers.jsp"></jsp:include>
						</div>


					</div>
					<div class="row marg-bottom-202">
						<!-- <div class="col-md-3">
							<label class="marg-top-10"></label>
						</div> -->
						<div class="col-md-9 btns-lower marg-top-20">
							<sec:authorize access="hasRole('REQUEST_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEdit" />
							<form:button type="submit" id="saveRfxTemplate" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" disabled="${!canEdit}">${btnValue}</form:button>
							<c:url value="/buyer/prTemplateList" var="createUrl" />
							<a href="${createUrl}" id="cancelId" class="btn btn-black hvr-pop marg-left-10 hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
							<c:if test="${prTemplate.id ne null}">
								<form:button type="button" id="saveAsPrTemplate" class="btn btn-alt btn-hover ph_btn_midium btn-warning hvr-pop pull-right ${canEdit ? '':'disabled'}">Save As</form:button>
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
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_midium btn-tooltip hvr-pop hvr-rectangle-out" id="saveAsTemp" data-original-title="Delete"><spring:message code="application.create" /></a>
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
<script type="text/javascript" src="<c:url value="/resources/js/view/prTemplate.js"/>"></script>