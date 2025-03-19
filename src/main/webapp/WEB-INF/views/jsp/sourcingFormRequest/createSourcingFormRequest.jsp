<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message var="prCreateDesk" code="application.pr.create" />
<sec:authentication property="principal.bqPageLength" var="bqPageLength" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prCreateDesk}] });
});
</script>
<input type="hidden" id="bqPageLength" value="${bqPageLength}">
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a
						href="${pageContext.request.contextPath}/buyer/buyerDashboard">
							<spring:message code="application.dashboard" />
					</a></li>
					<li class="active"><spring:message
							code="rfs.sourcing.form.creation" /></li>
				</ol>
				<!-- page title block -->
				<div class="example-box-wrapper wigad-new">
					<div class="rft-creater-heading marg-top-10">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
						<div class="alert alert-notice" id="idEventInfo"
							style="display: none">
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
						<div class="alert alert-notice row" id="idTemplateInfo"
							style="display: none">
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
						<h3 class="marg-bottom-10">
							<spring:message code="rfs.like.request.sourcing" />
						</h3>
						<div class="rft-creater-heading-inner example-box-wrapper">
							<div class="row">
								<div class="col-md-12 ">
									<ul class="nav-responsive nav nav-tabs bg-gradient-9">
										<li class="active">
											<a href="#tabTemplate" class="createEventsTopTabs" data-toggle="tab" id="tabTemplateId">
										 		 <img src="${pageContext.request.contextPath}/resources/images/new-tender.png"
													alt="New Tender" /> <span><spring:message code="rfs.new.request.label" /></span>
											</a>
										</li>
										<c:if test="${eventSettings.rfsCopyFromPrevious}">
											<li>
												<a href="#tabPrevious" data-toggle="tab" id="tabPreviousId">
													<img src="${pageContext.request.contextPath}/resources/images/copy-from-privious.png" alt=" Copy Previous" /> 
													<span> <spring:message code="rfa.createrft.copyfromprevious2" /></span>
												</a>
											</li>
										</c:if>
										<div class="pull-right searchTemplatefield">
											<h3 class="marg-template row">
												<spring:message code="rfi.createrfi.search.fromlist" />
											</h3>
											<div class="row">
												<div class="col-md-4 col-md-4-custom">
													<input name="templateName" id="idTemplateName"
														placeholder='<spring:message code="createrfi.template.name.placeholder"/>'
														type="text" data-validation="alphanumeric length "
														data-validation-allowing="/ "
														data-validation-length="max64" class="form-control"
														style="width: 108%" />
												</div>
												<div class="col-md-2">
													<button class="searchSourcingTemplate btn ph_btn_small btn-info hvr-pop hvr-rectangle-out"
														type="submit">
														<spring:message code="application.search" />
													</button>
												</div>
											</div>
										</div>
										<div class="pull-right searchpreviousfield flagvisibility">
											<h3 class="marg-previous row">
												<spring:message code="rfs.search.list.previously" />
											</h3>
											<div class="row">
												<div class="col-md-4 col-md-4-custom">
													<input name="formName" id="searchValue"
														placeholder='<spring:message code="rfs.sourcing.form.name"/>'
														class="form-control" style="width: 108%" />
												</div>
												<div class="col-md-2">
													<button class="searchrftEvent btn ph_btn_small btn-info hvr-pop hvr-rectangle-out" type="submit">
														<spring:message code="application.search" />
													</button>
												</div>
											</div>
										</div>
									</ul>
									<div class="tab-content">
										<div class="tab-pane active" id="tabTemplate">
											<div class="list-table-event marg-top-20">
												<div class="col-md-3 col-sm-6" style="padding-left: 0;"></div>
												<div class="col-md-9 col-sm-6"></div>
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
												<div class="privious-box-main pad_all_20 white-bg" id="prTemplates">
													<div class="row">
														<c:if test="${empty allSourcingTemplate }">
															<h4>
																<a style="background: none; border: none;"><spring:message code="application.not.templates" /></a>
															</h4>
														</c:if>
														<c:forEach items="${allSourcingTemplate}"
															var="sourcingTemp">
															<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates"
																id="${sourcingTemp.id}" data-value="${sourcingTemp.id}"
																style="display: block">
																<div
																	class="lower-bar-search-contant-main-block copy-frm-prev onHoverDiv"
																	id="test">
																	<div
																		class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">
																		<h4 class="text-ellipsis-x" title="${sourcingTemp.formName}">${sourcingTemp.formName}</h4>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-5 descBlock disp-flex">
																		<div class="green text-ellipsis-x description test">
																			<label class="pull-left "><spring:message code="application.description" /> :</label>
																			<span class="desc-color" data-toggle="tooltip" data-original-title="${sourcingTemp.description}">${sourcingTemp.description}</span>
																		</div>
																	</div>
																	<div
																		class="lower-bar-search-contant-main-contant pad-top-side-5">
																		<label><spring:message code="application.createdby" /> :</label> <span class="green">${sourcingTemp.createdBy.name}</span>
																	</div>
																	<div
																		class="lower-bar-search-contant-main-contant pad-top-side-5">
																		<label><spring:message
																				code="application.createddate" /> :</label> <span
																			class="green"> <fmt:formatDate
																				value="${sourcingTemp.createdDate}"
																				timeZone="${sessionScope['timeZone']}"
																				pattern="dd/MM/yyyy hh:mm a" />
																		</span>
																	</div>
																	<div
																		class="lower-bar-search-contant-main-contant  pad_all_10">
																		<div>
																			<spring:url value="/buyer/copyFromSourcingTemplate" var="newSourcing" htmlEscape="true" />
																			<form action="${newSourcing}" class="col-md-12"
																				method="post" style="float: right;">
																				<input type="hidden" id="sourcingTemplateId"
																					value="${sourcingTemp.id}" name="sourcingTemplateId">
																				<input type="hidden" name="${_csrf.parameterName}"
																					value="${_csrf.token}" />
																				<button
																					class="btn btn-info btn-block hvr-pop hvr-rectangle-out"
																					style="width: 100%" type="submit" onclick="javascript:$('#loading').show();">
																					<spring:message code="application.use.this.button" />
																				</button>
																			</form>
																		</div>
																	</div>
																</div>
															</div>
														</c:forEach>
													</div>
													<div class="pull-right marg-right-10">
														<div aria-label="Page navigation">
															<ul class="pagination" id="pagination" data-request="template"></ul>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="tab-pane" id="tabPrevious">
											<div>
												<div class="col-md-1-5 col-sm-1 col-xs-1 pull-left marg-right-10 marg-top-10" style="padding-left: 0px; padding-right: 0px;">
													<select class="disablesearch chosen-select" name="" data-parsley-id="0644" id="requestSelectPageLen" data-screenType="events">
														<option value="10">10</option>
														<option value="25">25</option>
														<option value="50">50</option>
														<option value="100">100</option>
													</select>
												</div>
												<div class="privious-box-main pad_all_20 white-bg" id="rftEvents">
													<div class="row">
														<c:if test="${empty allSourcingRequest}">
															<h4>
																<spring:message code="rfs.no.past.sourcing" />
															</h4>
														</c:if>
														<c:forEach items="${allSourcingRequest}"
															var="sourcingFormRequest">
															<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates"
																id="${sourcingFormRequest.id}"
																data-value="${sourcingFormRequest.id}"
																style="display: block">
																<div class="lower-bar-search-contant-main-block copy-frm-prev"
																	id="test">
																	<div
																		class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">
																		<h4 class="text-ellipsis-x" title="${sourcingFormRequest.sourcingFormName}">${sourcingFormRequest.sourcingFormName}</h4>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-5 descBlock disp-flex">
																		<div class="green text-ellipsis-x description test">
																			<label class="pull-left "><spring:message code="eventdetails.event.referencenumber" /> :</label>
																			 <span class="green" data-toggle="tooltip" data-original-title="${sourcingFormRequest.referanceNumber}">${sourcingFormRequest.referanceNumber}</span>
																		</div>
																	</div>
																	<div
																		class="lower-bar-search-contant-main-contant pad-top-side-10">
																		<label><spring:message
																				code="application.createdby" /> :</label> <span class="green">
																			${sourcingFormRequest.createdBy.name}</span>
																	</div>
																	<div
																		class="lower-bar-search-contant-main-contant pad-top-side-10">
																		<label><spring:message
																				code="application.createddate" /> : </label> <span
																			class="green"> <fmt:formatDate
																				pattern="dd/MM/yyyy hh:mm a"
																				value="${sourcingFormRequest.createdDate}"
																				timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																		</span>
																	</div>
																	<div class="lower-bar-search-contant-main-contant  pad_all_10">
																		<spring:url value="/buyer/copyFromSourcingFormRequest"
																			var="copyFromSourcingFormRequest" htmlEscape="true" />
																		<form action="${copyFromSourcingFormRequest}"
																			class="col-md-12" method="post" style="float: right;">
																			<input type="hidden" id="formId"
																				value="${sourcingFormRequest.id}" name="formId">
																			<input type="hidden" name="${_csrf.parameterName}"
																				value="${_csrf.token}" />
																			<button
																				class="btn btn-info btn-block hvr-pop hvr-rectangle-out"
																				data-toggle="tooltip" data-placement="top"
														                    	data-original-title="Create New RFS"
																				style="width: 100%" type="submit" onclick="javascript:$('#loading').show();">Use This</button> 
																		</form>
																	</div>
																</div>
