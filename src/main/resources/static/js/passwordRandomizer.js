function genPassword(len){
    var genPassword = "";
    var symbols = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    for (var i = 0; i < len; i++) {
        genPassword += symbols.charAt(Math.floor(Math.random() * symbols.length));
    }
    return genPassword;
}