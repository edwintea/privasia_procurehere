<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="page-content">
	 <div class="container">
		<!-- pageging  block -->
		  <ol class="breadcrumb">
                        <li><a href="Buyer_Dashboard.html">Dashboard</a></li>
                        <li class="active">Approval Route</li>
          </ol>
  <section class="create_list_sectoin">
  <c:url var="saveApprovalRoute" value="saveApprovalRoute" />
		<form:form id="approvalRouteRegistration" cssClass="bordered-row" method="post" action="${pageContext.request.contextPath}/buyer/approval/saveApprovalRoute" modelAttribute="approvalRoute">
		<div class="Section-title title_border gray-bg mar-b20">
             <h2 class="trans-cap supplier">PR Approval Route</h2>
		</div>
		<div class="panel sum-accord">
		      <div class="panel-heading">
                           <h4 class="panel-title"> <a class="bgnone-ar" href="#"> Approval Route </a></h4>
              </div>
			  <div id="collapseOne" class="panel-collapse collapse in">
				<div class="panel-body pad_all_15  border-bottom">
					<div class="main-panal-box-main">
						<div class="form-group">
						 <div class="row">
							<div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
								<label class="app-la">Route Name</label>
							</div>
							<div class="col-md-4 col-sm-6 col-xs-12 col-lg-3">
								<form:input path="routeName" data-validation-length="1-150" data-validation="required length" cssClass="form-control " id="idrouteName" />
							</div>
						</div>
						</div>
						
						<div class="form-group">
						 <div class="row">
							<div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
								<label class="app-la">Budget Limit</label>
							</div>
							<div class="col-md-4 col-sm-6 col-xs-12 col-lg-3">
								<form:select path="limitType" cssClass="form-control chosen-select disablesearch" id="idLimitType">
								<form:options items="${limitTypeList}" itemLabel="value" />
								</form:select>
							</div>
							<div class="col-md-4 col-sm-6 col-xs-12 col-lg-3">
										<form:input id="idBudgetLimit" path="limitValue" data-validation-length="1-14" data-validation="required length" cssClass="form-control " />
							</div>
						</div>
						</div>
						<div class="form-group">
								<div class="row">
									<div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
										<label class="app-la">Approval Status </label>
									</div>
									<div class="col-md-4 col-sm-6 col-xs-12 col-lg-3">
										<form:select path="status" cssClass="form-control chosen-select disablesearch" id="idLimitType">
											<form:options items="${statusList}" />
										</form:select>
									</div>
								</div>
							</div>
						<div class="form-group">
								<div class="row">
									<div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
										<label class="app-la">Approval Level </label>
									</div>
									<div class="col-md-4 col-sm-6 col-xs-12 col-lg-3">
										<button onclick="createLevels();" id="b1" class="btn add-more" type="button">+</button>
									</div>
								</div>
						</div>
					</div>
				</div>
			  </div>
		</div>
	  <div id="levelContainer">
	  
	  <c:forEach items="${approvalRoute.approvals}" var="approval" varStatus="status">
		<!--
                                <div class="panel sum-accord appLvl">
                                    <div class="panel-heading">
                                        <div class="panel-title rmicon"> 
										<span class=" pull-right">       
											<a class="removeLevl bgnone-ar"><span class="glyphicon glyphicon-remove"></span></a>
										</span>
										<h4>Level <span class="levelNumber">${status.index+1}</span></h4>
										</div>
                                    </div>
                                    <div id="collapseOne" class="panel-collapse collapse in">
                                        <div class="panel-body pad_all_15  border-bottom">
                                            <div class="main-panal-box-main">

                                                <div class="form-group">
                                                    <div class="row">
                                                        <div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
                                                            <label>Approvers Type</label>

                                                        </div>

                                                        <div class="col-md-6 col-sm-6 col-xs-12">

                                                            <label>
                                                                <input onclick="" value="1" type="radio" id="show" name="example-radio" class="custom-radio filterMeeting">Individual Approver
                                                            </label>

                                                            <label class="marg-left-20">
                                                                <input onclick="" value="2" type="radio" id="show1" name="example-radio" class="custom-radio filterMeeting">Multiple Approver
                                                            </label>

                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="form-group">
                                                    <div class="row">
                                                        <div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
                                                            <label>Approvers</label>

                                                        </div>

                                                        <div class="col-md-4 col-sm-6 col-xs-12 col-lg-3">
                                                            <div class="invite-supplier delivery-address">

                                                                <div class="role-upper">
                                                                    <div class="role-upper-inner">
                                                                        TEMPLATE 0001 <a href="#">X</a>
                                                                    </div>

                                                                    <div class="role-upper-inner">
                                                                        TEMPLATE 0002 <a href="#">X</a>
                                                                    </div>

                                                                </div>
                                                                <div class="chk_scroll_box">
                                                                    <div class="scroll_box_inner">

                                                                        <div class="role-main">

                                                                            <div class="role-bottom">
                                                                                <ul>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="Jhon" class="custom-checkbox" id="inlineCheckbox11">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>Jhon</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="David" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>David</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="Ajay" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>Ajay</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="Androw" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>Androw</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="TEMPLATE 0005" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>TEMPLATE 0005</span>
                                                                                    </li>
                                                                                </ul>
                                                                            </div>

                                                                        </div>
                                                                    </div>

                                                                </div>

                                                            </div>

                                                        </div>

                                                    </div>
                                                </div>

                                            </div>

                                        </div>

                                    </div>
                                </div>
 -->
	  </c:forEach>
	  </div>
		
		
		<!------ Level Frame ----->
				  <div id="levelContent" style="display:none">

                                <div class="panel sum-accord">
                                    <div class="panel-heading">
                                        <div class="panel-title rmicon"> 
										<span class=" pull-right">       
											<a class="removeLevl bgnone-ar"><span class="glyphicon glyphicon-remove"></span></a>
										</span>
										<h4>Level <span class="levelNumber">1</span></h4>
										</div>
                                    </div>
                                    <div id="collapseOne" class="panel-collapse collapse in">
                                        <div class="panel-body pad_all_15  border-bottom">
                                            <div class="main-panal-box-main">

                                                <div class="form-group">
                                                    <div class="row">
                                                        <div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
                                                            <label>Approvers Type</label>

                                                        </div>

                                                        <div class="col-md-6 col-sm-6 col-xs-12">

                                                            <label>
                                                                <input onclick="" value="1" type="radio" id="show" name="example-radio" class="custom-radio filterMeeting">Individual Approver
                                                            </label>

                                                            <label class="marg-left-20">
                                                                <input onclick="" value="2" type="radio" id="show1" name="example-radio" class="custom-radio filterMeeting">Multiple Approver
                                                            </label>

                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="form-group">
                                                    <div class="row">
                                                        <div class="col-md-3 col-sm-4 col-xs-12 col-lg-2">
                                                            <label>Approvers</label>

                                                        </div>

                                                        <div class="col-md-4 col-sm-6 col-xs-12 col-lg-3">
                                                            <div class="invite-supplier delivery-address">

                                                                <div class="role-upper">
                                                                    <div class="role-upper-inner">
                                                                        TEMPLATE 0001 <a href="#">X</a>
                                                                    </div>

                                                                    <div class="role-upper-inner">
                                                                        TEMPLATE 0002 <a href="#">X</a>
                                                                    </div>

                                                                </div>
                                                                <div class="chk_scroll_box">
                                                                    <div class="scroll_box_inner">

                                                                        <div class="role-main">

                                                                            <div class="role-bottom">
                                                                                <ul>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="Jhon" class="custom-checkbox" id="inlineCheckbox11">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>Jhon</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="David" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>David</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="Ajay" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>Ajay</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="Androw" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>Androw</span>
                                                                                    </li>
                                                                                    <li>
                                                                                        <div class="checkbox checkbox-info" style="margin: 0px;">
                                                                                            <label>

                                                                                                <input type="checkbox" value="TEMPLATE 0005" class="custom-checkbox" id="inlineCheckbox115">
                                                                                            </label>
                                                                                        </div>
                                                                                        <span>TEMPLATE 0005</span>
                                                                                    </li>
                                                                                </ul>
                                                                            </div>

                                                                        </div>
                                                                    </div>

                                                                </div>

                                                            </div>

                                                        </div>

                                                    </div>
                                                </div>

                                            </div>

                                        </div>

                                    </div>
                                </div>

                            </div>
		<!------ Level Frame END----->
		
		
		
		
	  <div class="float-left width-100">
		<div class="row">
		<div class="col-md-12">
		<button class="btn btn-info hvr-pop hvr-rectangle-out ph_btn" type="submit">Save</button>
		</div>
		</div>
	  </div>
		
		
	</form:form>
  </section>
		
		
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>


 <script>
