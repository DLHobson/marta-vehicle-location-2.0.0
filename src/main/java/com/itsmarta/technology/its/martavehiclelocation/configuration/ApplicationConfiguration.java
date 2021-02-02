package com.itsmarta.technology.its.martavehiclelocation.configuration;

import com.itsmarta.technology.its.martavehiclelocation.dao.RouterMessage;
import com.itsmarta.technology.its.martavehiclelocation.dao.impl.NMEAMessage;
import com.itsmarta.technology.its.martavehiclelocation.dao.impl.TAIPMessage;
import com.itsmarta.technology.its.martavehiclelocation.service.PublishLocationService;
import com.itsmarta.technology.its.martavehiclelocation.service.ReceiveRouterMessageService;
import com.itsmarta.technology.its.martavehiclelocation.service.impl.PublishVehicleLocationService;
import com.itsmarta.technology.its.martavehiclelocation.service.impl.RouterMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ApplicationConfiguration {
    @Bean(name="nmeaMsg")
    public RouterMessage getNMEAMessage() {
        return new NMEAMessage();
    }

    @Bean(name="taipMsg")
    public RouterMessage getTAIPMessage() {
        return new TAIPMessage();
    }

    @Bean(name="routerMessageSVC")
    public ReceiveRouterMessageService createReceiveMessageService() {
        return new RouterMessageService();
    }

    @Bean(name="publishLocationSVC")
    public PublishLocationService createPublishVehicleLocationMessageService() { return new PublishVehicleLocationService(); }

}
