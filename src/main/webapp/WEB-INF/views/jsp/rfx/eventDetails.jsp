<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/multiselect/multi-select.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxCreateDetails" code="application.rfx.create.details" />
<spring:message code="unmask.search.user" var="unMasUserPlace" />
<jsp:useBean id="now" class="java.util.Date" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreateDetails}] });
});
</script>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<li class="active">${eventType.value}</li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap tender-request-heading">
							<spring:message code="application.create" />
							${eventType.value}
						</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />
							: ${event.status}
						</h2>
					</div>
					<jsp:include page="eventHeader.jsp"></jsp:include>
					<jsp:include page="eventDetailsTour.jsp"></jsp:include>
					<div class="tab-pane active error-gap-div">
						<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/storeEventDetails">
							<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="upload_download_wrapper clearfix event_info">
								<h4>
									<spring:message code="eventdetails.event.information.label" />
								</h4>
								<div class="row">
									<div class="form-tander1 pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="eventdetails.event.id" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<p>${event.eventId}</p>
										</div>
									</div>
									<div class="form-tander1 pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="eventdetails.event.type" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<p>${eventType.value}</p>
										</div>
									</div>
									<div class="form-tander1 line-set pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="eventdetails.event.owner" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<p>${event.eventOwner.name }
												<br> ${event.eventOwner.communicationEmail} <br>
												<spring:message code="eventdetails.event.telephone" />${event.eventOwner.phoneNumber}
											</p>
										</div>
									</div>
								</div>
							</div>

							<jsp:include page="eventSettingsDetails.jsp"></jsp:include>


							<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form">
								<h4>
									<spring:message code="eventdetails.event.details" />
								</h4>
								<div class="form_field">
									<div>
										<form:hidden path="id" id="id" />
										<form:hidden path="tenantId" />
										<form:hidden path="eventId" />
										<form:hidden path="eventOwner.name" />
										<form:hidden path="eventOwner.communicationEmail" />
										<form:hidden path="eventOwner.phoneNumber" />
										<form:hidden path="template.id" />
										<form:hidden path="eventDetailCompleted" />
										<form:hidden path="documentCompleted" />
										<form:hidden path="supplierCompleted" />
										<form:hidden path="meetingCompleted" />
										<form:hidden path="cqCompleted" />
										<form:hidden path="bqCompleted" />
										<form:hidden path="envelopCompleted" />
										<form:hidden path="summaryCompleted" />
										<form:hidden path="status" value="${event.status}" />
										<form:hidden path="rfxEnvelopeReadOnly" />
										<c:if test="${eventType ne 'RFA' }">
											<form:hidden path="documentReq" />
											<form:hidden path="meetingReq" />
											<form:hidden path="questionnaires" />
											<form:hidden path="billOfQuantity" />
											<form:hidden path="scheduleOfRate" />
										</c:if>
										<c:if test="${eventType == 'RFA' }">
											<form:hidden path="auctionType" />
											<%-- <form:hidden path="eventVisibility" />  --%>
										</c:if>
										<input type="hidden" name="templateId" value="${templateId}"> <label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.referencenumber" />
										</label>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<spring:message code="eventdetails.event.place.referencenumber" var="reference" />
											<form:input path="referanceNumber" type="text" placeholder="${reference}" data-validation="required alphanumeric length" data-validation-allowing="/ -_" data-validation-length="max64" class="form-control autoSave" />
										</div>
									</div>
								</div>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'EVENT_NAME' )) : 'true' }">
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.name" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<spring:message code="eventdetails.event.place.eventname" var="eventname" />
												<form:textarea path="eventName" placeholder="${eventname}" data-validation="required" class="form-control autoSave" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'EVENT_NAME' )) : 'false'}" maxlength="200" />
												<span class="sky-blue"><spring:message code="eventdetails.valid.max3.characters" /></span>
											</div>
										</div>
									</div>
								</c:if>
								<div class="form_field">
									<div>
										<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.visibility" />
										</label>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<form:select path="eventVisibility" data-validation="required" cssClass="form-control chosen-select disablesearch visibilitySearch" id="idEventVisibility">
												<c:if test="${empty rfxTemplate}">
													<form:options items="${eventVisibility}" />
												</c:if>
												<c:if test="${!empty rfxTemplate}">
													<c:if test="${rfxTemplate.privateEvent}">
														<form:option value="PRIVATE" />
													</c:if>
													<c:if test="${rfxTemplate.partialEvent}">
														<form:option value="PARTIAL" />
													</c:if>
													<c:if test="${rfxTemplate.publicEvent}">
														<form:option value="PUBLIC" />
													</c:if>
												</c:if>
												<c:if test="${!empty rfxTemplate && empty rfxTemplate.privateEvent && empty rfxTemplate.partialEvent && empty rfxTemplate.publicEvent}">
													<form:options items="${eventVisibility}" />
												</c:if>
												<c:if test="${!empty rfxTemplate && !rfxTemplate.privateEvent && !rfxTemplate.partialEvent && !rfxTemplate.publicEvent}">
													<form:options items="${eventVisibility}" />
												</c:if>
											</form:select>
										</div>
									</div>
								</div>
								<!--Set Conditinal Not For RFA  -->

								<!--reomve display none when public event CR done please refer PH 161  -->
								<c:if test="${eventType != 'RFA' }">
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.startend.date" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-5">
												<div class="input-prepend input-group">
													<form:input readonly="true" path="eventVisibilityDates" data-date-start-date="0d" id="daterangepicker-time" data-startdate="" class="form-control for-clander-view for-clander-view" type="text" data-validation="required" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"></form:input>
												</div>
											</div>
										</div>
									</div>
									<!--reminder for event  -->
									<div class="form_field">
										<div class="col-sm-4 col-md-3 col-xs-6 control-label"></div>
										<div class="col-sm-5 col-md-5 col-xs-5">
											<div class="ph_table_border">
												<div class="reminderList marginDisable">
													<c:forEach items="${reminderList}" var="reminder">
														<div class="row reminderId" id="${reminder.id}">
															<input type="hidden" name="reminderDate" value="${ reminder.reminderDate}">
															<fmt:formatDate var="reminderDateTime" value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
															<input type="hidden" class="reminderDateDel" value="${reminderDateTime}">
															<div class="col-md-10">
																<p>
																	<b>Reminder before <c:if test="${not empty reminder.startReminder &&  reminder.startReminder}">
    																	Start Date:
																	</c:if> <c:if test="${not empty reminder.startReminder &&  !reminder.startReminder}">
    																	End Date:
																	</c:if>
																	</b>${reminderDateTime}</p>
															</div>
															<div class="col-md-2">
																<a href="" class="deleteReminder ${event.status == 'SUSPENDED' &&
																reminder.startReminder != null && reminder.startReminder != false ? 'disabled' : ''}" id="deleleteReminder" reminderId=""> <i class="fa fa-times-circle"></i>
																</a>
															</div>
														</div>
													</c:forEach>
													<c:if test="${empty reminderList}">
														<div class="row" id="">
															<div class="col-md-12">
																<p>
																	<spring:message code="label.add.reminder" />
																</p>
															</div>
														</div>
													</c:if>
												</div>
											</div>
										</div>
										<div class="col-md-1 col-xs-1">
											<div class="ring plus_btn_wrap">
												<a class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out" title='<spring:message code="add.reminder.placeholder" />' data-placement="top" id="idAddReminder" onclick="toggleRadios()" data-toggle="tooltip"> <img src="${pageContext.request.contextPath}/resources/images/ring_cion.png">
												</a>
											</div>
										</div>
									</div>

									<!-- PH-334 -->
									<c:if test="${eventType == 'RFI' }">
										<div id="rfiDiv">
											<div class="form_field">
												<div class="col-sm-4 col-md-3 col-xs-6 control-label">
													<label><spring:message code="eventdetails.expected.tender.time" /> </label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-5">
													<div class="input-prepend input-group">
														<%-- 	<jsp:useBean id="now" class="java.util.Date" /> --%>
														<form:input readonly="true" path="expectedTenderDateTimeRange" data-date-start-date="0d" id="" data-startdate="" class="daterangepickerTime form-control for-clander-view for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"></form:input>
													</div>
												</div>
											</div>

											<div class="form_field">
												<div class="col-sm-4 col-md-3 col-xs-6 control-label">
													<label class="marg-top-10"><spring:message code="eventdetails.fee.startend.time" /></label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-5">
													<div class="input-prepend input-group">
														<form:input readonly="true" path="feeDateTimeRange" data-date-start-date="0d" id="" data-startdate="" class="daterangepickerTime form-control for-clander-view for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"></form:input>
													</div>
												</div>
											</div>
										</div>
									</c:if>
									<!--reminder for event  -->
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.delivery.address" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6" id="idAddressDiv">
												<div class="input-prepend">
													<label class="physicalCriterion pull-left ${event.deliveryAddress != null ? 'flagvisibility' : ''}"> <input type="checkbox" id="inlineCheckbox114" value="" class="custom-checkbox" ${event.deliveryAddress != null ? "checked='true'" : ""} />
													</label> <span class="pull-left ${event.deliveryAddress != null ? 'active' : ''}"> <span class="phisicalArressBlock1 pull-left width100  ${event.deliveryAddress != null ? 'flagvisibility' : ''}"> <spring:message code="eventdetails.event.subbmission.criterion" />
													</span> <span class="phisicalArressBlock pull-left marg-top-10 width100   ${event.deliveryAddress != null ? 'buyerAddressRadios active' : ''}" style="padding: 10px 0;"> <c:if test="${not empty buyerAddress}">
																<div class="">
																	<div class="col-md-10">
																		<h5>${buyerAddress.title}</h5>
																		<span class='desc width100'>${buyerAddress.line1}, ${buyerAddress.line2}, ${buyerAddress.city}, ${buyerAddress.zip}, ${buyerAddress.state.stateName}, ${buyerAddress.state.country.countryName}</span>
																	</div>
																	<div class="col-md-2 align-right">
																		<a class="pull-right ${event.deliveryAddress != null ? '' : 'flagvisibility'}" title="" data-placement="top" id="deleteDeliveryAddress" data-toggle="tooltip" data-original-title="Delete Delivery Address" style="font-size: 18px; line-height: 1; padding: 0; color: #7f7f7f;"> <i class="fa fa-times-circle"></i>
																		</a>
																	</div>
																</div>
															</c:if>
													</span>
												</div>
												<div id="sub-credit" class="invite-supplier delivery-address collapse margin-top-10">
													<div class="role-upper ">
														<div class="col-sm-12 col-md-12 col-xs-12 float-left">
															<input type="text" placeholder='<spring:message code="event.search.address.placeholder" />' class="form-control delivery_add autoSave">
														</div>
														<!-- <div class="plus_btn_wrap float-right">
														<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="Add More" class="btn btn-black disabled btn-tooltip delivery_add_btn"> <i aria-hidden="true" class="glyph-icon icon-plus"></i>
														</a>
													</div> -->
													</div>
													<div class="chk_scroll_box">
														<div class="scroll_box_inner">
															<div class="role-main">
																<div class="role-bottom small-radio-btn">
																	<ul class="role-bottom-ul">
																		<c:forEach var="address" items="${addressList}">
																			<li>
																				<div class="radio-info">
																					<label> <form:radiobutton path="deliveryAddress" value="${address.id}" class="custom-radio" />
																					</label>
																				</div>
																				<div class="del-add">
																					<h5>${address.title}</h5>
																					<span class='desc width100'>${address.line1}, ${address.line2}, ${address.city}, ${address.zip}, ${address.state.stateName}, ${address.country}</span>
																				</div>
																			</li>
																		</c:forEach>
																	</ul>
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventsummary.eventdetail.deliverydate" />
											</label>
											<div class="col-sm-3 col-md-3 col-xs-3">
												<div class="input-prepend input-group">
													<spring:message code="dateformat.placeholder" var="dateformat" />
													<spring:message code="tooltip.delivery.date" var="deliverydatetool" />
													<form:input path="deliveryDate" autocomplete="off" readonly="readonly" data-placement="top" data-toggle="tooltip" data-original-title="${deliverydatetool}" class="nvclick form-control for-clander-view" data-validation="date" data-validation-optional="true" data-fv-date-min="15/10/2016" data-validation-format="dd/mm/yyyy"
														placeholder="${dateformat}" />
												</div>
											</div>
										</div>
									</div>
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.publish.date" />
											</label>
											<div class="col-sm-3 col-md-3 col-xs-3">
												<div class="input-prepend input-group ${event.status == 'SUSPENDED' ? 'disabled' : '' }">
													<spring:message code="tooltip.publish.date.before" var="publishdate" />
													<form:input path="eventPublishDate" readonly="readonly" data-placement="top" data-toggle="tooltip" data-original-title="${publishdate}" autocomplete="off" class="nvclick form-control for-clander-view autoSave" data-validation="required date" data-fv-date-min="15/10/2016" data-validation-format="dd/mm/yyyy" placeholder="${dateformat}" />
												</div>
											</div>
											<div class="col-md-2 col-sm-3 col-xs-3 col-lg-2">
												<div class="bootstrap-timepicker dropdown ${event.status == 'SUSPENDED' ? 'disabled' : '' }">
													<form:input path="eventPublishTime" autocomplete="off" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control autoSave" />
												</div>
											</div>
										</div>
									</div>
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.validity.days" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="input-prepend input-group">
													<spring:message code="eventdetails.event.place.validitydays" var="validitydays" />
													<form:input path="submissionValidityDays" id="idValidityDays" placeholder="${validitydays}" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'SUB_VALIDITY_DAYS' )) : 'false'}" class="form-control autoSave" type="text" data-validation="required length number" data-validation-length="1-3"
														data-validation-error-msg-length="Days value must be between 1-3." />
												</div>
											</div>
										</div>
									</div>
								</c:if>
								<c:if test="${eventType == 'RFA' }">
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventsummary.eventdetail.deliverydate" />
											</label>
											<div class="col-sm-3 col-md-3 col-xs-3">
												<div class="input-prepend input-group">
													<form:input path="deliveryDate" readonly="readonly" autocomplete="off" data-placement="top" data-toggle="tooltip" data-original-title="${deliverydatetool}" class="nvclick form-control for-clander-view" data-validation="date" data-fv-date-min="15/10/2016" data-validation-optional="true" data-validation-format="dd/mm/yyyy"
														placeholder="${dateformat}" />
												</div>
											</div>
										</div>
									</div>
								</c:if>

								<div class="form_field">
									<div id="idIndustryCategoryDiv">
										<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.category" />
										</label>
										<div class="col-sm-5 col-md-5 col-xs-6 ${ (!empty templateFields ? (tf:readonly( templateFields, 'EVENT_CATEGORY' ) ? 'disabled': '') : '')}">
											<div class="d-flex">
												<div>
													<spring:message code="rfi.createrfi.available.items" />
												</div>
												<div class="selected-item">
													<spring:message code="rfi.createrfi.selected.items" />
												</div>
											</div>
											<div class="input-prepend input-group">
												<input type="hidden" id="	Val" value='<c:out value="${indusCatList}" escapeXml="true" />'> <input type="hidden" name="industryCateg[]" id="industryCatArr"> <select id='callbacks' multiple class="searchable" class="autoSave">
													<c:forEach items="${industryCat}" var="category">
														<option value="${category.id}">${ category.code}-${  category.name}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>

								<div class="form_field">
									<c:if test="${(!empty templateFields ? (tf:visibility( templateFields, 'MINIMUM_SUPPLIER_RATING' )) : 'true' )}">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="sourcing.minimumSupplierRating" /> </label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="input-prepend input-group ${ (!empty templateFields ? (tf:readonly( templateFields, 'MINIMUM_SUPPLIER_RATING' ) ? 'disabled': '') : '')}">
													<form:input path="minimumSupplierRating" id="minimumSupplierRating" placeholder="Minimum Supplier Rating/Grade" class="form-control" type="text" 
														data-validation-optional="${!empty templateFields ? (tf:required( templateFields, 'MAXIMUM_SUPPLIER_RATING' ) ? 'false' : 'true') : 'true'}" 
														data-validation="${!empty templateFields ? (tf:required( templateFields, 'MINIMUM_SUPPLIER_RATING' ) ? 'required' : '') : ''} custom number validateMin"
														data-validation-allowing="range[0.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$" data-validation-error-msg-custom="Input value must be numeric within range from 0 to 9999.99" data-validation-error-msg-number="Input value must be numeric within range from 0 to 9999.99" />
												</div>
											</div>
										</div>
									</c:if>
								</div>


								<div class="form_field">
									<c:if test="${(!empty templateFields ? (tf:visibility( templateFields, 'MAXIMUM_SUPPLIER_RATING' )) : 'true' )}">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="sourcing.maximumSupplierRating" /> </label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div class="input-prepend input-group ${ (!empty templateFields ? (tf:readonly( templateFields, 'MAXIMUM_SUPPLIER_RATING' ) ? 'disabled': '') : '')}">
													<form:input path="maximumSupplierRating" id="maximumSupplierRating" placeholder="Maximum Supplier Rating/Grade" 
														class="error form-control" type="text" 
														data-validation-optional="${!empty templateFields ? (tf:required( templateFields, 'MAXIMUM_SUPPLIER_RATING' ) ? 'false' : 'true') : 'true'}" 
														data-validation="${!empty templateFields ? (tf:required( templateFields, 'MAXIMUM_SUPPLIER_RATING' ) ? 'required' : '') : ''} custom number validateMax"
														data-validation-allowing="range[1.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$" data-validation-error-msg-custom="Input value must be numeric within range from 1 to 9999.99" data-validation-error-msg-number="Input value must be numeric within range from 1 to 9999.99" />
												</div>
											</div>
										</div>
									</c:if>
								</div>


								<div class="form_field">
									<c:if test="${(!empty templateFields ? (tf:visibility( templateFields, 'PARTICIPATION_FEE_CURRENCY' )) : 'true' ) and (!empty templateFields ? (tf:visibility( templateFields, 'PARTICIPATION_FEES' )) : 'true' )}">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.paticipation.currency" />
											</label>
											<div class="col-sm-3 col-md-2 col-xs-3">
												<div class="form-group ${!empty templateFields ? (tf:readonly( templateFields, 'PARTICIPATION_FEE_CURRENCY' ) ? 'disabled' : '') : ''} ">

													<form:select path="participationFeeCurrency" id="participationFeeCurrency" data-validation="${!empty templateFields ? (tf:required( templateFields, 'PARTICIPATION_FEE_CURRENCY' ) ? 'required' : '') : ''}" cssClass="form-control chosen-select autoSave">
														<form:option value="">
															<spring:message code="currency.select" />
														</form:option>
														<form:options items="${currency}" itemValue="id" />
													</form:select>
												</div>
											</div>
										</div>

										<div class="col-sm-3 col-md-3 col-xs-3">
											<spring:message code="eventdetails.event.place.participationfee" var="participationfee" />
											<fmt:formatNumber var="participationFees" type="number" minFractionDigits="2" maxFractionDigits="2" value="${event.participationFees}" />
											<form:input path="participationFees" id="participationFees" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" autocomplete="off" value="${participationFees}" data-validation=" ${!empty templateFields ? (tf:required( templateFields, 'PARTICIPATION_FEES' ) ? 'required' : '') : ''} validate_part_dept positive" class="form-control autoSave"
												placeholder="${participationfee}" data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'PARTICIPATION_FEES' )) : 'false'}" maxLength="10" />
										</div>

									</c:if>
								</div>

								<div class="form_field">
									<c:if test="${(!empty templateFields ? (tf:visibility( templateFields, 'DEPOSIT_CURRENCY' )) : 'true') and (!empty templateFields ? (tf:visibility( templateFields, 'DEPOSIT' )) : 'true' ) }">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.deposite.currency" />
											</label>
											<div class="col-sm-3 col-md-2 col-xs-3">
												<div class="form-group ${!empty templateFields ? (tf:readonly( templateFields, 'DEPOSIT_CURRENCY' ) ? 'disabled' : '') : ''} ">

													<form:select path="depositCurrency" id="depositCurrency" cssClass="form-control chosen-select autoSave" data-validation="${!empty templateFields ? (tf:required( templateFields, 'DEPOSIT_CURRENCY' ) ? 'required' : '') : ''}">
														<form:option value="">
															<spring:message code="currency.select" />
														</form:option>
														<form:options items="${currency}" itemValue="id" />
													</form:select>
												</div>
											</div>
										</div>
										<div class="col-sm-3 col-md-3 col-xs-3">
											<spring:message code="eventdetails.event.place.deposit" var="depositFees" />
											<fmt:formatNumber var="deposit" type="number" minFractionDigits="2" maxFractionDigits="2" value="${event.deposit}" />
											<form:input path="deposit" autocomplete="off" class="form-control autoSave" placeholder="${depositFees}" value="${deposit}" data-validation="${!empty templateFields ? (tf:required( templateFields, 'DEPOSIT' ) ? 'required' : '') : ''} validate_part_dept positive " data-sanitize="numberFormat" data-sanitize-number-format="0,0.00"
												data-validation-regexp="^[\d,]{1,10}(\.\d{1,2})?$" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'DEPOSIT' )) : 'false'}" maxLength="10" />
										</div>

									</c:if>
								</div>




								<div class="btn-next">
									<form:button type="submit" id="submitEventDetails" style="display:none;" class="step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out" value="Next" />
								</div>
							</div>
							<c:if test="${eventType == 'RFA' }">
								<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form event_info">
									<h4>
										<spring:message code="eventdescription.name" />
									</h4>
									<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle">
										<div class="col-md-8">
											<p>
												<spring:message code="eventdescription.description.text" />
											</p>
										</div>
										<div class="form_field">
											<div class="marg-top-15">
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdescription.description.label" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<form:textarea path="eventDescription" class="form-control autoSave" data-validation="length" data-validation-length="max2000" />
													<span class="sky-blue"><spring:message code="createrfi.event.description.max.chars" /></span>
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${eventType == 'RFA' }">
								<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form event_info">
									<h4>
										<spring:message code="internalremarks.name" />
									</h4>
									<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle">
										<div class="form_field">
											<div>
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="internal.remarks.label" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<form:textarea path="internalRemarks" class="form-control autoSave" maxlength="2000" data-validation="length" data-validation-length="max2000" />
													<span class="sky-blue"><spring:message code="internal.remark.error" /></span>
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:if>
							<div class="upload_download_wrapper clearfix marg-top-10 event_info in">
								<h4>
									<spring:message code="eventdetails.event.contact.details" />
								</h4>
								<div class="form_field">
									<div>
										<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.correspondence.address" />
										</label>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<div>
												<form:hidden path="eventOwner.buyer.line1" />
												<form:hidden path="eventOwner.buyer.line2" />
												<form:hidden path="eventOwner.buyer.city" />
												<form:hidden path="eventOwner.buyer.state.stateName" />
												<form:hidden path="eventOwner.buyer.state.country.countryName" />
												<div class="input_wrapper buyerAddressRadios active">${event.eventOwner.buyer.line1}
													<br>${event.eventOwner.buyer.line2} <br>${event.eventOwner.buyer.city} ${event.eventOwner.buyer.state.stateName} <br>${event.eventOwner.buyer.state.country.countryName}
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="event_form">
									<div class="col-md-12">
										<!-- <table class="contactPersons ph_table display table table-bordered"> -->
										<table class="contactPersons display table table-bordered">
											<thead>
												<tr>
													<th class="text-left"><spring:message code="application.action" /></th>
													<th class="text-left"><spring:message code="eventdetails.event.title" /></th>
													<th class="text-left"><spring:message code="application.name" /></th>
													<th class="text-left"><spring:message code="application.designation" /></th>
													<th class="text-left"><spring:message code="application.contact" /></th>
													<th class="text-left"><spring:message code="application.mobile" /></th>
													<th class="text-left"><spring:message code="application.emailaddress" /></th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${eventContactsList}" var="contact">
													<tr>
														<td class="text-left" contact-id="${contact.id}"><a class="editContact" data-placement="top" title=<spring:message code="tooltip.edit1" /> href=""> <img src="${pageContext.request.contextPath}/resources/images/edit1.png">
														</a> <a class="deleteContact" data-placement="top" title=<spring:message code="tooltip.delete" /> href=""> <img src="${pageContext.request.contextPath}/resources/images/delete1.png">
														</a></td>
														<td class="text-left">${contact.title}</td>
														<td class="text-left">${contact.contactName}</td>
														<td class="text-left">${contact.designation}</td>
														<td class="text-left">${contact.contactNumber}</td>
														<td class="text-left">${contact.mobileNumber != null ? contact.mobileNumber : ""}</td>
														<td class="text-left">${contact.comunicationEmail}</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
										<div class="col-md-12 align-left">
											<div class="row">
												<a href="javascript:void(0);" title='<spring:message code="tooltip.add.contacts" />' class="btn btn-info btn-tooltip hvr-pop hvr-rectangle-out addContactPersonPop"> <spring:message code="eventdetails.event.add.contactperson" />
												</a>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="upload_download_wrapper clearfix marg-top-10 event_info w-42rm">
								<h4>
									<spring:message code="eventsummary.team.members.title" />
								</h4>
								<jsp:include page="eventTeamMembers.jsp"></jsp:include>
							</div>
							<!-- eventApprovals -->
							<c:if test="${!empty rfxTemplate ? (tf:rfxApprovalVisibility(rfxTemplate)) : 'true'}">
								<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab  in">
									<h4>
										<spring:message code="rfi.createrfi.approvalroute.label.2" />
									</h4>
									<div id="apprTab" data-aproval="${(tf:rfxApprovalRequired(rfxTemplate)) }" class="pad_all_15 collapse in float-left width-100 position-relative in">
										<jsp:include page="eventApproval.jsp"></jsp:include>
										<div class="form-group">
											<div class="row">
												<ol>
													<li><spring:message code="rfi.createrfi.approvalroute.add.contact" /></li>
													<li><spring:message code="rfi.createrfi.approvalroute.take.note" /></li>
													<li><spring:message code="rfi.createrfi.approvalroute.flagged.approved" /></li>
													<li><spring:message code="reminder.approvalroute.until.action" /></li>
												</ol>
											</div>
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${eventType == 'RFA' }">
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'PROCUREMENT_METHOD' )) ||  (tf:visibility( templateFields, 'PROCUREMENT_CATEGORY' )) : 'true' }">
									<div class="upload_download_wrapper collapseable clearfix mar-t20 event_info marg-bottom-10 marg-top-10 in">
										<h4>
											<spring:message code="eventdescription.procuement.label" />
										</h4>
										<div id="procurementInfo" class="import-supplier-inner-first-new collapse pad_all_15 global-list in">
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
															<form:select path="procurementMethod" cssClass="form-control chosen-select autoSave" id="idProcurementMethod" data-validation="${!empty templateFields ? (tf:required( templateFields, 'PROCUREMENT_METHOD' ) ? 'required' : '') : ''}" disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'PROCUREMENT_METHOD' )? 'true' : 'false') : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }"
																style="${!empty templateFields ? (tf:readonly( templateFields, 'PROCUREMENT_METHOD' )? 'opacity:0' : '') : ''  }">
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
															<form:select path="procurementCategories" cssClass="form-control chosen-select autoSave" id="idProcurementCategory" data-validation="${!empty templateFields ? (tf:required( templateFields, 'PROCUREMENT_CATEGORY' ) ? 'required' : '') : ''}" disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'PROCUREMENT_CATEGORY' )? 'true' : 'false') : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }"
																style="${!empty templateFields ? (tf:readonly( templateFields, 'PROCUREMENT_CATEGORY' )? 'opacity:0' : '') : ''  }">
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
							</c:if>
							
							<c:if test="${(!empty rfxTemplate ? rfxTemplate.visibleSuspendApproval : 'true')}">
								<div class="upload_download_wrapper clearfix marg-top-10 event_info w-42rm">
									<h4>
										<spring:message code="rfi.createrfi.approvalroute.suspend" />
									</h4>
									<div id="apprTab" class="pad_all_15 collapse in float-left width-100 position-relative in">
										<jsp:include page="eventSuspendApproval.jsp"></jsp:include>
									</div>
								</div>
							</c:if>
							
							<!-- Only For Rfa on condition  -->
							<!-- Rfa finance Start -->
								<jsp:include page="rfaEventDetails.jsp"></jsp:include>
								<!-- RFA SECTION OVER -->
						</form:form>
					</div>
					<div class="btn-next">
						<c:if test="${!buyerReadOnlyAdmin}">
							<input type="button" id="submitStep1EventDetail" class="top-marginAdminList step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out submitStep1" value=<spring:message code="application.next" /> />
						</c:if>
						<c:if test="${event.status eq 'DRAFT'}">
							<spring:message code="application.draft" var="draft" />
							<input type="button" id="submitStep1EventDetailDraft" class="top-marginAdminList step_btn_1 btn btn-black skipvalidation hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right" value="${draft}" />
						</c:if>
						<spring:message code="event.cancel.draft" var="cancelDraftLabel" />
						<spring:message code="cancel.event.button" var="cancelEventLabel" />
						<c:if test="${(event.status eq 'DRAFT' or event.status eq 'SUSPENDED') && ( eventPermissions.owner)}">
							<a href="#confirmCancelEvent" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" id="idCancelEvent" data-toggle="modal">${event.status eq 'DRAFT' ? cancelDraftLabel : cancelEventLabel}</a>
						</c:if>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<div class="modal fade" id="confirmDeleteReminder" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="rfaevent.confirm.delete.reminderlist" />
				</label> <input type="hidden" id="deleteIdReminder" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelReminder">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="addReminder" role="dialog" data-backdrop="static">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="label.add.reminder" />
				</h3>
				<button class="close for-absulate" data-dismiss="modal" id="crossRem" type="button" style="border: none;">
					<i class="fa fa-times-circle"></i>
				</button>
			</div>
			<form id="addReminder1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row d-flex-line mb-15">
						<input name="reminderId" id="reminderId" value="${reminderId}" type="hidden"> <label class="remind-lbl"> <spring:message code="label.remind.me" /> <c:if test="${eventType != 'RFA' }"> Before the</c:if>
						</label>
						<c:if test="${eventType != 'RFA' }">
							<input type="radio" class="time-lbl" name="reminderNotifyType" id="startRadio" value="Start" checked="checked"> Start Date
						<input type="radio" class="time-lbl ml-15" name="reminderNotifyType" id="endRadio" value="End"> End Date
					</c:if>
					</div>
					<div class="row">
						<div class="col-md-6">
							<input name="remindMe" id="remindMe" data-validation="required, number" data-validation-allowing="range[1;100]" type="text" class="form-control w-100" />
						</div>
						<div class="col-md-6 align-left">
							<select data-validation="required" class="custom-select remindMeTime" name="remindMeTime" id="">
								<c:forEach items="${intervalType}" var="interval">
									<option value="${interval}">${interval}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<c:if test="${eventType == 'RFA' }">
						<div class="before-time-msg" id="newMsg">
							<span class="timestart"><spring:message code="application.before.start.date" /></span>
						</div>
					</c:if>

				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="button" id="reminderButton" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" data-dismiss="modal">
						<spring:message code="application.save" />
					</button>
					<button type="button" id="reminderCan" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!--pop up  -->
