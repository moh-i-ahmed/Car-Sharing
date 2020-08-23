/**
 * GIJGO DateTimePicker Custom Configuration & Validation
 * 
 */

//Initialize DateTimePicker
function dateTimePickerInitializer() { 
	var dateTimeFormatter = 'hh:mm A DD/MM/YYYY';

	var startMoment = moment().add(15, 'minutes').format(dateTimeFormatter);	//start time 15 minutes from now
	var endMoment = moment().add(75, 'minutes').format(dateTimeFormatter);		//end time 75 minutes from now

	console.log("Start Moment time is " + startMoment);
	console.log("End Moment time is " + endMoment);

	//StartTime Initialization
	var $startTimepicker = $('#startTime').datetimepicker({
		uiLibrary: 'materialdesign', format: 'hh:MM TT dd/mm/yyyy', modal: true, header: true, footer: true });

	//EndTime Initialization
	var $endTimepicker = $('#endTime').datetimepicker({
		uiLibrary: 'materialdesign', format: 'hh:MM TT dd/mm/yyyy', modal: true, header: true, footer: true });    

	//Set default times
	$startTimepicker.val(startMoment); 
	$endTimepicker.val(endMoment);
}

//DateTimePicker EventListener 
function dateTimePickerEventListener() {

	$( "#startTime, #endTime" ).change(function() {
		//Time formatter
		var dateTimeFormatter = 'hh:mm A DD/MM/YYYY';
		
		var startTimeString = $("#startTime").val();
		var endTimeString = $("#endTime").val();

		var startTime = moment(startTimeString, dateTimeFormatter);			//Convert start time to moment object
		var endTime = moment(endTimeString, dateTimeFormatter);				//Convert end time to moment object
		
		var durationOutput = moment.duration(endTime.diff(startTime));		//Difference between start & end times

		console.log('Duration milliseconds: ' + durationOutput );

		//From stackoverflow: https://stackoverflow.com/questions/1787939/check-time-difference-in-javascript

		//Milliseconds value of duration
		var msec = durationOutput;			

		//Hour value
		var hours = Math.floor(msec / 1000 / 60 / 60);
		msec -= hours * 1000 * 60 * 60;

		//Minute value
		var minutes = Math.floor(msec / 1000 / 60);

		//Times in the past
		if (moment(startTime).isBefore(moment()) || moment(endTime).isBefore(moment())) {
			$("#request_car").attr("disabled", true);
			$("#error_time").text( "Invalid: Time selected is in the past" );
		} 
		//End time is before start time
		else if(moment(endTime).isSameOrBefore(moment(startTime))){
			$("#request_car").attr("disabled", true);
			$("#error_time").text( "Invalid: End time must be at least 30 mins after Start time" );
		}
		//Same times 
		else if(moment(endTime).isSame(moment(startTime))){
			$("#request_car").attr("disabled", true);
			$("#error_time").text("Choose different times");
		}
		//Less than 30 minutes
		else if((hours < 1) && (minutes < 30)){
			$("#request_car").attr("disabled", true);
			$("#error_time").text("Minimum usage time is 30 minutes");
		}
		//More than 30 minutes
		else if((hours < 1) && (minutes >= 30)){
			$('#request_car').removeAttr("disabled");
			$("#error_time").text(minutes + " mins");
		}
		//Exact hour(s)
		else if((hours >= 1) && (minutes == 0)){
			$('#request_car').removeAttr("disabled");
			if (hours == 1) {
				$("#error_time").text(hours + " hour");
			}
			else {
				$("#error_time").text(hours + " hours");
			}
		}
		//Exact hour(s)
		else if(hours >= 24){
			$("#request_car").attr("disabled", true);
			$("#error_time").text("Maximum usage time is 24 hours");
		}
		// More than 1 hour
		else{
			$('#request_car').removeAttr("disabled");
			$("#error_time").text(hours + " hours, " + minutes + " mins");
		}
	});
}
