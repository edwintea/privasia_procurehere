<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseoneThree"><spring:message
					code="summarydetails.auctionrules.title" /></a>
		</h4>
	</div>
	<div id="collapseoneThree"
		class="panel-collapse collapse collopaseInnerBlocksAll">
		<div class="panel-body pad_all_15  border-bottom">
			<c:if
				test="${event.auctionType eq 'FORWARD_DUTCH' or event.auctionType eq 'REVERSE_DUTCH' }">
				<div class="panel">
					<div class="panel-body">
						<table class="tabaccor width100" cellpadding="0" cellspacing="0"
							border="0">
							<tr>
								<td><strong><spring:message
											code="summarydetails.auctionrules.start.price" /> :</strong></td>
								<td><fmt:formatNumber type="number"
										minFractionDigits="${event.decimal}"
										maxFractionDigits="${event.decimal}"
										value="${auctionRules.dutchStartPrice}" /></td>
							</tr>
							<tr>
								<c:if test="${auctionRules.fowardAuction}">
									<td><strong><spring:message
												code="summarydetails.auctionrules.min.price" /> : </strong></td>
								</c:if>
								<c:if test="${!auctionRules.fowardAuction}">
									<td><strong><spring:message
												code="summarydetails.auctionrules.max.price" /> : </strong></td>
								</c:if>
								<td><fmt:formatNumber type="number"
										minFractionDigits="${event.decimal}"
										maxFractionDigits="${event.decimal}"
										value="${auctionRules.dutchMinimumPrice}" /></td>
							</tr>
							<tr>
								<c:if test="${!auctionRules.fowardAuction}">
									<td><strong><spring:message
												code="auctionrules.amount.increment" /> : </strong></td>
								</c:if>
								<c:if test="${auctionRules.fowardAuction}">
									<td><strong><spring:message
												code="auctionrules.amount.decrement" /> : </strong></td>
								</c:if>
								<td><fmt:formatNumber type="number"
										minFractionDigits="${event.decimal}"
										maxFractionDigits="${event.decimal}"
										value="${auctionRules.amountPerIncrementDecrement}" /></td>
							</tr>
							<tr>
								<td><c:if test="${auctionRules.fowardAuction}">
										<strong><spring:message
												code="auctionrules.interval.decrement" />: </strong>
									</c:if> <c:if test="${!auctionRules.fowardAuction}">
										<strong><spring:message
												code="auctionrules.interval.increment" />: </strong>
									</c:if></td>
								<td>${auctionRules.interval}&nbsp;${auctionRules.intervalType}</td>
							</tr>
						</table>
					</div>
				</div>
			</c:if>
			<!--English Auction rules  -->
			<c:if
				test="${event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH' or event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID' }">
				<div class="" id="dutchPanel">
					<div class="panel td-pad-none">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a><spring:message
										code="summarydetails.auctionrules.prebid.setting" /> </a>
							</h4>
						</div>
						<div>
							<div class="panel-body">
								<table class="tabaccor width100" cellpadding="0" cellspacing="0"
									border="0">
									<tr>
										<td><strong><spring:message
													code="summarydetails.auctionrules.prebid.by" />:</strong></td>
										<td>${auctionRules.preBidBy}</td>
									</tr>

									<tr>
										<c:if test="${auctionRules.preBidBy eq 'BUYER' }">
											<c:if test="${auctionRules.fowardAuction}">
												<td><strong><spring:message
															code="auctionrules.supplier.higher.price" />:</strong></td>
												<c:if test="${auctionRules.isPreBidHigherPrice}">
													<td><spring:message code="application.yes2" /></td>
												</c:if>
												<c:if test="${!auctionRules.isPreBidHigherPrice}">
													<td><spring:message code="application.no2" /></td>
												</c:if>
											</c:if>

											<c:if test="${!auctionRules.fowardAuction}">
												<td><strong><spring:message
															code="auctionrules.supplier.lower.price" />:</strong></td>
												<c:if test="${auctionRules.isPreBidHigherPrice}">
													<td><spring:message code="application.yes2" /></td>
												</c:if>
												<c:if test="${!auctionRules.isPreBidHigherPrice}">
													<td><spring:message code="application.no2" /></td>
												</c:if>
											</c:if>
										</c:if>
									</tr>

									<tr>
										<td><strong><spring:message
													code="auctionrules.allow.same.pre.bid" />:</strong></td>
										<c:if test="${!auctionRules.isPreBidSameBidPrice}">
											<td><spring:message code="application.not.allowed" /></td>
										</c:if>
										<c:if test="${auctionRules.isPreBidSameBidPrice}">
											<td><spring:message code="application.allowed" /></td>
										</c:if>
									</tr>
									<c:if test="${auctionRules.preBidBy =='BUYER'}">
										<tr>
											<td><strong><spring:message
														code="auction.rules.pre.bid.preset" />:</strong></td>
											<c:if test="${!auctionRules.preSetSamePreBidForAllSuppliers}">
												<td><spring:message code="application.not.allowed" /></td>
											</c:if>
											<c:if test="${auctionRules.preSetSamePreBidForAllSuppliers}">
												<td><spring:message code="application.allowed" /></td>
											</c:if>
										</tr>
									</c:if>

								</table>
							</div>
						</div>
					</div>
					<div class="panel td-pad-none">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a> <spring:message
										code="summarydetails.auctionrules.bid.price.setting" />
								</a>
							</h4>
						</div>
						<div>
							<div class="panel-body">
								<table class="tabaccor width100" cellpadding="0" cellspacing="0"
									border="0">
									<c:if
										test="${not empty auctionRules.itemizedBiddingWithTax or not empty auctionRules.lumsumBiddingWithTax}">
										<tr>
											<td><strong><spring:message
														code="auctionrules.bidding.type" />:</strong></td>
											<td><c:if
													test="${not empty auctionRules.itemizedBiddingWithTax and auctionRules.itemizedBiddingWithTax}">
													<p>
														<spring:message
															code="summarydetails.auctionrules.itemized.bids.tax" />
													</p>
												</c:if> <c:if
													test="${not empty auctionRules.itemizedBiddingWithTax and !auctionRules.itemizedBiddingWithTax}">
													<p>
														<spring:message
															code="summarydetails.auctionrules.itemized.bids.notax" />
													</p>
												</c:if> <c:if
													test="${not empty auctionRules.lumsumBiddingWithTax and auctionRules.lumsumBiddingWithTax}">
													<p>
														<spring:message
															code="summarydetails.auctionrules.lumpsum.bid.tax" />
													</p>
												</c:if> <c:if
													test="${not empty auctionRules.lumsumBiddingWithTax and !auctionRules.lumsumBiddingWithTax}">
													<p>
														<spring:message
															code="summarydetails.auctionrules.lumpsum.bid.notax" />
													</p>
												</c:if></td>
										</tr>
									</c:if>
									<c:if test="${not empty auctionRules.prebidAsFirstBid}">
										<tr>
											<td><strong><spring:message
														code="rfx.pre.bid.price" />:</strong></td>
											<c:if test="${auctionRules.prebidAsFirstBid}">
												<td><spring:message code="application.yes2" /></td>
											</c:if>
											<c:if test="${!auctionRules.prebidAsFirstBid}">
												<td><spring:message code="application.no2" /></td>
											</c:if>
										</tr>
									</c:if>
									<c:if test="${auctionRules.isBiddingMinValueFromPrevious}">
										<tr>
											<td><c:if test="${!auctionRules.fowardAuction}">
													<strong><spring:message
															code="summarydetails.auctionrules.min.decrement" />:</strong>
												</c:if> <c:if test="${auctionRules.fowardAuction}">
													<strong><spring:message
															code="summarydetails.auctionrules.min.increment" />:</strong>
												</c:if></td>
											<c:if
												test="${auctionRules.biddingMinValueType eq 'PERCENTAGE'}">
												<td>${auctionRules.biddingMinValueType}<span>&nbsp;:&nbsp;</span>
													<fmt:formatNumber type="number" minFractionDigits="2"
														maxFractionDigits="2"
														value="${auctionRules.biddingMinValue}" /> <span>%</span>
												</td>
											</c:if>
											<c:if test="${auctionRules.biddingMinValueType eq 'VALUE'}">
												<td>${auctionRules.biddingMinValueType}<span>&nbsp;:&nbsp;</span>
													<fmt:formatNumber type="number"
														minFractionDigits="${event.decimal}"
														maxFractionDigits="${event.decimal}"
														value="${auctionRules.biddingMinValue}" />
												</td>
											</c:if>
										</tr>
									</c:if>
									<c:if test="${not empty auctionRules.isStartGate}">
										<tr>
											<td><strong><spring:message
														code="summarydetails.auctionrules.start.gate" />:</strong></td>
											<c:if test="${auctionRules.isStartGate}">
												<td><spring:message code="application.yes2" /></td>
											</c:if>
											<c:if test="${!auctionRules.isStartGate}">
												<td><spring:message code="application.no2" /></td>
											</c:if>
										</tr>
									</c:if>
									<c:if test="${auctionRules.isBiddingPriceHigherLeadingBid}">
										<tr>
											<td><c:if test="${!auctionRules.fowardAuction}">
													<strong><spring:message
															code="auctionrules.price.lower.leadbid" /></strong>
												</c:if> <c:if test="${auctionRules.fowardAuction}">
													<strong><spring:message
															code="auctionrules.price.higher.leadbid" /></strong>
												</c:if></td>
											<c:if
												test="${auctionRules.biddingPriceHigherLeadingBidType eq 'PERCENTAGE'}">
												<td>${auctionRules.biddingPriceHigherLeadingBidType}&nbsp;<span>:</span>
													&nbsp; <fmt:formatNumber type="number"
														minFractionDigits="2" maxFractionDigits="2"
														value="${auctionRules.biddingPriceHigherLeadingBidValue}" />
													<span>%</span>
												</td>
											</c:if>
											<c:if
												test="${auctionRules.biddingPriceHigherLeadingBidType eq 'VALUE'}">
												<td>${auctionRules.biddingPriceHigherLeadingBidType}&nbsp;<span>:</span>
													&nbsp; <fmt:formatNumber type="number"
														minFractionDigits="${event.decimal}"
														maxFractionDigits="${event.decimal}"
														value="${auctionRules.biddingPriceHigherLeadingBidValue}" />
												</td>
											</c:if>
										</tr>
									</c:if>
									<tr>
										<c:if
											test="${not empty auctionRules.isBiddingAllowSupplierSameBid and (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')}">
											<tr>
												<td><strong><spring:message
															code="summarydetails.auctionrules.same.bid" />:</strong></td>
												<c:if test="${auctionRules.isBiddingAllowSupplierSameBid}">
													<td><spring:message code="application.allowed" /></td>
												</c:if>
												<c:if test="${!auctionRules.isBiddingAllowSupplierSameBid}">
													<td><spring:message code="application.not.allowed" />
													</td>
												</c:if>
											</tr>
										</c:if>
									</tr>
								</table>
							</div>
						</div>
					</div>
					<c:if
						test="${event.auctionType == 'FORWARD_ENGISH' or event.auctionType == 'REVERSE_ENGISH'}">
						<div class="panel td-pad-none">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a><spring:message
											code="auctionrules.buyer.console.setting" /> </a>
								</h4>
							</div>
							<div>
								<div class="panel-body">
									<table class="tabaccor width100" cellpadding="0"
										cellspacing="0" border="0">
										<tr>
											<td><strong>Price:</strong></td>
											<td>${auctionRules.buyerAuctionConsolePriceType.value}</td>
										</tr>
										<tr>
											<td><strong><spring:message
														code="supplier.name" />:</strong></td>
											<td>${auctionRules.buyerAuctionConsoleVenderType.value}</td>
										</tr>
										<tr>
											<td><strong><spring:message
														code="summarydetails.auctionrules.rank" />:</strong></td>
											<td>${auctionRules.buyerAuctionConsoleRankType.value}</td>
										</tr>
									</table>
								</div>
							</div>
						</div>


						<div class="panel td-pad-none">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a><spring:message
											code="auctionrules.supplier.console.setting" /> </a>
								</h4>
							</div>
							<div>
								<div class="panel-body">
									<table class="tabaccor width100" cellpadding="0"
										cellspacing="0" border="0">
										<tr>
											<td><strong><spring:message
														code="application.price1" />:</strong></td>
											<td>${auctionRules.auctionConsolePriceType.value}</td>
										</tr>
										<tr>
											<td><strong><spring:message
														code="supplier.name" />:</strong></td>
											<td>${auctionRules.auctionConsoleVenderType.value}</td>
										</tr>
										<tr>
											<td><strong><spring:message
														code="summarydetails.auctionrules.rank" />:</strong></td>
											<td>${auctionRules.auctionConsoleRankType.value}</td>
										</tr>
									</table>
								</div>
							</div>
						</div>



					</c:if>
				</div>
			</c:if>
		</div>
	</div>
	<style>
.tabaccor.width100 {
	width: 100% !important;
}
</style>
</div>