<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<title><spring:message code="eventDescription.title" /></title>
<spring:message var="rfxCreateDesc" code="application.rfx.create.description" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreateDesc}] });
});
</script>
<div id="page-wrapper">
	<input type="hidden" class="addBillOfQuantity" value="${eventType != 'RFI' && event.addBillOfQuantity}">
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
					<h2 class="trans-cap tender-request-heading">
						<spring:message code="application.create" />
						${eventType.value}
					</h2>
				</div>
				<jsp:include page="eventHeader.jsp" />
				<div class="clear"></div>
				<div class="row clearfix">
					<div class="col-sm-12"><jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" /></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				</div>
				<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="event" action="eventDescription">
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="eventdescription.name" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle">
							<div class="col-md-6 ">
								<p>
									<spring:message code="eventdescription.description.text" />
								</p>
								<div class="row">
									<div class="marg-top-15">
										<div class="col-md-4 col-sm-6">
											<label> <spring:message code="eventdescription.description.label" />
											</label>
										</div>
										<div class="col-md-8 col-sm-6">
											<form:hidden path="id" id="eventId" />
											<form:hidden path="template.id" />
											<form:textarea path="eventDescription" class="form-control autoSave" maxlength="2000" data-validation="length" data-validation-length="max2000" />
											<span class="sky-blue"><spring:message code="createrfi.event.description.max.chars" /></span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="internalremarks.name" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle">
							<div class="col-md-6">
								<div class="row">
									<div class="marg-top-15">
										<div class="col-md-4 col-sm-6">
											<label> <spring:message code="internal.remarks.label" />
											</label>
										</div>
										<div class="col-md-8 col-sm-6">
											<form:textarea path="internalRemarks" class="form-control autoSave" maxlength="2000" data-validation="length" data-validation-length="max2000" />
											<span class="sky-blue"><spring:message code="internal.remark.error" /></span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PROCUREMENT_METHOD' ) || tf:visibility( templateFields, 'PROCUREMENT_CATEGORY' )) : 'true' }">
						<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
							<div class="meeting2-heading">
								<h3>
									<spring:message code="sourcing.procurement.info" />
								</h3>
							</div>
							<div class="import-supplier-inner-first-new pad_all_15 global-list">
								<div class="col-md-6">
									<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PROCUREMENT_METHOD' )) : 'true' }">
										<div class="row marg-bottom-10">
											<div class="col-md-4 col-sm-6">
												<label class="marg-top-10"> <spring:message code="eventdescripton.procurement.info.method" />
												</label>
											</div>
											<div class="col-md-8 dd sky mar_b_10 col-sm-6 ">
												<c:if test="${!empty templateFields ? (tf:readonly(templateFields, 'PROCUREMENT_METHOD')) : 'false'}">
													<input type="hidden" name="procurementMethod" value="${event.procurementMethod.id}" />
												</c:if>
												<form:select path="procurementMethod" cssClass="form-control chosen-select autoSave" id="idProcurementMethod" 
													data-validation="${!empty templateFields ? (tf:required( templateFields, 'PROCUREMENT_METHOD' ) ? 'required' : '') : '' }"
													disabled="${tf:readonly( templateFields, 'PROCUREMENT_METHOD' )}" style="${!empty templateFields ? (tf:readonly( templateFields, 'PROCUREMENT_METHOD' )? 'opacity:0' : '') : ''  }">
													<form:option value="">
														<spring:message code="procurement.info.method.select" />
													</form:option>
													<form:options items="${procurementMethodList}" itemValue="id" itemLabel="procurementMethod" />
												</form:select>
											</div>
										</div>
									</c:if>
									<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PROCUREMENT_CATEGORY' )) : 'true' }">
										<div class="row marg-bottom-10">
											<div class="col-md-4 col-sm-6">
												<label class="marg-top-10"> <spring:message code="eventdescription.procurement.info.category" />
												</label>
											</div>
											<div class="col-md-8 dd sky mar_b_10 col-sm-6 ">
												<c:if test="${!empty templateFields ? (tf:readonly(templateFields, 'PROCUREMENT_CATEGORY')) : 'false'}">
													<input type="hidden" name="procurementCategories" value="${event.procurementCategories.id}" />
												</c:if>
												<form:select path="procurementCategories" cssClass="form-control chosen-select autoSave" id="idProcurementCategory" 
													data-validation="${!empty templateFields ? (tf:required( templateFields, 'PROCUREMENT_CATEGORY' ) ? 'required' : '') : '' }"
													 disabled="${tf:readonly( templateFields, 'PROCUREMENT_CATEGORY' )}" style="${!empty templateFields ? (tf:readonly( templateFields, 'PROCUREMENT_CATEGORY' )? 'opacity:0' : '') : ''  }">
													<form:option value="">
														<spring:message code="procurement.info.category.select" />
													</form:option>
													<form:options items="${procurementCategoryList}" itemValue="id" itemLabel="procurementCategories" />
												</form:select>
											</div>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</c:if>

					<div class="Invited-Supplier-List import-supplier white-bg">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="eventdescription.finance.label" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="col-md-6">
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'BASE_CURRENCY' )) : 'true' }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="eventdescription.basecurrency.label" />
											</label>
										</div>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6 ">
											<c:if test="${!empty templateFields ? (tf:readonly(templateFields, 'BASE_CURRENCY')): 'false' }">
												<input type="hidden" name="baseCurrency" value="${event.baseCurrency.id}" />
											</c:if>
											<form:select path="baseCurrency" data-validation="${!empty templateFields ? (tf:required( templateFields, 'BASE_CURRENCY' ) ? 'required' : '') : 'required' }" cssClass="form-control chosen-select autoSave" id="idCurrency" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'BASE_CURRENCY' )) : 'false' }"
												style="${!empty templateFields ? (tf:readonly( templateFields, 'BASE_CURRENCY' ) ? 'opacity:0' : '') : '' }">
												<form:option value="">
													<spring:message code="currency.select" />
												</form:option>
												<form:options items="${currency}" itemValue="id" />
											</form:select>
										</div>
									</div>
								</c:if>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'DECIMAL' )) : 'true' }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="eventdescription.decimal.label" />
											</label>
										</div>
										<c:if test="${!empty templateFields ? (tf:readonly(templateFields, 'DECIMAL')) : 'false' }">
											<input type="hidden" name="decimal" value="${event.decimal}" />
										</c:if>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<form:select path="decimal" data-validation="${!empty templateFields ? (tf:required( templateFields, 'DECIMAL' ) ? 'required' : '') : 'required'}" cssClass="form-control chosen-select  disablesearch decimalChange autoSave" id="iddecimal" disabled="${!empty templateFields ? (tf:readonly( templateFields, 'DECIMAL' )) : 'false' }"
												style="${!empty templateFields ? (tf:readonly( templateFields, 'DECIMAL' ) ? 'opacity:0' : '') : '' }">
												<form:option value="1">1</form:option>
												<form:option value="2">2</form:option>
												<form:option value="3">3</form:option>
												<form:option value="4">4</form:option>
												<form:option value="5">5</form:option>
												<form:option value="6">6</form:option>
											</form:select>
										</div>
									</div>
								</c:if>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'COST_CENTER' )) : 'true' }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="eventdescription.costcenter.label" />
											</label>
										</div>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6 ">
											<c:if test="${!empty templateFields ? (tf:readonly( templateFields, 'COST_CENTER' )) : 'false' }">
												<input type="hidden" name="costCenter" value="${event.costCenter.id}" />
											</c:if>
											<form:select path="costCenter" cssClass="form-control chosen-select autoSave" id="costCenterId" disabled="${tf:readonly( templateFields, 'COST_CENTER' ) }" 
												data-validation="${!empty templateFields ? (tf:required( templateFields, 'COST_CENTER' ) ? 'required' : '') : '' }"
												style="${tf:readonly( templateFields, 'COST_CENTER' ) ? 'opacity:0' : '' }">
												<form:option value="">
													<spring:message code="rfteventdescription.costcenter" />
												</form:option>
												<c:forEach items="${costCenter}" var="costCenter">
													<form:option value="${costCenter.id}">${costCenter.costCenter} - ${costCenter.description}</form:option>
												</c:forEach>
											</form:select>
										</div>
									</div>
								</c:if>
								<div class="row">
									<div class="col-md-4 col-sm-6">
										<label class="marg-top-10"> <spring:message code="label.businessUnit" />
										</label>
									</div>
									<div class="col-md-8 dd sky mar_b_10 col-sm-6 ${isIdSettingOn ? 'disabled' : ''} ">
										<div class="form-group autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'BUSINESS_UNIT' ) ? 'disabled' : '') : ''}">
											<form:select path="businessUnit" id="businessUnit" cssClass="form-control chosen-select autoSave" data-validation="required" class="custom-select">
												<form:option value="">
													<spring:message code="pr.select.business.unit" />
												</form:option>
												<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
											</form:select>
										</div>
									</div>
								</div>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'GROUP_CODE' )) : 'true' }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="label.groupCode" />
											</label>
										</div>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6 ">
											<c:if test="${!empty templateFields ? (tf:readonly( templateFields, 'GROUP_CODE' )) : 'false' }">
												<input type="hidden" name="groupCode" value="${event.groupCode.id}" />
											</c:if>
											<form:select path="groupCode" cssClass="form-control chosen-select autoSave" id="idGroupCode" disabled="${tf:readonly( templateFields, 'GROUP_CODE' ) }"
												data-validation="${!empty templateFields ? (tf:required( templateFields, 'GROUP_CODE' ) ? 'required' : '') : '' }" 
												 style="${tf:readonly( templateFields, 'GROUP_CODE' ) ? 'opacity:0' : '' }">
												<form:option value="">
													<spring:message code="rfs.select.group.Code" />
												</form:option>
												<c:forEach items="${groupCodeList}" var="gpCode">
													<form:option value="${gpCode.id}">${gpCode.groupCode} - ${gpCode.description}</form:option>
												</c:forEach>
											</form:select>
										</div>
									</div>
								</c:if>
								
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'BUDGET_AMOUNT' )) : 'true'  }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="eventdescription.budgetamount.label" />
											</label>
										</div>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<spring:message code="event.budget.amount.placeholder" var="budgetamt" />
											<fmt:formatNumber var="budgetAmount" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.budgetAmount}" />
											<form:input path="budgetAmount" type="text" value="${budgetAmount}" class="form-control autoSave" placeholder="${budgetamt}" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'BUDGET_AMOUNT' )) : 'false'}" data-validation="${!empty templateFields ? (tf:required( templateFields, 'BUDGET_AMOUNT' ) ? 'required' : '') : '' } validate_max_13 positive" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
											<strong></strong>
										</div>
									</div>
								</c:if>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'ESTIMATED_BUDGET' )) : 'true' }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="eventdescription.estimatedBudget.label" />
											</label>
										</div>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<fmt:formatNumber var="estimatedBudget" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.estimatedBudget}" />
											<form:input path="estimatedBudget" type="text" value="${estimatedBudget}" class="form-control autoSave" placeholder="${budgetamt}" 
											readonly="${!empty templateFields ? (tf:readonly( templateFields, 'ESTIMATED_BUDGET' )) : 'false'}" 
											data-validation="${!empty templateFields ? (tf:required( templateFields, 'ESTIMATED_BUDGET' ) ? 'required' : '') : '' } 
											validate_max_13 positive" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
										</div>
									</div>
								</c:if>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'HISTORIC_AMOUNT' )) : 'true' }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="eventdescription.historicamount.label" />
											</label>
										</div>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<fmt:formatNumber var="historicaAmount" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.historicaAmount}" />
											<form:input path="historicaAmount" type="text" value="${historicaAmount}" class="form-control autoSave" placeholder="${budgetamt}" 
											readonly="${!empty templateFields ? (tf:readonly( templateFields, 'HISTORIC_AMOUNT' )) : 'false'}" 
											data-validation="${!empty templateFields ? (tf:required( templateFields, 'HISTORIC_AMOUNT' ) ? 'required' : '') : '' } validate_max_13 positive" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
											<strong></strong>
										</div>
									</div>
								</c:if>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PAYMENT_TERM' )) : 'true' }">
									<div class="row marg-bottom-10">
										<div class="col-md-4 col-sm-6">
											<label class="marg-top-10"> <spring:message code="eventdescription.paymentterm.label" />
											</label>
										</div>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<spring:message code="pr.place.paymentTerm" var="paymentterm" />
											<form:textarea path="paymentTerm" placeholder="${paymentterm}" class="form-control textarea-autosize autoSave" maxlength="500" data-validation="${!empty templateFields ? (tf:required( templateFields, 'PAYMENT_TERM' ) ? 'required' : '') : '' } length" data-validation-length="max500" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'PAYMENT_TERM' )) : 'false' }" />
											<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
										</div>
									</div>
								</c:if>
							</div>
						</div>
					</div>
					<c:if test="${event.status == 'SUSPENDED'}">
						<form:hidden path="documentReq" />
						<form:hidden path="meetingReq" />
						<form:hidden path="questionnaires" />
						<form:hidden path="billOfQuantity" />
						<form:hidden path="scheduleOfRate" />

					</c:if>
					<c:if test="${event.status != 'SUSPENDED'}">
						<div class="Invited-Supplier-List import-supplier white-bg marg-top-20">
							<div class="meeting2-heading">
								<h3>
									<spring:message code="eventdescription.eventrquirement.label" />
								</h3>
							</div>
							<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle">
								<div class="col-md-6">
									<div class="row">
										<div class="form-tander1 requisition-summary-box">
											<div class="col-md-4 col-sm-6">
												<label> <spring:message code="eventdescription.document.label" />
												</label>
											</div>
											<div class="col-md-8 col-sm-6">
												<div class="radio_yes-no-main width100">
													<div class="radio_yes-no">
														<div class="radio-info">
															<label class="select-radio"> <form:radiobutton path="documentReq" class="custom-radio " value="0" /> <spring:message code="application.no" />
															</label>
														</div>
													</div>
													<div class="radio_yes-no">
														<div class="radio-info">
															<label class="select-radio"> <form:radiobutton path="documentReq" class="custom-radio " value="1" /> <spring:message code="application.yes" />
															</label>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="form-tander1 requisition-summary-box">
										<div class="row">
											<div class="col-md-4 col-sm-6">
												<label> <spring:message code="eventdescription.meeting.label" />
												</label>
											</div>
											<div class="col-md-8 col-sm-6">
												<div class="radio_yes-no-main width100">
													<div class="radio_yes-no">
														<div class="radio-info">
															<label class="select-radio"> <form:radiobutton path="meetingReq" class="custom-radio " value="0" /> <spring:message code="application.no" />
															</label>
														</div>
													</div>
													<div class="radio_yes-no">
														<div class="radio-info">
															<label class="select-radio"> <form:radiobutton path="meetingReq" class="custom-radio " value="1" /> <spring:message code="application.yes" />
															</label>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
