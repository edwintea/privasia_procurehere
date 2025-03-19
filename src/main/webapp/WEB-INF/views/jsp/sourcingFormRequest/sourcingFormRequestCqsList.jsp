<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxCreateDetails" code="application.rfx.create.details" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxQuestionnairesDesk}] });
});
</script>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li>
						<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
						</a>
					</li>
					<li class="active">
						<spring:message code="rfs.sourcing.form.request" />
					</li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap tender-request-heading">
							<spring:message code="rfs.create.sourcing.Questionnaire" />
						</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />
							: ${sourcingFormRequest.status}
						</h2>

					</div>
					<jsp:include page="sourcingFormHeader.jsp"></jsp:include>
					<div class="tab-pane active error-gap-div">
						<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
						<div class="clear"></div>
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<div class="upload_download_wrapper clearfix event_info">
							<c:if test="${showCq }">
						<div class="margin-bottom-10">
							<div class="meeting2-heading">
								<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
								  <button class="pull-right btn ph_btn_midium btn-success reArrangeOrder hvr-pop" type="button" data-toggle="tooltip" data-original-title='<spring:message code="rearrange.order"/>' style="margin-right: 20px; margin-top: 7px; height: 30px; line-height: 1;margin-bottom: 7px;"><spring:message code="rearrange.order"/></button>
							    </c:if>
							</div>
						     </div>
								<div class="Invited-Supplier-List dashboard-main">
									<div class="doc-fir-inner">
										<h4>
											<spring:message code="application.questionnaire" />
										</h4>
										<div class="Invited-Supplier-List dashboard-main">
											<div class="Invited-Supplier-List-table add-supplier id-list">
												<div class="ph_tabel_wrapper">
													<div class="main_table_wrapper ph_table_border payment marg-bottom-20 document-table  mega questionnaire-table">
														<table class="table ph_table border-none header documents-page top-fix-head" width="100%" border="0" cellspacing="0" cellpadding="0">
															<thead>
																<tr>
																	<th class=" align-center width_50_fix"><spring:message code="supplier.no.col" /></th>
																	<th class=" align-center width_200_fix"><spring:message code="application.name" /></th>
																	<th class=" align-center width_200_fix"><spring:message code="application.createddate" /></th>

																	<th class=" align-center width_200_fix"></th>
																</tr>
															</thead>
														</table>
														<c:forEach var="cqobj" items="${sourcingFormcq}" varStatus="idx">
															<table class="ph_table table border-none tab_trans timezone induslist documents-page" width="100%" border="0" cellspacing="0" cellpadding="0">
																<tbody class="catecontent">
																	<tr>
																	   <c:if test="${cq.cqOrder != null}">
																		 <td class="width_50_fix">${cqobj.cqOrder}</td>
																	   </c:if>
																	   <c:if test="${cq.cqOrder == null}">
																	     <td class="width_50_fix">${idx.index+1}</td>
																	   </c:if>
																		<td class="align-center width_200_fix">${cqobj.name}</td>
																		<td class="align-center width_200_fix">
																			<fmt:formatDate value="${cqobj.createdDate}" pattern="dd/MMM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																		</td>
																		<td class="align-center width_200_fix">
																			<a href="${pageContext.request.contextPath}/buyer/viewSourcingCqDetails/${sourcingFormRequest.id}/${cqobj.id}" sourcingReq-id="${sourcingFormRequest.id}" sourcingCq-id="${cqobj.id}" class=" btn btn-info btn-cq-view"><spring:message code="rfs.view.button" /></a>
																		</td>
																	</tr>
																</tbody>
															</table>
														</c:forEach>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="marg-top-20 btns-lower">
									<div class="row">
										<div class="col-md-12 col-xs-12 col-ms-12">
											<c:url var="rfsDocument" value="/buyer/rfsDocument/${sourcingFormRequest.id}" />
											<a href="${rfsDocument}" id="previousButton" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1 previousStep1"><spring:message code="application.previous" /></a>

											<spring:message code="event.document.next" var="Next" />
											<a id="BQLink" href='<c:url value="/buyer/createSourcingRequestBqList/${sourcingFormRequest.id}"/>' class="btn btn-info ph_btn marg-left-10 hvr-pop hvr-rectangle-out"> <spring:message code="application.next" />
											</a>
											<c:if test="${(isAdmin or eventPermissions.owner)}">
												<a href="#confirmCancelRequest" role="button" class="btn btn-danger ph_btn hvr-pop pull-right" data-toggle="modal"><spring:message code="rfs.summary.cancel.request" /></a>
											</c:if>
											<spring:message code="application.draft" var="draft" />
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${!showCq}">
								<div class="cqlistDetails">
									<form:form id="sourcingReqCqList" action="${pageContext.request.contextPath}/buyer/saveSourcingReqCqDetails/${sourcingFormRequest.id}?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="sourcingFormReqCqItemPojo" enctype="multipart/form-data">
										<form:hidden path="cqId" />
										<div class="Gen-ques ">
											<h4 class="marg-left-none">
												<spring:message code="rfs.cq.general" />
											</h4>
											<div class="Gen-ques-inner " style="padding:15px;">
												<p class="marg-top-15 marg-bottom-15">
													<font color="red">*</font>
													<spring:message code="rfs.cq.required.field" />
												</p>
												<div class="Gen-ques-inner1 pad_all_15 ">
													<c:forEach var="item" items="${sourcingFormReqCqItemPojo.itemList}" varStatus="status">
														<form:hidden path="itemList[${status.index}].id" />
														<form:hidden path="itemList[${status.index}].cq.id" />
														<form:hidden path="itemList[${status.index}].cqItem.id" />
														<div class="Quest-textbox ">
															<div class="row">
																<div class="col-md-12 col-sm-12 col-xs-12">
