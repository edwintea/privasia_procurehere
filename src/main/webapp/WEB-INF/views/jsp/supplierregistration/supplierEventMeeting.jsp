<%@ page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<spring:message var="supplierRfxMeetingsDesk" code="application.supplier.rfx.meetings" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxMeetingsDesk}] });
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
				var redirectUrl = getContextPath() + "/supplier/viewSupplierMeeting/${eventType}/${event.id}";
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
		$( "#supplierMeeting" ).click(function() {
			$( "#supplierMeeting" ).addClass('disableCq')
			});
		connect();
	});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="supplierSubmissionHeader.jsp" />
			<div class="tab-main-inner pad_all_15">
				<div id="tab-3" class="tab-content">
					<div class="row marg-top-20">
						<div class="col-md-3 col-sm-5 col-xs-5">
							<input type="hidden" name="eventId" id="eventId" value="${event.id}" /> <select class="custom-select1" name="" data-parsley-id="0644" id="eventFilter" path="" value="${event.id}">
								<option value="">All Meetings</option>
								<c:forEach items="${statusList}" var="item">
									<option value="${item}">${item}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="allmettingsData">
						<c:if test="${fn:length(listEventMeeting) == 0}">
							<div class="panel-heading Progress-Report-main marg-top-20 meeting-detail pending mettingEachBlock" data-metting="${meetingobj.id}">No Meetings.</div>
						</c:if>
						<c:forEach var="meetingobj" items="${listEventMeeting}">
							<div class="Progress-Report-main marg-top-20 meeting-detail pending mettingEachBlock" data-metting="${meetingobj.id}">
								<div class="panel-heading">
									<h4 class="panel-title">${meetingobj.title}</h4>
								</div>
								<div id="main" class="float-left set-h3-main">
									<div class="content">
										<div class="counter set-h3">
											<h3>
												<fmt:formatDate value="${meetingobj.appointmentDateTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</h3>
											<!-- /#Countdown Div -->
										</div>
										<!-- /.Counter Div -->
									</div>

									<%-- 									<c:if test="${today lt meetingobj.appointmentDateTime}">
										<fmt:formatDate value="${meetingobj.appointmentDateTime}" pattern="E, dd MMM yyyy HH:mm:ss Z" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="appointmentDateTime" />
										<div id="countdown${meetingobj.id}" class="countDownTimer" data-date="${appointmentDateTime}"></div>
									</c:if> --%>

								</div>
								<div class="meetingListInerCustom">
									<div class="meeting_inner">
										<div class="meeting_inner_main">
											<h3>${meetingobj.meetingType.value}</h3>
										</div>
										<c:forEach var="contact" items="${meetingobj.rfxEventMeetingContacts}">
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
										<div class="meeting_inner_main">
											<c:if test="${meetingobj.status== 'CANCELLED'}">
												<h3>
													<font color="red">${meetingobj.status}</font>
												</h3>
											</c:if>
											<c:if test="${meetingobj.status != 'CANCELLED'}">
												<h3>${meetingobj.status}</h3>
											</c:if>
										</div>
										<div class="meeting_inner_main">
											<label>Venue :</label>
											<p>${meetingobj.venue}</p>
										</div>
										<div class="meeting_inner_main">
											<label>Mandatory:</label> <span style="color:${meetingobj.meetingAttendMandatory ?'#ff0000':''}">${meetingobj.meetingAttendMandatory ?'<b>Yes</b>':'No'}</span>
										</div>
										<c:if test="${!empty meetingobj.eventMeetingDocument}">
											<div class="meeting_inner_main">
												<label>Attachment :</label>
												<c:forEach var="docs" items="${meetingobj.eventMeetingDocument}" varStatus="indx">
																				${indx.index + 1}. <a class="bluelink" href="${pageContext.request.contextPath}/supplier/downloadEventMeetingDocument/${eventType}/${docs.id}">${docs.fileName} (<fmt:formatNumber type="number" maxFractionDigits="1" value="${docs.fileSizeInKb}" /> KB)
													</a>
													<br />
												</c:forEach>
											</div>
										</c:if>
									</div>
									<div class="meeting_inner_main" style="margin-left: 3%;">
										<label>Remark:</label>
										<p class="pull-left">${meetingobj.remarks}${empty meetingobj.remarks ? 'N/A' : ''}</p>
									</div>
								</div>
								<c:if test="${meetingobj.supplierAttendance == null && meetingobj.status == 'SCHEDULED' && (event.status == 'ACTIVE' or event.status == 'CLOSED' or event.status == 'COMPLETE')}">
									<div class="popup_for_sure pad_all_15">
										<h3>Are you Going ?</h3>
										<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out openModelSure">Yes</button>
										<button class="btn ph_btn_small btn-black marg-left-10 fade-btn  hvr-pop hvr-rectangle-out1 openModelSureCancel">No</button>
									</div>
								</c:if>
								<c:if test="${meetingobj.supplierAttendance != null && meetingobj.supplierAttendance.meetingAttendanceStatus == 'Accepted'}">
									<div class="popup_for_sure1 pad_all_15 attending">
										<div class="Attandance-div">
											<h3>Meeting Accepted</h3>
											<div class="Attandance-div-inner">
												<label>Name</label>
												<p class="m_name">${meetingobj.supplierAttendance.name}</p>
											</div>
											<div class="Attandance-div-inner">
												<label><spring:message code="supplier.designation" /></label>
												<p class="m_designation">${meetingobj.supplierAttendance.designation}</p>
											</div>
											<div class="Attandance-div-inner">
												<label>Mobile Number</label>
												<p class="m_number">${meetingobj.supplierAttendance.mobileNumber}</p>
											</div>
											<div class="Attandance-div-inner">
												<label>Remark</label>
												<p class="m_remark">${meetingobj.supplierAttendance.remarks}</p>
											</div>
										</div>
										<c:if test="${(event.status == 'ACTIVE' or event.status == 'CLOSED' or event.status == 'COMPLETE') && meetingobj.status == 'SCHEDULED'}">
											<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out notgoingInmeeting">Not Going</button>
										</c:if>
									</div>
								</c:if>
								<c:if test="${meetingobj.supplierAttendance != null && meetingobj.supplierAttendance.meetingAttendanceStatus == 'Rejected'}">
									<div class="popup_for_sure1 pad_all_15 notAttending">
										<div class="Attandance-div">
											<input type="hidden" name="meetingId" id="meetingId" value="" />
											<h3>Not Attending</h3>
											<div class="Attandance-div-inner">
												<label>Name</label>
												<p class="m_name">${meetingobj.supplierAttendance.name}</p>
											</div>
											<div class="Attandance-div-inner">
												<label>Reject Date</label>
												<fmt:formatDate var="rejectDateTime" value="${meetingobj.supplierAttendance.actionDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												<p class="m_actionDate">${rejectDateTime}</p>
											</div>
											<div class="Attandance-div-inner">
												<label>Reason</label>
												<p class="m_reason" pattern="dd/MMM/yyyy -HH:mm a">${meetingobj.supplierAttendance.rejectReason}</p>
											</div>
										</div>
										<c:if test="${(event.status == 'ACTIVE' or event.status == 'CLOSED' or event.status == 'COMPLETE') && meetingobj.status == 'SCHEDULED'}">
											<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out goingToAttendmeeting">Going To Attend</button>
										</c:if>
									</div>
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
</div>
</div>
</div>
</div>
</div>
</div>
<div class="modal fade" id="myModal-sure" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="supplierMeetingRegisterForm" method="post" cssClass="form-horizontal bordered-row">
				<div class="modal-header">
					<button type="button" class="close for-absulate" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">Attendance</h4>
				</div>
				<input type="hidden" name="eventId" id="eventId" value="${event.id}" /> <input type="hidden" name="meetingId" id="meetingId" value="" />
				<div class="modal-body">
					<div class="form-group">
						<input type="text" class="form-control" name="name" id="name" data-validation="required length" data-validation-length="1-160" placeholder="Name">
					</div>
					<div class="form-group">
						<input type="text" class="form-control" name="designation" id="designation" data-validation="required length" placeholder="Designation" data-validation-length="max128">
					</div>
					<div class="form-group">
						<input type="text" class="form-control" name="mobileNumber" id="mobileNumber" data-validation="required length number" placeholder="Mobile Number" data-validation-ignore="+ " data-validation-length="6-14">
					</div>
					<div class="form-group">
						<textarea class="form-control marg_top_15 txt-clr" placeholder="Remarks" id="remarks" name="remarks"></textarea>
					</div>
				</div>
				<div class="modal-footer pf-lt">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="button" id="saveMeetingAttendance" class="btn btn-info ph_btn_small">Save</button>
					<button type="button" data-dismiss="modal" class="btn btn-black ph_btn_small marg-left-10 hvr-pop hvr-rectangle-out1">Cancel</button>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" id="myModal-sureCancle" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<form id="supplierMeetingCancelForm" method="post" cssClass="form-horizontal bordered-row">
				<div class="modal-header">
					<button type="button" class="close for-absulate" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">Not Attending</h4>
				</div>
				<input type="hidden" name="eventId" id="eventId" value="${event.id}" /> <input type="hidden" name="meetingId" id="meetingId" value="" /> <input type="hidden" name="rejectDate" id="rejectDate" value="${meetingobj.supplierAttendance.actionDate}" />
				<div class="modal-body">
					<div class="form-group">
						<input type="text" class="form-control" name="rejectname" id="rejectname" data-validation="required length" data-validation-length="1-160" placeholder="Name">
					</div>
					<div class="form-group">
						<textarea class="form-control marg_top_15" placeholder="reason" data-validation="required length" data-validation-length="1-160" id="rejectReason" name="rejectReason"></textarea>
					</div>
				</div>
				<div class="modal-footer">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="button" id="saveMeetingAttendanceCancel" class="btn btn-info ph_btn_small">Save</button>
					<button type="button" data-dismiss="modal" class="btn btn-black ph_btn_small marg-left-10 hvr-pop hvr-rectangle-out1">Cancel</button>
				</div>
			</form>
		</div>
	</div>
