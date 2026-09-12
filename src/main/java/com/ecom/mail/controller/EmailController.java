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

}