<%--									<c:if test="${eventType == 'RFI'  }">--%>
<%--										<div class="row">--%>
<%--											<div class="col-md-4 col-sm-6">--%>
<%--												<label> <spring:message code="eventdescription.questionnaires.label" />--%>
<%--												</label>--%>
<%--											</div>--%>
<%--											<div class="col-md-4 ">--%>

<%--												<div class="radio_yes-no">--%>
<%--													<div class="radio-info">--%>
<%--														<label class="select-radio"> </label>--%>
<%--													</div>--%>
<%--												</div>--%>
<%--												<div class="radio_yes-no">--%>
<%--													<div class="radio-info">--%>
<%--														<label class="select-radio"> <form:radiobutton path="questionnaires" class="custom-radio" value="1" /> <spring:message code="application.yes" />--%>
<%--														</label>--%>
<%--													</div>--%>
<%--												</div>--%>
<%--											</div>--%>
<%--										</div>--%>
<%--									</c:if>--%>
<%--									<c:if test="${eventType != 'RFI'  }">--%>
										<div class="row">
											<div class="form-tander1 requisition-summary-box">
												<div class="col-md-4 col-sm-6">
													<label> <spring:message code="eventdescription.questionnaires.label" />
													</label>
												</div>
												<div class="col-md-8 col-sm-6">
													<div class="radio_yes-no-main width100">
														<div class="radio_yes-no">
															<div class="radio-info">
																<label class="select-radio"> <form:radiobutton path="questionnaires" class="custom-radio" value="0" /> <spring:message code="application.no" />
																</label>
															</div>
														</div>
														<div class="radio_yes-no">
															<div class="radio-info">
																<label class="select-radio"> <form:radiobutton path="questionnaires" class="custom-radio" value="1" /> <spring:message code="application.yes" />
																</label>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
