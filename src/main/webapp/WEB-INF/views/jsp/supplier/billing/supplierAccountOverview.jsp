<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="https://js.stripe.com/v3/"></script>

<spring:message var="supplierAccountOverviewDesk" code="application.supplier.account.overview" />
<script>
	zE(function () {
		zE.setHelpCenterSuggestions({ labels: [${ supplierAccountOverviewDesk }] });
	});
</script>
<script>
	$(document).ready(function () {
		$('.mega').on('scroll', function () {
			$('.header').css('top', $(this).scrollTop());
		});
	});
</script>
<div id="page-content" view-name="supplierAccountOverview">
	<div class="container">
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<input type="hidden" id="stripePublishKey" value="${publicKey}">
		<jsp:useBean id="now" class="java.util.Date" />
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/supplier/supplierDashboard">
					<spring:message code="application.dashboard" /></a></li>
			<li class="active">
				<spring:message code="paymentbilling.account.overview" />
			</li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap acc-over">
				<spring:message code="paymentbilling.account.overview" />
			</h2>
		</div>
		<div class="clear"></div>
		<c:if test="${supplier.supplierPackage != null}">
			<div class="Invited-Supplier-List create_sub marg-bottom-20">
				<div class="col-md-20">
					<c:if test="${!empty supplier.supplierPackage.endDate}">
						<div class="col-md-8 marg-top-10">
							<h4>
								<spring:message code="supplierbilling.current.subscription.expires" />
								<fmt:formatDate value="${supplier.supplierPackage.endDate}" pattern="dd/MM/yyyy"/> 
							</h4>
						</div>
					</c:if>
					<div class="marg-top-10">

						<c:if test="${empty subscription.id or empty subscription.supplierPlan.id}">
							<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan"
								class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10 marg-bottom-10"
								style="float: right;">
								<spring:message code="supplier.dashboard.subscribe" /></a>
						</c:if>

						<c:if test="${not empty subscription.supplierPlan.id and subscription.supplierPlan.buyerLimit > 1 and supplier.supplierPackage.endDate > now}">
							<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10 marg-bottom-10"
							style="float: right;"><spring:message code="account.overview.renew"/></a>
						</c:if>

						<c:if test="${not empty subscription.supplierPlan.id and subscription.supplierPlan.buyerLimit == 1}">
							<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10 marg-bottom-10"
							style="float: right;"><spring:message code="supplier.dashboard.upgrade"/></a>
						</c:if>


						<c:if test="${not empty subscription.supplierPlan.id and now > supplier.supplierPackage.endDate}">
							<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan"
								class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10 marg-bottom-10"
								style="float: right;">
								<spring:message code="supplier.dashboard.subscribe" /></a>
						</c:if>


					</div>
				</div>
			</div>
		</c:if>
		<div class="Invited-Supplier-List marg-bottom-20 bill-detail">
			<div class="Invited-Supplier-List-table add-supplier white-bg">
				<div class="ph_tabel_wrapper">

					<c:if test="${supplierCountry != 'MY'}">
						<c:set var="tax" value="0" />
						<c:set var="amount" value="${subscription.supplierPlan.price}" />
					</c:if>
					<c:if test="${supplierCountry == 'MY'}">
						<c:set var="tax" value="${supplier.supplierSubscription.supplierPlan.tax}"/>
						<c:set var="amount" value="${subscription.supplierPlan.price}" />
					</c:if>

					<input type="hidden" value="${tax}" id="tax">
					<input type="hidden" value="${amount}" id="amount">
					<div class="account-overview">
						<table class="border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
							<tbody>
								<tr>
									<td style="vertical-align: top;">
										<spring:message code="account.overview.plan" />
									</td>
									<td><b> ${supplier.supplierPackage == null ? "No Subscription" :
										(supplier.supplierPackage.supplierPlan != null
										?supplier.supplierPackage.supplierPlan.planName:"FREE TRIAL")}</b>
										${supplier.supplierPackage == null ? "" : "<br />"}
										${supplier.supplierPackage
									== null ? "" : supplier.supplierPackage.supplierPlan.shortDescription}</td>
								</tr>
								<tr>
									<td>Subscription Status</td>
									<td><span
											style="color: ${supplier.supplierPackage != null ? (supplier.supplierPackage.subscriptionStatus == 'ACTIVE' ? 'green;' : '') : ''}">
											${supplier.supplierPackage != null ?
											supplier.supplierPackage.subscriptionStatus : 'Not Applicable'}</span></td>
								</tr>
								<tr>
									<td>
										<spring:message code="account.overview.subscription.valid" />
									</td>
									<td>
										<strong>
										 <c:if test="${supplier.supplierPackage != null}">
												<fmt:formatDate
													value="${supplier.supplierPackage.startDate}"
													pattern="dd/MM/yyyy" />
												to
												<fmt:formatDate value="${supplier.supplierPackage.endDate}"
													pattern="dd/MM/yyyy" />
												<c:choose>
													<c:when
														test="${supplier.supplierPackage != null and supplier.supplierPackage.endDate != null and (now gt supplier.supplierPackage.endDate)}">
														<span style="color: red;"> ( <spring:message
																code="account.overview.status.expired" />)
														</span>
													</c:when>
													<c:when
														test="${supplier.supplierPackage != null and supplier.supplierPackage.endDate != null and supplier.supplierPackage.startDate != null and (now ge supplier.supplierPackage.startDate and now le supplier.supplierPackage.endDate)}">
														<span style="color: green;">( <spring:message
																code="account.overview.status.active" />)
														</span>
													</c:when>
													<c:otherwise>
														<span style="color: blue;">( <spring:message
																code="account.overview.status.notactive" /> )
														</span>
													</c:otherwise>
												</c:choose>
											</c:if> <c:if test="${supplier.supplierPackage == null }">
												<spring:message code="application.not.applicable2" />
											</c:if>
									</strong>
									</td>
								</tr>
								<tr>
									<td><spring:message code="accountoverview.buyer.limit" />
									</td>
									<td>
										<strong> 
											<c:if test="${supplier.supplierPackage != null}">
												${supplier.supplierPackage.buyerLimit >= 999 ? 'All Buyers' :
												supplier.supplierPackage.buyerLimit }
											</c:if> 
											<c:if test="${supplier.supplierPackage == null }">
													<spring:message code="application.not.applicable2" />
											</c:if>
										</strong>
									</td>
								</tr>
								<c:if test="${!empty supplier.associatedBuyers}">
									<tr>
										<td style="vertical-align: top;"><spring:message
												code="accountoverview.associated.buyers" /></td>
										<td>
											<ul style="padding-left: 20px;">
												<c:forEach items="${supplier.associatedBuyers}" var="buyer">
													<li>${buyer.companyName}</li>
												</c:forEach>
											</ul>
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="" payment-amount="">
		<div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
			<!-- Modal content-->
			<div class="modal-content">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<button class="close for-absulate" id="makePaymentModalCloseId" type="button"
						data-dismiss="modal">x</button>
					<div class="row">
						<div class="col-md-12">
							<h3 class="title-hero" class="header-section">
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
	
									<c:if test="${subscription.currencyCode == 'MYR'}">
										<li class="col-md-5">
											<a href="#tab-example-2" id="tabTwoId" data-toggle="tab"
												class="list-group-item">
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
												<div class="row col-12">
													<img src="${pageContext.request.contextPath}/resources/assets/images/cards-logo.jpeg"
														class="payment-images-card">
												</div>
												<div class="row">
													<div class="col-12">
														<div class="plan-description">Making Payment for
															${subscription.supplierPlan != null ?
															subscription.supplierPlan.planName : ''} for an amount of ${subscription.supplierPlan != null ?
																price :
															''}$ for ${subscription.supplierPlan != null ?
															subscription.supplierPlan.period : ''}
															${subscription.supplierPlan != null ?
															subscription.supplierPlan.periodUnit : ''} ( Inclusive of ${subscription.supplierPlan != null ?
																tax : ''} % SST )</div>
													</div>
												</div>
												<div id="cardBlock">
													<div class="sr-root">
														<div class="sr-main">
															<form id="payment-form" class="sr-payment-form">
																<div
																	class="margin-top-15 parent-card-div row center-align-row">
																	<div class="col-md-12 padding-left-right-0">
																		<div class="sr-combo-inputs-row">
																			<div class="sr-input sr-card-element form-control"
																				id="card-element"></div>
																		</div>
																	</div>
																</div>
																<div class="sr-field-error" id="card-errors" role="alert">
																</div>
																<div class="margin-top-15 parent-card-div row">
																	<div class="col-md-12">
																		<button id="payByCardId" type="button"
																			class="ph_btn_small btn-success width-100 payment-btn btn">
																			Pay&nbsp;${subscription.supplierPlan.currency.currencyCode}&nbsp;<span id="card_span"></span>
																		</button>
																	</div>
																</div>
															</form>
														</div>
													</div>
												</div>
											</div>
											<div class="tab-pane fade" id="tab-example-2">
												<div class="row col-12">
													<img src="${pageContext.request.contextPath}/resources/assets/images/fpx-logo.png"
														class="payment-images">
												</div>
												<div class="row">
													<div class="col-12">
														<div class="plan-description">Making Payment for
															${subscription.supplierPlan != null ?
															subscription.supplierPlan.planName : ''} for an amount of ${subscription.supplierPlan != null ?
															price :
															''}$ for ${subscription.supplierPlan != null ?
															subscription.supplierPlan.period : ''}
															${subscription.supplierPlan != null ?
															subscription.supplierPlan.periodUnit : ''} ( Exclusive of ${subscription.supplierPlan != null ?
																tax : ''} % SST )</div>
													</div>
												</div>
												<div id="fpxBlock">
													<div class="payment-block">
														<form id="payment-form-fpx">
															<div class="row">
																<div class="col-md-12 align-center">
																	<div id="fpx-bank-element" class="form-control"
																		style="padding-top: 0;"></div>
																</div>
															</div>
															<div class="row margin-top-15">
																<div class="col-md-12 align-center">
																	<button id="fpx-button" data-secret=""
																		class="btn-success no-border ph_btn_small width-100 payment-btn btn">
																		Pay&nbsp;${subscription.supplierPlan.currency.currencyCode}&nbsp;<span id="fpx_span"></span>
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
</div>