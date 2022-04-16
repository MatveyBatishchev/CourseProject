$(document).ready(function() {
    let fullName = $("#inputDoctorFullName");
    let speciality = $("#inputDoctorSpeciality");
    $("#findDoctorButton").on("click", function() {
            $.ajax({
                type: "GET",
                url: "/doctors/find",
                data: {
                    fullName: fullName.val(),
                    speciality: speciality.val()
                },
                success: function (data) {
                    let doctors = JSON.parse(data);
                    if (doctors.length > 0) {
                        let s = '<div class="row gx-3 mb-3">\n';
                        for (let i = 0; i < doctors.length; i++) {
                            if (i !== 0 && i % 4 === 0) {
                                s += '</div>\n<div class="row gx-3 mb-3">\n';
                            }
                            console.log(doctors[i].specialities);
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
                        s += '</div>';
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

});

const declension = ['год', 'года', 'лет'];

function plural(number) {
    cases = [2, 0, 1, 1, 1, 2];
    return declension[ (number%100>4 && number%100<20)? 2 : cases[(number%10<5)?number%10:5] ];
}

