<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/multi-upload/fileupload.css"/>">
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
				<li><a  id="dashboard" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
				</a></li>
				<li><a  id="supplierFormList" href="${pageContext.request.contextPath}/buyer/supplierFormList"> <spring:message code="supplierForm.list" />
				</a></li>
				<li class="active">${btnValue} <spring:message code="create.suppliers.form" />
			</ol>
			<section class="create_list_sectoin">
			<form:form id="idCreateForm" class="form-horizontal bordered-row has-validation-callback" action="${pageContext.request.contextPath}/buyer/createSupplierForm" method="post" modelAttribute="supplierFormObj">
				<form:input id="suppformId" name="suppformId" path="id" type="hidden"/>
				<form:input id="btnValue" name="btnValue" value="${btnValue}" path="btnValue" type="hidden"/>
				<input id="formId" name="formId" value="${supplierFormObj.id}" type="hidden">
				<div class="Section-title title_border gray-bg">
					<h2 class="trans-cap">${btnValue} <spring:message code="create.suppliers.form" /></h2>
				</div>
				<div class="col-md-12 pad0 marg_top_15">
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div id="idMessageJsp">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					</div>
				</div>
				<div class="white-bg border-all-side float-left width-100 pad_all_15 marg-bottom-10">
					<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" >
						<div class="row marg-bottom-10">
							<div class="col-md-3">
								<label for="uom" class="marg-top-10">
									<spring:message code="supplierForm.name.label" />
								</label>
							</div>
							<div class="col-md-5">
							<spring:message code="supplierForm.name.label" var="cqname" />
								<form:input type="text" path="name" id="name" placeholder="${cqname}" data-validation="required length" class="form-control" data-validation-length="4-128" />
							</div>
						</div>
						<div class="row marg-bottom-10">
							<div class="col-md-3">
								<label for="uom" class="marg-top-10">
									<spring:message code="supplierForm.description.label" />
								</label>
							</div>
							<div class="col-md-5">
								<spring:message code="supplierForm.description.label" var="cqdesc" />
								<form:textarea rows="3" path="description" id="description" placeholder="${cqdesc}" data-validation="length" class="form-control" data-validation-length="0-500" />
								<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
							</div>
						</div>
						<div class="row marg-bottom-10">
							<div class="col-md-3 marg-top-20">
								<label for="uom" class="marg-top-10">
									<spring:message code="application.status" />
								</label>
							</div>
							<div class="col-md-5 marg-top-20">
								<form:select path="status" cssClass="form-control chosen-select" id="idStatus" >
									<form:options items="${statusList}" />
								</form:select>
							</div>
						</div>
						<jsp:include page="supplierFormApproval.jsp" />
					</div>
				</div>
				<div class="column_button_bar float-left width-80">
					<div class="upload_download_wrapper">
						<div class="left_button">
							<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="addsection_btn"><spring:message code="application.add.section.button" /></button>
								<button id="s1_tender_delete_btn" class="btn btn-black ph_btn_midium disabled hvr-pop hvr-rectangle-out1">
									<spring:message code="label.rftbq.button.delete" />
								</button>
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
									<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename show_name"></span>
								</div>
								<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" />
								</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />
								</span> <input type="file" data-buttonName="btn-black" id="uploadCqItemFile" name="uploadCqItemFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
								</span>
							</div>
							<button class="btn green-btn hvr-pop hvr-rectangle-out2" id="uploadCqItems">
								<i class="upload_icon"></i><spring:message code="suppplier.upload.forms.label" />
							</button>
							<!-- <div style="margin-left: 0; margin-right: 0;"> -->
							<button style="visibility: hidden" id="uploadCqItemsFile"></button>
						</div>
					</div>
				</div>
				<!-- Form With Score End  -->
				<div class="Invited-Supplier-List create_sub marg-bottom-20 default-setting cq_form_google_form" id="add_question_form" style="display: none;">
					<div class="cq_form_wrap">
						<div class="cq_form_inner">
							<form id="add_question_form1" name="add_question_form1">
								<input type="hidden" value="${isAssignedForm}" id="suspendedEvent">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><input type="hidden" id="formId" value="${supplierFormObj.id}"> <input type="hidden" name="parentId" id="parentId" value=""> <input type="hidden" name="itemId" id="itemId" value="">
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
													<div class="wrapper-demo">
														<div id="dd" class="wrapper-dropdown-3" tabindex="1">
															<span><spring:message code="cq.option.choice" /></span>
															<ul class="dropdown">
																<spring:message code="cq.option.text.value" var="cqText" />
																
																<li data-cqtype="${cqText}"><a id="test" href="#"> <img style="width: 18px !important; padding: 0; margin-right: 22px;" src="<c:url value="/resources/assets/images/textAnswer.svg"/>"> <spring:message code="cq.option.text" />
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
																<!--  PH-278  -->
																<spring:message code="cq.option.date.value" var="cqDate" />
																<li data-cqtype="${cqDate}"><a href="#"> <img src="<c:url value="/resources/assets/images/date_calender_icon2.png"/>"> <spring:message code="cq.option.date" />
																</a></li>

																<spring:message code="cq.option.number.value" var="cqNumber" />
																<li data-cqtype="${cqNumber}"><a href="#"> <img style="width: 18px !important; padding: 0; margin-right: 22px;" src="<c:url value="/resources/assets/images/numberAnswer.svg"/>"> <spring:message code="cq.option.number" />
																</a></li>
																<spring:message code="cq.option.paragraph.value" var="cqParagraph" />
																<li data-cqtype="${cqParagraph}"><a href="#"> <img src="<c:url value="/resources/assets/images/paragraph.png"/>"> <spring:message code="cq.option.paragraph" />
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

										<div id="eventDocumentList" class="width100 ">
											<div class="col-md-9 col-sm-9">
												<div class="input-group search_box_gray">
													<select class="chosen-select chosen" id="documentListArray" name="documentList" multiple data-placeholder="Select Document">
													<%-- 	<c:forEach items="${eventDocumentList}" var="document">
															<option value="${document.id}">${document.fileName}</option>
														</c:forEach> --%>
													</select>
												</div>
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
															<input type="text" required class="cqr_title4a"> <span class="highlight"></span> <span class="bar"></span> <label class="cqr_title4">Option 1</label>
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
										 <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
								       <div class="row fileupload-buttonbar">
						                    <div class="col-lg-6" style="margin-left: 15px; margin-top: 35px; margin-bottom: 10px;">
						                        <div class="float-left">
						                        <c:set var="fileType" value="" /> 
						                        <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
														<c:set var="fileType"  value="${fileType}${index.first ? '': ', '}${type}" />
												</c:forEach>
												<input type="hidden" id="fileSizeAllow" value=" ${ownerSettings.fileSizeLimit}">
												<input type="hidden" id="fileTypeAllow" value="<c:out value="${fileType}"/>">
								                  <span class="btn btn-md btn-success fileinput-button">
								                        <i class="glyph-icon icon-plus"></i>
								                        Add Attachments
								                      <input type="file" id="fileupload" name="files[]" multiple data-validation-allowing="${fileType}" data-validation="extension size" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB">
								                  </span>
						                        </div>
						                	</div>
						                </div>
						                <!-- The table listing the files available for upload/download -->
						                <table role="presentation" id="filesList" class="table table-striped margin-top20"><tbody class="files"></tbody></table>
										<div class="cq_action_box">
											<div class="row toggleButtons">
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

												<div class="col-sm-5 p-0">
													<div class="toggle_wrap supplierAttchment cq_action_icon flex-inline">
														<div class="toggle-button" id="showNumberOfScreens">
															<input type="hidden" name="isSupplierAttach" id="isSupplierAttach" value="0" />
															<button></button>
														</div>
														<div class="toggle_text">
															<spring:message code="cq.option.supp.attach.doc" />
														</div>
													</div>
												</div>

												<div class="col-sm-4">
													<div class="toggle_wrap SupplierAttachRequired cq_action_icon">
														<div class="toggle-button" id="supplierAttachRequired">
															<input type="hidden" name="isSupplierAttachRequired" id="isSupplierAttachRequired" value="0" />
															<button></button>
														</div>
														<div class="toggle_text"><spring:message code="questionnaire.attachment.required" /></div>
													</div>
												</div>
											</div>

											<div class="row" style="margin-top: 20px;">
												<div class="pull-left">
													<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="createCqItem">
														<spring:message code="application.create" />
													</button>
													<button class="btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out1" id="cancelCqItem">
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
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />    <input type="hidden" id="itemId" name="itemId"> <input type="hidden" name="sectionId" id="sectionId" />
							<div class="col-md-7 col-sm-7 col-xs-12">
								<div class="s1_creatItem_inlineControl">
									<div class="form-group ">
										<input type="text" class="form-control section_name" data-validation="required" id="sectionName" placeholder='<spring:message code="event.document.sectionname" />' maxlength="250">
									</div>
								</div>
							</div>
							<div class="col-md-7 col-sm-7 col-xs-12">
								<div class="s1_creatItem_inlineControl">
									<div class="form-group">
										<textarea type="text" class="form-control" id="sectionDescription" name="sectionDescription" placeholder='<spring:message code="section.description.placeholder" />' maxlength="300"></textarea>
										<span class="sky-blue"><spring:message code="cq.create.section.maxlimit" /></span>
									</div>
								</div>
							</div>
						</div>
						<div class="row marg-top-10">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="s1__frmbtn_block">
									<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="saveCqSection"><spring:message code="application.save" /></button>
									<button class="btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out1" id="cancelCqSection"><spring:message code="application.cancel" /></button>
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
								<input type="password" class="form-control" id="exampleInputPassword3" placeholder='<spring:message code="event.document.itemname" />'>
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
								<input type="text" class="form-control" id="exampleInputEmail3" placeholder='<spring:message code="quetionaaire.quantity.placeholder" />'>
							</div>
							<div class="form-group s1_frm_width_130">
								<input type="text" class="form-control" placeholder='<spring:message code="quetionaaire.unit.price.placeholder" />'>
							</div>
						</div>
						<div class="form-group s1_frm_width_500">
							<textarea class="form-control"></textarea>
						</div>
						<div class="form-inline form-group s1_pricinng_ctrl">
							<label class="control-label"> <spring:message code="label.rftbq.pricing" />
							</label> <select name="priceing" class="custom-select">
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
						<table class="header_table header bq_font" width="100%" cellpadding="0" cellspacing="0">
							<tr>
								<th class="hed_1 width_50 width_50_fix "><a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
								</a></th>
								<th class="hed_2 width_50  width_50_fix ">
									<div class="checkbox checkbox-info">
										<label> 
												<input type="checkbox" id="inlineCheckbox115" class="custom-checkbox checkallcheckbox">
										</label>
									</div>
								</th>
								<th class="hed_3 width_50  width_50_fix"><spring:message code="label.rftbq.th.No" /></th>
								<th class="hed_4 width_200 width_200_fix "><spring:message code="label.name" /></th>
								<th class="hed_5 width_200 width_200_fix"><spring:message code="application.question.type" /></th>
								<th class="hed_6 width_200 width_200_fix">&nbsp;</th>
							</tr>
						</table>
					</div>
					<div class="table_title_wrapper mega marg-for-cq-table">
						<section id="demo">
							<ol class="sortable ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="cqitemList">
								<c:forEach items="${formItemList}" var="cq">
									<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${cq.id}" data-parent="">
										<div class="menuDiv">
											<table class="table data ph_table diagnosis_list sorted_table">
												<tr class="sub_item" data-id="${cq.id}">
													<td class="width_50 width_50_fix move_col align-left"><a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
													</a></td>
													<td class="width_50 width_50_fix align-left">
														<div class="checkbox checkbox-info">
															<label> 
																	<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${cq.id}">
															</label>
														</div>
													</td>
													<td class="width_50 width_50_fix align-left"><span id="appLevel" value="${cq.level}">${cq.level}.${cq.order}</span></td>
													<td class="width_200 width_200_fix align-left">
													<span class="item_name">${cq.itemName}</span> 
													<c:if test="${not empty cq.itemDescription}">
													<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /> </span>
														<p class="s1_tent_tb_description s1_text_small">${cq.itemDescription}</p>
													</c:if>
													</td>
													<td class="width_200 width_200_fix align-left ">
														<button id="addQuestion" class="btn btn-info ph_btn_extra_small hvr-pop hvr-rectangle-out add_question_table">
															<spring:message code="application.add.question" />
														</button>
													</td>
													<td class="width_200 width_200_fix align-left"><a title="" class="btn btn-sm btn-gray ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_btn_table" href="#"> <spring:message code="application.edit" />
													</a></td>
												</tr>
											</table>
										</div> <c:if test="${not empty cq.children}">
											<ol>
												<c:forEach items="${cq.children}" var="cqchild">
													<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="${cqchild.id}" data-parent="${cq.id}">
														<div class="menuDiv sub-color-change">
															<table class="table data ph_table diagnosis_list sorted_table">
																<tr class="sub_item" data-id="${cqchild.id}" data-parent="${cq.id}">
																	<td class="width_50 width_50_fix move_col align-left"><a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
																	</a></td>
																	<td class="width_50 width_50_fix align-left">
																		<div class="checkbox checkbox-info">
																			<label>
																					<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${cqchild.id}">
																			</label>
																		</div>
																	</td>
																	<td class="width_50 width_50_fix align-left"><span>${cqchild.level}.${cqchild.order}</span></td>
																	<td class="width_200 width_200_fix align-left"><span class="item_name">${cqchild.itemName}</span> 
																	<c:if test="${not empty cqchild.itemDescription}">
																	<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" /></span>
																		<p class="s1_tent_tb_description s1_text_small">${cqchild.itemDescription}</p>
																	</c:if>
																	</td>
																	<td class="width_200 width_200_fix align-left"></td>
																	<td class="width_200 width_200_fix align-left"><a title="" class="btn btn-sm edit-btn-table ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_btn_table"> <spring:message code="application.edit" />
																	</a></td>
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
				
				<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-20"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15 marg-top-20">
							<form:button type="submit" value="save" id="saveForm" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" >${btnValue}</form:button>
							<c:url value="/buyer/supplierFormList" var="cancelUrl" />
							<a href="${cancelUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous" id="cancelButton">
								<spring:message code="application.button.closed" />
							</a>
							<c:if test="${not empty formItemList}">
								<form:button type="button" id="saveAsSupplierForm" class="btn btn-alt btn-hover ph_btn_midium btn-warning hvr-pop pull-right">
									<spring:message code="application.saveas" />
								</form:button>
							</c:if>
						</div>
					</div>
				</form:form>
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
				<label><spring:message code="bq.sure.delete.selected.item" /></label>
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
<div class="modal fade" id="myModalDocDelete" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">�</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="supplierForm.item.doc.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idItemdocConfirmDelete" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="#"  data-id='' title="Delete"> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- TEMPLATE SAVE AS POPUP -->
<div class="flagvisibility dialogBox" id="supplierFormSaveAsPopup" title="Supplier Form Save As">
	<div class="float-left width100 pad_all_15 white-bg">
		<form id="idsaveAsForm" class="form-horizontal bordered-row has-validation-callback"  method="post" >
		<input type="hidden" id="formId" name="formId" /><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="marg-top-20 tempaleData">
				<div class="row marg-bottom-10">
					<div class=" col-md-4">
						<label> <spring:message code="supplierForm.name.label" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="supplierForm.description.label" var="desc" />
						<spring:message code="supplierForm.name.label" var="name" />
						<input data-validation-length="4-128" data-validation="required length" class="form-control marg-bottom-10" name="formName" id="formNameCheck" placeholder="${name}" > <span class="customError"></span>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-md-4">
						<label> <spring:message code="supplierForm.description.label" />
						</label>
					</div>
					<div class="col-md-8">
						<textarea name="formDesc" class="form-control textarea-autosize" data-validation-length="0-500" data-validation="length" id="formDescriptionChek" placeholder="${desc}"></textarea>
					</div>
				</div>
			</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button  type="submit" title="" class="btn btn-info ph_btn_midium btn-tooltip hvr-pop hvr-rectangle-out" id="saveAsSupplierSubmit" data-original-title="Save"><spring:message code="application.create" /></button>
					<button type="button" id="cancelFormSUb" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		</form>
	</div>
