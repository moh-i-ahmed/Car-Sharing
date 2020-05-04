/**
 * Google Maps Scripts for the Car Sharing service
 */

//-------------------- User Dashboard Google Maps ----------------------------------------------------
/**
 * Places Autocomplete handler
 * @constructor
 */
function AutocompleteDirectionsHandler(map, origin_input, destination_input) {
	this.map = map;
	this.originPlaceId = null;
	this.destinationPlaceId = null;
	this.travelMode = 'DRIVING';
	this.directionsService = new google.maps.DirectionsService;
	this.directionsRenderer = new google.maps.DirectionsRenderer({
		polylineOptions: {
			strokeColor: "black"
		}});
	this.directionsRenderer.setMap(map);

	var originInput = origin_input;
	var destinationInput = destination_input;
	var leicesterCityBounds = new google.maps.LatLngBounds(
			new google.maps.LatLng(52.6037362, -1.18740),
			new google.maps.LatLng(52.640830, -1.112380));
	var originAutocomplete = new google.maps.places.Autocomplete(originInput, {
		types: ['geocode'],
		bounds: leicesterCityBounds,
		strictBounds: true,
		componentRestrictions: {
			country: "UK"
		}
	});

	// Specify just the place data fields that you need.
	//originAutocomplete.setFields(['place_id']);

	var destinationAutocomplete = new google.maps.places.Autocomplete(destinationInput, {
		types: ['geocode'],
		bounds: leicesterCityBounds,
		strictBounds: true,
		componentRestrictions: {
			country: "UK"
		}
	});
	// Specify just the place data fields that you need.
	//destinationAutocomplete.setFields(['place_id']);

	//  Update map and coordinates
	this.setupPlaceChangedListener(originAutocomplete, 'ORIG');
	this.setupPlaceChangedListener(destinationAutocomplete, 'DEST');

}

//Sets a listener place change the filter type on Places Autocomplete.
AutocompleteDirectionsHandler.prototype.setupPlaceChangedListener = function(
		autocomplete, mode) {
	var me = this;
	autocomplete.bindTo('bounds', this.map);

	autocomplete.addListener('place_changed', function() {
		//Pan Map
		map.panBy(250,100);

		var place = autocomplete.getPlace();
		console.log("Place now is: " + place.geometry.location.lat());

		if (!place.place_id) {
			window.alert('Please select an option from the dropdown list.');
			return;
		}
		// Origin Changed
		if (mode === 'ORIG') {
			me.originPlaceId = place.place_id;

			// Update Origin lat/lng input fields
			document.getElementById('loc_lat').value = place.geometry.location.lat();
			document.getElementById('loc_long').value = place.geometry.location.lng(); 
		} 
		// Destination Changed
		else {
			me.destinationPlaceId = place.place_id;

			// Update Destination lat/lng input fields
			document.getElementById('end_lat').value = place.geometry.location.lat();
			document.getElementById('end_long').value = place.geometry.location.lng();
		}
		// Draw route
		me.route();
	});
};

//Draw Route from Origin to Destination
AutocompleteDirectionsHandler.prototype.route = function() {
	if (!this.originPlaceId || !this.destinationPlaceId) {
		return;
	}
	var me = this;

	this.directionsService.route(
			{
				origin: {'placeId': this.originPlaceId},
				destination: {'placeId': this.destinationPlaceId},
				travelMode: this.travelMode
			},
			function(response, status) {
				if (status === 'OK') {
					me.directionsRenderer.setDirections(response);
					console.log('distance is ' + (response.routes[0].legs[0].distance.value)/1000 + ' km');
					//Set driving distance
					document.getElementById('distance').value = response.routes[0].legs[0].distance.value;
				} else {
					window.alert('Directions request failed due to ' + status);
				}
			});
};

