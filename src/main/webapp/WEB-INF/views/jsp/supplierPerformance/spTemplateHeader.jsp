<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<div class="example-box-wrapper wigad-new">
	<div id="form-wizard-2" class="form-wizard">
		<ul>
 			<li class="tb_1 ${template.detailCompleted ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/editSPTemplate?id=${template.id}" class="wiz-step" id="bubble" style="${template.detailCompleted ?'pointer-events: auto;':'pointer-events:none;'}" > 
					<label class="wizard-step">
						<span class="inner_circle"> <span class="step_num">1</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label>
					<span class="wizard-description"> <spring:message code="sourcingtemplates.template.details" /></span>
				</a>
			</li>

			<li class="tb_2 ${template.performanceCriteriaCompleted ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/supplierPerformanceTemplateCriteriaList/${template.id}" class="wiz-step" id="bubble" style="${template.performanceCriteriaCompleted ?'pointer-events: auto;':'pointer-events:none;'}">
					<label class="wizard-step">
						<span class="inner_circle"> <span class="step_num">8</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label>
					<span class="wizard-description"> <spring:message code="performance.criteria" />
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
