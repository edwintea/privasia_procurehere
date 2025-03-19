<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/saas.css"/>">
<div id="page-content">
	<div class="container">
		<div class="registraion_renew_wrap">
			<div class="reg_inner">
				<div class="reg_heading">
					<spring:message code="changeplan.enter.details"/> <br />[${subscription.plan.planName.concat(' - ').concat(subscription.plan.shortDescription)}]
				</div>
				<div class="reg_form_box">
					<div class="rf_inner">
						<form:form class="form-horizontal" commandName="subscription" id="idSubscribeForm" action='${pageContext.request.contextPath}/buyer/billing/changePlan/${buyer.buyerPackage.plan.id}' method="post">
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.company.name"/></div>
								<div class="rfr_field col-md-12">
									<spring:message code="company.name.placeholder" var="companyname"/>
									<form:input type="text" id="idCompanyName" cssClass="form-control" readonly="true" placeholder="${companyname}" data-validation="required length alphanumeric " data-validation-allowing="-_ &." data-validation-length="4-124"
										path="buyer.companyName" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.current.validity"/></div>
								<div class="rfr_field">
									<c:if test="${buyer.buyerPackage != null}">
										<jsp:useBean id="now" class="java.util.Date" />
										<fmt:formatDate var="fromDate" value="${buyer.buyerPackage.startDate}" pattern="dd/MM/yyyy" timeZone="${sessionScope['timeZone']}" />
										<fmt:formatDate var="toDate" value="${buyer.buyerPackage.endDate}" pattern="dd/MM/yyyy" timeZone="${sessionScope['timeZone']}" />
										<div class="col-md-9">
											<input type="text" class="form-control" value="${fromDate.concat(' to ').concat(toDate) }" readonly="true" />
										</div>
										<div class="col-md-3">
											<c:choose>
												<c:when test="${buyer.buyerPackage != null and buyer.buyerPackage.startDate != null and (now lt buyer.buyerPackage.startDate)}">
													<span style="color: red;"> (<spring:message code="account.overview.status.expired"/>) </span>
												</c:when>
												<c:when test="${buyer.buyerPackage != null and buyer.buyerPackage.endDate != null and (now gt buyer.buyerPackage.startDate and now lt buyer.buyerPackage.endDate)}">
													<span style="color: green;"> (<spring:message code="account.overview.status.active"/>) </span>
												</c:when>
												<c:otherwise>
													<span style="color: blue;"> (<spring:message code="account.overview.status.notactive"/>) </span>
												</c:otherwise>
											</c:choose>
										</div>
									</c:if>
									<c:if test="${buyer.buyerPackage == null }">
										<div class="col-md-12"><spring:message code="application.not.applicable2"/></div>
									</c:if>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="changeplan.choose.plan"/></div>
								<div class="rfr_field col-md-12">
									<div class="">
										<div class="input-prepend">
											<span class="phisicalArressBlock pull-left marg-top-10 width100   ${subscription.plan != null ? 'buyerAddressRadios active' : ''}" style="display: none; padding: 10px 0;">
												<c:if test="${not empty subscription.plan}">
													<div class="planSnip">
														<div class="col-md-10">
															<h5>${subscription.plan.planName}</h5>
															<span class='desc width100'>${subscription.plan.shortDescription}<br /><spring:message code="application.status" />: ${subscription.plan.planStatus}
															</span>
														</div>
														<div class="col-md-2 align-right">
															<a class="pull-right ${subscription.plan != null ? '' : 'flagvisibility'}" title="" data-placement="top" id="deletePlan" data-toggle="tooltip" data-original-title="Change Plan"
																style="font-size: 18px; line-height: 1; padding: 0; color: #7f7f7f;">
																<i class="fa fa-times-circle"></i>
															</a>
														</div>
													</div>
												</c:if>
										</div>
										<div id="sub-credit" class="invite-supplier delivery-address collapse margin-top-10">
											<div class="role-upper ">
												<div class="col-sm-12 col-md-12 col-xs-12 float-left">
													<input type="text" placeholder='<spring:message code="change.plan.placeholder" />' class="form-control delivery_add">
												</div>
											</div>
											<div class="chk_scroll_box">
												<div class="scroll_box_inner">
													<div class="role-main">
														<div class="role-bottom small-radio-btn">
															<ul class="role-bottom-ul">
																<c:forEach var="plan" items="${planList}">
																	<li>
																		<div class="radio-info">
																			<label> <form:radiobutton path="plan" data-validation-error-msg-container="#plan-sel-error" data-validation="required plan_change_check" data-chargemodel="${plan.chargeModel}" data-userlimit="${plan.userLimit}"
																					data-eventlimit="${plan.eventLimit}" data-planprice="${plan.price}" data-currency="${plan.currency.currencyCode}" value="${plan.id}" class="custom-radio planRadio" />
																			</label>
																		</div>
																		<div class="del-add">
																			<h5>${plan.planName}</h5>
																			<span class='desc width100'>${plan.shortDescription}<br /><spring:message code="application.status" />: ${plan.planStatus}
																			</span>
																		</div>
																	</li>
																</c:forEach>
															</ul>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<span id="plan-sel-error"></span>
								</div>
							</div>
							<div class="rf_row change_effective">
								<div class="rfr_text"><spring:message code="changeplan.change.effective" /></div>
								<div class="rfr_field col-md-12">
									<div class="radio_yes-no-main width100">
										<div class="radio_yes-no">
											<div class="radio-info">
												<label class="marg-right-10"> <form:radiobutton path="convertedToPaid" class="custom-radio immediately" value="0" /> <spring:message code="changeplan.immediatly" />
												</label>
											</div>
										</div>
										<div class="radio_yes-no">
											<div class="radio-info">
												<label class="marg-left-10"> <form:radiobutton path="convertedToPaid" class="custom-radio after-current" value="1" /> <spring:message code="changeplan.after.current.subscription" />
												</label>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.next.start.date" /></div>
								<div class="rfr_field col-md-12">
									<fmt:formatDate var="endDate" value="${buyer.buyerPackage.endDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									<input type="text" id="nextDate" class="form-control" value="${endDate}" readonly="true" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.price" /></div>
								<div class="rfr_field col-md-12">
									<input type="text" id="idPlanPriceTxt" class="form-control" value="${subscription.plan.currency.currencyCode} ${subscription.plan.price}" readonly="true" />
									<form:hidden id="idPlanPrice" path="plan.price" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="label.rftbq.th.quantity" /></div>
								<div class="rfr_field col-md-12">
									<spring:message code="period.unit.placeholder" var="periodunit"/>
									<form:input type="text" cssClass="form-control" placeholder="${periodunit}" data-validation="required number" data-validation-error-msg-number="Range allowed 1 to 9999" data-validation-allowing="range[1;9999]" path="planQuantity" />
									<span>(${subscription.plan.periodUnit}S)</span>
								</div>
							</div>
							<div class="rf_row" id="idPerUnitDiv">
								<div class="rfr_text"><spring:message code="renew.plan.credit.quantity" /></div>
								<div class="rfr_field col-md-12">
									<spring:message code="event.limit.placeholder" var="eventlimit"/>
									<form:input type="text" id="idCreditQuantity" cssClass="form-control" placeholder="${eventlimit}" data-validation="required number" data-validation-error-msg-number="Range allowed 1 to ${subscription.plan.eventLimit} for the chosen plan"
										data-validation-allowing="range[1;${subscription.plan.eventLimit}]" path="eventLimit" />
									<span>(PER ${subscription.plan.periodUnit})</span>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="eventawards.total.price"/></div>
								<div class="rfr_field col-md-12" style="color: #0095d5; font-weight: bold;">
									<span id="idTotalCurrency">${subscription.plan.currency.currencyCode}</span>
									<span id="idTotalPrice">${subscription.plan.price * subscription.planQuantity}</span>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text">&nbsp;</div>
								<div class="rfr_field col-md-12" id="idButtonHolder">
									<!-- input type='image' name='submit' src='https://www.paypalobjects.com/webstatic/en_US/i/btn/png/btn_subscribe_cc_147x47.png' border='0' align='top' alt='Check out with PayPal' / -->
									<!-- button class="frm_bttn1 hvr-pop sub_frm" onclick="window.location.href='confirm.html'">GO PREMIUM</button  -->
								</div>
								<div class="rfr_text">&nbsp;</div>
								<div class="rfr_field col-md-12">
									<!-- <img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges"> -->
								</div>
							</div>
						</form:form>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</div>
