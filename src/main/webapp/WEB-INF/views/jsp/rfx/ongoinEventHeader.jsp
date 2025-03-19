<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<!-- page title block -->

<style>

ul.tab li.current {
    border-left: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
    border-right: 1px solid #e8e8e8;
}

</style>

<div class="Section-title title_border gray-bg">
	<h2 class="trans-cap progress-head">${eventType}:${event.eventName}</h2>
	<h2 class="trans-cap pull-right">
		<spring:message code="application.status" />
		: ${event.status}
	</h2>
</div>
<c:if test="${event.status !='FINISHED'}">
	<div class="Invited-Supplier-List white-bg">
		<div class="row">
			<div class="col-md-6 col-sm-6 close-da">
				<div class="row">
					<div class="col-md-10">
						<div class="meeting2-heading-new">
							<jsp:useBean id="now" class="java.util.Date" />
							<c:if test="${eventType ne 'RFA'}">
								<c:if test="${now.time gt event.eventStart.time}">
									<label><spring:message code="application.closedate" /></label>
									<h3>
										<span> <fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</c:if>
								<c:if test="${now.time lt event.eventStart.time}">
									<label><spring:message code="application.startdate" /></label>
									<h3>
										<span> <fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</c:if>
							</c:if>
							<c:if test="${eventType eq 'RFA'}">
								<c:if test="${empty event.auctionResumeDateTime}">
									<c:if test="${now.time gt event.eventStart.time}">
										<label><spring:message code="application.closedate" /></label>
										<h3>
											<span> <fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</span>
										</h3>
									</c:if>
									<c:if test="${now.time lt event.eventStart.time}">
										<label><spring:message code="application.startdate" /></label>
										<h3>
											<span> <fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</span>
										</h3>
									</c:if>
								</c:if>
								<c:if test="${not empty event.auctionResumeDateTime}">
									<c:if test="${now.time gt event.auctionResumeDateTime.time}">
										<label><spring:message code="application.closedate" /></label>
										<h3>
											<span> <fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</span>
										</h3>
									</c:if>
									<c:if test="${now.time lt event.auctionResumeDateTime.time}">
										<label><spring:message code="application.resumedate" /></label>
										<h3>
											<span> <fmt:formatDate value="${event.auctionResumeDateTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</span>
										</h3>
									</c:if>
								</c:if>
							</c:if>
						</div>
					</div>
				</div>
				<c:if test="${eventType eq 'RFA' and event.status eq 'ACTIVE' and !(event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED')}">
					<div class="row">
						<div class="col-md-10">
							<div class="meeting2-heading-new">
								<c:if test="${event.auctionType eq 'FORWARD_DUTCH' or event.auctionType eq 'REVERSE_DUTCH'}">
									<a id="idDutchConsole" class="top-marginAdminList step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out span6 offset3" href="${pageContext.request.contextPath}/auction/dutchAuctionConsole/${event.id}"><spring:message code="rfaevent.auction.hall" /></a>
								</c:if>
								<c:if test="${event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH' or event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID'}">
									<a id="idEnglishConsole" class="top-marginAdminList step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out span6 offset3" href="${pageContext.request.contextPath}/buyer/englishAuctionConsole/${event.id}"><spring:message code="rfaevent.auction.hall" /></a>
								</c:if>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${eventType eq 'RFA' and (eventPermissions.owner or eventPermissions.viewer or  eventPermissions.editor  or eventPermissions.evaluator or eventPermissions.leadEvaluator or eventPermissions.conclusionUser or isAdmin or buyerReadOnlyAdmin)  and ((event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall)}">
					<div class="row">
							<div class="col-md-10">
								<div class="meeting2-heading-new">
									<c:if test="${event.auctionType eq 'FORWARD_DUTCH' or event.auctionType eq 'REVERSE_DUTCH'}">
										<a id="idDutchConsole" class="top-marginAdminList step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out span6 offset3" href="${pageContext.request.contextPath}/auction/dutchAuctionConsole/${event.id}"><spring:message code="rfaevent.auction.hall" /></a>
									</c:if>
									<c:if test="${event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH' or event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID'}">
										<a id="idEnglishConsole" class="top-marginAdminList step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out span6 offset3" href="${pageContext.request.contextPath}/buyer/englishAuctionConsole/${event.id}"><spring:message code="rfaevent.auction.hall" /></a>
									</c:if>
								</div>
							</div>
					</div>
				</c:if>
			</div>
			<div class="col-md-6 col-sm-6 close-da">
				<div id="main">
					<div class="content">
						<div class="counter">
							<jsp:useBean id="today" class="java.util.Date" />
							<c:if test="${eventType eq 'RFA' and event.status eq 'ACTIVE'}">
								<c:if test="${empty event.auctionResumeDateTime}">
									<c:if test="${today.time lt event.eventEnd.time and today.time gt event.eventStart.time}">
										<h3>
											<spring:message code="application.time.left.end" />
										</h3>
										<div class="main-example">
											<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventEnd" />
											<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/${eventType}/eventSummary/${event.id}" data-date="${eventEnd}"></div>
										</div>
									</c:if>
									<c:if test="${today.time lt event.eventStart.time}">
										<h3>
											<spring:message code="application.time.left.start" />
										</h3>
										<div class="main-example">
											<fmt:formatDate value="${event.eventStart}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventStart" />
											<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/RFA/eventSummary/${event.id}" data-date="${eventStart}"></div>
										</div>
									</c:if>
								</c:if>
								<c:if test="${!empty event.auctionResumeDateTime}">
									<c:if test="${today.time lt event.eventEnd.time and today.time gt event.auctionResumeDateTime.time}">
										<h3>
											<spring:message code="application.time.left.end" />
										</h3>
										<div class="main-example">
											<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventEnd" />
											<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/${eventType}/eventSummary/${event.id}" data-date="${eventEnd}"></div>
										</div>
									</c:if>
									<c:if test="${today.time lt event.auctionResumeDateTime.time}">
										<h3>
											<spring:message code="application.time.left.resume" />
										</h3>
										<div class="main-example">
											<fmt:formatDate value="${event.auctionResumeDateTime}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventStart" />
											<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/${eventType}/eventSummary/${event.id}" data-date="${eventStart}"></div>
										</div>
									</c:if>
								</c:if>
							</c:if>
							<c:if test="${eventType ne 'RFA' and event.status eq 'ACTIVE'}">

								<c:if test="${today.time lt event.eventEnd.time and today.time gt event.eventStart.time}">
									<div>
										<h3>
											<spring:message code="application.time.left.end" />
										</h3>
									</div>

									<div class="main-example">
										<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventEnd" />
										<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/${eventType}/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/${eventType}/eventSummary/${event.id}" data-date="${eventEnd}"></div>
									</div>
									<%-- <fmt:formatDate value="${event.eventEnd}"
										pattern="E, dd MMM yyyy HH:mm:ss Z"
										timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"
										var="eventEnd" />
									<div id="countdown" data-date="${eventEnd}"></div> --%>
								</c:if>
								<c:if test="${today.time lt event.eventStart.time}">
									<div>
										<h3>
											<spring:message code="application.time.left.start" />
										</h3>
									</div>
									<div class="main-example">
										<fmt:formatDate value="${event.eventStart}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="eventStart" />
										<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/event/${eventType}/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/${eventType}/eventSummary/${event.id}" data-date="${eventStart}"></div>
									</div>
									<%-- <fmt:formatDate value="${event.eventStart}"
										pattern="E, dd MMM yyyy HH:mm:ss Z"
										timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"
										var="eventStart" />
									<div id="countdown" data-date="${eventStart}"></div> --%>
								</c:if>
							</c:if>
						</div>
					</div>
				</div>
				<!-- /#Main Div -->
			</div>
			<!-- /.Columns Div -->
		</div>
		<!-- /.Row Div -->
	</div>
</c:if>
<div class="tab-main marg_top_20">
	<ul class="tab event-details-tabs">
		<!-- Enable progress tab event after event close for RFA type Dutch Auction and Rule type Lumpsum bidding to show Revised BQ submission status -->
		<c:if test="${!eventPermissions.awardApprover and (!event.viewSupplerName and !(eventPermissions.unMaskUser or (eventPermissions.unMaskUser && event.status == 'COMPLETE')) ) and !(eventPermissions.conclusionUser) and (eventPermissions.owner or eventPermissions.editor or eventPermissions.approverUser  or (eventPermissions.viewer and !eventPermissions.leadEvaluator and !eventPermissions.evaluator  and !eventPermissions.opener))}">
			<li class="tab-link ${showProgressTab == true ? 'current' : ''}" style="${(event.isOnGoing || event.status == 'ACTIVE' ||  event.status == 'CLOSED') ? '' : 'display:none'}"><a id="idOngoingProgTab" class="${showProgressTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/${eventType}/eventProgress/${event.id}"><spring:message code="buyer.eventReport.progresstab" /></a></li>
		</c:if>
		<c:if test="${!eventPermissions.awardApprover and event.viewSupplerName and !(eventPermissions.unMaskUser or (eventPermissions.unMaskUser && event.status == 'COMPLETE')) and !(eventPermissions.conclusionUser) }">
			<li class="tab-link ${showProgressTab == true ? 'current' : ''}" style="${(event.isOnGoing || event.status == 'ACTIVE' ||  event.status == 'CLOSED') ? '' : 'display:none'}"><a id="idOngoingProgTab" class="${showProgressTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/${eventType}/eventProgress/${event.id}"><spring:message code="buyer.eventReport.progresstab" /></a></li>
		</c:if>
		<c:if test="${!eventPermissions.awardApprover and !(eventPermissions.unMaskUser or (eventPermissions.unMaskUser && event.status == 'COMPLETE'))}">
			<li class="tab-link ${showSummaryTab == true ? 'current' : ''}"><a id="idOngoingSumTab" class="${showSummaryTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/${eventType}/viewSummary/${event.id}">Summary</a></li>
		</c:if>
		<c:if test="${!eventPermissions.awardApprover and envelopListCount != 0}">
			<li class="tab-link ${showEnvelopTab == true ? 'current' : ''}" style="${(event.isOnGoing || (event.status == 'COMPLETE' || event.status == 'CLOSED' )) ? '' : 'display:none'}"><a id="idOngoingEvalTab" class="${showEnvelopTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/${eventType}/envelopList/${event.id}"><spring:message code="buyer.eventReport.evaluationtab" /></a></li>
		</c:if>
		<c:if test="${!eventPermissions.awardApprover and !(event.status == 'DRAFT' || event.status == 'PENDING' || event.status == 'APPROVED' || event.status == 'CANCELED')}">
			<c:if test="${(!event.viewSupplerName and !(eventPermissions.unMaskUser or (eventPermissions.unMaskUser && event.status == 'COMPLETE'))) and (eventPermissions.owner or eventPermissions.editor or eventPermissions.approverUser or (eventPermissions.viewer and !eventPermissions.leadEvaluator and !eventPermissions.evaluator  and !eventPermissions.opener and !eventPermissions.revertBidUser and !eventPermissions.conclusionUser))}">
				<li class="tab-link ${showMessageTab == true ? 'current' : ''}"><a id="idOngoingMessageTab" class="${showMessageTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/${eventType}/viewMailBox/${event.id}"><spring:message code="buyer.eventReport.messagetab" /></a></li>
			</c:if>
			<c:if test="${event.viewSupplerName and !eventPermissions.revertBidUser and !eventPermissions.conclusionUser and !(eventPermissions.unMaskUser or (eventPermissions.unMaskUser && event.status == 'COMPLETE')) }">
				<li class="tab-link ${showMessageTab == true ? 'current' : ''}"><a id="idOngoingMessageTab" class="${showMessageTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/${eventType}/viewMailBox/${event.id}"><spring:message code="buyer.eventReport.messagetab" /></a></li>
			</c:if>
		</c:if>
		<c:if test="${showAwardTab or (eventType != 'RFI' and event.awardStatus != null and (event.status eq 'COMPLETE' or event.status eq 'FINISHED') and (eventPermissions.owner or eventPermissions.editor) and eventType != 'RFI' and event.billOfQuantity) }">
			<li class="tab-link ${showAwardTab == true ? 'current' : ''}"><a id="idOngoingAwardTab" class="${showAwardTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/${eventType}/eventAward/${event.id}"><spring:message code="eventsummary.href.award.event" /></a></li>
		</c:if>
	</ul>
</div>
<div class="clear" />
<div class="tab-main-inner pad_all_15 border-all-side float-left width-100">
	<div class="row">
		<div class="col-md-3">
			<b><spring:message code="application.eventid" />:</b> ${event.eventId}<br /> <b><spring:message code="application.eventowner" />:</b> ${event.eventOwner.name}, ${event.eventOwner.communicationEmail}<br />
			<c:if test="${eventType eq 'RFA'}">
				<b><spring:message code="rfx.auction.type.label" />:</b> ${event.auctionType.value}<br />
			</c:if>
			<c:if test="${rfxTemplate.templateName != null}">
				<b><spring:message code="application.event.template.name" />:</b> ${rfxTemplate.templateName}
				</c:if>
		</div>
				
		<div class="col-sm-12 col-md-9 pull-right">
			<!-- Below download button should display only event status has completed.
					Below Download Buttons should access only Admin, owner, editor and Viewer.
				-->
				<c:if test="${(eventType eq 'RFT' or eventType eq 'RFI' or eventType eq 'RFP' or eventType eq 'RFQ' ) and (isAdmin or buyerReadOnlyAdmin )  and (event.status eq 'FINISHED')}">
					<div class="pull-right">
						<form:form action="${pageContext.request.contextPath}/buyer/RFA/downloadAllReport/${event.id}/${eventType }">
							<button class="btn btn-sm w-indigo float-right  hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventReport.allReports.button" />'>
								<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
								</span> <span class="button-content"><spring:message code="eventReport.allReports.button" /> </span>
							</button>
						</form:form>
					</div>
				</c:if>
				
				<c:if test="${eventType eq 'RFT' and (isAdmin or buyerReadOnlyAdmin) and event.status eq 'COMPLETE' and ((!event.viewSupplerName and event.disableMasking) or (event.viewSupplerName and !event.disableMasking))}">
					<div class="pull-right">
						<form:form action="${pageContext.request.contextPath}/buyer/RFA/downloadAllReport/${event.id}/${eventType }">
							<button class="btn btn-sm w-indigo float-right  hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventReport.allReports.button" />'>
								<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
								</span> <span class="button-content"><spring:message code="eventReport.allReports.button" /> </span>
							</button>
						</form:form>
					</div>
				</c:if>
				
			<c:if test="${eventPermissions.owner or eventPermissions.editor or eventPermissions.viewer or isAdmin}">
				<c:choose>
					<c:when test="${!allowToView}">
						<c:if test="${eventPermissions.owner}">
							<c:if test="${(eventType eq 'RFA') and event.status eq 'FINISHED'}">
								<div class="pull-right">
									<form:form action="${pageContext.request.contextPath}/buyer/RFA/downloadAllReport/${event.id}/${eventType }">
										<button class="btn btn-sm w-indigo float-right  hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventReport.allReports.button" />'>
											<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
											</span> <span class="button-content"><spring:message code="eventReport.allReports.button" /> </span>
										</button>
									</form:form>
								</div>
							</c:if>

							<div class="pull-right">
								<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEvaluationSummary/${event.id}">
									<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventsummary.download.eventbtn" />'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
										</span> <span class="button-content"><spring:message code="eventReport.event.summary.button" /></span>
									</button>
								</form:form>
							</div>

						</c:if>
					</c:when>
					<c:otherwise>
						<div class="pull-right">
							<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEvaluationSummary/${event.id}">
								<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventsummary.download.eventbtn" />'>
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
									</span> <span class="button-content"><spring:message code="eventReport.event.summary.button" /></span>
								</button>
							</form:form>
						</div>
					</c:otherwise>
				</c:choose>

				<%-- <div class="pull-right">
									<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadShortEvaluationSummary/${eventType}/${event.id}" method="get">
										<button class="btn btn-sm float-right btn-warning hvr-pop marg-left-10 evaluationSummaryReport" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="download.short.evaluation" />' type="submit">
											<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
											</span> <span class="button-content">Short Evaluation Summary</span>
										</button>
									</form:form>
								</div> --%>
				<c:choose>
					<c:when test="${!allowToView}">
						<c:if test="${eventPermissions.owner}">
							<c:if test="${event.status eq 'COMPLETE' or event.status eq 'FINISHED'}">
								<c:if test="${empty event.evaluationConclusionEnvelopeEvaluatedCount}">
								<div class="pull-right">
									<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/evaluationSummary/${event.id}">
										<button class="btn btn-sm float-right btn-warning hvr-pop marg-left-10 evaluationSummaryReport" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventsummary.download.evaluationbtn" />' type="submit">
											<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
											</span> <span class="button-content"><spring:message code="eventReport.evaluation.summary.button" /></span>
										</button>
									</form:form>
								</div>
								</c:if>
							</c:if>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:if test="${event.status eq 'COMPLETE' or event.status eq 'FINISHED'}">
							<c:if test="${empty event.evaluationConclusionEnvelopeEvaluatedCount}">
							<div class="pull-right">
								<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/evaluationSummary/${event.id}">
									<button class="btn btn-sm float-right btn-warning hvr-pop marg-left-10 evaluationSummaryReport" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventsummary.download.evaluationbtn" />' type="submit">
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
										</span> <span class="button-content"><spring:message code="eventReport.evaluation.summary.button" /></span>
									</button>
								</form:form>
							</div>
							</c:if>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:if>
			<c:if test="${eventType eq 'RFA' and (eventPermissions.owner or eventPermissions.viewer or  eventPermissions.editor  or eventPermissions.evaluator or eventPermissions.leadEvaluator or eventPermissions.conclusionUser or isAdmin or buyerReadOnlyAdmin)  and ((event.status eq 'FINISHED' or event.status eq 'COMPLETE' or (event.auctionType == 'REVERSE_SEALED_BID' or event.auctionType == 'FORWARD_SEALED_BID' ? (false) : (event.status eq 'CLOSED') ) ) and event.viewAuctionHall)}">
				<div class="pull-right">
					<c:if test="${event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH' }">
						<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadBuyerBiddingEnglishReport/${event.id}">
							<button class="float-right btn btn-sm btn-success hvr-pop marg-left-10 downloadBuyerAuctionReport" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventsummary.download.english.auction" />' type="submit">
								<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
								</span> <span class="button-content"><spring:message code="eventReport.auction.report.button" /></span>
							</button>
						</form:form>
					</c:if>
					<c:if test="${! (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH') }">
						<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadBuyerAuctionReport/${event.id}">
							<button class="float-right btn btn-sm btn-success hvr-pop marg-left-10 downloadBuyerAuctionReport" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="eventsummary.download.auction.report" />' type="submit">
								<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
								</span> <span class="button-content"><spring:message code="eventReport.auction.report.button" /></span>
							</button>
						</form:form>
					</c:if>
				</div>
			</c:if>
			<c:if test="${eventPermissions.owner or isAdmin or eventPermissions.conclusionUser or eventPermissions.leadEvaluator or buyerReadOnlyAdmin}">
				<c:if test="${(event.status eq 'COMPLETE' or event.status eq 'FINISHED')}">
					<c:if test="${event.enableEvaluationConclusionUsers and eventPermissions.allUserConcludedPermatury}">
						<div class="pull-right" id="evaluationConlusionReport">
							<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEvaluationConclustionReport/${event.id}">
								<button type="submit" class="btn btn-sm float-right btn-warning hvr-pop marg-left-10" id="idEvalConclusionDownload" data-toggle="tooltip" data-placement="top" data-original-title="Download Evaluation Conclusion Report">
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
									</span> <span class="button-content"><spring:message code="eventReport.evaluation.conclusion.button" /></span>
								</button>
							</form:form>
						</div>
					</c:if>
				</c:if>
			</c:if>
		</div>
		
		<c:set var="requestPath" value="${requestScope['javax.servlet.forward.request_uri']}"/>
		<c:if test="${fn:contains(requestPath, 'eventAward') and eventType != 'RFI' and (event.awardStatus != null and (event.awardStatus == 'PENDING' or event.awardStatus == 'APPROVED'))}">
			<div class="col-sm-12 col-md-9 pull-right marg-top-10">
				<h5 class="trans-cap pull-right">
					<spring:message code="award.approval.status" />
					: ${event.awardStatus}
				</h5>
			</div>
		</c:if>
		
	</div>
</div>
</div>
<style>
.counter h3 {
	margin-bottom: 10px;
}

#countdown {
	margin-bottom: 10px;
}

.counter.auction-page.height-150, .counter {
	min-height: 50px;
}

.w-indigo {
	color: #fff !important;
	background-color: #3f51b5 !important
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/js-core/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/widgets/charts/morris/morris.js"></script>
<script>
	counterMeetings();
	//setInterval(function() {
	//counterMeetings();
	//}, 60000);
	function counterMeetings() {
		$('#countdown').each(
				function() {
					// set the date we're counting down to
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
						htmldata = '<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + days + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + hours
								+ '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + minutes + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">' + seconds + '</span></span>';
					}//$('#countdown').html(htmldata);
					$(countdown).html(htmldata);

				});
	}
</script>
