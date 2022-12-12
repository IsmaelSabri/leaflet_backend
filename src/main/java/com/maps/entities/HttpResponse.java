package com.maps.entities;

import com.fasterxml.jackson.annotation.JsonFormat;


import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;
@Data
public class HttpResponse {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Spain/Madrid")
	private Date fecha;
	private int httpStatusCode;
	private HttpStatus httpStatus;
	private String reason;
	private String message;

	public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
		this.fecha = new Date();
		this.httpStatusCode = httpStatusCode;
		this.httpStatus = httpStatus;
		this.reason = reason;
		this.message = message;
	}

}
