<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="Invited-Supplier-List import-supplier white-bg">
    <div class="meeting2-heading">
        <h3><spring:message code="rfi.createrfi.approvalroute.label.gr" /></h3>
    </div>


    <div id="apprTab" class="pad_all_15 collapse in float-leftwidth-100position-relative">
        <div class="gr-dynamic-approval form-group">
            <div class="row marg-bottom-10 pad_all_20">
                <div class="align-left col-md-5"></div>
                <div class="col-md-9 ">
                    <div class="check-wrapper first">
                        <spring:message code="grtemplate.label.visible" var="visible" />
                        <form:checkbox path="approvalGrVisible" class="custom-checkbox approvalGrCheck" title="${visible}" label="${visible}" />
                    </div>
                    <div class="check-wrapper">
                        <spring:message code="grtemplate.label.read.only" var="read" />
                        <form:checkbox path="approvalGrReadOnly" class="custom-checkbox readOnlyGrCheck" title="${read}" label="${read}" />
                    </div>
                    <div class="form_field  ${isTemplateUsed ? 'disabled':''}">
                        <div class="approvalcount row">
                            <div class="col-md-5">
                                <label class="control-label-gr"> <spring:message code="grtemplate.label.minimum" />
                                </label>
                            </div>

                            <div class="col-sm-3 col-md-5 col-xs-5">
                                <div class="input-prepend input-group">
                                    <spring:message code="sourcingForm.approval.place.approvalCount" var="approversCount" />
                                    <form:input path="minimumGrApprovalCount" autocomplete="off" id="minimumGrApprovalCount" placeholder="Enter Approval Count" class="form-control autoSave" type="number" min="0" max="9" />
                                    <span class="error" id="minimumGrCountError"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row cloneready marg-bottom-10 pad_all_20">
                <div class="align-left col-md-5">
                    <!--<label class="li-32 marg-none app-la">Add Approvers</label> -->
                </div>
                <div class="col-md-9">
                    <spring:message code="tooltip.add.approval.level" var="addapproval" />
                    <button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="grAddSelect" data-toggle="tooltip" data-placement="top" title="${addapproval}"><spring:message code="application.addapproval.button" /></button>
                </div>
                <span class="error-message-gr"></span>
            </div>
            <!-- For LOOP -->
            <c:if test="${not empty prTemplate.grApprovals}">
                <c:forEach items="${prTemplate.grApprovals}" var="approval" varStatus="status">
                    <div id="new-gr-approval-${status.index + 1}" class="row new-gr-approval">
                        <div class="align-right  col-md-3 ">
                            <label class="level"><spring:message code="rfi.createrfi.level" /> ${status.index + 1}</label>
                        </div>
                        <div class="col-md-5 ${assignedCount > 0 ? 'disabled':''}">
                            <form:select data-validation="required" id="grMultipleSelectExample-${status.index}" path="grApprovals[${status.index}].approvalUsers" cssClass="tagTypeMultiSelect user-list-all chosen-select  ${approval.done || (!empty prTemplate ? (tf:grApprovalReadonly(prTemplate)) : false) ? 'disabled':''}"
                                         multiple="multiple">
                                <c:forEach items="${userList}" var="usr">
                                    <c:if test="${usr.id == '-1' }">
                                        <form:option value="-1" label="${usr.user.name}" disabled="true" />
                                    </c:if>
                                    <c:if test="${usr.id != '-1' }">
                                        <form:option value="${usr.id}" label="${usr.user.name}" />
                                    </c:if>
                                </c:forEach>
                            </form:select>
                        </div>
                        <div class="col-md-4 pad0">
                            <div class="btn-address-field pt-0">
                                <button type="button" class="btn dropdown-toggle" data-toggle="dropdown">
                                    <i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
                                </button>
                                <ul class="dropdown-menu dropup mid-width">

                                    <li><a href="javascript:void(0);" class="small checkbox-check" tabIndex="-1">
                                        <spring:message code="application.any" var="any" />
                                        <form:checkbox path="grApprovals[${status.index}].approvalType" value="OR" cssClass="approval_gr_condition" label="${any}"></form:checkbox>
                                    </a></li>

                                    <li><a href="javascript:void(0);" class="small checkbox-check" tabIndex="-1">
                                        <spring:message code="buyercreation.all" var="all"/>
                                        <form:checkbox path="grApprovals[${status.index}].approvalType" value="AND" cssClass="approval_gr_condition" label="${all}"></form:checkbox>
                                    </a></li>
                                </ul>
                                <button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeGrApproval">
                                    <i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>
    </div>



    <style>
        .pt-0 {
            padding-top: 1px !important;
            padding-left: 10px;
        }

        .select2-container-multi {
            margin-top: 10px !important;
        }

        .row.gr-dynamic-approval {
            margin-top: 10px;
        }

        .new-gr-approval label {
            float: right;
            padding: 15px;
        }

        .checkbox-check label{
            float: none;
            padding: 6px 3px 0;
            font-family: 'open_sansregular',"Helvetica Neue",Helvetica,Arial,sans-serif;
            margin-bottom: 0px !important;

        }
        .mid-width{
            min-width: 170px !important;
        }

        .btn-address-field {
            padding-top: 10px;
            margin-left: -15px;
        }

        .removeGrApproval {
            margin-left: 10px;
        }

        .control-label-gr{
            margin-top: 12px;
        }

        .cloneready {
            margin-bottom: 10px;
        }

        .collapseable  .meeting2-heading .checkbox-primary label {
            color: #636363 !important;
            float: left;
            font-size: 14px;
            margin-bottom: 15px;
            padding: 20px 0 18px 15px !important;
            width: 100%;
        }

        .checkbox.inline.no_indent input {
            margin-left: 10px;
            padding-top: 0;
        }

        #grAddSelect {
            bottom: 40px;
            left: 10px;
        }
    </style>
    <script type="text/JavaScript">
        var countApproval=${fn:length(prTemplate.grApprovals)}
        var grIndex;
        grIndex = ${fn:length(prTemplate.grApprovals) + 1};
        $(document).ready(function() {

            $(document).delegate('.small', 'click', function(e) {
                //console.log($(this).find(".approval_gr_condition"));
                $(this).find(".approval_gr_condition").trigger("click");
            });

            //	document.getElementById("grAddSelect").addEventListener("click", function(e) {
            $("#grAddSelect").click(function(e) {
                e.preventDefault();
                var template = '<div id="new-gr-approval-'+grIndex+'" class="row new-gr-approval">';
                template += '<div class="align-right  col-md-3 "><label class="level"><spring:message code="rfi.createrfi.level"/> ' + grIndex + '</label></div>';
                template += '<div class="col-md-5" id="sel">';
                template += '<select data-validation="required" id="grMultipleSelectExample-'+(grIndex - 1)+'" name="grApprovals[' + (grIndex - 1) + '].approvalUsers" class="user-list-all chosen-select tagTypeMultiSelect" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" /> multiple>';
                <c:forEach items="${templateGrApprovalUserList}" var="users">
                <c:if test="${users.id == '-1' }">
                template += '<option value="-1" disabled >${users.user.name}</option>';
                </c:if>
                <c:if test="${users.id != '-1' }">
                template += '<option value="${users.id}" >${users.user.name}</option>';
                </c:if>
                </c:forEach>
                template += '</select></div><div class="col-md-4 pad0">';
                template += '<div class="btn-address-field pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
                template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
                template += '<ul class="dropdown-menu dropup">';
                template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="grApprovals[' + (grIndex - 1) + '].approvalType" checked="checked" value="OR" class="approval_gr_condition" type="checkbox"/>&nbsp;<spring:message code="application.any" /></a></li>';
                template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="grApprovals[' + (grIndex - 1) + '].approvalType" value="AND" class="approval_gr_condition"  type="checkbox"/>&nbsp;<spring:message code="buyercreation.all" /></a></li>	</ul>';
                template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeGrApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
                template += '</div>';

                $(".gr-dynamic-approval").append(template);
                $('#grMultipleSelectExample-'+(grIndex - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });
                updateUserList('',$('#select#grMultipleSelectExample-'+(grIndex - 1)),'ALL_USER');
                grIndex++;
            });

            $(document).delegate('.removeGrApproval', 'click', function(e) {
                $(this).closest(".new-gr-approval").remove();

                $(".new-gr-approval").each(function(i, v) {
                    i++;
                    $(this).attr("id", "new-gr-approval-" + i);
                    $(this).find(".level").text('Level ' + i);

                    $(this).find(".approval_gr_condition").each(function(){
                        $(this).attr("name",'grApprovals[' +(i-1) + '].approvalType');
                    }) // checkbox name regrIndex

                    $(this).find("input[name='grApprovals[" + i + "].approvalType']").each(function(){
                        $(this).attr('name','grApprovals[' +(i-1) + '].approvalType');
                    }); //Checkbox hidden val regrIndex


                    $(this).find(".tagTypeMultiSelect").each(function(){
                        $(this).attr("name",'grApprovals[' +(i-1) + '].approvalUsers');
                        $(this).attr("id", "grMultipleSelectExample-" + ((i-1)) + "");
                    }) //select name regrIndex

                    $(this).find("input[name='grApprovals[" + i + "].approvalUsers']").each(function(){
                        $(this).attr("name",'grApprovals[' +(i-1) + '].approvalUsers');
                    }) //select name regrIndex hidden
                });
                grIndex--;
            });

            $(document).delegate('.approval_gr_condition', 'click', function(e) {

                $(this).closest('.btn-address-field').find(".approval_gr_condition").each(function(){
                    $(this).prop('checked', false);
                    //console.log($(this))
                });

                $(this).prop('checked', true);

                var current_val = $(this).val();

                if (current_val == "OR") {
                    //console.log(current_val);
                    $(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-user" aria-hidden="true"></i>');
                }
                if (current_val == "AND") {
                    //console.log(current_val);
                    $(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-users" aria-hidden="true"></i>');
                }
            });

        });

        function toggleAddApprovalButtonGr() {
            // Target the Visible and Read Only checkboxes
            var isVisible = $('.approvalGrCheck').is(':checked');
            var isReadOnly = $('.readOnlyGrCheck').is(':checked');

            // Enable or disable the Add Approval button and minimum approval count field based on the Visible checkbox
            $('#grAddSelect').prop('disabled', !isVisible);
            $('#minimumGrApprovalCount').prop('disabled', !isVisible);

            // If Visible is unchecked, reset minimumGrApprovalCount to empty and remove error
            if (!isVisible) {
                $('#minimumGrCountError').text('');
                $('#minimumGrApprovalCount').removeClass('input-error');
                isReadOnly = false;
                // Remove all approval levels except the template
                $('.new-gr-approval').remove();

                // Reset the index counter
                grIndex = 1;
            }

            if (isVisible && $('#minimumGrApprovalCount').val() === '') {
                if(parseInt(countApproval) > 0){
                    $('#minimumGrApprovalCount').val(countApproval);
                }else{
                    $('#minimumGrApprovalCount').val();
                }
                $('#minimumGrCountError').text('');
                $('#minimumGrApprovalCount').removeClass('input-error');
            }



            // Hide only the minimum approval count text field and its label if both checkboxes are either checked or unchecked
            if ((!isVisible && !isReadOnly) || (isVisible && isReadOnly)) {
                $('#minimumGrApprovalCount').hide(); // Hide the field
                $('.control-label-gr').hide(); // Hide the label
                $('.new-gr-approval').show();
            } else {
                $('#minimumGrApprovalCount').show(); // Show the field
                $('.control-label-gr').show(); // Show the label
                $('.new-gr-approval').show();
            }
        }

        function validateGrMinimumApprovalCount() {
            let selector = '#minimumGrApprovalCount';
            let errorSelector = "#minimumGrCountError";
            let approvalCount = parseInt($(selector).val());
            let min = 0;
            let max = 9;

            $(errorSelector).text('');
            $(selector).removeClass('input-error');

            if (approvalCount < min) {
                $(selector).val(min);
            } else if ((approvalCount > max) || (isNaN(approvalCount))) {
                $(errorSelector).text('The value must be a single digit (0-9).');
                $(selector).addClass('input-error');
                $(selector).val('');
                return false;
            }
            return true;
        }

        function validateGrApprovers() {
            var isValid = true;
            var errorGrMessage = '';

            // Iterate through each approval level
            $('.new-gr-approval').each(function() {
                var level = $(this).find('.level').text().split(' ')[1];
                var selectedUsers = $(this).find('select.tagTypeMultiSelect').val() || [];

                // Check for duplicate users within the same level
                var uniqueUsers = new Set();
                var duplicateUsers = false;

                selectedUsers.forEach(function(user) {
                    if (uniqueUsers.has(user)) {
                        duplicateUsers = true;
                    } else {
                        uniqueUsers.add(user);
                    }
                });

                if (duplicateUsers) {
                    isValid = false;
                    errorGrMessage = 'Duplicate users are not allowed within the same approval level.';
                    $(this).find('.error-message-gr').text(errorGrMessage);
                    $(this).find('.error-message-gr').addClass('input-error');
                } else {
                    $(this).find('.error-message-gr').text('');
                    $(this).find('.error-message-gr').removeClass('input-error');
                }
            });

            // Display error message if not valid
            if (!isValid) {
                $('#approverCountError').text(errorGrMessage);
                $('#approverCountError').addClass('input-error');
            } else {
                $('#approverCountError').text('');
                $('#approverCountError').removeClass('input-error');
            }

            console.log('Approver Validation Status:', isValid); // Debugging line
            return isValid;
        }

        function validateGrMinimumApprovalAndApprovers() {
            var isValid = true;
            var errorMessage = '';

            // Check if minimum approval count is filled
            var minimumCount = $('#minimumGrApprovalCount').val();
            console.log('Minimum Approval Count:', minimumCount); // Debugging line



            if (minimumCount && minimumCount.trim() !== '') {
                // Check if there are no approvers selected
                var approversSelected = $('.new-gr-approval').find('select.tagTypeMultiSelect').toArray().some(function(select) {
                    return $(select).val() && $(select).val().length > 0;
                });

                console.log('Approvers Selected:', approversSelected); // Debugging line



            }

            // Display error message if not valid
            if (!isValid) {
                $('#minimumGrCountError').text(errorMessage);
                $('#minimumGrApprovalCount').addClass('input-error');
            } else {
                $('#minimumGrCountError').text('');
                $('#minimumGrApprovalCount').removeClass('input-error');
            }

            console.log('Validation Result:', isValid); // Debugging line
            return isValid;
        }

        // Ensure that the function runs when the 'approvalVisible' or 'readOnly' checkboxes are changed
        $('.approvalGrCheck, .readOnlyGrCheck').change(function() {
            toggleAddApprovalButtonGr();
        });

        // Call it once when the page loads to ensure the correct initial state
        $(document).ready(function() {
            toggleAddApprovalButtonGr();
        });

        // Attach validation function to the minimum approval count field on change
        $('#minimumGrApprovalCount').on('change', function() {
            validateGrMinimumApprovalCount();
        });



        // Attach validation function to the approvers count change
        $(document).on('change', '.new-gr-approval', function() {
            validateGrApprovers();
        });

        // Handle form submission to validate minimum approval count and approvers
        $('form').on('submit', function(e) {
            if ($('#minimumGrApprovalCount').val() === '') {
                $('#minimumGrApprovalCount').val('0');
            }
            if (!validateGrMinimumApprovalCount() || !validateGrApprovers() || !validateGrMinimumApprovalAndApprovers()) {
                e.preventDefault(); // Stop form submission if validation fails
                console.log('Form submission prevented due to validation error.');
            }
        });
    </script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
    <script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>

