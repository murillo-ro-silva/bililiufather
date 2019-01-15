package com.java.ms.bililiu.son.sqs;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.jms.annotation.EnableJms;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@SpringBootApplication
@EnableJms
public class BililiuSonApplication {

	public static void main(String[] args) {
		TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");
	    TimeZone.setDefault(timeZone);
		SpringApplication.run(BililiuSonApplication.class, args);
	}

}