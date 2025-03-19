<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="tab-main-inner pad_all_15">
	<div id="tab-4" class="tab-content">
		<div class="cqlistDetails">
				 <form:form  action=""  modelAttribute="supplierFormSubmissionItemPojo" > 
					<div class="Gen-ques">
						<h3 class="marg-left-none">
							<spring:message code="prsummary.general.information" />
						</h3>
						<div class="Gen-ques-inner">
							<p class="marg-top-15 marg-bottom-15">
								<font color="red">*</font>
								<spring:message code="rfs.cq.required.field" />
							</p>
							<div class="Gen-ques-inner1 pad_all_15">
							<c:forEach var="item" items="${supplierFormSubmissionItemPojo.itemList}" varStatus="status">
									<div class="Quest-textbox">
										<div class="row">
											<div class="col-md-12 col-sm-12 col-xs-12">
												<label>${item.formItem.level}.${item.formItem.order} ${item.formItem.optional ? '<font color="red">*</font>' : ''}&nbsp;&nbsp; ${item.formItem.itemName}</label>
											</div>
										</div>
										<div class="row">
											<div class="col-md-12">
												<div class="col-md-12 col-xs-12 note-tag mobileDesc">${item.formItem.itemDescription}.</div>
											</div>
										 <div class="col-md-5 col-sm-5 col-xs-6">
												<c:if test="${item.formItem.cqType.value == 'Text'}">
													<div>
														<form:textarea readonly="true"  class="form-control textarea-autosize" path="itemList[${status.index}].textAnswers"  ></form:textarea>
													</div>
												</c:if>
													<c:if test="${item.formItem.cqType.value == 'Choice'}">
													<div class="radio_yes-no width100 disabled"  >
														<c:forEach var="cqOptions" items="${item.formItem.displayCqOptions}" varStatus="opIndex">
															<c:if test="${ fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																<label class="width100 inlineStyle"> 
																	<form:radiobutton  class="custom-radio" path=""  value="${cqOptions}" checked="checked" disabled="disabled" /> ${cqOptions.value}
																</label>
															</c:if>
															<c:if test="${!fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																<label class="width100 inlineStyle"> 
																	<form:radiobutton class="custom-radio" path="" value="${cqOptions}"  disabled="disabled"/> ${cqOptions.value}
																</label>
															</c:if>
														</c:forEach>
														<div id="${item.formItem.id}-rediooption-err-msg"></div>
													</div>
												</c:if>
												<c:if test="${item.formItem.cqType.value == 'Choice with Score'}">
													<div class="radio_yes-no width100 disabled">
														<c:forEach var="cqOptions" items="${item.formItem.displayCqOptions}" varStatus="opIndex">
															<c:if test="${ fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																<label class="width100 inlineStyle"> 
																	<form:radiobutton  class="custom-radio" path=""  value="${cqOptions}" checked="checked"  disabled="disabled"/> ${cqOptions.value}
																</label>
															</c:if>
															<c:if test="${!fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																<label class="width100 inlineStyle"> 
																	<form:radiobutton  class="custom-radio" path="" value="${cqOptions}" disabled="disabled" /> ${cqOptions.value}
																</label>
															</c:if>
														</c:forEach>
													</div>
												</c:if>
												<c:if test="${item.formItem.cqType.value == 'List'}">
													<div class="disabled"> 
														<form:select cssClass="chosen-select form-control" path="itemList[${status.index}].listOptAnswers" data-validation="${item.formItem.optional ? 'required':'' }" >
															<form:option value="" disabled="disabled">Select</form:option>
															<form:options  items="${item.formItem.displayCqOptions}" itemLabel="value" />
														</form:select>
														</div>
													</c:if>
												<c:if test="${item.formItem.cqType.value == 'Checkbox'}">
													<div class="disabled">
														<c:forEach var="cqOptions" items="${item.formItem.displayCqOptions}" varStatus="opIndex">
															<div class="radio_yes-no width100">
																<c:if test="${ fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																	<form:checkbox class="custom-checkbox ${item.formItem.id}-class"  path="" value="${cqOptions}" checked="checked" /> ${cqOptions.value} </label>
																</c:if>
																<c:if test="${!fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																	<form:checkbox class="custom-checkbox ${item.formItem.id}-class"  path="" value="${cqOptions}"   /> ${cqOptions.value} </label>
																</c:if>
															</div>
														</c:forEach>
													</div>
												</c:if>
 												<c:if test="${item.formItem.cqType.value == 'Date'}">
													<div class="input-prepend input-group disabled">
														<form:input id="dateTypeAnswer" type="text" cssClass="bootstrap-datepicker  nvclick form-control for-clander-view"  value="${item.textAnswers}"  path="" autocomplete="off"  
														data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy" disabled="disabled"></form:input>
													</div>
												</c:if>

												<c:if test="${item.formItem.cqType.value == 'Number'}">
													<div id="numberTypeInput">
														<form:input id="numberAnswerinput"  class="numberTypeAnswer form-control textarea-autosize" path="" value="${item.textAnswers}" readonly="true" ></form:input>
													</div>
												</c:if>
												<c:if test="${item.formItem.cqType.value == 'Paragraph'}">
													<div>
														<form:textarea rows="4" readonly="true"  class="form-control textarea-autosize" path="itemList[${status.index}].textAnswers"></form:textarea>
 													</div>
												</c:if>
												<c:if test="${not empty item.itemAttachment}">
													<div class="margin-top-10">
														<div class="tab-main-inner pad_all_15 mb-13">
															<ul>
																<c:forEach var="formDoc" items="${item.itemAttachment}">
																<li>
	                                                                 <a href="${pageContext.request.contextPath}/buyer/downloadFormItemAttachment/${formDoc.id}" data-placement="top" class="buyerAttachDownload" title="Download"> ${formDoc.fileName}</a>
																</li>
																</c:forEach>
															</ul>
														</div>
													</div>
												</c:if>
												<c:if test="${item.formItem.attachment}">
													<div class="margin-top-10">
														<div id="Load_File-error-dialog${status.index}" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
														<div class="ph_table_border uploadedFileBlockQues pull-left width100 <c:if test="${empty supplierFormSubmissionItemPojo.itemList[status.index].fileName}">flagvisibility</c:if> " style="margin-top: 10px;">
															<div class="reminderList marginDisable">
																	<p style="margin-top: 10px; margin-left: 15px">Supplier Attachment</p>
																<div class="row" id="">
																	<div class="col-md-12">
																		<p>
																			<c:if test="${not empty supplierFormSubmissionItemPojo.itemList[status.index].fileName}">
																				<form:form method="GET">
																					<a class="pull-left formAttachmentDownload" title="Download" id="attachmentDownload"  href="${pageContext.request.contextPath}/buyer/downloadFormAttachment/${supplierForm.id}/${supplierFormSubmissionItemPojo.itemList[status.index].id}">${supplierFormSubmissionItemPojo.itemList[status.index].fileName}</a>
																				</form:form>
																			</c:if>
																		</p>
																	</div>
																</div>
															</div>
														</div>
													</div>
												</c:if>
											</div> 
											<div class="col-md-5 col-sm-5 col-xs-6 note-tag deskTopDesc">${item.formItem.itemDescription}.</div>
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</form:form>
			</div>
 	</div>
</div>

<style>
.radio input[type="radio"] {
    margin-left: 0;
}
</style>
