package com.java.ms.bililiu.son.sqs.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Component
public class SendMessageSQS {

	@Autowired
	private AmazonSQS amazonSQS;

	public String sendMessage(String message, String hash) {

		final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
		messageAttributes.put("Hash", new MessageAttributeValue().withDataType("String").withStringValue(hash));

		final SendMessageRequest sendMessageRequest = new SendMessageRequest()
				.withQueueUrl("apolo-captured-orders-fallback").withMessageBody(message).withMessageAttributes(messageAttributes);

		return amazonSQS.sendMessage(sendMessageRequest).getMessageId();
	}
}