<div class="modal fade" id="confirmDeleteContact" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="eventdetails.event.delete.contact" />
				</label> <input type="hidden" id="deleteIdContact" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelContact">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<spring:message code="eventdetails.event.add.contactperson" var="addeditTitle" />
<div class="flagvisibility dialogBox" id="addEditContactPopup" title="${addeditTitle}">
	<div class="float-left width100 pad_all_15 white-bg">
		<form:form class="bordered-row" id="demo-form-contact" data-parsley-validate="" method="post" modelAttribute="eventContact" action="${pageContext.request.contextPath}/buyer/${eventType}/createRftEventContact">
			<form:hidden path="id" />
			<form:hidden path="eventId" />
			<input type="hidden" id="contactId" name="contactId" />
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="eventdetails.event.title" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="eventdetails.event.contact.place.title" var="title" />
						<form:input type="text" path="title" placeholder="${title}" maxlength="128" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="eventdetails.event.contact.name" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="eventdetails.event.contact.place.name" var="name" />
						<form:input type="text" path="contactName" placeholder="${name}" data-validation="required custom" maxlength="128" data-validation-regexp="^[a-zA-Z0-9-.' ]*$" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.designation" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="eventdetails.event.contact.place.designation" var="designation" />
						<form:input type="text" path="designation" placeholder="${designation}" maxlength="128" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.contact.number" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="eventdetails.event.contact.place.contactno" var="contactno" />
						<form:input type="text" path="contactNumber" id="idContactNumber" placeholder="${contactno}" data-validation="length number" data-validation-optional="true" data-validation-ignore="+ " data-validation-length="6-14" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.mobile.number" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="eventdetails.event.contact.place.mobile" var="mobile" />
						<form:input type="text" path="mobileNumber" id="idMobileNumber" data-validation="length number" data-validation-optional="true" data-validation-ignore="+ " data-validation-length="0-14" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.fax.number" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="eventdetails.event.contact.place.fax" var="fax" />
						<form:input type="text" path="faxNumber" id="idFaxNumber" data-validation="length number" data-validation-optional="true" data-validation-ignore="+ " data-validation-length="0-14" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.emailaddress" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="eventdetails.event.contact.place.email" var="email" />
						<form:input type="text" placeholder="${email}" path="comunicationEmail" data-validation-optional="true" data-validation="length email" data-validation-length="0-128" class="form-control mar-b10 feature_box" />
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title='<spring:message code="event.update.contact.person"/>' class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out addContactPerson"> <spring:message code="eventdetails.event.add.contactperson" />
						</a> <a href="#" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1" onclick="javascript:$('#demo-form-contact').get(0).reset();"> <spring:message code="application.cancel" />
						</a>
					</div>
				</div>
			</div>
		</form:form>
	</div>
	<!-- </div>
	</div> -->
