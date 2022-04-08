var check = function (submitPassword) {
    $(document).ready(function () {
        const inputPassword = $('#inputPassword');
        const inputConfirmPassword = $('#inputConfirmPassword');
        const passwordMessage = $('#passwordMessage');

        if ((inputPassword.val() !== "") || (inputConfirmPassword.val() !== "")) {
            if ((inputPassword.val() === inputConfirmPassword.val()) && (inputPassword.val() !== "")) {
                passwordMessage.prop("hidden", false);
                passwordMessage.prop("class", "text-success");
                passwordMessage.html('matching');
                submitPassword.prop("disabled", false);
            } else {
                passwordMessage.prop("hidden", false);
                passwordMessage.prop("class", "text-danger");
                passwordMessage.html('not matching');
                submitPassword.prop("disabled", true);
            }
        }
        else {
            passwordMessage.prop("hidden", true);
        }
    });
}







