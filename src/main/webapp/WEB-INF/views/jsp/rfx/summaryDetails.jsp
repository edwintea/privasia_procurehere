<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
<jsp:useBean id="now" class="java.util.Date" />
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<script type="text/javascript">
	var stompClient = null;
	function connect() {
		var socket = new SockJS('${pageContext.request.contextPath}/auctions');
		stompClient = Stomp.over(socket);
		
		stompClient.connect({}, function(frame) {
			//setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/auctionTopic/${event.id}', function(messageOutput) {
				console.log("the responce : " +messageOutput.body);
 				if(messageOutput.body == 'CLOSED'){
					window.location.href = getContextPath() + "/buyer/${eventType}/viewSummary/${event.id}";
				} else if(messageOutput.body == 'SUSPENDED'){
					window.location.href = getContextPath() + "/buyer/${eventType}/viewSummary/${event.id}";
				}else if(messageOutput.body == 'RESUME'){
					window.location.href = getContextPath() + "/buyer/${eventType}/viewSummary/${event.id}";
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

	$(document).ready(function() {
		connect();
	});
	
	$(window).unload(function(){
		if(stompClient.connected) {
			console.log('Closing websocket');
			disconnect();
		}
	});
</script>

<div class="col-md-12 pad0">
	<div class="panel bgnone">
		<div class="panel-body">
			<div class="example-box-wrapper">
				<div class="panel-group" id="accordion">
					<div class="panel sum-accord">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" class="accordion" href="#collapseTwo">Event Detail </a>
							</h4>
						</div>
						<div id="collapseTwo" class="panel-collapse collapse in">
							<div class="panel-body pad_all_15  border-bottom">
								<div class="main-panal-box-main border-bottom">
									<c:if test="${!empty previousRequest}">
										<div class="main-panal-box ">
											<label><spring:message code="summaryDetails.form.request" />: </label>
											<p>
												<a id="idPreviousRequest" href="${pageContext.request.contextPath}/buyer/viewSourcingSummary/${previousRequest.id}"> ${previousRequest.sourcingFormName} - <i>[${previousRequest.formId}]</i>
												</a>
											</p>
										</div>
									</c:if>
									<div class="main-panal-box">
										<label><spring:message code="eventsummary.eventdetail.refId" /> : </label>
										<p>${event.eventId}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventsummary.eventdetail.refNumber" /> : </label>
										<p>${event.referanceNumber!=null?event.referanceNumber:'-'}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventsummary.eventdetail.completename" /> : </label>
										<p>${event.eventName!=null?event.eventName:'-'}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="application.eventowner" /> : </label>
										<p>${event.eventOwner.name}<br />${event.eventOwner.communicationEmail}<br />${event.eventOwner.phoneNumber}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventsummary.eventdetail.startdate" /> : </label>
										<p>
											<fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											<c:forEach items="${startReminders}" var="reminder" varStatus="status">
												<br />
												<spring:message code="rfaevent.reminder" /> ${status.index + 1}:
														<fmt:formatDate value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</c:forEach>
										</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventsummary.eventdetail.enddate" /> : </label>
										<p>
											<fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											<c:forEach items="${endReminders}" var="reminder" varStatus="status">
												<br />
												<spring:message code="rfaevent.reminder" /> ${status.index + 1}:
														<fmt:formatDate value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</c:forEach>
										</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventsummary.eventdetail.publishdate" /> : </label>
										<p>
											<fmt:formatDate value="${event.eventPublishDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</p>
									</div>

									<div class="main-panal-box">
										<label><spring:message code="eventdetails.event.visibility" /> : </label>
										<p>${event.eventVisibility!=null?event.eventVisibility:'-'}</p>
									</div>
									<c:if test="${eventType != 'RFA'}">
										<div class="main-panal-box">
											<label><spring:message code="eventdetails.event.validity.days" /> : </label>
											<p>${event.submissionValidityDays!=null?event.submissionValidityDays:'-'}</p>
										</div>
									</c:if>
									<div class="main-panal-box">
										<label><spring:message code="eventdetails.event.paticipation.currency" /> : </label> ${event.participationFeeCurrency !=null ? event.participationFeeCurrency.currencyCode : ''}
										<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${event.participationFees}" />
										<c:if test="${empty event.participationFees && empty event.participationFeeCurrency}">
											<p>-</p>
										</c:if>
									</div>
									<div class="main-panal-box">
										<label>Event Deposit Fee :</label> ${event.depositCurrency !=null ? event.depositCurrency.currencyCode :''}
										<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${event.deposit}" />
										<c:if test="${empty event.deposit && empty event.depositCurrency}">
											<p>-</p>
										</c:if>
									</div>

									<div class="main-panal-box ">
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
									</div>

									<div class="main-panal-box">
										<label><spring:message code="eventsummary.eventdetail.deliverydate" /> : </label>
										<!-- <p></p>-->
										<c:if test="${!empty event.deliveryDate}">
											<fmt:formatDate value="${event.deliveryDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</c:if>
										<c:if test="${empty event.deliveryDate}">
											<p>
												<spring:message code="application.not.applicable" />
											</p>
										</c:if>
									</div>

									<c:if test="${eventType == 'RFI'}">
										<c:if test="${!empty event.expectedTenderStartDate and !empty event.expectedTenderEndDate }">
											<div class="main-panal-box">
												<label>Expected Tender Start &#38; End Date:</label>
												<fmt:formatDate value="${event.expectedTenderStartDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												-
												<fmt:formatDate value="${event.expectedTenderEndDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</div>
										</c:if>
										<c:if test="${!empty event.feeStartDate and !empty event.feeEndDate }">
											<div class="main-panal-box">
												<label>Fee Start &#38; End Date:</label>
												<fmt:formatDate value="${event.feeStartDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												-
												<fmt:formatDate value="${event.feeEndDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</div>
										</c:if>
									</c:if>

									<div class="main-panal-box">
										<label><spring:message code="sourcing.minimumSupplierRating" /> : </label>
										<p>${not empty event.minimumSupplierRating?event.minimumSupplierRating:'N/A'}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="sourcing.maximumSupplierRating" /> : </label>
										<p>${not empty event.maximumSupplierRating?event.maximumSupplierRating:'N/A'}</p>
									</div>

									<c:if test="${previousEvent != null}">
										<div class="main-panal-box ">
											<label><spring:message code="eventsummary.eventdetail.previous" />: </label>
											<p>
												<a href="${pageContext.request.contextPath}/buyer/${event.previousEventType}/eventSummary/${event.previousEventId}"> ${previousEvent.eventName} - <i>[${previousEvent.eventId} ${event.nextEventType}]</i>
												</a>
											</p>
										</div>
									</c:if>
									<c:if test="${nextEvent !=null}">
										<div class="main-panal-box">
											<label><spring:message code="eventsummary.eventdetail.next" />: </label>
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
									</c:if>
									<div class="col-md-12 border-bottom"></div>
									<div class="main-panal-box-main comm-info">
										<table class="tabaccor padding-none-td  display table" width="100%" cellpadding="0" cellspacing="0" border="0">
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
													<td>${event.eventOwner.buyer.line1},<br> ${event.eventOwner.buyer.line2},<br> ${event.eventOwner.buyer.city} <br> ${event.eventOwner.buyer.state.stateName} <br> ${event.eventOwner.buyer.state.country.countryName} <br>
													</td>
													<c:if test="${eventType != 'RFA' }">
														<td><c:if test="${!empty event.deliveryAddress.title}">
														${event.deliveryAddress.title},<br>
															${event.deliveryAddress.line1},${event.deliveryAddress.line2},<br>
															${event.deliveryAddress.city},
															${event.deliveryAddress.zip},<br>
															${event.deliveryAddress.state.stateName} <br>
															${event.deliveryAddress.state.country.countryName} <br>
															</c:if> <c:if test="${empty event.deliveryAddress.title}">
															${'-'}<br>
															</c:if></td>
													</c:if>
												</tr>
											</tbody>
										</table>
									</div>
									<div class="col-md-12 border-bottom"></div>
									<div class="main-panal-box-main comm-info">
										<br />
										<h2>
											<spring:message code="eventdetails.event.contact.details" />
										</h2>
										<div class="table-responsive width100">
											<table class="contactPersons display table table-bordered topBorderTable">
												<thead>
													<tr>
														<th class="align-left width_150 width_150_fix"><spring:message code="eventdetails.event.title" /></th>
														<th class="align-left width_150 width_150_fix"><spring:message code="application.name" /></th>
														<th class="align-left width_150 width_150_fix"><spring:message code="application.designation" /></th>
														<th class="align-left width_150 width_150_fix"><spring:message code="application.contact" /></th>
														<th class="align-left width_150 width_150_fix"><spring:message code="application.mobile" /></th>
														<th class="align-left width_150 width_150_fix"><spring:message code="application.emailaddress" /></th>
													</tr>
												</thead>
												<tbody>
													<c:if test="${empty eventContactsList}">
														<td valign="top" colspan="6" class="dataTables_empty">No data available in table</td>
													</c:if>
													<c:if test="${!empty eventContactsList}">
														<c:forEach items="${eventContactsList}" var="contact">
															<tr>
																<td class="align-left width_150 width_150_fix">${contact.title != null ?contact.title:'-'}</td>
																<td class="align-left width_150 width_150_fix">${contact.contactName != null ?contact.contactName:'-'}</td>
																<td class="align-left width_150 width_150_fix">${contact.designation != null ?contact.designation:'-'}</td>
																<td class="align-left width_150 width_150_fix">${contact.contactNumber != null ?contact.contactNumber:'-'}</td>
																<td>${contact.mobileNumber != null ? contact.mobileNumber : "-"}</td>
																<td class="align-left width_150 width_150_fix">${contact.comunicationEmail != null ?contact.comunicationEmail:'-'}</td>
															</tr>
														</c:forEach>
													</c:if>
												</tbody>
											</table>
										</div>
										<div class="col-md-12 align-left">
											<div class="row"></div>
										</div>
									</div>
								</div>

								<div class="main-panal-box-main comm-info">
									<h2>
										<spring:message code="sourcing.procurement.information" />
									</h2>
									<div class="main-panal-box">
										<label><spring:message code="procurement.info.method" /> : </label>
										<p>${event.procurementMethod.procurementMethod}</p>
									</div>
									<div class="main-panal-box-main comm-info">
										<div class="main-panal-box">
											<label><spring:message code="procurement.info.category" /> : </label>
											<p>${event.procurementCategories.procurementCategories}</p>
										</div>
									</div>
								</div>
								<div class="main-panal-box-main comm-info">
									<h2>
										<spring:message code="eventsummary.eventdetail.commercialinformation" />
									</h2>
									<div class="main-panal-box">
										<label><spring:message code="pr.base.currency" /> : </label>
										<p>${event.baseCurrency.currencyCode}-${event.baseCurrency.currencyName}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventdescription.decimal.label" /> : </label>
										<p>${event.decimal!=null?event.decimal:'-'}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventdescription.paymentterm.label" /> : </label>
										<p>${event.paymentTerm!=null?event.paymentTerm:'-'}</p>
									</div>
								</div>
								<div class="main-panal-box-main comm-info">
									<div class="main-panal-box">
										<label><spring:message code="eventdescription.name" /> : </label>
										<p>${event.eventDescription!=null?event.eventDescription:'-'}</p>
									</div>
								</div>
								<div class="main-panal-box-main comm-info">
									<div class="main-panal-box">
										<label><spring:message code="eventdescription.costcenter.label" /> : </label>
										<p>${event.costCenter.costCenter!=null?event.costCenter.costCenter:' ' }-${event.costCenter.description!=null?event.costCenter.description:' ' }</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="label.groupCode" /> : </label>
										<p>${event.groupCode.groupCode != null ? event.groupCode.groupCode : '-'}-${event.groupCode.description != null ? event.groupCode.description : ''}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="label.businessUnit" /> : </label>
										<p>${not empty event.businessUnit ? event.businessUnit.unitName : '-'}</p>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventdescription.budgetamount.label" /> : </label>
										<p>
											<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.budgetAmount}" />
										</p>
										<c:if test="${empty event.budgetAmount}">
											<p>-</p>
										</c:if>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventdescription.historicamount.label" /> : </label>
										<p>
											<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.historicaAmount}" />
										</p>
										<c:if test="${empty event.historicaAmount}">
											<p>-</p>
										</c:if>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="eventdescription.estimatedBudget.label" /> : </label>
										<p>
											<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.estimatedBudget}" />
										</p>
										<c:if test="${empty event.estimatedBudget}">
											<p>-</p>
										</c:if>
									</div>
									<div class="main-panal-box">
										<label><spring:message code="internalremarks.summary.name" /></label>
										<p>${event.internalRemarks!=null?event.internalRemarks:'-'}</p>
									</div>
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
														<spring:message code="application.no" />
													</p>
												</c:if>
											</div>
											<c:if test="${event.autoDisqualify}">
												<div class="main-panal-box">
													<label><spring:message code="eventsummary.eventdetail.extension.disqualifycount" />: </label>
													<p>${event.bidderDisqualify}</p>
												</div>
											</c:if>
										</c:if>
									</div>
								</c:if>
							</div>
						</div>
					</div>
					<jsp:include page="summaryEventSettings.jsp" />
					<c:if test="${eventType eq 'RFA'}">
						<jsp:include page="summaryAuctionRules.jsp" />
					</c:if>
					<c:if test="${event.documentReq}">
						<jsp:include page="summaryDocuments.jsp" />
					</c:if>
					<c:if test="${(!event.viewSupplerName) and (eventPermissions.owner or eventPermissions.editor or eventPermissions.approverUser or ( eventPermissions.viewer and !eventPermissions.leadEvaluator and !eventPermissions.evaluator and !eventPermissions.opener))}">
						<jsp:include page="summaryInveiteSuppliers.jsp" />
					</c:if>
					<c:if test="${event.viewSupplerName}">
						<jsp:include page="summaryInveiteSuppliers.jsp" />
					</c:if>
					<c:if test="${event.meetingReq}">
						<jsp:include page="summaryEventMeetings.jsp" />
					</c:if>
					<c:if test="${event.questionnaires}">
						<jsp:include page="summaryCq.jsp" />
					</c:if>
					<c:if test="${event.billOfQuantity}">
						<jsp:include page="summaryBq.jsp" />
					</c:if>
					<c:if test="${event.scheduleOfRate}">
						<jsp:include page="summarySor.jsp" />
					</c:if>
					<jsp:include page="summaryEnvelop.jsp" />
					<jsp:include page="summaryTimeLine.jsp" />
					<jsp:include page="summaryEventTeamMembers.jsp" />
					<c:if test="${not empty event.approvals}">
						<jsp:include page="summaryEventApprovals.jsp" />
					</c:if>
					<c:if test="${not empty event.suspensionApprovals}">
						<jsp:include page="summaryEveSuspApprovals.jsp" />
					</c:if>
					
					<c:if test="${eventType != 'RFI'}">
						<jsp:include page="awardApprovals.jsp" />
					</c:if>
					
					<c:if test="${eventType == 'RFT' && enableEventUserControle }">
						<jsp:include page="eventAdditionalDocument.jsp" />
					</c:if>
					<c:if test="${event.status eq 'SUSPENDED'}">
						<div class="panel">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#mailbox"> <spring:message code="eventsummary.message" />
									</a>
								</h4>
							</div>
							<div id="mailbox" class="panel-collapse collapse">
								<jsp:include page="mailBox.jsp" />
							</div>
						</div>
					</c:if>
					<c:if test="${event.status eq 'FINISHED'}">
						<jsp:include page="eventConclusion.jsp" />
					</c:if>
					<c:choose>
						<c:when test="${(!event.viewSupplerName) and (eventPermissions.owner or eventPermissions.editor or eventPermissions.approverUser or ( eventPermissions.viewer and !eventPermissions.leadEvaluator and !eventPermissions.evaluator and !eventPermissions.opener ))}">
							<jsp:include page="summaryEventAudit.jsp" />
						</c:when>
						<c:otherwise>
							<c:if test="${(event.viewSupplerName and !((isAdmin or buyerReadOnlyAdmin)  and event.status eq 'COMPLETE')) or (isAdmin and event.status eq 'FINISHED') or (buyerReadOnlyAdmin and event.status eq 'FINISHED')}">
								<jsp:include page="summaryEventAudit.jsp" />
							</c:if>
							<c:if test="${eventType eq 'RFT' and (isAdmin or buyerReadOnlyAdmin)  and event.status eq 'COMPLETE' and ((!event.viewSupplerName and event.disableMasking) or (event.viewSupplerName and !event.disableMasking))}">
								<jsp:include page="summaryEventAudit.jsp" />
							</c:if>
						</c:otherwise>
					</c:choose>

					<c:if test="${eventType eq 'RFA' and !(event.auctionType eq 'REVERSE_DUTCH' or event.auctionType eq 'FORWARD_DUTCH') and !(event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED')}">
						<div class="panel sum-accord marg-top-5">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" class="accordion" href="#collapseBidHistory"> <spring:message code="auction.bidhistory.label" />
									</a>
								</h4>
							</div>
							<div id="collapseBidHistory" class="panel-collapse collapse">
								<div class="panel-body">
									<input type="hidden" value="${event.id}" class="eventIdInBidHis">
									<div class="table-responsive width100 borderAllData">
										<jsp:include page="/WEB-INF/views/jsp/auctionConsole/bidHistory.jsp" />
									</div>
								</div>
							</div>
						</div>
					</c:if>
					<c:if
						test="${eventType eq 'RFA' and !(event.auctionType eq 'REVERSE_DUTCH' or event.auctionType eq 'FORWARD_DUTCH') and (eventPermissions.owner or eventPermissions.viewer or  eventPermissions.editor  or eventPermissions.evaluator or eventPermissions.leadEvaluator or eventPermissions.conclusionUser or isAdmin or buyerReadOnlyAdmin or eventPermissions.approver)  and ((event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall)}">
						<div class="panel sum-accord marg-top-5">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" class="accordion" href="#collapseBidHistory"> <spring:message code="auction.bidhistory.label" />
									</a>
								</h4>
							</div>
							<div id="collapseBidHistory" class="panel-collapse collapse">
								<div class="panel-body">
									<input type="hidden" value="${event.id}" class="eventIdInBidHis">
									<div class="table-responsive width100 borderAllData">
										<jsp:include page="/WEB-INF/views/jsp/auctionConsole/bidHistory.jsp" />
									</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="table_f_action_btn submitButtonsAllActions">
							<!-- Draft Block -->
							<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') and ((eventPermissions.owner or eventPermissions.viewer or eventPermissions.editor))}">
								<!-- <div class="col-md-2"> -->
								<form:form modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/summeryPrevious" method="post" class="pull-left">
									<form:hidden path="id" />
									<form:hidden path="documentCompleted" />
									<form:hidden path="documentReq" />
									<form:hidden path="supplierCompleted" />
									<form:hidden path="eventVisibility" />
									<form:hidden path="meetingCompleted" />
									<form:hidden path="meetingReq" />
									<form:hidden path="cqCompleted" />
									<form:hidden path="questionnaires" />
									<form:hidden path="bqCompleted" />
									<form:hidden path="billOfQuantity" />
									<form:hidden path="scheduleOfRate" />
									<form:hidden path="envelopCompleted" />
									<form:hidden path="summaryCompleted" />
									<c:if test="${empty viewMode}">
										<form:button type="submit" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1" id="priviousStep">
											<spring:message code="application.previous" />
										</form:button>
									</c:if>
								</form:form>
								<!-- </div>
							<div class="col-md-6"></div> -->
								<c:if test="${(event.status eq 'SUSPENDED' or event.status eq 'DRAFT') and (eventPermissions.owner )}">
									<!-- <div class="col-md-2 pull-right"> -->
									<!-- finish -->
									<form:form modelAttribute="event" cssClass="pull-right" action="${pageContext.request.contextPath}/buyer/${eventType}/finishEvent" method="post" id="formResumeEvent">
										<form:hidden path="id" />
										<form:hidden path="eventName" />
										<form:hidden path="documentCompleted" />
										<form:hidden path="documentReq" />
										<form:hidden path="supplierCompleted" />
										<form:hidden path="eventVisibility" />
										<form:hidden path="meetingCompleted" />
										<form:hidden path="meetingReq" />
										<form:hidden path="cqCompleted" />
										<form:hidden path="questionnaires" />
										<form:hidden path="bqCompleted" />
										<form:hidden path="billOfQuantity" />
										<form:hidden path="scheduleOfRate" />
										<form:hidden path="envelopCompleted" />
										<form:hidden path="summaryCompleted" />
										<c:if test="${event.status eq 'SUSPENDED'}">
											<c:if test="${(eventType eq 'RFA' and event.eventStart le now) and event.enableSuspensionApproval == false}">
												<form:button type="button" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out pull-right" id="ResumeEvent">
													<spring:message code="eventsummary.button.resume" />
												</form:button>
											</c:if>
											<c:if test="${(eventType eq 'RFA' and event.eventStart le now) and (!empty event.suspensionApprovals && event.enableSuspensionApproval == true && isSuspensionApprovalActive == false)}">
												<form:button type="button" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out pull-right" id="rfaResumeEvent">
													<spring:message code="eventsummary.button.resume" />
												</form:button>
											</c:if>
											<c:if test="${(eventType eq 'RFA' and event.eventStart le now) and (empty event.suspensionApprovals && event.enableSuspensionApproval == true && isSuspensionApprovalActive == false)}">
												<form:button type="button" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out pull-right" id="ResumeEvent">
													<spring:message code="eventsummary.button.resume" />
												</form:button>
											</c:if>
											
											<c:if test="${(eventType != 'RFA' or (eventType eq 'RFA' and event.eventStart gt now)) and event.enableSuspensionApproval == false}">
												<form:button type="submit" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out pull-right">
													<spring:message code="eventsummary.button.resume" />
												</form:button>
											</c:if>
											<c:if test="${(eventType != 'RFA' or (eventType eq 'RFA' and event.eventStart gt now)) and (!empty event.suspensionApprovals && event.enableSuspensionApproval == true && isSuspensionApprovalActive == false)}">
												<form:button type="submit" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out pull-right" id="resumeAllEvent">
													<spring:message code="eventsummary.button.resume" />
												</form:button>
											</c:if>
											<c:if test="${(eventType != 'RFA' or (eventType eq 'RFA' and event.eventStart gt now)) and (empty event.suspensionApprovals && event.enableSuspensionApproval == true && isSuspensionApprovalActive == false)}">
												<form:button type="submit" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out pull-right">
													<spring:message code="eventsummary.button.resume" />
												</form:button>
											</c:if>
										</c:if>
										<c:if test="${event.status eq 'DRAFT'}">
											<form:button type="submit" id="finishEvent" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out pull-right">
												<spring:message code="application.finish" />
											</form:button>
										</c:if>
									</form:form>
									<!-- </div> -->
								</c:if>
							</c:if>
							<!-- Draft Block End -->
							<!-- Cancel Block -->
							<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') and (eventPermissions.owner)}">
								<!-- <div class="col-md-2"> -->
								<a href="#confirmCancelEvent" role="button" class="btn btn-danger ph_btn hvr-pop pull-right" data-toggle="modal">${event.status eq 'DRAFT' ? 'Cancel Draft' : 'Cancel Event'}</a>
								<!-- </div> -->
							</c:if>
							<!-- Approved Block End-->
							<!-- Suspend Block -->
							<c:if test="${event.status eq 'ACTIVE' and (eventPermissions.owner )}">
								<!-- <div class="col-md-offset-5 col-md-1"> -->
								<c:if test="${event.allowToSuspendEvent}">
									<a href="#confirmSuspendEvent" role="button" class="btn btn-warning ph_btn hvr-pop align-center" data-toggle="modal"><spring:message code="eventsummary.button.event.suspend" /></a>
									<!-- </div> -->
								</c:if>
							</c:if>
 
								<!-- PH-2652 requirement -->
<%-- 							<c:if test="${(event.status eq 'FINISHED' and event.awarded) and eventType != 'RFI' and event.billOfQuantity}"> --%>
<!-- 								<div class="marg-bottom-20 marg-left-20"> -->
<%-- 									<a href="${pageContext.request.contextPath}/buyer/${eventType}/eventAward/${eventId}"> --%>
<!-- 										<button class="btn btn-warning ph_btn hvr-pop align-center"> -->
<%-- 											<spring:message code="eventsummary.button.view.award" /> --%>
<!-- 										</button> -->
<!-- 									</a> -->
<!-- 								</div> -->
<%-- 							</c:if> --%>


							<c:choose>
								<c:when test="${(!event.viewSupplerName)}">
									<c:if test="${event.status eq 'COMPLETE' and event.disableMasking and (eventPermissions.owner)}">
										<div class="btn-group dropup">
											<button type="button" class="btn btn-warning dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
												<spring:message code="eventsummary.button.conclude" />
												<span class="caret caret-btn"></span>
											</button>
											<ul class="dropdown-menu dd-same" role="menu">

												<!-- <li id="crateNewEventId"><a href="#crateNewEvent" data-toggle="modal">Create Next Event</a></li> -->
												<c:if test="${eventType == 'RFI' or (eventType != 'RFI' and (empty event.awardStatus or (!empty event.awardStatus and event.awardStatus ne 'PENDING' and event.awardStatus ne 'APPROVED') )) }">
													<li id="crateNewEventId"><a><spring:message code="eventsummary.create.next.event.button" /></a></li>
													<li class="divider"></li>
													<li id="concludeWithRemark"><a><spring:message code="eventsummary.button.conclude.remark" /></a></li>
												</c:if>

												<c:if test="${eventType != 'RFI' and event.billOfQuantity and  not empty eventSuppliers}">
													<li class="divider"></li>
													<li id="awardEvent"><a href="${pageContext.request.contextPath}/buyer/${eventType}/eventAward/${eventId}"><spring:message code="eventsummary.href.award.event" /></a></li>
												</c:if>
											</ul>
										</div>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test="${event.status eq 'COMPLETE' and (eventPermissions.owner)}">
										<div class="btn-group dropup">
											<button type="button" class="btn btn-warning dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
												<spring:message code="eventsummary.button.conclude" />
												<span class="caret caret-btn"></span>
											</button>
											<ul class="dropdown-menu dd-same" role="menu">

												<!-- <li id="crateNewEventId"><a href="#crateNewEvent" data-toggle="modal">Create Next Event</a></li> -->
												<c:if test="${eventType == 'RFI' or (eventType != 'RFI' and (empty event.awardStatus or (!empty event.awardStatus and event.awardStatus ne 'PENDING' and event.awardStatus ne 'APPROVED')))}">
													<li id="crateNewEventId"><a><spring:message code="eventsummary.create.next.event.button" /></a></li>
													<li class="divider"></li>
													<li id="concludeWithRemark"><a><spring:message code="eventsummary.button.conclude.remark" /></a></li>
												</c:if>
												<c:if test="${eventType != 'RFI' and event.billOfQuantity and  not empty eventSuppliers}">
													<li class="divider"></li>
													<li id="awardEvent"><a href="${pageContext.request.contextPath}/buyer/${eventType}/eventAward/${eventId}"><spring:message code="eventsummary.href.award.event" /></a></li>
												</c:if>
											</ul>
										</div>
									</c:if>
								</c:otherwise>
							</c:choose>
 
						</div>
					</div>
					<!-- Active Block End-->
				</div>
			</div>
		</div>
	</div>
</div>
<!-- CONCLUDE EVENT -->
<div class="modal fade" id="concludeEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.conclude.event" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<jsp:include page="concludeEventArchive.jsp"></jsp:include>
			<%-- <jsp:include page="eventArchive.jsp"></jsp:include> --%>
		</div>
	</div>
</div>


<div class="modal fade" id="crateNewEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="award.conclude.event" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<jsp:include page="createNewEventArchive.jsp"></jsp:include>
		</div>
	</div>
</div>



<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
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
							<form:textarea path="cancelReason" id="cancelReason" class="width-100" placeholder="${reasonCancel}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no" />
					</button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<!-- SUSPEND EVENT -->
<div class="modal fade" id="confirmSuspendEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.suspension" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal" onclick="javascript:$('#idSuspendForm').get(0).reset();">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="eventsummary.confirm.to.suspend" />
				</label>
			</div>

			<form:form id="idSuspendForm" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/suspendEvent?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
				<form:hidden path="id" />
				<form:hidden path="eventName" />
				<form:hidden path="referanceNumber" />
				<c:if test="${eventType ne 'RFA'}">
					<div class="pad_all_20  radio_yes-no-main width100">
						<div class="pad_all_10 ">
							<div class="radio-info">
								<label class="select-radio"> <form:radiobutton path="suspensionType" cssClass="auction-spt-radio custom-radio" checked="checked" value="KEEP_NOTIFY" /> <spring:message code="eventsummary.radio.keep.notify" />
								</label>
							</div>
						</div>
						<c:if test="${eventType ne 'RFT' || !enableEventUserControle}">
							<div class="pad_all_10 ">
								<div class="radio-info">
									<label class="select-radio"> <form:radiobutton path="suspensionType" cssClass="auction-spt-radio custom-radio" value="KEEP_NO_NOTIFY" /> <spring:message code="eventsummary.radio.keep.no.notify" />
									</label>
								</div>
							</div>
							<div class="pad_all_10 ">
								<div class="radio-info">
									<label class="select-radio"> <form:radiobutton path="suspensionType" cssClass="auction-spt-radio custom-radio" value="DELETE_NOTIFY" /> <spring:message code="eventsummary.radio.delete.notify" />
									</label>
								</div>
							</div>
							<div class="pad_all_10 ">
								<div class="radio-info">
									<label class="select-radio"> <form:radiobutton path="suspensionType" cssClass="auction-spt-radio custom-radio" value="DELETE_NO_NOTIFY" /> <spring:message code="eventsummary.radio.delete.no.notify" />
									</label>
								</div>
							</div>
						</c:if>
						<c:if test="${eventType eq 'RFT' && enableEventUserControle}">
							<div class="pad_all_10 ">
								<c:set var="fileType" value="" />
								<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
									<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
								</c:forEach>

								<div class="row height-100">
									<div class="col-md-12">
										<div class="col-md-12 pad_all_15">
											<div id="appendFileSuspend"></div>


											<button name="addMore" id="addMoreFilesSuspend" class="more-btn btn btn-info ph_btn_small hvr-pop hvr-rectangle-out mr-10">Add Attachment</button>
											<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
											<div class="progressbar flagvisibility" data-value="0">
												<div class="progressbar-value bg-purple">
													<div class="progress-overlay"></div>
													<div class="progress-label">0%</div>
												</div>
											</div>
											<div>
												Note:<br />
												<ul>
													<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
													<li>Allowed file extensions: ${fileType}.</li>
												</ul>
											</div>
										</div>

									</div>
								</div>
							</div>
						</c:if>
					</div>
				</c:if>
				<div class="form-group col-md-6">
					<form:textarea path="suspendRemarks" data-validation="required length" data-validation-length="max500" class="form-control width-100 suspendRemarks" placeholder="Enter Remarks " />
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="suspendEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
						<spring:message code="application.yes" />
					</form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="javascript:$('#idSuspendForm').get(0).reset();">
						<spring:message code="application.no" />
					</button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<!-- team member dialog -->
<div class="flagvisibility dialogBox  ${!empty rfxTemplate and rfxTemplate.readOnlyTeamMember ?'disabled':''  }" id="teamMemberListPopup" title="Event Team Members">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="form_field">
			<div class="form-group ">
				<div class="">
					<div class="col-md-12 marginBottomA">
						<div class="ia-invite-controls-area">
							<div class="group">
								<div class="input-group mrg15T mrg15B">
									<select id="TeamMemberList" class="user-list-normal chosen-select" name="TeamMemberList" selected-id="approver-id" cssClass=" chosen-select">
										<option value=""><spring:message code="prsummary.select.team.member" /></option>
										<c:forEach items="${userTeamMemberList}" var="TeamMember">
											<c:if test="${TeamMember.id != '-1' }">
												<option value="${TeamMember.id}">${TeamMember.name}</option>
											</c:if>
											<c:if test="${TeamMember.id == '-1' }">
												<option value="-1" disabled="true">${TeamMember.name}</option>
											</c:if>
										</c:forEach>
									</select>
									<div class="input-group-btn">
										<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
											<span class="glyphicon glyphicon-eye-open"></span>
										</button>
										<ul class="dropdown-menu dropup">

											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_editor" value="Editor" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.editor" />
											</a></li>
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_viewer" value="Viewer" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.viewer" />
											</a></li>
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_Associate_Owner" value="Associate_Owner" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.associate.owner" />
											</a></li>
										</ul>
										<button class="btn btn-primary addTeamMemberToList" type="button">+</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-sm-12 col-xs-12 margeinAllMDZero">
						<div class="mem-tab">
							<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table">
								<c:forEach items="${event.teamMembers}" var="teamMembers">
									<tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}">
										<td class="width_50_fix ">
											<!--	<img src='<c:url value="/resources/images/profile_setting_image.png"/>' alt="profile " /> -->
										</td>
										<td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span>
										</td>
										<td class="edit-drop">
											<div class="advancee_menu">
												<div class="adm_box">
													<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="glyphicon ${teamMembers.teamMemberType=='Editor'?'glyphicon-pencil':teamMembers.teamMemberType=='Viewer'?'glyphicon-eye-open':teamMembers.teamMemberType=='Associate_Owner'?'fa fa-user-plus':''} " aria-hidden="true "
														title="${teamMembers.teamMemberType=='Editor'?'Editor':teamMembers.teamMemberType=='Viewer'?'Viewer':teamMembers.teamMemberType=='Associate_Owner'?'Associate Owner':''} "></i>
													</a>
													<ul class="dropdown-menu dropup">

														<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <input data-uid="${teamMembers.user.id}" id="${teamMembers.user.id}-Editor" ${teamMembers.teamMemberType=='Editor' ? 'checked' : ''} value="Editor" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.editor" />
														</a></li>
														<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="${teamMembers.user.id}-Viewer" ${teamMembers.teamMemberType=='Viewer'?'checked': ''} value="Viewer" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.viewer" />
														</a></li>
														<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="${teamMembers.user.id}-Associate_Owner" ${teamMembers.teamMemberType=='Associate_Owner'?'checked': ''} value="Associate_Owner" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" /> &nbsp;<spring:message
																	code="eventsummary.checkbox.associate.owner" />
														</a></li>
													</ul>
												</div>
											</div>
										</td>
										<td>
											<div class="cqa_del" data-toggle="dropdown" title='<spring:message code="tooltip.delete" />'>
												<a href="javascript:void(0);" list-type="Team Member" class="removeApproversList"><spring:message code="application.delete" /></a>
											</div>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</div>
					<div class="col-md-12 text-center margeinAllMDZero">
						<a class="closeDialog btn btn-black marg_top_20 hvr-pop ph_btn_small hvr-rectangle-out1" href="javascript:void(0);" onclick="window.location.reload();"><spring:message code="application.button.closed" /> </a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Envelope Evaluation Team -->
