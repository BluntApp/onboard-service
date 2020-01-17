package com.blunt.onboard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "twilio")
@Data
@Component
public class TwilioAccount {

  private String sid;
  private String token;
  private String messageCenter;

}
