$(document).ready(function() {

    const inputPassword = $('#inputPassword');
    const inputConfirmPassword = $('#inputConfirmPassword');
    const passwordMessage = $('#passwordMessage');
    const submitPassword = $('#regSubmit');

    setDatepicker($("#dateInput"), $("#regSubmit"));

    $("#agreementCheckbox").on("change", function() {
        if ($("#agreementCheckbox").is(":checked")) {
            if ((inputPassword.val() === inputConfirmPassword.val()) && (inputPassword.val() !== ""))
            $("#regSubmit").prop("disabled", false)
        }
        else {
            $("#regSubmit").prop("disabled", true)
        }
    });

    $("#inputPassword, #inputConfirmPassword").keyup( function() {
        if ((inputPassword.val() !== "") || (inputConfirmPassword.val() !== "")) {
            passwordMessage.prop("hidden", false);
            if ((inputPassword.val() === inputConfirmPassword.val()) && (inputPassword.val() !== "")) {
                passwordMessage.prop("class", "text-success");
                passwordMessage.html('matching');
                if ($("#agreementCheckbox").is(":checked"))
                submitPassword.prop("disabled", false);
            } else {
                passwordMessage.prop("class", "text-danger");
                passwordMessage.html('not matching');
                submitPassword.prop("disabled", true);
            }
        }
        else {
            passwordMessage.prop("hidden", true);
        }
    })
});