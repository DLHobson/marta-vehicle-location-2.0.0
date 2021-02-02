package com.itsmarta.technology.its.martavehiclelocation.configuration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix="router", ignoreInvalidFields = true)
@ConfigurationPropertiesScan
public class RouterMessageConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(RouterMessageConfiguration.class);

	//Router Configuration
	private final Message message = new Message();
	private final Listener listener = new Listener();

	@ConfigurationProperties(prefix="router.message", ignoreInvalidFields = true)
	public static class Message {
		private String type = "** INVALID FIELD - CHECK application.yml **";
		private Integer size = 999;

		public Message message() {
			logger.info("Message instantiated");
			logger.info(this.toString());

			return new Message();
		}
		
		public String getType() {
			return type;
		}
		public Integer getSize() {
			return size;
		}
		public void setType(String type) {
			this.type = type;
		}
		public void setSize(Integer size) {
			this.size = size;
		}
		@Override
		public String toString() {
			return "Message [type=" + type + ", size=" + size + "]";
		}
	}

	@ConfigurationProperties(prefix="ftl.coreservers", ignoreInvalidFields = true)
	public static class Listener {
		private String protocol = "** INVALID FIELD - CHECK application.yml **";
		private List<Integer> ports = new ArrayList<>(9999);

		public Listener listener() {
			logger.info("Listener instantiated");
			logger.info(this.toString());

			return new Listener();
		}
		public String getProtocol() {
			return protocol;
		}
		public List<Integer> getPorts() {
			return ports;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public void setPorts(List<Integer> ports) {
			this.ports = ports;
		}
		@Override
		public String toString() {
			return "Listener [protocol=" + protocol + ", ports=" + ports + "]";
		}


	}

	@Bean(name="routerMessageConfig")
	public RouterMessageConfiguration routerMessageConfig() {
		logger.info("RouterMessageConfiguration instantiated");
		logger.info(this.toString());
		return new RouterMessageConfiguration();
	}

	public Message getMessage() {
		return message;
	}

	public Listener getListener() {
		return listener;
	}

	@Override
	public String toString() {
		return "RouterMessageConfiguration{" +
				"message=" + message +
				", listener=" + listener +
				'}';
	}
}
