<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxCreatingQuestionnaires" code="application.rfx.create.questionnaires" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingQuestionnaires}] });
});
</script>
<div id="sb-site">
	<div id="page-wrapper">
		<div id="page-content-wrapper">
			<div id="page-content">
				<div class="container">
					<ol class="breadcrumb">
						<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
						</a></li>
						<li class="active">${eventType.value}</li>
					</ol>
					<!-- page title block -->
					<div class="Section-title title_border gray-bg">
						<h2 class="trans-cap"><spring:message code="questionnaire.label" /></h2>
						<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${event.status}</h2>
					</div>
					<div class="clear"></div>
					<div class="example-box-wrapper wigad-new">
						<jsp:include page="eventHeader.jsp" />
						<div class="tab-main-inner">
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="clear"></div>
							<div class="">
								<div class="meeting2-heading">
									<h3><spring:message code="questionnaire.create.list" /></h3>
									<button id="addQuestionaree" class="btn btn-info hvr-pop toggleCreateBq marg-left-20 marg-right-20 marg-top-10 marg-bottom-10 pull-right" type="button" data-original-title="Add Questionnaire"><spring:message code="questionnaire.add.button" /></button>
								  <c:if test="${event.status eq 'DRAFT'}">
									<button class="pull-right btn ph_btn_midium btn-success reArrangeOrder hvr-pop marg-top-10 marg-bottom-10" type="button" data-toggle="tooltip" data-original-title='<spring:message code="rearrange.order"/>'><spring:message code="rearrange.order"/></button>
								  </c:if>
								</div>
								<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" style="display: none;">
									<form:form id="idCreateRftCq" class="form-horizontal bordered-row has-validation-callback" action="${pageContext.request.contextPath}/buyer/${eventType}/createRftCq" method="post" modelAttribute="rftCq">
										<form:hidden id="eventIdHiddenField" path="rfxEvent.id" />
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
										<input id="cqId" name="id" value="" type="hidden">
										<header class="form_header"></header>
										<jsp:include page="createAddCq.jsp" />
									</form:form>
								</div>
							</div>
							<div id="tab-5" class="tab-content current doc-fir pad_all_15" style="display: none;">
								<div class="doc-fir-inner1">
									<h3><spring:message code="questionnaire.table.title" /></h3>
									<div class="Invited-Supplier-List dashboard-main">
										<div class="Invited-Supplier-List-table  add-supplier id-list">
											<div class="ph_tabel_wrapper">
												<div class="main_table_wrapper ph_table_border payment marg-bottom-20 document-table">
													<table class="table ph_table border-none  documents-page fix-top-head " width="100%" border="0" cellspacing="0" cellpadding="0">
														<thead>
															<tr>
																<%-- <th class="col-md-1"><spring:message code="application.no" /></th> --%>
																<th class="col-md-1 pad-left align-left"><spring:message code="application.action1"/></th>
																<th class="col-md-1 pad-left align-left"><spring:message code="rfaevent.no.col"/></th>
																<th class="col-md-4 align-left"><spring:message code="label.name" /></th>
																<th class="col-md-5 align-left"><spring:message code="application.description" /></th>
															</tr>
														</thead>
														<!-- </table>
													<table class="ph_table table border-none tab_trans timezone induslist documents-page" width="100%" border="0" cellspacing="0" cellpadding="0"> -->
														<tbody class="catecontent">
															<c:forEach items="${cqList}" var="cq" varStatus="loop">
																<tr>
																	<%-- <td class="col-md-1">${loop.index + 1}</td> --%>
																	<td class="col-md-1 align-left" data-id="${cq.id}">
																		<%-- <span title="" class="pull-left font-size-26">
																			<button type="submit" name="submitCq" class="btn view editCq">
																				<img src="<c:url value="/resources/images/edit1.png"/>" />
																			</button>
																		</span> --%>
																	
																		<form id="formShowCqItems" action="${pageContext.request.contextPath}/buyer/${eventType}/showCqItems" method="post">
																			<span class="pull-left font-size-26"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="cqId" value="${cq.id}" />
																			<input id="eventIdForShowCqItems" type="hidden" name="eventId" value="${event.id}" /> 
																				<button type="submit" name="submitCq" class=" view" id="idvewbutton" data-placement="top" title='<spring:message code="tooltip.edit" />'>
																					<img style="width: 20px;" src="<c:url value="/resources/images/edit1.png"/>" />
																				</button>
																			</span>
																		</form> <c:if test="${event.status eq 'DRAFT' || event.suspensionType eq 'DELETE_NOTIFY' || event.suspensionType eq 'DELETE_NO_NOTIFY'}">
																			<span class="pull-left font-size-26"> <form:form action="${pageContext.request.contextPath}/buyer/${eventType}/deleteCq" method="post" id="delete_${cq.id}" modelAttribute="rftCq">
																					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																					<form:hidden path="id" value="${cq.id}" />
																					<input type="hidden" class="cqid" value="${cq.id}" />
																					<a class="deleteCq btn" class="btn view" data-placement="top" title='<spring:message code="tooltip.delete" />'> <img src="<c:url value="/resources/images/delete1.png"/>" />
																					</a>
																				</form:form>
																			</span>
																		</c:if>
																	</td>
																	<c:if test="${cq.cqOrder != null}">
																	<td class="col-md-1 align-left" style="padding-left: 18px;">${cq.cqOrder}</td>
																	</c:if>
																	<c:if test="${cq.cqOrder == null}">
																	<td class="col-md-1 align-left" style="padding-left: 18px;">${loop.index + 1}</td>
																	</c:if>
																	<td class="col-md-4 align-left">${cq.name}</td>
																	<td class="col-md-5 align-left">${cq.description}</td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="clear"></div>
								<div class="row">
									<div class="col-md-12">
										<div class="table_f_action_btn">
											<spring:url value="cqPrevious" var="cqPrevious" htmlEscape="true" />
											<form class="bordered-row" id="submitPriviousForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/${eventType}/cqPrevious">
												<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<button type="submit" class="btn btn-lg btn-black hvr-pop hvr-rectangle-out1" value="Previous" name="previous" id="priviousStep">
													<spring:message code="application.previous" />
												</button>
											</form>
											<spring:url value="cqNext" var="cqNext" htmlEscape="true" />
											<form class="bordered-row" id="submitNextForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/${eventType}/cqNext">
												<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<button type="submit" class="btn hvr-pop hvr-rectangle-out btn-lg btn-info hvr-pop hvr-rectangle-out" value="Next" id="nextStep">
													<spring:message code="application.next" />
												</button>
											</form>
											<spring:message code="application.draft" var="draft" />
											<form action="${pageContext.request.contextPath}/buyer/${eventType}/cqSaveDraft" method="post" class="bordered-row pull-right">
												<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<c:if test="${event.status eq 'DRAFT'}">
													<input type="submit" id="idCqSaveDraft" class="step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop pull-right" value="${draft}" />
												</c:if>
											</form>
											<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') && (isAdmin or eventPermissions.owner)}">
											<spring:message code="event.cancel.draft" var="cancelDraftLabel" />
											<spring:message code="cancel.event.button" var="cancelEventLabel" />
												<a href="#confirmCancelEvent" role="button" class="btn btn-danger marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal" id="cancelEve">${event.status eq 'DRAFT' ? cancelDraftLabel : cancelEventLabel}</a>
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
	</div>
