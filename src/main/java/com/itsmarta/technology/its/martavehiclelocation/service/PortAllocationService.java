package com.itsmarta.technology.its.martavehiclelocation.service;


public interface PortAllocationService {
	
    Integer retrieveNextAvailablePort() throws InterruptedException;
    void setPortToAvailable(Integer key);
    boolean portsAvailable();

}
