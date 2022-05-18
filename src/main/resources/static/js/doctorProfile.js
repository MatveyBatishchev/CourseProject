$(document).ready(function () {

    const alertFirstPart = '<div class="mt-1 alert alert-success alert-dismissible fade show messageAlert" role="alert">';
    const alertSecondPart = '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>'

    $("#editDoctorButton").on('click', function() {
        if ($(this).text() === 'Редактировать') {
            $(".editable").prop('disabled', false);
            $("#getSpecialitiesCol").prop('hidden', true);
            $("#editSpecialitiesCol").prop('hidden', false);
            $("#chooseFileRow").prop('hidden', false);
            $(this).text('Подтвердить');
        }
        else {
            $("#inputGetSpecialities").val($("#inputEditSpecialities option:selected").toArray().map(item => item.text).join(', '));
            let doctor = {};
            doctor.id = doctorId;
            doctor.name = $("#inputName").val();
            doctor.surname = $("#inputSurname").val();
            doctor.patronymic = $("#inputPatronymic").val();
            doctor.email = $("#hiddenInputEmail").val();
            doctor.cabinet = $("#inputCabinet").val();
            doctor.experience = $("#inputExperience").val();
            doctor.specialities = $("#inputEditSpecialities").val();
            doctor.category = $("#inputCategory").val()
            console.log(JSON.stringify(doctor));
            $.ajax({
                type: "PUT",
                url: "/doctors/" + doctorId,
                data: {
                    doctor: JSON.stringify(doctor),
                    doctorImage: $("#inputFile").val()
                },
                success: function (data) {
                    $(".errors").html("");
                    if (data !== "") {
                        let errors = JSON.parse(data);
                        if (errors.hasOwnProperty('name')) {
                            $("#nameErrors").html(alertFirstPart + errors.name + alertSecondPart);
                        }
                        if (errors.hasOwnProperty('surname')) {
                            $("#surnameErrors").html(alertFirstPart + errors.surname + alertSecondPart);
                        }
                        if (errors.hasOwnProperty('patronymic')) {
                            $("#patronymicErrors").html(alertFirstPart + errors.patronymic + alertSecondPart);
                        }
                        if (errors.hasOwnProperty('experience')) {
                            $("#experienceErrors").html(alertFirstPart + errors.experience + alertSecondPart);
                        }
                        if (errors.hasOwnProperty('category')) {
                            $("#categoryErrors").html(alertFirstPart + errors.category + alertSecondPart);
                        }
                    }
                    else {
                        $("#editDoctorForm").submit();
                        $(".editable").prop('disabled', true);
                        $("#getSpecialitiesCol").prop('hidden', false);
                        $("#editSpecialitiesCol").prop('hidden', true);
                        $("#chooseFileRow").prop('hidden', true);
                        $("#editDoctorButton").text('Редактировать');
                    }
                },
                error: function (error) {
                    console.log(error);
                }
            });
        }
    });

});