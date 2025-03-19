<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="(hasRole('PASSWORD_SETTINGS') or hasRole('ADMIN')) and hasRole('BUYER')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<script>
zE(function() { zE.setHelpCenterSuggestions({ labels: [${prTemplateDesk}] }); });
</script>
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li>
				<a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/buyer/prTemplateList" var="createUrl" />
			<li class="active">Password Setting</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">Password Setting Administration</h2>
		</div>
		<form:form method="post" id="passwordSettingform" action="passwordSetting" modelAttribute="passwordSetting">
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="meeting2-heading">
					<h3>Password Rules</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
					<div class="margin-top-15">
						<div class="row marg-bottom-20 marg_left_0">
							<div class="col-md-3">
								<label path="passwordExpiryInDays" cssClass="marg-top-10 control-label">
									<spring:message code="passwordExpiryInDays" />
								</label>
							</div>
							<div class="col-md-5">
								<form:input path="passwordExpiryInDays" maxlength="3" data-validation="required number validate_zero" cssClass="form-control" placeholder="Enter Password Expiration period" />
							</div>
							<div class="col-md-4 d-flex">
								<form:checkbox path="enableExpiration" class="custom-checkbox autoDisabel" label="Enable password expiration" />
							</div>
						</div>
						<div class="row marg-bottom-20 marg_left_0">
							<div class="col-md-3">
								<label path="numberOfPasswordRemember" cssClass="marg-top-10 control-label">
									<spring:message code="password.reuse.history.limit" />
								</label>
							</div>
							<div class="col-md-5">
								<form:input path="numberOfPasswordRemember" id="numberOfPasswordRemember" maxlength="5" data-validation="required number validate_zero" cssClass="form-control" placeholder="Enter Password reuse history limit" />
							</div>
							<div class="col-md-4 d-flex">
								<form:checkbox path="enableReusePassword" class="custom-checkbox autoUpdate" label="Do not allow to reuse password" />
							</div>
						</div>
						<div class="row marg-bottom-20 marg_left_0">
							<div class="col-md-3">
								<label path="failedAttempts" cssClass="marg-top-10 control-label">
									<spring:message code="failedAttempts" />
								</label>
							</div>
							<div class="col-md-5">
								<form:input path="failedAttempts" id="failedAttempts" maxlength="3" data-validation="required  number validate_len" cssClass="form-control" placeholder="Enter Number of failed attempt allowed" />
							</div>
							<div class="col-md-4 d-flex">
								<form:checkbox path="enableFailedAttempts" class="custom-checkbox autoDis" label="Enable lock account after failed logins" />
							</div>
						</div>
					</div>
				</div>
			</div>	
				<div class="Invited-Supplier-List import-supplier white-bg">
					<div class="meeting2-heading">
						<h3>Password Complexity</h3>
					</div>
					<div class="import-supplier-inner-first-new pad_all_15 global-list">
						<div class="margin-top-15">
							<div class="row marg-bottom-20 marg_left_0">
								<div class="col-md-3">
									<label path="passwordLength" cssClass="marg-top-10 control-label">
										<spring:message code="passwordLength" />
									</label>
								</div>
								<div class="col-md-5">
									<form:input path="passwordLength" id="pwdLength" maxlength="2" data-validation="required  number validate_reverse " cssClass="form-control" placeholder="Enter Password length" />
									<span></span>
								</div>
							</div>
							<div class="form_field">
								<label class="col-sm-4 col-md-3 col-xs-6 control-label">Requires at least one lower case letter</label>
								<div class="col-sm-1 col-md-1 col-xs-1 ">
									<form:checkbox path="containOneLowerCaseLetters" id="oneLowerCaseLetter" class="custom-checkbox " data-validation="validate_checkminimum" data-validation-error-msg-container="#pwd-err-msg" />
								</div>
							</div>
							<div class="form_field">
								<label class="col-sm-4 col-md-3 col-xs-6 control-label">Requires at least one upper case letter</label>
								<div class="col-sm-1 col-md-1 col-xs-1 ">
									<form:checkbox path="containOneUpperCaseLetters" id="oneUpperCaseLetters" class="custom-checkbox " data-validation="validate_checkminimum" data-validation-error-msg-container="#pwd-err-msg" />
								</div>
							</div>
							<div class="form_field">
								<label class="col-sm-4 col-md-3 col-xs-6 control-label">Password should contain special character</label>
								<div class="col-sm-1 col-md-1 col-xs-1 ">
									<form:checkbox path="containNonAlphanumeric" id="containNonAlphanumericS" class="custom-checkbox " data-validation="validate_checkminimum" data-validation-error-msg-container="#pwd-err-msg" />
								</div>
							</div>
							<div class="form_field">
								<label class="col-sm-4 col-md-3 col-xs-6 control-label">Password should contain at least one number</label>
								<div class="col-sm-1 col-md-1 col-xs-1 ">
									<form:checkbox path="containOneNumbers" id="containOneNumber" class="custom-checkbox " data-validation="validate_checkminimum" data-validation-error-msg-container="#pwd-err-msg" />
								</div>
							</div>
						</div>
						<div id="pwd-err-msg"></div>
						<div class="row marg-bottom-20 marg_left_0">
							<div class="col-md-4 mr_25">
								<c:if test="${canEdit and !buyerReadOnlyAdmin}">
									<form:button type="submit" id="submitSettingForm" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">Update</form:button>
								</c:if>
							</div>
							<div class="col-md-4">
								<sec:authorize access="hasRole('FINANCE')">
									<a href="${pageContext.request.contextPath}/finance/financeDashboard" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">Cancel</a>
								</sec:authorize>
								<sec:authorize access="hasRole('SUPPLIER')">
									<a href="${pageContext.request.contextPath}/supplier/supplierDashboard" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">Cancel</a>
								</sec:authorize>
								<sec:authorize access="hasRole('BUYER')">
									<a href="${pageContext.request.contextPath}/buyer/buyerDashboard" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">Cancel</a>
								</sec:authorize>
								<sec:authorize access="hasRole('OWNER')">
									<a href="${pageContext.request.contextPath}/owner/ownerDashboard" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">Cancel</a>
								</sec:authorize>
							</div>
						</div>
					</div>
				</div>
		</form:form>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript">