</div>
<div class="modal fade" id="myModalDeleteRelatedItems1" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <%-- <spring:message code="application.delete.popup" /> --%> <spring:message code="rfaevent.confirm.delete.suppliers.meetings" />
				</label> <input type="hidden" id="selectedrelated" name="selectedrelated">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="idConfirmDeleteRelatedItems1">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
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
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body radio_name">
				<label> <spring:message code="rfaevent.meeting.sure.delete" />
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
							<spring:message code="event.reason.cancellation.placeholder" var="cancellation" />
							<form:textarea path="cancelReason" id="cancelReason" class="width-100" placeholder="${cancellation}" rows="3" data-validation="required length" data-validation-length="max500" />
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="submit" id="rfxCancelEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">Yes</form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">No</button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<style>
.err-red {
	color: #ff5757;
}

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

.physicalCriterion>div {
	width: 20px;
	float: left;
}

.physicalCriterion>span {
	width: calc(100% - 30px);
	float: left;
}

.bootstrap-timepicker-widget.dropdown-menu.open {
	max-width: 160px;
}

.bootstrap-timepicker-widget.dropdown-menu.open input {
	width: 35px;
	min-width: 35px;
	max-width: 35px;
}

#approverSCount {
	width: 0;
	height: 0;
	border: 0;
	margin-bottom: 10px;
}

