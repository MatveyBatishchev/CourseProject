$(document).ready(function () {

    let clickedButtonVal;
    let clickedButtonText;
    let enableDays = [];

    $("#inputSpeciality").on("change", function () {
        $.ajax({
            type: "GET",
            url: "/doctors/by_speciality",
            data: {
                    speciality: $("#inputSpeciality").val()
                },
            success: function (data) {
                let doctors = JSON.parse(data);
                console.log(doctors.length);
                if (doctors.length != 0) {
                    let s = '<option value="" disabled selected>Выберите специалиста</option>';
                    doctors.forEach(doctor => {
                        s += '<option value="' + doctor.id + '">' + doctor.surname + ' ' + doctor.name + ' ' + doctor.patronymic + '</option>';
                    })
                    $("#inputDoctor").html(s);
                }
                else {
                    $("#selectDoctorCol").html('<div class="card align-items-center justify-content-center mt-3">\n' +
                        '                                <div class="m-3">\n' +
                        '                                    <h4 class="themes">К сожалению на данным момент нет свободных специалистов. Мы постараемся как можно скорее исправить ситуацию</h4>\n' +
                        '                                    <i class="las la-sad-cry la-3x"></i>\n' +
                        '                                </div>\n' +
                        '                            </div>');
                }
                $("#doctorsRow").prop("hidden", false);
                $("#schedulesRow").prop("hidden", true);
                $("#timetablesRow").prop("hidden", true);
                $("#previewRow").prop("hidden", true);
                $("#callbackRow").prop("hidden", true);
                $("#submitButton").prop("disabled", true);
            },
            error: function (error) {
                console.log("ERROR : ", error);
            }
        });
    }); // появление выбора докторов

    $("#inputDoctor").on("change", function () {
        $.ajax({
            type: "GET",
            url: "/schedules/getDoctorSchedules",
            data: {
                doctorId: $("#inputDoctor").val()
            },
            success: function (data) {
                let schedules = JSON.parse(data);
                enableDays = [];
                schedules.forEach(schedule => {
                    enableDays.push(schedule.date.toString());
                });
                $("#inputSchedule").datepicker("destroy");
                $.datepicker.setDefaults( $.datepicker.regional[ "ru" ] );
                $('#inputSchedule').datepicker({
                    dateFormat: 'dd-mm-yy',
                    minDate: 0,
                    numberOfMonths: 2,
                    beforeShowDay: enableAllTheseDays,
                    onSelect: function () {
                        $.ajax({
                            type: "GET",
                            url: "/schedules/getTimeTables",
                            data: {
                                scheduleDate: $("#inputSchedule").datepicker({dateFormat: 'dd-mm-yy'}).val(),
                                doctorId: $("#inputDoctor").val()
                            },
                            success: function (data) {
                                let timetables = JSON.parse(data);
                                let s = '<div class="row-cols-4 mt-2 mb-3">';
                                for (let i = 0; i < timetables.length; i++) {
                                    if (i % 3 === 0)
                                        s += '</div><div class="row-cols-4 mt-2 mb-3 ml-2">';
                                    let sec = timetables[i].startTime[1];
                                    if (sec <= 9)
                                        sec = '0' + sec;
                                    s += '<a class="btn btn-secondary offset-1 col-md-2">' + timetables[i].startTime[0] + ':' + sec + '</a>';
                                }
                                s += '</div>';
                                $("#inputTimetable").html(s);
                                $("#timetablesRow").prop("hidden", false);
                                $("#previewRow").prop("hidden", true);
                                $("#callbackRow").prop("hidden", true);
                                $("#submitButton").prop("disabled", true);
                            },
                            error: function (error) {
                                console.log("ERROR : ", error);
                            }
                        });
                    } // появление кнопок времени
                });
                $("#schedulesRow").prop("hidden", false);
                $("#timetablesRow").prop("hidden", true);
                $("#previewRow").prop("hidden", true);
                $("#callbackRow").prop("hidden", true);
                $("#submitButton").prop("disabled", true);
            },
            error: function (error) {
                console.log("ERROR : ", error);
            }
        });
    }); // появление календаря дат

    $("#inputSchedule").on("change", function () {
    }); // появление кнопок времени

    $("#inputTimetable").on("click", "a.btn", function () {
        if (typeof clickedButtonVal !== "undefined")
            clickedButtonVal.prop("class", "btn btn-secondary offset-1 col-md-2") // возвращаем стиль кнопки при нажатии другой
        clickedButtonVal = $(this);
        clickedButtonText = $(this).html();
        $(this).prop("class", "btn btn-primary offset-1 col-md-2")

        $.ajax({
            type: "GET",
            url: "/doctors/getById",
            data: {
                doctorId: $("#inputDoctor").val(),
            },
            success: function (data) {
                console.log(data);
                let doctor = JSON.parse(data).doctor;
                $("#previewAppointment").html(data);
                $("#previewDoctorName")[0].innerHTML = 'Специалист: ' + doctor.surname + ' ' + doctor.name + ' ' + doctor.patronymic;
                $("#previewDoctorPhoto").prop("src", "/applicationFiles/doctors/" + doctor.id + "/" + doctor.image);
                $("#previewDoctorSpeciality")[0].innerHTML = 'Специальность: ' + $("#inputSpeciality option:selected").text();
                $("#previewExperience")[0].innerHTML = 'Стаж: ' + doctor.experience + ' ' + plural(doctor.experience);
                $("#previewCategory")[0].innerHTML = "Категория: " + doctor.category;
                $("#previewDate")[0].innerHTML = 'Время приёма: ' + $("#inputSchedule").datepicker({dateFormat: 'dd-mm-yy'}).val() + ' в ' + clickedButtonText;
                $("#previewCabinet")[0].innerHTML = 'Кабинет: ' + doctor.cabinet;
                $("#previewRow").prop("hidden", false);
                $("#callbackRow").prop("hidden", doctor.reg);
            },
            error: function (error) {
                console.log("ERROR : ", error);
            }
        }) // берём данные доктора для превью

        $("#submitButton").prop("disabled", false);
    });

    $("#submitButton").on("click", function () {
        $.ajax({
            type: "POST",
            url: "/appointments/new",
            data: {
                doctorId: $("#inputDoctor").val(),
                date: $("#inputSchedule").datepicker({dateFormat: 'dd-mm-yy'}).val(),
                time: clickedButtonText,
                callbackInfo: $("#inputSurname").val() + " " + $("#inputName").val() + " " + $("#inputTelephone").val()
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

});


