\<%@page import="com.privasia.procurehere.core.enums.RfxTypes"%>
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
<spring:message var="supplierRfxTeamMemberDesk" code="application.supplier.rfx.team.members" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxTeamMemberDesk}] });
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
				var redirectUrl = getContextPath() + "/supplier/viewSupplierTeam/${eventType}/${event.id}";
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
			<jsp:include page="supplierSubmissionHeader.jsp" />
			<div class="tab-main-inner pad_all_15">
				<div id="tab-4" class="tab-content">
					<jsp:include page="supplierTeamMembers.jsp" />
				</div>
			</div>
		</div>
	</div>
</div>
</div>
</div>
<script>
<c:if test="${eventPermissions.viewer and eventSupplier.submissionStatus !='REJECTED' and !eventSupplier.isRejectedAfterStart}">
	$(window).bind('load', function() {
		var allowedFields = '.tab-link > a,#idDutchConsole,#idEnglishConsole';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${eventSupplier.submissionStatus =='REJECTED' and eventSupplier.isRejectedAfterStart}">
		$(window).bind('load', function() {
			var allowedFields = '.tab-link > a';
			//var disableAnchers = ['#reloadMsg'];        
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
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierSubmissionEvent.js?1"/>"></script>
