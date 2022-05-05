$(document).ready(function () {
    $("#submitResetPassword").on('click', function () {
        $("#submitResetPassword").prop("disabled", true);
        let resetEmail = $("#resetPasswordEmail").val();
        if (validateEmail(resetEmail)) {
            checkEmailExist(resetEmail);
        } else {
            $("#errorPlaceholder").html('<div class="mt-2 alert alert-danger alert-dismissible fade show" role="alert">Недопустимое значение email-a!<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
            $("#submitResetPassword").prop("disabled", false);
        }
    });
});

function checkEmailExist(resetEmail) {
    $.ajax({
        type: "GET",
        url: "/patients/email-exists",
        data: {
            resetEmail: resetEmail
        },
        success: function (data) {
            sendResetPassword(data, resetEmail)
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function sendResetPassword(data, resetEmail) {
    if (JSON.parse(data)) {
        $.ajax({
            type: "GET",
            url: "/patients/email-reset",
            data: {
                resetEmail: resetEmail
            },
            success: function (data) {
                $("#resetPasswordModalBody").html('<p>' + data + '</p>');
                $("#resetPasswordModalFooter").html('');
                setTimeout(function () {
                    $('#resetPasswordModal').modal("hide");
                }, 10000);
            },
            error: function (error) {
                console.log(error);
            }
        });
    } else {
        $("#errorPlaceholder").html('<div class="mt-2 alert alert-danger alert-dismissible fade show" role="alert">Пользователь с данные email-ом не был найден!<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
        $("#submitResetPassword").prop("disabled", false);
    }
}

function validateEmail(email) {
    return email.match(/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
}