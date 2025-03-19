 // Reset modal form and validation states when modal is hidden
    $('.modal').on('hidden.bs.modal', function () {
        const form = $(this).find('form');
        // Only proceed if forms exist
        if (form.length > 0) {
            form[0].reset(); // Reset the form fields
            form.validate().resetForm(); // Reset the validation states
            form.find('.error').remove(); // Remove any existing error messages
            form.find('.has-error').removeClass('has-error'); // Remove error class
        }
    });