<%-- 																	<label>${item.cqItem.level}.${item.cqItem.order} ${item.cqItem.optional ? '<font color="red">*</font>' : ''}&nbsp;&nbsp; ${item.cqItem.itemName}</label> --%>
																 	<c:if test="${item.cqItem.order == 0}"><span class="section_name">${item.cqItem.level}.${item.cqItem.order} ${item.cqItem.optional ? '<font color="red">*</font>' : ''}&nbsp;&nbsp; ${item.cqItem.itemName}</span></c:if>
																 	<c:if test="${item.cqItem.order > 0}"><span class="item_name" >${item.cqItem.level}.${item.cqItem.order} ${item.cqItem.optional ? '<font color="red">*</font>' : ''}&nbsp;&nbsp; ${item.cqItem.itemName}</span></c:if>
																</div>
															</div>
															<div class="row">
																<div class="col-md-12">
																	<div class="col-md-12 col-xs-12 note-tag mobileDesc">${item.cqItem.itemDescription}.</div>
																</div>
																<div class="col-md-5 col-sm-5 col-xs-6">
																	<c:if test="${item.cqItem.cqType.value == 'Text'}">
																		<div>
																			<form:textarea data-validation="${item.cqItem.optional ? 'required':'' } length" data-validation-length="max1000" class="form-control textarea-autosize" path="itemList[${status.index}].textAnswers"></form:textarea>
																		</div>
																	</c:if>
																	<c:if test="${item.cqItem.cqType.value == 'Choice'}">
																		<div class="radio_yes-no width100">
																			<c:forEach var="cqOptions" items="${item.cqItem.displayCqOptions}" varStatus="opIndex">
																				<c:if test="${ fn:contains(sourcingFormReqCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<span class="width100 inlineStyle item_name"> <form:radiobutton data-validation="${item.cqItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" checked="checked" />&nbsp;&nbsp;${cqOptions.value}
																					</span>
																				</c:if>
																				<c:if test="${!fn:contains(sourcingFormReqCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<span class="width100 inlineStyle item_name"> <form:radiobutton class="custom-radio" data-validation-error-msg-container="#${item.cqItem.id}-rediooption-err-msg" data-validation="${item.cqItem.optional ? 'required':'' }" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" />&nbsp;&nbsp;${cqOptions.value}
																					</span>
																				</c:if>
																			</c:forEach>
																			<div id="${item.cqItem.id}-rediooption-err-msg"></div>
																		</div>
																	</c:if>
																	<c:if test="${item.cqItem.cqType.value == 'Choice with Score'}">
																		<div class="radio_yes-no width100">
																			<c:forEach var="cqOptions" items="${item.cqItem.displayCqOptions}" varStatus="opIndex">
																				<c:if test="${ fn:contains(sourcingFormReqCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<span class="width100 inlineStyle item_name"> <form:radiobutton data-validation-error-msg-container="#${item.cqItem.id}-redio-err-msg" data-validation="${item.cqItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" checked="checked" />&nbsp;&nbsp;${cqOptions.value}
																					</span>
																				</c:if>
																				<c:if test="${!fn:contains(sourcingFormReqCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<span class="width100 inlineStyle item_name"> <form:radiobutton data-validation-error-msg-container="#${item.cqItem.id}-redio-err-msg" data-validation="${item.cqItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" />&nbsp;&nbsp;${cqOptions.value}
																					</span>
																				</c:if>


																			</c:forEach>

																			<div id="${item.cqItem.id}-redio-err-msg"></div>
																		</div>

																	</c:if>
																	<c:if test="${item.cqItem.cqType.value == 'List'}">
																		<span class="dropUp">
																			<form:select id="cqItemOption" path="itemList[${status.index}].listOptAnswers" cssClass="chosen-select form-control option_list_all" data-cqId="${item.cq.id}" data-cqItemId="${item.cqItem.id}" data-validation="${item.cqItem.optional ? 'required':'' }" multiple="multiple">
<%-- 																				<form:options items="${item.cqItem.displayCqOptions}" itemLabel="value" /> --%>
																				<c:forEach items="${sourcingFormReqCqItemPojo.itemList[status.index].listOptAnswers}" var="sel">
																					<option value="${sel.order}" selected >${sel.value}</option>
																				</c:forEach>				
																				<c:forEach items="${optinonList}" var="entry">
																					<c:if test="${entry.key == item.cqItem.id}">
																						<c:forEach items="${entry.value}" var="option">
																							<c:if test="${option.id == '-1'}">
																								<form:option value="-1" label="${option.value}" disabled="true" />
																							</c:if>
																							<c:if test="${option.id != '-1' }">
																								<form:option value="${option.order}" label="${option.value}" />
																							</c:if>
																						</c:forEach>
																					</c:if>
																				</c:forEach>
																			</form:select>
																		</span>
																	</c:if>
																	<c:if test="${item.cqItem.cqType.value == 'Checkbox'}">
																		<div>
																			<c:forEach var="cqOptions" items="${item.cqItem.displayCqOptions}" varStatus="opIndex">
																				<div class="radio_yes-no width100 item_name">
																					<c:if test="${ fn:contains(sourcingFormReqCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																						<form:checkbox class="custom-checkbox ${item.cqItem.id}-class" data-validation-error-msg-container="#${item.cqItem.id}-err-msg" data-group="${item.cqItem.id}" data-validation="${item.cqItem.optional ?  'required_checkbox' : '' }" path="itemList[${status.index}].listOptAnswers[${opIndex.index}]" value="${cqOptions}" checked="checked" /> &nbsp;&nbsp;${cqOptions.value}
																					</c:if>
																					<c:if test="${!fn:contains(sourcingFormReqCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																						<form:checkbox class="custom-checkbox ${item.cqItem.id}-class" data-validation-error-msg-container="#${item.cqItem.id}-err-msg" data-group="${item.cqItem.id}" data-validation="${item.cqItem.optional ?  'required_checkbox' : '' }" path="itemList[${status.index}].listOptAnswers[${opIndex.index}]" value="${cqOptions}" /> &nbsp;&nbsp;${cqOptions.value}
																					</c:if>
																				</div>
																			</c:forEach>
																			<div id="${item.cqItem.id}-err-msg"></div>
																		</div>
																	</c:if>

																	<c:if test="${item.cqItem.attachment}">
																		<div class="margin-top-10">
																			<div class="fileinput fileinput-new input-group uploadFileBlockQuesInput" data-provides="fileinput" data-itemId="${item.cqItem.id}" <c:if test="${not empty sourcingFormReqCqItemPojo.itemList[status.index].fileName}">style="display:none;"</c:if>>
																				<div class="form-control" data-trigger="fileinput">
																					<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span>
																				</div>
																				<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"><spring:message code="application.selectfile2" /> </span> <span class="fileinput-exists"><spring:message code="application.change" /></span> <c:set var="fileType" value="" /> <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
																						<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
																					</c:forEach> <form:input class="uploadQuestionFileAttch" type="file" path="itemList[${status.index}].attachment" id="uploadFileAttachId" data-validation-allowing="${fileType}" data-validation="${item.cqItem.isSupplierAttachRequired ? 'required':'' } extension size" data-validation-error-msg-container="#Load_File-error-dialog${status.index}" data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation-error-msg-required="Please select file"
																						data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="You can only upload file pdf, docx xls and xlsx"></form:input>
																				</span> <a href="#" class="input-group-addon btn btn-black fileinput-exists" data-dismiss="fileinput"><spring:message code="application.remove" /></a>
																			</div>
																			<div id="Load_File-error-dialog${status.index}" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
																			<div class="ph_table_border uploadedFileBlockQues pull-left width100 <c:if test="${empty sourcingFormReqCqItemPojo.itemList[status.index].fileName}">flagvisibility</c:if>">
																				<div class="reminderList marginDisable">
																					<div class="row" id="">
																						<div class="col-md-12">
																							<p>
																								<c:if test="${not empty sourcingFormReqCqItemPojo.itemList[status.index].fileName}">
																									<form:form method="GET">
																										<c:url var="download" value="/buyer/downloadCqAttachment/${sourcingFormRequest.id}/${sourcingFormReqCqItemPojo.itemList[status.index].id}" />
																										<a class="pull-left" href="${download}">${sourcingFormReqCqItemPojo.itemList[status.index].fileName}</a>
																									</form:form>
																									<a class="pull-right removeFile" removeOtherId='${sourcingFormReqCqItemPojo.itemList[status.index].id}' otherCredFile='${sourcingFormReqCqItemPojo.itemList[status.index].fileName}'> <i class="fa fa-trash-o"></i>
																									</a>
																								</c:if>
																							</p>
																						</div>
																					</div>
																				</div>
																			</div>
																			<c:if test="${empty sourcingFormReqCqItemPojo.itemList[status.index].fileName}">
																				<span> <spring:message code="application.note" />:<br />
																					<ul>
																						<li>
																							<spring:message code="createrfi.documents.max.size" />
																							${ownerSettings.fileSizeLimit} MB
																						</li>
																						<li>
																							<spring:message code="createrfi.documents.allowed.file.extension" />
																							: ${fileType}.
																						</li>
																					</ul>
																				</span>
																			</c:if>
																		</div>
																	</c:if>
																</div>
																<div class="col-md-5 col-sm-5 col-xs-6 note-tag deskTopDesc">${item.cqItem.itemDescription}.</div>
															</div>
														</div>
													</c:forEach>
												</div>
												<input type="hidden" name="sourcingidHiddenData" id="sourcingidHiddenData" value="${sourcingFormRequest.id}" />
												<div class="row">
													<div class="col-md-12 col-xs-12 col-sm-12 margin-top-10 supplierCqSubmtBtons">
														<a href="${pageContext.request.contextPath}/buyer/sourcingFormRequestCqList/${sourcingFormRequest.id}" class="btn btn-black ph_btn_midium back_to_Question"><spring:message code="rfs.cq.back.quetionnaire" /></a>
														<form:button class="btn btn-info ph_btn_midium marg-left-10" id="savebutton" type="submit" >
															<spring:message code="application.save" />
														</form:button>
													</div>
												</div>
											</div>
										</div>
									</form:form>
								</div>
							</c:if>
						</div>
					</div>
				</section>
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
				<input type="hidden" name="formId" id="formId" value="${sourcingFormRequest.id}">
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
				<c:forEach items="${sourcingFormcq}" var="cq" varStatus="loop">
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
	
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelRequest" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form  action="${pageContext.request.contextPath}/buyer/cancelSourcingReq/${sourcingFormRequest.id}" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="request.confirm.to.cancel" /> </br>
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<textarea name="description" class="width-100" placeholder="${reasonCancel}" id="reasonCancel" rows="3" data-validation="required length" data-validation-length="max500" ></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="submit" id="RfsCancelRequest" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>	
	
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/widgets/file-input/file-input.js"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<script>
$.validate({
	lang : 'en',
	modules : 'file'
});


$.formUtils.addValidator({ name : 'required_checkbox', validatorFunction : function(value, $el, config, language, $form) {
	console.log('Required Checkbox group : ', $el.attr('data-group') , ' Total Checked : ', $('.' + $el.attr('data-group') + '-class :checked').length);
	var cbxGroup = $el.attr('data-group') + '-class';
	var totalChecked = $('input:checkbox.'+cbxGroup + ':checked').length;
	
	return (totalChecked > 0);
	
}, errorMessage : 'Select at least one option', errorMessageKey : 'err_required_checkbox' });


<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser or sourcingFormRequest.status ne 'DRAFT'}">
var allowedFields = '#meetingListNext,#priviousStep,#idvewbutton,#bubble,#previousButton,#BQLink';
	$(window).bind('load', function() {
//var disableAnchers = ['#reloadMsg'];        
disableFormFields(allowedFields);
});
</c:if>

