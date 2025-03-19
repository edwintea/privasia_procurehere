<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="poDetailsDesk" code="application.po.create.details" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${poDetailsDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li>
				    <a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" /></a>
                </li>
				<li class="active"><spring:message code="po.purchase.order" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="po.purchase.order" />
					</h2>
					<h2 class="trans-cap pull-right">
                        <spring:message code="buyer.dashboard.po.status" />
                        : ${po.status}
                    </h2>
				</div>
				<jsp:include page="poHeader.jsp"></jsp:include>
				<div class="clear"></div>

				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<jsp:include page="poTagHeader.jsp"></jsp:include>
				<div class="clear"></div>
				<c:url var="createPoDetails" value="/buyer/createPoDetails" />
				<input type="hidden" name="poId" id="poId" value="${po.id}" />
				<input type="hidden" name="prId" id="prId" value="${pr.id}" />
				<form:form id="poCreateForm" action="#" method="post" modelAttribute="po" acceptCharset="UTF-8">
					<div class="tab-pane active">
						<div class="tab-content Invited-Supplier-List ">
							<div class="tab-pane active white-bg" id="step-1">
								<div class="upload_download_wrapper clearfix mar-t20 event_info">
									<h4>
										<spring:message code="po.details" />
									</h4>
									<div class="row">
										<div class="form-tander1 pad_nbottom_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
                                                <label> <spring:message code="pr.number.label" /></label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:hidden path="id" />
												<form:hidden path="poId" />
												<form:hidden path="status" value="${postatus}" />
												<p class="line-height35">
												    <a href="${pageContext.request.contextPath}/buyer/prView/${pr.id}" >
												        ${pr.prId}
                                                    </a>
                                                </p>
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="po.reference.number" /></label>
											</div>
											<spring:message var="placeReference" code="pr.place.reference.number" />
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:input path="referenceNumber" type="text" placeholder="${placeReference}" data-validation="alphanumeric length" data-validation-allowing="-_ /" data-validation-optional="true" data-validation-length="1-64" class="form-control" />
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="po.name" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<spring:message var="placeName" code="pr.place.name" />
												<form:input path="name" type="text" readonly="${!empty templateFields ? (tf:prReadonly( templateFields, 'PR_NAME' )) : '' }" placeholder="${placeName}" data-validation="required length" data-validation-length="max128" class="form-control" />
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="po.description" />
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
												<label> <spring:message code="po.creator" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="buyerAddressRadios1 active width100 pull-left">
<%--													<p class="color6b set-line-height">--%>
													    <c:choose>
                                                            <c:when test="${!isAutoCreatePo}">
                                                                <p class="color6b set-line-height">
                                                                    ${po.createdBy.name} <br>
                                                                    ${po.createdBy.communicationEmail} <br>
                                                                    <c:if test="${not empty buyer.companyContactNumber}">
                                                                        <spring:message code="prdraft.tel" />: ${buyer.companyContactNumber}
                                                                    </c:if>
                                                                    <c:if test="${not empty buyer.faxNumber}">
                                                                        <spring:message code="prtemplate.fax" />: ${buyer.faxNumber}
                                                                    </c:if>
                                                                    <c:if test="${not empty po.createdBy.phoneNumber}">
                                                                        <spring:message code="prtemplate.hp" />: ${po.createdBy.phoneNumber}
                                                                    </c:if>
                                                                </p>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <p class="color6b set-line-height">
                                                                    ${pr.createdBy.name} <br>
                                                                    ${pr.createdBy.communicationEmail} <br>
                                                                    <c:if test="${not empty buyer.companyContactNumber}">
                                                                        <spring:message code="prdraft.tel" />: ${buyer.companyContactNumber}
                                                                    </c:if>
                                                                    <c:if test="${not empty buyer.faxNumber}">
                                                                        <spring:message code="prtemplate.fax" />: ${buyer.faxNumber}
                                                                    </c:if>
                                                                    <c:if test="${not empty pr.createdBy.phoneNumber}">
                                                                        <spring:message code="prtemplate.hp" />: ${pr.createdBy.phoneNumber}
                                                                    </c:if>
                                                                </p>
                                                            </c:otherwise>
                                                        </c:choose>
<%--													</p>--%>
												</div>
											</div>
										</div>
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="po.requester" /> :
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
													<label class="physicalCriterion pull-left"> </label> <span class="pull-left buyerAddressRadios <c:if test="${not empty po.correspondenceAddress}">active</c:if>"> <span class="phisicalArressBlock pull-left marg-top-10"> <c:if test="${not empty po.correspondenceAddress}">
                                                        <div class="">
                                                            <h5>${po.correspondenceAddress.title}</h5>
                                                            <span class='desc'>${po.correspondenceAddress.line1}, ${po.correspondenceAddress.line2}, ${po.correspondenceAddress.city}, ${po.correspondenceAddress.zip}, ${po.correspondenceAddress.state.stateName}, ${po.correspondenceAddress.state.country.countryName}</span>
                                                        </div>
                                                    </c:if>
													</span>
													</span>
												</div>
												<div id="sub-credit" class="invite-supplier delivery-address margin-top-10" ${buyerReadOnlyAdmin ? 'disabled' : ''} ${!empty templateFields ? (tf:prReadonly( templateFields, 'CORRESPONDENCE_ADDRESS' ) ? 'style="display: none;"' : '') : '' }>
													<div class="role-upper ">
														<div class="col-sm-12 col-md-12 col-xs-12 float-left">

															<input type="hidden" name="deliveryAddress" value="${po.deliveryAddress.id}"/>

															<input type="text" placeholder='<spring:message code="event.search.address.placeholder" />'
																class="form-control delivery_add">
														</div>
													</div>
													<div class="chk_scroll_box">
														<div class="scroll_box_inner" id=corrAddress>
															<div class="role-main">
																<div class="role-bottom small-radio-btn ${buyerReadOnlyAdmin ? 'disabled' : ''} ${!empty templateFields ? (tf:prReadonly( templateFields, 'CORRESPONDENCE_ADDRESS' ) ? 'disabled' : '') : '' }">
																	<ul class="role-bottom-ul">
																	    <c:forEach var="address" items="${addressList}" varStatus="status">
                                                                            <li>
                                                                                <div class="radio-info">
                                                                                    <label>
                                                                                        <form:radiobutton
                                                                                            path="correspondenceAddress"
                                                                                            id="test${status.index + 1}"
                                                                                            value="${address.id}"
                                                                                            class="custom-radio"
                                                                                            data-validation-error-msg-container="#address-buyer-dialog"
                                                                                            data-validation="buyer_address"
                                                                                            checked="${address.id eq correspondenceAddress.Id}"
                                                                                        />
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
									<spring:message code="po.finance" />
								</h4>
								<div class="event_form">

									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="pr.base.currency" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="form-group">
													<form:select path="currency"  cssClass="form-control chosen-select" data-validation="required" class="custom-select">
														<form:option value="${po.currency}">
															<%-- <spring:message code="po.select.curreny" /> --%>
														</form:option>
														<form:options items="${currencyList}" itemValue="id" />
													</form:select>
												</div>
											</div>
										</div>
									</div>

									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="pr.decimal" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="form-group">
													<form:select path="decimal" style="${!empty templateFields ? (tf:prReadonly( templateFields, 'DECIMAL' ) ? 'opacity:0' : '') : '' }"
																 cssClass="form-control chosen-select" data-validation="required" id="iddecimal" onchange="changeDecimal(this.value)">
														<form:option value="${po.decimal}">
															<spring:message code="po.select.decimal" />
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
													<div class="form-group disabled">
														<form:select path="costCenter" id="costCenterId" data-validation="${!empty templateFields ? (tf:prRequired( templateFields, 'COST_CENTER' ) ? 'required' : '') : ''}" style="${!empty templateFields ? (tf:prReadonly( templateFields, 'COST_CENTER' ) ? 'opacity:0' : '') : '' }" cssClass="form-control chosen-select" class="custom-select">
															<form:option value="">
																<spring:message code="po.select.cost.center" />
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
											<div class="col-sm-5 col-md-5 col-xs-6 disabled">
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
                                                        <form:select path="paymentTermes.id" id="paymentTermes" data-validation="${!empty templateFields ? (tf:prRequired(templateFields, 'PAYMENT_TERM' ) ? 'required' : '') : ''}" style="${!empty templateFields ? (tf:prReadonly( templateFields, 'PAYMENT_TERM' ) ? 'opacity:0' : '') : '' }" cssClass="form-control chosen-select" class="custom-select">
                                                            <form:option value="">
                                                                <spring:message code="pr.select.payment.terms" />
                                                            </form:option>

                                                            <c:forEach items="${paymentTermsList}" var="paymentTerm">
                                                                <form:option value="${paymentTerm.id}" >${paymentTerm.paymentTermCode} - ${paymentTerm.description}</form:option>
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
						<h4><spring:message code="po.event.team.members" /></h4>
						<jsp:include page="poTeamMembers.jsp"></jsp:include>
					</div>

					<c:if test="${!empty prTemplate and prTemplate.approvalPoVisible}">
                    	<c:if test="${po.status eq 'REVISE' or po.status eq 'DRAFT' or po.status eq 'SUSPENDED'}">
                            <div class="${prTemplate.approvalPoReadOnly?'disabled':''} upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab in approval_div">
                                <h4><spring:message code="po.approval.route" />
                                    <c:choose>
                                        <c:when test="${prTemplate.minimumPoApprovalCount > 0}"> : (Add minimum ${prTemplate.minimumPoApprovalCount} approval level)</c:when>
                                        <c:otherwise></c:otherwise>
                                    </c:choose>
                                </h4>
                                <div id="apprTab" data-aproval="${!prTemplate.approvalPoOptional}" class="pad_all_15 collapse in float-left width-100 position-relative in">
                                    <jsp:include page="poApproval.jsp"></jsp:include>
                                    <p>
                                        <input type="text" value=""  id="approverSCount" name="approverSCount" />
                                    </p>
                                </div>
                            </div>

                            <!-- hidden request by adilah
                            <div class="${prTemplate.approvalPoReadOnly?'disabled':''}">
                                <jsp:include page="poSummaryApprovals.jsp"></jsp:include>
                            </div>
                            -->
                    	</c:if>
                    </c:if>
                    <!-- hidden request by adilah
                    <div class="clear"></div>
                    <jsp:include page="poAudit.jsp" />
                    -->

                    <div class="clear"></div>

                    <c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED') && eventPermissions.owner}">
                        <div class="btn-next">
                            <button id="nextButton" type="submit" class="btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out ">
                                <spring:message code="application.next" />
                            </button>

                            <spring:message code="application.draft" var="draft" />
                            <input type="button" id="submitPoDraft" class="top-marginAdminList step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" value="${draft}" />
                            <c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED') && eventPermissions.owner}">
                                <a href="#confirmCancelPo" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal">Cancel PO</a>
                            </c:if>
                        </div>
					</c:if>
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

<jsp:include page="poModal.jsp"></jsp:include>

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

div.disabled {
  pointer-events: none;
}
</style>
<c:set var="userId" value="${userId}" scope="request" />
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
var toDashboard=false;
$('document').ready(function() {

    var addressId="${po.correspondenceAddress.id}";

	if(addressId){
		<c:forEach var="address" items="${addressList}" varStatus="status">
		    var addr="${address.id}";
		    console.log(addressId+" >>>> "+addr);

              if(addressId === addr){
                $("#test"+"${status.index+1}").prop("checked", true);
              }
	    </c:forEach>
	}

    var passedApproval=false;

	var availableBudget=$("#availableBudget").val();
	if(availableBudget==''){
		$("#remainAmt").css("visibility", "hidden");
	}
	/* setTimeout(function(){
		$("#remainAmt").text( "Remaining Amount : " + $('#availableBudget').val() +" "+ $('#budgetCurrencyCode').val());
	}, 1000);
	 */
});


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
<script type="text/javascript" src="<c:url value="/resources/js/view/poCreate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/po.js"/>"></script>
