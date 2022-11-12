package com.maps.service.impl;
import java.util.*;

import com.maps.entities.Edificio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maps.dao.IMarkerRepository;
import com.maps.entities.Marker;
import com.maps.service.IMarkerService;
@Service
public class MarkerServiceImpl implements IMarkerService{
	@Autowired
	private IMarkerRepository markerRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public List<Marker> getMarkers(){
		return markerRepository.findAll();
	}

	@Override
	public Marker addNewMarker(Marker marker) {
		LOGGER.info("Nuevo marker creado con id: " + marker.getId() + " con latitud: " + marker.getLat() + " y longitud: " + marker.getLng() +
				" con valoracion= " + marker.getStarRating());
		return markerRepository.save(marker);
	}

}
