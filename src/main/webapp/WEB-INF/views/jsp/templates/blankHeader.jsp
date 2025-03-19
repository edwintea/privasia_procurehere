<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
        <div id="page-wrapper" class="registration_step_pages">
            <div id="page-header" class="bg-gradient-9">
                <div class="Header_overlay"></div>
                <!-- <div id="mobile-navigation" style="none">
                    <a class="logo-content-small" title="MonarchUI"></a>
                </div> -->
                <div id="header-logo" class="logo-bg">
                <spring:message code="app.root.url"  var="appRootUrl" />
                    <a  class="logo-content-big" href="${appRootUrl}" title="Procurehere">Procurehere
                    </a>
                    <%-- <a  class="logo-content-small" href="<c:url value="/"/>" title="Procurehere">Procurehere
                    </a> --%>
                    <a id="close-sidebar" class="sr-only" href="#" title="Close sidebar">
                        <i class="glyph-icon icon-angle-left"></i>
                    </a>
                </div>
            </div>	
            
            