package com.ecom.mail.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.ecom.mail.dto.external.OrderItemResponse;
import com.ecom.mail.dto.external.OrderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMailHelper {

	private final BrevoEmailService emailService;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	public static final String ORDER_CONFIRMATION_SUBJECT = "Order Confirmation";
	public static final String ORDER_CONFIRMATION_TEMPLATE = "order-confirmation";

	public void sendOrderConfirmationMail(OrderResponse response, String to) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("bookingId", response.bookingId());
		variables.put("status", response.status());
		variables.put("totalAmount", response.totalAmount());
		variables.put("orderDate", getOrderDate(response.createdAt()));

		var items = response.items().stream()
				.map(i -> Map.of("productName", i.productName(), 
						"variant", getVariantName(i), 
						"quantity", i.quantity(),
						"priceAtBooking", i.priceAtBooking(),
						"lineTotal", String.valueOf(Double.valueOf(i.priceAtBooking()) * Double.valueOf(i.quantity()))))
				.toList();
		variables.put("items", items);
		emailService.sendMail(to, ORDER_CONFIRMATION_SUBJECT, ORDER_CONFIRMATION_TEMPLATE, variables);
	}

	private String getVariantName(OrderItemResponse item) {
		List<String> varList = Stream.of(item.color(), item.size())
						.filter(Objects::nonNull)
						.map(String::trim)
						.filter(s -> !s.isBlank())
						.toList();
		
		return varList.isEmpty() ? "Standard" : String.join("-", varList);
	}
	
	private String getOrderDate(LocalDateTime localDateTime) {
		return FORMATTER.format(localDateTime != null ? localDateTime : LocalDateTime.now());
	}
}
