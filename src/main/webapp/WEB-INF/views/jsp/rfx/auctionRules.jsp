<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<title><spring:message code="auctionRules.title" /></title>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<!-- pageging  block -->
				<ol class="breadcrumb">
					<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard"/></a></li>
					<li class="active">${eventType.value}</li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier"><spring:message code="summarydetails.auctionrules.title" /></h2>
						<h2 class="pull-right status " style="color: white;"><spring:message code="application.status" /> : ${event.status}</h2>
					</div>
					<jsp:include page="eventHeader.jsp"></jsp:include>
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<input type="hidden" id="eventDecimal" value="${event.decimal}"> <input type="hidden" value="${event.id}" id="eventId">
					<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="auctionRules" action="${pageContext.request.contextPath}/buyer/${eventType}/auctionRules">
						<form:hidden path="id" />
						<form:hidden path="event" id="event" />
						<form:hidden path="auctionType" />
						<form:hidden path="fowardAuction" />
						<div class="tab-pane active error-gap-div" id="step-1">
							<c:if test="${!(auctionRules.auctionType == 'FORWARD_DUTCH' or auctionRules.auctionType == 'REVERSE_DUTCH')}">
								<div class="upload_download_wrapper clearfix event_info">
									<h4><spring:message code="summarydetails.auctionrules.prebid.setting" /></h4>
									<div class="form-tander1 requisition-summary-box marg-bottom-10 marg-top-10">
										<div class="col-sm-5 col-md-3 col-xs-6">
											<form:label path="preBidBy"><spring:message code="auctionrules.initial.bid.price" /></form:label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-lg-4 setPrebidBy">
											<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'PRE_BID_BY')) : 'false') or (event.status =='SUSPENDED') }">
												<input type="hidden" name="preBidBy" value="${auctionRules.preBidBy}" />
											</c:if>
											<form:select path="preBidBy" cssClass="chosen-select disablesearch form-control autoSave" id="idPreBidBy" disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'PRE_BID_BY' )? 'true' : 'false') : 'false') or (event.status =='SUSPENDED' ? 'true' : 'false') }" style="${!empty templateFields ? (tf:readonly( templateFields, 'PRE_BID_BY' )? 'opacity:0' : '') : ''  }">
												<form:options items="${preBidByList}" />
											</form:select>
										</div>
									</div>
									<div class="form-tander1 requisition-summary-box ">
										<div class="col-sm-5 col-md-3 col-xs-6"></div>
										<div class="col-sm-5 col-md-5 col-xs-6 sup-pro">
											<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PRE_SUPPLIER_PROVIDE_HIGHER' )) : 'true' }">
												<div class=" radio-primary small-radio-btn1 lowerPriceSetting">
													<c:if test="${auctionRules.fowardAuction}">
														<form:label path="isPreBidHigherPrice" class="${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SUPPLIER_PROVIDE_HIGHER' )? 'disabled' : '') : '' }">
															<form:checkbox id="inlineRadio110" path="isPreBidHigherPrice" value="1" class="autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SUPPLIER_PROVIDE_HIGHER' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.supplier.higher.price" />
											</form:label>
													</c:if>
													<c:if test="${!auctionRules.fowardAuction}">
														<form:label path="isPreBidHigherPrice" class="${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SUPPLIER_PROVIDE_HIGHER' )? 'disabled' : '') : '' }">
															<form:checkbox id="inlineRadio110" path="isPreBidHigherPrice" value="1" class="${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SUPPLIER_PROVIDE_HIGHER' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.supplier.lower.price" />
											</form:label>
													</c:if>
												</div>
											</c:if>
											<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PRE_SUPPLIER_SAME_BID' )) : 'true' }">
												<form:label class="width-100 float-left ${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SUPPLIER_SAME_BID' )? 'disabled' : '') : '' }" path="isPreBidSameBidPrice">
													<form:checkbox id="inlineCheckbox114" path="isPreBidSameBidPrice" value="1" class="autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SUPPLIER_SAME_BID' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.allow.same.pre.bid" />
												</form:label>
											</c:if>
											
											<input type="hidden" id="idPreBidPricingExist"  value="${isPreBidPricingExist}" />
											<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PRE_SET_SAME_PRE_BID_ALL_SUPPLIER' )) : 'true' }">
												<div class="allowSamePreSetBidForAllSuppliers">
													<form:label path="preSetSamePreBidForAllSuppliers" class="width-100 float-left  ${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SET_SAME_PRE_BID_ALL_SUPPLIER' )? 'disabled' : '') : '' }" >
																<form:checkbox id="idPreSetSamePreBidForAllSuppliers" path="preSetSamePreBidForAllSuppliers" value="1" class="autoSave  ${!empty templateFields ? (tf:readonly( templateFields, 'PRE_SET_SAME_PRE_BID_ALL_SUPPLIER' )? 'disabled' : '') : '' }" /><spring:message code="auction.rules.pre.bid.preset" />
													</form:label>
												</div>
											</c:if>
										</div>
									</div>
								</div>
								<div class="upload_download_wrapper clearfix event_info marg-top-20">
									<h4 class="marg-none"><spring:message code="auctionrules.bid.price.setting" /></h4>
									<div class="form-tander1 requisition-summary-box pad_all_15">
										<div class="bid-price-inner">
											<div class="col-md-12 col-sm-12 col-xs-12">
												<label><spring:message code="auctionrules.bidding.type" /></label>
											</div>
											<div class="row marg-bottom-10">
												<div class="col-md-12 col-sm-12 col-xs-12">
													<div class="col-md-4 col-sm-6 col-xs-6">
														<form:label path="itemizedBiddingWithTax" class="width-100 float-left"><spring:message code="auctionrules.itemized.bidding" /></form:label>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-6">
														<form:label path="itemizedBiddingWithTax" class="${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }">
															<form:radiobutton id="idItemizedBidding1" path="itemizedBiddingWithTax" value="0" class="autoSave radioChangeItem ${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }" /> <spring:message code="auctionrules.option.without.tax" />
											</form:label>
														<form:label path="itemizedBiddingWithTax" class="${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }">
															<form:radiobutton id="idItemizedBidding" path="itemizedBiddingWithTax" value="1" class="autoSave radioChangeItem ${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }" /> <spring:message code="auctionrules.option.with.tax" />
											</form:label>
													</div>
												</div>
												<div class="col-md-12 col-sm-12 col-xs-12">
													<div class="col-md-4 col-sm-6 col-xs-6">
														<form:label path="lumsumBiddingWithTax" class="width-100 float-left"><spring:message code="auctionrules.lumpsum.bid" /></form:label>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-6">
														<form:label path="lumsumBiddingWithTax" class="${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }">
															<form:radiobutton id="idLumsumBidding1" path="lumsumBiddingWithTax" value="0" class="radioChangeLumsum autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }" /> <spring:message code="auctionrules.option.without.tax" />
											</form:label>
														<form:label path="lumsumBiddingWithTax" class="${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }">
															<form:radiobutton id="idLumsumBidding" path="lumsumBiddingWithTax" value="1" class="radioChangeLumsum autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'BIDDING_TYPE' )? 'disabled' : '') : '' }" /> <spring:message code="auctionrules.option.with.tax" />
											</form:label>
													</div>
												</div>
											</div>
											<c:if test="${!(auctionRules.auctionType == 'FORWARD_SEALED_BID' or auctionRules.auctionType == 'REVERSE_SEALED_BID')}">
												<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PRE_AS_FIRSTBID' )) : 'true' }">
													<div class="row marg-bottom-10">
														<div class="col-sm-12 col-md-9 col-xs-6 col-lg-8">
																<form:label path="prebidAsFirstBid" class="width-100 float-left  ${!empty templateFields ? (tf:readonly( templateFields, 'PRE_AS_FIRSTBID' )? 'disabled' : '') : '' }">
																	<form:checkbox id="idprebidAsFirstBid" path="prebidAsFirstBid" class="autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'PRE_AS_FIRSTBID' )? 'disabled' : '') : '' }" /><spring:message code="rfx.pre.bid.price" />
												</form:label>
														</div>
													</div>
												</c:if>
												<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'START_GATE' )) : 'true' }">
													<div class="row marg-bottom-10">
														<div class="col-sm-12 col-md-9 col-xs-6 col-lg-8">
															<c:if test="${auctionRules.fowardAuction}">
																<form:label path="isStartGate" class="width-100 float-left  ${!empty templateFields ? (tf:readonly( templateFields, 'START_GATE' )? 'disabled' : '') : '' }">
																	<form:checkbox id="idStartGate" path="isStartGate" class="autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'START_GATE' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.start.gate.increase" />
												</form:label>
															</c:if>
															<c:if test="${!auctionRules.fowardAuction}">
																<form:label path="isStartGate" class="width-100 float-left  ${!empty templateFields ? (tf:readonly( templateFields, 'START_GATE' )? 'disabled' : '') : '' }">
																	<form:checkbox id="idStartGate" path="isStartGate" class="autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'START_GATE' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.start.gate.lower" />
												</form:label>
															</c:if>
														</div>
													</div>
												</c:if>
											</c:if>
											<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )) : 'true' }">
												<div class="row marg-bottom-10">
													<div class="col-md-4 col-sm-6 col-xs-6">
														<c:if test="${auctionRules.fowardAuction}">
															<form:label path="isBiddingMinValueFromPrevious" class="width-100 float-left  ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )? 'disabled' : '') : '' }">
																<form:checkbox id="idBidFromPre" path="isBiddingMinValueFromPrevious" class="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.min.increment" />
												</form:label>
														</c:if>
														<c:if test="${!auctionRules.fowardAuction}">
															<form:label path="isBiddingMinValueFromPrevious" class="width-100 float-left  ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )? 'disabled' : '') : '' }">
																<form:checkbox id="idBidFromPre" path="isBiddingMinValueFromPrevious" class="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.min.decrement" />
												</form:label>
														</c:if>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-6 bidFromPreHide">
														<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'IS_BID_INCR_OWN_PREVIOUS')) : 'false')  or (event.status =='SUSPENDED')}">
															<input type="hidden" name="biddingMinValueType" value="${auctionRules.biddingMinValueType}" />
														</c:if>
														<form:select path="biddingMinValueType" cssClass="chosen-select disablesearch autoSave" id="idMinIncrementValue" disabled="${event.status =='SUSPENDED' ? 'true' : 'false'}">
															<form:options items="${minIncrementValueList}" disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )? 'true' : 'false') : 'false')}" style="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )? 'opacity:0' : '') : ''  }" />
														</form:select>
													</div>
												</div>
												<div class="row marg-bottom-10 bidFromPreHide">
													<div class=" col-md-offset-4  col-sm-5 col-md-5 col-xs-6 col-lg-4 autoSave">
														<c:if test="${auctionRules.biddingMinValueType eq 'PERCENTAGE'}">
															<fmt:formatNumber var="biddingMinValue" type="number" minFractionDigits="0" maxFractionDigits="2" groupingUsed="false" value="${auctionRules.biddingMinValue}" />
														</c:if>
														<c:if test="${auctionRules.biddingMinValueType eq 'VALUE'}">
															<fmt:formatNumber var="biddingMinValue" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${auctionRules.biddingMinValue}" />
														</c:if>
														<form:input path="biddingMinValue" value="${biddingMinValue}" class="form-control ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )? 'disabled' : '') : '' }" data-validation="required validate_customlength" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )) : 'false'}" />
													</div>
												</div>
											</c:if>
										</div>
										<c:if test="${!(auctionRules.auctionType == 'FORWARD_SEALED_BID' or auctionRules.auctionType == 'REVERSE_SEALED_BID')}">
											<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'IS_BID_HIGHER_LEADING' )) : 'true' }">
												<div class="bid-price-inner">
													<div class="row marg-bottom-20">
														<div class="col-sm-4 col-md-4 col-xs-6 col-lg-4">
															<form:label path="isBiddingAllowSupplierSameBid" class="width-100 float-left ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'disabled' : '') : '' }">
																<form:checkbox id="idSameBidSupp" path="isBiddingAllowSupplierSameBid" value="1" class="autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.allow.same.bid" />
												</form:label>
														</div>
														<div class="col-md-5 col-sm-5 col-xs-5 col-lg-5">
															<c:if test="${auctionRules.fowardAuction}">
																<form:label path="isBiddingPriceHigherLeadingBid" class="width-100 float-left ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'disabled' : '') : '' }">
																	<form:checkbox path="isBiddingPriceHigherLeadingBid" id="idBidFromLead" value="1" class="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.price.higher.leadbid" />
												</form:label>
															</c:if>
															<c:if test="${!auctionRules.fowardAuction}">
																<form:label path="isBiddingPriceHigherLeadingBid" class="width-100 float-left ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'disabled' : '') : '' }">
																	<form:checkbox path="isBiddingPriceHigherLeadingBid" id="idBidFromLead" value="1" class="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'disabled' : '') : '' }" /><spring:message code="auctionrules.price.lower.leadbid" />
												</form:label>
															</c:if>
														</div>
													</div>
													<div class="row marg-bottom-20">
														<div class=" col-md-offset-4  col-sm-5 col-md-5 col-xs-6 col-lg-4 hideDivFromLead">
															<c:if test="${!empty templateFields ? (tf:readonly(templateFields, 'IS_BID_HIGHER_LEADING')) : 'false' }">
																<input type="hidden" name="biddingPriceHigherLeadingBidType" value="${auctionRules.biddingPriceHigherLeadingBidType}" />
															</c:if>
															<form:select path="biddingPriceHigherLeadingBidType" cssClass="chosen-select disablesearch autoSave" id="idpriceHigherLeadingBid">
																<form:options items="${priceHigherLeadingBidList}" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'true' : 'false') : 'false' }" />
															</form:select>
														</div>
													</div>
													<div class="row marg-bottom-20">
														<div class=" col-md-offset-4  col-sm-5 col-md-5 col-xs-6 col-lg-4 hideDivFromLead autoSave">
															<c:if test="${auctionRules.biddingPriceHigherLeadingBidType eq 'PERCENTAGE'}">
																<fmt:formatNumber var="biddingPriceHigherLeadingBidValue" type="number" minFractionDigits="0" maxFractionDigits="2" groupingUsed="false" value="${auctionRules.biddingPriceHigherLeadingBidValue}" />
															</c:if>
															<c:if test="${auctionRules.biddingPriceHigherLeadingBidType eq 'VALUE'}">
																<fmt:formatNumber var="biddingPriceHigherLeadingBidValue" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${auctionRules.biddingPriceHigherLeadingBidValue}" />
															</c:if>
															<form:input path="biddingPriceHigherLeadingBidValue" value="${biddingPriceHigherLeadingBidValue}" data-validation-optional="true" class=" form-control ${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_HIGHER_LEADING' )? 'disabled' : '') : '' }" data-validation="required validate_customlength" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'IS_BID_INCR_OWN_PREVIOUS' )) : 'false'}" />
														</div>
													</div>
											</div>
											   </c:if>
										</c:if>
										<c:if test="${auctionRules.auctionType == 'FORWARD_ENGISH' or auctionRules.auctionType == 'REVERSE_ENGISH'}">
											<div class="bid-price-inner-first">
												<label><spring:message code="auctionrules.buyer.console.setting" /></label>
												<div class="row">
													<div class="form-tander1 requisition-summary-box">
														<div class="col-sm-5 col-md-3 col-xs-6">
															<label> <spring:message code="auctionrules.initial.price" /></label>
														</div>
														<div class="col-sm-7 col-md-6 col-md-offset-1 col-xs-6 col-lg-5">
															<div class="auction-Console-radio autoSave">
																<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'AUCTION_PRICE_TYPE')) : 'false') or (event.status =='SUSPENDED') }">
																	<input type="hidden" name="buyerAuctionConsolePriceType" value="${auctionRules.buyerAuctionConsolePriceType}" />
																</c:if>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_ALL" path="buyerAuctionConsolePriceType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_PRICE_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.showall" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1 ">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_LEADING" path="buyerAuctionConsolePriceType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_PRICE_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.leading" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_NONE" path="buyerAuctionConsolePriceType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_PRICE_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.none" />
																	</label>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="form-tander1 requisition-summary-box">
														<div class="col-sm-5 col-md-3 col-xs-6">
															<label><spring:message code="supplier.name" /></label>
														</div>
														<div class="col-sm-7 col-md-offset-1 col-md-6 col-xs-6 col-lg-5">
															<div class="auction-Console-radio autoSave">
																<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'AUCTION_SUPPLIER_TYPE')) : 'false') or (event.status =='SUSPENDED') }">
																	<input type="hidden" name="buyerAuctionConsoleVenderType" value="${auctionRules.buyerAuctionConsoleVenderType}" />
																</c:if>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_ALL" path="buyerAuctionConsoleVenderType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_SUPPLIER_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.showall" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_LEADING" path="buyerAuctionConsoleVenderType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_SUPPLIER_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.leading" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_NONE" path="buyerAuctionConsoleVenderType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_SUPPLIER_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.none" />
																	</label>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="form-tander1 requisition-summary-box">
														<div class="col-sm-5 col-md-3 col-xs-6">
															<label><spring:message code="summarydetails.auctionrules.rank" /></label>
														</div>
														<div class="col-sm-7 col-md-offset-1 col-md-6 col-xs-6 col-lg-5">
															<div class="auction-Console-radio autoSave">
																<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'AUCTION_RANK_TYPE')) : 'false') or (event.status =='SUSPENDED') }">
																-	<input type="hidden" name="buyerAuctionConsoleRankType" value="${auctionRules.buyerAuctionConsoleRankType}" />
																</c:if>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_ALL" path="buyerAuctionConsoleRankType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_RANK_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.showall" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_LEADING" path="buyerAuctionConsoleRankType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_RANK_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.leading" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_NONE" path="buyerAuctionConsoleRankType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_RANK_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.none" />
																	</label>
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
											<!-- for buyer action console setting -->
											<div class="bid-price-inner-first">
												<label><spring:message code="auctionrules.supplier.console.setting" /></label>
												<div class="row">
													<div class="form-tander1 requisition-summary-box">
														<div class="col-sm-5 col-md-3 col-xs-6">
															<label><spring:message code="auctionrules.initial.price" /> </label>
														</div>
														<div class="col-sm-7 col-md-6 col-md-offset-1 col-xs-6 col-lg-5">
															<div class="auction-Console-radio autoSave">
																<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'AUCTION_PRICE_TYPE')) : 'false') or (event.status =='SUSPENDED') }">
																	<input type="hidden" name="auctionConsolePriceType" value="${auctionRules.auctionConsolePriceType}" />
																</c:if>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_ALL" path="auctionConsolePriceType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_PRICE_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.showall" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1 ">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_LEADING" path="auctionConsolePriceType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_PRICE_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.leading" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_NONE" path="auctionConsolePriceType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_PRICE_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.none" />
																	</label>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="form-tander1 requisition-summary-box">
														<div class="col-sm-5 col-md-3 col-xs-6">
															<label><spring:message code="supplier.name" /></label>
														</div>
														<div class="col-sm-7 col-md-offset-1 col-md-6 col-xs-6 col-lg-5">
															<div class="auction-Console-radio autoSave">
																<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'AUCTION_SUPPLIER_TYPE')) : 'false') or (event.status =='SUSPENDED') }">
																	<input type="hidden" name="auctionConsoleVenderType" value="${auctionRules.auctionConsoleVenderType}" />
																</c:if>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_ALL" path="auctionConsoleVenderType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_SUPPLIER_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.showall" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_LEADING" path="auctionConsoleVenderType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_SUPPLIER_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.leading" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_NONE" path="auctionConsoleVenderType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_SUPPLIER_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.none" />
																	</label>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="form-tander1 requisition-summary-box">
														<div class="col-sm-5 col-md-3 col-xs-6">
															<label><spring:message code="summarydetails.auctionrules.rank" /></label>
														</div>
														<div class="col-sm-7 col-md-offset-1 col-md-6 col-xs-6 col-lg-5">
															<div class="auction-Console-radio autoSave">
																<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'AUCTION_RANK_TYPE')) : 'false') or (event.status =='SUSPENDED') }">
																	<input type="hidden" name="auctionConsoleRankType" value="${auctionRules.auctionConsoleRankType}" />
																</c:if>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_ALL" path="auctionConsoleRankType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_RANK_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.showall" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_MY_RANK" path="auctionConsoleRankType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_RANK_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.rank" />
																	</label>
																</div>
																<div class=" radio-primary small-radio-btn1">
																	<label> <form:radiobutton id="inlineRadio110" value="SHOW_NONE" path="auctionConsoleRankType" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'AUCTION_RANK_TYPE' )? 'true' : 'false') : 'false' }" /> <spring:message code="auctionrules.option.show.none" />
																	</label>
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
										</c:if>
									</div>
								</div>
							</c:if>
							<c:if test="${auctionRules.auctionType == 'FORWARD_DUTCH' or auctionRules.auctionType == 'REVERSE_DUTCH'}">
								<div class="upload_download_wrapper clearfix event_info marg-top-20">
									<h4><spring:message code="auctionrules.auction.setting" /></h4>
									<div class="form-tander1 requisition-summary-box marg-bottom-10 marg-top-10">
										<div class="col-sm-5 col-md-3 col-xs-6">
											<form:label path="dutchStartPrice"><spring:message code="auctionrules.start.price" /></form:label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-lg-4">
											<c:choose>
												<c:when test="${event.decimal==1}">
													<c:set var="decimalSet" value="0,0.0"></c:set>
												</c:when>
												<c:when test="${event.decimal==2}">
													<c:set var="decimalSet" value="0,0.00"></c:set>
												</c:when>
												<c:when test="${event.decimal==3}">
													<c:set var="decimalSet" value="0,0.000"></c:set>
												</c:when>
												<c:when test="${event.decimal==4}">
													<c:set var="decimalSet" value="0,0.0000"></c:set>
												</c:when>
												<c:when test="${event.decimal==5}">
													<c:set var="decimalSet" value="0,0.00000"></c:set>
												</c:when>
												<c:when test="${event.decimal==6}">
													<c:set var="decimalSet" value="0,0.000000"></c:set>
												</c:when>
											</c:choose>
											<fmt:formatNumber var="dutchStartPrice" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${auctionRules.dutchStartPrice}" />
											<form:input path="dutchStartPrice" id="dutchStartPrice" class="form-control autoSave" data-validation="required negative_value validate_customlength" value="${dutchStartPrice}" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}" />
										</div>
									</div>
									<div class="form-tander1 requisition-summary-box marg-bottom-10 marg-top-10">
										<div class="col-sm-5 col-md-3 col-xs-6">
											<c:if test="${auctionRules.fowardAuction}">
												<form:label path="dutchMinimumPrice"><spring:message code="auctionrules.min.price" /></form:label>
											</c:if>
											<c:if test="${!auctionRules.fowardAuction}">
												<form:label path="dutchMinimumPrice"><spring:message code="auctionrules.max.price" /></form:label>
											</c:if>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-lg-4">
											<fmt:formatNumber var="dutchMinimumPrice" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${auctionRules.dutchMinimumPrice}" />
											<form:input path="dutchMinimumPrice" class="form-control autoSave" id="dutchMinimumPrice" data-validation="required negative_value validate_customlength ${auctionRules.fowardAuction ? 'validate_forward' : 'validate_reverse'}" value="${dutchMinimumPrice}" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}" />
										</div>
									</div>
									<div class="form-tander1 requisition-summary-box marg-bottom-10 marg-top-10">
										<div class="col-sm-5 col-md-3 col-xs-6">
											<c:if test="${auctionRules.fowardAuction}">
												<form:label path="amountPerIncrementDecrement"><spring:message code="auctionrules.amount.decrement" /></form:label>
											</c:if>
											<c:if test="${!auctionRules.fowardAuction}">
												<form:label path="amountPerIncrementDecrement"><spring:message code="auctionrules.amount.increment" /></form:label>
											</c:if>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-lg-4">
											<fmt:formatNumber var="amountPerIncrementDecrement" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${auctionRules.amountPerIncrementDecrement}" />
											<form:input path="amountPerIncrementDecrement" class="form-control autoSave" data-validation="required negative_value validate_customlength" value="${amountPerIncrementDecrement}" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}" />
										</div>
									</div>
									<div class="form-tander1 requisition-summary-box marg-bottom-10 marg-top-10">
										<div class="col-sm-5 col-md-3 col-xs-6">
											<c:if test="${auctionRules.fowardAuction}">
												<form:label path="interval"><spring:message code="auctionrules.interval.decrement" /></form:label>
											</c:if>
											<c:if test="${!auctionRules.fowardAuction}">
												<form:label path="interval"><spring:message code="auctionrules.interval.increment" /></form:label>
											</c:if>
										</div>
										<div class="col-sm-3 col-md-3 col-xs-4 col-lg-2">
											<form:input path="interval" class="form-control autoSave" data-validation="required number length" data-validation-length="max3" />
										</div>
										<div class="col-sm-2 col-md-2 col-xs-2 col-lg-2">
											<form:select path="intervalType" class="chosen-select disablesearch autoSave" data-validation="required">
												<form:options items="${intervalTypeList}" />
											</form:select>
										</div>
									</div>
								</div>
							</c:if>
							<!-- <div class="float-left width-100">
								<div class="row">
									<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20">
										<input type="button" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1" value="Previous" /> <input type="submit" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out marg-left-10" value="Next" />
									</div>
								</div>
							</div> -->
						</div>
					</form:form>
					<div class="marg-top-20 btns-lower">
						<div class="row">
							<div class="col-md-12 col-xs-12 col-ms-12">
								<form:form class="bordered-row pull-left" id="submitPriviousForm" method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/eventCreationPrevious">
									<form:hidden path="id" />
									<form:button type="submit" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1 previousStep1" value="Previous" name="previous" id="priviousStep">
										<spring:message code="application.previous" />
									</form:button>
								</form:form>
								<spring:message code="application.next" var="next" />
								<input type="button" class="btn btn-info ph_btn marg-left-10 hvr-pop hvr-rectangle-out" value="${next}" name="next" id="nextStep" />
								<c:if test="${event.status eq 'DRAFT'}">
									<spring:message code="application.draft" var="draft" />
									<input type="button" id="nextStepDraft" class="step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop pull-right skipvalidation" value="${draft}" />
								</c:if>
								<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') && (isAdmin or eventPermissions.owner)}">
								<spring:message code="event.cancel.draft" var="cancelDraftLabel" />
								<spring:message code="cancel.event.button" var="cancelEventLabel" />
									<a href="#confirmCancelEvent" id="idCancelEvent" role="button" class="btn btn-danger marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal">${event.status eq 'DRAFT' ? cancelDraftLabel : cancelEventLabel}</a>
								</c:if>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmDiscardBid" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="discard.supplier.initial.price" />
					</span>
				</h3>
				<button class="close for-absulate closeModel " type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="confirm.discard.supplier.initial.price" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

				<form action="${pageContext.request.contextPath}/buyer/${eventType}/discaredSuppliersInitialPrice/${event.id}" method="post">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input class="pull-right btn ph_btn_midium btn-info hvr-pop hvr-rectangle-out margin-bottom-10 ph_btn_small" style="margin-right: 20px; height: 35px; line-height: 1; float: left !important;" value="Yes" type="submit" id="discardBqSupp" />
				</form>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right cancelPreBid" style="float: right !important" data-dismiss="modal">No</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmDiscardPreBid" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="discard.supplier.initial.price" />
				</h3>
				<button class="close for-absulate closeBqModel " type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="confirm.discard.supplier.initial.price" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">

				<input class="pull-right btn ph_btn_midium btn-info hvr-pop hvr-rectangle-out margin-bottom-10 ph_btn_small" style="margin-right: 20px; height: 35px; line-height: 1; float: left !important;" value="Yes" type="button" id="discardPreBidBqSupp" />
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right cancelBqPreBid" style="float: right !important" data-dismiss="modal">No</button>
			</div>
		</div>
	</div>