//Find user's current location 
function autolocator(originInput, destinationInput, button1, button2) {
	var locatorSection = document.getElementById("search_input");
	var locatorSection = document.getElementById("search_input")

	function init() {
		var locatorButton = document.getElementById("locator-button");
		locatorButton.addEventListener("click", locatorButtonPressed)
	}
	function locatorButtonPressed() {
		locatorSection.classList.add("loading")
		navigator.geolocation.getCurrentPosition(function (position) {
			getUserAddressBy(position.coords.latitude, position.coords.longitude)
		},
		function (error) {
			locatorSection.classList.remove("loading")
			alert("The Locator was denied :( Please add your address manually")
		})
	}
	function getUserAddressBy(lat, long) {
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function () {
			if (this.readyState == 4 && this.status == 200) {
				var address = JSON.parse(this.responseText)
				setAddressToInputField(address.results[0].formatted_address)
			}
		};
		xhttp.open("GET", "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + long + "&key=AIzaSyAi9qsACSRpv-drbqKhofMzyVFf_Hr_fI0", true);
		xhttp.send();
	}
	function setAddressToInputField(address) {
		var input = document.getElementById("search_input");
		var input = originInput;
		input.value = address;
	}
	init()
}

//Google Maps view for Request
function requestViewInitializer(map, directionsService, directionsRenderer) {
	var travelMode = 'DRIVING';
	directionsService = new google.maps.DirectionsService;
	directionsRenderer = new google.maps.DirectionsRenderer({
		label: 'Point',
		markerOptions: {
			draggable: false,
			animation: google.maps.Animation.DROP,
			map: map,
			//icon: 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png'
		},
		polylineOptions: {
			strokeColor: "black",
			//suppressMarkers: true
		}
	});

	map = new google.maps.Map(document.getElementById('map'), {
		mapTypeControl: false,
		center: {lat: 52.6369, lng: -1.1398},
		zoom: 18,
		options: {
			clickableIcons: false,       // Disable map icons
			streetViewControl: false,    // Disable street view
			fullscreenControl: false,    // Disable fullscreen button
			zoomControl: true,
			gestureHandling: 'none',
			draggable: false
		}
	});
	directionsRenderer.setMap(map);

	showRoute(originInput, destinationInput, directionsService, directionsRenderer);
}

//Show driving route to map view
function showRoute(originInput, destinationInput, directionsService, directionsRenderer) {
	var request = {
			origin: originInput,
			destination: destinationInput,
			travelMode: 'DRIVING'
	};
	directionsService.route(request, function(result, status) {
		if (status == 'OK') {
			directionsRenderer.setDirections(result);
			var leg = result.routes[0].legs[0];

			$("#pickup").text(leg.start_address);
			$("#dropoff").text(leg.end_address);
		}
	});
}

function geocodeLatLng(geocoder, map, infowindow, latlng) {
	geocoder.geocode({'location': latlng}, function(results, status) {
		if (status === 'OK') {
			if (results[0]) {
				map.setZoom(16);
				map.setCenter(latlng);
				// var iconBase =
				'https://developers.google.com/maps/documentation/javascript/examples/full/images/';
				var marker = new google.maps.Marker({
					position: latlng,
					draggable: false,
					animation: google.maps.Animation.DROP,
					map: map,
					//icon: iconBase + 'parking_lot_maps.png'
				});
				infowindow.setContent(results[0].formatted_address);
				infowindow.open(map, marker);
			} else {
				window.alert('No results found');
			}
		} else {
			window.alert('Geocoder failed due to: ' + status);
		}
	});
}

function carLocation(searchInput) {
	$(document).ready(function () {
		var leicesterCityBounds = new google.maps.LatLngBounds(
				new google.maps.LatLng(52.6037362, -1.18740),
				new google.maps.LatLng(52.640830, -1.112380));
		
		var autocomplete;
		autocomplete = new google.maps.places.Autocomplete((document.getElementById(searchInput)), {
			types: ['geocode'],
			bounds: leicesterCityBounds,
			strictBounds: true,
			componentRestrictions: {
				country: "UK"
			}
		});

		google.maps.event.addListener(autocomplete, 'place_changed', function () {
			var near_place = autocomplete.getPlace();
			document.getElementById('loc_lat').value = near_place.geometry.location.lat();
			document.getElementById('loc_long').value = near_place.geometry.location.lng();
		});
	});

	//Update Latitude/Longitude
	$(document).on('change', '#'+searchInput, function () {
		document.getElementById('latitude_input').value = '';
		document.getElementById('longitude_input').value = '';
	});
}