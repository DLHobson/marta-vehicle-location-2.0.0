package com.itsmarta.technology.its.martavehiclelocation.service;

import com.itsmarta.technology.its.martavehiclelocation.dao.RouterMessage;

import java.util.concurrent.CompletableFuture;

public interface ReceiveRouterMessageService {

	RouterMessage receiveRouterMessage(int udpPort) ;
	void healthCheck();
}
