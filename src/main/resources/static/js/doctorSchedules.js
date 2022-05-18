$(document).ready(function() {

    // fullCalendar creation
    var scrollTime = new Date().getUTCHours() + ":00:00";
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        themeSystem: 'bootstrap5',
        selectable: true,
        navLinks: true,
        selectHelper: true,
        slotDuration: "00:10:00",
        scrollTime: scrollTime,
        nowIndicator: true,
        allDaySlot: false,
        slotMinTime: '08:00',
        slotMaxTime: '20:00',
        locale: 'ru',
        height: 650,
        editable: false,
        headerToolbar: {
            start: 'prev,next today',
            center: 'title',
            end: 'timeGridWeek,timeGridDay'
        },
        dayHeaderFormat: {
            weekday: 'long'
        },
        slotLabelFormat: {
            hour: '2-digit',
            minute: '2-digit',
        },
        events: JSON.parse(timetableEvents)
    });
    calendar.render();

    console.log(timetableEvents);
    console.log(schedulesJson);


    // disabledDays
    let disabledDays = [];
    let schedules = JSON.parse(schedulesJson);
    if (schedules != null && schedules.length !== 0) {
        schedules.forEach(schedule => {
            disabledDays.push(schedule.date);
        });
    }
    let initialDays = disabledDays;

    let datepicker = $("#datepicker");
    let datepickerCard = $("#datepickerCard");
    let actionMarker = false;

    $.datepicker.setDefaults($.datepicker.regional["ru"]);

    let markDaysFunc = function markAllTheseDays(date) {
        let sdate = $.datepicker.formatDate('dd-mm-yy', date)
        if ($.inArray(sdate, disabledDays) !== -1) {
            return [true, "highlight-gray"];
        }
        return [true, "highlight-green"];
    }

    let ableDaysFunc = function ableAllTheseDays(date) {
        let sdate = $.datepicker.formatDate('dd-mm-yy', date)
        if ($.inArray(sdate, disabledDays) !== -1) {
            return [true];
        }
        return [false];
    }

    let disableDaysFunc = function disableAllTheseDays(date) {
        let sdate = $.datepicker.formatDate('dd-mm-yy', date)
        if ($.inArray(sdate, disabledDays) !== -1) {
            return [false];
        }
        return [true];
    }

    defaultDatepicker(markDaysFunc, false);

    // edit schedules
    let timetables;
    let timetablesCard = $("#timetablesCard");
    let timetablesRow = $("#timetablesRow");
    let editSchedulesButtons = $("#editScheduleButtons");
    let submitEditSchedule = $("#submitEditSchedule");
    let cancelEditSchedule = $("#cancelEditSchedule");

    $("#editSchedule").on('click', function() {
        cancel();
        defaultDatepicker(ableDaysFunc, true)
        datepickerCardMessage('Выберете дату, расписание которой хотите отредактировать', 'las la-calendar');
        toggleDatepickerHoverStyle(false);
    });

    $("#moveSchedule").on('click', function() {
        cancel();
        refreshDatepicker(ableDaysFunc, true);
        datepickerCardMessage('Выберете дату, расписание которой хотите переместить', 'las la-calendar');
        toggleDatepickerHoverStyle(false);
    });

    $("#copySchedule").on('click', function() {
        cancel();
        refreshDatepicker(ableDaysFunc, false);
        datepickerCardMessage('Выберете дату, расписание которой хотите скопировать', 'las la-calendar');
        toggleDatepickerHoverStyle(false);
    });

    let firstDate;
    let firstDateScheduleId;

    function refreshDatepicker(func, isMove) {
        datepicker.datepicker('destroy');
        datepicker.datepicker({
            dateFormat: 'dd-mm-yy',
            minDate: 0,
            numberOfMonths: 2,
            beforeShowDay: func,
            onSelect: function() {
                if (actionMarker) {
                    let message = "В выбранной дате уже есть существующее расписание, хотите перезаписать?";
                    let scheduleExists = schedules.some(e => e.date === datepicker.datepicker({dateFormat: 'dd-mm-yy'}).val());
                    if (isMove) {
                        if (!scheduleExists) {
                            message = 'Вы уверены, что хотите перенести расписание с ' + firstDate +
                                ' на ' + datepicker.datepicker({dateFormat: 'dd-mm-yy'}).val();
                        }
                        datepickerCardMessageWithButtons(message, 'las la-clone', 'submitMove');
                    }
                    else {
                        if (!scheduleExists) {
                            message = 'Вы уверены, что хотите скопировать расписание с ' + firstDate +
                                ' на ' + datepicker.datepicker({dateFormat: 'dd-mm-yy'}).val()
                        }
                        datepickerCardMessageWithButtons(message, 'las la-clone', 'submitCopy');
                    }

                    $("#submitMove").on('click', function () {
                        $.ajax({
                            type: "POST",
                            url: '/schedules/' + firstDateScheduleId + '/move',
                            data: {
                                moveDate: datepicker.datepicker({dateFormat: 'dd-mm-yy'}).val()
                            },
                            success: function (data) {
                                datepickerCardMessage('Расписание было успешно перенесено!', 'las la-check-circle');
                                setTimeout(function() {
                                    cancel();
                                }, 4000);
                            },
                            error: function (error) {
                                console.log('ERROR : ' + error);
                            }
                        });
                    });

                    $("#submitCopy").on('click', function () {
                        $.ajax({
                            type: "POST",
                            url: '/schedules/' + firstDateScheduleId + '/copy',
                            data: {
                                copyDate: datepicker.datepicker({dateFormat: 'dd-mm-yy'}).val()
                            },
                            success: function (data) {
                                datepickerCardMessage('Расписание было успешно скопировано!', 'las la-check-circle');
                                setTimeout(function() {
                                    cancel();
                                }, 4000);
                            },
                            error: function (error) {
                                console.log('ERROR : ' + error);
                            }
                        });
                    });
                }
                else {
                    firstDate = datepicker.datepicker({dateFormat: 'dd-mm-yy'}).val();
                    firstDateScheduleId = schedules.find(el => {
                        return el.date === firstDate
                    }).id;
                    disabledDays = [firstDate];
                    actionMarker = true;
                    refreshDatepicker(disableDaysFunc, isMove)
                }

                $(".cancel").on('click', function() {
                    cancel();
                })
            }
        });
    }

    function cancel() {
        actionMarker = false;
        datepickerCardMessage('Вы можете редактировать каждое расписание отдельно, переносить его или ' +
            'скопировать в другой день. (Длительность приёма останется такой-же)', 'las la-pencil-alt');
        toggleDatepickerHoverStyle(true);
        timetablesRow.html('');
        timetablesCard.prop('hidden', true);
        editSchedulesButtons.prop('hidden', true);
        disabledDays = initialDays;
        defaultDatepicker(markDaysFunc, false);
    }

    let schedule;
    function defaultDatepicker(func, isEdit) {
        datepicker.datepicker('destroy');
        datepicker.datepicker({
            dateFormat: 'dd-mm-yy',
            minDate: 0,
            numberOfMonths: 2,
            beforeShowDay: func,
            onSelect: function() {
                if (isEdit) {
                    datepickerCardMessage('Ниже представлены все графики выбранного расписания (серым отмечены занятые приёмом)! Нажмите на тот, который хотите изменить', 'las la-hand-pointer');
                    schedule = schedules.find(el => {
                        return el.date === datepicker.datepicker({dateFormat: 'dd-mm-yy'}).val()
                    });
                    $.ajax({
                        type: "GET",
                        url: '/schedules/' + schedule.id + '/timetables/all',
                        success: function (data) {
                            timetables = JSON.parse(data);
                            let s = '<div class="row justify-content-center buttonFont mt-2 mb-2">';
                            for (let i = 0; i < timetables.length; i++) {
                                if (i % 6 === 0 && i !== 0)
                                    s += '</div><div class="row justify-content-center buttonFont mb-2">';
                                let sec = timetables[i].startTime[1];
                                if (sec <= 9)
                                    sec = '0' + sec;
                                if (timetables[i].available) {
                                    s += '<button type="button" id="' + i + '" class="timeBlocks" style="width: 120px !important;">';
                                }
                                else {
                                    s += '<button type="button" id="' + i + '" class="timeBlocks" style="width: 120px !important; background-color: #d9d9d9 !important;" disabled>';
                                }
                                s += timetables[i].startTime[0] + ':' + sec + '</button>';
                            }
                            s += '</div>';
                            timetablesRow.html(s);
                            timetablesCard.prop('hidden', false);
                            editSchedulesButtons.prop('hidden', false);
                        },
                        error: function (error) {
                            console.log('ERROR : ' + error);
                        }
                    });
                }
            }
        });
    }

    let clickedButton;
    timetablesRow.on('click', 'button.timeBlocks', function() {
        $(this).addClass('activeButton');
        if (typeof clickedButton !== "undefined")
            clickedButton.removeClass("activeButton");
        clickedButton = $(this);
        $(this).addClass("activeButton");

        let minutes = $(this).text().split(":")[1];
        if (minutes.charAt(0) === '0') minutes = minutes.charAt(1);
        datepickerEditMessage($(this).prop('id'), $(this).text().split(":")[0], minutes);

        $("#submitEditTimetable").on('click', function() {
            let id = parseInt($("#timeTableId").text());
            let editHoursVal = parseInt($('#editHoursForm').val())
            let editMinutesVal = parseInt($('#editMinutesForm').val())
            timetables[id].startTime[0] = editHoursVal;
            timetables[id].startTime[1] = editMinutesVal;
            let timetableId = '#' + id;
            let timetable =  $(timetableId);
            if (editMinutesVal <= 9)
                editMinutesVal = '0' + editMinutesVal;
            timetable.text(editHoursVal + ':' + editMinutesVal);
            datepickerCardMessage('Ниже представлены все графики выбранного расписания (серым отмечены занятые приёмом)! Нажмите на тот, который хотите изменить', 'las la-hand-pointer');
        });

        $("#submitDeleteTimetable").on('click', function() {
            let id = parseInt($("#timeTableId").text());
            timetables[id] = null;
            let timetableId = '#' + id;
            let timetable = $(timetableId);
            timetable.remove();
            datepickerCardMessage('Ниже представлены все графики выбранного расписания (серым отмечены занятые приёмом)! Нажмите на тот, который хотите изменить', 'las la-hand-pointer');
        });

    });

    submitEditSchedule.on('click', function() {
        $.ajax({
            type: "PUT",
            url: '/schedules/' + schedule.id + '/edit-timetables',
            data: {
                timetablesJson: JSON.stringify(timetables.filter(elem => {
                    return elem !== null;
                }))
            },
            success: function (data) {
                datepickerCardMessage('Графики расписания были успешно изменены!', 'las la-check-circle');
                setTimeout(function() {
                    cancel();
                }, 4000);
            },
            error: function (error) {
                console.log('ERROR : ' + error);
            }
        });
    });

    cancelEditSchedule.on('click', function() {
        cancel();
    });

    function datepickerEditMessage(id, hours, minutes) {
        datepickerCard.html('<span class="buttonFont">Редактировать приём:</span>\n' +
            '                <div class="row d-flex justify-content-center mt-2 align-items-center">\n' +
            '                <div id="timeTableId" hidden>' + id + '</div>\n' +
            '                    <input class="form-control" style="width: 65px;" id="editHoursForm" type="number" min="8" max="22" value="' + hours + '">\n' +
            '                    <input class="form-control ms-3" style="width: 65px;" id="editMinutesForm" type="number" min="0" max="59" value="' + minutes + '">\n' +
            '                </div>\n' +
            '                 <div class="row mt-2">\n' +
            '                    <button class="btn btn-primary" id="submitEditTimetable">Подтвердить</button>\n' +
            '                    <button class="btn btn-danger mt-2" id="submitDeleteTimetable">Удалить</button>\n' +
            '                </div></div>');
    }

    function datepickerCardMessage(message, iconClass) {
        datepickerCard.html('<div className="m-3"><h4 className="themes" style="font-size: 20px">' + message +
            '</h4><i class="' + iconClass + ' la-3x"></i><div class="row d-flex align-items-center">');
    }

    function datepickerCardMessageWithButtons(message, iconClass, buttonId) {
        datepickerCard.html('<div className="m-3"><h4 className="themes" style="font-size: 20px">' + message +
            '</h4><i class="' + iconClass + ' la-3x"></i><div class="row d-flex align-items-center">' +
            '<button class=" mt-5 button btn btn-primary mx-auto" id="' + buttonId + '" style="width: 190px !important;">Подтвердить</button>' +
            '<button class="cancel mt-5 button btn btn-primary mx-auto" style="background-color: red !important; width: 120px !important;">Отменить</button></div>');

    }

    function toggleDatepickerHoverStyle(isDefault) {
        let style;
        if (isDefault) {
            style = '.ui-state-default:hover {\n' +
                '        background: #1f4326 !important;\n' +
                '    }\n' +
                '    .ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {\n' +
                '        background: #1f4326 !important;\n' +
                '    }';
        }
        else {
            style = '.ui-state-default:hover {\n' +
                '        background: #DD9919 !important;\n' +
                '    }\n' +
                '    .ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {\n' +
                '        background: #DD9919 !important;\n' +
                '    }';
        }
        $("#datepickerStyle").html(style);
    }

});