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
<%-- <jsp:include page="/WEB-INF/views/jsp/subscription/SelectSubscriptionPlan.jsp" /> --%>


<div class="container margin-bottom-5">
	<div style="width: 80%;margin: 0 auto;margin-top: 25px;">
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	</div>
	<div class="buyer-check-section">
		<p>
			Just one step left to subscribe to Procurehere's market leading simplified<br> e-procurement solution
		</p>
		<p>
			Complete your purchase through our secure payment system, and unlock access to the platform which<br> has saved users US$1.3 billion to date and counting
		</p>
	</div>
	<div class="row">
		<input type="hidden"  id="stripePublishKey" value="${publicKey}">
		<form:form commandName="buyer" action="${pageContext.request.contextPath}/buyerSubscription/eventBasedBuyerCheckout" id="idEventBasedBuyerCheckOutForm" method="post">
			<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0 margin-right-3"></div>
			<div class="col-sm-12 col-md-12 col-lg-4 col-xs-12 buyer-purchase-details">
				<div class="form-group">
					<label>Company Name</label>
					<form:input type="text" id="idCompanyName" cssClass="form-control" placeholder="Enter your company name" data-validation="required length alphanumeric company_name" data-validation-allowing="-_ &.()" data-validation-length="4-124"
						path="buyer.companyName" />
				</div>
				<div class="form-group">
					<label><spring:message code="supplier.registration.company.number" /></label>
					<form:input type="text" id="idCompRegNum" cssClass="form-control" placeholder="Enter your company registration number" data-validation="required length alphanumeric crn_number" data-validation-allowing="-_ " data-validation-length="2-124"
						path="buyer.companyRegistrationNumber" />
				</div>
				<div class="form-group">
					<label>Country of Registration</label>
					<form:select path="buyer.registrationOfCountry" id="idRegCountry" cssClass="form-control" data-validation="required crn_number company_name">
						<form:option value="">Select Country of Registration</form:option>
						<form:options items="${countryList}" itemValue="id" itemLabel="countryName" />
					</form:select>
				</div>
				<div class="form-group">
					<label>Full Name</label>
					<!-- <input type="text" class="form-control" id="r" name="fname"> -->
					<form:input type="text" cssClass="form-control" placeholder="Company contact person name" data-validation="required length" path="buyer.fullName" data-validation-length="4-100" />
				</div>
				<div class="form-group">
					<label>Login Email</label>
					<!-- <input type="text" class="form-control" id="rr" name="lname"> -->
					<form:input type="text" id="idLoginEmail" cssClass="form-control" placeholder="e.g. contact@company.com" data-validation="required length email login_email" data-validation-length="6-128" path="buyer.loginEmail" />
				</div>
				<div class="form-group">
					<label>Communication Email</label>
					<!-- <input type="email" class="form-control" id="rrr" name="email"> -->
					<form:input type="text" cssClass="form-control" placeholder="All email communications will be sent to this address" data-validation="email" path="buyer.communicationEmail" />
				</div>
				<div class="form-group">
					<label>Contact Number</label>
					<!-- <input type="number" class="form-control" id="rrrr" name="companyname"> -->
					<form:input type="text" id="idCntNum" cssClass="form-control" placeholder="e.g. 0123456789" data-validation="number length" path="buyer.companyContactNumber" data-validation-length="6-14" />
				</div>

				<p class="ts-pera">
					*By proceeding, you agree to our
					<span>
						<a href="<c:url value="/resources/termsandcondition.pdf"/>">terms & conditions</a>
					</span>
				</p>
				<!-- <span class="rfr_field" id="buyerUserBaseCheckOutBtn"></span> -->
				<form:button type="button" value="Pay" id="makeSupplierStripePayment"
						class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width">Pay</form:button>
				<div>
					<img src="<c:url value="/resources/images/public/Visa.png"/>" alt="Visa-badges">
					<img src="<c:url value="/resources/images/public/mastercard-badge.png"/>" alt="Mastercard-badge">
					<img src="<c:url value="/resources/images/public/american-express.png"/>" alt="American-Express">
					<img src="<c:url value="/resources/images/public/discover.png"/>" alt="visa-badge">
					<br>
					<!-- <img src="<c:url value="/resources/images/public/paypal.png"/>" alt="Paypal-badge"> -->
					<img src="<c:url value="/resources/images/public/McAfee.png"/>" alt="McAfee-badge">
				</div>

			</div>
			<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div>

			<div class="col-sm-12 col-md-12 col-lg-4 col-xs-12 pack-amount-details" id="ebp">
				<h4 class="border-btm">EVENT BASED</h4>
				<div class="pack-details">
					<input type="hidden" name="plan.id" id="planId" value="${buyerPlan.id}">
					<input type="hidden" id="eventPlanId" value="${buyerPlan.id}">
					<input type="hidden" name="range.id" id="rangeUserId">
					<input type="hidden" name="promoCodeId" id="promoCodeEventId">
					<input type="hidden" id="tax" value="${buyerPlan.tax}">
					<c:forEach items="${buyerPlan.rangeList}" var="range" varStatus="index" begin="0" end="0">
						<c:set var="endEventRange" value="${range.rangeEnd}" />
						<c:set var="eventPrice" value="${range.price}" />
					</c:forEach>
					<fmt:formatNumber groupingUsed="false" type="number" var="eventFirstPrice" minFractionDigits="2" maxFractionDigits="2" value="${eventPrice}" />
				</div>

				<div class="pack-details">

					<p>
						<span class="">Event Pack</span>
						<span class="pull-right" >${eventFirstPrice}</span>
					</p>
				</div>
				<div class="pack-details">
					<p>
						<span class="form-inline">
							Number of Events
							<%-- <input type="text" class="form-control" name="numberUserEvent" id="additionalEvents" data-validation="length number" value="1" data-validation-allowing="range[1;${endEventRange}]" data-validation-length="1-3"> --%>
						<form:input type="text" class="form-control" path="userQuantity" id="additionalEvents" data-validation="length number" value="1" data-validation-allowing="range[1;${endEventRange}]" data-validation-length="1-3"/>
						</span>

						<span class="pull-right" id="eventShowPrice">${eventFirstPrice}</span>
					</p>

					<p class="pack-margin-neg-12">US$ ${eventFirstPrice} per event</p>
					<p class="" style="margin-top: 5px !important;">( Inclusive of <span id="tax_span" style="color: #979797 !important;font-weight: 500;"></span> % SST )</p>
				</div>
				<div class="pack-details">
					<form class="form-group">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="form-inline">
							<label class="margin-right-20">Promo Code</label>
							<input type="text" class="form-control" id="promoCodeEvent" name="promoCode">
							<span id="promoErrorEvent"></span>
						</div>
					</form>
				</div>
				<h4>TOTAL FEE</h4>

				<div class="pack-details">
					<p>
						<span class="left-span">
							Event Pack x &nbsp;
							<span class="left-span" id="selectedNoEvent">1</span>
						</span>
						<span class="pull-right" id="eventPrice">
							<input type="hidden" name="eventPrice" value="${eventFirstPrice}">
							${eventFirstPrice}
						</span>
					</p>

				</div>
				<!-- <div class="pack-details">
					<p>
						<span class="left-span">Additional Events</span>
						<span class="pull-right" id="addtionalUserPrice">
							<input type="hidden" name="addtionalUserPrice" value="1">
							0.00
						</span>
					</p>
				</div> -->
				<div class="pack-details">
					<p>
						<span class="left-span">Promotional Code</span>
						<span class="pull-right" id="promoEventDiscountPrice">
							<input type="hidden" name="promoDiscountPrice" value="0">
							0.00
						</span>
					</p>
				</div>
				<h4 class="border-btm"></h4>
				<h4 class="pull-right" id="totalEventPrice">
					US$
					<input type="hidden" name="totalPrice" value="${eventFirstPrice}">
					${eventFirstPrice}
				</h4>


			</div>

			<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div>
		</form:form>

	</div>
</div>

<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="" payment-amount=""
  style="background: #0000004f;">
  <div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
    <!-- Modal content-->
    <div class="modal-content">
      <!-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> -->
      <div class="modal-body">
        <button class="close for-absulate" id="makePaymentModalCloseId" type="button" data-dismiss="modal">×</button>
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
                <c:if test="${buyerPlan.currency.currencyCode == 'MYR'}">
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
                              <div class="margin-top-15 parent-card-div row center-align-row">
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

<script src="https://js.stripe.com/v3/"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript">
	$.validate({ lang : 'en', modules : 'date, security' });
	
	
	window.paypalCheckoutReady = function() {
		paypal.checkout.setup('${merchantId}', {
			environment : '${paypalEnvironment}',
			container : 'idEventBasedBuyerCheckOutForm',
			condition : function() {
				return $('#idEventBasedBuyerCheckOutForm').isValid();
			},
			//button: 'placeOrderBtn'
			buttons : [ {
				container : 'buyerUserBaseCheckOutBtn',
				type : 'checkout',
				color : 'blue',
				size : 'medium',
				shape : 'rect'
			} ]
		});

	};
	
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/freeTrialPlan.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/freeTrialSignup.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyerCheckout.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">

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