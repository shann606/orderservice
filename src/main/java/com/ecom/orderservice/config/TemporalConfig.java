package com.ecom.orderservice.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

@Configuration
public class TemporalConfig {

	@Bean
	WorkflowClient getWorkFlowClient() {
		WorkflowServiceStubs workFlowClientStubs = WorkflowServiceStubs.newLocalServiceStubs();
		return WorkflowClient.newInstance(workFlowClientStubs);

	}

	@Bean
	RestTemplate getRestTemplate() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
	}

}
