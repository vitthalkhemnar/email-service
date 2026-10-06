package com.ecom.mail.dto;

public record SendOtpEmailRequest(
		String to,
		String otp
) {
}