$(document).on( "click", ".removeLevl", function() {
	
	  $(this).closest(".appLvl").remove();
	 ///// Reindex
	 $(".appLvl").each(function(i,v){
		$(this).find("#show").attr("name", "approvals[" + i+"].approvalType");
		$(this).find("#show1").attr("name", "approvals[" + i+"].approvalType");
		$(this).find(".levelNumber").text((i + 1));
	});
});
function createLevels() {
   // $("#levelContainer").html('');
	i = $(".appLvl").length;

			$("#levelContent .levelNumber").text((i + 1));
			$("#levelContent .panel input#show").attr("name", "approvals[" + i+"].approvalType");
			$("#levelContent .panel input#show1").attr("name", "approvals[" + i+"].approvalType");
			$("#levelContent .panel").clone().appendTo("#levelContainer").addClass("appLvl");
			$('#levelContainer input[type="checkbox"].custom-checkbox').uniform();
			$('#levelContainer input[type="radio"].custom-radio').uniform();
			$('#levelContainer .custom-select').uniform();

			$("#levelContainer .selector").append('<i class="glyph-icon icon-caret-down"></i>');

			$('#levelContainer .checker span').append('<i class="glyph-icon icon-check"></i>');
			$('#levelContainer .radio span').append('<i class="glyph-icon icon-circle"></i>');
			$('.role-bottom').find('input[type="checkbox"]').change(function() {

				var roleUperBlok = '';
				$(this).parents('.role-bottom').find('input[type="checkbox"]:checked').each(function() {
					var roleValue = $(this).val();
					roleUperBlok += '<div class="role-upper-inner">' + roleValue + '<a href="#" data-val="' + roleValue + '">X</a></div>';
				});
				$(this).parents('.chk_scroll_box').prev('.role-upper').html(roleUperBlok);
			});

			/* this code for refresh multiselcte checkbox on load time */
			$('.role-bottom').find('input[type="checkbox"]').trigger('change');

			$(document).delegate('.role-upper a', 'click', function(e) {

				e.preventDefault();
				var checkboxVal = $(this).attr('data-val');
				var checkObj = $(this).parents('.role-upper').next('.chk_scroll_box').find('input[type="checkbox"][value="' + checkboxVal + '"]');

				checkObj.prop('checked', false);
				$(this).parent('.role-upper-inner').remove();
				$.uniform.update(checkObj);
			});
	
	
	$("#levelContent .panel input#show").attr("name", "example-radio");
	$("#levelContent .panel input#show1").attr("name", "example-radio");
}
		
