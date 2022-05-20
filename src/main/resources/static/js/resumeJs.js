$(document).ready(function() {
    // Year prefix
    $("#yearPrefix").text(plural($("#doctorExperience").text()));

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
                    addReviewForm.html(' <div class="regardsBlock" >\n' +
                        '                                                    <p class="headlines">' + data + '</p>\n' +
                        '                                                </div>');
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
                    reviewsHTML += '<div class="d-flex align-items-center my-5">\n' +
                        '                            <div class="greenCommentLineAside" style="left: 255px;"></div>\n' +
                        '                            <div class="greenCommentLineAside" style="left: 275px; height: 180px"></div>\n' +
                        '                            <div class="doctorBody container-fluid row py-3" style="max-width: 1030px; background: white">\n' +
                        '                                <!--Фото отзыва-->\n' +
                        '                                <div class="col-3 photoFrameSquare ms-3">\n' +
                        '                                    <img class="photoResume" src="/applicationFiles/designElements/comment.jpg" alt="img">\n' +
                        '                                </div>\n' +
                        '                                <!--Комментарий-->\n' +
                        '                                <div class="col ms-4">\n' +
                        '                                    <span class="headlines">' + review.patient.reviewerName + '</span><br>\n' +
                        '                                    <div class="row ms-0 mt-1 col-md-12 d-flex align-items-center">\n' +
                        '                                        <div class="col ps-0">\n' +
                        '                                            <label class="buttonFont text-muted my-1"></label>\n' +
                        '                                            <p class="regTexts" id="reviewContent">' + review.comment + '\n' +
                        '                                            </p>\n' +
                        '                                        </div>\n' +
                        '                                    </div>\n' +
                        '                                </div>\n' +
                        '                                <!--Дата-->\n' +
                        '                                <div class="greenLineHorizontalDivider mt-4 mb-2" style="height: 2px; background: rgba(128,128,128,0.43);"></div>\n' +
                        '                                <div>\n' +
                        '                                    <span class="regTexts text-muted ms-4">Отзыв от: ' + review.date + '</span>\n' +
                        '                                </div>\n' +
                        '                            </div>\n' +
                        '                            <div class="greenCommentLineAside" style="left: -275px; height: 180px"></div>\n' +
                        '                            <div class="greenCommentLineAside" style="left: -255px;"></div>\n' +
                        '                        </div>';
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