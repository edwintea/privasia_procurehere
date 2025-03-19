<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<sec:authentication property="principal.tenantId" var="userTenantId" />
<spring:message var="buyerProfile" code="application.buyer.profile" />
<sec:authorize access="(hasRole('ROLE_COMPANY_DETAILS_EDIT') or hasRole('ADMIN')) and hasRole('BUYER')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_COMPANY_DETAILS_VIEW_ONLY')" var="buyerReadOnlyAdmin" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerProfile}] });
});
</script>
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.mr-15 {
	margin-right: 15px;
}

.w-100 {
	width: 100% !important;
	float: right !important;
}

.d-flex-btn {
	display: flex;
	justify-content: center;
	margin-right: 0;
}

#page-content-wrapper {
	min-height: 1200px;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.marLeft {
	margin-left: 45px;
}
</style>
<div id="page-content-wrapper">
	<div id="page-content">
	<div class="container">
			<ol class="breadcrumb">
				<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard" /></a></li>
				<li class="active"><spring:message code="buyer.profile.name" /></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head">
					<spring:message code="buyer.profile.name" />
				</h2>
				<div class="right-header-button"></div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="example-box-wrapper wigad-new white-bg">
				<div>
					<div class="col-lg-2 tab-section" id="buyerProfileSectionTabId">
						<spring:message code="tab.section.buyer.profile"/>
					</div>
					<div class="col-lg-2 tab-section tab-section-inactive" id="buyerProfileMaintenanceSectionTabId">
						<spring:message code="tab.section.published.profile"/>
					</div>
				</div>
			</div>
			
			<div id="buyerProfileSectionId" class="example-box-wrapper wigad-new white-bg">
				<!-- 	</div>
			</div> -->
				<div class="pro_set_wrap">
					<div class="Invited-Supplier-List import-supplier white-bg">

						<div class="rec_form">
							<div class="row">
								<div class="col-md-offset-3 col-md-6">
									<c:url var="buyerProfileForm" value="buyerProfileForm" />
									<form:form action="buyerProfileForm?${_csrf.parameterName}=${_csrf.token}" id="demo-form" method="post" modelAttribute="buyer" enctype="multipart/form-data" autocomplete="off">
										<form:hidden path="id" />
										<input type="hidden" name="d" value="${d}">
										<input type="hidden" name="v" value="${v}">
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.companyname" />
											</div>
											<div class="rec_form_inp">
												<spring:message code="company.name.placeholder" var="companyname" />
												<form:input type="text" path="companyName" placeholder="${companyname}" class="rec_inp_style1" data-validation="required" readonly="true" disabled="true" />
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.regnumber" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="companyRegistrationNumber" placeholder="" class="rec_inp_style1" data-validation="required" readonly="true" style="background: #f2f2f2 none repeat scroll 0 0;cursor: not-allowed;" />
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.fullname" />
											</div>
											<div class="rec_form_inp">
												<spring:message code="full.name.placeholder" var="fulname" />
												<form:input type="text" path="fullName" placeholder="${fulname}" class="rec_inp_style1" data-validation="required" readonly="true" disabled="true" />
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.mobileno" />
											</div>
											<div class="rec_form_inp">
												<spring:message code="mobile.number.placeholder" var="mobilenumber" />
												<form:input type="text" path="mobileNumber" id="idMobileNumber" placeholder="${mobilenumber}" class="rec_inp_style1" data-validation="required length" data-validation-ignore="+ " data-validation-length="6-14" />
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.contactno" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="companyContactNumber" id="idCompanyContactNumber" placeholder="" class="rec_inp_style1" data-validation="required length" data-validation-ignore="+ " data-validation-length="6-14" />
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.commemail" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="communicationEmail" placeholder="" class="rec_inp_style1" data-validation="required email" />
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.loginmail" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="loginEmail" class="rec_inp_style1" data-validation="required email" readonly="true" disabled="true" />
											</div>
										</div>
										<%-- <div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.Password" />
											</div>
											<div class="rec_form_inp">
												<spring:message code="changepassword.new.required" var="required" />
												<spring:message code="changepassword.new.length" var="length" />
												<spring:message code="changepassword.custom" var="custom" />
												<spring:message code="password.placeholder" var="pasword" />
												<form:password path="password" class="rec_inp_style1" placeholder="${pasword}" data-validation="required custom length" data-validation-length="max15" data-validation-regexp="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,15})"
													data-validation-error-msg-length="${length}" data-validation-error-msg-custom="${custom}" />
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="buyer.profilesetup.confirmpassword" />
											</div>
											<div class="rec_form_inp">
												<form:password path="" placeholder="" name="buyerPassword" class="rec_inp_style1" data-validation="confirmation" data-validation-confirm="password"></form:password>
											</div>
										</div> --%>
										<div class="rec_address">
											<spring:message code="buyer.profilesetup.company.address" />
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="application.addr1" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="line1" placeholder="" class="rec_inp_style1" data-validation="length" data-validation-allowing="- " data-validation-length="2-250"></form:input>
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="application.addr2" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="line2" placeholder="" class="rec_inp_style1"></form:input>
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="application.city" />
											</div>
											<div class="rec_form_inp">
												<spring:message code="buyerprofile.city.custom" var="errmsg" />
												<form:input type="text" path="city" placeholder="" class="rec_inp_style1" data-validation="required custom length" data-validation-length="2-200" data-validation-error-msg-custom="${errmsg}" data-validation-regexp="^([a-zA-Z\\-\\s]+)$"></form:input>
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="application.country" />
											</div>
											<div class="rec_form_inp">
												<form:select path="registrationOfCountry" data-validation="required" id="registrationOfCountry" class="chosen-select rec_inp_style2">
													<form:option value="">
														<spring:message code="application.country.registration" />
													</form:option>
													<form:options items="${registeredCountry}" itemValue="id" itemLabel="countryName" />
												</form:select>
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="application.state" />
											</div>
											<div class="rec_form_inp">
												<form:select path="state" data-validation="required" class="chosen-select rec_inp_style2" id="stateList">
													<form:option value="">
														<spring:message code="application.selectstate" />
													</form:option>
													<form:options items="${countryStates}" itemValue="id" itemLabel="stateName"></form:options>
												</form:select>
											</div>
										</div>
										<%-- <div class="rec_form_row">											
											<div class="rec_form_lable">Logo</div>
											<div class="rec_form_inp profile_logo">
												<c:if test="${empty logoImg}">
													<img id="logoImageHolder" src="${pageContext.request.contextPath}/resources/images/logo-image.png" alt="Logo" onclick="$('#logoImg').click()" />
													<div class="col-md-6">
														<a href="javascript:" onclick="$('#logoImg').click()">Upload Logo</a>
													</div>
													<div class="col-md-6">
														<a href="javascript:" id="removeLogo">Remove Logo</a>
													</div>
												</c:if>
												<c:if test="${not empty logoImg}">
													<img id="logoImageHolder" src="data:image/jpeg;base64,${logoImg}" alt="Logo" onclick="$('#logoImg').click()" />
													<div class="col-md-6">
														<a href="javascript:" onclick="$('#logoImg').click()">Upload Logo</a>
													</div>
													<div class="col-md-6">
														<a href="javascript:" id="removeLogo">Remove Logo</a>
													</div>
												</c:if>
												<form:input type="file" accept="image/*" style="visibility: hidden" name="logoImg" id="logoImg" path="" />
												<input type="hidden" id="removeFile" name="removeFile" value="false">
											</div>
										</div> --%>


										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="systemsetting.bu.logo" />
											</div>
											<div class="rec_form_inp ">
												<div class="profile">
													<c:if test="${empty logoImg}">
														<img id="logoImageHolder" src="${pageContext.request.contextPath}/resources/images/logo-image.png" alt="Logo" onclick="$('#logoImg').click()" />
														<div class="col-md-8">
															<a href="javascript:" onclick="$('#logoImg').click()"><spring:message code="systemsetting.bu.upload.logo" /></a>
														</div>
														<div class="col-md-4">
															<a href="javascript:" id="removeLogo"><spring:message code="systemsetting.bu.remove.logo" /></a>
														</div>
													</c:if>
													<c:if test="${not empty logoImg}">
														<img id="logoImageHolder" src="data:image/jpeg;base64,${logoImg}" alt="Logo" onclick="$('#logoImg').click()" />
														<div class="col-md-8">
															<a href="javascript:" onclick="$('#logoImg').click()"><spring:message code="systemsetting.bu.upload.logo" /></a>
														</div>
														<div class="col-md-4">
															<a href="javascript:" id="removeLogo"><spring:message code="systemsetting.bu.remove.logo" /></a>
														</div>
													</c:if>
													<form:input type="file" accept="image/*" style="visibility: hidden" name="logoImg" id="logoImg" path="" />
													<input type="hidden" id="removeFile" name="removeFile" value="false">
												</div>

											</div>
										</div>


										<div class="rec_form_row common_mar">
											<div class="rec_form_lable">Public Event URL</div>
											<div class="rec_form_inp marg_url">
												<a id="publicEventsId" class="copyLink">${appUrl}/publicEvents/${buyer.publicContextPath}</a>
												<button type="button" class="btn btn-primary demo-2 marg-top-10" data-clipboard-target=".copyLink">Copy link</button>
												<br>
											</div>
										</div>
										<div class="rec_form_row common_mar">
											<div class="rec_form_inp w-100">
												<div class="d-flex-btn recf_bttn recf_bttn1">
												<c:if test="${canEdit}">
													<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out mr-15">
														<spring:message code="application.update" />
													</button>
												</c:if>
													<a href="${pageContext.request.contextPath}/buyer/buyerDashboard" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"><spring:message code="application.cancel" /></a>
												</div>
											</div>
										</div>
									</form:form>
								</div>
							</div>
							<div class="clearfix"></div>
						</div>
			</div>
	</div>
	</div>
	<div id="buyerProfileMaintenanceSectionId" class="example-box-wrapper wigad-new white-bg" style="display: none;">
		<div class="pro_set_wrap">
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="rec_form">
					<div class="row">
						<div class="col-md-offset-1">
							<c:url var="buyerProfileForm" value="buyerProfileForm" />
							<form:form action="buyerPublishedProfileForm?${_csrf.parameterName}=${_csrf.token}" id="profile-maintainance-form" method="post" modelAttribute="buyer" enctype="multipart/form-data" autocomplete="off">
								<form:hidden path="id" />
								<input type="hidden" id="canEditId" value="${canEdit}"/>
								<div class="rec_form_row" style="margin-top: 10px;">
									<div>
										<spring:message code="buyer.publish.profile" var="visibleText" />
										<form:checkbox id="publishedProfileId" value="publishedProfile" path="publishedProfile" class="custom-checkbox" title="${visibleText}" label="${visibleText}"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div class="rec_form_lable">
										<spring:message code="buyer.profilesetup.commemail" />
									</div>
									<div class="rec_form_inp">
										<spring:message code="buyer.profilesetup.commemail.placeholder" var="commemail" />
										<form:input type="email" path="publishedProfileCommunicationEmail" placeholder="${commemail}" class="rec_inp_style1 disable-input" data-validation="length email" data-validation-length="0-128" readonly="true"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div class="rec_form_lable">
										<spring:message code="buyer.published.profile.contact.number" />
									</div>
									<div class="rec_form_inp">
										<spring:message code="buyer.published.profile.contact.number.placeholder" var="contact" />
										<form:input type="text" path="publishedProfileContactNumber" placeholder="${contact}" class="rec_inp_style1 disable-input" data-validation="length custom" data-validation-ignore="+ " data-validation-length="0-20" readonly="true" data-validation-regexp="^[0-9-+]{0,20}$"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div class="rec_form_lable">
										<spring:message code="buyer.published.profile.contact.person" />
									</div>
									<div class="rec_form_inp">
										<spring:message code="buyer.published.profile.contact.person.placeholder" var="contactName" />
										<form:input type="text" path="publishedProfileContactPerson" placeholder="${contactName}" class="rec_inp_style1 disable-input" data-validation="length" data-validation-length="0-128" readonly="true"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div class="rec_form_lable">
										<spring:message code="buyer.published.profile.website" />
									</div>
									<div class="rec_form_inp">
										<spring:message code="buyer.published.profile.website.placeholder" var="website" />
										<form:input type="text" path="publishedProfileWebsite" placeholder="${website}" class="rec_inp_style1 disable-input" data-validation="length" data-validation-length="0-64" readonly="true"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div class="rec_form_lable">
										<spring:message code="buyer.published.profile.information.to.suppliers" />
									</div>
									<div class="rec_form_inp">
										<spring:message code="buyer.published.profile.information.to.suppliers.placeholder" var="info" />
										<form:textarea path="publishedProfileInfoToSuppliers" placeholder="${info}" class="rec_inp_style1 disable-input" data-validation="length" data-validation-length="0-500" readonly="true"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div style="font-size: 14px;">
										<spring:message code="buyer.allow.industry.category" var="visibleText" />
										<form:checkbox id="allowIndCatId" value="publishedProfileIsAllowIndustryCat" path="publishedProfileIsAllowIndustryCat" class="custom-checkbox" title="${visibleText}" label="${visibleText}"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div class="rec_form_lable">
										<spring:message code="buyer.published.profile.min.categories" />
									</div>
									<div class="rec_form_inp">
										<spring:message code="buyer.published.profile.minmax.categories.placeholder" var="minmax" />
										<form:input id="minCategories" type="number" path="publishedProfileMinimumCategories" placeholder="${minmax}" class="rec_inp_style1 disable-input" data-validation="min_ind_cat positive greater_than_zero" readonly="true"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div class="rec_form_lable">
										<spring:message code="buyer.published.profile.max.categories" />
									</div>
									<div class="rec_form_inp">
										<spring:message code="buyer.published.profile.minmax.categories.placeholder" var="minmax" />
										<form:input id="maxCategories" type="number" path="publishedProfileMaximumCategories" placeholder="${minmax}" class="rec_inp_style1 disable-input" data-validation="max_ind_cat positive" readonly="true"/>
									</div>
								</div>
								<div class="rec_form_row">
									<div  class="rec_form_lable" style="font-size: 14px;">
										<spring:message code="buyer.allow.prequali.form" var="visibleText" />
										<form:checkbox id="isEnablePrequalFormId" value="isEnablePrequalificationForm" path="isEnablePrequalificationForm" class="custom-checkbox" title="${visibleText}" label="${visibleText}"/>
									</div>
									<div class="rec_form_inp ${buyer.isEnablePrequalificationForm ==false ? ' disabled': ''}" id="enableSupplierForm">
										<form:select path="supplierForm"  cssClass="form-control" class="chosen-select rec_inp_style2" id="chosenSupplierForm" data-validation='requiredSupplierForm'>
											<form:option value="">
												<spring:message code="buyer.select.supplier.form" var="selectForm" />
											</form:option>
												<form:options items="${supplierFormList}"  itemValue="id" itemLabel="name" ></form:options>
										</form:select>
									</div>
								</div>
								
								<div class="rec_form_row common_mar" style="margin-top: 20px;">
									<div class="rec_form_inp w-100">
										<div class="d-flex-btn recf_bttn recf_bttn1 w-100">
											<c:if test="${canEdit}">
											<form:button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out mr-15">
												<spring:message code="application.update" />
											</form:button>
											</c:if>
											<a href="${pageContext.request.contextPath}/buyer/buyerDashboard" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"><spring:message code="application.cancel" /></a>
										</div>
									</div>
								</div>
							</form:form>
						</div>
					</div>
					<div class="clearfix"></div>
				</div>
			</div>
