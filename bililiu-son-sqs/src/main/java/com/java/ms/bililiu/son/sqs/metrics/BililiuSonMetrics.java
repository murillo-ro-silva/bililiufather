package com.java.ms.bililiu.son.sqs.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * This class exposes main metrics to Ares
 * the metrics are:
 * - Exchange counter
 * @author Murillo
 */
@Component
public class BililiuSonMetrics {
	
	private final MeterRegistry registry;
	
	private final String MESSAGE_COUNTER_PROCESSED = "Message Counter Processed";
	private final String MESSAGE_COUNTER_PROCESSED_KEY = "type";
	private final String MESSAGE_COUNTER_PROCESSED_DESCRIPTION = "Total of messages processed by type";
	
	private final String MESSAGE_COUNTER_CAPTURED = "Message Counter Captured";
	private final String MESSAGE_COUNTER_CAPTURED_KEY = "type";
	private final String MESSAGE_COUNTER_CAPTURED_DESCRIPTION = "Total of messages captured by type";
	
	private final String STATUS_COUNTER_METRIC = "status_counter";
	private final String STATUS_COUNTER_KEY = "status";
	private final String STATUS_COUNTER_DESCRIPTION = "Status of messages";

	private final String MESSAGE_COUNTER_SEND_GATEWAY = "status_counter_send_gateway";
	private final String MESSAGE_COUNTER_SEND_GATEWAY_KEY = "status";
	private final String MESSAGE_COUNTER_SEND_GATEWAY_DESCRIPTION = "Status of messages sent to apolo gateway";

	private final String MESSAGE_COUNTER_CAPTURED_MAESTRO = "status_counter_captured_maestro";
	private final String MESSAGE_COUNTER_CAPTURED_MAESTRO_KEY = "status";
	private final String MESSAGE_COUNTER_CAPTURED_MAESTRO_DESCRIPTION = "Status of messages captured to Maestro";
		
	private Counter processedCreatedCounter;
	private Counter processedCancelledCounter;
	private Counter processedUpdateCounter;
	
	private Counter capturedCreatedCounter;
	private Counter capturedCancelledCounter;
	private Counter capturedUpdateCounter;
	
	private Counter totalMessageSendApoloGateway;
	private Counter totalMessageCapturedMaestro;

	private Counter successStatusCounter;
	private Counter errorStatusCounter;
	private Counter fallbackStatusCounter;
	private Counter unknownStatusCounter;
	
