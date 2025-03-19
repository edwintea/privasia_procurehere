<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="poRemarkDesk" code="application.po.create.remarks" />
<script type="text/javascript">
    zE(function() {
        zE.setHelpCenterSuggestions({ labels: [${poRemarkDesk}] });
    });
</script>
<div id="page-content-wrapper">
    <div id="page-content">
        <div class="container">
            <!-- pageging  block -->
            <ol class="breadcrumb">
                <c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
                <li>
                    <a id="dashboardLink" href="${buyerDashboard}">
                        <spring:message code="application.dashboard" />
                    </a>
                </li>
                <li class="active">
                    <spring:message code="po.purchase.order" />
                </li>
            </ol>
            <section class="create_list_sectoin">
                <div class="Section-title title_border gray-bg mar-b20">
                    <h2 class="trans-cap supplier">
                        <spring:message code="po.purchase.order" />
                    </h2>
                    <h2 class="trans-cap pull-right">
                        <spring:message code="application.status" />
                        : ${po.status}
                    </h2>
                </div>
                <jsp:include page="poHeader.jsp"></jsp:include>
                <div class="clear"></div>
                <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
                <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
                <div class="clear"></div>
                <jsp:include page="poViewSummaryTagHeader.jsp"></jsp:include>
                <jsp:include page="poViewSummary.jsp"></jsp:include>
            </section>
        </div>
    </div>
    <jsp:include page="poModal.jsp"></jsp:include>
</div>


<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/po.js"/>"></script>
<script>
    $.validate({
        lang : 'en',
        onfocusout : false,
        validateOnBlur : true,
        modules : 'date,sanitize'
    });
</script>
