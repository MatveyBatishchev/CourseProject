$(document).ready(function() {
    var confirmationCode = "";

    $('#warningDeleteModal').modal({
        backdrop: 'static',
        keyboard: true
    });

    $('#confirmDeleteModal').modal({
        backdrop: 'static',
        keyboard: true
    });

    const inputElements = [...document.querySelectorAll('input.code-input')]

    inputElements.forEach((ele, index) => {
        ele.addEventListener('keydown', (e) => {
            if (e.keyCode === 8 && e.target.value === '') inputElements[Math.max(0, index - 1)].focus()
        })
        ele.addEventListener('input', (e) => {
            const [first, ...rest] = e.target.value
            e.target.value = first ?? ''
            const lastInputBox = index === inputElements.length - 1
            const insertedContent = first !== undefined
            if (insertedContent && !lastInputBox) {
                inputElements[index + 1].focus()
                inputElements[index + 1].value = rest.join('')
                inputElements[index + 1].dispatchEvent(new Event('input'))
            }
        })
    })

    $("#warningDeleteButton").on("click", function() {
        confirmationCode = generateCode();
        $.ajax({
            type: "POST",
            url: "/patients/send-deleteCode",
            data: {
                patientEmail: $("#inputEmail").val(),
                confirmationCode: confirmationCode
            },
            success: function (data) {
                console.log("SUCCESS : ", data);
            },
            error: function (error) {
                console.log("ERROR : ", error);
            }
        });
    });

    $("#confirmDeleteButton").on("click", function() {
        var enteredCode = "";
        $('#confirmDeleteForm :input').each(function() {
            enteredCode += $(this).val();
        });
        if (enteredCode == confirmationCode) {
            let patientId = $("#inputId").val();
            $.ajax({
                type: "DELETE",
                url: '/patients/' + patientId,
                success: function (data) {
                    console.log("SUCCESS : ", data);
                },
                error: function (error) {
                    console.log("ERROR : ", error);
                }
            });
            $("#confirmDeleteModalBody").html('<p class="buttonFont text-center" style="margin-top: 2.5rem; font-size: 19px;" >Ваш аккаунт успешно удалён. Возвращайтесь к нам как можно скорее!</p>');
            $("#confirmDeleteModalFooter").html('');
            $("#closeConfirmDeleteModal").on("click", function() {
                window.location = "http://localhost:443/logout";
            })
        }
        else $("#incorrectCode").html('<div class="mt-2 alert alert-danger alert-dismissible fade show messageAlert" role="alert">Неверный код подтверждения!<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
    });

    function generateCode() {
        let code = "";
        for (let i=0; i<6; i++) {
            code += Math.floor(Math.random() * 10);
        }
        return code;
    }

});