<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<c:if test="${eventMeeting.id != null}">
					<h2 class="trans-cap tender-request-heading"><spring:message code="summaryevent.edit.meeting"/></h2>
				</c:if>
				<c:if test="${eventMeeting.id == null}">
					<h2 class="trans-cap tender-request-heading"><spring:message code="label.meeting.create"/></h2>
				</c:if>
			<%-- 	<div class="right-header-button">
					<button class="hvr-pop hvr-rectangle-out3 ph_btn_midium">
						<spring:message code="button.event.inbox" />
					</button>
				</div> --%>
			</div>
			<div class="clear"></div>
			<div class="clear"></div>
			<div class="example-box-wrapper wigad-new">
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<div id="idMessageJsp">
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				</div>
				<div class="Invited-Supplier-List white-bg">
					<div class=" meeting2-heading">
						<c:if test="${eventMeeting.id != null}">
							<h3><spring:message code="summarydetails.approval.comments"/></h3>
						</c:if>
						<c:if test="${eventMeeting.id == null}">
							<h3><spring:message code="label.meeting.create"/></h3>
						</c:if>
					</div>
					<form:form id="demo-form4" cssClass="form-horizontal bordered-row " action="${pageContext.request.contextPath}/buyer/${eventType}/createMeeting" method="post" modelAttribute="eventMeeting">
						<input type="hidden" name="source" value="summary">
						<form:hidden path="id" id="id" />
						<form:hidden path="rfxEvent.id" />
						<form:hidden path="rfxEvent.documentCompleted" />
						<form:hidden path="rfxEvent.documentReq" />
						<form:hidden path="rfxEvent.supplierCompleted" />
						<form:hidden path="rfxEvent.eventVisibility" />
						<form:hidden path="rfxEvent.meetingCompleted" />
						<form:hidden path="rfxEvent.meetingReq" />
						<form:hidden path="rfxEvent.cqCompleted" />
						<form:hidden path="rfxEvent.questionnaires" />
						<form:hidden path="rfxEvent.bqCompleted" />
						<form:hidden path="rfxEvent.billOfQuantity" />
						<form:hidden path="rfxEvent.envelopCompleted" />
						<form:hidden path="rfxEvent.summaryCompleted" />
						<jsp:include page="meeting.jsp" />
						<div class="col-md-2 marg-top-20 wAuto marg-bottom-10 col-xs-12 mr-lft">
							<form:button type="submit" id="createButton" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out">
								${btnValue}
							</form:button>
						</div>
					</form:form>
					<div class="col-md-2 marg-top-20 marg-bottom-10 col-xs-12">
						<form id=listForm method="post" action="${pageContext.request.contextPath}/buyer/${eventType}/meetingList">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" id="${event.id}" name="eventId" value="${event.id}">
							<input type="hidden" name="source" value="summary">
							<button type="submit" class="btn btn-black ph_btn marg-left-10 hvr-pop hvr-rectangle-out1">
								<spring:message code="application.cancel" />
							</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!--Add Past Meeting popup not implemented -->
