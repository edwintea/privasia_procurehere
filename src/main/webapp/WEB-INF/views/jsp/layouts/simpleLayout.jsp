<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

<title><tiles:insertAttribute name="title" /></title>

<!-- Favicons -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/fonts/opensans-font.css"/>">
<link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-144-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-144-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-144-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-144-precomposed.png"/>">
<link rel="shortcut icon" href="<c:url value="/resources/assets/images/icons/favicon.png"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">

<!-- HELPERS -->
<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/boilerplate.css"/>"> --%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/typography.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/border-radius.css"/>">

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/utils.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/colors.css"/>">

<!-- ELEMENTS -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/buttons.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/forms.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/content-box.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">

<!-- ICONS -->
<link rel="stylesheet" href="<c:url value="/resources/assets/icons/fontawesome/font-awesome.min.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">

<!-- WIDGETS -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/perfect-scrollbar/css/perfect-scrollbar.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/file-input/fileinput.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/uniform/uniform.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/wizard/wizard.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/chosen/chosen.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/tooltip/tooltip.css"/>">


<%-- <link rel="stylesheet" type="text/css"	href="<c:url value="/resources/assets/widgets/charts/piegage/piegage.css"/>">
	<link rel="stylesheet" type="text/css"	href="<c:url value="/resources/assets/widgets/charts/morris/morris.css"/>"> --%>

<!-- Admin theme -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/layout.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/procurehere.css?1"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/color-schemes/Procurehere-theme.css"/>">


<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/layout.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/procurehere.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/color-schemes/Procurehere-theme.css"/>"> --%>

<!-- Components theme -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/components/default.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/components/border-radius.css"/>">

<!-- Admin responsive -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/admin-responsive.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/datatable/datatable.css"/>">

<!-- JS Core -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-core.js"/>"></script>
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/modernizr.js"/>"></script>

<script src="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.min.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.css"/>">

<!-- Jgrowl Notifications -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.js"/>"></script>

<!-- Datepicker Block -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/datepicker/datepicker.css"/>">

<!-- Dialog Block -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/dialog/dialog.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/dialog/dialog.js"/>"></script>

<script type="text/javascript">
        $(window).load(function () {
            setTimeout(function () {
                $('#loading').fadeOut(400, "linear");
            }, 300);
        });
        
        
        function getContextPath() {
        	return "/${pageContext.request.contextPath}";
        }
        $.ajaxSetup({
    	    headers: {
    	    	'${_csrf.headerName}' : '${_csrf.token}'
    	    }
    	});
    </script>
<!-- <script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', '${initParam['GOOGLE_ANALYTICS_TRACKING_ID']}', 'auto');
  ga('send', 'pageview');
</script>
<script src="https://d1t9h2dnv9fxm3.cloudfront.net/scalend-tracker.js" ></script>
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
<style type="text/css">
html, body {
	height: 100%;
}

.leftSideOfCheckbox {
	width: 48%;
	float: left;
	border-right: 1px solid #d8d8d8;
	margin: 0 2% 0 0;
}

.rightSideOfCheckbox {
	width: 50%;
	float: left;
}

.leftSideOfGeogaphicCheckbox {
	width: 48%;
	float: left;
	border-right: 1px solid #d8d8d8;
	margin: 0 2% 0 0;
}

.rightSideOfGeogaphicCheckbox {
	width: 50%;
	float: left;
}
</style>

</head>

<body>
	<div id="sb-site">
		<div id="page-wrapper">

			<section id="site-content"> <tiles:insertAttribute name="body" /> </section>

		</div>
	</div>

	<!-- Uniform -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform-demo.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>

	<!-- Chosen -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>

	<!-- Superclick -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/superclick/superclick.js"/>"></script>


	<!-- Bootstrap Tooltip -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/tooltip/tooltip.js"/>"></script>

	<!-- Perfact scroll -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.jquery.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.min.js"/>"></script>

	<!-- Content box -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>

	<!--
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/parsley/parsley.js"/>"></script>
	-->

	<!-- EQul height js-->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>

	<!-- Theme layout -->
	<script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js?5"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/registration-page.js"/>"></script>
	<!-- PieGage -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage-demo.js"/>"></script>

	<!-- Morris charts -->
	<script type="text/javascript" src="<c:url value="/resources/assets/js-core/raphael.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/morris/morris.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable-bootstrap.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable-tabletools.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.spring-friendly.js"/>"></script>
	<!-- Required for datatable to make its server side ajax params Spring/Java friendly -->


	<!-- BX SLIDER --->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.min.js"/>"></script>
</body>
</html>