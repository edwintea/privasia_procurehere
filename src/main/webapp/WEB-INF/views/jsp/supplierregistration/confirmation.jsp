<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>

    <style type="text/css">
        html, body {
            height: 100%;
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
        <div id="page-wrapper" class="registration_step_pages">
            <div id="page-header" class="bg-gradient-9">
                <div class="Header_overlay"></div>
                <div id="mobile-navigation" style="none">
                    <a href="<c:url value="/login"/>" class="logo-content-small" title="MonarchUI"></a>
                </div>
                <div id="header-logo" class="logo-bg">
                    <a href="<c:url value="/login"/>" class="logo-content-big" title="Procurehere">Procurehere
                    </a>
                    <a href="<c:url value="/login"/>" class="logo-content-small" title="Procurehere">Procurehere
                    </a>
                    <a id="close-sidebar" class="sr-only" href="#" title="Close sidebar">
                        <i class="glyph-icon icon-angle-left"></i>
                    </a>
                </div>
            </div>
            <div id="page-content-wrapper">
                <div class="lightgray_bg_block"></div>
                <div class="gray_bg_block"></div>
                <div class="equal_2w_col">
                    <div class="col_50 first_blc">
						<jsp:include page="testimonial.jsp"></jsp:include>
                    </div>
                    <div class="col_50">

                        <div class="supplyer_reg_form text-center">

                            <header class="video_header" style="margin:30px 0;">
                                <p>
                                    Thank you for registering, your details<br />
                                    have been submitted for verification.<br />
                                    <br />
                                    You will receive an email within the next 48 hours
                             
                                </p>
                            </header>


                            <div class="video_wrapper">
                                <h4 class="video_caption">Meanwhile you can watch our introduction video</h4>
                                <div class="video_block">
									<iframe width="560" height="315" src="http://www.youtube.com/embed/sgJsf09kl2k?" frameborder="0" allowfullscreen></iframe>                                
                                </div>
                            </div>

                        </div>

                    </div>
                </div>

            </div>
        </div>


    </div>

    <!-- WIDGETS -->

    <script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>

    <!-- Input switch alternate -->
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/input-switch/inputswitch-alt.js"/>"></script>

    <!-- Slim scroll -->
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/slimscroll/slimscroll.js"/>"></script>


    <!-- BX SLIDER --->
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.css"/>">
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.min.js"/>"></script>

    <script>
        $('document').ready(function () {

            $('.carousel-reg').bxSlider({
                pager: false,
                useCSS: true
            });

        });
    </script>
    
    <script type="text/javascript">
        $(window).load(function () {
        	var minHgt = $(window).height() - 75;
        	$('.col_50, .equal_2w_col').css('min-height',minHgt);
            setTimeout(function () {
                $('#loading').fadeOut(400, "linear");
            }, 300);
        });
    </script>