</script>
<style>
.radio_yes-no {
	padding-bottom: 15px;
}

.radio_yes-no span {
	display: inline-block;
	width: 100%;
}

.Gen-ques-inner1  .radio_yes-no span label {
	width: auto;
	float: none;
	padding-left: 10px;
}

.width100.inlineStyle .radio+label {
	width: auto !important;
	padding: 0;
}

.width100.inlineStyle label {
	float: left;
	width: 95% !important;
	display: inline-block;
}

.width100.inlineStyle input {
	display: inline-block;
	float: left;
}

.hideElement {
	width: 0 !important;
	height: 0 !important;
	padding: 0 !important;
	min-width: 0 !important;
	max-width: 0;
	border: 0;
}
ul {
    list-style-type: none;
}
.header {
    position: relative !important;
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
.width_60_fix {
    width: 64px;
}
.width_135_fix {
    width: 135px;
}
.font-ch-weight{
font-weight: 600 !important;
}
.radio input[type="radio"] {
    margin-left: 0;
}
</style>
<script>
$(document).ready(function() {
	$("#cqItemOption").chosen({ search_contains: true });
	
		$(".general-btn").click(function() {
			$(".doc-fir-inner").hide();
		});
		$(".general-btn").click(function() {
			$(".Gen-ques").show();
		});

		$(".bill-quantityA").click(function() {
			$(".doc-fir-inner1").hide();
		});
		$(".bill-quantityA").click(function() {
			$(".Bill-option-A").show();
		});

		$(".bill-quantityB").click(function() {
			$(".doc-fir-inner1").hide();
		});
		$(".bill-quantityB").click(function() {
			$(".Bill-option-B").show();
		});
		$('.uploadQuestionFileAttch').change(function(e) {
			e.preventDefault();
			$('#sourcingReqCqList').isValid();
		});
		
 	 	$('#savebutton').click(function(e){
			e.preventDefault();
			if (!$('#sourcingReqCqList').isValid()) {
				return false;
			}
			$('#sourcingReqCqList').submit();
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
<script>
	$(document).ready(function() {
		$(".back_to_Question").click(function() {
			$(".Gen-ques").hide();
		});

		$(".back_to_Question").click(function() {
			$(".Quest-Scoring").hide();
		});
		$(".back_to_Question").click(function() {
			$(".doc-fir-inner").show();
		});

		$(".back_to_BQ").click(function() {
			$(".doc-fir-inner1").show();
		});

		$(".back_to_BQ").click(function() {
			$(".Bill-option-B").hide();
		});
		$(".back_to_BQ").click(function() {
			$(".Bill-option-A").hide();
		});
		$('.event-details-tabs li').click(function() {
			$(this).find('form').submit();
		});

	});
</script>
<script>
	$(document).ready(function() {
		$(".fade-btn").click(function() {
			$(".meeting-detail").hide();
		});
		$(".fade-btn").click(function() {
			$(".no-attand-meeting").show();
		});
	});
</script>
<script>
	$(document).ready(function() {
		$('select').chosen({ disable_search : true });
		$(".Questi-btn").click(function() {
			$(".doc-fir-inner").hide();
		});
		$(".Questi-btn").click(function() {
			$(".Quest-Scoring").show();
		});
	});
</script>
<script type="text/javascript">
	$('.table-1 .custom-checkbox').on('change', function() {

		var check = this.checked;
		$(".table-2 [type=checkbox]").each(function() {
			$(".table-2 [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
		});

	});

	$('.table-2 [type=checkbox]').on('change', function() {

		var total = $(".table-2 [type=checkbox]").length;
		var checked = $(".table-2 .checker .checked").length;
		var firstObj = $('.table-1 .custom-checkbox');

		if (checked == total) {
			firstObj.prop('checked', true);
		} else {
			firstObj.prop('checked', false);
		}
		$.uniform.update(firstObj);

	});

	$("#test-select ").treeMultiselect({ enableSelectAll : true,
	sortable : true });
</script>
<script>
	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});
</script>
<script>
	counterMeetings();
	setInterval(function() {
		counterMeetings();
	}, 60000);
	function counterMeetings() {
		$('.countDownTimer1').each(
				function() {
					var target_date = new Date($(this).attr('data-date')).getTime();
					var days, hours, minutes, seconds;
					var current_date = new Date().getTime();
					var seconds_left = (target_date - current_date) / 1000;
					days = parseInt(seconds_left / 86400);
					seconds_left = seconds_left % 86400;
					hours = parseInt(seconds_left / 3600);
					seconds_left = seconds_left % 3600;
					minutes = parseInt(seconds_left / 60);
					seconds = parseInt(seconds_left % 60);
					htmldata = '';
					if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
						htmldata = '<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + days + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + hours
								+ '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + minutes + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">' + seconds + '</span></span>';
					}
					$(this).html(htmldata);
				});
	}
</script>
<script>
		// set the date we're counting down to

		// get tag element
		//var countdown = document.getElementById('countdown');
		//alert(countdown);
		// update the tag with id "countdown" every 1 second
		counterMeetings1();
		setInterval(function() {

			counterMeetings1();

		}, 60000);
		function counterMeetings1() {
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
							htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes
									+ '</span><span class="seconds"><b>Seconds</b><br>' + seconds + '</span>';
						}//$('#countdown').html(htmldata);
						$(this).html(htmldata);

					});
		}
</script>
<script>
	// filter meetings
	$('.radio_meeting').on('click', '#showAll', function() {
		$('.meeting-detail').show();
		$('#eventFilter').children().first().html('All Meeting');
	});
	$('.radio_meeting').on('click', '#showAccepted', function() {
		$('.meeting-detail').hide();
		$('.meeting-detail.accepted').show();
		$('#eventFilter').children().first().html('Accepted Meeting');
	});
	$('.radio_meeting').on('click', '#showPending', function() {
		$('.meeting-detail').hide();
		$('.meeting-detail.pending').show();
		$('#eventFilter').children().first().html('Pending Meeting');
	});
	$('.radio_meeting').on('click', '#showCanceled', function() {
		$('.meeting-detail').hide();
		$('.meeting-detail.canceled').show();
		$('#eventFilter').children().first().html('Canceled Meeting');
	});
	
</script>

<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/sourcingFormRequestCqList.js?1"/>"></script>
