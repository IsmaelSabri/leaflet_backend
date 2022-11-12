package com.maps.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
//@Table(name = "edificio")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Edificio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String edificioId;
	private double lat;
	private double lng;
	private String descripcion;

	private String imageName;
	private String imageUrl;
	private String imageId;

	//private int pisos;
	private int puertas;
	private String calle;
	private int numero;
	private int cp;
	private int valoracion;
	private Date fechaCreacion;



	
}