</div>
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="eventsummary.confirm.cancel" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form:form modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/cancelEvent" method="post">
				<form:hidden path="id" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="eventsummary.confirm.to.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<form:textarea path="cancelReason" class="width-100" placeholder="${reasonCancel}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left"><spring:message code="application.yes" /></form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.no2" /></button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<!-- Bootstrap Datepicker -->
<!--<link rel="stylesheet" type="text/css" href="../../assets/widgets/datepicker/datepicker.css">-->
<!-- ZENDESK LABELS -->
<spring:message var="fwdEngRuleDesk" code="application.rfa.forward.english.auction.rules" />
<spring:message var="revEngRuleDesk" code="application.rfa.reverse.english.auction.rules" />
<spring:message var="fwdSealdRuleDesk" code="application.rfa.forward.sealed.bid.auction.rules" />
<spring:message var="revSealdRuleDesk" code="application.rfa.reverse.sealed.bid.auction.rules" />
<spring:message var="fwdDutchRuleDesk" code="application.rfa.forward.dutch.auction.rules" />
<spring:message var="revDutchRuleDesk" code="application.rfa.reverse.dutch.auction.rules" />
<c:set var="rfaRuleDesk" value="${fwdEngRuleDesk}" />
<c:choose>
	<c:when test="${!empty event && event.auctionType == 'FORWARD_ENGISH'}">
		<c:set var="rfaRuleDesk" value="${fwdEngRuleDesk}" />
	</c:when>
	<c:when test="${!empty event && event.auctionType == 'REVERSE_ENGISH'}">
		<c:set var="rfaRuleDesk" value="${revEngRuleDesk}" />
	</c:when>
	<c:when test="${!empty event && event.auctionType == 'FORWARD_SEALED_BID'}">
		<c:set var="rfaRuleDesk" value="${fwdSealdRuleDesk}" />
	</c:when>
	<c:when test="${!empty event && event.auctionType == 'REVERSE_SEALED_BID'}">
		<c:set var="rfaRuleDesk" value="${revSealdRuleDesk}" />
	</c:when>
	<c:when test="${!empty event && event.auctionType == 'FORWARD_DUTCH'}">
		<c:set var="rfaRuleDesk" value="${fwdDutchRuleDesk}" />
	</c:when>
	<c:when test="${!empty event && event.auctionType == 'REVERSE_DUTCH'}">
		<c:set var="rfaRuleDesk" value="${revDutchRuleDesk}" />
	</c:when>
	<c:otherwise>
		<c:set var="rfaRuleDesk" value="${fwdEngRuleDesk}" />
	</c:otherwise>
