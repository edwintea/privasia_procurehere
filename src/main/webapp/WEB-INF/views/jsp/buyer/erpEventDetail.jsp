<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

#page-content-wrapper {
	min-height: 1200px;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.marLeft {
	margin-left: 45px;
}
</style>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard"/></a></li>
				<li class="active"><spring:message code="erp.detail"/></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head"><spring:message code="erp.event.detail"/></h2>
				<h2 class="trans-cap pull-right"><spring:message code="application.status"/> : ${eventDetail.action}</h2>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="tab-pane active">
				<div class="tab-content Invited-Supplier-List ">
					<div class="tab-pane active white-bg" id="step-1">
						<div class="upload_download_wrapper clearfix mar-t20 event_info">
							<div class="main-panal-box-main border-bottom">
								<div class="main-panal-box">
									<label><spring:message code="application.referencenumber" /> : </label>
									<p>${eventDetail.prNo}</p>
								</div>
								<div class="main-panal-box">
									<label><spring:message code="erp.event.currency" /> : </label>
									<p>${eventPayload.curr}</p>
								</div>
								<%-- 								<div class="main-panal-box">
									<label>Event Category : </label>
									<p>${eventPayload.itmCat}</p>
								</div> --%>
								<c:if test="${eventDetail.action eq 'ERROR'}">
									<div class="main-panal-box">
										<label><spring:message code="erp.error.msg" />: </label>
										<p>${eventDetail.responseMsg}</p>
									</div>
								</c:if>
							</div>
							<div class="col-md-12 border-bottom"></div>
							<div class="main-panal-box-main comm-info">
								<h2><spring:message code="eventdescription.billofquantity.label" /></h2>
								<table class="tabaccor padding-none-td  display table" width="100%" cellpadding="0" cellspacing="0" border="0">
									<thead>
										<tr>
											<th><spring:message code="label.rftbq.th.itemname" /></th>
											<th><spring:message code="label.rftbq.th.quantity" /></th>
											<th><spring:message code="erp.label.uom" /></th>
											<c:if test="${not empty eventPayload.field1Label}">
												<th>${eventPayload.field1Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field2Label}">
												<th>${eventPayload.field2Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field3Label}">
												<th>${eventPayload.field3Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field4Label}">
												<th>${eventPayload.field4Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field5Label}">
												<th>${eventPayload.field5Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field6Label}">
												<th>${eventPayload.field6Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field7Label}">
												<th>${eventPayload.field7Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field8Label}">
												<th>${eventPayload.field8Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field9Label}">
												<th>${eventPayload.field9Label}</th>
											</c:if>
											<c:if test="${not empty eventPayload.field10Label}">
												<th>${eventPayload.field10Label}</th>
											</c:if>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="eventDetailsPayload" items="${eventPayloadValue}">

											<c:if test="${empty eventDetailsPayload.parent }">
												<tr style="background-color: #f9f9f9;">
													<td>${eventDetailsPayload.itemName}</td>
													<td><fmt:formatNumber type="number" maxFractionDigits="0" minFractionDigits="0" value="${eventDetailsPayload.quantity}" /> <%-- ${eventDetailsPayload.qty} --%></td>
													<td>${eventDetailsPayload.uom.uom}</td>
													<c:if test="${not empty eventPayload.field1Label}">
														<td>${eventDetailsPayload.field1}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field2Label}">
														<td>${eventDetailsPayload.field2}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field3Label}">
														<td>${eventDetailsPayload.field3}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field4Label}">
														<td>${eventDetailsPayload.field4}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field5Label}">
														<td>${eventDetailsPayload.field5}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field6Label}">
														<td>${eventDetailsPayload.field6}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field7Label}">
														<td><c:if test="${(eventPayload.field7Label eq 'Delivery Date' and not empty eventDetailsPayload.field7)}">
																<fmt:parseDate value="${eventDetailsPayload.field7}" var="parseDelDate" pattern="yyyyMMdd" />
																<fmt:formatDate value="${parseDelDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
															</c:if></td>
													</c:if>
													<c:if test="${not empty eventPayload.field8Label}">
														<td>${eventDetailsPayload.field8}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field9Label}">
														<td>${eventDetailsPayload.field9}</td>
													</c:if>
													<c:if test="${not empty eventPayload.field10Label}">
														<td>${eventDetailsPayload.field10}</td>
													</c:if>
												</tr>
											</c:if>
											<c:if test="${ not empty eventDetailsPayload.children}">
												<c:forEach var="children" items="${eventDetailsPayload.children}">
													<tr>

														<td>${children.itemName}</td>
														<td><fmt:formatNumber type="number" maxFractionDigits="0" minFractionDigits="0" value="${children.quantity}" /> <%-- ${eventDetailsPayload.qty} --%></td>
														<td>${children.uom.uom}</td>
														<c:if test="${not empty eventPayload.field1Label}">
															<td>${children.field1}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field2Label}">
															<td>${children.field2}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field3Label}">
															<td>${children.field3}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field4Label}">
															<td>${children.field4}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field5Label}">
															<td>${children.field5}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field6Label}">
															<td>${children.field6}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field7Label}">
															<td><c:if test="${(eventPayload.field7Label eq 'Delivery Date' and not empty eventDetailsPayload.field7)}">
																	<fmt:parseDate value="${children.field7}" var="parseDelDate" pattern="yyyyMMdd" />
																	<fmt:formatDate value="${parseDelDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</c:if></td>
														</c:if>
														<c:if test="${not empty eventPayload.field8Label}">
															<td>${children.field8}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field9Label}">
															<td>${children.field9}</td>
														</c:if>
														<c:if test="${not empty eventPayload.field10Label}">
															<td>${children.field10}</td>
														</c:if>
													</tr>
												</c:forEach>
											</c:if>


										</c:forEach>
									</tbody>
								</table>
							</div>
							<div>
								<%-- <a href="${pageContext.request.contextPath}/buyer/createEvent/${eventDetail.id}" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out btn-font">Create</a> --%>
								<c:if test="${eventDetail.action eq 'PENDING'}">
									<a href="#createEventModal" role="button" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out btn-font" data-toggle="modal"><spring:message code="erp.create.event.using" /></a>
								</c:if>

								<c:if test="${eventDetail.action eq 'DUPLICATE'}">
									<a href="#updateEventModal" role="button" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out btn-font" data-toggle="modal"><spring:message code="erp.overwrite" /></a>
								</c:if>


								<a href="${pageContext.request.contextPath}/buyer/erpManualList" id="cancelButton" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous marg-left-10"> <spring:message code="eventReport.cls" />
								</a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
