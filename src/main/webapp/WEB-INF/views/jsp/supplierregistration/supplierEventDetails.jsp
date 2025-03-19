<%@ page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bankmy.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<spring:message var="supplierRfxEventDetailsDesk" code="application.supplier.rfx.event.details" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxEventDetailsDesk}] });
});
</script>
<style type="text/css">
#declarationModal.reset-that {
  all: initial;
  * {
    all: unset;
  };
  word-break: break-all;
}
.mb {
  color:#ffffff
  };
</style>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container" >
			<c:if test="${eventType == 'RFA' && (event.status == 'CLOSED') && (!empty auctionRules.lumsumBiddingWithTax) && eventSupplier.revisedBidSubmitted == false && (eventSupplier.auctionRankingOfSupplier  != null and eventSupplier.numberOfBids ge 0)}">
				<c:set var="warn" value="You have not yet submitted your revised auction Bill of Quantities" scope="request" />
			</c:if>
			<c:if test="${eventType == 'RFA' && event.status == 'CLOSED' && (!empty auctionRules.lumsumBiddingWithTax) && !empty bq.revisedGrandTotal && bq.revisedGrandTotal != bq.totalAfterTax && !eventSupplier.revisedBidSubmitted}">
				<c:set var="error" value="Your Bill of Quantities total does not match with your final Auction bid price. Please revise and submit." scope="request" />
			</c:if>
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			
			<c:if test="${!hideEventDetails}">
			<jsp:include page="supplierSubmissionHeader.jsp" />
			
			<div class="tab-main-inner pad_all_15">
				<input type="hidden" value="${event.participationFees}" id="eventParticipationFeesId">
				<input type="hidden" value="${eventSupplier.feeReference}" id="feeReferenceId">
				<input type="hidden" value="${eventSupplier.feePaid}" id="feePaidId">
				<input type="hidden" value="${publishKey}" id="publishKeyId">
				<div id="tab-1" class="tab-content">
					<div class="example-box-wrapper wigad-new">
						<div class="col-md-12 pad0 marg-top-5">
							<div class="panel bgnone marg-none">
								<div class="panel-body">
									<div class="example-box-wrapper">
										<div class="panel-group" id="accordion">
											<div class="panel sum-accord">
												<div class="panel-heading">
													<h4 class="panel-title">
														<a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"><spring:message code="eventdetails.event.information.label" /> </a>
													</h4>
												</div>
												<div id="collapseTwo" class="panel-collapse collapse in">
													<div class="panel-body pad_all_15  border-bottom">
														<div class="main-panal-box-main">
															<div class="ra_event_row">
																<input type="hidden" value="${eventType}" id="eventTypeId">
																<label><spring:message code="application.eventid" />:</label>
																<p>${event.eventId}</p>
																<input type="hidden" value="${event.id}" id="eventIdNumberId">
															</div>
															<div class="ra_event_row">
																<label><spring:message code="application.eventtype" /> :</label>
																<p>${eventType.value}</p>
															</div>
															<c:if test="${eventType eq 'RFA'}">
																<div class="ra_event_row">
																	<label><spring:message code="rfx.auction.type.label" /> :</label>
																	<p>${event.auctionType.value}</p>
																</div>
															</c:if>
															<div class="ra_event_row">
																<label><spring:message code="eventdetails.event.referencenumber" /> :</label>
																<p>${event.referanceNumber}</p>
															</div>
															<div class="ra_event_row">
																<label><spring:message code="eventdetails.event.name" /> :</label>
																<p>${event.eventName}</p>
															</div>
															<div class="ra_event_row">
																<label><spring:message code="application.eventowner" /> :</label>
																<p>${event.eventOwner}
																	<br> ${event.eventOwnerEmail} <br> ${event.ownerPhoneNumber}
																</p>
															</div>
														</div>
													</div>
												</div>
											</div>
											<div class="panel sum-accord">
												<div class="panel-heading">
													<h4 class="panel-title">
														<a data-toggle="collapse" data-parent="#accordion" href="#collapseThree"> <spring:message code="eventsummary.timeline.title" /> </a>
													</h4>
												</div>
												<div id="collapseThree" class="panel-collapse collapse">
													<div class="panel-body pad_all_15  border-bottom">
														<div class="main-panal-box-main">
															<div class="ra_event_row">
																<label><spring:message code="application.startdate" /> :</label>
																<p>
																	<fmt:formatDate value="${event.eventStart}" pattern="E dd MMM yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</p>
															</div>
															<div class="ra_event_row">
																<label><spring:message code="rfaevent.end.date" /> :</label>
																<p>
																	<fmt:formatDate value="${event.eventEnd}" pattern="E dd MMM yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</p>
															</div>
															<div class="ra_event_row">
																<label><spring:message code="supplierevent.validity.days" /> :</label>
																<c:if test="${empty event.submissionValidityDays}">
																-
																</c:if>
																<p>${event.submissionValidityDays}</p>
															</div>
														</div>
													</div>
												</div>
											</div>
											<div class="panel sum-accord">
												<div class="panel-heading">
													<h4 class="panel-title">
														<a data-toggle="collapse" data-parent="#accordion" href="#collapseFour"> <spring:message code="subscription.contact.detail" /> </a>
													</h4>
												</div>
												<div id="collapseFour" class="panel-collapse collapse">
													<div class="panel-body pad_all_15  border-bottom">
														<div class="main-panal-box-main">
															<div class="ra_event_row">
																<label><spring:message code="supplierevent.person.incharge" /> :</label>
																<p>
																	<c:forEach var="contact" items="${eventContacts}">
																		<span class="marg-bottom-15 pull-left width100"> ${contact.contactName} <br> ${contact.comunicationEmail}<br>  <spring:message code="prdraft.tel" />: ${contact.contactNumber} <br>
																		</span>
																	</c:forEach>
																</p>
															</div>
														</div>
													</div>
												</div>
											</div>
											<div class="panel">
												<div class="panel-heading">
													<h4 class="panel-title">
														<a data-toggle="collapse" data-parent="#accordion" href="#collapseFive"> <spring:message code="eventdetails.event.correspondence.address" /> </a>
													</h4>
												</div>
												<div id="collapseFive" class="panel-collapse collapse">
													<div class="panel-body">
														<div class="ra_event_row">
															<label><spring:message code="supplierevent.submission.address" /> :</label>
															<p>
																${event.ownerLine1},<br /> ${event.ownerLine2},<br /> ${event.ownerCity}<br /> ${event.ownerState}<br /> ${event.ownerCountry}<br />
															</p>
														</div>
													</div>
												</div>
											</div>
											<div class="panel">
												<div class="panel-heading">
													<h4 class="panel-title">
														<a data-toggle="collapse" data-parent="#accordion" href="#collapseSix"> <spring:message code="eventdescription.finance.label" /> </a>
													</h4>
												</div>
												<div id="collapseSix" class="panel-collapse collapse">
													<div class="panel-body">
														<div class="ra_event_row">
															<label><spring:message code="pr.base.currency" /> :</label>
															<p>
																${event.baseCurrency} <br>
															</p>
														</div>
														<div class="ra_event_row">
															<label><spring:message code="eventdescription.decimal.label" /> :</label>
															<p>
																${event.decimal} <br>
															</p>
														</div>
														<div class="ra_event_row">
															<label><spring:message code="supplierevent.payment.term" /> :</label>
															<c:if test="${ empty event.paymentTerm}">
															-
															</c:if>
															<p>
																${event.paymentTerm} <br>
															</p>
														</div>
													</div>
												</div>
											</div>
											<div class="panel sum-accord">
												<div class="panel-heading">
													<h4 class="panel-title">
														<a data-toggle="collapse" data-parent="#accordion" href="#collapseEight"> <spring:message code="eventdescription.name" /></a>
													</h4>
												</div>
												<div id="collapseEight" class="panel-collapse collapse">
													<div class="panel-body">
														<div class="ra_event_row2">${event.eventDescription!=null?event.eventDescription:'N/A'}</div>
													</div>
												</div>
											</div>

											<jsp:include page="/WEB-INF/views/jsp/supplier/supplierEventAudit.jsp" />

											<c:if test="${eventType eq 'RFA'}">
												<jsp:include page="supplierAuctionRules.jsp"></jsp:include>
											</c:if>
  											<c:if test="${(!empty event.participationFees and event.participationFees.unscaledValue() ne 0 and eventSupplier.feePaid) or (empty event.participationFees or event.participationFees.unscaledValue() eq 0)}">
 												<c:if test="${allowThisEvent and isAdmin and ((event.eventVisibility != 'PRIVATE' and empty eventSupplier.submissionStatus) or eventSupplier.submissionStatus == 'INVITED'  or ( eventSupplier.rejectedTime le event.eventStart and eventSupplier.submissionStatus =='REJECTED') ) and event.eventPublishDate le today and event.status == 'ACTIVE'}"> 
														<div class="terms-acc">
															<input id="acceptInvitation" name="acceptInvitation" class="custom-checkbox acceptInvitation" type="checkbox"> 
																<label> <spring:message code="supplierevent.agree.buyer.terms" /> 
																<c:if test="${event.enableSupplierDeclaration}">
																	<a href="#declarationModal" id="previewButton"> <spring:message code="prtemplate.terms.condition" /></a>
																</c:if>
																<c:if test="${!event.enableSupplierDeclaration}">
																	<spring:message code="prtemplate.terms.condition" />
																</c:if>
																</label>
														</div>
 												</c:if> 
 											</c:if> 
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="marg-top-20 btns-lower">
				<div class="row rowForSuspend">
					<div class="col-md-12 col-xs-12 col-ms-12">
							<c:if test="${!empty event.participationFees and event.participationFees.unscaledValue() ne 0 and !eventSupplier.feePaid and event.status == 'ACTIVE' and publishKey == null}">
								<div class="alert alert-danger" id="buyerParticipationError">
									<div class="bg-red alert-icon">
										<i class="glyph-icon icon-times"></i>
									</div>
									<div class="alert-content">
										<h4 class="alert-title">Participation fee error.</h4>
										<p id="buyerParticipationSubError">Participation fee payment is required to accept this event.</p>
									</div>
								</div>
							</c:if>
 							<c:if test="${allowThisEvent and isAdmin and ((event.eventVisibility != 'PRIVATE' and empty eventSupplier.submissionStatus) or eventSupplier.submissionStatus == 'INVITED'   or (eventSupplier.rejectedTime le event.eventStart and eventSupplier.submissionStatus =='REJECTED') ) and event.eventPublishDate le today and event.status == 'ACTIVE'}"> 
									<form method="post" class="col-md-6" action="${pageContext.request.contextPath}/supplier/acceptOrRejectInvitation/${event.id}/${eventType}/true">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
 										<c:if test="${((!empty event.participationFees and event.participationFees.unscaledValue() ne 0 and eventSupplier.feePaid) or (empty event.participationFees or event.participationFees.unscaledValue() eq 0))}">
											<input type="submit" id="acceptInvite" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out disabled accept-invitation" value="Accept Invitation"></input>
 										</c:if> 
										<c:if test="${!empty event.participationFees and event.participationFees.unscaledValue() ne 0 and !eventSupplier.feePaid and event.status == 'ACTIVE' and publishKey ne null}">
										<input type="button" id="payFees" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out" value="Pay Participation Fee"></input>
										</c:if>
									</form>
 							</c:if> 
							<!-- For RFA - Supplier cannot reject event after Auction has Started. -->
							<c:if test="${allowThisEvent and isAdmin and (eventSupplier.submissionStatus == 'INVITED'  or eventSupplier.submissionStatus == 'ACCEPTED' ) and event.eventPublishDate le today and event.status == 'ACTIVE' and event.eventEnd ge today }">
								<input type="button" class="btn btn-black ph_btn marg-left-10 hvr-pop hvr-rectangle-out1 reject-invitation" value="Reject Invitation">
							</c:if>
							<c:set var="isResubmission" value="${eventType == 'RFA' && eventSupplier.submissionStatus =='COMPLETED' && (event.status == 'CLOSED') && (!empty auctionRules.lumsumBiddingWithTax) && eventSupplier.revisedBidSubmitted == false}" />
							<c:set var="disableFinish" value="${isResubmission && bq != null && bq.revisedGrandTotal != null && bq.revisedGrandTotal != bq.totalAfterTax }" />
 					</div>
				</div>
			</div>
			</c:if>
		</div>
	</div>
