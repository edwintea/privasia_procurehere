<%@page import="java.util.Calendar"%>
<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<spring:message var="accountOverviewDesk" code="application.buyer.account.overview" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${accountOverviewDesk}] });
});
</script>

<!-- Set current date without time -->
<%
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.AM_PM, Calendar.AM);
	cal.set(Calendar.HOUR, 0);
	cal.set(Calendar.MINUTE, 1);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	request.setAttribute("now", cal.getTime());
%>



<script>
	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});
</script>
<div id="page-content" view-name="buyerAccountOverview">
	<input type="hidden"  id="stripePublishKey" value="${publicKey}">
	<div class="container">
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard" /></a></li>
			<li class="active"><spring:message code="paymentbilling.account.overview" /></li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap acc-over">
				<spring:message code="paymentbilling.account.overview" />
			</h2>
		</div>
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<div class="clear"></div>
		<c:if test="${buyer.buyerPackage != null}">
			<div class="Invited-Supplier-List create_sub marg-bottom-20">
				<div class="col-md-20">
					<c:if test="${!empty buyer.buyerPackage.endDate}">
						<div class="col-md-8 marg-top-10 marg-bottom-20">
							<h4>
								<spring:message code="paymentbilling.subscription.expires" />
								<fmt:formatDate value="${buyer.buyerPackage.endDate }" pattern="dd/MM/yyyy" />
							</h4>
						</div>
					</c:if>
					<div class="marg-top-10 marg-bottom-20">
						<c:if test="${buyer.buyerPackage.plan != null && buyer.buyerPackage.plan.planType == 'PER_USER' && subscription.autoChargeSubscription != 'true' && subscription.plan.planName != 'FREETRIAL'}">
							<button class="btn btn-default btn-create pad-left-15 pad-right-15 bg hvr-pop hvr-rectangle-out1 marg-right-10" id="openRenew">
								<spring:message code="paymentbilling.renew.subscription.btn" />
							</button>
						</c:if>
						<c:if test="${buyer.buyerPackage.plan != null && subscription.plan.planName == 'FREETRIAL' && subscription.nextSubscription != null}">
							<c:set var="nextSubsPaymentTrans" value="${null}" />
							<c:forEach items="${subscription.nextSubscription.paymentTransactions}" var="trans" begin="0" end="1">
								<c:set var="nextSubsPaymentTrans" value="${trans}" />
							</c:forEach>
							<c:if test="${nextSubsPaymentTrans != null && nextSubsPaymentTrans.isCapturePayment == false}">
								<spring:message code="billing.free.trial.period" />
								<button class="btn btn-info btn-sm hvr-pop marg-left-20" id="confirmPayment" type="button">
									<spring:message code="account.overview.confirm.payment" />
								</button>
							</c:if>
						</c:if>
					</div>
					<div class="marg-top-10 marg-bottom-20">
						<c:if test="${buyer.buyerPackage.plan != null && subscription.plan.planName != 'FREETRIAL'}">
							<a href="${pageContext.request.contextPath}/buyer/billing/changeBuyerPlan/${buyer.buyerPackage.plan.id}" class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2" style="padding: 0px 10px 0px 10px; font-weight: bold; background: #00e1c3;"><spring:message code="paymentbilling.change.plan.btn" /></a>
						</c:if>
						<c:if test="${buyer.buyerPackage.plan != null && subscription.plan.planName == 'FREETRIAL'}"> 
						<a href="${pageContext.request.contextPath}/buyer/billing/buyBuyerPlan" class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2" style="padding: 0px 10px 0px 10px; font-weight: bold; background: #00e1c3; float: right; margin-right: 1%"><spring:message code="paymentbilling.buy.plan.btn" /></a>
						</c:if>
					</div>
				</div>
			</div>
		</c:if>
		<div class="Invited-Supplier-List create_sub marg-bottom-20 user-type">
			<div class="row">
				<div class="user-left col-md-6">
					<div>
						<div class="col-md-6">
							<h3>
								<spring:message code="account.overview.nomal.users" />
							</h3>
							<h2>
								<c:if test="${buyer.buyerPackage != null}">
												${buyer.buyerPackage.noOfUsers}/${buyer.buyerPackage.userLimit}
										</c:if>
								<c:if test="${buyer.buyerPackage == null}">
									<spring:message code="application.not.applicable2" />
								</c:if>
							</h2>

						</div>
						<div class="col-md-6">
							<h3>
								<spring:message code="account.overview.approver.limits" />
							</h3>
							<h2>
								<c:if test="${buyer.buyerPackage != null}">
												${buyer.buyerPackage.noOfApprovers}/${buyer.buyerPackage.userLimit}
										</c:if>
								<c:if test="${buyer.buyerPackage == null}">
									<spring:message code="application.not.applicable2" />
								</c:if>
							</h2>
						</div>
					</div>
					<div>
						<c:if test="${buyer.buyerPackage != null && buyer.buyerPackage.plan != null && buyer.buyerPackage.plan.planType == 'PER_USER' && subscription.plan.planName != 'FREETRIAL'}">
							<button class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2 openEventCredits" data-subscriptionId="${subscription.id}" href="javascript:void(0);" style="font-weight: bold;">
								<spring:message code="account.overview.buy.user" />
							</button>
						</c:if>
					</div>
				</div>
				<div class="user-right col-md-6">
					<h3>Event Credit Limits</h3>
					<h2>
						<c:if test="${buyer.buyerPackage != null}">
												${buyer.buyerPackage.noOfEvents}/${buyer.buyerPackage.eventLimit}
											</c:if>
						<c:if test="${buyer.buyerPackage == null}">
							<spring:message code="application.not.applicable2" />
						</c:if>
					</h2>
					<c:if test="${buyer.buyerPackage != null && buyer.buyerPackage.plan != null && buyer.buyerPackage.plan.planType == 'PER_EVENT' && subscription != null && subscription.plan.planName != 'FREETRIAL' }">
						<button class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2 openEventCredits" data-subscriptionId="${subscription.id}" href="javascript:void(0);" style="font-weight: bold;">
							<spring:message code="account.overview.buy.credits" />
						</button>
					</c:if>
				</div>
			</div>
		</div>
		<div class="clear"></div>
		<c:if test="${subscription != null}">
			<div class="Invited-Supplier-List marg-bottom-20 bill-detail">
				<div class="Invited-Supplier-List-table add-supplier white-bg">
					<div class="ph_tabel_wrapper">
						<div class="account-overview">
							<h3>
								<spring:message code="account.overview.current.subscription" />
							</h3>
							<c:if test="${buyer != null && buyer.registrationOfCountry != null && buyer.registrationOfCountry.countryCode != 'MY'}">
								<c:set var="tax" value="0" />
								<input type="hidden" id="taxFormt" value="0">
							</c:if>
							<c:if test="${buyer != null && buyer.registrationOfCountry != null && buyer.registrationOfCountry.countryCode == 'MY'}">
								<c:set var="tax" value="${not empty subscription.plan.tax ? subscription.plan.tax : 0}" />
								<input type="hidden" id="taxFormt" value="${tax}">
							</c:if>
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td align="left"><spring:message code="account.overview.plan" /></td>
									<td align="left"><b>${subscription.plan.planName}</b> <br />${subscription.plan.shortDescription}</td>
									<td align="right" class="">
										<!-- a href="#" class="btn ph_btn_small hvr-pop hvr-rectangle-out4">Change</a -->
									</td>
								</tr>
								<tr>
									<td align="left"><spring:message code="account.overview.subscription.quantity" /></td>
									<td align="left">${subscription.plan.planType == 'PER_USER' ? subscription.userQuantity : subscription.eventQuantity }${subscription.planType == 'PER_USER' ? ' Users' : ' Events' }</td>
									<td align="right" class=""></td>
								</tr>
								<tr>
									<td align="left"><spring:message code="account.overview.subscription.valid" /></td>
									<td align="left"><strong> <c:if test="${subscription != null}">
												<fmt:formatDate value="${subscription.startDate}" pattern="dd/MM/yyyy"/> to 
															<fmt:formatDate value="${subscription.endDate}" pattern="dd/MM/yyyy"/>
												<c:choose>
													<c:when test="${subscription != null and subscription.subscriptionStatus == 'EXPIRED'}">
														<span style="color: red;"> (<spring:message code="account.overview.status.expired" />)
														</span>
													</c:when>
													<c:when test="${subscription != null and subscription.subscriptionStatus == 'ACTIVE'}">
														<span style="color: green;"> (<spring:message code="account.overview.status.active" />)
														</span>
													</c:when>
													<c:otherwise>
														<span style="color: blue;"> (<spring:message code="account.overview.status.notactive" />)
														</span>
													</c:otherwise>
												</c:choose>
											</c:if>
									</strong></td>
									<td align="right" class="">
										<!-- a href="#" class="btn ph_btn_small btn-block hvr-pop hvr-rectangle-out4">Update</a  -->
									</td>
								</tr>
							</table>
							<h4 style="padding-top: 25px;">
								<spring:message code="account.overview.payment.description" />
							</h4>
							<div class="ph_table_border margin">
								<div class="mega range-header">
									<table class="header ph_table border-none layout-fixed" width="100%">
										<thead>
											<tr>
												<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.payment.time" /></th>
												<th class="width_200 width_400_fix align-left"><spring:message code="Product.remarks" /></th>
												<th class="width_150 width_150_fix align-left"><spring:message code="account.overview.ref.id" /></th>
												<th class="width_150 width_150_fix align-left"><spring:message code="account.overview.amount" /></th>
												<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.subscription.discount" /></th>
												<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.promotional.discount" /></th>
												<th class="width_100 width_100_fix align-left"><spring:message code="account.overview.tax" /></th>
												<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.tax.description" /></th>
												<th class="width_200 width_200_fix align-left"><spring:message code="prtemplate.total.amount" /></th>
												<th class="width_100 width_100_fix align-left"><spring:message code="label.currency" /></th>
												<th class="width_200 width_200_fix align-left"><spring:message code="promotion.title" /></th>
											</tr>
										</thead>
									</table>
									<table class="data ph_table border-none layout-fixed" width="100%">
										<tbody>

											<c:if test="${empty subscription.paymentTransactions}">
												<td valign="top" class="dataTables_empty" style="text-align: left;"><spring:message code="application.nodata" /></td>
											</c:if>

											<c:forEach items="${subscription.paymentTransactions}" var="trans">
												<c:if test="${trans.referenceTransactionId ne null}">
													<tr>
														<td class="width_200 width_200_fix overflow-table align-left"><fmt:formatDate value="${trans.createdDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" /></td>
														<td class="width_200 width_400_fix overflow-table align-left">${trans.remarks}</td>
														<td class="width_150 width_150_fix overflow-table align-left">${trans.referenceTransactionId}</td>
														<td class="width_150 width_150_fix overflow-table align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.priceAmount}" /></td>
														<td class="width_200 width_200_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.priceDiscount != null ? -trans.priceDiscount : 0}" /></td>
														<td class="width_200 width_200_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.promoCodeDiscount != null ? -trans.promoCodeDiscount : 0}" /></td>
														<td class="width_100 width_100_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.additionalTax != null ? trans.additionalTax : 0}" /></td>
														<td class="width_200 width_200_fix overflow-table  align-left">${trans.additionalTaxDesc}</td>
														<td class="width_200 width_200_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.totalPriceAmount}" /></td>
														<td class="width_100 width_100_fix overflow-table align-left">${trans.currencyCode}</td>
														<td class="width_200 width_200_fix overflow-table align-left">${trans.promoCode != null ? trans.promoCode.promoCode : 'No Promotional'}</td>
													</tr>
												</c:if>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${subscription.nextSubscription != null}">
			<c:set var="subs" value="${subscription.nextSubscription}" />
		</c:if>
		<c:forEach begin="0" end="20" varStatus="loop">
			<c:if test="${subs != null}">
				<div class="clear"></div>
				<div class="Invited-Supplier-List marg-bottom-20 bill-detail">
					<div class="Invited-Supplier-List-table add-supplier white-bg">
						<div class="ph_tabel_wrapper">
							<div class="account-overview">
								<h4>${loop.index == 0 ? 'Upcoming' : 'Future' }&nbsp;<spring:message code="account.overview.subscription" />
								</h4>
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td align="left"><spring:message code="account.overview.plan" /></td>
										<td align="left"><b>${subs.plan.planName}</b> <br />${subs.plan.shortDescription}</td>
									</tr>
									<tr>
										<td align="left"><spring:message code="account.overview.subscription.quantity" /></td>
										<td align="left">${subs.plan.planType == 'PER_USER' ? subs.userQuantity : subs.eventQuantity }${subs.planType == 'PER_USER' ? ' Users' : ' Events' }
											<button class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2 openEventCredits" data-subscriptionId="${subs.id}" href="javascript:void(0);" style="font-weight: bold; background: #00e1c3; margin-left: 40px;">
												<spring:message code="account.overview.buy.more" />${subs.planType == 'PER_USER' ? ' Users' : ' Credits' }</button>
										</td>
									</tr>
									<tr>
										<td align="left"><spring:message code="account.overview.subscription.valid" /></td>
										<td align="left"><strong> <c:if test="${subs != null}">
													<fmt:formatDate value="${subs.startDate}" pattern="dd/MM/yyyy" /> to 
															<fmt:formatDate value="${subs.endDate}" pattern="dd/MM/yyyy" />
													<c:choose>
														<c:when test="${subs != null and subs.subscriptionStatus == 'EXPIRED'}">
															<span style="color: red;"> (<spring:message code="account.overview.status.expired" />)
															</span>
														</c:when>
														<c:when test="${subs != null and subs.subscriptionStatus == 'ACTIVE'}">
															<span style="color: green;"> (<spring:message code="account.overview.status.active" />)
															</span>
														</c:when>
														<c:otherwise>
															<span style="color: blue;"> (<spring:message code="account.overview.status.notactive" />)
															</span>
														</c:otherwise>
													</c:choose>
												</c:if>
										</strong></td>
									</tr>
								</table>
								<h4 style="padding-top: 25px;">
									<spring:message code="account.overview.payment.description" />
								</h4>
								<div class="ph_table_border margin">
									<div class="mega range-header">
										<table class="header ph_table border-none layout-fixed" width="100%">
											<thead>
												<tr>
													<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.payment.time" /></th>
													<th class="width_200 width_400_fix align-left"><spring:message code="Product.remarks" /></th>
													<th class="width_150 width_150_fix align-left"><spring:message code="account.overview.ref.id" /></th>
													<th class="width_150 width_150_fix align-left"><spring:message code="account.overview.amount" /></th>
													<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.subscription.discount" /></th>
													<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.promotional.discount" /></th>
													<th class="width_100 width_100_fix align-left"><spring:message code="account.overview.tax" /></th>
													<th class="width_200 width_200_fix align-left"><spring:message code="account.overview.tax.description" /></th>
													<th class="width_200 width_200_fix align-left"><spring:message code="prtemplate.total.amount" /></th>
													<th class="width_100 width_100_fix align-left"><spring:message code="label.currency" /></th>
													<th class="width_200 width_200_fix align-left"><spring:message code="promotion.title" /></th>
												</tr>
											</thead>
										</table>
										<table class="data ph_table border-none layout-fixed" width="100%">
											<tbody>
												<c:if test="${empty subs.paymentTransactions}">
													<td valign="top" class="dataTables_empty" style="text-align: left;"><spring:message code="application.nodata" /></td>
												</c:if>
												<c:forEach items="${subs.paymentTransactions}" var="trans">
													<tr>
														<c:if test="${trans.referenceTransactionId ne null}">
															<td class="width_200 width_200_fix overflow-table align-left"><fmt:formatDate value="${trans.createdDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" /></td>
															<td class="width_200 width_400_fix overflow-table align-left">${trans.remarks}</td>
															<td class="width_150 width_150_fix overflow-table align-left">${trans.referenceTransactionId}</td>
															<td class="width_150 width_150_fix overflow-table align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.priceAmount}" /></td>
															<td class="width_200 width_200_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.priceDiscount != null ? -trans.priceDiscount : 0}" /></td>
															<td class="width_200 width_200_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.promoCodeDiscount != null ? -trans.promoCodeDiscount : 0}" /></td>
															<td class="width_100 width_100_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.additionalTax != null ? trans.additionalTax : 0}" /></td>
															<td class="width_200 width_200_fix overflow-table  align-left">${trans.additionalTaxDesc}</td>
															<td class="width_200 width_200_fix overflow-table  align-left"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trans.totalPriceAmount}" /></td>
															<td class="width_100 width_100_fix overflow-table align-left">${trans.currencyCode}</td>
															<td class="width_200 width_200_fix overflow-table align-left">${trans.promoCode != null ? trans.promoCode.promoCode : 'No Promotional'}</td>
														</c:if>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<c:choose>
					<c:when test="${subs.nextSubscription != null}">
						<c:set var="subs" value="${subs.nextSubscription}" />
					</c:when>
					<c:otherwise>
						<c:set var="subs" value="${null}" />
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</div>
</div>
<div class="modal fade" id="idRenewDialog" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="account.overview.renew" />
					${lastSubscription.plan.planName}
					<spring:message code="account.overview.subscription" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
				<!-- <div class="row">
					<div class="col-md-4">
						<h3>Plan Name</h3>
					</div>
					<div class="col-md-8">
						<h3></h3>
					</div>
				</div> -->
				<input type="hidden" id="currencyId" value="${lastSubscription.plan.currency.currencyCode}"> <input type="hidden" id="planType" value="${lastSubscription.planType}"> <input type="hidden" id="selectPeriodId" value="${lastSubscription.planPeriod.id}">
				<table id="rangeTable" class="marg-top-20">
					<fmt:formatNumber var="renewTaxFormt" type="number" minFractionDigits="0" maxFractionDigits="2" value="${tax}" />
					<c:forEach items="${lastSubscription.plan.rangeList}" var="range" varStatus="index">
						<tr class="row">
							<td class="col-md-6" data-id="${range.id}" data-start="${range.rangeStart}" data-end="${range.rangeEnd}" data-price="${range.price}">
								<h4 style="font-weight: bold; color: #3f96d8;">${range.displayLabel}${lastSubscription.plan.planType == 'PER_USER' ? ' Users': ' Events'}</h4>
							</td>
							<td class="col-md-6">${lastSubscription.plan.currency.currencyCode}<c:set var="itemTax" value="0" /> <span style="font-size: 23px;"> <c:if test="${not empty lastSubscription.plan.basePrice and index.index == 0 }">
										<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${lastSubscription.plan.basePrice}" />
										<fmt:formatNumber var="BasePrice" type="number" value="${lastSubscription.plan.basePrice}" />
										<input type="hidden" id="basePrice" value="${BasePrice}">
										<input type="hidden" id="baseUsers" value="${lastSubscription.plan.baseUsers}">
										<c:set var="itemTax" value="${BasePrice + ((BasePrice * renewTaxFormt)/100)}" />
									</c:if> <c:if test="${!(not empty lastSubscription.plan.basePrice and index.index == 0)}">
										<fmt:formatNumber var="rangePrice" type="number" minFractionDigits="2" maxFractionDigits="2" value="${range.price}" />
									${rangePrice}
									<c:set var="itemTax" value="${range.price + ((range.price * renewTaxFormt)/100)}" />
									</c:if>
							</span> <c:if test="${not empty lastSubscription.plan.basePrice and index.index == 0 }">
									/month
									</c:if> <c:if test="${!(not empty lastSubscription.plan.basePrice and index.index == 0)}">
								${lastSubscription.plan.planType == 'PER_USER' ? '/user/month': '/event'}
									</c:if> <br> (${itemTax} <spring:message code="account.overview.inclusive.of" /> ${renewTaxFormt}% <spring:message code="account.overview.gst" />)
							</td>
						</tr>
					</c:forEach>
				</table>
				<form id="idRenewSubscribeForm" action="${pageContext.request.contextPath}/buyer/billing/renew/${lastSubscription.plan.id}" method="post">
					<c:set var="tax" value="${not empty lastSubscription.plan.tax ? lastSubscription.plan.tax : 0}" />
					<input type="hidden" id="idRenewSubscribeFormPlanId" value="${lastSubscription.plan.id}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" id="renewTaxFormt" value="${renewTaxFormt}" />
					<!-- <div class="row marg-top-20">
						<div class="col-md-8">
							<input type="checkbox" id="autoChargeSubscription" name="autoChargeSubscription" class="custom-checkbox" /> <label style="line-height: 0px;"><spring:message code="account.overview.aucto.charge" /></label>
						</div>
						<div class="col-md-4"></div>
					</div> -->
					<div class="row marg-top-20">
						<div class="col-md-4">
							<label><spring:message code="account.overview.number.of" /> ${lastSubscription.plan.planType == 'PER_USER' ? 'User': 'Event'}</label>
						</div>
						<div class="col-md-8">
							<input type="text" class="form-control" name="numberUserEvent" id="numberUserEvent" value="${lastSubscription.planType == 'PER_USER' ? lastSubscription.userQuantity : lastSubscription.eventQuantity }" data-validation="required length number" data-validation-length="1-3"> <input type="hidden" name="rangeId" id="rangeId"> <span id="numberError"></span>
						</div>
					</div>
					<c:if test="${lastSubscription.plan.planType == 'PER_USER'}">
						<div class="row">
							<div class="col-md-12">
								<label><spring:message code="account.overview.subscription.period" /></label>
							</div>
						</div>
						<div class="periodRadioList">
							<c:forEach items="${lastSubscription.plan.planPeriodList}" var="period" varStatus="status">
								<div class="row">
									<div class="col-md-4"></div>
									<div class="col-md-8">
										<input type="radio" ${status.index eq 0 ?'checked':''} name="periodId" id="periodId" value="${period.id}" data-duration="${period.planDuration}" data-discount="${period.planDiscount}" class="periodDuration" /> <span style="font-size: 20px;">${period.planDuration} ${period.planDuration == '1' ? 'Month' : 'Months' } </span> &nbsp;&nbsp;&nbsp; <span>${period.planDiscount}</span> % Discount
									</div>
								</div>
							</c:forEach>
						</div>
					</c:if>
					<div class="row marg-top-20">
						<div class="col-md-4">
							<label><spring:message code="promo.code.title" /></label>
						</div>
						<div class="col-md-8">
							<input type="hidden" name="promoCodeId" id="promoCodeId"> <input type="text" class="form-control" id="promoCode" name="promoCode"> <span id="promoError"></span>
						</div>
					</div>
					<div class="row marg-top-20 flagvisibility" id="baseFeeTr">
						<div class="col-md-6">
							<label id="baseFeeLabel"></label>
						</div>
						<div class="col-md-3 align-right">
							<label id="baseFeeValue"></label>
						</div>
					</div>
					<div class="row marg-top-20" id="totalFeeTr">
						<div class="col-md-6">
							<label id="totalFeeLabel"></label>
						</div>
						<div class="col-md-3 align-right">
							<label id="totalFeeValue"></label>
						</div>
					</div>
					<!-- label to show free approver user -->
					<c:if test="${lastSubscription.plan.planType == 'PER_USER'}">
						<div class="row marg-top-20">
							<div class="col-md-6">
								<label id="approverUser"></label>
							</div>
							<div class="col-md-3 align-right">
								<label>0.00</label>
							</div>
						</div>
					</c:if>

					<div class="row marg-top-20 subsDiscount">
						<div class="col-md-6">
							<label id="totalFeeDiscountLabel"></label>
						</div>
						<div class="col-md-3 align-right">
							<label id="totalFeeDiscountValue"> </label>
						</div>
					</div>

					<div class="row marg-top-20">
						<div class="col-md-6">
							<label id="totalFeePromoLabel"></label>
						</div>
						<div class="col-md-3 align-right">
							<label id="totalFeePromoValue"> </label>
						</div>
					</div>

					<div class="row marg-top-20">
						<div class="col-md-6">
							<label><spring:message code="product.list.tax" /> ${renewTaxFormt}% <spring:message code="account.overview.gst" /></label>
						</div>
						<div class="col-md-3 align-right">
							<label id="renewTaxAmount"> </label>
						</div>
					</div>

					<div class="row marg-top-20">
						<div class="col-md-6" style="padding-right: 0px;">
							<label><spring:message code="account.overview.total.fee" /></label>
						</div>
						<div class="col-md-3 align-right">
							<label class="marg-right-10">${lastSubscription.currencyCode}</label>
							<label id="totalFeeAmount"> </label>
						</div>
					</div>

					<div class="row marg-top-20">
						<div class="col-md-3">&nbsp;</div>
						<div class="col-md-6">
							<div>
								<button type="button" value="Pay" id="pay_${plan.id}" plan-id="${lastSubscription.plan.id}" currency-code="${lastSubscription.plan.currency.currencyCode}" mode="renew"
								class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width stripe-payment-btn margin-bottom-15">Pay</button>
							</div>
						</div>
						<div class="col-md-3">&nbsp;</div>
					</div>
					<div class="row">
						<div class="col-md-2">&nbsp;</div>
						<div class="col-md-10">
							<!-- <img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges"> -->
						</div>
					</div>
					<!-- label to show free approver user -->
					<div class="col-md-12" style="margin-top: 15px">
						<span style='color: red;'> * </span>
						<spring:message code="account.overview.note.for" />
						<span id="userNo"><spring:message code="account.overview.each.other" /></span>
						<spring:message code="account.overview.approver.user" />
						</span>
						<spring:message code="account.overview.free" />
						.
					</div>
				</form>

			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="idUpdateSubsDialog" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<div id="UpdateSubsDialogText">
					<input type="text" class="form-control" id="updatePromoCode" name="updatePromoCode">
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="idconfirmPayment" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="account.overview.confirm.payment" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
			</div>


			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="prId" value="${pr.id}">
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="paymentbilling.sure.confirm.payment" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<a href="${pageContext.request.contextPath}/buyer/billing/capturePayment/${subscription.nextSubscription.id}" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left"><spring:message code="application.confirm" /></a>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="" payment-amount="">
		<div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
			<!-- Modal content-->
			<div class="modal-content">
				<input type="hidden" value="${subscription.id}" id="subscriptionId">
				<!-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> -->
				<div class="modal-body">
					<button class="close for-absulate" id="makePaymentModalCloseId" type="button"
						data-dismiss="modal">x</button>
					<div class="row">
						<div class="col-md-12">
							<h3 class="title-hero text-center" class="header-section">
								Choose Payment Mode
							</h3>
							<div class="example-box-wrapper">
								<ul class="list-group list-group-separator row list-group-icons alignment">
									<li class="col-md-5">
										<a href="#tab-example-1" id="tabOneId" data-toggle="tab" class="list-group-item">
											<i class="glyphicon glyphicon-credit-card" style="top: 2px;"></i>&nbsp;&nbsp;
											<spring:message code="payment.mode.card" />
										</a>
									</li>
									<li class="col-md-5" id="tabTwoIdParent">
											<a href="#tab-example-2" id="tabTwoId" data-toggle="tab"
												class="list-group-item">
												<div class="icon icon-fpx"></div>&nbsp;&nbsp;
												<spring:message code="payment.mode.fpx" />
											</a>
									</li>
								</ul>
								<div class="tab-content">
									<div class="panel">
										<div class="panel-body">
											<div class="tab-pane fade" id="tab-example-1">
												<div class="row col-12 alignment">
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
																			class="ph_btn_small btn-success full-width payment-btn btn alignment">
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
												<div class="row col-12 alignment">
													<img src="${pageContext.request.contextPath}/resources/assets/images/fpx-logo.png"
														class="payment-images">
												</div>
												<div id="fpxBlock">
													<div class="payment-block">
														<form id="payment-form-fpx">
															<div class="row">
																<div class="col-md-12 align-center">
																	<div id="fpx-bank-element"
																		class="form-control stripe-form-control"
																		style="padding-top: 0;"></div>
																</div>
															</div>
															<div class="row margin-top-15">
																<div class="col-md-12 align-center">
																	<button id="fpx-button" data-secret=""
																		class="btn-success no-border ph_btn_small full-width payment-btn btn alignment">
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

