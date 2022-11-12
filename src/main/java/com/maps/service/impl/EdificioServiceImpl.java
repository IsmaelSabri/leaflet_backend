package com.maps.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.maps.dao.IEdificioRepository;
import com.maps.entities.Edificio;
import com.maps.exception.NotAnImageFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static com.maps.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
public class EdificioServiceImpl {
    @Autowired
    private IEdificioRepository iEdificioRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    Cloudinary cloudinary;
    private Map<String, String> values = new HashMap<>();

    public EdificioServiceImpl() {
        values.put("cloud_name", "dpjb5jfh0");
        values.put("api_key", "769819413945758");
        values.put("api_secret", "CqZ2HVv8vBp0MJaaf2toINO6Qow");
        cloudinary = new Cloudinary(values);
    }

    public void deleteBuilding(Long id) throws IOException {
        this.iEdificioRepository.deleteById(id);
    }

    public Map deleteImg(String id) throws IOException {
        Map result = cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        return result;
    }

    public File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }

    public List<Edificio> getEdificios() {
        List<Edificio> buildings = iEdificioRepository.findAll();
        return buildings;
    }

    public Edificio addNewBuilding(String lat, String lng, String starRating, MultipartFile foto, String descripcion, String puertas, String calle, String numero, String cp) throws NotAnImageFileException, IOException {
        if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(foto.getContentType())) {
            throw new NotAnImageFileException(foto.getOriginalFilename() + NOT_AN_IMAGE_FILE);
        }
        Edificio edificio = new Edificio();
        edificio.setLat(Double.parseDouble(lat));
        edificio.setLng(Double.parseDouble(lng));
        // tratamiento de la imagen
        File file = convert(foto);
        String[] uuid = UUID.randomUUID().toString().split("-");
        Map result = cloudinary.uploader().upload(file, ObjectUtils.asMap(new StringBuilder().append(uuid[0]).append(uuid[4]).toString(), "multihouse/construcciones/edificios"));
        file.delete();
        edificio.setEdificioId(new StringBuilder().append(uuid[4]).reverse().toString());
        edificio.setImageName(result.get("original_filename").toString());
        edificio.setImageUrl( result.get("url").toString());
        edificio.setImageId(result.get("public_id").toString());
        System.out.println(puertas);
        edificio.setPuertas(Integer.parseInt(puertas));
        System.out.println(numero);

        edificio.setDescripcion(descripcion);
        edificio.setCalle(calle);
        edificio.setNumero(Integer.parseInt(numero));
        System.out.println("dfg");

        edificio.setCp(Integer.parseInt(cp));
        edificio.setFechaCreacion(new Date());
        edificio.setValoracion(Integer.parseInt(starRating));
        LOGGER.info(edificio.toString());
        return iEdificioRepository.save(edificio);
    }

    public void onImageChange(String id, MultipartFile foto) throws NotAnImageFileException, IOException {
        if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(foto.getContentType())) {
            throw new NotAnImageFileException(foto.getOriginalFilename() + NOT_AN_IMAGE_FILE);
        }
        List<Edificio> edificios =getEdificios();
        for (Edificio e : edificios) {
            if (e.getImageId().equalsIgnoreCase(id)) {
                this.deleteImg(id);
                File file = convert(foto);
                String[] uuid = UUID.randomUUID().toString().split("-");
                Map result = cloudinary.uploader().upload(file, ObjectUtils.asMap(new StringBuilder().append(uuid[0]).append(uuid[4]).toString(), "multihouse/construcciones/edificios"));
                file.delete();
                e.setImageName((String) result.get("original_filename"));
                e.setImageUrl((String) result.get("url"));
                e.setImageId((String) result.get("public_id"));
                break;
            }
        }
    }


}
