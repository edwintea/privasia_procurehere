<%@page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:useBean id="today" class="java.util.Date" scope="request" />

<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<script type="text/javascript">
	var stompClient = null;
	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	function connect() {
		var socket = new SockJS('${pageContext.request.contextPath}/auctions');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			//setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/auctionTopic/${event.id}', function(messageOutput) {
				console.log("the responce : " + messageOutput.body);
				if (messageOutput.body == 'CLOSED') {
					window.location.href = getContextPath() + "/supplier/viewSupplierEvent/${eventType}/${event.id}";
				} else if (messageOutput.body == 'SUSPENDED') {
					window.location.href = getContextPath() + "/supplier/viewSupplierEvent/${eventType}/${event.id}";
				} else if(messageOutput.body == 'RESUME'){
					window.location.href = getContextPath() + "/supplier/viewSupplierEvent/${eventType}/${event.id}";
				}

			});
		});
	}

	var errorHandler = function() {
		console.log("Connection lost..... reconnecting...");
		setTimeout(function() {
			connect();
		}, 5 * 1000); // retry connection in 5 secs
	}

	function disconnect() {
		if (stompClient != null && stompClient.ws.readyState === stompClient.ws.OPEN) {
			stompClient.disconnect();
		}
		console.log("Disconnected");
	}

	$(window).unload(function() {
		if (stompClient.connected) {
			console.log('Closing websocket');
			disconnect();
		}
	});

	$(document).ready(function() {
		connect();
	});
</script>


<c:set var="isTrial" value="${supplier.supplierSubscription.supplierPlan == null || supplier.supplierSubscription.supplierPlan.planStatus == 'FREE_TRIAL'}" />
<c:set var="isExpired" value="${now > supplier.supplierPackage.endDate}" />
<c:set var="isSingleBuyer" value="${!empty supplier.supplierSubscription && supplier.supplierSubscription.supplierPlan.buyerLimit == 1}" />
<c:set var="isAllBuyer" value="${!empty supplier.supplierSubscription && supplier.supplierSubscription.supplierPlan.buyerLimit > 1}" />
<c:set var="allowEventForThisBuyer" value="${false}" scope="request" />
<c:forEach items="${supplier.associatedBuyers}" var="buyer">
	<c:if test="${buyer.id == event.tenantId }">
		<c:set var="allowEventForThisBuyer" value="${true}" scope="request" />
	</c:if>
</c:forEach>
 <!-- page title block -->
<div class="page-title" style="display: ${(isTrial || isSingleBuyer || isExpired) ? 'block' : 'none'}">
	<p class="float_r_web">
		<span class="title-info_massage info_default">
			<c:if test="${isSingleBuyer}">
				<spring:message code="supplier.dashboard.common.unpaid.message"/>
				<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info btn-sm hvr-pop marg-left-20 do-not-disable"><spring:message code="supplier.dashboard.upgrade"/></a>
			</c:if>
			<c:if test="${isTrial && !isExpired}">
				<spring:message code="supplier.dashboard.common.unpaid.message"/>
				<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info btn-sm hvr-pop marg-left-20 do-not-disable"><spring:message code="supplier.dashboard.subscribe"/></a>
			</c:if>
			<c:if test="${isExpired}">
				<i class="fa fa-exclamation-triangle"></i>
				<spring:message code="supplier.dashboard.common.expired.message"/>
				<a href="${pageContext.request.contextPath}/supplier/billing/buyPlan" class="btn btn-info btn-sm hvr-pop marg-left-20 do-not-disable"><spring:message code="account.overview.renew"/></a>
			</c:if>
		</span>
	</p>
</div>



<ol class="breadcrumb">
	<li>
		<a href="${pageContext.request.contextPath}/supplier/supplierDashboard"><spring:message code="application.dashboard" /></a>
	</li>
	<li class="active">${eventType.value}${' '}<c:if test="${eventDetails}"><spring:message code="application.details" /></c:if>
	     <c:if test="${documents}"><spring:message code="application.document" /></c:if>
 	     <c:if test="${eventTeam}"><spring:message code="application.teamMembers" /></c:if>
	     <c:if test="${eventMeeting}"><spring:message code="label.meeting" /></c:if>
	     <c:if test="${eventCq}"><spring:message code="questionnaire.label"/></c:if>
	     <c:if test="${eventBq}"><spring:message code="bq.label"/></c:if>
		 <c:if test="${eventSor}"><spring:message code="eventdescription.schedule.rate.label"/></c:if>
	     <c:if test="${eventMessage}"><spring:message code="buyer.eventReport.messagetab"/></c:if>
	     <c:if test="${supplierEventSummary}"><spring:message code="application.submission"/></c:if>
	</li>
