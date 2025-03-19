<%@page import="com.privasia.procurehere.core.enums.RfxTypes"%>
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
				var redirectUrl = getContextPath() + "/supplier/viewSupplierDocument/${eventType}/${event.id}";
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
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="supplierSubmissionHeader.jsp" />
			<c:if
				test="${!(eventSupplier.submissionStatus != 'INVITED'  and ((eventType != 'RFA' and today ge event.eventStart) or eventType == 'RFA'))}">
				<div class="tab-main-inner pad_all_15">
					<div id="tab-2" class="tab-content">
						<h3><spring:message code="supplierevent.bq.not.allow"/></h3>
					</div>
				</div>
			</c:if>
			<c:if
				test="${eventSupplier.submissionStatus != 'INVITED'  and ((eventType != 'RFA' and today ge event.eventStart) or eventType == 'RFA')}">
				<div class="tab-main-inner pad_all_15">
					<div id="tab-2" class="tab-content">
						<h3><spring:message code="eventdescription.document.label" /></h3>
						<div class="Invited-Supplier-List dashboard-main">
							<div class="Invited-Supplier-List-table add-supplier id-list">
								<div class="ph_tabel_wrapper">
									<div class="main_table_wrapper ph_table_border payment marg-bottom-20 document-table  mega">
										<table class="table ph_table border-none header documents-page table-1" width="100%" border="0" cellspacing="0" cellpadding="0">
											<thead>
												<tr>
													<!-- <th align="center" class=" for-left width_50 fix-50">
													<div class="checkbox checkbox-info">
														<label> <input type="checkbox" id="inlineCheckbox114" class="custom-checkbox parentCheckbox">
														</label>
													</div>
												</th> -->
													<th class="align-center width_100 width_100_fix" >&nbsp;</th>
													<th class="align-center  for-left width_200 width_200_fix"><spring:message code="application.name"/></th>
													<th class=" align-center width_300 fix_300"><spring:message code="application.description"/></th>
													<th class=" align-center width_200 width_200_fix"><spring:message code="event.document.publishdate"/></th>
													<th class=" align-center width_200 width_200_fix"><spring:message code="application.type"/></th>
													<th class=" align-center width_200 width_200_fix"><spring:message code="eventsummary.listdocuments.size"/></th>
												</tr>
											</thead>
										</table>
										<table class="ph_table table border-none tab_trans timezone induslist documents-page table-2" width="100%" border="0" cellspacing="0"
											cellpadding="0">
											<tbody class="catecontent">
											<c:if test="event.eventEnd gt "></c:if>
												<c:forEach var="docs" items="${eventDocs}">
												<c:if test="${docs.internal == false}">
													<tr>
														<td class="align-center width_100 width_100_fix" >
															<%-- <div class="checkbox checkbox-info">
																<label>
																	<input type="checkbox" id="inlineCheckbox114" class="custom-checkbox childCheckbox" data-doc-id="9" value="${docs.id}">
																</label>
															</div> --%> <input type="hidden" name="docsId" value="${docs.id}"> <form:form method="GET">
																<c:url var="download" value="/supplier/downloadSupplierDocument/${eventType}/${docs.id}" />
																<a href="${download}" style="background: none;" data-placement="top" title='<spring:message code="tooltip.download" />'> <img
																	src="${pageContext.request.contextPath}/resources/images/download.png" alt="download">
																</a>
															</form:form>
														</td>
														<td class="width_200 width_200_fix">${docs.fileName}</a>
														</td>
														<td class="align-center width_300 fix_300">${docs.description}</td>
														<td class="align-center width_200 width_200_fix"><fmt:formatDate value="${docs.uploadDate}" pattern="dd/MM/yyyy hh:mm a"
																timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
														<td class="align-center width_200 width_200_fix">${docs.credContentType}</td>
														<td class="align-center width_200 width_200_fix"><fmt:formatNumber type="number" maxFractionDigits="1" value="${docs.fileSizeInKb}" />
															KB</td>
													</tr>
													</c:if>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
						<%-- <form:form method="post" action="${pageContext.request.contextPath}/supplier/downloadSupplierDocument/${eventType}">
							<input type="hidden" name="docsId" id="docsId" value="">
							<button class="btn disabled btn-info hvr-pop hvr-rectangle-out ph_btn_midium" id="downloadDoc">Download</button>
						</form:form> --%>
					</div>
				</div>
			</c:if>
		</div>
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
						htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes + '</span><span class="seconds"><b>Seconds</b><br>'
								+ seconds + '</span>';
					}//$('#countdown').html(htmldata);
					$(this).html(htmldata);

				});
	}
</script>
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script type="text/javascript">
	$('.table-1 .custom-checkbox').on('change', function() {
		var check = this.checked;
		$(".table-2 [type=checkbox]").each(function() {
			$(".table-2 [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
		});
		enableDownload();
	});

	$('.table-2 [type=checkbox]').on('change', function() {
		var total = $(".table-2 [type=checkbox]").length;
		var checked = $(".table-2 .checker .checked").length;
		var firstObj = $('.table-1 .custom-checkbox');
		if (checked == total) {
			firstObj.prop('checked', true);
		} else {
			firstObj.prop('checked', false);
		}
		enableDownload();
		$.uniform.update(firstObj);
	});

	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
	function enableDownload() {
		if ($('.table-2 [type=checkbox]:checked').length > 0) {
			$('#downloadDoc').removeClass('disabled');
		} else {
			$('#downloadDoc').addClass('disabled');
		}
	}
</script>
<script>
	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierSubmissionEvent.js?1"/>"></script>
