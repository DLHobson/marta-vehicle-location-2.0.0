package com.itsmarta.technology.its.martavehiclelocation.service;

import com.itsmarta.technology.its.martavehiclelocation.dao.RouterMessage;
import com.tibco.ftl.FTLException;

public interface PublishLocationService {
	
    void sendMessage(RouterMessage routerMsg);
}