</div>
</div>
</div>
<!-- Reject Event -->
<div class="modal fade" id="confirmRemoveEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.reject" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="rejectEventForm" method="post">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="supplierevent.not.agree.note" />
							</label><input type="hidden" id="deleteIdEvent" />
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Remark.(Mandatory)" rows="3" name="rejectionRemark" id="rejectionRemark" data-validation="required length" data-validation-length="max200"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1" id="confirmRemove">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confRemoveEvent">
						<spring:message code="application.yes" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" id="noRejectedEvent" data-dismiss="modal">
						<spring:message code="application.no" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- ----------------------------------PopUp- 2---------- -->
<div class="modal fade" id="confirmFinishEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm" /> <span>${isResubmission ? 'Finish Revised Submission' : ((eventType != 'RFA') ? 'Finish' : 'Pre-bid Submission ')}</span>
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				
				<label> <spring:message code="supplierevent.sure.submit.final" /> <span>${isResubmission ? 'Finish Revised Submission' : ((eventType != 'RFA') ? ' ' : 'pre-bid ')}</span>
					<spring:message code="supplierevent.once.submit.nomodify" /> 
				</label> <input type="hidden" id="finishIdEvent" />
					<input type="hidden" class="siteVisitMandatoryCheck" value="true">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out finish-invitation  finish-event" value="${event.id}"><spring:message code="application.yes" /></button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.no2" /></button>
			</div>
		</div>
	</div>
