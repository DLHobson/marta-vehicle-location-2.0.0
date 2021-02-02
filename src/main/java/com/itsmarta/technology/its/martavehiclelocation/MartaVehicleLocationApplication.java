package com.itsmarta.technology.its.martavehiclelocation;

import com.itsmarta.technology.its.martavehiclelocation.configuration.AsynchConfiguration;
import com.itsmarta.technology.its.martavehiclelocation.configuration.RealmServerConfiguration;
import com.itsmarta.technology.its.martavehiclelocation.configuration.RouterMessageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class MartaVehicleLocationApplication {

	@Autowired
	private AsynchConfiguration asynchConfig;

	public static void main(String[] args) {
		ApplicationContext appContext = SpringApplication.run(MartaVehicleLocationApplication.class, args);
	}

	@Bean(name="taskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(asynchConfig.getPool().getCoreSize());
		executor.setMaxPoolSize(asynchConfig.getPool().getMaxSize());
		executor.setQueueCapacity(asynchConfig.getPool().getQueueCapacity());
		executor.setKeepAliveSeconds(asynchConfig.getPool().getKeepAlive());
		executor.setAllowCoreThreadTimeOut(asynchConfig.getPool().getAllowCoreThreadTimeout());
		executor.setThreadNamePrefix(asynchConfig.getThreadNamePrefix());
		executor.initialize();
		return executor;
	}
}
