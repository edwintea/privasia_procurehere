<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<spring:message var="rfxCreateBq" code="application.rfx.create.bill.of.quantities" />
<spring:message var="rfaCreateBq" code="application.rfa.create.bill.of.quantities" />
<sec:authentication property="principal.bqPageLength" var="bqPageLength" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreateBq},${rfaCreateBq}] });
});
</script>
<script>
$(document).ready(function(){
  // Add smooth scrolling to all links
  $(".width_300 a").on('click', function(event) {

    if (this.hash !== "") {
      event.preventDefault();

      var hash = this.hash;
console.log("hash :" +hash);
      $('html, body').animate({
        scrollTop: $(hash).offset().top
      }, 1000, function(){
      window.location.hash = hash;
      });
    } // End if
  });
});
</script>
<input type="hidden" id="bqPageLength" value="${bqPageLength}">
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard" /></a></li>
				<li class="active"><spring:message code="rfs.sourcing.form.request" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg">
					<h2 class="trans-cap">
						<i aria-hidden="true" class="glyph-icon icon-list-alt"></i> <spring:message code="bq.list.create.item" />
					</h2>
					<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${sourcingFormRequest.status}</h2>
				</div>
				<jsp:include page="sourcingFormHeader.jsp"></jsp:include>
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<div class="white-bg border-all-side float-left width-100 pad_all_15 marg-bottom-10">
					<div class="margin-bottom-10">
						<div class="meeting2-heading">
							<h3 id="bqTitleId"><spring:message code="buyeraddress.caption.title" />:&nbsp;&nbsp;${sourcingFormRequestBq.name}</h3>
							<button class="pull-right btn ph_btn_midium btn-info toggleCreateBq hvr-pop hvr-rectangle-out editBqItem" type="button" data-toggle="tooltip"
								data-id="${sourcingFormRequestBq.id}" data-original-title='<spring:message code="tooltip.edit.sor" />' style="margin-right: 20px; margin-top: 7px; height: 30px; line-height: 1;"><spring:message code="tooltip.edit.sor" />
								</button>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" id="bqDisplay" style="display: none;">
							<form id="bqFormAddEdit" class="form-horizontal bordered-row has-validation-callback" action="" method="post">
								<input type="hidden" name="formId" id="formId" value="${sourcingFormRequest.id}">
								<input id="bqId" name="id" value="${sourcingFormRequestBq.id}" type="hidden">
								<input id="editBqName" name="editBqName" value="<c:out value='${sourcingFormRequestBq.name}'/>" type="hidden">
								<input id="editBqDesc" name="editBqDesc" value="<c:out value='${sourcingFormRequestBq.description}'/>" type="hidden">
								<input id="sourcingDecimal" value="${sourcingFormRequest.decimal}" type="hidden">
								<header class="form_header"></header>
								<jsp:include page="sourcingCreateAddSor.jsp" />
							</form>
						</div>
					</div>
				</div>
				
				 <div class="upload_download_wrapper">
					<h4 class="text_message"><spring:message code="bq.use.excel.list" /></h4>
					<div class="upload_download_button ">
						<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate">
							<i class="excel_icon"></i> <spring:message code="application.download.excel.button" />
						</button>
					<button class="btn green-btn hvr-pop hvr-rectangle-out2" id="uploadBqItems">
							<i class="upload_icon"></i> <spring:message code="application.upload.listitem.button" />
						</button>
						<div data-provides="fileinput" class="fileinput hide fileinput-new input-group">
							<spring:message code="event.doc.file.required" var="required" />
							<spring:message code="event.doc.file.length" var="filelength" />
							<spring:message code="event.doc.file.mimetypes" var="mimetypes" />
							<div data-trigger="fileinput" class="form-control">
								<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename show_name"></span>
							</div>
							<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" />
							</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />
							</span> <input type="file" data-buttonName="btn-black" id="uploadBqItemFile"  name="uploadBqItemFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
							</span>
						</div>
						<button style="visibility: hidden" id="uploadbqItemsFile"></button>
					</div>
				</div> 
				
				<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="sourcingFormRequestBqItem">
					<div class="column_button_bar">
						<div class="left_button">
							<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="s1_tender_addsection_btn">
								<spring:message code="label.rftbq.button.addsection" />
							</button>
							<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
								<button id="s1_tender_delete_btn" class="btn btn-black ph_btn_midium disabled ph_btn_midium hvr-pop hvr-rectangle-out1">
									<spring:message code="label.rftbq.button.delete" />
								</button>
							</c:if>
						</div>
						<input id="sourcingstatus" name="sourcingstatus" value="${sourcingFormRequest.status}" type="hidden">
						<div class="right_button">
							<button class="btn btn-default ph_btn_midium hvr-pop hvr-rectangle-out3" id="s1_tender_adddel_btn">
								<spring:message code="label.rftbq.button.deletecolumn" />
							</button>
						</div>
						<div class="right_button">
							<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="resetButton"><spring:message code="application.reset" /></button>
						</div>
						<div class="pull-right">
							<div class="row" style="margin-right: 0px;">
								<input name="bqItemSearch" type="text" id="bqItemSearch" placeholder='<spring:message code="sor.search.itemname.placeholder" />' class="form-control" />
							</div>
						</div>
					</div>
					<label class="label-top pull-right"> <spring:message code="application.recordsperpage" /> </label>
					<div class="col-md-1-5 col-sm-1 col-xs-1 pull-right marg-right-10" style="padding-left: 0px; padding-right: 0px;">
						<select class="disablesearch chosen-select" name="" data-parsley-id="0644" id="selectPageLen">
							<option value="10">10</option>
							<option value="30">30</option>
							<option value="50">50</option>
							<option value="100">100</option>
							<option value="500">500</option>
							<option value="9999"><spring:message code="application.all2" /></option>
						</select>
					</div>
					<div>
						<label class="label-top pull-left marg-right-10">&nbsp; &nbsp; <spring:message code="bq.jump.item" /> </label>
						<div class="col-md-1-5 col-sm-1 col-xs-1" style="padding-left: 0px; padding-right: 0px;">
							<select path="" class="chosen-select" id="chooseSection">
								<option value="">&nbsp;</option>
								<c:forEach items="${levelOrderList}" var="bqItemPojo">
									<option value="">${bqItemPojo.level}.${bqItemPojo.order}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="pull-right marg-right-10">
						<div aria-label="Page navigation" style="margin-top: -13px;">
							<ul class="pagination" id="pagination"></ul>
						</div>
					</div>
		</div>
		<!-- EVENT BQ ITEM LIST -->
		<section class="s1_white_panel s1_pad_25_all s1_creat_itmeBox hidecolumnoption" id="creat_seaction_form">
			<input type="hidden" name="formId" id="formId" value="${formId}">
			<input type="hidden" id="sectionBqId" value="${bqId}">
			<input type="hidden" name="parentId" id="parentId" value="">
			<input type="hidden" name="itemId" id="itemId" value="">
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<h3 class="s1_box_title"><spring:message code="application.create.new.section" /></h3>
				</div>
				<form id="addEditSectionForm">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="col-md-7 col-sm-7 col-xs-12">
						<div class="s1_creatItem_inlineControl">
							<div class="form-group">
								<input type="hidden" name="sectionId" id="sectionId" />
								<spring:message code="event.sorsection.required" var="required" />
								<input type="text" class="form-control" data-validation="required" data-validation-error-msg-required="${required}" id="sectionName" placeholder='<spring:message code="event.document.sectionname" />'
									name="sectionName" maxlength="250" />
							</div>
						</div>
					</div>
					<div class="col-md-7 col-sm-7 col-xs-12">
						<div class="s1_creatItem_inlineControl">
							<div class="form-group">
								<textarea type="text" class="form-control" id="sectionDescription" name="sectionDescription" placeholder='<spring:message code="section.description.placeholder" />' maxlength="1000"></textarea>
								<span class="sky-blue"><spring:message code="dashboard.valid.max2.characters" /></span>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-sm-12">
						<div class="s1__frmbtn_block">
							<button type="submit" class="btn btn-info ph_btn_midium" id="saveBqSection"><spring:message code="application.save" /></button>
							<button class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1 bqCancelSec"><spring:message code="application.cancel" /></button>
						</div>
					</div>
				</form>
			</div>
		</section>
		<section class="s1_white_panel s1_pad_25_all s1_creat_itmeBox hidecolumnoption" id="creat_subitem_form">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<h3 class="s1_box_title">
						<spring:message code="label.rftbq.createnewitem" />
					</h3>
				</div>
				<form name="addNewItems" id="addNewItems" method="post">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="formId" id="formId" value="${formId}">
					<input type="hidden" id="bqIdNewItems" value="${sourcingFormRequestBq.id}">
					<input type="hidden" name="parentId" id="parentId" value="">
					<input type="hidden" name="itemId" id="itemId" value="">
					<spring:message code="event.document.itemname" var="itemName" />
					<spring:message code="event.document.quantity" var="quantity" />
					<spring:message code="event.document.unitpricing" var="unitpricing" />
					<spring:message code="event.document.description" var="description" />
					<spring:message code="event.document.unitbudgetpricing" var="unitBudgetpricing" />
					<div class="row">
						<div class="col-md-6 col-sm-6 col-xs-12">
							<div class="form-group">
								<label><spring:message code="label.rftbq.th.itemname" /></label>
								<input type="text" data-validation="required" class="form-control" name="itemName" id="itemName" placeholder="${itemName}" maxlength="250">
							</div>
						</div>
						<div class="col-md-6 col-sm-6 col-xs-12">
							<div class="form-group">
								<label><spring:message code="label.rftbq.th.uom" /></label>
								<form:select path="uom" name="itemUnit" data-validation="required" id="itemUnit" items="${uomList}" itemValue="uom" itemLabel="uom" class="chosen-select" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6 col-sm-6 col-xs-12">
							<div class="form-group">
								<label class="marg-bottom-10"><spring:message code="application.description"/></label>
								<textarea class="form-control" id="itemDesc" name="itemDescription" placeholder='<spring:message code="event.bq.enter.description"/>' maxlength="2000"></textarea>
								<span class="sky-blue"><spring:message code="createrfi.event.description.max.chars" /></span>
							</div>
						</div>					
				
					</div>
					<div class="row extraFields" id="extraFields"></div>
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="s1__frmbtn_block">
							<button type="button" class="btn btn-info ph_btn_midium" id="itemSave">
								<spring:message code="label.rftbq.button.save" />
							</button>
							<button type="button" class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1 bqCancelSec"><spring:message code="application.cancel" /></button>
						</div>
					</div>
				</form>
			</div>
		</section>
		<section class="s1_white_panel s1_del_addcolumn" id="add_delete_column">
			<input type="hidden" name="bqFilledBy" id="bqFilledBy" value="${bqFilledBy}">
			<h4 class="s1_box_title"><spring:message code="sor.add.delete.column" /></h4>
			<form:form id="newFieldForm" modelAttribute="sourcingFormRequestBq">
				<div class="table-responsive-old">
					<table class="table">
						<thead>
						<tr>
							<th class="width_300"><spring:message code="bq.table.column.name" /></th>
							<th class="width_300"><spring:message code="bq.table.filled.in" /></th>
							<th class="width_100"><spring:message code="bq.table.supplier.view" /></th>
							<th class="width_100 text_table_center"><spring:message code="bq.table.required" /></th>
							<th class="width_100"><spring:message code="bq.table.remove" /></th>
						</tr>
						</thead>
						<tbody class="columnsExtra">
						<c:if test="${not empty sourcingFormRequestBq.field1Label}">
							<tr class="addColumsBlock" data-pos="1">
								<td class="width_300">
									<div class="form-group s1-mrg-10">
										<input type="text" class="form-control" id="field1Label" name="field1" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${sourcingFormRequestBq.field1Label}">
									</div>
								</td>
								<td class="width_300">
									<div class="form-group s1-mrg-10">
										<form:select path="field1FilledBy" data-validation="required" id="field1FilledBy" name="field1FilledBy" items="${bqFilledBy}" class="chosen-select" />
									</div>
								</td>
								<td class="width_100">
									<div class="checkbox checkbox-info checkbox_td_center s1-mrg-15">
										<label> <form:checkbox path="field1ToShowSupplier" id="field1ToShowSupplier" name="field1ToShowSupplier" class="custom-checkbox"></form:checkbox>
										</label>
									</div>
								</td>
								<td class="text_table_center width_100">
									<div class="checkbox checkbox-info checkbox_td_center s1-mrg-15">
										<label> <form:checkbox path="field1Required" id="field1Required" name="field1Required" class="custom-checkbox" />
										</label>
									</div>
								</td>
								<td class=" width_100"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove1" name="fieldRemove1"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
								</a></td>
							</tr>
						</c:if>

						<tr>
							<td colspan="5">
								<button class="btn btn-black ph_btn s1_mrg-r-20" type="button" id="AddColumnsToList"><spring:message code="label.rftbq.button.addcolumns" /></button>
								<button type="button" class="btn btn-info ph_btn" id="newFieldsSave">
									<spring:message code="label.rftbq.button.save" />
								</button>
							</td>
						</tr>
						</tbody>
					</table>
				</div>
			</form:form>
		</section>
		<div class="main_table_wrapper float-left width-100">
			<div class="table_fix_header">
				<table class="header_table header bq_font" width="100%" cellpadding="0" cellspacing="0" id="table_id">
					<tr>
						<th class="hed_1 width_50 width_50_fix align-left"><a href="javascript:void(0);">
								<i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
							</a></th>
						<th class="hed_2 width_50 width_50_fix align-left">
							<div class="checkbox checkbox-info">
								<label>
									<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
										<input type="checkbox" id="inlineCheckbox115" class="custom-checkbox checkallcheckbox ">
									</c:if>
								</label>
							</div>
						</th>
						<th class="hed_1 width_100_fix_custom align-left"><spring:message code="label.rftbq.th.No" /></th>
						<th class="hed_4 width_200 width_200_fix align-left"><spring:message code="label.rftbq.th.itemname" /></th>
						<th class="hed_5 width_100 width_100_fix align-left "><spring:message code="label.rftbq.th.uom" /></th>
						<c:if test="${not empty sourcingFormRequestBq.field1Label}">
							<th class="hed_4 width_200 width_200_fix extraFieldHeaders">${sourcingFormRequestBq.field1Label}</th>
						</c:if>
						<th class="hed_4 width_300 width_300_fix align_center"></th>
					</tr>
				</table>
			</div>
			<div class="marg-for-cq-table}" style="margin-top: 58px;">
				<section id="demo">
					<ol class="sortable ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="bqitemList">
						<c:forEach items="${bqList}" var="bq">
							<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${bq.id}" data-parent="" data-level="${bq.level}" data-order="${bq.order}">
								<div class="menuDiv">
									<table class="table data ph_table diagnosis_list sorted_table" id="table_id">
										<tr class="sub_item item_${bq.level}_${bq.order}" data-id="${bq.id}">
											<td class="width_50 width_50_fix move_col align-left">
												<a href="javascript:void(0);">
													<i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
												</a>
											</td>
											<td class="width_50 width_50_fix align-left">
												<div class="checkbox checkbox-info">
													<label>
														<c:if test="${sourcingFormRequest.status eq 'DRAFT' }">
															<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${bq.id}">
														</c:if>
													</label>
												</div>
											</td>
											<td class="width_100_fix_custom align-left">
												<span class="sectionD">${bq.level}.${bq.order}</span>
											</td>
											<td class="width_300 width_300_fix align-left">
												<span class="item_name sectionD">${bq.itemName}</span>
												<c:if test="${not empty bq.itemDescription}">
													<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /> </span>
													<p class="s1_tent_tb_description s1_text_small">${bq.itemDescription}</p>
												</c:if>
											</td>
											<c:if test="${not empty sourcingFormRequestBq.field1Label}">
												<td class="width_200 width_200_fix align-center">&nbsp;</td>
											</c:if>
											<td class="width_300 width_300_fix">
												<a title="" class="btn btn-sm edit-btn-table ph_btn_small hvr-pop hvr-rectangle-out1 Edit_section_table" href="#creat_seaction_form" style="display: inline-block;"><spring:message code="tooltip.edit" /></a>
												<a title="" class="btn btn-sm ph_btn_small btn-info marg-left-10 add_question_table" href="#creat_subitem_form"><spring:message code="label.rftbq.button.additem" /></a>
											</td>
										</tr>
									</table>
								</div> <c:if test="${not empty bq.children}">
									<ol>
										<c:forEach items="${bq.children}" var="bqchild">
											<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="${bqchild.id}"
												data-parent="${bq.id}" data-level="${bqchild.order}" data-order="${bqchild.order}">
												<div class="menuDiv sub-color-change">
													<table class="table data ph_table diagnosis_list sorted_table" id="table_id">
														<tr class="sub_item item_${bqchild.level}_${bqchild.order}" data-id="${bqchild.id}"  data-parent="${bq.id}">
															<td class="width_50 width_50_fix move_col align-left">
																<a href="javascript:void(0);">
																	<i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>
																</a>
															</td>
															<td class="width_50 width_50_fix align-left">
																<div class="checkbox checkbox-info">
																	<label>
																		<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
																			<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${bqchild.id}">
																		</c:if>
																	</label>
																</div>
															</td>
															<td class="width_100_fix_custom align-left">
																<span class="item_name itemNameD">${bqchild.level}.${bqchild.order}</span>
															</td>
															<td class="width_200 width_200_fix align-left">
																<span class="item_name itemNameD">
																	${bqchild.itemName}
																</span>
																<c:if test="${not empty bqchild.itemDescription}">
																<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" /> </span>
																<p class="s1_tent_tb_description s1_text_small">${bqchild.itemDescription}</p>
													           </c:if>
															</td>
															<td class="width_100 width_100_fix align-left">${bqchild.uom.uom}</td>
															
															<c:if test="${not empty sourcingFormRequestBq.field1Label}">
																<td  class="width_200 width_200_fix  align-center">${bqchild.field1}</td>
															</c:if>
															<td class="width_300 width_300_fix">
																<a title="" class="btn btn-sm ph_btn_small edit-btn-table hvr-pop hvr-rectangle-out1 Edit_subitme_table" href="#creat_subitem_form" style="display: inline-block;"><spring:message code="application.edit" /></a>
																<a title="" class="btn btn-sm add-btn-table ph_btn_small btn-default marg-left-10" style="visibility: hidden" href="javascript:void(0)"></a>
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
		</form:form>
		<div class="clear"></div>
		<div class="row">
			<div class="col-md-12">
				<div class="table_f_action_btn">
					<form class="bordered-row" id="submitPriviousForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/sourcingSorListPrevious">
						<input type="hidden" id="formId" value="${formId}" name="formId">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<button type="submit" class="btn btn-lg btn-black hvr-pop hvr-rectangle-out1" value="Previous" name="previous" id="priviousStep"><spring:message code="application.previous" /></button>
					</form>
					<form class="bordered-row" id="submitNextForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/sourcingSorNext">
						<input type="hidden" id="formId" value="${formId}" name="formId">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<button type="submit" class="btn hvr-pop hvr-rectangle-out btn-lg btn-info hvr-pop hvr-rectangle-out" value="Next" id="nextStep"><spring:message code="application.next" /></button>
					</form>
				</div>
			</div>
		</div>
		</section>
	</div>
