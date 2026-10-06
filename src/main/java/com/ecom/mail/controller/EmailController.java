package com.ecom.mail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.mail.dto.SendMailRequest;
import com.ecom.mail.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

	private final EmailService emailService;

	@PostMapping
	public ResponseEntity<?> sendMail(@RequestBody SendMailRequest req) {
		String template = "order-confirmation";
		String subject = "Order Confirmation";
		emailService.sendMail(req.to(), subject, template, req.data());
		
		return ResponseEntity.ok().build();
	}

	@PostMapping("/otp")
	public ResponseEntity<?> sendOtpMail(@RequestBody com.ecom.mail.dto.SendOtpEmailRequest req) {
		String otp = emailService.sendAndStoreOtp(req.to(), req.otp());
		return ResponseEntity.ok(java.util.Map.of(
				"message", "OTP sent and stored in Redis successfully",
				"to", req.to()
		));
	}
}
