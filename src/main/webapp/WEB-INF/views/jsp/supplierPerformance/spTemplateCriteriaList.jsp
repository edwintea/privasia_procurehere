<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css?2"/>">

<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_SP_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEditt" />
<sec:authorize access="hasRole('ROLE_SP_TEMPLATE_VIEW_ONLY')" var="readOnlyPermission" />

<spring:message var="rfxCreatingQuestionnaires" code="application.rfx.create.questionnaires" />
<spring:message code="sourcing.template.inactivate.label" var="inactivateLabel"/>
<spring:message code="buyer.dashboard.active" var="activeLabel"/>
<spring:message code="sourcing.template.inactive.label" var="inactiveLabel"/>

<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingQuestionnaires}] });
});

function canEditt() {
	return "${canEditt}";
}
</script>

<style>
.deleteCq {
	background: transparent none repeat scroll 0 0 !important;
	padding: 0 !important;
}

.col-md-3.pad-left.align-left {
	padding-left: 45px;
}

.cq_action_box{
	margin: 15px 2% 0 2% !important; 
}

.icon-trash {
	margin-top: 0px !important;
	width: auto !important;
}

/* @media (min-width: 1024px) {
	.width-1000px {
	    width: 1000px !important;
	    margin-right: auto;
	    margin-left: auto;
	}
}
 */
 
 
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
.custom-icon {
	font-size: 22px;
	padding-right: 10px;
	color: #92A0B3;
}

.cqr_title3a {
    font-size: 14px !important;
    font-family: "open_sansregular","Helvetica Neue",Helvetica,Arial,sans-serif !important;
    color: #000 !important;
} 
</style>


