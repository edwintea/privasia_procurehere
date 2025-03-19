<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<title><spring:message code="rftenvelop.title" /></title>
<spring:message var="rfxCreatingEnvelope" code="application.rfx.create.envelopes" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreatingEnvelope}] });
});
</script>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<!-- pageging  block -->
				<ol class="breadcrumb">
					<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<li class="active">${eventType.value}</li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg">
						<c:if test="${empty envelop.id}">
							<h2 class="trans-cap supplier">
								<spring:message code="label.rftenvelop.create" />
							</h2>
						</c:if>
						<c:if test="${!empty envelop.id}">
							<h2 class="trans-cap supplier"><spring:message code="rfienvelope.update" /></h2>
						</c:if>
						<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${event.status}</h2>
					</div>
					<jsp:include page="eventHeader.jsp" />
					<div class="row clearfix">
						<div class="col-sm-8">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<section class="s1_white_panel s1_addEnvelopForm">
								<form:form cssClass="form-horizontal" action="${pageContext.request.contextPath}/buyer/${eventType}/envelop" method="post" modelAttribute="envelop" id="idRftEnvelop">
									<spring:message code="rftenvelop.envelope.required.field" var="required" />
									<spring:message code="rftenvelop.placeholder.title" var="placeholder" />
									<spring:message code="rftenvelop.length.title" var="length" />
									<br />
									<br />
									<div class="form-group ${event.rfxEnvelopeReadOnly?'disabled':''} ">
										<label class="col-sm-4 col-md-4 control-label"><spring:message code="label.rftenvelop.name" /> </label>
											<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<spring:message code="rfienvelope.enter.envelop.name" var="envtitle"/>
											<form:input path="envelopTitle" type="text" placeholder="${envtitle}" data-validation-length="1-160" data-validation="required length alphanumeric" data-validation-allowing=" _-" class="form-control mar-b10" />
										</div>
									</div>
									<c:if test="${!event.viewSupplerName}">
									<div class="form-group ">
										<label class="col-sm-4 col-md-4 control-label"><spring:message code="envelope.envelope.prefix" /></label>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<spring:message code="rfienvelope.envelope.prefix" var="envprefix"/>
											<form:input path="preFix" type="text" placeholder="${envprefix}" data-validation-length="1-10" data-validation="required length alphanumeric" data-validation-allowing=" _-" class="form-control mar-b10" />
										</div>
									</div>
									</c:if>
									<div class="form-group ">
										<label class="col-sm-4 col-md-4 control-label"><spring:message code="envelope.envelope.description" /> </label>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<spring:message code="rfienvelope.envelope.description" var="envdesc"/>
											<form:textarea path="description" type="text" placeholder="${envdesc}" data-validation-length="0-250" data-validation="length" class="form-control mar-b10" />
											<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
										</div>
									</div>
									<%-- </c:if> --%>
									<input type="hidden" name="eventId" id="eventId" value="${event.id}" />
									<form:hidden path="id" id="id" />
									<div class="s1_pad_25_all">
										<div class="form-group">
											<label class="col-sm-4 col-md-4 control-label"> <spring:message code="label.rftenvelop.subbmissionopen.type" />
											</label>
											<div class="col-sm-8">
												<form:select path="envelopType" cssClass="chosen-select hideFields" data-validation="required" data-validation-error-msg-required="${required}">
													<form:option value="">
														<spring:message code="rftenvelop.submission.type" />
													</form:option>
													<form:options items="${submissionType}" />
												</form:select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-sm-4 col-md-4 control-label"> <spring:message code="label.rftenvelop.evaluation.owner" />
											</label>
											<div class="col-sm-8 col-md-8">
												<form:select path="leadEvaluater" cssClass="user-list-normal chosen-select" data-validation="required" id="selectedOwner">
													<option value=""><spring:message code="select.evaluation.owner" /></option>
													<c:forEach items="${evaluationOwner}" var="usr" >
														<c:if test="${usr.id == '-1' }">
															<form:option value="-1" label="${usr.name}" disabled="true" />
														</c:if>
														<c:if test="${usr.id != '-1' }">
															<form:option value="${usr.id}" label="${usr.name}" />
														</c:if>
													</c:forEach>
												</form:select>
											</div>
										</div>
										<div class="form-group ">
											<c:choose>
												<c:when test="${eventType == 'RFI'}">
													<label class="col-sm-4 col-md-4 control-label">
														<spring:message code="label.rftenvelop.attach.bq.rfi" />
													</label>
												</c:when>
												<c:otherwise>
													<label class="col-sm-4 col-md-4 control-label">
														<spring:message code="label.rftenvelop.attach.bq" />
													</label>
												</c:otherwise>
											</c:choose>
											<div class="col-sm-8">
												<div class="s1_dragdrop_area ui-widget-content">
													<ul id="dropable_area">
														<li class="placeholder"><spring:message code="rftenvelop.attach" /></li>
													</ul>
													<input id="datanoofbqcqs" type="text" style="padding: 0px; border: 0px none; height: 0px;" data-validation-error-msg-container="#bqcq-error-dialog" name="noOfbqs" data-validation="required" data-validation-error-msg-required="${required}" />
												</div>
												<div style="float: left;" id="bqcq-error-dialog"></div>
											</div>
										</div>
										
										<c:if test="${event.rfxEnvelopeOpening}">
													<div class="form-group ${event.rfxEnvelopeReadOnly?'disabled':''}  ">
										<label class="col-sm-4 col-md-4 control-label"><spring:message code="envelope.sequence" /> </label>
										<div class="col-md-8 dd sky mar_b_10 col-sm-6">
											<spring:message code="envelope.sequence" var="envdesc" />
											<form:input id="idNumber1" path="envelopSequence" placeholder="${envdesc}" data-validation-length="0-2" data-validation="length"  data-validation-error-msg="Only two digits are allowed" class="form-control mar-b10" />
											<div id="envelopeError"></div>

										</div>
									</div>
										</c:if>
										
										<c:if test="${eventPermissions.leadEvaluator}">
											<c:if test="${!empty envelop.id}">
												<div class="form-group " id="teamMember">
													<label class="col-sm-4 col-md-4 control-label"><spring:message code="eventsummary.envelopes.evaluator" /></label>
													<div class="col-sm-8 col-md-8">
														<div class="width100 pull-left">
															<div class="col-md-9 col-sm-9 pad0">
															 <span class="dropUp"> 
																<select id="evaluatorList1" class="user-list-normal chosen-select" selected-id="evaluator-id" cssClass="form-control chosen-select" name="userList1">
																	<option value=""><spring:message code="select.evaluator.placeholder" /></option>
																	<c:forEach items="${Evaluators}" var="evaluator">
																		<c:if test="${evaluator.id == '-1' }">
																			<option value="-1"  disabled="true" >${evaluator.name}</option>
																		</c:if>
																		<c:if test="${evaluator.id != '-1' }">
																			<option value="${evaluator.id}" >${evaluator.name}</option>
																		</c:if>																		
																	</c:forEach> 
																</select>
															</span>	
																<div class="form-error font-red  hide" id="evaluator-err"><spring:message code="select.evaluator.placeholder.error" /></div>
															</div>
															<span class="col-md-3 col-sm-3 pad0 ">
																<button class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out addEvaluatorToList" title="" data-placement="top" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.add.evaluator" />' list-type="Evaluator">
																	<i class="fa fa-plus" aria-hidden="true"></i>
																</button>
															</span>
														</div>
														<div class="col-md-12 ph_table_border pull-left  remarksBlock usersListTable marg-top-15" style="min-height: 38px;">
															<c:forEach items="${assignedEvaluators}" var="evaluator">
																<div class="row" evaluator-id="${evaluator.user.id}">
																	<div class="col-md-9">
																		<p>${evaluator.user.name}</p>
																	</div>
																	<div class="col-md-3">
																		<a href="" class="removeEvaluatorsList" list-type="Evaluator"> <i class="fa fa-times-circle"></i>
																		</a>
																	</div>
																</div>
															</c:forEach>
															<c:if test="${empty assignedEvaluators}">
																<div class="row" style="min-height: 20px;" evaluator-id="">
																	<div class="col-md-8 ">
																		<p><spring:message code="envelope.add.evaluator.placeholder" /></p>
																	</div>
																</div>
															</c:if>
														</div>
													</div>
												</div>
											</c:if>
										</c:if>
										<c:if test="${envelop.envelopType !='OPEN' or not empty envelop.envelopTitle}">
											<div class="form-group openerDiv">
												<label class="col-sm-4 col-md-4 control-label"><spring:message code="eventsummary.envelope.opener" /></label>
												<div class="col-sm-8 col-md-8">
												<spring:message code="rfienvelope.select.opener" var="evalOpenerPlaceholder" />
													<form:select path="openerUsers"  id="opener" cssClass="form-control user-list-normal chosen-select autoSave" multiple="multiple" data-validation="required" data-placeholder="${evalOpenerPlaceholder}">
														<%-- <option value=""><spring:message code="rfienvelope.select.opener" /></option> --%>
														<c:forEach items="${openers}" var="usr" >
															<c:if test="${usr.id == '-1' }">
																<form:option value="-1" label="${usr.name}" disabled="true" />
															</c:if>
															<c:if test="${usr.id != '-1' }">
																<form:option value="${usr.id}" label="${usr.name}" />
															</c:if>
														</c:forEach>
													</form:select>
												</div>
											</div>
										</c:if>
									</div>
								</form:form>
								<div class="form-group">
									<div class="col-sm-offset-4 col-sm-8">
										<spring:message code="application.cancel" var="cancel" />
										<spring:message code="application.save" var="save" />
										<c:url value="${pageContext.request.contextPath}/buyer/${eventType}/envelopList" var="createUrl" />
										<button type="submit" id="submitRftEnvelop" class="btn btn-info mrg_10 ph_btn_midium hvr-pop hvr-rectangle-out">
											<c:if test="${empty envelop.id}">
												<spring:message code="application.create" />
											</c:if>
											
											<c:if test="${!empty envelop.id  }">
												<spring:message code="application.update" />
											</c:if>
										</button>
										<form action="${pageContext.request.contextPath}/buyer/${eventType}/envelopCancel" method="post" class="mrg_10 pull-left">
											<input type="hidden" id="eventId" value="${event.id}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<button type="submit" formnovalidate="true" id="idBtnCancel" class="cancel btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out1">
												<spring:message code="application.cancel" />
											</button>
										</form>
									</div>
								</div>
						</div>
						<div class="col-sm-4">
							<section class="s1_question_list_panel" id="question_list">
								<ul class="s1_question_list list-reset">
								     <c:forEach items="${cqlist}" var="cqname" varStatus="loop">
										<li id="${cqname.id}">${cqname.name}&nbsp[Q]<input type="hidden" name="cqids[]" value="${cqname.id}">
										</li>
									</c:forEach>
									<c:forEach items="${bqlist}" var="bqname" varStatus="loop">
										<li id="${bqname.id}">${bqname.name}&nbsp[BQ]<input type="hidden" name="bqids[]" value="${bqname.id}">
										</li>
									</c:forEach>
									<c:forEach items="${sorlist}" var="sorname" varStatus="loop">
										<li id="${sorname.id}">${sorname.name}&nbsp[SOR]<input type="hidden" name="sorids[]" value="${sorname.id}">
										</li>
									</c:forEach>
									<c:forEach items="${cqOfEnvelope}" var="cqname" varStatus="loop">
										<li id="${cqname.id}" class="selected">${cqname.name}(Q)<input type="hidden" name="cqids[]" value="${cqname.id}">
									</c:forEach>
									<c:forEach items="${bqOfEnvelope}" var="bqname" varStatus="loop">
										<li id="${bqname.id}" class="selected">${bqname.name}(BQ)<input type="hidden" name="bqids[]" value="${bqname.id}">
									</c:forEach>
									<c:forEach items="${sorOfEnvelope}" var="sorname" varStatus="loop">
									    <li id="${sorname.id}" class="selected">${sorname.name}(SOR)<input type="hidden" name="sorids[]" value="${sorname.id}">
									</c:forEach>
								</ul>
							</section>
						</div>
				</section>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="table_f_action_btn"></div>
			</div>
		</div>
		<div class="clear"></div>
		</section>
	</div>
