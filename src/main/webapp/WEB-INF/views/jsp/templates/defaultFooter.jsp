<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="year" value="${now}" pattern="yyyy" />
<div class="clearfix"></div>
</div>
<div class="pset_footer">&copy; ${year} <spring:message code="footer.all.rights.reserved"/></div>
</div>

<style>

.redText {
	color: #ff7f7f !important;
}

</style>

<div class="modal fade" tabindex='-1' id="securitySession" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<!-- <button type="button" class="close" data-dismiss="modal">&times;</button> -->
				<h4 class="modal-title"><spring:message code="footer.session.about.expires"/></h4>
			</div>
			<div class="modal-body">
				<p><spring:message code="footer.auto.login"/></p>
				<p id="sessionCountdown" class="timerSection">
					<span id="stimeMinuts" class="timeMinuts">00</span>
					<span>:</span>
					<span id="stimeSeconds" class="timeSeconds">00</span>
				</p>
				<div style="display: flex; flex-direction: row; gap: 10px; align-items: center; justify-content: center">
					<button type="button" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out extandsession" onclick="javascript:resetExpiry();"><spring:message code="footer.extend.session.button"/></button>
					<button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium expiresession" onclick="javascript:logout();"><spring:message code="footer.logout.button"/></button>
				</div>
			</div>
			<!-- div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div -->
			<div id="sessiontimer"></div>
		</div>
	</div>
</div>
<sec:authorize access="hasRole('OWNER')" var="owner" />
<sec:authorize access="hasRole('BUYER')" var="buyer" />
<sec:authorize access="hasRole('SUPPLIER')" var="supplier" />
<script src="<c:url value="/resources/js/countdown.js"/>" type="text/javascript" charset="utf-8"></script>
<!--
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery.countdown.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/jquery.countdown.pack.js"/>"></script> -->
<script>

	var sessionCountdown;
	var popUpCT;
	var sessionFinalDate;
	
	// Do not activate session expiry for Auction Console.
	if(window.location.href.indexOf("Console") == -1) {
		$(document).ready(function() {
			startExpiryWatch();
		});
	}
	
	function addZero(i) {
		if (i < 10) {
			i = "0" + i;
		}
		return i;
	}
	
	function addMinutes(date, minutes) {
	    return new Date(date.getTime() + minutes*60000);
	}

	function resetExpiry() {
		if(sessionCountdown !== undefined) {
			sessionCountdown.countdown('pause');
			sessionCountdown.countdown('stop');
		}
		$("#securitySession").modal("hide");
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$.ajax({
			url : getContextPath() + "${owner ? '/owner' : buyer ? '/buyer' : supplier ? '/supplier' : ''}/extendSession",
			async : false,
			type : "GET",
			beforeSend : function(xhr) {
				//$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				console.log('Session extended....', textStatus, data, request);
			},
			error : function(request, textStatus, errorThrown) {
				console.log('Error extending Session....');
			},
			complete : function() {
				startExpiryWatch();
			}
		});
	}
	
	function startExpiryWatch() {
		
		$("#stimeMinuts").removeClass('redText');
		$("#stimeSeconds").removeClass('redText');

		var nowTemp = new Date();
		var mnth = nowTemp.getMonth()+1;
		popUpCT = addMinutes(nowTemp, 23);
		var sessionFinal = addMinutes(nowTemp, 25);
		sessionFinalDate = sessionFinal.getFullYear()+"/"+addZero(sessionFinal.getMonth() + 1)+"/"+addZero(sessionFinal.getDate())+" "+addZero(sessionFinal.getHours())+":"+addZero(sessionFinal.getMinutes())+":"+addZero(sessionFinal.getSeconds());
		
		console.log('Starting session expiry check until ', sessionFinalDate, ' warning at ', popUpCT);

		// If the countdown is already running, then just reset it with new time.
		if(sessionCountdown !== undefined) {
			sessionCountdown.countdown(sessionFinalDate);
			sessionCountdown.countdown('start');
		} else {
			sessionCountdown = $("#securitySession").countdown(sessionFinalDate, {precision: 1000})
			  .on('update.countdown', function(event) {
				  //console.log(new Date(event.timeStamp));
					if(event.timeStamp > popUpCT) {
						if(!isNaN(event.strftime('%M'))){
							
							// check if last 30 secs are remining
							if(event.strftime('%M') === '00' && parseInt(event.strftime('%S')) < 30) {
								// Toggle red color - make it appear as blinking
								if(parseInt(event.strftime('%S')) % 2 == 0) {
									$("#stimeMinuts").removeClass('redText');
									$("#stimeSeconds").removeClass('redText');
								} else {
									$("#stimeMinuts").addClass('redText');
									$("#stimeSeconds").addClass('redText');
								}
							}
							
							$("#stimeMinuts").html(event.strftime('%M'));
							$("#stimeSeconds").html(event.strftime('%S'));
							$("#securitySession").modal("show");
							//$(this).modal({backdrop: 'static',keyboard: false});
						}
					}
		  	}).on('finish.countdown', function(event) {
		  		$("#securitySession").modal("hide");
				window.location.href = '${pageContext.request.contextPath}/logout';
		 	})
		}
	}

	function logout() {
		window.location.href = '${pageContext.request.contextPath}/logout';
	}
	

</script>