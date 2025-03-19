<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>"> --%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/components/default.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/chosen/chosen.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/utils.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/forms.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/colors.css"/>">
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-1.11.3.min.js"/>"></script>

<!-- Start of procurehere Zendesk Widget script -->
<script>/*<![CDATA[*/window.zEmbed||function(e,t){var n,o,d,i,s,a=[],r=document.createElement("iframe");window.zEmbed=function(){a.push(arguments)},window.zE=window.zE||window.zEmbed,r.src="javascript:false",r.title="",r.role="presentation",(r.frameElement||r).style.cssText="display: none",d=document.getElementsByTagName("script"),d=d[d.length-1],d.parentNode.insertBefore(r,d),i=r.contentWindow,s=i.document;try{o=s}catch(e){n=document.domain,r.src='javascript:var d=document.open();d.domain="'+n+'";void(0);',o=s}o.open()._l=function(){var e=this.createElement("script");n&&(this.domain=n),e.id="js-iframe-async",e.src="https://assets.zendesk.com/embeddable_framework/main.js",this.t=+new Date,this.zendeskHost="procurehere.zendesk.com",this.zEQueue=a,this.body.appendChild(e)},o.write('<body onload="document._l();">'),o.close()}();
/*]]>*/</script>
<!-- End of procurehere Zendesk Widget script -->

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
<!--  <script>
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
	<section id="site-content"> <tiles:insertAttribute name="body" /> </section>

</body>
</html>