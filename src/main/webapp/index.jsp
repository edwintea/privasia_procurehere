<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="year" value="${now}" pattern="yyyy" />
<c:redirect url="/login" />
<title>Welcome</title>
<link href="<c:url value="/resources/assets/elements/saas.css"/>" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/component.css"/>" />
<link rel="stylesheet" href="<c:url value="/resources/assets/elements/reset.css"/>" />
<!-- CSS reset -->
<link rel="stylesheet" href="<c:url value="/resources/assets/elements/testimonail.css"/>" />
<!-- Resource style -->
<script src="<c:url value="/resources/assets/js-core/modernizr.custom.js"/>" /></script>
<script src="<c:url value="/resources/assets/js-core/jquery.dlmenu.js"/>" /></script>
<script>
	$(function() {
		$('#dl-menu').dlmenu();
	});
</script>
<div class="wrapper">
	<div class="header">
		<div class="header_top">
			<div class="logo">
				<a href="${pageContext.request.contextPath}" />
				<img src="${pageContext.request.contextPath}/resources/assets/images/saas_pro_logo.png">
				</a>
			</div>
			<div class="menu_wrap">
				<div class="nav_inner">
					<ul>
						<li>
							<a href="#">Features</a>
						</li>
						<li>
							<a href="${pageContext.request.contextPath}/buyerSubscription/selectPlan">Buyer Plans</a>
						</li>
						<li>
							<a href="${pageContext.request.contextPath}/publicEvents">Public Events</a>
						</li>
						<li>
							<a href="#">Contact us</a>
						</li>
					</ul>
				</div>
				<div id="dl-menu" class="dl-menuwrapper">
					<button class="dl-trigger">Open Menu</button>
					<ul class="dl-menu">
						<li>
							<a href="#">Features</a>
						</li>
						<li>
							<a href="${pageContext.request.contextPath}/buyerSubscription/selectPlan">Buyer Plans</a>
						</li>
						<li>
							<a href="${pageContext.request.contextPath}/publicEvents">Public Events</a>
						</li>
						<li>
							<a href="#">Contact us</a>
						</li>
					</ul>
				</div>
			</div>
