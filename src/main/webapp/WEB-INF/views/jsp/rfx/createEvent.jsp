<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message var="rfxChooseDesk" code="application.rfx.choose" />
<spring:message var="rfaChooseDesk" code="application.rfa.choose" />
<sec:authentication property="principal.bqPageLength" var="bqPageLength" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxChooseDesk},${rfaChooseDesk}] });
});
</script>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<div class="alert alert-notice marg-top-10" id="idTemplateInfo" style="display: none">
					<div class="bg-blue alert-icon">
						<i class="glyph-icon icon-info"></i>
					</div>
					<div class="alert-content">
						<h4 class="alert-title">Info</h4>
						<p id="idTemplateInfoMessage">
							Information message box using the
							<code>.alert-notice</code>
							color scheme. <a title="Link" href="#">Link</a>
						</p>
					</div>
				</div>
				<ol class="breadcrumb">
					<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />

					</a></li>
					<li class="active"><spring:message code="rfi.createrfi.eventcreate" /></li>
				</ol>
				<!-- page title block -->
				<div class="example-box-wrapper wigad-new">
					<div class="rft-creater-heading marg-top-10">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
						<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

						<div class="alert alert-notice marg-top-10" id="idEventInfo" style="display: none">
							<div class="bg-blue alert-icon">
								<i class="glyph-icon icon-info"></i>
							</div>
							<div class="alert-content">
								<h4 class="alert-title">Info</h4>
								<p id="idEventInfoMessage">
									Information message box using the
									<code>.alert-notice</code>
									color scheme. <a title="Link" href="#">Link</a>
								</p>
							</div>
						</div>
						<h3 class="marg-bottom-10">
							<spring:message code="rfi.createrfi.liketocreate" />
							${eventType}?
						</h3>
						<div class="rft-creater-heading-inner example-box-wrapper">
							<div class="row">
								<div class="col-md-12 ">
									<ul class="nav-responsive nav nav-tabs bg-gradient-9">
										<li class="active"><a href="#tab1" class="createEventsTopTabs" data-toggle="tab" id="tabTemplateId"> <img src="${pageContext.request.contextPath}/resources/images/new-tender.png" alt="New Tender" /> <span><spring:message code="application.new" /></span>
										</a></li>
										<c:if test="${(eventType == 'RFT' && eventSettings.rftCopyFromPrevious) || (eventType == 'RFI' && eventSettings.rfiCopyFromPrevious) || (eventType == 'RFP' && eventSettings.rfpCopyFromPrevious) || (eventType == 'RFQ' && eventSettings.rfqCopyFromPrevious) || (eventType == 'RFA' && eventSettings.rfaCopyFromPrevious)}">
											<li>
												<a href="#tab2" class="createEventsTopTabs" data-toggle="tab" id="tabPreviousId"> <img src="${pageContext.request.contextPath}/resources/images/copy-from-privious.png" alt=" Copy Previous" /> <span> <spring:message code="rfa.createrft.copyfromprevious2" /></span>
												</a>
											</li>
										</c:if>
										<div class="pull-right searchTemplatefield">
											<h3 class="marg-template row"">
												<spring:message code="rfi.createrfi.search.fromlist" />
											</h3>
											<div class="row">
												<div class="col-md-4 col-md-4-custom">
													<input name="templateName" id="idTemplateName" placeholder='<spring:message code="prtemplate.name.description.placeholder"/>' type="text" data-validation="alphanumeric length " data-validation-allowing="/ " data-validation-length="max64" class="form-control" />
												</div>
												<div class="col-md-2">
													<button class="searchRftTemplate btn ph_btn_small btn-info hvr-pop hvr-rectangle-out" type="submit">
														<spring:message code="application.search" />
													</button>
												</div>
											</div>
										</div>
										<div class="pull-right searchpreviousfield flagvisibility">
											<h3 class="marg-previous row">
												<spring:message code="rfi.createrfi.serach.previous" />
											</h3>
											<div class="row">
												<div class="col-md-4 col-sm-4 col-lg-4 col-xs-12">
													<input name="eventName" id="idEventName" placeholder='<spring:message code="createrfi.event.id.name"/>' class="form-control" style="width: 100%" />
												</div>
												<div class="col-md-4 col-sm-4 col-lg-4 col-xs-12" style="padding-bottom: 6px;">
													<select name="industryCategory" class="chosen-select" id="chosenCategoryAll">
														<option value="" style="width: 100%"><spring:message code="createrfi.all.categories.placeholder" /></option>
														<c:forEach items="${industryCategory}" var="cate">
															<option value="${cate.id}" label="${cate.code} - ${cate.name}" style="width: 100%">${cate.code}-${cate.name}</option>
														</c:forEach>
													</select>
												</div>
												<div class="col-md-3">
													<button class="searchrftEvent btn ph_btn_small btn-info hvr-pop hvr-rectangle-out" type="submit">
														<spring:message code="application.search" />
													</button>
												</div>
											</div>
										</div>
									</ul>
									<input type="hidden" id="eventTypeSearch" value="${eventType}" name="eventType">
									<div class="tab-content">
										<div class="tab-pane active" id="tab1">
											<div class="list-table-event marg-top-20">
												<div class="col-md-3">
													<c:if test="${eventType == 'RFA' && eventSettings.rfaCreateFromBlank}">
														<div class="previous-box blank-div idRftEventsHgt" data-toggle="modal" data-target="#myModal-auction">
															<a href="#"><spring:message code="rfi.createrfi.from.blank" /></a>
														</div>
													</c:if>
													<c:if test="${(eventType == 'RFT' && eventSettings.rftCreateFromBlank) || (eventType == 'RFI' && eventSettings.rfiCreateFromBlank) || (eventType == 'RFP' && eventSettings.rfpCreateFromBlank) || (eventType == 'RFQ' && eventSettings.rfqCreateFromBlank) }">
														<c:url value="/buyer/createBlankEvent" var="newEvent"></c:url>
														<form action="${ newEvent}" id="createFormBlank" method="post">
															<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
															<input type="hidden" name="eventType" value="${eventType}" />
															
															<div class="previous-box blank-div idRftEventsHgt" id="createBlankSubmit">
																<a href="#"><spring:message code="rfi.createrfi.from.blank" /></a>
															</div>
														</form>
													</c:if>
												</div>
											</div>
											<div>
												<div class="col-md-1-5 col-sm-1 col-xs-1 pull-left marg-right-10" style="padding-left: 0px; padding-right: 0px;">
													<select class="disablesearch chosen-select" name="" data-parsley-id="0644" id="selectPageLen" data-screenType="template">
														<option value="10">10</option>
														<option value="25">25</option>
														<option value="50">50</option>
														<option value="100">100</option>
													</select>
												</div>
												<div class="privious-box-main marg-top-20 pad_all_20 white-bg" id="rftTemplates">
													<div class="row">
														<c:if test="${empty allRfxTemplate}">
															<h4>
																<a style="background: none; border: none;"><spring:message code="application.not.templates" /></a>
															</h4>
														</c:if>
														<c:forEach items="${allRfxTemplate}" var="eventsTemplate">
															<%-- <div class="col-md-3" id="idTemp">
																<div class="previous-box">
																	<a href="#">${eventsTemplate.templateName}</a>
																</div>
															</div> --%>
															<div id="appendTemplate">
																<div class="col-md-3 marg-bottom-10 idRftEvent " id="${eventsTemplate.id}" data-value="${eventsTemplate.id}" style="display: block">
																	<div class="lower-bar-search-contant-main-block" id="test" style="height: auto;">
																		<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10 disp-f">
																			<h4 class="ellip-title" title="${eventsTemplate.templateName}">${eventsTemplate.templateName}</h4>
																		</div>
																		<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-f h-30">
																			<div>
																				<label style="width: 83px;"><spring:message code="eventdescription.description.label"/>:</label>
																			</div>
																			<div> 
																				<span class="green ellip-desc" data-toggle="tooltip" data-original-title="${eventsTemplate.templateDescription}">${eventsTemplate.templateDescription}</span>
																			</div>
																		</div>
																		<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-f">
																			<div>
																				<label style="width: 83px;"><spring:message code="application.createdby" />:</label> 
																			</div>
																			<div>
																				<span class="green ellip-desc">${eventsTemplate.createdBy.name}</span>
																			</div>
																		</div>
																		<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-f">
																			<fmt:formatDate var="createdDate" value="${eventsTemplate.createdDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" />
																			<div> <label style="width: 95px;"><spring:message code="application.createddate" />:</label> </div> 
																			<span class="green ellip-desc">${createdDate}</span>
																		</div>
																		<div class="lower-bar-search-contant-main-contant  pad_all_10">
																			<div>
																				<spring:url value="/buyer/copyFromTemplate" var="copyFromTemplate" htmlEscape="true" />
																				<form action="${copyFromTemplate}" class="col-md-12" method="post" style="float: none !important;">
																					<input type="hidden" id="eventType" value="${eventType}" name="eventType"> <input type="hidden" id="templateId" value="${eventsTemplate.id}" name="templateId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																					<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit" onclick="javascript:$('#loading').show();">
																						<spring:message code="application.use.this.button" />
																					</button>
																				</form>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														</c:forEach>
													</div>
													<div class="pull-right marg-right-10">
														<div aria-label="Page navigation">
															<ul class="pagination" id="pagination"></ul>
														</div>
													</div>
												</div>
											</div>	
										</div>
										<div class="tab-pane" id="tab2">
											<div>
												<div class="col-md-1-5 col-sm-1 col-xs-1 pull-left marg-right-10 marg-top-20" style="padding-left: 0px; padding-right: 0px;">
													<select class="disablesearch chosen-select" name="" data-parsley-id="0644" id="eventSelectPageLen" data-screenType="template">
														<option value="10">10</option>
														<option value="25">25</option>
														<option value="50">50</option>
														<option value="100">100</option>
													</select>
												</div>
												<div class="privious-box-main pad_all_20 white-bg" id="rftEvents">
													<div class="row">
														<c:if test="${empty events}">
															<h4>
																<spring:message code="application.no.past" />
															</h4>
														</c:if>
														<c:forEach items="${events}" var="event">
															<div id="appendEvent">
	
																<div class="col-md-3 marg-bottom-10 idRftEvent idRftEventsHgt1" id="${event.id}" data-value="${event.id}" style="display: block">
																	<div class="lower-bar-search-contant-main-block" id="test" style="height: 331px;">
																		<div class="light-gray-bg pad_all_10 name-between">
																			<div>
																				<h4>${event.eventId}</h4>
																			</div>
																			<div>
																				<h4>${event.status}</h4>
																			</div>
																		</div>
																		<div class="pad-top-side-5 disp-f">
																			<div>
																				<label><spring:message code="eventdetails.event.name" />:</label>
																			</div>
																			<div> 
																				<span class="green ellip-desc" data-toggle="tooltip" data-original-title="${event.eventName}">${event.eventName}</span>
																			</div>
																		</div>
																		<div class="pad-top-side-5 disp-f">
																			<div>
																				<label style="width: 80px;"><spring:message code="eventdetails.event.referencenumber" />:</label>
																			</div>
																			<div>
																				<span class="green ellip-desc" data-toggle="tooltip" data-original-title="${event.referanceNumber}">${event.referanceNumber}</span>
																			</div>																		
																		</div>
																		<div class="pad-top-side-5 disp-f">
																			<div>
																				<label><spring:message code="rfi.createrfi.category" />:</label>
																			</div>
																			<div>
																				<span class="category-ellip"> ${event.industryCategory.name}</span>
																			</div>																
																		</div>
																		<div class="pad-top-side-10">
																			<label><spring:message code="application.startdate" />: </label> <span> <fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${event.eventStart}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																			</span>
																		</div>
																		<div class="pad-top-side-5">
																			<label><spring:message code="rfaevent.end.date" />:</label> <span> <fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${event.eventEnd}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																			</span>
																		</div>
																		<c:if test="${eventType eq 'RFA'}">
																			<div class="pad-top-side-5">
																				<label><spring:message code="rfx.auction.type.label" />:</label> <span> ${event.auctionType}</span>
																			</div>
																		</c:if>
																		<div class="col-md-12 pad_all_10 w-100">
																			<spring:url value="/buyer/copyFrom" var="copyFrom" htmlEscape="true" />
																			<form action="${copyFrom}" class="hover_tooltip-top col-md-12" method="post">
																				<input type="hidden" id="eventType" value="${eventType}" name="eventType"> <input type="hidden" id="eventId" value="${event.id}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																				<button ${ event.templateActive ?  'disabled' : ''} class="btn ${ event.templateActive ?  'btn-black':'btn-info'} btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit" onclick="javascript:$('#loading').show();">
																					<spring:message code="application.use.this.button" />
																				</button>
																				<span class="tooltiptext-top"> ${ event.templateActive ?  'Template  being used is inactive ' : 'Create new Event'}</span>
																			</form>
																		</div>
																	</div>
																</div>
	
															</div>
														</c:forEach>
													</div>
													<div class="pull-right marg-right-10">
														<div aria-label="Page navigation">
															<ul class="pagination" id="eventsPagination"></ul>
														</div>
													</div>
												</div>
											</div>	
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="clear"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="myModal-auction" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<form method="post" action="${pageContext.request.contextPath}/buyer/createBlankEvent">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-content">
				<div class="modal-header">
					<h3>
						<spring:message code="auction.would.like.create" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
				</div>
				<input type="hidden" name="eventType" value="${eventType}">
				<div class="modal-body">
					<div class="auction-body">
						<div class="radio-primary small-radio-btn1">
							<c:forEach items="${auctionTypeList}" var="auctionType" varStatus="loop">
								<label class="col-md-8"> <input type="radio" id="inlineRadio110" name="auctionType" ${loop.index == 0  ? 'checked':''} value="${auctionType}" class="custom-radio"> ${auctionType.value}
								</label>
							</c:forEach>
						</div>
					</div>
				</div>
				<div class="modal-footer border-none float-left width-100 pad-top-0 ">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 " onclick="javascript:$('#loading').show();">
						<spring:message code="application.next" />
					</button>
				</div>
			</div>
		</form>
	</div>
