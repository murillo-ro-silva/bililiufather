package com.java.ms.bililiu.son.sqs.business;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import com.java.ms.bililiu.son.sqs.http.BililiuEndpointClient;
import com.java.ms.bililiu.son.sqs.message.exception.BililiuEndpointSendEventException;
import com.java.ms.bililiu.son.sqs.message.exception.SendSQSException;
import com.java.ms.bililiu.son.sqs.model.Application;
import com.java.ms.bililiu.son.sqs.model.Event;
import com.java.ms.bililiu.son.sqs.model.MessageSqs;
import com.java.ms.bililiu.son.sqs.model.Order;
import com.java.ms.bililiu.son.sqs.model.OrderSqs;
import com.java.ms.bililiu.son.sqs.model.SqsPayload;
import com.java.ms.bililiu.son.sqs.util.GeneratorUUID;
//import com.java.ms.bililiu.son.sqs.util.Loggers;
import com.java.ms.bililiu.son.sqs.util.SendMessageSQS;
import com.java.ms.bililiu.son.sqs.metrics.BililiuSonMetrics;
import com.java.ms.bililiu.commons.enums.Applications;
import com.java.ms.bililiu.commons.enums.SaleType;
import com.java.ms.bililiu.commons.enums.Status;
import com.java.ms.bililiu.commons.http.DeleteRequestBody;
import com.java.ms.bililiu.commons.http.PatchRequestStatus;
import com.java.ms.bililiu.commons.http.PatchRequestStatusBody;
import com.java.ms.bililiu.commons.http.PostRequestOrder;
import com.java.ms.bililiu.commons.http.PostRequestOrderBody;


/**
 * @author Murillo Note: Class containing all worker rules that invoke
 *         Apollo-Gateway endpoints, handles unknown messages and out-of-rule
 *         messages.
 */
@Component
public class BusinessRequest {

	@Autowired
	public JmsTemplate defaultJmsTemplate;

	@Autowired
	public BililiuEndpointClient apoloGatewayClient;

	@Autowired
	public ObjectMapper objectMapper;

	@Autowired
	public BililiuSonMetrics aresMetrics;

	@Autowired
	public GeneratorUUID generatorUUID;

	@Autowired
	public SendMessageSQS sendMessageSQS;

	@Value("${sqs.queue.name-unknown}")
	private String sqsQueueUnknownName;

