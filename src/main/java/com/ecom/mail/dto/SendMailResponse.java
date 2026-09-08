package com.ecom.mail.dto;

public record SendMailResponse(
		String to,
		String message
	) {
}
