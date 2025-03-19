<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

<div class="example-box-wrapper wigad-new">
	<div id="form-wizard-2" class="form-wizard">
		<ul>
			<li class="tb_1 ${spForm.supDetailCompleted ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/editSupplierPerformanceEvaluation/${spForm.id}" class="wiz-step" id="bubble" data-toggle="${spForm.supDetailCompleted ? '' : 'tab'}" style="${spForm.supDetailCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> 
					<label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">1</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="perfromance.evaluation.details" />
					</span>
				</a>
			</li>
			<li class="tb_2 ${spForm.summaryCompleted ? (step == '2' ? 'active' : 'completed' ) : 'current'}">
				<a href="${pageContext.request.contextPath}/buyer/supplierPerformanceSummary?id=${spForm.id}" class="wiz-step" id="bubble" data-toggle="${spForm.summaryCompleted ? '' : 'tab'}" style="${spForm.summaryCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> 
					<label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">2</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="application.summary" />
					</span>
				</a>
			</li>
		</ul>
	</div>
</div>
<script>
	renumberWizard();
	function renumberWizard() {
		$('#form-wizard-2').find('li.flagvisibility').hide().removeClass('flagvisibility');
		$('#form-wizard-2').find('li:visible').each(function(i) {
			$(this).find('.step_num').text(i + 1);
		});
	}
</script>