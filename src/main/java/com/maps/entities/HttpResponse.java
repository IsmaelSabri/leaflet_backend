package com.maps.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class HttpResponse {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "America/New_York")
	@Getter
	@Setter
	private Date fecha;
	@Getter
	@Setter
	private int httpStatusCode;
	@Getter
	@Setter
	private HttpStatus httpStatus;
	@Getter
	@Setter
	private String reason;
	@Getter
	@Setter
	private String message;

	public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
		this.fecha = new Date();
		this.httpStatusCode = httpStatusCode;
		this.httpStatus = httpStatus;
		this.reason = reason;
		this.message = message;
	}

}