<%--									</c:if>--%>
									<c:if test="${eventType != 'RFI'  }">
										<div class="form-tander1 requisition-summary-box">
											<div class="row">
												<div class="col-md-4 col-sm-6">
													<label> <spring:message code="eventdescription.billofquantity.label" />
													</label>
												</div>
												<div class="col-md-8 col-sm-6">
													<div class="radio_yes-no-main width100">
														<div class="radio_yes-no billofQuantityControl">
															<div class="radio-info">
																<label class="select-radio"> <form:radiobutton path="billOfQuantity" class="custom-radio " value="0" /> <spring:message code="application.no" />
																</label>
															</div>
														</div>
														<div class="radio_yes-no billofQuantityControl">
															<div class="radio-info">
																<label class="select-radio"> <form:radiobutton path="billOfQuantity" class="custom-radio " value="1" /> <spring:message code="application.yes" />
																</label>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</c:if>
<%--									<c:if test="${eventType != 'RFI'  }">--%>
										<div class="form-tander1 requisition-summary-box">
											<div class="row">
												<div class="col-md-4 col-sm-6">
													<label> <spring:message code="eventdescription.schedule.rate.label" />
													</label>
												</div>
												<div class="col-md-8 col-sm-6">
													<div class="radio_yes-no-main width100">
														<div class="radio_yes-no">
															<div class="radio-info">
																<label class="select-radio"> <form:radiobutton path="scheduleOfRate" class="custom-radio " value="0" /> <spring:message code="application.no" />
																</label>
															</div>
														</div>
														<div class="radio_yes-no">
															<div class="radio-info">
																<label class="select-radio"> <form:radiobutton path="scheduleOfRate" class="custom-radio " value="1" /> <spring:message code="application.yes" />
																</label>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
