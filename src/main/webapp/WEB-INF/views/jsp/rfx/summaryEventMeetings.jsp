<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseFive"><spring:message code="eventdescription.meeting.label" /></a>
		</h4>
	</div>
	<div id="collapseFive" class="panel-collapse collapse">
		<!-- allow meetings to be created until event is FINISHED - ref: #619 -->
		<c:if test="${(event.status == 'ACTIVE') and eventPermissions.owner}">
			<div class="marg-bottom-20 marg-left-20">
				<form action="${pageContext.request.contextPath}/buyer/${eventType}/createMeeting" method="get">
					<input type="hidden" name="source" value="summary">
					<input type="hidden" name="eventId" value="${eventId}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"><spring:message code="button.meetings.new.meeting" /></button>
				</form>
			</div>
		</c:if>
		<c:if test="${empty meetingList}">
			<div class="panel-body marg-bottom-20">
				<div class="meetingListInerCustom">
					<div class="meeting_inner">
						<div class="meeting_inner_main">
							<h3><spring:message code="eventsummary.meetings.no.meetings" /></h3>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<c:forEach var="listMeeting" items="${meetingList}">
			<div class="panel-body marg-bottom-20">
				<div class="pull-left width100">
					<div id="main" class="float-left">
						<div class="content">
							<div class="">
								<h3 class="col-md-12 text-left">
									<fmt:formatDate value="${listMeeting.appointmentDateTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</h3>
								<!-- /#Countdown Div -->
							</div>
							<!-- /.Counter Div -->
						</div>
						<!-- /.Content Div -->
					</div>
					<div class="meetingListInerCustom">
						<div class="meeting_inner">
							<div class="meeting_inner_main">
								<h3>${listMeeting.title}</h3>
							</div>
						</div>
						<div class="meeting_inner">
							<div class="meeting_inner_main" id="${listMeeting.id}">
								<h3>
									<font ${listMeeting.status== 'CANCELLED' ? 'color="red"' : ''}><spring:message code="application.status" />: ${listMeeting.status}</font>
								</h3>
							</div>
						</div>
					</div>
				</div>
				<div class="pull-left width100">
					<div id="main" class="float-left">
						<div class="content">
							<div class="counter">
								<jsp:useBean id="today" class="java.util.Date" />
								<c:if test="${today.time lt listMeeting.appointmentDateTime.time}">
									<div id="countdown${listMeeting.id}" class="countDownTimer" data-date="${listMeeting.appointmentDateTime}"></div>
								</c:if>
								<c:if test="${!empty listMeeting.inviteSuppliers}">
									<div class="col-md-12 suppliersList">
										<label class="marg-bottom-20"><spring:message code="rfasummary.meeting.suppliers" />:</label>
										<c:forEach var="inviteSupp" items="${listMeeting.inviteSuppliers}" varStatus="idx">
											<br />${idx.index + 1}. ${inviteSupp.companyName}
									</c:forEach>
									</div>
								</c:if>
								<!-- /#Countdown Div -->
							</div>
							<!-- /.Counter Div -->
						</div>
						<!-- /.Content Div -->
					</div>
					<div class="meetingListInerCustom">
						<div class="meeting_inner">
							<c:forEach var="contact" items="${listMeeting.rfxEventMeetingContacts}">
								<div class="">
									<c:if test="${!empty contact.contactName}">
										<p><img src="${pageContext.request.contextPath}/resources/images/meeting_profile.png" alt="meeting profile" />
										${contact.contactName}</p>
									</c:if>
									<c:if test="${!empty contact.contactEmail}">
										<p><img src="${pageContext.request.contextPath}/resources/images/meeting_message.png" alt="meeting message" />
										${contact.contactEmail}</p>
									</c:if>
									<c:if test="${!empty contact.contactNumber}">
										<p><img src="${pageContext.request.contextPath}/resources/images/meeting_phone.png" alt="meetting phone" />
										${contact.contactNumber}</p>
									</c:if>
								</div>
							</c:forEach>
						</div>
						<div class="meeting_inner">
							<div class="meeting_inner_main">
								<label><spring:message code="label.meeting.venue" /> :</label>
								<p class="pull-left">${listMeeting.venue}</p>
							</div>
							<div class="meeting_inner_main">
								<label>Mandatory: </label>
								<p class="pull-left" style="color:${listMeeting.meetingAttendMandatory ? '#ff0000' : ''}">${listMeeting.meetingAttendMandatory ?'<b>Yes</b>':'No'}</p>
							</div>
							<c:if test="${!empty listMeeting.eventMeetingDocument}">
								<div class="meeting_inner_main">
									<label><spring:message code="Product.Attatchment" /> :</label>
									<c:forEach var="docs" items="${listMeeting.eventMeetingDocument}" varStatus="indx">
									${indx.index + 1}. <a class="bluelink" href="${pageContext.request.contextPath}/buyer/${eventType}/downloadEventMeetingDocument/${docs.id}">${docs.fileName}
											(
											<fmt:formatNumber type="number" maxFractionDigits="1" value="${docs.fileSizeInKb}" />
											KB)
										</a>
										<br />
									</c:forEach>
								</div>
							</c:if>
						</div>
						<div class="meeting_inner_main" style="margin-left: 3%;">
							<label><spring:message code="pr.remark" />:</label>
							<p class="pull-left">${listMeeting.remarks} ${empty listMeeting.remarks ? 'N/A' : ''}</p>
						</div>
					</div>
					<div class="marg-bottom-20 mettingButtons">
						<div class="width100 pull-left" meeting-id="${listMeeting.id}" meeting-status="${listMeeting.status}">
							<c:if test="${(listMeeting.status !='COMPLETED' and listMeeting.status !='CANCELLED') and (event.status == 'ACTIVE' or event.status == 'SUSPENDED' or event.status == 'CLOSED' or event.status == 'COMPLETE')}">
								<a href="#" id="MeetingAttendanceButton${listMeeting.id}" data-placement="top" class="btn btn-info marg-left-10 marg-top-10 hvr-pop hvr-rectangle-out right-header-button MeetingAttendancePop mettingButtons"> <spring:message code="button.meetings.attendance" /> </a>
							</c:if>
							<c:if test="${listMeeting.status =='SCHEDULED' and (event.status == 'ACTIVE' or event.status == 'SUSPENDED' or event.status == 'CLOSED')}">
								<a href="#" id="summaryCancelMeetingButton${listMeeting.id}" data-placement="top" data-id="${listMeeting.id}" class="btn btn-info marg-left-10 hvr-pop hvr-rectangle-out right-header-button cancelMeetingPop mettingButtons marg-top-10">Cancel
									Meeting </a>
							</c:if>
							<c:if test="${listMeeting.status == 'SCHEDULED' and (event.status == 'ACTIVE') and eventPermissions.owner}">
								<form action="${pageContext.request.contextPath}/buyer/${eventType}/editMeeting" method="get" class="mettingButtons">
									<input type="hidden" name="source" value="summary">
									<input type="hidden" value="${listMeeting.id}" id="meetingId" name="meetingId">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<button id="editButton" class="btn btn-info marg-left-10 marg-top-10 hvr-pop hvr-rectangle-out right-header-button" type="submit">
										<spring:message code="application.edit" />
										<spring:message code="label.meeting" />
									</button>
								</form>
							</c:if>
							<c:if test="${(eventPermissions.owner or eventPermissions.editor or eventPermissions.viewer or isAdmin) and event.status != 'DRAFT'}">
								<form:form class="pull-right marg-left-10 marg-top-10 mettingButtons" action="${pageContext.request.contextPath}/buyer/${eventType}/downloadMeetingAttendance/${event.id}/Meetings/${listMeeting.id}">
									<button class="btn float-right btn-info hvr-pop" id="idMeetingDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.attendance" />'>
										<span class="glyph-icon icon-separator">
											<i class="glyph-icon icon-download"></i>
										</span>
										<span class="button-content"><spring:message code="button.meeting.attendance.report" /></span>
									</button>
								</form:form>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
	<jsp:include page="summaryMeetingAttendance.jsp" />
</div>