<div class="flagvisibility dialogBox" id="envelopListPopup" title="Envelope :">
	<div class="float-left width800 pad_all_15 white-bg">
		<form:form cssClass="form-horizontal" action="${pageContext.request.contextPath}/buyer/${eventType}/updateEvaluators" method="post" modelAttribute="envelop">
			<form:hidden path="id" id="id" />
			<input type="hidden" name="eventId" value="${event.id}" />
			<div class="form-group">
				<label class="col-sm-4 col-md-4 control-label"> <spring:message code="eventsummary.opening.type" />
				</label>
				<div class="col-sm-8">
					<form:select path="envelopType" cssClass="chosen-select hideFields disablesearch" data-validation="required" data-validation-error-msg-required="${required}">
						<form:option value="">
							<spring:message code="rftenvelop.submission.type" />
						</form:option>
						<form:options items="${submissionType}" />
					</form:select>
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-4 col-md-4 control-label"> <spring:message code="label.rftenvelop.evaluation.owner" />
				</label>
				<div class="col-sm-8 col-md-8">
					<form:select path="leadEvaluater" cssClass="chosen-select user-list-normal" data-validation="required">
						<option value=""><spring:message code="eventsummary.select.lead.evaluator" /></option>
						<c:forEach items="${evaluationOwner}" var="usr">
							<c:if test="${usr.id == '-1'}">
								<form:option value="-1" label="${usr.name}" disabled="true" />
							</c:if>
							<c:if test="${usr.id != '-1' }">
								<form:option value="${usr.id}" label="${usr.name}" />
							</c:if>
						</c:forEach>
					</form:select>
				</div>
			</div>
			<div class="form-group ">
				<label class="col-sm-4 col-md-4 control-label"><spring:message code="eventsummary.envelopes.evaluator" /></label>
				<div class="col-sm-8 col-md-8">
					<div class="width100 pull-left evelAtorAllPlusBlock">
						<div class="col-md-10 col-sm-10 pad0">
							<select id="evaluatorList" class="chosen-select user-list-normal" selected-id="evaluator-id" cssClass="form-control chosen-select" name="userList1">
								<option value=""><spring:message code="eventsummary.select.evaluator" /></option>
							</select>
							<div class="form-error font-red  hide" id="evaluator-err">
								<spring:message code="eventsummary.select.evaluator" />
							</div>
						</div>
						<span class="col-md-2 col-sm-2 pad0 text-right">
							<button id="addButtonForEve" class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out addEvaluatorToList" title="" data-placement="top" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.add.evaluator" />' list-type="Evaluator">
								<i class="fa fa-plus" aria-hidden="true"></i>
							</button>
						</span>
					</div>
					<div class="width100 pull-left" style="margin-top: 15px;">
						<div class="ph_table_border">
							<div class="usersListTable marginDisable">
								<div class="row blankEvelator" id="">
									<div class="col-md-12">
										<p>
											<spring:message code="envelope.add.evaluator.placeholder" />
										</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group openerDiv" id="idEnvOpeners">
				<label class="col-sm-4 col-md-4 control-label"><spring:message code="eventsummary.envelope.opener" /></label>
				<div class="col-sm-8 col-md-8">
					<spring:message code="rfienvelope.select.opener" var="evalOpenerPlaceholder" />
					<form:select path="openerUsers" cssClass="chosen-select user-list-normal" data-validation="required" multiple="multiple" id="openersList" data-placeholder="${evalOpenerPlaceholder}">
						<option value=""><spring:message code="rfienvelope.select.opener" /></option>
						<c:forEach items="${openers}" var="usr">
							<c:if test="${usr.id == '-1'}">
								<form:option value="-1" label="${usr.name}" disabled="true" />
							</c:if>
							<c:if test="${usr.id != '-1' }">
								<form:option value="${usr.id}" label="${usr.name}" />
							</c:if>
						</c:forEach>

					</form:select>
				</div>
			</div>
			<div class="form-group EnveloepButtonsInpopup">
				<div class="col-md-12">
					<div class="align-center">
						<!-- <button type="reset" id="resetEventContctForm" style="display: none;"></button> -->
						<button type="submit" id="buttonUpdate" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" id="buttonCancel" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1 val-rm">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<!-- Delete document dialog -->
