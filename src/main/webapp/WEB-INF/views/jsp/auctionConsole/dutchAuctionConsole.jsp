<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="readOnlyAdmin" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/assets/elements/featherlight.css"/>">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<sec:authorize access="hasRole('BUYER')">
	<spring:message var="rfxDutchFwdDesk" code="application.rfa.buyer.forward.dutch.console" />
	<spring:message var="rfxDutchRevDesk" code="application.rfa.buyer.reverse.dutch.console" />
	<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfxDutchFwdDesk}, ${rfxDutchRevDesk}] });
});
</script>
</sec:authorize>
<sec:authorize access="hasRole('SUPPLIER')">
	<spring:message var="supplierRfxDutchFwdDesk" code="application.rfa.supplier.forward.dutch.console" />
	<spring:message var="supplierRfxDutchRevDesk" code="application.rfa.supplier.reverse.dutch.console" />
	<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${supplierRfxDutchFwdDesk}, ${supplierRfxDutchRevDesk}] });
});
</script>
</sec:authorize>
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
				if (messageOutput.body == 'CLOSED') {
					showMessageOutput("Event has been closed.");
					
					window.location.href = getContextPath() + "/auction/dutchAuctionConsole/${event.id}";
				} else if (messageOutput.body == 'SUSPENDED') {
					window.location.href = getContextPath() + "/auction/dutchAuctionConsole/${event.id}";
				} else if (messageOutput.body == 'RESUME') {
					window.location.href = getContextPath() + "/auction/dutchAuctionConsole/${event.id}";
				}
			});
			
			stompClient.subscribe('/dutchAuctionData/${event.id}', function(messageOutput) {
				console.log("the responce : " + messageOutput.body);
				var obj = $.parseJSON(messageOutput.body);
				var i = obj['currentStep'];
				//var i = data.currentStepNo;
				if ($('*[data-timestamp="' + i + '"]').attr('class') != undefined) {
					//currentStep
					$('#currentStep').val(i);
					var offTop = $('*[data-timestamp="' + i + '"]').offset().top - 300;
					// $(document).scrollTop(offTop);

					$('.timestamp .active').hide();
					$('.timestamp .inactive').show();
					$('.timestamp').addClass('time-ammount-contant');
					$('.timestamp').removeClass('time-ammount-heading green-with-border').addClass('time-ammount-contant');
					$('*[data-timestamp="' + i + '"]').addClass('time-ammount-heading green-with-border').removeClass('time-ammount-contant').find('.active').show();
					$('*[data-timestamp="' + i + '"]').find('.inactive').hide();
					i++;
					$("#current_step").val(i);

				}
				var status = obj['eventStatus'];
				var myStatus = 'ACTIVE';
				if (status != myStatus) {
					//window.location.href = getContextPath() + "/auction/dutchAuctionConsole/" + eventId;
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
		var i = $("#load_current_step").val();
		console.log(i);
		//var i = data.currentStepNo;
		if ($('*[data-timestamp="' + i + '"]').attr('class') != undefined) {
			//currentStep
			$('#currentStep').val(i);
			var offTop = $('*[data-timestamp="' + i + '"]').offset().top - 300;
			// $(document).scrollTop(offTop);

			$('.timestamp .active').hide();
			$('.timestamp .inactive').show();
			$('.timestamp').addClass('time-ammount-contant');
			$('.timestamp').removeClass('time-ammount-heading green-with-border').addClass('time-ammount-contant');
			$('*[data-timestamp="' + i + '"]').addClass('time-ammount-heading green-with-border').removeClass('time-ammount-contant').find('.active').show();
			$('*[data-timestamp="' + i + '"]').find('.inactive').hide();
			i++;
			$("#current_step").val(i);

		}
	});
</script>



<div id="page-wrapper">
	<jsp:useBean id="today" class="java.util.Date" />
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<sec:authorize access="hasRole('BUYER')">
						<li>
							<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard"/></a>
						</li>
					</sec:authorize>
					<sec:authorize access="hasRole('SUPPLIER')">
						<li>
							<a id="dashboardLink" href="${pageContext.request.contextPath}/supplier/supplierDashboard"><spring:message code="application.dashboard"/></a>
						</li>
					</sec:authorize>
					<li class="active"><spring:message code="dutch.auctionhall.label"/></li>
				</ol>
				<div class="Section-title title_border gray-bg">
					<h2 class="trans-cap auction_hall_heading"><spring:message code="rfaevent.auction.hall"/></h2>
					<input type="hidden" value="${event.id}" id="eventId">
					<a href="#" class="pull-right" id="refreshEvent">
						<i class="fa fa-refresh fa-2x" aria-hidden="true"></i>
					</a>
					<!-- <button type="button" class="btn ph_btn_small hvr-pop hvr-rectangle-out del_inv pull-right" onclick="javascript:window.location.reload();" id="idRefreshPage"><i class="fa fa-refresh fa-2x" aria-hidden="true"></i></button> -->
				</div>
				<input type="hidden" id="auctionId" value="${auctionRules.id}">
				<%-- <input type="hidden" id="currentStep" value="${auctionRules.dutchAuctionCurrentStep}"> --%>
				<input type="hidden" id="current_step" value="1"> <input type="hidden" id="eventStatus" value="${event.status}"> <input type="hidden" id="startPolling"
					value="${(event.status == 'ACTIVE' and today ge event.eventStart) ? 'true' : 'false'}">
					<input type="hidden" id="load_current_step" value="${auctionRules.dutchAuctionCurrentStep}">
				<div class="ports-tital-heading marg-bottom-10">
					<div class="row">
						<div class="col-md-8 col-xs-8">
							<div class="ports-tital-heading li-32"><spring:message code="dutch.auction.case"/> : ${event.eventName}</div>
						</div>
						<sec:authorize access="hasRole('BUYER')">
							<c:if test="${event.status eq 'ACTIVE' and (eventPermissons.owner or isAdmin)}">
								<div class="col-md-4 col-xs-4">
									<a href="#confirmSuspendEvent" role="button" class="btn btn-alt btn-hover btn-danger hvr-pop del_inv pull-right" data-toggle="modal" id="idSuspendEvent1"><spring:message code="dutch.auction.suspend"/></a>
									<!-- <button type="button" class="btn btn-alt btn-hover btn-danger hvr-pop del_inv pull-right" id="idSuspendEvent">Suspend</button> -->
								</div>
							</c:if>
						</sec:authorize>
					</div>
				</div>
				<div id="timer-accord" class="Invited-Supplier-List white-bg-with-arrow small-accordin-tab">
					<div class="row">
						<div class="col-md-7 col-sm-7">
							<c:if test="${event.status eq 'ACTIVE'}">
								<c:if test="${empty event.auctionResumeDateTime}">
									<c:if test="${today lt eventStart}">
										<div class="meeting2-heading-new">
											<label><spring:message code="auctionhall.start.date"/> &amp; <spring:message code="application.time"/></label>
											<h3 class="date-time-show fixh3">
												<span>
													<fmt:formatDate value="${eventStart}" pattern="dd/MMM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												</span>
											</h3>
										</div>
									</c:if>
									<c:if test="${today gt eventStart and today le eventEnd}">
										<div class="meeting2-heading-new">
											<label><spring:message code="auctionhall.end.date"/> &amp; <spring:message code="application.time"/></label>
											<h3 class="date-time-show fixh3">
												<span>
													<fmt:formatDate value="${eventEnd}" pattern="dd/MMM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												</span>
											</h3>
										</div>
									</c:if>
								</c:if>
								<c:if test="${not empty event.auctionResumeDateTime}">
									<c:if test="${today lt event.auctionResumeDateTime}">
										<div class="meeting2-heading-new">
											<label>Auction Resume Date &amp; Time</label>
											<h3 class="date-time-show fixh3">
												<span>
													<fmt:formatDate value="${event.auctionResumeDateTime}" pattern="dd/MMM/yyyy -hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												</span>
											</h3>
										</div>
									</c:if>
									<c:if test="${today gt event.auctionResumeDateTime}">
										<div class="meeting2-heading-new">
											<label><spring:message code="auctionhall.end.date"/> &amp; <spring:message code="application.time"/></label>
											<h3 class="date-time-show fixh3">
												<span>
													<fmt:formatDate value="${eventEnd}" pattern="dd/MMM/yyyy -hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												</span>
											</h3>
										</div>
									</c:if>
								</c:if>
							</c:if>
							<c:if test="${event.status eq 'CLOSED'  or event.status eq 'COMPLETE' or event.status eq 'FINISHED' or event.status eq 'SUSPENDED'}">
								<div class="meeting2-heading-new">
									<label><spring:message code="auctionhall.start.date"/> &amp; <spring:message code="application.time"/></label>
									<h3 class="date-time-show fixh3">
										<span>
											<fmt:formatDate value="${eventStart}" pattern="dd/MMM/yyyy -hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</div>
							</c:if>
						</div>
						<div class="col-md-5 col-sm-5">
							<c:if test="${event.status eq 'ACTIVE'}">
								<%-- <c:if test="${event.status eq 'ACTIVE'}"> --%>
								<c:if test="${not empty event.auctionResumeDateTime}">
									<div class="counter" id="counter">
										<h3>Time Left To${today lt event.auctionResumeDateTime ? 'Start' : 'End'}</h3>
										<div class="main-example">
											<c:if test="${today lt event.auctionResumeDateTime}">
												<fmt:formatDate value="${event.auctionResumeDateTime}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<c:if test="${today ge event.auctionResumeDateTime}">
												<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<div class="countdown-container" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/auction/dutchAuctionConsole/${event.id}" id="main-example1"
												data-date="${counterDate}"></div>
										</div>
									</div>
								</c:if>
								<c:if test="${empty event.auctionResumeDateTime}">
									<div class="counter" id="counter">
										<h3>Time Left To ${today lt event.eventStart ? 'Start' : 'End'}</h3>
										<div class="main-example">
											<c:if test="${today lt event.eventStart}">
												<fmt:formatDate value="${event.eventStart}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<c:if test="${today ge event.eventStart}">
												<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<div class="countdown-container" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/auction/dutchAuctionConsole/${event.id}" id="main-example1"
												data-date="${counterDate}"></div>
										</div>
									</div>
								</c:if>
							</c:if>
							<!-- /#Main Div -->
							<%-- </c:if> --%>
							<c:if test="${event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED'}">
								<div class="meeting2-heading-new">
									<label>Auction Complete Date &amp; <spring:message code="application.time"/></label>
									<h3 class="date-time-show fixh3">
										<span>
											<fmt:formatDate value="${event.auctionComplitationTime}" pattern="dd/MMM/yyyy -hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</div>
							</c:if>
						</div>
						<!-- /.Columns Div -->
					</div>
					<!-- /.Row Div -->
				</div>
				<div class="white-bg border-all-side float-left width-100 pad_all_10">
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="tag-line">
								<h2>${eventType}:${event.eventName}</h2>
								<p><spring:message code="prtemplate.case.id"/> :${event.eventId}</p>
								<p><spring:message code="application.eventowner"/> :${event.eventOwner.name}, ${event.eventOwner.communicationEmail}</p>
								<p><spring:message code="rfx.auction.type.label"/> :${event.auctionType.value}</p>
							</div>
						</div>
					</div>
				</div>

				<!--Status for  -->
				<sec:authorize access="hasRole('BUYER')">
					<jsp:include page="dutchActiveSupplierList.jsp"></jsp:include>
				</sec:authorize>

				<div class="sub-price">
					<c:if test="${empty event.auctionResumeDateTime}">
						<c:set value="${event.eventStart}" var="auctionDate"></c:set>
					</c:if>
					<c:if test="${not empty event.auctionResumeDateTime}">
						<c:set value="${event.auctionResumeDateTime}" var="auctionDate"></c:set>
					</c:if>
					<c:if test="${today ge auctionDate}">
						<div class="Invited-Supplier-List white-bg pad_all_15">
							<c:if test="${event.status eq 'ACTIVE'}">
								<div class="time-ammount">
									<div class="time-ammount-inner">
										<div class="time-ammount-heading blue-with-border">
											<label class="top-side-arrow"><spring:message code="dutch.auction.time.case"/></label> <label><spring:message code="dutch.auction.amount"/></label>
										</div>
										<c:forEach items="${dutchAuctionPojoList}" var="dutchAuctionPojo" varStatus="loop">
											<div data-timestamp="${loop.index+1}" class="timestamp time-ammount-contant">
												<div class="inactive">
													<div class="time-ammount-contant-inner">${dutchAuctionPojo.currentSlotTime}</div>
													<div class="time-ammount-contant-inner">${currency}&nbsp;<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${dutchAuctionPojo.currentStepAmount}" />
													</div>
												</div>
												<div class="active">
													<div class="slote-left"><spring:message code="dutch.auction.current.time.slot"/></div>
													<label class="for-top-arrow">${dutchAuctionPojo.currentSlotTime}</label> <label class="dark">${currency} <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}"
															value="${dutchAuctionPojo.currentStepAmount}" />
													</label>
												</div>
											</div>
										</c:forEach>
									</div>
								</div>
							</c:if>
							<c:if test="${event.status eq 'COMPLETE' or event.status eq 'CLOSED' or event.status eq 'FINISHED'}">
								<c:if test="${not empty winingSupplier}">
									<sec:authorize access="hasRole('SUPPLIER')">
										<c:choose>
											<c:when test="${winingSupplier.id eq loggedInUserTenantId}">
												<div class="time-ammount">
													<div class="time-ammount-inner">
														<div class="time-ammount-heading blue-with-border">
															<label class="top-side-arrow"><spring:message code="buyercreation.company"/></label> <label><spring:message code="dutch.auction.accepted.amount"/></label>
														</div>
														<div class="time-ammount-contant-inner">${winingSupplier.companyName}</div>
														<div class="time-ammount-contant-inner">
															<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.winningPrice}" />
														</div>
													</div>
												</div>
											</c:when>
											<c:otherwise>
												<div class="time-ammount">
													<div class="time-ammount-inner">
														<div class="time-ammount-heading blue-with-border"></div>
														<div class="time-ammount-contant-inner" style="width: 100% !important;"><spring:message code="dutch.auction.concluded"/></div>
													</div>
												</div>
											</c:otherwise>
										</c:choose>
									</sec:authorize>
								</c:if>
								<c:if test="${empty winingSupplier}">
									<div class="time-ammount">
										<div class="time-ammount-inner">
											<div class="time-ammount-heading blue-with-border"></div>
											<div class="time-ammount-contant-inner" style="width: 100% !important;"><spring:message code="dutch.auction.concluded"/></div>
										</div>
									</div>
								</c:if>
								<sec:authorize access="hasRole('BUYER')">
									<c:if test="${not empty winingSupplier}">
										<div class="time-ammount">
											<div class="time-ammount-inner">
												<div class="time-ammount-heading blue-with-border">
													<label class="top-side-arrow"><spring:message code="buyercreation.company"/></label> <label><spring:message code="dutch.auction.accepted.amount"/></label>
												</div>
												<div class="time-ammount-contant-inner">${winingSupplier.companyName}</div>
												<div class="time-ammount-contant-inner">
													<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.winningPrice}" />
												</div>
											</div>
										</div>
									</c:if>
									<c:if test="${empty winingSupplier}">
										<div class="time-ammount">
											<div class="time-ammount-inner">
												<div class="time-ammount-heading blue-with-border"></div>
												<div class="time-ammount-contant-inner" style="width: 100% !important;"><spring:message code="dutch.auction.no.bid"/></div>
											</div>
										</div>
									</c:if>
								</sec:authorize>
							</c:if>
							<c:if test="${event.status eq 'SUSPENDED'}">
								<div class="time-ammount">
									<div class="time-ammount-inner">
										<div class="time-ammount-heading blue-with-border"></div>
										<div class="time-ammount-contant-inner" style="width: 100% !important;">Event Suspended</div>
									</div>
								</div>
							</c:if>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</div>