	/**
	 * @param requestApi:
	 *            responsible for receiving event from the maestro application queue
	 *            and directing to the Apollo-Gateway endpoints.
	 * @return Boolean return was used in the test class.
	 */
	public boolean requestApi(String message) {

		String uuid = generatorUUID.generator();

		this.aresMetrics.incrementMessageCapturedMaestro();

		objectMapper = new ObjectMapper();

		/*
		 * It interprets the "message" field of the queue and treats exception by
		 * inserting into another queue of unknown messages.
		 */
		SqsPayload sqsPayload = new SqsPayload();
		try {
			sqsPayload = objectMapper.readValue(message, SqsPayload.class);
		} catch (IOException e) {

			defaultJmsTemplate.convertAndSend(sqsQueueUnknownName, message);
			this.aresMetrics.incrementUnknownStatusCounter();
			return true;
		}

		/*
		 * Interprets the inside of the message from the queue and handles exceptions by
		 * inserting into another queue of unknown messages.
		 */
		Order order = new Order();
		Event event = new Event();
		Application application = new Application();

		try {
			JSONObject payloadMessage = new JSONObject(sqsPayload.getMessage());
			application = objectMapper.readValue(payloadMessage.get("application").toString(), Application.class);
			order = objectMapper.readValue(payloadMessage.get("order").toString(), Order.class);
			event = objectMapper.readValue(payloadMessage.get("event").toString(), Event.class);

		} catch (JsonParseException | JSONException | IOException e) {

			if (application.getId() == Applications.APP_BUZZORDER.getId()
					|| application.getId() == Applications.APP_TDC.getId()
					|| application.getId() == Applications.APP_SLASH.getId()) {

				defaultJmsTemplate.convertAndSend(sqsQueueUnknownName, sqsPayload);
				this.aresMetrics.incrementUnknownStatusCounter();

				return true;
			}
		}

		/*
		 * Starts validation of the rules relevant to the Apolo-Gateway context.
		 */
		if (event.getStatus().getId() == Status.ORDER_RECEIVED.getId()
				&& (application.getId() == Applications.APP_P52.getId()
						|| application.getId() == Applications.APP_NINO.getId())) {

			PostRequestOrder postRequestOrder = new PostRequestOrder(order.getId());
			PostRequestOrderBody postRequestOrderBody = new PostRequestOrderBody(postRequestOrder, event.getDate());

			boolean ok = apoloGatewayClient.createOrder(SaleType.CAPTURED, postRequestOrderBody.marshall(),
					order.getId(), event.getStatus().getId(), uuid);

			if (!ok) {
//				loggerWarn(sqsPayload.getMessage(), BurzumUtil.generateInfoLog(uuid, "POST", SaleType.CAPTURED.getType()));

				String sqsOk = sendMessage(order, event, "POST", uuid);
				
				if(sqsOk == null || sqsOk.isEmpty()) {
					throw new SendSQSException(
							String.format("Could not send event to queue [order: %s event date: %s queue: %s]",
									order.getId(), event.getDate(), "apolo-captured-orders-fallback"));
				}
				
				this.aresMetrics.incrementFallbackStatusCounter();

			}

//			loggerInfo(sqsPayload.getMessage(),
//					BurzumUtil.generateInfoLog(uuid, "POST", SaleType.CAPTURED.getType()));

			this.aresMetrics.incrementMessageSendApoloGateway();
			this.aresMetrics.incrementCapturedCreatedCounter();

			return ok;
		} else if (event.getStatus().getId() == Status.ORDER_APROVED.getId()
				|| event.getStatus().getId() == Status.QUICK_SALE.getId()
				|| event.getStatus().getId() == Status.AWAITING_INVOICE.getId()) {

			if (application.getId() == Applications.APP_BUZZORDER.getId()) {

				PostRequestOrder postRequestOrder = new PostRequestOrder(order.getSubOrder().getId());
				PostRequestOrderBody postRequestOrderBody = new PostRequestOrderBody(postRequestOrder, event.getDate());

				boolean ok = apoloGatewayClient.createOrder(SaleType.PROCESSED, postRequestOrderBody.marshall(),
						order.getSubOrder().getId(), event.getStatus().getId(), uuid);

				if (!ok) {
//					loggerError(sqsPayload.getMessage(),
//							BurzumUtil.generateInfoLog(uuid, "POST", SaleType.PROCESSED.getType()));
					this.aresMetrics.incrementFallbackStatusCounter();
					throw new BililiuEndpointSendEventException(String.format(
							"[POST-Processed] - Could not send event to Gateway [order: %s event date: %s]",
							order.getSubOrder().getId(), event.getDate()));
				}

//				loggerInfo(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "POST", SaleType.PROCESSED.getType()));

				this.aresMetrics.incrementMessageSendApoloGateway();
				this.aresMetrics.incrementProcessedCreatedCounter();

				return ok;
			} else if (application.getId() == Applications.APP_P52.getId()
					|| application.getId() == Applications.APP_NINO.getId()) {

				PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_APROVED.getId(),
						Status.ORDER_APROVED.getDescription());
				PatchRequestStatusBody patchRequestStatusBody = new PatchRequestStatusBody(patchRequestStatus,
						event.getDate());

				boolean ok = apoloGatewayClient.updateOrder(SaleType.CAPTURED, patchRequestStatusBody.marshall(),
						order.getId(), event.getStatus().getId(), uuid);

				if (!ok) {
//					loggerWarn(sqsPayload.getMessage(), BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.CAPTURED.getType()));

					String sqsOk = sendMessage(order, event, "PATCH", uuid);

					if (sqsOk == null || sqsOk.isEmpty()) {
						throw new SendSQSException(
								String.format("Could not send event to queue [order: %s event date: %s queue: %s]",
										order.getId(), event.getDate(), "apolo-captured-orders-fallback"));
					}
					
					this.aresMetrics.incrementFallbackStatusCounter();

				}

//				loggerInfo(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.CAPTURED.getType()));

				this.aresMetrics.incrementMessageSendApoloGateway();
				this.aresMetrics.incrementCapturedUpdateCounter();

				return ok;
			}

		} else if (event.getStatus().getId() == Status.ORDER_CANCELLED.getId()
				|| event.getStatus().getId() == Status.RETURN.getId()) {

			if (application.getId() == Applications.APP_TDC.getId()) {
				DeleteRequestBody deleteRequestBody = new DeleteRequestBody(event.getDate());

				boolean ok = apoloGatewayClient.deleteOrder(SaleType.PROCESSED, deleteRequestBody.marshall(),
						order.getSubOrder().getId(), event.getStatus().getId(), uuid);

				if (!ok) {

//					loggerError(sqsPayload.getMessage(),
//							BurzumUtil.generateInfoLog(uuid, "DELETE", SaleType.PROCESSED.getType()));
					this.aresMetrics.incrementFallbackStatusCounter();
					throw new BililiuEndpointSendEventException(
							String.format("[DELETE-Processed] - not send event to Gateway [order: %s event date: %s]",
									order.getSubOrder().getId(), event.getDate()));
				}

//				loggerInfo(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "DELETE", SaleType.PROCESSED.getType()));

				this.aresMetrics.incrementMessageSendApoloGateway();
				this.aresMetrics.incrementProcessedCancelledCounter();

				return ok;
			} else if (application.getId() == Applications.APP_BUZZORDER.getId()) {
				PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.ORDER_CANCELLED.getId(),
						Status.ORDER_CANCELLED.getDescription());
				PatchRequestStatusBody patchRequestStatusBody = new PatchRequestStatusBody(patchRequestStatus,
						event.getDate());

				boolean ok = apoloGatewayClient.updateOrder(SaleType.PROCESSED, patchRequestStatusBody.marshall(),
						order.getSubOrder().getId(), event.getStatus().getId(), uuid);

				if (!ok) {
//					loggerError(sqsPayload.getMessage(),
//							BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.PROCESSED.getType()));
					this.aresMetrics.incrementFallbackStatusCounter();
					throw new BililiuEndpointSendEventException(String.format(
							"[PATCH-Processed] - Could not send event to Gateway [order: %s event date: %s]",
							order.getSubOrder().getId(), event.getDate()));
				}

//				loggerInfo(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.PROCESSED.getType()));

				this.aresMetrics.incrementMessageSendApoloGateway();
				this.aresMetrics.incrementProcessedUpdateCounter();

				return ok;
			} else if (application.getId() == Applications.APP_P52.getId()
					|| application.getId() == Applications.APP_NINO.getId()) {

				DeleteRequestBody deleteRequestBody = new DeleteRequestBody(event.getDate());

				boolean ok = apoloGatewayClient.deleteOrder(SaleType.CAPTURED, deleteRequestBody.marshall(),
						order.getId(), event.getStatus().getId(), uuid);

				if (!ok) {
//					loggerWarn(sqsPayload.getMessage(), BurzumUtil.generateInfoLog(uuid, "DELETE", SaleType.CAPTURED.getType()));

					String sqsOk = sendMessage(order, event, "DELETE", uuid);

					if (sqsOk == null || sqsOk.isEmpty()) {
						throw new SendSQSException(
								String.format("Could not send event to queue [order: %s event date: %s queue: %s]",
										order.getId(), event.getDate(), "apolo-captured-orders-fallback"));
					}

					this.aresMetrics.incrementFallbackStatusCounter();
				}

//				loggerInfo(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "DELETE", SaleType.CAPTURED.getType()));

				this.aresMetrics.incrementMessageSendApoloGateway();
				this.aresMetrics.incrementCapturedCancelledCounter();

				return ok;
			}

		} else if (event.getStatus().getId() == Status.INVOICE.getId()
				&& application.getId() != Applications.APP_INTEGRA_COMMERCE.getId()) {
			PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.INVOICE.getId(),
					Status.INVOICE.getDescription());
			PatchRequestStatusBody patchRequestStatusBody = new PatchRequestStatusBody(patchRequestStatus,
					event.getDate());

			boolean ok = apoloGatewayClient.updateOrder(SaleType.PROCESSED, patchRequestStatusBody.marshall(),
					order.getSubOrder().getId(), event.getStatus().getId(), uuid);

			if (!ok) {
//				loggerError(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.PROCESSED.getType()));
				this.aresMetrics.incrementFallbackStatusCounter();
				throw new BililiuEndpointSendEventException(
						String.format("[PATCH-Processed] - Could not send event to Gateway [order: %s event date: %s]",
								order.getSubOrder().getId(), event.getDate()));
			}

