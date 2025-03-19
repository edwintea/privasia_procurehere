<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>



<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prDetailsDesk" code="application.pr.create.details" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prDetailsDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="pr.purchase.requisition" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="pr.purchase.requisition" />
					</h2>
				</div>
				<jsp:include page="prHeader.jsp"></jsp:include>
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<jsp:include page="prTagHeader.jsp"></jsp:include>
				<div class="clear"></div>
				<c:url var="createPrDetails" value="/buyer/createPrDetails" />
				<form:form id="prCreateForm" action="${createPrDetails}" method="post" modelAttribute="pr" acceptCharset="UTF-8">
					<div class="tab-pane active">
						<div class="tab-content Invited-Supplier-List ">
							<div class="tab-pane active white-bg" id="step-1">
								<div class="upload_download_wrapper clearfix mar-t20 event_info">
									<h4>
										<spring:message code="pr.details" />
									</h4>
									<div class="row">
										<div class="form-tander1 pad_left_15 mb-10">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> </label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:checkbox path="urgentPr" cssClass="custom-checkbox" />
												<label style="line-height: 0px;"><spring:message code="prtemplate.urgent.pr" /></label>
											</div>
										</div>
										<div class="form-tander1 pad_nbottom_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="pr.id" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:hidden path="id" />
												<form:hidden path="prId" />
												<form:hidden path="templateId" value="${prTemplate.id}" />
												<form:hidden path="status" value="${pr.status}" />
												<p class="line-height35">${pr.prId}</p>
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="pr.reference.number" />
												</label>
											</div>
											<spring:message var="placeReference" code="pr.place.reference.number" />
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:input path="referenceNumber" type="text" placeholder="${placeReference}" data-validation="alphanumeric length" data-validation-allowing="-_ /" data-validation-optional="true" data-validation-length="1-64" class="form-control" />
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="pr.name" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<spring:message var="placeName" code="pr.place.name" />
												<form:input path="name" type="text" readonly="${!empty templateFields ? (tf:prReadonly( templateFields, 'PR_NAME' )) : '' }" placeholder="${placeName}" data-validation="required length" data-validation-length="max128" class="form-control" />
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="pr.description" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<spring:message var="placeDescription" code="pr.place.description" />
												<form:textarea path="description" placeholder="${placeDescription}" rows="5" data-validation="length" data-validation-length="max1000" class="form-control"></form:textarea>
												<span class="sky-blue"><spring:message code="dashboard.valid.max2.characters" /></span>
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="pr.creator" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="buyerAddressRadios1 active width100 pull-left">
													<p class="color6b set-line-height">
														${pr.createdBy.name} <br> ${pr.createdBy.communicationEmail} <br>
														<c:if test="${not empty buyer.companyContactNumber}"> <spring:message code="prdraft.tel" />: ${buyer.companyContactNumber} </c:if>
														<c:if test="${not empty buyer.faxNumber}"> <spring:message code="prtemplate.fax" />: ${buyer.faxNumber}</c:if>
														<c:if test="${not empty pr.createdBy.phoneNumber}">  <spring:message code="prtemplate.hp" />: ${ pr.createdBy.phoneNumber}</c:if>
													</p>
												</div>
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="pr.requester" /> :
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<spring:message var="placeRequester" code="pr.place.requester" />
												<form:textarea path="requester" rows="5" maxlength="500" readonly="${!empty templateFields ? (tf:prReadonly( templateFields, 'REQUESTER' )) : '' }" placeholder="${placeRequester}" data-validation="required length" data-validation-length="max500" class="form-control"></form:textarea>
												<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
											</div>
										</div>
									</div>
									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="pr.correspondence.address" /> :
											</label>
											<div class="col-sm-5 col-md-5 col-xs-5">
												<div class="input-prepend input-group">
													<label class="physicalCriterion pull-left"> </label> <span class="pull-left buyerAddressRadios <c:if test="${not empty pr.correspondenceAddress}">active</c:if>"> <span class="phisicalArressBlock pull-left marg-top-10"> <c:if test="${not empty pr.correspondenceAddress}">
																<div class="">
																	<h5>${pr.correspondenceAddress.title}</h5>
																	<span class='desc'>${pr.correspondenceAddress.line1}, ${pr.correspondenceAddress.line2}, ${pr.correspondenceAddress.city}, ${pr.correspondenceAddress.zip}, ${pr.correspondenceAddress.state.stateName}, ${pr.correspondenceAddress.state.country.countryName}</span>
																</div>
															</c:if>
													</span>
													</span>
												</div>
												<div id="sub-credit" class="invite-supplier delivery-address margin-top-10" ${buyerReadOnlyAdmin ? 'disabled' : ''} ${!empty templateFields ? (tf:prReadonly( templateFields, 'CORRESPONDENCE_ADDRESS' ) ? 'style="display: none;"' : '') : '' }>
													<div class="role-upper ">
														<div class="col-sm-12 col-md-12 col-xs-12 float-left">
															
															<input type="text" placeholder='<spring:message code="event.search.address.placeholder" />'
																class="form-control delivery_add">
														</div>
													</div>
													<div class="chk_scroll_box">
														<div class="scroll_box_inner" id=corrAddress>
															<div class="role-main">
																<div class="role-bottom small-radio-btn ${buyerReadOnlyAdmin ? 'disabled' : ''} ${!empty templateFields ? (tf:prReadonly( templateFields, 'CORRESPONDENCE_ADDRESS' ) ? 'disabled' : '') : '' }">
																	<ul class="role-bottom-ul">
																		<c:forEach var="address" items="${addressList}">
																			<li>
																				<div class="radio-info">
																					<label> <form:radiobutton path="correspondenceAddress" id= "correspondenceAddress" value="${address.id}" class="custom-radio" data-validation-error-msg-container="#address-buyer-dialog" data-validation="buyer_address" />
																					</label>
																				</div>
																				<div class="del-add">
																					<h5>${address.title}</h5>
																					<span class='desc'>${address.line1}, ${address.line2}, ${address.city}, ${address.zip}, ${address.state.stateName}, ${address.country}</span>
																				</div>
																			</li>
																		</c:forEach>
																	</ul>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div id="address-buyer-dialog"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="upload_download_wrapper marg-top-10 event_info white-bg">
								<h4>
									<spring:message code="pr.finance" />
								</h4>
								<div class="event_form">
									<div class="form_field">
										<div class="form-group">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label><spring:message code="prtemplate.lock.budget" /> </label>
											</div>
											<div>
												<div class="col-sm-1 col-md-1 col-xs-1">
													<form:checkbox path="lockBudget" style="display:none" cssClass="custom-checkbox" />
													<input type="hidden" id="lockBudgetValue" value="${pr.lockBudget== true?'true':'false'}">
												
													<c:if test="${pr.lockBudget eq true }">
														<label class="line-height35"><img class="" style="margin-bottom: 7px;" height="20" width="20" title="Locked" src="${pageContext.request.contextPath}/resources/images/lock.png"> <spring:message code="application.yes2" /></label>
													</c:if>
													<c:if test="${pr.lockBudget eq false }">
														<label class="line-height35"><img class="" style="margin-bottom: 7px;" height="20" width="20" title="Locked" src="${pageContext.request.contextPath}/resources/images/lock.png"> <spring:message code="application.no2" /></label>
													</c:if>
												</div>
												
												<c:if test="${pr.lockBudget eq true }">
													<input type="hidden" id="availableBudget" value="${pr.availableBudget}">
													<div class="col-sm-4 col-md-3 col-xs-6" >
														<fmt:formatNumber var="remainingAmt" type="number" minFractionDigits="2" maxFractionDigits="6" value="${pr.availableBudget}" />
															<p class="alert-lbl bg-lbl" id="remainAmt">Remaining Amount : ${remainingAmt} ${pr.budgetCurrencyCode}</p>
													</div>	
												</c:if>	
											</div>	
										</div>
									</div>
									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="pr.base.currency" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="form-group ${!empty templateFields ? (tf:prReadonly( templateFields, 'BASE_CURRENCY' ) ? 'disabled' : '' )  : '' }">
													<form:select path="currency" style="${!empty templateFields ? (tf:prReadonly( templateFields, 'BASE_CURRENCY' ) ? 'opacity:0' : '') : ''  }" cssClass="form-control chosen-select" data-validation="required" class="custom-select">
														<form:option value="${pr.currency}">
															<%-- <spring:message code="pr.select.curreny" /> --%>
														</form:option>
														<form:options items="${currencyList}" itemValue="id" />
													</form:select>
												</div>
											</div>
										</div>
									</div>
									<c:if test="${pr.lockBudget eq true }">
									<c:if test="${pr.conversionRateRequired }">
										<div class="form_field">
											<div class="form-group">
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="pr.base.currency.conversion.rate" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<div class="form-group">
														<form:input readonly="${!empty templateFields ? (tf:prReadonly( templateFields, 'CONVERSION_RATE' ) ? 'true' : '') : '' }" path="conversionRate" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" class="form-control" data-validation="required validate_custom_length positive1" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" />
													</div>
												</div>
											</div>
										</div>
									</c:if>
									</c:if>
									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="pr.decimal" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="form-group ${!empty templateFields ? (tf:prReadonly( templateFields, 'DECIMAL' ) ? 'disabled' : '') : '' }">
													<form:select path="decimal" style="${!empty templateFields ? (tf:prReadonly( templateFields, 'DECIMAL' ) ? 'opacity:0' : '') : '' }"
																 cssClass="form-control chosen-select" data-validation="required" id="iddecimal" onchange="changeDecimal(this.value)">
														<form:option value="${pr.decimal}">
															<spring:message code="pr.select.decimal" />
														</form:option>
														<form:option value="1">1</form:option>
														<form:option value="2">2</form:option>
														<form:option value="3">3</form:option>
														<form:option value="4">4</form:option>
													</form:select>
												</div>
											</div>
										</div>
									</div>
									<c:if test="${!empty templateFields ? (tf:prVisibility( templateFields, 'COST_CENTER' )) : 'true'}">
										<div class="form_field">
											<div class="form-group">
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="label.costcenter" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<div class="form-group ${!empty templateFields ? (tf:prReadonly( templateFields, 'COST_CENTER' ) ? 'disabled' : '') : ''}">
														<form:select path="costCenter" id="costCenterId" data-validation="${!empty templateFields ? (tf:prRequired( templateFields, 'COST_CENTER' ) ? 'required' : '') : ''}" style="${!empty templateFields ? (tf:prReadonly( templateFields, 'COST_CENTER' ) ? 'opacity:0' : '') : '' }" cssClass="form-control chosen-select" class="custom-select">
															<form:option value="">
																<spring:message code="pr.select.cost.center" />
															</form:option>
														
															<c:forEach items="${costCenterList}" var="costCenter">
																<form:option value="${costCenter.id}">${costCenter.costCenter} - ${costCenter.description}</form:option>
															</c:forEach>
														</form:select>
													</div>
												</div>
											</div>
										</div>
									</c:if>
									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="label.businessUnit" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6 ${idSetting.idSettingType == 'BUSINESS_UNIT' ? '' : ''}">
												<div class="form-group ${!empty templateFields ? (tf:prReadonly( templateFields, 'BUSINESS_UNIT' ) ? '' : '') : ''}">
													<form:select path="businessUnit" id="businessUnit" cssClass="form-control chosen-select" data-validation="required" class="custom-select">
														<form:option value="">
															<spring:message code="pr.select.business.unit" />
														</form:option>
														<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
													</form:select>
												</div>																								
											</div>																		
										</div>
									</div>	

										<form:input style="display: none;" id="budgetCurrencyCode" readonly="true" path="budgetCurrencyCode" type="text" />
										<c:if test="${pr.lockBudget eq false && (!empty templateFields ? (tf:prVisibility(templateFields, 'AVAILABLE_BUDGET')) : true)}">
											<div class="form_field" style="margin-top: 15px;">
												<div class="form-group">
													<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="label.availableBudget" />
													</label>
													<div class="col-sm-5 col-md-5 col-xs-6">
														<div class="form-group ${!empty templateFields ? (tf:prReadonly( templateFields, 'AVAILABLE_BUDGET' ) ? 'disabled' : '') : ''}">
															<c:choose>
																<c:when test="${pr.decimal==1}">
																	<c:set var="decimalSet" value="0,0.0"></c:set>
																</c:when>
																<c:when test="${pr.decimal==2}">
																	<c:set var="decimalSet" value="0,0.00"></c:set>
																</c:when>
																<c:when test="${pr.decimal==3}">
																	<c:set var="decimalSet" value="0,0.000"></c:set>
																</c:when>
																<c:when test="${pr.decimal==4}">
																	<c:set var="decimalSet" value="0,0.0000"></c:set>
																</c:when>
															</c:choose>
															<fmt:formatNumber var="availableBudget" type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.availableBudget}" />
															<form:input id="availableBudget" path="availableBudget" type="text" class="form-control" placeholder="${availableBudget}" data-validation="validate_max_13 ${!empty templateFields ? (tf:prRequired( templateFields, 'AVAILABLE_BUDGET' ) ? ',required' : '') : ''}" data-sanitize="numberFormat" data-validation-length="max13" data-sanitize-number-format="${decimalSet}" data-validation-regexp="^[\d,]{1,7}(\.\d{1,4})?$" />
														</div>
													</div>
												</div>
											</div>
										</c:if>
										<form:hidden path="paymentTermDays" />
										<div class="form_field">
											<div class="form-group">
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> 
													<spring:message code="pr.payment.term" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6 ">
												<c:set var="paymentTermShow" value="${!empty templateFields ? (tf:prReadonly( templateFields, 'PAYMENT_TERM') ? 'true' : 'false') : 'false'}" />
													<c:if test="${paymentTermShow eq false || !empty pr.paymentTermes}">
														<form:hidden path="paymentTerm" />
														<div class="form-group ${!empty templateFields ? (tf:prReadonly( templateFields, 'PAYMENT_TERM' ) ? 'disabled' : '') : ''}">
															<form:select path="paymentTermes" id="paymentTermes" cssClass="form-control chosen-select" data-validation="required" class="custom-select">
																<form:option value="">
																	<spring:message code="pr.select.payment.terms" />
																</form:option>
																<c:forEach items="${paymentTermsList}" var="paymentTerm">
																	<form:option value="${paymentTerm.id}">${paymentTerm.paymentTermCode} - ${paymentTerm.description}</form:option>
																</c:forEach>
															</form:select>
														</div>
													</c:if>
													<c:if test="${paymentTermShow eq true && empty pr.paymentTermes}">
														<div class="form-group">
															<spring:message var="placePaymentTerm" code="pr.place.paymentTerm" />
															<form:textarea path="paymentTerm" rows="5" maxlength="500" readonly="${!empty templateFields ? (tf:prReadonly( templateFields, 'PAYMENT_TERM' )) : ''}" placeholder="${placePaymentTerm}" data-validation="required length" data-validation-length="max500" class="form-control"></form:textarea>
															<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
														</div>
													</c:if>
												</div>																		
											</div>
										</div>
