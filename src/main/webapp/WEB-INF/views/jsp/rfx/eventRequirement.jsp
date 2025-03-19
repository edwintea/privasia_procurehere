<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle">
		<div class="col-md-6">
			<div class="row">
				<div class="form-tander1 requisition-summary-box">
					<div class="col-md-4 col-sm-6">
						<label><spring:message code="eventdescription.document.label" /></label>
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
						<label><spring:message code="eventdescription.meeting.label" /></label>
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
			<div class="row">
				<div class="form-tander1 requisition-summary-box">
					<div class="col-md-4 col-sm-6">
						<label><spring:message code="eventdescription.questionnaires.label" /></label>
					</div>
					<div class="col-md-8 col-sm-6">
						<div class="radio_yes-no-main width100">
							<div class="radio_yes-no">
								<div class="radio-info">
									<label class="select-radio"> <form:radiobutton path="questionnaires" class="custom-radio " value="0" /> <spring:message code="application.no" />
									</label>
								</div>
							</div>
							<div class="radio_yes-no">
								<div class="radio-info">
									<label class="select-radio"> <form:radiobutton path="questionnaires" class="custom-radio " value="1" /> <spring:message code="application.yes" />
									</label>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<c:if test="${event.auctionType eq 'FORWARD_DUTCH' or event.auctionType eq 'REVERSE_DUTCH'}">
				<div class="form-tander1 requisition-summary-box">
					<div class="row">
						<div class="col-md-4 col-sm-6">
							<label><spring:message code="eventdescription.billofquantity.label" /></label>
						</div>
						<div class="col-md-8 col-sm-6">
							<div class="radio_yes-no-main width100">
								<div class="radio_yes-no">
									<div class="radio-info">
										<label class="select-radio"> <form:radiobutton path="billOfQuantity" class="custom-radio " value="0" /> <spring:message code="application.no" />
										</label>
									</div>
								</div>
								<div class="radio_yes-no">
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
<%--			<c:if test="${event.auctionType eq 'FORWARD_DUTCH' or event.auctionType eq 'REVERSE_DUTCH'}">--%>
				<div class="form-tander1 requisition-summary-box">
					<div class="row">
						<div class="col-md-4 col-sm-6">
							<label><spring:message code="eventdescription.schedule.rate.label" /></label>
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
<%--			</c:if>--%>
		</div>
	</div>
