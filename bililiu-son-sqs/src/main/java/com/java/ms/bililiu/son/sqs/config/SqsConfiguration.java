package com.java.ms.bililiu.son.sqs.config;

import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.java.ms.bililiu.son.sqs.message.error.MessageErrorHandler;

/**
 * @author Murillo
 * Note: Sqs and Parallel configuration.
 */
@Configuration
public class SqsConfiguration {
	
	@Autowired
	private MessageErrorHandler messageErrorHandler;

	@Value("${app.thread.coreSize}")
	private Integer coreSize;

	@Value("${app.thread.maxSize}")
	private Integer maxSize;

	@Value("${app.thread.capacity}")
	private Integer capacity;

	@Value("${app.thread.concurrency}")
	private String concurrency;
	
	@Value("${sqs.queue.region}")
	private String region;
	
	@Value("${credentials.acesskey}")
	private String credentialsAcessKey;
	
	@Value("${credentials.secretkey}")
	private String credentialsSecretKey;
	
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(this.coreSize);
		taskExecutor.setMaxPoolSize(this.maxSize);
		taskExecutor.setQueueCapacity(this.capacity);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}
	
	public AWSCredentialsProvider credentials() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.credentialsAcessKey, this.credentialsSecretKey));
	}  
	
	@Bean
	public AmazonSQS amazonSqs() {
		return AmazonSQSClient.builder()
							  .withRegion(this.region)
						      .withCredentials(this.credentials())
							  .build();
	}
    
	public SQSConnectionFactory sqsConnectionFactory() {		
		return new SQSConnectionFactory(new ProviderConfiguration(), this.amazonSqs());
	}
    
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(this.sqsConnectionFactory());
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		factory.setErrorHandler(this.messageErrorHandler);
		factory.setConcurrency(this.concurrency);
		factory.setTaskExecutor(this.taskExecutor());
	
		return factory;
	}

	@Bean
	public JmsTemplate defaultJmsTemplate() {
		return new JmsTemplate(this.sqsConnectionFactory());
	}
	
}