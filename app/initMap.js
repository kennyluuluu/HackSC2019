function initMap() {

	var USC_BOUNDS = {
			north: 34.025041245641624,
			south: 34.01805193609663,
			west: -118.29190204083517,
			east: -118.27867932420202
	}
	map = new google.maps.Map(document.getElementById('map'), {
		center: {lat: 34.02154666281329, lng: -118.28529068251858},
		zoom: 17,
		restriction : {
			latLngBounds: USC_BOUNDS,
			strictBounds: false
		},
		mapTypeControl: false,
	    scaleControl: true,
	    zoomControl: true,
	    zoomControlOptions: {
	        position: google.maps.ControlPosition.LEFT_TOP
	    },
	    fullscreenControl: true,
	    fullscreenControlOptions: {
	    	position: google.maps.ControlPosition.LEFT_TOP
	    },
	    streetViewControl: true,
	    streetViewControlOptions: {
	    	position: google.maps.ControlPosition.LEFT_TOP
	    }
	});

	var time;

	document.getElementById('time').addEventListener('change', function() {
		time = document.getElementById("time").value;
		if (time === "00") {
			time = new Date().getHours();
		} else {
			time = parseInt(time);
		}
		// query goes here
		console.log(typeof(time))
		console.log(time);
	})

	// var request = new XMLHttpRequest()
	// request.open('GET', ''. true)
	//
	// request.onload = function () {
	// 	var data = JSON.parse(this.response)
	// 	if (request.status === 200) {
	// 		data.forEach(class => {
	// 			if class.time === selectedHour {
	//
	// 			}
	// 		})
	// 	} else {
	// 		console.log('error')
	// 	}
	// }

}
