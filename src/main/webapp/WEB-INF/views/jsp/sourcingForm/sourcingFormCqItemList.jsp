<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<spring:message var="rfxCreatingQuestionnaires" code="application.rfx.create.questionnaires" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingQuestionnaires}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="questionnaire.create.cq.items" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg">
					<h2 class="trans-cap"><spring:message code="questionnaire.create.title" /></h2>
					<h2 class="trans-cap pull-right">Status : ${event.status}</h2>
				</div>
				<jsp:include page="eventHeader.jsp"></jsp:include>
				<div class="col-md-12 pad0 marg_top_15">
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div id="idMessageJsp">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					</div>
				</div>
				<div class="white-bg border-all-side float-left width-100 pad_all_15 marg-bottom-10">
					<div class="meeting2-heading">
						<h3 id="idMeetHead"><spring:message code="buyeraddress.caption.title" />:&nbsp;&nbsp;${rftCq.name}</h3>
						<button class="pull-right btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out toggleCreateBq view editCq" type="button" style="margin-right: 20px; margin-top: 7px; height: 30px; line-height: 1;" data-toggle="tooltip" data-id="${rftCq.id}" data-original-title="Edit Questionnaire"><spring:message code="questionnaire.edit.button" /></button>
					</div>
					<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" style="display: none;">
						<input id="suspensiontype" name="suspensiontype" value="${event.suspensionType}" type="hidden"> <input id="eventstatus" name="eventstatus" value="${event.status}" type="hidden">
						<form:form id="idCreateRftCq" class="form-horizontal bordered-row has-validation-callback" action="${pageContext.request.contextPath}/buyer/${eventType}/createRftCq" method="post" modelAttribute="rftCq">
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
							<input id="cqId" name="id" value="${rftCq.id}" type="hidden">
							<input id="editCqName" name="editCqName" value="${rftCq.name}" type="hidden">
							<input id="editCqDesc" name="editCqDesc" value="${rftCq.description}" type="hidden">
							<header class="form_header"></header>
							<jsp:include page="createAddCq.jsp" />
						</form:form>
					</div>
				</div>
				<div class="column_button_bar float-left width-80">
					<div class="upload_download_wrapper">
						<div class="left_button">
							<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="addsection_btn"><spring:message code="application.add.section.button" /></button>
							<c:if test="${event.status eq 'DRAFT' || event.suspensionType eq 'DELETE_NOTIFY' || event.suspensionType eq 'DELETE_NO_NOTIFY'}">
								<button id="s1_tender_delete_btn" class="btn btn-black ph_btn_midium disabled hvr-pop hvr-rectangle-out1">
									<spring:message code="label.rftbq.button.delete" />
								</button>
							</c:if>
						</div>
						<div class="right_button">
							<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal">
								<i class="excel_icon"></i><spring:message code="application.download.excel.button" />
							</button>
							<div data-provides="fileinput" class="fileinput fileinput-new hide input-group">
								<spring:message code="event.doc.file.required" var="required" />
								<spring:message code="event.doc.file.length" var="filelength" />
								<spring:message code="event.doc.file.mimetypes" var="mimetypes" />
								<div data-trigger="fileinput" class="form-control">
									<i class="glyphicon glyphicon-file fileinput-exists"></i>
									<span class="fileinput-filename show_name"></span>
								</div>
								<span class="input-group-addon btn btn-black btn-file">
									<span class="fileinput-new">
										<spring:message code="event.document.selectfile" />
									</span>
									<span class="fileinput-exists">
										<spring:message code="event.document.change" />
									</span>
									<input type="file" data-buttonName="btn-black" id="uploadCqItemFile" name="uploadCqItemFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
								</span>
							</div>
							<button class="btn green-btn hvr-pop hvr-rectangle-out2" id="uploadCqItems">
								<i class="upload_icon"></i><spring:message code="application.upload.listitem.button" />
							</button>
							<!-- <div style="margin-left: 0; margin-right: 0;"> -->
							<button style="visibility: hidden" id="uploadCqItemsFile"></button>
							<!-- </div> -->
							<%-- <button class="btn light-gray-new ph_btn_midium hvr-pop hvr-rectangle-out3" id="uploadCqItems" data-target="#myModal11" data-toggle="modal">
								<spring:message code="application..load.template" />
							</button> --%>
						</div>
					</div>
				</div>
				<!-- Form With Score  -->
				<!-- Form With Score End  -->
				<div class="Invited-Supplier-List create_sub marg-bottom-20 default-setting cq_form_google_form" id="add_question_form">
					<div class="cq_form_wrap">
						<div class="cq_form_inner">
							<form id="add_question_form1" name="add_question_form1">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="eventId" id="eventId" value="${event.id}"> <input type="hidden" id="cqId" value="${rftCq.id}"> <input type="hidden" name="parentId" id="parentId" value=""> <input type="hidden" name="itemId" id="itemId" value="">
								<div class="bottom_box">
									<div class="cq_row_parent box_active">
										<div class="box_qus_top">
											<div class="bqt_qus">
												<div class="group">
													<input type="text" id="itemName" placeholder='<spring:message	code="application.question" />' data-validation="required length" data-validation-length="max250" name="itemName" required class="cqr_title3a">
												</div>
											</div>
											<div class="bqt_list">
												<section class="main">
													<label class="answer_type"><spring:message code="questionnaire.question" /></label>
													<button class="close for-absulate" type="button" id="closeCqItemDiv">�</button>
													<!-- <button class="close for-absulate" type="button" data-dismiss="modal">�</button> -->
													<div class="wrapper-demo">
														<div id="dd" class="wrapper-dropdown-3" tabindex="1">
															<span><spring:message code="cq.option.choice" /></span>
															<ul class="dropdown">
																<spring:message code="cq.option.text.value" var="cqText" />
																<li data-cqtype="${cqText}"><a id="test" href="#"> <img src="<c:url value="/resources/assets/images/paragraph.png"/>"> <spring:message code="cq.option.text" />
																</a></li>
																<spring:message code="cq.option.choice.value" var="cqChoice" />
																<li data-cqtype="${cqChoice}"><a id="test2" href="#"> <img src="<c:url value="/resources/assets/images/redio_bttn_icon.png"/>"> <spring:message code="cq.option.choice" />
																</a></li>
																<spring:message code="cq.option.choice.with.score.value" var="cqChoiceScore" />
																<li data-cqtype="${cqChoiceScore}"><a href="#"> <img src="<c:url value="/resources/assets/images/redio_bttn_icon.png"/>"> <spring:message code="cq.option.choice.with.score" />
																</a></li>
																<spring:message code="cq.option.checkboxes.value" var="cqCheckbox" />
																<li data-cqtype="${cqCheckbox}"><a href="#"> <img src="<c:url value="/resources/assets/images/checkbox.png"/>"> <spring:message code="cq.option.checkboxes" />
																</a></li>
																<spring:message code="cq.option.list.value" var="cqList" />
																<li data-cqtype="${cqList}"><a href="#"> <img src="<c:url value="/resources/assets/images/dropdown_icon.png"/>"> <spring:message code="cq.option.list" />
																</a></li>
															</ul>
														</div>
													</div>
												</section>
											</div>
										</div>
										<div class="cq_row">
											<div class="group">
												<input type="text" placeholder='<spring:message code="application.description" />' id="itemDesc" name="itemDesc" data-validation="length" data-validation-length="0-1000" />
											</div>
										</div>
										<div class="bottom_box listboxBlock">
											<div class="width100 pull-left maxhgt400">
												<div class="box_qus_row1 drager_point">
													<div class="box_qus_radio">
														<img src="<c:url value="/resources/assets/images/Radio_Button_blank.png"/>">
													</div>
													<div class="box_qus_area">
														<div class="group">
															<input type="text" required class="cqr_title4a">
															<span class="highlight"></span>
															<span class="bar"></span>
															<label class="cqr_title4"><spring:message code="application.option.label" /> 1</label>
														</div>
													</div>
												</div>
											</div>
											<div class="box_qus_row2">
												<div class="box_qus_radio">
													<img src="<c:url value="/resources/assets/images/Radio_Button_blank.png"/>">
												</div>
												<div class="box_qus_area2">
													<a href="#" class="addOptionElement"> <spring:message code="cq.option.add.opt" />
													</a>
												</div>
											</div>
										</div>
										<div class="cq_action_box">


											<div class="row">
												<div class="col-sm-3">
													<div class="toggle_wrap">
														<div class="toggle-button">
															<input type="hidden" name="isRequired" value="0" />
															<button></button>
														</div>
														<div class="toggle_text">
															<spring:message code="application.required" />
														</div>
													</div>
												</div>

												<div class="col-sm-5">
													<div class="toggle_wrap supplierAttchment cq_action_icon">
														<div class="toggle-button" id="showNumberOfScreens"  style = "position:relative; left:17px;>
															<input type="hidden" name="isSupplierAttach" id="isSupplierAttach" value="0" />
															<button></button>
														</div>
														<div class="toggle_text" style="margin: -0px;">
															<spring:message code="cq.option.req.attach.doc" />
														</div>
													</div>
												</div>

												<div class="col-sm-4">
													<div class="toggle_wrap SupplierAttachRequired cq_action_icon">
														<div class="toggle-button" id="supplierAttachRequired">
															<input type="hidden" name="isSupplierAttachRequired" id="isSupplierAttachRequired" value="0" />
															<button></button>
														</div>
														<div class="toggle_text">Attachment Required</div>
													</div>
												</div>


											</div>

											<div class="row" style="margin-top: 20px;">


												<div class="pull-left">
													<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="createCqItem">
														<spring:message code="application.create" />
													</button>
													<button class="btn btn-lg btn-black ph_btn_midium hvr-pop hvr-rectangle-out1" id="cancelCqItem">
														<spring:message code="application.cancel" />
													</button>
												</div>
											</div>








										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
				<section class="s1_white_panel s1_pad_25_all s1_creat_itmeBox float-left width-100 hidecolumnoption" id="creat_seaction_form">
					<h3 class="s1_box_title">
						<spring:message code="application.create.new.section" />
					</h3>
					<form id="addSectionForm">
						<div class="row">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="eventId" id="eventId" value="${event.id}"> <input type="hidden" id="cqId" name="cqId" value="${rftCq.id}"> <input type="hidden" id="itemId" name="itemId"> <input type="hidden" name="sectionId" id="sectionId" />
							<div class="col-md-7 col-sm-7 col-xs-12">
								<div class="s1_creatItem_inlineControl">
									<div class="form-group ">
										<input type="text" class="form-control section_name" data-validation="required" id="sectionName" placeholder="Section Name" maxlength="250">
									</div>
								</div>
							</div>
							<div class="col-md-7 col-sm-7 col-xs-12">
								<div class="s1_creatItem_inlineControl">
									<div class="form-group">
										<textarea type="text" class="form-control" id="sectionDescription" name="sectionDescription" placeholder="Section Description" maxlength="300"></textarea>
										<span class="sky-blue">Max 300 characters only</span>
									</div>
								</div>
							</div>
						</div>
						<div class="row marg-top-10">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="s1__frmbtn_block">
									<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="saveCqSection">Save</button>
									<button class="btn btn-lg btn-black ph_btn_midium hvr-pop hvr-rectangle-out1" id="cancelCqSection">Cancel</button>
								</div>
							</div>
						</div>
					</form>
				</section>
				<section class="s1_white_panel s1_pad_25_all s1_creat_itmeBox hidecolumnoption" id="creat_subitem_form">
					<h3 class="text-center s1_box_title">
						<spring:message code="application.create.new.item" />
					</h3>
					<form>
						<div class="s1_creatItem_inlineControl">
							<div class="form-group s1_frm_width_230 ">
								<input type="password" class="form-control" id="exampleInputPassword3" placeholder="Item Name">
							</div>
							<div class="form-group s1_frm_width_130">
								<select name="unit" class="custom-select">
									<option selected><spring:message code="label.rftbq.th.unit" /></option>
									<option>1</option>
									<option>2</option>
									<option>3</option>
								</select>
							</div>
							<div class="form-group s1_frm_width_130">
								<input type="text" class="form-control" id="exampleInputEmail3" placeholder="Quantity">
							</div>
							<div class="form-group s1_frm_width_130">
								<input type="text" class="form-control" placeholder="Unit Price">
							</div>
						</div>
						<div class="form-group s1_frm_width_500">
							<textarea class="form-control"></textarea>
						</div>
						<div class="form-inline form-group s1_pricinng_ctrl">
							<label class="control-label">
								<spring:message code="label.rftbq.pricing" />
							</label>
							<select name="priceing" class="custom-select">
								<option selected><spring:message code="questionnaire.normal.pricing" /></option>
								<option>1</option>
								<option>2</option>
								<option>3</option>
							</select>
						</div>
						<div class="s1__frmbtn_block">
							<button type="submit" class="btn btn-info ph_btn">
								<spring:message code="application.create" />
							</button>
						</div>
					</form>
				</section>
				<div class="main_table_wrapper float-left width-100">
					<div class="table_fix_header">
						<table class="header_table header" width="100%" cellpadding="0" cellspacing="0">
							<tr>
								<th class="hed_1 width_50 width_50_fix ">
									<a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
									</a>
								</th>
								<th class="hed_2 width_50  width_50_fix ">
									<div class="checkbox checkbox-info">
										<label>
											<c:if test="${event.status eq 'DRAFT' || event.suspensionType eq 'DELETE_NOTIFY' || event.suspensionType eq 'DELETE_NO_NOTIFY'}">
												<input type="checkbox" id="inlineCheckbox115" class="custom-checkbox checkallcheckbox">
											</c:if>
										</label>
									</div>
								</th>
								<th class="hed_3 width_50  width_50_fix"></th>
								<th class="hed_4 width_200 width_200_fix ">
									<spring:message code="label.name" />
								</th>
								<th class="hed_5 width_200 width_200_fix">
									<spring:message code="application.question.type" />
								</th>
								<th class="hed_6 width_200 width_200_fix">&nbsp;</th>
							</tr>
						</table>
					</div>
					<div class="table_title_wrapper mega marg-for-cq-table ${eventPermissions.viewer or buyerReadOnlyAdmin ? 'disabled' : ''}">
						<section id="demo">
							<ol class="sortable ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="cqitemList">
								<c:forEach items="${cqItemList}" var="cq">
									<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${cq.id}" data-parent="">
										<div class="menuDiv">
											<table class="table data ph_table diagnosis_list sorted_table">
												<tr class="sub_item" data-id="${cq.id}">
													<td class="width_50 width_50_fix move_col align-left">
														<a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
														</a>
													</td>
													<td class="width_50 width_50_fix align-left">
														<div class="checkbox checkbox-info">
															<label>
																<c:if test="${event.status eq 'DRAFT' || event.suspensionType eq 'DELETE_NOTIFY' || event.suspensionType eq 'DELETE_NO_NOTIFY'}">
																	<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${cq.id}">
																</c:if>
															</label>
														</div>
													</td>
													<td class="width_50 width_50_fix align-left">
														<span>${cq.level}</span>
													</td>
													<td class="width_200 width_200_fix align-left">
														<span class="item_name">${cq.itemName}</span>
														<span class="item_detail">
															<a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />
															</a>
														</span>
														<p class="s1_tent_tb_description s1_text_small">${cq.itemDescription}</p>
													<td class="width_200 width_200_fix align-left ">
														<button class="btn btn-info ph_btn_extra_small hvr-pop hvr-rectangle-out add_question_table">
															<spring:message code="application.add.question" />
														</button>
													</td>
													<td class="width_200 width_200_fix align-left">
														<a title="" class="btn btn-sm btn-gray ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_btn_table" href="#"> <spring:message code="application.edit" />
														</a>
													</td>
												</tr>
											</table>
										</div> <c:if test="${not empty cq.children}">
											<ol>
												<c:forEach items="${cq.children}" var="cqchild">
													<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="${cqchild.id}" data-parent="${cq.id}">
														<div class="menuDiv sub-color-change">
															<table class="table data ph_table diagnosis_list sorted_table">
																<tr class="sub_item" data-id="${cqchild.id}" data-parent="${cq.id}">
																	<td class="width_50 width_50_fix move_col align-left">
																		<a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
																		</a>
																	</td>
																	<td class="width_50 width_50_fix align-left">
																		<div class="checkbox checkbox-info">
																			<label>
																				<c:if test="${event.status eq 'DRAFT' || event.suspensionType eq 'DELETE_NOTIFY' || event.suspensionType eq 'DELETE_NO_NOTIFY'}">
																					<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${cqchild.id}">
																				</c:if>
																			</label>
																		</div>
																	</td>
																	<td class="width_50 width_50_fix align-left">
																		<span>${cqchild.level}.${cqchild.order}</span>
																	</td>
																	<td class="width_200 width_200_fix align-left">
																		<span class="item_name">${cqchild.itemName}</span>
																		<span class="item_detail">
																			<a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />
																			</a>
																		</span>
																		<p class="s1_tent_tb_description s1_text_small">${cqchild.itemDescription}</p>
																	</td>
																	<td class="width_200 width_200_fix align-left"></td>
																	<td class="width_200 width_200_fix align-left">
																		<a title="" class="btn btn-sm edit-btn-table ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_btn_table"> <spring:message code="application.edit" />
																		</a>
																	</td>
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
				<div class="row">
					<div class="col-md-12">
						<div class="table_f_action_btn">
							<spring:url value="cqListPrevious" var="cqListPrevious" htmlEscape="true" />
							<form class="bordered-row" id="submitPriviousForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/${eventType}/cqListPrevious">
								<input type="hidden" id="eventId" value="${event.id}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<button type="submit" class="btn btn-lg btn-black hvr-pop hvr-rectangle-out1" value="Previous" id="priviousStep">
									<spring:message code="application.previous" />
								</button>
							</form>
							<spring:url value="cqNext" var="cqNext" htmlEscape="true" />
							<form class="bordered-row" id="submitNextForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/${eventType}/cqNext">
								<input type="hidden" id="eventId" value="${event.id}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<button type="submit" class="btn hvr-pop hvr-rectangle-out btn-lg btn-info hvr-pop hvr-rectangle-out" value="Next" id="priviousStep">
									<spring:message code="application.next" />
								</button>
							</form>
						</div>
					</div>
				</div>
			</section>
		</div>
	</div>
