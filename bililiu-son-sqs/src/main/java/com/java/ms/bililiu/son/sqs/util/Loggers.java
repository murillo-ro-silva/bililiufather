//package com.java.ms.bililiu.son.sqs.util;
//
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import net.logstash.logback.marker.Markers;
//
//public class Loggers {
//
//	private static final Logger LOG = LoggerFactory.getLogger(Loggers.class);
//
//	public void loggerInfo(final String message, final Map<String, Object> data) {
//		LOG.info(Markers.appendEntries(data), message);
//	}
//
//	public void loggerWarn(final String message, final Map<String, Object> data) {
//		LOG.warn(Markers.appendEntries(data), message);
//	}
//
//	public void loggerError(final String message, final Map<String, Object> data) {
//		LOG.error(Markers.appendEntries(data), message);
//	}
//}