<%-- 																<div class="flagvisibility dialogBox" id="${sourcingFormRequest.id}_dia">hello How are you</div> --%>
															</div>
														</c:forEach>
													</div>
													<div class="pull-right marg-right-10">
														<div aria-label="Page navigation">
															<ul class="pagination" id="requestPagination" data-request="events"></ul>
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
	<c:if test="${not empty openModelBu}">
		<div class="modal fade" id="myModal-auction" role="dialog">
			<div class="modal-dialog">
				<!-- Modal content-->
				<form
					action="${pageContext.request.contextPath}/buyer/copyFromSourcingFormRequest"
					method="post">
					<div class="modal-content">
						<div class="modal-header">
							<h3>
								<spring:message code="prevent.bu.would.like" />
							</h3>
							<button class="close for-absulate" type="button"
								data-dismiss="modal">×</button>
						</div>
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" />
						<div class="modal-body">
							<div class="auction-body">
								<input type="hidden" name="formId" value="${formId}"> <select
									name="businessUnitId" class="chosen-select disablesearch"
									id="idSettingType">
									<c:forEach items="${businessUnits}" var="businessUnit">
										<option value="${businessUnit.id}">${businessUnit.unitName}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div
							class="modal-footer border-none float-left width-100 pad-top-0 ">
							<button type="submit" onclick="javascript:$('#loading').show();"
								class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton">
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
				<form
					action="${pageContext.request.contextPath}/buyer/copyFromSourcingTemplate"
					method="post">
					<div class="modal-content" style="width: 100%; float: left;">
						<div class="modal-header">
							<h3>
								<spring:message code="prevent.bu.would.like.template" />
							</h3>
							<button class="close for-absulate" type="button"
								data-dismiss="modal">×</button>
						</div>
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" />
						<div class="modal-body">
							<div class="auction-body">
								<input type="hidden" name="templateId" value="${templateId}">
								<select name="businessUnitId"
									class="chosen-select disablesearch" id="idSettingType">
									<c:forEach items="${businessUnits}" var="businessUnit">
										<option value="${businessUnit.id}">${businessUnit.unitName}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div
							class="modal-footer border-none float-left width-100 pad-top-0 ">
							<button type="submit" onclick="javascript:$('#loading').show();"
								class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton">
								<spring:message code="application.next" />
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</c:if>
	<div class="modal fade" id="myModal-blank" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<c:set var="createBlankUrl"
				value="${pageContext.request.contextPath}/buyer/copyFromSourcingTemplate" />
			<form action="${createBlankUrl}" method="post">
				<div class="modal-content" style="width: 100%; float: left;">
					<div class="modal-header">
						<h3>
							<spring:message code="prevent.bu.would.like.template" />
						</h3>
						<button class="close for-absulate" type="button"
							data-dismiss="modal">×</button>
					</div>
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" /> <input type="hidden"
						name="sourcingTemplateId" value="${sourcingTemplateId}" />
					<div class="modal-body">
						<select name="businessUnitId" class="chosen-select disablesearch">
							<c:forEach items="${businessUnits}" var="businessUnit">
								<option value="${businessUnit.id}">${businessUnit.unitName}</option>
							</c:forEach>
						</select>
					</div>
					<div
						class="modal-footer border-none float-left width-100 pad-top-0 ">
						<button type="submit" onclick="javascript:$('#loading').show();"
							class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 ">
							<spring:message code="application.next" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
	<style>
