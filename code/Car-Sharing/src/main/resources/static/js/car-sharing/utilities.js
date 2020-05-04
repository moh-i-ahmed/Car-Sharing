/**
 * Utilities used all around the web application
 */

//Fade out Notifications after 5 seconds
function notificationAlert() {
	//Fade out toast after 5 seconds
	$(document).ready(function(){
		$('.toast').toast('show');
	});
	
	//Fade out alert after 5 seconds
    $(document).ready(function() {
        // show the alert
        setTimeout(function() {
            $(".alert").alert('close');
        }, 5000);  
    });
}

//Validate input to be only numerical
function onlyNumberKey(evt) { 
	// Only ASCII charactar in that range allowed 
	var ASCIICode = (evt.which) ? evt.which : evt.keyCode 
			if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57)) 
				return false; 
	return true; 
} 

//Countdown Timer
function countdownTimer() {
	var seconds = 60;
	$("countdown").text(seconds);

	setInterval(function () {
		seconds--;
		//console.log("Seconds " + seconds);
		document.getElementById("countdown").innerHTML = seconds;
		if (seconds == 0) {
			window.location.href='/user/dashboard';
		}
	}, 1000);
} 