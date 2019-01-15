package com.java.ms.bililiu.son.sqs.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.ms.bililiu.son.sqs.http.BililiuEndpointClient;
import com.java.ms.bililiu.son.sqs.message.exception.BililiuEndpointSendEventException;
import com.java.ms.bililiu.son.sqs.message.exception.SendSQSException;
import com.java.ms.bililiu.son.sqs.metrics.BililiuSonMetrics;
import com.java.ms.bililiu.son.sqs.model.SqsPayload;
import com.java.ms.bililiu.son.sqs.util.GeneratorUUID;
import com.java.ms.bililiu.son.sqs.util.SendMessageSQS;
import com.java.ms.bililiu.commons.enums.Status;
import com.java.ms.bililiu.commons.http.DeleteRequestBody;
import com.java.ms.bililiu.commons.http.PatchRequestStatus;
import com.java.ms.bililiu.commons.http.PatchRequestStatusBody;
import com.java.ms.bililiu.commons.http.PostRequestOrder;
import com.java.ms.bililiu.commons.http.PostRequestOrderBody;
import com.java.ms.bililiu.commons.enums.SaleType;

public class BusinessRequestTest {

	private BusinessRequest businessRequest;

	private String message;

	private String messageException;

	private String messageOrderEventApplication;

	@Before
	public void setup() throws Exception {

		businessRequest = new BusinessRequest();
		businessRequest.apoloGatewayClient = mock(BililiuEndpointClient.class);
		businessRequest.defaultJmsTemplate = mock(JmsTemplate.class);
		businessRequest.objectMapper = mock(ObjectMapper.class);
		businessRequest.aresMetrics = mock(BililiuSonMetrics.class);
		businessRequest.generatorUUID = mock(GeneratorUUID.class);
		businessRequest.sendMessageSQS = mock(SendMessageSQS.class);

		message = "{\n" + "  \"Type\" : \"Notification\",\n"
				+ "  \"MessageId\" : \"b50676cc-3c36-5ab5-b30f-25b0c5a5427f\",\n"
				+ "  \"TopicArn\" : \"arn:aws:sns:sa-east-1:075096048015:maestro-notifications-topic\",\n"
				+ "  \"Message\" : \"{\\\"order\\\":{\\\"id\\\":\\\"6296500367382881\\\",\\\"subOrder\\\":{\\\"id\\\":423733523},\\\"salesChannel\\\":{\\\"id\\\":1}},\\\"event\\\":{\\\"status\\\":{\\\"id\\\":%eventStatusId%,\\\"origin\\\":1},\\\"date\\\":\\\"2018-05-19T13:00:00.000Z\\\",\\\"createdAt\\\":\\\"2018-05-21T16:44:03.684Z\\\"},\\\"application\\\":{\\\"id\\\":%applicationId%,\\\"name\\\":\\\"P52AppStaging\\\"}}\",\n"
				+ "  \"Timestamp\" : \"2018-05-21T16:44:03.839Z\",\n" + "  \"SignatureVersion\" : \"1\",\n"
				+ "  \"Signature\" : \"IQNTRBTVOWefcVOa8ydeHE9r5m5lvQT37OpFmn+PJDIlOPW2fXGl1r+a+FQsiwM74PJmjWqJzLrNMZx3BCh24RmNIQchVYPuGgKQrM9PS4vyXijjH9R5tcH13dRqjl+6nNhdUx+vVZEjM8EN9olQtINIsn5zn0e0x4xU5JZXg6UWsyEGXoujx6X4AL8RFncvjEVuYxZYFr378yPjq/TFub/6bT8olHp1E8sikPYLAC7AKMW2CxRuyuGJkwzx6/UO0bfu5DsBIZOP3QQzZ/eAtg6Bj+BhRx7vvPrdVGtbEm6+o4NqHJn+W0RHnKdPVxX4EuaUQVy0RMRLDADpsbGYQQ==\",\n"
				+ "  \"SigningCertURL\" : \"https://sns.sa-east-1.amazonaws.com/SimpleNotificationService-ac565b8b1a6c5d002d285f9598aa1d9b.pem\",\n"
				+ "  \"UnsubscribeURL\" : \"https://sns.sa-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:sa-east-1:075096048015:maestro-notifications-topic:751a247f-bcf4-442c-ba5e-cebb20a5f758\",\n"
				+ "  \"MessageAttributes\" : {\n" + "    \"eventStatusId\" : {\"Type\":\"Number\",\"Value\":\"2\"},\n"
				+ "    \"eventStatusOrigin\" : {\"Type\":\"Number\",\"Value\":\"0\"},\n"
				+ "    \"applicationId\" : {\"Type\":\"Number\",\"Value\":\"15\"},\n"
				+ "    \"applicationName\" : {\"Type\":\"String\",\"Value\":\"buzzorder-workerAppStaging\"}\n" + "  }\n"
				+ "}";

		messageException = "{\n" + "  \"Type\" : \"Notification\",\n"
				+ "  \"Message\" : \"{\\\"order\\\":{\\\"id\\\":\\\"6296500367382881\\\",\\\"subOrder\\\":{\\\"id\\\":423733523},\\\"salesChannel\\\":{\\\"id\\\":1}},\\\"event\\\":{\\\"status\\\":{\\\"id\\\":%eventStatusId%,\\\"origin\\\":1},\\\"date\\\":\\\"2018-05-19T13:00:00.000Z\\\",\\\"createdAt\\\":\\\"2018-05-21T16:44:03.684Z\\\"},\\\"application\\\":{\\\"id\\\":%applicationId%,\\\"name\\\":\\\"P52AppStaging\\\"}}\",\n"
				+ "}";

		messageOrderEventApplication = "{\n" + "  \"Type\" : \"Notification\",\n"
				+ "  \"MessageId\" : \"b50676cc-3c36-5ab5-b30f-25b0c5a5427f\",\n"
				+ "  \"TopicArn\" : \"arn:aws:sns:sa-east-1:075096048015:maestro-notifications-topic\",\n"
				+ "  \"Message\" : \"{\\\"orderrr\\\":{\\\"id\\\":\\\"6296500367999999\\\",\\\"subOrderr\\\":{\\\"id\\\":423733523},\\\"salesChannel\\\":{\\\"id\\\":1}},\\\"event\\\":{\\\"status\\\":{\\\"id\\\":%eventStatusId%,\\\"origin\\\":1},\\\"date\\\":\\\"2018-05-19T13:00:00.000Z\\\",\\\"createdAt\\\":\\\"2018-05-21T16:44:03.684Z\\\"},\\\"application\\\":{\\\"id\\\":%applicationId%,\\\"name\\\":\\\"P52AppStaging\\\"}}\",\n"
				+ "  \"Timestamp\" : \"2018-05-21T16:44:03.839Z\",\n" + "  \"SignatureVersion\" : \"1\",\n"
				+ "  \"Signature\" : \"IQNTRBTVOWefcVOa8ydeHE9r5m5lvQT37OpFmn+PJDIlOPW2fXGl1r+a+FQsiwM74PJmjWqJzLrNMZx3BCh24RmNIQchVYPuGgKQrM9PS4vyXijjH9R5tcH13dRqjl+6nNhdUx+vVZEjM8EN9olQtINIsn5zn0e0x4xU5JZXg6UWsyEGXoujx6X4AL8RFncvjEVuYxZYFr378yPjq/TFub/6bT8olHp1E8sikPYLAC7AKMW2CxRuyuGJkwzx6/UO0bfu5DsBIZOP3QQzZ/eAtg6Bj+BhRx7vvPrdVGtbEm6+o4NqHJn+W0RHnKdPVxX4EuaUQVy0RMRLDADpsbGYQQ==\",\n"
				+ "  \"SigningCertURL\" : \"https://sns.sa-east-1.amazonaws.com/SimpleNotificationService-ac565b8b1a6c5d002d285f9598aa1d9b.pem\",\n"
				+ "  \"UnsubscribeURL\" : \"https://sns.sa-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:sa-east-1:075096048015:maestro-notifications-topic:751a247f-bcf4-442c-ba5e-cebb20a5f758\",\n"
				+ "  \"MessageAttributes\" : {\n" + "    \"eventStatusId\" : {\"Type\":\"Number\",\"Value\":\"2\"},\n"
				+ "    \"eventStatusOrigin\" : {\"Type\":\"Number\",\"Value\":\"0\"},\n"
				+ "    \"applicationId\" : {\"Type\":\"Number\",\"Value\":\"15\"},\n"
				+ "    \"applicationName\" : {\"Type\":\"String\",\"Value\":\"buzzorder-workerAppStaging\"}\n" + "  }\n"
				+ "}";

	}

