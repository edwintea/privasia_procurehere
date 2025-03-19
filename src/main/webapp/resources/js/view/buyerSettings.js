
$('#switchToGeneralSettingtId').on('click', function(e) {
    $(this).parent().addClass('active');
    $("#switchToEventSettingsId").parent().removeClass('active');
    $("#idGeneralSettings").show();
    $("#eventSettingsViewId").hide();
    
    $("#idGeneral").removeClass('hide');
    $("#idEvent").addClass('hide');
});

$('#switchToEventSettingsId').on('click', function(e) {
    $(this).parent().addClass('active');
    $("#switchToGeneralSettingtId").parent().removeClass('active');
    $("#idGeneralSettings").hide();
    $("#eventSettingsViewId").show();
    
    $("#idGeneral").addClass('hide');
    $("#idEvent").removeClass('hide');
    
});


