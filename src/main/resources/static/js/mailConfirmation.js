var minutes = 0; // глобальные переменные
var seconds = 0;

function startTimer(duration, display) {
    var timer = duration, minutes, seconds;

    $("#confirmPatientEmail").prop("disabled", true);
    if (timer == 120) {
        display.textContent = "Для повторной отправки ожидайте: 02:00";
        $("#timer").prop("hidden", false);
        timer--;
    }

    let intervalId = setInterval(function () {
        minutes = parseInt(timer / 60, 10); // определяем начальное кол-во минут
        seconds = parseInt(timer % 60, 10); // определяем начальное кол-во секунд

        minutes = minutes < 10 ? "0" + minutes : minutes; // если кол-во меньше 10 то добавляем ноль перед цифрой
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.textContent = "Для повторной отправки ожидайте: " + minutes + ":" + seconds; // выводим контент в переданных тег

        setCookie("minutes", minutes.toString(), 1);
        setCookie("seconds", seconds.toString(), 1);

        if (--timer < 0) {
            timer = 0;
            setCookie("minutes", "", -1);
            setCookie("seconds", "", -1);
            $("#confirmPatientEmail").prop("disabled", false);
            $("#timer").prop("hidden", true);
            clearInterval(intervalId);
        }
    }, 1000); // обновляем таймер каждую секунду
}

function checkTimer(confirmationMarker) {
    var minutes_data = getCookie("minutes");
    var seconds_data = getCookie("seconds");
    if (!minutes_data || !seconds_data || confirmationMarker) { // если нет куки не запускаем таймер
        // no cookie found use default
    } else {
        var timer_amount = parseInt(minutes_data * 60) + parseInt(seconds_data)
        var fiveMinutes = timer_amount, display = document.querySelector('#timer');
        startTimer(fiveMinutes, display);
    }
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
    }
    return "";
}

