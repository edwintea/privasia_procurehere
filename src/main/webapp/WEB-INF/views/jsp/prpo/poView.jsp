<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="poSummaryDesk" code="application.po.create.summary" />
<spring:message var="poDetailsDesk" code="application.po.details" />
<spring:message var="poDefineApprovalDesk" code="application.po.define.approvals" />
<script type="text/javascript">
<c:if test="${po.status eq 'APPROVED'}">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${poDetailsDesk}] });
});
</c:if>
<c:if test="${!(po.status eq 'APPROVED')}">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${poDefineApprovalDesk}, ${poSummaryDesk}] });
});			
</c:if>
</script>				
<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/poList" />
				<li>
					 <spring:message code="application.dashboard" />
				</li>
				<li>
					<a href="${buyerDashboard}"> <spring:message code="po.label" />
					</a>
				</li>
				
				<li class="active"><spring:message code="po.purchase.order" /></li>
			</ol>
			<c:if test="${po.status ne 'DRAFT' and po.status ne 'SUSPENDED' and po.status ne 'PENDING' }">
                <ul class="nav-responsive nav nav-tabs marg-bottom-5">
                    <li class="active"><a href="#tab1" data-toggle="tab">Purchase Order</a></li>
                    <c:choose>
                        <c:when test="${ po.status ne 'READY' and po.status ne 'ORDERED' and po.status ne 'ACCEPTED' and po.status ne 'DECLINED' and po.status ne 'CANCELLED' and po.status ne 'SUSPENDED' and po.status ne 'DRAFT' and po.status ne 'PENDING'}">
                            <li><a href="#tab2"  data-toggle="tab">Delivery Orders <i class="noti-round-absolute bs-badge badge-absolute badge-blue-alt">${po.doCount != null ? po.doCount : 0} </i> </a></li>
                            <li><a href="#tab3"  data-toggle="tab">GRN <i class="noti-round-absolute bs-badge badge-absolute badge-blue-alt">${po.grnCount != null ? po.grnCount : 0} </i> </a></li>
                            <li><a href="#tab4" data-toggle="tab">Invoices <i class="noti-round-absolute bs-badge badge-absolute badge-blue-alt">${po.invoiceCount != null ? po.invoiceCount : 0}</i> </a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="#tab5" data-toggle="tab">P2P Summary  </a></li>
                            <li><a href="#tab6" data-toggle="tab">Messages  </a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </c:if>
			<div class="tab-content">
			    <div class="tab-pane active" id="tab1">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> :  ${po.status}
						</h2>
					</div>
					<div class="clear"></div>
					<c:if test="${po.status eq 'DRAFT' or po.status eq 'SUSPENDED' }">
                        <jsp:include page="poHeader.jsp"></jsp:include>
                        <div class="clear"></div>
					</c:if>
					<jsp:include page="poSummary.jsp"></jsp:include>
				</div>
			    <div class="tab-pane" id="tab2">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> :  ${po.status}
						</h2>
					</div>
					<div class="clear"></div>
 			    	<jsp:include page="doQuickView.jsp"></jsp:include>
 				</div>
 				 <div class="tab-pane" id="tab3">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> :  ${po.status}
						</h2>
					</div>
					<div class="clear"></div>

 			    	<jsp:include page="grnQuickView.jsp"></jsp:include>
 				</div>
			    <div class="tab-pane" id="tab4">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> :  ${po.status}
						</h2>
					</div>
					<div class="clear"></div>
  			    	<jsp:include page="invoiceQuickView.jsp"></jsp:include>
 				</div>
 				<div class="tab-pane" id="tab5">
                    <div class="Section-title title_border gray-bg mar-b20">
                        <h2 class="trans-cap supplier">
                            <spring:message code="defaultmenu.po" /> : ${po.name}
                        </h2>
                        <h2 class="trans-cap pull-right">
                        <spring:message code="supplier.po.summary.poStatus" /> :  ${po.status}
                        </h2>
                    </div>
                    <div class="clear"></div>
                    <jsp:include page="p2pSummaryView.jsp"></jsp:include>
                </div>
                <div class="tab-pane" id="tab6">
                    <div class="Section-title title_border gray-bg mar-b20">
                        <h2 class="trans-cap supplier">
                            <spring:message code="defaultmenu.po" /> : ${po.name}
                        </h2>
                        <h2 class="trans-cap pull-right">
                        <spring:message code="supplier.po.summary.poStatus" /> :  ${po.status}
                        </h2>
                    </div>
                    <div class="clear"></div>
                    <jsp:include page="mailBox.jsp"></jsp:include>
                </div>
			</div>
		</div>
	</div>
</div>