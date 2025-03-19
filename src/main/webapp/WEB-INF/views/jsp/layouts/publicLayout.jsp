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
<%-- <link rel="shortcut icon" href="<c:url value="/resources/assets/images/icons/favicon.png"/>"> --%>
<link href="<c:url value="/resources/assets/images/icons/favicon.png"/>" rel="shortcut icon" type="image/vnd.microsoft.icon" />
<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome-4.7/css/font-awesome.min.css"/>"> --%> 
<link rel="stylesheet" type="text/css" href="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-3.6.0.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere-public.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/open-sans.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/js/owl.carousel.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/owl.carousel.min.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/owl.theme.default.min.css"/>">


<title><tiles:insertAttribute name="title" /></title>

<script>
if(top != window) {
top.location = window.location
}
</script>

<script type="text/javascript">
	/* $(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	}); */

	function getContextPath() {
		return "${pageContext.request.contextPath}";
	}
	
	$(window).scroll(function(){
		  var header_top = $('.header_top');
		   scroll = $(window).scrollTop();
		  if (scroll >= 10) {header_top.addClass('fixed-head');
		 
	   /* $('#main').css({"padding-top":"100px"});   */
		  $('.logo').addClass('logo-width-27');
		  $('.header-bottom').addClass('header-btm-55');  
		  $('.logo a img').addClass('logo-img-size');
		  }
		  else {header_top.removeClass('fixed-head');
		  $('.logo').removeClass('logo-width-27');
		  $('.header-bottom').removeClass('header-btm-55');  
		  $('.logo a img').removeClass('logo-img-size');  
		/*   $('#main').css({"padding-top":"0px"}); */
		  }
		});

	
</script>

<!-- Start of procurehere Zendesk Widget script -->
<script>/*<![CDATA[*/window.zEmbed||function(e,t){var n,o,d,i,s,a=[],r=document.createElement("iframe");window.zEmbed=function(){a.push(arguments)},window.zE=window.zE||window.zEmbed,r.src="javascript:false",r.title="",r.role="presentation",(r.frameElement||r).style.cssText="display: none",d=document.getElementsByTagName("script"),d=d[d.length-1],d.parentNode.insertBefore(r,d),i=r.contentWindow,s=i.document;try{o=s}catch(e){n=document.domain,r.src='javascript:var d=document.open();d.domain="'+n+'";void(0);',o=s}o.open()._l=function(){var e=this.createElement("script");n&&(this.domain=n),e.id="js-iframe-async",e.src="https://assets.zendesk.com/embeddable_framework/main.js",this.t=+new Date,this.zendeskHost="procurehere.zendesk.com",this.zEQueue=a,this.body.appendChild(e)},o.write('<body onload="document._l();">'),o.close()}();
/*]]>*/</script>




<!-- End of procurehere Zendesk Widget script -->



</head>
<body>

	<script type="text/javascript">
		/* $(function() {
			$('input[name="daterange"]').daterangepicker();
		}); */
	</script>
	<header id="header"> <tiles:insertAttribute name="header" /> </header>
	<div id="main" style="padding-top: 100px">
		<section id="site-content"> <tiles:insertAttribute name="body" /> </section>
	</div>
	<footer id="footer"> <tiles:insertAttribute name="footer" /> </footer>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>

</body>
</html>