</div>
<style>
#myModal-sure .form-group, #myModal-sureCancle .form-group {
	margin: 0 0 15px 0;
}

#myModal-sureCancle .modal-body {
	float: left;
	width: 100%;
}

.meeting_inner_main>label {
	float: left;
	width: auto;
	padding-right: 5px;
}

.pf-lt {
	padding-left: 98px !important;
}

.txt-clr {
	color: #2b2f33 !important;
}
</style>
<script>
	var eventStatus = '${event.status}';
	<c:if test="${eventPermissions.viewer and eventSupplier.submissionStatus !='REJECTED' and !eventSupplier.isRejectedAfterStart}">
		$(window).bind('load', function() {
					var allowedFields = '.tab-link > a,#eventFilter,#idDutchConsole,#idEnglishConsole';
					//var disableAnchers = ['#reloadMsg'];        
					disableFormFields(allowedFields);
				});
		</c:if>
	<c:if test="${eventSupplier.submissionStatus =='REJECTED' and eventSupplier.isRejectedAfterStart}">
		$(window).bind('load', function() {
			var allowedFields = '.tab-link > a';
			disableFormFields(allowedFields);
			
			$('#eventFilter').parent().addClass("disabled");
		});
	</c:if>

	counterMeetings();
	setInterval(function() {
		counterMeetings();
	}, 60000);
	function counterMeetings() {
		$('.countDownTimer1').each(
				function() {
					var target_date = new Date($(this).attr('data-date')).getTime();
					var days, hours, minutes, seconds;
					var current_date = new Date().getTime();
					var seconds_left = (target_date - current_date) / 1000;
					days = parseInt(seconds_left / 86400);
					seconds_left = seconds_left % 86400;
					hours = parseInt(seconds_left / 3600);
					seconds_left = seconds_left % 3600;
					minutes = parseInt(seconds_left / 60);
					seconds = parseInt(seconds_left % 60);
					htmldata = '';
					if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
						htmldata = '<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + days + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + hours
								+ '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + minutes + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">' + seconds + '</span></span>';
					}
					$(this).html(htmldata);
				});
	}
</script>
<script>
	// set the date we're counting down to

	// get tag element
	//var countdown = document.getElementById('countdown');
	//alert(countdown);
	// update the tag with id "countdown" every 1 second
	counterMeetings();
	setInterval(function() {

		counterMeetings();

	}, 60000);
	function counterMeetings() {
		$('.countDownTimer').each(
				function() {

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
						htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes + '</span><span class="seconds"><b>Seconds</b><br>'
								+ seconds + '</span>';
					}//$('#countdown').html(htmldata);
					$(this).html(htmldata);

				});
	}

	function counterMeetings() {
		$('#countdown').each(
				function() {

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
						htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes + '</span><span class="seconds"><b>Seconds</b><br>'
								+ seconds + '</span>';
					}//$('#countdown').html(htmldata);
					$(this).html(htmldata);

				});
	}
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierSubmissionEvent.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});

	
</script>
<style>
.disableCq {
	pointer-events: none !important;
}
</style>