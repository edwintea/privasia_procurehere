<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>




<style type="text/css">
img.wp-smiley, img.emoji {
	display: inline !important;
	border: none !important;
	box-shadow: none !important;
	height: 1em !important;
	width: 1em !important;
	margin: 0 .07em !important;
	vertical-align: -0.1em !important;
	background: none !important;
	padding: 0 !important;
}
.mar-r-15 {
	margin-right: 2%;
}
.rpt_style_basic .rpt_recommended_plan .rpt_head {
	background: #f0f0f0 !important; 
}
.rpt_style_basic .rpt_recommended_plan .rpt_title {
    background: #eaa210 !important;
    text-align: center !important;
    font-weight: 650 !important;   
}
.rpt_style_basic .rpt_recommended_plan .rpt_title_1 {
    background: #0092d0 !important;
    text-align: center !important;
    font-weight: 650 !important;
}
.rpt_style_basic .rpt_plan .rpt_head {
    border-top: transparent solid 2px !important;
}    
.rpt_style_basic .rpt_plan .rpt_title {
    border-bottom: transparent solid 1px !important;
 }
 .rpt_style_basic .rpt_recommended_plan {
    position: relative;
    -webkit-box-shadow: 0px 0px 5px 0px rgba(0,0,0,1);
    -moz-box-shadow: 0px 0px 5px 0px rgba(0,0,0,1);
    box-shadow: 0px 0px 5px 0px rgba(0,0,0,1);    
}
.rpt_style_basic .rpt_plan .rpt_title {
    border-bottom: #111 solid 1px;
    background: #222;
    padding: 14px 18px;
    font-size: 26px;
    color: white;
    line-height: 35px;
    border-top-left-radius: 0px !important;
    border-top-right-radius: 0px !important;
}
.rpt_style_basic .rpt_plan .rpt_title {
    border-bottom: #111 solid 1px;
    background: #222;
    padding: 14px 18px;
    font-size: 26px;
    color: white;
    line-height: 35px;
    border-top-left-radius: 0px !important;
    border-top-right-radius: 0px !important;
}
.rpt_style_basic .rpt_recommended_plan .rpt_head .rpt_price_0 {
    color: #eaa210 !important;
    font-weight: 650 !important; 
}
.rpt_style_basic .rpt_plan .rpt_head .rpt_price {
    text-shadow: none !important;
}
.rpt_style_basic .rpt_recommended_plan .rpt_head .rpt_price {
     font-size: 48px !important;
     line-height: 50px !important; 
}
.rpt_style_basic .rpt_recommended_plan .rpt_head .rpt_price_1{
	color: #0092d0 !important;
	font-weight: 650 !important;  
}
.rpt_style_basic .rpt_plan .rpt_head .rpt_description {
    padding: 0 20px 0px !important;
    color: #000 !important;
}
.rpt_style_basic .rpt_plan .rpt_foot { 
    border-bottom-left-radius: 0px !important;
    border-bottom-right-radius: 0px !important;
}
.aligncenter {
    text-align: center;
    margin: 0 auto;
    display: block;
}
</style>

<link rel='stylesheet' id='js_composer_front-css' href='<c:url value="/resources/assets/elements/js_composer.min.css"/>' type='text/css' media='all' />
<link rel='stylesheet' id='rpt-css' href='<c:url value="/resources/assets/elements/rpt_style.min.css"/>' type='text/css' media='all' />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/plan.css"/>">
<!-- style | custom css | theme options -->