</div>
<script src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<!-- Theme layout -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multi-upload/jquery.ui.widget.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multi-upload/jquery.iframe-transport.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multi-upload/jquery.fileupload.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multi-upload/jquery.fileupload-process.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multi-upload/jquery.fileupload-validate.js"/>"></script>

<script>
<c:if test="${isAssignedForm or supplierFormObj.status == 'INACTIVE'}">
$(window).bind('load', function() {
	var allowedFields='#cancelButton,#downloadTemplate,#dashboard, #idStatus,#saveForm,#saveAsSupplierForm';
 	disableFormFields(allowedFields);
});
</c:if>
</script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date,file'
	});

	
	$(document).delegate('#cancelCqSection', 'click', function(e) {
		$('#addSectionForm').get(0).reset();
	});
	
	
		$(document).delegate('#showNumberOfScreens', 'click', function(e) {
			var isSupplierAttach= document.getElementById("isSupplierAttach").value;
			if(isSupplierAttach==0){
			document.getElementById("supplierAttachRequired").disabled = false;
			}
			else{
				$(".cq_form_google_form").find('.toggle-button').find('[name="isSupplierAttachRequired"]').val('0').parent().removeClass('toggle-button-selected');
				document.getElementById("supplierAttachRequired").disabled = true;
			}
	});
		$("#saveForm").click(function(){
			if ($('#idCreateForm').isValid()) {
			$('#idCreateForm').submit();
			}else{
				return false;
			}
		});
	
		function deleteItemDocFile(id){
			var link=$("a#idItemdocConfirmDelete");
			link.attr("data-id", '');
			link.data('data-id', link.attr("data-id"));
			link.attr("data-id", link.data('data-id') + '' + id);
		}
		function doCancel() {
			var link = $("a#idItemdocConfirmDelete");
			link.data('data-id', link.attr("data-id"));
			link.attr("data-id", '');
		}
		

		$('#saveAsSupplierForm').click(function() {
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			$('.customError').html('');
			$('#formNameCheck').val("");
			$('#formDescriptionChek').val("");
			$("#supplierFormSaveAsPopup").dialog({
				modal : true,
				minWidth : 300,
				width : '50%',
				maxWidth : 400,
				minHeight : 150,
				dialogClass : "",
				show : "fadeIn",
				draggable : true,
				resizable : false,
				dialogClass : "dialogBlockLoaded"
			});
			$('.ui-widget-overlay').addClass('bg-white opacity-60');
			$('#supplierFormSaveAsPopup').find('#formId').val($('#suppformId').val());
			$('.ui-dialog-title').text("Supplier Form Save As");
		});
		
		$('#saveAsSupplierSubmit').on('click', function(e) {
			e.preventDefault();
			if($("#idsaveAsForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#saveAsSupplierSubmit').addClass('disabled');
				$('#cancelFormSUb').addClass('disabled');
	 			$('#idsaveAsForm').attr('action', getContextPath() + '/buyer/copySupplierForm');
				$("#idsaveAsForm").submit();
			}else{
				return;
			}
		});
		
</script>
<script type="text/javascript">
$( document ).ready(function() {
	$("#documentDropdown").hide();
	$("#eventDocumentList").hide();
});
</script>
	
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierFormCreate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/cq_form.js"/>"></script>
<style>
.answer_type {
	position: absolute;
	top: 5px;
	right: 174px;
}
#documentListArray_chosen {
	word-break: break-all !important;
}
.flex-inline {
	display: flex;
    flex-direction: row;
    flex-flow: row-reverse;
}
.p-0 {
	padding: 0;
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
.bq_font
{
 font-family: 'open_sanssemibold'; font-weight:normal;	
}
   
						                .file {
   position: relative;
   background: linear-gradient(to right, lightblue 50%, transparent 50%);
   background-size: 200% 100%;
   background-position: right bottom;
   transition:all 1s ease;
}
 .file.done {
   background: lightgreen;
}
 .file a {
   display: block;
   position: relative;
   padding: 5px;
   color: black;
}
.progressbar {
    height: 18px;
    background: green;
    }
</style>