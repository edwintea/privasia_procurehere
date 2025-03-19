<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_REQUEST_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEdit" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxCreatingQuestionnaires" code="application.rfx.create.questionnaires" />
<spring:message code="sourcing.template.inactivate.label" var="inactivateLabel"/>
<spring:message code="buyer.dashboard.active" var="activeLabel"/>
<spring:message code="sourcing.template.inactive.label" var="inactiveLabel"/>
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
						<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/sourceTemplateList">
						<spring:message code="sourcing.templates" /> 
					</a></li> 
					<c:if test="${status eq 'DRAFT'}">
					 <li> <spring:message code="sourcingtemplate.create.template" /> </li>
					 </c:if>
					 	<c:if test="${status eq 'ACTIVE' || status eq 'INACTIVE'}">
					 <li> <spring:message code="sourcingtemplate.template.update" /> </li>
					 </c:if>
					 					 
<!-- 					<li  class="active"> -->
					
<%-- 						${template.formName} --%>
<!-- 					</li> -->
					</ol>
					<!-- page title block -->
					<div class="Section-title title_border gray-bg">
						<h2 class="trans-cap"><spring:message code="questionnaire.label" /></h2>
						<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${template.status}</h2>
					</div>

					<jsp:include page="sourcingTemplateHeader.jsp"></jsp:include>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
						<div class="clear"></div>
					<div class="clear"></div>
					<div class="example-box-wrapper wigad-new">
						<%-- 	<jsp:include page="eventHeader.jsp" /> --%>
						<div class="tab-main-inner">
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="clear"></div>
							<div class="">
								<div class="meeting2-heading">
<%-- 									<h3><spring:message code="questionnaire.create.list" /></h3> --%>
									<button id="addCqq" class="btn btn-info hvr-pop toggleCreateBq marg-left-20 marg-right-20 marg-top-10 marg-bottom-10 pull-right" type="button" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.add.quetionnaire"  />'><spring:message code="questionnaire.add.button" /></button>
								    <button id="rearrangeId" class="pull-right btn ph_btn_midium btn-success reArrangeCqOrder hvr-pop marg-left-20 marg-right-20 marg-top-10 marg-bottom-10 pull-right" type="button" data-toggle="tooltip" data-original-title='<spring:message code="rearrange.order"/>'><spring:message code="rearrange.order"/></button>
								</div>
								<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" style="display: none;">

									<form id="cqForm" method="post" action="${pageContext.request.contextPath}/buyer/saveSourcingFormCq/${formId}">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<div class="row marg-bottom-10">
											<div class="col-md-3">
												<label for="uom" class="marg-top-10">
													<spring:message code="questionnaire.label" />&nbsp;&nbsp;
													<spring:message code="label.name" />
												</label>
											</div>
											<div class="col-md-5">
												<input type="text" id="name" name="name" ,palceholder="Questionnaire name" data-validation="required length" class="form-control" data-validation-length="4-124" />
											</div>
										</div>
										<div class="row marg-bottom-10">
											<div class="col-md-3">
												<label for="uom" class="marg-top-10">
													<spring:message code="questionnaire.label" />&nbsp;&nbsp;
													<spring:message code="application.description" />
												</label>
											</div>
											<div class="col-md-5">
												<textarea rows="5" name="description" id="description" palceholder="Questionnaire Description" data-validation="length" class="form-control" data-validation-length="0-500"></textarea>
												<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
											</div>
										</div>
										<div class="row marg-bottom-10">
											<div class="col-md-3">&nbsp;</div>
											<div class="col-md-5">
												<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" name="Save">
													<spring:message code="application.create" />
												</button>
												<button type="button" class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1" name="Cancel" id="cqCancel">
													<spring:message code="application.cancel" />
												</button>
											</div>
										</div>

									</form>
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
																<th class="col-md-1 align-left">
																	<spring:message code="rfaevent.no.col" />
																</th>
																<th class="col-md-4 align-left">
																	<spring:message code="label.name" />
																</th>
																<th class="col-md-5 align-left">
																	<spring:message code="application.description" />
																</th>
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
																		<form action="${pageContext.request.contextPath}/buyer/sourcingCqItems" method="post">
																			<span class="pull-left font-size-26">
																				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="cqId" value="${cq.id}" />
																				<button type="submit" name="submitCq" class="view" id="idvewbutton" data-toggle="tooltip" data-placement="top" title=<spring:message code="tooltip.edit" />>

																					<img style="width: 20px;" src="<c:url value="/resources/images/edit1.png"/>" />
																				</button>
																			</span>
																		</form>
																		<span class="pull-left font-size-26">
																			<c:if test="${!isTemplateUsed}">
																				<form:form action="${pageContext.request.contextPath}/buyer/deleteSourcingFormCq" method="post" id="delete_${cq.id}">
																					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																					<input type="hidden" name="cqId" class="cqId" value="${cq.id}" />
																					<input type="hidden" name="formId" value="${formId}" />

																					<a class="deleteCq btn" class="btn view" data-toggle="tooltip" data-placement="top" title=<spring:message code="tooltip.delete" />> <img src="<c:url value="/resources/images/delete1.png"/>" /></a>
																				</form:form>
																			</c:if>
																		</span>
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
											<div class="d-flex-bet"> 
												<div style="display: flex;">
													<a class="btn1 btn btn-lg btn-black hvr-pop hvr-rectangle-out1 " id="linkPrevious" style="float: left;" href="<c:url value="/buyer/sourcingTemplateDocument/${formId}" />"><spring:message code="event.document.previous" /></a>

													<form class="bordered-row" id="submitNextForm" method="get" action="${pageContext.request.contextPath}/buyer/finishTemplate/${formId }">
														<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
														<c:if test="${status eq 'DRAFT' || status eq 'ACTIVE' || status eq 'INACTIVE'}">
															<c:if test="${!isTemplateUsed }">
																<button type="submit" class="btn btn1  hvr-pop hvr-rectangle-out btn-lg btn-info hvr-pop hvr-rectangle-out" value="Next" id="priviousStep">${finishButton}</button>
															</c:if>
														</c:if>
													</form>
												</div>
												<div>
													<a class="btn1 step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" href="${pageContext.request.contextPath}/buyer/sourceTemplateList"> <spring:message code="application.cancel" />
													</a>
												</div>
											</div>

											<spring:url value="cqNext" var="cqNext" htmlEscape="true" />

											<spring:message code="application.draft" var="draft" />
											<form action="${pageContext.request.contextPath}/buyer/${eventType}/cqSaveDraft" method="post" class="bordered-row pull-right">
												<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
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
<div class="modal fade" id="myModalCqDelete" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="application.confirm.delete" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="rfaevent.confirm.delete.questionnaire" /></label>
				<input type="hidden" name="confirmRemoveCqId" id="confirmRemoveCqId">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">

				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confirmRemoveenvlop"><spring:message code="tooltip.delete" /></button>
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
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>

		</div>
	</div>
