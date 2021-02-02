package com.itsmarta.technology.its.martavehiclelocation;

import com.itsmarta.technology.its.martavehiclelocation.configuration.AsynchConfiguration;
import com.itsmarta.technology.its.martavehiclelocation.configuration.RealmServerConfiguration;
import com.itsmarta.technology.its.martavehiclelocation.configuration.RouterMessageConfiguration;
import com.itsmarta.technology.its.martavehiclelocation.dao.RouterMessage;
import com.itsmarta.technology.its.martavehiclelocation.service.PortAllocationService;
import com.itsmarta.technology.its.martavehiclelocation.service.PublishLocationService;
import com.itsmarta.technology.its.martavehiclelocation.service.ReceiveRouterMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class AppRunner implements CommandLineRunner {
	
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);
    
    @Autowired private ReceiveRouterMessageService routerMessageSVC;
    @Autowired private PublishLocationService publishLocationSVC;
    @Autowired private PortAllocationService portAllocationSVC;
    @Autowired RealmServerConfiguration realmServerConfig;
    @Autowired RouterMessageConfiguration routerMessageConfig;
	@Autowired private AsynchConfiguration asynchConfig;
	@Qualifier("taskExecutor")
	@Autowired private Executor taskExecutor;

	@Override
	public void run(String... args) throws Exception {
		Integer udpPort;
		CompletableFuture healthCheck = new CompletableFuture<>();

		healthCheck = CompletableFuture.runAsync(() -> routerMessageSVC.healthCheck());

		while(true) {
			try {

				 // Check for an available port from the map.  If a port is available then
				 // get one available port.  If there are no ports available then start over

				if(portAllocationSVC.portsAvailable()){
					udpPort = portAllocationSVC.retrieveNextAvailablePort();
				}
				else {
					continue;
				}
				if(udpPort != null){
					final int finalIntUDPPort = udpPort;
					CompletableFuture completableFuture = new CompletableFuture<>();

					logger.info("Calling: ReceiveRouterMessageService.receiveRouterMessage(" + udpPort + ") Asynchronously");

					 // Spawn a thread to listen for a message on the retrieved port.  Once a
					 // message is received, release the port for the next thread and publish
					 // the message to FTL.  Check for exceptions in the thread.  Then release
					 // the thread.

					completableFuture =
							CompletableFuture.supplyAsync(() -> routerMessageSVC.receiveRouterMessage(finalIntUDPPort),taskExecutor)
							.thenAccept(routerMessage -> {
								logger.info("Router Message: " + routerMessage.toString());
								portAllocationSVC.setPortToAvailable(finalIntUDPPort);
								logger.info("Calling: PublishLocationService.sendMessage(routerMsg)");
								publishLocationSVC.sendMessage((RouterMessage) routerMessage);
							})
							.exceptionally(ex -> {
								logger.error("Error encountered in thread: " + ex.getMessage());
								return null;
							});
					completableFuture.complete(null);
				}
			}
			catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
	}
}
