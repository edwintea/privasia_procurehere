<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:message var="rfxbidHistoryDesk" code="application.rfa.bid.history" />
<spring:message var="rfxEnglishFwdConsoleDesk" code="application.rfa.buyer.forward.english.console" />
<spring:message var="rfxEnglishRevConsoleDesk" code="application.rfa.buyer.reverse.english.console" />
<spring:message var="rfxSealedFwdConsoleDesk" code="application.rfa.buyer.forward.sealed.bid.console" />
<spring:message var="rfxSealedRevConsoleDesk" code="application.rfa.buyer.reverse.sealed.bid.console" />
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfxbidHistoryDesk}, ${rfxEnglishFwdConsoleDesk}, ${rfxEnglishRevConsoleDesk}, ${rfxSealedFwdConsoleDesk}, ${rfxSealedRevConsoleDesk}] });
});
</script>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);

		$(function() {
			"use strict";
			//$('.timepicker-example').timepicker();
		});

	});
</script>
<script>
	$(document).ready(function() {
		$(".for-toggle").click(function() {
			$(".add-contact-contant").toggle();
		});
	});
</script>
<script>
	$(document).ready(function() {

		$('ul.tabs li').click(function() {
			var tab_id = $(this).attr('data-tab');

			$('ul.tabs li').removeClass('current');
			$('.tab-content').removeClass('current');
			
			$(this).addClass('current');
			$("#" + tab_id).addClass('current');

/* 			if(tab_id == 'tab-1'){
				$('.bqBiddingValue').show();
				$('.auctionbids').hide();
			} else {
				$('.bqBiddingValue').hide();
				$('.auctionbids').show();
			}
 */
		})

	})
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
				showMessageOutput(messageOutput.body);
				console.log(messageOutput.body);
				if (messageOutput.body == 'CLOSED') {
					//showMessageOutput("Event has been closed.");
					window.location.href = getContextPath() + "/buyer/englishAuctionConsole/${event.id}";
				} else if (messageOutput.body == 'SUSPENDED') {
					console.log("messageOutput.body :" + messageOutput.body);
					window.location.href = getContextPath() + "/buyer/englishAuctionConsole/${event.id}";
				} else if (messageOutput.body == 'RESUME') {
					window.location.href = getContextPath() + "/buyer/englishAuctionConsole/${event.id}";
				}
			});
			
			stompClient.subscribe('/bidHistoryBuyerSide/${event.id}', function(
					messageOutput) {
						console.log("the responce message : ");
						<c:if test="${event.status == 'ACTIVE' and (!eventSupplier.disqualify)}">
						getBidHistoryOfSuppliers('${!empty sessionScope["graphArrangeBy"] ? sessionScope["graphArrangeBy"] : "Time"}');
						</c:if>
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
<style>
.notDisplay {
	opacity: 0;
	position: absolute;
	width: 100%;
}

#legend-container>table>tbody>tr {
	display: inline-block !important;
	margin-right: 10px;
	margin-top: 15px;
}

#legend-container {
	float: left;
	margin-bottom: 10px;
}

/* .flot_div_rotate {
margin-top:25px;
-webkit-transform: rotate(-90deg); 
-moz-transform: rotate(-90deg);	 
-ms-transform: rotate(-90deg);
-o-transform: rotate(-90deg);

} */
.flot-text>.flot-x-axis>div {
	
}

/* #data-example-1>.axisLabels {
	top: 55px !important;
} */
</style>

