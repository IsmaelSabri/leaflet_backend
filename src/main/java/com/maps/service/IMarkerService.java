package com.maps.service;

import com.maps.entities.Marker;

import java.util.*;

public interface IMarkerService {
	
	Marker addNewMarker(Marker marker);
	List<Marker> getMarkers();
}
