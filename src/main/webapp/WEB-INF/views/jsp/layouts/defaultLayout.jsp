<!DOCTYPE html>
<html>
<head>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authentication property="principal.jwtToken" var="jwtToken" />

<!-- <script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', '${initParam['GOOGLE_ANALYTICS_TRACKING_ID']}', 'auto');
  ga('send', 'pageview');
</script> -->

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<spring:message code="eventdetails.event.add.contactperson" var="addcontactlabel" />
<spring:message code="event.update.contact.person" var="updateContactLabel" />
<spring:message code="event.documents.description" var="documentDescriptionLabel" />
<spring:message code="event.update.document" var="updateDocumentLabel" />
<spring:message code="label.rftbq.createnewsection" var="createNewSectionLabel" />
<spring:message code="application.save" var="saveLabel" />
<spring:message code="application.update" var="updateLabel" />
<spring:message code="application.edit" var="editLabel" />
<spring:message code="update.quetionnaire.label" var="updateCqLabel" />
<spring:message code="tooltip.template.save.as" var="templateSaveLabel" />
<spring:message code="application.add.btn" var="addLabel" />
<spring:message code="application.add.question" var="addQueLabel" />
<spring:message code="label.rftbq.button.additem" var="addItemLabel" />


<script type="text/javascript">
var timeZoneLocation = "${sessionScope['timeZoneLocation']}";
var languageCode="${empty languageCode ? 'en' : languageCode}";
var addcontactlabel = "${addcontactlabel}";
var updateContactLabel="${updateContactLabel}";
var documentDescriptionLabel="${documentDescriptionLabel}";
var updateDocumentLabel="${updateDocumentLabel}";
var createNewSectionLabel="${createNewSectionLabel}";
var saveLabel="${saveLabel}";
var updateLabel="${updateLabel}";
var editLabel="${editLabel}";
var updateCqLabel="${updateCqLabel}";
var templateSaveLabel="${templateSaveLabel}";
var addLabel="${addLabel}";
var addQueLabel="${addQueLabel}";
var addItemLabel="${addItemLabel}";


</script>


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
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/border-radius.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/typography.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/utils.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/colors.css"/>">
<!-- ELEMENTS -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/buttons.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/forms.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/content-box.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<!-- ICONS -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/font-awesome.min.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawesome/fontawesome.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/icons/fontawsome-4.7/css/font-awesome.min.css"/>">
<!-- WIDGETS -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/perfect-scrollbar/css/perfect-scrollbar.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/file-input/fileinput.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/uniform/uniform.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/wizard/wizard.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/chosen/chosen.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/tooltip/tooltip.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/charts/piegage/piegage.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/charts/morris/morris.css"/>">
<!-- Admin theme -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/layout.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css?3"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia2.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/procurehere.css?3"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/color-schemes/Procurehere-theme.css"/>">
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
<!-- Jgrowl Notifications -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.css?1"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jgrowl-notifications/jgrowl.js?1"/>"></script>
<!-- Dialog Block -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/dialog/dialog.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/dialog/dialog.js"/>"></script>
<!-- Datepicker Block -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/datepicker/datepicker.css"/>">
<!-- Anno JS - Tour Guide -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery-tourbus.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.scrollTo.min.js"/>"></script>
<link rel="stylesheet" type="text/css" media="all" href="<c:url value="/resources/css/jquery-tourbus.min.css"/>">


<script>
if(top != window) {
top.location = window.location
}
</script>

<!--Counter  -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/countdown/jquery.countdown.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/moment.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/countdown/moment-timezone-with-datas.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/countdown/lodash.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/countdown/main.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/countdown/template-remainingTime.js?1"/>"></script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function getContextPath() {
		return "${pageContext.request.contextPath}";
	}

	function getBuyerContextPath(path) {
		return "${pageContext.request.contextPath}/buyer/${eventType}/" + path;
	}

	function getEventType() {
		return "${eventType}";
	}

	function getOwnerContextPath() {
		return "${pageContext.request.contextPath}/owner";
	}

	function getSupplierContextPath() {
		return "${pageContext.request.contextPath}/supplier";
	}
	
	$.ajaxSetup({
	    headers: {
	    	'${_csrf.headerName}' : '${_csrf.token}'
	    }
	});

	$(document).ajaxError(function myErrorHandler(event, xhr, ajaxOptions, thrownError) {
		if (xhr.status == 403) {
			alert("Session Timed Out. Request completed with response code " + xhr.status);
			window.location.href = getContextPath() + "/login";
		}
	});
	
	<c:if test="${!sessionScope.jwtLogin}">
	/* zE(function() {
		 zE.logout();
		});		 */
	window.zESettings = {
		authenticate: { jwt: '${jwtToken}' }
	};	
	</c:if>
	
	function doJwtLogout() {
		zE(function() {
			 zE.logout();
			});		
	}

  