</div>
</div>
<div class="modal fade" id="myModalDeleteColum" role="dialog">
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
				<label><spring:message code="bq.sure.delete.column" /></label>
				<input type="hidden" name="deleteColpos" id="deleteColpos" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="idConfirmDelete">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="myModalDeleteBQs" role="dialog">
	<div
		class="modal-dialog for-delete-all reminde.postin-wrepper {
	position: relative!important;
	margin-top: 9px;
	border: 1px solid #ddd;
	overflow-x: scroll;
	
}

.chosen-results {
	height: 170px !important;
}
	r">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="bq.sure.delete.selected.item" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="idConfirmDeleteBQs">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<style>
.addColumsBlock .checkbox.checkbox-info label {
	padding: 0;
}

.s1_remove_tr {
	padding-left: 20px;
}

.extraFieldHeaders {
	text-align: center;
}

.score-spinner1.ui-spinner-input {
	float: right;
	height: 31px;
	width: 75px;
}

#bqItemSearch {
	width: 287px;
}

ul.pagination {
	/* margin-bottom: 10px !important; */
	
}
.itemNameD {
	color: #3b3b3b;
	font-family: 'open_sansregular', "Helvetica Neue", Helvetica, Arial, sans-serif;
	font-size: 14px;
}

.sectionD {
	color: #3b3b3b;
	font-family: 'open_sanssemibold';
	font-size: 14px;
}