<div class="modal" id="myModal4">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="label.meeting.past.add" />
				</h3>
				<button class="close for-absulate" data-dismiss="modal" type="button">
					<spring:message code="application.button.x" />
					reminderList.marginDisable
				</button>
			</div>
			<div class="form-group float-left width100 pad_all_15 white-bg">
				<form action="">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="event_form">
						<div class="form_field">
							<div class="form-group">
								<label class="col-sm-4 control-label">
									<spring:message code="label.meeting.supplier.name" />
								</label>
								<div class="col-sm-6 col-md-6">
									<div class="input_wrapper">
										<spring:message code="label.meeting.email" var="email" />
										<input type="text" data-validation="email" placeholder="${email}" class="form-control mar-b10">
									</div>
								</div>
								<div class="col-sm-1 col-md-1">
									<div class="plus_btn_wrap gray ">
										<a href="#" data-toggle="tooltip" data-placement="top" title='<spring:message code="tooltip.add.more"/>' class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out">
											<i aria-hidden="true" class="glyph-icon icon-plus"></i>
										</a>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						reminderList.marginDisable
						<div class="form-group">
							<label class="col-sm-4 control-label">
								<spring:message code="label.meeting.remark" />
							</label>
							<div class="col-sm-6 col-md-6">
								<div class="form-group">
									<textarea placeholder="${remark}" rows="4" name="textarea1" data-validation="required" class="form-control"></textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						<div class="form-group">
							<label class="col-sm-4 control-label">
								<spring:message code="label.meeting.date" />
							</label>
							<div class="col-sm-6 col-md-6">
								<div class="input-prepend input-group">
									<input type="text" data-validation="birthdate" data-validation-format="mm-dd-yyyy" class="bootstrap-datepicker1 form-control for-clander-view">
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						confDelReminder
						<div class="form-group">
							<label class="col-sm-4 control-label">
								<spring:message code="label.meeting.personal.details" />
							</label>
							<div class="col-sm-6 col-md-6">
								<div class="form-group">
									<textarea rows="4" name="textarea1" data-validation="required" class="form-control"></textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						<div class="col-sm-offset-4 col-md-6 col-sm-6">
							<div class="form-group">
								<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">
									<spring:message code="button.supplier.add" />
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<div class="modal fade" id="addReminder" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="label.add.reminder" />
				</h3>
				<button class="close for-absulate" data-dismiss="modal" type="button" style="border: none;">
					<i class="fa fa-times-circle"></i>
				</button>
			</div>
			<form id="addMeetingReminder">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<label>
						<spring:message code="label.remind.me" />
					</label>
					<input name="remindMe" id="remindMe" data-validation="required, number" data-validation-allowing="range[1;100]" type="text" class="form-control col-md-6" />
					<div class="col-md-6 align-left">
						<select data-validation="required" class="custom-select remindMeTime" name="remindMeTime" id="">
							<c:forEach items="${intervalType}" var="interval">
								<option value="${interval}">${interval}</option>
							</c:forEach>
						</select>
					</div>
					<div class="before-time-msg">
						<spring:message code="label.meeting.start.date.time" />
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 pd-lf">
					<button type="button" id="reminderButton" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" data-dismiss="modal">
						<spring:message code="application.save" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!--pop up  -->
<div class="modal fade" id="confirmDeleteContect" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label>
					<spring:message code="summaryevent.sure.want.delete"/> "<b></b>" <spring:message code="summaryevent.from.contact.list"/>
				</label>
				<input type="hidden" id="deleteId" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelContact">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmDeleteDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label>
					<spring:message code="application.confirm.message.delete" />
				</label>
				<input type="hidden" id="deleteIdDocument" />
				<input type="hidden" id="deleteRefIdDocument" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelDocument">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmDeleteReminder" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="rfaevent.confirm.delete.reminder"/> </label>
				<input type="hidden" id="deleteIdReminder" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelReminder">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal" id="idCreateEditContactDlg">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="appliaction.contact.person"/></h3>
				<button class="close for-absulate" data-dismiss="modal" type="button" style="border: none;">
					<i class="fa fa-times-circle"></i>
				</button>
			</div>
			<div class=" width100 pad_all_15 white-bg">
				<form id="contactPerson">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="row">
						<label class="col-sm-4 control-label"><spring:message code="label.name"/></label>
						<div class="col-sm-6 col-md-6">
							<div class="input_wrapper form-group">
								<input type="hidden" name="id" id="id" />
								<input type="text" name="contactName" id="contactName" data-validation="required length custom" data-validation-length="max64" data-validation-regexp="^[a-zA-Z0-9-.' ]*$" placeholder='<spring:message code="contact.name.placeholder"/>' class="form-control mar-b10 emptyData">
							</div>
						</div>
					</div>
					<div class="row">
						<label class="col-sm-4 control-label"><spring:message code="label.email"/></label>
						<div class="col-sm-6 col-md-6">
							<div class="input_wrapper form-group">
								<input type="text" name="contactEmail" id="contactEmail" data-validation="required email" placeholder='<spring:message code="contact.email.placeholder"/>' class="form-control mar-b10 emptyData">
							</div>
						</div>
					</div>
					<div class="row">
						<label class="col-sm-4 control-label"><spring:message code="application.contactno"/></label>
						<div class="col-sm-6 col-md-6">
							<div class="input_wrapper form-group">
								<input type="text" name="contactNumber" id="contactNumber" placeholder='<spring:message code="contact.number.placeholder"/>' data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" class="form-control mar-b10 emptyData">
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 text-center">
							<div class="form-group">
								<input type="submit" id="addContact" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" value="Add" />
								<button type="button" data-dismiss="modal" class="btn btn-black ph_btn_small marg-left-10 hvr-pop hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<style>
input[readonly].for-clander-view, input[readonly].for-timepicker-view {
	cursor: default !important;
}

.wAuto {
	width: auto;
}

.mr-lft {
	margin-left: 364px;
}

.pd-lf {
	padding-left: 90px !important;
}
</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/createRftMeeting.js?2"/>"></script>