.tab-pane {
	padding: 20px !important;
}

.select-radio-lineHgt {
	line-height: 5px !important;
}

.radio {
	margin-top: 13px !important;
}

.hideDiv {
	display: none;
}

.btn-font {
	color: white !important;
	font-weight: 600;
}
</style>
<%-- <div class="modal fade" id="createEventModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Confirm Cancellation</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="prId" value="${pr.id}">
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label>
								Are you sure you want to cancel this PR. </br>Reason :
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Mention reason for cancellation" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">No</button>
				</div>
			</form>
		</div>
	</div>
</div> --%>
<div class="modal fade" title="Create Event" id="createEventModal" role="dialog">
	<div class="modal-dialog modal-width-custom">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="application.create.event" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="${pageContext.request.contextPath}/buyer/createErpEvent" method="post" id="idCreateEvent">
				<input type="hidden" name="auditId" value="${eventDetail.id}"> <input type="hidden" name="prNo" value="${eventDetail.prNo}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row marg-bottom-20">
						<div class="form-tander1 pad_left_15">
							<div class="col-sm-3 col-md-4 col-xs-6">
								<label><spring:message code="application.eventtype" /></label>
							</div>
							<div class="col-sm-5 col-md-6 col-xs-6 col-xs-6">
								<select name="eventType" class="form-control chosen-select disablesearch" data-validation="required" id="idType">
									<c:forEach items="${eventTypeList}" var="eventType">
										<option value="${eventType}">${eventType.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-tander1 pad_left_15 marg-top-20">
							<div class="col-sm-3 col-md-4 col-xs-6">
								<label><spring:message code="application.event.template" /></label>
							</div>
							<div class="col-sm-5 col-md-6 col-xs-6 col-xs-6">
								<select name="rfxTemplate" class="form-control chosen-select disablesearch" data-validation="required" id="idRfxTemplate">
									<c:forEach items="${rfxTemplateList}" var="template">
										<option value="${template.id}">${template.templateName}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<div class="row align-center">
						<button id="createEvent" type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20"><spring:message code="application.create"/></button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>




<div class="modal fade" title="Create Event" id="updateEventModal" role="dialog">
	<div class="modal-dialog modal-width-custom">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="erp.please.confirm.overwrite"/></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="${pageContext.request.contextPath}/buyer/updateErpEvent" method="post" id="idCreateEvent">
				<input type="hidden" name="auditId" value="${eventDetail.id}"> <input type="hidden" name="prNo" value="${eventDetail.prNo}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<label><spring:message code="erpevent.sure.overwrite.current"/></label>
					<div class="row align-center">
						<button id="createEvent" type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20"><spring:message code="erp.event.process.button"/></button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>





<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind(
			'load',
			function() {
				var allowedFields = '.button-previous, #dashboardLink';
				//var disableAnchers = ['#reloadMsg'];        
				disableFormFields(allowedFields);
				$('#page-content').find('select').not(allowedFields).parent(
						'div').addClass('disabled');
			});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
	$(document).ready(function() {

	});

	$(document).delegate(
			'#idType',
			'change',
			function(e) {
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				var eventType = $('#idType option:selected').text();
				$.ajax({
					url : getContextPath() + '/buyer/getRfxTemplates/'
							+ eventType,
					type : "GET",
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						xhr
								.setRequestHeader("Content-Type",
										"application/json");
						$('#loading').show();
					},
					success : function(data, textStatus, request) {
						var templateList = '';
						$.each(data, function(i, item) {
							templateList += '<option value="' + item.id +'" >'
									+ item.templateName + '</option>';
						});
						$('#idRfxTemplate').html(templateList).trigger(
								"chosen:updated");
					},
					complete : function() {
						$('#loading').hide();
					}
				});

			});

	
</script>