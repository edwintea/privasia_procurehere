<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<script type="text/javascript">
	$(function() {
		$('#dl-menu').dlmenu();
	});
</script>
<div class="header_top">
	<div class="logo">
		<a href="${pageContext.request.contextPath}" />
		<img src="${pageContext.request.contextPath}/resources/assets/images/saas_pro_logo.png">
		</a>
	</div>
	<%-- <div class="menu_wrap">
		<div class="nav_inner">
			<ul>
				<li>
					<a href="#">Features</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/buyerSubscription/selectPlan">Plans</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/publicEvents">Public Events</a>
				</li>
				<li>
					<a href="#">Contact us</a>
				</li>
			</ul>
		</div>
		<div id="dl-menu" class="dl-menuwrapper">
			<button class="dl-trigger">Open Menu</button>
			<ul class="dl-menu">
				<li>
					<a href="#">Features</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/buyerSubscription/selectPlan">Plans</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/publicEvents">Public Events</a>
				</li>
				<li>
					<a href="#">Contact us</a>
				</li>
			</ul>
		</div>
	</div>
	<c:url var="supplierSignup" value="" />
	<c:url var="login" value="${pageContext.request.contextPath}/login" />
	<div class="header_right">
		<a href="${pageContext.request.contextPath}/login" class="signin_bttn1">Login</a>
		<a href="${pageContext.request.contextPath}/supplierSignup" class="signin_bttn2">Supplier Signup</a>
	</div> --%>
	<div class="clear"></div>
</div>