</div>



<c:if test="${not empty openModelBu}">
	<div class="modal fade" id="myModal-copy" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<form action="${pageContext.request.contextPath}/buyer/copyFrom" method="post">
				<div class="modal-content" style="width: 100%; float: left;">
					<div class="modal-header">
						<h3>
							<spring:message code="business.unit.would.like" />
						</h3>
						<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
					</div>

					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="modal-body">
						<input type="hidden" name="eventId" value="${eventId}"> <input type="hidden" name="eventType" value="${eventType}"> <select name="businessUnitId" class="chosen-select disablesearch">
							<c:forEach items="${businessUnits}" var="businessUnit">
								<option value="${businessUnit.id}">${businessUnit.unitName}</option>
							</c:forEach>
						</select>

					</div>
					<div class="modal-footer border-none float-left width-100 pad-top-0 ">
						<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" onclick="javascript:$('#loading').show();">
							<spring:message code="application.next" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</c:if>
<c:if test="${not empty openModelForTemplateBu}">
	<div class="modal fade" id="myModal-template" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<form action="${pageContext.request.contextPath}/buyer/copyFromTemplate" method="post">
				<div class="modal-content" style="width: 100%; float: left;">
					<div class="modal-header">
						<h3>
							<spring:message code="eventsummary.which.businessunit" />
						</h3>
						<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
					</div>

					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="modal-body">

						<input type="hidden" name="templateId" value="${templateId}"> <input type="hidden" name="eventType" value="${eventType}"> <select name="businessUnitId" class="chosen-select disablesearch">
							<c:forEach items="${businessUnits}" var="businessUnit">
								<option value="${businessUnit.id}">${businessUnit.unitName}</option>
							</c:forEach>
						</select>

					</div>
					<div class="modal-footer border-none float-left width-100 pad-top-0 ">
						<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 " onclick="javascript:$('#loading').show();">
							<spring:message code="application.next" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</c:if>


