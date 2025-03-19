<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
<script>
	$(document).ready(function() {

		$('ul.tabs li').click(function() {
			var tab_id = $(this).attr('data-tab');

			$('ul.tabs li').removeClass('current');
			$('.tab-content').removeClass('current');

			$(this).addClass('current');
			$("#" + tab_id).addClass('current');
		})

		$('#buyerCancel').click(function(event) {
			window.location.href = getContextPath() + "/";
		});

	})
</script>
<link href="<c:url value="/resources/assets/elements/saas.css"/>" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/component.css"/>" />
<link rel="stylesheet" href="<c:url value="/resources/assets/elements/reset.css"/>" />
<!-- CSS reset -->
<link rel="stylesheet" href="<c:url value="/resources/assets/elements/testimonail.css"/>" />
<!-- Resource style -->
<script src="<c:url value="/resources/assets/js-core/modernizr.custom.js"/>" /></script>
<script src="<c:url value="/resources/assets/js-core/jquery.dlmenu.js"/>" /></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
</head>
<div class="wrapper ev-de">
	<%-- <div class="pro_set_top">
		<div class="pset_inner">
			<div class="pset_logo">
				<a href="#"><img src="<c:url value="/resources/assets/images/pro_logo.png"/>" alt="logo"></a>
			</div>
			<div class="pset_top_right">
				<a href="#" class="pset_top_bttn hvr-rectangle-out hvr-pop">Contact Us</a> <a href="#" class="pset_top_bttn hvr-rectangle-out hvr-pop marg-left-10">Help</a>
			</div>
			<div class="clearfix"></div>
		</div>
	</div> --%>
	<script type="text/javascript">
		$(function() {
			$('#dl-menu').dlmenu();
		});
	</script>
	<div class="header">
		<header id="header">
			<div class="header_top">
				<div class="logo">
					<a href="${pageContext.request.contextPath}"> <img src="${pageContext.request.contextPath}/resources/assets/images/saas_pro_logo.png">
					</a>
				</div>
				<!-- <div class="menu_wrap"> -->
				<%-- <div class="nav_inner">
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
					</div> --%>
				<%-- <div id="dl-menu" class="dl-menuwrapper">
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
					</div> --%>
				<!-- </div> -->
				<%-- <c:url var="supplierSignup" value="" />
				<c:url var="login" value="${pageContext.request.contextPath}/login" /> --%>
				<%-- <div class="header_right">
					<a href="${pageContext.request.contextPath}/login" class="signin_bttn1">Login</a>
					<a href="${pageContext.request.contextPath}/supplierSignup" class="signin_bttn2">Supplier Signup</a>
				</div> --%>

			</div>
		</header>
	</div>
	<div class="pro_set_wrap">
		<div style="height: 430px;" class="banner_area">
			<img src="<c:url value="/resources/assets/images/terms_header.jpg"/>" class="width100" alt="terms of use">
		</div>
		<%-- <div class="row clearfix">
			<div class="col-md-12">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			</div>
		</div> --%>
		<div class="tou_mid">
			
			<jsp:include page="./financetermsAndConditions.jsp" />
			<%-- <div class="tom_bot">
			<c:url var="buyerTermsOfUse" value="buyerTermsOfUse" />
			<form:form action="${buyerTermsOfUse}" id="demo-form" method="post" modelAttribute="buyer" autocomplete="off">
				<form:hidden path="id" />
				<input type="hidden" name="d" value="${d}">
				<input type="hidden" name="v" value="${v}">
				<div class="tob_btn">
					<button type="submit" class="tbb_blue  hvr-pop hvr-rectangle-out">Accept</button>
				</div>
				<div class="tob_btn">
					<button type="button" class="tbb_grey  hvr-pop hvr-rectangle-out1" id="buyerCancel">Cancel</button>
				</div>
				<div class="clear"></div>
			</form:form>
			<br /> <br />
		</div> --%>
			<div class="pset_footer">&copy; 2016 All rights reserved</div>
		</div>
	</div>
	<script type="text/javascript" src="<c:url value="/resources/js/view/financecompany.js"/>"></script>