</ol>


<div class="Section-title title_border gray-bg">
	<h2 class="trans-cap tender-request-heading">${event.eventName}</h2>
	<h2 class="trans-cap pull-right">
		Status: <span id="statusHeader"> ${event.status == 'CLOSED' or event.status == 'FINISHED' or event.status == 'COMPLETE' ? 'CLOSED': event.status} </span>
	</h2>
</div>
<c:if test="${!allowEventForThisBuyer}">
<div class="clear"></div>
<div  class="marg-bottom-15">
	<span class="ra_remain_dis bg-danger">
		<i class="fa fa-exclamation-triangle marg-left-10"></i>
		<strong><spring:message code="supplier.event.buyer.unpaid.message"/></strong>
	</span>
</div>
</c:if>
<c:if test="${eventType == 'RFA' && (event.status == 'CLOSED') && (!empty auctionRules.lumsumBiddingWithTax) && eventSupplier.revisedBidSubmitted == false}">
	<c:set var="warn" value="You have not yet submitted your revised auction Bill of Quantities" scope="request" />
</c:if>
<c:if test="${eventType == 'RFA' && event.status == 'CLOSED' && (!empty auctionRules.lumsumBiddingWithTax) && !empty bq.revisedGrandTotal && bq.revisedGrandTotal != bq.totalAfterTax && !eventSupplier.revisedBidSubmitted}">
	<c:set var="error" value="Your Bill of Quantities total does not match with your final Auction bid price. Please revise and submit." scope="request" />
