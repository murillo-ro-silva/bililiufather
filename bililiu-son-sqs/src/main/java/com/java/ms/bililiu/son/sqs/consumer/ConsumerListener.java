package com.java.ms.bililiu.son.sqs.consumer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.java.ms.bililiu.son.sqs.business.BusinessRequest;
//import com.java.ms.bililiu.son.sqs.notification.NotificationMessage;

/**
 * @author Murillo
 * Note: Consumer main queue(who comes anything Application) and request main class business.
 */
@Component
public class ConsumerListener {

	@Autowired
	BusinessRequest businessRequest;

	@JmsListener(destination = "${sqs.queue.name}")
//	@SlackNotify(prefix = "slack", message = NotificationMessage.class)
	public void messageConsumer(@Headers Map<String, Object> messageAttributes, @Payload String message){
		businessRequest.requestApi(message);
	}
}