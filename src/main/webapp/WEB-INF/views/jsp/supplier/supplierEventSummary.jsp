<%@page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<spring:message var="supplierRfxSummaryDesk" code="application.supplier.rfx.summary" />
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxSummaryDesk}] });
});
</script>
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
				var redirectUrl = getContextPath() + "/supplier/viewSupplierEventSummary/${eventType}/${event.id}";
				if (messageOutput.body == 'CLOSED') {
					window.location.href = redirectUrl;
				} else if (messageOutput.body == 'SUSPENDED') {
					document.getElementById("statusHeader").innerHTML = "SUSPENDED";
					$(".counterForTime").hide();
					//$(".rowForSuspend").hide();
				} else if (messageOutput.body == 'RESUME') {
					window.location.href = redirectUrl;
				}
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
	
	$(document).ready(function() {
		connect();
	});
</script>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">

			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/supplierregistration/supplierSubmissionHeader.jsp" />
			<div class="tab-main-inner pad_all_15">
				<div id="tab-1" class="tab-content">
					<div class="example-box-wrapper wigad-new">
						<div class="col-md-12 pad0 marg-top-5">
							<div class="panel bgnone marg-none">
								<div class="panel-body">
									<div class="example-box-wrapper">
										<div class="panel-group" id="accordion">
											<c:out value="${eventType}" />
											<c:out value="${event.status}" />

											<div class="pull-right mt-20">
												<form:form action="${pageContext.request.contextPath}/supplier/supplierSummaryReport/${eventType}/${event.id}">
													<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10 w-170 " data-toggle="tooltip" data-placement="top" data-original-title="Download Summary Report" type="submit">
														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="eventReport.event.summary.button" /></span>
													</button>
												</form:form>
											</div>
											<c:if test="${eventType eq 'RFA' and (event.status eq 'FINISHED' or event.status eq 'COMPLETE' or event.status eq 'CLOSED')}">
												<div class="pull-right mt-20">
													<form:form action="${pageContext.request.contextPath}/supplier/downloadSupplierAuctionReport/${eventType}/${event.id}">
														<button class="float-right btn btn-sm btn-success hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title="Download Auction Report" type="submit">
															<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
															</span> <span class="button-content"><spring:message code="eventReport.auction.report.button" /></span>
														</button>
													</form:form>
												</div>
											</c:if>

											<div class="panel sum-accord">
												<div class="panel-heading">
													<h4 class="panel-title">
														<a data-toggle="collapse" class="accordion" href="#collapseTwo"><spring:message code="eventsummary.eventdetail.title" /> </a>
													</h4>
												</div>
												<div id="collapseTwo" class="panel-collapse collapse in">
													<div class="panel-body pad_all_15  border-bottom">
														<div class="main-panal-box-main border-bottom">
															<div class="main-panal-box">
																<label><spring:message code="eventsummary.eventdetail.refId" /> : </label>
																<p>${event.eventId}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="eventsummary.eventdetail.refNumber" /> : </label>
																<p>${event.referanceNumber}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="eventsummary.eventdetail.completename" /> : </label>
																<p>${event.eventName}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="application.eventowner" /> : </label>
																<p>${event.eventOwner}<br />${event.eventOwnerEmail}<br />${event.ownerPhoneNumber}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="application.eventstartdate" /> : </label>
																<p>
																	<fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																	<c:forEach items="${startReminders}" var="reminder" varStatus="status">
																		<br />
														Reminder ${status.index + 1}:
														<fmt:formatDate value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																	</c:forEach>
																</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="application.eventenddate" /> : </label>
																<p>
																	<fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																	<c:forEach items="${endReminders}" var="reminder" varStatus="status">
																		<br />
														Reminder ${status.index + 1}:
														<fmt:formatDate value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																	</c:forEach>
																</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="eventdetails.event.publish.date" /> : </label>
																<p>
																	<fmt:formatDate value="${event.eventPublishDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</p>
															</div>
															<c:if test="${eventType != 'RFA'}">
																<div class="main-panal-box">
																	<label><spring:message code="eventdetails.event.visibility" /> : </label>
																	<p>${event.eventVisibility}</p>
																</div>
																<div class="main-panal-box">
																	<label><spring:message code="eventdetails.event.validity.days" /> : </label>
																	<p>${event.submissionValidityDays}</p>
																</div>
															</c:if>
															<div class="main-panal-box">
																<label><spring:message code="eventdetails.event.paticipation.currency" /> : </label> ${event.participationFeeCurrency != null ? event.participationFeeCurrency : ""}
																<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${event.participationFees}" />
																<c:if test="${empty event.participationFees && empty event.participationFeeCurrency }">
																	<p>-</p>
																</c:if>
															</div>
															<div class="main-panal-box">
																<label>Event Deposit Fee : </label> 
																 ${event.depositCurrency != null ? event.depositCurrency : ""}
																<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${event.deposit}" />
																<c:if test="${empty event.deposit && empty event.depositCurrency}">
																	<p>-</p>
																</c:if>
															</div>
															<%-- <div class="main-panal-box ">
																<div class="row">
																	<div class="col-md-6">
																		<label><spring:message code="eventdetails.event.category" /> : </label>
																	</div>

																	<div class="col-md-6">
																		<ul style="margin-left: -40px;">
																			<c:forEach items="${event.industryCategories}" var="industryCategories">
																				<li>${industryCategories.name}</li>
																			</c:forEach>
																		</ul>
																	</div>
																</div>
															</div> --%>



															<%-- <c:if test="${previousEvent !=null}">
										<div class="main-panal-box ">
											<label>Previous Event: </label>
											<p>
												<a href="${pageContext.request.contextPath}/buyer/${event.previousEventType}/eventSummary/${event.previousEventId}"> ${previousEvent.eventName} - <i>[${previousEvent.eventId} ${event.nextEventType}]</i>
												</a>
											</p>
										</div>
									</c:if> --%>
															<%-- <c:if test="${nextEvent !=null}">
										<div class="main-panal-box">
											<label>Next Event: </label>
											<c:if test="${nextEvent.status == 'DRAFT'}">
												<p>
													<a href="${pageContext.request.contextPath}/buyer/${event.nextEventType}/createEventDetails/${event.nextEventId}">${nextEvent.eventName} - <i>[${nextEvent.eventId} ${event.nextEventType}]</i>
													</a>
												</p>
											</c:if>
											<c:if test="${nextEvent.status != 'DRAFT'}">
												<p>
													<a href="${pageContext.request.contextPath}/buyer/${event.nextEventType}/eventSummary/${event.nextEventId}">${nextEvent.eventName} - <i>[${nextEvent.eventId} ${event.nextEventType}]</i>
													</a>
												</p>
											</c:if>
										</div>
									</c:if> --%>
															<div class="col-md-12 border-bottom"></div>
															<div class="main-panal-box-main comm-info">
																<table class="tabaccor padding-none-td" width="100%" cellpadding="0" cellspacing="0" border="0">
																	<thead>
																		<tr>
																			<th><spring:message code="eventsummary.eventdetail.correspondingadds" /></th>
																			<c:if test="${eventType != 'RFA' }">
																				<th><spring:message code="eventsummary.eventdetail.deliveryadds" /></th>
																			</c:if>
																		</tr>
																	</thead>
																	<tbody>
																		<tr>
																			<td>${event.ownerLine1},<br> ${event.ownerLine2},<br> ${event.ownerCity} <br> ${event.ownerState} <br> ${event.ownerCountry} <br>
																			</td>
																			<c:if test="${eventType != 'RFA' }">
																				<td><c:if test="${!empty event.deliveryAddress.title}">
														${event.deliveryAddress.title},<br>
															${event.deliveryAddress.line1},${event.deliveryAddress.line2},<br>
															${event.deliveryAddress.city},
															${event.deliveryAddress.zip},<br>
															${event.deliveryAddressState} <br>
															${event.deliveryAddressCountry} <br>
																					</c:if></td>
																			</c:if>
																		</tr>
																	</tbody>
																</table>
															</div>
															<div class="col-md-12 border-bottom"></div>
															<div class="main-panal-box-main comm-info contactPersonsEventSummery">
																<br />
																<h2>
																	<spring:message code="eventdetails.event.contact.details" />
																</h2>
																<table class="contactPersons ph_table display table table-bordered">
																	<thead>
																		<tr>
																			<th><spring:message code="eventdetails.event.title" /></th>
																			<th><spring:message code="application.name" /></th>
																			<th><spring:message code="application.designation" /></th>
																			<th><spring:message code="application.contact" /></th>
																			<th><spring:message code="application.mobile" /></th>
																			<th><spring:message code="application.emailaddress" /></th>
																		</tr>
																	</thead>
																	<tbody>
																		<c:forEach items="${eventContacts}" var="contact">
																			<tr>
																				<td>${contact.title}</td>
																				<td>${contact.contactName}</td>
																				<td>${contact.designation}</td>
																				<td>${contact.contactNumber}</td>
																				<td>${contact.mobileNumber != null ? contact.mobileNumber : ""}</td>
																				<td>${contact.comunicationEmail}</td>
																			</tr>
																		</c:forEach>
																	</tbody>
																</table>
																<div class="col-md-12 align-left">
																	<div class="row"></div>
																</div>
															</div>
														</div>
														<div class="main-panal-box-main comm-info">
															<h2>
																<spring:message code="eventsummary.eventdetail.commercialinformation" />
															</h2>
															<div class="main-panal-box">
																<label><spring:message code="pr.base.currency" /> : </label>
																<p>${event.baseCurrency}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="eventdescription.decimal.label" /> : </label>
																<p>${event.decimal}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="eventdescription.paymentterm.label" /> : </label>
																<p>${event.paymentTerm}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="eventdescription.name" /> : </label>
																<p>${event.eventDescription}</p>
															</div>
															<div class="main-panal-box">
																<label><spring:message code="label.businessUnit" /> : </label>
																<p>${event.businessUnit}</p>
															</div>
															<%-- 															<div class="main-panal-box">
																<label>Budget Amount : </label>
																<p>
																	<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.budgetAmount}" />
																</p>
															</div>
															<div class="main-panal-box">
																<label>Historic Amount : </label>
																<p>
																	<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.historicaAmount}" />
																</p>
															</div> --%>
														</div>
														<c:if test="${eventType eq 'RFA' and (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')}">
															<div class="col-md-12 border-bottom"></div>
															<div class="main-panal-box-main comm-info">
																<h2>
																	<spring:message code="eventsummary.eventdetail.time.extension" />
																</h2>
																<div class="main-panal-box">
																	<label><spring:message code="eventsummary.eventdetail.extensiontype" /> : </label>
																	<p>${event.timeExtensionType}</p>
																</div>
																<c:if test="${event.timeExtensionType eq 'AUTOMATIC'}">
																	<div class="main-panal-box">
																		<label><spring:message code="eventsummary.eventdetail.extensionduration" /> : </label>
																		<p>${event.timeExtensionDuration}-${event.timeExtensionDurationType}</p>
																	</div>
																	<div class="main-panal-box">
																		<label><spring:message code="eventsummary.eventdetail.extension.triggered" />: </label>
																		<p>${event.timeExtensionLeadingBidValue}-${event.timeExtensionLeadingBidType}</p>
																	</div>
																	<div class="main-panal-box">
																		<label><spring:message code="eventsummary.eventdetail.extension.maxRound" /> : </label>
																		<p>${event.extensionCount}</p>
																	</div>
																	<div class="main-panal-box">
																		<label><spring:message code="eventsummary.eventdetail.extension.disqualify" /> : </label>
																		<c:if test="${event.autoDisqualify}">
																			<p>
																				<spring:message code="application.yes" />
																			</p>
																		</c:if>
																		<c:if test="${!event.autoDisqualify}">
																			<p>
																				<spring:message code="application.no2" />
																			</p>
																		</c:if>
																	</div>
																</c:if>
															</div>
														</c:if>
													</div>
												</div>
											</div>
											<c:if test="${eventType eq 'RFA'}">
												<jsp:include page="/WEB-INF/views/jsp/supplierregistration/supplierAuctionRules.jsp"></jsp:include>
											</c:if>
											<jsp:include page="supplierSummaryMeeting.jsp" />
											<jsp:include page="supplierSummaryCq.jsp" />
											<c:if test="${eventType != 'RFI'}">
												<jsp:include page="supplierSummaryBq.jsp" />
											</c:if>
											<jsp:include page="supplierSummarySor.jsp" />
										</div>

										<div class="marg-top-20 btns-lower">
											<div class="row rowForSuspend">
												<div class="col-md-12 col-xs-12 col-ms-12">
														<c:set var="isResubmission" value="${eventType == 'RFA' && eventSupplier.submissionStatus =='COMPLETED' && (event.status == 'CLOSED') && (!empty auctionRules.lumsumBiddingWithTax) && eventSupplier.revisedBidSubmitted == false}" />
														<c:set var="disableFinish" value="${isResubmission && bq != null && bq.revisedGrandTotal != null && bq.revisedGrandTotal != bq.totalAfterTax }" />
 														<c:if test="${isResubmission or (eventSupplier.submissionStatus =='ACCEPTED' and event.status =='ACTIVE' and ((event.eventStart le today and eventType != 'RFA') or (event.eventPublishDate le today and eventType == 'RFA')))  and (eventPermissions.editor or isAdmin)}">
															<button class="btn btn-black ph_btn marg-left-10 hvr-pop hvr-rectangle-out1 finish-event pull-right ${disableFinish ? 'disabled' : ''} " value="${event.id}">${isResubmission ? 'Finish Revised Submission' : ((eventType != 'RFA') ? 'Finish' : 'Pre-bid Finish')}</button>
 														</c:if> 
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
<!-- ----------------------------------PopUp- 2---------- -->
<div class="modal fade" id="confirmFinishEvent" role="dialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm" />
					<span>${isResubmission ? 'Finish Revised Submission' : ((eventType != 'RFA') ? 'Finish' : 'Pre-bid Submission ')}</span>
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> Are you sure you want to submit ? This is your Final <span>${isResubmission ? 'Revised' : ((eventType != 'RFA') ? ' ' : 'pre-bid ')}</span> Submission, Once submitted, you will not be able to modify.
				</label> <input type="hidden" id="finishIdEvent" /> <input type="hidden" class="siteVisitMandatoryCheck" value="true">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out finish-invitation  finish-event" value="${event.id}">
					<spring:message code="application.yes" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no2" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmRejectedMeeting" role="dialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog for-delete-all reminder documentBlock warning-model">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header warning-inline">
				<img src="${pageContext.request.contextPath}/resources/images/warning.svg" alt="warning" width="20px">
				<h3 class="ml-12">Site Visit Mandatory</h3>
				<button class="close for-absulate siteVisitClose" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> Meeting "<span id="rejectedMeetingNamesList"></span>" is mandatory to attend. Our records shows that you have not attended. If this is a mistake kindly contact the event owner to update your attendance. Proceed anyway?
				</label> <input type="hidden" id="finishIdEvent" /> <input type="hidden" class="siteVisitMandatoryCheck" value="true">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out finish-invitation  finish-event" value="${event.id}">Yes</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right siteVisitClose" data-dismiss="modal">No</button>
			</div>
		</div>
	</div>