<div class="modal fade" id="confirmDeleteDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="application.confirm.message.delete" />
				</label> <input type="hidden" id="deleteIdDocument" /> <input type="hidden" id="deleteRefIdDocument" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelDocument">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<!--Approver  popup-->
<div class="flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListType" name="approverListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="prtemplate.sure.want.remove" />
				<span></span>
				<spring:message code="application.from" />
				<span></span>
				<spring:message code="application.list" />
				?
			</p>
		</div>
		<div class="event_form">
			<input id="id" type="hidden" value="${event.id}">
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeApproverListPerson" data-original-title='<spring:message code="tooltip.delete" />'><spring:message code="application.delete" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<!--  EDIT APPROVAL POPUP -->
<div class="flagvisibility dialogBox" id="editApprovalPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<c:url value="/buyer/${eventType}/updateEventApproval" var="updateEventApproval" />
		<form:form id="eventSummaryForm" action="${updateEventApproval}" method="post" modelAttribute="event">
			<form:hidden path="id" />
			<jsp:include page="eventApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center marg-top-20">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<button type="submit" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>

<!--  EDIT SUSPENSION APPROVAL POPUP -->
<div class="flagvisibility dialogBox" id="editSuspApprvlPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<c:url value="/buyer/${eventType}/updateEventSuspApproval" var="updateSuspEventApproval" />
		<form:form id="eventSummaryForm" action="${updateSuspEventApproval}" method="post" modelAttribute="event">
			<form:hidden path="id" />
			<jsp:include page="eventSuspendApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center marg-top-20">
						<button type="submit" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>

