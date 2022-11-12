package com.maps.entities;

import java.io.Serializable;
import java.util.*;

//@Entity
//@Table(name = "vivienda")
public class Vivienda implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4792606301802691579L;
	private Long id;
	private int piso;
	private int puerta;
	private List<String> fotos=new ArrayList<>();
	private List<String> comentarios=new ArrayList<>();

}