/* .idRftEvent {
	min-height: 230px;
} */
.desc-color {
	color: #06ccb3 !important;
}
.tooltip { 
    left: 80px !important;
}
.h-30 .tooltip-inner { 
    word-break: break-all;
}
.currentTemplates>div>div:last-child {
	position: absolute;
	bottom: 10px;
}

#rftEvents {
	min-height: 350px;
}

.marg-template {
	margin-bottom: 5px;
	margin-top: 10px;
	margin-left: 0px;
	margin-right: 94px;
	color: white !important;
}

.marg-previous {
	margin-bottom: 5px;
	margin-top: 10px;
	margin-left: 0px;
	margin-right: 10px;
	color: white !important;
}

.col-md-4.col-md-4-custom {
	width: 68.333333%;
}

.copy-frm-prev {
	min-height: 205px !important;
	/* overflow-y: scroll; */ 
}

.disp-flex {
	display: flex;
}

@media ( max-width : 1366px) and (min-width: 768px) {
	.text-ellipsis-x {
		width: 215px !important;
		white-space: nowrap !important;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	.copy-frm-prev {
		min-height: 225px !important;
		/* overflow-y: scroll; */ 
	}	
}
.lower-bar-search-contant-main-contant span {    
    white-space: nowrap;
   }
.text-ellipsis-x {
	white-space: nowrap !important;
	width: 300px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
	cursor: pointer;
}

.currentTemplates>div>div:last-child {
    position: relative !important;
    bottom: 5px;
}

/* #test:hover */
/* #test:hover {
cursor: pointer;
     overflow-y: scroll; 
     min-height: 205px; 
} */
</style>
	<script type="text/javascript" src="<c:url value="/resources/js/view/createSourcingRequest.js?2"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.twbsPagination.js"/>"></script>
	<script type="text/javascript">
	$("#test-select ").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
	<script type="text/javascript"
		src="<c:url value="/resources/js/numeral.min.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
	<script>
	$.validate({
		lang : 'en',
		modules : 'date,sanitize'
	});
	$(document).ready(function(){
		<c:if test="${not empty openModelForTemplateBu}">
		console.log("hiiiii");
		$('#myModal-blank').modal();
		</c:if>
		
		$('body').tooltip({
		    selector: '[data-toggle="tooltip"]'
		});
		
		var totalPage = 500;
		var visiblePage = 5;
		var templatesCount = '${templatesCount}';
		var bqPageLength = $('#bqPageLength').val();
		var startPageLen = $('#selectPageLen').val();
		if( bqPageLength === undefined || bqPageLength === ''){
			bqPageLength = 50;
		}
		console.log("templatesCount :" + templatesCount + "== bqPageLength :" + bqPageLength+ "=== 	templatesCount/startPageLen :" + templatesCount/startPageLen);

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
		var requestCount = '${requestCount}';
		var reqPageLen = $('#requestSelectPageLen').val();

		console.log("requestCount :" + requestCount + "== bqPageLength :" + bqPageLength+ "=== 	templatesCount/reqPageLen :" + requestCount/reqPageLen);

		totalRequestPage = Math.ceil(requestCount/reqPageLen);

		if(totalRequestPage == 0 ||  totalRequestPage === undefined || totalRequestPage === ''){
			totalRequestPage = 1;
		}

		if(totalRequestPage < 5){
			visibleRequestPage = totalRequestPage;
		}
		
		$(function (e) {
		    $('#requestPagination').twbsPagination({
		        totalPages: totalRequestPage,
		        visiblePages: visibleRequestPage
		    });
		});
		
		 $(document).delegate('.page-link', 'click', function(e) {
		 	 e.preventDefault();
			 if(template){
			 	  var searchVal = $('#idTemplateName').val();
		  		  var page = $('#pagination').find('li.active').text();
		          var pageNo = parseInt(page);
		  		  var pageLen = "20";
		          if ($('#selectPageLen option:selected').text()) {
		          	  pageLen = $('#selectPageLen').val();
		  		  }
		  		  var pageLength = parseInt(pageLen);
	
		          sourcingTemplatePagination(searchVal ,pageNo ,pageLength);
				 
			 }else {
				 var searchVal = $('#searchValue').val();
		  		  var page = $('#requestPagination').find('li.active').text();
		          var pageNo = parseInt(page);
		  		  var pageLen = "20";
		          if ($('#requestSelectPageLen option:selected').text()) {
		          	  pageLen = $('#requestSelectPageLen').val();
		  		  }
		  		  var pageLength = parseInt(pageLen);
		  		  	
		          sourcingRequestPagination(searchVal ,pageNo ,pageLength);
			 }
			
		});
		 
		 $('.searchSourcingTemplate').click(function(event) {
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

	  		  sourcingTemplatePagination(searchVal ,pageNo ,pageLength);
		 });
		 
		 $('#selectPageLen').on('change', function() {
			  console.log("change template >>>");
			  var value=this.value;
					
		      var searchVal = $('#idTemplateName').val();
			  var page = 1;
			  var pageNo = parseInt(page);
			  var pageLen = "20";
		      pageLen = value;
			  		
		      var pageLength = parseInt(pageLen);
			  console.log("+++++++++++++++++++ pageLength "+pageLength);
	
			  sourcingTemplatePagination(searchVal ,pageNo ,pageLength);
		 });
		 
		 $('.searchrftEvent').click(function(event) {
			  console.log("click click searchrftEvent >>>");
			  event.preventDefault();
			  var pgNo = "1";
		 	  var searchVal = $('#searchValue').val();
	          var pageNo = parseInt(pgNo);
	  		  var pageLen = "20";
	          if ($('#requestSelectPageLen option:selected').text()) {
	          	  pageLen = $('#requestSelectPageLen').val();
	  		  }
	  		  var pageLength = parseInt(pageLen);

	  		  console.log("################# click searchVal: "+searchVal+" #################pageNo: "+pageNo);
	  		  sourcingRequestPagination(searchVal ,pageNo ,pageLength);
		 });
		 
		 $('#requestSelectPageLen').on('change', function() {
			   console.log("change request >>>");
			   var value=this.value;
			    var pgNo = "1";
				var searchVal = $('#searchValue').val();
			  	var page = $('#pagination').find('li.active').text();
			    var pageNo = parseInt(pgNo);
			  	var pageLen = "20";
		        pageLen = value;
			  		
		        var pageLength = parseInt(pageLen);
			  	console.log("+++++++++++++++ pageLength "+pageLength +" ########## click searchVal: "+searchVal+" #################pageNo: "+pageNo);
		
			  	sourcingRequestPagination(searchVal ,pageNo ,pageLength);
		 });
		 
		 function sourcingTemplatePagination(searchVal, pageNo, pageLength) {
		 		console.log("Sourcing Template Pagination ........");
		 		
		 		var header = $("meta[name='_csrf_header']").attr("content");
		 		var token = $("meta[name='_csrf']").attr("content");
		 		$.ajax({
		 			type : "POST",
		 			url : getContextPath() + "/buyer/sourcingTemplatePagination",
		 			data : {
		 				'searchValue' : searchVal,
		 				'pageNo' : pageNo,
		 				'pageLength' : pageLength
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
		 					
		 					html += '<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates" id="' + value.id + '" data-value="' + value.id + '" style="display: block">';
		 					html += '<div class="lower-bar-search-contant-main-block copy-frm-prev" id="test">';
		 					html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
		 					html += '<h4 class="text-ellipsis-x" title="' + value.formName + '">' + value.formName + '</h4></div>';
		 					html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5 descBlock disp-flex">';
		 					html += '<div class="green text-ellipsis-x">';
		 					html += '<label class="pull-left">Description :</label> <span style="color: #06ccb3 !important" data-toggle="tooltip" data-original-title="' + value.description + '"> '
		 								+ ( value.description == undefined ? '' : value.description ) + ' </span> </div></div>';
		 					html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
		 					html += '<label>Created By :</label> <span class="green">' + (value.createdByName ? value.createdByName : '') + '</span></div>';
		 					html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
		 					html += '<label>Created Date :</label> <span class="green">' + value.createdDate + '</span> </div>';
		 					html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
		 					html += '<form action="' + getContextPath() + '/buyer/copyFromSourcingTemplate" class="col-md-12" method="post" style="float: right;">';
		 					html += '<input type="hidden" id="sourcingTemplateId" value="' + value.id + '" name="sourcingTemplateId">';
		 					html += '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">';
		 					html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
		 					html += '</form></div>';
		 					html += '</div></div></div>';
		 				});
		 				
		 				$('#prTemplates > .row').html(html);
		 				currentSourcingBlocks();
		 				if (!found) {
// 		 					$("#idTemplateInfoMessage").html('No matching data found');
// 		 					$("#idTemplateInfo").show();
// 		 					$('#rftEvents').hide();

							$('#prTemplates > .row').html('No matching data found');		 					
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
		 
		 function sourcingRequestPagination(searchVal, pageNo, pageLength) {
			 	console.log("Sourcing Request Pagination ........");
			 	
			 	    var header = $("meta[name='_csrf_header']").attr("content");
			 	    var token = $("meta[name='_csrf']").attr("content");
			 		$.ajax({
			 			type : "POST",
			 			url : getContextPath() + "/buyer/sourcingRequestPagination",
			 			data : {
			 				'searchValue' : searchVal,
			 				'pageNo' : pageNo,
			 				'pageLength' : pageLength
			 			},
			 			dataType : "json",
			 			beforeSend : function(xhr) {
			 				$('#loading').show();
			 				xhr.setRequestHeader(header, token);
			 				xhr.setRequestHeader("Accept", "application/json");
			 			},
			 			success : function(data) {
			 				
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
								html += '<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates" id="' + value.id + '" data-value="' + value.id + '" style="display: block">';
								html += '<div class="lower-bar-search-contant-main-block copy-frm-prev" id="test">';
								html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
								html += '<h4 class="text-ellipsis-x" title="' + value.sourcingFormName + '">' + (value.sourcingFormName ? value.sourcingFormName : '') + '</h4></div>';
								html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5 descBlock disp-flex">';
								html += '<div class="green text-ellipsis-x description test">';
								html += '<label class="pull-left">Reference Number :</label> <span class="green" data-toggle="tooltip" data-original-title="' + (value.referanceNumber ? value.referanceNumber : '') + '">' + (value.referanceNumber ? value.referanceNumber : '') + '</span>';
								html += '</div> </div>';
								html += '<div class="lower-bar-search-contant-main-contant pad-top-side-10">';
								html += '<label>Created By : </label> <span class="green">' + (value.createdByName ? value.createdByName : '') + '</span></div>';
								html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
								html += '<label>Created Date :</label> <span class="green">' + (value.createdDate ? value.createdDate : '') + '</span></div>';
								html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
								html += '<form action="' + getContextPath() + '/buyer/copyFromSourcingFormRequest" class="col-md-12" method="post" style="float: right;">';
								html += '<input type="hidden" id="formId" value="' + value.id + '" name="formId">';
								html += '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">';
								html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" data-toggle="tooltip" data-placement="top" data-original-title="Create New RFS" style="width: 100%" type="submit">Use This</button>';
								html += '</form>' + '</div></div></div></div>';
							});
			 				
							$('#rftEvents').show();
							$('#rftEvents > .row').html(html);
							currentSourcingBlocks();
							if (!found) {
// 								$("#idEventInfoMessage").html('No matching data found');
// 								$("#idEventInfo").show();
// 								$('#rftEvents').hide();

								$('#rftEvents').show();
								$('#rftEvents > .row').html('No matching data found');	
							}
							
			 				// set pagination according to result
							visiblePage = 5;
							var countOfRequest = data.recordsTotal;
							
							console.log(".........pageLength "+pageLength+"......totalPage "+totalPage+"  >>>>>>countOfTemplates "+countOfRequest);
							totalPage = Math.ceil(countOfRequest / pageLength);
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

							$('#requestPagination').twbsPagination('destroy');
							$('#requestPagination').twbsPagination($.extend(opts, {
								totalPages : totalPage,
								visiblePages : visiblePage
							}));
							var pagination = jQuery('#requestPagination').data('twbsPagination');
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
	
	
 
</script>
	<script type="text/javascript"
		src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>