<%--									</c:if>--%>
								</div>
							</div>
						</div>
					</c:if>
				</form:form>
				<div class="marg-top-20 btns-lower">
					<div class="row">
						<div class="col-md-12 col-xs-12 col-ms-12">
							<form:form class="bordered-row pull-left" id="submitPriviousForm" method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/eventCreationPrevious">
								<form:hidden path="id" />
								<form:button type="submit" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1 previousStep1" value="Previous" name="previous" id="priviousStep">
									<spring:message code="application.previous" />
								</form:button>
							</form:form>
							<spring:message code="application.next" var="next" />
							<input type="button" class="btn btn-info ph_btn marg-left-10 hvr-pop hvr-rectangle-out" value="${next}" name="next" id="nextStep" />
							<c:if test="${event.status eq 'DRAFT'}">
								<spring:message code="application.draft" var="draft" />
								<input type="button" id="nextStepDraft" class="step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop pull-right" value="${draft}" />
							</c:if>
							<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') && (isAdmin or eventPermissions.owner)}">
								<a href="#confirmCancelEvent" role="button" class="btn btn-danger marg-right-10 ph_btn hvr-pop right-header-button" id="idCancelEvent" data-toggle="modal">${event.status eq 'DRAFT' ? 'Cancel Draft' : 'Cancel Event'}</a>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="myModalDeleteRelatedItems" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body radio_name">
				<label> <spring:message code="rfaevent.meeting.sure.delete" /> ?
				</label>
			</div>
			<input type="hidden" id="selectedrelated" name="selectedrelated">
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="idConfirmDeleteRelatedItems">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
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
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
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
							<spring:message code="event.reason.cancellation.placeholder" var="mentionreason" />
							<form:textarea path="cancelReason" id="cancelReason"  class="width-100" placeholder="${mentionreason}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<style>
