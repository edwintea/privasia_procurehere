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
		<div class="registraion_wrap">
			<div class="reg_inner">
				<div class="reg_heading">
					<spring:message code="changeplan.confirm.change.plan"/> <br />[${subscription.plan.planName.concat(' - ').concat(subscription.plan.shortDescription)}]
				</div>
				<div class="reg_form_box">
					<div class="rf_inner">
						<form class="form-horizontal" id="idSubscribeForm" action='${pageContext.request.contextPath}/buyer/billing/confirmChangePlan' method="post">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="token" value="${sessionScope['TOKEN']}" /> <input type="hidden" name="payerId" value="${sessionScope['PAYERID']}" /> <input
								type="hidden" name="paymentTransactionId" value="${paymentTransaction.id}" />
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.company.name"/></div>
								<div class="rfr_field">
									<input type="text" id="idCompanyName" cssClass="form-control" readonly="true" placeholder='<spring:message code="company.name.placeholder" />' value="${buyer.companyName}" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.current.validity"/></div>
								<div class="rfr_field">
									<c:if test="${buyer.buyerPackage != null}">
										<jsp:useBean id="now" class="java.util.Date" />
										<fmt:formatDate var="fromDate" value="${buyer.buyerPackage.startDate}" pattern="dd/MM/yyyy" timeZone="${sessionScope['timeZone']}" />
										<fmt:formatDate var="toDate" value="${buyer.buyerPackage.endDate}" pattern="dd/MM/yyyy" timeZone="${sessionScope['timeZone']}" />
										<input type="text" class="form-control" value="${fromDate.concat(' to ').concat(toDate) }" readonly="true" />
										<c:choose>
											<c:when test="${buyer.buyerPackage != null and buyer.buyerPackage.startDate != null and (now lt buyer.buyerPackage.startDate)}">
												<span style="color: red;"> (<spring:message code="account.overview.status.expired"/>) </span>
											</c:when>
											<c:when test="${buyer.buyerPackage != null and buyer.buyerPackage.endDate != null and (now gt buyer.buyerPackage.startDate and now lt buyer.buyerPackage.endDate)}">
												<span style="color: green;"> (<spring:message code="account.overview.status.active"/>) 
											</c:when>
											<c:otherwise>
												<span style="color: blue;"> (<spring:message code="account.overview.status.notactive"/>) </span>
											</c:otherwise>
										</c:choose>
									</c:if>
									<c:if test="${buyer.buyerPackage == null }">
								<spring:message code="application.not.applicable2"/>
							</c:if>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="changeplan.confirm.selected.plan"/></div>
								<div class="rfr_field">
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
														<div class="col-md-2 align-right"></div>
													</div>
												</c:if>
											</span>
										</div>
									</div>
									<span id="plan-sel-error"></span>
								</div>
							</div>
							<%-- <div class="rf_row">
						<div class="rfr_text">Change Effective</div>
						<div class="rfr_field">
							<div class="radio_yes-no-main width100">
								<div class="radio_yes-no">
									<div class="radio-info">
										<label class="marg-right-10">
											<form:radiobutton path="convertedToPaid" class="custom-radio" disabled="true" value="0" />
											Immediately
										</label>
									</div>
								</div>
								<div class="radio_yes-no">
									<div class="radio-info">
										<label class="marg-left-10">
											<form:radiobutton path="convertedToPaid" class="custom-radio" disabled="true" value="1" />
											After current subscription ends
										</label>
									</div>
								</div>
							</div>
						</div>
					</div> --%>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.next.start.date" /></div>
								<div class="rfr_field">
									<fmt:formatDate var="endDate" value="${buyer.buyerPackage.endDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									<input type="text" class="form-control" value="${endDate}" readonly="true" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.price" /></div>
								<div class="rfr_field">
									<input type="text" class="form-control" value="${subscription.plan.currency.currencyCode} ${subscription.plan.price}" readonly="true" /> <input type="hidden" id="idPlanPrice" value="${subscription.plan.price}" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="label.rftbq.th.quantity" /></div>
								<div class="rfr_field">
									<input type="text" cssClass="form-control" placeholder='<spring:message code="period.unit.placeholder" />' data-validation="required number" data-validation-error-msg-number="Range allowed 1 to 9999" data-validation-allowing="range[1;9999]" value="${subscription.planQuantity }" readonly="true" />
									<span>(${subscription.plan.periodUnit}S)</span>
								</div>
							</div>
							<div class="rf_row" id="idPerUnitDiv">
								<div class="rfr_text"><spring:message code="renew.plan.credit.quantity" /></div>
								<div class="rfr_field">
									<input type="text" id="idCreditQuantity" cssClass="form-control" placeholder='<spring:message code="event.limit.placeholder" />' data-validation="required number" readonly="true" data-validation-error-msg-number="Range allowed 1 to ${subscription.plan.eventLimit} for the chosen plan"
										data-validation-allowing="range[1;${subscription.plan.eventLimit}]" value="${eventLimit}" />
									<span>(PER ${subscription.plan.periodUnit})</span>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="eventawards.total.price"/></div>
								<div class="rfr_field" style="color: #0095d5; font-weight: bold;">
									${subscription.plan.currency.currencyCode}
									<span id="idTotalPrice">${subscription.plan.price * subscription.planQuantity}</span>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text">&nbsp;</div>
								<div class="rfr_field" id="idButtonHolder">
									<button type="submit" class="frm_bttn1 hvr-pop sub_frm"><spring:message code="renew.plan.confirm.pay"/></button>
								</div>
							</div>
						</form>
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
	
	$(document).on('click', ".custom-radio", function() {
		if($(this).attr('data-chargeModel') == 'Per Unit') {
			$('#idPerUnitDiv').show();
			$('#idCreditQuantity').attr('data-validation-allowing','range[1;' + $(this).attr('data-eventLimit') + ']')
		} else {
			$('#idPerUnitDiv').hide();
		}
	});
	
	$(document).on("click", ".remove_del", function() {
		$(this).closest("li").slideUp();
	});

	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
	
</script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		/* $('#planQuantity').mask('99', {
			placeholder : "e.g. 12"
		}); */

		if ($('#idCreditQuantity').length) {
			$('#idCreditQuantity').mask('999', {
				placeholder : "e.g. 5"
			});
		}
	});
</script>