</div>
<style>
.warning-model {
	width: 450px !important;
}

.disablePaddings {
	padding: 0;
}

.ml-12 {
	margin-left: 12px;
}

.warning-inline {
	display: flex;
	align-items: center;
}

.disablePaddings label {
	color: #333 !important;
	font-size: 12px !important;
}

.disablePaddings label span {
	font-size: 12px !important;
}

.col-md-1.disablePaddings {
	max-width: 50px;
}

.minimizePadding {
	padding: 5px;
}

.mt-20 {
	margin-top: 25px;
	margin-bottom: 5px;
}

.w-170+.tooltip>.tooltip-inner {
	width: 180px !important;
}
</style>
<script>
	<c:if test="${eventPermissions.viewer and eventSupplier.submissionStatus !='REJECTED' and !eventSupplier.isRejectedAfterStart}">
		$(window).bind('load', function() {
			var allowedFields = '.tab-link > a';
			disableFormFields(allowedFields);
		});
		</c:if>
	
	counterMeetings();
	setInterval(function() {
		counterMeetings();
	}, 60000);
	function counterMeetings() {
		$('.countDownTimer').each(function() {

			var target_date = new Date($(this).attr('data-date')).getTime();

			// variables for time units
			var days, hours, minutes, seconds;

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
			htmldata = '';
			if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
				htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes + '</span><span class="seconds"><b>Seconds</b><br>' + seconds + '</span>';
			}//$('#countdown').html(htmldata);
			$(this).html(htmldata);

		});
	}
	
	$(document).delegate('.finish-event', 'click', function(e) {
		e.preventDefault();
		$('#confirmFinishEvent').find('#finishIdEvent').val($(this).attr('eventId'));
		$('#confirmFinishEvent').modal().show();
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierSubmissionEvent.js?1"/>"></script>
