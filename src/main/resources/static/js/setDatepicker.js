function setDatepicker(birthDate) {
    $(document).ready(function () {
        $.datepicker.setDefaults($.datepicker.regional["ru"]);
        birthDate.datepicker(
            {
                showAnim: 'slideDown',
                dateFormat: 'dd-mm-yy',
                changeMonth: true,
                changeYear: true,
                yearRange: "1930:2022",
                onClose: function() {
                    var date = birthDate.datepicker('getDate');
                    var month = date.getMonth() + 1;
                    month = month.toString().length == 1 ? '0' + month : month;
                    var day = date.getDate().toString().length == 1 ? '0' + date.getDate() : date.getDate();
                    $("#hiddenBirthDate").val(date.getFullYear() + "-" + month + "-" + day);
                }
            });

        var cleave = new Cleave(birthDate, {
            date: true,
            delimiter: '-',
            datePattern: ['d', 'm', 'Y']
        });

        Scrollbar.init(document.querySelector('.ui-datepicker-year'), options);
        Scrollbar.init(document.querySelector('.ui-datepicker-month'), options);

    });
}