	public BililiuSonMetrics(MeterRegistry registry) {
		this.registry = registry;
		this.processedCreatedCounter = Counter.builder(this.MESSAGE_COUNTER_PROCESSED)
						                      .tag(this.MESSAGE_COUNTER_PROCESSED_KEY, "Order Created")
						                      .description(this.MESSAGE_COUNTER_PROCESSED_DESCRIPTION)				 
						                      .register(this.registry);
				
		this.processedCancelledCounter = Counter.builder(this.MESSAGE_COUNTER_PROCESSED)
						                        .tag(this.MESSAGE_COUNTER_PROCESSED_KEY, "Order Cancelled")
						                        .description(this.MESSAGE_COUNTER_PROCESSED_DESCRIPTION)
						                        .register(this.registry);
				
		this.processedUpdateCounter = Counter.builder(this.MESSAGE_COUNTER_PROCESSED)
					                         .tag(this.MESSAGE_COUNTER_PROCESSED_KEY, "Order Update")
					                         .description(this.MESSAGE_COUNTER_PROCESSED_DESCRIPTION)
					                         .register(this.registry);
			
		this.capturedCreatedCounter = Counter.builder(this.MESSAGE_COUNTER_CAPTURED)
								             .tag(this.MESSAGE_COUNTER_CAPTURED_KEY, "Order Created")
								             .description(this.MESSAGE_COUNTER_CAPTURED_DESCRIPTION)				 
								             .register(this.registry);
				
		this.capturedCancelledCounter = Counter.builder(this.MESSAGE_COUNTER_CAPTURED)
							                   .tag(this.MESSAGE_COUNTER_CAPTURED_KEY, "Order Cancelled")
							                   .description(this.MESSAGE_COUNTER_CAPTURED_DESCRIPTION)
							                   .register(this.registry);
				
		this.capturedUpdateCounter = Counter.builder(this.MESSAGE_COUNTER_CAPTURED)
				                    	  	.tag(this.MESSAGE_COUNTER_CAPTURED_KEY, "Order Update")
				                    	  	.description(this.MESSAGE_COUNTER_CAPTURED_DESCRIPTION)
				                    	  	.register(this.registry);

		this.totalMessageSendApoloGateway = Counter.builder(this.MESSAGE_COUNTER_SEND_GATEWAY)
									               .tag(this.MESSAGE_COUNTER_SEND_GATEWAY_KEY, "Success Send Apolo Gateway")
									               .description(this.MESSAGE_COUNTER_SEND_GATEWAY_DESCRIPTION)
									               .register(this.registry);
		
		this.totalMessageCapturedMaestro = Counter.builder(this.MESSAGE_COUNTER_CAPTURED_MAESTRO)
				                           .tag(this.MESSAGE_COUNTER_CAPTURED_MAESTRO_KEY, "Success Capted Message Maestro")
				                           .description(this.MESSAGE_COUNTER_CAPTURED_MAESTRO_DESCRIPTION)
				                           .register(this.registry);
				                           
		this.totalMessageSendApoloGateway = Counter.builder(this.STATUS_COUNTER_METRIC)
									               .tag(this.STATUS_COUNTER_KEY, "Success Send Apolo Gateway")
									               .description(this.STATUS_COUNTER_DESCRIPTION)
									               .register(this.registry);

		this.totalMessageSendApoloGateway = Counter.builder(this.MESSAGE_COUNTER_SEND_GATEWAY)
									               .tag(this.MESSAGE_COUNTER_SEND_GATEWAY_KEY, "Success Send Apolo Gateway")
									               .description(this.MESSAGE_COUNTER_SEND_GATEWAY_DESCRIPTION)
									               .register(this.registry);
		
		this.totalMessageCapturedMaestro = Counter.builder(this.MESSAGE_COUNTER_CAPTURED_MAESTRO)
				                           .tag(this.MESSAGE_COUNTER_CAPTURED_MAESTRO_KEY, "Success Capted Message Maestro")
				                           .description(this.MESSAGE_COUNTER_CAPTURED_MAESTRO_DESCRIPTION)
				                           .register(this.registry);
		
		this.errorStatusCounter = Counter.builder(this.STATUS_COUNTER_METRIC)
				                         .tag(this.STATUS_COUNTER_KEY, "Error")
				                         .description(this.STATUS_COUNTER_DESCRIPTION)
				                         .register(this.registry);
		
		this.fallbackStatusCounter = Counter.builder(this.STATUS_COUNTER_METRIC)
							                .tag(this.STATUS_COUNTER_KEY, "Fallback")
							                .description(this.STATUS_COUNTER_DESCRIPTION)
							                .register(this.registry);
		
		this.unknownStatusCounter = Counter.builder(this.STATUS_COUNTER_METRIC)
							               .tag(this.STATUS_COUNTER_KEY, "Unknown")
							               .description(this.STATUS_COUNTER_DESCRIPTION)
							               .register(this.registry);

	}
	
	public void incrementProcessedCreatedCounter() {
		this.processedCreatedCounter.increment();
	}
	
	public void incrementProcessedCancelledCounter() {
		this.processedCancelledCounter.increment();
	}
	
	public void incrementProcessedUpdateCounter() {
		this.processedUpdateCounter.increment();
	}

	public void incrementCapturedCreatedCounter() {
		this.capturedCreatedCounter.increment();
	}
	
	public void incrementCapturedCancelledCounter() {
		this.capturedCancelledCounter.increment();
	}
	
	public void incrementCapturedUpdateCounter() {
		this.capturedUpdateCounter.increment();
	}
	
	public void incrementMessageSendApoloGateway() {
		this.totalMessageSendApoloGateway.increment();
	}
	
	public void incrementMessageCapturedMaestro() {
		this.totalMessageCapturedMaestro.increment();
	}
	
	public void incrementSuccessStatusCounter() {
		this.successStatusCounter.increment();
	}
	
	public void incrementErrorStatusCounter() {
		this.errorStatusCounter.increment();
	}
	
	public void incrementFallbackStatusCounter() {
		this.fallbackStatusCounter.increment();
	}
	
	public void incrementUnknownStatusCounter() {
		this.unknownStatusCounter.increment();
	}
	
}