<div class="flagvisibility dialogBox" id="editEnvelopPopup" title="Edit Envelop">
	<div class="float-left width100 pad_all_15 white-bg">
		<form:form id="eventEnvelopForm" method="post" modelAttribute="event">
			<form:hidden path="id" />
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<button type="submit" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<c:if test="${eventType eq 'RFA'}">
	<div class="flagvisibility dialogBox" id="resumeEventPopup" title="Resume Event" style="width: 200px !important;">
		<div class="float-left width100 pad_all_15 white-bg">
			<form:form method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/finishEvent">
				<form:hidden path="id" />
				<form:hidden path="eventName" />
				<div class="width100 form-group">
					<div class="col-md-6 col-sm-6 col-xs-6">
						<form:input id="auctionResumeDateTime" path="auctionResumeDateTime" class="bootstrap-datepicker form-control for-clander-view" data-validation="required" data-date-format="mm/dd/yy" />
					</div>
					<div class="col-md-6 col-sm-6 col-xs-6">
						<form:input id="auctionResumeTime" path="auctionResumeTime" class="timepicker-example for-timepicker-view form-control" data-validation="required" />
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group align-center">
							<button type="submit" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
								<spring:message code="application.update" />
							</button>
							<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
								<spring:message code="application.cancel" />
							</button>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