</div>
<div class="modal fade" id="myModalCqDelete" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="application.confirm.delete" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="rfaevent.confirm.delete.questionnaire" /></label> <input type="hidden" name="confirmRemoveCqId" id="confirmRemoveCqId">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confirmRemoveenvlop"><spring:message code="application.delete" /></button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.cancel" /></button>
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
														<label> <spring:message code="eventsummary.confirm.to.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
						<spring:message	code="event.reason.cancellation.placeholder" var="reasonCancellation"/>
							<form:textarea path="cancelReason" id="cancelReason" class="width-100" placeholder="${reasonCancellation}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit"  id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left"><spring:message code="application.yes" /></form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="resetReasonField();"><spring:message code="application.no2" /></button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmReArrangeOrder" role="dialog">
		<div class="modal-dialog" style="width: 90%; max-width: 800px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3>Rearrange Order</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
				</div>
				<input type="hidden" name="eventId" id="eventId" value="${event.id}"> 
				<input type="hidden" name="eventType" id="eventType" value="${eventType}">
				<div class="modal-body">
				    <table class="header_table header bq_font" width="100%" cellpadding="0" cellspacing="0" id="table_id">
					   <tr>
						<th class="width_50 width_60_fix move_col align-left"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyph-icon fa-arrows-v move_icon"></i>
						</a></th>
						<th class="width_100 width_135_fix align-left font-ch-weight" ><spring:message code="rfaevent.no.col"/></th>
					     <th class="width_200 width_200_fix align-left font-ch-weight"><spring:message code="application.name"/></th>
					    <th class="width_200 width_200_fix align-left font-ch-weight"><spring:message code="application.description"/></th>
					</tr>
				    </table>
				    
			   <section id="demo">
				<ul class="sortable ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="sortable">
				<c:forEach items="${cqList}" var="cq" varStatus="loop">
					<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${cq.id}">
						<div class="menuDiv">
						  <table class="table data ph_table diagnosis_list sorted_table" id="table_id" style=" background: #fff;">
						   <tr class="sub_item item" data-id="${cq.id}">
						    <td class="width_50 width_60_fix move_col align-left"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyph-icon fa-arrows-v move_icon"></i>
							</a></td>
						 <c:if test="${cq.cqOrder != null}">
					     <td class="width_100 width_135_fix align-left orderChange"><span>${cq.cqOrder}</span></td>
					     </c:if>
					     <c:if test="${cq.cqOrder == null}">
					      <td class="width_100 width_135_fix align-left orderChange"><span>${loop.index+1}</span></td>
					     </c:if>
					     <td class="width_200 width_200_fix align-left"><span class="item_name">${cq.name}</span></td>
					     <td class="width_200 width_200_fix align-left"><span class="item_name">${cq.description}</span></td>
				        </tr>
					  </table>
					 </div>
				   </li>
		       </c:forEach>
			 </ul>
		    </section>
			     </div>
			     <div class="modal-footer  text-center">
					<button type="button" class="btn btn-info ph_btn_midium" id="updateCqOrder" data-dismiss="modal">
						<spring:message code="application.update" />
					</button>
					<button class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1 " data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div> 
