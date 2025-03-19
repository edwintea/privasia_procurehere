<%@ page isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message code="supplier.onboarding.only" var="display" />
<div id="sb-site">
	<!-- <div id="page-wrapper"> -->
	<!-- PAGE HEADER BLOCK -->
	<!-- PAGE HEADER BLOCK ENDS-->
	<div id="page-sidebar">
		<div class="scroll-sidebar">
				<ul id="sidebar-menu">
						<li class="no-menu" title="Sus Sap"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>Sus Sap</span>
						</a></li>
						<li class="divider"></li>
						<li class="no-menu" title="Test Screen"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>Test Screen</span>
						</a></li>
						<li class="divider"></li>
						<li class="no-menu" title="Training"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>Training</span>
						</a></li>
						<li class="divider"></li>
						<li class="no-menu" title="Help"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>Help</span>
						</a></li>
						<li class="divider"></li>
						<li class="no-menu" title="IWK Knowledgebases"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>IWK Knowledgebases</span>
						</a></li>
						<li class="divider"></li>
						<li class="no-menu" title="eProcurement Forum"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>eProcurement Forum</span>
						</a></li>
						<li class="divider"></li>
						<li class="no-menu" title="Pendarftaran Online/Online"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>Pendarftaran Online/Online</span>
						</a></li>
						<li class="divider"></li>
						<li class="no-menu" title="Registration"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span>Registration</span>
						</a></li>
						<li class="divider"></li>
				</ul>
			<!-- #sidebar-menu -->
		</div>
	</div>
</div>
<style>
.outerDivFull {
	margin-left: 150px;
}
</style>