</c:choose>
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfaRuleDesk}] });
});
</script>
<script>
	<c:if test="${eventPermissions.viewer or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#priviousStep,#nextStep,#idCancelEvent';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$(document).ready(function() {
		$('#approvedButton').click(function(e) {
			e.preventDefault();
			$(this).addClass('disabled');
			$('#approvedRejectForm').attr('action', getBuyerContextPath("approve"));
			$('#approvedRejectForm').submit();
		});

		$('#rfxCancelEvent').click(function() {
			$(this).addClass('disabled');
		});
		
		$('#rejectedButton').click(function(e) {
			e.preventDefault();
			$(this).addClass('disabled');
			$('#approvedRejectForm').attr('action', getBuyerContextPath("reject"));
			$('#approvedRejectForm').submit();

		});
	});

	$.formUtils.addValidator({
		name : 'validate_forward',
		validatorFunction : function(value, $el, config, language, $form) {
			if (isNaN($("#dutchStartPrice").val().replace(/,/g, '')) || isNaN($("#dutchMinimumPrice").val().replace(/,/g, '')) || $("#dutchStartPrice").val() == '' || $("#dutchMinimumPrice").val() == '') {
				return false;
			}
		if(value < 0 ){
			errorMessage : 'Value can not be negative '
		return false;
		}
			var stP = parseFloat($("#dutchStartPrice").val().replace(/,/g, ''));
			var minP = parseFloat($("#dutchMinimumPrice").val().replace(/,/g, ''));
			if (stP > minP) {
				return true;
			} else {
				return false;
			}
		},
		errorMessage : 'Minimum price should be less than start price and should be greater than zero',
		errorMessageKey : 'lte'
	});

	$.formUtils.addValidator({
		name : 'validate_customlength',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 10) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 10 characters',
		errorMessageKey : 'validateCustomLength'
	});
	
	$.formUtils.addValidator({
		name : 'negative_value',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
	
			 var value =val[0].replace(/,/g, '');
			 if(value < 0){
				 return false;
			 }else{
				return true;
			 }
			
		},
		errorMessage : 'Please enter positive value  ',
		errorMessageKey : 'validateCustomLength'
	});
	