<!-- 									<div class="row"> -->
<!-- 										<div class="col-sm-4 col-md-3 col-xs-6"></div> -->
<!-- 									</div> -->
<!-- 									<div class="form_field" style="margin-top:15px"> -->
<%-- 										<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="pr.payment.term" /> --%>
<!-- 										</label> -->
<!-- 										<div class="col-sm-5 col-md-5 col-xs-6"> -->
<!-- 											<div class="form-group"> -->
<%-- 												<spring:message var="placePaymentTerm" code="pr.place.paymentTerm" /> --%>
<%-- 												<form:textarea path="paymentTerm" rows="5" maxlength="500" readonly="${!empty templateFields ? (tf:prReadonly( templateFields, 'PAYMENT_TERM' )) : ''}" placeholder="${placePaymentTerm}" data-validation="required length" data-validation-length="max500" class="form-control"></form:textarea> --%>
<%-- 												<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span> --%>
<!-- 											</div> -->
<!-- 										</div> -->
<!-- 									</div> -->

									<%-- <div class="form_field">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label>Terms & Conditions :</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:textarea path="termsAndConditions" rows="5" readonly="${!empty templateFields ? (tf:prReadonly( templateFields, 'TERM_AND_CONDITION' )) : '' }" placeholder="Terms & Conditions" data-validation="required length" data-validation-length="max1000"
													class="form-control"></form:textarea>
													<span class="sky-blue">Max 1000 characters only</span>
											</div>
										</div> --%>
								
							</div>
						</div>
					</div>
					<div class="upload_download_wrapper clearfix mar-t20 event_info ">
						<h4><spring:message code="pr.event.team.members" /></h4>
						<jsp:include page="prTeamMembers.jsp"></jsp:include>
					</div>
					<c:if test="${!empty prTemplate ? (tf:prApprovalVisibility(prTemplate)) : 'true'}">
						<%-- 	<c:out var="appRequired" value="${(tf:prApprovalRequired(prTemplate)) }"></c:out> --%>
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab in">
						<h4>
							<spring:message code="rfi.createrfi.approvalroute.label.pr.creation" /> 
							: (Add minimum ${prTemplate.minimumApprovalCount} approval level<c:if test="${prTemplate.minimumApprovalCount > 1}">s</c:if>)
						</h4>
							<%--   <div class="meeting2-heading">
								<div class="checkbox checkbox-primary">  
									<label>  <input id="inlineCheckbox110" ${!empty pr.prApprovals ? "checked='checked'" : ""} ${!empty prTemplate ? 'disabled="(tf:prApprovalReadonly(prTemplate))"' : ''} data-toggle="collapse" data-target="#apprTab"
											class="custom-checkbox" type="checkbox">  
									 </label>
								 </div>
							</div>   --%>
							<div id="apprTab" data-aproval="${(tf:prApprovalRequired(prTemplate)) }" class="pad_all_15 collapse in float-left width-100 position-relative in">
								<!-- <div class="col-md-6"> -->
								<jsp:include page="prApproval.jsp"></jsp:include>
								<p>
									<input type="text" value="" data-validation="check_approver" id="approverSCount" name="approverSCount" />
								</p>
								<!-- 	</div> -->
							</div>
						</div>
					</c:if>
					<div class="btn-next">
						<button id="nextButton" class="btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1">
							<spring:message code="application.next" />
						</button>
						<spring:message code="application.draft" var="draft" />
						<input type="button" id="submitStep1PrDetailDraft" class="top-marginAdminList step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" value="${draft}" />
						<c:if test="${pr.status eq 'DRAFT' && (isAdmin or eventPermissions.owner)}">
							<a href="#confirmCancelPr" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="prtemplate.cancel.pr" /></a>
						</c:if>
					</div>
				</form:form>
			</section>
		</div>
	</div>
