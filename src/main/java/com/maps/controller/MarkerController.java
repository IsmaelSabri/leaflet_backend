package com.maps.controller;

import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import com.maps.entities.Edificio;
import com.maps.service.impl.EdificioServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.maps.entities.Marker;
import com.maps.service.IMarkerService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = { "/map" })
@CrossOrigin(origins = { "*" })
//@CrossOrigin(origins = "http://localhost:4200")
public class MarkerController {

	@Autowired
	private IMarkerService markerService;
	private String ruta;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@GetMapping("/list")
	public ResponseEntity<List<Marker>> getAllMarkers() {
		List<Marker> markers = markerService.getMarkers();
		return new ResponseEntity<>(markers, OK);
	}

	@PostMapping("/new")
	public ResponseEntity<Marker> addNewMarker(@RequestParam("lat") String lat, @RequestParam("lng") String lng,
											   @RequestParam("starRating") String starRating)
			throws IOException {
		Marker markerNuevo = new Marker();
		markerNuevo.setLat(lat);
		markerNuevo.setLng(lng);
		markerNuevo.setLat(starRating);
		markerService.addNewMarker(markerNuevo);
		LOGGER.info("lat: " + markerNuevo.getLat() + " lng: " + markerNuevo.getLng() + " starRating= "
		+ markerNuevo.getStarRating());
		return new ResponseEntity<Marker>(markerNuevo, HttpStatus.CREATED);
	}


}
