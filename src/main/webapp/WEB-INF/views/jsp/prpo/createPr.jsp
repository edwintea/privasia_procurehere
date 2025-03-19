<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
		  				<div class="alert alert-notice row" id="idTemplateInfo" style="display: none">
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
					<li class="active"><spring:message code="prtemplate.creation.title" /></li>
				</ol>
				<!-- page title block -->
				<div class="example-box-wrapper wigad-new">
					<div class="rft-creater-heading marg-top-10">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
						<div class="alert alert-notice" id="idEventInfo" style="display: none">
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
						<h3 class="marg-bottom-10"><spring:message code="prtemplate.how.like.create" /></h3>
						<div class="rft-creater-heading-inner example-box-wrapper">
							<div class="row">
								<div class="col-md-12 ">
									<ul class="nav-responsive nav nav-tabs bg-gradient-9">
										<li class="active"><a href="#tabTemplate" data-toggle="tab" id="tabTemplateId" style="padding: 10px 0;"> <img src="${pageContext.request.contextPath}/resources/images/new-tender.png" alt="New Tender" /> <span><spring:message code="prtemplate.create.by.template" /></span>
										</a></li>
										<c:if test="${eventSettings.prCopyFromPrevious}">
											<li>
												<a href="#tabPrevious" data-toggle="tab" id="tabPreviousId"> <img src="${pageContext.request.contextPath}/resources/images/copy-from-privious.png" alt=" Copy Previous" /> <span> <spring:message code="rfa.createrft.copyfromprevious2" /></span>
												</a>
											</li>
										</c:if>
										<div class="pull-right searchTemplatefield">
											<h3 class="marg-template row"><spring:message code="rfi.createrfi.search.fromlist"/></h3>
											<div class="row">
												<div class="col-md-4 col-md-4-custom">
													<input name="templateName" id="idTemplateName" placeholder='<spring:message code="createrfi.template.name.placeholder"/>' type="text" data-validation="alphanumeric length " data-validation-allowing="/ " data-validation-length="max64" class="form-control" style="width: 108%" />
												</div>
												<div class="col-md-2">
													<button class="searchPrTemplate btn ph_btn_small btn-info hvr-pop hvr-rectangle-out" type="submit"><spring:message code="application.search"/></button>
												</div>
											</div>
										</div>
										<div class="pull-right searchpreviousfield flagvisibility">
											<h3 class="marg-previous row"><spring:message code="rfi.createrfi.serach.previous"/></h3>
											<div class="row">
												<div class="col-md-4 col-md-4-custom">
													<input name="prName" id="searchValue" placeholder='<spring:message code="prtemplate.name.ref.placeholder"/>' class="form-control" style="width: 108%" />
												</div>
												<div class="col-md-2">
													<button class="searchrftEvent btn ph_btn_small btn-info hvr-pop hvr-rectangle-out" type="submit"><spring:message code="application.search"/></button>
												</div>
											</div>
										</div>
									</ul>
									<div class="tab-content">
										<div class="tab-pane active" id="tabTemplate">
											<div class="list-table-event marg-top-20">
												<div class="col-md-3 col-sm-6" style="padding-left: 0;">
												</div>
												<div class="col-md-9 col-sm-6">
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
												<div class="privious-box-main pad_all_20 white-bg" id="prTemplates">
													<div class="row">
														<c:if test="${empty allPrTemplate}">
															<h4>
																<a style="background: none; border: none;"><spring:message code="rfi.createrfi.from.blank"/></a>
															</h4>
														</c:if>
														<c:forEach items="${allPrTemplate}" var="prTemp">
															<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates" id="${prTemp.id}" data-value="${prTemp.id}" style="display: block">
																<div class="lower-bar-search-contant-main-block copy-frm-prev" id="test">
																	<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">
																		<h4 class="ellip-title" title="${prTemp.templateName}">${prTemp.templateName}</h4>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-flex disp-f h-30">
																		<div>
																			<label style="width: 83px;"><spring:message code="application.description"/> :</label>
																		</div>
																		<div> 
																			<span class="green ellip-desc" data-toggle="tooltip" data-original-title="${prTemp.templateDescription}">${prTemp.templateDescription}</span>
																		</div>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-flex">
																		<div><label class="w-85"><spring:message code="application.createdby"/> :</label></div>
																		<div class="green text-ellipsis-x">${prTemp.createdBy.name}</div>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-flex">
																		<div><label class="w-95"><spring:message code="application.createddate"/> :</label></div>
																		<div class="green"><fmt:formatDate value="${prTemp.createdDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" /></div>
																	</div>
																	<div class="lower-bar-search-contant-main-contant  pad_all_10">
																		<div>
																			<spring:url value="/buyer/copyPrFromTemplate" var="copyPrFromTemplate" htmlEscape="true" />
																			<form action="${copyPrFromTemplate}" class="col-md-12 hover_tooltip-top" method="post" style="float: right;">
																				<input type="hidden" id="templateId" value="${prTemp.id}" name="templateId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																				<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit" onclick="javascript:$('#loading').show();"><spring:message code="application.use.this.button"/></button>
																			</form>
																		</div>
																	</div>
																</div>
															</div>
														</c:forEach>
													</div>
													<div class="pull-right marg-right-10">
														<div aria-label="Page navigation">
															<ul class="pagination" id="pagination" data-pageType="template"></ul>
														</div>
													</div>
												</div>
											</div>	
										</div>
										<div class="tab-pane" id="tabPrevious">
											<div>
												<div class="col-md-1-5 col-sm-1 col-xs-1 pull-left marg-right-10 marg-top-20" style="padding-left: 0px; padding-right: 0px;">
													<select class="disablesearch chosen-select" name="" data-parsley-id="0644" id="prSelectPageLen" data-screenType="template">
														<option value="10">10</option>
														<option value="25">25</option>
														<option value="50">50</option>
														<option value="100">100</option>
													</select>
												</div>
												<div class="privious-box-main pad_all_20 white-bg" id="rftEvents">
													<div class="row">
														<c:if test="${empty prList}">
															<h4><spring:message code="prtemplate.no.past.pr" /></h4>
														</c:if>
														<c:forEach items="${prList}" var="pr">
															<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates" id="${pr.id}" data-value="${pr.id}" style="display: block">
																<div class="lower-bar-search-contant-main-block min-height-300" id="test">
																	<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">
																		<h4>${pr.name}</h4>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-flex">
																		<div><label class="w-135"><spring:message code="application.referencenumber" /> :</label></div>
																		<div class="green text-ellipsis-x">${pr.referenceNumber}</div>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-10 disp-flex">
																		<div><label class="w-85"><spring:message code="application.createdby"/> :</label></div>
																		<div class="green text-ellipsis-x">${pr.createdBy.name}</div>
																	</div>
																	<div class="lower-bar-search-contant-main-contant pad-top-side-10 disp-flex">
																		<div><label class="w-95"><spring:message code="application.createddate"/> : </label></div>
																		<div class="green text-ellipsis-x"> <fmt:formatDate pattern="dd/MM/yyyy hh:mm a" value="${pr.prCreatedDate}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																		</div>
																	</div>
																	<div class="lower-bar-search-contant-main-contant  pad_all_10">
																		<spring:url value="/buyer/copyFromPr" var="copyFromPr" htmlEscape="true" />
																		<form action="${copyFromPr}" class="col-md-12 hover_tooltip-top" method="post" style="float: right;">
																			<input type="hidden" id="pr" value="${pr.id}" name="prId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																			<button ${ pr.templateActive ?  'disabled' : ''} class="btn ${ pr.templateActive ?  'btn-black':'btn-info'} btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit" onclick="javascript:$('#loading').show();"><spring:message code="application.use.this.button"/></button>
																			<span class="tooltiptext-top"> ${ pr.templateActive ?  'Not able to copy due to template is inactive' : 'Create new PR'}</span>
																		</form>
	
																	</div>
																</div>
																<div class="flagvisibility dialogBox" id="${pr.id}_dia">hello How are you</div>
															</div>
														</c:forEach>
													</div>
													<div class="pull-right marg-right-10">
														<div aria-label="Page navigation">
															<ul class="pagination" id="prPagination"></ul>
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
<c:if test="${not empty openModelBu}">
	<div class="modal fade" id="myModal-auction" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<form action="${pageContext.request.contextPath}/buyer/copyFromPr" method="post">
				<div class="modal-content">
					<div class="modal-header">
						<h3><spring:message code="prevent.bu.would.like"/></h3>
						<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
					</div>

					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="modal-body">
						<div class="auction-body">
							<input type="hidden" name="prId" value="${prid}"> <select name="businessUnitId" class="chosen-select disablesearch" id="idSettingType">
								<c:forEach items="${businessUnits}" var="businessUnit">
									<option value="${businessUnit.id}">${businessUnit.unitName}</option>
								</c:forEach>
							</select>

						</div>
					</div>
					<div class="modal-footer border-none float-left width-100 pad-top-0 ">
						<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton" onclick="javascript:$('#loading').show();"><spring:message code="application.next" /></button>
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
			<form action="${pageContext.request.contextPath}/buyer/copyPrFromTemplate" method="post">
				<div class="modal-content" style="width: 100%; float: left;">
					<div class="modal-header">
						<h3><spring:message code="prevent.bu.would.like.template"/></h3>
						<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
					</div>

					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="modal-body">
						<div class="auction-body">
							<input type="hidden" name="templateId" value="${templateId}"> <select name="businessUnitId" class="chosen-select disablesearch" id="idSettingType">
								<c:forEach items="${businessUnits}" var="businessUnit">
									<option value="${businessUnit.id}">${businessUnit.unitName}</option>
								</c:forEach>
							</select>

						</div>
					</div>
					<div class="modal-footer border-none float-left width-100 pad-top-0 ">
						<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton" onclick="javascript:$('#loading').show();"><spring:message code="application.next" /></button>
					</div>
				</div>
			</form>
		</div>
	</div>