<sec:authorize access="hasRole('SUPPLIER')">
	<c:if test="${event.status eq 'ACTIVE'}">
		<c:if test="${today ge eventStart}">
			<div class="side-sticky-offer">
				<i class="fa fa-money" aria-hidden="true"></i> <label class="cu-off"><spring:message code="dutch.auction.offer.price"/></label>
				<div class="cur-off">
					<label><spring:message code="dutch.click.btn.takebid"/></label>
					<button class="btn btn-info ph_btn_extra_small hvr-pop hvr-rectangle-out" id="idTakeBid" data-toggle="modal" data-target="#myModal1"><spring:message code="dutch.auction.take.bid"/></button>
				</div>
			</div>
		</c:if>
	</c:if>
</sec:authorize>
<!--popup-->
<div class="modal fade" id="myModal1" role="dialog">
	<div class="modal-dialog for-delete-all">
		<!-- Modal content-->
		<form action="${pageContext.request.contextPath}/auction/submitDutchAuction/${auctionRules.id}" method="post">
			<div class="modal-content">
				<input type="hidden" name="currentStepNo" id="currentStep" value="${auctionRules.dutchAuctionCurrentStep}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<button type="button" class="close for-absulate" data-dismiss="modal">&times;</button>
				<div class="modal-body"><spring:message code="dutch.auction.sure.bid"/></div>
				<div class="modal-footer">
					<input type="submit" class="btn btn-info  ph_btn_small hvr-pop hvr-rectangle-out del_inv pull-left" value="Yes" />
					<button type="button" class="btn btn-black ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.no2" /></button>
				</div>
			</div>
		</form>
	</div>
