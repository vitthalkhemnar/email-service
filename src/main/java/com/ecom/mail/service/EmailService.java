package com.ecom.mail.service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private static final long OTP_EXPIRY_MINUTES = 5;
	private final SecureRandom secureRandom = new SecureRandom();

	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
	private final StringRedisTemplate redisTemplate;

	public void sendMail(String to, String subject, String templateName, Map<String, Object> variables) {
		try {
			Context context = new Context();
			context.setVariables(variables);
			String htmlBody = templateEngine.process(templateName, context);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlBody, true);
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			log.error("Error while sending mail.", e);
		}
	}

	public String sendAndStoreOtp(String to, String customOtp) {
		String key = to.trim().toLowerCase();
		String otp = (customOtp != null && !customOtp.isBlank())
				? customOtp.trim()
				: String.format("%06d", secureRandom.nextInt(1_000_000));

		// Store in Redis: Key is email, TTL is 5 minutes
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
