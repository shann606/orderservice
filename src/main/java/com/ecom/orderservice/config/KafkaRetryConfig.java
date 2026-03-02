package com.ecom.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;

@EnableKafka
@Configuration
public class KafkaRetryConfig {

	@Bean
	RetryTopicConfiguration retryMessages(KafkaTemplate<String, Object> template) {

		return RetryTopicConfigurationBuilder.newInstance().maxAttempts(4).fixedBackOff(1000)
				.autoCreateTopics(true, 1, (short) 1).dltSuffix("-dlt").create(template);

	}

}
