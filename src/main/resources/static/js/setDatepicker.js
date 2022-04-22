function setDatepicker(birthDate, submitButton) {
    $(document).ready(function () {

        var style = $('<style> .ui-datepicker { position: absolute !important; ' +
                                    'top: ' + (birthDate.offset().top + birthDate.outerHeight()) + 'px !important; left: ' + birthDate.offset().left + 'px !important; }</style>')
        $('html > head').append(style);


        $.datepicker.setDefaults($.datepicker.regional["ru"]);
        birthDate.datepicker(
            {
                showAnim: 'slideDown',
                dateFormat: 'dd-mm-yy',
                changeMonth: true,
                changeYear: true,
                yearRange: "1930:2022",
                orientation: "auto",
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