</c:if>
<div class="clear"></div>
<div class="example-box-wrapper wigad-new resp-wid marg-top-10">
	<div id="timer-accord" class="Invited-Supplier-List white-bg-with-arrow marg-bottom-15 small-accordin-tab">
		<div class="row">
			<div class="col-md-7 col-sm-7 col-xs-12">
				<div class="row">
					<div class="col-md-9">
						<c:if test="${today lt event.eventStart}">
							<div class="meeting2-heading-new">
								<label><spring:message code="application.startdate" /> &amp; <spring:message code="application.time" /></label>
								<h3 class="date-time-show">
									<span> <fmt:formatDate value="${event.eventStart}" pattern="dd/MMM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</span>
								</h3>
							</div>
						</c:if>
						<c:if test="${today ge event.eventStart}">
							<div class="meeting2-heading-new">
								<label><spring:message code="rfaevent.end.date" /> &amp; <spring:message code="application.time" /></label>
								<h3 class="date-time-show">
									<span> <fmt:formatDate value="${event.eventEnd}" pattern="dd/MMM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</span>
								</h3>
							</div>
						</c:if>
						<span>
							<div class="meeting2-heading-new">
								<c:if test="${eventSupplier.submissionStatus == 'INVITED'  && event.status != 'SUSPENDED'}">
									<div class="ra_remain_dis">
										<h6>
											<spring:message code="supplierevent.kindly.term.condition" />
											.
										</h6>
									</div>
								</c:if>
								<div class="ra_fee">
									<spring:message code="rfxtemplate.participation.fee" />
									: ${event.participationFeeCurrency != null ? event.participationFeeCurrency : ""}
									<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${event.participationFees}" />
								</div>
							</div>
						</span>
					</div>
				</div>
				<div class="tag-line marg-left-10 marg-bottom-10 width100">
					<span> <b>Event ID:</b> ${event.eventId}
					</span>
					<c:if test="${eventType == 'RFA' }">
						<span> <b><spring:message code="rfx.auction.type.label" />:</b> ${event.auctionType.value}
						</span>
					</c:if>
					<span> <b><spring:message code="eventsummary.eventdetail.reference" />:</b> ${event.referanceNumber}
					</span> <br /> <span> <b><spring:message code="application.eventowner" />:</b> ${event.eventOwner}/${event.eventOwnerEmail}
					</span>
				</div>
				<c:if test="${eventSupplier.submitted && (eventSupplier.submissionStatus eq 'COMPLETED')}">
					<c:if test="${eventType eq 'RFA'}">
						<div class="row">
							<div class="col-md-9">
								<div class="tag-line marg-left-10 marg-bottom-10">
									<c:if test="${event.auctionType eq 'FORWARD_DUTCH' or event.auctionType eq 'REVERSE_DUTCH'}">
										<a id="idDutchConsole" class="step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out span6 offset3" href="${pageContext.request.contextPath}/auction/dutchAuctionConsole/${event.id}"><spring:message code="rfaevent.auction.hall" /></a>
									</c:if>
									<c:if test="${event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH' or event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID'}">
										<a id="idEnglishConsole" class="step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out span6 offset3" href="${pageContext.request.contextPath}/supplier/englishAuctionConsole/${event.id}"><spring:message code="rfaevent.auction.hall" /></a>
									</c:if>
								</div>
							</div>
						</div>
					</c:if>
				</c:if>
			</div>
			<c:if test="${eventType eq 'RFA' and event.status eq 'ACTIVE'}">
				<c:if test="${empty event.auctionResumeDateTime }">
					<c:if test="${(today.time lt event.eventEnd.time and today.time gt event.eventStart.time) or today.time lt event.eventStart.time}">
						<div class="col-md-5 col-sm-5 col-xs-12">
							<div class="content">
								<div class="counter counterForTime">
									<c:if test="${today.time lt event.eventEnd.time and today.time gt event.eventStart.time}">
										<div class="col-md-12 text-center align-right" style="margin: 0;">
											<h3>
												<spring:message code="supplierevent.time.left.end" />
											</h3>
										</div>
										<div class="main-example" style="margin-right: 10px">
											<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventEnd" />
											<div class="countdown-container align-right" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/supplier/viewSupplierEvent/RFA/${event.id}" data-date="${eventEnd}"></div>
										</div>
									</c:if>
									<c:if test="${today.time lt event.eventStart.time and event.status eq 'ACTIVE'}">
										<div class="col-md-12 text-center align-right" style="margin: 0;">
											<h3>
												<spring:message code="supplierevent.time.left.start" />
											</h3>
										</div>
										<div class="main-example">
											<fmt:formatDate value="${event.eventStart}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventStart" />
											<div class="countdown-container align-right" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/supplier/viewSupplierEvent/RFA/${event.id}" data-date="${eventStart}"></div>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</c:if>
				</c:if>
				<c:if test="${!empty event.auctionResumeDateTime and event.status eq 'ACTIVE'}">
					<c:if test="${(today.time lt event.eventEnd.time and today.time gt event.auctionResumeDateTime.time) or today.time lt event.auctionResumeDateTime.time}">
						<div class="col-md-5 col-sm-5 col-xs-12">
							<div class="content">
								<div class="counter counterForTime">
									<c:if test="${today.time lt event.eventEnd.time and today.time gt event.auctionResumeDateTime.time}">
										<div class="col-md-12 text-center align-right" style="margin: 0;">
											<h3>
												<spring:message code="supplierevent.time.left.end" />
											</h3>
										</div>
										<div class="main-example" style="margin-right: 100px">
											<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventEnd" />
											<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/supplier/viewSupplierEvent/RFA/${event.id}" data-date="${eventEnd}"></div>
										</div>
									</c:if>
									<c:if test="${today.time lt event.auctionResumeDateTime.time}">
										<div class="col-md-12 text-center align-right" style="margin: 0;">
											<h3>
												<spring:message code="supplierevent.time.left.resume" />
											</h3>
										</div>
										<div class="main-example">
											<fmt:formatDate value="${event.auctionResumeDateTime}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventStart" />
											<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/supplier/viewSupplierEvent/RFA/${event.id}" data-date="${eventStart}"></div>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</c:if>
				</c:if>
			</c:if>
			<c:if test="${eventType ne 'RFA' and event.status eq 'ACTIVE'}">
				<c:if test="${(today.time lt event.eventEnd.time and today.time gt event.eventStart.time) or today.time lt event.eventStart.time}">
					<div class="col-md-5 col-sm-5 col-xs-12">
						<div class="content">
							<div class="counter counterForTime">
								<c:if test="${today.time lt event.eventEnd.time and today.time gt event.eventStart.time}">
									<div class="col-md-12 text-center align-right" style="margin: 0;">
										<h3>
											<spring:message code="supplierevent.time.left.end" />
										</h3>
									</div>
									<fmt:formatDate value="${event.eventEnd}" pattern="E, dd MMM yyyy HH:mm:ss Z" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventEnd" />
									<div id="countdown" class="countDownTimer" data-date="${eventEnd}"></div>
								</c:if>
								<c:if test="${today.time lt event.eventStart.time}">
									<div class="col-md-12 text-center align-right" style="margin: 0;">
										<h3>
											<spring:message code="supplierevent.time.left.start" />
										</h3>
									</div>
									<fmt:formatDate value="${event.eventStart}" pattern="E, dd MMM yyyy HH:mm:ss Z" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventStart" />
									<div id="countdown" class="countDownTimer" data-date="${eventStart}"></div>
								</c:if>
							</div>
						</div>
					</div>
				</c:if>
			</c:if>
			<c:if test="${eventSupplier.submissionStatus eq 'ACCEPTED' or eventSupplier.submissionStatus eq 'COMPLETED' }" >
				<div>
					 <span class="col-md-12 align-right pd-rgt-bt" >
						  <b><h4><spring:message code="eventsummary.eventdetail.submission.status" />:
				        	 <span class="${eventSupplier.submissionStatus == 'COMPLETED' ? 'text-success' : 'text-danger' }">
				     	   		 ${eventSupplier.submissionStatus == 'COMPLETED' ? 'SUBMITTED' : 'PENDING' }
				        	 </span>
				  		</h4></b>
				 	</span>
				</div>
			</c:if>

			<!-- /#Main Div -->
		</div>
	</div>