</div>
<!-- remove editor list popup -->
<div class="flagvisibility dialogBox" id="removeUserListPopup">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="userListId" name="userListId" value=""> <input type="hidden" id="userListType" name="userListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 userInfoBlock">
				
				<spring:message code="event.confirm.to.remove" /> <span></span> <spring:message code="application.from" /> <span></span>
				<spring:message code="application.list" />
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeUserListPerson" data-original-title="Delete"><spring:message code="tooltip.delete" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<!-- remove viewer list popup -->
<div class="flagvisibility dialogBox" id="removeViewerListPopup">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="viewerListId" name="viewerListId" value=""> <input type="hidden" id="viewerListType" name="viewerListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 viewerInfoBlock">
				
				<spring:message code="event.confirm.to.remove" /> <span></span> <spring:message code="application.from" /> <span></span>
				<spring:message code="application.list" />
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					
					<a href="javascript:void(0);" title=""
						class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeViewerListPerson"
						data-original-title="Delete"><spring:message code="tooltip.delete" /></a>
					<button type="button"
						class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<!-- cancel pr popup  -->
<div class="modal fade" id="confirmCancelPr" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="prId" value="${pr.id}">
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							
							<label> <spring:message code="prtemplate.sure.want.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
								<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<textarea class="width-100"
								placeholder="${reasonCancel}" rows="3"
								name="cancelReason" id="cancelReason"
								data-validation="required length"
								data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPr" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.no2" /></button>
				</div>
			</form>
		</div>
	</div>