[aria-describedby="addEditContactPopup"] {
	max-width: 600px;
}

.d-flex-line {
	display: flex;
	align-items: center;
}

#addReminder1 .time-lbl {
	width: auto;
	margin-right: 15px;
}

#addReminder1 .remind-lbl {
	width: auto;
	margin-left: 15px;
	margin-right: 20px;
}

.ml-15 {
	margin-left: 15px !important;
}

.mb-15 {
	margin-bottom: 15px;
}

.w-100 {
	width: 100% !important;
}

.marginbottom {
	margin-bottom: 1.1%;
}

#allowEvaluation {
	margin-top: 6%;
}

/* #rfxEnvOpeningAfter { */
/* 	margin-top: -27%; */
/* } */
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript">
	/* Datepicker bootstrap */

	$(function() {
		"use strict";
		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

		$('.bootstrap-datepicker').bsdatepicker({
			format : 'dd/mm/yyyy',
			minDate : now,
			onRender : function(date) {
				return date.valueOf() < now.valueOf() ? 'disabled' : '';
			}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});
		

		$('#submitStep1EventDetail').click(function(e) {
			e.preventDefault();
		
			var participationFeeCurrency = ($('#participationFeeCurrency').val());
			var participationFees = ($('#participationFees').val());
			$('#participationFees').parent().removeClass('has-error').find('.form-error').remove();
			$('#participationFeeCurrency').parent().removeClass('has-error').find('.form-error').remove();
			if((participationFeeCurrency == '' &&  participationFees != '' ) ){
				$('#participationFeeCurrency').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			}else{
				$('#participationFeeCurrency').parent().removeClass('has-error').find('.form-error').remove();
			}
			
			if((participationFeeCurrency != '' &&  participationFees == '' )){
				$('#participationFees').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			}else{
				$('#participationFees').parent().removeClass('has-error').find('.form-error').remove();
			}
			
			var depositCurrency = ($('#depositCurrency').val());
			var deposit = ($('#deposit').val());
			$('#deposit').parent().removeClass('has-error').find('.form-error').remove();
			$('#depositCurrency').parent().removeClass('has-error').find('.form-error').remove();
			if((depositCurrency == '' &&  deposit != '' ) ){
				$('#depositCurrency').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			}else{
				$('#depositCurrency').parent().removeClass('has-error').find('.form-error').remove();
			}
			
			if((depositCurrency != '' &&  deposit == '' )){
				$('#deposit').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			}else{
				$('#deposit').parent().removeClass('has-error').find('.form-error').remove();
			}
			
			<c:if test="${!empty rfxTemplate and rfxTemplate.enableSuspendApproval and !rfxTemplate.optionalSuspendApproval}">
				if($('.approvalSuspRouteBox .tagTypeMultiSelect').length == 0) {
					$('.errSuspendApproval').show();
					return;
				}
			</c:if>
			
			if($('#demo-form1').isValid()) {
				$('#demo-form1').attr('action', getBuyerContextPath('storeEventDetails'));
				$('#demo-form1').submit();
			}
		});

		$('#submitStep1EventDetailDraft').click(function() {
			$('#demo-form1').attr('action', getBuyerContextPath('saveAsDraft'));
			$('#demo-form1').submit();
		});

		

		$( ".autoSave" ).change(function() {
	
			<c:if test="${event.status =='DRAFT'}">
			 // $("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'}) 
			</c:if>
			});
	
	
		$('.bootstrap-datepicker').datepicker().on('changeDate', function (ev) {
		    $('.bootstrap-datepicker').change();
		});
		
		$('.bootstrap-datepicker').change(function () {
			<c:if test="${event.status =='DRAFT'}">
			/*  $("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'}) */
			</c:if>
		
		});
		
		$('#rfxCancelEvent').click(function() {
			var cancelRequest = $('#cancelReason').val();
			if (cancelRequest != '') {
				$(this).addClass('disabled');
			}
		});
	});
	
	$('#daterangepicker-time').on('show.daterangepicker', function(ev, picker) {
	      <c:if test="${event.status == 'SUSPENDED' and event.eventStart le now}">
			$('.calendar.left').addClass('disabled');
         </c:if>
	});
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		
		$('#addReminder').on('hidden.bs.modal', function () {
		    $(this).find('form').trigger('reset');
		})
			
		$('#idValidityDays').mask('999');
		$('#idMobileNumber').mask('+00 00000000000', {
			placeholder : "e.g. +60172887734"
		});
		$('#idFaxNumber').mask('+00 00000000000', {
			placeholder : "e.g. +60 122735465"
		});
		$('.timepicker-example').timepicker({
			disableFocus : true,
			explicitMode : false
		}).on('show.timepicker', function(e) {
			setTimeout(function() {
				$('.bootstrap-timepicker-widget.dropdown-menu.open input').addClass('disabled');
			}, 500);
		});
	});
