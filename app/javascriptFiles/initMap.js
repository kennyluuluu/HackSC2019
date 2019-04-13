
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
		disableDoubleClickZoom: true,
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
	
	map.addListener('dblclick', function(event){
		alert(map.getZoom());
		alert(map.getCenter());
	});

	var firstPartition = createFourQuadLatLng(USC_BOUNDS);
//	var searchBox = new google.maps.places.PlacesService(map);
//	searchBox.findPlaceFromQuery({
//		fields : ["geometry.location"],
//		locationBias : USC_BOUNDS,
//		query : "school"
//	}, function(places, status) {
//		if (status == google.maps.places.PlacesServiceStatus.OK) {
//			alert(places.length);
//			places.forEach(function(place){
//				var placeMarker = new google.maps.Marker({
//					position : place.geometry.location,
//					map: map
//				});
//				placeMarker.setMap(map);
//			});
//		}
//	});
}

function createFourQuadLatLng(latLngBound) {
	var middleLat = latLngBound.south + (latLngBound.north-latLngBound.south)/2;
	var middleLng = latLngBound.west + (latLngBound.east-latLngBound.west)/2;
	
	var topLeft = {
			north: latLngBound.north,
			south: middleLat,
			west: middleLng,
			east: latLngBound.east
	}
	
	var topRight = {
			north: latLngBound.north,
			south: middleLat,
			west: latLngBound.west,
			east: middleLng
	}
	
	var botLeft = {
			north: middleLat,
			south: latLngBound.sound,
			west: latLngBound.west,
			east: middleLng
	}
	
	var botRight = {
			north: middleLat,
			south: latLngBound.south,
			west: middleLng,
			east: latLngBound.east
	}
	
	return [
		topLeft,
		topRight,
		botLeft,
		botRight
	];
}