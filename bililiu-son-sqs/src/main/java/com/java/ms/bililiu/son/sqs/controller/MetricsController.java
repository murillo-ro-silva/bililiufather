package com.java.ms.bililiu.son.sqs.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.java.ms.bililiu.son.sqs.message.exception.MetricsRedirectException;

/**
 * This class exposes metrics for Prometheus
 * @author Murillo
 */
@RestController
public class MetricsController {
	
	/**
	 * Exposes /metrics endpoint for Prometheus monitoring
	 * @param HttpServletRequest request incoming request
	 * @param HttpServletResponse response outgoing response
	 */
	@RequestMapping(value = "metrics", method = RequestMethod.GET)
	public void metrics(HttpServletRequest request, HttpServletResponse response) {
		String schema = request.getScheme();
		String server = request.getServerName();
		int port = request.getServerPort();
		
		String url = String.format("%s://%s:%d/actuator/prometheus", schema, server, port);
		
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new MetricsRedirectException(e.getMessage()) ;
		}
	}

}