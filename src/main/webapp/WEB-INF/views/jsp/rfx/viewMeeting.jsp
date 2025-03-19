<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxCreatingMeetings" code="application.rfx.create.meetings" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingMeetings}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li>
					<a href="${pageContext.request.contextPath}/buyer/buyerDashboard">
						<spring:message code="application.dashboard" />
					</a>
				</li>
				<li class="active">
					<spring:message code="application.create" /> ${eventType.value}
				</li>
				<li class="active">
					<spring:message code="label.meeting" />
				</li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap tender-request-heading">
					<spring:message code="label.meeting" />
				</h2>
				<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${event.status}</h2>
			</div>
			<div class="clear"></div>
			<jsp:include page="eventHeader.jsp"></jsp:include>
			<div class="clear"></div>
			<div class="example-box-wrapper wigad-new">
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<div class="Invited-Supplier-List white-bg">
					<div class="meeting2-heading">
						<h3>
							<fmt:formatDate value="${eventMeeting.appointmentDateTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							&nbsp;&nbsp;&nbsp;&nbsp; ${eventMeeting.title}(
							<c:if test="${eventMeeting.status== 'CANCELLED'}">
								<font color="red">${eventMeeting.status}</font>
							</c:if>
							<c:if test="${eventMeeting.status != 'CANCELLED'}">${eventMeeting.status}</c:if>
							)
						</h3>
						<div class="right-header-button">
							<form action="${pageContext.request.contextPath}/buyer/${eventType}/editMeeting" method="get">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<input type="hidden" name="source" value="meeting"> <input type="hidden" value="${eventMeeting.id}" id="meetingId" name="meetingId">
								<button id="editButton" ${eventMeeting.status=='CANCELLED' ? "disabled='disabled'" : ""} class="hvr-pop hvr-rectangle-out3" type="submit">
									<spring:message code="application.edit" />
								</button>
							</form>
						</div>
					</div>
					<div class="meeting_main pad_all_15">
						<div class="meeting_main_inner-left">
							<div class="row marg-bottom-30">
								<div class="col-md-3">
									<label>
										<spring:message code="label.meeting.venue" />
										:
									</label>
								</div>
								<div class="col-md-6">
									<div class="venue-address">${eventMeeting.venue}</div>
								</div>
							</div>
							<div class="row marg-bottom-30">
								<div class="col-md-3">
									<label>
										<spring:message code="label.meeting.contact.information" />
										:
									</label>
								</div>
								<div class="col-md-6">
									<div class="venue-address">
										<c:forEach items="${eventMeeting.rfxEventMeetingContacts}" var="contactPerson">
											<c:if test="${!empty contactPerson.contactName}">
												<img src="${pageContext.request.contextPath}/resources/images/meeting_profile.png" alt="meeting profile" />
												${contactPerson.contactName}<br />
											</c:if>
											<c:if test="${!empty contactPerson.contactEmail}">
												<img src="${pageContext.request.contextPath}/resources/images/meeting_message.png" alt="meeting message" />
												${contactPerson.contactEmail}<br />
											</c:if>
											<c:if test="${!empty contactPerson.contactNumber}">
												<img src="${pageContext.request.contextPath}/resources/images/meeting_phone.png" alt="meetting phone" />
												${contactPerson.contactNumber}<br />
											</c:if>
											<br>
										</c:forEach>
									</div>
								</div>
							</div>
							<div class="row marg-bottom-30">
								<div class="col-md-3">
									<label>
										<spring:message code="label.meeting.remark" />
										:
									</label>
								</div>
								<div class="col-md-6">
									<div class="venue-address">${eventMeeting.remarks}</div>
								</div>
							</div>
						</div>
						<div class="meeting_main_inner-right">
							<div class="">
								<h3><spring:message code="rfaevent.meetings.invited.suppliers" /></h3>
								<div class="chk_scroll_box">
									<div class="scroll_box_inner overscrollInnerBox">
										<c:forEach items="${listSupplier}" var="sp">
											<div class="suppliers-name">
												<label>${sp.companyName}</label>
												<c:if test="${fn:length(listSupplier) > 1}">
													<span>
														<a href="" class="removeMeetSupplier ${eventMeeting.status=='CANCELLED' ? 'disabled' : ''}" removeSupplierId="${sp.id}">
															<img src="${pageContext.request.contextPath}/resources/images/black-xross.png">
														</a>
													</span>
												</c:if>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 marg-top-20"></div>
				<div class="col-md-6 marg-top-20">
					<c:if test="${event.status != 'DRAFT'}">
						<c:if test="${eventMeeting.status == 'SCHEDULED'}">
							<a href="javascript:void(0);" id="cancelMeetingButton" data-id="${eventMeeting.id}" data-placement="top" }
						class="btn btn-info ph_btn marg-left-10 hvr-pop hvr-rectangle-out right-header-button cancelMeetingPop"> <spring:message code="rfaevent.cancel.meeting.button" /> </a>
						</c:if>
					</c:if>
					<form action="${pageContext.request.contextPath}/buyer/${eventType}/meetingList" method="post">
						<input type="hidden" name="source" value="meeting" /> <input type="hidden" name="eventId" value="${eventMeeting.rfxEvent.id}" /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<button type="submit" class="btn btn-info ph_btn marg-left-10 hvr-pop hvr-rectangle-out right-header-button" id="idMeetingListBtn">
							<spring:message code="label.meeting.list" />
						</button>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<!----------------------popup----------------  -->
<div class="flagvisibility dialogBox" id="cancelPopUp" title="Confirm Cancel Meeting">
	<div class="float-left width100 pad_all_15 white-bg">
		<form method="post" id="cancelMeetingForm">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="meetingId" id="meetingId" value="${eventMeeting.id}">
			<div class="row">
				<label class="col-sm-4 control-label"><spring:message code="eventsummary.cancel.reason" /></label>
				<div class="col-sm-6 col-md-6">
					<div class="form-group">
					<spring:message code="event.reason.cancellation.placeholder" var="reasonCancellation"/>
						<textarea placeholder="${reasonCancellation}" rows="4" name="cancelReason" id="cancelMeetingReason" data-validation="required length" data-validation-length="max250" class="form-control"></textarea>
						<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<spring:url value="${pageContext.request.contextPath}/buyer/${eventType}/createMeeting" var="createUrl" htmlEscape="true" />
						<button type="submit" id="cancelMeeting" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out cancelOkMeeting"><spring:message code="eventsummary.ok" /></button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/createRftMeeting.js?2"/>"></script>
<script>
	<c:if test="${eventPermissions.viewer}">
	$(window).bind('load', function() {
		var allowedFields = '#idMeetingListBtn,#bubble';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
</script>