</div>
<div class="modal fade" id="confirmSuspendEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="eventsummary.confirm.suspension"/></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal" onclick="javascript:$('#idSuspendForm').get(0).reset();">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="rfaevent.sure.want.suspended"/></label>
			</div>
			<form:form id="idSuspendForm" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/suspendEvent" method="post">
				<form:hidden path="id" />
				<form:hidden path="eventName" />
				<form:hidden path="referanceNumber" />
				<form:hidden path="auctionType" />
				<input type="radio" name="auctionConsole" value="1" checked="checked" style="display: none;">
				<div class="form-group col-md-6">
					<spring:message code="auction.remarks.placeholder"  var="remark"/>
					<form:textarea path="suspendRemarks" class="form-control width-100 suspendRemarks" data-validation="required length" data-validation-length="max500" placeholder="${remark}" />
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="suspendEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"><spring:message code="application.yes" /></form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="javascript:$('#idSuspendForm').get(0).reset();"><spring:message code="application.no2" /></button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/view/auctionConsole.js"/>"></script>
<style type="text/css">
.small-accordin-tab .meeting2-heading-new label {
	width: 100%;
}

.fa.fa-money {
	color: #fff;
	font-size: 25px;
	margin: 10px;
	float: left;
}

.cu-off {
	float: left;
	color: #fff !important;
	line-height: 43px;
	cursor: pointer;
}

