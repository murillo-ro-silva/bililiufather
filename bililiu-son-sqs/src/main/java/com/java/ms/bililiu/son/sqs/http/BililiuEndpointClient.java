package com.java.ms.bililiu.son.sqs.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.java.ms.bililiu.commons.enums.SaleType;
import com.java.ms.bililiu.commons.http.bililiu.endpoint.FallbackMessage;

/**
 * @author Murillo
 * Note: Client with methods rest api (POST,PATCH and Delete) which considers the request and response parameters specific to the Apollo-Gateway invoke rules.
 */
@Component
public class BililiuEndpointClient {

	private static final Logger log = LoggerFactory.getLogger(BililiuEndpointClient.class);
	
	@Value("${bililiu-endpoint.client.endpoint}")
	private String  baseUrl;
	
	@Value("${bililiu-endpoint.client.token}")
	private String  token;
	
	@Value("${bililiu-endpoint.client.retries}")
	private Integer retries;
	
	@Value("${bililiu-endpoint.client.timeout}")
	private Integer timeout;
	
	@Value("${bililiu-endpoint.client.connections}")
	private Integer connections;
	
	@Value("${sqs.queue.name-fallback}")
	private String sqsQueueFallbackName;
	
	@Value("${bililiu-endpoint.captured.delay}")
	private Integer delay;
	
	@Value("${bililiu-endpoint.captured.attempts}")
	private Integer attempts;

	@Autowired
	public JmsTemplate defaultJmsTemplate;
	
	private RestTemplate rest;
	
	private HttpHeaders headers;

	@PostConstruct
	public void init () {
		
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		
		if (baseUrl.contains("https")) {			
			SSLConnectionSocketFactory socketFactory = null; 
			
			try {
				socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
			} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
				log.error(e.getMessage());
			}
			
			httpClientBuilder.setSSLSocketFactory(socketFactory);		
		}
		
		
		CloseableHttpClient client = httpClientBuilder.setMaxConnTotal(this.connections)
						 							  .setMaxConnPerRoute(this.connections)
						 							  .build();
		
		HttpComponentsClientHttpRequestFactory configurations = new HttpComponentsClientHttpRequestFactory(client);
		
		configurations.setConnectionRequestTimeout(this.timeout);
		configurations.setConnectTimeout(this.timeout);
		configurations.setReadTimeout(this.timeout);	
		
		this.rest = new RestTemplate(configurations);
		
		this.headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", this.token);
	}

	public boolean createOrder(SaleType salesType, String body, String orderId, Integer eventId, String hash) {		
		String endpoint = String.format("%s/orders/%s", this.baseUrl, salesType.getType());
		
		HttpHeaders httpHeaders = new HttpHeaders();	
		httpHeaders.add("Hash",hash);
		httpHeaders.add("Content-Type", "application/json");
		
		HttpEntity<String> request = new HttpEntity<>(body, httpHeaders);
		return requestWithRetries(endpoint, HttpMethod.POST, request, orderId, eventId, salesType);
	}

	public boolean updateOrder(SaleType salesType, String body, String orderId, Integer eventId, String hash) {
		String endpoint = String.format("%s/orders/%s/%s", this.baseUrl, orderId, salesType.getType());
		
		HttpHeaders httpHeaders = new HttpHeaders();	
		httpHeaders.add("Hash",hash);
		httpHeaders.add("Content-Type", "application/json");
		
		HttpEntity<String> request = new HttpEntity<>(body, httpHeaders);
		return requestWithRetries(endpoint, HttpMethod.PATCH, request, orderId, eventId, salesType);
	}

	public boolean deleteOrder(SaleType salesType, String body, String orderId, Integer eventId, String hash) {		
		String endpoint = String.format("%s/orders/%s/%s", this.baseUrl, orderId, salesType.getType());
		
		HttpHeaders httpHeaders = new HttpHeaders();	
		httpHeaders.add("Hash",hash);
		httpHeaders.add("Content-Type", "application/json");
		
		HttpEntity<String> request = new HttpEntity<>(body, httpHeaders);
		return requestWithRetries(endpoint, HttpMethod.DELETE, request, orderId, eventId, salesType);
	}

	private boolean requestWithRetries(String endpoint, HttpMethod method, HttpEntity<String> request, String orderId, Integer eventId, SaleType salesType) {
		int counter = this.retries;

		ResponseEntity<String> response = null;			

		do {
			counter--;

			try {
				response = rest.exchange(endpoint, method, request, String.class);
			} catch (HttpClientErrorException | HttpServerErrorException e1) {
				String errorMessage = String.format("[%s] Could not send event with order id %s to gateway (%s)", method, orderId, e1.getMessage());
				String retryMessage = String.format("[%s] Trying to send event with order id %s to gateway again (%s)", method, orderId, e1.getMessage());
				
				HttpStatus statusCode = e1.getStatusCode();
				
				if (statusCode.is4xxClientError()) {
					if (statusCode == HttpStatus.PRECONDITION_FAILED || statusCode == HttpStatus.BAD_REQUEST) {
						log.error(String.format("[%s] Got client error with status %s from order %s. Message sent to fallback queue", method, statusCode, orderId));
						FallbackMessage fallbackMessage = new FallbackMessage(orderId, salesType, method, eventId);
						this.defaultJmsTemplate.convertAndSend(this.sqsQueueFallbackName, fallbackMessage.toJson());
						return true;	
					} else if (statusCode == HttpStatus.NOT_FOUND && salesType == SaleType.CAPTURED) {
						return false;
					} else {
						if (counter == 0) {
							log.error(errorMessage);
							return false;
						} else {
							log.warn(retryMessage);
						}
					}
				} else if (statusCode.is5xxServerError()) {
					if (counter == 0) {
						log.error(errorMessage);
						return false;
					} else {
						log.warn(retryMessage);
					}
				} else {
					log.error(errorMessage);
					FallbackMessage fallbackMessage = new FallbackMessage(orderId, salesType, method, eventId);
					this.defaultJmsTemplate.convertAndSend(this.sqsQueueFallbackName, fallbackMessage.toJson());
					return true;
				}												
			} catch (Exception e2) {
				if (counter == 0) {
					log.error(String.format("[%s] Got unknow error from order %s. Message sent to fallback queue", method, orderId));
					FallbackMessage fallbackMessage = new FallbackMessage(orderId, salesType, method, eventId);
					this.defaultJmsTemplate.convertAndSend(this.sqsQueueFallbackName, fallbackMessage.toJson());
					return true;
				} else {
					log.warn(String.format("[%s] Trying to send event with order id %s to gateway again (%s)", method, orderId, e2.getMessage()));
				}
			}
		} while (response == null && counter > 0);

		return true;
	}

}