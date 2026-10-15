package com.ecom.mail.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BrevoEmailService {

	private static final long OTP_EXPIRY_MINUTES = 5;
	private final SecureRandom secureRandom = new SecureRandom();

	private final SpringTemplateEngine templateEngine;
	private final StringRedisTemplate redisTemplate;
	private final RestTemplate restTemplate;

	@Value("${brevo.api.key}")
	private String apiKey;

	@Value("${brevo.sender.email}")
	private String senderEmail;

	@Value("${brevo.sender.name}")
	private String senderName;

	public BrevoEmailService(
			SpringTemplateEngine templateEngine,
			StringRedisTemplate redisTemplate) {
		this.templateEngine = templateEngine;
		this.redisTemplate = redisTemplate;
		this.restTemplate = new RestTemplate();
	}

	public void sendMail(String to, String subject, String templateName, Map<String, Object> variables) {
		try {
			// 1. Process your Thymeleaf template into an HTML string
			Context context = new Context();
			context.setVariables(variables);
			String htmlBody = templateEngine.process(templateName, context);

			// 2. Setup Brevo HTTP API Endpoint
			String url = "https://api.brevo.com/v3/smtp/email";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("api-key", apiKey);

			// 3. Build the request body structure matching Brevo's API spec
			Map<String, Object> body = new HashMap<>();
			body.put("sender", Map.of("name", senderName, "email", senderEmail));
			body.put("to", List.of(Map.of("email", to)));
			body.put("subject", subject);
			body.put("htmlContent", htmlBody);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

			// 4. Send the POST request over HTTPS (Port 443 - never blocked by Render)
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			log.info("Email sent successfully via Brevo to [{}]. Status: {}", to, response.getStatusCode());

		} catch (Exception e) {
			log.error("Error while sending mail via Brevo to [{}]: {}", to, e.getMessage(), e);
		}
	}

	public String sendAndStoreOtp(String to, String customOtp) {
		String key = to.trim().toLowerCase();
		String otp = (customOtp != null && !customOtp.isBlank())
				? customOtp.trim()
				: String.format("%06d", secureRandom.nextInt(1_000_000));

		// Store in Redis with a 5-minute TTL
		redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
		log.info("Stored OTP in Redis with key [{}] and TTL {} minutes", key, OTP_EXPIRY_MINUTES);

		sendMail(to, "Your Login OTP", "otp-confirmation", Map.of("otp", otp));
		return otp;
	}

	public String getStoredOtp(String email) {
		if (email == null) return null;
		return redisTemplate.opsForValue().get(email.trim().toLowerCase());
	}
}