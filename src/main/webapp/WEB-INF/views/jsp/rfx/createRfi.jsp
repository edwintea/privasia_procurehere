<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<title><spring:message code="rftenvelop.title" /></title>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li>
						<a href="${pageContext.request.contextPath}/buyer/buyerDashboard">
							<spring:message code="application.dashboard" />
						</a>
					</li>
					<li class="active"><spring:message code="rfi.createrfi.eventcreate"/></li>
				</ol>
				<jsp:include page="eventHeader.jsp"></jsp:include>
				<!-- page title block -->
				<div class="example-box-wrapper wigad-new">
					<div class="rft-creater-heading marg-top-10">
						<h3 class="marg-bottom-10"><spring:message code="rfi.would.like.create"/></h3>
						<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
						<div class="rft-creater-heading-inner example-box-wrapper">
							<div class="row">
								<div class="col-md-12 ">
									<ul class="nav-responsive nav nav-tabs bg-gradient-9">
										<li class="active">
											<a href="#tab1" data-toggle="tab">
												<img src="${pageContext.request.contextPath}/resources/images/new-tender.png" alt="New Tender" /> <span><span><spring:message code="application.new"/></span>
											</a>
										</li>
										<li>
											<a href="#tab2" data-toggle="tab">
												<img src="${pageContext.request.contextPath}/resources/images/copy-from-privious.png" alt=" Copy Previous" /><span> <spring:message code="rfa.createrft.copyfromprevious" /></span>
											</a>
										</li>
									</ul>
									<div class="tab-content">
										<div class="tab-pane active" id="tab1">
											<div class="list-table-event marg-top-20">
												<h3 class="marg-bottom-20"><spring:message code="rfa.createrft.searchmessage"/></h3>
												<div class="alert alert-notice" id="idTemplateInfo" style="display: none">
													<div class="bg-blue alert-icon">
														<i class="glyph-icon icon-info"></i>
													</div>
													<div class="alert-content">
														<h4 class="alert-title">Info</h4>
														<p id="idTemplateInfoMessage">
															Information message box using the
															<code>.alert-notice</code>
															color scheme.
															<a title="Link" href="#">Link</a>
														</p>
													</div>
												</div>
												<div class="row">
													<div class="col-md-3">
														<input name="templateName" id="idTemplateName" placeholder='<spring:message code="rfi.template.name.placeholder"/>' type="text" data-validation="alphanumeric length " data-validation-allowing="/ " data-validation-length="max64" class="form-control" />
													</div>
												</div>
												<div class="row">
													<div class="col-md-2 marg-top-20">
														<button class="searchRftTemplate btn ph_btn_small btn-info hvr-pop hvr-rectangle-out" type="submit"><spring:message code="application.search"/></button>
													</div>
												</div>
											</div>
											<div class="privious-box-main marg-top-20 pad_all_20 white-bg" id="rftTemplates">
												<div class="row">
													<div class="col-md-3">
														<div class="previous-box blank-div">
															<c:url value="createRfiEvent" var="newEvent"></c:url>
															<a href="${newEvent}"><spring:message code="rfi.blank"/></a>
														</div>
													</div>
													<c:if test="${empty allRfxTemplate}">
														<h4>
															<a style="background: none; border: none;"><spring:message code="application.no.records.found"/></a>
														</h4>
													</c:if>
													<c:forEach items="${allRfxTemplate}" var="rfxEventsTemplate">
														<%-- <div class="col-md-3" id="idTemp">
															<div class="previous-box">
																<a href="#">${rftEventsTemplate.templateName}</a>
															</div>
														</div> --%>
														<div class="col-md-4 marg-bottom-10 idRftEvent" id="${rfxEventsTemplate.id}" data-value="${rfxEventsTemplate.id}" style="display: block">
															<div class="lower-bar-search-contant-main-block" id="test">
																<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">
																	<h4>${rfxEventsTemplate.templateName}</h4>
																</div>
																<div class="lower-bar-search-contant-main-contant pad-top-side-5">
																	<label><spring:message code="application.description"/> :</label> <span class="green">${rfxEventsTemplate.templateDescription}</span>
																</div>
																<div class="lower-bar-search-contant-main-contant pad-top-side-5">
																	<label><spring:message code="application.createdby"/> :</label> <span class="green">${rfxEventsTemplate.createdBy.name}</span>
																</div>
																<div class="lower-bar-search-contant-main-contant pad-top-side-5">
																	<label><spring:message code="application.createddate"/> :</label> <span class="green"><fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${rfxEventsTemplate.createdDate}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"/></span>
																</div>
																<div class="lower-bar-search-contant-main-contant  pad_all_10">
																	<div>
																		<spring:url value="/buyer/copyFromTemplate" var="copyFromTemplate" htmlEscape="true" />
																		<form action="${copyFromTemplate}" class="col-md-6" method="post" style="float: right;">
																			<input type="hidden" id="templateId" value="${rfxEventsTemplate.id}" name="templateId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																			<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit"><spring:message code="application.use.this.button"/></button>
																		</form>
																	</div>
																	<div>
																		<button class="btn btn-black for-form-back hvr-pop hvr-rectangle-out1" type="submit"><spring:message code="rfi.quick.view"/></button>
																	</div>
																</div>
															</div>
														</div>
													</c:forEach>
												</div>
											</div>
										</div>
										<div class="tab-pane" id="tab2">
											<div class="list-table-event marg-top-20">
												<h3 class="marg-bottom-20"><spring:message code="rfi.createrfi.serach.previous"/></h3>
												<div class="alert alert-notice" id="idEventInfo" style="display: none">
													<div class="bg-blue alert-icon">
														<i class="glyph-icon icon-info"></i>
													</div>
													<div class="alert-content">
														<h4 class="alert-title">Info</h4>
														<p id="idEventInfoMessage">
															Information message box using the
															<code>.alert-notice</code>
															color scheme.
															<a title="Link" href="#">Link</a>
														</p>
													</div>
												</div>
												<div class="row">
													<div class="col-md-3">
														<input name="referenceNumber" id="idRefNumber" placeholder='<spring:message code="eventdetails.event.referencenumber" />' type="text" data-validation="alphanumeric length " data-validation-allowing="/ " data-validation-length="max64" class="form-control" />
													</div>
													<div class="col-md-3">
														<input name="eventName" id="idEventName" placeholder='<spring:message code="eventdetails.event.name" />' class="form-control" />
													</div>
													<div class="col-md-5 col-xs-4">
														<div class="white-bg">
															<select name="industryCategory" class="chosen-select white-bg" id="chosenCategoryAll">
																<option value="" label="Select Category" />
																<c:if test="${not empty rfiEvent.industryCategory}">
																	<option value="${rfiEvent.industryCategory.id}" label="${rfiEvent.industryCategory.code} - ${ritEvent.industryCategory.name}" />
																</c:if>
															</select>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="col-md-2 marg-top-20">
														<button class="searchrftEvent btn ph_btn_small btn-info hvr-pop hvr-rectangle-out" type="submit"><spring:message code="application.search"/></button>
													</div>
												</div>
											</div>
											<c:if test="${empty allRfiEvents}">
												<h4>
													<a style="background: none; border: none;"><spring:message code="application.no.records.found"/></a>
												</h4>
											</c:if>
											<div class="privious-box-main marg-top-20 pad_all_20 white-bg" id="rftEvents">
												<div class="row">
													<c:forEach items="${allRfiEvents}" var="event">
														<%-- <div class="col-md-3">
															<div class="previous-box">
																<input type="hidden" name="eventId" value="${event.id}">
																<a href="#">${event.eventName}</a>
															</div>
														</div> --%>
														<div class="col-md-3 marg-bottom-10 idRftEvent" id="${event.id}" data-value="${event.id}" style="display: block">
															<div class="lower-bar-search-contant-main-block" id="test">
																<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">
																	<h4>${event.eventName}</h4>
																</div>
																<div class="lower-bar-search-contant-main-contant pad-top-side-5">
																	<label><spring:message code="eventdetails.event.referencenumber" /> :</label> <span class="green">${event.referanceNumber}</span>
																</div>
																<div class="lower-bar-search-contant-main-contant pad-top-side-5">
																	<label><spring:message code="eventdetails.event.category"/> :</label> <span> ${event.industryCategory.name}</span>
																</div>
																<div class="lower-bar-search-contant-main-contant pad-top-side-10">
																	<label><spring:message code="application.eventstartdate"/> : </label> <span><fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${event.eventStart}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"/></span>
																</div>
																<div class="lower-bar-search-contant-main-contant pad-top-side-5">
																	<label><spring:message code="application.eventenddate"/> :</label> <span><fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${event.eventEnd}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"/></span>
																</div>
																<div class="lower-bar-search-contant-main-contant  pad_all_10">
																	<div>
																		<spring:url value="/buyer/copyFromRft" var="copyFromRft" htmlEscape="true" />
																		<form action="${copyFromRft}" class="col-md-6" method="post" style="float: right;">
																			<input type="hidden" id="eventId" value="${event.id}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																			<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit"><spring:message code="application.use.this.button"/></button>
																		</form>
																	</div>
																	<div>
																		<button class="btn btn-black for-form-back hvr-pop hvr-rectangle-out1" type="submit"><spring:message code="rfi.quick.view"/></button>
																	</div>
																</div>
															</div>
														</div>
													</c:forEach>
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
</div>
<script type="text/javascript" src="<c:url value="/resources/js/view/createRft.js"/>"></script>
<script type="text/javascript">
	$("#test-select ").treeMultiselect({ enableSelectAll : true, sortable : true });
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script>
	$.validate({ lang : 'en', modules : 'date' });
</script>
<script>
	$(function() {

		$(document).on("change", "#load_file", function() {
			$(".show_name").html($(this).val());
		});

		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#tags").autocomplete({ source : availableTags });
		$("#tagres").autocomplete({ source : availableTags });
	});
</script>