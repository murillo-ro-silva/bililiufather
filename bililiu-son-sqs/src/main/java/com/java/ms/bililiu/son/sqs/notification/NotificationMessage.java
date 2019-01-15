//package com.java.ms.bililiu.son.sqs.notification;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import com.luizalabs.apolo.commons.notification.SlackBaseMessage;
//
//public class NotificationMessage extends SlackBaseMessage {
//
//	@Override
//	public String message(String error, String env) {
//		Map<String,String> color = new HashMap<>();
//		color.put("color", "#ff0000");
//		
//		JSONObject fieldError = new JSONObject();
//		fieldError.put("title", "Error");	 
//		fieldError.put("value", error);
//		fieldError.put("short", "true");
//		
//		JSONObject fieldEnv = new JSONObject();
//		fieldEnv.put("title", "Environment");
//		fieldEnv.put("value", env);
//		fieldEnv.put("short", "true");
//		
//		JSONArray fields = new JSONArray();				
//		fields.put(fieldError);
//		fields.put(fieldEnv);
//		
//		JSONObject attachmentsObject = new JSONObject();
//		attachmentsObject.put("color", "#ff0000");
//		attachmentsObject.put("fields", fields);
//		
//		JSONArray attachments = new JSONArray();		
//		attachments.put(attachmentsObject);
//		
//		JSONObject message = new JSONObject();
//		message.put("username","Ares Worker");
//		message.put("icon_emoji",":warning:");
//		message.put("text", "Error ares-worker");
//		message.put("attachments", attachments);
//		
//		return message.toString();
//	}
//
//}