<div id="sb-site">
	<div id="page-wrapper">
		<div id="page-content-wrapper">
			<div id="page-content">
				<div class="container">
					<ol class="breadcrumb">
						<li>
							<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" /></a>
						</li>
						<li>
							<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/spTemplateList"><spring:message code="sptemplate.list" /></a>
						</li>
						<li class="active">
							<spring:message code="sptemplate.create" />
						</li>
					</ol>
					<!-- page title block -->
					<div class="Section-title title_border gray-bg">
						<h2 class="trans-cap"><spring:message code="performance.criteria.lable" /></h2>
						<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${!template.performanceCriteriaCompleted ? 'DRAFT' : template.status}</h2>
					</div>

					<jsp:include page="spTemplateHeader.jsp"></jsp:include>
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
									<button id="addCriteria" class="btn btn-info hvr-pop toggleCreateBq marg-left-20 marg-right-20 marg-top-10 marg-bottom-10 pull-left" type="button" data-toggle="tooltip" data-original-title='Add criteria' data-templ-id="${template.id}" ><spring:message code="criteria.add.button" /></button>
								</div>
								<div class="Invited-Supplier-List create_sub marg-bottom-20 default-setting cq_form_google_form" style="display:none;" id="add_subCriteria_form" >
									<div class="cq_form_wrap">
										<div class="cq_form_inner">
											<c:url var="saveLink" value="/buyer/supplierPerformanceTemplateCriteriaList/saveSPTCriteria" />
											<form:form id="addSubCriteriaform" name="addSubCriteriaform" cssClass="form-horizontal" autocomplete="off" action="${saveLink}" method="post" modelAttribute="criteria">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
												<form:hidden path="id" id="criteriaId"/>
												<form:hidden path="template.id" value="${criteria.template.id}" name="template.id" id="templateId" />
												<form:hidden path="parent.id" value="${criteria.parent.id}" name="parent.id" id="parentId" />
												<div class="bottom_box">
													<div class="cq_row_parent box_active">
														<div class="cq_row">
															<div class="group">
																<form:input autocomplete="off" path="name" class="cqr_title3a" data-validation="required length" id="criteriaName" data-validation-length="max120" placeholder="Enter Sub Criteria" name="crtrName" />
															</div>
														</div>
														<div class="cq_row">
															<div class="group">
																<form:input autocomplete="off" path="description" class="cqr_title3a" data-validation="length" id="description" data-validation-length="max250" placeholder="Enter Sub Criteria Description" name="description" />
															</div>
														</div>
														<div class="bottom_box listboxBlock">
															<div class="width100 pull-left maxhgt400">
																<div class="box_qus_row1 drager_point">
																	<div class="col-md-12 col-sm-12 col-xs-12 row">
																		<div class="s1_creatItem_inlineControl col-md-2 col-sm-1 col-xs-1">
																			<div class="form-group ">
																				<form:input autocomplete="off" path="maximumScore" class="form-control" data-validation="required number" id="maximumScore" readonly="true" data-validation-length="max250" placeholder="e.g. 100" name="maxScore" data-validation-allowing="range[1;999]" data-validation-regexp="/^(0|[1-9]\d*)$/" 
																					data-validation-error-msg-number="Input value must be numeric within range from 1 to 999" data-validation-error-msg-container="#maxScoreErrorContainer" />	
																			</div>
																		</div>
																		<div class="col-md-6 col-sm6 col-xs-6">
																			<div class="form-group ">
																				<label class="marg-top-10"><spring:message code="label.maxscore" /></label>
																			</div>
																		</div>
																		<div class="form-group" id="maxScoreErrorContainer" style="padding-left: inherit;"></div>
																	</div>
																	<div class="col-md-12 col-sm-12 col-xs-12 row">
																		<div class="s1_creatItem_inlineControl col-md-2 col-sm-1 col-xs-1">
																			<div class="form-group ">
																				<fmt:formatNumber groupingUsed = "false" var="wtg" type="number" value="${criteria.weightage}" />
																				<form:input autocomplete="off" path="weightage" value="${wtg}" id="weightage" class="form-control validateNumber" placeholder="e.g. 20" data-validation="number required custom" 
																				data-validation-allowing="range[0.1;999.99],float" data-validation-error-msg-number="Input value must be numeric within range from 0.1 to 999.99"
																				data-validation-regexp="^\d{0,3}(\.\d{1,2})?$" data-validation-ignore="," data-validation-error-msg="Only numbers allowed, length should be less than 3 and after decimal 2 digits allowed"
																				 data-validation-error-msg-container="#weightageErrorContainer" />
																			</div>
																		</div>
																		<div class="col-md-6 col-sm6 col-xs-6">
																			<div class="form-group ">
																				<label class="marg-top-10"><spring:message code="label.weightage" /></label>
																			</div>
																		</div>
																		<div class="form-group" id="weightageErrorContainer" style="padding-left: inherit;"></div>
																	</div>
																</div>
															</div>
														</div>
														<div class="cq_action_box">
															<div class="row" style="margin-top: 10px;">
																<div class="pull-left">
																	<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="createSubCriteria" type="submit">
																		<spring:message code="application.create" />
																	</button>
																	<button class="btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out1" type="button" id="cancelSubCriteriaForm">
																		<spring:message code="application.cancel" />
																	</button>
																</div>
															</div>
														</div>
													</div>
												</div>
											</form:form>
										</div>
									</div>
								</div>
								
								<!-- CRITERIA TABLE -->
			 					<div class="main_table_wrapper width-1000px width-100">
									<div class="table_fix_header">
										<table class="header_table header bq_font" width="100%" cellpadding="0" cellspacing="0" id="table_id">
											<tr>
												<th class="width_50 width_50_fix "><a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i></a></th>
												<th class="width_150 width_150_fix">Actions</th>
												<th class="width_50 width_50_fix"><spring:message code="label.rftbq.th.No" /></th>
												<th class=""><spring:message code="label.criteria" /></th>
												<th class="width_150 width_150_fix align-right"><spring:message code="label.maxscore" /></th>
												<th class="width_150 width_150_fix align-right"><spring:message code="label.weightage" /></th>
											</tr>
										</table>
									</div>
									<div class="marg-for-cq-table ${eventPermissions.viewer or buyerReadOnlyAdmin or readOnlyPermission ? 'disabled' : ''}" style="margin-top: 56px;">
										<section id="demo">
											<ol class="${eventPermissions.viewer or buyerReadOnlyAdmin or isTemplateUsed or readOnlyPermission ? '' : 'sortable'} ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="bqitemList">
												<c:forEach items="${returnList}" var="bq">
													<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${bq.id}" data-parent="" data-level="${bq.level}" data-order="${bq.order}">
														<div class="menuDiv">
															<table class="table data ph_table diagnosis_list sorted_table" id="table_id" style=" background: #fff;">
																<tr class="sub_item" data-id="${bq.id}">
																	<td class="width_50 width_50_fix move_col align-left">
																		<a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
																		</a>
																	</td>
																	<td class="width_150 width_150_fix align-left">
																		<c:if test="${!isTemplateUsed }">
																			<c:if test="${bq.order == 0}">
																				<a title="" style="float:left;" class="Edit_section_table" data-criteria-id="${bq.id}" data-template-id="${bq.template.id}" href="#creat_seaction_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i>
																				</a>
																				<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="${bq.id}" data-templt-id="${bq.template.id}" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>
																				<a title="" style="float:left;" class="add_subCriteria_table" id="addSubCriteria" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-plus" title="Add" data-original-title=".icon-plus" aria-describedby="tooltip69681"></i> </a>
																			</c:if>
																			<c:if test="${bq.order != 0}">
																				<a title="" style="float:left;" class="editSubitme" data-crtr-id="${bq.id}" data-templ-id="${bq.template.id}" data-parent-id="${bq.parent.id}" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a>
																				<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="${bq.id}" data-templt-id="${bq.template.id}" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>
																			</c:if>
																		</c:if>
																	</td>
																	
																	<c:if test="${bq.order == 0}">
																		<td class="width_50 width_50_fix align-left">
																			<span class="sectionD section_name">${bq.level}.${bq.order}</span>
																		</td>
																		<td class="width_200 align-left">
																			<span class="sectionD section_name">${bq.name}</span>
																			<c:if test="${not empty bq.description}">
																				<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />
																				</a>
																				</span>
																			</c:if>
																			<p class="s1_tent_tb_description s1_text_small">${bq.description}</p>
																		</td>
																		<td class="width_150 width_150_fix align-right section_name">${bq.maximumScore}</td>
																		<td class="width_150 width_150_fix align-right section_name">${bq.weightage}</td>
																	</c:if>
																	<c:if test="${bq.order != 0}">
																		<td class="width_50 width_50_fix align-left">
																			<span class="sectionD">${bq.level}.${bq.order}</span>
																		</td>
																		<td class="width_200 align-left">
																			<span class="sectionD">${bq.name}</span>
																			<c:if test="${not empty bq.description}">
																				<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />
																				</a>
																				</span>
																			</c:if>
																			<p class="s1_tent_tb_description s1_text_small">${bq.description}</p>
																		</td>
																		<td class="width_150 width_150_fix align-right ">${bq.maximumScore}</td>
																		<td class="width_150 width_150_fix align-right ">${bq.weightage}</td>
																	</c:if>
																</tr>
						
															</table>
														</div> 
														<c:if test="${not empty bq.children}">
															<ol>
																<c:forEach items="${bq.children}" var="bqchild">
																	<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="${bqchild.id}" data-parent="${bq.id}" data-level="${bqchild.order}" data-order="${bqchild.order}">
																		<div class="menuDiv sub-color-change">
																			<table style=" background: #eef7fc;" class="table data ph_table diagnosis_list sorted_table" id="table_id">
																				<tr class="sub_item" data-id="${bqchild.id}">
																					<td class="width_50 width_50_fix move_col align-left">
																						<a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
																						</a>
																					</td>
																					<td class="width_150 width_150_fix align-left">
																						<c:if test="${!isTemplateUsed }">
																							<c:if test="${bqchild.order == 0}">
																								<a title="" style="float:left;" class="Edit_section_table" data-criteria-id="${bqchild.id}" data-template-id="${bqchild.template.id}" href="#creat_seaction_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i>
																								</a>
																								<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="${bqchild.id}" data-templt-id="${bqchild.template.id}" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>
																								<a title="" style="float:left;" class="add_subCriteria_table" id="addSubCriteria" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-plus" title="Add" data-original-title=".icon-plus" aria-describedby="tooltip69681"></i> </a>
																							</c:if>
																							<c:if test="${bqchild.order != 0}">
																								<a title="" style="float:left;" class="editSubitme" data-crtr-id="${bqchild.id}" data-templ-id="${bqchild.template.id}" data-parent-id="${bqchild.parent.id}" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a>
																								<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="${bqchild.id}" data-templt-id="${bqchild.template.id}" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>
																							</c:if>
																						</c:if>
																					</td>
																					
																					<c:if test="${bqchild.order == 0}">
																						<td class="width_50 width_50_fix align-left">
																							<span class="sectionD section_name">${bqchild.level}.${bqchild.order}</span>
																						</td>
																						<td class="width_200 align-left">
																							<span class="sectionD section_name">${bqchild.name}</span>
																							<c:if test="${not empty bqchild.description}">
																								<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />
																								</a>
																								</span>
																							</c:if>
																							<p class="s1_tent_tb_description s1_text_small">${bqchild.description}</p>
																						</td>
																						<td class="width_150 width_150_fix align-right section_name">${bqchild.maximumScore}</td>
																						<td class="width_150 width_150_fix align-right section_name">${bqchild.weightage}</td>
																					</c:if>
																					<c:if test="${bqchild.order != 0}">
																						<td class="width_50 width_50_fix align-left">
																							<span class="sectionD">${bqchild.level}.${bqchild.order}</span>
																						</td>
																						<td class="width_200 align-left">
																							<span class="sectionD">${bqchild.name}</span>
																							<c:if test="${not empty bqchild.description}">
																								<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />
																								</a>
																								</span>
																							</c:if>
																							<p class="s1_tent_tb_description s1_text_small">${bqchild.description}</p>
																						</td>
																						<td class="width_150 width_150_fix align-right">${bqchild.maximumScore}</td>
																						<td class="width_150 width_150_fix align-right">${bqchild.weightage}</td>
																					</c:if>
																				</tr>
																			</table>
																		</div>
																	</li>
																</c:forEach>
															</ol>
														</c:if>
													</li>
												</c:forEach>
											</ol>
										</section>
									</div>
								</div>
								<!-- CRITERIA TABLE -->
											

								
							</div>
							<div id="tab-5" class="tab-content current doc-fir pad_all_15" style="display: none;">
								<div class="clear"></div>
								<div class="row">
									<div class="col-md-12">
										<div class="table_f_action_btn">
											<spring:url value="cqPrevious" var="cqPrevious" htmlEscape="true" />
											<div class="d-flex-bet"> 
												<div style="display: flex;">
													<a class="btn1 btn btn-lg btn-black hvr-pop hvr-rectangle-out1" id="previousButton" style="float: left;" href="<c:url value="/buyer/editSPTemplate?id=${template.id}" />"><spring:message code="event.document.previous" /></a>

													<form class="bordered-row" id="submitNextForm" method="get" action="${pageContext.request.contextPath}/buyer/finishSPTemplate/${template.id}">
														<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
														<c:if test="${canEditt and !isTemplateUsed}">
															<button type="submit" class="btn btn1  hvr-pop hvr-rectangle-out btn-lg btn-info hvr-pop hvr-rectangle-out" value="Next" id="priviousStep">Update</button>
														</c:if>
													</form>
												</div>
											</div>

											<spring:url value="cqNext" var="cqNext" htmlEscape="true" />

											<spring:message code="application.draft" var="draft" />
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

