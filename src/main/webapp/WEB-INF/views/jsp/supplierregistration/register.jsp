<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:message var="supplierPlanDesk" code="application.owner.supplier.subscription.plan" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere-public.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/chosen/chosen.css"/>">


<div class="container margin-bottom-5">
	<style>
	.buyer-check-section p:nth-child(1) {
		margin: auto !important;
	}
	
	.chosen-search-input {
		width: 150px !important;
	}
	
	.chosen-container-multi .chosen-choices li.search-field .default {
	  color: #999 !important;
	}
	
	.chosen-container {
		border: 0px !important;
	}
	
	.search-choice {
		border: 1px solid #555 !important;
	}
	
	</style>
	
	<div style="width: 80%;margin: 0 auto;margin-top: 25px;">
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	</div>
	
	<form:form action="register" id="registorForm" method="post" modelAttribute="supplier" autocomplete="off">
		<div class="buyer-check-section" style="margin-bottom:10px !important;">
			<p class="font-27">Fill in your details to start your registration process</p>
		</div>
		<div class="container-fluid">
			<div class="col-md-offset-3 col-sm-12 col-md-12 col-lg-6 col-xs-12 buyer-purchase-details">

				<div class="form-group">
					<label>Company Name</label>
					<!-- <input type="text" class="form-control" id="" name="companyname"> -->
					<form:input path="companyName" cssClass="form-control" id="idCompanyName" autocomplete="off" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric " data-validation-allowing="-_ &.()" data-validation-length="4-124" />
				</div>
				<div class="form-group">
					<label><spring:message code="supplier.registration.company.number" /></label>
					<form:input path="companyRegistrationNumber" cssClass="form-control" id="idCompRegNum" autocomplete="off" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric " data-validation-allowing="-_ " data-validation-length="2-124" />
				</div>
				 <div class="form-group">
					<label>Country of Registration</label>
					<form:select path="registrationOfCountry" id="idRegCountry" cssClass="form-control" data-validation="required ">
						<form:option value="">Select Country of Registration</form:option>
						<form:options items="${countryList}" itemValue="id" itemLabel="countryName" />
					</form:select>
				</div> 

				<div class="form-group">
					<label>Login Email</label>
					<form:input path="loginEmail" cssClass="form-control" id="idLoginEmail" autocomplete="off" placeholder="This email cannot be changed later" data-validation="required length email " data-validation-length="max124" />
				</div>

				<div class="form-group">
					<label>Password</label>
					<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
					<input type="text" style="display:none;">
					<form:password path="password" cssClass="form-control pwd" autocomplete="new-password" readonly="readonly" onfocus="this.removeAttribute('readonly');" id="exampleInputPass1" placeholder="Minimum 8 characters with one number, one capital letter" data-validation="custom" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg="${!empty regex.message? regex.message :''}" />
					<p class="pass-desc" id="passwordPlaceHolder">${!empty regex.message?regex.message:''}</p>
				</div>

				<div class="form-group">
					<label>Name</label>
					<form:input path="fullName" cssClass="form-control" autocomplete="off" id="idFullName" placeholder="User Name" data-validation="required length alphanumeric" data-validation-allowing="-_ ." data-validation-length="2-128" />
				</div>
 				<div class="form-group">
					<label>Contact Number</label>
					<form:input path="companyContactNumber" cssClass="form-control" autocomplete="off" id="idCompanyContactNumber" placeholder="+60345678906" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
				</div>
 				<div class="form-group">
					<label>Select Categories</label>
					<span class="dropUp">
					<form:select autocomplete="off" data-validation="required" path="industryCategories" cssClass="chosen-select"  multiple="multiple" data-placeholder="Select Categories">
						<c:forEach items="${icList}" var="ic" >
							<c:if test="${ic.id == '-1' }">
								<form:option value="-1" label="${ic.name}" disabled="true" />
							</c:if>
							<c:if test="${ic.id != '-1' }">
								<form:option value="${ic.id}" label="${ic.name}" />
							</c:if>
						</c:forEach>						
					</form:select>
					</span>

					<p class="ts-pera">
						Select the categories that are related to the services you provide
						<span> 
							<a target="_blank" href="<c:url value="/resources/westportsCategory.pdf"/>">Click here to view Category</a>
						</span>
					</p>
					
				</div>
				 

				<div class="form-group tc-style06">
					<span>
						<input type="checkbox" name="agree" id="agree" value="agree">&nbsp;I have read and agree to Westports <a target="_blank" href="<c:url value="/resources/westportstermsandcondition.pdf"/>">Terms and Conditions & Anti-Bribery policy.</a>
					</span>
				</div>

				<div id="">
					<form:button type="submit" disabled="true" id="agreeSubmit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width">Register as Supplier</form:button>
 				</div>
				
			</div>
			<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div>
		</div>
	</form:form>
</div>


<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
 <script type="text/javascript">
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});

 </script>
 
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});

</script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
  
		
		$("#agree").click(function() {
				if ($('#agree').is(":checked")) {
					$("#agreeSubmit").attr("disabled", false);
				} else {
					$("#agreeSubmit").attr("disabled", true);
				}
			});
		
		$(".pwd").click(function() {
			$('#passwordPlaceHolder').hide();
		});
 
		$('#idCompanyContactNumber').mask('+00 00000000000', {
			placeholder : "+60 322761533"
		});
	});
	
  
	
 </script>