</div>
</div>
</div>
<div class="modal fade" id="myModalEnvelop" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
                 <c:choose>
					 <c:when test="${eventType == 'RFI'}">
						 <label> <spring:message code="envelope.remove.bqcq.popup.rfi" /> </label>
					 </c:when>
					 <c:otherwise>
						 <label> <spring:message code="envelope.remove.bqcq.popup" /> </label>
					 </c:otherwise>
				 </c:choose>
				<input type="hidden" name="confirmRemovebqcqId" id="confirmRemovebqcqId">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confirmRemovebqcq">
					<spring:message code="application.remove" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
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

.remarksBlock {
	padding: 12px;
	overflow: hidden;
	text-align: left;
}

#industryCategoryList li:first-child {
	border-top: 1px solid #ccc;
}

.disablefield {
	pointer-events: none !important;
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

.editable-input, .editable-buttons {
	display: inline-block;
}

.editable-buttons {
	position: absolute;
}

.editable-buttons button {
	height: 45px;
	width: 45px
}

.editable-input {
	height: 45px;
	width: 40%;
}

.editable-input input, .editable-input textarea {
	width: 100% !important;
	height: 100% !important;
}

#evaluatorList1_chosen .chosen-drop, #evaluatorList1_chosen .chosen-results
	{
	text-align: left;
}

#submitRftEnvelop {
	float: left;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript">
	/* Datepicker bootstrap */

	$(function() {
		"use strict";
		$('.bootstrap-datepicker').bsdatepicker({
			format : 'dd/mm/yyyy'
		});

		/* $(document).on("click", "#saveEnvelop", function() {
			$("#idRftEnvelop").submit();
		}); */
		$('#envelopType').chosen({
			disable_search : true
		});

		$('#submitRftEnvelop').click(function() {
			 $("#envelopeError").text("")

			var value = $("#idNumber1").val();
			if(value == 0 && value !='')
				{
				 $("#envelopeError").text("0 not allowed").css("color", "#ff5757");
                   return false;
				}
			 $("#envelopeError").text("")
		
			 if($('#envelopType').val() == 'OPEN'){
					$('#opener').removeAttr('data-validation');
				}else{
						$('#opener').attr('data-validation','required');
				}
			 
			$('#datanoofbqcqs').blur();
			$('#saveInlineEdit').trigger('click');
			$('#idRftEnvelop').submit();
			});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/rftEnvelop.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idValidityDays').mask('999');
		$('#idNumber1').mask('00', {
		});
		//$("select[dropup]").next().find(".chosen-drop").addClass("dropup");
		//$("select[dropup]").next().find(".chosen-single").addClass("dropup");
		
	});
</script>
<!-- daterange picker js and css start -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<!-- Theme layout -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/js/view/createrftevent.js?2"/>"></script>
<%-- <script type="text/javascript" src="<c:url value="/resources/js/xeditable.js"/>"></script> --%>
<script>
<c:if test="${(eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'KEEP_NOTIFY' or event.suspensionType == 'KEEP_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
	$(window).bind('load', function() {
		var allowedFields = '#idBtnCancel';
		<c:if test="${eventPermissions.leadEvaluator}">
			allowedFields = '#idBtnCancel,#evaluatorList1,.addEvaluatorToList,#submitRftEnvelop';
		</c:if>
		
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#idBtnCancel';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
		$('.ui-draggable').draggable( "disable" )
		$(".removeButton").prop("onclick", false);
	});
	</c:if>

/* 	$(function() {
		$.fn.editable.defaults.mode = 'inline';
		$("#titleEdit").editable({
			type : "text",
			title : "Enter envelop title",
			mode : "inline",
			validate : function(value) {
				if (value == "")
					return "Envelope title is mandatory";
			}
		}).on("save", function(e, params) {
			$('#titleHidden').val(params.newValue);
		});

		$("#descEdit").editable({
			type : "text",
			mode : "inline",
			emptytext : '  ',
		}).on("save", function(e, params) {
			$('#descHidden').val(params.newValue);
		});

	}); */
	
	$(document).ready(function() {
		var type1 = $('.hideFields').val();
		if(type1 == 'OPEN'){
			$('.openerDiv').hide();
		}
		 $('.hideFields').change(function() {
			var type = $('.hideFields').val();
			 
			if (type === 'OPEN') {
				$('.openerDiv').hide();
			} else {
				$('.openerDiv').show();
			}
		});
		

 
	});
	
	$('#selectedOwner').change(function() {
		if (!$('#editEnvelopeForm').isValid()) {
			return false;
		}
		
		var  loggedInUserId = '${loggedInUserId}';
		var leadEvaluater = $('#selectedOwner').val();

		if(loggedInUserId !== leadEvaluater)
			{
		  $("#teamMember").hide();
		   }else
			   {
				  $("#teamMember").show();

			   }
		
		 var values = this.value;
		 var eventId = $("#eventId").val();
		var envelopeId = $('#id').val();
		var evaluatorList = new Array(100);

		console.log('envelope ID : ' + envelopeId+"......."+eventId);
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getBuyerContextPath('removeEvaluatorFromList');
		 $.ajax({
				type : "POST",
				url : ajaxUrl,

				data : {
					'eventId' : eventId,
					'envelopeId' : envelopeId,
					'values':values
				},
				beforeSend : function(xhr) {

					$('#loading').show();
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					$('#loading').hide();
				},
				success : function(data) {
					
					console.log(data);
					var userList = '<option value="">Select Evaluator</option>';
					$.each(data, function(i, user) {
						if(user.id != '-1'){
							userList += '<option value="' + user.id + '">' + user.name + '</option>';
						} else{
							userList += '<option value="-1" disabled>' + user.name + '</option>';
						}
					});
					
					$("#evaluatorList1").html(userList);
					$("#evaluatorList1").trigger("chosen:updated");
					
					},
				error : function(request, textStatus, errorThrown) {
					console.log("error");
					console.log("ERROR : " + request.getResponseHeader('error'));
					var error = request.getResponseHeader('error');
					if (request.getResponseHeader('error')) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
					}
					
					 $.jGrowl('User has been already assigned as Evaluator', {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					}); 
					
				},
				complete : function() {
					$('#loading').hide();
				}
			});
	
	});
	
	
</script>
<!-- Evaluator Popup -->
<div class="flagvisibility dialogBox" id="removeEvaluatorListPopup">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="evaluatorListId" name="evaluatorListId" value=""> <input type="hidden" id="evaluatorListType" name="evaluatorListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 evaluatorInfoBlock">
				<spring:message code="event.confirm.to.remove" /> "<span></span>" <spring:message code="application.from" /> <span></span> <spring:message code="application.envelope.list" />
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeEvaluatorListPerson" data-original-title='<spring:message code="tooltip.delete" />'><spring:message code="application.delete" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>