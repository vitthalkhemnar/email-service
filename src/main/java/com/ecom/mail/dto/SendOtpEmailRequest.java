package com.ecom.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for sending and caching an OTP email")
public record SendOtpEmailRequest(
		@Schema(description = "Recipient email address", example = "customer@example.com")
		String to,

		@Schema(description = "6-digit OTP code to send", example = "123456")
		String otp
) {
}
