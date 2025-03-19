<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="panel td-pad-none bg-none">
	<div class="panel-heading marg-none">
		<h4 class="panel-title">
			<a data-toggle="collapse" data-parent="#accordion" class="collapsed" href="#collapseone"><spring:message code="summarydetails.auctionrules.title" /> </a>
		</h4>
	</div>
	<div id="collapseone" class="panel-collapse collapse">
		<div class="panel">
			<div class="panel-body">
				<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td><strong><spring:message code="application.type" />:</strong></td>
						<td>${event.auctionType.value}</td>
					</tr>
				</table>
			</div>
		</div>
		<c:if test="${event.auctionType == 'FORWARD_ENGISH' or event.auctionType == 'REVERSE_ENGISH'}">
			<div class="panel td-pad-none">
				<div class="panel-heading">
					<h4 class="panel-title">
						<a><spring:message code="supplierevent.auction.console.setting" /> </a>
					</h4>
				</div>
				<div>
					<div class="panel-body">
						<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
							<tr>
								<td><strong>Price:</strong></td>
								<td>${auctionRules.auctionConsolePriceType.value}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="supplier.name" />:</strong></td>
								<td>${auctionRules.auctionConsoleVenderType.value}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="summarydetails.auctionrules.rank" />:</strong></td>
								<td>${auctionRules.auctionConsoleRankType.value}</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</c:if>
		<div class="panel td-pad-none">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a><spring:message code="supplierevent.pre.bid.settings" /> </a>
				</h4>
			</div>
			<div>
				<div class="panel-body">
					<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td><strong><spring:message code="supplierevent.initial.price.keyin" />:</strong></td>
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
									
					</table>
				</div>
			</div>
		</div>
		<div class="panel td-pad-none">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a><spring:message code="summarydetails.auctionrules.bid.price.setting" /> </a>
				</h4>
			</div>
			<div>
				<div class="panel-body">
					<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
						<c:if test="${not empty auctionRules.itemizedBiddingWithTax or not empty auctionRules.lumsumBiddingWithTax}">
							<tr>
								<td><strong><spring:message code="auctionrules.bidding.type" />:</strong></td>
								<td><c:if test="${not empty auctionRules.itemizedBiddingWithTax and auctionRules.itemizedBiddingWithTax}">
										<p>
											<spring:message code="summarydetails.auctionrules.itemized.bids.tax" />
										</p>
									</c:if> <c:if test="${not empty auctionRules.itemizedBiddingWithTax and !auctionRules.itemizedBiddingWithTax}">
										<p>
											<spring:message code="summarydetails.auctionrules.itemized.bids.notax" />
										</p>
									</c:if> <c:if test="${not empty auctionRules.lumsumBiddingWithTax and auctionRules.lumsumBiddingWithTax}">
										<p>
											<spring:message code="supplierevent.lumpsum.bidding.tax" />
										</p>
									</c:if> <c:if test="${not empty auctionRules.lumsumBiddingWithTax and !auctionRules.lumsumBiddingWithTax}">
										<p>
											<spring:message code="supplierevent.lumpsum.bidding.wotax" />
										</p>
									</c:if></td>
								<%-- <td>
									<img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info">
								</td> --%>
							</tr>
						</c:if>
						<c:if test="${not empty auctionRules.prebidAsFirstBid}">
							<tr>
								<td><strong><spring:message code="rfx.pre.bid.price" />:</strong></td>
								<c:if test="${auctionRules.prebidAsFirstBid}">
									<td><spring:message code="application.yes2" /></td>
								</c:if>
								<c:if test="${!auctionRules.prebidAsFirstBid}">
									<td><spring:message code="application.no2" /></td>
								</c:if>
							</tr>
						</c:if>
						<c:if test="${not empty auctionRules.isBiddingMinValueFromPrevious and auctionRules.isBiddingMinValueFromPrevious}">
							<tr>
								<c:if test="${auctionRules.fowardAuction}">
									<td><strong><spring:message code="summarydetails.auctionrules.min.increment" />:</strong></td>
								</c:if>
								<c:if test="${!auctionRules.fowardAuction}">
									<td><strong><spring:message code="auctionrules.min.decrement" />:</strong></td>
								</c:if>
								<td>${auctionRules.biddingMinValueType}<span>&nbsp;-&nbsp;</span> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${auctionRules.biddingMinValue}" /></td>
							</tr>
						</c:if>
						<c:if test="${not empty auctionRules.isBiddingPriceHigherLeadingBid and auctionRules.isBiddingPriceHigherLeadingBid}">
							<tr>
								<c:if test="${auctionRules.fowardAuction}">
									<td><strong><spring:message code="auctionrules.price.higher.leadbid" />:</strong></td>
								</c:if>
								<c:if test="${!auctionRules.fowardAuction}">
									<td><strong><spring:message code="auctionrules.price.lower.leadbid" />:</strong></td>
								</c:if>
								<td>${auctionRules.biddingPriceHigherLeadingBidType}<span>&nbsp;-&nbsp;</span> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${auctionRules.biddingPriceHigherLeadingBidValue}" />
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty auctionRules.isStartGate}">
							<tr>
								<td><strong><spring:message code="summarydetails.auctionrules.start.gate" />:</strong></td>
								<c:if test="${auctionRules.isStartGate}">
									<td><spring:message code="application.yes2" /></td>
								</c:if>
								<c:if test="${!auctionRules.isStartGate}">
									<td><spring:message code="application.no2" /></td>
								</c:if>
							</tr>
						</c:if>
						<c:if test="${not empty auctionRules.isBiddingAllowSupplierSameBid}">
							<tr>
								<td><strong><spring:message code="summarydetails.auctionrules.same.bid" />:</strong></td>
								<c:if test="${auctionRules.isBiddingAllowSupplierSameBid}">
									<td><spring:message code="application.allowed" /></td>
								</c:if>
								<c:if test="${!auctionRules.isBiddingAllowSupplierSameBid}">
									<td><spring:message code="application.not.allowed" /></td>
								</c:if>
							</tr>
						</c:if>
					</table>
				</div>
			</div>
		</div>
		<%-- <c:if test="${not empty event.timeExtensionType and event.timeExtensionType eq 'AUTOMATIC'}">
			<div class="panel td-pad-none">
				<div class="panel-heading">
					<h4 class="panel-title">
						<a>Time Extention - ${event.timeExtensionType} </a>
					</h4>
				</div>
				<div>
					<div class="panel-body">
						<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
							<tr>
								<td><strong>Extention:</strong></td>
								<td>Yes</td>
							</tr>
							<tr>
								<td><strong>Extention Duration:</strong></td>
								<td>${event.timeExtensionDuration} - ${event.timeExtensionDurationType}</td>
								<td><img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info"></td>
							</tr>
							<tr>
								<td><strong>Tigger time:</strong></td>
								<td>${event.timeExtensionLeadingBidValueLast} - ${event.timeExtensionLeadingBidType}</td>
								<td><img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info"></td>
							</tr>
							<tr>
								<td><strong>Extention Round:</strong></td>
								<td>max ${event.extensionCount}</td>
								<td><img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info"></td>
							</tr>
							<tr>
								<td><strong>Auto Disqualify:</strong></td>
								<td>Yes ${event.autoDisqualify}</td>
								<td><img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info"></td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</c:if> --%>
		<%-- <div class="panel td-pad-none">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a>Bidding Price </a>
				</h4>
			</div>
			<div>
				<div class="panel-body">
					<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td><strong>Same Bid price:</strong></td>
							<td>Not Allow</td>
							<td><img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info"></td>
						</tr>
						<tr>
							<td><strong>Price Gap:</strong></td>
							<td>RM 1000.00</td>
							<td><img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info"></td>
						</tr>
						<tr>
							<td><strong>Minimum price Change:</strong></td>
							<td>RM 1000.00</td>
							<td><img data-placement="top" data-toggle="tooltip" data-original-title="Lorem ipsum dolor sit amet. " src="${pageContext.request.contextPath}/resources/images/info_ico.png" alt="info"></td>
						</tr>
					</table>
				</div>
			</div>
		</div> --%>
	</div>
</div>