//			loggerInfo(sqsPayload.getMessage(),
//					BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.PROCESSED.getType()));

			this.aresMetrics.incrementMessageSendApoloGateway();
			this.aresMetrics.incrementProcessedUpdateCounter();

			return ok;
		} else if (order.getSubOrder().getId() != null && event.getStatus().getId() == Status.UPDATE_LEGACY.getId()
				&& application.getId() == Applications.APP_SLASH.getId()) {

			PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.UPDATE_LEGACY.getId(),
					Status.UPDATE_LEGACY.getDescription());
			PatchRequestStatusBody patchRequestStatusBody = new PatchRequestStatusBody(patchRequestStatus,
					event.getDate());

			boolean ok = apoloGatewayClient.updateOrder(SaleType.PROCESSED, patchRequestStatusBody.marshall(),
					order.getSubOrder().getId(), event.getStatus().getId(), uuid);

			if (!ok) {
//				loggerError(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.PROCESSED.getType()));
				this.aresMetrics.incrementFallbackStatusCounter();
				throw new BililiuEndpointSendEventException(
						String.format("[PATCH-Processed] - Could not send event to Gateway [order: %s event date: %s]",
								order.getSubOrder().getId(), event.getDate()));
			}

//			loggerInfo(sqsPayload.getMessage(),
//					BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.PROCESSED.getType()));

			this.aresMetrics.incrementMessageSendApoloGateway();
			this.aresMetrics.incrementProcessedUpdateCounter();

			return ok;
		} else if (event.getStatus().getId() == Status.INTEGRATED.getId()
				&& (application.getId() == Applications.APP_SLASH.getId()
						|| application.getId() == Applications.APP_SULLIVAN.getId())) {

			PatchRequestStatus patchRequestStatus = new PatchRequestStatus(Status.INTEGRATED.getId(),
					Status.INTEGRATED.getDescription());
			PatchRequestStatusBody patchRequestStatusBody = new PatchRequestStatusBody(patchRequestStatus,
					event.getDate());

			boolean ok = apoloGatewayClient.updateOrder(SaleType.CAPTURED, patchRequestStatusBody.marshall(),
					order.getId(), event.getStatus().getId(), uuid);

			if (!ok) {
//				loggerWarn(sqsPayload.getMessage(),
//						BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.CAPTURED.getType()));

				String sqsOk = sendMessage(order, event, "PATCH", uuid);

				if (sqsOk == null || sqsOk.isEmpty()) {
					throw new SendSQSException(
							String.format("Could not send event to queue [order: %s event date: %s queue: %s]",
									order.getId(), event.getDate(), "apolo-captured-orders-fallback"));
				}

				this.aresMetrics.incrementFallbackStatusCounter();
			}

//			loggerInfo(sqsPayload.getMessage(),
//					BurzumUtil.generateInfoLog(uuid, "PATCH", SaleType.CAPTURED.getType()));

			this.aresMetrics.incrementMessageSendApoloGateway();
			this.aresMetrics.incrementCapturedUpdateCounter();

			return ok;
		}

		return true;
	}

	public String sendMessage(Order order, Event event, String verb, String hash) {

		MessageSqs messageSqs = new MessageSqs();
		OrderSqs orderSqs = new OrderSqs();

		orderSqs.setId(order.getId());
		messageSqs.setVerb(verb);
		messageSqs.setCreatedAt(event.getDate());
		messageSqs.setOrder(orderSqs);

		return sendMessageSQS.sendMessage(messageSqs.toJson(), hash);

	}
}