</c:if>

	<div class="flagvisibility dialogBox" id="resumeAllEventPopup" title="Suspend Approval Confirmation " style="width: 200px !important;">
		<div class="float-left width100 pad_all_15 white-bg">
<%-- 			<form:form method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/finishEvent"> --%>
<%-- 				<form:hidden path="id" /> --%>
				<div class="width100 form-group modal-body" style="font-size: 14px;">
					<spring:message code="suspension.approval.dialoque" />
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
								<button type="button" class="btn btn-info btn-tooltip hvr-pop pull-left hvr-rectangle-out ph_btn_small" id="resumeConfirm">
									Yes
								</button>
							<button type="button" class="closeDialog btn btn-black marg-left-10 pull-right hvr-pop hvr-rectangle-out1 ph_btn_small btn-default">
								No
							</button>
						</div>
					</div>
				</div>
<%-- 			</form:form> --%>
		</div>
	</div>

<!--   --------------------Resume Rfa Event----------------  -->
<div class="flagvisibility dialogBox" id="rfaResumeEventPopup" title="Suspend Approval Confirmation " style="width: 200px !important;">
		<div class="float-left width100 pad_all_15 white-bg">
<%-- 			<form:form method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/finishEvent"> --%>
<%-- 				<form:hidden path="id" /> --%>
<%-- 				<form:hidden path="eventName" /> --%>
				<div class="width100 form-group modal-body">
					<spring:message code="suspension.approval.dialoque" />
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<button type="button" class="btn btn-info btn-tooltip hvr-pop pull-left hvr-rectangle-out ph_btn_small" id="rfaResumeConfirm">
								Yes
							</button>
							<button type="button" class="closeDialog btn btn-black marg-left-10 pull-right hvr-pop hvr-rectangle-out1 ph_btn_small btn-default">
								No
							</button>
						</div>
					</div>
				</div>
<%-- 			</form:form> --%>
		</div>
	</div>

<!--   --------------------Cancel Meeting PopUp----------------  -->
<div class="flagvisibility dialogBox" id="cancelPopUp" title="Confirm Cancel Meeting">
	<div class="float-left width100 pad_all_15 white-bg">
		<form method="post" id="cancelMeetingForm">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="meetingId" id="meetingId" value="">
			<div class="row">
				<label class="col-sm-4 control-label"><spring:message code="eventsummary.cancel.reason" /></label>
				<div class="col-sm-6 col-md-6">
					<div class="form-group">
						<textarea placeholder='<spring:message  code="event.reason.cancellation.placeholder" />' rows="4" name="cancelMeetingReason" id="cancelMeetingReason" data-validation="required length" data-validation-length="max250" class="form-control"></textarea>
						<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<spring:url value="${pageContext.request.contextPath}/buyer/${eventType}/createMeeting" var="createUrl" htmlEscape="true" />
						<button type="submit" id="cancelMeeting" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out cancelOkMeeting">
							<spring:message code="eventsummary.ok" />
						</button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<div class="flagvisibility dialogBox" id="RevertBidPopup" title="Confirm Revert Bid">
	<div class="float-left width100 pad_all_15 white-bg">

		<div class="row">
			<div class="col-md-12">
				<label class="col-sm-4 control-label " style="width: 100%; font-size: 14px;"> <spring:message code="eventsummary.provide.reason" /> <b id="price"></b>
				</label>
			</div>
		</div>
		<div class="row">
			<input type="hidden" id="bidId" name="bidId">
			<div class="col-sm-12 col-md-12">
				<div class="form-group ">
					<textarea placeholder='<spring:message	code="event.reason.reverting.placeholder" />' rows="4" name="revertReason" id="revertReason" data-validation="required length" data-validation-length="max250" class="form-control"></textarea>
					<div class="check">
						<span class="sky-blue"><spring:message code="application.required" /></span>
					</div>
					<div class="check2">
						<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-group d-flex">
					<button type="submit" id="revertToBidReason" style="text-align: center;" class="btnrevert btn btn-info hvr-pop hvr-rectangle-out btn-tooltip cancelOkMeeting">
						<spring:message code="application.yes" />
					</button>
					<button type="button" style="margin-right: 0%;" class="btncancel btnrevert closeDialog btn btn-black hvr-pop hvr-rectangle-out1 cancel">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="ConfirmDiscardAward" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="discard.Award.confirmation" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
				<div class="row">
					<div class="modal-body col-md-12">
						<label> <spring:message code="discard.award.dialog.text" />
						</label>
					</div>
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" id="saveConfirm" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
					<spring:message code="application.yes" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="ConfirmDiscardAwardWithRemark" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="discard.Award.confirmation" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
				<div class="row">
					<div class="modal-body col-md-12">
						<label> <spring:message code="discard.award.dialog.text" />
						</label>
					</div>
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" id="saveConfirmDiscard" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
					<spring:message code="application.yes" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no" />
				</button>
			</div>
		</div>
	</div>
</div>

<style>
.approverInfoBlock {
	margin-top: 50px;
}

.ui-widget-content {
	margin-top: 5px;
}

.ui-draggable .ui-dialog-titlebar {
	z-index: 9;
}

.d-flex {
	display: flex;
	justify-content: center;
}

.btnrevert {
	/* 	height: 48px; */
	min-width: 120px;
	margin-right: 7%;
	/* 	font-size: 16px; */
	/* 	line-height: 48px; */
}

.btncancel {
	/*	margin-left: 100px;*/
	
}

