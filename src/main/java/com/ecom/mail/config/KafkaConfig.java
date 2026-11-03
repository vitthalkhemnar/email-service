package com.ecom.mail.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.kafka.common.config.SslConfigs;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJacksonJavaTypeMapper;
import org.springframework.kafka.support.mapping.JacksonJavaTypeMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	@Bean
    DefaultKafkaConsumerFactoryCustomizer kafkaConsumerCustomizer() {
        return factory -> {
            try {
                ClassPathResource resource = new ClassPathResource("ca.pem");
                if (resource.exists()) {
                    File tempCertFile = File.createTempFile("kafka-ca-", ".pem");
                    tempCertFile.deleteOnExit();
                    try (InputStream in = resource.getInputStream();
                         OutputStream out = new FileOutputStream(tempCertFile)) {
                        in.transferTo(out);
                    }
                    log.info("Kafka CA cert extracted from classpath to: {}", tempCertFile.getAbsolutePath());
                    factory.updateConfigs(Map.of(
                        SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, tempCertFile.getAbsolutePath(),
                        SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM"
                    ));
                }
            } catch (Exception e) {
                log.error("Failed to extract Kafka CA certificate from classpath", e);
            }
        };
    }
}
