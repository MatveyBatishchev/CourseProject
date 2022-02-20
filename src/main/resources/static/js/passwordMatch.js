var check = function() {
    if (document.getElementById('password').value ==
        document.getElementById('confirm_password').value) {
        document.getElementById('passwordMessage').style.color = 'green';
        document.getElementById('passwordMessage').innerHTML = 'matching';
        document.getElementById('submit').disabled = false;
    } else {
        document.getElementById('passwordMessage').style.color = 'red';
        document.getElementById('passwordMessage').innerHTML = 'not matching';
        document.getElementById('submit').disabled = true;
    }
}