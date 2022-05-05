$(document).ready(function() {

    let pageNumber = 0;
    let fullName = $("#inputDoctorFullName");
    let speciality = $("#inputDoctorSpeciality");
    let moreButton = $("#moreDoctorsButton");

    checkPages();


    $("#findDoctorButton").on("click", function() {
        pageNumber = 0;
        // перенести в другое место
        //if (pageNumber+1 != totalPages) $("#moreDoctorsButton").prop('hidden', false);
            $.ajax({
                type: "GET",
                url: "/doctors/by-expanded-search",
                data: {
                    fullName: fullName.val(),
                    speciality: speciality.val(),
                    pageNumber: 0
                },
                success: function (data) {
                    console.log(data);
                    let jsonObject = JSON.parse(data);
                    let doctors = JSON.parse(jsonObject.doctors);
                    if (doctors.length > 0) {
                        totalPages = jsonObject.totalPages;
                        console.log(totalPages)
                        checkPages();
                        let s = '<div class="row gx-3 mb-3">\n';
                        for (let i = 0; i < doctors.length; i++) {
                            if (i !== 0 && i % 4 === 0) {
                                s += '</div>\n<div class="row gx-3 mb-3">\n';
                            }
                            s += '<div class="col-md-3">\n' +
                                '                          <div class="card" style="width: 18rem;">\n' +
                                '                            <div class="card-body">\n' +
                                '                             <div class="row">\n' +
                                '                              <div class="col-md-6">\n' +
                                '                                <div class="photo mt-2"></div>\n' +
                                '                              </div>\n' +
                                '                              <div class="col-md-6 pt-2">\n' +
                                '                                <h5 class="card-title">' + doctors[i].surname + ' ' + doctors[i].name + ' ' + doctors[i].patronymic + '</h5>\n' +
                                '                                <p class="card-text mt-4 font-weight-underline">' + doctors[i].specialities + '</p>\n' +
                                '                              </div>\n' +
                                '                             </div>\n' +
                                '                              <p class="card-text mt-3">Cтаж: ' + doctors[i].experience + ' ' + plural(doctors[i].experience) + '</p>\n' +
                                '                              <p class="card-text mt-1">Категория: ' + doctors[i].category + '</p>\n' +
                                '                              <a href="#" class="btn btn-secondary">Отзывы</a>\n' +
                                '                              <a href="#" class="btn btn-primary">Записаться онлайн</a>\n' +
                                '                            </div>\n' +
                                '                          </div>\n' +
                                '                          </div>\n';
                        }
                        s += '</div>\n<div id="moreDoctorsPlaceholder"></div>';
                        $("#foundDoctors").html(s);
                    }
                    else {
                        $("#foundDoctors").html('<p class="text-center mt-5">По вашему запросу ничего не найдено</p>');
                    }
                },
                error: function(error) {
                }
            });
    })

    $("#inputDoctorSpeciality").on("change", function() {
        $(this).find('option[value=""]').prop('text', 'Все направления');
    })

    moreButton.on('click', function() {
        pageNumber++;
        if (totalPages === pageNumber+1) $(this).prop('hidden', true);
        $.ajax({
            type: "GET",
            url: "/doctors/by-expanded-search",
            data: {
                fullName: fullName.val(),
                speciality: speciality.val(),
                pageNumber: pageNumber
            },
            success: function (data) {
                let doctors = JSON.parse(JSON.parse(data).doctors);
                if (doctors.length > 0) {
                    let s = '<div class="row gx-3 mb-3">\n';
                    for (let i = 0; i < doctors.length; i++) {
                        if (i !== 0 && i % 4 === 0) {
                            s += '</div>\n<div class="row gx-3 mb-3">\n';
                        }
                        s += '<div class="col-md-3">\n' +
                            '                          <div class="card" style="width: 18rem;">\n' +
                            '                            <div class="card-body">\n' +
                            '                             <div class="row">\n' +
                            '                              <div class="col-md-6">\n' +
                            '                                <div class="photo mt-2"></div>\n' +
                            '                              </div>\n' +
                            '                              <div class="col-md-6 pt-2">\n' +
                            '                                <h5 class="card-title">' + doctors[i].surname + ' ' + doctors[i].name + ' ' + doctors[i].patronymic + '</h5>\n' +
                            '                                <p class="card-text mt-4 font-weight-underline">' + doctors[i].specialities + '</p>\n' +
                            '                              </div>\n' +
                            '                             </div>\n' +
                            '                              <p class="card-text mt-3">Cтаж: ' + doctors[i].experience + ' ' + plural(doctors[i].experience) + '</p>\n' +
                            '                              <p class="card-text mt-1">Категория: ' + doctors[i].category + '</p>\n' +
                            '                              <a href="#" class="btn btn-secondary">Отзывы</a>\n' +
                            '                              <a href="#" class="btn btn-primary">Записаться онлайн</a>\n' +
                            '                            </div>\n' +
                            '                          </div>\n' +
                            '                          </div>\n';
                    }
                    s += '</div><div id="moreDoctorsPlaceholder"></div>';
                    $("#moreDoctorsPlaceholder").replaceWith(s);
                }
                else {
                    $("#foundDoctors").html('<p class="text-center mt-5">По вашему запросу ничего не найдено</p>');
                }
            },
            error: function(error) {
                console.log(error);
            }
        });
    });

    function checkPages() {
        if (totalPages == 1) moreButton.prop('hidden', true);
        else moreButton.prop('hidden', false);
    }

});