.cur-off input {
	font-size: 14px;
	height: 38px;
	margin: 0 auto;
	text-align: center;
	width: 65%;
}

.side-sticky-offer {
	background: rgba(0, 150, 213, 1) none repeat scroll 0 0;
	border-radius: 0 0 5px 5px;
	cursor: pointer;
	height: 43px;
	overflow: hidden;
	position: fixed;
	right: -50px;
	top: 300px;
	width: 140px;
	z-index: 999;
	/* Safari */
	-webkit-transform: rotate(90deg);
	/* Firefox */
	-moz-transform: rotate(90deg);
	/* IE */
	-ms-transform: rotate(90deg);
	/* Opera */
	-o-transform: rotate(90deg);
	/* Internet Explorer */
	filter: progid: DXImageTransform.Microsoft.BasicImage(rotation=3);
}

.side-sticky-offer .close-sticky {
	background: #0096d5 none repeat scroll 0 0;
	border-radius: 50%;
	color: #fff !important;
	display: none;
	font-size: 13px;
	height: 20px;
	left: -5px;
	position: absolute;
	text-align: center;
	top: -5px;
	width: 20px;
}

.side-sticky-offer.offer-open .close-sticky {
	display: block;
}

.side-sticky-offer.offer-open {
	background: #fafafa none repeat scroll 0 0;
	box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
	height: auto;
	width: auto;
	overflow: visible;
	-webkit-transform: rotate(0deg);
	/* Firefox */
	-moz-transform: rotate(0deg);
	/* IE */
	-ms-transform: rotate(0deg);
	/* Opera */
	-o-transform: rotate(0deg);
	/* Internet Explorer */
	filter: progid: DXImageTransform.Microsoft.BasicImage(rotation=0);
	right: 0;
}