</div>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">

	$(document).delegate('.delivery_add', 'keyup', function() {
		var $rows = $('.role-bottom-ul li');
		var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
		$rows.show().filter(function() {
			var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
			return !~text.indexOf(val);
		}).hide();
	});

	$(document).delegate('.role-bottom-ul li [type="radio"]', 'click', function() {
		var dataAddress = '<div class="planSnip"><div class="col-md-10">';
		dataAddress += $(this).closest('li').children('.del-add').html();
		dataAddress += '</div><div class="col-md-2 align-right">';
		dataAddress += '<a class="pull-right" title="" data-placement="top" id="deletePlan" data-toggle="tooltip" data-original-title="Delete Delivery Address" style=" font-size: 18px; line-height: 1; padding: 0; color: #7f7f7f;"><i class="fa fa-times-circle"></i></a>';
		dataAddress += '</div></div>';
		$('.phisicalArressBlock').html(dataAddress);
		$('.phisicalArressBlock').show();
		$('#deletePlan').show();
		$.uniform.update();
		vertiCaleALignDeleteAddress();
		$("#sub-credit").slideUp();
	});
	
	vertiCaleALignDeleteAddress();

	$(document).delegate('#deletePlan', 'click', function() {
		$('.phisicalArressBlock').html('');
		$('.phisicalArressBlock').hide();
		$('.role-bottom-ul li [type="radio"]').prop('checked', false);
		$('#deletePlan').hide();
		$.uniform.update();
		$("#sub-credit").slideDown();
	});

	function vertiCaleALignDeleteAddress() {
		var marginTop = ($('.phisicalArressBlock').height() - $('#deletePlan').height()) / 2;
		$('#deletePlan').css('margin-top', marginTop);
	}

	//$("#sub-credit").slideToggle();

	//$("#sub-credit").slideUp();

	$(document).on("keyup", ".delivery_add", function() {
		if ($(this).val() != "") {
			$('.delivery_add_btn').removeClass('btn-black disabled').addClass('btn-blue');
		} else {
			$('.delivery_add_btn').removeClass('btn-blue').addClass('btn-black disabled');
		}
	});

	$(document).on("click", ".delivery_add_btn", function() {
		if ($(this).hasClass('btn-blue')) {
			var txt = $(".delivery_add").val();

			var clo = '<li><div class="radio-primary"><label><input type="radio" id="inlineRadio110" name="example-radio1" ></label></div>  <div class="del-add"><span class="desc">' + txt + '</span><div class="li-links"><a href="#">Edit</a><a class="remove_del" href="javascript:void(0);">Remove</a></div></div></li>';
			$('.role-bottom-ul').prepend(clo);

			$('.role-bottom-ul').find("[type=radio]").uniform();
			$('.role-bottom-ul li:first').find(".radio span").append('<i class="glyph-icon icon-circle"></i>');

			$(".delivery_add").val('');
			$('.delivery_add_btn').removeClass('btn-blue');
			$('.delivery_add_btn').addClass('btn-black disabled');
		}
	});

	<c:if test="${subscription.plan.chargeModel != 'PER_UNIT'}">
		$('#idPerUnitDiv').hide();
	</c:if>
	
	$(document).on('click', ".planRadio", function() {
		console.log($(this).attr('data-planprice'));
		console.log($(this).attr('data-currency') + ' ' + $(this).attr('data-planprice'));
		$('#idPlanPrice').val($(this).attr('data-planprice'));
		$('#idPlanPriceTxt').val($(this).attr('data-currency') + ' ' + $(this).attr('data-planprice')); 
		
		$('#idTotalCurrency').text($(this).attr('data-currency'));
		$('#idTotalPrice').html(parseInt($.trim($(this).attr('data-planprice'))) * parseInt($.trim($('#planQuantity').val())));

		if($(this).attr('data-chargemodel') == 'Per Unit') {
			$('#idPerUnitDiv').show();
			$('#idCreditQuantity').attr('data-validation-allowing','range[1;' + $(this).attr('data-eventlimit') + ']')
		} else {
			$('#idPerUnitDiv').hide();
		}
	});
	
	$(document).on('click', ".immediately", function() {
		var date = new Date( Date.parse( new Date() ) ); 
	    date.setDate( date.getDate() + 1 );
	    $('#nextDate').val($.datepicker.formatDate('dd/mm/yy', date));
	});
	$(document).on('click', ".after-current", function() {
		setNextDate();
	});
	
	
	function setNextDate(){
		var endDate = '${buyer.buyerPackage.endDate}';
		var date = new Date( Date.parse( endDate ) ); 
	    date.setDate( date.getDate() + 1 );
	    $('#nextDate').val($.datepicker.formatDate('dd/mm/yy', date));
	}
	$(document).on("click", ".remove_del", function() {
		$(this).closest("li").slideUp();
	});

	$(document).ready(function() {
		<c:if test="${buyer.currentSubscription !=null && buyer.currentSubscription.subscriptionStatus == 'EXPIRED'}">
		var date = new Date( Date.parse( new Date() ) ); 
	    //date.setDate( date.getDate() + 1 );
	    $('#nextDate').val($.datepicker.formatDate('dd/mm/yy', date));
	    $('.change_effective').hide();
	</c:if>
	<c:if test="${buyer.currentSubscription !=null && buyer.currentSubscription.subscriptionStatus != 'EXPIRED'}">
	$('.after-current').prop('checked', true);
	setNextDate();
	</c:if>

		$('#planQuantity').keyup(function() {
			if ($("input[name='plan']:checked").attr('data-chargemodel') == 'Per Unit') {
				$('#idTotalPrice').html(parseInt($.trim($('#idPlanPrice').val())) * parseInt($.trim($(this).val())) * parseInt($.trim($('#idCreditQuantity').val())));
			} else {
				console.log(parseInt($.trim($('#idPlanPrice').val())));
				console.log(parseInt($.trim($(this).val())));
				$('#idTotalPrice').html(parseInt($.trim($('#idPlanPrice').val())) * parseInt($.trim($(this).val())));
			}
		});
		if ($('#idCreditQuantity').length) {
			$('#idCreditQuantity').keyup(function() {
				if ($('#idCreditQuantity')) {
					$('#idTotalPrice').html(parseInt($.trim($('#idPlanPrice').val())) * parseInt($.trim($(this).val())) * parseInt($.trim($('#planQuantity').val())));
				}
			});
		}
	});
	
	$.formUtils.addValidator({
		name : 'plan_change_check',
		validatorFunction : function(value, $el, config, language, $form) {
			if($("input[name='plan']:checked").val() == '${subscription.plan.id}') {
				response = false;
			} else {
				response = true;
			}
			console.log(response);
			return response;
		},
		errorMessage : 'You have selected the same plan as current. Please choose another plan.',
		errorMessageKey : 'badPlan'
	});

	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
	
	window.paypalCheckoutReady = function() {
		paypal.checkout.setup('${merchantId}', {
			environment : '${paypalEnvironment}',
			container : 'idSubscribeForm',
			condition : function() {
				return $('#idSubscribeForm').isValid();
			},
			//button: 'placeOrderBtn'
			buttons : [ {
				container : 'idButtonHolder',
				type : 'checkout',
				color : 'blue',
				size : 'medium',
				shape : 'rect'
			} ]
		});
	};
</script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#planQuantity').mask('99', {
			placeholder : "e.g. 12"
		});

		if ($('#idCreditQuantity').length) {
			$('#idCreditQuantity').mask('999', {
				placeholder : "e.g. 5"
			});
		}
	});
</script>
