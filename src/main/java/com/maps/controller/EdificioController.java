package com.maps.controller;
import com.cloudinary.utils.ObjectUtils;
import com.maps.entities.Edificio;
import com.maps.exception.NotAnImageFileException;
import com.maps.service.impl.EdificioServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.maps.constant.FileConstant.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping(path = { "/buildings"})
@CrossOrigin(origins = {"*"})
public class EdificioController {
    @Autowired
    private EdificioServiceImpl edificioService;
    public static final String PROPERTY_DELETED_SUCCESSFULLY = "User deleted successfully";
    public static final String IMAGE_UPDATED_SUCCESSSFULLY = "Image has been updated succesfully!!";


    @PostMapping("/new")
    public ResponseEntity<?> addNewBuilding(@RequestParam("lat") String lat,
                                                   @RequestParam("lng") String lng,
                                                   @RequestParam("starRating") String starRating,
                                                   @RequestParam("foto") MultipartFile foto,
                                                   @RequestParam("descripcion") String descripcion,
                                                   @RequestParam("puertas") String puertas,
                                                   @RequestParam("calle") String calle,
                                                   @RequestParam("numero") String numero,
                                                   @RequestParam("cp") String cp
    ) throws IOException, NotAnImageFileException {
        edificioService.addNewBuilding(lat, lng, starRating, foto, descripcion, puertas, calle, numero, cp);
        return new ResponseEntity<Edificio>(HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Edificio>> getAllBuildings() {
        List<Edificio> edificios = edificioService.getEdificios();
        return new ResponseEntity<>(edificios, OK);
    }

    // Cambiar foto -> no se puede borrar
    @PutMapping("/put/{id}") // public_id
    public ResponseEntity<?> onImageChange(@PathVariable("id") String id, @RequestParam("foto") MultipartFile foto) throws IOException, NotAnImageFileException {
        edificioService.onImageChange(id,foto);
        return new ResponseEntity(IMAGE_UPDATED_SUCCESSSFULLY, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) throws IOException {
        Long newId = Long.parseLong(id);
        this.edificioService.deleteBuilding(newId);
        return new ResponseEntity(PROPERTY_DELETED_SUCCESSFULLY, HttpStatus.OK);
    }

}