</div>
<div class="clear"></div>
<div class="tab-main">
	<ul class="tab event-details-tabs supplier-tab">
		<%-- <c:if test="${event.status ne 'SUSPENDED'}"> --%>
		<li class="tab-link ${eventDetails == true ? 'current' : ''}"><a class="${eventDetails == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSupplierEvent/${eventType}/${event.id}"><spring:message code="eventdetails.event.details" /></a></li>
		<c:if test="${eventSupplier.submissionStatus =='ACCEPTED' or eventSupplier.submissionStatus =='COMPLETED' or (eventSupplier.submissionStatus =='REJECTED' and eventSupplier.isRejectedAfterStart)}">
			<li class="tab-link ${eventTeam == true ? 'current' : ''}"><a class="${eventTeam == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSupplierTeam/${eventType}/${event.id}" ><spring:message code="prevent.team.members" /></a></li>
			<c:if test="${event.documentReq}">
				<li class="tab-link ${documents == true ? 'current' : ''}"><a class="${documents == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSupplierDocument/${eventType}/${event.id}" style="${(eventType != 'RFA' and today lt event.eventStart ) ? 'pointer-events:none;' : ''}"><spring:message code="eventdescription.document.label" /></a></li>
			</c:if>
			<c:if test="${event.meetingReq}">
				<li class="tab-link ${eventMeeting == true ? 'current' : ''}"><a id ="supplierMeeting" class="${eventMeeting == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSupplierMeeting/${eventType}/${event.id}" ><spring:message code="label.meeting" /></a></li>
			</c:if>
			<c:if test="${event.questionnaires and (event.suspensionType != 'DELETE_NOTIFY' and event.suspensionType != 'DELETE_NO_NOTIFY')}">
				<li class="tab-link ${eventCq == true ? 'current' : ''}"><a class="${eventCq == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSupplierCq/${eventType}/${event.id}" style="${(eventType != 'RFA' and today lt event.eventStart ) ? 'pointer-events:none;' : ''}"><spring:message code="questionnaire.label" /></a></li>
			</c:if>
			<c:if test="${eventType ne 'RFI' and  event.billOfQuantity and (event.suspensionType != 'DELETE_NOTIFY' and event.suspensionType != 'DELETE_NO_NOTIFY')}">
				<li class="tab-link ${eventBq == true ? 'current' : ''}"><a class="${eventBq == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewBqList/${eventType}/${event.id}" style="${(eventType != 'RFA' and today lt event.eventStart ) ? 'pointer-events:none;' : ''}"><spring:message code="bq.label" /></a></li>
			</c:if>
			<c:if test="${event.scheduleOfRate and (event.suspensionType != 'DELETE_NOTIFY' and event.suspensionType != 'DELETE_NO_NOTIFY')}">
				<li class="tab-link ${eventSor == true ? 'current' : ''}"><a class="${eventSor == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSorList/${eventType}/${event.id}" style="${(eventType != 'RFA' and today lt event.eventStart ) ? 'pointer-events:none;' : ''}"><spring:message code="eventdescription.schedule.rate.label" /></a></li>
			</c:if>
			<li class="tab-link ${eventMessage == true ? 'current' : ''}"><a class="${eventMessage == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSupplierEventMessages/${eventType}/${event.id}" ><spring:message code="buyer.eventReport.messagetab" /></a></li>
			<li class="tab-link ${supplierEventSummary == true ? 'current' : ''}"><a class="${supplierEventSummary == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/viewSupplierEventSummary/${eventType}/${event.id}" > <!-- Summary --> <spring:message code="application.submission" />
			</a></li>
		</c:if>
	</ul>
</div>

<style>

.pd-rgt-bt {
	padding-right: 30px;
    padding-bottom: 5px;
}

</style>