</div>
<div class="modal fade" id="confirmRemoveEvent" role="dialog">
			<div class="modal-dialog for-delete-all reminder">
				<div class="modal-content">
					<div class="modal-header">
						<h3>
							<spring:message code="application.confirm.unpublish"/>
						</h3>
						<button class="close for-absulate" type="button" data-dismiss="modal" id="cross">X</button>
					</div>
					<div class="modal-body">
						<label><spring:message code="application.confirm.unpublish.modal.body"/></label>
					</div>
					<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
						<div style="margin-top: 15px;">
							<button type="submit" class="wd-25 btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="removePublishedProfile">
									<spring:message code="application.yes" />
							</button>
							<button type="button" class="wd-25 btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" id="cancel">
								<spring:message code="application.no" />
							</button>
						</div>
					</div>
				</div>
			</div>
</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>

	$.validate({ lang : 'en',
	modules : 'date, security' });

$.formUtils.addValidator({
    name: 'min_ind_cat',
    validatorFunction: function (value, $el, config, language, $form) {
		var response = true;
		var minVal = Number($("#minCategories").val());
		var maxValue = Number($("#maxCategories").val());
		if (minVal>maxValue) {
            response = false;
        }
        return response;
    },
    errorMessage: 'Input value must be lower than the value in Maximum Number of Categories',
    errorMessageKey: 'minIndCatId'
});