ul#industryCategoryList {
	list-style: none;
	padding: 0;
	position: absolute;
	z-index: 9;
	background: #fff;
	border-left: 1px solid #ccc;
	width: 100%;
	border-right: 1px solid #ccc;
	max-height: 200px;
	overflow: auto;
}

#industryCategoryList li:first-child {
	border-top: 1px solid #ccc;
}

#industryCategoryList li {
	border-bottom: 1px solid #ccc;
	padding: 10px;
	cursor: pointer;
}

#industryCategoryList li:hover {
	background: #0cb6ff;
	color: #fff;
}

.radio_yes-no label {
	line-height: 2;
}

.radio_yes-no div[id^="uniform-"], .radio_yes-no div[id^="uniform-"] span,
	.radio_yes-no div[id^="uniform-"] input {
	width: 20px;
	height: 20px;
}

.radio_yes-no div[id^="uniform-"] span.checked i {
	font-size: 9px !important;
	height: 18px;
	line-height: 18px;
}

.disabled {
	cursor: not-allowed;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript">


$('#businessUnit').on('change',function() {
	/* <c:if test="${event.status =='DRAFT'}">
		$("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraftDesc'), type: 'post'})
	</c:if> */
	var value=this.value;
		
	
		$.ajax({
			type : "POST",
			url : getContextPath() + "/buyer/checkBusinessUnitSetting/${eventType}",
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


var addBillOfQuantity = Boolean($(".addBillOfQuantity").val() == 'true');
if(addBillOfQuantity){
	$(".billofQuantityControl").removeClass("disabled");
}else{
	$(".billofQuantityControl").addClass("disabled", true);
}


	/* Datepicker bootstrap */
	<c:if test="${event.status == 'SUSPENDED'}">
	$(window).bind('load', function() {
		var allowedFields = '#nextStep,#priviousStep,#bubble,#eventDescription,#nextStepDraft,#idCancelEvent';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#nextStep,#priviousStep,#bubble';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>

	$(function() {
		"use strict";
		$('.bootstrap-datepicker').bsdatepicker({
			format : 'dd/mm/yyyy'
		});

		/* $(document).on("click", ".step_btn_1", function() {
			$("#demo-form").submit();
		}); */
	/* 	 $('#nextStep').click(function(e) {
			e.preventDefault();
			if ($('#demo-form1').isValid()) {
				$('#demo-form1').submit();
			}
		}); */  

		$('#nextStep').click(function() {
			if ($('#demo-form1').isValid()) {
				$('#demo-form1').attr('action', getBuyerContextPath('eventDescription'));
				$('#demo-form1').submit();
			}
		});

		$('#nextStepDraft').click(function() {
			
				$('#demo-form1').attr('action', getBuyerContextPath('saveAsDraftDescription'));
				$('#demo-form1').submit();
			
		});

		$('#rfxCancelEvent').click(function() {
			var cancelRequest = $('#cancelReason').val();
			if (cancelRequest != '') {
				$(this).addClass('disabled');
			}
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		/* ignore: allInputs, */
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
	
	
</script>
<script>
$( ".autoSave" ).change(function() {
	/* <c:if test="${event.status =='DRAFT'}">
		$("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraftDesc'), type: 'post'})
	</c:if> */
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
	
	console.log(">>>>>>>>>>>>>>>>>>>>> ");
	<c:if test="${isEnableBUAndGPCCorr}">
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var url = getContextPath() +  "/buyer/getAssingedGroupCodeList";
		$.ajax({
			type : "GET",
			url : url,
			data : {
				buId : value,
			},
			beforeSend : function(xhr) {
				var html='';
				$('#idGroupCode').html(html);
			    $("#idGroupCode").trigger("chosen:updated");
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
				html += '<option value="" selected="selected">'+ 'Select Group Code' +'</option>';
				$.each(data, function(i, item) {
					if(item.description.length > 0){
						html += '<option value="' + item.id + '">' + item.groupCode + ' - ' + item.description + '</option>';
					}else{
						html += '<option value="' + item.id + '">' + item.groupCode + '</option>';
					}
				});
				$('#idGroupCode').append(html);
				$("#idGroupCode").trigger("chosen:updated");
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
</script>

<script type="text/javascript">
$(document).ready(
		function() {
			
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
			
		});
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/ajaxFormPlugin.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<!-- daterange picker js and css start -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Theme layout -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/js/view/createrftevent.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
