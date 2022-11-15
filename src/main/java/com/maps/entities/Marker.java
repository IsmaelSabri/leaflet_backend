package com.maps.entities;

import java.io.Serializable;
import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "marker")
@NoArgsConstructor
@ToString
@Data
public class Marker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "lat")
	private String lat;
	@Column(name = "lng")
	private String lng;

	@Column(name = "starRating")
	private String starRating;

	public Marker(Long id, String lat, String lng, String starRating) {
		super();
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.starRating=starRating;
	}

}
