<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<title><spring:message code="rftenvelop.title" /></title>
<spring:message var="rfxCreatingEnvelope" code="application.rfx.create.envelopes" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingEnvelope}] });
});
</script>
<div id="sb-site">
	<div id="page-wrapper">
		<div id="page-content-wrapper">
			<div id="page-content">
				<div class="container">
					<!-- pageging  block -->
					<ol class="breadcrumb">
						<li>
							<a href="${pageContext.request.contextPath}/buyer/buyerDashboard">
								<spring:message code="application.dashboard" />
							</a>
						</li>
						<li class="active">${eventType.value}</li>
					</ol>
					<section class="create_list_sectoin">
						<div class="Section-title title_border gray-bg">
							<h2 class="trans-cap">
								<i aria-hidden="true" class="glyph-icon"><img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/envelop-icon.png" alt="pr" /></i>
								<spring:message code="rftenvelope.envelopes" />
							</h2>
							<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${event.status}</h2>
						</div>
						<jsp:include page="eventHeader.jsp"></jsp:include>
						<c:if test="${!event.rfxEnvelopeReadOnly}">
						<div  class="column_button_bar2 clearfix" >
							<div class="left_button">
								<spring:url value="${pageContext.request.contextPath}/buyer/${eventType}/showEnvelop" var="showEnvelop" htmlEscape="true" />
								<form action="${pageContext.request.contextPath}/buyer/${eventType}/showEnvelop" method="post">
									<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<button id="createEnvelop" type="submit" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out hvr-pop hvr-rectangle-out">
										<spring:message code="label.rftenvelop.create" />
									</button>
								</form>
							</div>
						</div>
						</c:if>
						<div class="row clearfix">
							<div class="col-sm-12">
								<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							</div>
						</div>
						<c:if test="${empty envelopList}">
							<div class="sa_envelop_accordian panel">
								<div class="panel-heading" role="tab" id="headingOne">
									<h4 class="panel-title">
										<a style="background: none;"><spring:message code="application.no.records.found" /></a>
									</h4>
								</div>
							</div>
						</c:if>
						<div class="row clearfix">
							<div class="col-sm-12">
								<section class="sa_envelop_accordian">
									<div class="panel-group sa_ph_accordian" id="accordion1" role="tablist" aria-multiselectable="true">
										<c:forEach items="${envelopList}" var="envelop" varStatus="status">
											<div class="panel panel-default">
												<div class="panel-heading" role="tab" id="headingOne">
													<h4 class="panel-title">
														<a role="button" data-idenv="${envelop.id}" class="accordian" data-toggle="collapse" data-parent="#accordion" id="getBqcq" href="#collapseOne${envelop.id}" aria-expanded="true" aria-controls="collapseOne">
															<i aria-hidden="true" class="glyph-icon icon-envelope-o" ></i> ${envelop.envelopTitle}
															<span class="sa_normal_txt"></span>
															<br />
															<span class="sa_sealed_date">${envelop.description}</span>
														</a>
													</h4>
												</div>
												<div id="collapseOne${envelop.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
													<div class="panel-body" style="padding: 15px 0;">
														<div class="col-md-3">
															<ul class="s1_question_list bqcqListsEnvlop">
															</ul>
														</div>
														<div class="col-md-4"></div>
														<div class="col-md-5">
															<spring:url value="${pageContext.request.contextPath}/buyer/${eventType}/editEnvelope" var="editEnvelope" htmlEscape="true" />
															<form id="editEnvelopeForm" action="${pageContext.request.contextPath}/buyer/${eventType}/editEnvelope" method="post" class="pull-left mrg_10">
																<input type="hidden" id="envelopId" value="${envelop.id}" name="envelopId"> <input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																<input type="image" id="idEditEnvelope" data-placement="top" title='<spring:message code="tooltip.edit" />' src="${pageContext.request.contextPath}/resources/images/edit1.png">
															</form>
															<c:if test="${!event.rfxEnvelopeReadOnly}">
														     <spring:url value="${pageContext.request.contextPath}/buyer/${eventType}/deleteEnvelope" var="deleteEnvelope" htmlEscape="true" />
															
															<form onsubmit="" action="${pageContext.request.contextPath}/buyer/${eventType}/deleteEnvelope" method="post" class="pull-left mrg_10" id="delete_${envelop.id}">
																<input type="hidden" class="envelopId" value="${envelop.id}" name="envelopId"> <input type="hidden" class="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}"
																	value="${_csrf.token}" />
																<a class="deleteEnvelop" href="javascript:void(0);" data-placement="top" title='<spring:message code="tooltip.delete" />'>
																	<img src="${pageContext.request.contextPath}/resources/images/delete1.png">
																</a>
															</form>
														</c:if>
															
														</div>
													</div>
												</div>
											</div>
										</c:forEach>
									</div>
								</section>
							</div>
						</div>
						<div class="clear"></div>
						<div class="row">
							<div class="col-md-12">
								<div id="bqcq-error-dialog"></div>
								<div class="table_f_action_btn">
									<spring:url value="${pageContext.request.contextPath}/buyer/${eventType}/envelopPrevious" var="envelopPrevious" htmlEscape="true" />
									<form action="${pageContext.request.contextPath}/buyer/${eventType}/envelopPrevious" method="post" style="float: left;">
										<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<button type="submit" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1" id="priviousStep">
											<spring:message code="application.previous" />
										</button>
									</form>
									<spring:url value="envelopNext" var="envelopNext" htmlEscape="true" />
									<form class="bordered-row pull-left" id="submitNextForm" method="post" action="${pageContext.request.contextPath}/buyer/${eventType}/envelopNext">
										<spring:message code="rftenvelop.envelope.required" var="required" />
										<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<button type="submit" class="btn hvr-pop hvr-rectangle-out btn-lg btn-info hvr-pop hvr-rectangle-out" value="Next" id="nextStep"><spring:message code="application.next" /></button>
									</form>
									<spring:message code="application.draft" var="draft" />
									<spring:url value="${pageContext.request.contextPath}/buyer/${eventType}/envelopSaveDraft" var="envelopSaveDraft" htmlEscape="true" />
									<form action="${pageContext.request.contextPath}/buyer/${eventType}/envelopSaveDraft" method="post" style="float: right;">
										<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<c:if test="${event.status eq 'DRAFT'}">
											<input type="submit" id="idEnvelopSaveDraft" class="step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop pull-right" value="${draft}" />
										</c:if>
									</form>
									<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') && (isAdmin or eventPermissions.owner)}">
									<spring:message code="event.cancel.draft" var="cancelDraftLabel" />
									<spring:message code="cancel.event.button" var="cancelEventLabel" />
										<a href="javascript:void(0);" role="button" class="btn btn-danger marg-right-10 ph_btn hvr-pop right-header-button cancelEvent" id="idCancelEvent" data-toggle="modal">${event.status eq 'DRAFT' ? cancelDraftLabel : cancelEventLabel}</a>
									</c:if>
								</div>
							</div>
						</div>
					</section>
				</div>
			</div>
		</div>
	</div>