	@Test
	public void validadePostProcessed() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "2");
		message = message.replace("%applicationId%", "15");

		// Action
		PostRequestOrder postRequestOrder = new PostRequestOrder("423733523");
		PostRequestOrderBody body = new PostRequestOrderBody(postRequestOrder, "2018-05-19T13:00:00.000Z");
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.createOrder(SaleType.PROCESSED, body.marshall(), "423733523",
				Status.ORDER_APROVED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}

	@Test(expected = BililiuEndpointSendEventException.class)
	public void validadePostProcessedException() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "2");
		message = message.replace("%applicationId%", "15");

		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		businessRequest.requestApi(message);

	}
	
	@Test(expected = BililiuEndpointSendEventException.class)
	public void validateDeleteProcessedException() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "15");

		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		businessRequest.requestApi(message);

	}

	@Test(expected = BililiuEndpointSendEventException.class)
	public void validateUpdateLegacyProcessedException() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "143");
		message = message.replace("%applicationId%", "28");

		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		businessRequest.requestApi(message);

	}
	
	@Test
	public void validadeDeleteProcessed() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "16");

		// Action
		DeleteRequestBody deleteRequestBody = new DeleteRequestBody("2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "423733523";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.deleteOrder(SaleType.PROCESSED, deleteRequestBody.marshall(),
				orderSubOrderId, Status.ORDER_CANCELLED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadeDeleteProcessedBuzzOrder() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "15");
		
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_CANCELLED.getId(),
                Status.ORDER_CANCELLED.getDescription());

		// Action
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "423733523";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.PROCESSED, body.marshall(),
				orderSubOrderId, Status.ORDER_CANCELLED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}

	@Test(expected = BililiuEndpointSendEventException.class)
	public void validadeDeleteProcessedException() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "16");

		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		businessRequest.requestApi(message);

	}


	@Test
	public void validadePatchProcessed() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "7");
		message = message.replace("%applicationId%", "0");

		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.INVOICE.getId(),
				Status.INVOICE.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "423733523";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.PROCESSED, body.marshall(), orderSubOrderId,
				Status.INVOICE.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}

	@Test
	public void validadePatchProcessedUpdateLegacy() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "143");
		message = message.replace("%applicationId%", "28");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.UPDATE_LEGACY.getId(),
				Status.UPDATE_LEGACY.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "423733523";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.PROCESSED, body.marshall(), orderSubOrderId,
				Status.UPDATE_LEGACY.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}

	@Test(expected = BililiuEndpointSendEventException.class)
	public void validadePatchProcessedException() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "7");
		message = message.replace("%applicationId%", "15");

		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		businessRequest.requestApi(message);

	}

	@Test
	public void ValidadeMapper() throws IOException {

		// Scenario
		SqsPayload sqsPayload = mock(SqsPayload.class);
		message = message.replace("%eventStatusId%", "0");
		message = message.replace("%applicationId%", "0");

		// Action
		when(sqsPayload.getMessage()).thenReturn(message);
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		boolean requestApi = businessRequest.requestApi(message);

		// Verification
		assertTrue(requestApi);
	}

	@Test
	public void validadeMapperIOException() throws IOException {

		// Scenario
		SqsPayload sqsPayload = mock(SqsPayload.class);

		// Action
		when(sqsPayload.getMessage()).thenReturn(messageException);
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		boolean requestApi = businessRequest.requestApi(messageException);

		// Verification
		assertTrue(requestApi);

	}

	@Test
	public void ValidateMapperOrderEventApplication() throws IOException {

		// Scenario
		messageOrderEventApplication = messageOrderEventApplication.replace("%eventStatusId%", "1");
		messageOrderEventApplication = messageOrderEventApplication.replace("%applicationId%", "15");
		SqsPayload sqsPayload = mock(SqsPayload.class);

		// Action
		when(sqsPayload.getMessage()).thenReturn(messageOrderEventApplication);
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		boolean requestApi = businessRequest.requestApi(messageOrderEventApplication);

		// Verification
		assertTrue(requestApi);
		
	}
	
	@Test
	public void ValidateMapperOrderEventApplicationTdc() throws IOException {

		// Scenario
		messageOrderEventApplication = messageOrderEventApplication.replace("%eventStatusId%", "1");
		messageOrderEventApplication = messageOrderEventApplication.replace("%applicationId%", "16");
		SqsPayload sqsPayload = mock(SqsPayload.class);

		// Action
		when(sqsPayload.getMessage()).thenReturn(messageOrderEventApplication);
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		boolean requestApi = businessRequest.requestApi(messageOrderEventApplication);

		// Verification
		assertTrue(requestApi);
		
	}
	
	@Test
	public void ValidateMapperOrderEventApplicationSlash() throws IOException {

		// Scenario
		messageOrderEventApplication = messageOrderEventApplication.replace("%eventStatusId%", "1");
		messageOrderEventApplication = messageOrderEventApplication.replace("%applicationId%", "28");
		SqsPayload sqsPayload = mock(SqsPayload.class);

		// Action
		when(sqsPayload.getMessage()).thenReturn(messageOrderEventApplication);
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		boolean requestApi = businessRequest.requestApi(messageOrderEventApplication);

		// Verification
		assertTrue(requestApi);
		
	}
	
	@Test
	public void validadePostCapturedP52Failure() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "1");
		message = message.replace("%applicationId%", "1");

		// Action
		PostRequestOrder postRequestOrder = new PostRequestOrder("423733523");
		PostRequestOrderBody body = new PostRequestOrderBody(postRequestOrder, "2018-05-19T13:00:00.000Z");
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.createOrder(SaleType.CAPTURED, body.marshall(), "423733523",
				Status.ORDER_RECEIVED.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn("OK");

		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
	@Test(expected = SendSQSException.class)
	public void validadePostCapturedP52FailureToSendSQS() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "1");
		message = message.replace("%applicationId%", "1");

		// Action
		PostRequestOrder postRequestOrder = new PostRequestOrder("423733523");
		PostRequestOrderBody body = new PostRequestOrderBody(postRequestOrder, "2018-05-19T13:00:00.000Z");
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.createOrder(SaleType.CAPTURED, body.marshall(), "423733523",
				Status.ORDER_RECEIVED.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
	@Test
	public void validadePostCapturedP52Sucess() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "1");
		message = message.replace("%applicationId%", "1");

		// Action
		PostRequestOrder postRequestOrder = new PostRequestOrder("6296500367382881");
		PostRequestOrderBody body = new PostRequestOrderBody(postRequestOrder, "2018-05-19T13:00:00.000Z");
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.createOrder(SaleType.CAPTURED, body.marshall(), "6296500367382881",
				Status.ORDER_RECEIVED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadePatchCapturedP52AprovedOrder() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "2");
		message = message.replace("%applicationId%", "1");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.ORDER_APROVED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadePatchCapturedP52QuickSale() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "118");
		message = message.replace("%applicationId%", "1");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.QUICK_SALE.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	
	@Test
	public void validadePatchCapturedP52AwatingInvoce() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "136");
		message = message.replace("%applicationId%", "1");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.AWAITING_INVOICE.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	
	@Test
	public void validadePatchCapturedNinoAprovedOrder() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "2");
		message = message.replace("%applicationId%", "8");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.ORDER_APROVED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadePatchCapturedNinoQuickSale() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "118");
		message = message.replace("%applicationId%", "8");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.QUICK_SALE.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	
	@Test
	public void validadePatchCapturedNinoAwatingInvoce() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "136");
		message = message.replace("%applicationId%", "8");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.AWAITING_INVOICE.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	
	
	@Test
	public void validadePatchCapturedNinoAwatingInvoceFailure() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "136");
		message = message.replace("%applicationId%", "8");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.PROCESSED, body.marshall(), orderSubOrderId,
				Status.AWAITING_INVOICE.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn("OK");

		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
	@Test(expected = SendSQSException.class)
	public void validadePatchCapturedNinoAwatingInvoceFailureToSendSQS() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "136");
		message = message.replace("%applicationId%", "8");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
				Status.ORDER_APROVED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.PROCESSED, body.marshall(), orderSubOrderId,
				Status.AWAITING_INVOICE.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn("");

		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
	@Test
	public void validadePostCapturedNinoSucess() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "1");
		message = message.replace("%applicationId%", "8");

		// Action
		PostRequestOrder postRequestOrder = new PostRequestOrder("6296500367382881");
		PostRequestOrderBody body = new PostRequestOrderBody(postRequestOrder, "2018-05-19T13:00:00.000Z");
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.createOrder(SaleType.CAPTURED, body.marshall(), "6296500367382881",
				Status.ORDER_RECEIVED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadeDeleteCapturedOrderP52Cancelled() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "1");

		// Action
		DeleteRequestBody deleteRequestBody = new DeleteRequestBody("2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.deleteOrder(SaleType.CAPTURED, deleteRequestBody.marshall(),
				orderSubOrderId, Status.ORDER_CANCELLED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadeDeleteCapturedOrderNinoCancelled() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "8");

		// Action
		DeleteRequestBody deleteRequestBody = new DeleteRequestBody("2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.deleteOrder(SaleType.CAPTURED, deleteRequestBody.marshall(),
				orderSubOrderId, Status.ORDER_CANCELLED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadeDeleteCapturedOrderCancelledFailure() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "8");

		// Action
		DeleteRequestBody deleteRequestBody = new DeleteRequestBody("2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.deleteOrder(SaleType.PROCESSED, deleteRequestBody.marshall(),
				orderSubOrderId, Status.ORDER_CANCELLED.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn("OK");
		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
	@Test(expected = SendSQSException.class)
	public void validadeDeleteCapturedOrderCancelledFailureToSendSQS() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "3");
		message = message.replace("%applicationId%", "8");

		// Action
		DeleteRequestBody deleteRequestBody = new DeleteRequestBody("2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.deleteOrder(SaleType.PROCESSED, deleteRequestBody.marshall(),
				orderSubOrderId, Status.ORDER_CANCELLED.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
	@Test
	public void validadePatchCapturedSlash() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "8");
		message = message.replace("%applicationId%", "28");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.INTEGRATED.getId(),
				Status.INTEGRATED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.INTEGRATED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadePatchCapturedSullivan() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "8");
		message = message.replace("%applicationId%", "9");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.INTEGRATED.getId(),
				Status.INTEGRATED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.CAPTURED, body.marshall(), orderSubOrderId,
				Status.INTEGRATED.getId(), "TESTE")).thenReturn(true);

		// Verification
		assertThat(businessRequest.requestApi(message), is(true));

	}
	
	@Test
	public void validadePatchCapturedIntegredFailure() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "8");
		message = message.replace("%applicationId%", "9");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.INTEGRATED.getId(),
				Status.INTEGRATED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.PROCESSED, body.marshall(), orderSubOrderId,
				Status.INTEGRATED.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn("OK");

		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
	@Test(expected = SendSQSException.class)
	public void validadePatchCapturedIntegredFailureToSendSQS() throws Exception {

		// Scenario
		message = message.replace("%eventStatusId%", "8");
		message = message.replace("%applicationId%", "9");
		
		// Action
		PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.INTEGRATED.getId(),
				Status.INTEGRATED.getDescription());
		PatchRequestStatusBody body = new PatchRequestStatusBody(patchRequestStatus, "2018-05-19T13:00:00.000Z");
		String orderSubOrderId = "6296500367382881";
		when(businessRequest.generatorUUID.generator()).thenReturn("TESTE");
		when(businessRequest.apoloGatewayClient.updateOrder(SaleType.PROCESSED, body.marshall(), orderSubOrderId,
				Status.INTEGRATED.getId(), "TESTE")).thenReturn(true);
		when(businessRequest.sendMessageSQS.sendMessage(Mockito.anyString(), Mockito.anyString())).thenReturn("");

		// Verification
		assertThat(businessRequest.requestApi(message), is(false));

	}
	
}