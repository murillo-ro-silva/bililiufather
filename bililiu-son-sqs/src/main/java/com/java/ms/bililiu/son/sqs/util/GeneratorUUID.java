package com.java.ms.bililiu.son.sqs.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class GeneratorUUID {

	public String generator() {
		UUID uuid = UUID.randomUUID();
		return  uuid.toString();
	}
}