</div>
<!--pop up  -->
<div class="modal fade" id="confirmDeleteenvlp" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form action="${pageContext.request.contextPath}/buyer/${eventType}/deleteEnvelope" method="post" class="pull-left mrg_10" id="delete_${envelop.id}">
				<div class="modal-body">
					<label>
						<spring:message code="envelope.delete.popup" />
					</label>
					<input type="hidden" id="envepID" name="envelopId" /> <input type="hidden" id="eventId" name="eventId" />
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confenvlp">
						<spring:message code="application.delete" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
		</form>
		</div>
	</div>
</div>
<div class="modal fade" id="myModalEnvelopDelete" role="dialog">
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
					<spring:message code="envelope.delete.popup" />
				</label>
				<input type="hidden" name="confirmRemoveenvelopId" id="confirmRemoveenvelopId">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confirmRemoveenvlop">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
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
				<label>
					<spring:message code="application.country.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/deleteCountry?countryId=" title='<spring:message code="tooltip.delete" />'>
					<spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="eventsummary.confirm.cancel" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal" onclick="resetReasonField();">&times;</button>
			</div>
			<form:form modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/cancelEvent" method="post">
				<form:hidden path="id" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label>
								<spring:message code="eventsummary.confirm.to.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="cancelreason"/>
							<form:textarea path="cancelReason" id="cancelReason" class="width-100" placeholder="${cancelreason}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left"><spring:message code="application.yes" /></form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="resetReasonField();"><spring:message code="application.no2" /></button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<style>
