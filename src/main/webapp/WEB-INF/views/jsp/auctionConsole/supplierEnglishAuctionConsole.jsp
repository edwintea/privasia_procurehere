<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>

<spring:message var="supplierRfxEnglishFwdDesk" code="application.rfa.supplier.forward.english.console" />
<spring:message var="supplierRfxEnglishRevDesk" code="application.rfa.supplier.reverse.english.console" />
<spring:message var="supplierRfxSealedBidFwdDesk" code="application.rfa.supplier.forward.sealed.bid.console" />
<spring:message var="supplierRfxSealedBidRevDesk" code="application.rfa.supplier.reverse.sealed.bid.console" />
<script type="text/javascript">
zE(function() {
	<c:if test="${event.auctionType == 'REVERSE_ENGISH'or event.auctionType == 'FORWARD_ENGISH'}">
	zE.setHelpCenterSuggestions({ labels: [${supplierRfxEnglishFwdDesk}, ${supplierRfxEnglishRevDesk}] });
</c:if>
<c:if test="${event.auctionType == 'FORWARD_SEALED_BID'or event.auctionType == 'REVERSE_SEALED_BID'}">
zE.setHelpCenterSuggestions({ labels: [${supplierRfxSealedBidFwdDesk}, ${supplierRfxSealedBidRevDesk}] });
</c:if>
});
</script>
<script type="text/javascript">
	var stompClient = null;

	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible'
				: 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	function connect() {
		var socket = new SockJS('${pageContext.request.contextPath}/auctions');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			//setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/auctionTopic/${event.id}', function(
					messageOutput) {
				console.log("the responce : " +messageOutput.body);
 				if(messageOutput.body == 'CLOSED'){
					//showMessageOutput("Event has been closed.");
					window.location.href = getContextPath() + "/supplier/englishAuctionConsole/${event.id}";
				} else if(messageOutput.body == 'SUSPENDED'){
					window.location.href = getContextPath() + "/supplier/englishAuctionConsole/${event.id}";
				} else if(messageOutput.body == 'RESUME'){
					window.location.href = getContextPath() + "/supplier/englishAuctionConsole/${event.id}";
				}
				
 				
			});
			
			stompClient.subscribe('/auctionTimeExtension/${event.id}', function(
					messageOutput) {
				console.log("the responce : " +messageOutput.body);
				var obj = $.parseJSON(messageOutput.body);
				var info = "Event extended for "+obj['infoMessage'];
				showMessageOutput(info);
				 $.jGrowl(info, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
			});
			
			stompClient.subscribe('/auctionFesibleBid/${event.id}', function(
					messageOutput) {
				console.log("the responce : " +messageOutput.body);
				var obj = $.parseJSON(messageOutput.body);
				var info = obj['data'];
				//$('#fbid').html(parseFloat(info).toFixed(${event.decimal}));
				var bidAmount = ReplaceNumberWithCommas(parseFloat(info).toFixed(${event.decimal}));
				$('#fbid').text(bidAmount);
				$('[id="feasibleBid"]').val(info);
				var info = "Next Fesible bid is now revised to: "+bidAmount;
				showMessage('WARN',info);
			});
			
			stompClient.subscribe('/auctionFesibleBid/${event.id}/${supplierId}', function(
					messageOutput) {
				console.log("the responce own  : " +messageOutput.body);
				var obj = $.parseJSON(messageOutput.body);
				var info = obj['data'];
				//$('#fbid').html(parseFloat(info).toFixed(${event.decimal}));
				//$('#fbid').text(ReplaceNumberWithCommas(parseFloat(info).toFixed(${event.decimal})));
				$('[id="feasibleBid"]').val(info);
				var bidAmount = ReplaceNumberWithCommas(parseFloat(info).toFixed(${event.decimal}));
				$('#fbid').text(bidAmount);
				var info = "Next Fesible bid is now revised to: "+bidAmount;
				showMessage('WARN',info);
			});
			
			stompClient.subscribe('/auctionSupplierList/${event.id}', function(
					messageOutput) {
				console.log("the responce List  : ");
				<c:if test="${event.status == 'ACTIVE' and (!eventSupplier.disqualify)}">
				reloadBidderList();
				</c:if>
			});
			
			stompClient.subscribe('/bidHistorySupplierSide/${event.id}/${supplierId}', function(
			messageOutput) {
				console.log("the responce message : ");
				<c:if test="${event.status == 'ACTIVE' and (!eventSupplier.disqualify)}">
				getBidHistoryOfSupplier('${!empty sessionScope["graphArrangeBy"] ? sessionScope["graphArrangeBy"] : "Time"}');
				</c:if>
			});
			
			
			
		});
	}

	var errorHandler = function() {
		console.log("Connection lost..... reconnecting...");
	    setTimeout(function(){ connect(); }, 5 * 1000); // retry connection in 5 secs
	}

	function disconnect() {
		if (stompClient != null && stompClient.ws.readyState === stompClient.ws.OPEN) {
			stompClient.disconnect();
		}
		console.log("Disconnected");
	}

	$(window).unload(function(){
		if(stompClient.connected) {
			console.log('Closing websocket');
			disconnect();
		}
	});

	$(document).ready(
	function() {
		connect();
	});