</div>
<div class="modal fade" id="myModalDeleteCQs" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
			</div>
			<div class="modal-body">
				<label>Are you sure you want to delete selected items?</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="idConfirmDelete">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<!-- Theme layout -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});

	<c:if test="${(eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'KEEP_NOTIFY' or event.suspensionType == 'KEEP_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
		var allowedFields = '#meetingListNext,#priviousStep,.s1_view_desc,#ideditbutton,#bubble,#downloadTemplate';
	$(window).bind('load', function() {
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>

	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	var allowedFields = '#meetingListNext,#priviousStep,.s1_view_desc,#bubble,#downloadTemplate';
		$(window).bind('load', function() {
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$(document).delegate('#cancelCqSection', 'click', function(e) {
		$('#addSectionForm').get(0).reset();
	});
	
	
		$(document).delegate('#showNumberOfScreens', 'click', function(e) {
			
			var isSupplierAttach= document.getElementById("isSupplierAttach").value;
		
			if(isSupplierAttach==0){
			document.getElementById("supplierAttachRequired").disabled = false;
			//document.getElementById("isSupplierAttachRequired").value=0;
			
			
						
			
			}
			else{
				
				$(".cq_form_google_form").find('.toggle-button').find('[name="isSupplierAttachRequired"]').val('0').parent().removeClass('toggle-button-selected');
				document.getElementById("supplierAttachRequired").disabled = true;
				
			}
	})
	

	
	
	
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/eventCq.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/cq_form.js"/>"></script>
<style>
.answer_type {
	position: absolute;
	top: 5px;
	right: 174px;
}

#add_question_form1 input:focus::-webkit-input-placeholder {
	color: transparent;
}

#add_question_form1 input:focus:-moz-placeholder {
	color: transparent;
} /* Firefox 18- */
#add_question_form1 input:focus::-moz-placeholder {
	color: transparent;
} /* Firefox 19+ */
#add_question_form1 input:focus:-ms-input-placeholder {
	color: transparent;
} /* oldIE ;) */
</style>