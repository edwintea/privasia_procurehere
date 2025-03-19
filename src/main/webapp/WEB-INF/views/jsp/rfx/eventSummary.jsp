<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxCreatingSummaryDesk" code="application.rfx.create.summary" />
<spring:message var="rfxTeamMembersDesk" code="application.rfx.team.members" />
<spring:message var="rfxApprovalDesk" code="application.rfx.approvals" />
<spring:message var="rfxDefineApprovalDesk" code="application.rfx.define.approvals" />
<spring:message var="rfxMeetingAttendanceDesk" code="application.rfx.summary.meeting.attendance" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingSummary}, ${rfxTeamMembersDesk}, ${rfxApprovalDesk}, ${rfxDefineApprovalDesk}, ${rfxMeetingAttendanceDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a id="dashboard" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active">${eventType.value} <spring:message code="application.summary" /></li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head">${eventType.value} <spring:message code="application.summary" /></h2>
				<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${event.status}</h2>
			</div>
			<!-- page title block -->
			<div class="clear"/>
			<jsp:include page="eventHeader.jsp" />
			<div class="clear"/>
			<jsp:include page="summaryDetails.jsp" />
		</div>
	</div>
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
							htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes
									+ '</span><span class="seconds"><b>Seconds</b><br>' + seconds + '</span>';
						}//$('#countdown').html(htmldata);
						$(this).html(htmldata);

					});
		}
	</script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
	<script type="text/javascript">
		/* Datepicker bootstrap */

		$(function() {
			"use strict";
			var nowTemp = new Date();
			var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

			$('.bootstrap-datepicker').bsdatepicker({
				format : 'dd/mm/yyyy',
				minDate : now,
				onRender : function(date) {
					return date.valueOf() < now.valueOf() ? 'disabled' : '';
				}

			}).on('changeDate', function(e) {
				$(this).blur();
				//$(this).val()
				$("#auctionResumeDateTime").val($(this).val());

				$(this).bsdatepicker('hide');
			});

			$('.timepicker-example').timepicker();
			$('.timepicker-example').change(function(e) {

				$("#auctionResumeTime").val($(this).val());
			});
			
			var hours = nowTemp.getHours();
			  var minutes = nowTemp.getMinutes();
			  var ampm = hours >= 12 ? 'PM' : 'AM';
			  hours = hours % 12;
			  hours = hours ? hours : 12; // the hour '0' should be '12'
			  minutes = minutes < 10 ? '0'+(minutes+1) : (minutes+1);
			  var onlyTime = hours + ':' + (minutes);
			  var strTime = onlyTime + ' ' + ampm;
			$("#auctionResumeTime").val(strTime);
			$("#auctionResumeDateTime").val($.datepicker.formatDate('dd/mm/yy', now));
		});
	</script>