.s1_question_list.bqcqListsEnvlop>li {
	cursor: default;
	margin-bottom: 2px;
	padding: 5px;
}

.modal-dialog.for-delete-all.reminder {
	width: 590px;
	!
	important;
}

div.radio[id^='uniform-']>span {
	margin-top: 0 !important;
}

label.select-radio {
	width: auto;
}

#event {
	padding-left: 0;
}

.input-group.mrg15T.mrg15B {
	background-color: #f5f5f5;
	margin-bottom: 0;
	padding: 0;
}

.margeinAllMDZero {
	margin: 0;
	clear: both;
}

.marginBottomA {
	margin-bottom: 20px;
}

#appUsersList td {
	padding: 5px;
}

.mem-tab {
	border: 1px solid #ddd;
	border-radius: 2px;
	float: left;
	height: 300px;
	overflow: auto;
	position: relative;
	width: 100%;
}

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

.caret {
	color: #fff !important;
}

.cq_row_parent .input-group-btn {
	width: 200px;
}

.dropdown-menu input {
	display: inline !important;
	width: auto !important;
}

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dropdown-menu .checkbox input[type="checkbox"] {
	position: relative !important;
	margin-left: 0 !important;
}

.open>.dropdown-menu {
	padding-bottom: 17px;
	padding-top: 0;
}

#appUsersList tr:nth-child(odd) {
	background: #FFF;
}

#appUsersList tr:nth-child(even) {
	background: #f5f5f5;
}

.caret-btn {
	float: right;
	top: 16px;
	position: relative;
}

/* #eventTeamMembersList td, #eventTeamMembersList th {
	text-align: left !important;
	max-width: 100px !important;
}

#eventTeamMembersList {
	margin: 0 !important;
}

#eventTeamMembersList_length, #eventTeamMembersList_info,
	#eventTeamMembersList_paginate {
	display: none;
}
 */
.editTeamMemberList {
	margin-left: 50px;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting_asc::after {
	content: "" !important;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting::after {
	content: "" !important
}

.bootstrap-timepicker-widget, #confirmDeleteContect {
	z-index: 9999;
}

#meetingListPopup.modal-content {
	width: 840px;
	height: 600px;
	overflow-y: scroll
}

#concludeWithRemark .for-delete-all.reminder {
	width: 90%;
	max-width: 600px;
}

.inactiveCaption {
	margin: 0 0px 0 0px !important;
	font-weight: bold !important;
	color: #ff1d33 !important;
}

.dd-same .divider {
	margin: 0 !important;
}

.dd-same #crateNewEventId {
	margin-top: 0;
}

.dd-same {
	border: 1px solid #e5e5e5;
	padding-bottom: 0 !important;
}

.dd-same li a {
	padding: 7px 20px;
	font-weight: 600;
}
.editSuspBtn {
	left: 190px;
    top: 17px;
}

.exSixbtn {
    left: 170px;
    top: 17px;
}

#editSuspApprvlPopup .open > .dropdown-menu{ position: absolute;
    min-width: 64px!important;
    width: 80px !important;
    height: 60px;}
	
	.ia-invite-controls-area .dropdown-menu{
		 position:absolute; min-width: 100px!important;
	}
	
	#appUsersList .dropdown-menu{ min-width: 100px!important;}
	
	
	.ia-invite-controls-area .dropdown-menu a, #editSuspApprvlPopup .open > .dropdown-menu a{
    	line-height: 19px;}
    
    #editSuspApprvlPopup .open > .dropdown-menu{ min-width: 100px!important;}
	