<!-- Edit Section -->
<div id="crateNewSection" class="modal fade" role="dialog">
	<form:form id="addEditSectionForm" cssClass="form-horizontal" action="saveSPTCriteria" method="post" modelAttribute="criteria">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<div class="modal-dialog" style="width: 90%; max-width: 800px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center" id="sectionTitle">
						<spring:message code="application.add.section.button" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</div>
				<div class="modal-body">
					<form:hidden path="id" id="crtrId"/>
					<form:hidden path="template.id" value="${criteria.template.id}" name="template.id" id="templateId" />
					<div class="row marg-bottom-10">
						<div class="col-md-4 marg-top-15">
							<label> <spring:message code="sptemplate.criteria.name" />
							</label>
						</div>
						<div class="col-md-8">
							<form:input autocomplete="off" path="name" class="form-control" data-validation="required length" id="crtrName" data-validation-length="max120" placeholder='Criteria Name' name="crtrName" />
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<label> <spring:message code="application.description" />
							</label>
						</div>
						<div class="col-md-8">
							<form:textarea path="description" data-validation-length="max300" data-validation="length" data-validation-allowing=" _-" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" cssClass="form-control"
													id="idDescription" placeholder="Criteria Description" value="${description}" />
						</div>
					</div>
					<div class="row marg-bottom-10">
						<div class="col-md-4 marg-top-10">
							<label> <spring:message code="label.weightage" />
							</label>
						</div>
						<div class="col-md-8">
							<form:input autocomplete="off" type="text" id="wghtg" path="weightage" class="form-control validateNumber" placeholder='20' data-validation="required number custom"
							data-validation-error-msg-required="This is a required field"	
							data-validation-allowing="range[0.1;999.99],float" data-validation-error-msg-number="Input value must be numeric within range from 1 to 999"
							data-validation-regexp="^\d{0,3}(\.\d{1,2})?$" data-validation-ignore="," data-validation-error-msg="Only numbers allowed, length should be less than 3 and after decimal 2 digits allowed"/>
							
						</div>
					</div>	
					<div class="row marg-bottom-10" style="padding-left: 15px;">	
						<div class="col-md-8 col-sm-8 col-xs-8 marg-top-10">
							<div class="form-group ">
								<form:checkbox path="allowToUpdateSectionWeightage" id="allowToUpdateSectionWeightage" class="custom-checkbox" title="Allow Form Creator to update section weightage" label="Allow Form Owner to update section weightage" />
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer text-center">
					<button class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium" id="savePrSection" type="submit">Create</button>
					<button class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium" type="button" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form:form>
