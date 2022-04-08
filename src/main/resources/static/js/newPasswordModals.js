$(document).ready(function () {

    $('#checkPasswordModal').modal({
        backdrop: 'static',
        keyboard: true
    });
    $('#changePasswordModal').modal({
        backdrop: 'static',
        keyboard: true
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
            type: "POST",
            url: "/patients/newPassword",
            data: {
                providedPassword: $("#inputPassword").val(),
                patientId: $("#inputId").val()
            },
            success: function (data) {
                var message = JSON.parse(data);
                console.log(JSON.parse(data));
                console.log(typeof JSON.parse(data));
                if (typeof message === "object") {
                    var errors = "<ul>";
                    message.forEach( err => {
                        errors += '<li>' + err + '</li>';
                    });
                    errors += "<ul>";
                    $("#errorsPassword").html('<div class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" role="alert">'+ errors +
                        '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
                }
                else {
                    switch (message) {
                        case "same":
                            $("#errorsPassword").html('<div class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" role="alert">Новый пароль совпадает с предыдущим!' +
                                '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
                            break;
                        case "success":
                            $("#changePasswordModalBody").html('<p>Новый пароль был успешно установлен!</p>');
                            break;
                        default:
                            $("#changePasswordModalBody").html('<p>' + data + '</p>');
                            break;
                    }
                }
            }
        });
    });

    // disable кнопки при пустом поле
    let checkPassword = $('#checkActualPassword');
    checkPassword.on('keyup', function () {
        let submitCheckPassword = $('#submitCheckPassword');
        if (checkPassword.val() === "") submitCheckPassword.prop("disabled", true);
        else submitCheckPassword.prop("disabled", false);
    });

    // сброс паролей и видимости при закрытии окна
    $(".btn-close").on('click', function() {
        $("#checkActualPassword").val("");
        $("#inputPassword").val("");
        $("#inputConfirmPassword").val("");
        console.log($(".togglePasswordClass")[0].classList);
        console.log($(".togglePasswordClass")[0].classList.contains("bi-eye"));
        if ($(".togglePasswordClass")[0].classList.contains("bi-eye") === true)
            $(".togglePasswordClass")[0].click();
    });
});