<div id="page-content">
	<jsp:useBean id="today" class="java.util.Date" />
	<input type="hidden" value="${event.id}" id="eventId"> <input type="hidden" id="eventDecimal" value="${event.decimal}"> <input type="hidden" value="${bqId}" id="bqId">
	<div class="container">
		<div class="clear"></div>
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard"/></a></li>
			<li class="active"><spring:message code="rfaevent.auction.hall"/></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap supplier"><spring:message code="rfaevent.auction.label"/>: ${event.eventName}</h2>
			<h2 class="trans-cap pull-right"><spring:message code="application.status" />: ${event.status}</h2>
			<sec:authorize access="hasRole('BUYER')">
				<c:if test="${event.status eq 'ACTIVE'}">
					<div class="pull-right marg-right-10">
						<!-- <button type="button" class="btn btn-alt btn-hover btn-danger hvr-pop del_inv" id="idSuspendEvent">Suspend</button> -->
						<a href="#confirmSuspendEvent" role="button" class="btn btn-alt btn-hover btn-danger hvr-pop del_inv" data-toggle="modal" id="idSuspendEvent1"><spring:message code="eventsummary.button.event.suspend"/></a>
					</div>
				</c:if>
				<c:if test="${(event.status eq 'ACTIVE' and event.timeExtensionType eq 'MANUALLY' and today ge event.eventStart and eventPermissions.owner) and (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')}">
					<div class="pull-right marg-right-10">
						<a href="#timeExtension" role="button" class="btn btn-alt btn-hover btn-primary hvr-pop del_inv" data-toggle="modal"><spring:message code="eventsummary.eventdetail.time.extension"/></a>
					</div>
				</c:if>
			</sec:authorize>
		</div>



		<div id="timer-accord"
			class="Invited-Supplier-List white-bg-with-arrow ${(not empty event.auctionResumeDateTime && today lt event.auctionResumeDateTime) || (empty event.auctionResumeDateTime && today lt event.eventStart) ? '' : 'small-accordin-tab' }">
			<div class="row">
				<div class="col-md-7 col-sm-7">
					<c:if test="${event.status ne 'CLOSED' and event.status ne 'COMPLETE' and event.status ne 'FINISHED' and event.status ne 'SUSPENDED'}">
						<c:if test="${empty event.auctionResumeDateTime}">
							<c:if test="${today lt event.eventStart}">
								<div class="meeting2-heading-new">
									<label><spring:message code="auctionhall.start.date"/> &amp; <spring:message code="application.time"/></label>
									<br />
									<h3 class="date-time-show fixh3 pull-left">
										<span>
											<fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</div>
							</c:if>
							<c:if test="${today gt event.eventStart}">
								<div class="meeting2-heading-new">
									<label><spring:message code="auctionhall.end.date"/> &amp; <spring:message code="application.time"/></label>
									<br />
									<h3 class="date-time-show fixh3 pull-left">
										<span>
											<fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</div>
							</c:if>
						</c:if>
						<c:if test="${not empty event.auctionResumeDateTime}">
							<c:if test="${today lt event.auctionResumeDateTime}">
								<div class="meeting2-heading-new">
									<label><spring:message code="auctionhall.resume.date"/> &amp; <spring:message code="application.time"/></label>
									<br />
									<h3 class="date-time-show fixh3 pull-left">
										<span>
											<fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</div>
							</c:if>
							<c:if test="${today gt event.auctionResumeDateTime}">
								<div class="meeting2-heading-new">
									<label><spring:message code="auctionhall.end.date"/> &amp; <spring:message code="application.time"/></label>
									<br />
									<h3 class="date-time-show fixh3 pull-left">
										<span>
											<fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</span>
									</h3>
								</div>
							</c:if>
						</c:if>
					</c:if>
					<c:if test="${event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED'  or event.status eq 'SUSPENDED'}">
						<div class="meeting2-heading-new">
							<label><spring:message code="auctionhall.start.date"/> &amp; <spring:message code="application.time"/></label>
							<br />
							<h3 class="date-time-show fixh3 pull-left">
								<span>
									<fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</span>
							</h3>
						</div>
					</c:if>
					<div class="tag-line marg-left-10 marg-bottom-10 width100">
						<span>
							<b><spring:message code="application.eventid"/>:</b> ${event.eventId}
						</span>
						<span>
							<b><spring:message code="rfx.auction.type.label" />:</b> ${event.auctionType.value}
						</span>
						<span>
							<b><spring:message code="eventsummary.eventdetail.reference"/>:</b> ${event.referanceNumber}
						</span>
						<br />
						<span>
							<b><spring:message code="application.eventowner"/>:</b> ${event.eventOwner.name}/${event.eventOwner.communicationEmail}
						</span>
					</div>
				</div>
				<c:if test="${event.status eq 'ACTIVE'}">
					<div class="col-md-5 col-sm-5">
						<div id="main" class="marg-right-40">
							<div class="">
								<c:if test="${not empty event.auctionResumeDateTime}">
									<c:set var="auctionStarted" value="true" />
									<div class="counter" id="counter">
										<h3><spring:message code="auctionhall.time.left"/> ${today lt event.auctionResumeDateTime ? 'Resume' : 'End'}</h3>
										<div class="main-example">
											<c:if test="${today lt event.auctionResumeDateTime}">
												<fmt:formatDate value="${event.auctionResumeDateTime}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<c:if test="${today ge event.auctionResumeDateTime}">
												<c:set var="auctionStarted" value="true" />
												<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<div class="countdown-container" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/englishAuctionConsole/${event.id}" id="main-example1"
												data-date="${counterDate}"></div>
										</div>
									</div>
								</c:if>
								<c:if test="${empty event.auctionResumeDateTime}">
									<div class="counter" id="counter">
										<h3><spring:message code="auctionhall.time.left"/> ${today lt event.eventStart ? 'Start' : 'End'}</h3>
										<div class="main-example">
											<c:if test="${today lt event.eventStart}">
												<fmt:formatDate value="${event.eventStart}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<c:if test="${today ge event.eventStart}">
												<c:set var="auctionStarted" value="true" />
												<fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="counterDate" />
											</c:if>
											<div class="countdown-container" data-url="${pageContext.request.contextPath}/timeRemaining/event/RFA/${event.id}" data-reload-url="${pageContext.request.contextPath}/buyer/englishAuctionConsole/${event.id}" id="main-example1"
												data-date="${counterDate}"></div>
										</div>
									</div>
								</c:if>
								<!-- /.Counter Div -->
							</div>
							<!-- /.Content Div -->
						</div>
						<!-- /#Main Div -->
					</div>
				</c:if>
				<c:if test="${event.status eq 'CLOSED' or event.status eq 'FINISHED' or event.status eq 'COMPLETE'}">
					<div class="meeting2-heading-new">
						<label><spring:message code="auctionhall.end.date"/> &amp; <spring:message code="application.time"/></label>
						<br />
						<h3 class="date-time-show fixh3 pull-left">
							<span>
								<fmt:formatDate value="${event.eventEnd}" pattern="dd/MMM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</span>
						</h3>
					</div>
				</c:if>
				<!-- /.Columns Div -->
			</div>
			<!-- /.Row Div -->
		</div>
		<div class="marg_top_20 pull-right">
			<a class="btn btn-alt btn-hover btn-primary hvr-pop event-details del_inv" href="${pageContext.request.contextPath}/buyer/RFA/eventSummary/${event.id}"><spring:message code="auctionhall.back.to.eventdetails"/></a>
		</div>
		<c:if test="${event.status eq 'SUSPENDED'}">
			<div id="timer-accord" class="Invited-Supplier-List  white-bg-with-arrow small-accordin-tab marg-bottom-20 marg-top-20">
				<div class="row">
					<div class="col-md-12">
						<div class="time-ammount">
							<div class="time-ammount-inner">
								<div class="time-ammount-heading blue-with-border"></div>
								<div class="time-ammount-contant-inner" style="width: 100% !important; color: red;">
									<spring:message code="auctionhall.suspended"/><br /><spring:message code="eventsummary.reason"/>: "${event.suspendRemarks}"
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>

		<c:if test="${(event.status eq 'ACTIVE' ) and (event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID')}">
			<jsp:include page="biddingSupplierListForBuyer.jsp" />
		</c:if>
		<c:if test="${today gt event.eventStart}">
			<c:if
				test="${((event.status eq 'SUSPENDED' or event.status eq 'ACTIVE' or event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED' ) and  (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')) or ((event.status eq 'COMPLETE' or event.status eq 'CLOSED' or event.status eq 'FINISHED') and (event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID'))}">
				<div class="tab-main marg_top_20">
					<ul class="tabs">
						<li class="tab-link current " data-tab="tab-1"><spring:message code="auctionhall.auction.console"/></li>
						<li class="tab-link bidHistory" data-tab="tab-2"><spring:message code="auctionhall.bid.history"/></li>
					</ul>
				</div>
				<div class="tab-main-inner pad_all_15">
					<div id="tab-1" class="tab-content current" style="display: none">
						<div class="width-100 bg-show-me float-left pad_all_5 ">
							<div class="col-md-2 col-sm-2 marg-top-10">
								<label><spring:message code="auctionhall.show.top.bid"/>: </label>
							</div>
							<div class="col-md-2 col-sm-2">
								<select class="chosen-select disablesearch " id="idSelectBidders">
									<option value="999"><spring:message code="auctionhall.all.bids"/></option>
									<option value="5"><spring:message code="auctionhall.top5.bids"/></option>
									<option value="4"><spring:message code="auctionhall.top4.bids"/></option>
									<option value="3"><spring:message code="auctionhall.top3.bids"/></option>
									<option value="2"><spring:message code="auctionhall.top2.bids"/></option>
									<option value="1"><spring:message code="auctionhall.top1.bids"/></option>
								</select>
							</div>
						</div>
						<jsp:include page="biddingSupplierListForBuyer.jsp" />
						<c:if test="${(empty auctionRules.lumsumBiddingWithTax) or (auctionRules.lumsumBiddingWithTax eq true)}">
							<div class="lower-bid-item-list bg-show-me pad_all_5 marg-top-20 marg-bottom-20">
								<div class=" ">
									<div class="col-md-3 col-sm-3 marg-top-10">
										<label><spring:message code="bidsupplier.details"/>: </label>
									</div>
									<div class="col-md-3 col-sm-3">
										<select class="chosen-select supplierSelect yehSupplierHai" id="supplierSelect">
											<option value=""><spring:message code="Product.favoriteSupplier"/></option>
											<c:forEach items="${eventSupplierList}" var="eventSupplier">
												<option value="${eventSupplier.id}">${eventSupplier.companyName}</option>
											</c:forEach>
										</select>
									</div>
								</div>
								<div class="price-total flagvisibility">
									<p>
										<spring:message code="eventawards.total"/>:
										<span id="amountAfterTax"></span>
									</p>
								</div>
							</div>
							<div class="bqBiddingValue lower-table-reserve-auction price-total flagvisibility">
								<table cellpadding="0" cellspacing="0" width="100%">
								</table>
								<div class="col-md-12">
									<div class="border-none pull-left">
										<b><spring:message code="prsummary.total2"/> :</b>
									</div>
									<div class="border-none pull-right">
										<b><span id="totalBqSupplier" class="pull-right"></span></b>
									</div>
								</div>
							</div>
							<div class="lower-table-reserve-auction1 padDetailsDiv" style="height: 150px; float: left; width: 100%;"></div>
						</c:if>
					</div>


					<!--  BID HISTORY -->
					<div id="tab-2" class="tab-content doc-fir notDisplay">
						<div class="col-md-12">
							<div class="row">
								<div class="col-md-2 col-sd-2 marg-top-10">
									<label><spring:message code="bidsupplier.show.graph"/>:</label>
								</div>
								<div class="col-md-2 col-sd-2">
									<select class="chosen-select disablesearch onBidTypeChange">
										<option value="Time" ${sessionScope["graphArrangeBy"] eq 'Time' ? 'selected' : ''}><spring:message code="application.time"/></option>
										<option value="BidNumber" ${sessionScope["graphArrangeBy"] eq 'BidNumber' ? 'selected' : ''}><spring:message code="auctionhall.bid.number"/></option>
									</select>
								</div>
								<div class="col-md-8 col-sd-8">
									<span class="pull-right"><spring:message code="bidsupplier.zoom.option"/></span>
								</div>
							</div>

							<div class="row">
								<div class="event-graph" style="margin-bottom: 20px;">
									<div class="example-box-wrapper">
										<div id="data-example-1" class="mrg20B" style="width: 100%; height: 370px;"></div>
									</div>
								</div>
								<c:if test="${auctionRules.buyerAuctionConsoleVenderType ne 'SHOW_NONE'}">
								
								<div class="demo-container">
									<div id="placeholder" class="demo-placeholder"></div>
									<div id="legend-container"></div>
								</div>
								</c:if>

							</div>
							<div class="row">
							<jsp:include page="bidHistory.jsp"></jsp:include>
							</div>

						</div>
					</div>
				</div>
			</c:if>
		</c:if>
	</div>