.s1_question_list.bqcqListsEnvlop>li {
	cursor: default;
	margin-bottom: 2px;
	padding: 5px;
}


.disable{
  pointer-events: none !important;

}
</style>
<!-- WIDGETS -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});
</script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Theme layout -->
<!-- PieGage -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage-demo.js"/>"></script>
<!-- Morris charts -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/raphael.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/morris/morris.js"/>"></script>
<script type="text/javascript">
	function resetReasonField() {
		document.getElementById('cancelReason').value = '';
	}
</script>
<!-- Morris charts demo -->
<script>

<c:if test="${(eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'KEEP_NOTIFY' or event.suspensionType == 'KEEP_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
	$(window).bind('load', function() {
		var allowedFields = '#nextStep,#priviousStep,.accordian,#idViewButton,#bubble,#idEditEnvelope,#idCancelEvent,#idEnvelopSaveDraft,.s1_view_desc';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		
		var allowedFields = '#nextStep,#priviousStep,.accordian,#bubble,#idEditEnvelope,.s1_view_desc';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>

	$(document).ready(function() {
		
		$('#rfxCancelEvent').click(function() {
			$(this).addClass('disabled');
		});

		$('.role-bottom').find('input[type="checkbox"]').change(function() {

			var roleUperBlok = '';
			$(this).parents('.role-bottom').find('input[type="checkbox"]:checked').each(function() {
				var roleValue = $(this).val();
				roleUperBlok += '<div class="role-upper-inner">' + roleValue + '<a href="#" data-val="' + roleValue + '">X</a></div>';
			});
			$(this).parents('.chk_scroll_box').prev('.role-upper').html(roleUperBlok);
		});

		/* this code for refresh multiselcte checkbox on load time */
		$('.role-bottom').find('input[type="checkbox"]').trigger('change');

		$(document).delegate('.role-upper a', 'click', function(e) {

			e.preventDefault();
			var checkboxVal = $(this).attr('data-val');
			var checkObj = $(this).parents('.role-upper').next('.chk_scroll_box').find('input[type="checkbox"][value="' + checkboxVal + '"]');

			checkObj.prop('checked', false);
			$(this).parent('.role-upper-inner').remove();
			$.uniform.update(checkObj);
		});

		$('#question_list').perfectScrollbar({
			suppressScrollX : true
		});
		
		
	});

	$(function() {

		$('[data-toggle="collapse"]').click(function(e) {
			e.preventDefault();
			var target_element = $(this).attr("href");
			var envelopId = $.trim($(this).attr('data-idenv'));
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getBuyerContextPath('bqCqList'),
				data : {
					'envelopId' : envelopId
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					var html = '';
					if ($.isEmptyObject(data)) {
						html = 'No items found in this envelope.';
					}
					$.each(data, function(i, item) {
						html += '<li>' + item + '</li>';
					});
					$(target_element).find('.bqcqListsEnvlop').html(html);
				},
				error : function(error) {
					console.log(error);
				},
				complete : function() {
					$('#loading').hide();
				}
			});
			$(target_element).collapse('toggle');
			
			return false;
		});

		$(document).on("change", "#load_file", function() {
			$(".show_name").html($(this).val());
		});

		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#tags").autocomplete({
			source : availableTags
		});
		$("#tagres").autocomplete({
			source : availableTags
		});
	});
	$('.deleteEnvelop').click(function(e) {
		e.preventDefault();

		$('#confirmRemoveenvelopId').val($(this).siblings('.envelopId').val());
		$('#myModalEnvelopDelete').modal('show');
		return false;
	});
	$(document).delegate('#confirmRemoveenvlop', 'click', function(e) {
		e.preventDefault();
		var id = $('#confirmRemoveenvelopId').val();
		$('#myModalEnvelop').modal('hide');
		$('#delete_' + id).submit();
	});
	$('.cancelEvent').click(function(e) {
		e.preventDefault();
		$('#confirmCancelEvent').modal('show');
		return false;
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/rftEnvelop.js"/>"></script>