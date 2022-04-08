$(document).ready(function () {

    $("#file-input").on("change", function () {
        var file = $("#file-input").get(0).files[0];
        if (file) {
            var reader = new FileReader();

            reader.onload = function(){
                $(".mainPhotoInput").prop("src", reader.result);
            }

            reader.readAsDataURL(file);
        }
    });
});