$.formUtils.addValidator({
    name: 'max_ind_cat',
    validatorFunction: function (value, $el, config, language, $form) {
		var response = true;
		var minVal = Number($("#minCategories").val());
		var maxValue = Number($("#maxCategories").val());
        if (minVal > maxValue) {
            response = false;
        }
        return response;
    },
    errorMessage: 'Input value must be greater than the value in Minimum Number of Categories',
    errorMessageKey: 'maxIndCatId'
});

$.formUtils.addValidator({
    name: 'greater_than_zero',
    validatorFunction: function (value, $el, config, language, $form) {
		var response = true;
		var minVal = Number($("#minCategories").val());
		if (1>minVal) {
            response = false;
        }
        return response;
    },
    errorMessage: 'Input value must be greater than 0',
    errorMessageKey: 'minIndCatId'
});

$.formUtils.addValidator({
	  name : 'requiredSupplierForm',
	  validatorFunction : function(value, $el, config, language, $form) {		  
		  var response = true;
		  var labelVal = $('#chosenSupplierForm').val();
		  if((labelVal == '' || labelVal.length == 0) && $('#isEnablePrequalFormId').is(':checked') ){
			  response = false;
		  }
		  return response;
	  },
	  errorMessage : 'This is a required field',
	  errorMessageKey: 'requiredSupplierFormCheck'
	});