// 	$.formUtils.addValidator({
// 		name : 'zero_value',
// 		validatorFunction : function(value, $el, config, language, $form) {
// 			var val = value.split(".");
	
// 			 var value =val[0].replace(/,/g, '');
// 			 if(value < 1){
// 				 return false;
// 			 }else{
// 				return true;
// 			 }
			
// 		},
// 		errorMessage : 'Value shoud be greater than zero.  ',
// 		errorMessageKey : 'validateCustomLength'
// 	});
	

	$.formUtils.addValidator({
		name : 'validate_reverse',
		validatorFunction : function(value, $el, config, language, $form) {
			if (isNaN($("#dutchStartPrice").val().replace(/,/g, '')) || isNaN($("#dutchMinimumPrice").val().replace(/,/g, '')) || $("#dutchStartPrice").val() == '' || $("#dutchMinimumPrice").val() == '') {
				return false;
			}

			var stP = parseFloat($("#dutchStartPrice").val().replace(/,/g, ''));
			var maxP = parseFloat($("#dutchMinimumPrice").val().replace(/,/g, ''));
			if (stP < maxP) {
				return true;
			} else {
				return false;
			}
		},
		errorMessage : 'Maximum price should be greater than Start price',
		errorMessageKey : 'lte'
	});

	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});

	$(document).ready(function() {

		$('.column_button_bar').on('click', '#s1_tender_adddel_btn', function(event) {
			event.preventDefault();
			$('#add_delete_column').show();
		});

		$('.column_button_bar').on('click', '#s1_tender_additem_btn', function(event) {
			event.preventDefault();
			$('#creat_seaction_form').show();
		});

		$('.create_list_sectoin').on('click', '.bq_tender_addsub_item', function(event) {
			event.preventDefault();
			$('#creat_subitem_form').show();
		});

		$(function() {
			$('#idPreBidBy').on('change', function() {
				preBidByOnchange();
			});
		});
		preBidByOnchange();
		function preBidByOnchange() {
			if ($('#idPreBidBy').val() == 'BUYER') {
				$(".lowerPriceSetting").show();
				$(".allowSamePreSetBidForAllSuppliers").show();
			} else {
				$(".lowerPriceSetting").hide();
				$(".allowSamePreSetBidForAllSuppliers").hide();
			}
		}

		if ($('#idItemizedBidding').prop('checked') == true) {
			$('input[name="lumsumBiddingWithTax"]').prop('checked', false);
		} else if ($('#idLumsumBidding').prop('checked') == true) {
			$('input[name="itemizedBiddingWithTax"]').prop('checked', false);
		}

		/* if (document.getElementById("idItemizedBidding").checked == true) {
			$('input[name="lumsumBiddingWithTax"]').prop('checked', false);
		} else if (document.getElementById("idLumsumBidding").checked == true) {
			$('input[name="itemizedBiddingWithTax"]').prop('checked', false);
		} */

		$('.radioChangeItem').on('change', function() {
			$('input[name="lumsumBiddingWithTax"]').prop('checked', false);
		});
		$('.radioChangeLumsum').on('change', function() {
			$('input[name="itemizedBiddingWithTax"]').prop('checked', false);

		});

	});