function reIndexLevel(){
	
}		
	
</script>



<script>
	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idBudgetLimit').mask('999,99,99,99', {
			placeholder : "e.g. 1000"
		});
	});
</script>
<script>
	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});

		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#tags").autocomplete({
			source : availableTags
		});
		$("#tagres").autocomplete({
			source : availableTags
		});

		$.validate({
			lang : 'en'
		});

		$(".correction-ancher").click(function() {
			$(this).parent().parent().next(".correction-textarea").show('fast');
		});

		$(".correction-ancher").click(function() {
			$(this).parent().parent().hide('fast');
		});

	});
</script>
<script>
	$(document).ready(function() {

		$('.role-bottom').find('input[type="checkbox"]').change(function() {

			var roleUperBlok = '';
			$(this).parents('.role-bottom').find('input[type="checkbox"]:checked').each(function() {
				var roleValue = $(this).val();
				roleUperBlok += '<div class="role-upper-inner">' + roleValue + '<a href="#" data-val="' + roleValue + '">X</a></div>';
			});
			$(this).parents('.chk_scroll_box').prev('.role-upper').html(roleUperBlok);
		});

		/* this code for refresh multiselcte checkbox on load time */
		$('.role-bottom').find('input[type="checkbox"]').trigger('change');

		$(document).delegate('.role-upper a', 'click', function(e) {

			e.preventDefault();
			var checkboxVal = $(this).attr('data-val');
			var checkObj = $(this).parents('.role-upper').next('.chk_scroll_box').find('input[type="checkbox"][value="' + checkboxVal + '"]');

			checkObj.prop('checked', false);
			$(this).parent('.role-upper-inner').remove();
			$.uniform.update(checkObj);
		});

		$(".custom-select").uniform();
		$(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
	});
</script>
<style>
.bgnone-ar {
	background: none !important;
}

#levelContainer div.radio[id^="uniform-"]>span {
	margin-top: 0;
}
.panel {
    float: left;
    width: 100%;
}
.app-la {
	line-height: 30px;
}
.rmicon {
	 padding: 20px 15px;
}
.panel.sum-accord .panel-title .removeLevl {
	padding:0;
}
#approvalRouteRegistration .panel.sum-accord .panel-heading{
	 /* position: inherit; */
}
</style>