</script>
<script type="text/javascript " src="<c:url value="/resources/js/view/buyer.js?1"/>"></script>
<script type="text/javascript " src="<c:url value="/resources/js/jquery.copy-to-clipboard.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript " src="<c:url value="/resources/js/view/buyerProfileMaintenance.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idMobileNumber').mask('+00 00000000000', { placeholder : "e.g. +60 122735465" });
		$('#idCompanyContactNumber').mask('+00 00000000000', { placeholder : "e.g. +60 122735465" });
		
		 $('#publicEventsId').click(function(e){
			e.preventDefault();
			var url = $(this).text(); 
		    window.open(url, "_blank"); 
		    /* var tenantId  = url.substring(url.lastIndexOf('/') + 1);
			alert (tenantId);
		    window.open(url, " getContextPath() + '/publicEvents' + '/tenantId' ");	 */ 
		    }); 
		 
		 $(document).ready(function() {
				$("#image-holder").on('click', '.thumb-image', function() {
					$(this).toggleClass("selectedItem");
				});

				$("#selectedItem").on("click", function() {
					$(".selectedItem").remove();
	});

				$("#logoImg").on('change', function() {
					if (typeof (FileReader) == null) {
						var image_holder = document.getElementById("logoImageHolder").src;
						console.log("=====");
						image_holder.attr('src', getContextPath() + '/resources/images/logo-image.png');
					} else if (typeof (FileReader) != "undefined") {
						$("#removeFile").val(false);
						var image_holder = $("#logoImageHolder");
						image_holder.empty();
						var reader = new FileReader();
						reader.onload = function(e) {
							image_holder.attr('src', e.target.result);
						}
						//console.log("=====");
						image_holder.show();
						reader.readAsDataURL($(this)[0].files[0]);
					} else {
						//alert("This browser does not support FileReader.");
					}
				});

				
				$("#isEnablePrequalFormId").on('change', function() {
					if($('#isEnablePrequalFormId').is(':checked')) {
						$("#enableSupplierForm").removeClass("disabled");
						$("#chosenSupplierForm").val('').trigger("chosen:updated");
					}else{
						$("#enableSupplierForm").addClass("disabled");
						$("#chosenSupplierForm").validate();
					}
				});
				
				$("#removeLogo").click(function() {
					$("#logoImg").val("");
					$("#removeFile").val(true);
					$('#logoImageHolder').attr('src', getContextPath() + '/resources/images/logo-image.png')
				});
			});
		 
	});
