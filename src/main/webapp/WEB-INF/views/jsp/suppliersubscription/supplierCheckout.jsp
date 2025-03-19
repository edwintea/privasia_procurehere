<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:message var="supplierPlanDesk" code="application.owner.supplier.subscription.plan" />
<!-- <script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierPlanDesk}] });
});
</script> -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bankmy.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/utils.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere-public.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js?1"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">

<div class="container margin-bottom-5">
	<div id="loading" class="opacity-60">
		<div class="spinner">
			<div class="bounce1"></div>
			<div class="bounce2"></div>
			<div class="bounce3"></div>
		</div>
	</div>
	
	<div style="width: 80%;margin: 0 auto;margin-top: 25px;">
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	</div>
	<c:url var="doInitiateSupplierPayment" value="doInitiateSupplierPayment" />
	<form:form action="${doInitiateSupplierPayment}" id="supplierCheckoutForm" method="post" modelAttribute="supplier" autocomplete="off">
		<input type="hidden"  id="stripePublishKey" value="${publicKey}">
		<div class="buyer-check-section">
			<p class="font-27">Fill in your details to start your subscription process</p>
			<p>&nbsp;</p>			
		</div>
		<div class="row">
			<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0 margin-right-3"></div>
			<div class="col-sm-12 col-md-12 col-lg-4 col-xs-12 buyer-purchase-details">

				<div class="form-group">
					<label>Company Name</label>
					<!-- <input type="text" class="form-control" id="" name="companyname"> -->
					<form:input path="companyName" cssClass="form-control" id="idCompanyName" placeholder="Once submitted, cannot be changed" 
					data-validation="required length alphanumeric company_name" 
					data-validation-allowing=",-_ &.()\/" 
					data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_ &.()\/ and spaces"
					data-validation-length="4-124" />
				</div>
				<div class="form-group">
					<label><spring:message code="supplier.registration.company.number" /></label>
					<form:input path="companyRegistrationNumber" cssClass="form-control" id="idCompRegNum" placeholder="Once submitted, cannot be changed" 
					data-validation="required length alphanumeric crn_number" 
					data-validation-allowing=" &.,/()_-" 
					data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_&.()/ and spaces"
					data-validation-length="2-124" />
				</div>
				 <div class="form-group">
					<label>Country of Registration</label>
					<form:select path="registrationOfCountry" id="idRegCountry" cssClass="form-control" data-validation="required crn_number company_name">
						<form:option value="">Select Country of Registration</form:option>
						<form:options items="${countryList}" itemValue="id" itemLabel="countryName" />
					</form:select>
				</div> 
				<div class="form-group">
					<label>Full Name</label>
					<form:input path="fullName" cssClass="form-control" id="idFullName" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric" data-validation-allowing="-_ ." data-validation-length="2-128" />
				</div>
				<div class="form-group">
					<label><spring:message code="supplier.designation" /></label>
					<form:input path="designation" cssClass="form-control" id="idDesignation" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric" data-validation-allowing="- " data-validation-length="2-128" />
				</div>
				<div class="form-group">
					<label>Login Email</label>
					<form:input path="loginEmail" cssClass="form-control" id="idLoginEmail" placeholder="This email cannot be changed later" data-validation="required length email login_email" data-validation-length="max124" />
				</div>

				<div class="form-group">
					<label>Communication Email</label>
					<form:input path="communicationEmail" class="form-control" id="idCommunicationEmail" placeholder="Email will be sent to this address" data-validation="required length email" data-validation-length="max124" />
				</div>

				<div class="form-group">
					<label>Admin Mobile Number</label>
					<form:input path="mobileNumber" cssClass="form-control" id="idAdminMobileNo" placeholder="Country code and number e.g. +6017666666" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
				</div>
				<div class="form-group">
					<label>Contact Number</label>
					<form:input path="companyContactNumber" cssClass="form-control" id="idCompanyContactNumber" placeholder="+60345678906" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
				</div>
				<div class="form-group">
					<label>Password</label>
					<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
					<form:password path="password" cssClass="form-control pwd" id="exampleInputPass1" placeholder="Minimum 8 characters with one number, one capital letter" data-validation="custom" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg="${!empty regex.message? regex.message :''}" />
					<p class="pass-desc" id="passwordPlaceHolder">${!empty regex.message?regex.message:''}</p>
				</div>
				<!--   <div class="form-group">
          <div id="g-recaptcha" data-sitekey="6LdG1iUTAAAAAJB9POQXAHttIeHra0NuqA1NwzBg"><div style="width: 304px; height: 78px;"><div><iframe src="https://www.google.com/recaptcha/api2/anchor?ar=1&amp;k=6LezmB8TAAAAAFfN-D8nEnnuGpPohFZn4JKUbdNg&amp;co=aHR0cHM6Ly9hcHAucHJvY3VyZWhlcmUuY29tOjQ0Mw..&amp;hl=en&amp;v=v1526338122299&amp;theme=light&amp;size=normal&amp;cb=r6ek5xxe09ir" width="304" height="78" role="presentation" frameborder="0" scrolling="no" sandbox="allow-forms allow-popups allow-same-origin allow-scripts allow-top-navigation allow-modals allow-popups-to-escape-sandbox"></iframe></div><textarea id="g-recaptcha-response" name="g-recaptcha-response" class="g-recaptcha-response" style="width: 250px; height: 40px; border: 1px solid #c1c1c1; margin: 10px 25px; padding: 0px; resize: none;  display: none; "></textarea></div></div>
          <input id="recaptchaResponse" name="recaptchaResponse" type="hidden" value="" class="form-control">


        </div> -->
				<p class="ts-pera">
					*By proceeding, you agree to our <span> <a target="_blank" href="<c:url value="/resources/termsandcondition.pdf"/>">terms & conditions</a>
					</span>
				</p>

				<div id="paypalPayment">
					<!-- <span class="rfr_field proceedPayment" id="supplierCheckOutBtn"></span> -->
					<form:button type="button" value="Pay" id="makeSupplierStripePayment"
						class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width">Pay</form:button>
					<div>
						<img src="<c:url value="/resources/images/public/Visa.png"/>" alt="Visa"> <img src="<c:url value="/resources/images/public/mastercard-badge.png"/>" alt="Mastercard"> <img src="<c:url value="/resources/images/public/american-express.png"/>" alt="American Express"> <img src="<c:url value="/resources/images/public/discover.png"/>" alt="visa-badge"> <br> 
						<img src="<c:url value="/resources/images/public/McAfee.png"/>" alt="McAfee">
					</div>
				</div>
			</div>
			<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div>
			<div class="col-sm-12 col-md-12 col-lg-4 col-xs-12 pack-amount-details">
				<input type="hidden" name="promoCodeId" id="promoCodeId">
				<input type="hidden" id="tax" value="${plan.tax}">
				<h4 class="border-btm toptitle" id="toptitle">UNLIMITED BUYER</h4>
				<div class="pack-details">
					<p class="sub-heading">Supplier Subscription</p>
					<form>
						<input type="hidden" name="supplierPrice" id="supplierPrice" value="0.00">
						<!-- <div class="radio">
							<div class="pack-details">
								<label class="width-label-100"> <input type="radio" name="supplerPlan"  value="SINGLEBUYER" checked="checked"> <span class="left-span">Single
										Buyer Plan</span> <span class="pull-right width-label-100-span-2">
										0.00
								</span>
								</label>
								<p class="usd-details">US$30 per annum</p>
							</div>
						</div> -->
						<div class="radio">
							<div class="pack-details">
								<label class="width-label-100"> <input type="radio" name="supplerPlan" value="ALLBUYER" checked="checked"> <span class="left-span">Unlimited Buyer Plan</span> <span class="pull-right width-label-100-span-2" id="amount_span">
								</span>
								</label>
								<p class="usd-details">US$${plan.price} per annum</p>
								<p class="usd-details">( Inclusive of <span id="tax_span" style="color: #979797 !important;font-weight: 500;"></span>% SST )</p>
							</div>
						</div>
					</form>
				</div>
				<div class="pack-details">
					<div class="form-inline">
						<label class="margin-right-20">Promo Code</label> <input type="text" name="promocode" id="promocode" class="form-control"> <span class="pull-right " id="promoDiscountPrice"> <input type="hidden" name="promoDiscountPrice" value="0.00"> 0.00
						</span> <span id="promoError"></span>
					</div>
				</div>
				<h4 class="border-btm"></h4>
				<div>
					<h4 class="pull-left">TOTAL FEE</h4>
					<h4 class="pull-right" id="totalPrice">
						<input type="hidden" name="totalPrice" id="totalPriceInput" value="0" /> US$ 0.00
					</h4>
				</div>
			</div>
			<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div>
		</div>
	</form:form>
