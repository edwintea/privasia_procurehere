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

	<c:if test="${eventType == 'RFA' }">
		<div class="upload_download_wrapper collapseable clearfix mar-t20 event_info marg-bottom-10 marg-top-10 in">
			<h4>
				<spring:message code="eventdescription.finance.label" />
			</h4>
			<div id="financeleb" class="import-supplier-inner-first-new collapse pad_all_15 global-list in">
				<div class="col-md-6">
					<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'BASE_CURRENCY' )) : 'true' }">
						<div class="row marg-bottom-10">
							<div class="col-md-4 col-sm-6">
								<label class="marg-top-10"> <spring:message code="eventdescription.basecurrency.label" />
								</label>
							</div>
							<div class="col-md-8 dd sky mar_b_10 col-sm-6 ">
								<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'BASE_CURRENCY')) : 'false')  or (event.status =='SUSPENDED')}">
									<input type="hidden" name="baseCurrency" value="${event.baseCurrency.id}" />
								</c:if>
								<form:select path="baseCurrency" data-validation="${!empty templateFields ? (tf:required( templateFields, 'BASE_CURRENCY' ) ? 'required' : '') : 'required'}" cssClass="form-control chosen-select autoSave" id="idCurrency"
									disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'BASE_CURRENCY' )? 'true' : 'false'): 'false' ) or (event.status =='SUSPENDED'  ? 'true' : 'false')}" style="${!empty templateFields ? (tf:readonly( templateFields, 'BASE_CURRENCY' )? 'opacity:0' : '') : '' }">
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
							<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'DECIMAL')) : 'false')  or (event.status =='SUSPENDED')}">
								<input type="hidden" name="decimal" value="${event.decimal}" />
							</c:if>
							<div class="col-md-8 dd sky mar_b_10 col-sm-6">
								<form:select path="decimal" data-validation="${!empty templateFields ? (tf:required( templateFields, 'DECIMAL' ) ? 'required' : '') : 'required'}" cssClass="form-control chosen-select disablesearch decimalChange autoSave" id="iddecimal"
									disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'DECIMAL' )? 'true'  : 'false') : 'false' )  or (event.status =='SUSPENDED' ? 'true' : 'false') }" style="${!empty templateFields ? (tf:readonly( templateFields, 'DECIMAL' )? 'opacity:0'  : '') : '' }">
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
								<c:if test="${!empty templateFields ? (tf:readonly(templateFields, 'COST_CENTER')) : 'false'}">
									<input type="hidden" name="costCenter" value="${event.costCenter.id}" />
								</c:if>
								<form:select path="costCenter" cssClass="form-control chosen-select autoSave" id="costCenterId" 
									data-validation="${!empty templateFields ? (tf:required( templateFields, 'COST_CENTER' ) ? 'required' : '') : '' }"
									disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'COST_CENTER' )? 'true' : 'false') : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }"
									style="${!empty templateFields ? (tf:readonly( templateFields, 'COST_CENTER' )? 'opacity:0' : '') : ''  }">
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
							<div class="form-group ${!empty templateFields ? (tf:readonly( templateFields, 'BUSINESS_UNIT' ) ? 'disabled' : '') : ''} ">
								<form:select path="businessUnit" id="businessUnitId" cssClass="form-control chosen-select autoSave" data-validation="required" class="custom-select">
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
							<div class="col-md-8 dd sky mar_b_10 col-sm-6">
								<c:if test="${!empty templateFields ? (tf:readonly( templateFields, 'GROUP_CODE' )) : 'false' }">
									<input type="hidden" name="groupCode" value="${event.groupCode.id}" />
								</c:if>
								<form:select path="groupCode" cssClass="form-control chosen-select autoSave" id="idGroupCode" 
									data-validation="${!empty templateFields ? (tf:required( templateFields, 'GROUP_CODE' ) ? 'required' : '') : '' }"
									disabled="${tf:readonly( templateFields, 'GROUP_CODE' ) }" style="${tf:readonly( templateFields, 'GROUP_CODE' ) ? 'opacity:0' : '' }">
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
					<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'BUDGET_AMOUNT' )) : 'true' }">
						<div class="row marg-bottom-10">
							<div class="col-md-4 col-sm-6">
								<label class="marg-top-10"> <spring:message code="eventdescription.budgetamount.label" />
								</label>
							</div>
							<div class="col-md-8 dd sky mar_b_10 col-sm-6">
								<spring:message code="event.budget.amount.placeholder" var="budgetamt" />
								<fmt:formatNumber var="budgetAmount" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.budgetAmount}" />
								<form:input path="budgetAmount" type="text" value="${budgetAmount}" class="form-control autoSave" placeholder="${budgetamt}" 
								readonly="${!empty templateFields ? (tf:readonly( templateFields, 'BUDGET_AMOUNT' )) : 'false'}" 
								data-validation="${!empty templateFields ? (tf:required( templateFields, 'BUDGET_AMOUNT' ) ? 'required' : '') : '' } validate_max_13 positive" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
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
									data-validation="${!empty templateFields ? (tf:required( templateFields, 'ESTIMATED_BUDGET' ) ? 'required' : '') : '' } validate_max_13 positive"
									data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
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
									data-validation="${!empty templateFields ? (tf:required( templateFields, 'HISTORIC_AMOUNT' ) ? 'required' : '') : '' } validate_max_13 positive"
									data-validation-regexp="^[\d,]{1,10}(\.\d{1,${event.decimal}})?$" />
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
								<form:textarea path="paymentTerm" placeholder="${paymentterm}" class="form-control textarea-autosize autoSave" 
								data-validation="${!empty templateFields ? (tf:required( templateFields, 'PAYMENT_TERM' ) ? 'required' : '') : '' } length" data-validation-length="0-124" 
								readonly="${!empty templateFields ? (tf:readonly( templateFields, 'PAYMENT_TERM' )) : 'false'}" />
							</div>
						</div>
					</c:if>
				</div>
			</div>
		</div>
		<!-- Rfa finance End -->
		<!--RFA Event Timeline  -->
		<div class="upload_download_wrapper collapseable clearfix mar-t20 event_info marg-bottom-10">
			<h4>
				<spring:message code="eventsummary.timeline.title" />
			</h4>
			<div id="etimeline" class="collapse in">
				<div class="pad_all_15 float-left width-100 time-set in">
					<h3>
						<spring:message code="rfaevent.timeline.time.setting" />
					</h3>
					<div class="event-timeline-inner">
						<div class="row">
							<div class="col-md-4 col-sm-6 col-xs-12 col-lg-4">
								<label><spring:message code="rfaevent.timeline.when.send.invite" /></label>
							</div>
							<div class="col-md-2 col-sm-3 col-xs-6 col-lg-2">
								<div class="input-prepend input-group ${event.status == 'SUSPENDED' ? 'disabled' : '' }">
									<form:input class="bootstrap-datepicker form-control for-clander-view" data-validation="required" path="eventPublishDate" autocomplete="off" data-date-format="mm/dd/yy" />
								</div>
							</div>
							<div class="col-md-2 col-sm-3 col-xs-6 col-lg-2">
								<div class="bootstrap-timepicker dropdown ${event.status == 'SUSPENDED' ? 'disabled' : '' }">
									<form:input path="eventPublishTime" data-validation="required" autocomplete="off" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control autoSave" />
								</div>
							</div>
							<!-- <div class="col-md-2 col-sm-3 col-xs-12 col-lg-2">
						<select class="custom-select">
							<option>Minuts</option>
						</select>
					</div> -->
						</div>
					</div>
				</div>
				<div class="pad_all_15 float-left width-50 Auction-specific-time-main">
					<label class="marg-bottom-20"><spring:message code="rfaevent.event.starts.ends" /></label>
					<div class="Auction-specific-time">
						<label> <form:radiobutton path="auctionStartRelative" data-toggle="spectime" cssClass="radio_yes-no auction-spt-radio custom-radio autoSave" value="0" /> <spring:message code="rfaevent.radio.specific.time" />
						</label>
					</div>
					<div class="Auction-specific-time">
						<label> <form:radiobutton path="auctionStartRelative" data-toggle="reltime" cssClass="auction-spt-radio custom-radio autoSave" value="1" /> <spring:message code="rfaevent.radio.time.relative" />
						</label>
					</div>
				</div>
				<div class="pad_all_15 float-left width-100">
					<div class="ac-time-togle collapse ${event.auctionStartRelative ? '' : 'in' }" id="spectime">
						<div class="row marg-bottom-20">
							<div class="col-md-4 col-sm-6 col-xs-12 col-lg-4">
								<label class=""><spring:message code="application.startdate" /> &amp; <spring:message code="application.time" /></label>
							</div>
							<div class="col-md-2 col-sm-3 col-xs-6 col-lg-2">
								<div class="input-prepend input-group ${(event.status == 'SUSPENDED' and event.eventStart le now) ? 'disabled' : '' }">
									<form:input class="bootstrap-datepicker form-control for-clander-view startDateAuction" data-dtype="start" data-validation="required" path="eventStart" data-startdate="" data-date-format="mm/dd/yy" autocomplete="off" />
								</div>
							</div>
							<div class="col-md-2 col-sm-3 col-xs-6 col-lg-2">
								<div class="bootstrap-timepicker dropdown ${(event.status == 'SUSPENDED' and event.eventStart le now) ? 'disabled' : '' }">
									<form:input path="eventStartTime" onfocus="this.blur()" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control startTimeAuction autoSave" data-dtype="start" data-validation="required" autocomplete="off" />
								</div>
							</div>
							<div class="col-md-1 col-sm-1 col-xs-1">
								<div class="ring plus_btn_wrap " data-target="#myModal6" data-toggle="modal">
									<a class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out rfaIdAddReminder ${(event.status == 'SUSPENDED' and event.eventStart le now) ? 'disabled' : '' }" title="Add Reminder" data-placement="top" data-dtype="start" id="idAddReminder" data-toggle="tooltip"> <img
										src="${pageContext.request.contextPath}/resources/images/ring_cion.png">
									</a>
								</div>
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-4 col-sm-6 col-xs-12 col-lg-4"></div>
							<div class="col-md-4 col-sm-6 col-xs-12 col-lg-4">
								<div class="ph_table_border">
									<div class="reminderList marginDisable">
										<c:forEach items="${startReminderList}" var="reminder">
											<div class="row reminderId" id="${reminder.id}">
												<input type="hidden" name="reminderDate" value="${ reminder.reminderDate}">
												<fmt:formatDate var="reminderDateTime" value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												<input type="hidden" class="reminderDateDel" value="${reminderDateTime}">
												<div class="col-md-10">
													<p>
														<b><spring:message code="rfaevent.reminder" />: </b>${reminderDateTime}</p>
												</div>
												<div class="col-md-2">
													<a href="" class="deleteReminder ${event.status == 'SUSPENDED'
													 ? 'disabled' : ''}" id="deleleteReminder" data-remtype="start" reminderId=""> <i class="fa fa-times-circle"></i>
													</a>
												</div>
											</div>
										</c:forEach>
										<c:if test="${empty startReminderList}">
											<div class="row" id="">
												<div class="col-md-12">
													<p>
														<spring:message code="rfi.no.reminder" />
													</p>
												</div>
											</div>
										</c:if>
									</div>
								</div>
							</div>
						</div>
						<c:if test="${event.auctionType != 'FORWARD_DUTCH' and event.auctionType != 'REVERSE_DUTCH'}">
							<div class="row marg-bottom-20">
								<div class="col-md-4 col-sm-6 col-xs-12 col-lg-4">
									<label class=""><spring:message code="rfaevent.end.date" /> &amp; <spring:message code="application.time" /></label>
								</div>
								<div class="col-md-2 col-sm-3 col-xs-6 col-lg-2">
									<div class="input-prepend input-group">
										<form:input autocomplete="off" class="bootstrap-datepicker form-control for-clander-view endDateAuction" data-dtype="end" data-validation="required" path="eventEnd" data-date-format="mm/dd/yy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</div>
								</div>
								<div class="col-md-2 col-sm-3 col-xs-6 col-lg-2">
									<div class="bootstrap-timepicker dropdown">
										<form:input path="eventEndTime" class="timepicker-example for-timepicker-view form-control endTimeAuction autoSave" data-dtype="end" data-validation="required" />
									</div>
								</div>
								<div class="col-md-1 col-sm-1 col-xs-1">
									<div class="ring plus_btn_wrap" data-target="#myModal6" data-toggle="modal">
										<a class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out rfaIdAddReminder" data-dtype="end" title="Add Reminder" data-placement="top" id="idAddReminder" data-toggle="tooltip"> <img src="${pageContext.request.contextPath}/resources/images/ring_cion.png">
										</a>
									</div>
								</div>
							</div>
							<div class="row marg-bottom-20">
								<div class="col-md-4 col-sm-6 col-xs-12 col-lg-4"></div>
								<div class="col-md-4 col-sm-6 col-xs-12 col-lg-4">
									<div class="ph_table_border">
										<div class="reminderList marginDisable">
											<c:forEach items="${reminderList}" var="reminder">
												<div class="row reminderId" id="${reminder.id}">
													<input type="hidden" name="reminderDate" value="${ reminder.reminderDate}">
													<fmt:formatDate var="reminderDateTime" value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
													<input type="hidden" class="reminderDateDel" value="${reminderDateTime}">
													<div class="col-md-10">
														<fmt:formatDate var="reminderDateTime" value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
														<p>
															<b>Reminder: </b>${reminderDateTime}</p>
													</div>
													<div class="col-md-2">
														<a href="" class="deleteReminder ${event.status == 'SUSPENDED' &&
																reminder.startReminder != null && reminder.startReminder != false ? 'disabled' : ''}" id="deleleteReminder" data-remtype="end" reminderId=""> <i class="fa fa-times-circle"></i>
														</a>
													</div>
												</div>
											</c:forEach>
											<c:if test="${empty reminderList}">
												<div class="row" id="">
													<div class="col-md-12">
														<p>
															<spring:message code="rfi.no.reminder" />
														</p>
													</div>
												</div>
											</c:if>
										</div>
									</div>
								</div>
							</div>
						</c:if>
					</div>
					<div class="ac-time-togle collapse ${event.auctionStartRelative ? 'in' : '' }" id="reltime">
						<c:if test="${event.auctionType != 'FORWARD_DUTCH' and event.auctionType != 'REVERSE_DUTCH'}">
							<div class="row marg-bottom-20">
								<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
									<label class="pad-left-23-with-margin"><spring:message code="rfaevent.auction.duration" /></label>
								</div>
								<div class="col-md-3 col-sm-3 col-xs-12 col-lg-2">
									<form:input class="form-control autoSave" path="auctionDuration" data-validation="required" />
								</div>
								<div class="col-md-2 col-sm-3 col-xs-12 col-lg-2">
									<form:select path="auctionDurationType" cssClass="form-control disablesearch chosen-select autoSave" id="status">
										<form:options items="${auctionDurationTypeList}" />
									</form:select>
								</div>
							</div>
						</c:if>
						<div class="row marg-bottom-20">
							<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
								<label class="pad-left-23-with-margin">After the following auction ends</label>
							</div>
							<div class="col-md-5 col-sm-4 col-xs-12 col-lg-4">
								<form:select path="previousAuction" cssClass="chosen-select autoSave" id="idChooseEvent" data-validation="required">
									<form:option value="">
										<spring:message code="rfaevent.keyword.serach.placeholder" />
									</form:option>
									<c:if test="${event.previousAuction != null}">
										<form:option value="${event.previousAuction.id}" label="${event.previousAuction.eventName}" />
									</c:if>
									<form:options items="${finishedEvent}" itemValue="id" itemLabel="eventName" />
								</form:select>
							</div>
						</div>
						<div class="row">
							<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
								<label class="pad-left-23-with-margin"><spring:message code="rfaevent.auction.starts" /></label>
							</div>
							<div class="col-md-3 col-sm-3 col-xs-12 col-lg-2">
								<form:input class="form-control autoSave" path="auctionStartDelay" data-validation="required" />
							</div>
							<div class="col-md-2 col-sm-3 col-xs-12 col-lg-2">
								<form:select path="auctionStartDelayType" cssClass="form-control disablesearch chosen-select autoSave" id="status">
									<form:options items="${auctionStartDelayTypeList}" />
								</form:select>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<c:if test="${event.auctionType != 'FORWARD_DUTCH' and event.auctionType != 'REVERSE_DUTCH' and event.auctionType != 'FORWARD_SEALED_BID' and event.auctionType != 'REVERSE_SEALED_BID' }">
			<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'TIME_EXT_TYPE' )) : 'true' }">
				<div class="upload_download_wrapper collapseable clearfix mar-t20 event_info marg-bottom-10">
					<h4>
						<spring:message code="eventsummary.eventdetail.time.extension" />
					</h4>
					<div id="timeext" class="collapse pad_all_15 float-left width-100 in">
						<div class="row marg-bottom-20">
							<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
								<label><spring:message code="rfaevent.select.extension.type" /></label>
							</div>
							<div class="col-md-7 col-sm-3 col-xs-12 col-lg-6">
								<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'TIME_EXT_TYPE')) : 'false')or (event.status =='SUSPENDED' ? 'true' :'false') }">
									<input type="hidden" name="timeExtensionType" value="${event.timeExtensionType}" />
								</c:if>
								<form:select id="idTypeExtension" path="timeExtensionType" class="chosen-select disablesearch autoSave" disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'true' : 'false') : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }"
									style="${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'opacity:0' : '') : ''  }">
									<form:option value="AUTOMATIC">
										<spring:message code="rfaevent.automatic.option" />
									</form:option>
									<form:option value="MANUALLY">
										<spring:message code="rfaevent.manual.option" />
									</form:option>
								</form:select>
							</div>
						</div>
						<div class="row marg-bottom-20 hideDiv">
							<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
								<label><spring:message code="rfaevent.time.extension" /></label>
							</div>
							<div class="col-md-4 col-sm-3 col-xs-12 col-lg-3">
								<form:input path="timeExtensionDuration" data-validation="required number" class="form-control autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'disabled' : '') : '' }" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )) : 'false'}" maxlength="3" />
							</div>
							<div class="col-md-3 col-sm-3 col-xs-12 col-lg-3">
								<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'TIME_EXT_TYPE')) : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }">
									<input type="hidden" name="timeExtensionDurationType" value="${event.timeExtensionDurationType}" />
								</c:if>
								<form:select path="timeExtensionDurationType" id="status" cssClass="form-control disablesearch chosen-select autoSave" disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'true' : 'false') : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }"
									style="${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'opacity:0' : '') : ''  }">
									<form:option value="MINUTE">
										<spring:message code="label.minute" />
									</form:option>
									<form:option value="HOUR">
										<spring:message code="rfaevent.hours" />
									</form:option>
								</form:select>
							</div>
						</div>
						<!-- Display none because its not in use now  -->
						<div class="row marg-bottom-20 hideDiv">
							<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
								<label><spring:message code="rfaevent.time.extension.triggered" /></label>
							</div>
							<div class="col-md-4 col-sm-3 col-xs-12 col-lg-3">
								<form:input type="text" path="timeExtensionLeadingBidValue" data-validation="required number" class="form-control autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'disabled' : '') : '' }" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )) : 'false'}" maxLength="3" />
							</div>
							<div class="col-md-3 col-sm-3 col-xs-12 col-lg-3">
								<c:if test="${(!empty templateFields ? (tf:readonly(templateFields, 'TIME_EXT_TYPE')) : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }">
									<input type="hidden" name="timeExtensionLeadingBidType" value="${event.timeExtensionLeadingBidType}" />
								</c:if>
								<form:select path="timeExtensionLeadingBidType" id="idTimeExtType" cssClass="form-control disablesearch chosen-select autoSave" disabled="${(!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'true' : 'false') : 'false') or (event.status =='SUSPENDED' ? 'true' :'false') }"
									style="${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'opacity:0' : '') : ''  }">
									<form:option value="MINUTE">
										<spring:message code="label.minute" />
									</form:option>
									<form:option value="HOUR">
										<spring:message code="rfaevent.hours" />
									</form:option>
								</form:select>
							</div>
							<div class="col-md-3 col-sm-3 col-xs-12 col-lg-3 line-height-set">
								<label><spring:message code="rfaevent.before.event.end" /></label>
							</div>
						</div>
						<!-- Display none because its not in use now  -->
						<div class="row marg-bottom-20 hideDiv">
							<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
								<label><spring:message code="rfaevent.max.rounds.extension" /></label>
							</div>
							<div class="col-md-4 col-sm-3 col-xs-12 col-lg-3">
								<form:input path="extensionCount" data-validation="required number" class="form-control autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )? 'disabled' : '') : '' }" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'TIME_EXT_TYPE' )) : 'false'}" maxLength="3" />
							</div>
						</div>
						<div class="hideDiv">
							<div class="row marg-bottom-20 hideDiv">
								<div class="col-md-7 col-xs-7 col-lg-7 col-md-offset-4 col-lg-offset-3 marg-bottom-20">
									<c:choose>
										<c:when test="${!empty templateFields && tf:readonly(templateFields, 'BIDDER_DISQUALIFY_COUNT')}">
											<form:checkbox path="autoDisqualify" id="idAutoDisqualify" class="disabled autoSave" disabled="true" readonly="true" />
										</c:when>
										<c:otherwise>
											<form:checkbox path="autoDisqualify" id="idAutoDisqualify" class="autoSave" readonly="false" />
										</c:otherwise>
									</c:choose>
									<label>Auto disqualify number of worst-ranking bidder for each round of extension</label>
								</div>
							</div>
							<div class="row marg-bottom-20 disqualifySupplier">
								<div class="col-md-4 col-sm-12 col-xs-12 col-lg-3">
									<label><spring:message code="rfaevent.disqualify.supplier.count" /></label>
								</div>
								<div class="col-md-4 col-sm-3 col-xs-12 col-lg-3">
									<form:input path="bidderDisqualify" class="form-control autoSave ${!empty templateFields ? (tf:readonly( templateFields, 'BIDDER_DISQUALIFY_COUNT' )? 'disabled' : '') : '' }" readonly="${!empty templateFields ? (tf:readonly( templateFields, 'BIDDER_DISQUALIFY_COUNT' )) : 'false'}" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</c:if>
		<c:if test="${event.status == 'SUSPENDED'}">
			<form:hidden path="documentReq" />
			<form:hidden path="meetingReq" />
			<form:hidden path="questionnaires" />
			<form:hidden path="billOfQuantity" />
			<form:hidden path="scheduleOfRate" />
		</c:if>
		<c:if test="${event.status != 'SUSPENDED'}">
			<div class="upload_download_wrapper collapseable clearfix mar-t20 event_info marg-bottom-10">
				<h4>
					<spring:message code="eventdescription.eventrquirement.label" />
				</h4>
				<jsp:include page="eventRequirement.jsp"></jsp:include>
			</div>
		</c:if>
	</c:if>
							