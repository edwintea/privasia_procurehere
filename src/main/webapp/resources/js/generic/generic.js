/*
This is a genetic js file . This file should be used by all the jsp file
 */


// It's a generic method , so be careful to change anything here , it would affect all the places
// just pass the element id of the form
function removeAllValidationMessage(elementId) {
    var form = document.getElementById(elementId);
    var fields = form.querySelectorAll('.form-control');
    var validationMessages = form.querySelectorAll('.help-block.form-error');

    form.classList.remove('was-validated');

    fields.forEach(function(field) {
        field.classList.remove('is-invalid');
        field.style.borderColor = '';
    });

    validationMessages.forEach(function(message) {
        message.innerHTML = '';
    });

    form.querySelectorAll('.has-error').forEach(function(parentDiv) {
        parentDiv.classList.remove('has-error');
    });
}


// This will remove validation for a single field , just pass the id here
function removeValidationMessageForAFeild(elementId) {
    var quantityField = document.getElementById(elementId);
    var validationMessage = quantityField.nextElementSibling; // Assuming the validation message is the next sibling element
    var parentFormGroup = quantityField.closest('.form-group');
    if (parentFormGroup) {
    	parentFormGroup.classList.remove('has-error');
    }

    quantityField.classList.remove('is-invalid');
    quantityField.classList.remove('was-validated');

    if (validationMessage) {
    	validationMessage.innerHTML = ''; // This will clear the message if found
    }
    quantityField.style.borderColor = '';
}

/*
 This method will validate the input file , just pass the elementId
 Please note that the file size mentioned here , If need to change the size , then include the file size in the method
 paramater and adjust it from everywhere this method is used
 */
function validateFile(elementId) {
    var fileInput = document.getElementById(elementId);
    var file = fileInput.files[0]; // Assuming only one file is selected

    // Check if a file is selected
    if (file) {
        // Check file extension
        var allowedExtensions = fileInput.getAttribute('data-validation-allowing').split(',').map(function(extension) {
            return extension.trim();
        });

        var fileName = file.name;
        var fileExtension = fileName.split('.').pop().toLowerCase();
        if (!allowedExtensions.includes(fileExtension)) {
            // alert('Invalid file extension. Allowed extensions are: ' + allowedExtensions.join(', '));
            return false;
        }

        // Check file size
        var maxSizeMB = parseInt(fileInput.getAttribute('data-validation-max-size'));
        var maxSizeBytes = maxSizeMB * 1024 * 1024;
        if (file.size > maxSizeBytes) {
            // alert('File size exceeds the limit of ' + maxSizeMB + 'MB');
            return false;
        }

        // File is valid
        return true;
    } else {
        // No file selected
        // alert('Please select a file.');
        return false;
    }
}