</div>

<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="" payment-amount=""
	style="background: #0000004f;">
	<div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
		<!-- Modal content-->
		<div class="modal-content">
			<!-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> -->
			<div class="modal-body">
				<button class="close for-absulate" id="makePaymentModalCloseId" type="button"
					data-dismiss="modal">×</button>
				<div class="row">
					<div class="col-md-12">
						<h3 class="title-hero text-center" class="header-section">
							Choose Payment Mode
						</h3>
						<div class="example-box-wrapper">
							<ul class="list-group list-group-separator row list-group-icons place-center">
								<li class="col-md-5">
									<a href="#tab-example-1" id="tabOneId" data-toggle="tab" class="list-group-item">
										<i class="glyphicon glyphicon-credit-card" style="top: 2px;"></i>&nbsp;&nbsp;
										<spring:message code="payment.mode.card" />
									</a>
								</li>
								<c:if test="${plan.currency.currencyCode == 'MYR'}">
									<li class="col-md-5">
										<a href="#tab-example-2" id="tabTwoId" data-toggle="tab" class="list-group-item">
											<div class="icon icon-fpx"></div>&nbsp;&nbsp;
											<spring:message code="payment.mode.fpx" />
										</a>
									</li>
								</c:if>
							</ul>
							<div class="tab-content">
								<div class="panel">
									<div class="panel-body">
										<div class="tab-pane fade" id="tab-example-1">
											<div class="row col-12 place-center">
												<img src="${pageContext.request.contextPath}/resources/assets/images/cards-logo.jpeg"
													class="payment-images-card">
											</div>
											<div id="cardBlock">
												<div class="sr-root">
													<div class="sr-main">
														<form id="payment-form" class="sr-payment-form">
															<div
																class="margin-top-15 parent-card-div row center-align-row">
																<div class="col-md-12 padding-left-right-0">
																	<div class="sr-combo-inputs-row">
																		<div class="sr-input sr-card-element form-control stripe-form-control"
																			id="card-element"></div>
																	</div>
																</div>
															</div>
															<div class="sr-field-error" id="card-errors" role="alert">
															</div>
															<div class="margin-top-15 parent-card-div row">
																<div class="col-md-12">
																	<button id="payByCardId" type="button"
																		class="ph_btn_small btn-success full-width payment-btn btn place-center">
																		Pay&nbsp;<span id="checkoutCardAmount"></span>
																	</button>
																</div>
															</div>
														</form>
													</div>
												</div>
											</div>
										</div>
										<div class="tab-pane fade" id="tab-example-2">
											<div class="row col-12 place-center">
												<img src="${pageContext.request.contextPath}/resources/assets/images/fpx-logo.png"
													class="payment-images">
											</div>
											<div id="fpxBlock">
												<div class="payment-block">
													<form id="payment-form-fpx">
														<div class="row">
															<div class="col-md-12 align-center">
																<div id="fpx-bank-element" class="form-control stripe-form-control"
																	style="padding-top: 0;"></div>
															</div>
														</div>
														<div class="row margin-top-15">
															<div class="col-md-12 align-center">
																<button id="fpx-button" data-secret=""
																	class="btn-success no-border ph_btn_small full-width payment-btn btn place-center">
																	Pay&nbsp;<span id="checkoutFpxAmount"></span>
																</button>
															</div>
														</div>
													</form>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
