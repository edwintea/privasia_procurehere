$(document).ready(function () {
    if ($('#canEditId').val() === "true") {
        if ($("#allowIndCatId").is(':checked')) {
            $("#minCategories").prop('readonly', false)
            $("#minCategories").removeClass('disable-input')
            $('#minCategories').attr('data-validation', 'required min_ind_cat positive greater_than_zero');
            $("#maxCategories").prop('readonly', false)
            $("#maxCategories").removeClass('disable-input')
            $('#maxCategories').attr('data-validation', 'required max_ind_cat positive');
            if ($("#minCategories").val() === "") {
                $("#minCategories").val(1);
            }
            if ($("#maxCategories").val() === "") {
                $("#maxCategories").val(2);
            }
        }

        if ($("#publishedProfileId").is(':checked')) {
            $('#publishedProfileCommunicationEmail').prop('readonly', false);
            $('#publishedProfileCommunicationEmail').attr('data-validation', 'length required email');
            $("#publishedProfileCommunicationEmail").removeClass('disable-input')
            $('#publishedProfileContactNumber').prop('readonly', false);
            $("#publishedProfileContactNumber").removeClass('disable-input')
            $('#publishedProfileContactPerson').prop('readonly', false);
            $("#publishedProfileContactPerson").removeClass('disable-input')
            $('#publishedProfileContactPerson').attr('data-validation', 'length required');
            $('#publishedProfileWebsite').prop('readonly', false);
            $("#publishedProfileWebsite").removeClass('disable-input')
            $('#publishedProfileInfoToSuppliers').prop('readonly', false);
            $("#publishedProfileInfoToSuppliers").removeClass('disable-input')
        }
    } else {
        $('#publishedProfileId').prop('disabled', true);
        $('#allowIndCatId').prop('disabled', true);
    }
});

$("#buyerProfileSectionTabId").click(function () {
    $("#buyerProfileMaintenanceSectionTabId").addClass('tab-section-inactive');
    $("#buyerProfileSectionTabId").removeClass('tab-section-inactive');
    $("#buyerProfileSectionId").show();
    $("#buyerProfileMaintenanceSectionId").hide();
});

$("#buyerProfileMaintenanceSectionTabId").click(function () {
    $("#buyerProfileMaintenanceSectionTabId").removeClass('tab-section-inactive');
    $("#buyerProfileSectionTabId").addClass('tab-section-inactive');
    $("#buyerProfileSectionId").hide();
    $("#buyerProfileMaintenanceSectionId").show();
});

$("#allowIndCatId").click(function () {
    if ($("#allowIndCatId").is(':checked')) {
        $("#minCategories").prop('readonly', false);
        $("#minCategories").removeClass('disable-input');
        $("#maxCategories").prop('readonly', false);
        $("#maxCategories").removeClass('disable-input');
        $('#maxCategories').attr('data-validation', 'required max_ind_cat positive');
        $('#minCategories').attr('data-validation', 'required min_ind_cat positive greater_than_zero');
        if ($("#minCategories").val() === "") {
            $("#minCategories").val(1);
        }
        if ($("#maxCategories").val() === "") {
            $("#maxCategories").val(2);
        }
    } else {
        $("#minCategories").prop('readonly', true);
        $("#minCategories").val(null);
        $("#minCategories").addClass('disable-input');
        $('#minCategories').attr('data-validation', 'min_ind_cat positive greater_than_zero');
        $("#maxCategories").prop('readonly', true);
        $("#maxCategories").val(null);
        $("#maxCategories").addClass('disable-input');
        $('#maxCategories').attr('data-validation', 'max_ind_cat positive');
    }
});

$("#publishedProfileId").click(function () {
    if ($("#publishedProfileId").is(':checked')) {
        $('#publishedProfileCommunicationEmail').prop('readonly', false);
        $("#publishedProfileCommunicationEmail").removeClass('disable-input');
        $('#publishedProfileContactNumber').prop('readonly', false);
        $("#publishedProfileContactNumber").removeClass('disable-input');
        $('#publishedProfileContactPerson').prop('readonly', false);
        $("#publishedProfileContactPerson").removeClass('disable-input');
        $('#publishedProfileWebsite').prop('readonly', false);
        $("#publishedProfileWebsite").removeClass('disable-input');
        $('#publishedProfileInfoToSuppliers').prop('readonly', false);
        $("#publishedProfileInfoToSuppliers").removeClass('disable-input');
        $('#publishedProfileContactPerson').attr('data-validation', 'length required');
        $('#publishedProfileCommunicationEmail').attr('data-validation', 'length email required');
    } else {
        $('#publishedProfileContactPerson').attr('data-validation', 'length');
        $('#publishedProfileCommunicationEmail').attr('data-validation', 'length email');
        $("#confirmRemoveEvent").show();
        $("#confirmRemoveEvent").removeClass('fade');
    }
});

$("#removePublishedProfile").click(function () {
    $('#publishedProfileCommunicationEmail').prop('readonly', true);
    $("#publishedProfileCommunicationEmail").addClass('disable-input')
    $('#publishedProfileContactNumber').prop('readonly', true);
    $("#publishedProfileContactNumber").addClass('disable-input')
    $('#publishedProfileContactPerson').prop('readonly', true);
    $("#publishedProfileContactPerson").addClass('disable-input')
    $('#publishedProfileWebsite').prop('readonly', true);
    $("#publishedProfileWebsite").addClass('disable-input')
    $('#publishedProfileInfoToSuppliers').prop('readonly', true);
    $("#publishedProfileInfoToSuppliers").addClass('disable-input')
    $("#confirmRemoveEvent").hide();
});

$("#cancel").click(function () {
    $("#confirmRemoveEvent").hide();
    $('#publishedProfileId').click();
});

$("#cross").click(function () {
    $("#confirmRemoveEvent").hide();
    $('#publishedProfileId').click();
});