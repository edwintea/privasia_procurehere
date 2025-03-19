<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<c:set var="viewMode" value="true" scope="request" />
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
			stompClient.subscribe('/auctionTopic/SPSUSPEND', function(messageOutput) {
				console.log("the responce : " + messageOutput.body);
 				if (messageOutput.body == '${spForm.id}') {
					window.location.href = window.location.href;
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
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="clear"></div>
<%-- 			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" /> --%>
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active">Supplier Performance form <spring:message code="application.summary" /></li>
			</ol>
			<jsp:include page="spFormEvaluatorSummaryDetails.jsp" />
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
