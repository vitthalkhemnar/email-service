package com.ecom.mail.service;

import java.util.Map;

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

	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

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

}