</div>
<style>
.bg-lbl{
	margin-bottom: 10px;
    background: red;    
    color: #fff !important;    
    display: inline-block;
    border-radius: 5px;
    padding-left: 10px !important;
    padding-right: 10px !important;
    margin-left: 10px !important;
}
.alert-lbl {
	color: red;
	padding: 7px 5px;
	font-size: 14px;
	font-weight: bold;
}

.buyerAddressRadios1 {
	border: 1px solid #dfe8f1;
	border-radius: 2px;
	display: block !important;
	padding: 10px;
}

.item>label {
	float: right;
	width: 90%;
}

.item>input {
	float: left;
}

.item {
	min-height: 100px;
	height: auto;
}

span.optinTxt {
	float: left;
	width: calc(100% - 60px);
}

.role-bottom {
	padding-top: 0px !important;
}

#approverSCount {
	width: 0;
	height: 0;
	border: 0;
	margin-bottom: 10px;
}

.pad_left_15 {
	padding-top: 0px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 10px;
}

.pad_nbottom_15 {
	padding-top: 15px;
	padding-right: 15px;
	padding-bottom: 0px;
	padding-left: 15px;
}


.mb-10 {
	margin-bottom: 10px;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>


$('document').ready(function() {
	
	$('#cancelPr').click(function() {
		$(this).addClass('disabled');
	});
	
	var availableBudget=$("#availableBudget").val();
	if(availableBudget==''){
		$("#remainAmt").css("visibility", "hidden");
	}
	/* setTimeout(function(){ 
		$("#remainAmt").text( "Remaining Amount : " + $('#availableBudget').val() +" "+ $('#budgetCurrencyCode').val());
	}, 1000);
	 */
	 
	
});

<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
$(window).bind('load', function() {
	var allowedFields = '#nextButton,#dashboardLink,#bubble,#correspondenceAddress';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('.dropUp').addClass('disabled');
	$('#corrAddress').addClass('readOnlyClass');
	$('#corrAddress').addClass('disabled');
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});


</c:if>
	<%--$.formUtils.addValidator({--%>
	<%--  name : 'check_approver',--%>
	<%--  validatorFunction : function(value, $el, config, language, $form) {		  --%>
	<%--	  var response = true;--%>
	<%--	  var labelVal = $.trim($('#apprTab').find('select').select2("val"));--%>
	<%--	  if((labelVal == '' || $('#apprTab').find('select').length == 0) && ${(tf:prApprovalRequired(prTemplate)) } == true){--%>
	<%--		  response = false;--%>
	<%--	  }--%>
	<%--	  return response;--%>
	<%--  },--%>
	<%--  errorMessage : 'Approver is required',--%>
	<%--  errorMessageKey: 'badCheckApprover'--%>
	<%--});--%>
	$.validate({
		lang : 'en',
		modules : 'date,sanitize'
	});
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
	
	$(".item").each(function(){
		//var html = "<input type='checkbox'><label>"+$(this).text()+"</label>";
		//$(this).html(html);
	});
	
	
	$('#businessUnit').on('change',function() {
		var value=this.value;
			
		
			$.ajax({
				type : "POST",
				url : getContextPath() + "/buyer/checkBusinessUnitSetting/PR",
				
				beforeSend : function(xhr) {
					$('#loading').show();
				
				},
				complete : function() {
					$('#loading').hide();
				},
				success : function(data, textStatus, request) {
					/* showMessage('SUCCESS', request.getResponseHeader('success'));
					$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
					$('div[id=idGlobalSuccess]').show(); */
					console.log("success");
				},
				error : function(request, textStatus, errorThrown) {
				console.log("error");
					//	showMessage('ERROR', request.getResponseHeader('error'));
					$("#businessUnit").val(value)
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					// alert('Error: ' + request.getResponseHeader('error'));
				}
			});
		});
	
	$.formUtils.addValidator({
		name : 'validate_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 10) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 10 characters',
		errorMessageKey : 'validateLengthCustom'
	});
	
	$.formUtils.addValidator({
		name : 'validate_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 20) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 20 number',
		errorMessageKey : 'validateLengthCustom'
	});

	$.formUtils.addValidator({
		name : 'positive1',
		required : false,
		validatorFunction : function(val, $el) {

			if (val != null && val != '')
				return Number(val.replace(/,/g, "")) >= 0;
		},
		errorMessage : 'Please Insert Correct Amount',
		errorMessageKey : 'positiveCustomValuu'
	});
	
	$.formUtils.addValidator({
		name : 'validate_max_13',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 13) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 13 characters',
		errorMessageKey : 'validateLengthCustom'
	});
	
	$('#businessUnit').on('change', function(){
			var value=this.value;
			if(value != undefined && value != ''){
			<c:if test="${isEnableUnitAndCostCorrelation}">
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var url = getContextPath() +  "/buyer/getAssingedCostCenterList";
				$.ajax({
					type : "GET",
					url : url,
					data : {
						unitId : value,
					},
					beforeSend : function(xhr) {
						var html='';
						$('#costCenterId').html(html);
					    $("#costCenterId").trigger("chosen:updated");
						$('#loading').show();
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
					},
					complete : function() {
						$('#loading').hide();
					},
					success : function(data) {
						var html='';
						html += '<option value="" selected="selected">'+ 'Select Cost Center' +'</option>';
						$.each(data, function(i, item) {
							if(item.description.length > 0){
								html += '<option value="' + item.id + '">' + item.costCenter + ' - ' + item.description + '</option>';
							}else{
								html += '<option value="' + item.id + '">' + item.costCenter + '</option>';
							}
						});
						$('#costCenterId').append(html);
						$("#costCenterId").trigger("chosen:updated");
					},
					error : function(request, textStatus, errorThrown) {
						var error = request.getResponseHeader('error');
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
				});
			</c:if>
			}
		})

       function changeDecimal(val) {
		   if( val != null ) {
			   var availableBudget = document.getElementById('availableBudget');
			   var value = parseFloat(availableBudget.value.replace(/[^\d\.]/g,''));
			   if(value != null) {
				   var roundedUpValue = Math.ceil(value * 10000) / 10000;
				   var formattedValue = roundedUpValue.toFixed(val);

				   if(!isNaN(formattedValue)) {
					   availableBudget.value = formattedValue;
				   }
			   }
		   }
	   }
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prCreate.js"/>"></script>
<script>
$(document).ready(function() {
    // Store form data before navigation
    $('#nextButton').click(function(e) {
        if (!validationApprover()) {
			e.preventDefault();
		}
		localStorage.setItem('prName', $('#name').val());
        localStorage.setItem('prDescription', $('#description').val());
    });

    // Restore form data on page load
    var savedName = localStorage.getItem('prName');
    var savedDesc = localStorage.getItem('prDescription');
    
    if (savedName && $('#name').val() == '') {
        $('#name').val(savedName);
    }
    if (savedDesc && $('#description').val() == '') {
        $('#description').val(savedDesc);
    }

    // Clear storage after successful save
    $('#prCreateForm').on('submit', function() {
        localStorage.removeItem('prName');
        localStorage.removeItem('prDescription');
    });
});
</script>