</script>
<!-- Add Reminder  -->
<script>
	<c:if test="${event.status =='SUSPENDED'}">
	$(window).bind('load',function() {
		var allowedFields = '#submitStep1EventDetail,#bubble,#idCancelEvent,#idAddReminder,#deleleteReminder,#submitStep1EventDetailDraft,#daterangepicker-time,#idValidityDays,#idCurrency,#participationFees,#deposit,.addContactPersonPop,#title,#contactName,#designation,#contactNumber,#mobileNumber,#faxNumber,#comunicationEmail,#resetEventContctForm,.closeDialog,.addContactPerson,.editContact,.deleteContact,#eventStart,#eventStartTime,#eventEnd,#eventEndTime,#leavePageModal,#stayOnpageModal,#closeConfirmLeavePageDialog,.s1_view_desc,#prevMail,#nextMail,#reloadMsg,#enableSuspensionApproval,#addSelectSuspend, .approvalSuspRouteBox, .suspRemoveApproval, #appType, .susp-apprvl, .suspApprvl, .appr-DP, .suspApprvl select, .suspApprvl span, .suspApprvl input, .suspApprvl div, .suspApprvl a, .suspApprvl li, .apprvlType a, .apprvlType input';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		
		$('.dropUp').addClass('disabled');
		$('.suspApprvl span').removeClass('disabled');
	});
	</c:if>

	function toggleRadios() {
		<c:if test="${event.status == 'SUSPENDED' and event.eventStart le now}">
		$('#startRadio').prop('disabled', true).prop('checked', false);
		$('#endRadio').prop('disabled', false).prop('checked', true);
		</c:if>
	}
	
	<c:if test="${event.status =='SUSPENDED' && sendForSuspApproval}">
	$(window).bind('load',function() {
		var allowedFields = '#submitStep1EventDetail,#bubble,#idCancelEvent,#submitStep1EventDetailDraft';
		disableFormFields(allowedFields);
		
		$('.dropUp').addClass('disabled');
	});
	</c:if>
	
	 <c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#submitStep1EventDetail,#bubble, #dashboardLink,.s1_view_desc';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('.dropUp').addClass('disabled');
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
		$('#page-content').find('.for-clander-view').not(allowedFields).parent('div').addClass('disabled');
		$('#page-content').find('.for-timepicker-view').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if> 

	$(document).ready(function() {

		$('.rfaIdAddReminder').click(function(e) {
			e.preventDefault();
			//var reminderDate = $.trim($('#daterangepicker-time').val());
			var reminderDate = $.trim($(this).closest('.row').find('.for-clander-view').val());
			var reminderTime = $.trim($(this).closest('.row').find('.for-timepicker-view').val());
			if (reminderDate != '' && reminderTime != '') {
				$("#addReminder").attr('data-dtype', $(this).data('dtype'));
				$("#addReminder").find('.remindMeTime').trigger("change");
				$("#addReminder").find('.remindMeTime option[value="DAYS"]').attr('selected','selected');
				$("#addReminder").modal("show");
				 if($("#addReminder").attr("data-dtype") === "end"){
				        $('.timestart').text ('Before the end date & time');
				    }else{
				    	  $('.timestart').text ('Before the start date & time');
				    }
			} else {
				$(this).closest('.row').find('input').blur();
			}
		});
		
		
			$("#idAutoDisqualify").click(function() {
				hideAutoDis();
			});
		hideAutoDis();
		function hideAutoDis() {
			if ($('#idAutoDisqualify').is(":checked")) {
				$(".disqualifySupplier ").show();
			} else {
				$(".disqualifySupplier ").hide();
			}
		}
	});
	
	
	 $.formUtils.addValidator({
		  name : 'check_approver',
		  validatorFunction : function(value, $el, config, language, $form) {		  
			  var response = true;
			  var labelVal = $.trim($('#apprTab').find('select').select2("val"));
			  if((labelVal == '' || $('#apprTab').find('select').length == 0) && ${(tf:rfxApprovalRequired(rfxTemplate)) } == true){
				  response = false;
			  }
			  return response;
		  },
		  errorMessage : 'Approver is required',
		  errorMessageKey: 'badCheckApprover'
		});
		$.validate({
			lang : 'en',
			modules : 'date'
		});
		$("#test-select").treeMultiselect({
			enableSelectAll : true,
			sortable : true
		});
		
		$(".item").each(function(){
			//var html = "<input type='checkbox'><label>"+$(this).text()+"</label>";
			//$(this).html(html);
		}); 
