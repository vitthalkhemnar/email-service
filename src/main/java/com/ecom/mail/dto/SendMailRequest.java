package com.ecom.mail.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for sending an email")
public record SendMailRequest (
		@Schema(description = "Recipient email address", example = "customer@example.com")
		String to,

		@Schema(description = "Template context variables")
		Map<String, Object> data
	) {
}
