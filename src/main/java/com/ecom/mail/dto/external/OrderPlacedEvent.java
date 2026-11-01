package com.ecom.mail.dto.external;

public record OrderPlacedEvent(
	    OrderResponse order,
	    String email
) {
}