<%-- <div class="modal fade" id="myModal-blank" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<form action="${pageContext.request.contextPath}/buyer/${eventType}/createRfaEvent" method="post" >
			<div class="modal-content" style="width: 100%; float: left;">
				<div class="modal-header">
					<h3>Which Business Unit would you like to Use For This Template?</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
				</div>

				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<input type="hidden" name="auctionType" value="${auctionType}"> 
						<input type="hidden" name="eventType" value="${eventType}">
						
						
						<select name="businessUnitId" class="chosen-select disablesearch" >
							<c:forEach items="${businessUnits}" var="businessUnit">
								<option value="${businessUnit.id}">${businessUnit.unitName}</option>
						</c:forEach>
						</select>

				</div>
				<div class="modal-footer border-none float-left width-100 pad-top-0 ">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 "  >Next</button>
				</div>
			</div>
		</form>
	</div>
</div>
 --%>


<div class="modal fade" id="myModal-blank" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<c:set var="createBlankUrl" value="" />
		<c:choose>
			<c:when test="${eventType eq 'RFT'}">
				<c:set var="createBlankUrl" value="${pageContext.request.contextPath}/buyer/${eventType}/createRftEvent" />
			</c:when>
			<c:when test="${eventType eq 'RFA'}">
				<c:set var="createBlankUrl" value="${pageContext.request.contextPath}/buyer/${eventType}/createRfaEvent" />
			</c:when>

			<c:when test="${eventType eq 'RFP'}">
				<c:set var="createBlankUrl" value="${pageContext.request.contextPath}/buyer/${eventType}/createRfpEvent" />
			</c:when>

			<c:when test="${eventType eq 'RFI'}">
				<c:set var="createBlankUrl" value="${pageContext.request.contextPath}/buyer/${eventType}/createRfiEvent" />
			</c:when>

			<c:when test="${eventType eq 'RFQ'}">
				<c:set var="createBlankUrl" value="${pageContext.request.contextPath}/buyer/${eventType}/createRfqEvent" />
			</c:when>


		</c:choose>



		<form action="${createBlankUrl}" method="post">
			<div class="modal-content" style="width: 100%; float: left;">
				<div class="modal-header">
					<h3>
						<spring:message code="eventsummary.which.businessunit" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
				</div>

				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<input type="hidden" name="auctionType" value="${auctionType}"> <input type="hidden" name="eventType" value="${eventType}"> <select name="businessUnitId" class="chosen-select disablesearch">
						<c:forEach items="${businessUnits}" var="businessUnit">
							<option value="${businessUnit.id}">${businessUnit.unitName}</option>
						</c:forEach>
					</select>

				</div>
				<div class="modal-footer border-none float-left width-100 pad-top-0 ">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 " onclick="javascript:$('#loading').show();">
						<spring:message code="application.next" />
					</button>
				</div>
				
			</div>
		</form>
	</div>
