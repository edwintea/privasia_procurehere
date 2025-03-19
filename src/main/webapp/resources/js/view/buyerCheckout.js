var stripe = null;
var elements = null;

$(document).ready(function () {
    stripe = Stripe($('#stripePublishKey').val());
    elements = stripe.elements()
    $(document).on('submit', '#payment-form-fpx', function (event) {
        var fpxBank = elements.getElement('fpxBank');
        event.preventDefault();
        payByFpx(event, fpxBank);
    });

});


$(document).on("click", "#makeSupplierStripePayment", function (e) {
    if ((window.location.href.indexOf('userBasedBuyerCheckout') != -1 && $('#idUserBasedBuyerCheckOutForm').isValid())
        || (window.location.href.indexOf('eventBasedBuyerCheckout') != -1 && $('#idEventBasedBuyerCheckOutForm').isValid())) {
        e.preventDefault();
        $('#makeSupplierStripePayment').prop('disabled', true);
        $('#makeSupplierStripePayment').addClass('disabled');
        var amount = 0;
        if ($('#totalPriceValue').val()) {
            amount = Number($('#totalPriceValue').val());
        } else if ($('input[name=totalPrice]').val()) {
            amount = Number($('input[name=totalPrice]').val());
        }
        $('#checkoutCardAmount').html(' USD ' + amount.toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
        $('#checkoutFpxAmount').html(' USD ' + amount.toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
        $('#makePaymentModal').modal().show();
        $('#tabOneId').click();
        $('div[id=idGlobalError]').hide();
        $('div[id=idGlobalSuccess]').hide();
    }
});

$('#makePaymentModal').on('hide.bs.modal', function (e) {
    $('#makeSupplierStripePayment').prop('disabled', false);
    $('#makeSupplierStripePayment').removeClass('disabled');
    var errorMsg = document.querySelector(".sr-field-error");
    errorMsg.textContent = '';
    var fpxBank = elements.getElement('fpxBank');
    var card = elements.getElement('card');
    card ? card.clear() : '';
    fpxBank ? fpxBank.clear() : '';
    $('#makeSupplierStripePayment').prop('disabled', false);
    $('#makeSupplierStripePayment').removeClass('disabled');
});

$('#tabOneId').on('shown.bs.tab', function (e) {
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();
    var card = elements.getElement('card');
    $('#payByCardId').disabled = true;
    $('#payByCardId').addClass('disabled');
    $('#payByCardId').removeClass('btn-success');
    $('#makePaymentModal').attr('payment-mode', "card")
    var style = {
        base: {
            color: "#32325d",
            fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
            fontSmoothing: "antialiased",
            fontSize: "16px",
            "::placeholder": {
                color: "#aab7c4"
            }
        },
        invalid: {
            color: "#fa755a",
            iconColor: "#fa755a"
        }
    };
    card = elements.create("card", { style: style, hidePostalCode: true });
    card.mount("#card-element");
    card.on('change', function (event) {

        if (event.error) {
            $('#payByCardId').disabled = true;
            $('#payByCardId').removeClass('btn-success');
            $('#payByCardId').addClass('disabled');
        }

        if (event.complete) {
            $('#payByCardId').disabled = false;
            $('#payByCardId').removeClass('disabled');
            $('#payByCardId').addClass('btn-success');
        }
        var displayError = document.getElementById('card-errors');
        if (event.error) {
            displayError.textContent = event.error.message;
        } else {
            displayError.textContent = '';
        }
    });

});

$('#tabOneId').on('hidden.bs.tab', function (e) {
    var card = elements.getElement('card');
    card ? card.destroy() : '';
    document.getElementById('card-errors').textContent = '';
});

$('#tabTwoId').on('shown.bs.tab', function (e) {
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();
    var fpxBank = elements.getElement('fpxBank');
    $('#loading').show();
    $('#fpx-button').disabled = true;
    $('#fpx-button').addClass('disabled');
    $('#fpx-button').removeClass('btn-success');
    var style = { base: { padding: '10px 12px', color: '#32325d', fontSize: '16px', } };
    fpxBank = elements.create('fpxBank', { style: style, accountHolderType: 'individual' });
    e.preventDefault();
    $('#makePaymentModal').attr('payment-mode', "fpx")
    fpxBank.mount('#fpx-bank-element');
    fpxBank.on('change', function (event) {
        $('#loading').hide();
        if (event.error) {
            $('#loading').hide();
            showErrorForPayment(event.error)
        }
        if (event.complete) {
            $('#fpx-button').disabled = false;
            $('#fpx-button').removeClass('disabled');
            $('#fpx-button').addClass('btn-success');
        }
    });
});

$('#tabTwoId').on('hidden.bs.tab', function (e) {
    var fpxBank = elements.getElement('fpxBank');
    fpxBank ? fpxBank.destroy() : '';
});

$(document).on("click", "#payByCardId", function (e) {
    payByCard();
});

function payByFpx(event, fpxBank) {
    var url = null;
    if (window.location.href.indexOf('userBasedBuyerCheckout') != -1) {
        url = getContextPath() + '/buyerSubscription/userBasedBuyerCheckout/' +
            '?promoCodeId='
            + $('#promoCodeUserId').val()
            + '&periodId='
            + $("input[name=periodId]:checked").val()
            + '&basePrice='
            + Number($('input[name=basePrice]').val())
            + '&addtionalUserPrice='
            + Number($('input[name=addtionalUserPrice]').val())
            + '&subscriptionDiscountPrice='
            + Number($('input[name=subscriptionDiscountPrice]').val())
            + '&promoDiscountPrice='
            + Number($('input[name=promoDiscountPrice]').val())
            + '&totalPrice='
            + Number($('input[name=totalPrice]').val())
            + '&country='
            + ($('#idRegCountry').val())
            + '&paymentMode=fpx';
        var form = $('#idUserBasedBuyerCheckOutForm').serializeArray();
        var formData = { buyer: {}, range: {}, plan: {} };
        $.each(form, function (i, v) {
            var obj = v.name.split('.')[0];
            if (obj == 'buyer') {
                formData.buyer[v.name.split('.')[1]] = v.value;
            } else if (obj == 'range') {
                formData.range[v.name.split('.')[1]] = v.value;
            } else if (obj == 'plan') {
                formData.plan[v.name.split('.')[1]] = v.value;
            } else {
                formData[v.name] = v.value;
            }

        });
    } else if (window.location.href.indexOf('eventBasedBuyerCheckout') != -1) {
        url = getContextPath() + '/buyerSubscription/eventBasedBuyerCheckout/' +
            '?promoCodeId='
            + $('#promoCodeEventId').val()
            + '&promoDiscountPrice='
            + Number($('input[name=promoDiscountPrice]').val())
            + '&totalPrice='
            + Number($('input[name=totalPrice]').val())
            + '&country='
            + ($('#idRegCountry').val())
            + '&paymentMode=fpx';
        var form = $('#idEventBasedBuyerCheckOutForm').serializeArray();
        var formData = { buyer: {}, range: {}, plan: {} };
        $.each(form, function (i, v) {
            var obj = v.name.split('.')[0];
            if (obj == 'buyer') {
                formData.buyer[v.name.split('.')[1]] = v.value;
            } else if (obj == 'range') {
                formData.range[v.name.split('.')[1]] = v.value;
            } else if (obj == 'plan') {
                formData.plan[v.name.split('.')[1]] = v.value;
            } else {
                formData[v.name] = v.value;
            }

        });
    }

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json",
        data: JSON.stringify(formData),
        dataType: 'json',
        beforeSend: function (xhr) {
            $('#loading').show();
        },
        success: function (data) {
            console.log(data);
            $('#fpx-button').attr('data-secret', data.clientSecret);
            event.preventDefault();
            var fpxButton = document.getElementById('fpx-button');
            var clientSecret = fpxButton.dataset.secret;
            stripe.confirmFpxPayment(clientSecret, { payment_method: { fpx: fpxBank, }, return_url: window.location.href }).then(function (result) {
                if (result.error) {
                    showErrorForPayment(result.error.message)
                }
            });
        },
        error: function (request) {
            $('#makePaymentModalCloseId').click();
            if (request.getResponseHeader('error')) {
                $('div[id=idGlobalError]').hide();
                $('div[id=idGlobalSuccess]').hide();
                showErrorForPayment(request.getResponseHeader('error').split(",").join("<br/>"))
            }
            $('#loading').hide();
        },
        complete: function () {
            $('#loading').hide();
        }
    });
}

