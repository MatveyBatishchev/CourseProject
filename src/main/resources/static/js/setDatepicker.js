function setDatepicker(birthDate, submitButton) {
    $(document).ready(function () {
        $.datepicker.setDefaults($.datepicker.regional["ru"]);
        birthDate.datepicker(
            {
                showAnim: 'slideDown',
                dateFormat: 'dd-mm-yy',
                changeMonth: true,
                changeYear: true,
                yearRange: "1930:2022",
            });

        var cleave = new Cleave(birthDate, {
            date: true,
            delimiter: '-',
            datePattern: ['d', 'm', 'Y']
        });

        submitButton.on('click', function() {
            var date = birthDate.datepicker('getDate');
            var month = date.getMonth() + 1;
            month = month.toString().length === 1 ? '0' + month : month;
            var day = date.getDate().toString().length === 1 ? '0' + date.getDate() : date.getDate();
            $("#hiddenBirthDate").val(date.getFullYear() + "-" + month + "-" + day);
        });

    });
}

