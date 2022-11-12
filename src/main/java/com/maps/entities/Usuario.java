package com.maps.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "usuario")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Usuario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8822528375787916969L;
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Long id;
	private String usuarioId;
	private String nombre;
	private String primerApellido;
	private String username;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	private String email;
	private String fotoPerfilUrl;
	private Date fechaDeUltimoAcceso;
	private Date mostrarFechaDeUltimoAcceso;
	private Date fechaRegistro;
	private String rol;
	private String[] authorities;
	private boolean isActive;
	private boolean isNotLocked;

}
