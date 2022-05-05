$(document).ready(function () {

    let submitButton = $("#submitButton");
    let doctorsRow = $("#doctorsRow");
    let schedulesRow = $("#schedulesRow");
    let timetablesRow = $("#timetablesRow");
    let previewRow = $("#previewRow");
    let callbackRow = $("#callbackRow");

    let inputSpeciality = $("#inputSpeciality");
    let inputDoctor = $("#inputDoctor");
    let inputSchedule = $("#inputSchedule");
    let inputTimetable = $("#inputTimetable");

    $.datepicker.setDefaults( $.datepicker.regional[ "ru" ] );

    inputSpeciality.on("change", function () {
        $.ajax({
            type: "GET",
            url: "/doctors/by-speciality",
            data: {
                    speciality: $(this).val()
                },
            success: function (data) {
                let doctors = JSON.parse(data);
                if (doctors.length !== 0) {
                    let s = '<option value="" disabled selected>Выберите специалиста</option>';
                    doctors.forEach(doctor => {
                        s += '<option value="' + doctor.id + '">' + doctor.surname + ' ' + doctor.name + ' ' + doctor.patronymic + '</option>';
                    })
                    $("#noFoundDoctorsMessage").prop('hidden', true);
                    inputDoctor.html(s);
                    $("#selectDoctorDiv").prop('hidden', false);
                }
                else {
                    $("#noFoundDoctorsMessage").prop('hidden', false);
                    $("#selectDoctorDiv").prop('hidden', true);
                }
                doctorsRow.prop("hidden", false);
                schedulesRow.prop("hidden", true);
                timetablesRow.prop("hidden", true);
                previewRow.prop("hidden", true);
                callbackRow.prop("hidden", true);
                submitButton.prop("disabled", true);
                reverseAnimation();
            },
            error: function (error) {
                console.log("ERROR : ", error);
            }
        });
    });

    let enableDays = [];

    inputDoctor.on("change", function () {
        $.ajax({
            type: "GET",
            url: "/schedules/doctor/" + $(this).val(),
            success: function (data) {
                let schedules = JSON.parse(data);
                enableDays = [];
                schedules.forEach(schedule => {
                    enableDays.push(schedule.date.toString());
                });
                inputSchedule.datepicker("destroy");
                inputSchedule.datepicker({
                    dateFormat: 'dd-mm-yy',
                    minDate: 0,
                    numberOfMonths: 2,
                    beforeShowDay: enableAllTheseDays,
                    onSelect: function () {
                        let found = schedules.find(function(schedule, index) {
                            if(schedule.date === inputSchedule.datepicker({dateFormat: 'dd-mm-yy'}).val())
                                return true;
                        });
                        $.ajax({
                            type: "GET",
                            url: "/schedules/" + found.id + "/timetables",
                            success: function (data) {
                                let timetables = JSON.parse(data);
                                let s = '<div class="row justify-content-center buttonFont mt-2 mb-2">';
                                for (let i = 0; i < timetables.length; i++) {
                                    if (i % 4 === 0 && i !== 0)
                                        s += '</div><div class="row justify-content-center buttonFont mb-2">';
                                    let sec = timetables[i].startTime[1];
                                    if (sec <= 9)
                                        sec = '0' + sec;
                                    s += '<button type="button" class="timeBlocks">' + timetables[i].startTime[0] + ':' + sec + '</button>';
                                }
                                s += '</div>';
                                inputTimetable.html(s);
                                timetablesRow.prop("hidden", false);
                                previewRow.prop("hidden", true);
                                callbackRow.prop("hidden", true);
                                submitButton.prop("disabled", true);
                                reverseAnimation();
                            },
                            error: function (error) {
                                console.log("ERROR : ", error);
                            }
                        });
                    } // появление кнопок времени
                });
                schedulesRow.prop("hidden", false);
                timetablesRow.prop("hidden", true);
                previewRow.prop("hidden", true);
                callbackRow.prop("hidden", true);
                submitButton.prop("disabled", true);
                reverseAnimation();
            },
            error: function (error) {
                console.log("ERROR : ", error);
            }
        });
    });

    let clickedButtonVal;
    let clickedButtonText;
    let previewDoctorName = $("#previewDoctorName")[0];
    let previewDoctorPhoto = $("#previewDoctorPhoto");
    let previewDoctorSpeciality = $("#previewDoctorSpeciality")[0];
    let previewDoctorExperience = $("#previewDoctorExperience")[0];
    let previewDoctorCategory = $("#previewDoctorCategory")[0];
    let previewAppointmentDate = $("#previewAppointmentDate")[0];
    let previewDoctorCabinet = $("#previewDoctorCabinet")[0];

    inputTimetable.on("click", "button.timeBlocks", function () {
        if (typeof clickedButtonVal !== "undefined")
            clickedButtonVal.removeClass("activeButton");
        clickedButtonVal = $(this);
        clickedButtonText = $(this).html();
        $(this).addClass("activeButton");

        $.ajax({
            type: "GET",
            url: "/doctors/" + inputDoctor.val() + "/json",
            success: function (data) {
                let doctor = JSON.parse(data).doctor;
                previewDoctorName.innerHTML = 'Специалист: ' + doctor.surname + ' ' + doctor.name + ' ' + doctor.patronymic;
                previewDoctorPhoto.prop("src", "/applicationFiles/doctors/" + doctor.id + "/" + doctor.image);
                previewDoctorSpeciality.innerHTML = 'Специальность: ' + $("#inputSpeciality option:selected").text();
                previewDoctorExperience.innerHTML = 'Стаж: ' + doctor.experience + ' ' + plural(doctor.experience);
                previewDoctorCategory.innerHTML = "Категория: " + doctor.category;
                previewAppointmentDate.innerHTML = 'Время приёма: ' + $("#inputSchedule").datepicker({dateFormat: 'dd-mm-yy'}).val() + ' в ' + clickedButtonText;
                previewDoctorCabinet.innerHTML = 'Кабинет: ' + doctor.cabinet;
                previewRow.prop("hidden", false);
                callbackRow.prop("hidden", doctor.reg);
                if (doctor.reg) submitButton.prop("disabled", false);
                directAnimation();
            },
            error: function (error) {
                console.log("ERROR : ", error);
            }
        });
    });

    let inputName = $("#inputName");
    let inputSurname = $("#inputSurname");
    let inputTelephone = $("#inputTelephone");

    $(".callbackInput").on('change', function() {
        if ($(this).prop('hidden') !== true && inputName.val() !== "" && inputSurname.val() !== "" && inputTelephone.val().length >= 17) {
            submitButton.prop('disabled', false);
        }
        else submitButton.prop('disabled', true);
    })

    submitButton.on("click", function () {
        $.ajax({
            type: "POST",
            url: "/appointments/new",
            data: {
                doctorId: inputDoctor.val(),
                date: inputSchedule.datepicker({dateFormat: 'dd-mm-yy'}).val(),
                time: clickedButtonText,
                callbackInfo: inputSurname.val() + " " + inputName.val() + " " + inputTelephone.val()
            },
            success: function(data) {
                window.location.href = "https://localhost:443" + data;
            }
        })
    });

    function enableAllTheseDays(date) {
        let sdate = $.datepicker.formatDate('dd-mm-yy', date)
        if ($.inArray(sdate, enableDays) !== -1) {
            return [true];
        }
        return [false];
    }

    let firstBlock = $('.firstBlock');
    let secondBlock = $('.secondBlock');
    let thirdBlock = $('.thirdBlock');
    let attrTransition = $('.attrTransition');
    let wordsTransition = $('.wordsTransition');
    let greenTriangles = $(".greenTriangles");

    function directAnimation() {
        firstBlock.addClass('firstBlockAfter');
        secondBlock.addClass('secondBlockAfter');
        thirdBlock.addClass('thirdBlockAfter');
        attrTransition.addClass('attrTransitionAfter');
        wordsTransition.addClass('wordsTransitionAfter');
        greenTriangles.addClass('greenTrianglesAfter');
    }

    function reverseAnimation() {
        firstBlock.removeClass('firstBlockAfter');
        secondBlock.removeClass('secondBlockAfter');
        thirdBlock.removeClass('thirdBlockAfter');
        attrTransition.removeClass('attrTransitionAfter');
        wordsTransition.removeClass('wordsTransitionAfter');
        greenTriangles.removeClass('greenTrianglesAfter');
    }

});


