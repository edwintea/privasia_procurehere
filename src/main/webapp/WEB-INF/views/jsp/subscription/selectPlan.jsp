<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:message var="supplierPlanDesk" code="application.owner.supplier.subscription.plan" />
<!-- <script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierPlanDesk}] });
});
</script> -->
<jsp:include page="/WEB-INF/views/jsp/subscription/SelectSubscriptionPlan.jsp" />