<%-- 			<c:url var="supplierSignup" value="/suppliersubscription/selectPlan" />
 --%>			<c:url var="supplierSignup" value="/supplierSignup" />
			<c:url var="login" value="/login" />
			<div class="header_right">
				<a href="${login}" class="signin_bttn1">Login</a>
				<a href="${supplierSignup}" class="signin_bttn2">Supplier Signup</a>
			</div>
			<div class="clear"></div>
		</div>
		<div class="banner_area">
			<div class="banner_inner">
				<div class="banner_inside">
					<div class="bi_text">Procurement made Simple</div>
					<div class="bi_text2">Most Intuitive e-procurement system</div>
					<div class="bi_try">
						<div class="bit_bttn">
							<a href="${pageContext.request.contextPath}/subscription/selectPlan" class="try_bttn_style">Try Now</a>
							<div class="clear"></div>
						</div>
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="mid_area">
		<div class="mid_section1">
			<div class="ms1_inner">
				<div class="ms1_top">
					<div class="ms1t_left">
						<div class="ms1t_row">
							<div class="ms1_icon1">
								<img src="${pageContext.request.contextPath}/resources/assets/images/saas_effi_icon.png">
							</div>
							<div class="ms1_text">
								<div class="ms1_text_heading">Efficient</div>
								<div class="ms1_text_cont">Saves 60% time and 50% cost</div>
							</div>
						</div>
						<div class="ms1t_row">
							<div class="ms1_icon1">
								<img src="${pageContext.request.contextPath}/resources/assets/images/saas_trans_icon.png">
							</div>
							<div class="ms1_text">
								<div class="ms1_text_heading">Transparent</div>
								<div class="ms1_text_cont">
									Automated workflow to remove<br> errors and leakage
								</div>
							</div>
						</div>
						<div class="ms1t_row">
							<div class="ms1_icon1">
								<img src="${pageContext.request.contextPath}/resources/assets/images/saas_intitutive_icon.png">
							</div>
							<div class="ms1_text">
								<div class="ms1_text_heading">Intuitive</div>
								<div class="ms1_text_cont">
									Designed with user at the center <br> for zero learning curve
								</div>
							</div>
						</div>
					</div>
					<div class="ms1t_right">
						<img src="${pageContext.request.contextPath}/resources/assets/images/saas_pc_view.jpg">
					</div>
				</div>
				<div class="ms1_bot">
					<div class="blue_bttn">
						<a href="#" class="blue_bttn_style">Try Now</a>
						<div class="clear"></div>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		<div class="mid_section2">
			<div class="ms2_inner">
				<div class="ms2_heading">
					<div class="ms2_htext">
						<h1>Features</h1>
					</div>
					<div class="ms2_sub_text">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
						exercitation</div>
				</div>
				<div class="ms2_fwrap">
					<div class="ms2_fbox">
						<div class="ms2_fbox_icon">
							<div class="ms2_icon_inner">
								<img src="${pageContext.request.contextPath}/resources/assets/images/feature_1.png">
							</div>
						</div>
						<div class="ms2_contet">
							<div class="ms2_cheading">Feature 1</div>
							<div class="ms2_ctext">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et exercitation</div>
						</div>
					</div>
					<div class="ms2_fbox">
						<div class="ms2_fbox_icon">
							<div class="ms2_icon_inner">
								<img src="${pageContext.request.contextPath}/resources/assets/images/feature_2.png">
							</div>
						</div>
						<div class="ms2_contet">
							<div class="ms2_cheading">Feature 2</div>
							<div class="ms2_ctext">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et exercitation</div>
						</div>
					</div>
					<div class="ms2_fbox">
						<div class="ms2_fbox_icon">
							<div class="ms2_icon_inner">
								<img src="${pageContext.request.contextPath}/resources/assets/images/feature_3.png">
							</div>
						</div>
						<div class="ms2_contet">
							<div class="ms2_cheading">Feature 3</div>
							<div class="ms2_ctext">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et exercitation</div>
						</div>
					</div>
					<div class="ms2_fbox">
						<div class="ms2_fbox_icon">
							<div class="ms2_icon_inner">
								<img src="${pageContext.request.contextPath}/resources/assets/images/feature_4.png">
							</div>
						</div>
						<div class="ms2_contet">
							<div class="ms2_cheading">Feature 4</div>
							<div class="ms2_ctext">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et exercitation</div>
						</div>
					</div>
				</div>
				<div class="ms2_buttton">
					<div class="blue_bttn">
						<a href="#" class="blue_bttn_style">More features</a>
						<div class="clear"></div>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		<div class="mid_section3">
			<div class="ms3_inner">
				<div class="ms2_heading">
					<div class="ms2_htext">
						<h1>Testimonial</h1>
					</div>
					<div class="ms2_sub_text">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
						exercitation</div>
					<div class="testi_wrap">
						<div class="testi_inner">
							<div class="cd-testimonials-wrapper cd-container">
								<ul class="cd-testimonials">
									<li>
										<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Lorem ipsum dolor sit amet, consectetur adipisicing elit,
											sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna
											aliqua.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
										<div class="cd-author">
											<img src="${pageContext.request.contextPath}/resources/images/avatar-1.jpg" alt="Author image">
											<ul class="cd-author-info">
												<li>MyName</li>
												<li>CEO, AmberCreative</li>
											</ul>
										</div>
									</li>
									<li>
										<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Necessitatibus ea, perferendis error repudiandae numquam dolor fuga temporibus. Unde omnis, consequuntur.</p>
										<div class="cd-author">
											<img src="${pageContext.request.contextPath}/resources/images/avatar-2.jpg" alt="Author image">
											<ul class="cd-author-info">
												<li>MyName</li>
												<li>Designer, CodyHouse</li>
											</ul>
										</div>
									</li>
									<li>
										<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quam totam nulla est, illo molestiae maxime officiis, quae ad, ipsum vitae deserunt molestias eius alias.</p>
										<div class="cd-author">
											<img src="${pageContext.request.contextPath}/resources/images/avatar-3.jpg" alt="Author image">
											<ul class="cd-author-info">
												<li>MyName</li>
												<li>CEO, CompanyName</li>
											</ul>
										</div>
									</li>
								</ul>
								<!-- cd-testimonials -->
								<a href="#0" class="cd-see-all">See all</a>
							</div>
							<!-- cd-testimonials-wrapper -->
							<div class="cd-testimonials-all">
								<div class="cd-testimonials-all-wrapper">
									<ul>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Odit totam saepe iste maiores neque animi molestias nihil illum nisi temporibus.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-1.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Labore nostrum nisi, doloremque error hic nam nemo doloribus porro impedit perferendis. Tempora, distinctio hic suscipit. At
												ullam eaque atque recusandae modi fugiat voluptatem laborum laboriosam rerum, consequatur reprehenderit omnis, enim pariatur nam, quidem, quas vel reiciendis aspernatur consequuntur.
												Commodi quasi enim, nisi alias fugit architecto, doloremque, eligendi quam autem exercitationem consectetur.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-2.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Exercitationem quibusdam eveniet, molestiae laborum voluptatibus minima hic quasi accusamus ut facere, eius expedita,
												voluptatem? Repellat incidunt veniam quaerat, qui laboriosam dicta. Quidem ducimus laudantium dolorum enim qui at ipsum, a error.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-3.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Libero voluptates officiis tempore quae officia! Beatae quia deleniti cum corporis eos perferendis libero reiciendis nemo
												iusto accusamus, debitis tempora voluptas praesentium repudiandae laboriosam excepturi laborum, nisi optio repellat explicabo, incidunt ex numquam. Ullam perferendis officiis harum
												doloribus quae corrupti minima quia, aliquam nostrum expedita pariatur maxime repellat, voluptas sunt unde, inventore.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-4.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Odit totam saepe iste maiores neque animi molestias nihil illum nisi temporibus.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-5.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Perspiciatis quia quas, quis illo adipisci voluptate ex harum iste commodi nulla dolor. Eius ratione quod ab!</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-6.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Consequatur, dignissimos iure rem fugiat consequuntur officiis.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-1.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. At temporibus tempora necessitatibus reiciendis provident deserunt maxime sit id. Dicta aut voluptatibus placeat quibusdam
												vel, dolore.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-2.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Corporis iusto sapiente, excepturi velit, beatae possimus est tenetur cumque fugit tempore dolore fugiat! Recusandae, vel
												suscipit? Perspiciatis non similique sint suscipit officia illo, accusamus dolorum, voluptate vitae quia ea amet optio magni voluptatem nemo, natus nihil.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-3.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolor quasi officiis pariatur, fugit minus omnis animi ut assumenda quod commodi, ad a alias maxime unde suscipit magnam,
												voluptas laboriosam ipsam quibusdam quidem, dolorem deleniti id.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-4.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. At temporibus tempora necessitatibus reiciendis provident deserunt maxime sit id. Dicta aut voluptatibus placeat quibusdam
												vel, dolore.Lorem ipsum dolor sit amet, consectetur adipisicing elit. At temporibus tempora necessitatibus reiciendis provident deserunt maxime sit id. Dicta aut voluptatibus placeat
												quibusdam vel, dolore.Lorem ipsum dolor sit amet, consectetur adipisicing elit. At temporibus tempora necessitatibus reiciendis provident deserunt maxime sit id. Dicta aut voluptatibus
												placeat quibusdam vel, dolore.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-5.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
										<li class="cd-testimonials-item">
											<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Atque tempore ipsam, eos suscipit nostrum molestias reprehenderit, rerum amet cum similique a, ipsum soluta delectus
												explicabo nihil repellat incidunt! Minima magni possimus mollitia deserunt facere, tempore earum modi, ea ipsa dicta temporibus suscipit quidem ut quibusdam vero voluptatibus nostrum
												excepturi explicabo nulla harum, molestiae alias. Ab, quidem rem fugit delectus quod.</p>
											<div class="cd-author">
												<img src="${pageContext.request.contextPath}/resources/images/avatar-6.jpg" alt="Author image">
												<ul class="cd-author-info">
													<li>MyName</li>
													<li>CEO, CompanyName</li>
												</ul>
											</div>
											<!-- cd-author -->
										</li>
									</ul>
								</div>
								<!-- cd-testimonials-all-wrapper -->
								<a href="#0" class="close-btn">Close</a>
							</div>
							<!-- cd-testimonials-all -->
						</div>
						<div class="clear"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="footer_wrap">© ${year} All rights reserved.</div>
	</div>
</div>
<script src="<c:url value="/resources/assets/js-core/jquery.flexslider-min.js"/>" /></script>
<script src="<c:url value="/resources/assets/js-core/main.js"/>" /></script>