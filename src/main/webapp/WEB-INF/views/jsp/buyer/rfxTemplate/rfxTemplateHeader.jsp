<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<div class="example-box-wrapper wigad-new">
	<div id="form-wizard-2" class="form-wizard">
		<ul>
			<li class="tb_1 ${template.completeTemplateDetails ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/rfxTemplate/editRfxTemplate?id=${template.id}" id="bubble" data-toggle="${template.completeTemplateDetails ? '' : 'tab'}">
				<label class="wizard-step">
						<span class="inner_circle"> <span class="step_num">1</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
				</label>
				<span class="wizard-description"> <spring:message code="sourcingtemplates.template.details" /></span>
			</a></li>
			<li class="tb_2 ${template.documentCompleted ? (step == '2' ? 'active' : 'completed' ) : (step == '2' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/rfxTemplate/rfxTemplateDocument/${template.id}" id="bubble" data-toggle="${template.completeTemplateDetails || template.documentCompleted ? '' : 'tab'}">
				<label class="wizard-step">
						<span class="inner_circle"> <span class="step_num">2</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
				</label>
				<span class="wizard-description"> <spring:message code="application.document" />
					</span>
			</a></li>
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
	var isChange;
	$(document).ready(function() {
		$('#page-content').find("input, select, textarea").change(function() {
			isChange = true;
		});
		$('#form-wizard-2 li a').click(function() {
			if (($(this).attr('data-toggle') == undefined || $(this).attr('data-toggle') == '') && isChange == true) {
				$('#currentClickedUrl').val($(this).attr('href'));
				$('#confirmLeavePageOnSave').modal({
					backdrop : 'static',
					keyboard : false
				});
				return false;
			}
		});
		$('#leavePageModal').click(function() {
			var targetUrl = $('#currentClickedUrl').val();
			window.location.href = targetUrl;
			$('#confirmLeavePageOnSave').modal('hide');
		});
		$('#stayOnpageModal').click(function() {
			$('#currentClickedUrl').val('');
			$('#confirmLeavePageOnSave').modal('hide');
		});
	});
</script>