.width_100_fix_custom {
	min-width: 80px !important;
	width: 80px !important;
	padding-left: 0px !important;
	padding-right: 0px !important;
}

.ph_table td a {
	color: #ffffff !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.twbsPagination.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script>
var totalPage = 500;
var visiblePage = 5;
var totalBqItemCount = '${totalBqItemCount}';
var bqPageLength = $('#bqPageLength').val();
if( bqPageLength === undefined || bqPageLength === ''){
	bqPageLength = 50;
}
console.log("totalBqItemCount :" + totalBqItemCount + "== bqPageLength :" + bqPageLength+ "===totalBqItemCount/bqPageLength :" + totalBqItemCount/bqPageLength);



totalPage = Math.ceil(totalBqItemCount/bqPageLength);

if(totalPage == 0 ||  totalPage === undefined || totalPage === ''){
	totalPage = 1;
}

if(totalPage < 5){
visiblePage = totalPage;
}
	$.validate({
		lang : 'en',
		modules : 'file,sanitize'
	});
	// itemQuantity
	// BUYER_FIXED_PRICE
	
		$(function (e) {
//		e.preventDefault();
	    var obj = $('#pagination').twbsPagination({
	        totalPages: totalPage,
	        visiblePages: visiblePage
	    });
	});

	    $(document).delegate('.page-link', 'click', function(e) {
	 	   e.preventDefault();
	 	  var searchVal = $('#bqItemSearch').val();
  		var choosenVal = $('#chooseSection option:selected').text();
  		var page = $('#pagination').find('li.active').text();
          var pageNo = parseInt(page);
  		var pageLen = "50";
          if ($('#selectPageLen option:selected').text()) {
          	pageLen = $('#selectPageLen').val();
  		}
  		var pageLength = parseInt(pageLen);
//          console.log("searchVal :"+searchVal + " choosenVal  : "+choosenVal +" page :"+pageNo + " pageLength : "+pageLength) ;

  		var isPageEnable = false;
  		if(searchVal === "" || choosenVal === ""){
  		isPageEnable = true; 
  		}
          searchFilterBqItem(choosenVal , searchVal ,pageNo ,pageLength, isPageEnable);
	     });
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/sourcingRequestSor.js?5"/>"></script>
<script>
	<c:if test="${supplierBqCount or (eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'KEEP_NOTIFY' or event.suspensionType == 'KEEP_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
		var allowedFields = '#nextStep,#priviousStep,.s1_view_desc,#idViewButton,#bubble ,#resetButton,#bqItemSearch,#selectPageLen ,#chooseSection';
	$(window).bind('load', function() {
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	var allowedFields = '#nextStep,#priviousStep,.s1_view_desc,#bubble, #downloadTemplate ,#resetButton,#bqItemSearch,#selectPageLen,#chooseSection';
	$(window).bind('load', function() {
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	/* $('html,body').animate({
	    scrollTop : showTable.offset().top
	   }, 'slow'); */
	
	
</script>
<style>
.s1_view_desc {
	color: #0095d5 !important;
}

.label-top {
	margin-top: 10px;
}

.width_140_fix {
	width: 140px;
}
.allignToRight{
	text-align: center !important;
}

.bq_font
{
 font-family: 'open_sanssemibold'; font-weight:normal;	
}
</style>