function payByCard() {
    var card = elements.getElement('card');

    var url = null;
    if (window.location.href.indexOf('userBasedBuyerCheckout') != -1) {
        url = getContextPath() + '/buyerSubscription/userBasedBuyerCheckout/' +
            '?promoCodeId='
            + $('#promoCodeUserId').val()
            + '&periodId='
            + $("input[name=periodId]:checked").val()
            + '&basePrice='
            + Number($('input[name=basePrice]').val())
            + '&addtionalUserPrice='
            + Number($('input[name=addtionalUserPrice]').val())
            + '&subscriptionDiscountPrice='
            + Number($('input[name=subscriptionDiscountPrice]').val())
            + '&promoDiscountPrice='
            + Number($('input[name=promoDiscountPrice]').val())
            + '&totalPrice='
            + Number($('input[name=totalPrice]').val())
            + '&country='
            + ($('#idRegCountry').val())
            + '&paymentMode=card';
        var form = $('#idUserBasedBuyerCheckOutForm').serializeArray();
        var formData = { buyer: {}, range: {}, plan: {} };
        $.each(form, function (i, v) {
            var obj = v.name.split('.')[0];
            if (obj == 'buyer') {
                formData.buyer[v.name.split('.')[1]] = v.value;
            } else if (obj == 'range') {
                formData.range[v.name.split('.')[1]] = v.value;
            } else if (obj == 'plan') {
                formData.plan[v.name.split('.')[1]] = v.value;
            } else {
                formData[v.name] = v.value;
            }

        });
    } else if (window.location.href.indexOf('eventBasedBuyerCheckout') != -1) {
        url = getContextPath() + '/buyerSubscription/eventBasedBuyerCheckout/' +
            '?promoCodeId='
            + $('#promoCodeEventId').val()
            + '&promoDiscountPrice='
            + Number($('input[name=promoDiscountPrice]').val())
            + '&totalPrice='
            + Number($('input[name=totalPrice]').val())
            + '&country='
            + ($('#idRegCountry').val())
            + '&paymentMode=card';
        var form = $('#idEventBasedBuyerCheckOutForm').serializeArray();
        var formData = { buyer: {}, range: {}, plan: {} };
        $.each(form, function (i, v) {
            var obj = v.name.split('.')[0];
            if (obj == 'buyer') {
                formData.buyer[v.name.split('.')[1]] = v.value;
            } else if (obj == 'range') {
                formData.range[v.name.split('.')[1]] = v.value;
            } else if (obj == 'plan') {
                formData.plan[v.name.split('.')[1]] = v.value;
            } else {
                formData[v.name] = v.value;
            }
        });
    }

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json",
        data: JSON.stringify(formData),
        dataType: 'json',
        beforeSend: function (xhr) {
            $('#loading').show();
        },
        success: function (data) {
            stripe.confirmCardPayment(data.clientSecret, { payment_method: { card: card } }).then(function (result) {
                if (result.error) {
                    $('div[id=idGlobalError]').hide();
                    $('div[id=idGlobalSuccess]').hide();
                    showErrorForPayment(result.error.message);
                } else {
                    $('#loading').show();
                    var location = window.location.href;
                    location += '?&payment_intent=' + result.paymentIntent.id;
                    window.location.href = location;
                }
            });
        },
        error: function (request) {
            $('#makePaymentModalCloseId').click();
            if (request.getResponseHeader('error')) {
                showErrorForPayment(request.getResponseHeader('error').split(",").join("<br/>"))
            }
            $('#loading').hide();
        },
        complete: function () {
            $('#loading').hide();
        }
    });
}

function showErrorForPayment(errorMsgText) {
    $('#payByCardId').disabled = true;
    $('#payByCardId').addClass('disabled');
    $('#payByCardId').removeClass('btn-success');
    $('#fpx-button').disabled = true;
    $('#fpx-button').addClass('disabled');
    $('#fpx-button').removeClass('btn-success');
    $('#makePaymentModalCloseId').click();
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();
    var fpxBank = elements ? elements.getElement('fpxBank') : '';
    var card = elements ? elements.getElement('card') : '';
    card ? card.clear() : '';
    fpxBank ? fpxBank.clear() : '';
    $('p[id=idGlobalErrorMessage]').html(errorMsgText);
    $('div[id=idGlobalError]').show();
    document.getElementById("idGlobalError").scrollIntoView({ behavior: 'smooth', block: 'center' })

}