<script type="text/javascript" src="<c:url value="/resources/js/view/buyerAccountOverview.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({ lang : 'en' });
</script>
<script>
window.paypalCheckoutReady = function() {
		paypal.checkout.setup('${merchantId}', { environment : '${paypalEnvironment}', container : 'idRenewSubscribeForm', condition : function() {
			return $('#idRenewSubscribeForm').isValid();
		},
		//button: 'placeOrderBtn'
		buttons : [ { container : 'idButtonHolder', type : 'checkout', color : 'blue', size : 'medium', shape : 'rect' } ] });
	};
</script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script src="https://js.stripe.com/v3/"></script>

<style>
	.width_400_fix {
		min-width: 400px !important;
	}

	.ph_table.header th {
		padding: 14px 10px !important;
		color: #424242;
		font-weight: 600;
		font-size: 13px;
	}

	.ph_table_border,
	.margin {
		margin: 20px 20px 20px 0px;
	}

	.user-type button {
		width: auto;
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

	.margin-bottom-15 {
		margin-bottom: 15px;
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

	.pass-desc {
		color: #7f7f7f;
		font-weight: normal;
		/* 	font-size: 13px; */
	}

	.full-width {
		width: 100%;
	}

	.disabled {
		background: #aeaeae !important;
		color: #ababab !important;
	}

	.layout-fixed {
		table-layout: fixed;
	}

	.overflow-table {
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}

	.ph_table.data td {
		background: #fff none repeat scroll 0 0;
		border-bottom: 1px solid #ddd;
		color: #5d5d5d;
		font-size: 14px;
		padding: 15px 10px;
		border-top: 1px solid rgba(221, 221, 221, 0) !important;
	}

	.align-right {
		text-align: right;
	}
	
</style>