</script>
<style>
.notDisplay {
	opacity: 0;
	position: absolute;
	width: 100%;
}

#legend-container>table>tbody>tr {
	display: inline-block !important;
	margin-right: 10px;
	margin-top: 15px;
}

#legend-container {
	float: left;
	margin-bottom: 10px;
}

.zindex1 {
	z-index: 1 !important;
}
</style>


<div id="sb-site">
	<jsp:useBean id="today" class="java.util.Date" />
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
	<div id="page-wrapper">
		<input type="hidden" value="${bqId}" id="myBqId"> <input type="hidden" value="${supplierId}" id="supplierId"> <input type="hidden" id="auctionType" value="${event.auctionType}"> <input type="hidden" value="${event.decimal}" id="decimal"> <input type="hidden" value="${lastBidOfSuppliers}" id="lastBidOfSuppliers"> <input type="hidden" value="${feasibleBid}" id="nextFeasibleBid"> <input type="hidden" value="${auctionRules.fowardAuction}" id="forwardAuction">
		<div id="page-content-wrapper">
			<input type="hidden" id="startPolling" value="${(event.status == 'ACTIVE' and today ge event.eventStart) ? 'true' : 'false'}"> <input type="hidden" id="eventId" value="${event.id}">
			<div id="page-content">
				<div class="container">
					<ol class="breadcrumb">
						<li><a id="dashboard" href="${pageContext.request.contextPath}/supplier/supplierDashboard"> <spring:message code="application.dashboard" />

						</a></li>
						<li class="active"><spring:message code="rfaevent.auction.hall" /> ${sessionScope["timeZone"]}</li>
					</ol>
					<!-- page title block -->
					<div class="Section-title title_border gray-bg">
						<h2 class="trans-cap auction_hall_heading">
							<spring:message code="rfaevent.auction.hall" />
							: ${event.eventName}
						</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />
							: ${event.status}
						</h2>
						<c:if test="${event.status == 'CLOSED' && (!empty auctionRules.lumsumBiddingWithTax) && ( eventSupplier.auctionRankingOfSupplier  != null and eventSupplier.numberOfBids ge 0) }">
							<div class="pull-right">
								<a href="${pageContext.request.contextPath}/supplier/viewBqList/RFA/${event.id}" role="button" class="btn btn-alt btn-hover btn-primary hvr-pop revise_submission marg-right-10" data-toggle="modal"><spring:message code="supplierauction.revise.submission" /></a>
							</div>
						</c:if>
					</div>
					<div class="ports-tital-heading marg-bottom-10">
						<!-- for auction console  bid warning-->
						<c:if test="${(empty event.auctionResumeDateTime ? today ge event.eventStart : today ge event.auctionResumeDateTime) and event.status eq 'ACTIVE'}">
							<c:if test="${auctionRules.isStartGate and auctionRules.prebidAsFirstBid}">
								<c:if test="${bidCountForSupplier le '1'}">
									<c:set var="warn" value="There is start gate defined for this event. so you have to submit second bid to view full auction console." scope="request" />
								</c:if>
							</c:if>
							<c:if test="${auctionRules.isStartGate and !auctionRules.prebidAsFirstBid}">
								<c:if test="${bidCountForSupplier le '0'}">
									<c:set var="warn" value="There is start gate defined for this event. so you have to submit first bid to view full auction console." scope="request" />
								</c:if>
							</c:if>
						</c:if>
					</div>
					<div class="clear"></div>
					<c:if test="${eventSupplier.submitted}">
						<div class="main_div_auction_hall">
							<div class="auction_hall">
								<div id="timer-accord" class="Invited-Supplier-List white-bg-with-arrow ${(event.status == 'ACTIVE' and today gt event.eventStart) ? 'small-accordin-tab' : '' }">
									<div class="row">
										<div class="col-md-7 col-sm-7">
											<c:if test="${event.status ne 'COMPLETE' and event.status ne 'CLOSED' and event.status ne 'FINISHED' and event.status ne 'SUSPENDED'}">
												<c:if test="${empty event.auctionResumeDateTime}">
													<c:if test="${today lt event.eventStart}">
														<div class="meeting2-heading-new width100">
															<label class="width100  pull-left"><spring:message code="auctionhall.start.date" /> &amp; <spring:message code="application.time" /></label> <br>
															<h3 class="date-time-show fixh3 pull-left">
																<span> <fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</span>
															</h3>
														</div>
													</c:if>
													<c:if test="${today gt event.eventStart}">
														<div class="meeting2-heading-new width100">
															<label class="width100  pull-left"><spring:message code="auctionhall.end.date" /> &amp; <spring:message code="application.time" /></label> <br>
															<h3 class="date-time-show fixh3 pull-left">
																<span> <fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</span>
															</h3>
														</div>
													</c:if>
												</c:if>
												<c:if test="${not empty event.auctionResumeDateTime}">
													<c:if test="${today lt event.auctionResumeDateTime}">
														<div class="meeting2-heading-new width100">
															<label class="width100  pull-left"><spring:message code="auctionhall.resume.date" /> &amp; <spring:message code="application.time" /></label> <br>
															<h3 class="date-time-show fixh3 pull-left">
																<span> <fmt:formatDate value="${event.auctionResumeDateTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</span>
															</h3>
														</div>
													</c:if>
													<c:if test="${today gt event.auctionResumeDateTime}">
														<div class="meeting2-heading-new width100">
															<label class="width100  pull-left"><spring:message code="auctionhall.end.date" /> &amp; <spring:message code="application.time" /></label> <br>
															<h3 class="date-time-show fixh3 pull-left">
																<span> <fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</span>
															</h3>
														</div>
													</c:if>
												</c:if>
											</c:if>
											<c:if test="${event.status eq 'COMPLETE' or event.status eq 'CLOSED' or event.status eq 'FINISHED' or event.status eq 'SUSPENDED'}">
												<div class="meeting2-heading-new width100">
													<label class="width100  pull-left"><spring:message code="auctionhall.start.date" /> &amp; <spring:message code="application.time" /></label> <br>
													<h3 class="date-time-show fixh3 pull-left">
														<span> <fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
														</span>
													</h3>
												</div>
											</c:if>
											<div class="tag-line width100 marg-bottom-10 marg-left-10">
												<span> <b><spring:message code="application.eventid" />:</b> ${event.eventId}
												</span> <span> <b><spring:message code="rfx.auction.type.label" />:</b> ${event.auctionType.value}
												</span> <span> <b><spring:message code="eventsummary.eventdetail.reference" />:</b> ${event.referanceNumber}
												</span> <br /> <span> <b><spring:message code="application.eventowner" />:</b> ${event.eventOwner.name}/${event.eventOwner.communicationEmail}
												</span>
											</div>
										</div>
										<div class="col-md-5 col-sm-5">
											<c:if test="${event.status eq 'ACTIVE'}">
												<%-- <c:if test="${event.status eq 'ACTIVE'}"> --%>
												<c:if test="${not empty event.auctionResumeDateTime}">
													<div class="counter" id="counter">
														<c:if test="${today lt event.auctionResumeDateTime}">
															<h3>
																<spring:message code="application.time.left.resume" />
															</h3>
														</c:if>
														<c:if test="${today ge event.auctionResumeDateTime}">
															<h3>
																<spring:message code="application.time.left.end" />
															</h3>
														</c:if>
														<div class="main-example">
															<c:if test="${today lt event.auctionResumeDateTime}">
																<fmt:formatDate value="${event.auctionResumeDateTime}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
															</c:if>
															<c:if test="${today ge event.auctionResumeDateTime}">
																<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
															</c:if>
															<div class="countdown-container" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/supplier/englishAuctionConsole/${event.id}" id="main-example1" data-date="${counterDate}"></div>
														</div>
													</div>
												</c:if>
												<c:if test="${empty event.auctionResumeDateTime}">
													<div class="counter" id="counter">
														<c:if test="${today lt event.eventStart}">
															<h3>
																<spring:message code="application.time.left.start" />
															</h3>
														</c:if>
														<c:if test="${today ge event.eventStart}">
															<h3>
																<spring:message code="application.time.left.end" />
															</h3>
														</c:if>
														<div class="main-example">
															<c:if test="${today lt event.eventStart}">
																<fmt:formatDate value="${event.eventStart}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
															</c:if>
															<c:if test="${today ge event.eventStart}">
																<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
															</c:if>
															<div class="countdown-container" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/supplier/englishAuctionConsole/${event.id}" id="main-example1" data-date="${counterDate}"></div>
														</div>
													</div>
												</c:if>
											</c:if>
											<c:if test="${event.status eq 'CLOSED' or event.status eq 'FINISHED' or event.status eq 'COMPLETE'}">
												<div class="meeting2-heading-new">
													<label><spring:message code="auctionhall.end.date" /> &amp; <spring:message code="application.time" /></label><br />
													<h3 class="date-time-show fixh3 pull-left">
														<span> <fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
														</span>
													</h3>
												</div>
											</c:if>
											<!-- /#Main Div -->
											<%-- </c:if> --%>
										</div>
										<!-- /.Columns Div -->
									</div>
									<!-- /.Row Div -->
								</div>
							</div>
							<c:if test="${feasibleBid ne '' and event.status eq 'ACTIVE'}">
								<div class="marg_top_20 alert alert-warning ajax-msg  pull-left">
									<span class="fesibleBid"><spring:message code="supplierauction.next.feasible.bid" />: </span> <span class="fesibleBid" id="fbid"><fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${feasibleBid}" /></span>&nbsp;&nbsp;<span id="fesibleCur" class="fesibleBid">${event.baseCurrency}</span>
								</div>
							</c:if>

							<div class="marg_top_20 pull-right">
								<a id="idBackToEventDetails" class="btn btn-alt btn-hover btn-primary hvr-pop del_inv" href="${pageContext.request.contextPath}/supplier/viewSupplierEvent/RFA/${event.id}"><spring:message code="supplierauction.back.event.details" /></a>
							</div>
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="tab-main marg_top_20">
								<ul class="tabs">
									<li class="tab-link current" data-tab="tab-1"><spring:message code="auctionhall.auction.console" /></li>
									<li class="tab-link zindex1" data-tab="tab-2"><spring:message code="auctionhall.bid.history" /></li>
								</ul>
							</div>
							<div class="tab-main-inner pad_all_15">
								<div id="tab-1" class="tab-content current" style="display: none">
									<c:if test="${empty event.auctionResumeDateTime}">
										<c:set value="${event.eventStart}" var="auctionDate"></c:set>
									</c:if>
									<c:if test="${not empty event.auctionResumeDateTime}">
										<c:set value="${event.auctionResumeDateTime}" var="auctionDate"></c:set>
									</c:if>
									<c:if test="${today le auctionDate and not empty event.auctionResumeDateTime}">
										<c:if test="${auctionRules.isStartGate and auctionRules.prebidAsFirstBid}">
											<c:if test="${bidCountForSupplier gt '1'}">
												<jsp:include page="biddingSupplierList.jsp" />
											</c:if>
										</c:if>
										<c:if test="${auctionRules.isStartGate and !auctionRules.prebidAsFirstBid}">
											<c:if test="${bidCountForSupplier gt '0'}">
												<jsp:include page="biddingSupplierList.jsp" />
											</c:if>
										</c:if>
									</c:if>
									<c:if test="${today ge auctionDate and !eventSupplier.disqualify}">
										<c:if test="${auctionRules.isStartGate and auctionRules.prebidAsFirstBid}">
											<c:if test="${bidCountForSupplier gt '1'}">
												<jsp:include page="biddingSupplierList.jsp" />
											</c:if>
										</c:if>
										<c:if test="${!auctionRules.isStartGate and auctionRules.prebidAsFirstBid}">
											<jsp:include page="biddingSupplierList.jsp" />
										</c:if>
										<c:if test="${auctionRules.isStartGate and !auctionRules.prebidAsFirstBid}">
											<c:if test="${bidCountForSupplier gt '0'}">
												<jsp:include page="biddingSupplierList.jsp" />
											</c:if>
										</c:if>
										<c:if test="${!auctionRules.isStartGate and !auctionRules.prebidAsFirstBid}">
											<c:if test="${bidCountForSupplier gt '0'}">
												<jsp:include page="biddingSupplierList.jsp" />
											</c:if>
										</c:if>
										<c:if test="${event.status eq 'ACTIVE' or event.status eq 'CLOSED'}">
											<jsp:include page="auctionHallBqSubmission.jsp"></jsp:include>
										</c:if>
									</c:if>
									<c:if test="${eventSupplier.disqualify}">
										<div id="timer-accord" class="Invited-Supplier-List  white-bg-with-arrow small-accordin-tab marg-bottom-20">
											<div class="row">
												<div class="col-md-12">
													<div class="time-ammount">
														<div class="time-ammount-inner">
															<div class="time-ammount-heading blue-with-border"></div>
															<div class="time-ammount-contant-inner" style="width: 100% !important; color: red;">
																<spring:message code="supplierauction.disqualified.auction" />
															</div>
															<div class="time-ammount-contant-inner" style="width: 100% !important;">
																<b><spring:message code="Product.remarks" /> : - <span>${eventSupplier.disqualifyRemarks}</span></b>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</c:if>
									<c:if test="${event.status eq 'SUSPENDED'}">
										<div id="timer-accord" class="Invited-Supplier-List  white-bg-with-arrow small-accordin-tab marg-bottom-20">
											<div class="row">
												<div class="col-md-12">
													<div class="time-ammount">
														<div class="time-ammount-inner">
															<div class="time-ammount-heading blue-with-border"></div>
															<div class="time-ammount-contant-inner" style="width: 100% !important; color: red;">
																<spring:message code="auctionhall.suspended" />
															</div>
															<div class="time-ammount-contant-inner" style="width: 100% !important;">
																<b><spring:message code="Product.remarks" /> : - <span>${event.suspendRemarks}</span></b>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</c:if>
								</div>


								<div id="tab-2" class="tab-content doc-fir notDisplay">
									<div class="col-md-12">
										<div class="col-md-2 col-sd-2 marg-top-10">
											<label><spring:message code="bidsupplier.show.graph" />:</label>
										</div>
										<div class="col-md-3 col-sd-3">
											<select class="chosen-select disablesearch onBidTypeChange">
												<option value="Time" ${sessionScope["graphArrangeBy"] eq 'Time' ? 'selected' : ''}><spring:message code="application.time" /></option>
												<option value="BidNumber" ${sessionScope["graphArrangeBy"] eq 'BidNumber' ? 'selected' : ''}><spring:message code="auctionhall.bid.number" /></option>
											</select>
										</div>
										<div class="col-md-7 col-sd-7">
											<span class="pull-right"><spring:message code="bidsupplier.zoom.option" /></span>
										</div>

									</div>

									<div class="event-graph">
										<div class="example-box-wrapper">
											<div id="data-example-1" class="mrg20B" style="width: 100%; height: 300px;"></div>
										</div>
									</div>
									<div class="demo-container">
										<div id="placeholder" class="demo-placeholder"></div>
										<div id="legend-container"></div>
									</div>
								</div>
							</div>
						</div>
					</c:if>
					<c:if test="${!eventSupplier.submitted}">
						<div id="timer-accord" class="Invited-Supplier-List  white-bg-with-arrow small-accordin-tab marg-bottom-20">
							<div class="row">
								<div class="col-md-12">
									<div class="time-ammount">
										<div class="time-ammount-inner">
											<div class="time-ammount-heading blue-with-border"></div>
											<div class="time-ammount-contant-inner" style="width: 100% !important;">
												<b><spring:message code="supplierauction.not.authorized" /></b>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- Jgrowl Notifications -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/js/view/supplierEnglishSubmission.js?2"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>