$( document ).ready(function() {
	var enabelExp=$('.autoDisabel').val().toString();
	if(enabelExp=='true'){
		$('#passwordExpiryInDays').prop('disabled', false);
	}else{
	
		$('#passwordExpiryInDays').prop('disabled', true);
	}
    $('#enableExpiration1').click(function(){
    	
    	$('.form-error').hide();
        if($(this).is(":checked")){
		$('#passwordExpiryInDays').prop('disabled', false);
        }else{
        	$('#passwordExpiryInDays').val('');
        	$('#passwordExpiryInDays').prop('disabled', true);
        }
        
    });
    if($('.autoDisabel').is(":checked")){
		$('#passwordExpiryInDays').prop('disabled', false);
        }else{
        	$('#passwordExpiryInDays').prop('disabled', true);
        }

   	 
    
    
	var enabelExp=$('.autoDis').val().toString();
	if(enabelExp=='true'){
		$('#failedAttempts').prop('disabled', false);
	}else{
		$('#failedAttempts').prop('disabled', true);
	}
    $('#enableFailedAttempts1').click(function(){
    	$('.form-error').hide();
        if($(this).is(":checked")){
		$('#failedAttempts').prop('disabled', false);
        }else{
        	$('#failedAttempts').val('');
        	$('#failedAttempts').prop('disabled', true);
        }	
    });
    if($('.autoDis').is(":checked")){
		$('#failedAttempts').prop('disabled', false);
        }else{
        	$('#failedAttempts').prop('disabled', true);
        }
    
    
    var enabelPasswordReuse=$('.autoUpdate').val().toString();
	
	if(enabelPasswordReuse =='true'){
		$('#numberOfPasswordRemember').prop('disabled', false);
	}else{
		$('#numberOfPasswordRemember').prop('disabled', true);
	}
    $('#enableReusePassword1').click(function(){
    	$('.form-error').hide();
        if($(this).is(":checked")){
        	$('#numberOfPasswordRemember').prop('disabled', false);
        }else{
        	$('#numberOfPasswordRemember').val('');
        	$('#numberOfPasswordRemember').prop('disabled', true);
        }	
    });
    
    if($('.autoUpdate').is(":checked")){
		$('#numberOfPasswordRemember').prop('disabled', false);
    }else{
       	$('#numberOfPasswordRemember').prop('disabled', true);
    }
    
    
    
	$.formUtils.addValidator({
		name : 'validate_reverse',
		validatorFunction : function(value, $el, config, language, $form) {
			if (isNaN($("#pwdLength").val().replace(/,/g, '')) || isNaN($("#pwdLength").val().replace(/,/g, '')) || $("#pwdLength").val() == '' || $("#pwdLength").val() == '') {
				return false;
			}

			var stp = parseFloat($("#pwdLength").val().replace(/,/g, ''));
			if (stp < 8 || stp >15 ) {
				return false;
			}
		},
		errorMessage : 'Value must be between  8 and 15.',
		errorMessageKey : 'lte'
	});

	$.formUtils.addValidator({
		name : 'validate_zero',
		validatorFunction : function(value, $el, config, language, $form) {
			if (isNaN($("#pwdLength").val().replace(/,/g, '')) || isNaN($("#pwdLength").val().replace(/,/g, '')) || $("#pwdLength").val() == '' || $("#pwdLength").val() == '') {
				return false;
			}

			var stp = parseFloat($("#failedAttempts").val().replace(/,/g, ''));
			if (stp == 0) {
				return false;
			}
			
			var stp = parseFloat($("#numberOfPasswordRemember").val().replace(/,/g, '')); 
			if (stp == 0) {
				return false;
			}
			
			var stp = parseFloat($("#passwordExpiryInDays").val().replace(/,/g, ''));
			if (stp == 0) {
				return false;
			}
		},
		errorMessage : 'Value cannot be zero',
		errorMessageKey : 'lte'
	});

	$.formUtils.addValidator({
		name : 'validate_len',
		validatorFunction : function(value, $el, config, language, $form) {
			if (isNaN($("#failedAttempts").val().replace(/,/g, '')) || isNaN($("#failedAttempts").val().replace(/,/g, '')) || $("#failedAttempts").val() == '' || $("#failedAttempts").val() == '') {
				return false;
			}

			var stp = parseFloat($("#failedAttempts").val().replace(/,/g, ''));
			if (stp < 3 || stp >10 ) {
				return false;
			}
		},
		errorMessage : 'Value must be between  3 and 10.',
		errorMessageKey : 'lte'
	});
      
	
	$.formUtils.addValidator({
		
		name : 'validate_checkminimum',
		
		validatorFunction : function(value, $el, config, language, $form) {
			var i=0;
			 if($('#oneLowerCaseLetter').prop("checked") == true){
				   i++;
			   }
			   if($('#oneUpperCaseLetters').prop("checked") == true){
				   i++;
			   }
			   if($('#containNonAlphanumericS').prop("checked") == true){
				   i++;
			   }
			   if($('#containOneNumber').prop("checked") == true){
				   i++;
			   }
			   console.log(i);
			   if(i<2){
				   return false;
			   }
		},
		errorMessage : 'Please select minimum two required password rules',
		errorMessageKey : 'lte'
		
});

	
/* 	$("#submitSettingForm").click(function(){
		var i=0;
		
		  
			console.log('-------------------------->'+i++);
		
	  $('#passwordSettingform').submit();
	  
	  
	}) */
	
	});

</script>
<style>
.d-flex {
	display: flex;
}

.mr_25 {
	margin-left: 25%;
}

.margin-top-15 {
	margin-top: 1%;
}
</style>
<script>
$.validate({
		lang : 'en'
		
	});
	



</script>