</div>
<!-- ----------------------------------PopUp- 3---------- -->
<div class="flagvisibility dialogBox" id="declarationModal"  style="overflow: auto;" title="<spring:message code="declaration.popup.title" />">
	<div class="float-left width800 pad_all_15 white-bg">
		<div class="row" style="margin-left:10px;">
				<span class="reset-that" id="declarationContent">${supplierDeclaration.content}</span>
		</div>
	</div>
</div>

<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="" payment-amount=""
	style="background: #0000004f;">
	<div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
		<!-- Modal content-->
		<div class="modal-content">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-body">
				<button class="close for-absulate" id="makePaymentModalCloseId" type="button"
					data-dismiss="modal">×</button>
				<div class="row">
					<div class="col-md-12">
						<h3 class="title-hero" class="header-section">
							Choose Payment Mode
						</h3>
						<div class="example-box-wrapper">
							<ul class="list-group list-group-separator row list-group-icons alignment">
								<li class="col-md-5">
									<a href="#tab-example-1" id="tabOneId" data-toggle="tab" class="list-group-item">
										<i class="glyphicon glyphicon-credit-card" style="top: 2px;"></i>&nbsp;&nbsp;
										<spring:message code="payment.mode.card" />
									</a>
								</li>

								<c:if test="${event.participationFeeCurrency == 'MYR'}">
									<li class="col-md-5">
										<a href="#tab-example-2" id="tabTwoId" data-toggle="tab"
											class="list-group-item">
											<div class="icon icon-fpx"></div>&nbsp;&nbsp;
											<spring:message code="payment.mode.fpx" />
										</a>
									</li>
								</c:if>
							</ul>
							<div class="tab-content">
								<div class="panel">
									<div class="panel-body">

										<div class="tab-pane fade" id="tab-example-1">
											<div class="row col-12">
												<img src="${pageContext.request.contextPath}/resources/assets/images/cards-logo.jpeg"
													class="payment-images-card">
											</div>
											<div>
												<form id="emailCardForm">
													<div class="form-group">
														<label class="col-12 control-label group-label">Communication
															Email</label>
														<div class="col-12">
															<input type="text" class="form-control"
																id="paymentEmailCard"
																placeholder="Supplier communication email..."
																data-validation="email required"
																value="${supplier.communicationEmail}">
														</div>
													</div>
												</form>
											</div>
											<div id="cardBlock">
												<div class="sr-root">
													<div class="sr-main">
														<form id="payment-form" class="sr-payment-form">
															<div
																class="margin-top-15 parent-card-div row center-align-row">
																<div class="col-md-12 padding-left-right-0">
																	<div class="sr-combo-inputs-row">
																		<div class="sr-input sr-card-element form-control"
																			id="card-element"></div>
																	</div>
																</div>
															</div>
															<div class="sr-field-error" id="card-errors" role="alert">
															</div>
															<div class="margin-top-15 parent-card-div row">
																<div class="col-md-12">
																	<button id="payByCardId" type="button"
																		class="ph_btn_small btn-success width-100 payment-btn btn">
																		Pay ${event.participationFeeCurrency}
																		${event.participationFees}
																	</button>
																</div>
															</div>
														</form>
													</div>
												</div>
											</div>
										</div>
										<div class="tab-pane fade" id="tab-example-2">
											<div class="row col-12">
												<img src="${pageContext.request.contextPath}/resources/assets/images/fpx-logo.png"
													class="payment-images">
											</div>
											<div>
												<form id="emailFpxForm">
													<div class="form-group">
														<label class="col-12 control-label group-label">Communication
															Email</label>
														<div class="col-12">
															<input type="text" class="form-control bottom-border-only"
																id="paymentEmailFpx"
																placeholder="Supplier communication email..."
																data-validation="email required"
																value="${supplier.communicationEmail}">
														</div>
													</div>
												</form>
											</div>
											<div id="fpxBlock">
												<div class="payment-block">
													<form id="payment-form-fpx">
														<div class="row">
															<div class="col-md-12 align-center">
																<div id="fpx-bank-element" class="form-control"
																	style="padding-top: 0;"></div>
															</div>
														</div>
														<div class="row margin-top-15">
															<div class="col-md-12 align-center">
																<button id="fpx-button" data-secret=""
																	class="btn-success no-border ph_btn_small width-100 payment-btn btn">
																	Pay ${event.participationFeeCurrency}
																	${event.participationFees}
																</button>
															</div>
														</div>
													</form>
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