</div>
<div class="modal fade" id="timeExtension" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="eventsummary.eventdetail.time.extension" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form action="${pageContext.request.contextPath}/buyer/timeExtension/${eventId}" method="post">
				<div class="col-md-12 marg-bottom-10">
					<div class="row">
						<div class="modal-body col-md-12">
							<label>
								<spring:message code="bidsupplier.current.end.time"/> :
								<fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
							</label>
						</div>
						<div class="col-md-6">
							<input type="text" name="timeExtension" class="form-control" data-validation="required number" data-validation-allowing="range[1;100]" placeholder='<spring:message code="auction.interval.placeholder" />' /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						</div>
						<div class="col-md-6">
							<select name="timeExtensionType" class="form-control chosen-select disablesearch">
								<option value="MINUTE"><spring:message code="rfaevent.minutes"/></option>
								<option value="HOUR"><spring:message code="rfaevent.hours"/></option>
							</select>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Extend"  onclick="javascript:$('#loading').show();"/>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.cancel" /></button>
				</div>
			</form>
		</div>
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
				<label><spring:message code="rfaevent.sure.want.suspended"/></label>
			</div>
			<form:form id="idSuspendForm" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/suspendEvent" method="post">
				<form:hidden path="id" />
				<form:hidden path="eventName" />
				<form:hidden path="referanceNumber" />
				<form:hidden path="auctionType" />
				<input type="radio" name="auctionConsole" value="1" checked="checked" data-validation="required" style="display: none;">
				<div class="form-group col-md-6">
				<spring:message code="auction.remarks.placeholder"  var="auctionRemark"/>
					<form:textarea path="suspendRemarks" data-validation="required length" data-validation-length="max500" class="form-control width-100 suspendRemarks" placeholder="${auctionRemark}" />
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="suspendEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"><spring:message code="application.yes" /></form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="javascript:$('#idSuspendForm').get(0).reset();"><spring:message code="application.no2" /></button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyerEnglishAuction.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-resize.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-stack.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-pie.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/jquery.flot.time.js"/>"></script>
<!--<script src="jquery-1.7.0.min.js "></script>
	<script src="featherlight.js " type="text/javascript " charset="utf-8 "></script> -->
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/chart-js/Chart.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-axislabels.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/flot-tickrotate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/flot/jquery.flot.selection.js"/>"></script>




