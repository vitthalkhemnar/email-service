package com.ecom.mail.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ecom.mail.dto.external.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {
	
	private final OrderMailHelper orderMail;

	@KafkaListener(topics = "order-placed-topic", groupId = "email-service-group")
	public void handleOrderPlacedEvent(OrderPlacedEvent event) {
		log.info("Received OrderPlacedEvent for order: {}", event.order().bookingId());
		orderMail.sendOrderConfirmationMail(event.order(), event.email());
	}
}
