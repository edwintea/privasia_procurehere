<%@page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxCreatingMeetings" code="application.rfx.create.meetings" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingMeetings}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<li>
					<a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a>
				</li>
				<li class="active">${eventType.value}</li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="label.meeting.list" />
				</h2>
				<h2 class="trans-cap pull-right">Status : ${event.status}</h2>
			</div>
			<jsp:include page="eventHeader.jsp"></jsp:include>
			<div class="container-fluid col-md-12">
				<div class="row">
					<div class="col_12">
						<div class="white_box_brd pad_all_15">
							<section class="index_table_block">
								<header class="form_header">
									<c:if test="${not empty errors}">
										<span class="error">${errors}</span>
									</c:if>
								</header>
								<div class="row">
									<div class="col-xs-12">
										<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
										<div class="ph_tabel_wrapper scrolableTable_UserList">
											<table id="tableList" class="ph_table display table table-bordered noarrow">
												<thead>
													<tr>
														<th class="width_100 width_100_fix align-left"><spring:message code="application.action1" /></th>
														<th class="align-left"><spring:message code="label.meeting.appointment.date.time" /></th>
														<th class="align-left">
															<%-- <spring:message code="label.meeting.contact.person" /> --%> <spring:message code="eventdetails.event.title" />
														</th>
														<th class="align-left"><spring:message code="label.meeting.type" /></th>
														<th class="align-left"><spring:message code="label.meeting.attend.mandatory" /></th>
														<th class="align-left">
															<%-- <spring:message code="label.meeting.contact.person" /> --%> <spring:message code="application.status" />
														</th>
													</tr>
												</thead>
												<c:forEach var="list" items="${meetingList}">
													<tr>
														<td class="width_100 width_100_fix align-left">
															<form action="${pageContext.request.contextPath}/buyer/${eventType}/viewMeeting" method="post" id="${list.id}">
																<input type="hidden" name="meetingId" value="${list.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <a href="#" onclick="document.getElementById('${list.id}').submit();" id="idvewbutton"> <i class="fa fa-eye" aria-hidden="true" data-placement="top" title='<spring:message code="tooltip.view" />'></i>
																</a>
																<c:if test="${event.status =='DRAFT'}">
																&nbsp;
																<a href="#myModal" onClick="javascript:updateLink('${list.id}');" role="button" data-toggle="modal"> <img src="${pageContext.request.contextPath}/resources/images/delete1.png" data-placement="top" title='<spring:message code="tooltip.delete" />'>
																	</a>
																</c:if>
															</form>
														<td class="align-left">
															<fmt:formatDate value="${list.appointmentDateTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
														</td>
														<td class="align-left">
															<c:out value="${list.title}" default="" />
														</td>
														<td class="align-left">
															<c:out value="${list.meetingType}" />
														</td>
														<td class="align-left">
															<c:out value="${list.meetingAttendMandatory?'Yes':'No'}" />
														</td>
														<td class="align-left">
															<c:if test="${list.status =='CANCELLED'}">
																<font color="red">${list.status}</font>
															</c:if>
															<c:if test="${list.status !='CANCELLED'}">
														${list.status}
														</c:if>
														</td>
													</tr>
												</c:forEach>
											</table>
										</div>
										<div class="">
											<div class="marg-top-20">
												<form action="${pageContext.request.contextPath}/buyer/${eventType}/createMeeting" method="get">
													<input type="hidden" name="source" value="meeting"> <input type="hidden" name="eventId" value="${eventId}" /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<button type="submit" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out">
														<spring:message code="button.create.meeting" />
													</button>
												</form>
											</div>
										</div>
										<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
							</section>
						</div>
						<div class="marg-top-20 btns-lower">
							<div class="row">
								<div class="col-md-12 marg-top-10" style="text-align: right;">
									<form action="${pageContext.request.contextPath}/buyer/${eventType}/meetingPrevious" method="post" style="float: left;">
										<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<button type="submit" class="btn btn-black ph_btn marg-bottom-10 hvr-pop hvr-rectangle-out1" id="priviousStep">
											<spring:message code="application.previous" />
										</button>
									</form>
									<form action="${pageContext.request.contextPath}/buyer/${eventType}/meetingNext" method="post" style="float: left;">
										<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<button type="submit" class="btn btn-info ph_btn marg-left-10 marg-bottom-10 hvr-pop hvr-rectangle-out" id="meetingListNext">
											<spring:message code="application.next" />
										</button>
									</form>
									<spring:message code="application.draft" var="draft" />
									<form action="${pageContext.request.contextPath}/buyer/${eventType}/meetingSaveDraft" method="post" style="float: right;">
										<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<c:if test="${event.status eq 'DRAFT'}">
											<input type="submit" id="idMeetingSaveDraft" class="step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop pull-right" value="${draft}" />
										</c:if>
									</form>
									<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') && (isAdmin or eventPermissions.owner)}">
										<spring:message code="event.cancel.draft" var="cancelDraftLabel" />
										<spring:message code="cancel.event.button" var="cancelEventLabel" />
										<a href="#confirmCancelEvent" role="button" class="btn btn-danger marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal">${event.status eq 'DRAFT' ? cancelDraftLabel : cancelEventLabel}</a>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- NEw add -->
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form:form modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/cancelEvent" method="post">
				<form:hidden path="id" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">

							<label> <spring:message code="eventsummary.confirm.to.cancel" /> </br> <spring:message code="eventsummary.reason" /> :
							</label>
						</div>
						<div class="form-group col-md-6">
						<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<form:textarea path="cancelReason" id="cancelReason" class="width-100" placeholder="${reasonCancel}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" onclick="javascript:doCancel();" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="confirm.meeting.delete" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/${eventType}/deleteMeeting?meetingId=" title='<spring:message code="tooltip.delete" />'> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn pull-right btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/createRftMeeting.js?2"/>"></script>
<script type="text/javascript">
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#meetingListNext,#priviousStep,#idvewbutton,#bubble';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	function updateLink(id) {
		var link = $("a#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}
	
		$(document).ready(function() {

			$('#rfxCancelEvent').click(function() {
				var cancelRequest = $('#cancelReason').val();
				if (cancelRequest != '') {
					$(this).addClass('disabled');
				}
			});
		});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>