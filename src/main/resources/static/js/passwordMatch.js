var check = function() {
    if (document.getElementById('passwordInput').value ==
        document.getElementById('confirmPasswordInput').value) {
        document.getElementById('passwordMessage').style.color = 'green';
        document.getElementById('passwordMessage').innerHTML = 'matching';
        document.getElementById('regSubmit').disabled = false;
    } else {
        document.getElementById('passwordMessage').style.color = 'red';
        document.getElementById('passwordMessage').innerHTML = 'not matching';
        document.getElementById('regSubmit').disabled = true;
    }
}