</c:if>



<style>
/* .idRftEvent {
	min-height: 230px;
} */
.currentTemplates>div>div:last-child {
	position: absolute;
	bottom: 10px;
}
.w-95 {
	width: 95px;
}
.w-85 {
	width: 85px;
}
.w-135 {
	width: 135px;
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

.disp-flex {
	display: flex;
}
.min-height-300 {
	min-height: 250px !important;
	max-height: 300px !important;
}
@media screen and (min-width: 1024px) and (max-width: 1600px) {
.min-height-300 {
	min-height: 240px !important;
 	/* min-height: 425px !important; */
 	}
}
.text-ellipsis-x {
    white-space: nowrap !important;
    width: 300px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
    cursor: pointer;
}
@media (max-width: 1366px) and (min-width: 768px) {
	.text-ellipsis-x {
	    width: 215px;
	    white-space: nowrap !important;
	    overflow: hidden;
	    text-overflow: ellipsis;
	}
}
.text-ellipsis-x:hover{
white-space: pre-wrap !important;
    width: auto !important;
    word-break: break-all !important;
    word-wrap: break-word;
    overflow: unset;
    text-overflow: unset;
    display: block;
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

.ellip-title {
	white-space: nowrap !important;
	width: 315px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: block;
}

</style>
<script type="text/javascript" src="<c:url value="/resources/js/view/createPr.js?2"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.twbsPagination.js"/>"></script>
<script type="text/javascript">
	$("#test-select ").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>

<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date,sanitize'
	});
	$(document).ready(function() {
		<c:if test="${not empty openModelBu}">
		$('#myModal-auction').modal();
		</c:if>
		
		<c:if test="${not empty openModelForTemplateBu}">
		$('#myModal-template').modal();
		</c:if>
		
	
		$('body').tooltip({
		    selector: '[data-toggle="tooltip"]'
		});
		
		

		var totalPage = 500;
				var visiblePage = 5;
				var templatesCount = '${prTemplatesCount}';
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
				var prCount = '${totalPrCount}';
				var prPageLen = $('#prSelectPageLen').val();

				console.log("requestCount :" + prCount + "== bqPageLength :" + bqPageLength+ "=== 	prCount/prPageLen :" + prCount/prPageLen);

				totalRequestPage = Math.ceil(prCount/prPageLen);

				if(totalRequestPage == 0 ||  totalRequestPage === undefined || totalRequestPage === ''){
					totalRequestPage = 1;
				}

				if(totalRequestPage < 5){
					visibleRequestPage = totalRequestPage;
				}
				
				$(function (e) {
				    $('#prPagination').twbsPagination({
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
				  		  console.log("@@@@@@@@@@@@@@@@@@@@ searchVal "+searchVal);
			
				  		prTemplatePagination(searchVal ,pageNo ,pageLength);
						 
					 }else {
						 
						 var searchVal = $('#searchValue').val();
				  		  var page = $('#prPagination').find('li.active').text();
				          var pageNo = parseInt(page);
				  		  var pageLen = "20";
				          if ($('#prSelectPageLen option:selected').text()) {
				          	  pageLen = $('#prSelectPageLen').val();
				  		  }
				  		  var pageLength = parseInt(pageLen);
				  		  console.log("Previous @@@@@@@@@@@ searchVal: "+searchVal+" @@@@@@@@@@@ pageNo: "+pageNo+" @@@@@@@@@@@ pageLength: "+pageLength);
			
				  		  purchaseRequestPagination(searchVal ,pageNo ,pageLength);
					 }
					
				});
				 
				 $('.searchPrTemplate').click(function(event) {
					  
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
			  		  prTemplatePagination(searchVal ,pageNo ,pageLength);
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
			
					  prTemplatePagination(searchVal ,pageNo ,pageLength);
				 });
				 
				 $('.searchrftEvent').click(function(event) {
					  console.log("click click searchrftEvent >>>");
					  event.preventDefault();
					  var pgNo = "1";
				 	  var searchVal = $('#searchValue').val();
			          var pageNo = parseInt(pgNo);
			  		  var pageLen = "20";
			          if ($('#prSelectPageLen option:selected').text()) {
			          	  pageLen = $('#prSelectPageLen').val();
			  		  }
			  		  var pageLength = parseInt(pageLen);

			  		  console.log("################# click searchVal: "+searchVal+" #################pageNo: "+pageNo);
			  		  purchaseRequestPagination(searchVal ,pageNo ,pageLength);
				 });
				 
				 $('#prSelectPageLen').on('change', function() {
					   console.log("change request >>>");
					   var value=this.value;
						var searchVal = $('#searchValue').val();
					  	var page = "1";
					    var pageNo = parseInt(page);
					  	var pageLen = "20";
				        pageLen = value;
					  		
				        var pageLength = parseInt(pageLen);
					  	console.log("+++++++++++++++ pageLength "+pageLength +" ########## click searchVal: "+searchVal+" #################pageNo: "+pageNo);
				
					  	purchaseRequestPagination(searchVal ,pageNo ,pageLength);
				 });
				 
				 function prTemplatePagination(searchVal, pageNo, pageLength) {
					 	console.log("PR Template Pagination ....... ");
					 	var header = $("meta[name='_csrf_header']").attr("content");
					 	var token = $("meta[name='_csrf']").attr("content");
					 		$.ajax({
					 			type : "POST",
					 			url : getContextPath() + "/buyer/prTemplatePagination",
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
										html += '<div class="lower-bar-search-contant-main-block copy-frm-prev onHoverDiv" id="test">';
										html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
										html += '<h4 class="ellip-title" title="' + value.templateName + '">' + value.templateName + '</h4>';
										html += '</div>';
										html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-flex disp-f h-30">';
										html += '<div><label style="width: 83px;">Description :</label></div>'
										html += '<div><span data-toggle="tooltip" data-original-title="'+value.templateDescription+'" class="green ellip-desc">' + ( value.templateDescription == undefined ? '' : value.templateDescription ) + '</span></div>'
										html += '</div>' 
										html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
										html += '<label>Created By :</label> <span class="green">' + (value.createdBy ? value.createdBy.name : '') + '</span></div>';
										html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
										html += '<label>Created Date :</label> <span class="green">' + value.createdDate + '</span> </div>';
										html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
										html += '<form action="' + getContextPath() + '/buyer/copyPrFromTemplate" class="col-md-12" method="post" style="float: right;">';
										html += '<input type="hidden" id="templateId" value="' + value.id + '" name="templateId">';
										html += '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">';
										html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
										html += '</form></div>';
										html += '</div></div></div>';
									});
					 				
									$('#prTemplates > .row').html(html);
									currentPrBlocks();
									if (!found) {
// 										$("#idTemplateInfoMessage").html('No matching data found');
// 										$("#idTemplateInfo").show();
// 										$('#rftEvents').hide();

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

									console.log(">>>>>>>>>>>totalPage "+totalPage+">>>>>>countOfTemplates "+countOfTemplates);
									
									$('#pagination').twbsPagination('destroy');
									$('#pagination').twbsPagination($.extend(opts, {
										totalPages : totalPage,
										visiblePages : visiblePage
									}));
									var pagination = jQuery('#pagination').data('twbsPagination');
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
				 
				 function purchaseRequestPagination(searchVal, pageNo, pageLength) {
					 	console.log("Purchase Request Pagination .......");
					 	var header = $("meta[name='_csrf_header']").attr("content");
					 	var token = $("meta[name='_csrf']").attr("content");
					 		$.ajax({
					 			type : "POST",
					 			url : getContextPath() + "/buyer/purchaseRequestPagination",
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
											html += '<div class="col-md-3 marg-bottom-10 idRftEvent" id="' + value.id + '" data-value="' + value.id + '" style="display: block">';
											html += '<div class="lower-bar-search-contant-main-block min-height-300" id="test">';
											html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
											html += '<h4>' + (value.name ? value.name : '') + '</h4></div>';
											html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
											html += '<label>Reference Number :</label> <span class="green">' + (value.referenceNumber ? value.referenceNumber : '') + '</span> </div>';
											html += '<div class="lower-bar-search-contant-main-contant pad-top-side-10">';
											html += '<label>Created By : </label> <span class="green">' + (value.createdBy ? value.createdBy.name : '') + '</span></div>';
											html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
											html += '<label>Created Date :</label> <span class="green">' + (value.prCreatedDate ? value.prCreatedDate : '') + '</span></div>';
											html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
											html += '<form action="' + getContextPath() + '/buyer/copyFromPr" class="col-md-12 hover_tooltip-top" method="post" style="float: right;">';
											html += '<input type="hidden" id="prId" value="' + value.id + '" name="prId">';
											html += '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">';
											//html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
											
											if (value.templateActive) {
												html += '<button disabled class="btn btn-black btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
												html += '<span class="tooltiptext-top"> Not able to copy due to template is inactive </span>'
											} else {
												html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
												html += '<span class="tooltiptext-top"> Create New PR </span>'
											}
											
											html += '</form>'
													+ '</div></div></div></div>';
									});
									
					 				$('#rftEvents').show();
									$('#rftEvents > .row').html(html);
									currentPrBlocks();
									if (!found) {
// 										$("#idEventInfoMessage").html('No matching data found');
// 										$("#idEventInfo").show();
// 										$('#rftEvents').hide();
										
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
									
									$('#prPagination').twbsPagination('destroy');
									$('#prPagination').twbsPagination($.extend(opts, {
										totalPages : totalPage,
										visiblePages : visiblePage
									}));
									var pagination = jQuery('#prPagination').data('twbsPagination');
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
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