<!--Flot Graph   -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-resize.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-stack.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-pie.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/jquery.flot.time_new.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-axislabels.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/jquery.flot.selection.js"/>"></script>

<!-- https://raw.githubusercontent.com/flot/flot/master/jquery.flot.time.js
 -->
<script>
	<c:if test="${eventPermissions.viewer or event.status ne 'ACTIVE'}">
	$(window).bind('load', function() {
		var allowedFields = '.tab-link > a , #dashboard,.s1_view_desc,.revise_submission,#idBackToEventDetails';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});

	</c:if>

	var decimalSet = '${decimalSet}';
	var decimalValue = ${event.decimal};
	// set the date we're counting down to
	var target_date = new Date('Jan, 31, 2017').getTime();

	// variables for time units
	var days, hours, minutes, seconds;

	// get tag element
	//var countdown = document.getElementById('countdown1');
	//alert(countdown);
	// update the tag with id "countdown1" every 1 second
	/*
	setInterval(function() {

		// find the amount of "seconds" between now and target
		var current_date = new Date().getTime();
		var seconds_left = (target_date - current_date) / 1000;

		// do some time calculations
		days = parseInt(seconds_left / 86400);
		seconds_left = seconds_left % 86400;

		hours = parseInt(seconds_left / 3600);
		seconds_left = seconds_left % 3600;

		minutes = parseInt(seconds_left / 60);
		seconds = parseInt(seconds_left % 60);

		// format countdown string + set tag value
		htmldata = '<span class="days"><b>Days</b>' + days + '</span><span class="hours"><b>Hours</b>' + hours + '</span><span class="minutes"><b>Minutes</b>' + minutes + '</span><span class="seconds"><b>Seconds</b>' + seconds + '</span>';
		$('#countdown1').html(htmldata);

	}, 1000);

	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$(this).find('.header').css('top', $(this).scrollTop());

		});

		//$('select').select();
	});
	 */

	$(document).ready(function() {

		<c:if test="${event.status == 'ACTIVE' and (!eventSupplier.disqualify)}">
		setInterval(reloadBidderList, 3000);
		//reloadBidderList();
		</c:if>
		$(".Invited-Supplier-List").click(function() {
			$("#timer-accord").toggleClass("small-accordin-tab");
		});
		 
		 $('#submitBidPrice1').click(function() {
				$("#submitBidPrice1").addClass("disabled", true);
			});
		 
		$('#submitBidPrice').click(function() {
			$("#submitBidPrice").addClass("disabled", true);
			var eventId = $('#supplierBqList').find('[name="rfteventId"]').val();
			var bqId = $('#myBqId').val();
			$('#supplierBqList').attr('action', getSupplierContextPath() + '/submitEnglishAuction/' + eventId + "/" + bqId);
			$('#supplierBqList').submit();
		});
		$('#undoButton').click(function() {
			var eventId = $('#eventIdRefresh').val();
			//var eventId = $('#supplierBqList').find('[name="rfteventId"]').val();
			window.location.href = getContextPath() + "/supplier/englishAuctionConsole/" + eventId;
		});

		//	$('.undoButtonDiv').hide();
		

	});

	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});

	$(document).ready(function() {

		$('ul.tabs li').click(function() {
			var tab_id = $(this).attr('data-tab');

			$('ul.tabs li').removeClass('current');
			$('.tab-content').removeClass('current');

			$(this).addClass('current');
			$("#" + tab_id).addClass('current');
		})
		
		// Load it first time... there after it will be timer based...
		getBidHistoryOfSupplier('${!empty sessionScope["graphArrangeBy"] ? sessionScope["graphArrangeBy"] : "Time"}');

		$(".Invited-Supplier-List").click(function() {
			$("#timer-accord").toggleClass("small-accordin-tab");
		});


	});
	
	
	$('.onBidTypeChange').on('change', function() {
		  getBidHistoryOfSupplier(this.value);
		});
	
	
		// chart initial	

		//Interacting with Data Points example

		function convertTotimeStamp(myDateTime) {
			var arrAY = myDateTime.split(" ");
			var time = arrAY[1] + " " + arrAY[2];
			var hours = Number(time.match(/^(\d+)/)[1]);
			var minutes = Number(time.match(/:(\d+)/)[1]);
			var seconds = Number(arrAY[1].split(':')[2]);
			var AMPM = time.match(/\s(.*)$/)[1];
			if (AMPM == "PM" && hours < 12)
				hours = hours + 12;
			if (AMPM == "AM" && hours == 12)
				hours = hours - 12;
			var sHours = hours.toString();
			var sMinutes = minutes.toString();
			var sSeconds = seconds.toString();
			if (hours < 10)
				sHours = "0" + sHours;
			if (minutes < 10)
				sMinutes = "0" + sMinutes;
			if (seconds < 10)
				sSeconds = "0" + sSeconds;
			//	console.log(sHours + ":" + sMinutes);			
			dateString = arrAY[0] + " " + sHours + ":" + sMinutes+":"+sSeconds;
			dateParts = dateString.match(/(\d+)\/(\d+)\/(\d+) (\d+):(\d+)/);
			var date = new Date(dateParts[3], parseInt(dateParts[2], 10) - 1, dateParts[1], sHours, sMinutes,sSeconds);
			return date.getTime();
		}
		function getBidHistoryOfSupplier(arrangeBidBy) {
			
			var xmode = "";
			var xtickDecimal = "";
			var xmax = "";
			var xlabel = "";
			
			var myDataObj= null;
			var eventId = $("#eventId").val();
			//var bqId = $("#bqId").val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var ajaxUrl = getContextPath() + '/supplier/getBidHistoryOfOwnSupplier';
			var bqData = {
				'eventId' : eventId,
				'arrangeBidBy' : arrangeBidBy
			};
			$.ajax({
				url : ajaxUrl,
				type : "POST",
				data : bqData,
				beforeSend : function(xhr) {
					//$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				success : function(data, textStatus, request) {
					
					
					if(arrangeBidBy === "Time"){
						xmode = "time";
						xtickDecimal = null;
						xmax = null;
						xlabel = "(Bid submission Time)";
					}else{
						xmode = "number";
						xtickDecimal = 0;
						xmax = data.totalBids;
						xlabel = "(Bid number)";
					}
					var dataObject = [];// new Object();
					var count = 1;
					var max = 10;
					console.log(data.supplierBids);
					/* $(data.supplierAuction).each(function(i, supplier) {
						supArray = {};
						var bidData = [];
						$(supplier.auctionSupplierBids).each(function(i, bids) {
							var newbid = [];
							console.log(bids.bidDateAndTime);
							tooltipData = bids.bidDateAndTime;
							timStamp = count++;
							console.log("timStamp"+timStamp);
							newbid.push(timStamp);
							newbid.push(parseFloat(bids.bidPrice).toFixed(decimalValue));
							newbid.push(tooltipData)
							bidData.push(newbid);
						});
						supArray.data = bidData;
						supArray.label = supplier.supplierCompanyName;
						dataObject.push(supArray);
						//myDataObj.push(supArray);
						console.log(data);
						console.log("---------------------------------------------")
						console.log(dataObject);
						
					}); */
					if(count > max){
						max = count+1;
					}
					
					var chartOptions = {
							
							xaxes : [ {
								mode : xmode,
								timeformat: "%d/%m %h:%M:%S",
								tickDecimals: xtickDecimal
							} ],
							yaxes : [ {
								min : data.minPrice,
								max : data.maxPrice,
								tickDecimals : decimalValue,
								tickFormatter: function ReplaceNumberWithCommas(yourNumber) {
									var n = parseFloat(yourNumber).toFixed(decimalValue).toString().split(".");
									n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
									// Combines the two sections
									return n.join(".");
								} 
							} ],     selection:{
				                mode: "xy"
				            },
							series : {
								shadowSize : 0,
								lines : {
									show : true,
									lineWidth : 2
								},
								points : {
									show : true
								}
							},
							grid : {
								labelMargin : 10,
								hoverable : true,
								clickable : true,
								borderWidth : 1,
								borderColor : 'rgba(82, 167, 224, 0.06)'
							},
							legend : {
								backgroundColor : '#fff',
								 container:$("#legend-container")
							},
							yaxis : {
								tickColor : 'rgba(0, 0, 0, 0.06)',
								font : {
									color : 'rgba(0, 0, 0, 0.4)'
								},
								axisLabel: '(Bid Value in ${event.baseCurrency})' 
							},
							xaxis : {
								tickColor : 'rgba(0, 0, 0, 0.06)',
								font : {
									color : 'rgba(0, 0, 0, 0.4)'
								},
								axisLabel: xlabel
							},
							colors : [ "#68b3e2", "#ffad13", "#52a7e0", "#a3cc56", "#23527c", "#4cae4c", "#308dcc", "#d67520", "#269abc", "#2d2d2d" ],
							tooltip : true,
							tooltipOpts : {
								 content: function(label, xval, yval, flotItem){
								        return label+"<br> Bid : ${event.baseCurrency} %y <br/> Time : "+flotItem.series.data[flotItem.dataIndex][2];
								      }
								/* content : function(dataObject.dataIndex, xval, yval) {
							          var content = "%s %x " + myDataObj;
							          return content;
							        } */
								//content : supArray.label+dataObject[0].data[0]+"<br> Bid : %y <br/> Time : %x "
							},
							axisLabels:{
								show: true
							}
						}
					var plot = $.plot($("#data-example-1"), data.supplierBids, chartOptions);
					
					$("#data-example-1").dblclick(function () {
						plot = $.plot($("#data-example-1"), data.supplierBids, chartOptions);
					});
					
					$("#data-example-1").bind("plotselected", function (event, ranges) {

						// clamp the zooming to prevent eternal zoom

						if (ranges.xaxis.to - ranges.xaxis.from < 0.00001) {
							ranges.xaxis.to = ranges.xaxis.from + 0.00001;
						}

						if (ranges.yaxis.to - ranges.yaxis.from < 0.00001) {
							ranges.yaxis.to = ranges.yaxis.from + 0.00001;
						}

						// do the zooming

						console.log('Zoom From : ' + ranges.xaxis.from + ' To : ' + ranges.xaxis.to);
						
						// getData(ranges.xaxis.from, ranges.xaxis.to)
						
						plot = $.plot("#data-example-1", data.supplierBids,
							$.extend(true, {}, chartOptions, {
								xaxis: { min: ranges.xaxis.from, max: ranges.xaxis.to }
								//yaxis: { min: ranges.yaxis.from, max: ranges.yaxis.to }
							})
						);

						// don't fire event on the overview to prevent eternal loop

						//overview.setSelection(ranges, true);
						// for Overview in zoom link  view-source:http://www.flotcharts.org/flot/examples/zooming/index.html
					});

					//$('#loading').hide();

				},
				error : function(request, textStatus, errorThrown) {
					console.log(request.responseText);
				},
				complete : function() {
					//$('#loading').hide();
					$('.notDisplay').removeClass('notDisplay').hide();
				}
			});

		}
</script>

<style>
.chosen-container-single-nosearch {
	margin-left: 5px !important;
	width: 59% !important;
	text-align: left;
}

.fesibleBid {
	font-size: 18Px;
}

.fesibleBidLi {
	content: " ";
}
</style>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Theme layout -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