</div>



<style>
.w-100 {
	width: 100%;
}
.h-30 .tooltip-inner { 
    word-break: break-all;
}
.ellip-title {
	white-space: nowrap !important;
	width: 315px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
}

.idRftEventsHgt>div>div:last-child {
	position: absolute;
	bottom: 0px;
}

.idRftEventsHgt1>div>div:last-child {
	position: absolute;
	bottom: 0px;
}

.disp-f {
	display: flex;
}
.h-30 {
	height: 30px;
}
#rftEvents {
	min-height: 350px;
}

.marg-template {
	margin-bottom: 8px;
	margin-top: 10px;
	margin-left: 0px;
	margin-right: 75px;
	color: white !important;
}

.marg-previous {
	margin-bottom: 8px;
	margin-top: 10px;
	margin-left: 0px;
	margin-right: 300px;
	color: white !important;
}

.col-md-4.col-md-4-custom {
	width: 68.333333%;
}

.col-md-3.col-md-3-custom {
	width: 27%;
}

@media only screen and (max-width: 1420px) {
	.searchpreviousfield, .searchTemplatefield {
		float: left !important;
		margin-left: 10px;
	}
}

.ellip-desc {
	white-space: nowrap !important;
	width: 215px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
	cursor: pointer;
}

@media only screen and (max-width: 1677px) {
	.ellip-desc {		
		width: 175px;
	}
}
@media only screen and (max-width: 1500px) {
	.ellip-desc {		
		width: 125px;
	}
}
@media only screen and (max-width: 1011px) {
	.ellip-desc {		
		width: 90px;
	}
}

.event-ellip {
	white-space: nowrap;
	width: 215px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
}
@media only screen and (max-width: 1677px) {
	.event-ellip {
		width: 175px;
	}
}
@media only screen and (max-width: 1500px) {
	.event-ellip {
		width: 125px;
	}
}
@media only screen and (max-width: 1011px) {
	.event-ellip {
		width: 90px;
	}
}

.category-ellip {
	white-space: nowrap;
	width: 245px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
}
@media only screen and (max-width: 1677px) {
	.category-ellip {
		width: 175px;
	}
}
@media only screen and (max-width: 1500px) {
	.category-ellip {
		width: 125px;
	}
}
@media only screen and (max-width: 1011px) {
	.category-ellip {
		width: 90px;
	}
}

.ref-ellip {
	white-space: nowrap;
	width: 160px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
}
.w-95 {
	width: 95px;
}
@media (max-width: 1366px) and (min-width: 768px) {
	.ref-ellip {
		width: 130px !important;
	}
}
.hover_tooltip-top .tooltiptext-top {
	visibility: hidden;
	width: 120px;
	background-color: #263a4a;
	color: #f6d532;
	text-align: center;
	border-radius: 3px;
	position: absolute;
	z-index: 9999;
	bottom: 140%;
	left: 50%;
	margin-left: -60px;
	padding: 8px;
	font-size: 10px;
	white-space: normal;
	text-transform: capitalize;
	color: #f6d532;
}

.hover_tooltip-top .tooltiptext-top::after {
	content: "";
	position: absolute;
	top: 100%;
	left: 50%;
	margin-left: -5px;
	border-width: 5px;
	border-style: solid;
	border-color: #263a4a transparent transparent;
}

.hover_tooltip-top:hover .tooltiptext-top {
	visibility: visible;
}
.tooltip { 
    left: 80px !important;
}
.readOnlyClass {
	pointer-events: none;
	!
	important;
}

