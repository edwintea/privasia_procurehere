<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
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
<link rel="shortcut icon" href="<c:url value="/resources/assets/images/icons/favicon.png"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-1.11.3.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia2.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/procurehere.css?1"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/color-schemes/Procurehere-theme.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/components/default.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/layout.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere-public.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/open-sans.css"/>">


<!-- ICONS -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery.scrollTo.min.js"/>"></script>
<!-- Admin responsive -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/admin-responsive.css"/>">
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<title><tiles:insertAttribute name="title" /></title>

<style>
.pos-t-100 {
	position: relative;
	top: 100px;
}
.w-100 {
width: 100%;
}
#main {
	overflow-x: hidden;
}
#page-content {
    margin-left: 0;
}
#page-content-wrapper {
    z-index: 0;
}
.logo-width-27 {
    width: 27%;
}
</style>

<script type="text/javascript">
	function getContextPath() {
		return "${pageContext.request.contextPath}";
	}
	
	$(window).scroll(function(){
		
		  var header_top = $('.header_top');
		   scroll = $(window).scrollTop();
		  if (scroll >= 10) {header_top.addClass('fixed-head');			
		  $('.logo').addClass('logo-width-27');
		  $('.header-bottom').addClass('header-btm-55');  
		  $('.logo a img').addClass('logo-img-size');
		  }
		  else {header_top.removeClass('fixed-head');
		  $('.logo').removeClass('logo-width-27');
		  $('.header-bottom').removeClass('header-btm-55');  
		  $('.logo a img').removeClass('logo-img-size');  
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

<!-- Start of procurehere Zendesk Widget script -->
<script>/*<![CDATA[*/window.zEmbed||function(e,t){var n,o,d,i,s,a=[],r=document.createElement("iframe");window.zEmbed=function(){a.push(arguments)},window.zE=window.zE||window.zEmbed,r.src="javascript:false",r.title="",r.role="presentation",(r.frameElement||r).style.cssText="display: none",d=document.getElementsByTagName("script"),d=d[d.length-1],d.parentNode.insertBefore(r,d),i=r.contentWindow,s=i.document;try{o=s}catch(e){n=document.domain,r.src='javascript:var d=document.open();d.domain="'+n+'";void(0);',o=s}o.open()._l=function(){var e=this.createElement("script");n&&(this.domain=n),e.id="js-iframe-async",e.src="https://assets.zendesk.com/embeddable_framework/main.js",this.t=+new Date,this.zendeskHost="procurehere.zendesk.com",this.zEQueue=a,this.body.appendChild(e)},o.write('<body onload="document._l();">'),o.close()}();
/*]]>*/</script>





<!-- Google Tag Manager -->
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-KC3J7RN');</script>
<!-- End Google Tag Manager -->


</head>
<body>

<!-- Google Tag Manager (noscript) -->
<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-KC3J7RN"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->

	<header id="header">
		<tiles:insertAttribute name="header" />
	</header>
	<div class="w-100">
		<div class="">
			<div id="main" style="padding-top: 100px">
			<section id="site-content"> <tiles:insertAttribute name="body" /> </section>
		</div>
	</div>
	</div>
	  
	
	<footer id="footer">
		<tiles:insertAttribute name="footer" />
	</footer>

</body>
</html>