</script>
<!-- <script>
$(document).ready(function(){
	 var txt = "";
	 var valSel ="";
	 var valrem = "";
	  $("#reminderButton ,#reminderCan, #crossRem").click(function(){
		  $("#newMsg").empty();
      });
	 
	 valSel = $(".remindMeTime :selected").attr('value');
	 
	 $("input").keyup(function(){
	     valrem = $("#remindMe").val();
	     txt = "";
	     txt += "Remind me "+"<html><i>"+ valrem +"</i></html>"+" "+ valSel +" before the end date & time .";
	     $("#newMsg").html(txt);
	    
	    });
	 
	 $(".remindMeTime").change(function () {
    	 valSel = $(".remindMeTime :selected").attr('value');
    	  txt = "";
 	     txt += "Remind me "+"<html><i>"+ valrem +"</i></html>"+" "+valSel +" before the end date & time .";
 	     $("#newMsg").html(txt);
    
	    })
});
</script> -->
<style>
.content-between {
	display: flex;
	justify-content: space-between;
	font-size: 14px;
	font-weight: bold;
	margin-bottom: 5px;
}

.usersListTable .row i.fa {
	font-size: 18px;
	color: #7f7f7f;
	padding-top: 0;
}

.marginDisable .row {
	cursor: pointer;
}

