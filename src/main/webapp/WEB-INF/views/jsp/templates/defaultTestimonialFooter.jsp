<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="year" value="${now}" pattern="yyyy" />
<style>
.pset_footer {
    width: 100%;
    float: left;
    text-align: center;
    color: #fff;
    padding: 15px 0;
    margin: 0;
    font-size: 15px;
    background: #0083c9;
}
</style>
<%-- <div class="mid_section3">
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
								<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed
									do eiusmod tempor incididunt ut labore et dolore magna aliqua.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Lorem
									ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
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
										ullam eaque atque recusandae modi fugiat voluptatem laborum laboriosam rerum, consequatur reprehenderit omnis, enim pariatur nam, quidem, quas vel reiciendis aspernatur consequuntur. Commodi
										quasi enim, nisi alias fugit architecto, doloremque, eligendi quam autem exercitationem consectetur.</p>
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
										iusto accusamus, debitis tempora voluptas praesentium repudiandae laboriosam excepturi laborum, nisi optio repellat explicabo, incidunt ex numquam. Ullam perferendis officiis harum doloribus
										quae corrupti minima quia, aliquam nostrum expedita pariatur maxime repellat, voluptas sunt unde, inventore.</p>
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
									<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Atque tempore ipsam, eos suscipit nostrum molestias reprehenderit, rerum amet cum similique a, ipsum soluta delectus explicabo
										nihil repellat incidunt! Minima magni possimus mollitia deserunt facere, tempore earum modi, ea ipsa dicta temporibus suscipit quidem ut quibusdam vero voluptatibus nostrum excepturi
										explicabo nulla harum, molestiae alias. Ab, quidem rem fugit delectus quod.</p>
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
</div> --%>
<div class="pset_footer">&copy; ${year} All rights reserved</div>
<div class="modal fade" id="securitySession" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<!-- <button type="button" class="close" data-dismiss="modal">&times;</button> -->
				<h4 class="modal-title">Session Expire Extend</h4>
			</div>
			<div class="modal-body">
				<p>Some text in the modal.</p>
				<p class="timerSection">
					<span class="timeMinuts">00</span>
					<span>:</span>
					<span class="timeSeconds">00</span>
				</p>
				<p>
					<button type="button" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out extandsession">Extend session</button>
					<button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium expiresession">Logout</button>
				</p>
			</div>
			<!-- <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div> -->
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		//$("#myBtn").click(function(){
		//$("#securitySession").modal();
		//});
		/* $('.for-clander-view').change(function(){
			alert($(this).val());
		}); */
	});
</script>