</script>
<script>
	$(document).ready(function() {
		
		$( ".autoSave" ).change(function() {
			<c:if test="${event.status =='DRAFT'}">
				$("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveAsDraftAuctionRules'), type: 'post'})
			</c:if>
			});
				
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});
	/*
	$(document).ready(function() {
		$('[data-toggle="tooltip"]').tooltip();
	});
	 */
	$('#nextStep').click(function(e) {
		e.preventDefault();
		if ($('#demo-form1').isValid()) {
			$('#demo-form1').submit();
		}
	});

	$('#nextStep').click(function() {
		if ($('#demo-form1').isValid()) {
			$('#demo-form1').attr('action', getBuyerContextPath('auctionRules'));
			$('#demo-form1').submit();
		}
	});

	$('#nextStepDraft').click(function() {
		$('#demo-form1').attr('action', getBuyerContextPath('saveAsDraftAuctionRules'));
		$('#demo-form1').submit();

	});

	$(".skipvalidation ").on('click', function(e) {
		if ($("#skipper").val() == undefined) {
			e.preventDefault();
			$(this).after("<input type='hidden' id='skipper' value='1'>");
			$('form.has-validation-callback :input').each(function() {
				$(this).on('beforeValidation', function(value, lang, config) {
					$(this).attr('data-validation-skipped', 1);
				});
			});
			$(this).trigger("click");
		}
	});

	// Show hide div for check box
	$(document).ready(function() {

		$("#idPreBidBy").change(function () {
			var val=$('#idPreBidBy :selected').text();
			var eventId= $('#eventId').val();
			var ajaxUrl = getContextPath() + '/buyer/RFA/checkPrebidByBuyer/' + eventId;
			
	       $.ajax({
	    	   url : ajaxUrl,
				type : "GET",
				success : function(data) {
					if(data>0 && (val!=undefined && val=="SUPPLIER")){
						$('#confirmDiscardBid').modal('show');	
						$('#myOption').val($('#myOption').text());
					}
				}
	       });
	    });
		
		$('#preSetSamePreBidForAllSuppliers').change(function (e) {
	        var isExist=$("#idPreBidPricingExist").val();
	        if(isExist=='true'){
				$('#confirmDiscardPreBid').modal('show');	
	        }
		});
		

		$("#discardPreBidBqSupp").click(function () {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var eventId= $('#eventId').val();
			var ajaxUrl = getContextPath() + '/buyer/RFA/discaredSuppliersPreBidInitialPrice/' + eventId;
			
	       $.ajax({
	    	  	url : ajaxUrl,
				type : "POST",
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
						$('#confirmDiscardPreBid').modal('hide');	
						$('#idPreBidPricingExist').val('false');
						var success = request.getResponseHeader('success');
						if (success != null) {
							$("#idGlobalSuccessMessage").html(success);
							$("#idGlobalSuccess").show();
							$("#idGlobalError").hide();
	 					}
				},
				error : function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
				complete : function() {
					$('#loading').hide();
				}
	       });
	    });
		
		$( ".cancelBqPreBid" ).click(function() {
			$('#confirmDiscardPreBid').modal('hide');	

			if($('#preSetSamePreBidForAllSuppliers').is(':checked')){
				$('#preSetSamePreBidForAllSuppliers').prop('checked', false); 
			}else{
				$('#preSetSamePreBidForAllSuppliers').prop('checked', true); 
			}

		});

		$( ".closeBqModel" ).click(function() {
			$('#confirmDiscardPreBid').modal('hide');	
			if($('#preSetSamePreBidForAllSuppliers').is(':checked')){
				$('#preSetSamePreBidForAllSuppliers').prop('checked', false); 
			}else{
				$('#preSetSamePreBidForAllSuppliers').prop('checked', true); 
			}
		});
		
		$( ".cancelPreBid" ).click(function() {
			$("#idPreBidBy").val("BUYER").prop('selected',true);
			$("#idPreBidBy").trigger("chosen:updated");
			//preBidByOnchange()
			$(".lowerPriceSetting").show();
		});
		
		
		$( ".closeModel" ).click(function() {
			$("#idPreBidBy").val("BUYER").prop('selected',true);
			$("#idPreBidBy").trigger("chosen:updated");
			//preBidByOnchange()
			$(".lowerPriceSetting").show();
		});
		
		$(function() {
			$("#idBidFromPre").click(function() {
				hideBidFromPre();
			});
		});
		hideBidFromPre();
		function hideBidFromPre() {
			if ($('#idBidFromPre').is(":checked")) {
				$(".bidFromPreHide").show();
			} else {
				$(".bidFromPreHide").hide();
			}
		}

		$(function() {
			$("#idBidFromLead").click(function() {
				hideBidFromOther();
			});
		});
		hideBidFromOther();
		function hideBidFromOther() {
			if ($('#idBidFromLead').is(":checked")) {
				$(".hideDivFromLead").show();
			} else {
				$(".hideDivFromLead").hide();
			}
		}

		$('#idBidFromLead').on('change', function() {
			$('input[name="isBiddingAllowSupplierSameBid"]').prop('checked', false);
		});
		$('#idSameBidSupp').on('change', function() {
			$('input[name="isBiddingPriceHigherLeadingBid"]').prop('checked', false);
			hideBidFromOther();
		});

	});

	//---------
	$('#idMinIncrementValue').on('change', function() {
		$('#biddingMinValue').val('');
	});
	$('#idpriceHigherLeadingBid').on('change', function() {
		$('#biddingPriceHigherLeadingBidValue').val('');
	});
	$('#biddingMinValue').on('change', function() {
		var biddingMinValue = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
		if ($('#idMinIncrementValue').val() === 'VALUE') {

			if ($(this).val() === '' || $(this).val() === undefined) {
				$('#biddingMinValue').val('');
			} else {
				biddingMinValue = !isNaN(biddingMinValue) ? biddingMinValue : 0;
				$('#biddingMinValue').val(ReplaceNumberWithCommas(biddingMinValue.toFixed($('#eventDecimal').val())));
			}
		} else {
			if ($(this).val() === '' || $(this).val() === undefined) {
				$('#biddingMinValue').val('');
			} else {
				biddingMinValue = !isNaN(biddingMinValue) ? biddingMinValue : 0;
				var bidValue = $(this).val().replace(/\,|\s|\#/g, '').split('.');
				if (bidValue[1] === '' || bidValue[1] === undefined) {
					$('#biddingMinValue').val(biddingMinValue);
				} else {
					$('#biddingMinValue').val(biddingMinValue.toFixed(2));
				}
			}
		}
	});

	$('#biddingPriceHigherLeadingBidValue').on('change', function() {
		var biddingPriceHigherLeadingBidValue = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
		if ($('#idpriceHigherLeadingBid').val() === 'VALUE') {

			if ($(this).val() === '' || $(this).val() === undefined) {
				$('#biddingPriceHigherLeadingBidValue').val('');
			} else {
				biddingPriceHigherLeadingBidValue = !isNaN(biddingPriceHigherLeadingBidValue) ? biddingPriceHigherLeadingBidValue : 0;
				$('#biddingPriceHigherLeadingBidValue').val(ReplaceNumberWithCommas(biddingPriceHigherLeadingBidValue.toFixed($('#eventDecimal').val())));
			}
		} else {
			if ($(this).val() === '' || $(this).val() === undefined) {
				$('#biddingPriceHigherLeadingBidValue').val('');
			} else {
				biddingPriceHigherLeadingBidValue = !isNaN(biddingPriceHigherLeadingBidValue) ? biddingPriceHigherLeadingBidValue : 0;
				var bidValue = $(this).val().replace(/\,|\s|\#/g, '').split('.');
				if (bidValue[1] === '' || bidValue[1] === undefined) {
					$('#biddingPriceHigherLeadingBidValue').val(biddingPriceHigherLeadingBidValue);
				} else {
					$('#biddingPriceHigherLeadingBidValue').val(biddingPriceHigherLeadingBidValue.toFixed(2));
				}
			}
		}
	});

	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
</script>
<script>
	<jsp:useBean id="today" class="java.util.Date" />
	<c:if test="${event.status =='SUSPENDED'}">
	<c:if test="${event.eventStart le today}">
	$(window).bind('load', function() {
		var allowedFields = '#priviousStep,#bubble,#nextStep,#idCancelEvent';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${event.eventStart gt today}">
	$(window).bind('load', function() {
		var allowedFields = '#priviousStep,#bubble,#nextStep,#idCancelEvent';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	</c:if>
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '#priviousStep,#bubble,#nextStep,#idSumDownload,#idCancelEvent';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/ajaxFormPlugin.js"/>"></script>
<style>
input[type="radio"], input[type="checkbox"] {
	margin: 12px 10px 0;
}
</style>