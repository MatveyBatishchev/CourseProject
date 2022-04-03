$(document).ready(function() {
    const togglePassword = $(".togglePasswordClass").on("click", function () {
        $(".inputPasswordClass").each(function () {
            const type = $(this).prop("type") === "password" ? "text" : "password";
            $(this).prop("type", type);
        })
        togglePassword.toggleClass("bi-eye");
    });
});
