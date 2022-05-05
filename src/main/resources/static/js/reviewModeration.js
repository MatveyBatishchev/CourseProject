$(document).ready(function () {

    $(".approveReviewButton").on('click', function() {
        let approveButton = $(this);
        if (!approveButton.hasClass('clicked')) {
            approveButton.text("Вы уверены?");
            approveButton.addClass('clicked');
            setTimeout(function() {
                approveButton.text("Одобрить");
                approveButton.removeClass('clicked')
            }, 5000);
        } else {
            let reviewId = $(this).closest('.tableTr');
            $.ajax({
                type: "PUT",
                url: "/reviews/" + reviewId.find('.reviewId').text() + "/approve",
                success: function (data) {
                    console.log("SUCCESS : ", data);
                },
                error: function (error) {
                    console.log("ERROR : ", error);
                }
            });
            reviewId.fadeOut();
        }
    });

    $(".deleteReviewButton").on('click', function() {
        let deleteButton = $(this);
        if (!deleteButton.hasClass('clicked')) {
            deleteButton.text("Вы уверены?");
            deleteButton.addClass('clicked');
            setTimeout(function() {
                deleteButton.text("Удалить");
                deleteButton.removeClass('clicked')
            }, 5000);
        } else {
            let reviewId = $(this).closest('.tableTr');
            $.ajax({
                type: "DELETE",
                url: "/reviews/" + reviewId.find('.reviewId').text() + "/delete",
                success: function (data) {
                    console.log("SUCCESS : ", data);
                },
                error: function (error) {
                    console.log("ERROR : ", error);
                }
            });
            reviewId.fadeOut();
        }
    });



});