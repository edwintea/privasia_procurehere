$(function() {
    let resetError=()=>{
        $('#cancelReason').val('').css('border-color', 'black');
        $('.cancelPo').removeAttr('disabled','disabled');
        $('.form-error').hide();
        $('#reviseJustification').val('').css('border-color', 'black');
        $('.suspendPo').removeAttr('disabled','disabled');
        $('.error').val('').css('border-color', 'black');
    }
    $('.modal_cancel_po').on('shown.bs.modal', function () { //modal cancel po event
        resetError();
    });

    $('.modal_cancel_po').on('hidden.bs.modal', function () { //modal cancel po event
        resetError();
    });

    $('.confirm_revise_po').on('shown.bs.modal', function () { //modal suspend po event
        resetError();
    });

    $('.confirm_revise_po').on('hidden.bs.modal', function () { //modal suspend po event
        resetError();
    });

    $('.teamMemberListPopup').on('hidden.bs.modal', function () { //modal team member unload event
        getPoTeamMembers();
    });

    $('.modal_purchase_item').on('hidden.bs.modal', function () { //modal in purchaseItem
        resetError();
        if(!isError){
            setTimeout(function(){
                window.location.reload();
            },1000)
        }
    });

    $('.modal_purchase_item').on('shown.bs.modal', function () { //modal in purchaseItem
        let pricePerUnit = $('#pricePerUnit').val();
        if(pricePerUnit ==''){
            $('#pricePerUnit').val(1)
        }
        resetError();
    });
})