.marginDisable .row:hover {
	background: #fafcfe;
}

.d-flex {
	display: flex;
	font-size: 14px;
	font-weight: bold;
	margin-bottom: 5px;
}

.d-flex-line {
	display: flex;
}

.selected-item {
	position: absolute;
	left: 55%;
}

.readOnlyClass {
	pointer-events: none !important;
	cursor: not-allowed !important;
}

input[readonly].for-clander-view {
	cursor: default !important;
}

.chosen-container-single .chosen-single span {
	width: 40rem;
}

.ml-10 {
	margin-left: 10px;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multiselect/jquery.multi-select.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multiselect/jquery.quicksearch.js"/>"></script>
<script>
$('#search').multiSelect({
    afterSelect: function(values){
    },
    afterDeselect: function(values){
      
    }
  });
</script>
<script type="text/javascript">
</script>
<script>
$(document).ready(
		function() {
			
		 		if ($('#enableEvlDeclare').is(':checked') ) {
		  			$('#evaluationProcessDiv').show();
		  		}
		  		else{
		 			$('#evaluationProcessDiv').hide();
		 		}
			
		 		if ($('#enableSupplierDeclare').is(':checked') ) {
		 			$('#evaluationSupplierDiv').show();
		  		}
		  		else{
		  			$('#evaluationSupplierDiv').hide();
		 		}
			   
		/* 	var closeReadOnlyEnv1 = $('#closeReadOnlyEnv').val();
			if(closeReadOnlyEnv1 !=undefined){
				if(closeReadOnlyEnv1.toString()=="true"){
				$('#readOnlyCloseEnvelope').addClass("readOnlyEnv");
				}
			}
			var closeReadOnlyViewSupp1 = $('#closeReadOnlyViewSupp').val();
			if(closeReadOnlyViewSupp1 !=undefined){
				if(closeReadOnlyViewSupp1.toString()=="true"){
				$('#readOnlyViewSupplier').addClass("readOnlyEnv");
				}
			} */
			
			
			if(document.getElementById('viewSupplerName')!=null){
				if(document.getElementById('viewSupplerName').checked) {
					$('#unMaskedUserDiv').show();
				}else{
					$('#unMaskedUserDiv').hide();
				} 
			}
			$("#viewSupplerName").change(function() {
			    if(this.checked) {
			    	$('#unMaskedUserDiv').show();
			    }else{
			    	$('#unMaskedUserDiv').hide();
			    }
			});

			if(document.getElementById('enableEvaluationConclusionUsers') != null){
				if(document.getElementById('enableEvaluationConclusionUsers').checked) {
					$('#evaluationConclusionUsersDiv').show();
				}else{
					$('#evaluationConclusionUsersDiv').hide();
				} 
			}
			$("#enableEvaluationConclusionUsers").change(function() {
			    if(this.checked) {
			    	$('#evaluationConclusionUsersDiv').show();
			    }else{
			    	$('#evaluationConclusionUsersDiv').hide();
			    }
			});

			
			if(document.getElementById('revertLastBid')!=null){
				if(document.getElementById('revertLastBid').checked) {
				$('#revertBidUserDiv').show();
				}else{
					$('#revertBidUserDiv').hide();
				} 
				}
				$("#revertLastBid").change(function() {
				    if(this.checked) {
				    	$('#revertBidUserDiv').show();
				    }else{
				    	$('#revertBidUserDiv').hide();
				    }
				});

			var arry=[];
			var count=0;
			var size=0;
			$('.searchable').multiSelect({
				  selectableHeader: "<input type='text' id='industCat' class='search-ico search-input' autocomplete='off' placeholder='<spring:message code="createrfi.type.search.placeholder" />\'>",
				  afterInit: function(ms){
				    var that = this,
				        $selectableSearch = that.$selectableUl.prev(),
				        $selectionSearch = that.$selectionUl.prev(),
				        selectableSearchString = '#'+that.$container.attr('id')+' .ms-elem-selectable:not(.ms-selected)',
				        selectionSearchString = '#'+that.$container.attr('id')+' .ms-elem-selection.ms-selected';
						
				    that.qs1 = $selectableSearch.quicksearch(selectableSearchString)
				    .on('keydown', function(e){
				    	 var nameList=$('#callbacks').val();
				    	/* alert(nameList) */
				    	
				    	
				    	if(e.which!="8"){
				    	
				    	var val=$("#industCat").val();
				    	
				    	var nameList1;
				    	var nameList2;
				    	if(val.length >= 3){
					    	console.log(val);
					    	$.ajax({
					    		url: getBuyerContextPath('searchCategory'),
					    		method : 'POST',
					    		data: { search : val },
					    	 	success: function (result) {
					    	 		console.log('result : ', result);
					    	 		$.each(result, function() {
					    	 				var id;
					    	 				var val;
					    	 				var code;
					    	 			$.each(this, function(index, value) {
					    	 				if(index=="id"){
					    	 					id=value;
					    	 				}if(index=="code"){
					    	 					code=value;
					    	 				}
					    	 			   	if(index=="name"){
	 				    	 					$('#callbacks').multiSelect('addOption', { value: id, text: code+" - "+value, index: 0 });
	 				    	 			   	}
					    	 			});
					    	 		});
					    	 		
					    	 		/* $('#callbacks').multiSelect('refresh'); */
					        	}
					    	});
				      	}
				    }
				      if (e.which === 40){
				        that.$selectableUl.focus();
				        return false;
				      }
				    });

				    that.qs2 = $selectionSearch.quicksearch(selectionSearchString)
				    .on('keydown', function(e){
				      if (e.which == 40){
				        that.$selectionUl.focus();
				        return false;
				      }
				    });
				  },
				  afterSelect: function(values){
					  console.log("===========>"+values);
					  
					  arry.push(values.toString());
				    $('#industryCatArr').val(arry);
				    this.qs1.cache();
				    this.qs2.cache();
					if(count >= size){
						<c:if test="${event.status =='DRAFT'}">
				   			 /* $("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'}) */
						</c:if>
					}
					
				  },
				  afterDeselect: function(values){
					  console.log("===========>"+values);
					  var index = arry.indexOf(values.toString());
					    if (index > -1) {
					    	arry.splice(index,1);
					    }
					    $('#industryCatArr').val(arry);    
					    this.qs1.cache();
					    this.qs2.cache();
					    <c:if test="${event.status =='DRAFT'}">
					    	//$("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'})
					    </c:if>
				  }
				});
			
			
			
		 	<c:forEach items="${event.industryCategories}" var="category">
		 	size=${fn:length(event.industryCategories)}
		 	count =count +1;
			if(count==size){
				count=size+1;
			}	
			$('.searchable').multiSelect('select', "${category.id}");
		
		    </c:forEach> 
		   	
		    /* $('#industryCatArr').val(arry); */
	

		});


	if(!$("#openingSeq").prop('checked')) {
		$(".btn-radio").hide();
		$(".sequence").hide();
	}

	$('#openingSeq').change(function(e) {
		if($(this).prop('checked')) {
			$(".btn-radio").show();
			$(".sequence").show();
		} else {
			$(".btn-radio").hide();
			$(".sequence").hide();
		}
	}); 


$('#idCurrency').on('change', function() {
	<c:if test="${event.status =='DRAFT'}">
	 	// $("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'})
	</c:if>
	});
	
$.formUtils.addValidator({
	name : 'validateMax',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var minValue=$("#minimumSupplierRating").val();
		 var maxValue=$("#maximumSupplierRating").val();
		 if(maxValue != '' && minValue != '') {
			 if(parseFloat($("#maximumSupplierRating").val()) < parseFloat($("#minimumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
				 response = false;
			 }
		 }
		return response;
	},
	errorMessage : 'Maximum supplier Rating/Grade must be greater than Minimum Supplier Rating/Grade',
	errorMessageKey : 'badMinMax'
});

$.formUtils.addValidator({
	name : 'validateMin',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var minValue=$("#minimumSupplierRating").val();
		 var maxValue=$("#maximumSupplierRating").val();
		 if(maxValue != '' && minValue != '') {
			 if(parseFloat($("#minimumSupplierRating").val()) > parseFloat($("#maximumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
				 response = false;
			 }
		 }
		return response;
	},
	errorMessage : 'Minimum supplier Rating/Grade must be smaller than Maximum Supplier Rating/Grade',
	errorMessageKey : 'badMinMax'
});



$.formUtils.addValidator({
	name : 'validateBothRequired',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var minValue=$("#minimumSupplierRating").val();
		 var maxValue=$("#maximumSupplierRating").val();
		 console.log('Min : ', minValue, ' - Max : ', maxValue);
		 if((maxValue != '' && minValue == '') || (maxValue == '' && minValue != '')) {
			response = false; 
		 }
		return response;
	},
	errorMessage : 'Provide values for both Minimum and Maximum Supplier Rating/Grade',
	errorMessageKey : 'missingMinMax'
});
  
$('#maximumSupplierRating').on('keyup', function() {
	$('#maximumSupplierRating').validate(function(valid, elem) {});
	$('#minimumSupplierRating').validate(function(valid, elem) {});
});
  
$('#minimumSupplierRating').on('keyup', function() {
	$('#minimumSupplierRating').validate(function(valid, elem) {});
	$('#maximumSupplierRating').validate(function(valid, elem) {});
});

$('#participationFeeCurrency').change(function() {
	$('#participationFees').parent().removeClass('has-error').find('.form-error').remove();
	$('#participationFeeCurrency').parent().removeClass('has-error').find('.form-error').remove();
	var participationFees = ($('#participationFees').val());
	var participationFeeCurrency = ($('#participationFeeCurrency').val());
	
	if((participationFees == '' &&  participationFeeCurrency != '' ) ){
		$('#participationFees').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
	}else{
		$('#participationFees').parent().removeClass('has-error').find('.form-error').remove();
	}
	
	if((participationFees != '' &&  participationFeeCurrency == '' )){
		$('#participationFeeCurrency').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
	}else{
		$('#participationFeeCurrency').parent().removeClass('has-error').find('.form-error').remove();
	}
});

	$('#participationFees').change(function() {
		var participationFeeCurrency = ($('#participationFeeCurrency').val());
		var participationFees = ($('#participationFees').val());
		$('#participationFees').parent().removeClass('has-error').find('.form-error').remove();
		$('#participationFeeCurrency').parent().removeClass('has-error').find('.form-error').remove();
		
		if((participationFeeCurrency == '' &&  participationFees != '' ) ){
			$('#participationFeeCurrency').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
		}else{
			$('#participationFeeCurrency').parent().removeClass('has-error').find('.form-error').remove();
		}
		
		if((participationFeeCurrency != '' &&  participationFees == '' )){
			$('#participationFees').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
		}else{
			$('#participationFees').parent().removeClass('has-error').find('.form-error').remove();
		}
	});
	
	
	$('#depositCurrency').change(function() {
		$('#deposit').parent().removeClass('has-error').find('.form-error').remove();
		$('#depositCurrency').parent().removeClass('has-error').find('.form-error').remove();
		var depositCurrency = ($('#depositCurrency').val());
		var deposit = ($('#deposit').val());
		
		if((deposit == '' &&  depositCurrency != '' ) ){
			$('#deposit').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
		}else{
			$('#deposit').parent().removeClass('has-error').find('.form-error').remove();
		}
		
		if((deposit != '' &&  depositCurrency == '' )){
			$('#depositCurrency').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
		}else{
			$('#depositCurrency').parent().removeClass('has-error').find('.form-error').remove();
		}
	});

		$('#deposit').change(function() {
			var depositCurrency = ($('#depositCurrency').val());
			var deposit = ($('#deposit').val());
			$('#deposit').parent().removeClass('has-error').find('.form-error').remove();
			$('#depositCurrency').parent().removeClass('has-error').find('.form-error').remove();
			if((depositCurrency == '' &&  deposit != '' ) ){
				$('#depositCurrency').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
			}else{
				$('#depositCurrency').parent().removeClass('has-error').find('.form-error').remove();
			}
			
			if((depositCurrency != '' &&  deposit == '' )){
				$('#deposit').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
			}else{
				$('#deposit').parent().removeClass('has-error').find('.form-error').remove();
			}
		});
		
		$(document).on('change', '#enableEvlDeclare', function() {
	 		if ($(this).is(':checked') ) {
	  			$('#evaluationProcessDiv').show();
	  		}
	  		else{
	 			$('#evaluationProcessDiv').hide();
	 			$('#chosenEvaluationDeclaraton').val('').trigger('chosen:updated');
	 		}
		 });
		
		$(document).on('change', '#enableSupplierDeclare', function() {
	 		if ($(this).is(':checked') ) {
	 			$('#evaluationSupplierDiv').show();
	  		}
	  		else{
	  			$('#evaluationSupplierDiv').hide();
	 			$('#choseSupplierDeclaration').val('').trigger('chosen:updated');
	 		}
		 });

		
		
		$.formUtils.addValidator({
			  name : 'requiredEvlDeclaration',
			  validatorFunction : function(value, $el, config, language, $form) {		  
				  var response = true;
				  var labelVal = $('#chosenEvaluationDeclaraton').val();
				  if((labelVal == '' || labelVal.length == 0) && $('#enableEvlDeclare').is(':checked') ){
					  response = false;
				  }
				  return response;
			  },
			  errorMessage : 'This is a required field',
			  errorMessageKey: 'requiredDeclarationCheck'
			});

		$.formUtils.addValidator({
			  name : 'requiredSupplierDeclaration',
			  validatorFunction : function(value, $el, config, language, $form) {		  
				  var response = true;
				  var labelVal = $('#choseSupplierDeclaration').val();
				  if((labelVal == '' || labelVal.length == 0) && $('#enableSupplierDeclare').is(':checked') ){
					  response = false;
				  }
				  return response;
			  },
			  errorMessage : 'This is a required field',
			  errorMessageKey: 'requiredDeclarationCheck'
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

		$('#businessUnitId').on('change', function(){
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
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/ajaxFormPlugin.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<!-- daterange picker js and css start -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Multi-select -->
<script type="text/javascript" src="<c:url value="/resources/js/view/createrftevent.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