// <script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript">
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});

	// window.paypalCheckoutReady = function() {
	// 	paypal.checkout.setup('${merchantId}', {
	// 		environment : '${paypalEnvironment}',
	// 		container : 'supplierCheckoutForm',
	// 		condition : function() {
	// 			return $('#supplierCheckoutForm').isValid();
	// 		},
	// 		//button: 'placeOrderBtn'
	// 		buttons : [ {
	// 			container : 'supplierCheckOutBtn',
	// 			type : 'checkout',
	// 			color : 'blue',
	// 			size : 'medium',
	// 			shape : 'rect'
	// 		} ]
	// 	});

	// };
</script>
<style type="text/css">
	.alert-danger a,
	.alert-danger,
	.danger,
	.parsley-error {
		color: #620f0f;
		border-color: #db6a6a;
		background: #ffc6c6;
	}

	.alert {
		position: relative;
		padding: 10px;
		text-align: left;
		border-width: 0;
		border-style: solid;
	}

	.progress-bar-danger,
	.bootstrap-switch-danger,
	.checkbox-danger div[id^='uniform-'] span.checked,
	.radio-danger div[id^='uniform-'] span.checked,
	.badge-danger,
	.label-danger,
	.btn-danger,
	.bg-danger,
	.hover-red:hover,
	.hover-danger:hover,
	.bg-red {
		color: #fff;
		border-color: #cf4436;
		background: #e74c3c;
	}


	.stripe-form-control {
		font-size: 13px !important;
		background: #fff !important;
		height: 38px !important;
		color: #2b2f33 !important;
		border: #dfe8f1 solid 1px !important;
	}


	.tab-pane {
		padding: 0;
		float: left;
		width: 100%;
	}

	.lazy,
	.tab-pane,
	[data-toggle=buttons]>.btn>input[type=radio],
	[data-toggle=buttons]>.btn>input[type=checkbox],
	.mix,
	.hide {
		display: none;
	}

	.tab-pane.active,
	.collapse.in {
		display: block;
	}

	.panel {
		float: left;
		width: 100%;
		margin-bottom: 20px;
		border-width: 1px;
		border-style: solid;
		border-radius: 4px;
		background-color: #fff;
	}

	.panel {
		box-shadow: 0 0 0 !important;
	}

	.thumb-pane,
	.mailbox-wrapper .nav-list li a,
	.ui-tabs-nav,
	.tabs-navigation>ul,
	.tabs-navigation>ul li>a,
	.tabs-navigation>ul li.ui-state-hover>a,
	.ui-accordion .ui-accordion-header,
	.panel,
	.panel-group .panel-heading+.panel-collapse .panel-body,
	.panel-group .panel-footer+.panel-collapse .panel-body,
	.panel-footer,
	.panel-heading,
	.timeline-box:before,
	.timeline-box .tl-item .popover,
	.popover-title,
	.ui-dialog .ui-dialog-titlebar,
	.thumbnail,
	.img-thumbnail,
	.daterangepicker .calendar-date,
	.ui-spinner .ui-spinner-button,
	.ms-container .ms-list,
	.ms-container .ms-selectable li.ms-elem-selectable,
	.ms-container .ms-selection li.ms-elem-selection,
	.chosen-container-multi .chosen-choices li.search-choice,
	.chosen-container .chosen-drop,
	.chosen-container,
	.chosen-container-single .chosen-single div,
	.chosen-container-active.chosen-with-drop .chosen-single div,
	div[id^='uniform-'] span,
	div.selector,
	.selector i,
	.list-group-item,
	.nav-tabs,
	.nav-tabs>li>a:hover,
	.nav-tabs>li>a:focus,
	.nav .open>a,
	.nav .open>a:hover,
	.nav .open>a:focus,
	.button-pane,
	.ui-datepicker-buttonpane,
	.ui-dialog-buttonpane,
	.content-box,
	.content-box-header.bg-default,
	.content-box-header.bg-gray,
	.content-box-header.bg-white,
	.panel-box.bg-default,
	.panel-box.bg-gray,
	.panel-box.bg-white,
	.panel-content.bg-default,
	.panel-content.bg-gray,
	.panel-content.bg-white,
	.pagination>li>a,
	.pagination>li>span,
	.dashboard-buttons .btn,
	.bg-default,
	.fc-state-default,
	.fc-widget-header,
	.fc-widget-content,
	.ui-datepicker .ui-datepicker-buttonpane button,
	.btn-default,
	.popover-title,
	.bordered-row .form-group,
	.bg-white.dashboard-box .button-pane,
	.bg-white.tile-box .tile-footer,
	.mail-toolbar,
	.email-body,
	.ui-dialog,
	.ui-datepicker,
	.dropdown-menu,
	.popover,
	.ui-menu,
	.minicolors-panel,
	.jvectormap-label,
	.jvectormap-zoomin,
	.jvectormap-zoomout,
	.posts-list li,
	.border-default {
		border-color: #dfe8f1;
	}

	.example-box-wrapper .icon-box,
	.example-box-wrapper .ui-slider,
	.example-box-wrapper .ui-rangeSlider,
	.example-box-wrapper .panel-layout,
	.example-box-wrapper .image-box,
	.example-box-wrapper .ui-accordion,
	.example-box-wrapper .dashboard-box,
	.example-box-wrapper .content-box,
	.example-box-wrapper .tile-box,
	.example-box-wrapper .jvectormap-container,
	.example-box-wrapper>.hasDatepicker,
	.example-box-wrapper>.minicolors,
	.example-box-wrapper .minicolors,
	.example-box-wrapper .ui-tabs,
	.example-box-wrapper>img,
	.example-box-wrapper>.thumbnail,
	.example-box-wrapper>.img-humbnail,
	.example-box-wrapper>.display-block.dropdown-menu,
	.example-box-wrapper>.dropdown,
	.example-box-wrapper>.dropup,
	.example-box-wrapper>form,
	.example-box-wrapper>.progressbar,
	.example-box-wrapper .loading-spinner,
	.example-box-wrapper .loading-stick,
	.example-box-wrapper .nav,
	.example-box-wrapper .jcrop-holder,
	.example-box-wrapper .alert,
	.example-box-wrapper .list-group,
	.example-box-wrapper>h6,
	.example-box-wrapper .dataTables_wrapper,
	.example-box-wrapper .scrollable-content,
	.example-box-wrapper>.pagination,
	.example-box-wrapper>.btn-group-vertical,
	.example-box-wrapper>.btn-toolbar,
	.example-box-wrapper>.btn-group,
	.example-box-wrapper>.btn,
	.example-box-wrapper>.panel-layout {
		margin-bottom: 20px;
	}

	.title-hero {
		margin: 0 0 15px;
		padding: 0;
		text-transform: uppercase;
		font-size: 14px;
		opacity: .7;
	}

	.example-box-wrapper {
		float: left;
		width: 100%;
		margin-bottom: 0;
		position: relative;
	}

	h1,
	h2,
	h3,
	h4,
	h5,
	h6 {
		color: #424242;
	}


	.place-center {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.btn.disabled {
		background: #aeaeae;
		color: #c7c7c7;
		opacity: 1;
	}

	.ph_btn_small {
		height: 35px;
		min-width: 100px;
		line-height: 35px;
	}

	.full-width {
		width: 100%;
	}

	.pass-desc {
		color: #7f7f7f;
		font-weight: normal;
		/* 	font-size: 13px; */
	}

	.payment-div {
		text-align: left;
		color: #4f4d4d !important;
		font-family: 'open_sanssemibold';
		font-weight: normal;
		margin-top: 15px;
	}

	.payment-block {
		text-align: left;
		margin-top: 10px;
		margin-bottom: 10px;
	}

	.pad-left-12 {
		padding-left: 12px;
	}

	.pad-left-0 {
		padding-left: 0;
	}

	.margin-top-15 {
		margin-top: 15px;
	}

	.parent-card-div {
		display: flex;
		align-items: center;
	}

	.sr-field-error {
		margin-top: 10px;
		text-align: center;
		font-size: 14px;
		color: #ff5757;
	}

	.stripe-iframe {
		position: absolute;
		left: 30%;
		top: 10%;
	}

	ul {
		list-style: none;
	}

	.list-group-icons .list-group-item {
		font-weight: 700;
		display: block;
		padding: 15px 10px;
		text-align: center;
		text-overflow: ellipsis;
	}

	.list-group-icons .list-group-item>.glyph-icon {
		font-size: 18px;
		float: none;
		margin: 0 auto;
	}

	.alignment {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.group-label {
		text-align: right;
		margin-top: 10px;
	}

	.modal-dialog.for-delete-all.reminder .modal-body label {
		float: left;
		width: 100%;
		text-align: left;
	}

	.modal-dialog.for-delete-all.reminder .modal-body input {
		float: left;
		width: 100%;
		margin-right: 2%;
		margin-top: 0;
	}

	.header-section {
		text-align: center;
		margin-bottom: 20px;
		color: black;
		font-weight: 900;
	}

	.payment-images {
		width: 30%;
		height: 60px;
		max-width: 125px;
		margin: 10px;
	}

	.payment-images-card {
		width: 65%;
		height: 60px;
		max-width: 260px;
	}

	.payment-btn:focus {
		outline-color: #58d68c;
	}

	.center-align-row {
		width: 100%;
		margin-left: 0;
	}

	.padding-left-right-0 {
		padding-left: 0;
		padding-right: 0;
	}

	#loadingbar,
	.irs-line-mid,
	.irs-line-left,
	.irs-line-right,
	div.switch-toggle.switch-on,
	.bootstrap-switch-primary,
	.ui-tabs-nav li.ui-state-active>a,
	.ui-tabs-nav li.ui-state-active.ui-state-hover>a,
	.ms-hover.ui-state-focus,
	.ui-accordion-header.ui-accordion-header-active,
	.ui-slider-handle,
	.ui-rangeSlider-bar,
	.ui-dialog-buttonset button,
	.ui-datepicker .ui-datepicker-current-day a,
	.ui-datepicker .ui-datepicker-current-day span,
	.ui-datepicker .ui-datepicker-prev,
	.ui-datepicker .ui-datepicker-next,
	.daterangepicker .ranges li.active,
	.daterangepicker .ranges li.active:hover,
	.form-wizard>ul>li.active .wizard-step,
	.ui-spinner .ui-spinner-button:hover,
	.ui-menu li>a:hover,
	.ms-list .ms-hover,
	.chosen-container .chosen-results li.active-result.highlighted,
	div[id^="uniform-"] span.checked,
	.nav>li.active>a,
	.nav>li.active>a:hover,
	.nav>li.active>a:focus,
	a.list-group-item.active,
	a.list-group-item.active:hover,
	a.list-group-item.active:focus,
	li.active a.list-group-item,
	li.active a.list-group-item:hover,
	li.active a.list-group-item:focus,
	.label-primary,
	.badge-primary,
	.fc-event,
	.bg-primary,
	.btn-primary,
	.owl-controls .owl-page span,
	#nav-toggle.collapsed span,
	#nav-toggle span::before,
	#nav-toggle span::after {
		background: #0cb6ff none repeat scroll 0 0 !important;
		color: #fff !important;
	}

	.alert .alert-icon {
		line-height: 34px;
		float: left;
		width: 34px;
		height: 34px;
		margin: 5px 10px 0 0;
		text-align: center;
		border-radius: 2px;
		display: flex;
		justify-content: center;
		align-content: center;
		align-items: center;
	}

	.bg-red {
		color: #fff;
		border-color: #cf4436;
		background: #e74c3c;
	}

	.alert .alert-title {
		font-size: 12px;
		font-weight: bold;
		margin: 4px 0 3px;
		padding: 0;
		text-transform: uppercase;
	}

	.alert h4 {
		margin-top: 0;
		color: inherit;
	}

	.fc-icon,
	#page-sidebar li ul li a:before,
	#page-sidebar li a.sf-with-ul:after,
	.search-choice-close:before,
	.ui-dialog-titlebar-close:before,
	.glyph-icon:before,
	.ui-icon:before,
	.dataTables_paginate a i:before {
		font-family: FontAwesome;
		font-weight: normal;
		font-style: normal;
		display: inline-block;
		text-align: center;
		text-decoration: none;
		background: 0;
		speak: none;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
	}
</style>
<script src="https://js.stripe.com/v3/"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$(".pwd").click(function() {
			$('#passwordPlaceHolder').hide();
		});

		$('#idAdminMobileNo').mask('+00 00000000000', {
			placeholder : "+60 122735465"
		});
		$('#idCompanyContactNumber').mask('+00 00000000000', {
			placeholder : "+60 322761533"
		});
	});
</script>

<script type="text/javascript" src="<c:url value="/resources/js/view/supplierCheckout.js"/>"></script>