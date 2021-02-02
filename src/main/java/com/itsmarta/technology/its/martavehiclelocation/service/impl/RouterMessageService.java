package com.itsmarta.technology.its.martavehiclelocation.service.impl;

import com.itsmarta.technology.its.martavehiclelocation.configuration.HeartbeatConfiguration;
import com.itsmarta.technology.its.martavehiclelocation.configuration.RouterMessageConfiguration;
import com.itsmarta.technology.its.martavehiclelocation.dao.RouterMessage;
import com.itsmarta.technology.its.martavehiclelocation.dao.impl.TAIPMessage;
import com.itsmarta.technology.its.martavehiclelocation.service.ReceiveRouterMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.CompletableFuture;

public class RouterMessageService implements ReceiveRouterMessageService {
	private static final Logger logger = LogManager.getLogger(RouterMessageService.class);

	@Autowired private RouterMessageConfiguration routerMessageConfig;
	@Autowired private HeartbeatConfiguration heartbeatConfiguration;
	private RouterMessage taipMsg;
	private RouterMessage nmeaMsg;

	@Override
	public RouterMessage receiveRouterMessage(int udpPort) {
		logger.info("Entering: receiveRouterMessage(" + udpPort + ")");
		CompletableFuture<RouterMessage> future = new CompletableFuture<>();
		RouterMessage message = null;
		DatagramSocket serverSocket = null;

		try
		{
			serverSocket = new DatagramSocket(udpPort);

			byte[] receiveData = new byte[routerMessageConfig.getMessage().getSize()];
			byte[] sendData;

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			logger.info("Listening for datagram packet on port: " + udpPort);

			serverSocket.receive(receivePacket);

			logger.info("Message received on port: " + udpPort);

			String sentence = new String(receivePacket.getData()).trim();
			logger.info("Message: " + sentence);

			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			int msgLen = receivePacket.getLength();

			char firstChar = sentence.charAt(0);

			// Determine the type of message format received, TAIP or NMEA

			if(firstChar == '>') {
				logger.info("TAIP Message received");

				message = new TAIPMessage();

				message.setSourceIPaddress(IPAddress);
				message.setSourcePort(port);
				message.setSentence(sentence.trim());
				message.setMessageLength(msgLen);
//				message.ParseMessageString(sentence.trim());

				logger.info("Router Message: " + message.toString());

				String capitalizedSentence = message.getSentence().toUpperCase();

				sendData = capitalizedSentence.getBytes();

				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						message.getSourceIPaddress(),
						message.getSourcePort());

				serverSocket.send(sendPacket);

			}
			else if(firstChar == '$') {
				logger.info("NMEA Message received.  Will log and disregard.");

				logger.error("*** Invalid message format received.  NMEA Message: " + sentence + "***");
			}
		}
		catch(SocketException sockEx) {
			logger.error("Port [" + udpPort + "] not open: " + sockEx.getMessage());
			logger.error(sockEx.getMessage());
		}
		catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		finally {
			try{
				serverSocket.close();
			}
			catch(Exception ex) {
				logger.error("Exception occured while closing socket: " + ex.getMessage());
			}
		}

		logger.info("Exiting: receiveRouterMessage(" + udpPort + ")");

		return message;
	}

	@Override
	public void healthCheck() {
		logger.info("Entering: healthCheck()");

		ServerSocket server;
		Socket socket = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		int port = heartbeatConfiguration.getPort();

		try {
			server = new ServerSocket(port);

			logger.info("Heartbeat server started");

			while(true)
			{
				try {
					socket = server.accept();

					// Read a message sent by client application
					ois = new ObjectInputStream(socket.getInputStream());
					String message = (String) ois.readObject();
					logger.info("Azure Probe: "+ message);

					// Send a response to the client application
					oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(heartbeatConfiguration.getSuccessMsg());

				}
				catch (IOException | ClassNotFoundException ioEx) {
					logger.error(ioEx.getMessage());
					oos.writeObject(heartbeatConfiguration.getFailureMsg());

				}
			}
		}
		catch (IOException ioEx) {
			logger.error(ioEx.getMessage());
		}
		finally {
			try {
				ois.close();
				oos.close();
				socket.close();
			}
			catch(Exception ex) {
				logger.error("Port [" + port + "] not open: " + ex.getMessage());
				logger.error(ex.getMessage());
			}
			logger.info("Exiting: healthCheck()");
		}
	}

}
