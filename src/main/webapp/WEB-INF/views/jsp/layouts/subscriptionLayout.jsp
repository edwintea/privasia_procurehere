<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title><tiles:insertAttribute name="title" /></title>
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-1.11.3.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/fonts/opensans-font.css"/>">
<link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-144-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-114-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-72-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-57-precomposed.png"/>">
<link rel="shortcut icon" href="<c:url value="/resources/assets/images/icons/favicon.png"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/components/default.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/chosen/chosen.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/utils.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/forms.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/colors.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/saas.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/component.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/reset.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/daterangepicker.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/testimonail.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-1.11.3.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/modernizr.custom.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/moment.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.dlmenu.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.flexslider-min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/main.js"/>"></script>


<!-- Required for datatable to make its server side ajax params Spring/Java friendly -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery.spring-friendly.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/datatable/datatable.css"/>">
 <script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable.js"/>"></script> 
 
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function getContextPath() {
		return "${pageContext.request.contextPath}";
	}
</script>
<!-- <script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', '${initParam['GOOGLE_ANALYTICS_TRACKING_ID']}', 'auto');
  ga('send', 'pageview');
</script> -->
<!-- <script src="https://d1t9h2dnv9fxm3.cloudfront.net/scalend-tracker.js" ></script>
<script>
window.scatrack('newTracker', '${initParam['CLICKSTREAM_ANALYTICS_TRACKER_NAME']}', { // Initialise a tracker
appId: '${initParam['CLICKSTREAM_ANALYTICS_APP_ID']}',
forceSecureTracker: true,
sessionCookieTimeout: 7800,
post:true,
contexts: {
optimizelyVisitor: true,
geolocation: true,
performanceTiming: true,
gaCookies: true,
augurIdentityLite: true
}
});
var pageTitle = document.title;
//console.log("pageTitle :" + pageTitle);
window.scatrack('setInitialParams', '', pageTitle, 10, 60);
</script> -->
</head>
<body>
	<script type="text/javascript">
		$(function() {
			$('input[name="daterange"]').daterangepicker();
		});
	</script>
	<div class="wrapper ev-de">
		<div class="header">

			<header id="header"> <tiles:insertAttribute name="header" /> </header>

		</div>
		<div class="fix-mid-back"></div>
		<div class="fix-mid-back-lower"></div>
		<div class="clear"></div>
		<div class="mid_area">
			<div class="registraion_wrap">

				<section id="site-content"> <tiles:insertAttribute name="body" /> </section>

			</div>
			<footer id="footer"> <tiles:insertAttribute name="footer" /> </footer>
		</div>

	</div>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>

</body>
</html>