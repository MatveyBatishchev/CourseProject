$(document).ready(function () {

    $('#checkPasswordModal').modal({
        backdrop: 'static',
        keyboard: true
    });
    $('#changePasswordModal').modal({
        backdrop: 'static',
        keyboard: true
    });

    // disable кнопки при пустом поле
    let checkPassword = $('#checkActualPassword');
    checkPassword.on('keyup', function () {
        let submitCheckPassword = $('#submitCheckPassword');
        if (checkPassword.val() === "") submitCheckPassword.prop("disabled", true);
        else submitCheckPassword.prop("disabled", false);
    });

    // проверка совпадения паролей
    $("#submitCheckPassword").on("click", function () {
        $.ajax({
            type: "GET",
            url: "/patients/comparePasswords",
            data: {
                providedPassword: $("#checkActualPassword").val(),
                patientId: $("#inputId").val()
            },
            success: function (data) {
                if (data !== "true") {
                    $("#incorrectPassword").html('<div class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" ' +
                        'role="alert">Неверный пароль!<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
                } else {
                    $('#checkPasswordModal').modal("hide");
                    $("#changePasswordModal").modal("toggle");
                }
            }
        });
    });

    // создание нового пароля
    $("#submitChangePassword").on("click", function () {
        $.ajax({
            type: "GET",
            url: "/patients/comparePasswords",
            data: {
                providedPassword: $("#inputPassword").val(),
                patientId: $("#inputId").val()
            },
            success: function (data) {
                if (data === "true") {
                    $("#samePassword").html('<div class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" role="alert">Новый пароль совпадает с предыдущим!' +
                        '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
                } else {
                    $("#hiddenPassword").val(data);
                    $("#changePasswordModalBody").html('<p>Новый пароль будет успешно установлен при подтверждении редактирования</p>');
                }
            }
        });
    });

});