</script>
<script type="text/javascript">
	function resetReasonField() {
		document.getElementById('cancelReason').value = '';
	}
</script>
<script src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<!-- Theme layout -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/eventCq.js"/>"></script>
<script type="text/javascript">
	<c:if test="${(eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'KEEP_NOTIFY' or event.suspensionType == 'KEEP_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
		var allowedFields = '#meetingListNext,#priviousStep,#nextStep,,.s1_view_desc,#idViewButton,#bubble,#cancelEve';
	$(window).bind('load', function() {
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	var allowedFields = '#meetingListNext,#priviousStep,#nextStep,.s1_view_desc,#idvewbutton,#bubble';
		$(window).bind('load', function() {
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
});
</c:if>
	 
	$(document).ready(function() {

		$('#rfxCancelEvent').click(function() {
			$(this).addClass('disabled');
		});
		
		$('.deleteCq').click(function(e) {
			e.preventDefault();
			$('#confirmRemoveCqId').val($(this).siblings('.cqid').val());
			$('#myModalCqDelete').modal('show');

		});

		$(document).delegate('#confirmRemoveenvlop', 'click', function(e) {
			e.preventDefault();
			var id = $('#confirmRemoveCqId').val();
			$('#myModalBqDelete').modal('hide');
			$('#delete_' + id).submit();
		});
		
		$( function() {
			$("#sortable").sortable({
			    //update ordering number
				update: function (event, ui) {
			        $('.orderChange span').each(function (i) {
			        	 var number = i + 1;
			             $(this).text(number);
			        });
			    }
			});
			$("#sortable").disableSelection();
	  });
	});
</script>
<style>
.deleteCq {
	background: transparent none repeat scroll 0 0 !important;
	padding: 0 !important;
}

.col-md-3.pad-left.align-left {
	padding-left: 45px;
}
.header {
    position: relative !important;
}
.width_60_fix {
    width: 64px;
}
.width_135_fix {
    width: 135px;
}
.header+* {
	margin-top: 0px !important;
}
.ph_table {
    border: 1px solid #e8e8e8;
    border-top: 0;
    -moz-border-radius: 2px;
    border-radius: 2px;
}
ul {
    list-style-type: none;
}
.font-ch-weight{
font-weight: 600 !important;
}
</style>