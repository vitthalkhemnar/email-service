package com.ecom.mail.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.mail.dto.SendMailRequest;
import com.ecom.mail.dto.SendOtpEmailRequest;
import com.ecom.mail.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@SecurityRequirement(name = "bearerAuth")
public class EmailController {

	private final EmailService emailService;

	@Tag(name = "Email Notifications", description = "Endpoints for sending transactional and confirmation emails")
	@Operation(
		summary = "Send transactional email",
		description = "Sends an HTML transactional email (e.g. order confirmation) to the specified customer email."
	)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<Void> sendMail(@RequestBody SendMailRequest req) {
		String template = "order-confirmation";
		String subject = "Order Confirmation";
		emailService.sendMail(req.to(), subject, template, req.data());
		
		return ResponseEntity.ok().build();
	}

	@Tag(name = "OTP Service", description = "Endpoints for sending and storing authentication OTPs via email")
	@Operation(
		summary = "Send OTP email",
		description = "Dispatches the HTML OTP email template and stores the OTP in Redis under the recipient's email key (TTL: 5 minutes)."
	)
	@PostMapping("/otp")
	public ResponseEntity<Map<String, String>> sendOtpMail(@RequestBody SendOtpEmailRequest req) {
		String otp = emailService.sendAndStoreOtp(req.to(), req.otp());
		return ResponseEntity.ok(Map.of(
				"message", "OTP sent and stored in Redis successfully",
				"to", req.to()
		));
	}
}