</style>
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
<script>
	
	<c:if test="${event.status == 'SUSPENDED' and !(eventPermissions.owner or eventPermissions.editor)}">
	$(window).bind('load',function() {
		var allowedFields = '#dashboard,#priviousStep,.accordion,#approvedButton,#rejectedButton,#remarks,#idOngoingProgTab,#idOngoingSumTab,#idOngoingEvalTab,#idSumDownload,.editEnvelopList,#evaluatorList,#addButtonForEve,#buttonCancel,#buttonUpdate,.bluelink,#prevMail,#nextMail,#reloadMsg, #suspApprovedButton, #suspRejectedButton ,#idOngoingAwardTab,#idPreviousRequest';
		<c:if test="${eventPermissions.revertBidUser}">
		allowedFields += ',.yehSupplierHai,.refreshAuctionBids,#revertToBidReason,.btnrevert.#revertReason';
		</c:if>
		disableFormFields(allowedFields);
	});
	</c:if>

	
	<c:if test="${!eventPermissions.owner and eventPermissions.viewer or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#dashboard,#priviousStep,.accordion,#approvedButton,#rejectedButton,#remarks,#idOngoingProgTab,#idOngoingSumTab,#idOngoingEvalTab,#idOngoingMessageTab,#idSumDownload,.editEnvelopList,#evaluatorList,#addButtonForEve,#buttonCancel,#buttonUpdate,.bluelink, #suspApprovedButton, #suspRejectedButton, #idOngoingAwardTab,#idPreviousRequest';
		
		<c:if test="${eventType eq 'RFA' and eventPermissions.viewer and ((event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall)}">
			allowedFields +=',#idDutchConsole,#idEnglishConsole,.auctionBidSupplier,.downloadBuyerAuctionReport';
		</c:if>
		disableFormFields(allowedFields);
		
	});
	</c:if>
	
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or subscriptionExpired or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		console.log("allowedFields >>>  222");
		var allowedFields = '#idDutchConsole,#idEnglishConsole,.auctionBidSupplier,#dashboard,#priviousStep,.accordion,#idSumDownload,.bluelink,.downloadSnapshot,.navigation, #reloadMsg,#bubble,#idOngoingProgTab,#idOngoingSumTab,#idOngoingEvalTab,#idOngoingMessageTab,.evaluationSummaryReport,.downloadBuyerAuctionReport,#approvedButton,#rejectedButton,#remarks,#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton, #suspApprovedButton, #suspRejectedButton, #idOngoingAwardTab,#idPreviousRequest';
		<c:if test="${eventPermissions.leadEvaluator and !subscriptionExpired}">
		allowedFields += ',#evaluatorList,.editEnvelopList,#addButtonForEve,#buttonCancel,#buttonUpdate';
		</c:if>	
		<c:if test="${eventType eq 'RFA' and (eventPermissions.viewer or buyerReadOnlyAdmin) and !subscriptionExpired and ((event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall)}">
			allowedFields +=',#idDutchConsole,#idEnglishConsole,.auctionBidSupplier,.downloadBuyerAuctionReport';
		</c:if>
		disableFormFields(allowedFields);
		 $('#page-content').find('#idMeetingDownload').removeClass('disabled');
	});
	</c:if>
	<c:if test="${eventPermissions.owner or isAdmin or eventPermissions.conclusionUser or eventPermissions.leadEvaluator or buyerReadOnlyAdmin}">
		$(window).bind('load', function() {
			$('#page-content').find('#idEvalConclusionDownload').removeClass('disabled');
		});
	</c:if>
	
	
	$(document).ready(function() {
		
		
		$('#finishEvent').click(function() {
			$(this).addClass('disabled');
		});

		$('#suspendEvent').click(function() {
			var suspendRemarks = $('.suspendRemarks').val();
			if (suspendRemarks != '') {
			$(this).addClass('disabled');
			}
		});


		$('#rfxCancelEvent').click(function() {
			var cancelRequest = $('#cancelReason').val();
			if (cancelRequest != '') {
				$(this).addClass('disabled');
			}
		});
		
		$('#crateNewEventId').click(function (event) {
			checkBusinessUnit("");
			
			var awardStatus = '${eventType != "RFI" ? event.awardStatus : ""}'
			
			if(awardStatus == 'DRAFT') {
				$('#ConfirmDiscardAward').modal('show')
			} else {
				$('#AwRemk').val('');
		    	$('#crateNewEvent').modal('show')
			}
		});
		 
		$('#saveConfirm').click(function (event) {
			$('#ConfirmDiscardAward').modal('hide')
			checkBusinessUnit("");
		   	$('#crateNewEvent').modal('show')
		});
		 
		 $('#concludeWithRemark').click(function (event) {
			 checkBusinessUnit("");
			 var awardStatus = '${eventType != "RFI" ? event.awardStatus : ""}'
			 if(awardStatus == 'DRAFT') {
			     $('#ConfirmDiscardAwardWithRemark').modal('show')
			 } else {
		        $('#concludeEvent').modal('show')
			 }
		 });
		 
		 $('#saveConfirmDiscard').click(function (event) {
			 $('#ConfirmDiscardAwardWithRemark').modal('hide')
			 checkBusinessUnit("");
		        $('#concludeEvent').modal('show')
		    });
		
		$('#approvedButton').click(function(e) {
			e.preventDefault();
			$(this).addClass('disabled');
			$('#approvedRejectForm').attr('action', getBuyerContextPath("approve"));
			$('#approvedRejectForm').submit();
		});

		$('#rejectedButton').click(function(e) {
			e.preventDefault();
			$(this).addClass('disabled');
			$('#approvedRejectForm').attr('action', getBuyerContextPath("reject"));
			$('#approvedRejectForm').submit();

		});
		
		
		$('#suspApprovedButton').click(function(e) {
			e.preventDefault();
			$(this).addClass('disabled');
			$('#suspApprovedRejectForm').attr('action', getBuyerContextPath("approveSuspension"));
			$('#suspApprovedRejectForm').submit();
		});

		$('#suspRejectedButton').click(function(e) {
			e.preventDefault();
			$(this).addClass('disabled');
			$('#suspApprovedRejectForm').attr('action', getBuyerContextPath("rejectSuspension"));
			$('#suspApprovedRejectForm').submit();

		});
	});

	$(document).delegate('.editTeamMemberList', 'click', function(e) {
		console.log("************");
		$("#teamMemberListPopup").dialog({
			modal : true,
			minWidth : 300,
			width : '90%',
			maxWidth : 600,
			minHeight : 200,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
	});

	
	$('document').ready(function() {
		var i=1;	
		//alert("eventTeamMembersList :"+${event.id});
		var eventTemMembersTable = $('#eventTeamMembersList').DataTable({
			"processing" : true,
			"serverSide" : true,
			"paging" : false,
			"info" : false,
			"ordering" : false,
			"searching" : false,
			"ajax" : {
				"url" : getContextPath() + '/buyer/${eventType}/eventTeamMembersList/${event.id}'
 			},
			"columns" : [ {
				"data" : "user.name",
				"className" : "align-left",
				"defaultContent" : "Demo Test"
			}, {
				"data" : "user.loginId",
				"className" : "align-left",
				"defaultContent" : "Demo 2"
			}, {
				"data" : "teamMemberType",
				"className" : "align-left",
				"defaultContent" : ""
			} ]
		});
		
		
		
$('#addMoreFilesSuspend').on('click',function (e){
			
			e.preventDefault();
			i++;
			var html='<div class="row hideThis">'
			html+='<div class="col-md-6 mt-10">'
			html+='<spring:message code="event.doc.file.descrequired" var="descrequired" />'
			html+='<spring:message code="event.doc.file.maxlimit" var="maxlimit" />'
			html+='<spring:message code="event.document.filedesc" var="filedesc" />'
			html+='<label>File Description</label> <input class="form-control mb-10" name="docDescription" id="docDescription" data-validation=" required length" data-validation-length="max250" type="text" placeholder="${filedesc} ${maxlimit}">'
			html+='</div>'
			html+='<div class="col-md-6 mt-10">'
			html+='<label>Select File</label>'
			html+='<div class="pos-rel">'
			html+='<div class="fileinput fileinput-new input-group  w-88" data-provides="fileinput">'
			html+='<spring:message code="meeting.doc.file.length" var="filelength" />'
			html+='<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />'
			html+='<div data-trigger="fileinput" class="form-control">'
			html+='<span class="fileinput-filename show_name"></span>'
			html+='	</div>'
			html+='<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="application.selectfile" />'
			html+='</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />'
			html+='</span> <input name="docs" id="docs" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog'+i+ '"data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" data-validation=" required extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">'
			html+='</span>'
			html+='</div>'
			html+='<div id="Load_File-error-dialog'+i +'"style="width: 100%; float: left; margin: 0 0 0 0;"></div>'
			html+='<button class="close select-abs closePopUp" type="button">&times;</button>'
			html+='</div>'
			html+='</div>'
			html+='</div>'
			
			$('#appendFileSuspend').append(html);
		});
		
        $(document).delegate('.closePopUp', 'click', function(){
          	 $(this).closest("div.hideThis").hide();
        });	
		
	});

	
	$(document).delegate('#ResumeEvent', 'click', function(e) {
		e.preventDefault();
		$("#resumeEventPopup").dialog({
			modal : true,
			minWidth : 200,
			width : '60%',
			maxWidth : 300,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
	});
	
	$(document).delegate('#resumeAllEvent', 'click', function(e) {
		e.preventDefault();
		$("#resumeAllEventPopup").dialog({
			modal : true,
			minWidth : 200,
			width : '30%',
			maxWidth : 300,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
	});
	
	$(document).delegate('#resumeConfirm', 'click', function(e) {
		$('#formResumeEvent').submit();
	});
	
	
	
	$(document).delegate('#rfaResumeEvent', 'click', function(e) {
		e.preventDefault();
		$("#rfaResumeEventPopup").dialog({
			modal : true,
			minWidth : 200,
			width : '30%',
			maxWidth : 300,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
	});
	
	$(document).delegate('#rfaResumeConfirm', 'click', function(e) {
		e.preventDefault();
// 		$('#formResumeEvent').submit();

		$("#rfaResumeEventPopup").remove();
		
		$("#resumeEventPopup").dialog({
			modal : true,
			minWidth : 200,
			width : '60%',
			maxWidth : 300,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
	});
	
	
	//hide opener field
	$(document).ready(function() {
		$('.hideFields').change(function() {
			var type = $('.hideFields').val();
			if (type === 'OPEN') {
				$('.openerDiv').hide();
 			} else {
				$('.openerDiv').show();
			}
		});
		
	});
	
	
	// Fetch envelope data
	$('.editEnvelopList').click(function(e) {
		e.preventDefault();
		
		var loggedInUserId = '${loggedInUserId}';
		var owner = ${eventPermissions.owner};
		var currentBlock = $(this);
		console.log(currentBlock.data('id'));
		var envelopeId = currentBlock.data('id');
		$('#envelopListPopup input[name="id"]').val(envelopeId);
		//alert("editEnvelopList....." + envelopeId);
		console.log('envelope ID : ' + envelopeId);
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$.ajax({
			type : "POST",
			url : getBuyerContextPath('summaryEnvelop'),
			data : {
				'envelopeId' : envelopeId
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success : function(data) {
				console.log("Start : " + new Date());
				var isLeadEvaluator = false;
				var isEvaluator = false;
				var isOpener = false;		
				
				if(data.leadEvaluater != undefined || data.leadEvaluater != null || data.envelopType != null) {
					// adding selected user if not in list
					var addSelectedUser = true;
					$("#envelopListPopup #leadEvaluater option").each(function(i, item){
						if(data.leadEvaluater.id == $(this).val() ){
							addSelectedUser = false;
						}
					});
					
					if(addSelectedUser){
						$('#envelopListPopup #leadEvaluater').append('<option value="'+data.leadEvaluater.id+'">' + data.leadEvaluater.name + '</option>');
					}
					
					$('#envelopListPopup #leadEvaluater').val(data.leadEvaluater.id).trigger("chosen:updated");
					$('#envelopListPopup #envelopType').val(data.envelopType).trigger("chosen:updated");
					$("#leadEve").val(data.leadEvaluater.id);	
					if(loggedInUserId == data.leadEvaluater.id) {
						isLeadEvaluator = true;
					}
					
					updateUserList('', $("#leadEvaluater"), 'NORMAL_USER'); 
				}
				/* if (data.opener != undefined || data.opener != null) { 
					// adding selected user if not in list
					var addOpenSelectedUser = true;
					$("#envelopListPopup #opener option").each(function(i, item){
						if(data.opener.id == $(this).val() ){
							addOpenSelectedUser = false;
						}
					});
					
					if(addOpenSelectedUser){
						$('#envelopListPopup #opener').append('<option value="'+data.opener.id+'">' + data.opener.name + '</option>');
					}
					
					$('#envelopListPopup #opener').val(data.opener.id).trigger("chosen:updated");
					$("#EnveOpener").val(data.opener.id);					
					if(loggedInUserId == data.opener.id) {
						isOpener = true;
					}
					
					updateUserList('', $("#opener"), 'NORMAL_USER'); 
				}	 */
				if(data.envelopType == 'OPEN'){
					$("#idEnvOpeners").css("display","none");
				}				
				
				console.log("Start Adding Evaluator: " + new Date());
				var html = '<option value="">Select Evaluator</option>';
				$.each(data.evaluators, function(i, evaluator) {
					if(evaluator.id != '-1'){
						html += '<option value="'+evaluator.id+'">' + evaluator.name + '</option>';
					}else{
						html += '<option value="-1" disabled>' + evaluator.name + '</option>';
					}
				});
				console.log("End Adding Evaluator: " + new Date());
				$('#envelopListPopup #evaluatorList').html(html).trigger("chosen:updated");
				$('.usersListTable.marginDisable .row').not('.blankEvelator').remove();
				console.log("Start Adding Assigned Evaluator: " + new Date());
				$.each(data.assignedEvaluators, function(i, asevaluator) {
					$('.usersListTable.marginDisable .blankEvelator').remove();
					var dataHtml = '<div class="row" evaluator-id="'+asevaluator.user.id+'">';
					dataHtml += '<input type="hidden" name="evaluators['+i+'].user.id" value="'+asevaluator.user.id+'">';
					dataHtml += '<input type="hidden" name="evaluators['+i+'].id" value="'+asevaluator.id+'">';
					dataHtml += '<div class="col-md-10"><p>' + asevaluator.user.name + '</p></div>';
					dataHtml += '<div class="col-md-2"><a href="" class="removeEvaluatorsList"><i class="fa fa-times-circle"></i></a></div></div>';
					$('.usersListTable.marginDisable').append(dataHtml);
					$('#envelopListPopup #evaluatorList option[value="' + asevaluator.user.id + '"]').remove();
					$('#envelopListPopup #evaluatorList').trigger("chosen:updated");					
					if(loggedInUserId == asevaluator.user.id && !owner) {
						isEvaluator = true;
					}
				});
				console.log("End Adding Assigned Evaluator: " + new Date());
				
				var htmlop = '';
				$.each(data.openers, function(i, opener) {
					if(opener.id != '-1'){
						htmlop += '<option value="'+opener.id+'">' + opener.name + '</option>';
					}else{
						htmlop += '<option value="-1" disabled>' + opener.name + '</option>';
					}
				});
				/* if (data.opener != undefined || data.opener != null) { 
					htmlop += '<option value="'+data.opener.id+'">' + data.opener.name + '</option>';	
				} */
				$('#envelopListPopup #openersList').html(htmlop).trigger("chosen:updated");
				
				//Assigned Openers
				var openerUserList=[];
				$.each(data.assignedOpeners, function(i, opener) {
					openerUserList.push(opener.user.id);
				});
				
				/* if (data.opener != undefined || data.opener != null) { 
					openerUserList.push(data.opener.id);
				} */
				
				  $('#envelopListPopup #openersList').val(openerUserList);
				  $('#envelopListPopup #openersList').trigger("chosen:updated");
				   
				$("#envelopListPopup").dialog({
					modal : true,
					minWidth : 300,
					width : '90%',
					maxWidth : 600,
					minHeight : 200,
					dialogClass : "",
					show : "fadeIn",
					draggable : true,
					resizable : false,
					dialogClass : "dialogBlockLoaded"
				});
				$('.ui-dialog-title').text('Envelope : '+data.envelopTitle);
				if(owner) {
					$('#envelopListPopup #leadEvaluater').css('opacity', '0').siblings('div.chosen-container').removeClass('disabled');
					//$('#envelopListPopup #opener').css('opacity', '0').siblings('div.chosen-container').removeClass('disabled');
				} else {
					$('#envelopListPopup #leadEvaluater').css('opacity', '0').siblings('div.chosen-container').addClass('disabled');
					//$('#envelopListPopup #opener').css('opacity', '0').siblings('div.chosen-container').addClass('disabled');
				}
				if(isLeadEvaluator) {
					$('#envelopListPopup #evaluatorList').removeAttr('disabled', 'disabled'); // select for evaluators
					$('#envelopListPopup .addEvaluatorToList').removeAttr('readonly', 'readonly').removeClass('disabled'); // Add Button
					$('#envelopListPopup .removeEvaluatorsList').removeClass('disabled'); // Remove button
				} else {
					$('#envelopListPopup #evaluatorList').attr('disabled', 'disabled').css('opacity', '0'); // select for evaluators
					$('#envelopListPopup .addEvaluatorToList').attr('readonly', 'readonly').addClass('disabled'); // Add Button
					$('#envelopListPopup .removeEvaluatorsList').addClass('disabled'); // Remove button
				}
				$('#envelopListPopup #evaluatorList').trigger("chosen:updated");
				if(data.evaluationStatus=='COMPLETE'){
					$('#envelopListPopup #openersList').addClass('disabled');
				}
				else if(owner){
					$('#envelopListPopup #openersList').removeClass('disabled'); 
				}
				else{
					$('#envelopListPopup #openersList').addClass('disabled'); 
				}
				$('#envelopListPopup #openersList').trigger("chosen:updated");
				console.log("End : " + new Date());
				
				
				
			},
			error : function(request, textStatus, errorThrown) {
				console.log("error");
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});

		$(document).delegate('.addEvaluatorToList', 'click', function(e) {
			e.preventDefault();
			if ($('#envelopListPopup #evaluatorList').val() != '') {
				var evaluator = $('#envelopListPopup #evaluatorList').val();
				var isExisit = false;
				$('.usersListTable.marginDisable .row').not('.blankEvelator').each(function(i) {
					var existingId =$(this).find('[type="hidden"][name*=".user.id"]').attr('name', 'evaluators[' + i + '].user.id').val();
					console.log(">>>  " + existingId);
					if(existingId === evaluator){
						isExisit = true;
						return;
					}
				});
				if(!isExisit){
				var evaluatorName = $('#envelopListPopup #evaluatorList option[value="' + evaluator + '"]').text();
				$('.usersListTable.marginDisable .blankEvelator').remove();
				var eIndex = $('.usersListTable.marginDisable .row').length;
				var dataHtml = '<div class="row" evaluator-id="'+evaluator+'">';
				dataHtml += '<input type="hidden" name="evaluators['+eIndex+'].user.id" value="'+evaluator+'">';
				dataHtml += '<input type="hidden" name="evaluators['+eIndex+'].id" value="">';
				dataHtml += '<div class="col-md-10"><p>' + evaluatorName + '</p></div>';
				dataHtml += '<div class="col-md-2"><a href="" class="removeEvaluatorsList"><i class="fa fa-times-circle"></i></a></div></div>';
				$('.usersListTable.marginDisable').append(dataHtml);
				$('#envelopListPopup #evaluatorList option[value="' + evaluator + '"]').remove();
				 updateUserList('', $("#evaluatorList"), 'NORMAL_USER'); 
				$('#envelopListPopup #evaluatorList').trigger("chosen:updated");
				}
			}
		});

		$(document).delegate('.removeEvaluatorsList', 'click', function(e) {
			e.preventDefault();
			var evaluator = $(this).closest('.row').attr('evaluator-id');
			var evaluatorName = $(this).closest('.row').find('.col-md-10 p').text();
			var dataHtml = '<option value="'+evaluator+'">' + evaluatorName + '</option>';
			$(this).closest('.row').remove();
			if ($('.usersListTable.marginDisable .row').length == 0) {
				var dataHtml = '<div class="row blankEvelator">';
				dataHtml += '<div class="col-md-12"><p>Add Evaluator</p></div></div>';
				$('.usersListTable.marginDisable').append(dataHtml);
			}
			$('.usersListTable.marginDisable .row').not('.blankEvelator').each(function(i) {
				$(this).find('[type="hidden"][name*=".user.id"]').attr('name', 'evaluators[' + i + '].user.id');
				$(this).find('[type="hidden"][name*="].id"]').attr('name', 'evaluators[' + i + '].id');
			});
			$('#envelopListPopup #evaluatorList').append(dataHtml).trigger("chosen:updated");
			 updateUserList('', $("#evaluatorList"), 'NORMAL_USER'); 
		});

	});
	
	
	//for the auction bid history on change of supplier

	function fetchSupplierAuctionBids() {
		$('.auctionbids > table > tbody ').html('');
		var currencyCode = '${event.baseCurrency.currencyCode}';
		var supplierId = $('.auctionBidSupplier').val();
		var eventId = $('.eventIdInBidHis').val();
		var decimal = '${event.decimal}';
		if (supplierId == '') {
			return;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/buyer/RFA/getAuctionBidsOfSuppliersInSummary/' + eventId + '/' + supplierId;
		console.log(ajaxUrl);
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				var html = '';
				console.log(data);
				$.each(data, function(i, item) {
					console.log(item);
					html += '<tr>';
					if (item.rankForBid === undefined || item.rankForBid === '') {
						html += '<td>&nbsp</td>';
					} else {
						html += '<td>' + item.rankForBid + '</td>';
					}
					html += '<td class="align-right">' + currencyCode + ' ' + ReplaceNumberWithCommas(item.amount.toFixed(decimal)) + '</td><td class="align-right">' + item.bidSubmissionDate + '</td>';
					<c:if test="${event.status eq 'SUSPENDED' and eventPermissions.revertBidUser}">
					html += '<td class="align-right"><input class="revertBid btn btn-alt btn-hover btn-warning hvr-pop" type="submit" data-id='+ item.id +' data-price='+item.amount+'  value="Revert to this bid"></td><tr>';
					</c:if>
				});
				console.log(html);
				$('.auctionbids > table > tbody ').html(html);
			},
			error : function(request, textStatus, errorThrown) {
				console.log(request.responseText);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	}
	
	$('.refreshAuctionBids').click(function() {
		fetchSupplierAuctionBids();
	});

	$('.auctionBidSupplier').change(function() {
		fetchSupplierAuctionBids();
	});

	$(".val-rm").click(function(){
		console.log("Remove error Messages");
		 $('.form-error').hide();
		 $('.error').removeClass("error");
	});
	
	jQuery(document).ready(function() {
		selectSupplier();
		// for hide div on select on timeExtensionType
		$('.selectSupp').on('change', function() {
			selectSupplier();
		});
	});

	function selectSupplier() {
		if ($('.selectSupp').val() == '') {
			$(".hideDiv").hide();
			$("#bidsThead").hide();
			$(".padDiv").show();
		} else {
			$(".hideDiv").show();
			$("#bidsThead").show();
			$(".padDiv").hide();
		}
	}
	

	$(document).delegate('.revertBid', 'click', function(e) {
		e.preventDefault();
		$('#price').text($(this).attr('data-price'));
		$('#bidId').val($(this).attr('data-id'));	
		$("#RevertBidPopup").dialog({
			modal : true,
			width : '30%',
			maxWidth : 300,
			minHeight : 100,
			dialogClass : "",
		});
		 $(".check").hide(); 
		 $(".check2").hide(); 
		 $("#revertReason").val("");
	});
	
	$(document).delegate('#revertToBidReason', 'click', function(e) {
		var supplierId = $('.auctionBidSupplier').val();
		var eventId = $('.eventIdInBidHis').val();
		var revertReason = $('#revertReason').val();
		var auctionBidId = $('#bidId').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/buyer/RFA/revertOnAuctionBidInSummaryWithRemark/' + eventId + '/' + supplierId;
		var auctionBidId = {
			'auctionBidId' : auctionBidId,
			'revertReason':revertReason,
		};
		if(revertReason==""){
			 $(".check").show(); 
			 $(".check2").hide(); 
			return false;
		}else if(revertReason.length>250){
			$(".check2").show(); 
			$(".check").hide(); 
			return false;
		}
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : auctionBidId,
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				$('#loading').hide();
				if (request.getResponseHeader('success')) {
					var success = request.getResponseHeader('success');
					$.jGrowl(success, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
				}
			},
			error : function(request, textStatus, errorThrown) {
				console.log(request.responseText);
			},
			complete : function() {
				$('#loading').hide();
				$('#RevertBidPopup').dialog('close');
			}
		});  
	});
	
	
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/createrftevent.js?2"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/createRftMeeting.js?4"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/summaryAddSupplier.js"/>"></script>