</div>
<!-- TEMPLATE ACTIVE AS POPUP -->
<div class="flagvisibility dialogBox" id="prTemplateactivePopup" title="Template Active">
	<div class="float-left width100 pad_all_15 white-bg">
		<form>
			<div class="marg-top-20 tempaleData"></div>
			<input type="hidden" value="${sourceForm.status eq 'ACTIVE' ? inactiveLabel : activeLabel }" id="tempTitle">
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<form action="${pageContext.request.contextPath}/buyer/activeSourcingFormTemplate" method="post">
						<input type="hidden" id="templateId" name="templateId" value="${templateId}" /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<!-- 						<input type="submit" title="" class="btn btn-info marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1" id="activeSourcingTemp" data-original-title="Delete" -->
						<%-- 							value="${sourceForm.status eq 'ACTIVE' ? 'Inactive' : 'Active' }" />  --%>
						<a class="btn ph_btn_midium btn-tooltip hvr-pop  ${sourceForm.status eq 'ACTIVE' ? 'btn-danger' : 'btn-info hvr-rectangle-out' }" id="activeSourcingTemp" data-role="submit" data-original-title="Delete">${sourceForm.status eq 'ACTIVE' ? inactivateLabel : activeLabel }</a> <a class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1"><spring:message code="application.cancel" /></a>
					</form>
				</div>
			</div>
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
				<input type="hidden" name="formId" id="formId" value="${formId}">
				<div class="modal-body">
				    <table class="header_table header bq_font" width="100%" cellpadding="0" cellspacing="0" id="table_id">
					   <tr>
						<th class="width_50 width_60_fix move_col align-left"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyph-icon fa-arrows-v move_icon"></i>
						</a></th>
						<th class="width_100 width_135_fix align-left font-ch-weight" ><spring:message code="rfaevent.no.col"/></th>
					     <th class="width_200 width_200_fix align-left font-ch-weight"><spring:message code="application.name"/></th>
					    <th class="width_200 width_200_fix font-ch-weight"><spring:message code="application.description"/></th>
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
					<button type="button" class="btn btn-info ph_btn_midium" id="updateTemplateCqOrder" data-dismiss="modal">
						<spring:message code="application.update" />
					</button>
					<button class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1 " data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div> 
<script src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<!-- Theme layout -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});
	
	$(function() {
		$("[data-role=submit]").click(function() {
			$(this).closest("form").submit();
		});
	});

</script>

<style>
<!--
.btn1 {
	height: 38px !important;
	min-width: 180px !important;
	font-size: 16px !important;
	line-height: 38px;
}

.d-flex-bet {
    display: flex;
    justify-content: space-between;
}
-->

.header+* {
	margin-top: 0px !important;
}
.ph_table {
    border: 1px solid #e8e8e8;
    border-top: 0;
    -moz-border-radius: 2px;
    border-radius: 2px;
}
.width_60_fix {
    width: 64px;
}
.width_135_fix {
    width: 135px;
}
.font-ch-weight{
font-weight: 600 !important;
}
ul {
    list-style-type: none;
}
.header {
    position: relative !important;
}
</style>

<script type="text/javascript" src="<c:url value="/resources/js/view/eventCq.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/sourceformCq.js?1"/>"></script>
<script type="text/javascript">
	
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	var allowedFields = '#meetingListNext,#priviousStep,#idvewbutton,#bubble';
		$(window).bind('load', function() {
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
});
</c:if>
	 
	$(document).ready(function() {
		if(${isTemplateUsed}){
			$("#addCqq").hide();
			$('deleteCq').hide();
			$("#rearrangeId").hide();
		}
		
	
		$('.deleteCq').click(function(e) {
			e.preventDefault();
			$('#confirmRemoveCqId').val($(this).siblings('.cqId').val());
			$('#myModalCqDelete').modal('show');

		});

		$(document).delegate('#confirmRemoveenvlop', 'click', function(e) {
			e.preventDefault();
			var id = $('#confirmRemoveCqId').val();
			$('#myModalBqDelete').modal('hide');
			$('#delete_'+id).submit();
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
	  } );
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
</style>

<script>
 <c:if test="${buyerReadOnlyAdmin or !canEdit}">
	$(window).bind('load', function() {
		var allowedFields = '#cancelId, #dashboardLink, #listLink, .submitStep1 .linkPrevious, #idvewbutton';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
</c:if>
</script> 
