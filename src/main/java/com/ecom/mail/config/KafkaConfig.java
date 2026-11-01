package com.ecom.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJacksonJavaTypeMapper;
import org.springframework.kafka.support.mapping.JacksonJavaTypeMapper;

@Configuration
public class KafkaConfig {

	@Bean
	RecordMessageConverter converter() {
		JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
		DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
		typeMapper.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
		typeMapper.addTrustedPackages("*");
		converter.setTypeMapper(typeMapper);
		return converter;
	}

}
