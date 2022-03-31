$(document).ready(function () {

    $('#checkPasswordModal').modal({
        backdrop: 'static',
        keyboard: true
    });
    $('#changePasswordModal').modal({
        backdrop: 'static',
        keyboard: true
    });

    // показ и скрытие пороля
    const togglePassword = $(".togglePasswordClass").on("click", function () {
        $(".inputPasswordClass").each(function () {
            const type = $(this).prop("type") === "password" ? "text" : "password";
            $(this).prop("type", type);
            togglePassword.toggleClass("bi-eye");
        })
    });

    const inputPassword = $('#inputPassword');
    const inputConfirmPassword = $('#inputConfirmPassword');
    const passwordMessage = $('#passwordMessage');
    const submitChangePassword = $('#submitChangePassword');

    // вывод сообщения совпадения паролей
    inputConfirmPassword.on('keyup', function () {
        if ((inputPassword.val() === inputConfirmPassword.val()) && (inputPassword.val() !== "")) {
            passwordMessage.prop("class", "text-success");
            passwordMessage.html('matching');
            submitChangePassword.prop("disabled", false);
        } else {
            passwordMessage.prop("class", "text-danger");
            passwordMessage.html('not matching');
            submitChangePassword.prop("disabled", true);
        }
    });

    // disable кнопки при пустом поле
    let checkPassword = $('#checkActualPassword');
    checkPassword.on('keyup', function () {
        let submitCheckPassword = $('#submitCheckPassword');
        if (checkPassword.val() === "") submitCheckPassword.prop("disabled", true);
        else submitCheckPassword.prop("disabled", false);
    });

    // проверка совпадения паролей
    $("#submitCheckPassword").on("click", function() {
        if ($("#checkActualPassword").val() !== $("#hiddenPassword").val())
            $("#incorrectPassword").html('<div class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" role="alert">Неверный пароль!<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
        else {
            $('#checkPasswordModal').modal("hide");
            $("#changePasswordModal").modal("toggle");
        }
    });

    // создание нового пароля
    $("#submitChangePassword").on("click", function() {
        $("#hiddenPassword").val($("#inputPassword").val());
        console.log($("#hiddenPassword").val());
        $("#changePasswordModalBody").html('<p>Новый пароль будет успешно установлен при подтверждении редактирования</p>')
    });

});