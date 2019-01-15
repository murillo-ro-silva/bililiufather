package com.java.ms.bililiu.son.sqs.notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Slack HTTP client for monitification purpose
 * @author pupo
 */
@Component
public class SlackNotifier {
	
	private static final Log LOG = LogFactory.getLog(SlackNotifier.class);
	
	@Value("${env}")
	private String env;	
	
	@Value("${slack.endpoint}")
	private String endpoint;	

	/**
	 * Send error to Slack Rest API
	 * @param String error error message
	 */
	@Async
	public void send(final String error) {		
		
		try {			
			HttpClient client = HttpClientBuilder.create().build();
			
			HttpPost post = new HttpPost(this.endpoint);
			post.addHeader("Content-type", "application/json");
			
			Map<String,String> color = new HashMap<>();
			color.put("color", "#ff0000");
			
			JSONObject fieldError = new JSONObject();
			fieldError.put("title", "Error");	 
			fieldError.put("value", error);
			fieldError.put("short", "true");
			
			JSONObject fieldEnv = new JSONObject();
			fieldEnv.put("title", "Environment");
			fieldEnv.put("value", this.env);
			fieldEnv.put("short", "true");
			
			JSONArray fields = new JSONArray();				
			fields.put(fieldError);
			fields.put(fieldEnv);
			
			JSONObject attachmentsObject = new JSONObject();
			attachmentsObject.put("color", "#ff0000");
			attachmentsObject.put("fields", fields);
			
			JSONArray attachments = new JSONArray();		
			attachments.put(attachmentsObject);
			
			JSONObject message = new JSONObject();
			message.put("username","Ares-Worker ");
			message.put("icon_emoji",":warning:");
			message.put("text", "You have errors in worker: ");
			message.put("attachments", attachments);
			 
			StringEntity entity = new StringEntity(message.toString());
			entity.setContentType("application/json");
			post.setEntity(entity);							
			
			client.execute(post); 			
		} catch (IOException | JSONException e) {
			LOG.error("Error sending slack notification: " + e.getMessage());
		}      
        
	}

}