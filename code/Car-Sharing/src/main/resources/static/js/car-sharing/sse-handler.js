/**
 * SSE source handler
 *
 */
function initializeSSE(source) {

	// Open connection
	// check if it's from a correct source https://www.html5rocks.com/en/tutorials/eventsource/basics/
	const eventSource = source;//new EventSource('http://localhost:8080/sse/notification/' + requestID);
	eventSource.onmessage = e => {
		const msg = e.data;
		const msgID = e.lastEventId;

		// Request hasn't started
		if (msgID == 'unstarted') {
			//$('#time').text(msg);
			console.log("id: " + e.lastEventId  + ", data: " + e.data);
		}
		// Request is active
		else if (msgID == 'active') {
			$('#time').text(msg);
			console.log("id: " + e.lastEventId  + ", data: " + e.data);   
			//setTimeout(function() {
			//location.reload();
			//}, 3000);
		}
		// Request has ended
		else if (msgID == 'ended') {
			// Close connection
			eventSource.close();

			// Show timer modal
			$('#requestInfoModal').modal('show');

			// Change HTML text
			$('#time').text(msg);
			$('#status').text('Completed');

			// Remove Div  
			$('#timerGroup').remove();
			$('#buttonDiv').remove();
			//$('#cancelBtn').text("Ended");

			// Refresh page
			countdownTimer();
			setTimeout(function() {
				//location.reload();
				window.location.href='/user/dashboard';
			}, 150000);
		}
		else {
			$('#time').text(msg);

			console.log("id: " + e.lastEventId  + ", data: " + e.data);
		} 
	};
	eventSource.onopen = e => console.log('open');
	eventSource.onerror = e => {
		if (e.readyState == EventSource.CLOSED) {
			console.log('close');
		}
		else {
			console.log(e);
		}
	};
	eventSource.addEventListener('second', function(e) {
		console.log('second', e.data);
	}, false);
}