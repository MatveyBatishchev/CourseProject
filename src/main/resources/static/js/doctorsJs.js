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
                    let jsonObject = JSON.parse(data);
                    let doctors = JSON.parse(jsonObject.doctors);
                    if (doctors.length > 0) {
                        totalPages = jsonObject.totalPages;
                        checkPages();
                        let s = '<div class="row mb-1">\n';
                        for (let i = 0; i < doctors.length; i++) {
                            if (i !== 0 && i % 4 === 0) {
                                s += '</div>\n<div class="row mb-1">\n';
                            }
                            let imageSrc;
                            if (doctors[i].image == null) imageSrc = '/applicationFiles/designElements/doctorAvatar.png'
                            else imageSrc = '/applicationFiles/doctors/' + doctors[i].id +'/' + doctors[i].image;
                            s += '<div class="col-3 mb-3">\n' +
                                '                                    <div class="doctorBody" style="width: 300px; height: 350px;">\n' +
                                '                                        <div class="container-fluid my-3">\n' +
                                '                                            <a href="https://localhost/doctors/' + doctors[i].id +'/resume" style="color: black">\n' +
                                '                                                <div class="row" style="height: 200px;">\n' +
                                '                                                    <div class="col-5 mt-2 photoFrameSquare">\n' +
                                '                                                        <img class="doctorPhotoInDoctors" src="' + imageSrc + '"\n' +
                                '                                                             alt="">\n' +
                                '                                                    </div>\n' +
                                '                                                    <div class="col-6 buttonFont mt-2">\n' +
                                '                                                        <h5 style="word-wrap: break-word;">' + doctors[i].surname + ' ' + doctors[i].name + ' ' + doctors[i].patronymic + '</h5>\n' +
                                '                                                        <div style="font-size: 19px;">' + doctors[i].category + '</div>\n' +
                                '                                                        <div style="font-size: 19px; margin-top: 6px;">Стаж: ' + doctors[i].experience + ' ' + plural(doctors[i].experience) + '</div>\n' +
                                '                                                    </div>\n' +
                                '                                                </div>\n' +
                                '                                                <div class="d-flex justify-content-center align-items-center doctorCardSpecialityTag" style="height: 60px;">\n' +
                                '                                                    <div class="buttonFont mt-2 pb-2 text-center">' + doctors[i].specialities.join(", ") + '</div>\n' +
                                '                                                </div>\n' +
                                '                                            </a>\n' +
                                '                                            <!--Buttons-->\n' +
                                '                                            <div class="mt-3 mb-3">\n' +
                                '                                                <a class="btn btn-primary button " style="height: auto;">Записаться онлайн</a>\n' +
                                '                                            </div>\n' +
                                '                                        </div>\n' +
                                '                                    </div>\n' +
                                '                                </div>';
                        }
                        s += '</div>\n<div id="moreDoctorsPlaceholder"></div>';
                        $("#foundDoctors").html(s);
                    }
                    else {
                        $("#foundDoctors").html('<p class="text-center buttonFont mt-5">По вашему запросу ничего не найдено</p>');
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
                        let imageSrc;
                        if (doctors[i].image == null) imageSrc = '/applicationFiles/designElements/doctorAvatar.png'
                            else imageSrc = '/applicationFiles/doctors/' + doctors[i].id +'/' + doctors[i].image;
                        s += '<div class="col-3 mb-3">\n' +
                            '                                    <div class="doctorBody" style="width: 300px; height: 350px;">\n' +
                            '                                        <div class="container-fluid my-3">\n' +
                            '                                            <a href="https://localhost/doctors/' + doctors[i].id +'/resume" style="color: black">\n' +
                            '                                                <div class="row" style="height: 200px;">\n' +
                            '                                                    <div class="col-5 mt-2 photoFrameSquare">\n' +
                            '                                                        <img class="doctorPhotoInDoctors" src="' + imageSrc + '"\n' +
                            '                                                             alt="">\n' +
                            '                                                    </div>\n' +
                            '                                                    <div class="col-6 buttonFont mt-2">\n' +
                            '                                                        <h5 style="word-wrap: break-word;">' + doctors[i].surname + ' ' + doctors[i].name + ' ' + doctors[i].patronymic + '</h5>\n' +
                            '                                                        <div style="font-size: 19px;">' + doctors[i].category + '</div>\n' +
                            '                                                        <div style="font-size: 19px; margin-top: 6px;">Стаж: ' + doctors[i].experience + ' ' + plural(doctors[i].experience) + '</div>\n' +
                            '                                                    </div>\n' +
                            '                                                </div>\n' +
                            '                                                <div class="d-flex justify-content-center align-items-center doctorCardSpecialityTag" style="height: 60px;">\n' +
                            '                                                    <div class="buttonFont mt-2 pb-2 text-center">' + doctors[i].specialities.join(", ") + '</div>\n' +
                            '                                                </div>\n' +
                            '                                            </a>\n' +
                            '                                            <!--Buttons-->\n' +
                            '                                            <div class="mt-3 mb-3">\n' +
                            '                                                <a class="btn btn-primary button " style="height: auto;">Записаться онлайн</a>\n' +
                            '                                            </div>\n' +
                            '                                        </div>\n' +
                            '                                    </div>\n' +
                            '                                </div>';
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
        if (totalPages === 1) moreButton.prop('hidden', true);
        else moreButton.prop('hidden', false);
    }

});