.side-sticky-offer.offer-open i, .side-sticky-offer.offer-open .cu-off {
	display: none;
}

.side-sticky-offer i img {
	float: left;
	width: auto;
}

.ringit-icon {
	float: left;
	margin: 14px 8px 0 10px;
	width: 30px;
}

.side-sticky-offer.offer-open .ringit-icon {
	display: none
}

#countdown1 span.seconds {
	display: inline-table !important;
}

#countdown1 {
	width: 100%;
}

.timestamp .active {
	display: none
}

.time-ammount-inner label.dark {
	color: #000000 !important;
}

@media only screen and (max-width: 980px) {
	.side-sticky-offer.offer-open {
		right: 36%;
		position: fixed;
		top: 200px;
	}
}

@media only screen and (max-width: 768px) {
	.side-sticky-offer.offer-open {
		right: 36%;
		position: fixed;
		top: 200px;
		width: 250px;
	}
}

@media screen and (max-width: 600px) {
	.side-sticky-offer.offer-open {
		right: 0;
		position: fixed;
		top: 0;
		width: 100%;
	}
	.slote-left {
		display: none;
	}
}

.fa-refresh:hover {
	cursor: pointer;
}

.fa-refresh {
	color: #fff;
	padding-top: 5px;
}

#tableList td {
	text-align: center;
}

.disableRow {
	background-color: red;
	color: black;
	margin-top: 10px;
	visibility: hidden;
}
</style>
<script>

$(document).ready(function() {
	
	$('#suspendEvent').click(function() {
		var suspendRemarks = $('.suspendRemarks').val();
		if (suspendRemarks != '') {
		$(this).addClass('disabled');
		}
	});
	
});
	<c:if test="${eventPermissions.viewer or readOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.tab-link > a, #refreshEvent, #dashboardLink';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
</script>
<script src="<c:url value="/resources/assets/js-core/featherlight.min.js"/>" type="text/javascript" charset="utf-8"></script>