package com.ecom.mail.dto;

import java.util.Map;

public record SendMailRequest (
		String to,
		Map<String, Object> data
	) {
}
