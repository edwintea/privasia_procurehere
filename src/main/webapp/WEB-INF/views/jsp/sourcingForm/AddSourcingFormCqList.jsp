<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

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
						<h2 class="trans-cap tender-request-heading"><spring:message code="application.create" /> ${eventType.value}</h2>
						<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${event.status}</h2>
					</div>
					<jsp:include page="sourcingTemplateHeader.jsp"></jsp:include>
				</section>
				<div class="row" style="margin-left: 40px">
					<div class="meeting2-heading col-sm-4 col-lg-4 col-md-4">
						<!-- <!-- <h3>Create Questionnaire</h3> -->
						-->
					</div>
					<div>
						<button class="btn btn-info hvr-pop toggleCreateBq marg-left-20 marg-right-20 marg-top-10 marg-bottom-10 pull-right" type="button" data-toggle="tooltip" data-original-title="Add Questionnaire" style="margin-right: 1080px;"><spring:message code="questionnaire.add.button" /></button>
					</div>
					<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" style="display: none;"></div>
				</div>


				<div class="tab-pane active error-gap-div">
					<form id="cqForm" method="post" action="${pageContext.request.contextPath}/buyer/sourcingFormCqList/${formId}">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="row marg-bottom-10">
							<div class="col-md-3">
								<label for="uom" class="marg-top-10">
									<spring:message code="application.questionnaire" />&nbsp;&nbsp;
									<spring:message code="label.name" />
								</label>
							</div>
							<div class="col-md-5">
								<input type="text" id="name" name="name" placeholder='<spring:message code="addsourcing.quetionnaire.name"/>' data-validation="required length" class="form-control" data-validation-length="4-124" />
							</div>
						</div>
						<div class="row marg-bottom-10">
							<div class="col-md-3">
								<label for="uom" class="marg-top-10">
									<spring:message code="application.questionnaire" />&nbsp;&nbsp;
									<spring:message code="application.description" />
								</label>
							</div>
							<div class="col-md-5">
								<textarea rows="5" name="description" id="description" placeholder='<spring:message code="rfaevent.questionnaire.description.placeholder"/>' data-validation="length" class="form-control" data-validation-length="0-500"></textarea>
								<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
							</div>
						</div>
						<div class="row marg-bottom-10">
							<div class="col-md-3">&nbsp;</div>
							<div class="col-md-5">
								<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" name="Save" id="cqSave">
									<spring:message code="application.create" />
								</button>
								<button type="button" class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1" name="Cancel" id="cqCancel" onclick="javascript:$('#idCreateRftCq').get(0).reset();">
									<spring:message code="application.cancel" />
								</button>
							</div>
						</div>

					</form>
				</div>

			</div>
		</div>
		<script>
			$(function() {
				var cqName = $('#editCqName').val();
				$('.cq_form_google_form').hide();
				$('[data-toggle="tooltip"]').tooltip();
				$('.toggleCreateBq, #cqCancel').click(function() {
					$(".form-error").remove();
					$("#name").parent().removeClass("has-error");
					$("#name").removeClass("error");
					$('#name').css("border-color", "");
					$('.toggleCreateBq').parent().next().slideToggle();
					$('.toggleCreateBq').find('i').toggleClass('fa-minus').toggleClass('fa-plus');
					$('#idCreateRftCq').find('#description').val('');
					$('#idCreateRftCq').find('#name').val('');
					$('#idMeetHead').text('Title : ' + cqName);
					$('#cqSave').text('Save');
				});

			});
		</script>