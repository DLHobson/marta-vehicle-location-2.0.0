package com.itsmarta.technology.its.martavehiclelocation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix="heartbeat", ignoreInvalidFields = true)
@ConfigurationPropertiesScan
public class HeartbeatConfiguration {

    //Heartbeat Server Configuration
    private String host = "** INVALID FIELD - CHECK application.yml **";
    private Integer port = 9999;
    private String successMsg = "** INVALID FIELD - CHECK application.yml **";
    private String failureMsg = "** INVALID FIELD - CHECK application.yml **";

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }

    public String getFailureMsg() {
        return failureMsg;
    }

    public void setFailureMsg(String failureMsg) {
        this.failureMsg = failureMsg;
    }
}