</script>
<style>
.recf_bttn1 {
	display: flex;
}

.marg_left_can {
	margin-left: 2%;
}

.marg_url {
	margin-top: 2.4%;
}

.common_mar {
	margin-bottom: 6%;
}

.profile_logo img {
	width: 200px;
	height: 100px;
}

@media screen and (max-width: 768px) {
	.marg_url {
		word-wrap: break-word;
	}
	.recf_bttn1 {
		display: block;
	}
	.marg_left_can {
		margin-top: 2%;
		margin-left: 0px;
	}
}

.tab-section
{
	font-size: 15px;
    color: #414141;
    font-family: "open_sansregular";
    border: 1px solid #55c2f1;
    margin-right: 10px;
    height: 35px;
    justify-content: center;
    display: flex;
    align-items: center;
	font-weight: bold;
	cursor: pointer;
}

.tab-section-inactive
{
	background: gray;
}

.wd-25
{
	width: 25%;
}

.disable-input
{
	background: #f2f2f2 none repeat scroll 0 0;cursor: not-allowed;
}

input:focus::-webkit-input-placeholder { color:black; }
input:focus:-moz-placeholder { color:black; } / FF 4-18 /
input:focus::-moz-placeholder { color:black; } / FF 19+ /
input:focus:-ms-input-placeholder { color:black; } / IE 10+ /
</style>