<script>

$(document).ready(function() {
	$('#acceptInvite').click(function() {
		$(this).addClass('disabled');
	});

	$('#confRemoveEvent').on('click', function(e) {
		e.preventDefault();
		if($("#rejectEventForm").isValid()) {
			var eventId=$("#eventIdNumberId").val();
			var eventType=$("#eventTypeId").val();
			$(this).addClass('disabled');
			$('#confRemoveEvent').addClass('disabled');
 			$('#noRejectedEvent').addClass('disabled');
 			$('#rejectEventForm').attr('action', getContextPath() + '/supplier/acceptOrRejectInvitation/'+ eventId +'/'+ eventType +'/false');
			$("#rejectEventForm").submit();
		}else{
			return;
		}
	});
});
	<c:if test="${eventPermissions.viewer}">
	$(window).bind('load', function() {
		var allowedFields = '.tab-link > a,#idDutchConsole,#idEnglishConsole';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>

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
		$("#previewButton").click(function(){
				$("#declarationModal").dialog({
					modal : true,
					minWidth : 300,
					width : '50%',
					maxWidth : 600,
					minHeight : 200,
					maxHeight:500,
					dialogClass : "",
					show : "fadeIn",
					draggable : true,
					resizable : false,
					dialogClass : "dialogBlockLoaded",
				});
	});
</script>
<script src="https://js.stripe.com/v3/"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierSubmissionEvent.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>

<style>
	.payment-div {
		text-align: left;
		color: #4f4d4d !important;
		font-family: 'open_sanssemibold';
		font-weight: normal;
		margin-top: 15px;
	}

	.payment-block {
		text-align: left;
		margin-top: 10px;
		margin-bottom: 10px;
	}

	.pad-left-12 {
		padding-left: 12px;
	}

	.pad-left-0 {
		padding-left: 0;
	}

	.margin-top-15 {
		margin-top: 15px;
	}

	.parent-card-div {
		display: flex;
		align-items: center;
	}

	.sr-field-error {
		margin-top: 10px;
		text-align: center;
		font-size: 14px;
		color: #ff5757;
	}

	.stripe-iframe {
		position: absolute;
		left: 30%;
		top: 10%;
	}

	ul {
		list-style: none;
	}

	.list-group-icons .list-group-item {
		font-weight: 700;
		display: block;
		padding: 15px 10px;
		text-align: center;
		text-overflow: ellipsis;
	}

	.list-group-icons .list-group-item>.glyph-icon {
		font-size: 18px;
		float: none;
		margin: 0 auto;
	}

	.alignment {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.group-label {
		text-align: right;
		margin-top: 10px;
	}

	.modal-dialog.for-delete-all.reminder .modal-body label {
		float: left;
		width: 100%;
		text-align: left;
	}

	.modal-dialog.for-delete-all.reminder .modal-body input {
		float: left;
		width: 100%;
		margin-right: 2%;
		margin-top: 0;
	}

	.header-section {
		text-align: center;
		margin-bottom: 20px;
		color: black;
		font-weight: 900;
	}

	.payment-images {
		width: 30%;
		height: 60px;
		max-width: 125px;
		margin: 10px;
	}

	.payment-images-card {
		width: 65%;
		height: 60px;
		max-width: 260px;
	}

	.payment-btn:focus {
		outline-color: #58d68c;
	}

	.center-align-row {
		width: 100%;
		margin-left: 0;
	}

	.padding-left-right-0 {
		padding-left: 0;
		padding-right: 0;
	}
</style>

