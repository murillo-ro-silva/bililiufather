package com.java.ms.bililiu.son.sqs.message.error;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import com.java.ms.bililiu.son.sqs.metrics.BililiuSonMetrics;
import com.java.ms.bililiu.son.sqs.notification.SlackNotifier;

/**
 * @author Murillo Note: Centralizes the errors that are obtained in the project
 *         and notifies the slack.
 */
@Component
public class MessageErrorHandler implements ErrorHandler {

	private static final Log log = LogFactory.getLog(MessageErrorHandler.class);

	@Autowired
	private SlackNotifier slackNotifier;

	@Autowired
	private BililiuSonMetrics aresMetrics;

	@Value("${slack.enabled}")
	private Boolean slackEnabled;

	@Override
	public void handleError(Throwable throwable) {

		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		String stackTrace = sw.getBuffer().toString();
		log.error(stackTrace);

		this.aresMetrics.incrementErrorStatusCounter();
	}

}