$(document).ready(function () {

    let inputDate = $("#inputDate");
    let disabledDays = [];
    let schedules = JSON.parse(schedulesJson);
    console.log(schedules);
    if (schedules != null && schedules.length !== 0) {
        schedules.forEach(schedule => {
            disabledDays.push(schedule.date);
        });
    }

    $.datepicker.setDefaults($.datepicker.regional["ru"]);
    $("#datepicker").datepicker({
        dateFormat: 'yy-mm-dd',
        minDate: 0,
        numberOfMonths: 2,
        beforeShowDay: disableAllTheseDays,
        onSelect: function () {
            inputDate.val($("#datepicker").datepicker({dateFormat: 'yyyy-MM-dd'}).val());
        }
    });

    function disableAllTheseDays(date) {
        let sdate = $.datepicker.formatDate('dd-mm-yy', date)
        if ($.inArray(sdate, disabledDays) !== -1) {
            return [false];
        }
        return [true];
    }

    let newScheduleButton = $("#newScheduleButton");
    let newScheduleForm = $("#newScheduleForm");
    let timetablesRow = $("#timetablesRow");
    let editTimetableRow = $("#editTimetableRow")
    let timetables;
    let schedule;

    newScheduleButton.on('click', function() {
        if (newScheduleForm[0].checkValidity()) {
            if (inputDate.val() === "") alert("Выберете пожалуйста дату")
            else {
                let previewSchedule = {};
                previewSchedule.date = inputDate.val();
                previewSchedule.startTime = $("#inputStartTime").val();
                previewSchedule.endTime = $("#inputEndTime").val();
                previewSchedule.consultingTime = $("#inputConsultingTime").val();
                schedule = JSON.stringify(previewSchedule);
                $.ajax({
                    type: "GET",
                    url: "/schedules/preview/timetables",
                    data: {
                        previewSchedule: schedule,
                        breakTime: $("#inputBreakTime").val(),
                        breakDuration: $("#inputBreakDuration").val()
                    },
                    success: function (data) {
                        timetables = JSON.parse(data);
                        let s = '<div class="row justify-content-center buttonFont mt-2 mb-2">';
                        for (let i = 0; i < timetables.length; i++) {
                            if (i % 5 === 0 && i !== 0)
                                s += '</div><div class="row justify-content-center buttonFont mb-2">';
                            let sec = timetables[i].startTime[1];
                            if (sec <= 9)
                                sec = '0' + sec;
                            s += '<button type="button" id="' + i + '" class="timeBlocks" style="width: 120px !important;">' + timetables[i].startTime[0] + ':' + sec + '</button>';
                        }
                        s += '</div>';
                        timetablesRow.html(s);
                        newScheduleButton.prop('hidden', true);
                        $("#submitNewScheduleButton").prop('hidden', false);
                        $("#datepicker").attr('readonly', 'readonly');
                        $("#inputStartTime").prop('disabled', true);
                        $("#inputEndTime").prop('disabled', true);
                        $("#inputConsultingTime").prop('disabled', true);
                        $("#inputBreakTime").prop('disabled', true);
                        $("#inputBreakDuration").prop('disabled', true);
                        editTimetableRow.prop("hidden", false);
                    },
                    error: function (error) {
                        console.log("ERROR: " + error)
                    }
                });
            }
        }
        else newScheduleForm[0].reportValidity();
    });

    let clickedButton;
    timetablesRow.on('click', 'button.timeBlocks', function() {
        $(this).addClass('activeButton');
        if (typeof clickedButton !== "undefined")
            clickedButton.removeClass("activeButton");
        clickedButton = $(this);
        $(this).addClass("activeButton");

        let minutes = $(this).text().split(":")[1];
        if (minutes.charAt(0) === '0') minutes = minutes.charAt(1);
        editTimetableRow.html('<span class="buttonFont">Редактировать приём:</span>\n' +
            '                <div class="row d-flex justify-content-center mt-2 align-items-center">\n' +
            '                <div id="timeTableId" hidden>' + $(this).prop('id') + '</div>\n' +
            '                    <input class="form-control" style="width: 65px;" id="editHoursForm" type="number" min="8" max="22" value="' + $(this).text().split(":")[0] + '">\n' +
            '                    <input class="form-control ms-3" style="width: 65px;" id="editMinutesForm" type="number" min="0" max="59" value="' + minutes + '">\n' +
            '                </div>\n' +
            '                 <div class="row mt-2">\n' +
            '                    <button class="btn btn-primary button" style="height: 40px;" id="submitEditTimetable">Подтвердить</button>\n' +
            '                    <button class="btn btn-danger button mt-2" style="background: gray !important; height: 40px;" id="submitDeleteTimetable">Удалить</button>\n' +
            '                </div></div>');

        $("#submitEditTimetable").on('click', function() {
            let id = parseInt($("#timeTableId").text());
            let editHoursVal = parseInt($('#editHoursForm').val());
            let editMinutesVal = parseInt($('#editMinutesForm').val());
            timetables[id].startTime[0] = editHoursVal;
            timetables[id].startTime[1] = editMinutesVal;
            let timetableId = '#' + id;
            let timetable =  $(timetableId);
            if (editMinutesVal <= 9)
                editMinutesVal = '0' + editMinutesVal;
            timetable.text(editHoursVal + ':' + editMinutesVal);
            getEditRulesDiv(timetable);
            console.log(timetables);
        });

        $("#submitDeleteTimetable").on('click', function() {
            let id = parseInt($("#timeTableId").text());
            timetables[id] = null;
            let timetableId = '#' + id;
            let timetable = $(timetableId);
            timetable.prop('hidden', true);
            getEditRulesDiv(timetable);
            console.log(timetables);
        });


    });

    function getEditRulesDiv(timetable) {
        timetable.removeClass('activeButton');
        $("#editTimetableRow").html('<div class="m-3">\n' +
            '                                        <h4 class="themes" style="font-size: 20px">Вы можете редактировать каждый приём отдельно. (Длительность  приёма останется такой-же)</h4>\n' +
            '                                        <i class="las la-pencil-alt la-3x"></i>\n' +
            '                                    </div>');
    }

    $("#submitNewScheduleButton").on('click', function() {
        $.ajax({
            type: "POST",
            url: "/schedules/new",
            data: {
                scheduleJson: schedule,
                timetablesJson: JSON.stringify(timetables.filter(elem => {
                    return elem !== null;
                })),
            },
            success: function (data) {
                window.location = "http://localhost:443" + data;
                console.log("SUCCESS: " + data)
            },
            error: function(error) {
                console.log("ERROR : " + error)
            }
        });
    })

});