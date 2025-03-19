<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message var="ownerSettingsDesk" code="application.owner.settings" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${ownerSettingsDesk}] });
});
</script>
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}
</style>
<div id="page-content" view-name="ownerSettings">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb">
			<li>
				<a href="${pageContext.request.contextPath}/owner/ownerDashboard">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<li class="active">
				<%-- <c:out  value='${btnValue}'/>  --%>
				<spring:message code="label.ownersettings" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="ownersettings.administration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<spring:message code="label.ownersettings" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="ownerSettingsForm" data-parsley-validate="" cssClass="form-horizontal bordered-row" modelAttribute="ownerSettings" method="post" action="ownerSettings">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<form:hidden path="id" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="timeZone" for="idTimeZone" class="marg-top-10">
								<spring:message code="label.timezone" />
							</form:label>
						</div>
						<div class="col-md-5">
							<div class="input_wrapper form-group">
								<spring:message code="timezone.required" var="required" />
								<form:select path="timeZone" id="idTimeZone" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
									<form:option value="">
										<spring:message code="buyersettings.selecttimezone" />
									</form:option>
									<form:options items="${timeZone}" itemValue="id"></form:options>
								</form:select>
								<form:errors path="timeZone" cssClass="error" />
							</div>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<label class="col-md-3"> Supplier approval notifications email </label>
						<div class="col-md-5">
							<div class="input_wrapper form-group">
								<spring:message code="eventdetails.event.contact.place.email" var="email" />
								<form:input type="text" placeholder="${email}" path="supplierSignupNotificationEmailAccount" data-validation="required length email" data-validation-length="max150" class="form-control mar-b10 feature_box" />
							</div>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<label class="col-md-3"> Buyer Subscription Notifications Email </label>
						<div class="col-md-5">
							<div class="input_wrapper form-group">
								<spring:message code="eventdetails.event.contact.place.email" var="email" />
								<form:input type="text" placeholder="${email}" path="buyerSubscriptionNotificationEmail" data-validation="required length email" data-validation-length="max150" class="form-control mar-b10 feature_box" />
							</div>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<label class="col-md-3"> <spring:message code="application.filesize" />
						</label>
						<div class=" col-md-5">
							<div class="fSizein form-group">
								<div class="chosen-container chosen-container-single chosen-single">
									<span></span>
									<label class="infoSticker"> MB </label>
									<spring:message code="ownerSettings.filesize" var="filesize" />
									<form:input type="text" placeholder="${filesize}" path="fileSizeLimit" data-validation="length required number" data-validation-length="max6" class="chosen-container-single form-control mar-b10 feature_box" />
								</div>
							</div>
						</div>
					</div>
					<div class="row marg-bottom-10 indusCatgryToHideOnGlobal">
						<label class="col-md-3"> <spring:message code="application.filetype" />
						</label>
						<div class="col-md-5 marg-bottom-10">
							<div class="input_wrapper form-group">
								<form:input type="text" path="fileTypes" class="form-control mar-b10 feature_box" />
							</div>
							<form:errors path="fileTypes" cssClass="error" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<label class="col-md-3"> Supplier Charge Start Date </label>
						<div class="col-md-4">
							<div class="input_wrapper form-group">
								<form:input path="supplierChargeStartDate" readonly="readonly" class="nvclick form-control for-clander-view" data-validation="date" data-validation-optional="true" data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy" />
							</div>
						</div>
						<div class="col-md-2">
							<button id="resetTaxButton" class="btn btn-sm btn-black btn-tooltip" title="Reset Date" data-placement="top" data-toggle="tooltip">
								<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
							</button>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<label class="col-md-3"> Buyer Subscription Expiry Reminder </label>
						<div class=" col-md-5">
							<div class="fSizein form-group">
								<div class="chosen-container chosen-container-single chosen-single">
									<span></span>
									<label class="infoSticker" style="width:25%"> Days&nbsp;(Before) </label>
									<form:input type="text" placeholder="Enter the days to remind before Expiry" path="buyerSubsExpiryReminder" data-validation="custom" data-validation-regexp="^[1-9]$|^[1-2][0-9]$|^3[0]$"
										data-validation-error-msg="Please enter a number between 1 and 30." class="chosen-container-single form-control mar-b10 feature_box" />
								</div>
							</div>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<label class="col-md-3" style="padding-left: 6px;"> Supplier Subscription Expiry Reminder </label>
						<div class=" col-md-5">
							<div class="fSizein form-group">
								<div class="chosen-container chosen-container-single chosen-single">
									<span></span>
									<label class="infoSticker" style="width:25%"> Days&nbsp;(Before)</label>
									<form:input type="text" placeholder="Enter the days to remind before Expiry" path="supplierSubsExpiryReminder" data-validation="custom" data-validation-regexp="^[1-9]$|^[1-2][0-9]$|^3[0]$"
										data-validation-error-msg="Please enter a number between 1 and 30." class="chosen-container-single form-control mar-b10 feature_box" />
								</div>
							</div>
						</div>
					</div>

					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-8 dd sky mar_b_15">
							<form:button type="submit" id="saveOwnerSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">
								<spring:message code="application.update" />
							</form:button>
							<c:url value="/owner/ownerDashboard" var="ownerDashboard" />
							<a href="${ownerDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">
								<spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/bootstrap-tagsinput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap-tagsinput.css"/>">
<script>
	$(document).ready(function() {
		$.validate({
			lang : 'en'
		});
		$("#fileTypes").tagsinput('items');
		$('#idtimeZone').mask('GMT+00:00', {
			placeholder : "<spring:message code="timezone.placeholder"/>"
		});
		$('#fileTypes').keypress(function(e) {
			if (e.which == 13) {

				e.preventDefault();
				return false; //<---- Add this line
			}
		});

		$('#ownerSettingsForm .bootstrap-tagsinput').on('keyup keypress', function(e) {
			console.log(e.keyCode + " " + e.which);
			var keyCode = e.keyCode || e.which;
			if (keyCode === 13 || keyCode === 46 || keyCode === 190) {
				e.preventDefault();
				return false;
			}

		});

		/* Datepicker bootstrap */

		$(function() {
			"use strict";

			$('#supplierChargeStartDate').bsdatepicker({
				format : 'dd/mm/yyyy',
				onRender : function(date) {
					if (date.valueOf() < $.now()) {
						return 'disabled';
					}
				}

			}).on('changeDate', function(e) {
				$(this).blur();
				$(this).bsdatepicker('hide');
			});

			$.validate({
				lang : 'en'
			});

		});
		$('#resetTaxButton').on('click', function(e) {
			e.preventDefault();
			$('#supplierChargeStartDate').val('');
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
_
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>
<style>
.fSizein  .chosen-container-single .chosen-single {
	padding: 0 !important
}

.infoSticker {
	background: #eff4f6 none repeat scroll 0 0;
	color: #2b2f33;
	display: block;
	height: 38px;
	line-height: 38px;
	margin-top: -19px;
	position: absolute;
	right: 0;
	text-align: center;
	top: 50%;
	width: 38px;
	z-index: 4;
}

.fSizein  .has-error .infoSticker {
	margin-top: -32px;
	top: 55%;
	right: 1px;
	height: 33px;
	line-height: 38px;
}

.fSizein .chosen-container.chosen-container-single.chosen-single.has-error
	{
	border: medium none;
}
</style>