<script type="text/javascript">
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.tab-link > a, .supplierSelect, .auctionBidSupplier, .refreshAuctionBids, #idSelectBidders, .event-details, #dashboardLink';
	//var disableAnchers = ['#reloadMsg'];   
	<c:if test="${(event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall}">
		allowedFields +=', .onBidTypeChange, .auctionBidSupplier';
 	</c:if>
	disableFormFields(allowedFields);
});
</c:if>
	var amountDecimalValue = '${event.decimal}';
	var currencyCode = '${event.baseCurrency}';

	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});

	$(document).ready(function() {
		$('timeExtension').mask('000000', {
			placeholder : "Interval e.g. 5"
		});
	});

	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});

	$(document).ready(function() {
		
		$('#suspendEvent').click(function() {
			var suspendRemarks = $('.suspendRemarks').val();
			if (suspendRemarks != '') {
			$(this).addClass('disabled');
			}
		});
		
		$(".dropdown img.flag").addClass("flagvisibility");

		$(".dropdown dt a").click(function() {
			$(".dropdown dd ul").toggle();
		});

		$(".dropdown dd ul li a").click(function() {
			var text = $(this).html();
			$(".dropdown dt a span").html(text);
			$(".dropdown dd ul").hide();
			$("#result").html("Selected value is: " + getSelectedValue("sample"));
		});

		function getSelectedValue(id) {
			return $("#" + id).find("dt a span.value").html();
		}

		$(document).bind('click', function(e) {
			var $clicked = $(e.target);
			if (!$clicked.parents().hasClass("dropdown"))
				$(".dropdown dd ul").hide();
		});

		$("#flagSwitcher").click(function() {
			$(".dropdown img.flag").toggleClass("flagvisibility");
		});
		
		
		<c:if test="${event.status == 'ACTIVE'}">
		setInterval(reloadBidderList, 3000);
		//setInterval(getBidHistoryOfSuppliers, 5000);
		</c:if>

		// Load it first time... there after it will be timer based...
		getBidHistoryOfSuppliers('${!empty sessionScope["graphArrangeBy"] ? sessionScope["graphArrangeBy"] : "Time"}');

		$(".Invited-Supplier-List").click(function() {
			$("#timer-accord").toggleClass("small-accordin-tab");
		});
	});

		// chart initial	

		//Interacting with Data Points example

		function convertTotimeStamp(myDateTime) {
			var myDateTime = '07/12/2017 04:47:10';
			var arrAY = myDateTime.split(" ");
			var time = arrAY[1] + " " + arrAY[2];
			var hours = Number(time.match(/^(\d+)/)[1]);
			var minutes = Number(time.match(/:(\d+)/)[1]);
			var seconds = Number(arrAY[1].split(':')[2]);
			var AMPM = time.match(/\s(.*)$/)[1];
			if (AMPM == "PM" && hours < 12)
				hours = hours + 12;
			if (AMPM == "AM" && hours == 12)
				hours = hours - 12;
			var sHours = hours.toString();
			var sMinutes = minutes.toString();
			var sSeconds = seconds.toString();
			if (hours < 10)
				sHours = "0" + sHours;
			if (minutes < 10)
				sMinutes = "0" + sMinutes;
			if (seconds < 10)
				sSeconds = "0" + sSeconds;
			//	console.log(sHours + ":" + sMinutes);			
			dateString = arrAY[0] + " " + sHours + ":" + sMinutes+":"+sSeconds;
			dateParts = dateString.match(/(\d+)\/(\d+)\/(\d+) (\d+):(\d+)/);
			var date = new Date(dateParts[3], parseInt(dateParts[2], 10) - 1, dateParts[1], sHours, sMinutes,sSeconds);
			console.log(date.getTime());
			return date.getTime();
		}

		function getBidHistoryOfSuppliers(arrangeBidBy) {
			//var arrangeBidBy = "Time";
			var xmode = "";
			var xtickDecimal = "";
			var xmax = "";
			var xlabel = "";
			var xtimeZone = "Asia/Singapore";
			
			var eventId = $("#eventId").val();
			var bqId = $("#bqId").val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var ajaxUrl = getContextPath() + '/buyer/getBidHistoryOfSuppliers';
			var bqData = {
				'eventId' : eventId,
				'arrangeBidBy' : arrangeBidBy
			};
			$.ajax({
				url : ajaxUrl,
				type : "POST",
				data : bqData,
				beforeSend : function(xhr) {
					//$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				success : function(data, textStatus, request) {
					var max = 10;
					console.log(data);
					if(arrangeBidBy === "Time"){
						xmode = "time";
						xtickDecimal = null;
						xmax = null;
						xlabel = "(Bid submission Time)";
						
					}else{
						xmode = "number";
						xtickDecimal = 0;
						xmax = data.totalBids;
						xlabel = "(Bid number)";
					}
					
					var chartOptions = {
						xaxes : [ {
							mode : xmode,
							timeformat: "%d/%m %H:%M:%S",
							tickDecimals: xtickDecimal
							//max: xmax
						} ],
						yaxes : [ {
							min : data.minPrice,
							max : data.maxPrice,
							tickDecimals : amountDecimalValue,
							tickFormatter: function ReplaceNumberWithCommas(yourNumber) {
								var n = parseFloat(yourNumber).toFixed(amountDecimalValue).toString().split(".");
								n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
								// Combines the two sections
								return n.join(".");
							}
						} ],     
						selection:{
			                mode: "xy"
			            },
						series : {
							shadowSize : 0,
							lines : {
								show : true
								//lineWidth : 2
							},
							points : {
								show : true
							}
						},
						grid : {
							labelMargin : 10,
							hoverable : true,
							clickable : true,
							borderWidth : 1,
							borderColor : 'rgba(82, 167, 224, 0.06)'
						},
						legend : {
							backgroundColor : '#fff',
							 container:$("#legend-container")
						},
						yaxis : {
							tickColor : 'rgba(0, 0, 0, 0.06)',
							font : {
								color : 'rgba(0, 0, 0, 0.85)'
							},
							axisLabel: '(Bid Value in ${event.baseCurrency})' 
						},
						xaxis : {
							tickColor : 'rgba(0, 0, 0, 0.06)',
							font : {
								color : 'rgba(0, 0, 0, 0.85)'
							},
							axisLabel: xlabel,
							//rotateTicks: 90
						},
						colors : [ "#68b3e2", "#ffad13", "#a3cc56", "#23527c", "#4cae4c", "#308dcc", "#d67520", "#269abc", "#2d2d2d" ],
						tooltip : true,
						tooltipOpts : {
						       content: function(label, xval, yval, flotItem){
							        return label+"<br> Bid : ${event.baseCurrency} %y <br/> Time : "+flotItem.series.data[flotItem.dataIndex][2];
							   }
						}
					}
					
					var plot = $.plot($("#data-example-1"), data.supplierBids, chartOptions);
					console.log("**************************************************************"+data.supplierBids)
					$("#data-example-1").dblclick(function () {
						plot = $.plot($("#data-example-1"), data.supplierBids, chartOptions);
					});
					
					$("#data-example-1").bind("plotselected", function (event, ranges) {

						// clamp the zooming to prevent eternal zoom

						if (ranges.xaxis.to - ranges.xaxis.from < 0.00001) {
							ranges.xaxis.to = ranges.xaxis.from + 0.00001;
						}

						if (ranges.yaxis.to - ranges.yaxis.from < 0.00001) {
							ranges.yaxis.to = ranges.yaxis.from + 0.00001;
						}

						// do the zooming

						console.log('Zoom From : ' + ranges.xaxis.from + ' To : ' + ranges.xaxis.to);
						
						// getData(ranges.xaxis.from, ranges.xaxis.to)
						
						plot = $.plot("#data-example-1", data.supplierBids,
							$.extend(true, {}, chartOptions, {
								xaxis: { min: ranges.xaxis.from, max: ranges.xaxis.to }
								//yaxis: { min: ranges.yaxis.from, max: ranges.yaxis.to }
							})
						);

						// don't fire event on the overview to prevent eternal loop

						//overview.setSelection(ranges, true);
						// for Overview in zoom link  view-source:http://www.flotcharts.org/flot/examples/zooming/index.html
					});
					
				},
				error : function(request, textStatus, errorThrown) {
					console.log(request.responseText);
				},
				complete : function() {
					$('#loading').hide();
					$('.notDisplay').removeClass('notDisplay').hide();
				}
			});

		};
		
		$('.onBidTypeChange').on('change', function() {
			  getBidHistoryOfSuppliers(this.value);
			});

	function fetchSupplierAuctionBids() {
		$('.auctionbids > table > tbody ').html('');
		var supplierId = $('.auctionBidSupplier').val();
		if (supplierId == '') {
			return;
		}

		var eventId = $("#eventId").val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/buyer/getAuctionBidsOfSuppliers/' + eventId + '/' + supplierId;
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				var html = '';
				console.log(data);
				$.each(data, function(i, item) {
					console.log(item);
					html += '<tr>';
					if (item.rankForBid === undefined || item.rankForBid === '') {
						html += '<td>&nbsp</td>';
					} else {
						html += '<td>' + item.rankForBid + '</td>';
					}
					html += '<td class="align-right">' + currencyCode + ' ';
					if(item.amount != null)
					html += ReplaceNumberWithCommas(item.amount.toFixed($('#eventDecimal').val())) ;
					else{
						html +=' '
					}
					html += '</td><td class="align-right">' + item.bidSubmissionDate + '</td>';
					<c:if test="${event.status eq 'SUSPENDED' and eventPermissions.revertBidUser}">
					html += '<td class="align-right"><input class="revertBid btn btn-alt btn-hover btn-warning hvr-pop" type="submit" data-id='+ item.id +' value="Revert to this bid"></td><tr>';
					</c:if>
				});
				console.log(html);
				$('.auctionbids > table > tbody ').html(html);
			},
			error : function(request, textStatus, errorThrown) {
				console.log(request.responseText);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	}
	

	
	function ReplaceNumberWithCommas(yourNumber) {
		if(yourNumber!=null){
		var n = yourNumber.toString().split(".");
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
		}
		
	}
	
	
	
	
</script>