<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.id" var="loggedInUserId" />

<c:if test="${productContract.status == 'DRAFT' or ((productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED') and eventPermissions.owner ) }">

<div class="example-box-wrapper wigad-new">
	<div id="form-wizard-2" class="form-wizard">
		<ul>
			<li class="tb_1 ${productContract.contractDetailCompleted ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/productContractListEdit?id=${productContract.id}" id="bubble" data-toggle="${productContract.contractDetailCompleted ? '' : 'tab'}" style="${productContract.contractDetailCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">1</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="product.contract.details" />
					</span>
			</a></li>
			<li class="tb_2 ${productContract.contractItemCompleted ? (step == '2' ? 'active' : 'completed' ) : (step == '2' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/productContractItemList/${productContract.id}" id="bubble" data-toggle="${productContract.contractItemCompleted ? '' : 'tab'}" style="${productContract.contractItemCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">2</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="contract.item.list" />
					</span>
			</a></li>
			<li class="tb_3 ${productContract.contractSummaryCompleted ? (step == '3' ? 'active' : 'completed' ) : (step == '3' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/contractSummary/${productContract.id}" id="bubble" data-toggle="${productContract.contractSummaryCompleted ? '' : 'tab'}" style="${productContract.contractSummaryCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">3</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="application.summary" />
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
</script>

</c:if>