</script>
<title><tiles:insertAttribute name="title" /></title>
<c:set scope="session" var="jwtLogin" value="true" />


<!-- Start of procurehere Zendesk Widget script -->
<script>/*<![CDATA[*/window.zEmbed||function(e,t){var n,o,d,i,s,a=[],r=document.createElement("iframe");window.zEmbed=function(){a.push(arguments)},window.zE=window.zE||window.zEmbed,r.src="javascript:false",r.title="",r.role="presentation",(r.frameElement||r).style.cssText="display: none",d=document.getElementsByTagName("script"),d=d[d.length-1],d.parentNode.insertBefore(r,d),i=r.contentWindow,s=i.document;try{o=s}catch(e){n=document.domain,r.src='javascript:var d=document.open();d.domain="'+n+'";void(0);',o=s}o.open()._l=function(){var e=this.createElement("script");n&&(this.domain=n),e.id="js-iframe-async",e.src="https://assets.zendesk.com/embeddable_framework/main.js",this.t=+new Date,this.zendeskHost="procurehere.zendesk.com",this.zEQueue=a,this.body.appendChild(e)},o.write('<body onload="document._l();">'),o.close()}();
/*]]>*/</script>
<!-- End of procurehere Zendesk Widget script -->

<!-- script src="https://d1t9h2dnv9fxm3.cloudfront.net/scalend-tracker.js" ></script -->

<script>
zE(function() {
    zE.identify({
      name: '<sec:authentication property="principal.name" />',
      email: '<sec:authentication property="principal.username" />'.replace(/&#64;/g,'@').replace(/&#46;/g,'.'), // replace the escape characters with actual chars.
      organization: '<sec:authentication property="principal.companyName" />'
    });
  });
</script>


</head>
<body>

	<!-- ~~~~~~~~~~~~~~~~~~~ -->
	<!-- APPCUES INTEGRATION -->
	<!-- ~~~~~~~~~~~~~~~~~~~ -->
	<script src="//fast.appcues.com/38428.js"></script>
	<script>
try{

var showWizardTutorial = ${sessionScope['showWizardTutorial']};
if(showWizardTutorial){
	Appcues.identify('<sec:authentication property="principal.id" />', { 
		name: '<sec:authentication property="principal.name" />',
		userType: '<sec:authentication property="principal.tenantType"/>',
		userRole: '<sec:authentication property="principal.roleName" />',
		isFreeTrial:'<sec:authentication property="principal.isFreeTrial"/>',
	});
}

}catch (e) {
	console.log(e);
}
</script>

	<div id="loading" class="opacity-60">
		<div class="spinner">
			<div class="bounce1"></div>
			<div class="bounce2"></div>
			<div class="bounce3"></div>
		</div>
	</div>
	<header id="header">
		<tiles:insertAttribute name="header" />
	</header>
	<section id="sidemenu">
		<tiles:insertAttribute name="menu" />
	</section>
	<section id="site-content">
		<div id="page-content-wrapper">
			<tiles:insertAttribute name="body" />
		</div>
	</section>
	<footer id="footer">
		<tiles:insertAttribute name="footer" />
	</footer>
	<!-- Uniform -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform-demo.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js?1"/>"></script>
	<!-- Superclick -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/superclick/superclick.js?1"/>"></script>
	<!-- Chosen -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js?1"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js?1"/>"></script>
	<!-- Bootstrap Tooltip -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/tooltip/tooltip.js"/>"></script>

	<!-- Added for inline editors -->
	<script type="text/javascript" src="<c:url value="/resources/js/popover.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/xeditable.js"/>"></script>
	<!-- Added for inline editors -->
	
	<!-- Perfact scroll -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.jquery.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.min.js"/>"></script>
	<!-- Content box -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/parsley/parsley.js"/>"></script>
	<!-- EQul height js-->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
	<!-- Theme layout -->
	<script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js?6"/>"></script>
	<!-- PieGage -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage-demo.js"/>"></script>
	<!-- Morris charts -->
	<script type="text/javascript" src="<c:url value="/resources/assets/js-core/raphael.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/morris/morris.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datetimesorting.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/resources/js/jquery.spring-friendly.js"/>"></script>
	<!-- Required for datatable to make its server side ajax params Spring/Java friendly -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable-bootstrap.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable-tabletools.js"/>"></script>
	<!-- BX SLIDER --->
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.css"/>">
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.min.js"/>"></script>
	<script>
		$('document').ready(function() {
			$('.carousel-reg').bxSlider({
				pager : false,
				useCSS : true
			});

		});
	</script>

</body>
</html>