<!-- style | custom css | page options -->
<style id="mfn-dnmc-page-css">
@media only screen and (min-width: 768px) {
	.icon_box.icon_position_left {
		padding-left: 125px;
	}
	.eightbox .vc_column-inner {
		min-height: 380px !important;
		padding: 10px;
	}
	.eightbox-bottom {
		padding: 0 12.5%;
	}	
	.rpt_2_plans .rpt_plan {
    width: 49%;
    float: left;
}
.rpt_style_basic .rpt_recommended_plan .rpt_head .rpt_price {
    font-size: 37px !important;    
}
@media only screen and (min-width: 1240px){
.section_wrapper, .container {
    max-width: none;
}
}

.wpb-js-composer .vc_tta-color-grey.vc_tta-style-classic .vc_active .vc_tta-panel-heading .vc_tta-controls-icon::after,
	.wpb-js-composer .vc_tta-color-grey.vc_tta-style-classic .vc_active .vc_tta-panel-heading .vc_tta-controls-icon::before,
	.wpb-js-composer .vc_tta-color-grey.vc_tta-style-classic .vc_tta-controls-icon::after,
	.wpb-js-composer .vc_tta-color-grey.vc_tta-style-classic .vc_tta-controls-icon::before
	{
	border-color: #fff !important;
}

.wpb-js-composer .vc_tta .vc_tta-controls-icon.vc_tta-controls-icon-chevron::before
	{
	content: '';
	margin: 2px;
	/* display: block; */
	position: absolute;
	box-sizing: border-box;
	left: 2px;
	right: 3px;
	top: 4px;
	bottom: 2px;
	border-style: solid;
	border-width: 0 1px 1px 0 !important;
	-webkit-transform: rotate(45deg) translate(-25%, -25%);
	-ms-transform: rotate(45deg) translate(-25%, -25%);
	transform: rotate(45deg) translate(-25%, -25%);
}

.rpt_style_basic .rpt_plan .rpt_features .rpt_feature_0-0,
	.rpt_feature_1-0, .rpt_feature_0-5, .rpt_feature_1-5, .rpt_feature_0-10,
	.rpt_feature_0-13, .rpt_feature_1-10, .rpt_feature_1-13 {
	background: #666;
	color: #fff !important;
}

.wpb-js-composer .vc_tta.vc_general .vc_tta-panel-title {
	font-size: 14px;
}

.rpt_feature_1-9 {
	text-decoration: line-through;
}

.wpb-js-composer .vc_tta.vc_tta-accordion .vc_tta-controls-icon-position-left .vc_tta-controls-icon
	{
	padding: 3px;
}
</style>

<!--[if lt IE 9]>
<script id="mfn-html5" src="https://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
<meta name="generator" content="Powered by Visual Composer - drag and drop page builder for WordPress." />
<!--[if lte IE 9]><link rel="stylesheet" type="text/css" href="https://www.procurehere.blog/wp-content/plugins/js_composer/assets/css/vc_lte_ie9.min.css" media="screen"><![endif]-->
<link rel="icon" href="https://www.procurehere.blog/wp-content/uploads/2018/05/saas_pro_logo-1.png" sizes="32x32" />
<link rel="icon" href="https://www.procurehere.blog/wp-content/uploads/2018/05/saas_pro_logo-1.png" sizes="192x192" />
<link rel="apple-touch-icon-precomposed" href="https://www.procurehere.blog/wp-content/uploads/2018/05/saas_pro_logo-1.png" />
<meta name="msapplication-TileImage" content="https://www.procurehere.blog/wp-content/uploads/2018/05/saas_pro_logo-1.png" />
<style type="text/css" data-type="vc_shortcodes-custom-css">
.vc_custom_1527823518314 {
	padding-top: 50px !important;
}

.vc_custom_1527823596138 {
	padding-top: 50px !important;
	padding-bottom: 50px !important;
	background-color: #f0f0f0 !important;
}

.vc_custom_1528245821201 {
	margin-right: 10px !important;
	margin-left: 10px !important;
}

.vc_custom_1528660030984 {
	padding-top: 10px !important;
	padding-right: 10px !important;
	padding-bottom: 10px !important;
	padding-left: 10px !important;
}

.vc_custom_1527697325152 {
	padding-top: 50px !important;
}
</style>
<noscript>
	<style type="text/css">
.wpb_animate_when_almost_visible {
	opacity: 1;
}
</style>
</noscript>
<script src='https://www.google.com/recaptcha/api.js'></script>

<script type="text/javascript" src="//cdn.wishpond.net/connect.js?merchantId=1419231&writeKey=185257b4d56c" async></script>

<!-- body -->




<!-- mfn_hook_content_before -->
<!-- mfn_hook_content_before -->
<!-- #Content -->
<div id="Content">
	<div class="content_wrapper clearfix">

		<!-- .sections_group -->
		<div class="sections_group">

			<div class="entry-content" itemprop="mainContentOfPage">

				<div class="section the_content has_content">
					<div class="section_wrapper">
						<div class="the_content_wrapper">
							<div data-vc-full-width="true" data-vc-full-width-init="false" class="vc_row wpb_row vc_row-fluid vc_custom_1527823518314">
								<div class="wpb_column vc_column_container vc_col-sm-12">
									<div class="vc_column-inner ">
										<div class="wpb_wrapper">
											<div class="wpb_text_column wpb_content_element ">
												<div class="wpb_wrapper">
													<h2 style="text-align: center;">Choose your Procurehere Package</h2>
													<!-- <p style="text-align: center;">
														<span style="font-weight: 400;">Unlock the benefits of e-procurement today with our simple, accessible subscription packages.</span>
													</p> -->

												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="vc_row-full-width vc_clearfix"></div>
							<div data-vc-full-width="true" data-vc-full-width-init="false" class="row">
<!-- 								<div class="wpb_column vc_column_container col-sm-2 col-md-3"> -->
<!-- 									<div class="vc_column-inner "> -->
<!-- 										<div class="wpb_wrapper"></div> -->
<!-- 									</div> -->
<!-- 								</div> -->
				<div class="col-md-3"></div>
								<div class="col-md-6">
									<div class="vc_column-inner vc_custom_1528245821201">
										<div class="wpb_wrapper">
											<div class="wpb_text_column wpb_content_element ">
												<div class="wpb_wrapper">
													<div id="rpt_pricr" class="rpt_plans rpt_2_plans rpt_style_basic">
														<div class="">

															<c:if test="${not empty userPlan}">

																<div class="mar-r-15 rpt_plan   rpt_plan_${status.index} rpt_recommended_plan ">
																	<div style="text-align: left;" class="rpt_title rpt_title_0">
																		Starter Pack
<!-- 																		<img class="rpt_recommended" src="https://www.procurehere.blog/wp-content/plugins/dk-pricr-responsive-pricing-table/inc/img/rpt_recommended.png" /> -->
																	</div>
																	<div class="rpt_head rpt_head_0">
																		<fmt:formatNumber groupingUsed="false" var="userBasePrice" type="number" value="${userPlan.basePrice}" />
																		<div class="rpt_price rpt_price_0">${userPlan.currency.currencyCode}${ userBasePrice}</div>
																		<div class="rpt_description rpt_description_0">Recommended for any business size and users</div>
																	</div>
																	<div class="rpt_features rpt_features_0">
																		<div style="color: black;" class="rpt_feature rpt_feature_0-0">
																			<strong class="heads">RFx / Tender </strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-1">RFi</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-2">RFq</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-3">RFp</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-4">RFt (i.e. Tender)</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-5">
																			<strong>Auction</strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-6">English Auction</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-7">Dutch Auction</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-8">Sealed Bid</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-9">Requisitioning</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-10">
																			<strong>Features</strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-11">Template</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-12">Approval Route</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-13">
																			<strong>Usage</strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-14">Unlimited Events</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-15">3 Users</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_0-17">
																			<strong>${userPlan.currency.currencyCode} ${userBasePrice}/Month</strong>
																		</div>
																	</div>
																	<c:url var="changePlan" value="/buyer/billing/userBasedBuyerPlan" />
																	<a href="${changePlan}" style="background: #eaa210" class="rpt_foot rpt_foot_0">Buy Plan</a>
																</div>
															</c:if>
															<c:if test="${not empty evntPlan}">
																<div class="rpt_plan   rpt_plan_1 rpt_recommended_plan ">
																	<div style="text-align: left;" class="rpt_title rpt_title_1">
																		Event Pack
<!-- 																		<img class="rpt_recommended" src="https://www.procurehere.blog/wp-content/plugins/dk-pricr-responsive-pricing-table/inc/img/rpt_recommended.png" /> -->
																	</div>
																	<div class="rpt_head rpt_head_1">
																		<fmt:formatNumber groupingUsed="false" var="BasePrice" type="number" value="${evntPlan.basePrice}" />
																		<div class="rpt_price rpt_price_1">${evntPlan.currency.currencyCode}${ BasePrice}</div>
																		<div class="rpt_description rpt_description_1">
																			Recommended for 1 time events
																		</div>
																	</div>
																	<div class="rpt_features rpt_features_1">
																		<div style="color: black;" class="rpt_feature rpt_feature_1-0">
																			<strong class="heads">RFx / Tender </strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-1">RFi</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-2">RFq</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-3">RFp</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-4">RFt (i.e. Tender)</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-5">
																			<strong>Auction</strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-6">English Auction</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-7">Dutch Auction</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-8">Sealed Bid</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-9">-</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-10">
																			<strong>Features</strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-11">Template</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-12">Approval Route</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-13">
																			<strong>Usage</strong>
																		</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-14">1 Event</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-15">Unlimited Users</div>
																		<div style="color: black;" class="rpt_feature rpt_feature_1-17">
																			<strong>${evntPlan.currency.currencyCode} 499/Event</strong>
																		</div>
																	</div>
																	<c:url var="changePlan" value="/buyer/billing/eventBasedBuyerPlan" />
																	<a href="${changePlan}" style="background: #4ab3eb" class="rpt_foot rpt_foot_1">Buy Plan</a>
																</div>
															</c:if>
														</div>
													</div>
													<div style="clear: both;"></div>

												</div>
											</div>
											<div class="vc_row wpb_row vc_inner vc_row-fluid vc_custom_1528660030984">
												<div class="wpb_column vc_column_container vc_col-sm-6">
													<div class="vc_column-inner ">
														<div class="wpb_wrapper">
															<div class="wpb_text_column wpb_content_element ">
																<div class="wpb_wrapper">
																	<div style="background: #f0f0f0; padding: 10px; margin-left: -10px; margin-right: -10px; text-align: center; border: solid 1px #ccc;">
																		<strong style="color: #666;">Additional Users</strong><br /> (1 user + 1 free approver )
																		</p>
																		<hr />
																		<p>
																			<strong style="color: #666;">US$99/Month</strong>
																	</div>

																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="wpb_column vc_column_container vc_col-sm-6">
													<div class="vc_column-inner vc_custom_1527697325152">
														<div class="wpb_wrapper"></div>
													</div>
												</div>
											</div>
											<div class="wpb_text_column wpb_content_element ">
												<div class="wpb_wrapper">
													<p>
														<img class="size-full wp-image-709 aligncenter" src="https://www.procurehere.blog/wp-content/uploads/2018/05/payment-logis.png" alt="" width="397" height="40"
															srcset="https://www.procurehere.blog/wp-content/uploads/2018/05/payment-logis.png 397w, https://www.procurehere.blog/wp-content/uploads/2018/05/payment-logis-300x30.png 300w, https://www.procurehere.blog/wp-content/uploads/2018/05/payment-logis-260x26.png 260w, https://www.procurehere.blog/wp-content/uploads/2018/05/payment-logis-50x5.png 50w, https://www.procurehere.blog/wp-content/uploads/2018/05/payment-logis-150x15.png 150w"
															sizes="(max-width: 397px) 100vw, 397px" />
													</p>
													<p style="text-align: center;">
														<span style="color: #666666; font-size: 12px;">For your security, all orders are processed on a secured server. </span>
													</p>

												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-3"></div>
<!-- 								<div class="wpb_column vc_column_container col-sm-2 col-md-3"> -->
<!-- 									<div class="vc_column-inner "> -->
<!-- 										<div class="wpb_wrapper"></div> -->
<!-- 									</div> -->
<!-- 								</div> -->
							</div>

						</div>
					</div>
				</div>
				<div class="section section-page-footer">
					<div class="section_wrapper clearfix">

						<div class="column one page-pager"></div>

					</div>
				</div>

			</div>


		</div>

		<!-- .four-columns - sidebar -->

	</div>
</div>

<!-- #Wrapper -->

