<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
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
<link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-114-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-72-precomposed.png"/>">
<link rel="apple-touch-icon-precomposed" href="<c:url value="/resources/assets/images/icons/apple-touch-icon-57-precomposed.png"/>">
<link rel="shortcut icon" href="<c:url value="/resources/assets/images/icons/favicon.png"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css?1"/>">

<!-- HELPERS -->
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
<link href="<c:url value="/resources/assets/icons/fontawesome/font-awesome.min.css"/>" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">

<!-- WIDGETS -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/chosen/chosen.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/uniform/uniform.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/wizard/wizard.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/perfect-scrollbar/css/perfect-scrollbar.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/file-input/fileinput.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/tooltip/tooltip.css"/>">


<!-- Admin theme -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/layout.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/procurehere.css?1"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/color-schemes/Procurehere-theme.css"/>">

<!-- Components theme -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/components/default.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/components/border-radius.css"/>">

<!-- Admin responsive -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/admin-responsive.css"/>">

<!-- JS Core -->

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-core.js"/>"></script>
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/modernizr.js"/>"></script>

<!-- Jgrowl Notifications -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.js"/>"></script>

<!-- Dialog Block -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/dialog/dialog.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/dialog/dialog.js"/>"></script>

<!-- Datepicker Block -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/datepicker/datepicker.css"/>">

	<!-- Chosen -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js?1"/>"></script>

<script type="text/javascript">
        $(window).load(function () {
            setTimeout(function () {
                $('#loading').fadeOut(400, "linear");
            }, 300);
        });
        
        function getContextPath() {
        	return "${pageContext.request.contextPath}";
        }       
        
        $.ajaxSetup({
    	    headers: {
    	    	'${_csrf.headerName}' : '${_csrf.token}'
    	    }
    	});
    </script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', '${initParam['GOOGLE_ANALYTICS_TRACKING_ID']}', 'auto');
  ga('send', 'pageview');
</script>
<!-- <script src="https://d1t9h2dnv9fxm3.cloudfront.net/scalend-tracker.js" ></script>
 -->
<script>
/*window.scatrack('newTracker', '${initParam['CLICKSTREAM_ANALYTICS_TRACKER_NAME']}', { // Initialise a tracker
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
*/
</script>


<!-- Start of procurehere Zendesk Widget script -->
<script>/*<![CDATA[*/window.zEmbed||function(e,t){var n,o,d,i,s,a=[],r=document.createElement("iframe");window.zEmbed=function(){a.push(arguments)},window.zE=window.zE||window.zEmbed,r.src="javascript:false",r.title="",r.role="presentation",(r.frameElement||r).style.cssText="display: none",d=document.getElementsByTagName("script"),d=d[d.length-1],d.parentNode.insertBefore(r,d),i=r.contentWindow,s=i.document;try{o=s}catch(e){n=document.domain,r.src='javascript:var d=document.open();d.domain="'+n+'";void(0);',o=s}o.open()._l=function(){var e=this.createElement("script");n&&(this.domain=n),e.id="js-iframe-async",e.src="https://assets.zendesk.com/embeddable_framework/main.js",this.t=+new Date,this.zendeskHost="procurehere.zendesk.com",this.zEQueue=a,this.body.appendChild(e)},o.write('<body onload="document._l();">'),o.close()}();
/*]]>*/</script>
<!-- End of procurehere Zendesk Widget script -->

</head>

<body>
	<style type="text/css">
html, body {
	height: 100%;
}

.pset_footer {
	position: absolute;
/* 	bottom: 0; */
}

#loading {
	background: rgba(255, 255, 255, 0.5);
} 
</style>
	<div id="sb-site">
		<div id="loading">
			<div class="spinner">
				<div class="bounce1"></div>
				<div class="bounce2"></div>
				<div class="bounce3"></div>
			</div>
		</div>

		<header id="header">
			<tiles:insertAttribute name="header" />
		</header>

		<section id="site-content">
			<tiles:insertAttribute name="body" />
		</section>

		<footer id="footer">
			<tiles:insertAttribute name="footer" />
		</footer>
	</div>
</body>
</html>
