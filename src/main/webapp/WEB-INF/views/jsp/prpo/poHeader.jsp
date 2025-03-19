<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div class="example-box-wrapper wigad-new">
	<div id="form-wizard-2" class="form-wizard">
		<ul>
			<li class="tb_1 ${po.poDetailCompleted ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/poCreate/${po.id}?prId=${pr.id}" id="bubble" data-toggle="${pr.prDetailCompleted ? '' : 'tab'}" style="${pr.prDetailCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
                <span class="inner_circle">
                    <span class="step_num">1</span>
                    <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
                </span>
                </label> <span class="wizard-description">
                    <spring:message code="po.details" />
                </span>
			</a></li>
			<li class="tb_2 ${po.documentCompleted ? (step == '2' ? 'active' : 'completed' ) : (step == '2' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/po/poDocumentView/${po.id}?prId=${pr.id}" id="bubble" data-toggle="${pr.prDetailCompleted ? '' : 'tab'}" style="${pr.documentCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">2</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="po.documents" />
					</span>
			</a></li>
			<%-- <li class="tb_3 ${po.supplierCompleted ? (step == '3' ? 'active' : 'completed' ) : (step == '3' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/poDelivery/${po.id}?prId=${pr.id}" id="bubble" data-toggle="${pr.documentCompleted ? '' : 'tab'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">3</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="application.supplier.detail" />
					</span>
			</a></li> --%>
			<li class="tb_4 ${po.deliveryCompleted ? (step == '4' ? 'active' : 'completed' ) : (step == '4' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/poDelivery/${po.id}?prId=${pr.id}" id="bubble" data-toggle="${pr.deliveryCompleted ? '' : 'tab'}" style="${pr.deliveryCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">4</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="po.delivery.details" />
					</span>
			</a></li>
			<li class="tb_5 ${po.poItemCompleted ? (step == '5' ? 'active' : 'completed' ) : (step == '5' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/poPurchaseItem/${po.id}?prId=${pr.id}" id="bubble" data-toggle="${pr.deliveryCompleted ? '' : 'tab'}" style="${pr.prItemCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">5</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="po.purchase.items" />
					</span>
			</a></li>
			<li class="tb_6 ${po.remarkCompleted ? (step == '6' ? 'active' : 'completed' ) : (step == '6' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/poRemark/${po.id}?prId=${pr.id}" id="bubble" data-toggle="${pr.prItemCompleted ? '' : 'tab'}" style="${pr.remarkCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">6</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="po.remarks" />
					</span>
			</a></li>
			<li class="tb_7 ${po.summaryCompleted ? (step == '7' ? 'active' : 'completed' ) : (step == '7' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/poView/${po.id}?prId=${pr.id}" id="bubble" data-toggle="${pr.remarkCompleted ? '' : 'tab'}" style="${pr.summaryCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">7</span>
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