.name-between {
	display: flex;
	justify-content: space-between;
}
.radio-primary .radio span.checked {
    background: #0cb6ff !important;
    border-color: #c7c7c7;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/view/createRft.js?2"/>"></script>
<script type="text/javascript">
	$("#test-select ").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.twbsPagination.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date,sanitize'
	});
	
	$(document).ready(function() {
		$('body').tooltip({
		    selector: '[data-toggle="tooltip"]'
		});
	});
		
	$(document).ready(function() {
	var pagForEvent=false;
	var pagForTemplate=true;

	var pageNoForEvent = 1;
	var pageNoForTemplate=1;

	$('#tabTemplateId').click(function (){
		pagForEvent=false;
		pagForTemplate=true;
	});

	$('#tabPreviousId').click(function (){
		pagForEvent=true;
		pagForTemplate=false;
	});

	$(".searchrftEventTT").click(function(){
	pageNoForEvent = 0;
	var searchValue = $('#searchValue').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var data={};
	var searchValue1 = $('#idEventName').val();
	var industryCategory1 = $('#chosenCategoryAll').val();
	if(pagForEvent){
		$.ajax({
			type : "POST",
			url :  getContextPath()+"/buyer/paginationForEvents/"+eventType+"/"+pageNoForEvent,
			data : {
				"searchValue" : searchValue1,
				"industryCategory" : industryCategory1
			},
			dataType : "json",
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				$('#rftEvents > .row').html("");
				var isEventAvailable=false;
				for(i in data){
					isEventAvailable=true;
					var isTEmplateActive = (data[i].templateActive)?"disabled":"";
					var isTemplateActiveClass=(data[i].templateActive)?"btn btn-black btn-block hvr-pop hvr-rectangle-out":"btn btn-info btn-block hvr-pop hvr-rectangle-out"
					var tooltipClass = (data[i].templateActive ? " Template being used is inactive " : "Create new Event" )  
					
					var html="<div id=\"appendEvent\">";
					
					html+= '<div class="col-md-3 marg-bottom-10 idRftEvent idRftEventsHgt1" id="' + data[i].id+ '" data-value="' + data[i].id + '" style="display: block;">'
						html+='<div class="lower-bar-search-contant-main-block" id="test" style="min-height: 331px;">';
						html+='<div class="light-gray-bg pad_all_10 name-between">'
						html+='<div><h4>'+data[i].eventId+'</h4></div>'
						html+='<div><h4>' +data[i].status+ '</h4></div>'
						html+='</div>'
						html+='<div class="pad-top-side-5 disp-f">'
						html+='<label><spring:message code="eventdetails.event.name" />:</label> <span class="green event-ellip">'+(data[i].eventName ? data[i].eventName : "" )+'</span>'
						html+='</div>'
						html+='<div class="pad-top-side-5 disp-f">'
						html+='<label><spring:message code="eventdetails.event.referencenumber"/>:</label> <span class="green ref-ellip">'+(data[i].referanceNumber ? data[i].referanceNumber : " " )+'</span>';
						html+='</div>'
						html+='<div class="pad-top-side-5 disp-f">'
						html+='<label><spring:message code="rfi.createrfi.category" />:</label> <span class="category-ellip">'+ (data[i].industryCategory ? data[i].industryCategory : "")+'</span>'
						html+='</div>'
						html+='<div class="pad-top-side-10">';
						html+='<label><spring:message code="application.startdate" />: </label> <span>  '+ (data[i].eventStart ? data[i].eventStart : "")   +'';
						html+='</span></div>';
						html+='<div class="pad-top-side-5">';
						html+='<label><spring:message code="rfaevent.end.date" />:</label> <span>  '+ ( data[i].eventEnd  ? data[i].eventEnd : "") +''
						html+='</span></div>';
						if(eventType=="RFA"){
							html+= '<div class="pad-top-side-5 disp-f"><div><label class="w-95"><spring:message code="rfx.auction.type.label" />:</label></div> <div class="category-ellip">'+data[i].auctionType+'</div></div>'						}
						html+='<div class="col-md-12 pad_all_10">'
						html+='<spring:url value="/buyer/copyFrom" var="copyFrom" htmlEscape="true" />'
						html+='<form action="${copyFrom}" class="hover_tooltip-top col-md-12" method="post">'	
						html+='<input type="hidden" id="eventType" value="'+eventType+'" name="eventType"> <input type="hidden" id="eventId" value="'+data[i].id+'" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />'
						html+='<button ' +isTEmplateActive +' class="'+isTemplateActiveClass+'" style="width: 100%" type="submit">'
						html+='<spring:message code="application.use.this.button" />'
						html+='</button>'
						html+='<span class="tooltiptext-top">'+ tooltipClass+ '</span>';
						html+='</form>'
						html+='</div></div></div>';
						html+='</div>';
						$('#rftEvents > .row').append(html);
				}
				if(isEventAvailable){
					pageNoForEvent=1;
				}
				
			},
			error : function(request, textStatus, errorThrown) {
																
			}
		});
	}
		
	});
	
	
	$( "#chosenCategoryAllll" ).change(function() {
		return;
		pageNoForEvent = 0;
		var searchValue = $('#searchValue').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var data={};
		var searchValue1 = $('#idEventName').val();
		var industryCategory1 = $('#chosenCategoryAll').val();
		if(pagForEvent){
			$.ajax({
				type : "POST",
				url :  getContextPath()+"/buyer/paginationForEvents/"+eventType+"/"+pageNoForEvent,
				data : {
					"searchValue" : searchValue1,
					"industryCategory" : industryCategory1
				},
				dataType : "json",
				beforeSend : function(xhr) {
					$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				complete : function() {
					$('#loading').hide();
				},
				success : function(data) {
					
					var isEventAvailable=false;
					$('#rftEvents > .row').html("");
					for(i in data){
						isEventAvailable = true;
						var isTEmplateActive = (data[i].templateActive)?"disabled":"";
						var isTemplateActiveClass=(data[i].templateActive)?"btn btn-black btn-block hvr-pop hvr-rectangle-out":"btn btn-info btn-block hvr-pop hvr-rectangle-out"
						var tooltipClass = (data[i].templateActive ? " Template being used is inactive " : "Create new Event" )  
						
						var html="<div id=\"appendEvent\">";
						
						html+= '<div class="col-md-3 marg-bottom-10 idRftEvent idRftEventsHgt1" id="' + data[i].id+ '" data-value="' + data[i].id + '" style="display: block;">'
							html+='<div class="lower-bar-search-contant-main-block" id="test" style="min-height: 331px;">';
							html+='<div class="light-gray-bg pad_all_10 name-between">'
							html+='<div><h4>'+data[i].eventId+'</h4></div>'
							html+='<div><h4>' +data[i].status+ '</h4></div>'
							html+='</div>'
							html+='<div class="pad-top-side-5 disp-f">'
							html+='<label><spring:message code="eventdetails.event.name" />:</label> <span class="green event-ellip">'+(data[i].eventName ? data[i].eventName : "" )+'</span>'
							html+='</div>'
							html+='<div class="pad-top-side-5 disp-f">'
							html+='<label><spring:message code="eventdetails.event.referencenumber"/>:</label> <span class="green ref-ellip">'+(data[i].referanceNumber ? data[i].referanceNumber : " " )+'</span>';
							html+='</div>'
							html+='<div class="pad-top-side-5 disp-f">'
							html+='<label><spring:message code="rfi.createrfi.category" />:</label> <span class="category-ellip">'+ (data[i].industryCategory ? data[i].industryCategory : "")+'</span>'
							html+='</div>'
							html+='<div class="pad-top-side-10">';
							html+='<label><spring:message code="application.startdate" />: </label> <span>  '+ (data[i].eventStart ? data[i].eventStart : "")   +'';
							html+='</span></div>';
							html+='<div class="pad-top-side-5">';
							html+='<label><spring:message code="rfaevent.end.date" />:</label> <span>  '+ ( data[i].eventEnd  ? data[i].eventEnd : "") +''
							html+='</span></div>';
							if(eventType=="RFA"){
								html+= '<div class="pad-top-side-5 disp-f"><div><label class="w-95"><spring:message code="rfx.auction.type.label" />:</label></div> <div class="category-ellip">'+data[i].auctionType+'</div></div>'							}
							html+='<div class="col-md-12 pad_all_10">'
							html+='<spring:url value="/buyer/copyFrom" var="copyFrom" htmlEscape="true" />'
							html+='<form action="${copyFrom}" class="hover_tooltip-top col-md-12" method="post">'	
							html+='<input type="hidden" id="eventType" value="'+eventType+'" name="eventType"> <input type="hidden" id="eventId" value="'+data[i].id+'" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />'
							html+='<button ' +isTEmplateActive +' class="'+isTemplateActiveClass+'" style="width: 100%" type="submit">'
							html+='<spring:message code="application.use.this.button" />'
							html+='</button>'
							html+='<span class="tooltiptext-top">'+ tooltipClass+ '</span>';
							html+='</form>'
							html+='</div></div></div>';
							html+='</div>';
							$('#rftEvents > .row').append(html);
					}
					if(isEventAvailable){
						pageNoForEvent=1;	
					}
					
					
				},
				error : function(request, textStatus, errorThrown) {
																	
				}
			});
		}
		
		});



	var eventType=$('#eventTypeSearch').val();
		<c:if test="${not empty openModelBu}">
		$('#myModal-copy').modal();
		</c:if>
		
		<c:if test="${not empty openModelForTemplateBu}">
		$('#myModal-template').modal();
		</c:if>
		
		<c:if test="${not empty modelBuForBlank}">
		
		$('#myModal-blank').modal();
		</c:if>
		
		
		
		var totalPage = 500;
		var visiblePage = 5;
		var templatesCount = '${rfxTemplateCount}';
		var bqPageLength = $('#bqPageLength').val();
		var startPageLen = $('#selectPageLen').val();
		if( bqPageLength === undefined || bqPageLength === ''){
			bqPageLength = 50;
		}
		console.log("templatesCount :" + templatesCount + "== bqPageLength :" + bqPageLength+ "=== 	templatesCount/startPageLen :" + templatesCount/startPageLen+" startPageLen: "+startPageLen);

		totalPage = Math.ceil(templatesCount/startPageLen);

		if(totalPage == 0 ||  totalPage === undefined || totalPage === ''){
			totalPage = 1;
		}

		if(totalPage < 5){
			visiblePage = totalPage;
		}
		
		$(function (e) {
		    $('#pagination').twbsPagination({
		        totalPages: totalPage,
		        visiblePages: visiblePage
		    });
		});
		
		var totalRequestPage = 500;
		var visibleRequestPage = 5;
		var prCount = '${rfxCount}';
		var eventPageLen = $('#eventSelectPageLen').val();

		console.log("requestCount :" + prCount + "== bqPageLength :" + bqPageLength+ "=== 	prCount/eventPageLen :" + prCount/eventPageLen);

		totalRequestPage = Math.ceil(prCount/eventPageLen);

		if(totalRequestPage == 0 ||  totalRequestPage === undefined || totalRequestPage === ''){
			totalRequestPage = 1;
		}

		if(totalRequestPage < 5){
			visibleRequestPage = totalRequestPage;
		}
		
		$(function (e) {
		    $('#eventsPagination').twbsPagination({
		        totalPages: totalRequestPage,
		        visiblePages: visibleRequestPage
		    });
		});
		
		 $(document).delegate('.page-link', 'click', function(e) {
		 	 e.preventDefault();
			 if(pagForTemplate){
			 	  var searchVal = $('#idTemplateName').val();
		  		  var page = $('#pagination').find('li.active').text();
		          var pageNo = parseInt(page);
		  		  var pageLen = "20";
		          if ($('#selectPageLen option:selected').text()) {
		          	  pageLen = $('#selectPageLen').val();
		  		  }
		  		  var pageLength = parseInt(pageLen);
		  		  console.log("@@@@@@@@@@@@@@@@@@@@ searchVal "+searchVal);
	
		  		rfxTemplatePagination(searchVal ,pageNo ,pageLength);
				 
			 }else {
				 console.log("********************* previous");
				 var searchVal = $('#idEventName').val();
		  		  var page = $('#eventsPagination').find('li.active').text();
		          var pageNo = parseInt(page);
		  		  var pageLen = "20";
		          if ($('#eventSelectPageLen option:selected').text()) {
		          	  pageLen = $('#eventSelectPageLen').val();
		  		  }
		  		  var pageLength = parseInt(pageLen);
		  		  console.log("Previous @@@@@@@@@@@ searchVal: "+searchVal+" @@@@@@@@@@@ pageNo: "+pageNo+" @@@@@@@@@@@ pageLength: "+pageLength);
	
		  		  rfxEventPagination(searchVal ,pageNo ,pageLength);
			 }
			
		});
		 
		 $('.searchRftTemplate').click(function(event) {
			  console.log("click click >>>");
			  event.preventDefault();
			  var pgNo = "1";
		 	  var searchVal = $('#idTemplateName').val();
	          var pageNo = parseInt(pgNo);
	  		  var pageLen = "20";
	          if ($('#selectPageLen option:selected').text()) {
	          	  pageLen = $('#selectPageLen').val();
	  		  }
	  		  var pageLength = parseInt(pageLen);

	  		  console.log("################# click searchVal: "+searchVal+" #################pageNo: "+pageNo);
	  		  rfxTemplatePagination(searchVal ,pageNo ,pageLength);
		 });
		 
		 $('#selectPageLen').on('change', function() {
			  console.log("change template >>>");
			  var value=this.value;
					
		      var searchVal = $('#idTemplateName').val();
			  var page = "1";
			  var pageNo = parseInt(page);
			  var pageLen = "20";
		      pageLen = value;
			  		
		      var pageLength = parseInt(pageLen);
			  console.log("+++++++++++++++++++ pageLength "+pageLength);
	
			  rfxTemplatePagination(searchVal ,pageNo ,pageLength);
		 });
		
		 $('.searchrftEvent').click(function(event) {
			  console.log("click click searchrftEvent >>>");
			  event.preventDefault();
			  var pgNo = "1";
		 	  var searchVal = $('#idEventName').val();
	          var pageNo = parseInt(pgNo);
	  		  var pageLen = "20";
	          if ($('#eventSelectPageLen option:selected').text()) {
	          	  pageLen = $('#eventSelectPageLen').val();
	  		  }
	  		  var pageLength = parseInt(pageLen);

	  		  console.log("################# click searchVal: "+searchVal+" #################pageNo: "+pageNo);
	  		  rfxEventPagination(searchVal ,pageNo ,pageLength);
		 });
		 
		 $('#eventSelectPageLen').on('change', function() {
			   console.log("change request >>>");
			   var value=this.value;
				var searchVal = $('#idEventName').val();
			  	var page = "1";
			    var pageNo = parseInt(page);
			  	var pageLen = "20";
		        pageLen = value;
			  		
		        var pageLength = parseInt(pageLen);
			  	console.log("+++++++++++++++ pageLength "+pageLength +" ########## click searchVal: "+searchVal+" #################pageNo: "+pageNo);
		
			  	rfxEventPagination(searchVal ,pageNo ,pageLength);
		 });
		 
		 $( "#chosenCategoryAll" ).change(function() {
				 console.log("change chosenCategoryAll >>>");
				if(pagForEvent){
					var searchValue = $('#idEventName').val();
					var industryCategory1 = $('#chosenCategoryAll').val();
					var pgNo = "1";
			        var pageNo = parseInt(pgNo);
			  		var pageLen = "10";
			        if ($('#eventSelectPageLen option:selected').text()) {
			          	  pageLen = $('#eventSelectPageLen').val();
			  		}
			  		var pageLength = parseInt(pageLen);
		
			  		rfxEventPagination(searchValue ,pageNo ,pageLength);
					
				}
			});
		
		 function rfxTemplatePagination(searchVal, pageNo, pageLength) {
			 	console.log("PR Template Pagination ....... ");
			 	var header = $("meta[name='_csrf_header']").attr("content");
			 	var token = $("meta[name='_csrf']").attr("content");
			 	var eventType = $('#eventTypeSearch').val();
			 	
			 		$.ajax({
			 			type : "POST",
			 			url : getContextPath() + "/buyer/rfxTemplatePagination",
			 			data : {
			 				'searchValue' : searchVal,
			 				'pageNo' : pageNo,
			 				'pageLength' : pageLength,
			 				 'eventType' : eventType
			 			},
			 			dataType : "json",
			 			beforeSend : function(xhr) {
			 				$('#loading').show();
			 				xhr.setRequestHeader(header, token);
			 				xhr.setRequestHeader("Accept", "application/json");
			 			},
			 			success : function(data) {
			 				// console.log(data.length);
			 				var html = '';
			 				$('.custom-checkbox.checksubcheckbox').uniform();
			 				$('.checker span').find('.glyph-icon.icon-check').remove();
			 				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			 				$("#idTemplateInfoMessage").html('');
							$("#idTemplateInfo").hide();
			 				$('#loading').hide();
			 				
			 				var found = false;
			 				$.each(data.data, function(key, value) {
			 					found = true;
								html += '<div class="col-md-3 marg-bottom-10 idRftEvent " id="' + value.id + '" data-value="' + value.id + '" style="display: block">'
										+ '<div class="lower-bar-search-contant-main-block" id="test" style="height: auto;">'
										+ '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10 disp-f">'
										+ '<h4 class="ellip-title" title="' + value.templateName + '">' + value.templateName + '</h4>'
										+ '</div>'
										+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-f h-30">'
										+ '<div><label style="width: 83px;">Description:</label></div>'
										+ '<div><span data-toggle="tooltip" data-original-title=" ' + value.templateDescription + ' " class="green ellip-desc">' + ( value.templateDescription == undefined ? '' : value.templateDescription ) + '</span></div>'
										+ '</div>' 
										+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5">'
										+ '<label>Created By:</label> <span class="green">' + (value.createdBy ? value.createdBy.name : '') + '</span></div>' + '<div class="lower-bar-search-contant-main-contant pad-top-side-5">'
										+ '<label>Created Date:</label> <span class="green">' + value.createdDate + '</span> </div>' + '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>' + '<form action="' + getContextPath()
										+ '/buyer/copyFromTemplate" class="col-md-12" method="post" style="float: right;">' + '<input type="hidden" id="eventType" value="' + eventType + '" name="eventType"> <input type="hidden" id="templateId" value="'
										+ value.id + '" name="templateId">' + '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">'
										+ '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>' + '</form></div>';
  								   html += '</div></div></div>';
							});
			 				
							$('#rftTemplates > .row').html(html);
							priviousTemplatesBlocksEvent();
							if (!found) {
// 								$("#idTemplateInfoMessage").html('No matching data found');
// 								$("#idTemplateInfo").show();

								$('#rftTemplates > .row').html('No matching data found');
							}
			 				
			 			// set pagination according to result
							visiblePage = 5;
							var countOfTemplates = data.recordsTotal;
							
							console.log(".........pageLength "+pageLength+"......totalPage "+totalPage+"  >>>>>>countOfTemplates "+countOfTemplates);
							totalPage = Math.ceil(countOfTemplates / pageLength);
							if (totalPage == 0) {
								totalPage = 1;
							}

							if (totalPage < 5) {
								visiblePage = totalPage;
							}
							var opts = {
								totalPages : 500,
								visiblePages : 5
							};

							console.log(">>>>>>>>>>>totalPage "+totalPage+">>>>>>countOfTemplates "+countOfTemplates);
							
							$('#pagination').twbsPagination('destroy');
							$('#pagination').twbsPagination($.extend(opts, {
								totalPages : totalPage,
								visiblePages : visiblePage
							}));
							var pagination = jQuery('#pagination').data('twbsPagination');
							pagination.show(pageNo);
							
			 			},
			 			error : function(request, textStatus, errorThrown) {
			 				console.log("error");
			 				$('p[id=idGlobalErrorMessage]').html(request.getRespongth, pageNo);
			 				$('div[id=idGlobalError]').show();
			 				$('#loading').hide();
			 			},
			 			complete : function() {
			 				$('#loading').hide();
			 			}
			 		});
			 }
		 
		 function rfxEventPagination(searchVal, pageNo, pageLength) {
			 	console.log("Events Pagination ....... ");
			 	var header = $("meta[name='_csrf_header']").attr("content");
			 	var token = $("meta[name='_csrf']").attr("content");
			 	var eventType = $('#eventTypeSearch').val();
			 	var industryCategory = $('#chosenCategoryAll').val();
			 	
			 		$.ajax({
			 			type : "POST",
			 			url : getContextPath() + "/buyer/rfxEventPagination",
			 			data : {
			 				'searchValue' : searchVal,
			 				'pageNo' : pageNo,
			 				'pageLength' : pageLength,
			 				'eventType' : eventType,
			 				'industryCategory' : industryCategory
			 			},
			 			dataType : "json",
			 			beforeSend : function(xhr) {
			 				$('#loading').show();
			 				xhr.setRequestHeader(header, token);
			 				xhr.setRequestHeader("Accept", "application/json");
			 			},
			 			success : function(data) {
			 				// console.log(data.length);
			 				var html = '';
			 				$('.custom-checkbox.checksubcheckbox').uniform();
			 				$('.checker span').find('.glyph-icon.icon-check').remove();
			 				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			 				$("#idTemplateInfoMessage").html('');
							$("#idTemplateInfo").hide();
			 				$('#loading').hide();
			 				
			 				var found = false;
			 				$.each(data.data, function(key, value) {
			 					found = true;
								html += '<div class="col-md-3 marg-bottom-10 idRftEvent " id="' + value.id + '" data-value="' + value.id + '" style="display: block">' 
										+ '<div class="lower-bar-search-contant-main-block" id="test" style="min-height: 331px;">'
										+ '<div class="light-gray-bg pad_all_10 name-between">' 
										+ '<div>'
											+ '<h4>' + (value.eventId ? value.eventId : '')
											+ '</h4>'
										+ '</div>'
										+ '<div><h4>' + (value.status ? value.status : '') + '</h4></div>' 
										+ '</div>'
										+ '<div class="pad-top-side-5 disp-f">' 
										+ '<div><label>Event Name:</label></div>'
										+ '<div> <span class="green ellip-desc" data-toggle="tooltip" data-original-title="' + (value.eventName ? value.eventName : '') + '">' + (value.eventName ? value.eventName : '') + '</span></div> </div>'
										+ '<div class="pad-top-side-5 disp-f">' 
										+ '<div><label style="width: 80px;">Reference Number:</label></div>'
										+ '<div><span class="green ellip-desc" data-toggle="tooltip" data-original-title="' + (value.referanceNumber ? value.referanceNumber : '') + '">' + (value.referanceNumber ? value.referanceNumber : '') + '</span> </div></div>'
										+ '<div class="pad-top-side-5 disp-f">'
										+ '<label>Category:</label> <span class="category-ellip"> ' + (value.industryCategory ? value.industryCategory.name : '') + '</span></div>'
										+ '<div class="pad-top-side-10">'
										+ '<label>Start Date: </label> <span>' + (value.eventStart ? value.eventStart : '') + '</span></div>'
										+ '<div class="pad-top-side-5">' 
										+ '<label>End Date:</label> <span>' + (value.eventEnd ? value.eventEnd : '') + '</span></div>'
								if (eventType == "RFA") {
									html += '<div class="pad-top-side-5">'
									html += '<label>Auction Type:</label> <span >' + (value.rfaAuctionType ? value.rfaAuctionType : '') + '</span> </div>'
								}
								html +='<div class="lower-bar-search-contant-main-contant  pad_all_10" style="position: absolute; bottom: 0;"><div>'
										+ '<form action="' + getContextPath() + '/buyer/copyFrom" class="col-md-12" method="post" style="float: right;">'
										+ '<input type="hidden" id="eventType" value="' + eventType + '" name="eventType">' 
										+ '<input type="hidden" id="eventId" value="' + value.id + '" name="eventId">' + '<input type="hidden" id="_csrf" value="'
										+ token + '" name="_csrf">' + '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>' 
										+ '</form>' + '</div></div></div></div>';
							});

			 				$('#rftEvents').show();
							$('#rftEvents > .row').html(html);
							priviousTemplatesBlocksEvent();
							if (!found) {
// 								$("#idEventInfoMessage").html('No matching data found');
// 								$("#idEventInfo").show();
// 								$('#rftEvents').hide();

								$('#rftEvents').show();
								$('#rftEvents > .row').html('No matching data found');

							}
							
			 			// set pagination according to result
							visiblePage = 5;
							var countOfTemplates = data.recordsTotal;
							
							console.log(".........pageLength "+pageLength+"......totalPage "+totalPage+"  >>>>>>countOfTemplates "+countOfTemplates);
							totalPage = Math.ceil(countOfTemplates / pageLength);
							if (totalPage == 0) {
								totalPage = 1;
							}

							if (totalPage < 5) {
								visiblePage = totalPage;
							}
							var opts = {
								totalPages : 500,
								visiblePages : 5
							};

							console.log(">>>>>>>>>>>totalPage "+totalPage+">>>>>>countOfTemplates "+countOfTemplates);
							
							$('#eventsPagination').twbsPagination('destroy');
							$('#eventsPagination').twbsPagination($.extend(opts, {
								totalPages : totalPage,
								visiblePages : visiblePage
							}));
							var pagination = jQuery('#eventsPagination').data('twbsPagination');
							console.log(">>>>>>>>>>>pageNo "+pageNo);
							pagination.show(pageNo);
							
			 			},
			 			error : function(request, textStatus, errorThrown) {
			 				console.log("error");
			 				$('p[id=idGlobalErrorMessage]').html(request.getRespongth, pageNo);
			 				$('div[id=idGlobalError]').show();
			 				$('#loading').hide();
			 			},
			 			complete : function() {
			 				$('#loading').hide();
			 			}
			 		});
			 }
		
		
		
		
	});
		
	
	$("#createBlankSubmit").click(function() {
  		$('#createBlankSubmit').addClass("readOnlyClass");
		$("#createFormBlank").submit();
	});
	
	
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
