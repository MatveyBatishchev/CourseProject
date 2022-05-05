$(document).ready(function () {

    $('#checkPasswordModal').modal({
        backdrop: 'static',
    });
    $('#changePasswordModal').modal({
        backdrop: 'static',
    });

    $('#checkPasswordModal, #changePasswordModal, #warningDeleteModal, #confirmDeleteModal').on('hidden.bs.modal', function () {
        $("#checkActualPassword").val("");
        $("#inputPassword").val("");
        $("#inputConfirmPassword").val("");
        if ($(".togglePasswordClass")[0].classList.contains("bi-eye") === true)
            $(".togglePasswordClass")[0].click();
        $(".squareInput").each(function() {
            $(this).val('');
        });
        $(".alert").each(function() {
            $(this).alert('close');
        });
    })

    // проверка совпадения паролей
    $("#submitCheckPassword").on("click", function () {
        $.ajax({
            type: "GET",
            url: "/patients/compare-passwords",
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
            url: "/patients/save-newPassword",
            data: {
                providedPassword: $("#inputPassword").val(),
                patientId: $("#inputId").val()
            },
            success: function (data) {
                var message = JSON.parse(data);
                if (typeof message === "object") {
                    var errors = '';
                    message.forEach( err => {
                        errors += '<li class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" ' +
                            'role="alert"><div class="me-3">' + err + '</div><button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></li>';
                    });
                    $("#errorsPassword").html(errors);
                }
                else {
                    switch (message) {
                        case "same":
                            $("#errorsPassword").html('<li class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" ' +
                                'role="alert">Новый пароль не должен совпадать с предыдущим<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></li>');
                            break;
                        case "success":
                            $("#changePasswordModalBody").html('<p class="buttonFont text-center" style="margin-top: 4rem; font-size: 19px;" >Новый пароль был успешно установлен!</p>');
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

    // // сброс форм и видимости при закрытии окна
    // $(".buttonCloseModal").on('click', function() {
    //
    // });

});