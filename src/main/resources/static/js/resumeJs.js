$(document).ready(function() {
    // Year prefix
    $("#yearPrefix").text(plural($("#doctorExperience").text()));

    // Doctor specialities
    if (doctorSpecialities.length !== 0) {
        let specialitiesColumns = "";
        for (let i = 0; i < doctorSpecialities.length; i++) {
            if (i === 3) break;
            specialitiesColumns += '<div class="col col-md-4 p-0">\n' +
                '                                                <button class="button tag">' + doctorSpecialities[i] + '</button>\n' +
                '                                            </div>';
        }
        $("#doctorSpecialities").html(specialitiesColumns);
    }

    // ResumeParts render
    const edjsParser = edjsHTML();
    $("#aboutDoctorDiv").html(edjsParser.parse(JSON.parse(aboutDoctor)));
    $("#educationDiv").html(edjsParser.parse(JSON.parse(education)));
    $("#workPlacesDiv").html(edjsParser.parse(JSON.parse(workPlaces)));

    // Add Review Form
    let submitAddReview = $("#submitAddReview");
    let reviewComment = $("#reviewComment");
    let addReviewForm = $("#addReviewForm");
    let reviewAlert = $("#reviewAlert");

    // New review
    submitAddReview.on('click', function() {
        if (reviewComment.val().length < 50) {
            reviewAlert.html('<span class="text-danger">Минимально допустимое количество символов: 100. Длина текста сейчас: ' + reviewComment.val().length + '.</span>');
        }
        else {
            reviewAlert.html('');
            $.ajax({
                type: "POST",
                url: "/reviews/new",
                data: {
                    reviewComment: reviewComment.val(),
                    doctorId: doctorId
                },
                success: function (data) {
                    addReviewForm.html('<span style="text-align: center; font-size: 22px !important;">' + data + '</span>');
                },
                error: function (error) {
                    console.log(error);
                }
            });
        }
    });

    // Pagination
    let pageNumber = 1;
    let reviewsPerPage = 1;
    let totalPages = (reviewsNumber % reviewsPerPage === 0)? reviewsNumber / reviewsPerPage : (reviewsNumber / reviewsPerPage) + 1;
    let moreReviewsButton = $("#moreReviews");

    if (pageNumber === totalPages) moreReviewsButton.prop('hidden', true);
    moreReviewsButton.on('click', function() {
        $.ajax({
            type: "GET",
            url: "/reviews/doctor/" + doctorId + "/page/" + pageNumber,
            success: function (data) {
                let reviews = JSON.parse(data);
                let reviewsHTML = "";
                reviews.forEach(review => {
                    reviewsHTML += '<div class="card mt-3 reviewCard">\n' +
                        '                    <span class="text-muted">' + review.date + '</span>\n' +
                        '                    <div class="underline"></div>\n' +
                        '                    <div class="row col-md-12 mt-3">\n' +
                        '                        <div class="col-md-1">\n' +
                        '                            <div class="reviewPhoto"><img src="/applicationFiles/designElements/messageIcon.png" alt="img"></div>\n' +
                        '                        </div>\n' +
                        '                        <div class="col-md-11 ps-4" >\n' +
                        '                            <span style="font-size: 23px !important;">' + review.patient.reviewerName + '</span><br>\n' +
                        '                            <div class="row ms-0 mt-1 col-md-12 d-flex align-items-center">\n' +
                        '                                <div class="col ps-0">\n' +
                        '                                    <label class="mt-2 mb-2" for="reviewContent">Комментарий:</label>\n' +
                        '                                    <p id="reviewContent" style="background: #f5f5f5; border: 0; border-radius: 10px; padding: 0.5rem;">' + review.comment + '</p>\n' +
                        '                                </div>\n' +
                        '                            </div>\n' +
                        '                        </div>\n' +
                        '                    </div>\n' +
                        '                </div>';
                });
                reviewsHTML += '<div id="moreReviewsPlaceholder"></div>';
                $("#moreReviewsPlaceholder").replaceWith(reviewsHTML);
                pageNumber++;
                if (pageNumber === totalPages) moreReviewsButton.prop('hidden', true);
            },
            error: function (error) {
                console.log(error);
            }
        });
    });


});