</div>

<div class="modal fade" id="myModalCriteriaDelete" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="application.confirm.delete" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="temp.criteria.confirm.delete" /></label>
				<input type="hidden" name="removeCriteriaId" id="removeCriteriaId">
				<input type="hidden" name="removetemplateId" id="removetemplateId">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confirmDeleteCriteria"><spring:message code="tooltip.delete" /></button>
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
<%-- 						<c:if test="${cq.cqOrder != null}"> --%>
					<c:if test="${cq.order != null}">
					 <td class="width_100 width_135_fix align-left orderChange">
<%-- 						 	<span>${cq.cqOrder}</span> --%>
					 	<span>${cq.order}</span>
					 </td>
					</c:if>
<%-- 						<c:if test="${cq.cqOrder == null}"> --%>
					<c:if test="${cq.order == null}">
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

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/spCriteria.js"/>"></script>

<script>
<c:if test="${readOnlyPermission or buyerReadOnlyAdmin or isTemplateUsed}">
$(window).bind('load', function() {
	var allowedFields = '.wiz-step,#saveRfxTemplate,#previousButton,#dashboardLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
});
</c:if>

	$.validate({
		lang : 'en',
		modules : 'date'
	});
	
	$(function() {
		$("[data-role=submit]").click(function() {
			$(this).closest("form").submit();
		});
	});

	var xyz = '${template.maximumScore}';
	var isTemplateUsed = ${readOnlyPermission or buyerReadOnlyAdmin or isTemplateUsed};
	
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
	
	$('#savePrSection').click(function(){
		if($('#addEditSectionForm').isValid()) {
			$('#loading').show();
		}
	});
	
	$('#createSubCriteria').click(function(){
		if($('#addSubCriteriaform').isValid()) {
			$('#loading').show();
		}
	});
	
	$('#previousButton').click(function(){
		$('#loading').show();
	});
	
	$('#priviousStep').click(function(){
		$('#loading').show();
	});
	

	
</script>
