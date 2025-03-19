<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${!empty eventMeetings}">
	<div class="panel sum-accord">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" class="accordion" href="#collapseMeeting"> <spring:message code="label.meeting" /> </a>
			</h4>
		</div>
		<div id="collapseMeeting" class="panel-collapse collapse">
			<c:forEach var="listMeeting" items="${eventMeetings}">
				<div class="panel-body marg-bottom-20">
					<div id="main" class="float-left">
						<div class="content">
							<div class="counter">
								<h3>
									<fmt:formatDate value="${listMeeting.appointmentDateTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</h3>
								<jsp:useBean id="today" class="java.util.Date" />
								<c:if test="${today.time lt listMeeting.appointmentDateTime.time}">
									<div id="countdown${listMeeting.id}" class="countDownTimer" data-date="${listMeeting.appointmentDateTime}"></div>
								</c:if>
							</div>
						</div>
						<div class="meeting_inner_main marg-left-20">
							<div class="row marg-left-20">
								<label><spring:message code="supplierevent.response" />:</label>
								<span>${listMeeting.supplierAttendance.meetingAttendanceStatus}</span>
							</div>
						</div>
						<div class="meeting_inner_main marg-left-20">
							<div class="row marg-left-20">
								<label><spring:message code="pr.remark" />:</label>
								<span>${listMeeting.remarks} ${empty listMeeting.remarks ? 'N/A' : '' }</span>
							</div>
						</div>
					</div>
					<div class="meetingListInerCustom">
						<div class="meeting_inner">
							<div class="meeting_inner_main">
								<h3>${listMeeting.title}</h3>
							</div>
							<c:forEach var="contact" items="${listMeeting.rfxEventMeetingContacts}">
								<div class="meeting_inner_main">
									<c:if test="${!empty contact.contactName}">
										<img src="${pageContext.request.contextPath}/resources/images/meeting_profile.png" alt="meeting profile" />
										<p>${contact.contactName}</p>
									</c:if>
									<c:if test="${!empty contact.contactEmail}">
										<img src="${pageContext.request.contextPath}/resources/images/meeting_message.png" alt="meeting message" />
										<p>${contact.contactEmail}</p>
									</c:if>
									<c:if test="${!empty contact.contactNumber}">
										<img src="${pageContext.request.contextPath}/resources/images/meeting_phone.png" alt="meetting phone" />
										<p>${contact.contactNumber}</p>
									</c:if>
								</div>
							</c:forEach>													
						</div>
						<div class="meeting_inner">
							<div class="meeting_inner_main" id="${listMeeting.id}">
								<c:if test="${listMeeting.status== 'CANCELLED'}">
									<h3>
										<font color="red">${listMeeting.status}</font>
									</h3>
								</c:if>
								<c:if test="${listMeeting.status != 'CANCELLED'}">
									<h3>${listMeeting.status}</h3>
								</c:if>
							</div>
							<c:if test="${!empty listMeeting.eventMeetingDocument}">
								<div class="meeting_inner_main">
									<label><spring:message code="Product.Attatchment" /> :</label>
									<c:forEach var="docs" items="${listMeeting.eventMeetingDocument}" varStatus="indx">
										<span> ${indx.index + 1}. </span>
										<a class="bluelink" href="${pageContext.request.contextPath}/supplier/downloadEventMeetingDocument/${eventType}/${docs.id}">${docs.fileName}
											(
											<fmt:formatNumber type="number" maxFractionDigits="1" value="${docs.fileSizeInKb}" />
											KB)
										</a>
										</a>
										<br />
									</c:forEach>
								</div>
							</c:if>
						</div>
						<div class="meeting_inner_main">
								<div class="row marg-left-10">
								<div class="col-md-6">
									<label>Venue:</label>
									<span>${listMeeting.venue}</span>
								</div>					
								<div class="col-md-6">
									<label>Mandatory:</label>
									<span style="color:${listMeeting.meetingAttendMandatory ? '#ff0000' : ''}">${listMeeting.meetingAttendMandatory ?'<b>Yes</b>':'No'}</span>
								</div>			
								</div>
							</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</c:if>